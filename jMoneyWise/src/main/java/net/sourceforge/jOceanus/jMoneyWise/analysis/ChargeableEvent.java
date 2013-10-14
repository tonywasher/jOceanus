/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.analysis;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

/**
 * Chargeable event for LifeBonds.
 * @author Tony
 */
public final class ChargeableEvent
        implements OrderedIdItem<Integer>, JDataContents, Comparable<ChargeableEvent> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(ChargeableEvent.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName();
    }

    /**
     * The Gains field id.
     */
    public static final JDataField FIELD_GAINS = FIELD_DEFS.declareEqualityField("Gains");

    /**
     * The Slice field id.
     */
    public static final JDataField FIELD_SLICE = FIELD_DEFS.declareEqualityField("Slice");

    /**
     * The Taxation field id.
     */
    public static final JDataField FIELD_TAXATION = FIELD_DEFS.declareEqualityField("Taxation");

    /**
     * The Event field id.
     */
    public static final JDataField FIELD_EVENT = FIELD_DEFS.declareEqualityField("Event");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_GAINS.equals(pField)) {
            return theGains;
        }
        if (FIELD_SLICE.equals(pField)) {
            return theSlice;
        }
        if (FIELD_TAXATION.equals(pField)) {
            return theTaxation;
        }
        if (FIELD_EVENT.equals(pField)) {
            return theEvent;
        }
        return JDataFieldValue.UnknownField;
    }

    /**
     * The Gains.
     */
    private final JMoney theGains;

    /**
     * The Slice.
     */
    private final JMoney theSlice;

    /**
     * The Taxation.
     */
    private JMoney theTaxation = null;

    /**
     * The Event.
     */
    private final Event theEvent;

    /**
     * Obtain the amount.
     * @return the amount
     */
    public JMoney getAmount() {
        return theGains;
    }

    /**
     * Obtain the slice.
     * @return the slice
     */
    public JMoney getSlice() {
        return theSlice;
    }

    /**
     * Obtain the taxation.
     * @return the taxation
     */
    public JMoney getTaxation() {
        return theTaxation;
    }

    /**
     * Obtain the event.
     * @return the event
     */
    public Event getEvent() {
        return theEvent;
    }

    @Override
    public Integer getOrderedId() {
        return getEvent().getId();
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public JDateDay getDate() {
        return getEvent().getDate();
    }

    /**
     * Obtain the comments.
     * @return the comments
     */
    public String getComments() {
        return getEvent().getComments();
    }

    /**
     * Obtain the tax credit.
     * @return the tax credit
     */
    public JMoney getTaxCredit() {
        return getEvent().getTaxCredit();
    }

    /**
     * Obtain the qualifying years.
     * @return the years
     */
    public Integer getYears() {
        return getEvent().getYears();
    }

    /**
     * Constructor.
     * @param pEvent the Event
     * @param pGains the Gains
     */
    private ChargeableEvent(final Event pEvent,
                            final JMoney pGains) {
        /* Calculate slice */
        theSlice = new JMoney(pGains);
        theSlice.divide(pEvent.getYears());

        /* Store the values */
        theGains = pGains;
        theEvent = pEvent;
    }

    @Override
    public int compareTo(final ChargeableEvent pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Make sure that the object is a ChargeableEvent */
        if (pThat.getClass() != this.getClass()) {
            return -1;
        }

        /* Access the object as a Chargeable Event */
        ChargeableEvent myThat = (ChargeableEvent) pThat;

        /* Compare the underlying events */
        return getEvent().compareTo(myThat.getEvent());
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (getClass() != pThat.getClass()) {
            return false;
        }

        /* Access as Chargeable Event */
        ChargeableEvent myThat = (ChargeableEvent) pThat;

        /* Check equality */
        return Difference.isEqual(getEvent(), myThat.getEvent());
    }

    @Override
    public int hashCode() {
        return getEvent().hashCode();
    }

    /**
     * Apply taxation of total slice to the individual events. This tax is first split proportionally among the slices and then multiplied by the years of each
     * individual event
     * @param pTax the calculated taxation for the slice
     * @param pTotal the slice total of the event list
     */
    protected void applyTax(final JMoney pTax,
                            final JMoney pTotal) {
        /* Calculate the portion of tax that applies to this slice */
        JMoney myPortion = pTax.valueAtWeight(getSlice(), pTotal);

        /* Multiply by the number of years */
        theTaxation = new JMoney(myPortion);
        theTaxation.multiply(getYears());
    }

    /**
     * List of ChargeableEvents.
     */
    public static class ChargeableEventList
            extends OrderedIdList<Integer, ChargeableEvent>
            implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(ChargeableEventList.class.getSimpleName());

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName()
                   + "("
                   + size()
                   + ")";
        }

        /**
         * Size Field Id.
         */
        public static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UnknownField;
        }

        /**
         * Constructor.
         */
        public ChargeableEventList() {
            super(ChargeableEvent.class);
        }

        /**
         * Add Chargeable Event to List.
         * @param pEvent the base event
         * @param pGains the gains
         */
        public void addEvent(final Event pEvent,
                             final JMoney pGains) {
            /* Create the chargeable event */
            ChargeableEvent myEvent = new ChargeableEvent(pEvent, pGains);

            /* Add it to the list */
            append(myEvent);
        }

        /**
         * Get the SliceTotal of the chargeable event list. Each slice is the Value of the event divided by the number of years that the charge is to be sliced
         * over
         * @return the slice total of the chargeable event list
         */
        public JMoney getSliceTotal() {
            /* Access the iterator */
            Iterator<ChargeableEvent> myIterator = iterator();

            /* Initialise the total */
            JMoney myTotal = new JMoney();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                ChargeableEvent myEvent = myIterator.next();

                /* Add in this slice */
                myTotal.addAmount(myEvent.getSlice());
            }

            /* Return the total */
            return myTotal;
        }

        /**
         * Get the TaxTotal of the chargeable event list. This is the total of the tax that has been apportioned to each slice
         * @return the tax total of the chargeable event list
         */
        public JMoney getTaxTotal() {
            /* Access the iterator */
            Iterator<ChargeableEvent> myIterator = iterator();

            /* Initialise the total */
            JMoney myTotal = new JMoney();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                ChargeableEvent myEvent = myIterator.next();

                /* Add in this slice */
                myTotal.addAmount(myEvent.getTaxation());
            }

            /* Return the total */
            return myTotal;
        }

        /**
         * Get the GainsTotal of the chargeable event list. Each slice is the Value of the event divided by the number of years that the charge is to be sliced
         * over
         * @return the slice total of the chargeable event list
         */
        public JMoney getGainsTotal() {
            /* Access the iterator */
            Iterator<ChargeableEvent> myIterator = iterator();

            /* Initialise the total */
            JMoney myTotal = new JMoney();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                ChargeableEvent myEvent = myIterator.next();

                /* Add in this slice */
                myTotal.addAmount(myEvent.getAmount());
            }

            /* Return the total */
            return myTotal;
        }

        /**
         * Apply taxation of total slice to the individual events. This tax is first split proportionally among the slices and then multiplied by the years of
         * each individual event
         * @param pTax the calculated taxation for the slice
         * @param pTotal the slice total of the event list
         */
        public void applyTax(final JMoney pTax,
                             final JMoney pTotal) {
            /* Access the iterator */
            Iterator<ChargeableEvent> myIterator = iterator();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                ChargeableEvent myEvent = myIterator.next();

                /* Apply tax to this slice */
                myEvent.applyTax(pTax, pTotal);
            }
        }
    }
}
