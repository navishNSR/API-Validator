package com.example.apitestmanager.atm.models.response.uploadfileresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RootGetUploadFileResponse {

	@JsonProperty("status")
	private String status;

	@JsonProperty("file_name")
	private String fileName;

	@JsonProperty("file_path")
	private String filePath;
}