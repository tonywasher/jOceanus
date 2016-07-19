/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.data;

/**
 * Enumeration of data types.
 * @author Tony Washer
 */
public enum MetisDataType {
    /**
     * String.
     */
    STRING,

    /**
     * Short.
     */
    SHORT,

    /**
     * Integer.
     */
    INTEGER,

    /**
     * Long.
     */
    LONG,

    /**
     * Boolean.
     */
    BOOLEAN,

    /**
     * Float.
     */
    FLOAT,

    /**
     * Double.
     */
    DOUBLE,

    /**
     * Date.
     */
    DATE,

    /**
     * Date.
     */
    DATEDAY,

    /**
     * CharArray.
     */
    CHARARRAY,

    /**
     * BigInteger.
     */
    BIGINTEGER,

    /**
     * BigDecimal.
     */
    BIGDECIMAL,

    /**
     * Money.
     */
    MONEY,

    /**
     * Rate.
     */
    RATE,

    /**
     * Units.
     */
    UNITS,

    /**
     * Price.
     */
    PRICE,

    /**
     * Dilution.
     */
    DILUTION,

    /**
     * Ratio.
     */
    RATIO,

    /**
     * Link.
     */
    LINK,

    /**
     * LinkSet.
     */
    LINKSET;

    /**
     * is the dataType numeric?
     * @return true/false
     */
    public boolean isNumeric() {
        switch (this) {
            case STRING:
            case CHARARRAY:
            case LINK:
            case LINKSET:
            case DATE:
            case DATEDAY:
                return false;
            default:
                return true;
        }
    }
}
