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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataFormat;
import net.sourceforge.joceanus.jmetis.list.MetisNestedHashMap;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdItem;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;

/**
 * Dilution Events relating to stock dilution.
 * @author Tony Washer
 */
public final class DilutionEvent
        implements MetisOrderedIdItem<Integer>, MetisDataContents, Comparable<DilutionEvent> {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.DILUTION_NAME.getValue());

    /**
     * Id Field Id.
     */
    private static final MetisField FIELD_ID = FIELD_DEFS.declareEqualityField(DataItem.FIELD_ID.getName());

    /**
     * Security Field Id.
     */
    private static final MetisField FIELD_SECURITY = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.SECURITY.getItemName());

    /**
     * Date Field Id.
     */
    private static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

    /**
     * Dilution Field Id.
     */
    private static final MetisField FIELD_DILUTION = FIELD_DEFS.declareEqualityField(StaticDataResource.TRANSINFO_DILUTION.getValue());

    /**
     * Transaction Field Id.
     */
    private static final MetisField FIELD_TRANS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TRANSACTION.getItemName());

    /**
     * The Id.
     */
    private final int theId;

    /**
     * The Security.
     */
    private final Security theSecurity;

    /**
     * The Date.
     */
    private final TethysDate theDate;

    /**
     * The Dilution.
     */
    private final TethysDilution theDilution;

    /**
     * The Transaction.
     */
    private Transaction theTransaction = null;

    /**
     * Create a dilution event from a transaction.
     * @param pId the id for the dilution
     * @param pTrans the underlying transaction
     */
    protected DilutionEvent(final int pId,
                            final Transaction pTrans) {
        /* Access the account TODO */
        TransactionAsset myAsset = pTrans.getAccount();
        if (!(myAsset instanceof SecurityHolding)) {
            myAsset = pTrans.getPartner();
        }

        /* Store the values */
        theId = pId;
        theSecurity = SecurityHolding.class.cast(myAsset).getSecurity();
        theDate = pTrans.getDate();
        theDilution = pTrans.getDilution();
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
        if (FIELD_ID.equals(pField)) {
            return theId;
        }
        if (FIELD_SECURITY.equals(pField)) {
            return theSecurity;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_DILUTION.equals(pField)) {
            return theDilution;
        }
        if (FIELD_TRANS.equals(pField)) {
            return theTransaction;
        }
        return MetisFieldValue.UNKNOWN;
    }

    /**
     * Obtain the Security.
     * @return the security
     */
    public Security getSecurity() {
        return theSecurity;
    }

    /**
     * Obtain the Date.
     * @return the date
     */
    public TethysDate getDate() {
        return theDate;
    }

    /**
     * Obtain the Dilution.
     * @return the dilution
     */
    public TethysDilution getDilution() {
        return theDilution;
    }

    /**
     * Obtain the Transaction.
     * @return the transaction
     */
    public Transaction getTransaction() {
        return theTransaction;
    }

    @Override
    public Integer getOrderedId() {
        return theId;
    }

    @Override
    public int compareTo(final DilutionEvent pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If the dates differ */
        int iDiff = MetisDifference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the security */
        return getSecurity().compareTo(pThat.getSecurity());
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

        /* Access as Dilution Event */
        DilutionEvent myThat = (DilutionEvent) pThat;

        /* Check equality */
        return MetisDifference.isEqual(getDate(), myThat.getDate())
               && MetisDifference.isEqual(getSecurity(), myThat.getSecurity())
               && MetisDifference.isEqual(getTransaction(), myThat.getTransaction());
    }

    @Override
    public int hashCode() {
        int hash = getDate().hashCode();
        hash ^= getSecurity().hashCode();
        if (getTransaction() != null) {
            hash ^= getTransaction().hashCode();
        }
        return hash;
    }

    /**
     * List of dilutions for a security.
     */
    public static final class DilutionEventList
            extends ArrayList<DilutionEvent>
            implements MetisDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6952350898773468201L;

        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.DILUTION_LIST.getValue());

        /**
         * Security Field Id.
         */
        private static final MetisField FIELD_SECURITY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.SECURITY.getFieldName());

        /**
         * Size Field Id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Security.
         */
        private final transient Security theSecurity;

        /**
         * Constructor.
         * @param pSecurity the security
         */
        private DilutionEventList(final Security pSecurity) {
            theSecurity = pSecurity;
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theSecurity.formatObject());
            myBuilder.append("(");
            myBuilder.append(size());
            myBuilder.append(")");
            return myBuilder.toString();
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_SECURITY.equals(pField)) {
                return theSecurity;
            }
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return MetisFieldValue.UNKNOWN;
        }

        /**
         * Obtain security.
         * @return the security
         */
        public Security getSecurity() {
            return theSecurity;
        }
    }

    /**
     * Map of DilutionLists indexed by Security Id.
     */
    public static class DilutionEventMap
            extends MetisNestedHashMap<Integer, DilutionEventList>
            implements MetisDataFormat {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2572420680159829956L;

        /**
         * The next Id.
         */
        private int theNextId = 1;

        /**
         * Constructor.
         */
        protected DilutionEventMap() {
        }

        /**
         * Constructor.
         * @param pSource the source map
         * @param pDate the last date
         */
        protected DilutionEventMap(final DilutionEventMap pSource,
                                   final TethysDate pDate) {
            /* Iterate through the source map */
            Iterator<Entry<Integer, DilutionEventList>> myIterator = pSource.entrySet().iterator();
            while (myIterator.hasNext()) {
                Entry<Integer, DilutionEventList> myEntry = myIterator.next();

                /* Access the id and list iterator */
                Integer myId = myEntry.getKey();
                DilutionEventList mySource = myEntry.getValue();
                Iterator<DilutionEvent> myEventIterator = mySource.iterator();
                DilutionEventList myList = null;

                /* Loop through the entries */
                while (myEventIterator.hasNext()) {
                    DilutionEvent myEvent = myEventIterator.next();

                    /* Check that we are wanting this event */
                    if (pDate.compareTo(myEvent.getDate()) > 0) {
                        break;
                    }

                    /* If this is the first entry */
                    if (myList == null) {
                        /* Create new entry */
                        myList = new DilutionEventList(mySource.getSecurity());
                        put(myId, myList);
                    }

                    /* Add the event */
                    myList.add(myEvent);
                }
            }
        }

        @Override
        public String formatObject() {
            return getClass().getSimpleName();
        }

        /**
         * Add Dilution Event to Map.
         * @param pTrans the base transaction
         */
        protected void addDilution(final Transaction pTrans) {
            /* Create the dilution event */
            DilutionEvent myDilution = new DilutionEvent(theNextId++, pTrans);

            /* Look for the list associated with the security */
            Security mySecurity = myDilution.getSecurity();
            DilutionEventList myList = get(mySecurity.getId());
            if (myList == null) {
                /* allocate new list if necessary */
                myList = new DilutionEventList(mySecurity);
                put(mySecurity.getId(), myList);
            }

            /* Add it to the list */
            myList.add(myDilution);
        }

        /**
         * Does this security have diluted prices?
         * @param pSecurity the security to test
         * @return <code>true</code> if the security has diluted prices, <code>false</code>
         * otherwise
         */
        public boolean hasDilution(final Security pSecurity) {
            /* Check for dilutions for this security */
            return pSecurity != null && get(pSecurity.getId()) != null;
        }

        /**
         * Obtain the dilution factor for the security and date.
         * @param pSecurity the security to dilute
         * @param pDate the date of the price
         * @return the dilution factor
         */
        public TethysDilution getDilutionFactor(final Security pSecurity,
                                           final TethysDate pDate) {
            /* Access the dilutions for this security */
            List<DilutionEvent> myList = get(pSecurity.getId());
            if (myList == null) {
                return null;
            }

            /* Loop through the items */
            ListIterator<DilutionEvent> myIterator = myList.listIterator(myList.size());
            TethysDilution myDilution = new TethysDilution(TethysDilution.MAX_DILUTION);
            while (myIterator.hasPrevious()) {
                DilutionEvent myEvent = myIterator.previous();

                /* If the event is earlier than we are interested in */
                if (pDate.compareTo(myEvent.getDate()) > 0) {
                    break;
                }

                /* add in the dilution factor */
                myDilution = myDilution.getFurtherDilution(myEvent.getDilution());
            }

            /* If there is no dilution at all */
            if (myDilution.compareTo(TethysDilution.MAX_DILUTION) == 0) {
                myDilution = null;
            }

            /* Return to caller */
            return myDilution;
        }
    }
}
