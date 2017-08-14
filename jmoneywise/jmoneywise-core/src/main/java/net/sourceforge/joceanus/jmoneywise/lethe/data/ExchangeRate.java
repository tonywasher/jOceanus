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
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

/**
 * ExchangeRate class.
 */
public class ExchangeRate
        extends DataItem<MoneyWiseDataType>
        implements Comparable<ExchangeRate> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.EXCHANGERATE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.EXCHANGERATE.getListName();

    /**
     * Local Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    /**
     * Date Field Id.
     */
    public static final MetisField FIELD_DATE = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue(), MetisDataType.DATE);

    /**
     * From Currency Field Id.
     */
    public static final MetisField FIELD_FROM = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataResource.XCHGRATE_FROM.getValue(), MetisDataType.LINK);

    /**
     * To Currency Field Id.
     */
    public static final MetisField FIELD_TO = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataResource.XCHGRATE_TO.getValue(), MetisDataType.LINK);

    /**
     * Rate Field Id.
     */
    public static final MetisField FIELD_RATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.XCHGRATE_RATE.getValue(), MetisDataType.RATIO);

    /**
     * Circular Rate Error.
     */
    private static final String ERROR_CIRCLE = MoneyWiseDataResource.XCHGRATE_ERROR_CIRCLE.getValue();

    /**
     * Default Rate Error.
     */
    private static final String ERROR_DEF = MoneyWiseDataResource.XCHGRATE_ERROR_DEFAULT.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pRate The Rate to copy
     */
    protected ExchangeRate(final ExchangeRateBaseList<? extends ExchangeRate> pList,
                           final ExchangeRate pRate) {
        /* Set standard values */
        super(pList, pRate);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public ExchangeRate(final ExchangeRateBaseList<? extends ExchangeRate> pList) {
        super(pList, 0);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private ExchangeRate(final ExchangeRateList pList,
                         final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            Object myValue = pValues.getValue(FIELD_DATE);
            if (myValue instanceof TethysDate) {
                setValueDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                final TethysDateFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDate((String) myValue));
            }

            /* Store the From currency */
            myValue = pValues.getValue(FIELD_FROM);
            if (myValue instanceof Integer) {
                setValueFromCurrency((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueFromCurrency((String) myValue);
            }

            /* Store the To currency */
            myValue = pValues.getValue(FIELD_TO);
            if (myValue instanceof Integer) {
                setValueToCurrency((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueToCurrency((String) myValue);
            }

            /* Store the Rate */
            myValue = pValues.getValue(FIELD_RATE);
            if (myValue instanceof TethysRatio) {
                setValueExchangeRate((TethysRatio) myValue);
            } else if (myValue instanceof String) {
                final String myString = (String) myValue;
                setValueExchangeRate(myString);
                setValueExchangeRate(myFormatter.parseValue(myString, TethysRatio.class));
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
    public ExchangeRate(final ExchangeRateList pList) {
        super(pList, 0);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return getDate() + " " + getFromCurrency().getCurrency().getCurrencyCode() + ":"
               + getToCurrency().getCurrency().getCurrencyCode() + "="
               + getExchangeRate().toString();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public boolean includeXmlField(final MetisField pField) {
        /* Determine whether fields should be included */
        if (FIELD_DATE.equals(pField)) {
            return true;
        }
        if (FIELD_FROM.equals(pField)) {
            return true;
        }
        if (FIELD_TO.equals(pField)) {
            return true;
        }
        if (FIELD_RATE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain Date.
     * @return the name
     */
    public TethysDate getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain From currency.
     * @return the currency
     */
    public AssetCurrency getFromCurrency() {
        return getFromCurrency(getValueSet());
    }

    /**
     * Obtain fromCurrencyId.
     * @return the fromCurrencyId
     */
    public Integer getFromCurrencyId() {
        final AssetCurrency myCurr = getFromCurrency();
        return (myCurr == null)
                                ? null
                                : myCurr.getId();
    }

    /**
     * Obtain FromCurrencyName.
     * @return the fromCurrencyName
     */
    public String getFromCurrencyName() {
        final AssetCurrency myCurr = getFromCurrency();
        return (myCurr == null)
                                ? null
                                : myCurr.getName();
    }

    /**
     * Obtain To currency.
     * @return the currency
     */
    public AssetCurrency getToCurrency() {
        return getToCurrency(getValueSet());
    }

    /**
     * Obtain toCurrencyId.
     * @return the toCurrencyId
     */
    public Integer getToCurrencyId() {
        final AssetCurrency myCurr = getToCurrency();
        return (myCurr == null)
                                ? null
                                : myCurr.getId();
    }

    /**
     * Obtain ToCurrencyName.
     * @return the toCurrencyName
     */
    public String getToCurrencyName() {
        final AssetCurrency myCurr = getToCurrency();
        return (myCurr == null)
                                ? null
                                : myCurr.getName();
    }

    /**
     * Obtain ExchangeRate.
     * @return the rate
     */
    public TethysRatio getExchangeRate() {
        return getExchangeRate(getValueSet());
    }

    /**
     * Obtain InverseRate.
     * @return the inverse rate
     */
    public TethysRatio getInverseRate() {
        final TethysRatio myRate = getExchangeRate();
        return (myRate == null)
                                ? null
                                : myRate.getInverseRatio();
    }

    /**
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static TethysDate getDate(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, TethysDate.class);
    }

    /**
     * Obtain From Currency.
     * @param pValueSet the valueSet
     * @return the currency
     */
    public static AssetCurrency getFromCurrency(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_FROM, AssetCurrency.class);
    }

    /**
     * Obtain To Currency.
     * @param pValueSet the valueSet
     * @return the currency
     */
    public static AssetCurrency getToCurrency(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TO, AssetCurrency.class);
    }

    /**
     * Obtain ExchangeRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static TethysRatio getExchangeRate(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RATE, TethysRatio.class);
    }

    /**
     * Set date value.
     * @param pValue the value
     */
    private void setValueDate(final TethysDate pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
    }

    /**
     * Set from currency value.
     * @param pValue the value
     */
    private void setValueFromCurrency(final AssetCurrency pValue) {
        getValueSet().setValue(FIELD_FROM, pValue);
    }

    /**
     * Set from currency value.
     * @param pValue the value
     */
    private void setValueFromCurrency(final Integer pValue) {
        getValueSet().setValue(FIELD_FROM, pValue);
    }

    /**
     * Set from currency value.
     * @param pValue the value
     */
    private void setValueFromCurrency(final String pValue) {
        getValueSet().setValue(FIELD_FROM, pValue);
    }

    /**
     * Set to currency value.
     * @param pValue the value
     */
    private void setValueToCurrency(final AssetCurrency pValue) {
        getValueSet().setValue(FIELD_TO, pValue);
    }

    /**
     * Set to currency value.
     * @param pValue the value
     */
    private void setValueToCurrency(final Integer pValue) {
        getValueSet().setValue(FIELD_TO, pValue);
    }

    /**
     * Set to currency value.
     * @param pValue the value
     */
    private void setValueToCurrency(final String pValue) {
        getValueSet().setValue(FIELD_TO, pValue);
    }

    /**
     * Set exchange rate value.
     * @param pValue the value
     */
    protected void setValueExchangeRate(final TethysRatio pValue) {
        getValueSet().setValue(FIELD_RATE, pValue);
    }

    /**
     * Set exchange rate value.
     * @param pValue the value
     */
    private void setValueExchangeRate(final String pValue) {
        getValueSet().setValue(FIELD_RATE, pValue);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public ExchangeRate getBase() {
        return (ExchangeRate) super.getBase();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ExchangeRateBaseList<? extends ExchangeRate> getList() {
        return (ExchangeRateBaseList<? extends ExchangeRate>) super.getList();
    }

    @Override
    public int compareTo(final ExchangeRate pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the date */
        int iDiff = MetisDifference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            /* Sort in reverse date order !! */
            return -iDiff;
        }

        /* Check the from currency */
        iDiff = MetisDifference.compareObject(getFromCurrency(), pThat.getFromCurrency());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the to currency */
        iDiff = MetisDifference.compareObject(getToCurrency(), pThat.getToCurrency());
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

        /* Resolve currencies */
        final MoneyWiseData myData = getDataSet();
        final AssetCurrencyList myCurrencies = myData.getAccountCurrencies();
        resolveDataLink(FIELD_FROM, myCurrencies);
        resolveDataLink(FIELD_TO, myCurrencies);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final TethysDate pDate) {
        setValueDate(pDate);
    }

    /**
     * Set a new from currency.
     * @param pCurrency the new from currency
     */
    public void setFromCurrency(final AssetCurrency pCurrency) {
        setValueFromCurrency(pCurrency);
    }

    /**
     * Set a new to currency.
     * @param pCurrency the new to currency
     */
    public void setToCurrency(final AssetCurrency pCurrency) {
        setValueToCurrency(pCurrency);
    }

    /**
     * Set a new exchange rate.
     * @param pRate the new rate
     */
    public void setExchangeRate(final TethysRatio pRate) {
        setValueExchangeRate(pRate);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the currencies referred to */
        getFromCurrency().touchItem(this);
        getToCurrency().touchItem(this);
    }

    @Override
    public void validate() {
        final ExchangeRateBaseList<? extends ExchangeRate> myList = getList();
        final AssetCurrency myFrom = getFromCurrency();
        final AssetCurrency myTo = getToCurrency();
        final TethysDate myDate = getDate();
        final TethysRatio myRate = getExchangeRate();
        final TethysDateRange myRange = getDataSet().getDateRange();

        /* Date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this currency */
            final ExchangeRateDataMap<? extends ExchangeRate> myMap = myList.getDataMap();
            if (!myMap.validRateCount(this)) {
                addError(ERROR_DUPLICATE, FIELD_DATE);
            }

            /* The date must be in-range */
            if (myRange.compareTo(myDate) != 0) {
                addError(ERROR_RANGE, FIELD_DATE);
            }
        }

        /* FromCurrency must be non-null and enabled */
        if (myFrom == null) {
            addError(ERROR_MISSING, FIELD_FROM);
        } else if (!myFrom.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_FROM);
        }

        /* ToCurrency must be non-null and enabled */
        if (myTo == null) {
            addError(ERROR_MISSING, FIELD_TO);
        } else if (!myTo.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_TO);
        }

        /* Check currency combination */
        if ((myFrom != null) && (myTo != null)) {
            /* Must be different */
            if (myFrom.equals(myTo)) {
                addError(ERROR_CIRCLE, FIELD_TO);
            }

            /* From currency must be the default currency */
            final AssetCurrency myDefault = getDataSet().getDefaultCurrency();
            if (!myFrom.equals(myDefault)) {
                addError(ERROR_DEF, FIELD_FROM);
            }
        }

        /* Rate must be non-null and positive non-zero */
        if (myRate == null) {
            addError(ERROR_MISSING, FIELD_RATE);
        } else if (!myRate.isNonZero()) {
            addError(ERROR_ZERO, FIELD_RATE);
        } else if (!myRate.isPositive()) {
            addError(ERROR_NEGATIVE, FIELD_RATE);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Update base rate from an edited rate.
     * @param pRate the edited rate
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pRate) {
        /* Can only update from an event exchange rate */
        if (!(pRate instanceof ExchangeRate)) {
            return false;
        }
        final ExchangeRate myRate = (ExchangeRate) pRate;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Date if required */
        if (!MetisDifference.isEqual(getDate(), myRate.getDate())) {
            setValueDate(myRate.getDate());
        }

        /* Update the from currency if required */
        if (!MetisDifference.isEqual(getFromCurrency(), myRate.getFromCurrency())) {
            setValueFromCurrency(myRate.getFromCurrency());
        }

        /* Update the to currency if required */
        if (!MetisDifference.isEqual(getToCurrency(), myRate.getToCurrency())) {
            setValueToCurrency(myRate.getToCurrency());
        }

        /* Update the rate if required */
        if (!MetisDifference.isEqual(getExchangeRate(), myRate.getExchangeRate())) {
            setValueExchangeRate(myRate.getExchangeRate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void adjustMapForItem() {
        final ExchangeRateBaseList<? extends ExchangeRate> myList = getList();
        final ExchangeRateDataMap<ExchangeRate> myMap = (ExchangeRateDataMap<ExchangeRate>) myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * Price List.
     * @param <T> the data type
     */
    public abstract static class ExchangeRateBaseList<T extends ExchangeRate>
            extends DataList<T, MoneyWiseDataType> {
        /**
         * Construct an empty CORE Price list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected ExchangeRateBaseList(final MoneyWiseData pData,
                                       final Class<T> pClass,
                                       final MoneyWiseDataType pItemType) {
            /* Call super-constructor */
            super(pClass, pData, pItemType, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected ExchangeRateBaseList(final ExchangeRateBaseList<T> pSource) {
            /* Call super-constructor */
            super(pSource);
        }

        @Override
        public ExchangeRateDataMap<T> getDataMap() {
            return (ExchangeRateDataMap<T>) super.getDataMap();
        }

        @Override
        protected ExchangeRateDataMap<T> allocateDataMap() {
            return new ExchangeRateDataMap<>();
        }
    }

    /**
     * The ExchangeRate List class.
     */
    public static class ExchangeRateList
            extends ExchangeRateBaseList<ExchangeRate> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, DataList.FIELD_DEFS);

        /**
         * Default Field Id.
         */
        private static final MetisField FIELD_DEFAULT = FIELD_DEFS.declareLocalField(StaticDataResource.CURRENCY_DEFAULT.getValue());

        /**
         * The default currency.
         */
        private AssetCurrency theDefault;

        /**
         * Construct an empty CORE ExchangeRate list.
         * @param pData the DataSet for the list
         */
        protected ExchangeRateList(final MoneyWiseData pData) {
            super(pData, ExchangeRate.class, MoneyWiseDataType.EXCHANGERATE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected ExchangeRateList(final ExchangeRateList pSource) {
            super(pSource);
        }

        @Override
        public MetisFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_DEFAULT.equals(pField)) {
                return theDefault;
            }

            /* Pass onwards */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain default currency.
         * @return the default currency
         */
        public AssetCurrency getDefaultCurrency() {
            return theDefault;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return ExchangeRate.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        protected ExchangeRateList getEmptyList(final ListStyle pStyle) {
            final ExchangeRateList myList = new ExchangeRateList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pRate item
         * @return the newly added item
         */
        @Override
        public ExchangeRate addCopyItem(final DataItem<?> pRate) {
            /* Can only clone an ExchangeRate */
            if (!(pRate instanceof ExchangeRate)) {
                throw new UnsupportedOperationException();
            }

            final ExchangeRate myRate = new ExchangeRate(this, (ExchangeRate) pRate);
            add(myRate);
            return myRate;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public ExchangeRate addNewItem() {
            final ExchangeRate myRate = new ExchangeRate(this);
            add(myRate);
            return myRate;
        }

        @Override
        public ExchangeRate addValuesItem(final DataValues<MoneyWiseDataType> pValues)
                throws OceanusException {
            /* Create the rate */
            final ExchangeRate myRate = new ExchangeRate(this, pValues);

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

        /**
         * Convert a monetary value to the currency.
         * @param pValue the value to convert
         * @param pCurrency the required currency
         * @param pDate the date of the conversion
         * @return the converted value
         */
        public TethysMoney convertCurrency(final TethysMoney pValue,
                                           final AssetCurrency pCurrency,
                                           final TethysDate pDate) {
            /* Obtain the existing currency */
            TethysMoney myValue = pValue;
            final AssetCurrencyList myCurrencies = getDataSet().getAccountCurrencies();
            final Currency myCurrent = pValue.getCurrency();
            final Currency myDefault = theDefault.getCurrency();
            final Currency myTarget = pCurrency.getCurrency();

            /* Handle no conversion required */
            if (myCurrent.equals(myTarget)) {
                return pValue;
            }

            /* If the value is not already the default currency */
            if (!myCurrent.equals(myDefault)) {
                /* Find the required exchange rate */
                final TethysRatio myRate = findRate(myCurrencies.findCurrency(myCurrent), pDate);

                /* Convert the currency */
                myValue = myValue.convertCurrency(myDefault, myRate);
            }

            /* If we need to convert to a non-default currency */
            if (!myDefault.equals(myTarget)) {
                /* Find the required exchange rate */
                final TethysRatio myRate = findRate(pCurrency, pDate);

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
        private TethysRatio findRate(final AssetCurrency pCurrency,
                                     final TethysDate pDate) {
            /* pass call to data map */
            return getDataMap().getRateForDate(pCurrency, pDate);
        }

        /**
         * Set the default currency.
         * @param pCurrency the new default currency
         */
        public void setDefaultCurrency(final AssetCurrency pCurrency) {
            /* Access the iterator */
            final Iterator<ExchangeRate> myIterator = iterator();
            TethysRatio myCurrRate = null;
            TethysDate myCurrDate = null;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                final ExchangeRate myCurr = myIterator.next();

                /* Access details */
                final TethysDate myDate = myCurr.getDate();
                final AssetCurrency myTo = myCurr.getToCurrency();
                final TethysRatio myRatio = myCurr.getExchangeRate();

                /* If this is a new date */
                if ((myCurrDate == null) || (!myDate.equals(myCurrDate))) {
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
                    myCurr.setExchangeRate(new TethysRatio(myRatio, myCurrRate));
                }

                /* Set from currency */
                myCurr.setFromCurrency(pCurrency);
            }

            /* Set the new default currency */
            theDefault = pCurrency;
        }
    }

    /**
     * The dataMap class.
     * @param <T> the data type
     */
    public static class ExchangeRateDataMap<T extends ExchangeRate>
            implements DataMapItem<T, MoneyWiseDataType>, MetisDataContents {
        /**
         * Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDataResource.MONEYWISEDATA_MAP_MULTIMAP.getValue());

        /**
         * CategoryMap Field Id.
         */
        public static final MetisField FIELD_MAPOFMAPS = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_MAP_MAPOFMAPS.getValue());

        /**
         * RateMap Field Id.
         */
        private static final MetisField FIELD_MAPOFRATES = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.XCHGRATE_MAP_MAPOFRATES.getValue());

        /**
         * Map of Maps.
         */
        private final Map<AssetCurrency, Map<TethysDate, Integer>> theMapOfMaps;

        /**
         * Map of Rates.
         */
        private final Map<AssetCurrency, RateList> theMapOfRates;

        /**
         * Constructor.
         */
        public ExchangeRateDataMap() {
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
        public void adjustForItem(final T pItem) {
            /* Access the Currency Id */
            final AssetCurrency myCurrency = pItem.getToCurrency();
            if (myCurrency == null) {
                return;
            }

            /* Access the map */
            Map<TethysDate, Integer> myMap = theMapOfMaps.get(myCurrency);
            if (myMap == null) {
                myMap = new HashMap<>();
                theMapOfMaps.put(myCurrency, myMap);
            }

            /* Adjust rate count */
            final TethysDate myDate = pItem.getDate();
            final Integer myCount = myMap.get(myDate);
            if (myCount == null) {
                myMap.put(myDate, DataInstanceMap.ONE);
            } else {
                myMap.put(myDate, myCount + 1);
            }

            /* Access the list */
            RateList myList = theMapOfRates.get(myCurrency);
            if (myList == null) {
                myList = new RateList(myCurrency);
                theMapOfRates.put(myCurrency, myList);
            }

            /* Add element to the list */
            myList.add(pItem);
        }

        /**
         * Check validity of Rate.
         * @param pItem the rate
         * @return true/false
         */
        public boolean validRateCount(final ExchangeRate pItem) {
            /* Access the Details */
            final AssetCurrency myCurrency = pItem.getToCurrency();
            final TethysDate myDate = pItem.getDate();

            /* Access the map */
            final Map<TethysDate, Integer> myMap = theMapOfMaps.get(myCurrency);
            if (myMap != null) {
                final Integer myResult = myMap.get(myDate);
                return DataInstanceMap.ONE.equals(myResult);
            }
            return false;
        }

        /**
         * Check availability of date for a currency.
         * @param pCurrency the currency
         * @param pDate the key to look up
         * @return true/false
         */
        public boolean availableDate(final AssetCurrency pCurrency,
                                     final TethysDate pDate) {
            /* Access the map */
            final Map<TethysDate, Integer> myMap = theMapOfMaps.get(pCurrency);
            return myMap == null
                   || myMap.get(pDate) == null;
        }

        /**
         * Obtain rate for date.
         * @param pCurrency the currency
         * @param pDate the date
         * @return the latest rate for the date.
         */
        public TethysRatio getRateForDate(final AssetCurrency pCurrency,
                                          final TethysDate pDate) {
            /* Access list for currency */
            final RateList myList = theMapOfRates.get(pCurrency);
            if (myList != null) {
                /* Loop through the rates */
                final ListIterator<ExchangeRate> myIterator = myList.listIterator();
                while (myIterator.hasNext()) {
                    final ExchangeRate myCurr = myIterator.next();

                    /* Access the date */
                    final TethysDate myDate = myCurr.getDate();

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
        public TethysRatio[] getRatesForRange(final AssetCurrency pCurrency,
                                              final TethysDateRange pRange) {
            /* Set rate */
            TethysRatio myFirst = TethysRatio.ONE;
            TethysRatio myLatest = TethysRatio.ONE;
            final TethysDate myStart = pRange.getStart();

            /* Access list for security */
            final RateList myList = theMapOfRates.get(pCurrency);
            if (myList != null) {
                /* Loop through the rates */
                final ListIterator<ExchangeRate> myIterator = myList.listIterator(myList.size());
                while (myIterator.hasPrevious()) {
                    final ExchangeRate myCurr = myIterator.previous();

                    /* Check for the range of the date */
                    final TethysDate myDate = myCurr.getDate();
                    final int iComp = pRange.compareTo(myDate);

                    /* If this is later than the range we are finished */
                    if (iComp < 0) {
                        break;
                    }

                    /* Record as best rate */
                    myLatest = myCurr.getExchangeRate();

                    /* Adjust first rate */
                    if ((iComp > 0)
                        || (myDate.compareTo(myStart) == 0)) {
                        myFirst = myLatest;
                    }
                }
            }

            /* Return the rates */
            return new TethysRatio[]
            { myFirst, myLatest };
        }

        /**
         * Obtain rateList cursor.
         * @param pCurrency the currency
         * @return the latest rate for the date.
         */
        public ListIterator<ExchangeRate> rateIterator(final AssetCurrency pCurrency) {
            /* Access list for currency */
            final RateList myList = theMapOfRates.get(pCurrency);
            return (myList != null)
                                    ? myList.listIterator(myList.size())
                                    : null;
        }

        /**
         * Rate List class.
         */
        private static final class RateList
                extends ArrayList<ExchangeRate>
                implements MetisDataContents {
            /**
             * Serial Id.
             */
            private static final long serialVersionUID = 438341994570572575L;

            /**
             * Report fields.
             */
            private static final MetisFields FIELD_DEFS = new MetisFields(RateList.class.getSimpleName());

            /**
             * Size Field Id.
             */
            private static final MetisField FIELD_SIZE = FIELD_DEFS
                    .declareLocalField(MetisDataResource.LIST_SIZE.getValue());

            /**
             * The currency.
             */
            private final transient AssetCurrency theCurrency;

            /**
             * Constructor.
             * @param pCurrency the currency
             */
            private RateList(final AssetCurrency pCurrency) {
                theCurrency = pCurrency;
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
                return theCurrency.formatObject()
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
