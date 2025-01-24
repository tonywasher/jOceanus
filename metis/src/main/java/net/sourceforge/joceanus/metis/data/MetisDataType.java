/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.metis.data;

import java.util.List;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataIndexedItem;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;

/**
 * Enumeration of data types.
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
     * Enum.
     */
    ENUM,

    /**
     * Date.
     */
    DATE,

    /**
     * ByteArray.
     */
    BYTEARRAY,

    /**
     * CharArray.
     */
    CHARARRAY,

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
     * Ratio.
     */
    RATIO,

    /**
     * Link.
     */
    LINK,

    /**
     * LinkPair.
     */
    LINKPAIR,

    /**
     * LinkSet.
     */
    LINKSET,

    /**
     * Object (not relevant/none-of-the-above).
     */
    OBJECT,

    /**
     * Context (varies on context).
     */
    CONTEXT;

    /**
     * is the dataType numeric?
     * @return true/false
     */
    public boolean isNumeric() {
        switch (this) {
            case SHORT:
            case INTEGER:
            case LONG:
            case MONEY:
            case PRICE:
            case RATE:
            case UNITS:
            case RATIO:
                return true;
            default:
                return false;
        }
    }

    /**
     * Obtain class for item.
     * @return the class (or null if indeterminate)
     */
    public Class<?> getDataTypeClass() {
        switch (this) {
            case STRING:
                return String.class;
            case BYTEARRAY:
                return byte[].class;
            case CHARARRAY:
                return char[].class;
            case ENUM:
                return Enum.class;
            case BOOLEAN:
                return Boolean.class;
            case SHORT:
                return Short.class;
            case INTEGER:
                return Integer.class;
            case LONG:
                return Long.class;
            case DATE:
                return OceanusDate.class;
            case MONEY:
                return OceanusMoney.class;
            case PRICE:
                return OceanusPrice.class;
            case UNITS:
                return OceanusUnits.class;
            case RATE:
                return OceanusRate.class;
            case RATIO:
                return OceanusRatio.class;
            case LINK:
            case LINKPAIR:
                return MetisDataIndexedItem.class;
            case LINKSET:
                return List.class;
            default:
                return null;
        }
    }
}
