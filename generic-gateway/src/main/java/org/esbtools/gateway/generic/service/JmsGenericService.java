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
package org.esbtools.gateway.generic.service;

import org.apache.commons.collections4.MapUtils;
import org.esbtools.gateway.GatewayResponse;
import org.esbtools.gateway.exception.GenericFailedException;
import org.esbtools.gateway.generic.GenericRequest;
import org.esbtools.gateway.generic.GenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Map;

@Service
public class JmsGenericService implements GenericService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JmsGenericService.class);

    protected JmsGenericConfiguration jmsGenericConfiguration;

    @Autowired
    public JmsGenericService(JmsGenericConfiguration jmsGenericConfiguration) {
        this.jmsGenericConfiguration = jmsGenericConfiguration;
    }

    public String getSystemName() {
        return jmsGenericConfiguration.getEndSystem();
    }

    @Override
    public GenericResponse generic(GenericRequest genericRequest) {
        LOGGER.info("{}", genericRequest);
        genericRequest.ensureRequiredPropertiesHaveValues();
        return enqueue(genericRequest);
    }

    private GenericResponse enqueue(final GenericRequest genericRequest) {
        GenericResponse genericResponse = new GenericResponse();
        try {
            LOGGER.info("Sending message {}", genericRequest.getPayload());
            jmsGenericConfiguration.getBroker().send(genericRequest.getDestination(), new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    Message message = session.createTextMessage(genericRequest.getPayload());
                    if(MapUtils.isNotEmpty(genericRequest.getHeaders())) {
                        for (Map.Entry<String, String> header : genericRequest.getHeaders().entrySet()) {
                            LOGGER.info("Adding header key={}, value{}", header.getKey(), header.getValue());
                            message.setStringProperty(header.getKey(), header.getValue());
                        }
                    }
                    return message;
                }
            });
            genericResponse.setStatus(GatewayResponse.Status.Success);
        } catch (RuntimeException e) {
            LOGGER.error("An error occurred when enqueuing the generic message: {}", genericRequest, e);
            throw new GenericFailedException(genericRequest);
        }
        LOGGER.info("{}", genericResponse);
        return genericResponse;
    }

}
