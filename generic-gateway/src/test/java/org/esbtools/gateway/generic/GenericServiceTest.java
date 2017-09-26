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
import org.esbtools.gateway.exception.IncompleteRequestException;
import org.esbtools.gateway.exception.GenericFailedException;
import org.esbtools.gateway.generic.service.GenericService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
public class GenericServiceTest {

    @Autowired
    @Qualifier("gitHubGenericService")
    private GenericService genericService;

    @Autowired
    @Qualifier("badHubGenericService")
    private GenericService badGenericService;

    @Before
    public void setupTest() {

    }

    @After
    public void tearDown() {
        genericService = null;
    }

    @Test
    public void doesRequestWithAllRequiredValuesReturnSuccessfulResponse() {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setSystem("GitHub");
        genericRequest.setDestination("Destination");
        genericRequest.setPayload("Login");
        Map<String, String> headers = new HashMap<>();
        headers.put("gitHubUserId", "derek63");
        headers.put("otherUserId", "dhaynes");
        genericRequest.setHeaders(headers);

        GenericResponse genericResponse = genericService.generic(genericRequest);
        assertEquals(GatewayResponse.Status.Success, genericResponse.getStatus());
        assertEquals(null, genericResponse.getErrorMessage());
    }

    @Test(expected = IncompleteRequestException.class)
    public void doesMissingSystemResultInException() {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setDestination("Destination");
        genericRequest.setPayload("Login");
        Map<String, String> headers = new HashMap<>();
        headers.put("gitHubUserId", "derek63");
        headers.put("otherUserId", "dhaynes");
        genericRequest.setHeaders(headers);

        genericService.generic(genericRequest);
    }

    @Test(expected = IncompleteRequestException.class)
    public void doesMissingPayloadResultInException() {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setSystem("GitHub");
        genericRequest.setDestination("Destination");
        Map<String, String> headers = new HashMap<>();
        headers.put("gitHubUserId", "derek63");
        headers.put("otherUserId", "dhaynes");
        genericRequest.setHeaders(headers);

        genericService.generic(genericRequest);
    }

    @Test(expected = GenericFailedException.class)
    public void doesServerErrorResultInException() {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setSystem("BadHub");
        genericRequest.setDestination("Destination");
        genericRequest.setPayload("Login");
        Map<String, String> headers = new HashMap<>();
        headers.put("gitHubUserId", "derek63");
        headers.put("otherUserId", "dhaynes");
        genericRequest.setHeaders(headers);

        badGenericService.generic(genericRequest);
    }

}
