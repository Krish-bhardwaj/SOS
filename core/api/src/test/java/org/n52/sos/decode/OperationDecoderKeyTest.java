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
package org.n52.sos.decode;

import org.junit.Assert;

import org.junit.Test;

import org.n52.svalbard.decode.OperationDecoderKey;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.janmayen.http.MediaTypes;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class OperationDecoderKeyTest {

    @Test
    public void testHashCode() {
        Assert.assertEquals(
                new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                        SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP).hashCode(),
                new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                        SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP).hashCode());
        Assert.assertEquals(
                new OperationDecoderKey(null, Sos2Constants.SERVICEVERSION,
                        SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP).hashCode(),
                new OperationDecoderKey(null, Sos2Constants.SERVICEVERSION,
                        SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP).hashCode());
        Assert.assertEquals(
                new OperationDecoderKey(SosConstants.SOS, null, SosConstants.Operations.GetCapabilities.name(),
                        MediaTypes.APPLICATION_KVP).hashCode(),
                new OperationDecoderKey(SosConstants.SOS, null, SosConstants.Operations.GetCapabilities.name(),
                        MediaTypes.APPLICATION_KVP).hashCode());
        Assert.assertEquals(
                new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, (String) null,
                        MediaTypes.APPLICATION_KVP).hashCode(),
                new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, (String) null,
                        MediaTypes.APPLICATION_KVP).hashCode());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(
                new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                        SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP),
                new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                        SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP));
        Assert.assertEquals(
                new OperationDecoderKey(null, Sos2Constants.SERVICEVERSION,
                        SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP),
                new OperationDecoderKey(null, Sos2Constants.SERVICEVERSION,
                        SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP));
        Assert.assertEquals(
                new OperationDecoderKey(SosConstants.SOS, null, SosConstants.Operations.GetCapabilities.name(),
                        MediaTypes.APPLICATION_KVP),
                new OperationDecoderKey(SosConstants.SOS, null, SosConstants.Operations.GetCapabilities.name(),
                        MediaTypes.APPLICATION_KVP));
        Assert.assertEquals(
                new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, (String) null,
                        MediaTypes.APPLICATION_KVP),
                new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, (String) null,
                        MediaTypes.APPLICATION_KVP));
    }
}
