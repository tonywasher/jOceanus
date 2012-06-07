/*******************************************************************************
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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.DilutedPrice;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.AccountPrice;
import uk.co.tolcroft.finance.views.DilutionEvent.DilutionEventList;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;

public class ViewPrice extends AccountPrice {
    /**
     * Object name
     */
    public static String objName = ViewPrice.class.getSimpleName();

    /**
     * List name
     */
    public static String listName = objName + "s";

    /**
     * Report fields
     */
    protected static final JDataFields theLocalFields = new JDataFields(objName, AccountPrice.FIELD_DEFS);

    /* Called from constructor */
    @Override
    public JDataFields declareFields() {
        return theLocalFields;
    }

    /* Field IDs */
    public static final JDataField FIELD_DILUTION = theLocalFields.declareEqualityValueField("Dilution");
    public static final JDataField FIELD_DILUTEDPRICE = theLocalFields
            .declareEqualityValueField("DilutedPrice");

    /**
     * Is the account subject to dilutions
     */
    private final boolean hasDilutions;

    /**
     * The active set of values
     */
    private EncryptedValueSet theValueSet;

    @Override
    public void declareValues(EncryptedValueSet pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /* Access methods */
    public Dilution getDilution() {
        return getDilution(theValueSet);
    }

    public DilutedPrice getDilutedPrice() {
        return getDilutedPrice(theValueSet);
    }

    public static Dilution getDilution(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DILUTION, Dilution.class);
    }

    public static DilutedPrice getDilutedPrice(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DILUTEDPRICE, DilutedPrice.class);
    }

    private void setValueDilution(Dilution pDilution) {
        theValueSet.setValue(FIELD_DILUTION, pDilution);
    }

    private void setValueDilutedPrice(DilutedPrice pPrice) {
        theValueSet.setValue(FIELD_DILUTEDPRICE, pPrice);
    }

    /* Linking methods */
    @Override
    public AccountPrice getBase() {
        return (AccountPrice) super.getBase();
    }

    /**
     * Calculate Diluted values
     */
    protected void calculateDiluted() {
        /* Access the list for the item */
        ViewPriceList myList = (ViewPriceList) getList();

        /* Set null default dilution */
        setValueDilution(null);
        setValueDilutedPrice(null);

        /* Access Price and date */
        DateDay myDate = getDate();
        Price myPrice = getPrice();
        Account myAccount = getAccount();

        /* If we have can look at dilutions */
        if ((hasDilutions) && (myDate != null) && (myPrice != null)) {
            /* Determine the dilution factor for the date */
            Dilution myDilution = myList.getDilutions().getDilutionFactor(myAccount, myDate);

            /* If we have a dilution factor */
            if (myDilution != null) {
                /* Store dilution details */
                setValueDilution(myDilution);
                setValueDilutedPrice(myPrice.getDilutedPrice(myDilution));
            }
        }
    }

    /**
     * Construct a copy of a Price
     * @param pList the list
     * @param pPrice The Price
     */
    protected ViewPrice(ViewPriceList pList,
                        AccountPrice pPrice) {
        /* Set standard values */
        super(pList, pPrice);

        /* Determine whether the account has dilutions */
        hasDilutions = ((ViewPriceList) getList()).hasDilutions;

        /* Calculate diluted values */
        calculateDiluted();
    }

    /* Standard constructor for a newly inserted price */
    private ViewPrice(ViewPriceList pList) {
        super(pList);

        /* Determine whether the account has dilutions */
        hasDilutions = ((ViewPriceList) getList()).hasDilutions;
    }

    /**
     * Set a new price
     * @param pPrice the price
     */
    @Override
    public void setPrice(Price pPrice) throws JDataException {
        super.setPrice(pPrice);
        calculateDiluted();
    }

    /**
     * Set a new date
     * @param pDate the new date
     */
    @Override
    public void setDate(DateDay pDate) {
        /* Store date */
        super.setDate(pDate);
        calculateDiluted();
    }

    /**
     * Price List
     */
    public static class ViewPriceList extends AccountPriceList {
        /* Members */
        private Account theAccount = null;
        private DilutionEventList theDilutions = null;
        private boolean hasDilutions = false;

        /* Access methods */
        private DilutionEventList getDilutions() {
            return theDilutions;
        }

        public boolean hasDilutions() {
            return hasDilutions;
        }

        /**
         * Construct an edit extract of a Price list
         * 
         * @param pView The master view
         * @param pAccount The account to extract rates for
         */
        public ViewPriceList(View pView,
                             Account pAccount) {
            /* Declare the data and set the style */
            super(pView.getData());
            setStyle(ListStyle.EDIT);

            /* Local variables */
            AccountPrice.AccountPriceList myPrices;
            AccountPrice myCurr;
            ViewPrice myItem;
            DataListIterator<AccountPrice> myIterator;

            /* Store the account */
            theAccount = pAccount;

            /* Skip to alias if required */
            if ((theAccount != null) && (theAccount.getAlias() != null))
                theAccount = theAccount.getAlias();

            /* Access the base prices */
            myPrices = getData().getPrices();
            setBase(myPrices);

            /* Store dilution list and record whether we have dilutions */
            theDilutions = pView.getDilutions();
            hasDilutions = theDilutions.hasDilution(theAccount);

            /* Access the list iterator */
            myIterator = myPrices.listIterator(true);

            /* Loop through the list */
            while ((myCurr = myIterator.next()) != null) {
                /* Check the account */
                int myResult = theAccount.compareTo(myCurr.getAccount());

                /* Skip different accounts */
                if (myResult != 0)
                    continue;

                /* Copy the item */
                myItem = new ViewPrice(this, myCurr);
                add(myItem);
            }
        }

        /* Disable extract lists. */
        @Override
        public ViewPriceList getUpdateList() {
            return null;
        }

        @Override
        public ViewPriceList getEditList() {
            return null;
        }

        @Override
        public ViewPriceList getShallowCopy() {
            return null;
        }

        @Override
        public ViewPriceList getDeepCopy(DataSet<?> pData) {
            return null;
        }

        public ViewPriceList getDifferences(ViewPriceList pOld) {
            return null;
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return ((theAccount != null) && (theAccount.isLocked()));
        }

        /**
         * Disable Add a new item
         * @return the new item
         */
        @Override
        public ViewPrice addNewItem(DataItem<?> pElement) {
            return null;
        }

        /**
         * Add a new item to the edit list
         * @return the newly added item
         */
        @Override
        public ViewPrice addNewItem() {
            ViewPrice myPrice = new ViewPrice(this);
            add(myPrice);
            return myPrice;
        }
    }
}
