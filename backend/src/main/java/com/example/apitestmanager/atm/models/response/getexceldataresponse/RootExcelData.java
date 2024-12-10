package com.example.apitestmanager.atm.models.response.getexceldataresponse;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RootExcelData {

	@JsonProperty("data")
	private List<RootExcelDataItem> rootGetExcelDataReponse;

}