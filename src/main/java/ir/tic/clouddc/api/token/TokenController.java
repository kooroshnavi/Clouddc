package ir.tic.clouddc.api.token;

import ir.tic.clouddc.person.PersonService;
import jakarta.persistence.EntityNotFoundException;
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

    private final PersonService personService;

    public TokenController(TokenService tokenService, PersonService personService) {
        this.tokenService = tokenService;
        this.personService = personService;
    }

    @GetMapping
    public String tokenView(Model model) {
        var fullList = tokenService.getTokenList();
        if (!model.containsAttribute("hasToken")) {
            model.addAttribute("hasToken", false);
        }
        if (!model.containsAttribute("success")) {
            model.addAttribute("success", false);
        }
        boolean isActive = false;
        if (!fullList.isEmpty()) {
            isActive = fullList
                    .stream()
                    .filter(authenticationToken -> authenticationToken.getPerson().getUsername().equals(personService.getCurrentUsername()))
                    .anyMatch(AuthenticationToken::isValid);
        }

        var adminAccess = personService.hasAdminAuthority();
        if (adminAccess) {
            model.addAttribute("tokenList", fullList
                    .stream()
                    .filter(authenticationToken -> authenticationToken.getPerson().getUsername().equals(personService.getCurrentUsername()))
                    .sorted(Comparator.comparing(AuthenticationToken::isValid).reversed())
                    .toList());
            List<AuthenticationToken> otherPersonValidTokenList = tokenService.getPersonTokenList(fullList, true);
            List<AuthenticationToken> otherPersonExpiredTokenList = tokenService.getPersonTokenList(fullList, false);
            model.addAttribute("otherPersonValidTokenList", otherPersonValidTokenList);
            model.addAttribute("otherPersonExpiredTokenList", otherPersonExpiredTokenList);
        } else {
            model.addAttribute("tokenList", fullList.stream().sorted(Comparator.comparing(AuthenticationToken::isValid).reversed()));
        }
        model.addAttribute("isActive", isActive);
        model.addAttribute("adminAccess", adminAccess);

        return "tokenView2";
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
            throw new EntityNotFoundException("Token not found");
        }
        List<RequestRecord> requestRecordList = tokenService
                .getRequestRecordHistoryList(token)
                .stream()
                .sorted(Comparator.comparing(RequestRecord::getId).reversed())
                .toList();
        model.addAttribute("requestRecordList", requestRecordList);
        model.addAttribute("token", token);
        model.addAttribute("adminAccess", personService.hasAdminAuthority());
        if (token.getPerson().equals(personService.getCurrentPerson())) {
            model.addAttribute("match", true);
        } else {
            model.addAttribute("match", false);
        }

        return "tokenHistoryList";
    }

    @GetMapping("/revoke")
    public String revokeToken(RedirectAttributes redirectAttributes) {
        boolean hasToken = tokenService.hasToken();
        if (hasToken) {
            tokenService.revokeToken();
            redirectAttributes.addFlashAttribute("success", true);
        } else {
            redirectAttributes.addFlashAttribute("hasToken", true);
        }

        return "redirect:/webservice/token";
    }

    @GetMapping("/person/{personId}/revoke")
    public String revokePersonToken(RedirectAttributes redirectAttributes, @PathVariable Integer personId) {
        boolean personTokenRevoked = tokenService.revokePersonToken(personId);
        if (personTokenRevoked) {
            redirectAttributes.addFlashAttribute("success", true);
        } else {
            redirectAttributes.addFlashAttribute("hasToken", true);
        }

        return "redirect:/webservice/token";
    }
}
