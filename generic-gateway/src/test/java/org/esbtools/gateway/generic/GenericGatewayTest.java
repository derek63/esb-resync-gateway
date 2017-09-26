/*
 Copyright 2015 esbtools Contributors and/or its affiliates.

 This file is part of esbtools.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.esbtools.gateway.generic;

import org.esbtools.gateway.GatewayResponse;
import org.esbtools.gateway.exception.GatewayErrorMessages;
import org.esbtools.gateway.generic.controller.GenericGateway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("classpath:/rest-servlet.xml")
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" , "classpath:/rest-servlet.xml"})
public class GenericGatewayTest {

    private MockMvc mockMvc;

    @Autowired
    private GenericGateway genericGateway;

    @Before
    public void setupTest() {
        mockMvc = MockMvcBuilders.standaloneSetup(genericGateway).build();
    }

    @After
    public void tearDown() {
        genericGateway = null;
    }

    @Test
    public void doesRequestWithAllRequiredValuesReturnSuccessfulResponse() throws Exception {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setSystem("GitHub");
        genericRequest.setDestination("Destination");
        genericRequest.setPayload("Login");
        Map<String, String> headers = new HashMap<>();
        headers.put("gitHubUserId", "derek63");
        headers.put("otherUserId", "dhaynes");
        genericRequest.setHeaders(headers);

        mockMvc.perform(post("/generic").content(genericRequest.toJson()).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(successfulResponse()));
    }

    @Test
    public void doesMissingSystemReturnBadRequestResponse() throws Exception {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setDestination("Destination");
        genericRequest.setPayload("Login");
        Map<String, String> headers = new HashMap<>();
        headers.put("gitHubUserId", "derek63");
        headers.put("otherUserId", "dhaynes");
        genericRequest.setHeaders(headers);

        mockMvc.perform(post("/generic").content(genericRequest.toJson()).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(invalidSystem(null)));
    }

    @Test
    public void doesMissingDestinationReturnBadRequestResponse() throws Exception {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setSystem("GitHub");
        genericRequest.setPayload("Login");
        Map<String, String> headers = new HashMap<>();
        headers.put("gitHubUserId", "derek63");
        headers.put("otherUserId", "dhaynes");
        genericRequest.setHeaders(headers);

        mockMvc.perform(post("/generic").content(genericRequest.toJson()).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(incompleteRequestResponse(genericRequest)));
    }

    @Test
    public void doesMissingPayloadReturnBadRequestResponse() throws Exception {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setSystem("GitHub");
        genericRequest.setDestination("Destination");
        Map<String, String> headers = new HashMap<>();
        headers.put("gitHubUserId", "derek63");
        headers.put("otherUserId", "dhaynes");
        genericRequest.setHeaders(headers);

        mockMvc.perform(post("/generic").content(genericRequest.toJson()).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(incompleteRequestResponse(genericRequest)));
    }

    @Test
    public void doesMissingHeadersReturnOkResponse() throws Exception {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setSystem("GitHub");
        genericRequest.setDestination("Destination");
        genericRequest.setPayload("Login");

        mockMvc.perform(post("/generic").content(genericRequest.toJson()).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(successfulResponse()));
    }

    @Test
    public void doesSendingUnconfiguredSystemReturnErrorResponse() throws Exception {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setSystem("BitHub");
        genericRequest.setDestination("Destination");
        genericRequest.setPayload("Login");
        Map<String, String> headers = new HashMap<>();
        headers.put("gitHubUserId", "derek63");
        headers.put("otherUserId", "dhaynes");
        genericRequest.setHeaders(headers);

        mockMvc.perform(post("/generic").content(genericRequest.toJson()).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(systemNotConfigured("BitHub")));
    }

    @Test
    public void doesServerErrorReturnInternalServerErrorResponse() throws Exception {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setSystem("BadHub");
        genericRequest.setDestination("Destination");
        genericRequest.setPayload("Login");
        Map<String, String> headers = new HashMap<>();
        headers.put("gitHubUserId", "derek63");
        headers.put("otherUserId", "dhaynes");
        genericRequest.setHeaders(headers);

        mockMvc.perform(post("/generic").content(genericRequest.toJson()).contentType("application/json"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(genericFailed(genericRequest)));
    }

    private String successfulResponse() {
        return new GenericResponse(GatewayResponse.Status.Success, null).toJson();
    }

    private String incompleteRequestResponse(GenericRequest genericRequest) {
        return new GenericResponse(GatewayResponse.Status.Error, GatewayErrorMessages.incompleteRequest(genericRequest)).toJson();
    }

    private String invalidSystem(String systemName) {
        return new GenericResponse(GatewayResponse.Status.Error, GatewayErrorMessages.invalidSystem(systemName)).toJson();
    }

    private String systemNotConfigured(String systemName) {
        return new GenericResponse(GatewayResponse.Status.Error, GatewayErrorMessages.systemNotConfigured(systemName)).toJson();
    }

    private String genericFailed(GenericRequest genericRequest) {
        return new GenericResponse(GatewayResponse.Status.Error, GatewayErrorMessages.genericFailed(genericRequest)).toJson();
    }
}
