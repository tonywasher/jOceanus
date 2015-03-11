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

import net.sourceforge.joceanus.jmetis.list.NestedHashMap;
import net.sourceforge.joceanus.jmetis.list.OrderedIdItem;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;

/**
 * Dilution Events relating to stock dilution.
 * @author Tony Washer
 */
public final class DilutionEvent
        implements OrderedIdItem<Integer>, JDataContents, Comparable<DilutionEvent> {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.DILUTION_NAME.getValue());

    /**
     * Id Field Id.
     */
    private static final JDataField FIELD_ID = FIELD_DEFS.declareEqualityField(DataItem.FIELD_ID.getName());

    /**
     * Security Field Id.
     */
    private static final JDataField FIELD_SECURITY = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.SECURITY.getItemName());

    /**
     * Date Field Id.
     */
    private static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

    /**
     * Dilution Field Id.
     */
    private static final JDataField FIELD_DILUTION = FIELD_DEFS.declareEqualityField(StaticDataResource.TRANSINFO_DILUTION.getValue());

    /**
     * Transaction Field Id.
     */
    private static final JDataField FIELD_TRANS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TRANSACTION.getItemName());

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
    private final JDateDay theDate;

    /**
     * The Dilution.
     */
    private final JDilution theDilution;

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
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
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
        return JDataFieldValue.UNKNOWN;
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
    public JDateDay getDate() {
        return theDate;
    }

    /**
     * Obtain the Dilution.
     * @return the dilution
     */
    public JDilution getDilution() {
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
        int iDiff = Difference.compareObject(getDate(), pThat.getDate());
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
        return Difference.isEqual(getDate(), myThat.getDate())
               && Difference.isEqual(getSecurity(), myThat.getSecurity())
               && Difference.isEqual(getTransaction(), myThat.getTransaction());
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
     * Map of DilutionLists indexed by Security Id.
     */
    public static class DilutionEventMap
            extends NestedHashMap<Integer, List<DilutionEvent>>
            implements JDataFormat {
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
                                   final JDateDay pDate) {
            /* Iterate through the source map */
            Iterator<Entry<Integer, List<DilutionEvent>>> myIterator = pSource.entrySet().iterator();
            while (myIterator.hasNext()) {
                Entry<Integer, List<DilutionEvent>> myEntry = myIterator.next();

                /* Access the id and list iterator */
                Integer myId = myEntry.getKey();
                Iterator<DilutionEvent> myEventIterator = myEntry.getValue().iterator();
                List<DilutionEvent> myList = null;

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
                        myList = new ArrayList<DilutionEvent>();
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
            List<DilutionEvent> myList = get(mySecurity.getId());
            if (myList == null) {
                /* allocate new list if necessary */
                myList = new ArrayList<DilutionEvent>();
                put(mySecurity.getId(), myList);
            }

            /* Add it to the list */
            myList.add(myDilution);
        }

        /**
         * Does this security have diluted prices?
         * @param pSecurity the security to test
         * @return <code>true</code> if the security has diluted prices, <code>false</code> otherwise
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
        public JDilution getDilutionFactor(final Security pSecurity,
                                           final JDateDay pDate) {
            /* Access the dilutions for this security */
            List<DilutionEvent> myList = get(pSecurity.getId());
            if (myList == null) {
                return null;
            }

            /* Loop through the items */
            Iterator<DilutionEvent> myIterator = myList.iterator();
            JDilution myDilution = new JDilution(JDilution.MAX_DILUTION);
            while (myIterator.hasNext()) {
                DilutionEvent myEvent = myIterator.next();

                /* If the event is earlier than we are interested in */
                if (pDate.compareTo(myEvent.getDate()) > 0) {
                    break;
                }

                /* add in the dilution factor */
                myDilution = myDilution.getFurtherDilution(myEvent.getDilution());
            }

            /* If there is no dilution at all */
            if (myDilution.compareTo(JDilution.MAX_DILUTION) == 0) {
                myDilution = null;
            }

            /* Return to caller */
            return myDilution;
        }
    }
}
