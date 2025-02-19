package ir.tic.clouddc.cloud;

import ir.tic.clouddc.resource.ResourceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CloudServiceImpl implements CloudService {

    private final CloudRepository cloudRepository;

    private final ResourceService resourceService;

    private final CephRepository cephRepository;

    public CloudServiceImpl(CloudRepository cloudRepository, ResourceService resourceService, CephRepository cephRepository) {
        this.cloudRepository = cloudRepository;
        this.resourceService = resourceService;
        this.cephRepository = cephRepository;

    }

    @Override
    public void saveManualData(ManualData manualData) {
        Ceph ceph = new Ceph();
        ceph.setCapacity(manualData.getCapacity());
        ceph.setLocalDateTime(LocalDateTime.now());
        ceph.setProvider(resourceService.getReferencedUtilizer(1006));
        ceph.setServiceType('1');
        ceph.setUnit("PB");
        CephUtilizer cephUtilizer = new CephUtilizer();
        cephUtilizer.setUtilizer(resourceService.getReferencedUtilizer(1002));
        cephUtilizer.setProvider(ceph);
        cephUtilizer.setUsage(manualData.getUsed());
        cephUtilizer.setLocalDateTime(ceph.getLocalDateTime());
        ceph.setCurrent(true);
        ceph.setCephUtilizerList(List.of(cephUtilizer));
        switch (manualData.getUnit()) {
            case "1":
                cephUtilizer.setUnit("PB");
            case "2":
                cephUtilizer.setUnit("TB");
            case "3":
                cephUtilizer.setUnit("GB");
        }

        var oldResult = cephRepository.getCurrentCeph(1006);
        if (oldResult.isPresent()) {
            oldResult.get().setCurrent(false);
            cloudRepository.saveAll(List.of(ceph, oldResult.get()));
        } else {
            cloudRepository.save(ceph);
        }
    }

    @Override
    public Ceph getXasCurrentCephData() {
        var currentXasData = cephRepository.getCurrentCeph(1006);
        if (currentXasData.isPresent()) {
            return currentXasData.get();
        } else {
            throw new NoSuchElementException();
        }
    }
}
