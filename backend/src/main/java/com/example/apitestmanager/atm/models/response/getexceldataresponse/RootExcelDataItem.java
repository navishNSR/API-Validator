package com.example.apitestmanager.atm.models.response.getexceldataresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class RootExcelDataItem {

	@JsonProperty("headers")
	private Object headers;

	@JsonProperty("endpoint")
	private Object endpoint;

	@JsonProperty("method")
	private Object method;

	@JsonProperty("request_body")
	private Object requestBody;

	@JsonProperty("base_url")
	private Object baseUrl;

	@JsonProperty("response_body")
	private Object responseBody;

	@JsonProperty("status_code")
	private Object statusCode;
}