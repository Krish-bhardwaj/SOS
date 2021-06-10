/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.aquarius.requests;

import java.util.Map;

import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.proxy.request.AbstractGetRequest;

public class GetUnitList extends AbstractGetRequest {

    private String grouIdentifier;

    public GetUnitList() {
    }

    public String getGrouIdentifier() {
        return grouIdentifier;
    }

    public void setGrouIdentifier(String grouIdentifier) {
        this.grouIdentifier = grouIdentifier;
    }

    public boolean hasGroupIdentifier() {
        return getGrouIdentifier() != null && !getGrouIdentifier().isEmpty();
    }

    @Override
    public Map<String, String> getQueryParameters() {
        Map<String, String> parameter = createMap();
        if (hasGroupIdentifier()) {
            parameter.put(AquariusConstants.Parameters.GROUP_IDENTIFIER, getGrouIdentifier());
        }
        return parameter;
    }

    @Override
    public String getPath() {
        return AquariusConstants.Paths.GET_UNIT_LIST;
    }

}
