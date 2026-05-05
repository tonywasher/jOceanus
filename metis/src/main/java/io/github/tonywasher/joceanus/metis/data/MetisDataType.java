/*
 * Metis: Java Data Framework
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
package io.github.tonywasher.joceanus.metis.data;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataIndexedItem;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

import java.util.List;

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
     *
     * @return true/false
     */
    public boolean isNumeric() {
        return switch (this) {
            case SHORT, INTEGER, LONG, MONEY, PRICE, RATE, UNITS, RATIO -> true;
            default -> false;
        };
    }

    /**
     * Obtain class for item.
     *
     * @return the class (or null if indeterminate)
     */
    public Class<?> getDataTypeClass() {
        return switch (this) {
            case STRING -> String.class;
            case BYTEARRAY -> byte[].class;
            case CHARARRAY -> char[].class;
            case ENUM -> Enum.class;
            case BOOLEAN -> Boolean.class;
            case SHORT -> Short.class;
            case INTEGER -> Integer.class;
            case LONG -> Long.class;
            case DATE -> OceanusDate.class;
            case MONEY -> OceanusMoney.class;
            case PRICE -> OceanusPrice.class;
            case UNITS -> OceanusUnits.class;
            case RATE -> OceanusRate.class;
            case RATIO -> OceanusRatio.class;
            case LINK, LINKPAIR -> MetisDataIndexedItem.class;
            case LINKSET -> List.class;
            default -> null;
        };
    }
}
