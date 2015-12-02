/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdItem;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Chargeable event for LifeBonds.
 */
public final class ChargeableEvent
        implements MetisOrderedIdItem<Integer>, MetisDataContents, Comparable<ChargeableEvent> {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.CHARGE_NAME.getValue());

    /**
     * The Gains field id.
     */
    private static final MetisField FIELD_GAINS = FIELD_DEFS.declareEqualityField(AnalysisResource.SECURITYATTR_GAINS.getValue());

    /**
     * The Slice field id.
     */
    private static final MetisField FIELD_SLICE = FIELD_DEFS.declareEqualityField(AnalysisResource.CHARGE_SLICE.getValue());

    /**
     * The Taxation field id.
     */
    private static final MetisField FIELD_TAXATION = FIELD_DEFS.declareEqualityField(AnalysisResource.CHARGE_TAX.getValue());

    /**
     * The Transaction field id.
     */
    private static final MetisField FIELD_TRANS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TRANSACTION.getItemName());

    /**
     * The Gains.
     */
    private final TethysMoney theGains;

    /**
     * The Slice.
     */
    private final TethysMoney theSlice;

    /**
     * The Taxation.
     */
    private TethysMoney theTaxation = null;

    /**
     * The Transaction.
     */
    private final Transaction theTransaction;

    /**
     * Constructor.
     * @param pTrans the Transaction
     * @param pGains the Gains
     */
    protected ChargeableEvent(final Transaction pTrans,
                              final TethysMoney pGains) {
        /* Calculate slice */
        theSlice = new TethysMoney(pGains);
        theSlice.divide(pTrans.getYears());

        /* Store the values */
        theGains = pGains;
        theTransaction = pTrans;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName();
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_GAINS.equals(pField)) {
            return theGains;
        }
        if (FIELD_SLICE.equals(pField)) {
            return theSlice;
        }
        if (FIELD_TAXATION.equals(pField)) {
            return theTaxation == null
                                      ? MetisFieldValue.SKIP
                                      : theTaxation;
        }
        if (FIELD_TRANS.equals(pField)) {
            return theTransaction;
        }
        return MetisFieldValue.UNKNOWN;
    }

    /**
     * Obtain the amount.
     * @return the amount
     */
    public TethysMoney getAmount() {
        return theGains;
    }

    /**
     * Obtain the slice.
     * @return the slice
     */
    public TethysMoney getSlice() {
        return theSlice;
    }

    /**
     * Obtain the taxation.
     * @return the taxation
     */
    public TethysMoney getTaxation() {
        return theTaxation;
    }

    /**
     * Obtain the event.
     * @return the event
     */
    public Transaction getTransaction() {
        return theTransaction;
    }

    @Override
    public Integer getOrderedId() {
        return getTransaction().getId();
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public TethysDate getDate() {
        return getTransaction().getDate();
    }

    /**
     * Obtain the comments.
     * @return the comments
     */
    public String getComments() {
        return getTransaction().getComments();
    }

    /**
     * Obtain the tax credit.
     * @return the tax credit
     */
    public TethysMoney getTaxCredit() {
        return getTransaction().getTaxCredit();
    }

    /**
     * Obtain the qualifying years.
     * @return the years
     */
    public Integer getYears() {
        return getTransaction().getYears();
    }

    @Override
    public int compareTo(final ChargeableEvent pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the underlying transactions */
        return getTransaction().compareTo(pThat.getTransaction());
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
        return MetisDifference.isEqual(getTransaction(), myThat.getTransaction());
    }

    @Override
    public int hashCode() {
        return getTransaction().hashCode();
    }

    /**
     * Apply taxation of total slice to the individual events. This tax is first split
     * proportionally among the slices and then multiplied by the years of each individual event
     * @param pTax the calculated taxation for the slice
     * @param pTotal the slice total of the event list
     */
    protected void applyTax(final TethysMoney pTax,
                            final TethysMoney pTotal) {
        /* Calculate the portion of tax that applies to this slice */
        TethysMoney myPortion = pTax.valueAtWeight(getSlice(), pTotal);

        /* Multiply by the number of years */
        theTaxation = new TethysMoney(myPortion);
        theTaxation.multiply(getYears());
    }

    /**
     * List of ChargeableEvents.
     */
    public static class ChargeableEventList
            extends MetisOrderedIdList<Integer, ChargeableEvent>
            implements MetisDataContents {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.CHARGE_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Constructor.
         */
        protected ChargeableEventList() {
            super(ChargeableEvent.class);
        }

        /**
         * Constructor.
         * @param pSource the source list.
         * @param pRange the range of events to copy
         */
        protected ChargeableEventList(final ChargeableEventList pSource,
                                      final TethysDateRange pRange) {
            /* Call super class */
            super(ChargeableEvent.class);

            /* Loop through the source */
            Iterator<ChargeableEvent> myIterator = pSource.iterator();
            while (myIterator.hasNext()) {
                ChargeableEvent myEvent = myIterator.next();

                /* Check the range */
                int iDiff = pRange.compareTo(myEvent.getDate());
                if (iDiff > 0) {
                    continue;
                } else if (iDiff < 0) {
                    break;
                }

                /* Add to the list */
                append(myEvent);
            }
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(getDataFields().getName());
            myBuilder.append("(");
            myBuilder.append(size());
            myBuilder.append(")");
            return myBuilder.toString();
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return MetisFieldValue.UNKNOWN;
        }

        /**
         * Add Chargeable Transaction to List.
         * @param pTrans the base transaction
         * @param pGains the gains
         */
        public void addTransaction(final Transaction pTrans,
                                   final TethysMoney pGains) {
            /* Create the chargeable event */
            ChargeableEvent myEvent = new ChargeableEvent(pTrans, pGains);

            /* Add it to the list */
            append(myEvent);
        }

        /**
         * Get the SliceTotal of the chargeable event list. Each slice is the Value of the event
         * divided by the number of years that the charge is to be sliced over
         * @return the slice total of the chargeable event list
         */
        public TethysMoney getSliceTotal() {
            /* Initialise the total */
            TethysMoney myTotal = new TethysMoney();

            /* Loop through the list */
            Iterator<ChargeableEvent> myIterator = iterator();
            while (myIterator.hasNext()) {
                ChargeableEvent myEvent = myIterator.next();

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
        public TethysMoney getTaxTotal() {
            /* Initialise the total */
            TethysMoney myTotal = new TethysMoney();

            /* Loop through the list */
            Iterator<ChargeableEvent> myIterator = iterator();
            while (myIterator.hasNext()) {
                ChargeableEvent myEvent = myIterator.next();

                /* Add in this slice */
                myTotal.addAmount(myEvent.getTaxation());
            }

            /* Return the total */
            return myTotal;
        }

        /**
         * Get the GainsTotal of the chargeable event list. Each slice is the Value of the event
         * divided by the number of years that the charge is to be sliced over
         * @return the slice total of the chargeable event list
         */
        public TethysMoney getGainsTotal() {
            /* Initialise the total */
            TethysMoney myTotal = new TethysMoney();

            /* Loop through the list */
            Iterator<ChargeableEvent> myIterator = iterator();
            while (myIterator.hasNext()) {
                ChargeableEvent myEvent = myIterator.next();

                /* Add in this slice */
                myTotal.addAmount(myEvent.getAmount());
            }

            /* Return the total */
            return myTotal;
        }

        /**
         * Apply taxation of total slice to the individual events. This tax is first split
         * proportionally among the slices and then multiplied by the years of each individual event
         * @param pTax the calculated taxation for the slice
         * @param pTotal the slice total of the event list
         */
        public void applyTax(final TethysMoney pTax,
                             final TethysMoney pTotal) {
            /* Loop through the list */
            Iterator<ChargeableEvent> myIterator = iterator();
            while (myIterator.hasNext()) {
                ChargeableEvent myEvent = myIterator.next();

                /* Apply tax to this slice */
                myEvent.applyTax(pTax, pTotal);
            }
        }
    }
}
