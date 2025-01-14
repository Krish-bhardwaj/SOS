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
package org.n52.sos.web.common;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public interface JSONConstants {
    String BINDING_KEY = "binding";
    String BINDINGS_KEY = "bindings";
    String RESPONSE_FORMAT_KEY = "responseFormat";
    String PROCEDURE_DESCRIPTION_FORMAT_KEY = "procedureDescriptionFormat";
    String OBSERVATION_ENCODINGS_KEY = "observationEncodings";
    String PROCEDURE_ENCODINGS_KEY = "procedureEncodings";
    String EXTENDED_CAPABILITIES_EXTENSION_KEY = "extendedCapabilitiesExtensions";
    String EXTENDED_CAPABILITIES_DOMAIN_KEY = "extendedCapabilitiesDomain";
    String OFFERING_EXTENSION_EXTENSION_KEY = "offeringExtensionExtensions";
    String OFFERING_EXTENSION_DOMAIN_KEY = "offeringExtensionDomain";
    String OPERATIONS_KEY = "operations";
    String SERVICE_KEY = "service";
    String ACTIVE_KEY = "active";
    String VERSION_KEY = "version";
    String OPERATION_KEY = "operation";
}
