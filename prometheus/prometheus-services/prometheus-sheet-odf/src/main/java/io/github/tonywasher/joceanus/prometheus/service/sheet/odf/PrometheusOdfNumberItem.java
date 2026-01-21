/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.prometheus.service.sheet.odf;

import io.github.tonywasher.joceanus.prometheus.service.sheet.odf.PrometheusOdfNameSpace.PrometheusOdfItem;

/**
 * Number element.
 */
public enum PrometheusOdfNumberItem
        implements PrometheusOdfItem {
    /**
     * Text.
     */
    TEXT("text"),

    /**
     * Style.
     */
    STYLE("style"),

    /**
     * NumberStyle.
     */
    NUMBERSTYLE("number-style"),

    /**
     * DateStyle.
     */
    DATESTYLE("date-style"),

    /**
     * Textual.
     */
    TEXTUAL("textual"),

    /**
     * Day.
     */
    DAY("day"),

    /**
     * Month.
     */
    MONTH("month"),

    /**
     * Year.
     */
    YEAR("year"),

    /**
     * BooleanStyle.
     */
    BOOLEANSTYLE("boolean-style"),

    /**
     * BooleanValue.
     */
    BOOLEANVALUE("boolean-value"),

    /**
     * Boolean.
     */
    BOOLEAN("boolean"),

    /**
     * Number.
     */
    NUMBER("number"),

    /**
     * Grouping.
     */
    GROUPING("grouping"),

    /**
     * MinIntDigits.
     */
    MININTDIGITS("min-integer-digits"),

    /**
     * DecPlaces.
     */
    DECPLACES("decimal-places"),

    /**
     * Styles.
     */
    PERCENTAGESTYLE("percentage-style");

    /**
     * Name.
     */
    private final String theName;

    /**
     * Qualified Name.
     */
    private String theQualifiedName;

    /**
     * Constructor.
     *
     * @param pName the name
     */
    PrometheusOdfNumberItem(final String pName) {
        theName = pName;
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public String getQualifiedName() {
        if (theQualifiedName == null) {
            theQualifiedName = PrometheusOdfNameSpace.buildQualifiedName(this);
        }
        return theQualifiedName;
    }

    @Override
    public PrometheusOdfNameSpace getNameSpace() {
        return PrometheusOdfNameSpace.NUMBER;
    }
}
