/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.data;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisIndexedItem;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;

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
     * Dilution.
     */
    DILUTION,

    /**
     * DilutedPrice.
     */
    DILUTEDPRICE,

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
            case STRING:
            case BYTEARRAY:
            case CHARARRAY:
            case LINK:
            case LINKSET:
            case DATE:
            case BOOLEAN:
            case ENUM:
                return false;
            default:
                return true;
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
                return TethysDate.class;
            case MONEY:
                return TethysMoney.class;
            case PRICE:
                return TethysPrice.class;
            case UNITS:
                return TethysUnits.class;
            case RATE:
                return TethysRate.class;
            case RATIO:
                return TethysRatio.class;
            case DILUTION:
                return TethysDilution.class;
            case DILUTEDPRICE:
                return TethysDilutedPrice.class;
            case LINK:
                return MetisIndexedItem.class;
            case LINKSET:
                return TethysItemList.class;
            default:
                return null;
        }
    }
}
