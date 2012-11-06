/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDilutedPrice;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.views.DilutionEvent.DilutionEventList;

/**
 * Extension of AccountPrice to cater for diluted prices.
 * @author Tony Washer
 */
public class ViewPrice
        extends AccountPrice {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = ViewPrice.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AccountPrice.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Dilution Field Id.
     */
    public static final JDataField FIELD_DILUTION = FIELD_DEFS.declareEqualityValueField("Dilution");

    /**
     * Diluted Price Field Id.
     */
    public static final JDataField FIELD_DILUTEDPRICE = FIELD_DEFS.declareEqualityValueField("DilutedPrice");

    /**
     * Is the account subject to dilutions?
     */
    private final boolean hasDilutions;

    /**
     * Obtain dilution.
     * @return the dilution
     */
    public JDilution getDilution() {
        return getDilution(getValueSet());
    }

    /**
     * Obtain diluted price.
     * @return the diluted price
     */
    public JDilutedPrice getDilutedPrice() {
        return getDilutedPrice(getValueSet());
    }

    /**
     * Obtain dilution.
     * @param pValueSet the valueSet
     * @return the dilution
     */
    public static JDilution getDilution(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DILUTION, JDilution.class);
    }

    /**
     * Obtain diluted price.
     * @param pValueSet the valueSet
     * @return the diluted price
     */
    public static JDilutedPrice getDilutedPrice(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DILUTEDPRICE, JDilutedPrice.class);
    }

    /**
     * Set dilution.
     * @param pValue the dilution
     */
    private void setValueDilution(final JDilution pValue) {
        getValueSet().setValue(FIELD_DILUTION, pValue);
    }

    /**
     * Set diluted price.
     * @param pValue the diluted price
     */
    private void setValueDilutedPrice(final JDilutedPrice pValue) {
        getValueSet().setValue(FIELD_DILUTEDPRICE, pValue);
    }

    @Override
    public AccountPrice getBase() {
        return (AccountPrice) super.getBase();
    }

    /**
     * Calculate Diluted values.
     */
    protected final void calculateDiluted() {
        /* Access the list for the item */
        ViewPriceList myList = (ViewPriceList) getList();

        /* Set null default dilution */
        setValueDilution(null);
        setValueDilutedPrice(null);

        /* Access Price and date */
        JDateDay myDate = getDate();
        JPrice myPrice = getPrice();
        Account myAccount = getAccount();

        /* If we have can look at dilutions */
        if ((hasDilutions)
            && (myDate != null)
            && (myPrice != null)) {
            /* Determine the dilution factor for the date */
            JDilution myDilution = myList.getDilutions().getDilutionFactor(myAccount, myDate);

            /* If we have a dilution factor */
            if (myDilution != null) {
                /* Store dilution details */
                setValueDilution(myDilution);
                setValueDilutedPrice(myPrice.getDilutedPrice(myDilution));
            }
        }
    }

    /**
     * Construct a copy of a Price.
     * @param pList the list
     * @param pPrice The Price
     */
    protected ViewPrice(final ViewPriceList pList,
                        final AccountPrice pPrice) {
        /* Set standard values */
        super(pList, pPrice);

        /* Determine whether the account has dilutions */
        hasDilutions = ((ViewPriceList) getList()).hasDilutions;

        /* Calculate diluted values */
        calculateDiluted();
    }

    /**
     * Standard constructor for a newly inserted price.
     * @param pList the list
     */
    private ViewPrice(final ViewPriceList pList) {
        super(pList, pList.getAccount());

        /* Determine whether the account has dilutions */
        hasDilutions = ((ViewPriceList) getList()).hasDilutions;
    }

    @Override
    public void setPrice(final JPrice pPrice) throws JDataException {
        super.setPrice(pPrice);
        calculateDiluted();
    }

    @Override
    public void setDate(final JDateDay pDate) {
        /* Store date */
        super.setDate(pDate);
        calculateDiluted();
    }

    /**
     * Price List.
     */
    public static class ViewPriceList
            extends EncryptedList<ViewPrice> {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(ViewPriceList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return ViewPriceList.class.getSimpleName();
        }

        @Override
        protected ViewPriceList getEmptyList(final ListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        /**
         * The Account field id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField("Account");

        /**
         * The Dilutions field id.
         */
        public static final JDataField FIELD_DILUTIONS = FIELD_DEFS.declareEqualityField("Dilutions");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ACCOUNT.equals(pField)) {
                return theAccount;
            }
            if (FIELD_DILUTIONS.equals(pField)) {
                return (theDilutions.size() > 0) ? theDilutions : JDataFieldValue.SkipField;
            }
            return super.getFieldValue(pField);
        }

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        /**
         * The account.
         */
        private final Account theAccount;

        /**
         * Dilutions list.
         */
        private final DilutionEventList theDilutions;

        /**
         * Does the account have dilutions?
         */
        private boolean hasDilutions = false;

        /**
         * Obtain account.
         * @return the account
         */
        private Account getAccount() {
            return theAccount;
        }

        /**
         * Obtain dilutions.
         * @return the dilutions
         */
        private DilutionEventList getDilutions() {
            return theDilutions;
        }

        /**
         * Do we have dilutions?
         * @return true/false
         */
        public boolean hasDilutions() {
            return hasDilutions;
        }

        /**
         * Construct an edit extract of a Price list.
         * @param pView The master view
         * @param pAccount The account to extract rates for
         */
        public ViewPriceList(final View pView,
                             final Account pAccount) {
            /* Declare the data and set the style */
            super(ViewPrice.class, pView.getData());
            setStyle(ListStyle.EDIT);

            /* Skip to alias if required */
            if ((pAccount != null)
                && (pAccount.getAlias() != null)) {
                theAccount = pAccount.getAlias();
            } else {
                theAccount = pAccount;
            }

            /* Access the base prices */
            AccountPriceList myPrices = getDataSet().getPrices();
            setBase(myPrices);

            /* Store dilution list and record whether we have dilutions */
            theDilutions = pView.getDilutions();
            hasDilutions = theDilutions.hasDilution(theAccount);

            /* Access the list iterator */
            Iterator<AccountPrice> myIterator = myPrices.listIterator();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                AccountPrice myCurr = myIterator.next();
                /* Check the account */
                int myResult = theAccount.compareTo(myCurr.getAccount());

                /* Skip different accounts */
                if (myResult != 0) {
                    continue;
                }

                /* Copy the item */
                ViewPrice myItem = new ViewPrice(this, myCurr);
                add(myItem);
            }
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return ((theAccount != null) && (theAccount.isLocked()));
        }

        @Override
        public ViewPrice addCopyItem(final DataItem pElement) {
            throw new UnsupportedOperationException();
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public ViewPrice addNewItem() {
            ViewPrice myPrice = new ViewPrice(this);
            myPrice.setAccount(theAccount);
            add(myPrice);
            return myPrice;
        }
    }
}
