/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseStaticResource;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Dilution Events relating to stock dilution.
 * @author Tony Washer
 */
public final class MoneyWiseAnalysisDilutionEvent
        implements MetisFieldTableItem, Comparable<MoneyWiseAnalysisDilutionEvent> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisDilutionEvent> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisDilutionEvent.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_ID, MoneyWiseAnalysisDilutionEvent::getIndexedId);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.SECURITY, MoneyWiseAnalysisDilutionEvent::getSecurity);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, MoneyWiseAnalysisDilutionEvent::getDate);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticResource.TRANSINFO_DILUTION, MoneyWiseAnalysisDilutionEvent::getDilution);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseAnalysisDilutionEvent::getTransaction);
    }

    /**
     * The Id.
     */
    private final int theId;

    /**
     * The Security.
     */
    private final MoneyWiseSecurity theSecurity;

    /**
     * The Date.
     */
    private final TethysDate theDate;

    /**
     * The Dilution.
     */
    private final TethysRatio theDilution;

    /**
     * The Transaction.
     */
    private MoneyWiseTransaction theTransaction;

    /**
     * Create a dilution event from a transaction.
     * @param pId the id for the dilution
     * @param pTrans the underlying transaction
     */
    protected MoneyWiseAnalysisDilutionEvent(final int pId,
                                             final MoneyWiseTransaction pTrans) {
        /* Access the account TODO */
        MoneyWiseTransAsset myAsset = pTrans.getAccount();
        if (!(myAsset instanceof MoneyWiseSecurityHolding)) {
            myAsset = pTrans.getPartner();
        }

        /* Store the values */
        theId = pId;
        theSecurity = ((MoneyWiseSecurityHolding) myAsset).getSecurity();
        theDate = pTrans.getDate();
        theDilution = pTrans.getDilution();
        theTransaction = pTrans;
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisDilutionEvent> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return getDataFieldSet().getName();
    }

    /**
     * Obtain the Security.
     * @return the security
     */
    public MoneyWiseSecurity getSecurity() {
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
    public TethysRatio getDilution() {
        return theDilution;
    }

    /**
     * Obtain the Transaction.
     * @return the transaction
     */
    public MoneyWiseTransaction getTransaction() {
        return theTransaction;
    }

    @Override
    public Integer getIndexedId() {
        return theId;
    }

    @Override
    public int compareTo(final MoneyWiseAnalysisDilutionEvent pThat) {
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
        final MoneyWiseAnalysisDilutionEvent myThat = (MoneyWiseAnalysisDilutionEvent) pThat;

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
    public static final class MoneyWiseAnalysisDilutionEventList
            implements MetisFieldItem, MetisDataList<MoneyWiseAnalysisDilutionEvent> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisDilutionEventList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisDilutionEventList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.SECURITY, MoneyWiseAnalysisDilutionEventList::getSecurity);
        }

        /**
         * The list.
         */
        private final List<MoneyWiseAnalysisDilutionEvent> theList;

        /**
         * Security.
         */
        private final MoneyWiseSecurity theSecurity;

        /**
         * Constructor.
         * @param pSecurity the security
         */
        private MoneyWiseAnalysisDilutionEventList(final MoneyWiseSecurity pSecurity) {
            theSecurity = pSecurity;
            theList = new ArrayList<>();
        }

        @Override
        public List<MoneyWiseAnalysisDilutionEvent> getUnderlyingList() {
            return theList;
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisDilutionEventList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return theSecurity.formatObject(pFormatter);
        }

        /**
         * Obtain security.
         * @return the security
         */
        public MoneyWiseSecurity getSecurity() {
            return theSecurity;
        }
    }

    /**
     * Map of DilutionLists indexed by Security Id.
     */
    public static class MoneyWiseAnalysisDilutionEventMap
            implements MetisDataObjectFormat, MetisDataMap<Integer, MoneyWiseAnalysisDilutionEventList> {
        /**
         * The next Id.
         */
        private int theNextId = 1;

        /**
         * The Map.
         */
        private final Map<Integer, MoneyWiseAnalysisDilutionEventList> theMap;

        /**
         * Constructor.
         */
        protected MoneyWiseAnalysisDilutionEventMap() {
            theMap = new HashMap<>();
        }

        /**
         * Constructor.
         * @param pSource the source map
         * @param pDate the last date
         */
        protected MoneyWiseAnalysisDilutionEventMap(final MoneyWiseAnalysisDilutionEventMap pSource,
                                                    final TethysDate pDate) {
            /* Initialise */
            this();

            /* Iterate through the source map */
            final Iterator<Entry<Integer, MoneyWiseAnalysisDilutionEventList>> myIterator = pSource.getUnderlyingMap().entrySet().iterator();
            while (myIterator.hasNext()) {
                final Entry<Integer, MoneyWiseAnalysisDilutionEventList> myEntry = myIterator.next();

                /* Access the id and list iterator */
                final Integer myId = myEntry.getKey();
                final MoneyWiseAnalysisDilutionEventList mySource = myEntry.getValue();
                final Iterator<MoneyWiseAnalysisDilutionEvent> myEventIterator = mySource.iterator();
                MoneyWiseAnalysisDilutionEventList myList = null;

                /* Loop through the entries */
                while (myEventIterator.hasNext()) {
                    final MoneyWiseAnalysisDilutionEvent myEvent = myEventIterator.next();

                    /* Check that we are wanting this event */
                    if (pDate.compareTo(myEvent.getDate()) > 0) {
                        break;
                    }

                    /* If this is the first entry */
                    if (myList == null) {
                        /* Create new entry */
                        myList = new MoneyWiseAnalysisDilutionEventList(mySource.getSecurity());
                        put(myId, myList);
                    }

                    /* Add the event */
                    myList.add(myEvent);
                }
            }
        }

        @Override
        public Map<Integer, MoneyWiseAnalysisDilutionEventList> getUnderlyingMap() {
            return theMap;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return getClass().getSimpleName();
        }

        /**
         * Add Dilution Event to Map.
         * @param pTrans the base transaction
         */
        public void addDilution(final MoneyWiseTransaction pTrans) {
            /* Create the dilution event */
            final MoneyWiseAnalysisDilutionEvent myDilution = new MoneyWiseAnalysisDilutionEvent(theNextId++, pTrans);

            /* Look for the list associated with the security */
            final MoneyWiseSecurity mySecurity = myDilution.getSecurity();
            MoneyWiseAnalysisDilutionEventList myList = get(mySecurity.getIndexedId());
            if (myList == null) {
                /* allocate new list if necessary */
                myList = new MoneyWiseAnalysisDilutionEventList(mySecurity);
                put(mySecurity.getIndexedId(), myList);
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
        public boolean hasDilution(final MoneyWiseSecurity pSecurity) {
            /* Check for dilutions for this security */
            return pSecurity != null && get(pSecurity.getIndexedId()) != null;
        }

        /**
         * Obtain the dilution factor for the security and date.
         * @param pSecurity the security to dilute
         * @param pDate the date of the price
         * @return the dilution factor
         */
        public TethysRatio getDilutionFactor(final MoneyWiseSecurity pSecurity,
                                             final TethysDate pDate) {
            /* Access the dilutions for this security */
            final MoneyWiseAnalysisDilutionEventList myDilutionList = get(pSecurity.getIndexedId());
            if (myDilutionList == null) {
                return null;
            }
            final List<MoneyWiseAnalysisDilutionEvent> myList = myDilutionList.getUnderlyingList();

            /* Loop through the items */
            final ListIterator<MoneyWiseAnalysisDilutionEvent> myIterator = myList.listIterator(myList.size());
            TethysRatio myDilution = new TethysRatio(TethysRatio.ONE);
            while (myIterator.hasPrevious()) {
                final MoneyWiseAnalysisDilutionEvent myEvent = myIterator.previous();

                /* If the event is earlier than we are interested in */
                if (pDate.compareTo(myEvent.getDate()) > 0) {
                    break;
                }

                /* add in the dilution factor */
                myDilution = myDilution.multiplyBy(myEvent.getDilution());
            }

            /* If there is no dilution at all */
            if (myDilution.compareTo(TethysRatio.ONE) == 0) {
                myDilution = null;
            }

            /* Return to caller */
            return myDilution;
        }
    }
}
