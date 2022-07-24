package com.InspiredLabs.SS_RegistrationApplication.service.fileUpload;


import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.logging.Logger;

import static com.InspiredLabs.SS_RegistrationApplication.utils.Constant.BUCKET_NAME;

@Service
public class FileUploadService {
    private final Logger LOGGER = Logger.getLogger(FileUploadService.class.getName());
    private final TransferManager transferManager;


    public FileUploadService(TransferManager transferManager) {
        this.transferManager = transferManager;
    }

    public void upload(String fileName) {

        File file = new File(fileName);
        LOGGER.info(" ******** This is is the uploaded files path " + file.getAbsolutePath());
        if (file.exists()) {
            try {
                Upload upload = transferManager.upload(BUCKET_NAME, fileName, file);
                upload.waitForCompletion();
                LOGGER.info("File upload very successful......");
            } catch (AmazonClientException e) {
                LOGGER.info("File upload unsuccessful.......");
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } else {
            LOGGER.warning("!!!! File Not Found " + file.getAbsolutePath() + " !!!!!!");
        }
    }


}
