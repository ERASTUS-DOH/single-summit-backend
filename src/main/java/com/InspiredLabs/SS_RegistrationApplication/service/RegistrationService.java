package com.InspiredLabs.SS_RegistrationApplication.service;

import com.InspiredLabs.SS_RegistrationApplication.dto.Participant;
import com.InspiredLabs.SS_RegistrationApplication.dto.request.InHouseRegistrationDetails;
import com.InspiredLabs.SS_RegistrationApplication.dto.request.RegistrationDetails;
import com.InspiredLabs.SS_RegistrationApplication.exception.InvalidUserException;
import com.InspiredLabs.SS_RegistrationApplication.repository.ParticipantRepository;
import com.InspiredLabs.SS_RegistrationApplication.service.fileUpload.FileUploadService;
import com.InspiredLabs.SS_RegistrationApplication.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static com.InspiredLabs.SS_RegistrationApplication.utils.Constant.IMAGE_NAME;
import static com.InspiredLabs.SS_RegistrationApplication.utils.Constant.VERIFICATION_CODE;

@Service
public class RegistrationService {
    private static final Logger LOGGER = Logger.getLogger(RegistrationService.class.getName());

    private final QRCodeService qrCodeService;

    private final ParticipantRepository participantRepository;

    private final EmailService emailService;

    private final FileUploadService fileUploadService;

    @Autowired
    public RegistrationService(QRCodeService qrCodeService, ParticipantRepository participantRepository, EmailService emailService, FileUploadService fileUploadService) {
        this.qrCodeService = qrCodeService;
        this.participantRepository = participantRepository;
        this.emailService = emailService;
        this.fileUploadService = fileUploadService;
    }

    public void registerParticipant(RegistrationDetails details) throws IOException, InterruptedException, InvalidUserException {

        Map<String, String> codes = getImageAndVerificationCode();

        Participant participant = new Participant();
        participant.setFirstName(details.firstName);
        participant.setLastName(details.lastName);
        participant.setEmail(details.email);
        participant.setTelephone(details.telephone);
        participant.setGender(details.gender);
        participant.setMemberShipStatus(details.membershipStatus);
        participant.setFirstTimerStatus(details.firstTimerStatus);
        participant.setPublicityAvenue(details.publicityAvenue);
        participant.setVerificationCode(codes.get(VERIFICATION_CODE));
        participant.setVerificationStatus(false);
        participant.setImage_name(codes.get(IMAGE_NAME));

        //verify if user already exist with the same mail:

        Optional<Participant> optionalParticipant = this.participantRepository.getParticipantByEmail(participant.email);
        if (optionalParticipant.isPresent()) {
            throw new InvalidUserException("User with email " + participant.email + " already exist");
        }


        try {
            LOGGER.info("QR-Code generation started..........");
            qrCodeService.generateQRCode(codes);
            LOGGER.info("QR-Code generated successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info("fileUpload");
        fileUploadService.upload(participant.image_name);
        LOGGER.info("Upload successful");

        LOGGER.info(participant.toString());
        LOGGER.info("Sending out email");
        emailService.sendEmail(participant);

        this.participantRepository.save(participant);

    }

    public void inHouseRegistration(InHouseRegistrationDetails registrationDetails) throws InvalidUserException {
        Participant participant = new Participant();
        participant.setFirstName(registrationDetails.firstName);
        participant.setLastName(registrationDetails.lastName);
        participant.setEmail(registrationDetails.email);
        participant.setTelephone(registrationDetails.telephone);
        participant.setGender(registrationDetails.gender);
        participant.setMemberShipStatus(registrationDetails.membershipStatus);
        participant.setFirstTimerStatus(registrationDetails.firstTimerStatus);
        participant.setPublicityAvenue(registrationDetails.publicityAvenue);
        participant.setVerificationStatus(true);

        Optional<Participant> optionalParticipant = this.participantRepository.getParticipantByEmail(participant.email);
        if (optionalParticipant.isPresent()) {
            throw new InvalidUserException("User with email " + participant.email + " already exist");
        }

        this.participantRepository.save(participant);
    }


    public static Map<String, String> getImageAndVerificationCode() {
        String randomCode = Constant.uniqueCodeGenerator();
        String imageCode = randomCode.split("-")[0];
        String imageName = "QR-image-" + imageCode + ".png";

        String verificationCode = randomCode.replace("-", "");

        Map<String, String> result = new HashMap<>();
        result.put(IMAGE_NAME, imageName);
        result.put(VERIFICATION_CODE, verificationCode);

        return result;
    }
}
