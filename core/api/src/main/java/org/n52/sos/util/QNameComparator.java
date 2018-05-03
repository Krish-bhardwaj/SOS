/**
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
 * Software GmbH
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
package org.n52.sos.util;

import java.util.Comparator;

import javax.xml.namespace.QName;

/**
 * Comparator for {@link QName}s.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public class QNameComparator implements Comparator<QName> {
    public static final QNameComparator INSTANCE = new QNameComparator();

    @Override
    public int compare(QName o1, QName o2) {
        if (o1.getPrefix() != null) {
            if (o2.getPrefix() != null) {
                int prefix = o1.getPrefix().compareTo(o2.getPrefix());
                if (prefix != 0) {
                    return prefix;
                }
            } else {
                return 1;
            }
        } else if (o2.getPrefix() != null) {
            return -1;
        }
        return o1.getLocalPart().compareTo(o2.getLocalPart());
    }

}