/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.data.statics;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.data.MetisDataResource;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersionValues;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataClass;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem;

import java.util.Currency;

/**
 * AssetCurrency data type.
 *
 * @author Tony Washer
 */
public class MoneyWiseCurrency
        extends PrometheusStaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.CURRENCY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.CURRENCY.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseCurrency> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseCurrency.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareBooleanField(MoneyWiseStaticResource.CURRENCY_REPORTING);
    }

    /**
     * Copy Constructor.
     *
     * @param pList     The list to associate the Account Currency with
     * @param pCurrency The Account Currency to copy
     */
    protected MoneyWiseCurrency(final MoneyWiseCurrencyList pList,
                                final MoneyWiseCurrency pCurrency) {
        super(pList, pCurrency);
    }

    /**
     * Basic constructor.
     *
     * @param pList The list to associate the Account Currency with
     * @param pName Name of Account Currency
     * @throws OceanusException on error
     */
    private MoneyWiseCurrency(final MoneyWiseCurrencyList pList,
                              final String pName) throws OceanusException {
        super(pList, pName);
        setValueReporting(Boolean.FALSE);
        setValueEnabled(Boolean.TRUE);
        setValueDesc(getCurrencyClass().getCurrency().getDisplayName());
    }

    /**
     * Basic constructor.
     *
     * @param pList  The list to associate the Account Currency with
     * @param pClass Class of Account Currency
     * @throws OceanusException on error
     */
    private MoneyWiseCurrency(final MoneyWiseCurrencyList pList,
                              final MoneyWiseCurrencyClass pClass) throws OceanusException {
        super(pList, pClass);
        setValueReporting(Boolean.FALSE);
        setValueEnabled(Boolean.TRUE);
        setValueDesc(pClass.getCurrency().getDisplayName());
    }

    /**
     * Values constructor.
     *
     * @param pList   The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseCurrency(final MoneyWiseCurrencyList pList,
                              final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);

        /* Store the Default */
        final Object myValue = pValues.getValue(MoneyWiseStaticResource.CURRENCY_REPORTING);
        if (myValue instanceof Boolean b) {
            setValueReporting(b);
        } else if (myValue instanceof String s) {
            final OceanusDataFormatter myFormatter = getDataSet().getDataFormatter();
            setValueReporting(myFormatter.parseValue(s, Boolean.class));
        } else {
            setValueReporting(Boolean.FALSE);
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseStaticResource.CURRENCY_REPORTING.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Is this the reporting currency.
     *
     * @return true/false
     */
    public Boolean isReporting() {
        return getValues().getValue(MoneyWiseStaticResource.CURRENCY_REPORTING, Boolean.class);
    }

    /**
     * Set reporting indication.
     *
     * @param pValue the value
     */
    private void setValueReporting(final Boolean pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticResource.CURRENCY_REPORTING, pValue);
    }

    /**
     * Return the Currency class of the AccountCurrency.
     *
     * @return the class
     */
    public MoneyWiseCurrencyClass getCurrencyClass() {
        return (MoneyWiseCurrencyClass) super.getStaticClass();
    }

    @Override
    public MoneyWiseCurrency getBase() {
        return (MoneyWiseCurrency) super.getBase();
    }

    @Override
    public MoneyWiseCurrencyList getList() {
        return (MoneyWiseCurrencyList) super.getList();
    }

    /**
     * Return the Currency of the AccountCurrency.
     *
     * @return the currency
     */
    public Currency getCurrency() {
        return getCurrencyClass().getCurrency();
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Handle differences in default value */
        final MoneyWiseCurrency myThat = (MoneyWiseCurrency) pThat;
        if (!isReporting().equals(myThat.isReporting())) {
            return Boolean.TRUE.equals(isReporting())
                    ? -1
                    : 1;
        }

        /* Handle normally */
        return super.compareValues(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        final MetisFieldVersionValues myValues = getValues();

        /* Adjust Default */
        final Object myReporting = myValues.getValue(MoneyWiseStaticResource.CURRENCY_REPORTING);
        if (myReporting == null) {
            setValueReporting(Boolean.FALSE);
        }
    }

    /**
     * Set reporting indication.
     *
     * @param pReporting the new indication
     */
    public void setReporting(final Boolean pReporting) {
        setValueReporting(pReporting);
    }

    @Override
    public boolean applyChanges(final PrometheusDataItem pData) {
        /* Can only apply changes for AccountCurrency */
        if (!(pData instanceof MoneyWiseCurrency)) {
            return false;
        }

        /* Access the data */
        final MoneyWiseCurrency myData = (MoneyWiseCurrency) pData;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myData);

        /* Update the reporting indication if required */
        if (!isReporting().equals(myData.isReporting())) {
            setReporting(myData.isReporting());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Represents a list of {@link MoneyWiseCurrency} objects.
     */
    public static class MoneyWiseCurrencyList
            extends PrometheusStaticList<MoneyWiseCurrency> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseCurrencyList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCurrencyList.class);

        /**
         * Construct an empty CORE account currency list.
         *
         * @param pData the DataSet for the list
         */
        public MoneyWiseCurrencyList(final PrometheusDataSet pData) {
            super(MoneyWiseCurrency.class, pData, MoneyWiseStaticDataType.CURRENCY, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        private MoneyWiseCurrencyList(final MoneyWiseCurrencyList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseCurrencyList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseCurrency.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseCurrencyClass> getEnumClass() {
            return MoneyWiseCurrencyClass.class;
        }

        @Override
        public MoneyWiseCurrencyDataMap getDataMap() {
            return (MoneyWiseCurrencyDataMap) super.getDataMap();
        }

        @Override
        protected MoneyWiseCurrencyList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseCurrencyList myList = new MoneyWiseCurrencyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseCurrency addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone an AccountCurrency */
            if (!(pItem instanceof MoneyWiseCurrency)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseCurrency myCurr = new MoneyWiseCurrency(this, (MoneyWiseCurrency) pItem);
            add(myCurr);
            return myCurr;
        }

        @Override
        public MoneyWiseCurrency addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Obtain the type of the item.
         *
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add an AccountCurrency to the list.
         *
         * @param pCurrency the Name of the account currency
         * @return the new currency
         * @throws OceanusException on error
         */
        public MoneyWiseCurrency addBasicItem(final String pCurrency) throws OceanusException {
            /* Create a new Account Currency */
            final MoneyWiseCurrency myCurr = new MoneyWiseCurrency(this, pCurrency);

            /* Check that this AccountCurrencyId has not been previously added */
            if (!isIdUnique(myCurr.getIndexedId())) {
                myCurr.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }

            /* Add the Account Currency to the list */
            add(myCurr);
            return myCurr;
        }

        @Override
        public MoneyWiseCurrency addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the currency */
            final MoneyWiseCurrency myCurrency = new MoneyWiseCurrency(this, pValues);

            /* Check that this CurrencyId has not been previously added */
            if (!isIdUnique(myCurrency.getIndexedId())) {
                myCurrency.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCurrency, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myCurrency);

            /* Return it */
            return myCurrency;
        }

        @Override
        public void populateDefaults() throws OceanusException {
            /* Initialise the reporting currency */
            initialiseReporting();

            /* Ensure that the list is sorted */
            reSort();
        }

        /**
         * Initialise the reporting currency.
         */
        public void initialiseReporting() {
            /* Determine the default currency */
            final Currency myCurrency = OceanusMoney.getDefaultCurrency();

            /* Find the currency in the list */
            MoneyWiseCurrency myCurr = findCurrency(myCurrency);
            if (myCurr == null) {
                /* Default to GBP if local currency not found */
                myCurr = findItemByClass(MoneyWiseCurrencyClass.GBP);
            }

            /* If we have a currency */
            if (myCurr != null) {
                /* Set it as the reporting */
                myCurr.setReporting(Boolean.TRUE);
                myCurr.setValueEnabled(Boolean.TRUE);
            }
        }

        /**
         * find a currency in the list.
         *
         * @param pCurrency the currency to find
         * @return The currency
         */
        public MoneyWiseCurrency findCurrency(final Currency pCurrency) {
            /* Look up the currency */
            final MoneyWiseCurrencyClass myClass = MoneyWiseCurrencyClass.fromCurrency(pCurrency);
            return findItemByClass(myClass);
        }

        /**
         * Find the reporting currency.
         *
         * @return The reporting currency
         */
        public MoneyWiseCurrency findReporting() {
            /* look up the reporting in the map */
            final MoneyWiseCurrencyDataMap myMap = getDataMap();
            return myMap == null
                    ? null
                    : myMap.getReporting();
        }

        @Override
        protected MoneyWiseCurrency newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the currency */
            final MoneyWiseCurrency myCurr = new MoneyWiseCurrency(this, (MoneyWiseCurrencyClass) pClass);

            /* Check that this CurrId has not been previously added */
            if (!isIdUnique(myCurr.getIndexedId())) {
                myCurr.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myCurr);

            /* Return it */
            return myCurr;
        }

        /**
         * Set reporting currency.
         *
         * @param pCurrency the new reporting currency.
         */
        public void setReportingCurrency(final MoneyWiseCurrency pCurrency) {
            /* Find the reportingdefault currency */
            final MoneyWiseCurrency myCurr = findReporting();

            /* If we are changing the currency */
            if (!pCurrency.equals(myCurr)) {
                /* If we have a default value */
                if (myCurr != null) {
                    /* Clear default value */
                    myCurr.pushHistory();
                    myCurr.setReporting(Boolean.FALSE);
                }

                /* Set new currency */
                pCurrency.pushHistory();
                pCurrency.setReporting(Boolean.TRUE);
            }
        }

        @Override
        protected MoneyWiseCurrencyDataMap allocateDataMap() {
            return new MoneyWiseCurrencyDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    public static final class MoneyWiseCurrencyDataMap
            extends PrometheusStaticDataMap<MoneyWiseCurrency> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseCurrencyDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCurrencyDataMap.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseStaticResource.CURRENCY_REPORTING, MoneyWiseCurrencyDataMap::getReporting);
        }

        /**
         * Reporting value.
         */
        private MoneyWiseCurrency theReporting;

        /**
         * Reporting count.
         */
        private Integer theReportingCount;

        /**
         * Constructor.
         */
        private MoneyWiseCurrencyDataMap() {
        }

        @Override
        public MetisFieldSet<MoneyWiseCurrencyDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        @Override
        public void resetMap() {
            super.resetMap();
            theReporting = null;
            theReportingCount = null;
        }

        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Adjust order count */
            final MoneyWiseCurrency myItem = (MoneyWiseCurrency) pItem;
            if (Boolean.TRUE.equals(myItem.isReporting())) {
                theReporting = myItem;
                theReportingCount = theReportingCount == null
                        ? ONE
                        : theReportingCount + 1;
            }

            /* Adjust name/order count */
            super.adjustForItem(pItem);
        }

        /**
         * find reporting currency.
         *
         * @return the reporting currency
         */
        public MoneyWiseCurrency getReporting() {
            return theReporting;
        }

        /**
         * Check validity of report count.
         *
         * @return true/false
         */
        public boolean validReportCount() {
            return ONE.equals(theReportingCount);
        }
    }
}
