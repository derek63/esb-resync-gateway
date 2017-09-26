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
package org.esbtools.gateway.generic.controller;

import org.esbtools.gateway.GatewayResponse;
import org.esbtools.gateway.exception.IncompleteRequestException;
import org.esbtools.gateway.exception.InvalidSystemException;
import org.esbtools.gateway.exception.GenericFailedException;
import org.esbtools.gateway.exception.SystemConfigurationException;
import org.esbtools.gateway.generic.GenericRequest;
import org.esbtools.gateway.generic.GenericResponse;
import org.esbtools.gateway.generic.service.GenericService;
import org.esbtools.gateway.generic.service.GenericServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class GenericGateway {

    protected GenericServiceRepository genericServiceRepository;

    @Autowired
    public GenericGateway(GenericServiceRepository genericServiceRepository) {
        this.genericServiceRepository = genericServiceRepository;
    }

    @RequestMapping(value="/generic", method=RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, headers = "content-type=application/json")
    public ResponseEntity<GenericResponse> generic(@RequestBody GenericRequest genericRequest) {
        GenericService genericService = genericServiceRepository.getBySystem(genericRequest.getSystem());
        GenericResponse genericResponse = genericService.generic(genericRequest);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @ExceptionHandler(InvalidSystemException.class)
    private ResponseEntity<GenericResponse> invalidSystemExceptionHandler (InvalidSystemException e) {
        GenericResponse genericResponse = new GenericResponse(GatewayResponse.Status.Error, e.getMessage());
        return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SystemConfigurationException.class)
    private ResponseEntity<GenericResponse> systemConfigurationExceptionHandler (SystemConfigurationException e) {
        GenericResponse genericResponse = new GenericResponse(GatewayResponse.Status.Error, e.getMessage());
        return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GenericFailedException.class)
    private ResponseEntity<GenericResponse> genericFailedExceptionHandler (GenericFailedException e) {
        GenericResponse genericResponse = new GenericResponse(GatewayResponse.Status.Error, e.getMessage());
        return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IncompleteRequestException.class)
    private ResponseEntity<GenericResponse> incompleteRequestExceptionHandler (IncompleteRequestException e) {
        GenericResponse genericResponse = new GenericResponse(GatewayResponse.Status.Error, e.getMessage());
        return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
    }

}
