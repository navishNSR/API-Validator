package com.example.apitestmanager.atm.services;

import com.example.apitestmanager.atm.models.response.UniversalAPIResponse;
import com.example.apitestmanager.atm.models.response.getexceldataresponse.RootExcelDataItem;
import com.example.apitestmanager.atm.models.response.uploadfileresponse.RootGetUploadFileResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class AppServices {

    @Autowired
    private RestAssuredServices restAssuredServices;

    public ResponseEntity<?> uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
        }
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        try (InputStream inputStream = file.getInputStream()) {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = file.getOriginalFilename();
            if (fileName != null){
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

                String absolutePath = filePath.toFile().getAbsolutePath();
                return ResponseEntity.ok(new RootGetUploadFileResponse("File Uploaded Successfully", fileName, absolutePath));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RootGetUploadFileResponse("File Upload Failed", "", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    public UniversalAPIResponse getApiResponse(RootExcelDataItem request){
        try {
            Response response = restAssuredServices.callMethod(request);
            return new UniversalAPIResponse(response, null);
        } catch (Exception e){
            log.error("Error Occurred: ", e);
            return new UniversalAPIResponse(null, e);
        }
    }
}
