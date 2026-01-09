/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012-2026 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.prometheus.service.sheet.odf;

/**
 * Oasis Attribute values.
 */
public enum PrometheusOdfValue {
    /**
     * Collapse.
     */
    COLLAPSE("collapse"),

    /**
     * Visible.
     */
    VISIBLE("visible"),

    /**
     * Column.
     */
    COLUMN("column"),

    /**
     * Boolean.
     */
    BOOLEAN("boolean"),

    /**
     * Currency.
     */
    CURRENCY("currency"),

    /**
     * Date.
     */
    DATE("date"),

    /**
     * Float.
     */
    FLOAT("float"),

    /**
     * Percentage.
     */
    PERCENTAGE("percentage"),

    /**
     * String.
     */
    STRING("string");

    /**
     * The value.
     */
    private final String theValue;

    /**
     * Constructor.
     * @param pValue the value
     */
    PrometheusOdfValue(final String pValue) {
        theValue = pValue;
    }

    /**
     * Obtain the value.
     * @return the value
     */
    public String getValue() {
        return theValue;
    }

    /**
     * Obtain matching value.
     * @param pValue the value
     * @return the matching value (or null)
     */
    public static PrometheusOdfValue findValueType(final String pValue) {
        /* Handle null case specially */
        if (pValue == null) {
            return null;
        }

        /* Loop to find the value type */
        for (final PrometheusOdfValue myValue : values()) {
            if (pValue.equalsIgnoreCase(myValue.getValue())) {
                return myValue;
            }
        }

        /* Type not found */
        return null;
    }
}
