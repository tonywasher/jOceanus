/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedRate;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * DepositRate data type.
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
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    /**
     * Deposit Field Id.
     */
    public static final MetisField FIELD_DEPOSIT = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataType.DEPOSIT.getItemName(), MetisDataType.LINK);

    /**
     * Rate Field Id.
     */
    public static final MetisField FIELD_RATE = FIELD_DEFS.declareEqualityEncryptedField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_RATE.getValue(), MetisDataType.RATE);

    /**
     * Bonus Field Id.
     */
    public static final MetisField FIELD_BONUS = FIELD_DEFS.declareEqualityEncryptedField(MoneyWiseDataResource.DEPOSITRATE_BONUS.getValue(), MetisDataType.RATE);

    /**
     * EndDate Field Id.
     */
    public static final MetisField FIELD_ENDDATE = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataResource.DEPOSITRATE_ENDDATE.getValue(), MetisDataType.DATE);

    /**
     * Null Date Error.
     */
    private static final String ERROR_NULLDATE = MoneyWiseDataResource.DEPOSITRATE_ERROR_NULLDATE.getValue();

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
     * @throws OceanusException on error
     */
    private DepositRate(final DepositRateList pList,
                        final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access the formatter */
        MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

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
            if (myValue instanceof TethysRate) {
                setValueRate((TethysRate) myValue);
            } else if (myValue instanceof byte[]) {
                setValueRate((byte[]) myValue);
            } else if (myValue instanceof String) {
                String myString = (String) myValue;
                setValueRate(myString);
                setValueRate(myFormatter.parseValue(myString, TethysRate.class));
            }

            /* Store the Bonus */
            myValue = pValues.getValue(FIELD_BONUS);
            if (myValue instanceof TethysRate) {
                setValueBonus((TethysRate) myValue);
            } else if (myValue instanceof byte[]) {
                setValueBonus((byte[]) myValue);
            } else if (myValue instanceof String) {
                String myString = (String) myValue;
                setValueBonus(myString);
                setValueBonus(myFormatter.parseValue(myString, TethysRate.class));
            }

            /* Store the EndDate */
            myValue = pValues.getValue(FIELD_ENDDATE);
            if (myValue instanceof TethysDate) {
                setValueEndDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                TethysDateFormatter myParser = myFormatter.getDateFormatter();
                setValueEndDate(myParser.parseDateDay((String) myValue));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException
                | OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisField pField) {
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
        MetisEncryptedValueSet myValues = getValueSet();
        Object myDeposit = myValues.getValue(FIELD_DEPOSIT);
        Object myRate = myValues.getValue(FIELD_RATE);
        Object myEndDate = myValues.getValue(FIELD_ENDDATE);

        /* Access formatter */
        MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

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
    public TethysRate getRate() {
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
    private MetisEncryptedRate getRateField() {
        return getRateField(getValueSet());
    }

    /**
     * Obtain Bonus.
     * @return the bonus rate
     */
    public TethysRate getBonus() {
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
    private MetisEncryptedRate getBonusField() {
        return getBonusField(getValueSet());
    }

    /**
     * Obtain date.
     * @return the date
     */
    public TethysDate getDate() {
        return getEndDate();
    }

    /**
     * Obtain End Date.
     * @return the End Date
     */
    public TethysDate getEndDate() {
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
    public static Deposit getDeposit(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DEPOSIT, Deposit.class);
    }

    /**
     * Obtain Rate.
     * @param pValueSet the valueSet
     * @return the Rate
     */
    public static TethysRate getRate(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_RATE, TethysRate.class);
    }

    /**
     * Obtain Encrypted Rate.
     * @param pValueSet the valueSet
     * @return the Bytes
     */
    public static byte[] getRateBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_RATE);
    }

    /**
     * Obtain Rate Field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static MetisEncryptedRate getRateField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RATE, MetisEncryptedRate.class);
    }

    /**
     * Obtain Bonus.
     * @param pValueSet the valueSet
     * @return the Bonus
     */
    public static TethysRate getBonus(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_BONUS, TethysRate.class);
    }

    /**
     * Obtain Encrypted Bonus.
     * @param pValueSet the valueSet
     * @return the Bytes
     */
    public static byte[] getBonusBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_BONUS);
    }

    /**
     * Obtain Bonus field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static MetisEncryptedRate getBonusField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_BONUS, MetisEncryptedRate.class);
    }

    /**
     * Obtain End Date.
     * @param pValueSet the valueSet
     * @return the End Date
     */
    public static TethysDate getEndDate(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ENDDATE, TethysDate.class);
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
     * @throws OceanusException on error
     */
    private void setValueRate(final TethysRate pValue) throws OceanusException {
        setEncryptedValue(FIELD_RATE, pValue);
    }

    /**
     * Set the rate.
     * @param pBytes the encrypted rate
     * @throws OceanusException on error
     */
    private void setValueRate(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_RATE, pBytes, TethysRate.class);
    }

    /**
     * Set the rate.
     * @param pValue the rate
     */
    private void setValueRate(final MetisEncryptedRate pValue) {
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
     * @throws OceanusException on error
     */
    private void setValueBonus(final TethysRate pValue) throws OceanusException {
        setEncryptedValue(FIELD_BONUS, pValue);
    }

    /**
     * Set the encrypted bonus.
     * @param pBytes the encrypted bonus
     * @throws OceanusException on error
     */
    private void setValueBonus(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_BONUS, pBytes, TethysRate.class);
    }

    /**
     * Set the bonus.
     * @param pValue the bonus
     */
    private void setValueBonus(final MetisEncryptedRate pValue) {
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
    private void setValueEndDate(final TethysDate pValue) {
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
     * Compare this rate to another to establish sort order.
     * @param pThat The Rate to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
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
        int iDiff = MetisDifference.compareObject(getDate(), pThat.getDate());
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
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_DEPOSIT, myData.getDeposits());
    }

    /**
     * Resolve links in an updateSet.
     * @param pUpdateSet the update Set
     * @throws OceanusException on error
     */
    private void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* Resolve parent within list */
        DepositList myDeposits = pUpdateSet.getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);
        resolveDataLink(FIELD_DEPOSIT, myDeposits);
    }

    /**
     * Validate the rate.
     */
    @Override
    public void validate() {
        DepositRateList myList = getList();
        TethysDate myDate = getEndDate();
        TethysRate myRate = getRate();
        TethysRate myBonus = getBonus();

        /* Count instances of this date for the account */
        DepositRateDataMap myMap = myList.getDataMap();
        if (!myMap.validRateCount(this)) {
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
            if (myBonus.isZero()) {
                addError(ERROR_ZERO, FIELD_BONUS);
            } else if (!myBonus.isPositive()) {
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
     * @throws OceanusException on error
     */
    public void setRate(final TethysRate pRate) throws OceanusException {
        setValueRate(pRate);
    }

    /**
     * Set a new bonus.
     * @param pBonus the rate
     * @throws OceanusException on error
     */
    public void setBonus(final TethysRate pBonus) throws OceanusException {
        setValueBonus(pBonus);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setEndDate(final TethysDate pDate) {
        setValueEndDate(pDate == null
                                      ? null
                                      : new TethysDate(pDate));
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the underlying deposit */
        getDeposit().touchItem(this);
    }

    @Override
    public void touchOnUpdate() {
        /* Touch deposit */
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
        if (!MetisDifference.isEqual(getRate(), myRate.getRate())) {
            setValueRate(myRate.getRateField());
        }

        /* Update the bonus if required */
        if (!MetisDifference.isEqual(getBonus(), myRate.getBonus())) {
            setValueBonus(myRate.getBonusField());
        }

        /* Update the date if required */
        if (!MetisDifference.isEqual(getEndDate(), myRate.getEndDate())) {
            setValueEndDate(myRate.getEndDate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        DepositRateList myList = getList();
        DepositRateDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * List class.
     */
    public static class DepositRateList
            extends EncryptedList<DepositRate, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, DataList.FIELD_DEFS);

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

        @Override
        public MetisFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public MetisFields getItemFields() {
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

        @Override
        protected DepositRateDataMap getDataMap() {
            return (DepositRateDataMap) super.getDataMap();
        }

        /**
         * Construct an edit extract of a Rate list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public DepositRateList deriveEditList(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
            /* Build an empty List */
            DepositRateList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the list */
            Iterator<DepositRate> myIterator = iterator();
            while (myIterator.hasNext()) {
                DepositRate myCurr = myIterator.next();

                /* Copy the item */
                DepositRate myItem = new DepositRate(myList, myCurr);
                myItem.resolveUpdateSetLinks(pUpdateSet);
                myList.append(myItem);

                /* Adjust the map */
                myItem.adjustMapForItem();
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

        @Override
        public DepositRate addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the rate */
            DepositRate myRate = new DepositRate(this, pValues);

            /* Check that this RateId has not been previously added */
            if (!isIdUnique(myRate.getId())) {
                myRate.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myRate, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myRate);

            /* Return it */
            return myRate;
        }

        @Override
        protected DepositRateDataMap allocateDataMap() {
            return new DepositRateDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    public static class DepositRateDataMap
            implements DataMapItem<DepositRate, MoneyWiseDataType>, MetisDataContents {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDataResource.MONEYWISEDATA_MAP_MULTIMAP.getValue());

        /**
         * MapOfMaps Field Id.
         */
        private static final MetisField FIELD_MAPOFMAPS = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_MAP_MAPOFMAPS.getValue());

        /**
         * RateMap Field Id.
         */
        private static final MetisField FIELD_MAPOFRATES = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.DEPOSITRATE_MAP_MAPOFRATES.getValue());

        /**
         * Map of Maps.
         */
        private final Map<Deposit, Map<TethysDate, Integer>> theMapOfMaps;

        /**
         * Map of Rates.
         */
        private final Map<Deposit, RateList> theMapOfRates;

        /**
         * Constructor.
         */
        public DepositRateDataMap() {
            /* Create the maps */
            theMapOfMaps = new HashMap<>();
            theMapOfRates = new HashMap<>();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_MAPOFMAPS.equals(pField)) {
                return theMapOfMaps;
            }
            if (FIELD_MAPOFRATES.equals(pField)) {
                return theMapOfRates;
            }

            /* Unknown */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public void resetMap() {
            theMapOfMaps.clear();
            theMapOfRates.clear();
        }

        @Override
        public void adjustForItem(final DepositRate pItem) {
            /* Access the Deposit Id */
            Deposit myDeposit = pItem.getDeposit();
            if (myDeposit == null) {
                return;
            }

            /* Access the map */
            Map<TethysDate, Integer> myMap = theMapOfMaps.get(myDeposit);
            if (myMap == null) {
                myMap = new HashMap<>();
                theMapOfMaps.put(myDeposit, myMap);
            }

            /* Adjust rate count */
            TethysDate myDate = pItem.getEndDate();
            Integer myCount = myMap.get(myDate);
            if (myCount == null) {
                myMap.put(myDate, DataInstanceMap.ONE);
            } else {
                myMap.put(myDate, myCount + 1);
            }

            /* Access the list */
            RateList myList = theMapOfRates.get(myDeposit);
            if (myList == null) {
                myList = new RateList(myDeposit);
                theMapOfRates.put(myDeposit, myList);
            }

            /* Add element to the list */
            myList.add(pItem);
        }

        /**
         * Check validity of Rate.
         * @param pItem the rate
         * @return true/false
         */
        public boolean validRateCount(final DepositRate pItem) {
            /* Access the Details */
            Deposit myDeposit = pItem.getDeposit();
            TethysDate myDate = pItem.getEndDate();

            /* Access the map */
            Map<TethysDate, Integer> myMap = theMapOfMaps.get(myDeposit);
            if (myMap != null) {
                Integer myResult = myMap.get(myDate);
                return DataInstanceMap.ONE.equals(myResult);
            }
            return false;
        }

        /**
         * Check availability of date for a deposit.
         * @param pDeposit the deposit
         * @param pDate the key to look up
         * @return true/false
         */
        public boolean availableDate(final Deposit pDeposit,
                                     final TethysDate pDate) {
            /* Access the map */
            Map<TethysDate, Integer> myMap = theMapOfMaps.get(pDeposit);
            return myMap == null
                   || myMap.get(pDate) == null;
        }

        /**
         * Obtain rate for date.
         * @param pDeposit the deposit
         * @param pDate the date
         * @return the latest rate for the date.
         */
        public DepositRate getRateForDate(final Deposit pDeposit,
                                          final TethysDate pDate) {
            /* Access list for deposit */
            RateList myList = theMapOfRates.get(pDeposit);
            if (myList != null) {
                /* Loop through the rates */
                ListIterator<DepositRate> myIterator = myList.listIterator(myList.size());
                while (myIterator.hasPrevious()) {
                    DepositRate myCurr = myIterator.previous();

                    /* Access the date */
                    TethysDate myDate = myCurr.getDate();

                    /* break loop if we have the correct record */
                    if ((myDate == null)
                        || (myDate.compareTo(pDate) >= 0)) {
                        return myCurr;
                    }
                }
            }

            /* return null */
            return null;
        }

        /**
         * Rate List class.
         */
        private static final class RateList
                extends ArrayList<DepositRate>
                implements MetisDataContents {
            /**
             * Serial Id.
             */
            private static final long serialVersionUID = -7315972503678709555L;

            /**
             * Report fields.
             */
            private static final MetisFields FIELD_DEFS = new MetisFields(RateList.class.getSimpleName());

            /**
             * Size Field Id.
             */
            private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE.getValue());

            /**
             * The deposit.
             */
            private final transient Deposit theDeposit;

            /**
             * Constructor.
             * @param pDeposit the deposit
             */
            private RateList(final Deposit pDeposit) {
                theDeposit = pDeposit;
            }

            @Override
            public MetisFields getDataFields() {
                return FIELD_DEFS;
            }

            @Override
            public Object getFieldValue(final MetisField pField) {
                if (FIELD_SIZE.equals(pField)) {
                    return size();
                }
                return MetisFieldValue.UNKNOWN;
            }

            @Override
            public String formatObject() {
                return theDeposit.formatObject()
                       + "("
                       + size()
                       + ")";
            }

            @Override
            public String toString() {
                return formatObject();
            }
        }
    }
}