package com.InspiredLabs.SS_RegistrationApplication.controller;

import com.InspiredLabs.SS_RegistrationApplication.dto.request.InHouseRegistrationDetails;
import com.InspiredLabs.SS_RegistrationApplication.dto.request.RegistrationDetails;
import com.InspiredLabs.SS_RegistrationApplication.exception.InvalidUserException;
import com.InspiredLabs.SS_RegistrationApplication.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("SSRegistration/api/v1")
public class RegistrationController {

    public RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService){
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> participantRegistration(@RequestBody @Valid RegistrationDetails registrationPayload) throws IOException, InterruptedException, InvalidUserException {
        registrationService.registerParticipant(registrationPayload);
        return new ResponseEntity<>("Registration Successful",HttpStatus.OK);
    }

    @PostMapping("/register/inHouse")
    public ResponseEntity<String> participantInHouseRegistration(@RequestBody @Valid InHouseRegistrationDetails registrationDetails) throws InvalidUserException {
        registrationService.inHouseRegistration(registrationDetails);
        return new ResponseEntity<>("Registration Successful", HttpStatus.OK);
    }


}
