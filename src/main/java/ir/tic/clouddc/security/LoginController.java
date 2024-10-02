package ir.tic.clouddc.security;

import ir.tic.clouddc.otp.OtpForm;
import ir.tic.clouddc.otp.OtpRequest;
import ir.tic.clouddc.otp.OTPService;
import ir.tic.clouddc.person.Address;
import ir.tic.clouddc.person.AddressRepository;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Controller
@Slf4j
public class LoginController {

    private final OTPService otpService;
    private final AddressRepository addressRepository;

    private final PersonService personService;

    @Autowired
    public LoginController(OTPService otpService, AddressRepository addressRepository, PersonService personService) {
        this.otpService = otpService;
        this.addressRepository = addressRepository;
        this.personService = personService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "multiple", required = false) String multiple,
                                HttpServletRequest request, Model model) throws UnknownHostException, SocketException {

        var OTPForm = UtilService.createChallenge(new OtpForm());

        model.addAttribute("index", OTPForm.getIndex());
        model.addAttribute("otpRequest", OTPForm);

        if (error != null) {
            model.addAttribute("error", error);
        }

        if (logout != null) {
            model.addAttribute("logout", logout);
        }

        if (multiple != null) {
            model.addAttribute("multiple", multiple);
        }

        if (!model.containsAttribute("error")) {
            model.addAttribute("error", false);
        }

        if (!model.containsAttribute("notFound")) {
            model.addAttribute("notFound", false);
        }

        if (!model.containsAttribute("disabled")) {
            model.addAttribute("disabled", false);
        }

        model.addAttribute("date", UtilService.getCurrentDate());
        model.addAttribute("remoteAddress", request.getRemoteAddr());

        return "otp1";
    }

    @PostMapping("/login/otp")
    public String getAddress(Model model, @Valid @ModelAttribute("otpRequest") OtpRequest otpRequest, Errors errors
            , HttpServletRequest request, RedirectAttributes redirectAttributes) throws ExecutionException {

        if (errors.hasErrors() || !Objects.equals(otpRequest.getProvidedAnswer(), UtilService.FORM_CAPTCHA_RESULT.get(otpRequest.getIndex()))) {
            var error2 = true;
            model.addAttribute("error2", error2);
            redirectAttributes.addFlashAttribute("error", true);

            return "redirect:/login";
        }

        if (otpService.getOtpUid(otpRequest.getAddress()).isBlank()) {
            var address = userIsKnown(otpRequest.getAddress());
            if (address.isPresent()) {
                var person = personService.getReferencedPerson(address.get().getId());
                if (!person.isEnabled()) {
                    redirectAttributes.addFlashAttribute("disabled", true);

                    return "redirect:/login";
                }
                UUID otpUid = UUID.randomUUID();
                UUID expiryTimeUUID = UUID.nameUUIDFromBytes(otpUid.toString().getBytes(StandardCharsets.UTF_8));

                otpService.generateLoginOTP(otpRequest.getAddress()
                        , otpUid.toString()
                        , expiryTimeUUID.toString()
                        , request.getRemoteAddr()
                        , LocalDateTime.now());
                var exp = otpService.getOtpExpiry(otpUid.toString());
                LocalDateTime expiry = LocalDateTime.parse(exp);
                model.addAttribute("otpInput", new OtpForm());
                model.addAttribute("otpUid", otpUid.toString());
                model.addAttribute("secondsLeft", LocalDateTime.now().until(expiry, ChronoUnit.SECONDS));

                return "otp-verify";
            } else {
                redirectAttributes.addFlashAttribute("notFound", true);

                return "redirect:/login";
            }

        } else {
            var OTPUid = otpService.getOtpUid(otpRequest.getAddress());
            var exp = otpService.getOtpExpiry(OTPUid);
            LocalDateTime expiry = LocalDateTime.parse(exp);
            model.addAttribute("otpInput", new OtpForm());
            model.addAttribute("otpUid", OTPUid);
            model.addAttribute("secondsLeft", LocalDateTime.now().until(expiry, ChronoUnit.SECONDS));

            return "otp-verify";
        }
    }

    @GetMapping("/login/otp/verify")
    private String otpRedirectPage(Model model
            , @RequestParam String otpUid) throws ExecutionException {

        boolean invalid = true;
        var exp = otpService.getOtpExpiry(otpUid);
        LocalDateTime expiry = LocalDateTime.parse(exp);
        model.addAttribute("invalid", invalid);
        model.addAttribute("otpInput", new OtpForm());
        model.addAttribute("otpUid", otpUid);
        model.addAttribute("secondsLeft", (LocalDateTime.now().until(expiry, ChronoUnit.SECONDS)));

        return "otp-verify";
    }

    private Optional<Address> userIsKnown(String address) {

        return addressRepository.findByValue(address);
    }
}
