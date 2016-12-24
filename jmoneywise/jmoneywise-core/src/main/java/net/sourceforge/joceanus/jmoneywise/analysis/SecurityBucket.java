/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Currency;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdItem;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate.ExchangeRateDataMap;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceDataMap;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * The Security Bucket class.
 */
public final class SecurityBucket
        implements MetisDataContents, Comparable<SecurityBucket>, MetisOrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.SECURITY_NAME.getValue());

    /**
     * Analysis Field Id.
     */
    private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * SecurityHolding Field Id.
     */
    private static final MetisField FIELD_HOLDING = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.ASSETTYPE_SECURITYHOLDING.getValue());

    /**
     * Currency Field Id.
     */
    private static final MetisField FIELD_CURRENCY = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.CURRENCY.getItemName());

    /**
     * Security Type Field Id.
     */
    private static final MetisField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.SECURITYTYPE.getItemName());

    /**
     * Base Field Id.
     */
    private static final MetisField FIELD_BASE = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES.getValue());

    /**
     * History Field Id.
     */
    private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY.getValue());

    /**
     * FieldSet map.
     */
    private static final Map<MetisField, SecurityAttribute> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, SecurityAttribute.class);

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The security holding.
     */
    private final SecurityHolding theHolding;

    /**
     * The security.
     */
    private final Security theSecurity;

    /**
     * The portfolio.
     */
    private final Portfolio thePortfolio;

    /**
     * The currency.
     */
    private final AssetCurrency theCurrency;

    /**
     * Is this a foreign currency?
     */
    private final Boolean isForeignCurrency;

    /**
     * The security type.
     */
    private final SecurityType theCategory;

    /**
     * The dataSet.
     */
    private final MoneyWiseData theData;

    /**
     * Values.
     */
    private final SecurityValues theValues;

    /**
     * The base values.
     */
    private final SecurityValues theBaseValues;

    /**
     * History Map.
     */
    private final BucketHistory<SecurityValues, SecurityAttribute> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pHolding the security holding
     */
    private SecurityBucket(final Analysis pAnalysis,
                           final SecurityHolding pHolding) {
        /* Store the details */
        theHolding = pHolding;
        theCurrency = pHolding.getAssetCurrency();
        theSecurity = pHolding.getSecurity();
        thePortfolio = pHolding.getPortfolio();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Obtain category */
        theCategory = theSecurity.getSecurityType();

        /* Determine currency */
        AssetCurrency myReportingCurrency = pAnalysis.getCurrency();
        AssetCurrency myHoldingCurrency = pHolding.getAssetCurrency();

        /* Determine whether we are a foreign currency */
        isForeignCurrency = !MetisDifference.isEqual(myReportingCurrency, myHoldingCurrency);
        Currency myCurrency = AccountBucket.deriveCurrency(myHoldingCurrency);
        Currency myRepCurrency = AccountBucket.deriveCurrency(myReportingCurrency);

        /* Create the history map */
        SecurityValues myValues = isForeignCurrency
                                                    ? new SecurityValues(myCurrency, myRepCurrency)
                                                    : new SecurityValues(myCurrency);
        theHistory = new BucketHistory<>(myValues);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    private SecurityBucket(final Analysis pAnalysis,
                           final SecurityBucket pBase) {
        /* Copy details from base */
        theHolding = pBase.getSecurityHolding();
        theCurrency = pBase.getCurrency();
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getSecurityType();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap());

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private SecurityBucket(final Analysis pAnalysis,
                           final SecurityBucket pBase,
                           final TethysDate pDate) {
        /* Copy details from base */
        theHolding = pBase.getSecurityHolding();
        theCurrency = pBase.getCurrency();
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getSecurityType();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap(), pDate);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private SecurityBucket(final Analysis pAnalysis,
                           final SecurityBucket pBase,
                           final TethysDateRange pRange) {
        /* Copy details from base */
        theHolding = pBase.getSecurityHolding();
        theCurrency = pBase.getCurrency();
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getSecurityType();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_HOLDING.equals(pField)) {
            return theHolding;
        }
        if (FIELD_CURRENCY.equals(pField)) {
            return theCurrency;
        }
        if (FIELD_CATEGORY.equals(pField)) {
            return theCategory;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_BASE.equals(pField)) {
            return theBaseValues;
        }

        /* Handle Attribute fields */
        SecurityAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof TethysDecimal) {
                return ((TethysDecimal) myValue).isNonZero()
                                                             ? myValue
                                                             : MetisFieldValue.SKIP;
            }
            return myValue;
        }

        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theSecurity.getName();
    }

    /**
     * Obtain the decorated name.
     * @return the decorated name
     */
    public String getDecoratedName() {
        return theHolding.getName();
    }

    /**
     * Obtain the holding.
     * @return the holding
     */
    public SecurityHolding getSecurityHolding() {
        return theHolding;
    }

    /**
     * Obtain the security.
     * @return the security
     */
    public Security getSecurity() {
        return theSecurity;
    }

    /**
     * Obtain the portfolio.
     * @return the portfolio
     */
    public Portfolio getPortfolio() {
        return thePortfolio;
    }

    /**
     * Obtain the currency.
     * @return the currency
     */
    public AssetCurrency getCurrency() {
        return theCurrency;
    }

    /**
     * Is this a foreign currency?
     * @return true/false
     */
    public Boolean isForeignCurrency() {
        return isForeignCurrency;
    }

    @Override
    public Integer getOrderedId() {
        return theSecurity.getId();
    }

    /**
     * Obtain the security type.
     * @return the security type
     */
    public SecurityType getSecurityType() {
        return theCategory;
    }

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    protected MoneyWiseData getDataSet() {
        return theData;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain date range.
     * @return the range
     */
    public TethysDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public SecurityValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public SecurityValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public SecurityValues getValuesForTransaction(final Transaction pTrans) {
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain previous values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public SecurityValues getPreviousValuesForTransaction(final Transaction pTrans) {
        return theHistory.getPreviousValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForTransaction(final Transaction pTrans,
                                                final SecurityAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain money delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysMoney getMoneyDeltaForTransaction(final Transaction pTrans,
                                                   final SecurityAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaMoneyValue(pTrans, pAttr);
    }

    /**
     * Obtain units delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysUnits getUnitsDeltaForTransaction(final Transaction pTrans,
                                                   final SecurityAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaUnitsValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<SecurityValues, SecurityAttribute> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final SecurityAttribute pAttr,
                            final Object pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final SecurityAttribute pAttr) {
        /* Access value of object */
        Object myValue = getAttribute(pAttr);

        /* Return the value */
        return (myValue != null)
                                 ? myValue
                                 : MetisFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static SecurityAttribute getClassForField(final MetisField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getAttribute(final SecurityAttribute pAttr) {
        /* Obtain the value */
        return theValues.getValue(pAttr);
    }

    @Override
    public int compareTo(final SecurityBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the portfolio */
        int iDiff = MetisDifference.compareObject(getPortfolio(), pThat.getPortfolio());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the Securities */
        return getSecurity().compareTo(pThat.getSecurity());
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }
        if (!(pThat instanceof SecurityBucket)) {
            return false;
        }

        /* Compare the Portfolios */
        SecurityBucket myThat = (SecurityBucket) pThat;
        if (!getPortfolio().equals(myThat.getPortfolio())) {
            return false;
        }

        /* Compare the Securities */
        if (!getSecurity().equals(myThat.getSecurity())) {
            return false;
        }

        /* Compare the date ranges */
        return getDateRange().equals(myThat.getDateRange());
    }

    @Override
    public int hashCode() {
        return getSecurity().hashCode();
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    protected void adjustCounter(final SecurityAttribute pAttr,
                                 final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(pAttr);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    protected void adjustCounter(final SecurityAttribute pAttr,
                                 final TethysUnits pDelta) {
        TethysUnits myValue = theValues.getUnitsValue(pAttr);
        myValue = new TethysUnits(myValue);
        myValue.addUnits(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Register the transaction.
     * @param pHelper the helper
     * @return the registered values
     */
    protected SecurityValues registerTransaction(final TransactionHelper pHelper) {
        /* Register the event in the history */
        return theHistory.registerTransaction(pHelper.getTransaction(), theValues);
    }

    /**
     * value the asset for a particular range.
     * @param pRange the range of valuation
     */
    private void valueAsset(final TethysDateRange pRange) {
        /* Obtain the appropriate price */
        MoneyWiseData myData = theAnalysis.getData();
        SecurityPriceDataMap<SecurityPrice> myPriceMap = myData.getSecurityPriceDataMap();
        TethysPrice[] myPrices = myPriceMap.getPricesForRange(theSecurity, pRange);

        /* Access base units */
        TethysUnits myUnits = theBaseValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysPrice myPrice = myPrices[0];

        /* Calculate the value */
        theBaseValues.setValue(SecurityAttribute.PRICE, myPrice);
        theBaseValues.setValue(SecurityAttribute.VALUATION, myUnits.valueAtPrice(myPrice));

        /* Access units */
        myUnits = theValues.getUnitsValue(SecurityAttribute.UNITS);
        myPrice = myPrices[1];

        /* Calculate the value */
        setValue(SecurityAttribute.PRICE, myPrice);
        setValue(SecurityAttribute.VALUATION, myUnits.valueAtPrice(myPrice));
    }

    /**
     * value the foreign asset for a particular range.
     * @param pRange the range of valuation
     */
    private void valueForeignAsset(final TethysDateRange pRange) {
        /* Obtain the appropriate price */
        MoneyWiseData myData = theAnalysis.getData();
        SecurityPriceDataMap<SecurityPrice> myPriceMap = myData.getSecurityPriceDataMap();
        TethysPrice[] myPrices = myPriceMap.getPricesForRange(theSecurity, pRange);
        ExchangeRateDataMap<ExchangeRate> myRateMap = myData.getExchangeRateDataMap();
        TethysRatio[] myRates = myRateMap.getRatesForRange(theSecurity.getAssetCurrency(), pRange);
        Currency myCurrency = theAnalysis.getCurrency().getCurrency();

        /* Access base units */
        TethysUnits myUnits = theBaseValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysPrice myPrice = myPrices[0];
        TethysRatio myRate = myRates[0];

        /* Calculate the value */
        TethysMoney myValue = myUnits.valueAtPrice(myPrice);
        TethysMoney myLocalValue = myValue.convertCurrency(myCurrency, myRate.getInverseRatio());

        /* Record it */
        theBaseValues.setValue(SecurityAttribute.PRICE, myPrice);
        theBaseValues.setValue(SecurityAttribute.FOREIGNVALUE, myValue);
        theBaseValues.setValue(SecurityAttribute.EXCHANGERATE, myRate);
        theBaseValues.setValue(SecurityAttribute.VALUATION, myLocalValue);

        /* Access units */
        myUnits = theValues.getUnitsValue(SecurityAttribute.UNITS);
        myPrice = myPrices[1];
        myRate = myRates[1];

        /* Calculate the value */
        myValue = myUnits.valueAtPrice(myPrice);
        myLocalValue = myValue.convertCurrency(myCurrency, myRate.getInverseRatio());

        /* Record it */
        setValue(SecurityAttribute.PRICE, myPrice);
        setValue(SecurityAttribute.EXCHANGERATE, myRate);
        setValue(SecurityAttribute.FOREIGNVALUE, myValue);
        setValue(SecurityAttribute.VALUATION, myLocalValue);
    }

    /**
     * calculate the profit for a priced asset.
     */
    private void calculateProfit() {
        /* Calculate the profit */
        TethysMoney myValuation = theValues.getMoneyValue(SecurityAttribute.VALUEDELTA);
        TethysMoney myProfit = new TethysMoney(myValuation);
        myProfit.subtractAmount(theValues.getMoneyValue(SecurityAttribute.INVESTED));
        myProfit.addAmount(theValues.getMoneyValue(SecurityAttribute.DIVIDEND));
        myProfit.addAmount(theValues.getMoneyValue(SecurityAttribute.GROWTHADJUST));

        /* Set the attribute */
        setValue(SecurityAttribute.PROFIT, myProfit);

        /* Calculate the profit minus the dividend */
        TethysMoney myMarketProfit = new TethysMoney(myProfit);
        myMarketProfit.subtractAmount(theValues.getMoneyValue(SecurityAttribute.DIVIDEND));
        setValue(SecurityAttribute.MARKETPROFIT, myMarketProfit);
    }

    /**
     * calculate the deltas for a priced asset.
     */
    private void calculateDeltas() {
        /* Obtain a copy of the value */
        TethysMoney myValue = theValues.getMoneyValue(SecurityAttribute.VALUATION);
        myValue = new TethysMoney(myValue);

        /* Subtract any base value */
        myValue.subtractAmount(theBaseValues.getMoneyValue(SecurityAttribute.VALUATION));

        /* Set the delta */
        setValue(SecurityAttribute.VALUEDELTA, myValue);

        if (isForeignCurrency) {
            /* Obtain a copy of the value */
            myValue = theValues.getMoneyValue(SecurityAttribute.FOREIGNVALUE);
            myValue = new TethysMoney(myValue);

            /* Subtract any base value */
            myValue.subtractAmount(theBaseValues.getMoneyValue(SecurityAttribute.FOREIGNVALUE));

            /* Set the delta */
            setValue(SecurityAttribute.FOREIGNVALUEDELTA, myValue);
        }
    }

    /**
     * Analyse the bucket.
     * @param pRange the range of valuation
     */
    protected void analyseBucket(final TethysDateRange pRange) {
        /* Value the asset over the range */
        if (isForeignCurrency) {
            valueForeignAsset(pRange);
        } else {
            valueAsset(pRange);
        }

        /* Calculate the deltas */
        calculateDeltas();

        /* Calculate the profit */
        calculateProfit();

        /* Calculate the market movement */
        if (isForeignCurrency) {
            calculateForeignMarket();
        } else {
            calculateMarket();
        }
    }

    /**
     * Adjust to base.
     */
    private void adjustToBase() {
        /* Adjust to base values */
        theValues.adjustToBaseValues(theBaseValues);
        theBaseValues.resetBaseValues();
    }

    /**
     * Calculate market movement.
     */
    private void calculateMarket() {
        /* Obtain the delta value */
        TethysMoney myValue = theValues.getMoneyValue(SecurityAttribute.VALUEDELTA);
        myValue = new TethysMoney(myValue);

        /* Subtract the investment */
        myValue.subtractAmount(theValues.getMoneyValue(SecurityAttribute.INVESTED));

        /* Set the delta */
        setValue(SecurityAttribute.MARKETGROWTH, myValue);
    }

    /**
     * Calculate foreign market movement.
     */
    private void calculateForeignMarket() {
        /* Obtain the local market growth */
        TethysMoney myBaseValue = theValues.getMoneyValue(SecurityAttribute.VALUEDELTA);
        myBaseValue = new TethysMoney(myBaseValue);

        /* Subtract the investment */
        myBaseValue.subtractAmount(theValues.getMoneyValue(SecurityAttribute.INVESTED));

        /* Set the basic growth */
        setValue(SecurityAttribute.LOCALMARKETGROWTH, myBaseValue);

        /* Obtain the foreign growth */
        TethysMoney myValue = theValues.getMoneyValue(SecurityAttribute.FOREIGNVALUEDELTA);
        myValue = new TethysMoney(myValue);

        /* Subtract the investment */
        myValue.subtractAmount(theValues.getMoneyValue(SecurityAttribute.FOREIGNINVESTED));

        /* Set the foreign growth */
        setValue(SecurityAttribute.FOREIGNMARKETGROWTH, myValue);

        /* Calculate the local equivalent */
        Currency myCurrency = theAnalysis.getCurrency().getCurrency();
        TethysRatio myRate = theValues.getRatioValue(SecurityAttribute.EXCHANGERATE);
        myValue = myValue.convertCurrency(myCurrency, myRate.getInverseRatio());

        /* Set the market growth */
        setValue(SecurityAttribute.MARKETGROWTH, myValue);

        /* Calculate the fluctuation */
        TethysMoney myFluct = new TethysMoney(myBaseValue);
        myFluct.subtractAmount(myValue);
        setValue(SecurityAttribute.CURRENCYFLUCT, myFluct);
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return theValues.isActive();
    }

    /**
     * SecurityValues class.
     */
    public static class SecurityValues
            extends BucketValues<SecurityValues, SecurityAttribute> {
        /**
         * Constructor.
         * @param pCurrency the account currency
         */
        protected SecurityValues(final Currency pCurrency) {
            /* Initialise class */
            super(SecurityAttribute.class);

            /* Initialise units etc. to zero */
            setValue(SecurityAttribute.UNITS, new TethysUnits());
            setValue(SecurityAttribute.RESIDUALCOST, new TethysMoney(pCurrency));
            setValue(SecurityAttribute.INVESTED, new TethysMoney(pCurrency));
            setValue(SecurityAttribute.REALISEDGAINS, new TethysMoney(pCurrency));
            setValue(SecurityAttribute.GROWTHADJUST, new TethysMoney(pCurrency));
            setValue(SecurityAttribute.DIVIDEND, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pCurrency the account currency
         * @param pReportingCurrency the reporting currency
         */
        protected SecurityValues(final Currency pCurrency,
                                 final Currency pReportingCurrency) {
            /* Initialise class */
            this(pReportingCurrency);

            /* Initialise additional values to zero */
            setValue(SecurityAttribute.FOREIGNINVESTED, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pSource the source map.
         * @param pCountersOnly only copy counters
         */
        private SecurityValues(final SecurityValues pSource,
                               final boolean pCountersOnly) {
            /* Initialise class */
            super(pSource, pCountersOnly);
        }

        @Override
        protected SecurityValues getCounterSnapShot() {
            return new SecurityValues(this, true);
        }

        @Override
        protected SecurityValues getFullSnapShot() {
            return new SecurityValues(this, false);
        }

        /**
         * Is this a foreign security?
         * @return true/false
         */
        private boolean isForeignSecurity() {
            return getValue(SecurityAttribute.FOREIGNINVESTED) != null;
        }

        @Override
        protected void adjustToBaseValues(final SecurityValues pBase) {
            /* Adjust invested/gains values */
            adjustMoneyToBase(pBase, SecurityAttribute.INVESTED);
            adjustMoneyToBase(pBase, SecurityAttribute.REALISEDGAINS);
            adjustMoneyToBase(pBase, SecurityAttribute.GROWTHADJUST);
            adjustMoneyToBase(pBase, SecurityAttribute.DIVIDEND);

            /* If we are a foreign security */
            if (isForeignSecurity()) {
                adjustMoneyToBase(pBase, SecurityAttribute.FOREIGNINVESTED);
            }
        }

        @Override
        protected void resetBaseValues() {
            /* Create a zero value in the correct currency */
            TethysMoney myValue = getMoneyValue(SecurityAttribute.RESIDUALCOST);
            myValue = new TethysMoney(myValue);
            myValue.setZero();

            /* Reset Growth Adjust values */
            setValue(SecurityAttribute.GROWTHADJUST, myValue);
            setValue(SecurityAttribute.INVESTED, new TethysMoney(myValue));
            setValue(SecurityAttribute.REALISEDGAINS, new TethysMoney(myValue));
            setValue(SecurityAttribute.DIVIDEND, new TethysMoney(myValue));

            /* If we are a foreign security */
            if (isForeignSecurity()) {
                /* Create a zero value in the correct currency */
                myValue = getMoneyValue(SecurityAttribute.FOREIGNINVESTED);
                myValue = new TethysMoney(myValue);
                myValue.setZero();

                /* Reset Invested values */
                setValue(SecurityAttribute.FOREIGNINVESTED, myValue);
            }
        }

        /**
         * Are the values active?
         * @return true/false
         */
        public boolean isActive() {
            TethysUnits myUnits = getUnitsValue(SecurityAttribute.UNITS);
            return (myUnits != null) && (myUnits.isNonZero());
        }
    }

    /**
     * SecurityBucket list class.
     */
    public static class SecurityBucketList
            extends MetisOrderedIdList<Integer, SecurityBucket>
            implements MetisDataContents {

        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.SECURITY_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected SecurityBucketList(final Analysis pAnalysis) {
            super(SecurityBucket.class);
            theAnalysis = pAnalysis;
        }

        /**
         * Construct a view List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        protected SecurityBucketList(final Analysis pAnalysis,
                                     final SecurityBucketList pBase) {
            /* Initialise class */
            super(SecurityBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<SecurityBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                SecurityBucket myCurr = myIterator.next();

                /* Access the bucket */
                SecurityBucket myBucket = new SecurityBucket(pAnalysis, myCurr);

                /*
                 * Ignore idle securities. Note that we must include securities that have been
                 * closed in order to adjust Market Growth.
                 */
                if (!myBucket.isIdle()) {
                    /* Add to the list */
                    append(myBucket);
                }
            }
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected SecurityBucketList(final Analysis pAnalysis,
                                     final SecurityBucketList pBase,
                                     final TethysDate pDate) {
            /* Initialise class */
            super(SecurityBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<SecurityBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                SecurityBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                SecurityBucket myBucket = new SecurityBucket(pAnalysis, myCurr, pDate);

                /*
                 * Ignore idle securities. Note that we must include securities that have been
                 * closed in order to adjust Market Growth.
                 */
                if (!myBucket.isIdle()) {
                    /* Add to the list */
                    append(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        protected SecurityBucketList(final Analysis pAnalysis,
                                     final SecurityBucketList pBase,
                                     final TethysDateRange pRange) {
            /* Initialise class */
            super(SecurityBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<SecurityBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                SecurityBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                SecurityBucket myBucket = new SecurityBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Adjust to base and add to the list */
                    myBucket.adjustToBase();
                    append(myBucket);
                }
            }
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            return MetisFieldValue.UNKNOWN;
        }

        /**
         * Obtain the SecurityBucket for a given security holding.
         * @param pHolding the security holding
         * @return the bucket
         */
        public SecurityBucket getBucket(final SecurityHolding pHolding) {
            /* Locate the bucket in the list */
            Security mySecurity = pHolding.getSecurity();
            SecurityBucket myItem = findItemById(mySecurity.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new SecurityBucket(theAnalysis, pHolding);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain an orphan SecurityBucket for a given security holding.
         * @param pHolding the security holding
         * @return the bucket
         */
        public SecurityBucket getOrphanBucket(final SecurityHolding pHolding) {
            /* Allocate an orphan bucket */
            return new SecurityBucket(theAnalysis, pHolding);
        }

        /**
         * Mark active securities.
         * @return true/false are there active securities?
         * @throws OceanusException on error
         */
        protected boolean markActiveSecurities() throws OceanusException {
            /* Loop through the buckets */
            boolean areActive = false;
            Iterator<SecurityBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                SecurityBucket myCurr = myIterator.next();
                Security mySecurity = myCurr.getSecurity();

                /* If we are active */
                if (myCurr.isActive()) {
                    /* Set the security as relevant */
                    mySecurity.setRelevant();
                    areActive = true;
                }

                /* If we are closed */
                if (mySecurity.isClosed()) {
                    /* Ensure that we have correct closed dates */
                    mySecurity.adjustClosed();

                    /* If we are Relevant */
                    if (mySecurity.isRelevant() && theAnalysis.getData().checkClosedAccounts()) {
                        /* throw exception */
                        throw new MoneyWiseDataException(myCurr, "Illegally closed security");
                    }
                }
            }

            /* Return active indication */
            return areActive;
        }
    }
}
