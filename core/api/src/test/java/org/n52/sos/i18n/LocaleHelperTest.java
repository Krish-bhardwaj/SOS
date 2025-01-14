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
package org.n52.sos.i18n;

import org.hamcrest.Matchers;

import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import org.n52.janmayen.i18n.LocaleHelper;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class LocaleHelperTest {
    @Rule
    public final ErrorCollector errors = new ErrorCollector();
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSerialization() {
        String string = LocaleHelper.encode(Locale.GERMAN);
        errors.checkThat(LocaleHelper.decode(string, null), Matchers.is(Locale.GERMAN));
    }

    @Test
    public void test() {

        //IETF BCP 47
        // ISO 639 alpha-2 or alpha-3
        String string = LocaleHelper.encode(Locale.GERMAN);
        //System.out.println(Locale.GERMAN.toLanguageTag());
        errors.checkThat(LocaleHelper.decode(string, null), Matchers.is(Locale.GERMAN));
        errors.checkThat(LocaleHelper.decode("de_DE", null), Matchers.is(Locale.GERMANY));
        errors.checkThat(LocaleHelper.decode("de DE", null), Matchers.is(Locale.GERMANY));
        errors.checkThat(LocaleHelper.decode("de-de", null), Matchers.is(Locale.GERMANY));
        errors.checkThat(LocaleHelper.decode("de-DE", null), Matchers.is(Locale.GERMANY));
        errors.checkThat(LocaleHelper.decode("deu", null), Matchers.is(new Locale("deu")));
    }
}
