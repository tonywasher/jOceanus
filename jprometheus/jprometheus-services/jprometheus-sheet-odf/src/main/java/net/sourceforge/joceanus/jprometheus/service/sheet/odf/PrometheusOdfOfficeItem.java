/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.service.sheet.odf;

import net.sourceforge.joceanus.jprometheus.service.sheet.odf.PrometheusOdfNameSpace.PrometheusOdfItem;

/**
 * Office element.
 */
public enum PrometheusOdfOfficeItem
    implements PrometheusOdfItem {
    /**
     * Body.
     */
    BODY("body"),

    /**
     * SpreadSheet.
     */
    SPREADSHEET("spreadsheet"),

    /**
     * ValueType.
     */
    VALUETYPE("value-type"),

    /**
     * Value.
     */
    VALUE("value"),

    /**
     * DateValue.
     */
    DATEVALUE("date-value"),

    /**
     * BooleanValue.
     */
    BOOLEANVALUE("boolean-value"),

    /**
     * Currency.
     */
    CURRENCY("currency"),

    /**
     * Styles.
     */
    STYLES("automatic-styles");

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
     * @param pName the name
     */
    PrometheusOdfOfficeItem(final String pName) {
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
        return PrometheusOdfNameSpace.OFFICE;
    }
}
