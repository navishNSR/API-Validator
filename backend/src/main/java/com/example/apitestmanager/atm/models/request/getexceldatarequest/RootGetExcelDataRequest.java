package com.example.apitestmanager.atm.models.request.getexceldatarequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RootGetExcelDataRequest{

	@JsonProperty("file_path")
	private String filePath;

}