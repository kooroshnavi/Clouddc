package com.navi.dcim.security;

import com.navi.dcim.otp.OtpService;
import com.navi.dcim.person.Address;
import com.navi.dcim.person.AddressRepository;
import com.navi.dcim.person.PersonService;
import com.navi.dcim.utils.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
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
    public String recieveAddress(Model model, @Valid @ModelAttribute("otpRequest") OtpRequest otpRequest, Errors errors
                                    , HttpServletRequest request) {

        System.out.println(otpRequest.getAddress());

        Optional<Address> address = addressRepository.findByValue(otpRequest.getAddress());

        if (errors.hasErrors()){
            var error2 = true;
            model.addAttribute("error2", error2);
            return "otp1";
        }

        if (!address.isPresent()) {
            var notFound = true;
            model.addAttribute("notFound", notFound);
            return "otp1";
        }

        if (address.isPresent()){
           // otpService.sendOtpMessage(address.get(), request.getRemoteAddr(), LocalDateTime.now());
            return "otp-verify";
        }


        return "otp1";
    }

}
