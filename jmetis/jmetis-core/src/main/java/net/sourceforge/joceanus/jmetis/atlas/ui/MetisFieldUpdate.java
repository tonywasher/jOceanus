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

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Field Update.
 */
public class MetisFieldUpdate {
    /**
     * The field.
     */
    private final MetisFieldId theField;

    /**
     * The new value.
     */
    private final Object theValue;

    /**
     * Constructor.
     * @param pField the source field
     * @param pNewValue the new Value
     */
    public MetisFieldUpdate(final MetisFieldId pField,
                            final Object pNewValue) {
        theField = pField;
        theValue = pNewValue;
    }

    /**
     * Obtain the source field.
     * @return the field
     */
    public MetisFieldId getField() {
        return theField;
    }

    /**
     * Obtain the value as specific type.
     * @param <I> the value class
     * @param pClass the required class
     * @return the value
     * @throws OceanusException on error
     */
    public <I> I getValue(final Class<I> pClass) throws OceanusException {
        try {
            return pClass.cast(theValue);
        } catch (ClassCastException e) {
            throw new MetisDataException("Invalid dataType", e);
        }
    }

    /**
     * Obtain the value as String.
     * @return the value
     * @throws OceanusException on error
     */
    public String getString() throws OceanusException {
        return getValue(String.class);
    }

    /**
     * Obtain the value as Character Array.
     * @return the value
     * @throws OceanusException on error
     */
    public char[] getCharArray() throws OceanusException {
        return getValue(char[].class);
    }

    /**
     * Obtain the value as Short.
     * @return the value
     * @throws OceanusException on error
     */
    public Short getShort() throws OceanusException {
        return getValue(Short.class);
    }

    /**
     * Obtain the value as Integer.
     * @return the value
     * @throws OceanusException on error
     */
    public Integer getInteger() throws OceanusException {
        return getValue(Integer.class);
    }

    /**
     * Obtain the value as Long.
     * @return the value
     * @throws OceanusException on error
     */
    public Long getLong() throws OceanusException {
        return getValue(Long.class);
    }

    /**
     * Obtain the value as Boolean.
     * @return the value
     * @throws OceanusException on error
     */
    public Boolean getBoolean() throws OceanusException {
        return getValue(Boolean.class);
    }

    /**
     * Obtain the value as DateDay.
     * @return the value
     * @throws OceanusException on error
     */
    public TethysDate getDate() throws OceanusException {
        return getValue(TethysDate.class);
    }

    /**
     * Obtain the value as Money.
     * @return the value
     * @throws OceanusException on error
     */
    public TethysMoney getMoney() throws OceanusException {
        return getValue(TethysMoney.class);
    }

    /**
     * Obtain the value as Rate.
     * @return the value
     * @throws OceanusException on error
     */
    public TethysRate getRate() throws OceanusException {
        return getValue(TethysRate.class);
    }

    /**
     * Obtain the value as Price.
     * @return the value
     * @throws OceanusException on error
     */
    public TethysPrice getPrice() throws OceanusException {
        return getValue(TethysPrice.class);
    }

    /**
     * Obtain the value as Units.
     * @return the value
     * @throws OceanusException on error
     */
    public TethysUnits getUnits() throws OceanusException {
        return getValue(TethysUnits.class);
    }

    /**
     * Obtain the value as Dilution.
     * @return the value
     * @throws OceanusException on error
     */
    public TethysDilution getDilution() throws OceanusException {
        return getValue(TethysDilution.class);
    }

    /**
     * Obtain the value as Ratio.
     * @return the value
     * @throws OceanusException on error
     */
    public TethysRatio getRatio() throws OceanusException {
        return getValue(TethysRatio.class);
    }

    /**
     * Obtain the value as Event.
     * @return the value
     * @throws OceanusException on error
     */
    @SuppressWarnings("unchecked")
    public TethysEvent<TethysUIEvent> getEvent() throws OceanusException {
        return (TethysEvent<TethysUIEvent>) getValue(TethysEvent.class);
    }
}
