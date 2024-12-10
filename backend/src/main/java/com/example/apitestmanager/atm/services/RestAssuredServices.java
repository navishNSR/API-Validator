package com.example.apitestmanager.atm.services;

import com.example.apitestmanager.atm.models.response.getexceldataresponse.RootExcelDataItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RestAssuredServices {

    public Response callMethod(RootExcelDataItem data) throws JsonProcessingException {
        String method = (String) data.getMethod();
        Response response = null;

        switch (method) {
            case "GET":
                response = getMethod(data);
                break;
            case "POST":
                response = postMethod(data);
                break;
            case "PUT":
                response = putMethod(data);
                break;
            case "PATCH":
                response = patchMethod(data);
                break;
//            case "DELETE":
//                payload = deleteMethod(payload);
//                break;
//            case "POST_FORM":
//                payload = postMethodWithFormData(payload);
//                break;
//            case "POST_FORM_TEXT":
//                payload = postMethodWithFormData(payload, true);
//                break;
            default:
                log.error("Invalid API method");
                break;
        }

        return response;

    }


    @SuppressWarnings("unchecked")
    public Response getMethod(RootExcelDataItem data) throws JsonProcessingException {

        log.info("GET Method");

        String baseUrl = (String) data.getBaseUrl();
        String endpoint = (String) data.getEndpoint();
        String method = (String) data.getMethod();

        RestAssured.baseURI = baseUrl;
        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .request(method, endpoint);

        return response;
    }

    @SuppressWarnings("unchecked")
    public Response postMethod(RootExcelDataItem data) {
        log.info("POST Method");

        try {
            String baseUrl = (String) data.getBaseUrl();
            String endpoint = (String) data.getEndpoint();
            String method = (String) data.getMethod();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonBody = mapper.readTree(data.getRequestBody().toString());
            RestAssured.baseURI = baseUrl;
            Response response = null;
            response = RestAssured.given()
                    .baseUri(baseUrl)
                    .body(jsonBody)
                    .contentType(ContentType.JSON)
                    .request(method, endpoint);

            return response;
        } catch (Exception e) {
            log.error("Error during POST request: ", e);
        }
        return null;
    }

    public Response putMethod(RootExcelDataItem data) {
        log.info("PUT Method");
        try {
            String baseUrl = (String) data.getBaseUrl();
            String endpoint = (String) data.getEndpoint();
            String method = (String) data.getMethod();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonBody = mapper.readTree(data.getRequestBody().toString());
            RestAssured.baseURI = baseUrl;
            Response response = null;
            if (data.getRequestBody() != null) {
                response = RestAssured.given()
                        .baseUri(baseUrl)
                        .body(jsonBody)
                        .contentType(ContentType.JSON)
                        .request(method, endpoint);
            } else {
                response = RestAssured.given()
                        .baseUri(baseUrl)
                        .contentType(ContentType.JSON)
                        .request(method, endpoint);
            }
            return response;
        } catch (Exception e) {
            log.error("Error during PUT request: ", e);
        }
        return null;
    }

    public Response patchMethod(RootExcelDataItem data) {
        log.info("PATCH Method");
        try {
            String baseUrl = (String) data.getBaseUrl();
            String endpoint = (String) data.getEndpoint();
            String method = (String) data.getMethod();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonBody = mapper.readTree(data.getRequestBody().toString());
            RestAssured.baseURI = baseUrl;
            Response response = null;
            if (data.getRequestBody() != null) {
                response = RestAssured.given()
                        .baseUri(baseUrl)
                        .body(jsonBody)
                        .contentType(ContentType.JSON)
                        .request(method, endpoint);
            } else {
                response = RestAssured.given()
                        .baseUri(baseUrl)
                        .contentType(ContentType.JSON)
                        .request(method, endpoint);
            }
            return response;
        } catch (Exception e) {
            log.error("Error during PATCH request: ", e);
        }
        return null;
    }

//    public Payload postMethodWithFormData(Payload payload) {
//        return postMethodWithFormData(payload, false);
//    }
//
//    public Payload postMethodWithFormData(Payload payload, Boolean isTextTypePresent) {
//        try {
//            logger.info("Post Method");
//            RestAssured.baseURI = baseUrl;
//            logger.info(RestAssured.baseURI + payload.reqrelateiveURl);
//
//            Response response = null;
//            RequestSpecification request = RestAssured.given().headers(payload.reqHeaders)
//                    .contentType("multipart/form-data").request();
//
//            payload.multiPart.forEach((key, value) -> {
//                if (key.equalsIgnoreCase("file") || key.equalsIgnoreCase("attachment") || key.equalsIgnoreCase("document")) {
//                    request.multiPart(key, new File(value));
//                } else if (isTextTypePresent) {
//                    request.multiPart(key, value, "text/plain");
//                } else {
//                    request.multiPart(key, new File(value), "application/pdf");
//                }
//            });
//
//            response = request.when().post(payload.reqrelateiveURl);
//
//            String strResponse = response.asString();
//            payload.resStrbody = Common.getPrettyJsonString(strResponse);
//            payload.resStatusCode = response.getStatusCode();
//            payload.response = response;
//        } catch (Exception e) {
//            logException(e);
//        } finally {
//            payload.multiPart = new HashMap<>();
//        }
//        return payload;
//    }
//
//    public Payload postMethodWithOkHttpClient(Payload payload, String... contentType) {
//        try {
//            logger.info("Post Method with OkHttpClient");
//
//            logger.info(payload.baseUrl + payload.reqrelateiveURl);
//
//            MediaType stream = null;
//
//            if (contentType.length > 0)
//                stream = MediaType.get(contentType[0]);
//            else
//                stream = MediaType.get("application/json");
//
//            OkHttpClient client = new OkHttpClient();
//
//            Headers headers = Headers.of(payload.reqHeaders);
//
//            HttpUrl.Builder httpBuilder = HttpUrl.parse(payload.baseUrl).newBuilder();
//
//            RequestBody body = RequestBody.create(stream, payload.reqbody);
//            for (Map.Entry<String, String> param : payload.reqparameter.entrySet()) {
//                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
//            }
//
//            Request request = new Request.Builder().url(httpBuilder.build()).headers(headers).post(body).build();
//            okhttp3.Response response = client.newCall(request).execute();
//
//            payload.resStrbody = Common.getPrettyJsonString(response.body().string());
//            payload.resStatusCode = response.code();
//
//        } catch (Exception e) {
//            logException(e);
//        }
//        return payload;
//    }
//
//
//
//    public Payload deleteMethod(Payload payload) {
//
//        try {
//            logger.info("Delete Method");
//            RestAssured.baseURI = baseUrl;
//            logger.info(payload.toString());
//            Response response = (Response) RestAssured.given().log().all().params(payload.reqparameter)
//                    .headers(payload.reqHeaders).body(payload.reqbody).when().log().all()
//                    .delete(payload.reqrelateiveURl);
//            response.then().log().all();
//            String strResponse = response.asString();
//            payload.resStrbody = Common.getPrettyJsonString(strResponse);
//            payload.resStatusCode = response.getStatusCode();
//            payload.response = response;
//        } catch (Exception e) {
//            logException(e);
//        }
//        return payload;
//    }
}
