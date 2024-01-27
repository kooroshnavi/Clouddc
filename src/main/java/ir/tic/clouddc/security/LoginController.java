package ir.tic.clouddc.security;

import ir.tic.clouddc.otp.OtpForm;
import ir.tic.clouddc.otp.OtpRequest;
import ir.tic.clouddc.otp.OtpService;
import ir.tic.clouddc.person.Address;
import ir.tic.clouddc.person.AddressRepository;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.task.TaskService;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Controller
@Slf4j
public class LoginController {

    private final PersonService personService;
    private final OtpService otpService;
    private final AddressRepository addressRepository;
    private final TaskService taskService;

    @Autowired
    public LoginController(PersonService personService, OtpService otpService, AddressRepository addressRepository, TaskService taskService) {
        this.personService = personService;
        this.otpService = otpService;
        this.addressRepository = addressRepository;
        this.taskService = taskService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout, Model model) {

        model.addAttribute("otpRequest", new OtpForm());

        String errorMessage = null;
        if (error != null) {
            model.addAttribute("error", error);
        }

        if (logout != null) {
            model.addAttribute("logout", logout);
        }

        return "otp1";
    }


    @PostMapping("/login/otp")
    public String getAddress(Model model, @Valid @ModelAttribute("otpRequest") OtpRequest otpRequest, Errors errors
            , HttpServletRequest request) throws ExecutionException {

        if (errors.hasErrors()) {
            var error2 = true;
            model.addAttribute("error2", error2);
            return "otp1";
        }

        if (otpService.getOtpUid(otpRequest.getAddress()).isBlank()) {
            if (userIsKnown(otpRequest.getAddress())) {
                UUID otpUid = UUID.randomUUID();
                UUID expiryTimeUUID = UUID.nameUUIDFromBytes(otpUid.toString().getBytes(StandardCharsets.UTF_8));

                otpService.generateOtp(otpRequest.getAddress()
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
                var notFound = true;
                model.addAttribute("notFound", notFound);
                return "otp1";
            }

        } else {
            var exp = otpService.getOtpExpiry(otpService.getOtpUid(otpRequest.getAddress()));
            LocalDateTime expiry = LocalDateTime.parse(exp);
            model.addAttribute("otpInput", new OtpForm());
            model.addAttribute("otpUid", otpService.getOtpUid(otpRequest.getAddress()));
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

    private boolean userIsKnown(String address) {
        Optional<Address> person = addressRepository.findByValue(address);
        if (person.isPresent()) {
            return true;
        }
        return false;
    }

}
