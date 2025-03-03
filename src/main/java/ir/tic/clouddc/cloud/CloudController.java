package ir.tic.clouddc.cloud;

import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DecimalFormat;
import java.util.Optional;

@Controller
@RequestMapping("/cloud")
@Slf4j
public class CloudController {

    private final CloudService cloudService;

    public CloudController(CloudService cloudService) {
        this.cloudService = cloudService;
    }

    @GetMapping("/overview")
    public String showManualDataForm(Model model) {
        ManualData manualData = new ManualData();
        model.addAttribute("manualData", manualData);
        if (!model.containsAttribute("success")) {
            model.addAttribute("success", false);
        }
        var currentXas = cloudService.getCurrentService(1, 1006);
        if (currentXas.isPresent()) {
            var xasResult = (Ceph) currentXas.get();
            model.addAttribute("noXasData", false);
            currentXas.get().setPersianDateTime(UtilService.getFormattedPersianDateAndTime(currentXas.get().getLocalDateTime().toLocalDate(), currentXas.get().getLocalDateTime().toLocalTime()));
            model.addAttribute("currentXasCeph", xasResult);
            model.addAttribute("currentXasUsage", xasResult.getCephUtilizerList().get(0).getUsage());
        } else {
            model.addAttribute("noXasData", true);
        }

        var currentSedad = cloudService.getCurrentService(1, 1003);
        if (currentSedad.isPresent()) {
            var sedadResult = (Ceph) currentSedad.get();
            model.addAttribute("noSedadData", false);
            currentSedad.get().setPersianDateTime(UtilService.getFormattedPersianDateAndTime(currentSedad.get().getLocalDateTime().toLocalDate(), currentSedad.get().getLocalDateTime().toLocalTime()));
            model.addAttribute("currentSedadCeph", sedadResult);
            model.addAttribute("currentSedadUsage", sedadResult.getCephUtilizerList().get(0).getUsage());
        } else {
            model.addAttribute("noSedadData", true);
        }

        return "cloudOverview";
    }

    @PostMapping("/submitManualData")
    public String submitManualData(@ModelAttribute ManualData manualData, RedirectAttributes redirectAttributes) {
        cloudService.saveManualData(manualData);
        redirectAttributes.addFlashAttribute("success", true);

        return "redirect:/cloud/overview";
    }

    @GetMapping("/serviceType/{serviceType}")
    public String showServiceStatus(Model model, @PathVariable Integer serviceType) {
        Optional<? extends CloudProvider> result;
        result = cloudService.getServiceStatus(serviceType);
        if (result.isPresent()) {
            var service = (Ceph) result.get();
            model.addAttribute("service", service);
            model.addAttribute("utilizerList", service.getCephUtilizerList());
            var serviceHistory = cloudService.getServiceHistory(serviceType, service.getProvider().getId());
            model.addAttribute("serviceHistoryList", serviceHistory);
            model.addAttribute("idForm", new HistoryIdForm());
            var positive = remainCalculation(service);
            model.addAttribute("positive", positive);

            return "CloudServiceStatus";

        }
        return "404";
    }

    @PostMapping("/history")
    public String showServiceHistory(@ModelAttribute HistoryIdForm idForm, Model model) {
        Optional<? extends CloudProvider> optionalCloudProvider = cloudService.getCloudProvider(idForm.getId());
        if (optionalCloudProvider.isPresent()) {
            var service = (Ceph) optionalCloudProvider.get();
            model.addAttribute("service", service);
            model.addAttribute("utilizerList", service.getCephUtilizerList());
            var serviceHistory = cloudService.getServiceHistory(service.getServiceType(), service.getProvider().getId());
            model.addAttribute("serviceHistoryList", serviceHistory);
            model.addAttribute("idForm", new HistoryIdForm());
            var positive = remainCalculation(service);
            model.addAttribute("positive", positive);

            return "CloudServiceStatus";
        }

        return "404";
    }

    private static boolean remainCalculation(Ceph service) {
        float remains;
        if (service.getCapacityUnit().equals(service.getUsageUnit())) {
            remains = (float) (service.getCapacity() * 0.8) - service.getUsage();
        } else if (service.getUsageUnit().equals("TB")) {
            remains = (float) (service.getCapacity() * 0.8) - (service.getUsage() / 1000);
        } else {
            remains = (float) (service.getCapacity() * 0.8) - (service.getUsage() / 1000 / 1000);
        }
        boolean sign;
        sign = remains > 0;
        if (remains < 0) {
            remains = Math.abs(remains);
        }

        DecimalFormat remainFormat = new DecimalFormat("0.00");
        if (remains >= 1) {
            service.setReadOnlyRemainingUnit("PB");
        } else {
            remains *= 1000;
            if (remains >= 1) {
                service.setReadOnlyRemainingUnit("TB");
            } else {
                remains *= 1000;
                service.setReadOnlyRemainingUnit("GB");
            }
        }
        service.setReadOnlyRemaining(remainFormat.format(Float.valueOf(remains)));

        return sign;
    }

}
