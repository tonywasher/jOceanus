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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusEncryptedPair;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedValueSet;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * DepositRate data type.
 * @author Tony Washer
 */
public class DepositRate
        extends EncryptedItem {
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
    public static final MetisLetheField FIELD_DEPOSIT = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataType.DEPOSIT.getItemName(), MetisDataType.LINK);

    /**
     * Rate Field Id.
     */
    public static final MetisLetheField FIELD_RATE = FIELD_DEFS.declareEqualityEncryptedField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_RATE.getValue(), MetisDataType.RATE);

    /**
     * Bonus Field Id.
     */
    public static final MetisLetheField FIELD_BONUS = FIELD_DEFS.declareEqualityEncryptedField(MoneyWiseDataResource.DEPOSITRATE_BONUS.getValue(), MetisDataType.RATE);

    /**
     * EndDate Field Id.
     */
    public static final MetisLetheField FIELD_ENDDATE = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataResource.DEPOSITRATE_ENDDATE.getValue(), MetisDataType.DATE);

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
                        final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access the formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

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
                final String myString = (String) myValue;
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
                final String myString = (String) myValue;
                setValueBonus(myString);
                setValueBonus(myFormatter.parseValue(myString, TethysRate.class));
            }

            /* Store the EndDate */
            myValue = pValues.getValue(FIELD_ENDDATE);
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
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisLetheField pField) {
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
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        /* Access Key Values */
        final EncryptedValueSet myValues = getValueSet();
        final Object myDeposit = myValues.getValue(FIELD_DEPOSIT);
        final Object myRate = myValues.getValue(FIELD_RATE);
        final Object myEndDate = myValues.getValue(FIELD_ENDDATE);

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
    private PrometheusEncryptedPair getRateField() {
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
    private PrometheusEncryptedPair getBonusField() {
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
        final Deposit myDeposit = getDeposit();
        return (myDeposit == null)
                                   ? null
                                   : myDeposit.getId();
    }

    /**
     * Obtain DepositName.
     * @return the depositName
     */
    public String getDepositName() {
        final Deposit myDeposit = getDeposit();
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
    public static TethysRate getRate(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_RATE, TethysRate.class);
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
    private static PrometheusEncryptedPair getRateField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RATE, PrometheusEncryptedPair.class);
    }

    /**
     * Obtain Bonus.
     * @param pValueSet the valueSet
     * @return the Bonus
     */
    public static TethysRate getBonus(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_BONUS, TethysRate.class);
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
    private static PrometheusEncryptedPair getBonusField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_BONUS, PrometheusEncryptedPair.class);
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
    private void setValueRate(final PrometheusEncryptedPair pValue) {
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
    private void setValueBonus(final PrometheusEncryptedPair pValue) {
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
    public int compareValues(final DataItem pThat) {
        /* Access as DepositRate */
        final DepositRate myThat = (DepositRate) pThat;

        /* If header settings differ */
        if (isHeader() != pThat.isHeader()) {
            return isHeader()
                    ? -1
                    : 1;
        }

        /* If the date differs */
        int iDiff = MetisDataDifference.compareObject(getDate(), myThat.getDate());
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
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_DEPOSIT, myData.getDeposits());
    }

    /**
     * Resolve links in an updateSet.
     * @param pUpdateSet the update Set
     * @throws OceanusException on error
     */
    private void resolveUpdateSetLinks(final UpdateSet pUpdateSet) throws OceanusException {
        /* Resolve parent within list */
        final DepositList myDeposits = pUpdateSet.getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);
        resolveDataLink(FIELD_DEPOSIT, myDeposits);
    }

    /**
     * Validate the rate.
     */
    @Override
    public void validate() {
        final DepositRateList myList = getList();
        final TethysDate myDate = getEndDate();
        final TethysRate myRate = getRate();
        final TethysRate myBonus = getBonus();

        /* Count instances of this date for the account */
        final DepositRateDataMap myMap = myList.getDataMap();
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
    public boolean applyChanges(final DataItem pRate) {
        /* Can only update from an DepositRate */
        if (!(pRate instanceof DepositRate)) {
            return false;
        }
        final DepositRate myRate = (DepositRate) pRate;

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
        final DepositRateList myList = getList();
        final DepositRateDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * List class.
     */
    public static class DepositRateList
            extends EncryptedList<DepositRate> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DepositRateList> FIELD_DEFS = MetisFieldSet.newFieldSet(DepositRateList.class);

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
            final DepositRateList myList = new DepositRateList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MetisFieldSet<DepositRateList> getDataFieldSet() {
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
        public DepositRateList deriveEditList(final UpdateSet pUpdateSet) throws OceanusException {
            /* Build an empty List */
            final DepositRateList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the list */
            final Iterator<DepositRate> myIterator = iterator();
            while (myIterator.hasNext()) {
                final DepositRate myCurr = myIterator.next();

                /* Copy the item */
                final DepositRate myItem = new DepositRate(myList, myCurr);
                myItem.resolveUpdateSetLinks(pUpdateSet);
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
        public DepositRate addCopyItem(final DataItem pRate) {
            /* Can only clone a DepositRate */
            if (!(pRate instanceof DepositRate)) {
                throw new UnsupportedOperationException();
            }

            final DepositRate myRate = new DepositRate(this, (DepositRate) pRate);
            add(myRate);
            return myRate;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public DepositRate addNewItem() {
            final DepositRate myRate = new DepositRate(this);
            add(myRate);
            return myRate;
        }

        @Override
        public DepositRate addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the rate */
            final DepositRate myRate = new DepositRate(this, pValues);

            /* Check that this RateId has not been previously added */
            if (!isIdUnique(myRate.getId())) {
                myRate.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myRate, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myRate);

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
            implements DataMapItem, MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DepositRateDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(DepositRateDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_MAP_MAPOFMAPS, DepositRateDataMap::getMapOfMaps);
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.DEPOSITRATE_MAP_MAPOFRATES, DepositRateDataMap::getMapOfRates);
        }

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
        public MetisFieldSet<DepositRateDataMap> getDataFieldSet() {
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
        private Map<Deposit, Map<TethysDate, Integer>> getMapOfMaps() {
            return theMapOfMaps;
        }

        /**
         * Obtain mapOfRates.
         * @return the map
         */
        private Map<Deposit, RateList> getMapOfRates() {
            return theMapOfRates;
        }

        @Override
        public void resetMap() {
            theMapOfMaps.clear();
            theMapOfRates.clear();
        }

        @Override
        public void adjustForItem(final DataItem pItem) {
            /* Access the Deposit Id */
            final DepositRate myItem = (DepositRate) pItem;
            final Deposit myDeposit = myItem.getDeposit();
            if (myDeposit == null) {
                return;
            }

            /* Access the map */
            final Map<TethysDate, Integer> myMap = theMapOfMaps.computeIfAbsent(myDeposit, m -> new HashMap<>());

            /* Adjust rate count */
            final TethysDate myDate = myItem.getEndDate();
            final Integer myCount = myMap.get(myDate);
            if (myCount == null) {
                myMap.put(myDate, DataInstanceMap.ONE);
            } else {
                myMap.put(myDate, myCount + 1);
            }

            /* Access the list */
            final RateList myList = theMapOfRates.computeIfAbsent(myDeposit, RateList::new);

            /* Add element to the list */
            myList.add(myItem);
        }

        /**
         * Check validity of Rate.
         * @param pItem the rate
         * @return true/false
         */
        public boolean validRateCount(final DepositRate pItem) {
            /* Access the Details */
            final Deposit myDeposit = pItem.getDeposit();
            final TethysDate myDate = pItem.getEndDate();

            /* Access the map */
            final Map<TethysDate, Integer> myMap = theMapOfMaps.get(myDeposit);
            if (myMap != null) {
                final Integer myResult = myMap.get(myDate);
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
        public DepositRate getRateForDate(final Deposit pDeposit,
                                          final TethysDate pDate) {
            /* Access list for deposit */
            final RateList myList = theMapOfRates.get(pDeposit);
            if (myList != null) {
                /* Loop through the rates */
                final ListIterator<DepositRate> myIterator = myList.listIterator(myList.size());
                while (myIterator.hasPrevious()) {
                    final DepositRate myCurr = myIterator.previous();

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
        private static final class RateList
                implements MetisFieldItem, MetisDataList<DepositRate> {
            /**
             * Report fields.
             */
            private static final MetisFieldSet<RateList> FIELD_DEFS = MetisFieldSet.newFieldSet(RateList.class);

            /*
             * UnderlyingMap Field Id.
             */
            static {
                FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE, RateList::size);
            }

            /**
             * The list.
             */
            private final List<DepositRate> theList;

            /**
             * The deposit.
             */
            private final Deposit theDeposit;

            /**
             * Constructor.
             * @param pDeposit the deposit
             */
            private RateList(final Deposit pDeposit) {
                theDeposit = pDeposit;
                theList = new ArrayList<>();
            }

            @Override
            public MetisFieldSet<RateList> getDataFieldSet() {
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
            public List<DepositRate> getUnderlyingList() {
                return theList;
            }
        }
    }
}
