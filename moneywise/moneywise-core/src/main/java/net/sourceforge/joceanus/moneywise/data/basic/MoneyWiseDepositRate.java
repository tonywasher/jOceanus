/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataMapItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.data.PrometheusEncryptedDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusEncryptedFieldSet;
import net.sourceforge.joceanus.jprometheus.data.PrometheusEncryptedPair;
import net.sourceforge.joceanus.jprometheus.data.PrometheusEncryptedValues;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * DepositRate data type.
 * @author Tony Washer
 */
public class MoneyWiseDepositRate
        extends PrometheusEncryptedDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.DEPOSITRATE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.DEPOSITRATE.getListName();

    /**
     * Report fields.
     */
    private static final PrometheusEncryptedFieldSet<MoneyWiseDepositRate> FIELD_DEFS = PrometheusEncryptedFieldSet.newEncryptedFieldSet(MoneyWiseDepositRate.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(MoneyWiseBasicDataType.DEPOSIT);
        FIELD_DEFS.declareEncryptedRateField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE);
        FIELD_DEFS.declareEncryptedRateField(MoneyWiseBasicResource.DEPOSITRATE_BONUS);
        FIELD_DEFS.declareDateField(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE);
    }

    /**
     * Null Date Error.
     */
    private static final String ERROR_NULLDATE = MoneyWiseBasicResource.DEPOSITRATE_ERROR_NULLDATE.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPeriod The Period to copy
     */
    protected MoneyWiseDepositRate(final MoneyWiseDepositRateList pList,
                                   final MoneyWiseDepositRate pPeriod) {
        /* Set standard values */
        super(pList, pPeriod);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public MoneyWiseDepositRate(final MoneyWiseDepositRateList pList) {
        super(pList, 0);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseDepositRate(final MoneyWiseDepositRateList pList,
                                 final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access the formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Account */
            Object myValue = pValues.getValue(MoneyWiseBasicDataType.DEPOSIT);
            if (myValue instanceof Integer) {
                setValueDeposit((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueDeposit((String) myValue);
            }

            /* Store the Rate */
            myValue = pValues.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE);
            if (myValue instanceof TethysRate) {
                setValueRate((TethysRate) myValue);
            } else if (myValue instanceof byte[]) {
                setValueRate((byte[]) myValue);
            } else if (myValue instanceof String) {
                final String myString = (String) myValue;
                setValueRate(myString);
                setValueRate(myFormatter.parseValue(myString, TethysRate.class));
            }

            /* Store the Bonus */
            myValue = pValues.getValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS);
            if (myValue instanceof TethysRate) {
                setValueBonus((TethysRate) myValue);
            } else if (myValue instanceof byte[]) {
                setValueBonus((byte[]) myValue);
            } else if (myValue instanceof String) {
                final String myString = (String) myValue;
                setValueBonus(myString);
                setValueBonus(myFormatter.parseValue(myString, TethysRate.class));
            }

            /* Store the EndDate */
            myValue = pValues.getValue(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE);
            if (myValue instanceof TethysDate) {
                setValueEndDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                final TethysDateFormatter myParser = myFormatter.getDateFormatter();
                setValueEndDate(myParser.parseDate((String) myValue));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException
                 | OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseBasicDataType.DEPOSIT.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.DEPOSITRATE_BONUS.equals(pField)) {
            return getBonus() != null;
        }
        if (MoneyWiseBasicResource.DEPOSITRATE_ENDDATE.equals(pField)) {
            return getEndDate() != null;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        /* Access Key Values */
        final PrometheusEncryptedValues myValues = getValues();
        final Object myDeposit = myValues.getValue(MoneyWiseBasicDataType.DEPOSIT);
        final Object myRate = myValues.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE);
        final Object myEndDate = myValues.getValue(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE);

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();
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
        return getValues().getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE, TethysRate.class);
    }

    /**
     * Obtain Encrypted rate.
     * @return the Bytes
     */
    public byte[] getRateBytes() {
        return getValues().getEncryptedBytes(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE);
    }

    /**
     * Obtain Encrypted Rate Field.
     * @return the Field
     */
    private PrometheusEncryptedPair getRateField() {
        return getValues().getEncryptedPair(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE);
    }

    /**
     * Obtain Bonus.
     * @return the bonus rate
     */
    public TethysRate getBonus() {
        return getValues().getValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS, TethysRate.class);
    }

    /**
     * Obtain Encrypted bonus.
     * @return the Bytes
     */
    public byte[] getBonusBytes() {
        return getValues().getEncryptedBytes(MoneyWiseBasicResource.DEPOSITRATE_BONUS);
    }

    /**
     * Obtain Encrypted Rate Field.
     * @return the Field
     */
    private PrometheusEncryptedPair getBonusField() {
        return getValues().getEncryptedPair(MoneyWiseBasicResource.DEPOSITRATE_BONUS);
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
        return getValues().getValue(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE, TethysDate.class);
    }

    /**
     * Obtain Deposit.
     * @return the deposit
     */
    public MoneyWiseDeposit getDeposit() {
        return getValues().getValue(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDeposit.class);
    }

    /**
     * Obtain DepositId.
     * @return the depositId
     */
    public Integer getDepositId() {
        final MoneyWiseDeposit myDeposit = getDeposit();
        return myDeposit == null
                ? null
                : myDeposit.getIndexedId();
    }

    /**
     * Obtain DepositName.
     * @return the depositName
     */
    public String getDepositName() {
        final MoneyWiseDeposit myDeposit = getDeposit();
        return myDeposit == null
                ? null
                : myDeposit.getName();
    }

    /**
     * Set the account.
     * @param pValue the account
     */
    private void setValueDeposit(final MoneyWiseDeposit pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicDataType.DEPOSIT, pValue);
    }

    /**
     * Set the deposit id.
     * @param pId the deposit id
     */
    private void setValueDeposit(final Integer pId) {
        getValues().setUncheckedValue(MoneyWiseBasicDataType.DEPOSIT, pId);
    }

    /**
     * Set the deposit name.
     * @param pName the deposit name
     */
    private void setValueDeposit(final String pName) {
        getValues().setUncheckedValue(MoneyWiseBasicDataType.DEPOSIT, pName);
    }

    /**
     * Set the rate.
     * @param pValue the rate
     * @throws OceanusException on error
     */
    private void setValueRate(final TethysRate pValue) throws OceanusException {
        setEncryptedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE, pValue);
    }

    /**
     * Set the rate.
     * @param pBytes the encrypted rate
     * @throws OceanusException on error
     */
    private void setValueRate(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE, pBytes, TethysRate.class);
    }

    /**
     * Set the rate.
     * @param pValue the rate
     */
    private void setValueRate(final PrometheusEncryptedPair pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE, pValue);
    }

    /**
     * Set the rate.
     * @param pValue the rate
     */
    private void setValueRate(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE, pValue);
    }

    /**
     * Set the bonus rate.
     * @param pValue the bonus rate
     * @throws OceanusException on error
     */
    private void setValueBonus(final TethysRate pValue) throws OceanusException {
        setEncryptedValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS, pValue);
    }

    /**
     * Set the encrypted bonus.
     * @param pBytes the encrypted bonus
     * @throws OceanusException on error
     */
    private void setValueBonus(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS, pBytes, TethysRate.class);
    }

    /**
     * Set the bonus.
     * @param pValue the bonus
     */
    private void setValueBonus(final PrometheusEncryptedPair pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS, pValue);
    }

    /**
     * Set the bonus.
     * @param pValue the bonus
     */
    private void setValueBonus(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS, pValue);
    }

    /**
     * Set the end date.
     * @param pValue the date
     */
    private void setValueEndDate(final TethysDate pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE, pValue);
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseDepositRate getBase() {
        return (MoneyWiseDepositRate) super.getBase();
    }

    @Override
    public MoneyWiseDepositRateList getList() {
        return (MoneyWiseDepositRateList) super.getList();
    }

    /**
     * Compare this rate to another to establish sort order.
     * @param pThat The Rate to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Access as DepositRate */
        final MoneyWiseDepositRate myThat = (MoneyWiseDepositRate) pThat;

        /* If header settings differ */
        if (isHeader() != pThat.isHeader()) {
            return isHeader()
                    ? -1
                    : 1;
        }

        /* If the date differs */
        final int iDiff = MetisDataDifference.compareObject(getDate(), myThat.getDate());
        if (iDiff != 0) {
            /* Sort in reverse date order !! */
            return -iDiff;
        }

        /* Compare the deposits */
        return getDeposit().compareTo(myThat.getDeposit());
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(MoneyWiseBasicDataType.DEPOSIT, myData.getDeposits());
    }

    /**
     * Resolve links in an updateSet.
     * @param pEditSet the edit Set
     * @throws OceanusException on error
     */
    private void resolveEditSetLinks(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Resolve parent within list */
        final MoneyWiseDepositList myDeposits = pEditSet.getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class);
        resolveDataLink(MoneyWiseBasicDataType.DEPOSIT, myDeposits);
    }

    /**
     * Validate the rate.
     */
    @Override
    public void validate() {
        final MoneyWiseDepositRateList myList = getList();
        final TethysDate myDate = getEndDate();
        final TethysRate myRate = getRate();
        final TethysRate myBonus = getBonus();

        /* Count instances of this date for the account */
        final MoneyWiseDepositRateDataMap myMap = myList.getDataMap();
        if (!myMap.validRateCount(this)) {
            /* Each date must be unique for deposit (even null) */
            addError(myDate == null
                    ? ERROR_NULLDATE
                    : ERROR_DUPLICATE, MoneyWiseBasicResource.DEPOSITRATE_ENDDATE);
        }

        /* The Rate must be non-zero and greater than zero */
        if (myRate == null) {
            addError(ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE);
        } else if (!myRate.isPositive()) {
            addError(ERROR_NEGATIVE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE);
        }

        /* The bonus rate must be non-zero if it exists */
        if (myBonus != null) {
            if (myBonus.isZero()) {
                addError(ERROR_ZERO, MoneyWiseBasicResource.DEPOSITRATE_BONUS);
            } else if (!myBonus.isPositive()) {
                addError(ERROR_NEGATIVE, MoneyWiseBasicResource.DEPOSITRATE_BONUS);
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
    public void setDeposit(final MoneyWiseDeposit pValue) {
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
    public boolean applyChanges(final PrometheusDataItem pRate) {
        /* Can only update from an DepositRate */
        if (!(pRate instanceof MoneyWiseDepositRate)) {
            return false;
        }
        final MoneyWiseDepositRate myRate = (MoneyWiseDepositRate) pRate;

        /* Store the current detail into history */
        pushHistory();

        /* Update the rate if required */
        if (!MetisDataDifference.isEqual(getRate(), myRate.getRate())) {
            setValueRate(myRate.getRateField());
        }

        /* Update the bonus if required */
        if (!MetisDataDifference.isEqual(getBonus(), myRate.getBonus())) {
            setValueBonus(myRate.getBonusField());
        }

        /* Update the date if required */
        if (!MetisDataDifference.isEqual(getEndDate(), myRate.getEndDate())) {
            setValueEndDate(myRate.getEndDate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final MoneyWiseDepositRateList myList = getList();
        final MoneyWiseDepositRateDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * List class.
     */
    public static class MoneyWiseDepositRateList
            extends PrometheusEncryptedList<MoneyWiseDepositRate> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseDepositRateList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositRateList.class);

        /**
         * Construct an empty CORE rate list.
         * @param pData the DataSet for the list
         */
        protected MoneyWiseDepositRateList(final MoneyWiseDataSet pData) {
            super(MoneyWiseDepositRate.class, pData, MoneyWiseBasicDataType.DEPOSITRATE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseDepositRateList(final MoneyWiseDepositRateList pSource) {
            super(pSource);
        }

        @Override
        protected MoneyWiseDepositRateList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseDepositRateList myList = new MoneyWiseDepositRateList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MetisFieldSet<MoneyWiseDepositRateList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseDepositRate.FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        @Override
        protected MoneyWiseDepositRateDataMap getDataMap() {
            return (MoneyWiseDepositRateDataMap) super.getDataMap();
        }

        /**
         * Construct an edit extract of a Rate list.
         * @param pEditSet the editSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public MoneyWiseDepositRateList deriveEditList(final PrometheusEditSet pEditSet) throws OceanusException {
            /* Build an empty List */
            final MoneyWiseDepositRateList myList = getEmptyList(PrometheusListStyle.EDIT);
            myList.ensureMap();
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.DEPOSITRATE, myList);

            /* Loop through the list */
            final Iterator<MoneyWiseDepositRate> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseDepositRate myCurr = myIterator.next();

                /* Copy the item */
                final MoneyWiseDepositRate myItem = new MoneyWiseDepositRate(myList, myCurr);
                myItem.resolveEditSetLinks(pEditSet);
                myList.add(myItem);

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
        public MoneyWiseDepositRate addCopyItem(final PrometheusDataItem pRate) {
            /* Can only clone a DepositRate */
            if (!(pRate instanceof MoneyWiseDepositRate)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseDepositRate myRate = new MoneyWiseDepositRate(this, (MoneyWiseDepositRate) pRate);
            add(myRate);
            return myRate;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public MoneyWiseDepositRate addNewItem() {
            final MoneyWiseDepositRate myRate = new MoneyWiseDepositRate(this);
            add(myRate);
            return myRate;
        }

        @Override
        public MoneyWiseDepositRate addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the rate */
            final MoneyWiseDepositRate myRate = new MoneyWiseDepositRate(this, pValues);

            /* Check that this RateId has not been previously added */
            if (!isIdUnique(myRate.getIndexedId())) {
                myRate.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myRate, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myRate);

            /* Return it */
            return myRate;
        }

        @Override
        protected MoneyWiseDepositRateDataMap allocateDataMap() {
            return new MoneyWiseDepositRateDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    public static class MoneyWiseDepositRateDataMap
            implements PrometheusDataMapItem, MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseDepositRateDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositRateDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_MAPOFMAPS, MoneyWiseDepositRateDataMap::getMapOfMaps);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.DEPOSITRATE_MAP_MAPOFRATES, MoneyWiseDepositRateDataMap::getMapOfRates);
        }

        /**
         * Map of Maps.
         */
        private final Map<MoneyWiseDeposit, Map<TethysDate, Integer>> theMapOfMaps;

        /**
         * Map of Rates.
         */
        private final Map<MoneyWiseDeposit, MoneyWiseRateList> theMapOfRates;

        /**
         * Constructor.
         */
        public MoneyWiseDepositRateDataMap() {
            /* Create the maps */
            theMapOfMaps = new HashMap<>();
            theMapOfRates = new HashMap<>();
        }

        @Override
        public MetisFieldSet<MoneyWiseDepositRateDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain mapOfMaps.
         * @return the map
         */
        private Map<MoneyWiseDeposit, Map<TethysDate, Integer>> getMapOfMaps() {
            return theMapOfMaps;
        }

        /**
         * Obtain mapOfRates.
         * @return the map
         */
        private Map<MoneyWiseDeposit, MoneyWiseRateList> getMapOfRates() {
            return theMapOfRates;
        }

        @Override
        public void resetMap() {
            theMapOfMaps.clear();
            theMapOfRates.clear();
        }

        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Access the Deposit Id */
            final MoneyWiseDepositRate myItem = (MoneyWiseDepositRate) pItem;
            final MoneyWiseDeposit myDeposit = myItem.getDeposit();
            if (myDeposit == null) {
                return;
            }

            /* Access the map */
            final Map<TethysDate, Integer> myMap = theMapOfMaps.computeIfAbsent(myDeposit, m -> new HashMap<>());

            /* Adjust rate count */
            final TethysDate myDate = myItem.getEndDate();
            final Integer myCount = myMap.get(myDate);
            if (myCount == null) {
                myMap.put(myDate, PrometheusDataInstanceMap.ONE);
            } else {
                myMap.put(myDate, myCount + 1);
            }

            /* Access the list */
            final MoneyWiseRateList myList = theMapOfRates.computeIfAbsent(myDeposit, MoneyWiseRateList::new);

            /* Add element to the list */
            myList.add(myItem);
        }

        /**
         * Check validity of Rate.
         * @param pItem the rate
         * @return true/false
         */
        public boolean validRateCount(final MoneyWiseDepositRate pItem) {
            /* Access the Details */
            final MoneyWiseDeposit myDeposit = pItem.getDeposit();
            final TethysDate myDate = pItem.getEndDate();

            /* Access the map */
            final Map<TethysDate, Integer> myMap = theMapOfMaps.get(myDeposit);
            if (myMap != null) {
                final Integer myResult = myMap.get(myDate);
                return PrometheusDataInstanceMap.ONE.equals(myResult);
            }
            return false;
        }

        /**
         * Check availability of date for a deposit.
         * @param pDeposit the deposit
         * @param pDate the key to look up
         * @return true/false
         */
        public boolean availableDate(final MoneyWiseDeposit pDeposit,
                                     final TethysDate pDate) {
            /* Access the map */
            final Map<TethysDate, Integer> myMap = theMapOfMaps.get(pDeposit);
            return myMap == null
                    || myMap.get(pDate) == null;
        }

        /**
         * Obtain rate for date.
         * @param pDeposit the deposit
         * @param pDate the date
         * @return the latest rate for the date.
         */
        public MoneyWiseDepositRate getRateForDate(final MoneyWiseDeposit pDeposit,
                                                   final TethysDate pDate) {
            /* Access list for deposit */
            final MoneyWiseRateList myList = theMapOfRates.get(pDeposit);
            if (myList != null) {
                /* Loop through the rates */
                final ListIterator<MoneyWiseDepositRate> myIterator = myList.listIterator(myList.size());
                while (myIterator.hasPrevious()) {
                    final MoneyWiseDepositRate myCurr = myIterator.previous();

                    /* Access the date */
                    final TethysDate myDate = myCurr.getDate();

                    /* break loop if we have the correct record */
                    if (myDate == null
                            || myDate.compareTo(pDate) >= 0) {
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
        private static final class MoneyWiseRateList
                implements MetisFieldItem, MetisDataList<MoneyWiseDepositRate> {
            /**
             * Report fields.
             */
            private static final MetisFieldSet<MoneyWiseRateList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseRateList.class);

            /*
             * UnderlyingMap Field Id.
             */
            static {
                FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE, MoneyWiseRateList::size);
            }

            /**
             * The list.
             */
            private final List<MoneyWiseDepositRate> theList;

            /**
             * The deposit.
             */
            private final MoneyWiseDeposit theDeposit;

            /**
             * Constructor.
             * @param pDeposit the deposit
             */
            private MoneyWiseRateList(final MoneyWiseDeposit pDeposit) {
                theDeposit = pDeposit;
                theList = new ArrayList<>();
            }

            @Override
            public MetisFieldSet<MoneyWiseRateList> getDataFieldSet() {
                return FIELD_DEFS;
            }

            @Override
            public String formatObject(final TethysUIDataFormatter pFormatter) {
                return theDeposit.formatObject(pFormatter)
                        + "("
                        + size()
                        + ")";
            }

            @Override
            public String toString() {
                return theDeposit.toString()
                        + "("
                        + size()
                        + ")";
            }

            @Override
            public List<MoneyWiseDepositRate> getUnderlyingList() {
                return theList;
            }
        }
    }
}
