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
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedRate;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;

/**
 * AccountRate data type.
 * @author Tony Washer
 */
public class DepositRate
        extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<DepositRate> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.DEPOSITRATE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.DEPOSITRATE.getListName();

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Deposit Field Id.
     */
    public static final JDataField FIELD_DEPOSIT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.DEPOSIT.getItemName());

    /**
     * Rate Field Id.
     */
    public static final JDataField FIELD_RATE = FIELD_DEFS.declareEqualityValueField(ResourceMgr.getString(MoneyWiseDataResource.MONEYWISEDATA_FIELD_RATE));

    /**
     * Bonus Field Id.
     */
    public static final JDataField FIELD_BONUS = FIELD_DEFS.declareEqualityValueField(ResourceMgr.getString(MoneyWiseDataResource.DEPOSITRATE_BONUS));

    /**
     * EndDate Field Id.
     */
    public static final JDataField FIELD_ENDDATE = FIELD_DEFS.declareEqualityValueField(ResourceMgr.getString(MoneyWiseDataResource.DEPOSITRATE_ENDDATE));

    /**
     * Null Date Error.
     */
    private static final String ERROR_NULLDATE = ResourceMgr.getString(MoneyWiseDataResource.DEPOSITRATE_ERROR_NULLDATE);

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_DEPOSIT.equals(pField)) {
            return true;
        }
        if (FIELD_RATE.equals(pField)) {
            return true;
        }
        if (FIELD_BONUS.equals(pField)) {
            return getBonus() != null;
        }
        if (FIELD_ENDDATE.equals(pField)) {
            return getEndDate() != null;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public String formatObject() {
        /* Access Key Values */
        EncryptedValueSet myValues = getValueSet();
        Object myDeposit = myValues.getValue(FIELD_DEPOSIT, Object.class);
        Object myRate = myValues.getValue(FIELD_RATE, Object.class);
        Object myEndDate = myValues.getValue(FIELD_ENDDATE, Object.class);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myFormatter.formatObject(myDeposit));
        myBuilder.append('@');
        myBuilder.append(myFormatter.formatObject(myRate));
        if (myEndDate != null) {
            myBuilder.append("->");
            myBuilder.append(myFormatter.formatObject(myEndDate));
        }

        /* return it */
        return myBuilder.toString();
    }

    /**
     * Obtain Rate.
     * @return the rate
     */
    public JRate getRate() {
        return getRate(getValueSet());
    }

    /**
     * Obtain Encrypted rate.
     * @return the Bytes
     */
    public byte[] getRateBytes() {
        return getRateBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Rate Field.
     * @return the Field
     */
    private EncryptedRate getRateField() {
        return getRateField(getValueSet());
    }

    /**
     * Obtain Bonus.
     * @return the bonus rate
     */
    public JRate getBonus() {
        return getBonus(getValueSet());
    }

    /**
     * Obtain Encrypted bonus.
     * @return the Bytes
     */
    public byte[] getBonusBytes() {
        return getBonusBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Rate Field.
     * @return the Field
     */
    private EncryptedRate getBonusField() {
        return getBonusField(getValueSet());
    }

    /**
     * Obtain date.
     * @return the date
     */
    public JDateDay getDate() {
        return getEndDate();
    }

    /**
     * Obtain End Date.
     * @return the End Date
     */
    public JDateDay getEndDate() {
        return getEndDate(getValueSet());
    }

    /**
     * Obtain Deposit.
     * @return the deposit
     */
    public Deposit getDeposit() {
        return getDeposit(getValueSet());
    }

    /**
     * Obtain DepositId.
     * @return the depositId
     */
    public Integer getDepositId() {
        Deposit myDeposit = getDeposit();
        return (myDeposit == null)
                                  ? null
                                  : myDeposit.getId();
    }

    /**
     * Obtain DepositName.
     * @return the depositName
     */
    public String getDepositName() {
        Deposit myDeposit = getDeposit();
        return (myDeposit == null)
                                  ? null
                                  : myDeposit.getName();
    }

    /**
     * Obtain Deposit.
     * @param pValueSet the valueSet
     * @return the Deposit
     */
    public static Deposit getDeposit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DEPOSIT, Deposit.class);
    }

    /**
     * Obtain Rate.
     * @param pValueSet the valueSet
     * @return the Rate
     */
    public static JRate getRate(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_RATE, JRate.class);
    }

    /**
     * Obtain Encrypted Rate.
     * @param pValueSet the valueSet
     * @return the Bytes
     */
    public static byte[] getRateBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_RATE);
    }

    /**
     * Obtain Rate Field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedRate getRateField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RATE, EncryptedRate.class);
    }

    /**
     * Obtain Bonus.
     * @param pValueSet the valueSet
     * @return the Bonus
     */
    public static JRate getBonus(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_BONUS, JRate.class);
    }

    /**
     * Obtain Encrypted Bonus.
     * @param pValueSet the valueSet
     * @return the Bytes
     */
    public static byte[] getBonusBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_BONUS);
    }

    /**
     * Obtain Bonus field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedRate getBonusField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_BONUS, EncryptedRate.class);
    }

    /**
     * Obtain End Date.
     * @param pValueSet the valueSet
     * @return the End Date
     */
    public static JDateDay getEndDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ENDDATE, JDateDay.class);
    }

    /**
     * Set the account.
     * @param pValue the account
     */
    private void setValueDeposit(final Deposit pValue) {
        getValueSet().setValue(FIELD_DEPOSIT, pValue);
    }

    /**
     * Set the deposit id.
     * @param pId the deposit id
     */
    private void setValueDeposit(final Integer pId) {
        getValueSet().setValue(FIELD_DEPOSIT, pId);
    }

    /**
     * Set the deposit name.
     * @param pName the deposit name
     */
    private void setValueDeposit(final String pName) {
        getValueSet().setValue(FIELD_DEPOSIT, pName);
    }

    /**
     * Set the rate.
     * @param pValue the rate
     * @throws JOceanusException on error
     */
    private void setValueRate(final JRate pValue) throws JOceanusException {
        setEncryptedValue(FIELD_RATE, pValue);
    }

    /**
     * Set the rate.
     * @param pBytes the encrypted rate
     * @throws JOceanusException on error
     */
    private void setValueRate(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_RATE, pBytes, JRate.class);
    }

    /**
     * Set the rate.
     * @param pValue the rate
     */
    private void setValueRate(final EncryptedRate pValue) {
        getValueSet().setValue(FIELD_RATE, pValue);
    }

    /**
     * Set the rate.
     * @param pValue the rate
     */
    private void setValueRate(final String pValue) {
        getValueSet().setValue(FIELD_RATE, pValue);
    }

    /**
     * Set the bonus rate.
     * @param pValue the bonus rate
     * @throws JOceanusException on error
     */
    private void setValueBonus(final JRate pValue) throws JOceanusException {
        setEncryptedValue(FIELD_BONUS, pValue);
    }

    /**
     * Set the encrypted bonus.
     * @param pBytes the encrypted bonus
     * @throws JOceanusException on error
     */
    private void setValueBonus(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_BONUS, pBytes, JRate.class);
    }

    /**
     * Set the bonus.
     * @param pValue the bonus
     */
    private void setValueBonus(final EncryptedRate pValue) {
        getValueSet().setValue(FIELD_BONUS, pValue);
    }

    /**
     * Set the bonus.
     * @param pValue the bonus
     */
    private void setValueBonus(final String pValue) {
        getValueSet().setValue(FIELD_BONUS, pValue);
    }

    /**
     * Set the end date.
     * @param pValue the date
     */
    private void setValueEndDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_ENDDATE, pValue);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public DepositRate getBase() {
        return (DepositRate) super.getBase();
    }

    @Override
    public DepositRateList getList() {
        return (DepositRateList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPeriod The Period to copy
     */
    protected DepositRate(final DepositRateList pList,
                          final DepositRate pPeriod) {
        /* Set standard values */
        super(pList, pPeriod);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public DepositRate(final DepositRateList pList) {
        super(pList, 0);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private DepositRate(final DepositRateList pList,
                        final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access the formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Account */
            Object myValue = pValues.getValue(FIELD_DEPOSIT);
            if (myValue instanceof Integer) {
                setValueDeposit((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueDeposit((String) myValue);
            }

            /* Store the Rate */
            myValue = pValues.getValue(FIELD_RATE);
            if (myValue instanceof JRate) {
                setValueRate((JRate) myValue);
            } else if (myValue instanceof byte[]) {
                setValueRate((byte[]) myValue);
            } else if (myValue instanceof String) {
                String myString = (String) myValue;
                setValueRate(myString);
                setValueRate(myFormatter.parseValue(myString, JRate.class));
            }

            /* Store the Bonus */
            myValue = pValues.getValue(FIELD_BONUS);
            if (myValue instanceof JRate) {
                setValueBonus((JRate) myValue);
            } else if (myValue instanceof byte[]) {
                setValueBonus((byte[]) myValue);
            } else if (myValue instanceof String) {
                String myString = (String) myValue;
                setValueBonus(myString);
                setValueBonus(myFormatter.parseValue(myString, JRate.class));
            }

            /* Store the EndDate */
            myValue = pValues.getValue(FIELD_ENDDATE);
            if (myValue instanceof JDateDay) {
                setValueEndDate((JDateDay) myValue);
            } else if (myValue instanceof String) {
                JDateDayFormatter myParser = myFormatter.getDateFormatter();
                setValueEndDate(myParser.parseDateDay((String) myValue));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException
                | JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Compare this rate to another to establish sort order.
     * @param pThat The Rate to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the sort order
     */
    @Override
    public int compareTo(final DepositRate pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If header settings differ */
        if (isHeader() != pThat.isHeader()) {
            return isHeader()
                             ? -1
                             : 1;
        }

        /* If the date differs */
        int iDiff = Difference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            /* Sort in reverse date order !! */
            return -iDiff;
        }

        /* Compare the deposits */
        iDiff = getDeposit().compareTo(pThat.getDeposit());
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

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_DEPOSIT, myData.getDeposits());
    }

    /**
     * Resolve links in an updateSet.
     * @param pUpdateSet the update Set
     * @throws JOceanusException on error
     */
    private void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* Resolve parent within list */
        DepositList myDeposits = pUpdateSet.findDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);
        resolveDataLink(FIELD_DEPOSIT, myDeposits);
    }

    /**
     * Validate the rate.
     */
    @Override
    public void validate() {
        DepositRateList myList = getList();
        JDateDay myDate = getEndDate();
        JRate myRate = getRate();
        JRate myBonus = getBonus();

        /* Count instances of this date for the account */
        int myCount = myList.countInstances(myDate, getDeposit());
        if (myCount > 1) {
            /* Each date must be unique for deposit (even null) */
            addError(myDate == null
                                   ? ERROR_NULLDATE
                                   : ERROR_DUPLICATE, FIELD_ENDDATE);
        }

        /* The Rate must be non-zero and greater than zero */
        if (myRate == null) {
            addError(ERROR_MISSING, FIELD_RATE);
        } else if (!myRate.isPositive()) {
            addError(ERROR_NEGATIVE, FIELD_RATE);
        }

        /* The bonus rate must be non-zero if it exists */
        if (myBonus != null) {
            if (myRate.isZero()) {
                addError(ERROR_ZERO, FIELD_BONUS);
            } else if (!myRate.isPositive()) {
                addError(ERROR_NEGATIVE, FIELD_BONUS);
            }
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Set the deposit.
     * @param pValue the deposit
     */
    public void setDeposit(final Deposit pValue) {
        setValueDeposit(pValue);
    }

    /**
     * Set a new rate.
     * @param pRate the rate
     * @throws JOceanusException on error
     */
    public void setRate(final JRate pRate) throws JOceanusException {
        setValueRate(pRate);
    }

    /**
     * Set a new bonus.
     * @param pBonus the rate
     * @throws JOceanusException on error
     */
    public void setBonus(final JRate pBonus) throws JOceanusException {
        setValueBonus(pBonus);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setEndDate(final JDateDay pDate) {
        setValueEndDate(new JDateDay(pDate));
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the underlying deposit */
        getDeposit().touchItem(this);
    }

    /**
     * Update Rate from a Rate extract.
     * @param pRate the updated item
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pRate) {
        /* Can only update from an DepositRate */
        if (!(pRate instanceof DepositRate)) {
            return false;
        }

        DepositRate myRate = (DepositRate) pRate;

        /* Store the current detail into history */
        pushHistory();

        /* Update the rate if required */
        if (!Difference.isEqual(getRate(), myRate.getRate())) {
            setValueRate(myRate.getRateField());
        }

        /* Update the bonus if required */
        if (!Difference.isEqual(getBonus(), myRate.getBonus())) {
            setValueBonus(myRate.getBonusField());
        }

        /* Update the date if required */
        if (!Difference.isEqual(getEndDate(), myRate.getEndDate())) {
            setValueEndDate(myRate.getEndDate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * List class.
     */
    public static class DepositRateList
            extends EncryptedList<DepositRate, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public JDataFields getItemFields() {
            return DepositRate.FIELD_DEFS;
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
         * Construct an empty CORE rate list.
         * @param pData the DataSet for the list
         */
        protected DepositRateList(final MoneyWiseData pData) {
            super(DepositRate.class, pData, MoneyWiseDataType.DEPOSITRATE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private DepositRateList(final DepositRateList pSource) {
            super(pSource);
        }

        @Override
        protected DepositRateList getEmptyList(final ListStyle pStyle) {
            DepositRateList myList = new DepositRateList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Construct an edit extract of a Rate list.
         * @return the edit list
         */
        public DepositRateList deriveEditList() {
            /* Build an empty List */
            DepositRateList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the list */
            Iterator<DepositRate> myIterator = iterator();
            while (myIterator.hasNext()) {
                DepositRate myCurr = myIterator.next();

                /* Copy the item */
                DepositRate myItem = new DepositRate(myList, myCurr);
                myList.append(myItem);
            }

            /* Return the List */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pRate item
         * @return the newly added item
         */
        @Override
        public DepositRate addCopyItem(final DataItem<?> pRate) {
            /* Can only clone a DepositRate */
            if (!(pRate instanceof DepositRate)) {
                throw new UnsupportedOperationException();
            }

            DepositRate myRate = new DepositRate(this, (DepositRate) pRate);
            add(myRate);
            return myRate;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public DepositRate addNewItem() {
            DepositRate myRate = new DepositRate(this);
            add(myRate);
            return myRate;
        }

        /**
         * Count the instances of a date.
         * @param pDate the date
         * @param pDeposit the deposit
         * @return the instance count
         */
        protected int countInstances(final JDateDay pDate,
                                     final Deposit pDeposit) {
            /* Loop through the items to find the entry */
            int iCount = 0;
            Iterator<DepositRate> myIterator = iterator();
            while (myIterator.hasNext()) {
                DepositRate myCurr = myIterator.next();
                if (Difference.isEqual(pDate, myCurr.getEndDate())
                    && pDeposit.equals(myCurr.getDeposit())) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Obtain the most relevant rate for a Deposit and a Date.
         * @param pDeposit the Deposit for which to get the rate
         * @param pDate the date from which a rate is required
         * @return The relevant Rate record
         */
        public DepositRate getLatestRate(final Deposit pDeposit,
                                         final JDateDay pDate) {
            /* Loop through the Rates */
            ListIterator<DepositRate> myIterator = listIterator();
            while (myIterator.hasPrevious()) {
                DepositRate myCurr = myIterator.previous();
                /* Skip records that do not belong to this deposit */
                if (!Difference.isEqual(myCurr.getDeposit(), pDeposit)) {
                    continue;
                }

                /* Access the date */
                JDateDay myDate = myCurr.getDate();

                /* break loop if we have the correct record */
                if ((myDate == null) || (myDate.compareTo(pDate) >= 0)) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Resolve update set links.
         * @param pUpdateSet the updateSet
         * @throws JOceanusException on error
         */
        public void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
            /* Loop through the items */
            Iterator<DepositRate> myIterator = iterator();
            while (myIterator.hasNext()) {
                DepositRate myCurr = myIterator.next();
                myCurr.resolveUpdateSetLinks(pUpdateSet);
            }
        }

        @Override
        public DepositRate addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the rate */
            DepositRate myRate = new DepositRate(this, pValues);

            /* Check that this RateId has not been previously added */
            if (!isIdUnique(myRate.getId())) {
                myRate.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myRate, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myRate);

            /* Return it */
            return myRate;
        }
    }
}
