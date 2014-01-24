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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedPrice;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jmoneywise.views.SpotPrices;
import net.sourceforge.joceanus.jmoneywise.views.SpotPrices.SpotList;
import net.sourceforge.joceanus.jmoneywise.views.SpotPrices.SpotPrice;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;

/**
 * SecurityPrice data type.
 * @author Tony Washer
 */
public class SecurityPrice
        extends EncryptedItem<MoneyWiseList>
        implements Comparable<SecurityPrice> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = SecurityPrice.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(SecurityPrice.class.getName());

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Security Field Id.
     */
    public static final JDataField FIELD_SECURITY = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataSecurity"));

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDate"));

    /**
     * Price Field Id.
     */
    public static final JDataField FIELD_PRICE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataPrice"));

    /**
     * Obtain Price.
     * @return the price
     */
    public JPrice getPrice() {
        return getPrice(getValueSet());
    }

    /**
     * Obtain Encrypted Price.
     * @return the Bytes
     */
    public byte[] getPriceBytes() {
        return getPriceBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Price Field.
     * @return the field
     */
    public EncryptedPrice getPriceField() {
        return getPriceField(getValueSet());
    }

    /**
     * Obtain Date.
     * @return the date
     */
    public JDateDay getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain Security.
     * @return the security
     */
    public Account getSecurity() {
        return getSecurity(getValueSet());
    }

    /**
     * Obtain SecurityId.
     * @return the securityId
     */
    public Integer getSecurityId() {
        Account mySecurity = getSecurity();
        return (mySecurity == null)
                                   ? null
                                   : mySecurity.getId();
    }

    /**
     * Obtain SecurityName.
     * @return the securityName
     */
    public String getSecurityName() {
        Account mySecurity = getSecurity();
        return (mySecurity == null)
                                   ? null
                                   : mySecurity.getName();
    }

    /**
     * Obtain Security.
     * @param pValueSet the valueSet
     * @return the Security
     */
    public static Account getSecurity(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_SECURITY, Account.class);
    }

    /**
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static JDateDay getDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, JDateDay.class);
    }

    /**
     * Obtain Price.
     * @param pValueSet the valueSet
     * @return the Price
     */
    public static JPrice getPrice(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_PRICE, JPrice.class);
    }

    /**
     * Obtain Encrypted Price.
     * @param pValueSet the valueSet
     * @return the Price
     */
    public static byte[] getPriceBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_PRICE);
    }

    /**
     * Obtain Price Field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedPrice getPriceField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PRICE, EncryptedPrice.class);
    }

    /**
     * Set the account.
     * @param pValue the account
     */
    protected void setSecurity(final Account pValue) {
        setValueSecurity(pValue);
    }

    /**
     * Set the security.
     * @param pValue the security
     */
    private void setValueSecurity(final Account pValue) {
        getValueSet().setValue(FIELD_SECURITY, pValue);
    }

    /**
     * Set the security id.
     * @param pId the security id
     */
    private void setValueSecurity(final Integer pId) {
        getValueSet().setValue(FIELD_SECURITY, pId);
    }

    /**
     * Set the security name.
     * @param pName the security name
     */
    private void setValueSecurity(final String pName) {
        getValueSet().setValue(FIELD_SECURITY, pName);
    }

    /**
     * Set the price.
     * @param pValue the price
     * @throws JOceanusException on error
     */
    private void setValuePrice(final JPrice pValue) throws JOceanusException {
        setEncryptedValue(FIELD_PRICE, pValue);
    }

    /**
     * Set the encrypted price.
     * @param pBytes the encrypted price
     * @throws JOceanusException on error
     */
    private void setValuePrice(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_PRICE, pBytes, JPrice.class);
    }

    /**
     * Set the price.
     * @param pValue the price
     */
    public void setValuePrice(final EncryptedPrice pValue) {
        getValueSet().setValue(FIELD_PRICE, pValue);
    }

    /**
     * Set the date.
     * @param pValue the date
     */
    private void setValueDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public SecurityPrice getBase() {
        return (SecurityPrice) super.getBase();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPrice The Price
     */
    protected SecurityPrice(final EncryptedList<? extends SecurityPrice, MoneyWiseList> pList,
                            final SecurityPrice pPrice) {
        /* Set standard values */
        super(pList, pPrice);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pSecurity the security
     */
    protected SecurityPrice(final EncryptedList<? extends SecurityPrice, MoneyWiseList> pList,
                            final Account pSecurity) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
        setValueSecurity(pSecurity);
    }

    /**
     * Open Constructor.
     * @param pList the list
     * @param pId the id
     * @param pSecurity the security name
     * @param pDate the date
     * @param pPrice the price
     * @throws JOceanusException on error
     */
    private SecurityPrice(final EncryptedList<? extends SecurityPrice, MoneyWiseList> pList,
                          final Integer pId,
                          final String pSecurity,
                          final JDateDay pDate,
                          final String pPrice) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Access the DataSet and parser */
            MoneyWiseData myDataSet = getDataSet();
            JDataFormatter myFormatter = myDataSet.getDataFormatter();
            JDecimalParser myParser = myFormatter.getDecimalParser();

            /* Record security, date and price */
            setValueSecurity(pSecurity);
            setValueDate(pDate);
            setValuePrice(myParser.parsePriceValue(pPrice));

            /* Catch Exceptions */
        } catch (IllegalArgumentException | JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Secure Constructor.
     * @param pList the list
     * @param pId the id
     * @param pControlId the control id
     * @param pSecurityId the security id
     * @param pDate the date
     * @param pPrice the price
     * @throws JOceanusException on error
     */
    private SecurityPrice(final EncryptedList<? extends SecurityPrice, MoneyWiseList> pList,
                          final Integer pId,
                          final Integer pControlId,
                          final Integer pSecurityId,
                          final JDateDay pDate,
                          final byte[] pPrice) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Record the Id */
            setValueSecurity(pSecurityId);

            /* Store the controlId */
            setControlKey(pControlId);

            /* Record the date and price */
            setValueDate(pDate);
            setValuePrice(pPrice);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public int compareTo(final SecurityPrice pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the dates */
        int iDiff = Difference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the securities */
        iDiff = getSecurity().compareTo(pThat.getSecurity());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        MoneyWiseData myData = getDataSet();
        AccountList myAccounts = myData.getAccounts();
        ValueSet myValues = getValueSet();

        /* Adjust Security */
        Object mySecurity = myValues.getValue(FIELD_SECURITY);
        if (mySecurity instanceof Account) {
            mySecurity = ((Account) mySecurity).getId();
        }
        if (mySecurity instanceof Integer) {
            Account mySec = myAccounts.findItemById((Integer) mySecurity);
            if (mySec == null) {
                addError(ERROR_UNKNOWN, FIELD_SECURITY);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueSecurity(mySec);
        } else if (mySecurity instanceof String) {
            Account mySec = myAccounts.findItemByName((String) mySecurity);
            if (mySec == null) {
                addError(ERROR_UNKNOWN, FIELD_SECURITY);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueSecurity(mySec);
        }
    }

    /**
     * Validate the price.
     */
    @Override
    public void validate() {
        JDateDay myDate = getDate();
        JPrice myPrice = getPrice();
        SecurityPriceList myList = (SecurityPriceList) getList();
        MoneyWiseData mySet = getDataSet();

        /* The date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this security */
            if (myList.countInstances(myDate, getSecurity()) > 1) {
                addError(ERROR_DUPLICATE, FIELD_DATE);
            }

            /* The date must be in-range */
            if (mySet.getDateRange().compareTo(myDate) != 0) {
                addError(ERROR_RANGE, FIELD_DATE);
            }
        }

        /* The Price must be non-zero and greater than zero */
        if (myPrice == null) {
            addError(ERROR_MISSING, FIELD_PRICE);
        } else if (myPrice.isZero()) {
            addError(ERROR_ZERO, FIELD_PRICE);
        } else if (!myPrice.isPositive()) {
            addError(ERROR_NEGATIVE, FIELD_PRICE);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Set a new price.
     * @param pPrice the price
     * @throws JOceanusException on error
     */
    public void setPrice(final JPrice pPrice) throws JOceanusException {
        setValuePrice(pPrice);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final JDateDay pDate) {
        setValueDate(pDate);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the underlying security */
        getSecurity().touchItem(this);
    }

    /**
     * Update Price from an item Element.
     * @param pItem the price extract
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pItem) {
        if (pItem instanceof SpotPrice) {
            SpotPrice mySpot = (SpotPrice) pItem;
            return applyChanges(mySpot);
        } else if (pItem instanceof SecurityPrice) {
            SecurityPrice myPrice = (SecurityPrice) pItem;
            return applyChanges(myPrice);
        }
        return false;
    }

    /**
     * Update Price from a Price extract.
     * @param pPrice the price extract
     * @return whether changes have been made
     */
    private boolean applyChanges(final SecurityPrice pPrice) {
        /* Store the current detail into history */
        pushHistory();

        /* Update the price if required */
        if (!Difference.isEqual(getPrice(), pPrice.getPrice())) {
            setValuePrice(pPrice.getPriceField());
        }

        /* Update the date if required */
        if (!Difference.isEqual(getDate(), pPrice.getDate())) {
            setValueDate(pPrice.getDate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Update Price from a Price extract.
     * @param pPrice the price extract
     * @return whether changes have been made
     */
    private boolean applyChanges(final SpotPrice pPrice) {
        /* If we are setting a null price */
        if (pPrice.getPrice() == null) {
            /* We are actually deleting the price */
            setDeleted(true);
            return true;

            /* else we have a price to set */
        }

        /* Store the current detail into history */
        pushHistory();

        /* Update the price if required */
        if (!Difference.isEqual(getPrice(), pPrice.getPrice())) {
            setValuePrice(pPrice.getPriceField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Price List.
     */
    public static class SecurityPriceList
            extends EncryptedList<SecurityPrice, MoneyWiseList> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE price list.
         * @param pData the DataSet for the list
         */
        protected SecurityPriceList(final MoneyWiseData pData) {
            super(SecurityPrice.class, pData);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private SecurityPriceList(final SecurityPriceList pSource) {
            super(pSource);
        }

        @Override
        protected SecurityPriceList getEmptyList(final ListStyle pStyle) {
            SecurityPriceList myList = new SecurityPriceList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public SecurityPriceList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (SecurityPriceList) super.cloneList(pDataSet);
        }

        /**
         * Add a new item to the core list.
         * @param pPrice item
         * @return the newly added item
         */
        @Override
        public SecurityPrice addCopyItem(final DataItem<?> pPrice) {
            if (pPrice instanceof SecurityPrice) {
                SecurityPrice myPrice = new SecurityPrice(this, (SecurityPrice) pPrice);
                add(myPrice);
                return myPrice;
            } else {
                return null;
            }
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public SecurityPrice addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Count the instances of a date.
         * @param pDate the date
         * @param pSecurity the security
         * @return The Item if present (or null)
         */
        public int countInstances(final JDateDay pDate,
                                  final Account pSecurity) {
            /* Access the list iterator */
            Iterator<SecurityPrice> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                SecurityPrice myCurr = myIterator.next();
                if (!pDate.equals(myCurr.getDate())) {
                    continue;
                }
                if (pSecurity.equals(myCurr.getSecurity())) {
                    iCount++;
                }
            }

            /* return to caller */
            return iCount;
        }

        /**
         * Obtain the most relevant price for a Date.
         * @param pSecurity the security
         * @param pDate the date from which a price is required
         * @return The relevant Price record
         */
        public SecurityPrice getLatestPrice(final Account pSecurity,
                                            final JDateDay pDate) {
            /* Skip to alias if required */
            Account mySecurity = pSecurity;
            if (mySecurity.getAlias() != null) {
                mySecurity = pSecurity.getAlias();
            }

            /* Access the list iterator */
            Iterator<SecurityPrice> myIterator = iterator();
            SecurityPrice myPrice = null;

            /* Loop through the Prices */
            while (myIterator.hasNext()) {
                SecurityPrice myCurr = myIterator.next();

                /* Skip records that do not belong to this security */
                if (!Difference.isEqual(myCurr.getSecurity(), mySecurity)) {
                    continue;
                }

                /* break loop if we have passed the date */
                if (myCurr.getDate().compareTo(pDate) > 0) {
                    break;
                }

                /* Record the best case so far */
                myPrice = myCurr;
            }

            /* Return the price */
            return myPrice;
        }

        /**
         * Apply changes from a Spot Price list.
         * @param pPrices the spot prices
         */
        public void applyChanges(final SpotPrices pPrices) {
            /* Access details */
            JDateDay myDate = pPrices.getDate();
            SpotList myList = pPrices.getPrices();

            /* Access the iterator */
            Iterator<SpotPrice> myIterator = myList.listIterator();

            /* Loop through the spot prices */
            while (myIterator.hasNext()) {
                SpotPrice mySpot = myIterator.next();

                /* Access the price for this date if it exists */
                SecurityPrice myPrice = mySpot.getBase();
                EncryptedPrice myPoint = mySpot.getPriceField();

                /* If the state is not clean */
                if (mySpot.getState() != DataState.CLEAN) {
                    /* If we have an underlying price */
                    if (myPrice != null) {
                        /* Apply changes to the underlying entry */
                        myPrice.applyChanges(mySpot);

                        /* else if we have a new price with no underlying */
                    } else if (myPoint != null) {
                        /* Create the new Price */
                        myPrice = new SecurityPrice(this, mySpot.getSecurity());

                        /* Set the date and price */
                        myPrice.setDate(new JDateDay(myDate));
                        myPrice.setValuePrice(myPoint);

                        /* Add to the list and link backwards */
                        mySpot.setBase(myPrice);
                        add(myPrice);
                    }

                    /* Clear history and set as a clean item */
                    mySpot.clearHistory();
                }
            }
        }

        /**
         * Add a Price.
         * @param pId the id
         * @param pDate the date
         * @param pSecurity the security
         * @param pPrice the price
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final JDateDay pDate,
                                final String pSecurity,
                                final String pPrice) throws JOceanusException {
            /* Create the PricePoint */
            SecurityPrice myPrice = new SecurityPrice(this, pId, pSecurity, pDate, pPrice);

            /* Check that this PriceId has not been previously added */
            if (!isIdUnique(myPrice.getId())) {
                myPrice.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPrice, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPrice);
        }

        /**
         * Load an Encrypted price.
         * @param pId the id
         * @param pControlId the control id
         * @param pDate the date
         * @param pSecurityId the security id
         * @param pPrice the price
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final JDateDay pDate,
                                  final Integer pSecurityId,
                                  final byte[] pPrice) throws JOceanusException {
            /* Create the price and PricePoint */
            SecurityPrice myPrice = new SecurityPrice(this, pId, pControlId, pSecurityId, pDate, pPrice);

            /* Check that this PriceId has not been previously added */
            if (!isIdUnique(pId)) {
                myPrice.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPrice, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPrice);
        }
    }
}
