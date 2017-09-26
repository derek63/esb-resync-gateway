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

import org.esbtools.gateway.generic.service.JmsGenericConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

import static org.junit.Assert.assertEquals;


public class JmsGenericConfigurationTest {

    JmsGenericConfiguration jmsGenericConfiguration;
    JmsTemplate jmsTemplate;

    @Before
    public void setUp() throws Exception {
        jmsTemplate = new JmsTemplate();
        jmsGenericConfiguration = new JmsGenericConfiguration("endSystem", jmsTemplate, "genericQueue");
    }

    @After
    public void tearDown() throws Exception {
        jmsGenericConfiguration = null;
    }

    @Test
    public void testGetEndSystem() throws Exception {
        assertEquals("endSystem", jmsGenericConfiguration.getEndSystem());
    }

    @Test
    public void testSetEndSystem() throws Exception {
        jmsGenericConfiguration.setEndSystem("endSystem1");
        assertEquals("endSystem1", jmsGenericConfiguration.getEndSystem());
    }

    @Test
    public void testGetBroker() throws Exception {
        assertEquals(jmsTemplate, jmsGenericConfiguration.getBroker());
    }

    @Test
    public void testSetBroker() throws Exception {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsGenericConfiguration.setBroker(jmsTemplate);
        assertEquals(jmsTemplate, jmsGenericConfiguration.getBroker());
    }

    @Test
    public void testGetGenericQueue() throws Exception {
        assertEquals("genericQueue", jmsGenericConfiguration.getGenericQueue());
    }

    @Test
    public void testSetGenericQueue() throws Exception {
        jmsGenericConfiguration.setGenericQueue("genericQueue1");
        assertEquals("genericQueue1", jmsGenericConfiguration.getGenericQueue());
    }
}
