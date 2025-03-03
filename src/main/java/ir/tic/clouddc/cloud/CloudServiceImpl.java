package ir.tic.clouddc.cloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.tic.clouddc.api.data.CephResult;
import ir.tic.clouddc.api.data.DataService;
import ir.tic.clouddc.api.response.ErrorResult;
import ir.tic.clouddc.api.response.Response;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.security.RestTokenAuthenticationObject;
import ir.tic.clouddc.utils.UtilService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@EnableScheduling
@Slf4j
@Transactional
public class CloudServiceImpl implements CloudService {

    private static final int SABZ_SYSTEM = 1007;
    private static final int XAS = 1006;
    private static final int EITAA = 1002;
    private static final int BALE = 1003;
    private static final int SOROOSH = 1004;
    private static final int GAP = 1005;
    private static final int iGAP = 1017;
    private static int CEPH_SERVICE_TYPE = 1;
    private final CloudRepository cloudRepository;
    private final ResourceService resourceService;
    private final CephRepository cephRepository;
    private final DataService dataService;
    private final CephUtilizerRepository cephUtilizerRepository;

    @Autowired
    public CloudServiceImpl(CloudRepository cloudRepository, ResourceService resourceService, CephRepository cephRepository, DataService dataService, CephUtilizerRepository cephUtilizerRepository) {
        this.cloudRepository = cloudRepository;
        this.resourceService = resourceService;
        this.cephRepository = cephRepository;
        this.dataService = dataService;
        this.cephUtilizerRepository = cephUtilizerRepository;
    }

    @Scheduled(cron = "0 0 6,19 * * *")
    public void cloudDataScheduler() throws JsonProcessingException {
        SecurityContextHolder.getContext().setAuthentication(new RestTokenAuthenticationObject((List.of(new SimpleGrantedAuthority("API_GET_AUTH"))), (null)));

        var clusterResponse = dataService.getCephClusterDataResponse();
        var usageResponse = dataService.getCephMessengerUsageDataResponse();

        if (clusterResponse.getStatus().equals("OK") && usageResponse.getStatus().equals("OK")) {
            CephResult totalResult = (CephResult) clusterResponse.getResultList().get(0);
            CephResult usedResult = (CephResult) clusterResponse.getResultList().get(1);
            var totalCapacity = totalResult.getValue();
            var usage = usedResult.getValue();

            Ceph ceph = new Ceph();
            ceph.setProvider(resourceService.getReferencedUtilizer(SABZ_SYSTEM));
            ceph.setServiceType('1'); // CEPH
            ceph.setCapacity(Float.parseFloat(totalCapacity));
            ceph.setUsage(Float.parseFloat(usage));
            ceph.setCapacityUnit(totalResult.getUnit());
            ceph.setUsageUnit(usedResult.getUnit());
            ceph.setLocalDateTime(LocalDateTime.now());
            ceph.setCurrent(true);

            List<CephUtilizer> cephUtilizerList = new ArrayList<>();
            int counter = 0;
            do {
                var messengerUsage = (CephResult) usageResponse.getResultList().get(counter);
                CephUtilizer cephUtilizer = new CephUtilizer();
                cephUtilizer.setUsage(Float.parseFloat(messengerUsage.getValue()));
                cephUtilizer.setUnit(messengerUsage.getUnit());
                cephUtilizer.setProvider(ceph);
                cephUtilizer.setLocalDateTime(ceph.getLocalDateTime());
                switch (counter) {
                    case 0 -> cephUtilizer.setUtilizer(resourceService.getReferencedUtilizer(BALE));
                    case 1 -> cephUtilizer.setUtilizer(resourceService.getReferencedUtilizer(SOROOSH));
                    case 2 -> cephUtilizer.setUtilizer(resourceService.getReferencedUtilizer(GAP));
                    case 3 -> cephUtilizer.setUtilizer(resourceService.getReferencedUtilizer(iGAP));
                    default -> cephUtilizer.setUtilizer(resourceService.getReferencedUtilizer(1014));
                }
                cephUtilizerList.add(cephUtilizer);

                counter += 1;
            } while (counter < 4);

            ceph.setCephUtilizerList(cephUtilizerList);

            var oldResult = cephRepository.getCurrentCeph(SABZ_SYSTEM);
            if (oldResult.isPresent()) {
                oldResult.get().setCurrent(false);
                cloudRepository.saveAll(List.of(ceph, oldResult.get()));
            } else {
                cloudRepository.save(ceph);
            }
            log.info("Scheduler Successful");
        } else {
            log.error("API Not Available");
        }
        SecurityContextHolder.clearContext();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<ServiceHistory> getServiceHistory(int serviceType, int providerID) {
        List<ServiceHistory> serviceHistoryList = new ArrayList<>();
        switch (serviceType) {
            default -> {
                var serviceHistory = cloudRepository
                        .getServiceHistory('1', providerID)
                        .stream()
                        .sorted(Comparator.comparing(CloudProviderIDLocalDateProjection::getDate).reversed())
                        .toList();
                if (!serviceHistory.isEmpty()) {
                    serviceHistory.forEach(cloudProviderIDLocalDateProjection -> {
                        ServiceHistory serviceDateForm = new ServiceHistory();
                        serviceDateForm.setId(cloudProviderIDLocalDateProjection.getId());
                        var date = cloudProviderIDLocalDateProjection.getDate().toLocalDate();
                        var time = cloudProviderIDLocalDateProjection.getDate().toLocalTime();
                        serviceDateForm.setPersianDateTime(UtilService.getFormattedPersianDateAndTime(date, time));

                        serviceHistoryList.add(serviceDateForm);
                    });
                }
            }
        }

        return serviceHistoryList;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Optional<? extends CloudProvider> getCloudProvider(int cloudProviderId) {
        var cloudProvider = cloudRepository.getReferenceById(cloudProviderId);
        var result = Hibernate.unproxy(cloudProvider, CloudProvider.class);
        result.setServiceName("Storage");
        var date = result.getLocalDateTime().toLocalDate();
        var time = result.getLocalDateTime().toLocalTime();
        result.setPersianDateTime(UtilService.getFormattedPersianDateAndTime(date, time));

        return Optional.of(result);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void saveManualData(ManualData manualData) {
        Ceph ceph = new Ceph();
        ceph.setCapacity(manualData.getCapacity());
        ceph.setLocalDateTime(LocalDateTime.now());
        ceph.setProvider(resourceService.getReferencedUtilizer(manualData.getProvider()));
        ceph.setServiceType('1');
        ceph.setCapacityUnit("PB");
        CephUtilizer cephUtilizer = new CephUtilizer();
        if (manualData.getProvider() == XAS) {
            cephUtilizer.setUtilizer(resourceService.getReferencedUtilizer(EITAA));
        } else {
            cephUtilizer.setUtilizer(resourceService.getReferencedUtilizer(BALE));
        }
        cephUtilizer.setProvider(ceph);
        cephUtilizer.setUsage(manualData.getUsed());
        cephUtilizer.setLocalDateTime(ceph.getLocalDateTime());
        ceph.setUsage(cephUtilizer.getUsage());
        ceph.setCurrent(true);
        ceph.setCephUtilizerList(List.of(cephUtilizer));
        if (manualData.getUnit() == 1) {
            cephUtilizer.setUnit("TB");
        } else {
            cephUtilizer.setUnit("PB");
        }
        ceph.setUsageUnit(cephUtilizer.getUnit());
        var oldResult = cephRepository.getCurrentCeph(manualData.getProvider());
        if (oldResult.isPresent()) {
            oldResult.get().setCurrent(false);
            cloudRepository.saveAll(List.of(ceph, oldResult.get()));
        } else {
            cloudRepository.save(ceph);
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Optional<? extends CloudProvider> getCurrentService(int serviceType, int providerID) {
        switch (serviceType) {
            default -> {
                return cloudRepository.getCurrentCloudProvider('1', providerID);
            }
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('API_GET_AUTH')")
    public Response getXasCephUsageData() {
        var xasCephData = getCurrentService(CEPH_SERVICE_TYPE, XAS);
        if (xasCephData.isPresent()) {
            var xasResult = (Ceph) xasCephData.get();
            CephResult totalCapacity = new CephResult(1, "حجم کل", String.valueOf(xasResult.getCapacity()), xasResult.getCapacityUnit());
            CephResult usedCapacity = new CephResult(2, "اندیشه یاوران تمدن امروز (ایتا)", String.valueOf(xasResult.getCephUtilizerList().get(0).getUsage()), xasResult.getCephUtilizerList().get(0).getUnit());

            return new Response("OK"
                    ,
                    "استوریج ابری امین آسیا (ابر اختصاصی ایتا)"
                    , UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime())
                    , List.of(totalCapacity, usedCapacity));
        }
        return new Response("Error"
                , "خطا در دریافت اطلاعات"
                , UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now())
                , List.of(new ErrorResult("وب سرویس ریموت سامانه مانیتورینگ جهت دریافت اطلاعات در دسترس نمی باشد")));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('API_GET_AUTH')")
    public Response getSedadUsageData() {
        var sedadCeph = getCurrentService(CEPH_SERVICE_TYPE, BALE);
        if (sedadCeph.isPresent()) {
            var sedadResult = (Ceph) sedadCeph.get();
            CephResult totalCapacity = new CephResult(1, "حجم کل", String.valueOf(sedadResult.getCapacity()), sedadResult.getCapacityUnit());
            CephResult usedCapacity = new CephResult(2, sedadResult.getProvider().getName(), String.valueOf(sedadResult.getCephUtilizerList().get(0).getUsage()), sedadResult.getCephUtilizerList().get(0).getUnit());

            return new Response("OK"
                    ,
                    "استوریج ابری سداد فارس (ابر پشتیبان)"
                    , UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime())
                    , List.of(totalCapacity, usedCapacity));
        }
        return new Response("Error"
                , "خطا در دریافت اطلاعات"
                , UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now())
                , List.of(new ErrorResult("وب سرویس ریموت سامانه مانیتورینگ جهت دریافت اطلاعات در دسترس نمی باشد")));
    }

    @Override
    public Response getSabzClusterData() {
        var sedadCeph = getCurrentService(CEPH_SERVICE_TYPE, SABZ_SYSTEM);
        if (sedadCeph.isPresent()) {
            var sedadResult = (Ceph) sedadCeph.get();
            CephResult totalCapacity = new CephResult(1, "حجم کل", String.valueOf(sedadResult.getCapacity()), sedadResult.getCapacityUnit());
            CephResult usedCapacity = new CephResult(2, "حجم مصرفی", String.valueOf(sedadResult.getUsage()), sedadResult.getUsageUnit());

            return new Response("OK"
                    ,
                    "استوریج ابری سبزسیستم"
                    , UtilService.getFormattedPersianDateAndTime(sedadResult.getLocalDateTime().toLocalDate(), sedadResult.getLocalDateTime().toLocalTime())
                    , List.of(totalCapacity, usedCapacity));
        }

        return new Response("Error"
                , "خطا در دریافت اطلاعات"
                , UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now())
                , List.of(new ErrorResult("وب سرویس ریموت سامانه مانیتورینگ جهت دریافت اطلاعات در دسترس نمی باشد")));
    }

    @Override
    public Response getSabzMessengerData() {
        var currentSedadCeph = getCurrentService(CEPH_SERVICE_TYPE, SABZ_SYSTEM);
        if (currentSedadCeph.isPresent()) {
            List<CephUtilizerRepository.CephUtilizerProjection> cephUtilizerList = cephUtilizerRepository
                    .getCephUtilizerList(currentSedadCeph.get().getProvider().getId())
                    .stream()
                    .sorted(Comparator.comparing(CephUtilizerRepository.CephUtilizerProjection::getUtilizerId))
                    .toList();
            log.info(cephUtilizerList.toString());
            List<CephResult> cephResultList = new ArrayList<>();
            for (int i = 0; i < cephUtilizerList.size(); i++) {
                var result = cephUtilizerList.get(i);
                CephResult cephResult = new CephResult(i + 1, result.getUtilizerName(), String.valueOf(result.getUsage()), result.getUnit());
                cephResultList.add(cephResult);
            }

            return new Response("OK"
                    ,
                    "استوریج ابری سبزسیستم - حجم مصرفی پیام رسان ها"
                    , UtilService.getFormattedPersianDateAndTime(currentSedadCeph.get().getLocalDateTime().toLocalDate(), currentSedadCeph.get().getLocalDateTime().toLocalTime())
                    , cephResultList);

        } else {
            return new Response("Error"
                    , "خطا در دریافت اطلاعات"
                    , UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now())
                    , List.of(new ErrorResult("وب سرویس ریموت سامانه مانیتورینگ جهت دریافت اطلاعات در دسترس نمی باشد")));
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Optional<? extends CloudProvider> getServiceStatus(Integer serviceType) {
        Optional<? extends CloudProvider> currentService;
        switch (serviceType) {
            case 1 -> currentService = getCurrentService(CEPH_SERVICE_TYPE, SABZ_SYSTEM);
            case 4 -> currentService = getCurrentService(CEPH_SERVICE_TYPE, XAS);
            default -> currentService = getCurrentService(CEPH_SERVICE_TYPE, BALE);
        }
        if (currentService.isPresent()) {
            var service = currentService.get();
            var serviceDate = service.getLocalDateTime().toLocalDate();
            var serviceTime = service.getLocalDateTime().toLocalTime();
            currentService.get().setServiceName("Storage");
            currentService.get().setPersianDateTime(UtilService.getFormattedPersianDateAndTime(serviceDate, serviceTime));
        }

        return currentService;
    }
}