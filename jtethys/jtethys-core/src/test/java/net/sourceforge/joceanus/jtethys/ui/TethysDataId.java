/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

/**
 * Column Ids.
 */
public enum TethysDataId {
    /**
     * Name.
     */
    NAME("Name"),

    /**
     * Password.
     */
    PASSWORD("Password"),

    /**
     * Date.
     */
    DATE("Date"),

    /**
     * Short.
     */
    SHORT("Short"),

    /**
     * Integer.
     */
    INTEGER("Integer"),

    /**
     * Long.
     */
    LONG("Long"),

    /**
     * Boolean.
     */
    BOOLEAN("Boolean"),

    /**
     * XtraBoolean.
     */
    XTRABOOL("XtraBoolean"),

    /**
     * Money.
     */
    MONEY("Money"),

    /**
     * Price.
     */
    PRICE("Price"),

    /**
     * Rate.
     */
    RATE("Rate"),

    /**
     * Ratio.
     */
    RATIO("Ratio"),

    /**
     * Units.
     */
    UNITS("Units"),

    /**
     * Dilution.
     */
    DILUTION("Dilution"),

    /**
     * DilutedPrice.
     */
    DILUTEDPRICE("DilutedPrice"),

    /**
     * Scroll.
     */
    SCROLL("Scroll"),

    /**
     * List.
     */
    LIST("List"),

    /**
     * Updates.
     */
    UPDATES("Updates");

    /**
     * The name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pName the name
     */
    TethysDataId(final String pName) {
        theName = pName;
    }

    @Override
    public String toString() {
        return theName;
    }
}
