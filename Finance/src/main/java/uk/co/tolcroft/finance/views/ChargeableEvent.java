/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.finance.views;

import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.ReportItem;
import net.sourceforge.JDataManager.ReportList;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JSortedList.SortedListIterator;
import uk.co.tolcroft.finance.data.Event;

/**
 * Chargeable event for LifeBonds.
 * @author Tony
 */
public final class ChargeableEvent extends ReportItem<ChargeableEvent> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(ChargeableEvent.class.getSimpleName(),
            ReportItem.theLocalFields);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
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
        return super.getFieldValue(pField);
    }

    /**
     * The Gains.
     */
    private final Money theGains;

    /**
     * The Slice.
     */
    private final Money theSlice;

    /**
     * The Taxation.
     */
    private Money theTaxation = null;

    /**
     * The Event.
     */
    private final Event theEvent;

    /**
     * Obtain the amount.
     * @return the amount
     */
    public Money getAmount() {
        return theGains;
    }

    /**
     * Obtain the slice.
     * @return the slice
     */
    public Money getSlice() {
        return theSlice;
    }

    /**
     * Obtain the taxation.
     * @return the taxation
     */
    public Money getTaxation() {
        return theTaxation;
    }

    /**
     * Obtain the event.
     * @return the event
     */
    public Event getEvent() {
        return theEvent;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public DateDay getDate() {
        return getEvent().getDate();
    }

    /**
     * Obtain the description.
     * @return the description
     */
    public String getDesc() {
        return getEvent().getDesc();
    }

    /**
     * Obtain the tax credit.
     * @return the tax credit
     */
    public Money getTaxCredit() {
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
     * @param pList the list
     * @param pEvent the Event
     * @param pGains the Gains
     */
    private ChargeableEvent(final ChargeableEventList pList,
                            final Event pEvent,
                            final Money pGains) {
        /* Call super constructor */
        super(pList);

        /* Local variables */
        long myValue;

        /* Calculate slice */
        myValue = pGains.getAmount();
        myValue /= pEvent.getYears();

        /* Store the values */
        theGains = pGains;
        theSlice = new Money(myValue);
        theEvent = pEvent;
    }

    /**
     * Compare this ChargeableEvent to another to establish sort order.
     * @param pThat The Event to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
     */
    @Override
    public int compareTo(final Object pThat) {
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

    /**
     * Apply taxation of total slice to the individual events. This tax is first split proportionally among
     * the slices and then multiplied by the years of each individual event
     * @param pTax the calculated taxation for the slice
     * @param pTotal the slice total of the event list
     */
    protected void applyTax(final Money pTax,
                            final Money pTotal) {
        /* Calculate the portion of tax that applies to this slice */
        Money myPortion = pTax.valueAtWeight(getSlice(), pTotal);

        /* Multiply by the number of years */
        long myValue = myPortion.getValue() * getYears();
        theTaxation = new Money(myValue);
    }

    /**
     * List of ChargeableEvents.
     */
    public static class ChargeableEventList extends ReportList<ChargeableEvent> {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(
                ChargeableEventList.class.getSimpleName(), ReportList.theLocalFields);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
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
                             final Money pGains) {
            /* Create the chargeable event */
            ChargeableEvent myEvent = new ChargeableEvent(this, pEvent, pGains);

            /* Add it to the list */
            add(myEvent);
        }

        /**
         * Get the SliceTotal of the chargeable event list. Each slice is the Value of the event divided by
         * the number of years that the charge is to be sliced over
         * @return the slice total of the chargeable event list
         */
        public Money getSliceTotal() {
            /* Access the iterator */
            SortedListIterator<ChargeableEvent> myIterator = listIterator();

            /* Initialise the total */
            Money myTotal = new Money(0);

            /* Loop through the list */
            ChargeableEvent myEvent;
            while ((myEvent = myIterator.next()) != null) {
                /* Add in this slice */
                myTotal.addAmount(myEvent.getSlice());
            }

            /* Return the total */
            return myTotal;
        }

        /**
         * Get the TaxTotal of the chargeable event list. This is the total of the tax that has been
         * apportioned to each slice
         * @return the tax total of the chargeable event list
         */
        public Money getTaxTotal() {
            /* Access the iterator */
            SortedListIterator<ChargeableEvent> myIterator = listIterator();

            /* Initialise the total */
            Money myTotal = new Money(0);

            /* Loop through the list */
            ChargeableEvent myEvent;
            while ((myEvent = myIterator.next()) != null) {
                /* Add in this slice */
                myTotal.addAmount(myEvent.getTaxation());
            }

            /* Return the total */
            return myTotal;
        }

        /**
         * Get the GainsTotal of the chargeable event list. Each slice is the Value of the event divided by
         * the number of years that the charge is to be sliced over
         * @return the slice total of the chargeable event list
         */
        public Money getGainsTotal() {
            /* Access the iterator */
            SortedListIterator<ChargeableEvent> myIterator = listIterator();

            /* Initialise the total */
            Money myTotal = new Money(0);

            /* Loop through the list */
            ChargeableEvent myEvent;
            while ((myEvent = myIterator.next()) != null) {
                /* Add in this slice */
                myTotal.addAmount(myEvent.getAmount());
            }

            /* Return the total */
            return myTotal;
        }

        /**
         * Apply taxation of total slice to the individual events. This tax is first split proportionally
         * among the slices and then multiplied by the years of each individual event
         * @param pTax the calculated taxation for the slice
         * @param pTotal the slice total of the event list
         */
        public void applyTax(final Money pTax,
                             final Money pTotal) {
            /* Access the iterator */
            SortedListIterator<ChargeableEvent> myIterator = listIterator();

            /* Loop through the list */
            ChargeableEvent myEvent;
            while ((myEvent = myIterator.next()) != null) {
                /* Apply tax to this slice */
                myEvent.applyTax(pTax, pTotal);
            }
        }
    }
}
