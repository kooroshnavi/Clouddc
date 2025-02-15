package ir.tic.clouddc.api.token;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/webservice/token")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping
    public String tokenView(Model model) {
        var tokenList = tokenService.getTokenList().stream().sorted(Comparator.comparing(AuthenticationToken::isValid).reversed()).toList();
        model.addAttribute("tokenList", tokenList);
        if (!model.containsAttribute("hasToken")) {
            model.addAttribute("hasToken", false);
        }
        if (!model.containsAttribute("success")) {
            model.addAttribute("success", false);
        }
        boolean isActive = false;
        if (!tokenList.isEmpty()) {
            isActive = tokenList.stream().anyMatch(AuthenticationToken::isValid);
        }
        model.addAttribute("isActive", isActive);

        return "tokenView";
    }

    @GetMapping("/register")
    public String registerToken(RedirectAttributes redirectAttributes) {
        boolean hasToken = tokenService.hasToken();
        if (hasToken) {
            redirectAttributes.addFlashAttribute("hasToken", true);
        } else {
            tokenService.register();
            redirectAttributes.addFlashAttribute("success", true);
        }

        return "redirect:/webservice/token";
    }

    @GetMapping("/{tokenId}/history")
    public String showTokenHistory(Model model, @PathVariable Integer tokenId) {
        var token = tokenService.getToken(tokenId);
        if (token == null) {
            return "404";
        }
        List<RequestRecord> requestRecordList = tokenService.getRequestRecordHistoryList(token);

        model.addAttribute("requestRecordList", requestRecordList);
        model.addAttribute("token", token);

        return "tokenHistoryList";
    }

    @GetMapping("/revoke")
    public String revokeToken(RedirectAttributes redirectAttributes) {
        boolean hasToken = tokenService.hasToken();
        if (hasToken) {
            tokenService.revokeToken();
            redirectAttributes.addFlashAttribute("success", true);
        }
        else {
            redirectAttributes.addFlashAttribute("hasToken", true);
        }

        return "redirect:/webservice/token";
    }
}
