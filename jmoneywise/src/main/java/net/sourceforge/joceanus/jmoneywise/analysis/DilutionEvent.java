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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.list.OrderedIdItem;
import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;

/**
 * Dilution Events relating to stock dilution.
 * @author Tony Washer
 */
public final class DilutionEvent
        implements OrderedIdItem<Integer>, JDataContents, Comparable<DilutionEvent> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DilutionEvent.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName();
    }

    /**
     * Id Field Id.
     */
    private static final JDataField FIELD_ID = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataId"));

    /**
     * Security Field Id.
     */
    private static final JDataField FIELD_SECURITY = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataSecurity"));

    /**
     * Date Field Id.
     */
    private static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataDate"));

    /**
     * Dilution Field Id.
     */
    private static final JDataField FIELD_DILUTION = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataDilution"));

    /**
     * Transaction Field Id.
     */
    private static final JDataField FIELD_TRANS = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataTrans"));

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
        if (this == pThat) {
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
     * Create a dilution event from a transaction.
     * @param pId the id for the dilution
     * @param pTrans the underlying transaction
     */
    protected DilutionEvent(final int pId,
                            final Transaction pTrans) {
        /* Local variables */
        AssetBase<?> myAsset;

        /* Access the category */
        EventCategory myCategory = pTrans.getCategory();

        /* Switch on the category type */
        switch (myCategory.getCategoryTypeClass()) {
            case STOCKRIGHTSTAKEN:
                myAsset = pTrans.getCredit();
                break;
            case STOCKSPLIT:
            case STOCKRIGHTSWAIVED:
            case STOCKDEMERGER:
            default:
                myAsset = pTrans.getDebit();
                break;
        }

        /* Store the values */
        theId = pId;
        theSecurity = Security.class.cast(myAsset);
        theDate = pTrans.getDate();
        theDilution = pTrans.getDilution();
        theTransaction = pTrans;
    }

    /**
     * List of DilutionEvents.
     */
    public static class DilutionEventList
            extends OrderedIdList<Integer, DilutionEvent>
            implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"));

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
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataSize"));

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * The next Id.
         */
        private int theNextId = 1;

        /**
         * Constructor.
         */
        public DilutionEventList() {
            super(DilutionEvent.class);
        }

        /**
         * Add Dilution Event to List.
         * @param pTrans the base transaction
         */
        public void addDilution(final Transaction pTrans) {
            /* Create the dilution event */
            DilutionEvent myDilution = new DilutionEvent(theNextId++, pTrans);

            /* Add it to the list */
            add(myDilution);
        }

        /**
         * Does this security have diluted prices?
         * @param pSecurity the security to test
         * @return <code>true</code> if the security has diluted prices, <code>false</code> otherwise
         */
        public boolean hasDilution(final Security pSecurity) {
            /* Loop through the items */
            Iterator<DilutionEvent> myIterator = listIterator();
            while (myIterator.hasNext()) {
                DilutionEvent myEvent = myIterator.next();

                /* If the event is for this security */
                if (!Difference.isEqual(pSecurity, myEvent.getSecurity())) {
                    /* Set result and break loop */
                    return true;
                }
            }

            /* Return no dilution */
            return false;
        }

        /**
         * Obtain the dilution factor for the security and date.
         * @param pSecurity the security to dilute
         * @param pDate the date of the price
         * @return the dilution factor
         */
        public JDilution getDilutionFactor(final Security pSecurity,
                                           final JDateDay pDate) {
            /* No factor if the security has no dilutions */
            if (!hasDilution(pSecurity)) {
                return null;
            }

            /* Loop through the items */
            Iterator<DilutionEvent> myIterator = listIterator();
            JDilution myDilution = new JDilution(JDilution.MAX_DILUTION);
            while (myIterator.hasNext()) {
                DilutionEvent myEvent = myIterator.next();
                /* If the event is for this security, and if the dilution date is later */
                if ((Difference.isEqual(pSecurity, myEvent.getSecurity()))
                    && (pDate.compareTo(myEvent.getDate()) < 0)) {
                    /* add in the dilution factor */
                    myDilution = myDilution.getFurtherDilution(myEvent.getDilution());
                }
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
