package com.example.apitestmanager.atm.controllers;

import com.example.apitestmanager.atm.models.request.getexceldatarequest.RootGetExcelDataRequest;
import com.example.apitestmanager.atm.models.response.UniversalAPIResponse;
import com.example.apitestmanager.atm.models.response.getexceldataresponse.RootExcelDataItem;
import com.example.apitestmanager.atm.services.AppServices;
import com.example.apitestmanager.atm.services.RestAssuredServices;
import com.example.apitestmanager.atm.utils.AppUtils;
import com.example.apitestmanager.atm.utils.DynamicPOJOGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin(origins = "http://localhost:3000")
public class AppController {

    @Autowired
    private AppServices appServices;

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private RestAssuredServices restAssuredServices;

    @Autowired
    private DynamicPOJOGenerator dynamicPOJOGenerator;

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        return appServices.uploadFile(file);
    }

    @PostMapping("/extract-data")
    public ResponseEntity<?> extractDataFromFile(@RequestBody RootGetExcelDataRequest request) throws IOException {
        List<Map<String, Object>> extractExcelResponse = appUtils.readExcel(request.getFilePath());
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        List<RootExcelDataItem> rootGetExcelDataItems = new ArrayList<>();
        for (Map<String, Object> item : extractExcelResponse) {
            RootExcelDataItem rootExcelDataItem = new RootExcelDataItem();
            rootExcelDataItem.setBaseUrl(item.get("Base URL"));
            rootExcelDataItem.setEndpoint(item.get("Endpoint"));
            rootExcelDataItem.setMethod(item.get("Method"));
            rootExcelDataItem.setHeaders(item.get("Header"));
            rootExcelDataItem.setRequestBody(item.get("Request Body"));
            rootExcelDataItem.setResponseBody(item.get("Response Body"));
            rootGetExcelDataItems.add(rootExcelDataItem);
        }
        rootGetExcelDataItems.remove(rootGetExcelDataItems.get(0));

        List<JsonNode> finalResponses = new ArrayList<>();
        for (RootExcelDataItem data : rootGetExcelDataItems) {
            ObjectNode rootNode;
            if (data.getMethod().toString().trim().isEmpty() || data.getMethod() == null){
                ObjectMapper mapper = new ObjectMapper();
                rootNode = mapper.createObjectNode();
                rootNode.put("error", "Method field is Empty");
                rootNode.put("status_code", 400);
                rootNode.put("base_url", data.getBaseUrl().toString());
                rootNode.put("endpoint", data.getEndpoint().toString());
                rootNode.put("method", data.getMethod().toString());
                rootNode.put("request_body", data.getRequestBody().toString());
            } else if (data.getBaseUrl().toString().trim().isEmpty() || data.getBaseUrl() == null) {
                ObjectMapper mapper = new ObjectMapper();
                rootNode = mapper.createObjectNode();
                rootNode.put("error", "Base URL field is Empty");
                rootNode.put("status_code", 400);
                rootNode.put("base_url", data.getBaseUrl().toString());
                rootNode.put("endpoint", data.getEndpoint().toString());
                rootNode.put("method", data.getMethod().toString());
                rootNode.put("request_body", data.getRequestBody().toString());
            } else if (data.getEndpoint().toString().trim().isEmpty() || data.getEndpoint() == null) {
                ObjectMapper mapper = new ObjectMapper();
                rootNode = mapper.createObjectNode();
                rootNode.put("error", "End Point field is Empty");
                rootNode.put("status_code", 400);
                rootNode.put("base_url", data.getBaseUrl().toString());
                rootNode.put("endpoint", data.getEndpoint().toString());
                rootNode.put("method", data.getMethod().toString());
                rootNode.put("request_body", data.getRequestBody().toString());
            } else {
                UniversalAPIResponse hitApiResponse = appServices.getApiResponse(data);
                if (hitApiResponse.getResponse() != null) {
                    ObjectMapper ob = new ObjectMapper();
                    JsonNode responseBody = null;
                    try {
                        responseBody = ob.readTree(hitApiResponse.getResponse().getBody().asString());
                        rootNode = (ObjectNode) responseBody;
                        rootNode.put("status_code", hitApiResponse.getResponse().getStatusCode());
                        rootNode.put("base_url", data.getBaseUrl().toString());
                        rootNode.put("endpoint", data.getEndpoint().toString());
                        rootNode.put("method", data.getMethod().toString());
                        rootNode.put("request_body", data.getRequestBody().toString());
                    } catch (Exception e) {
                        ObjectMapper mapper = new ObjectMapper();
                        rootNode = mapper.createObjectNode();
                        rootNode.put("status_code", hitApiResponse.getResponse().getStatusCode());
                        rootNode.put("base_url", data.getBaseUrl().toString());
                        rootNode.put("endpoint", data.getEndpoint().toString());
                        rootNode.put("method", data.getMethod().toString());
                        rootNode.put("request_body", data.getRequestBody().toString());
                        rootNode.put("response_body", hitApiResponse.getResponse().getBody().prettyPrint());
                        rootNode.put("message", e.toString());
                    }
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    rootNode = mapper.createObjectNode();
                    rootNode.put("status_code", hitApiResponse.getResponse().getStatusCode());
                    rootNode.put("message", hitApiResponse.getException().toString());
                    rootNode.put("base_url", data.getBaseUrl().toString());
                    rootNode.put("endpoint", data.getEndpoint().toString());
                    rootNode.put("method", data.getMethod().toString());
                }
            }
            finalResponses.add(rootNode);
        }
        return ResponseEntity.ok(finalResponses);
    }




    @PostMapping("/get-api-response")
    public ResponseEntity<?> getResponseForApi(@RequestBody RootExcelDataItem request) throws Exception {
        if (request.getMethod().toString().trim().isEmpty() || request.getMethod() == null){
            return new ResponseEntity<>("Error: Method field is Empty", HttpStatus.BAD_REQUEST);
        } else if (request.getBaseUrl().toString().trim().isEmpty() || request.getBaseUrl() == null) {
            return new ResponseEntity<>("Error: Base URL field is Empty",HttpStatus.BAD_REQUEST);
        } else if (request.getEndpoint().toString().trim().isEmpty() || request.getEndpoint() == null) {
            return new ResponseEntity<>("Error: End Point field is Empty",HttpStatus.BAD_REQUEST);
        }
        Response response = restAssuredServices.callMethod(request);
        ObjectMapper ob = new ObjectMapper();
        JsonNode responseBody = ob.readTree(response.getBody().asString());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);

//        List<String> reqBody = new JsonUtils().createPayloads(json);
//        Class<?> generatedClass = DynamicPOJOGenerator.generatePOJO((String) request.getRequestBody());
    }

}
