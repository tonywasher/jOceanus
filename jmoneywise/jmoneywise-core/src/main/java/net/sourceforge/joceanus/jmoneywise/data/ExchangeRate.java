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

import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.data.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jprometheus.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;

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
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME);

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

    /**
     * From Currency Field Id.
     */
    public static final JDataField FIELD_FROM = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.XCHGRATE_FROM.getValue());

    /**
     * To Currency Field Id.
     */
    public static final JDataField FIELD_TO = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.XCHGRATE_TO.getValue());

    /**
     * Rate Type Field Id.
     */
    public static final JDataField FIELD_RATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.XCHGRATE_RATE.getValue());

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
     * @throws JOceanusException on error
     */
    private ExchangeRate(final ExchangeRateList pList,
                         final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            Object myValue = pValues.getValue(FIELD_DATE);
            if (myValue instanceof JDateDay) {
                setValueDate((JDateDay) myValue);
            } else if (myValue instanceof String) {
                JDateDayFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDateDay((String) myValue));
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
            if (myValue instanceof JRatio) {
                setValueExchangeRate((JRatio) myValue);
            } else if (myValue instanceof String) {
                String myString = (String) myValue;
                setValueExchangeRate(myString);
                setValueExchangeRate(myFormatter.parseValue(myString, JRatio.class));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
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
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return getDate() + " " + getFromCurrency().getCurrency().getCurrencyCode() + ":" + getToCurrency().getCurrency().getCurrencyCode() + "="
               + getExchangeRate().toString();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
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
    public JDateDay getDate() {
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
        AssetCurrency myCurr = getFromCurrency();
        return (myCurr == null)
                               ? null
                               : myCurr.getId();
    }

    /**
     * Obtain FromCurrencyName.
     * @return the fromCurrencyName
     */
    public String getFromCurrencyName() {
        AssetCurrency myCurr = getFromCurrency();
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
        AssetCurrency myCurr = getToCurrency();
        return (myCurr == null)
                               ? null
                               : myCurr.getId();
    }

    /**
     * Obtain ToCurrencyName.
     * @return the toCurrencyName
     */
    public String getToCurrencyName() {
        AssetCurrency myCurr = getToCurrency();
        return (myCurr == null)
                               ? null
                               : myCurr.getName();
    }

    /**
     * Obtain ExchangeRate.
     * @return the rate
     */
    public JRatio getExchangeRate() {
        return getExchangeRate(getValueSet());
    }

    /**
     * Obtain InverseRate.
     * @return the inverse rate
     */
    public JRatio getInverseRate() {
        JRatio myRate = getExchangeRate();
        return (myRate == null)
                               ? null
                               : myRate.getInverseRatio();
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
     * Obtain From Currency.
     * @param pValueSet the valueSet
     * @return the currency
     */
    public static AssetCurrency getFromCurrency(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_FROM, AssetCurrency.class);
    }

    /**
     * Obtain To Currency.
     * @param pValueSet the valueSet
     * @return the currency
     */
    public static AssetCurrency getToCurrency(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TO, AssetCurrency.class);
    }

    /**
     * Obtain ExchangeRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static JRatio getExchangeRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RATE, JRatio.class);
    }

    /**
     * Set date value.
     * @param pValue the value
     */
    private void setValueDate(final JDateDay pValue) {
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
    protected void setValueExchangeRate(final JRatio pValue) {
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
    public ExchangeRateList getList() {
        return (ExchangeRateList) super.getList();
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
        int iDiff = Difference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the from currency */
        iDiff = Difference.compareObject(getFromCurrency(), pThat.getFromCurrency());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the to currency */
        iDiff = Difference.compareObject(getToCurrency(), pThat.getToCurrency());
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

        /* Resolve currencies */
        MoneyWiseData myData = getDataSet();
        AssetCurrencyList myCurrencies = myData.getAccountCurrencies();
        resolveDataLink(FIELD_FROM, myCurrencies);
        resolveDataLink(FIELD_TO, myCurrencies);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final JDateDay pDate) {
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
    public void setExchangeRate(final JRatio pRate) {
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
        ExchangeRateList myList = getList();
        AssetCurrency myFrom = getFromCurrency();
        AssetCurrency myTo = getToCurrency();
        JDateDay myDate = getDate();
        JRatio myRate = getExchangeRate();
        JDateDayRange myRange = getDataSet().getDateRange();

        /* Date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this currency */
            ExchangeRateDataMap<? extends ExchangeRate> myMap = myList.getDataMap();
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
            AssetCurrency myDefault = getDataSet().getDefaultCurrency();
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

        ExchangeRate myRate = (ExchangeRate) pRate;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Date if required */
        if (!Difference.isEqual(getDate(), myRate.getDate())) {
            setValueDate(myRate.getDate());
        }

        /* Update the from currency if required */
        if (!Difference.isEqual(getFromCurrency(), myRate.getFromCurrency())) {
            setValueFromCurrency(myRate.getFromCurrency());
        }

        /* Update the to currency if required */
        if (!Difference.isEqual(getToCurrency(), myRate.getToCurrency())) {
            setValueToCurrency(myRate.getToCurrency());
        }

        /* Update the rate if required */
        if (!Difference.isEqual(getExchangeRate(), myRate.getExchangeRate())) {
            setValueExchangeRate(myRate.getExchangeRate());
        }

        /* Check for changes */
        return checkForHistory();
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
        protected ExchangeRateDataMap<T> getDataMap() {
            return (ExchangeRateDataMap<T>) super.getDataMap();
        }

        @Override
        protected ExchangeRateDataMap<T> allocateDataMap() {
            return new ExchangeRateDataMap<T>();
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
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        /**
         * Default Field Id.
         */
        private static final JDataField FIELD_DEFAULT = FIELD_DEFS.declareLocalField(StaticDataResource.CURRENCY_DEFAULT.getValue());

        /**
         * The default currency.
         */
        private AssetCurrency theDefault = null;

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
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
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
        public JDataFields getItemFields() {
            return ExchangeRate.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        protected ExchangeRateList getEmptyList(final ListStyle pStyle) {
            ExchangeRateList myList = new ExchangeRateList(this);
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

            ExchangeRate myRate = new ExchangeRate(this, (ExchangeRate) pRate);
            add(myRate);
            return myRate;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public ExchangeRate addNewItem() {
            ExchangeRate myRate = new ExchangeRate(this);
            add(myRate);
            return myRate;
        }

        @Override
        public ExchangeRate addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the rate */
            ExchangeRate myRate = new ExchangeRate(this, pValues);

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

        /**
         * Convert a monetary value to the currency.
         * @param pValue the value to convert
         * @param pCurrency the required currency
         * @param pDate the date of the conversion
         * @return the converted value
         */
        public JMoney convertCurrency(final JMoney pValue,
                                      final AssetCurrency pCurrency,
                                      final JDateDay pDate) {
            /* Obtain the existing currency */
            JMoney myValue = pValue;
            AssetCurrencyList myCurrencies = getDataSet().getAccountCurrencies();
            Currency myCurrent = pValue.getCurrency();
            Currency myDefault = theDefault.getCurrency();
            Currency myTarget = pCurrency.getCurrency();

            /* Handle no conversion required */
            if (myCurrent.equals(myTarget)) {
                return pValue;
            }

            /* If the value is not already the default currency */
            if (!myCurrent.equals(myDefault)) {
                /* Find the required exchange rate */
                ExchangeRate myRate = findRate(myCurrencies.findCurrency(myCurrent), pDate);

                /* Convert the currency */
                myValue = myValue.convertCurrency(myDefault, myRate.getInverseRate());
            }

            /* If we need to convert to a non-default currency */
            if (!myDefault.equals(myTarget)) {
                /* Find the required exchange rate */
                ExchangeRate myRate = findRate(pCurrency, pDate);

                /* Convert the currency */
                myValue = myValue.convertCurrency(myTarget, myRate.getExchangeRate());
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
        private ExchangeRate findRate(final AssetCurrency pCurrency,
                                      final JDateDay pDate) {
            /* Access the iterator */
            Iterator<ExchangeRate> myIterator = iterator();
            ExchangeRate myRate = null;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                ExchangeRate myCurr = myIterator.next();

                /* break loop if we have passed the date */
                if (myCurr.getDate().compareTo(pDate) > 0) {
                    break;
                }

                /* If this is the correct currency */
                if (pCurrency.equals(myCurr.getToCurrency())) {
                    /* Record as best rate */
                    myRate = myCurr;
                }
            }

            /* Return the exchange rate */
            return myRate;
        }

        /**
         * Set the default currency.
         * @param pCurrency the new default currency
         */
        public void setDefaultCurrency(final AssetCurrency pCurrency) {
            /* Access the iterator */
            Iterator<ExchangeRate> myIterator = iterator();
            JRatio myCurrRate = null;
            JDateDay myCurrDate = null;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                ExchangeRate myCurr = myIterator.next();

                /* Access details */
                JDateDay myDate = myCurr.getDate();
                AssetCurrency myTo = myCurr.getToCurrency();
                JRatio myRatio = myCurr.getExchangeRate();

                /* If this is a new date */
                if ((myCurrDate == null) || (!myDate.equals(myCurrDate))) {
                    /* Access the current rate for the new default currency */
                    /* TODO This must exist on the same date */
                    myCurrRate = findRate(pCurrency, myDate).getExchangeRate();
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
                    myCurr.setExchangeRate(new JRatio(myRatio, myCurrRate));
                }

                /* Set from currency */
                myCurr.setFromCurrency(pCurrency);
            }

            /* Set the new default currency */
            theDefault = pCurrency;
        }

        @Override
        public void postProcessOnLoad() throws JOceanusException {
            /* Resolve links and sort the data */
            resolveDataSetLinks();
            reSort();

            /* Validate the exchangeRates */
            validateOnLoad();
        }

        @Override
        public void prepareForAnalysis() {
            /* Just ensure the map */
            ensureMap();
        }

        @Override
        protected void ensureMap() {
            /* Null operation */
        }
    }

    /**
     * The dataMap class.
     * @param <T> the data type
     */
    protected static class ExchangeRateDataMap<T extends ExchangeRate>
            implements DataMapItem<T, MoneyWiseDataType>, JDataContents {
        /**
         * Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(MoneyWiseDataResource.MONEYWISEDATA_MAP_MULTIMAP.getValue());

        /**
         * CategoryMap Field Id.
         */
        public static final JDataField FIELD_MAPOFMAPS = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_MAP_MAPOFMAPS.getValue());

        /**
         * Map of Maps.
         */
        private final Map<AssetCurrency, Map<JDateDay, Integer>> theMapOfMaps;

        /**
         * Constructor.
         */
        public ExchangeRateDataMap() {
            /* Create the maps */
            theMapOfMaps = new HashMap<AssetCurrency, Map<JDateDay, Integer>>();
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_MAPOFMAPS.equals(pField)) {
                return theMapOfMaps;
            }

            /* Unknown */
            return JDataFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public void resetMap() {
            theMapOfMaps.clear();
        }

        @Override
        public void adjustForItem(final T pItem) {
            /* Access the Currency Id */
            AssetCurrency myCurrency = pItem.getToCurrency();
            if (myCurrency == null) {
                return;
            }

            /* Access the map */
            Map<JDateDay, Integer> myMap = theMapOfMaps.get(myCurrency);
            if (myMap == null) {
                myMap = new HashMap<JDateDay, Integer>();
                theMapOfMaps.put(myCurrency, myMap);
            }

            /* Adjust rate count */
            JDateDay myDate = pItem.getDate();
            Integer myCount = myMap.get(myDate);
            if (myCount == null) {
                myMap.put(myDate, DataInstanceMap.ONE);
            } else {
                myMap.put(myDate, myCount + 1);
            }
        }

        /**
         * Check validity of Rate.
         * @param pItem the rate
         * @return true/false
         */
        public boolean validRateCount(final ExchangeRate pItem) {
            /* Access the Details */
            AssetCurrency myCurrency = pItem.getToCurrency();
            JDateDay myDate = pItem.getDate();

            /* Access the map */
            Map<JDateDay, Integer> myMap = theMapOfMaps.get(myCurrency);
            if (myMap != null) {
                Integer myResult = myMap.get(myDate);
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
                                     final JDateDay pDate) {
            /* Access the map */
            Map<JDateDay, Integer> myMap = theMapOfMaps.get(pCurrency);
            return myMap != null
                                ? myMap.get(pDate) == null
                                : true;
        }
    }
}