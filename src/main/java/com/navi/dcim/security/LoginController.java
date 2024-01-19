package com.navi.dcim.security;

import com.navi.dcim.otp.OtpForm;
import com.navi.dcim.otp.OtpRequest;
import com.navi.dcim.otp.OtpService;
import com.navi.dcim.person.Address;
import com.navi.dcim.person.AddressRepository;
import com.navi.dcim.person.PersonService;
import com.navi.dcim.utils.UtilService;
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

    @Autowired
    public LoginController(PersonService personService, OtpService otpService, AddressRepository addressRepository) {
        this.personService = personService;
        this.otpService = otpService;
        this.addressRepository = addressRepository;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout, Model model) {


        String errorMessage = null;
        if (error != null) {
            model.addAttribute("error", error);
        }

        if (logout != null) {
            model.addAttribute("logout", logout);
        }

        return "login";
    }

    @GetMapping("/login/otp")
    public String showOtpLogin(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout, Model model) {


        String errorMessage = null;
        OtpRequest otpRequest = new OtpRequest();
        if (error != null) {
            model.addAttribute("error", error);
        }

        if (logout != null) {
            model.addAttribute("logout", logout);
        }

        model.addAttribute("otpRequest", otpRequest);
        model.addAttribute("date", UtilService.getCurrentDate());

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

    @PostMapping("/login/otp/verify")
    private String getOTPRequest(@ModelAttribute("otpInput") OtpForm otpForm
            , Model model
            , @RequestParam String otpUid) throws ExecutionException {
        StringBuilder concat = new StringBuilder();
        concat.append(otpForm.getD1());
        concat.append(otpForm.getD2());
        concat.append(otpForm.getD3());
        concat.append(otpForm.getD4());
        concat.append(otpForm.getD5());
        concat.append(otpForm.getD6());
        final String providedOtp = concat.toString();
        log.info(providedOtp);

        var result = otpService.verifyOtp(otpUid, providedOtp);

        if (result.equals("-1")) {
            boolean invalid = true;
            var exp = otpService.getOtpExpiry(otpUid);
            LocalDateTime expiry = LocalDateTime.parse(exp);
            model.addAttribute("invalid", invalid);
            model.addAttribute("otpInput", new OtpForm());
            model.addAttribute("otpUid", otpUid);
            model.addAttribute("secondsLeft", (LocalDateTime.now().until(expiry, ChronoUnit.SECONDS) - 10));
            return "otp-verify";
        } else if (result.equals("0")) {
            return "login";
        }

        return "welcome";
    }

    private boolean userIsKnown(String address) {
        Optional<Address> person = addressRepository.findByValue(address);
        if (person.isPresent()) {
            return true;
        }
        return false;
    }

}
