package com.example.apitestmanager.atm.models.response;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UniversalAPIResponse {

    private Response response;

    private Exception exception;

}
