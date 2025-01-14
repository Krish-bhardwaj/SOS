/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.web.admin.i18n.ajax;

import org.n52.iceland.i18n.metadata.I18NObservablePropertyMetadata;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.web.common.ControllerConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ControllerConstants.Paths.OBSERVABLE_PROPERTY_I18N_AJAX_ENDPOINT)
public class ObservablePropertyI18NAjaxEndpoint extends AbstractAdminI18NAjaxEndpoint<I18NObservablePropertyMetadata> {

    @Override
    protected boolean isValid(SosContentCache cache, String id) {
        return cache.hasObservableProperty(id);
    }

    @Override
    protected I18NObservablePropertyMetadata create(String id) {
        return new I18NObservablePropertyMetadata(id);
    }

    @Override
    protected Class<I18NObservablePropertyMetadata> getType() {
        return I18NObservablePropertyMetadata.class;
    }

}
