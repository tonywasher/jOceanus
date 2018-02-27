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
package net.sourceforge.joceanus.jmetis.atlas.ui;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSimpleId;
import net.sourceforge.joceanus.jtethys.ui.TethysDataId;

/**
 * Test Data FieldIds.
 */
public enum MetisTestDataField
        implements MetisDataFieldId {
    /**
     * Name.
     */
    NAME(TethysDataId.NAME),

    /**
     * Password.
     */
    PASSWORD(TethysDataId.PASSWORD),

    /**
     * Date.
     */
    DATE(TethysDataId.DATE),

    /**
     * Boolean.
     */
    BOOLEAN(TethysDataId.BOOLEAN),

    /**
     * XtraBoolean.
     */
    XTRABOOL(TethysDataId.XTRABOOL),

    /**
     * Short.
     */
    SHORT(TethysDataId.SHORT),

    /**
     * Integer.
     */
    INTEGER(TethysDataId.INTEGER),

    /**
     * Long.
     */
    LONG(TethysDataId.LONG),

    /**
     * Money.
     */
    MONEY(TethysDataId.MONEY),

    /**
     * Price.
     */
    PRICE(TethysDataId.PRICE),

    /**
     * Units.
     */
    UNITS(TethysDataId.UNITS),

    /**
     * Rate.
     */
    RATE(TethysDataId.RATE),

    /**
     * Ratio.
     */
    RATIO(TethysDataId.RATIO),

    /**
     * Dilution.
     */
    DILUTION(TethysDataId.DILUTION),

    /**
     * DilutedPrice.
     */
    DILUTEDPRICE(TethysDataId.DILUTEDPRICE),

    /**
     * Scroll.
     */
    SCROLL(TethysDataId.SCROLL),

    /**
     * List.
     */
    LIST(TethysDataId.LIST),

    /**
     * Updates.
     */
    UPDATES(TethysDataId.UPDATES);

    /**
     * The FieldId.
     */
    private final MetisDataFieldId theField;

    /**
     * Constructor.
     * @param pField the field
     */
    MetisTestDataField(final TethysDataId pField) {
        theField = new MetisFieldSimpleId(pField.toString());
    }

    @Override
    public String getId() {
        return theField.getId();
    }

    @Override
    public String toString() {
        return getId();
    }
}
