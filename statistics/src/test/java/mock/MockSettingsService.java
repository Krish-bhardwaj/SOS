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
package mock;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.SettingDefinition;
import org.n52.faroe.SettingValue;
import org.n52.faroe.SettingValueFactory;
import org.n52.faroe.SettingsService;

public class MockSettingsService implements SettingsService {

    @Override
    public void changeSetting(SettingValue<?> newValue) throws ConfigurationError {

    }

    @Override
    public void configure(Object object) throws ConfigurationError {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void deleteSetting(SettingDefinition<?> setting) throws ConfigurationError {

    }

    @Override
    public SettingDefinition<?> getDefinitionByKey(String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getKeys() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> SettingValue<T> getSetting(SettingDefinition<T> key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> SettingValue<T> getSetting(String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SettingDefinition<?>> getSettingDefinitions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public SettingValueFactory getSettingFactory() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<SettingDefinition<?>, SettingValue<?>> getSettings() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void reconfigure() {
        // TODO Auto-generated method stub

    }

    @Override
    public void configureOnce(Object object) throws ConfigurationError {
    }

    @Override
    public void addSetting(SettingDefinition<?> def) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addSettings(Collection<SettingDefinition<?>> defs) {
        // TODO Auto-generated method stub

    }

}
