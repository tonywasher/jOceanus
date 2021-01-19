/*******************************************************************************
 * Metis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.test.atlas.ui;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSimpleId;

/**
 * Test Data FieldIds.
 */
public enum MetisTestDataField
        implements MetisDataFieldId {
    /**
     * Name.
     */
    NAME(MetisDataType.STRING),

    /**
     * Password.
     */
    PASSWORD(MetisDataType.CHARARRAY),

    /**
     * Date.
     */
    DATE(MetisDataType.DATE),

    /**
     * Boolean.
     */
    BOOLEAN(MetisDataType.BOOLEAN),

    /**
     * XtraBoolean.
     */
    XTRABOOL(MetisDataType.BOOLEAN),

    /**
     * Short.
     */
    SHORT(MetisDataType.SHORT),

    /**
     * Integer.
     */
    INTEGER(MetisDataType.INTEGER),

    /**
     * Long.
     */
    LONG(MetisDataType.LONG),

    /**
     * Money.
     */
    MONEY(MetisDataType.MONEY),

    /**
     * Price.
     */
    PRICE(MetisDataType.PRICE),

    /**
     * Units.
     */
    UNITS(MetisDataType.UNITS),

    /**
     * Rate.
     */
    RATE(MetisDataType.RATE),

    /**
     * Ratio.
     */
    RATIO(MetisDataType.RATIO),

    /**
     * Dilution.
     */
    DILUTION(MetisDataType.DILUTION),

    /**
     * DilutedPrice.
     */
    DILUTEDPRICE(MetisDataType.DILUTEDPRICE),

    /**
     * Scroll.
     */
    SCROLL(MetisDataType.LINK),

    /**
     * List.
     */
    LIST(MetisDataType.LINKSET),

    /**
     * Updates.
     */
    UPDATES(MetisDataType.CONTEXT);

    /**
     * The FieldId.
     */
    private final MetisDataFieldId theField;

    /**
     * The DataType.
     */
    private final MetisDataType theDataType;

    /**
     * Constructor.
     * @param pField the field
     */
    MetisTestDataField(final MetisDataType pField) {
        theField = new MetisFieldSimpleId(name());
        theDataType = pField;
    }

    @Override
    public String getId() {
        return theField.getId();
    }

    @Override
    public String toString() {
        return getId();
    }

    /**
     * Obtain dataType.
     * @return the dataType
     */
    public MetisDataType getDataType() {
        return theDataType;
    }
}
