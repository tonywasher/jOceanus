/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;

/**
 * Dilution Events relating to stock dilution.
 * @author Tony Washer
 */
public final class DilutionEvent
        implements MetisFieldTableItem, Comparable<DilutionEvent> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<DilutionEvent> FIELD_DEFS = MetisFieldSet.newFieldSet(DilutionEvent.class);

    /**
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_ID, DilutionEvent::getIndexedId);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.SECURITY, DilutionEvent::getSecurity);
        FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE, DilutionEvent::getDate);
        FIELD_DEFS.declareLocalField(StaticDataResource.TRANSINFO_DILUTION, DilutionEvent::getDilution);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSACTION, DilutionEvent::getTransaction);
    }

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
    private Transaction theTransaction;

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
    public MetisFieldSet<DilutionEvent> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return getDataFieldSet().getName();
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
    public Integer getIndexedId() {
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
        final int iDiff = MetisDataDifference.compareObject(getDate(), pThat.getDate());
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
        final DilutionEvent myThat = (DilutionEvent) pThat;

        /* Check equality */
        return MetisDataDifference.isEqual(getDate(), myThat.getDate())
               && MetisDataDifference.isEqual(getSecurity(), myThat.getSecurity())
               && MetisDataDifference.isEqual(getTransaction(), myThat.getTransaction());
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
            implements MetisFieldItem, MetisDataList<DilutionEvent> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<DilutionEventList> FIELD_DEFS = MetisFieldSet.newFieldSet(DilutionEventList.class);

        /**
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseDataType.SECURITY, DilutionEventList::getSecurity);
        }

        /**
         * The list.
         */
        private final ArrayList<DilutionEvent> theList;

        /**
         * Security.
         */
        private final Security theSecurity;

        /**
         * Constructor.
         * @param pSecurity the security
         */
        private DilutionEventList(final Security pSecurity) {
            theSecurity = pSecurity;
            theList = new ArrayList<>();
        }

        @Override
        public List<DilutionEvent> getUnderlyingList() {
            return theList;
        }

        @Override
        public MetisFieldSet<DilutionEventList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return theSecurity.formatObject(pFormatter);
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
            implements MetisDataObjectFormat, MetisDataMap<Integer, DilutionEventList> {
        /**
         * The next Id.
         */
        private int theNextId = 1;

        /**
         * The Map.
         */
        private final Map<Integer, DilutionEventList> theMap;

        /**
         * Constructor.
         */
        protected DilutionEventMap() {
            theMap = new HashMap<>();
        }

        /**
         * Constructor.
         * @param pSource the source map
         * @param pDate the last date
         */
        protected DilutionEventMap(final DilutionEventMap pSource,
                                   final TethysDate pDate) {
            /* Initialise */
            this();

            /* Iterate through the source map */
            final Iterator<Map.Entry<Integer, DilutionEventList>> myIterator = pSource.getUnderlyingMap().entrySet().iterator();
            while (myIterator.hasNext()) {
                final Map.Entry<Integer, DilutionEventList> myEntry = myIterator.next();

                /* Access the id and list iterator */
                final Integer myId = myEntry.getKey();
                final DilutionEventList mySource = myEntry.getValue();
                final Iterator<DilutionEvent> myEventIterator = mySource.iterator();
                DilutionEventList myList = null;

                /* Loop through the entries */
                while (myEventIterator.hasNext()) {
                    final DilutionEvent myEvent = myEventIterator.next();

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
        public Map<Integer, DilutionEventList> getUnderlyingMap() {
            return theMap;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return getClass().getSimpleName();
        }

        /**
         * Add Dilution Event to Map.
         * @param pTrans the base transaction
         */
        protected void addDilution(final Transaction pTrans) {
            /* Create the dilution event */
            final DilutionEvent myDilution = new DilutionEvent(theNextId++, pTrans);

            /* Look for the list associated with the security */
            final Security mySecurity = myDilution.getSecurity();
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
            final DilutionEventList myDilutionList = get(pSecurity.getId());
            if (myDilutionList == null) {
                return null;
            }
            final List<DilutionEvent> myList = myDilutionList.getUnderlyingList();

            /* Loop through the items */
            final ListIterator<DilutionEvent> myIterator = myList.listIterator(myList.size());
            TethysDilution myDilution = new TethysDilution(TethysDilution.MAX_DILUTION);
            while (myIterator.hasPrevious()) {
                final DilutionEvent myEvent = myIterator.previous();

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
