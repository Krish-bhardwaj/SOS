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
package org.n52.aqd.decode.kvp.v1;

import java.util.Collections;
import java.util.Set;

import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.OperationDecoderKey;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.janmayen.http.MediaTypes;
import org.n52.sos.decode.kvp.v2.DescribeSensorKvpDecoderv20;

public class AqdDescribeSensorKvpDecoder extends DescribeSensorKvpDecoderv20 {

    private static final DecoderKey KVP_DECODER_KEY_TYPE = new OperationDecoderKey(AqdConstants.AQD,
            AqdConstants.VERSION, SosConstants.Operations.DescribeSensor, MediaTypes.APPLICATION_KVP);

    @Override
    public Set<DecoderKey> getKeys() {
        return Collections.singleton(KVP_DECODER_KEY_TYPE);
    }

}
