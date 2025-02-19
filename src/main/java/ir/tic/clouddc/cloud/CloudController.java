package ir.tic.clouddc.cloud;

import ir.tic.clouddc.utils.UtilService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cloud")
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

        var currentXas = cloudService.getXasCurrentCephData();
        currentXas.setPersianDateTime(UtilService.getFormattedPersianDateAndTime(currentXas.getLocalDateTime().toLocalDate(), currentXas.getLocalDateTime().toLocalTime()));
        model.addAttribute("currentCeph", currentXas);
        model.addAttribute("currentUsage", currentXas.getCephUtilizerList().get(0).getUsage());

        return "cloudOverview";
    }

    @PostMapping("/submitManualData")
    public String submitManualData(@ModelAttribute ManualData manualData, RedirectAttributes redirectAttributes) {
        cloudService.saveManualData(manualData);
        redirectAttributes.addFlashAttribute("success", true);

        return "redirect:/cloud/overview";
    }

}
