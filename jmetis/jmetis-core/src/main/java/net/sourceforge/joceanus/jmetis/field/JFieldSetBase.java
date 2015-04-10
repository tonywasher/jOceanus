/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.field;

import net.sourceforge.joceanus.jmetis.JMetisDataException;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusItemEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;

/**
 * Field Set. This handles a set of fields for an item, populating the fields rendering and parsing the data.
 */
public abstract class JFieldSetBase
        implements JOceanusEventProvider {
    /**
     * The Event Manager.
     */
    private final JOceanusEventManager theEventManager;

    /**
     * The Data Formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Is the data being refreshed?
     */
    private boolean isRefreshing = false;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    public JFieldSetBase(final JDataFormatter pFormatter) {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Create the event manager */
        theEventManager = new JOceanusEventManager();
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public JDataFormatter getDataFormatter() {
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
    protected void notifyUpdate(final JDataField pField,
                                final Object pNewValue) {
        /* If we are not refreshing data */
        if (!isRefreshing) {
            /* Create the notification */
            FieldUpdate myUpdate = new FieldUpdate(pField, pNewValue);

            /* Fire the notification */
            theEventManager.fireActionEvent(myUpdate);
        }
    }

    /**
     * Field Update Notification.
     */
    public static final class FieldUpdate {
        /**
         * The field.
         */
        private final JDataField theField;

        /**
         * The new value.
         */
        private final Object theValue;

        /**
         * Constructor.
         * @param pField the source field
         * @param pNewValue the new Value
         */
        protected FieldUpdate(final JDataField pField,
                              final Object pNewValue) {
            theField = pField;
            theValue = pNewValue;
        }

        /**
         * Obtain the source field.
         * @return the field
         */
        public JDataField getField() {
            return theField;
        }

        /**
         * Obtain the value as specific type.
         * @param <I> the value class
         * @param pClass the required class
         * @return the value
         * @throws JOceanusException on error
         */
        public <I> I getValue(final Class<I> pClass) throws JOceanusException {
            try {
                return pClass.cast(theValue);
            } catch (ClassCastException e) {
                throw new JMetisDataException("Invalid dataType", e);
            }
        }

        /**
         * Obtain the value as String.
         * @return the value
         * @throws JOceanusException on error
         */
        public String getString() throws JOceanusException {
            return getValue(String.class);
        }

        /**
         * Obtain the value as Character Array.
         * @return the value
         * @throws JOceanusException on error
         */
        public char[] getCharArray() throws JOceanusException {
            return getValue(char[].class);
        }

        /**
         * Obtain the value as Short.
         * @return the value
         * @throws JOceanusException on error
         */
        public Short getShort() throws JOceanusException {
            return getValue(Short.class);
        }

        /**
         * Obtain the value as Integer.
         * @return the value
         * @throws JOceanusException on error
         */
        public Integer getInteger() throws JOceanusException {
            return getValue(Integer.class);
        }

        /**
         * Obtain the value as Long.
         * @return the value
         * @throws JOceanusException on error
         */
        public Long getLong() throws JOceanusException {
            return getValue(Long.class);
        }

        /**
         * Obtain the value as Boolean.
         * @return the value
         * @throws JOceanusException on error
         */
        public Boolean getBoolean() throws JOceanusException {
            return getValue(Boolean.class);
        }

        /**
         * Obtain the value as DateDay.
         * @return the value
         * @throws JOceanusException on error
         */
        public JDateDay getDateDay() throws JOceanusException {
            return getValue(JDateDay.class);
        }

        /**
         * Obtain the value as Money.
         * @return the value
         * @throws JOceanusException on error
         */
        public JMoney getMoney() throws JOceanusException {
            return getValue(JMoney.class);
        }

        /**
         * Obtain the value as Rate.
         * @return the value
         * @throws JOceanusException on error
         */
        public JRate getRate() throws JOceanusException {
            return getValue(JRate.class);
        }

        /**
         * Obtain the value as Price.
         * @return the value
         * @throws JOceanusException on error
         */
        public JPrice getPrice() throws JOceanusException {
            return getValue(JPrice.class);
        }

        /**
         * Obtain the value as Units.
         * @return the value
         * @throws JOceanusException on error
         */
        public JUnits getUnits() throws JOceanusException {
            return getValue(JUnits.class);
        }

        /**
         * Obtain the value as Dilution.
         * @return the value
         * @throws JOceanusException on error
         */
        public JDilution getDilution() throws JOceanusException {
            return getValue(JDilution.class);
        }

        /**
         * Obtain the value as ItemEvent.
         * @return the value
         * @throws JOceanusException on error
         */
        public JOceanusItemEvent getItemEvent() throws JOceanusException {
            return getValue(JOceanusItemEvent.class);
        }
    }
}
