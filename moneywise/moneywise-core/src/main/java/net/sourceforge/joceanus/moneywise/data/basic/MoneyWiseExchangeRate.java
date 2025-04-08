/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticResource;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateFormatter;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInstanceMap;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataMapItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * ExchangeRate class.
 */
public class MoneyWiseExchangeRate
        extends PrometheusDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.EXCHANGERATE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.EXCHANGERATE.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseExchangeRate> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseExchangeRate.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareDateField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        FIELD_DEFS.declareLinkField(MoneyWiseBasicResource.XCHGRATE_FROM);
        FIELD_DEFS.declareLinkField(MoneyWiseBasicResource.XCHGRATE_TO);
        FIELD_DEFS.declareRatioField(MoneyWiseBasicResource.XCHGRATE_RATE);
    }

    /**
     * Circular Rate Error.
     */
    public static final String ERROR_CIRCLE = MoneyWiseBasicResource.XCHGRATE_ERROR_CIRCLE.getValue();

    /**
     * Default Rate Error.
     */
    public static final String ERROR_DEF = MoneyWiseBasicResource.XCHGRATE_ERROR_DEFAULT.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pRate The Rate to copy
     */
    protected MoneyWiseExchangeRate(final MoneyWiseExchangeRateBaseList<? extends MoneyWiseExchangeRate> pList,
                                    final MoneyWiseExchangeRate pRate) {
        /* Set standard values */
        super(pList, pRate);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public MoneyWiseExchangeRate(final MoneyWiseExchangeRateBaseList<? extends MoneyWiseExchangeRate> pList) {
        super(pList, 0);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseExchangeRate(final MoneyWiseExchangeRateList pList,
                                  final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final OceanusDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            Object myValue = pValues.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            if (myValue instanceof OceanusDate d) {
                setValueDate(d);
            } else if (myValue instanceof String s) {
                final OceanusDateFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDate(s));
            }

            /* Store the From currency */
            myValue = pValues.getValue(MoneyWiseBasicResource.XCHGRATE_FROM);
            if (myValue instanceof Integer i) {
                setValueFromCurrency(i);
            } else if (myValue instanceof String s) {
                setValueFromCurrency(s);
            }

            /* Store the To currency */
            myValue = pValues.getValue(MoneyWiseBasicResource.XCHGRATE_TO);
            if (myValue instanceof Integer i) {
                setValueToCurrency(i);
            } else if (myValue instanceof String s) {
                setValueToCurrency(s);
            }

            /* Store the Rate */
            myValue = pValues.getValue(MoneyWiseBasicResource.XCHGRATE_RATE);
            if (myValue instanceof OceanusRatio r) {
                setValueExchangeRate(r);
            } else if (myValue instanceof String myString) {
                setValueExchangeRate(myString);
                setValueExchangeRate(myFormatter.parseValue(myString, OceanusRatio.class));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public MoneyWiseExchangeRate(final MoneyWiseExchangeRateList pList) {
        super(pList, 0);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        /* Access formatter */
        final OceanusDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myFormatter.formatObject(getDate()));
        myBuilder.append(" ");
        myBuilder.append(myFormatter.formatObject(getFromCurrency().getCurrency().getCurrencyCode()));
        myBuilder.append(": ");
        myBuilder.append(myFormatter.formatObject(getToCurrency().getCurrency().getCurrencyCode()));
        myBuilder.append('=');
        myBuilder.append(myFormatter.formatObject(getExchangeRate()));
        return myBuilder.toString();
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.XCHGRATE_FROM.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.XCHGRATE_TO.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.XCHGRATE_RATE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain Date.
     * @return the name
     */
    public OceanusDate getDate() {
        return getValues().getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, OceanusDate.class);
    }

    /**
     * Obtain From currency.
     * @return the currency
     */
    public MoneyWiseCurrency getFromCurrency() {
        return getValues().getValue(MoneyWiseBasicResource.XCHGRATE_FROM, MoneyWiseCurrency.class);
    }

    /**
     * Obtain fromCurrencyId.
     * @return the fromCurrencyId
     */
    public Integer getFromCurrencyId() {
        final MoneyWiseCurrency myCurr = getFromCurrency();
        return myCurr == null
                ? null
                : myCurr.getIndexedId();
    }

    /**
     * Obtain FromCurrencyName.
     * @return the fromCurrencyName
     */
    public String getFromCurrencyName() {
        final MoneyWiseCurrency myCurr = getFromCurrency();
        return myCurr == null
                ? null
                : myCurr.getName();
    }

    /**
     * Obtain To currency.
     * @return the currency
     */
    public MoneyWiseCurrency getToCurrency() {
        return getValues().getValue(MoneyWiseBasicResource.XCHGRATE_TO, MoneyWiseCurrency.class);
    }

    /**
     * Obtain toCurrencyId.
     * @return the toCurrencyId
     */
    public Integer getToCurrencyId() {
        final MoneyWiseCurrency myCurr = getToCurrency();
        return myCurr == null
                ? null
                : myCurr.getIndexedId();
    }

    /**
     * Obtain ToCurrencyName.
     * @return the toCurrencyName
     */
    public String getToCurrencyName() {
        final MoneyWiseCurrency myCurr = getToCurrency();
        return myCurr == null
                ? null
                : myCurr.getName();
    }

    /**
     * Obtain ExchangeRate.
     * @return the rate
     */
    public OceanusRatio getExchangeRate() {
        return getValues().getValue(MoneyWiseBasicResource.XCHGRATE_RATE, OceanusRatio.class);
    }

    /**
     * Obtain InverseRate.
     * @return the inverse rate
     */
    public OceanusRatio getInverseRate() {
        final OceanusRatio myRate = getExchangeRate();
        return myRate == null
                ? null
                : myRate.getInverseRatio();
    }

    /**
     * Set date value.
     * @param pValue the value
     */
    private void setValueDate(final OceanusDate pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, pValue);
    }

    /**
     * Set from currency value.
     * @param pValue the value
     */
    private void setValueFromCurrency(final MoneyWiseCurrency pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.XCHGRATE_FROM, pValue);
    }

    /**
     * Set from currency value.
     * @param pValue the value
     */
    private void setValueFromCurrency(final Integer pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.XCHGRATE_FROM, pValue);
    }

    /**
     * Set from currency value.
     * @param pValue the value
     */
    private void setValueFromCurrency(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.XCHGRATE_FROM, pValue);
    }

    /**
     * Set to currency value.
     * @param pValue the value
     */
    private void setValueToCurrency(final MoneyWiseCurrency pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.XCHGRATE_TO, pValue);
    }

    /**
     * Set to currency value.
     * @param pValue the value
     */
    private void setValueToCurrency(final Integer pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.XCHGRATE_TO, pValue);
    }

    /**
     * Set to currency value.
     * @param pValue the value
     */
    private void setValueToCurrency(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.XCHGRATE_TO, pValue);
    }

    /**
     * Set exchange rate value.
     * @param pValue the value
     */
    protected void setValueExchangeRate(final OceanusRatio pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.XCHGRATE_RATE, pValue);
    }

    /**
     * Set exchange rate value.
     * @param pValue the value
     */
    private void setValueExchangeRate(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.XCHGRATE_RATE, pValue);
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseExchangeRate getBase() {
        return (MoneyWiseExchangeRate) super.getBase();
    }

    @Override
    @SuppressWarnings("unchecked")
    public MoneyWiseExchangeRateBaseList<? extends MoneyWiseExchangeRate> getList() {
        return (MoneyWiseExchangeRateBaseList<? extends MoneyWiseExchangeRate>) super.getList();
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Access as ExchangeRate */
        final MoneyWiseExchangeRate myThat = (MoneyWiseExchangeRate) pThat;

        /* If the date differs */
        int iDiff = MetisDataDifference.compareObject(getDate(), myThat.getDate());
        if (iDiff != 0) {
            /* Sort in reverse date order !! */
            return -iDiff;
        }

        /* Compare From Currency */
        iDiff = MetisDataDifference.compareObject(getFromCurrency(), myThat.getFromCurrency());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the toCurrency */
        return getToCurrency().compareTo(myThat.getToCurrency());
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve currencies */
        final MoneyWiseDataSet myData = getDataSet();
        final MoneyWiseCurrencyList myCurrencies = myData.getAccountCurrencies();
        resolveDataLink(MoneyWiseBasicResource.XCHGRATE_FROM, myCurrencies);
        resolveDataLink(MoneyWiseBasicResource.XCHGRATE_TO, myCurrencies);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final OceanusDate pDate) {
        setValueDate(pDate);
    }

    /**
     * Set a new from currency.
     * @param pCurrency the new from currency
     */
    public void setFromCurrency(final MoneyWiseCurrency pCurrency) {
        setValueFromCurrency(pCurrency);
    }

    /**
     * Set a new to currency.
     * @param pCurrency the new to currency
     */
    public void setToCurrency(final MoneyWiseCurrency pCurrency) {
        setValueToCurrency(pCurrency);
    }

    /**
     * Set a new exchange rate.
     * @param pRate the new rate
     */
    public void setExchangeRate(final OceanusRatio pRate) {
        setValueExchangeRate(pRate);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the currencies referred to */
        getFromCurrency().touchItem(this);
        getToCurrency().touchItem(this);
    }

    /**
     * Update base rate from an edited rate.
     * @param pRate the edited rate
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pRate) {
        /* Can only update from an event exchange rate */
        if (!(pRate instanceof MoneyWiseExchangeRate)) {
            return false;
        }
        final MoneyWiseExchangeRate myRate = (MoneyWiseExchangeRate) pRate;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Date if required */
        if (!MetisDataDifference.isEqual(getDate(), myRate.getDate())) {
            setValueDate(myRate.getDate());
        }

        /* Update the from currency if required */
        if (!MetisDataDifference.isEqual(getFromCurrency(), myRate.getFromCurrency())) {
            setValueFromCurrency(myRate.getFromCurrency());
        }

        /* Update the to currency if required */
        if (!MetisDataDifference.isEqual(getToCurrency(), myRate.getToCurrency())) {
            setValueToCurrency(myRate.getToCurrency());
        }

        /* Update the rate if required */
        if (!MetisDataDifference.isEqual(getExchangeRate(), myRate.getExchangeRate())) {
            setValueExchangeRate(myRate.getExchangeRate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final MoneyWiseExchangeRateBaseList<? extends MoneyWiseExchangeRate> myList = getList();
        final MoneyWiseExchangeRateDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * Price List.
     * @param <T> the data type
     */
    public abstract static class MoneyWiseExchangeRateBaseList<T extends MoneyWiseExchangeRate>
            extends PrometheusDataList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(MoneyWiseExchangeRateBaseList.class);
        }

        /**
         * Construct an empty CORE Price list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected MoneyWiseExchangeRateBaseList(final MoneyWiseDataSet pData,
                                                final Class<T> pClass,
                                                final MoneyWiseBasicDataType pItemType) {
            /* Call super-constructor */
            super(pClass, pData, pItemType, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseExchangeRateBaseList(final MoneyWiseExchangeRateBaseList<T> pSource) {
            /* Call super-constructor */
            super(pSource);
        }

        @Override
        public MoneyWiseExchangeRateDataMap getDataMap() {
            return (MoneyWiseExchangeRateDataMap) super.getDataMap();
        }

        @Override
        protected MoneyWiseExchangeRateDataMap allocateDataMap() {
            return new MoneyWiseExchangeRateDataMap();
        }
    }

    /**
     * The ExchangeRate List class.
     */
    public static class MoneyWiseExchangeRateList
            extends MoneyWiseExchangeRateBaseList<MoneyWiseExchangeRate> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseExchangeRateList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseExchangeRateList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseStaticResource.CURRENCY_REPORTING, MoneyWiseExchangeRateList::getReportingCurrency);
        }

        /**
         * The reporting currency.
         */
        private MoneyWiseCurrency theReporting;

        /**
         * Construct an empty CORE ExchangeRate list.
         * @param pData the DataSet for the list
         */
        protected MoneyWiseExchangeRateList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWiseExchangeRate.class, MoneyWiseBasicDataType.EXCHANGERATE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseExchangeRateList(final MoneyWiseExchangeRateList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseExchangeRateList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain reporting currency.
         * @return the reporting currency
         */
        public MoneyWiseCurrency getReportingCurrency() {
            return theReporting;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseExchangeRate.FIELD_DEFS;
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        @Override
        protected MoneyWiseExchangeRateList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseExchangeRateList myList = new MoneyWiseExchangeRateList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pRate item
         * @return the newly added item
         */
        @Override
        public MoneyWiseExchangeRate addCopyItem(final PrometheusDataItem pRate) {
            /* Can only clone an ExchangeRate */
            if (!(pRate instanceof MoneyWiseExchangeRate)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseExchangeRate myRate = new MoneyWiseExchangeRate(this, (MoneyWiseExchangeRate) pRate);
            add(myRate);
            return myRate;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public MoneyWiseExchangeRate addNewItem() {
            final MoneyWiseExchangeRate myRate = new MoneyWiseExchangeRate(this);
            add(myRate);
            return myRate;
        }

        @Override
        public MoneyWiseExchangeRate addValuesItem(final PrometheusDataValues pValues)
                throws OceanusException {
            /* Create the rate */
            final MoneyWiseExchangeRate myRate = new MoneyWiseExchangeRate(this, pValues);

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

        /**
         * Convert a monetary value to the currency.
         * @param pValue the value to convert
         * @param pCurrency the required currency
         * @param pDate the date of the conversion
         * @return the converted value
         */
        public OceanusMoney convertCurrency(final OceanusMoney pValue,
                                            final MoneyWiseCurrency pCurrency,
                                            final OceanusDate pDate) {
            /* Obtain the existing currency */
            OceanusMoney myValue = pValue;
            final MoneyWiseCurrencyList myCurrencies = getDataSet().getAccountCurrencies();
            final Currency myCurrent = pValue.getCurrency();
            final Currency myReporting = theReporting.getCurrency();
            final Currency myTarget = pCurrency.getCurrency();

            /* Handle no conversion required */
            if (myCurrent.equals(myTarget)) {
                return pValue;
            }

            /* If the value is not already the reporting currency */
            if (!myCurrent.equals(myReporting)) {
                /* Find the required exchange rate */
                final OceanusRatio myRate = findRate(myCurrencies.findCurrency(myCurrent), pDate);

                /* Convert the currency */
                myValue = myValue.convertCurrency(myReporting, myRate);
            }

            /* If we need to convert to a non-default currency */
            if (!myReporting.equals(myTarget)) {
                /* Find the required exchange rate */
                final OceanusRatio myRate = findRate(pCurrency, pDate);

                /* Convert the currency */
                myValue = myValue.convertCurrency(myTarget, myRate);
            }

            /* Return the converted currency */
            return myValue;
        }

        /**
         * Find the exchange rate.
         * @param pCurrency the currency to find
         * @param pDate the date to find the exchange rate for
         * @return the exchange rate
         */
        private OceanusRatio findRate(final MoneyWiseCurrency pCurrency,
                                      final OceanusDate pDate) {
            /* pass call to data map */
            return getDataMap().getRateForDate(pCurrency, pDate);
        }

        /**
         * Set the default currency.
         * @param pCurrency the new default currency
         */
        public void setReportingCurrency(final MoneyWiseCurrency pCurrency) {
            /* Access the iterator */
            final Iterator<MoneyWiseExchangeRate> myIterator = iterator();
            OceanusRatio myCurrRate = null;
            OceanusDate myCurrDate = null;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                final MoneyWiseExchangeRate myCurr = myIterator.next();

                /* Access details */
                final OceanusDate myDate = myCurr.getDate();
                final MoneyWiseCurrency myTo = myCurr.getToCurrency();
                final OceanusRatio myRatio = myCurr.getExchangeRate();

                /* If this is a new date */
                if (myCurrDate == null || !myDate.equals(myCurrDate)) {
                    /* Access the current rate for the new default currency */
                    /*
                     * TODO This must exist on the same date or else the currency cannot be set as
                     * default
                     */
                    myCurrRate = findRate(pCurrency, myDate);
                    myCurrDate = myDate;
                }

                /* Update the item */
                myCurr.pushHistory();

                /* If this is a conversion to the new default */
                if (myTo.equals(pCurrency)) {
                    /* Switch the direction of the currencies */
                    myCurr.setToCurrency(myCurr.getFromCurrency());

                    /* Invert the ratio */
                    myCurr.setExchangeRate(myRatio.getInverseRatio());

                    /* Else does not currently involve the new currency */
                } else {
                    /* Need to combine the rates */
                    myCurr.setExchangeRate(new OceanusRatio(myRatio, myCurrRate));
                }

                /* Set from currency */
                myCurr.setFromCurrency(pCurrency);
            }

            /* Set the new reporting currency */
            theReporting = pCurrency;
        }
    }

    /**
     * The dataMap class.
     */
    public static class MoneyWiseExchangeRateDataMap
            implements PrometheusDataMapItem, MetisFieldItem {
        /**
         * Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<MoneyWiseExchangeRateDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseExchangeRateDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_MAPOFMAPS, MoneyWiseExchangeRateDataMap::getMapOfMaps);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.XCHGRATE_MAP_MAPOFRATES, MoneyWiseExchangeRateDataMap::getMapOfRates);
        }

        /**
         * Map of Maps.
         */
        private final Map<MoneyWiseCurrency, Map<OceanusDate, Integer>> theMapOfMaps;

        /**
         * Map of Rates.
         */
        private final Map<MoneyWiseCurrency, MoneyWiseRateList> theMapOfRates;

        /**
         * Constructor.
         */
        public MoneyWiseExchangeRateDataMap() {
            /* Create the maps */
            theMapOfMaps = new HashMap<>();
            theMapOfRates = new HashMap<>();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public MetisFieldSet<MoneyWiseExchangeRateDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain mapOfMaps.
         * @return the map
         */
        private Map<MoneyWiseCurrency, Map<OceanusDate, Integer>> getMapOfMaps() {
            return theMapOfMaps;
        }

        /**
         * Obtain mapOfRates.
         * @return the map
         */
        private Map<MoneyWiseCurrency, MoneyWiseRateList> getMapOfRates() {
            return theMapOfRates;
        }

        @Override
        public void resetMap() {
            theMapOfMaps.clear();
            theMapOfRates.clear();
        }

        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Access the Currency Id */
            final MoneyWiseExchangeRate myItem = (MoneyWiseExchangeRate) pItem;
            final MoneyWiseCurrency myCurrency = myItem.getToCurrency();
            if (myCurrency == null) {
                return;
            }

            /* Access the map */
            final Map<OceanusDate, Integer> myMap = theMapOfMaps.computeIfAbsent(myCurrency, c -> new HashMap<>());

            /* Adjust rate count */
            final OceanusDate myDate = myItem.getDate();
            final Integer myCount = myMap.get(myDate);
            if (myCount == null) {
                myMap.put(myDate, PrometheusDataInstanceMap.ONE);
            } else {
                myMap.put(myDate, myCount + 1);
            }

            /* Access the list */
            final MoneyWiseRateList myList = theMapOfRates.computeIfAbsent(myCurrency, MoneyWiseRateList::new);

            /* Add element to the list */
            myList.add(myItem);
        }

        /**
         * Check validity of Rate.
         * @param pItem the rate
         * @return true/false
         */
        public boolean validRateCount(final MoneyWiseExchangeRate pItem) {
            /* Access the Details */
            final MoneyWiseCurrency myCurrency = pItem.getToCurrency();
            final OceanusDate myDate = pItem.getDate();

            /* Access the map */
            final Map<OceanusDate, Integer> myMap = theMapOfMaps.get(myCurrency);
            if (myMap != null) {
                final Integer myResult = myMap.get(myDate);
                return PrometheusDataInstanceMap.ONE.equals(myResult);
            }
            return false;
        }

        /**
         * Check availability of date for a currency.
         * @param pCurrency the currency
         * @param pDate the key to look up
         * @return true/false
         */
        public boolean availableDate(final MoneyWiseCurrency pCurrency,
                                     final OceanusDate pDate) {
            /* Access the map */
            final Map<OceanusDate, Integer> myMap = theMapOfMaps.get(pCurrency);
            return myMap == null
                    || myMap.get(pDate) == null;
        }

        /**
         * Obtain rate for date.
         * @param pCurrency the currency
         * @param pDate the date
         * @return the latest rate for the date.
         */
        public OceanusRatio getRateForDate(final MoneyWiseCurrency pCurrency,
                                           final OceanusDate pDate) {
            /* Access list for currency */
            final MoneyWiseRateList myList = theMapOfRates.get(pCurrency);
            if (myList != null) {
                /* Loop through the rates */
                final Iterator<MoneyWiseExchangeRate> myIterator = myList.iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseExchangeRate myCurr = myIterator.next();

                    /* Access the date */
                    final OceanusDate myDate = myCurr.getDate();

                    /* break loop if we have the correct record */
                    if (myDate.compareTo(pDate) >= 0) {
                        return myCurr.getExchangeRate();
                    }
                }
            }

            /* return null */
            return null;
        }

        /**
         * Obtain rates for range.
         * @param pCurrency the currency
         * @param pRange the date range
         * @return the two deep array of rates for the range.
         */
        public OceanusRatio[] getRatesForRange(final MoneyWiseCurrency pCurrency,
                                               final OceanusDateRange pRange) {
            /* Set rate */
            OceanusRatio myFirst = OceanusRatio.ONE;
            OceanusRatio myLatest = OceanusRatio.ONE;
            final OceanusDate myStart = pRange.getStart();

            /* Access list for security */
            final MoneyWiseRateList myList = theMapOfRates.get(pCurrency);
            if (myList != null) {
                /* Loop through the rates */
                final ListIterator<MoneyWiseExchangeRate> myIterator = myList.listIterator(myList.size());
                while (myIterator.hasPrevious()) {
                    final MoneyWiseExchangeRate myCurr = myIterator.previous();

                    /* Check for the range of the date */
                    final OceanusDate myDate = myCurr.getDate();
                    final int iComp = pRange.compareToDate(myDate);

                    /* If this is later than the range we are finished */
                    if (iComp < 0) {
                        break;
                    }

                    /* Record as best rate */
                    myLatest = myCurr.getExchangeRate();

                    /* Adjust first rate */
                    if (iComp > 0
                            || myDate.compareTo(myStart) == 0) {
                        myFirst = myLatest;
                    }
                }
            }

            /* Return the rates */
            return new OceanusRatio[]
                    { myFirst, myLatest };
        }

        /**
         * Obtain rateList cursor.
         * @param pCurrency the currency
         * @return the latest rate for the date.
         */
        public ListIterator<MoneyWiseExchangeRate> rateIterator(final MoneyWiseCurrency pCurrency) {
            /* Access list for currency */
            final MoneyWiseRateList myList = theMapOfRates.get(pCurrency);
            return myList != null
                    ? myList.listIterator(myList.size())
                    : null;
        }

        /**
         * Rate List class.
         */
        private static final class MoneyWiseRateList
                implements MetisFieldItem, MetisDataList<MoneyWiseExchangeRate> {
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
            private final List<MoneyWiseExchangeRate> theList;

            /**
             * The currency.
             */
            private final MoneyWiseCurrency theCurrency;

            /**
             * Constructor.
             * @param pCurrency the currency
             */
            private MoneyWiseRateList(final MoneyWiseCurrency pCurrency) {
                theCurrency = pCurrency;
                theList = new ArrayList<>();
            }

            @Override
            public MetisFieldSet<MoneyWiseRateList> getDataFieldSet() {
                return FIELD_DEFS;
            }

            @Override
            public String formatObject(final OceanusDataFormatter pFormatter) {
                return theCurrency.formatObject(pFormatter)
                        + "("
                        + size()
                        + ")";
            }

            @Override
            public String toString() {
                return theCurrency.toString()
                        + "("
                        + size()
                        + ")";
            }

            @Override
            public List<MoneyWiseExchangeRate> getUnderlyingList() {
                return theList;
            }
        }
    }
}
