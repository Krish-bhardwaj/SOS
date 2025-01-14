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
package org.n52.sos.ogc.om.values;

import java.util.Objects;

import org.apache.xmlbeans.XmlObject;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.om.values.visitor.ValueVisitor;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SosXmlValue implements org.n52.shetland.ogc.om.values.XmlValue<XmlObject> {

    private XmlObject xml;
    private UoM unit;

    public SosXmlValue(XmlObject xml) {
        this.xml = xml;
    }

    @Override
    public SosXmlValue setValue(XmlObject value) {
        this.xml = value;
        return this;
    }

    @Override
    public XmlObject getValue() {
        return this.xml;
    }

    @Override
    public void setUnit(String unit) {
        this.unit = new UoM(unit);
    }

    @Override
    public Value<XmlObject> setUnit(UoM unit) {
        this.unit = unit;
        return this;
    }

    @Override
    public String getUnit() {
        if (isSetUnit()) {
            return unit.getUom();
        }
        return null;
    }

    @Override
    public UoM getUnitObject() {
        return this.unit;
    }

    @Override
    public boolean isSetUnit() {
        return getUnitObject() != null && !getUnitObject().isEmpty();
    }

    @Override
    public boolean isSetValue() {
        return this.xml != null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.xml);
        hash = 41 * hash + Objects.hashCode(this.unit);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SosXmlValue other = (SosXmlValue) obj;
        if (!Objects.equals(this.unit, other.unit)) {
            return false;
        }
        if (!Objects.equals(this.xml, other.xml)) {
            return false;
        }
        return true;
    }

    @Override
    public <X, E extends Exception> X accept(ValueVisitor<X, E> visitor) throws E {
        return visitor.visit(this);
    }

}
