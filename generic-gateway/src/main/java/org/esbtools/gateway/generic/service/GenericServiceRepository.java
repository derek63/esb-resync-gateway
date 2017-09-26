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

import org.apache.commons.lang3.StringUtils;
import org.esbtools.gateway.exception.InvalidSystemException;
import org.esbtools.gateway.exception.SystemConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class GenericServiceRepository {

    private Set<GenericService> genericServices;

    @Autowired
    public GenericServiceRepository(Set<GenericService> genericServices) {
        this.genericServices = genericServices;
    }

    public GenericService getBySystem(String system) {
        if(StringUtils.isBlank(system)) {
            throw new InvalidSystemException(system);
        } else {
            for (GenericService genericService : genericServices) {
                if (system.equalsIgnoreCase(genericService.getSystemName())) {
                    return genericService;
                }
            }
        }
        throw new SystemConfigurationException(system);
    }
}
