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
package org.esbtools.gateway.resubmit.controller;

import org.esbtools.gateway.GatewayResponse;
import org.esbtools.gateway.exception.IncompleteRequestException;
import org.esbtools.gateway.exception.InvalidSystemException;
import org.esbtools.gateway.exception.ResubmitFailedException;
import org.esbtools.gateway.exception.SystemConfigurationException;
import org.esbtools.gateway.resubmit.ResubmitRequest;
import org.esbtools.gateway.resubmit.ResubmitResponse;
import org.esbtools.gateway.resubmit.service.ResubmitService;
import org.esbtools.gateway.resubmit.service.ResubmitServiceRepository;
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
public class ResubmitGateway {

    protected ResubmitServiceRepository resubmitServiceRepository;

    @Autowired
    public ResubmitGateway(ResubmitServiceRepository resubmitServiceRepository) {
        this.resubmitServiceRepository = resubmitServiceRepository;
    }

    @RequestMapping(value="/resubmit", method=RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, headers = "content-type=application/json")
    public ResponseEntity<ResubmitResponse> resubmit(@RequestBody ResubmitRequest resubmitRequest) {
        ResubmitService resubmitService = resubmitServiceRepository.getBySystem(resubmitRequest.getSystem());
        ResubmitResponse resubmitResponse = resubmitService.resubmit(resubmitRequest);
        return new ResponseEntity<>(resubmitResponse, HttpStatus.OK);
    }

    @ExceptionHandler(InvalidSystemException.class)
    private ResponseEntity<ResubmitResponse> invalidSystemExceptionHandler (InvalidSystemException e) {
        ResubmitResponse resubmitResponse = new ResubmitResponse(GatewayResponse.Status.Error, e.getMessage());
        return new ResponseEntity<>(resubmitResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SystemConfigurationException.class)
    private ResponseEntity<ResubmitResponse> systemConfigurationExceptionHandler (SystemConfigurationException e) {
        ResubmitResponse resubmitResponse = new ResubmitResponse(GatewayResponse.Status.Error, e.getMessage());
        return new ResponseEntity<>(resubmitResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResubmitFailedException.class)
    private ResponseEntity<ResubmitResponse> resubmitFailedExceptionHandler (ResubmitFailedException e) {
        ResubmitResponse resubmitResponse = new ResubmitResponse(GatewayResponse.Status.Error, e.getMessage());
        return new ResponseEntity<>(resubmitResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IncompleteRequestException.class)
    private ResponseEntity<ResubmitResponse> incompleteRequestExceptionHandler (IncompleteRequestException e) {
        ResubmitResponse resubmitResponse = new ResubmitResponse(GatewayResponse.Status.Error, e.getMessage());
        return new ResponseEntity<>(resubmitResponse, HttpStatus.BAD_REQUEST);
    }

}
