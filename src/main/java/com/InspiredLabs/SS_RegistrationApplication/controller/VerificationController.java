package com.InspiredLabs.SS_RegistrationApplication.controller;

import com.InspiredLabs.SS_RegistrationApplication.dto.Participant;
import com.InspiredLabs.SS_RegistrationApplication.exception.InvalidVerificationCodeException;
import com.InspiredLabs.SS_RegistrationApplication.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("SSRegistration/api/v1/verification")
public class VerificationController {
    //Verify user based on user id.

    private final VerificationService verificationService;

    @Autowired
    public VerificationController(VerificationService verificationService){
        this.verificationService = verificationService;
    }

    @PostMapping("/verify/{verificationCode}")
    public ResponseEntity<String> verifyParticipant(@PathVariable String verificationCode) throws InvalidVerificationCodeException {
        this.verificationService.verify(verificationCode);
        return new ResponseEntity<>("Verification Successful", HttpStatus.OK);
    }

    @GetMapping("/participants/verified")
    public ResponseEntity<List<Participant>> getAllVerifiedParticipants(){
        List<Participant> verifiedParticipants = this.verificationService.getAllVerifiedParticipants();
        return new ResponseEntity<>(verifiedParticipants, HttpStatus.OK);
    }

    @GetMapping("/participants")
    public ResponseEntity<List<Participant>> getAllRegisteredParticipants(){
        List<Participant> registeredParticipants = this.verificationService.getRegisteredParticipants();
        return new ResponseEntity<>(registeredParticipants, HttpStatus.OK);
    }

}
