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
package net.sourceforge.joceanus.jmetis.field;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Field Set. This handles a set of fields for an item, populating the fields rendering and parsing
 * the data.
 */
public abstract class MetisFieldSetBase
        implements TethysEventProvider<MetisFieldEvent> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisFieldEvent> theEventManager;

    /**
     * The Data Formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Is the data being refreshed?
     */
    private boolean isRefreshing = false;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    public MetisFieldSetBase(final MetisDataFormatter pFormatter) {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public TethysEventRegistrar<MetisFieldEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public MetisDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Set refreshing data flag.
     * @param refreshingData true/false
     */
    public void setRefreshingData(final boolean refreshingData) {
        /* Record flag */
        isRefreshing = refreshingData;
    }

    /**
     * Notify that a field update has occurred.
     * @param pField the source field
     * @param pNewValue the new Value
     */
    protected void notifyUpdate(final MetisField pField,
                                final Object pNewValue) {
        /* If we are not refreshing data */
        if (!isRefreshing) {
            /* Create the notification */
            MetisFieldUpdate myUpdate = new MetisFieldUpdate(pField, pNewValue);

            /* Fire the notification */
            theEventManager.fireEvent(MetisFieldEvent.FIELDUPDATED, myUpdate);
        }
    }

    /**
     * Field Update Notification.
     */
    public static final class MetisFieldUpdate {
        /**
         * The field.
         */
        private final MetisField theField;

        /**
         * The new value.
         */
        private final Object theValue;

        /**
         * Constructor.
         * @param pField the source field
         * @param pNewValue the new Value
         */
        public MetisFieldUpdate(final MetisField pField,
                                final Object pNewValue) {
            theField = pField;
            theValue = pNewValue;
        }

        /**
         * Obtain the source field.
         * @return the field
         */
        public MetisField getField() {
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
}
