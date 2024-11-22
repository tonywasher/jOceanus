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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisHistory;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * The Security Bucket class.
 */
public final class MoneyWiseAnalysisSecurityBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisSecurityBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisSecurityBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisSecurityBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.ASSETTYPE_SECURITYHOLDING, MoneyWiseAnalysisSecurityBucket::getSecurityHolding);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.SECURITYTYPE, MoneyWiseAnalysisSecurityBucket::getSecurityType);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.CURRENCY, MoneyWiseAnalysisSecurityBucket::getCurrency);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_BASEVALUES, MoneyWiseAnalysisSecurityBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_HISTORY, MoneyWiseAnalysisSecurityBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseAnalysisSecurityAttr.class, MoneyWiseAnalysisSecurityBucket::getAttributeValue);
    }

    /**
     * The analysis.
     */
    private final MoneyWiseAnalysis theAnalysis;

    /**
     * The security holding.
     */
    private final MoneyWiseSecurityHolding theHolding;

    /**
     * The security.
     */
    private final MoneyWiseSecurity theSecurity;

    /**
     * The portfolio.
     */
    private final MoneyWisePortfolio thePortfolio;

    /**
     * The currency.
     */
    private final MoneyWiseCurrency theCurrency;

    /**
     * Is this a foreign currency?
     */
    private final Boolean isForeignCurrency;

    /**
     * The security type.
     */
    private final MoneyWiseSecurityType theCategory;

    /**
     * Values.
     */
    private final MoneyWiseAnalysisSecurityValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseAnalysisSecurityValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseAnalysisHistory<MoneyWiseAnalysisSecurityValues, MoneyWiseAnalysisSecurityAttr> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pHolding the security holding
     */
    MoneyWiseAnalysisSecurityBucket(final MoneyWiseAnalysis pAnalysis,
                                    final MoneyWiseSecurityHolding pHolding) {
        /* Store the details */
        theHolding = pHolding;
        theCurrency = pHolding.getAssetCurrency();
        theSecurity = pHolding.getSecurity();
        thePortfolio = pHolding.getPortfolio();
        theAnalysis = pAnalysis;

        /* Obtain category */
        theCategory = theSecurity.getCategory();

        /* Determine currency */
        final MoneyWiseCurrency myReportingCurrency = pAnalysis.getCurrency();
        final MoneyWiseCurrency myHoldingCurrency = pHolding.getAssetCurrency();

        /* Determine whether we are a foreign currency */
        isForeignCurrency = !MetisDataDifference.isEqual(myReportingCurrency, myHoldingCurrency);
        final Currency myCurrency = MoneyWiseAnalysisAccountBucket.deriveCurrency(myHoldingCurrency);
        final Currency myRepCurrency = MoneyWiseAnalysisAccountBucket.deriveCurrency(myReportingCurrency);

        /* Create the history map */
        final MoneyWiseAnalysisSecurityValues myValues = Boolean.TRUE.equals(isForeignCurrency)
                ? new MoneyWiseAnalysisSecurityValues(myCurrency, myRepCurrency)
                : new MoneyWiseAnalysisSecurityValues(myCurrency);
        theHistory = new MoneyWiseAnalysisHistory<>(myValues);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    private MoneyWiseAnalysisSecurityBucket(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseAnalysisSecurityBucket pBase) {
        /* Copy details from base */
        theHolding = pBase.getSecurityHolding();
        theCurrency = pBase.getCurrency();
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getSecurityType();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap());

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
    private MoneyWiseAnalysisSecurityBucket(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseAnalysisSecurityBucket pBase,
                                            final OceanusDate pDate) {
        /* Copy details from base */
        theHolding = pBase.getSecurityHolding();
        theCurrency = pBase.getCurrency();
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getSecurityType();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pDate);

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
    private MoneyWiseAnalysisSecurityBucket(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseAnalysisSecurityBucket pBase,
                                            final OceanusDateRange pRange) {
        /* Copy details from base */
        theHolding = pBase.getSecurityHolding();
        theCurrency = pBase.getCurrency();
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getSecurityType();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisSecurityBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getDecoratedName();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getSecurityName() {
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
    public MoneyWiseSecurityHolding getSecurityHolding() {
        return theHolding;
    }

    /**
     * Obtain the security.
     * @return the security
     */
    public MoneyWiseSecurity getSecurity() {
        return theSecurity;
    }

    /**
     * Obtain the portfolio.
     * @return the portfolio
     */
    public MoneyWisePortfolio getPortfolio() {
        return thePortfolio;
    }

    /**
     * Obtain the currency.
     * @return the currency
     */
    public MoneyWiseCurrency getCurrency() {
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
    public Integer getIndexedId() {
        return theSecurity.getIndexedId();
    }

    /**
     * Obtain the security type.
     * @return the security type
     */
    public MoneyWiseSecurityType getSecurityType() {
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
     * Obtain the analysis.
     * @return the analysis
     */
    MoneyWiseAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain date range.
     * @return the range
     */
    public OceanusDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public MoneyWiseAnalysisSecurityValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public MoneyWiseAnalysisSecurityValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public MoneyWiseAnalysisSecurityValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain previous values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public MoneyWiseAnalysisSecurityValues getPreviousValuesForTransaction(final MoneyWiseTransaction pTrans) {
        return theHistory.getPreviousValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public OceanusDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans,
                                                 final MoneyWiseAnalysisSecurityAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain money delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public OceanusMoney getMoneyDeltaForTransaction(final MoneyWiseTransaction pTrans,
                                                    final MoneyWiseAnalysisSecurityAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaMoneyValue(pTrans, pAttr);
    }

    /**
     * Obtain units delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public OceanusUnits getUnitsDeltaForTransaction(final MoneyWiseTransaction pTrans,
                                                    final MoneyWiseAnalysisSecurityAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaUnitsValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private MoneyWiseAnalysisHistory<MoneyWiseAnalysisSecurityValues, MoneyWiseAnalysisSecurityAttr> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    public void setValue(final MoneyWiseAnalysisSecurityAttr pAttr,
                         final Object pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseAnalysisSecurityAttr pAttr) {
        /* Access value of object */
        final Object myValue = getAttribute(pAttr);

        /* Return the value */
        return myValue != null
                ? myValue
                : MetisDataFieldValue.SKIP;
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getAttribute(final MoneyWiseAnalysisSecurityAttr pAttr) {
        /* Obtain the value */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    public void adjustCounter(final MoneyWiseAnalysisSecurityAttr pAttr,
                              final OceanusMoney pDelta) {
        OceanusMoney myValue = theValues.getMoneyValue(pAttr);
        myValue = new OceanusMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    public void adjustCounter(final MoneyWiseAnalysisSecurityAttr pAttr,
                              final OceanusUnits pDelta) {
        OceanusUnits myValue = theValues.getUnitsValue(pAttr);
        myValue = new OceanusUnits(myValue);
        myValue.addUnits(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Register the transaction.
     * @param pHelper the helper
     * @return the registered values
     */
    public MoneyWiseAnalysisSecurityValues registerTransaction(final MoneyWiseAnalysisTransactionHelper pHelper) {
        /* Register the event in the history */
        return theHistory.registerTransaction(pHelper.getTransaction(), theValues);
    }

    /**
     * value the asset for a particular range.
     * @param pRange the range of valuation
     */
    private void valueAsset(final OceanusDateRange pRange) {
        /* Obtain the appropriate price */
        final MoneyWiseDataSet myData = theAnalysis.getData();
        final MoneyWiseSecurityPriceDataMap myPriceMap = myData.getSecurityPriceDataMap();
        final OceanusPrice[] myPrices = myPriceMap.getPricesForRange(theSecurity, pRange);

        /* Access base units */
        OceanusUnits myUnits = theBaseValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        OceanusPrice myPrice = myPrices[0];

        /* Calculate the value */
        theBaseValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
        theBaseValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myUnits.valueAtPrice(myPrice));

        /* Access units */
        myUnits = theValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        myPrice = myPrices[1];

        /* Calculate the value */
        setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
        setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myUnits.valueAtPrice(myPrice));
    }

    /**
     * value the foreign asset for a particular range.
     * @param pRange the range of valuation
     */
    private void valueForeignAsset(final OceanusDateRange pRange) {
        /* Obtain the appropriate price */
        final MoneyWiseDataSet myData = theAnalysis.getData();
        final MoneyWiseSecurityPriceDataMap myPriceMap = myData.getSecurityPriceDataMap();
        final OceanusPrice[] myPrices = myPriceMap.getPricesForRange(theSecurity, pRange);
        final MoneyWiseExchangeRateDataMap myRateMap = myData.getExchangeRateDataMap();
        final OceanusRatio[] myRates = myRateMap.getRatesForRange(theSecurity.getAssetCurrency(), pRange);
        final Currency myCurrency = theAnalysis.getCurrency().getCurrency();

        /* Access base units */
        OceanusUnits myUnits = theBaseValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        OceanusPrice myPrice = myPrices[0];
        OceanusRatio myRate = myRates[0];

        /* Calculate the value */
        OceanusMoney myValue = myUnits.valueAtPrice(myPrice);
        OceanusMoney myLocalValue = myValue.convertCurrency(myCurrency, myRate);

        /* Record it */
        theBaseValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
        theBaseValues.setValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUE, myValue);
        theBaseValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myRate);
        theBaseValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myLocalValue);

        /* Access units */
        myUnits = theValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        myPrice = myPrices[1];
        myRate = myRates[1];

        /* Calculate the value */
        myValue = myUnits.valueAtPrice(myPrice);
        myLocalValue = myValue.convertCurrency(myCurrency, myRate);

        /* Record it */
        setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
        setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myRate);
        setValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUE, myValue);
        setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myLocalValue);
    }

    /**
     * calculate the profit for a priced asset.
     */
    private void calculateProfit() {
        /* Calculate the profit */
        final OceanusMoney myValuation = theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA);
        final OceanusMoney myProfit = new OceanusMoney(myValuation);
        myProfit.subtractAmount(theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED));
        myProfit.addAmount(theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.DIVIDEND));
        myProfit.addAmount(theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST));

        /* Set the attribute */
        setValue(MoneyWiseAnalysisSecurityAttr.PROFIT, myProfit);

        /* Calculate the profit minus the dividend */
        final OceanusMoney myMarketProfit = new OceanusMoney(myProfit);
        myMarketProfit.subtractAmount(theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.DIVIDEND));
        setValue(MoneyWiseAnalysisSecurityAttr.MARKETPROFIT, myMarketProfit);
    }

    /**
     * calculate the deltas for a priced asset.
     */
    private void calculateDeltas() {
        /* Obtain a copy of the value */
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);
        myValue = new OceanusMoney(myValue);

        /* Subtract any base value */
        myValue.subtractAmount(theBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));

        /* Set the delta */
        setValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA, myValue);

        if (Boolean.TRUE.equals(isForeignCurrency)) {
            /* Obtain a copy of the value */
            myValue = theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUE);
            myValue = new OceanusMoney(myValue);

            /* Subtract any base value */
            myValue.subtractAmount(theBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUE));

            /* Set the delta */
            setValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUEDELTA, myValue);
        }
    }

    /**
     * Analyse the bucket.
     * @param pRange the range of valuation
     */
    void analyseBucket(final OceanusDateRange pRange) {
        /* Value the asset over the range */
        if (Boolean.TRUE.equals(isForeignCurrency)) {
            valueForeignAsset(pRange);
        } else {
            valueAsset(pRange);
        }

        /* Calculate the deltas */
        calculateDeltas();

        /* Calculate the profit */
        calculateProfit();

        /* Calculate the market movement */
        if (Boolean.TRUE.equals(isForeignCurrency)) {
            calculateForeignMarket();
        } else {
            calculateMarket();
        }
    }

    /**
     * Adjust to base.
     */
    void adjustToBase() {
        /* Adjust to base values */
        theValues.adjustToBaseValues(theBaseValues);
        theBaseValues.resetBaseValues();
    }

    /**
     * Calculate market movement.
     */
    private void calculateMarket() {
        /* Obtain the delta value */
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA);
        myValue = new OceanusMoney(myValue);

        /* Subtract the investment */
        myValue.subtractAmount(theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED));

        /* Set the delta */
        setValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH, myValue);
    }

    /**
     * Calculate foreign market movement.
     */
    private void calculateForeignMarket() {
        /* Obtain the local market growth */
        OceanusMoney myBaseValue = theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA);
        myBaseValue = new OceanusMoney(myBaseValue);

        /* Subtract the investment */
        myBaseValue.subtractAmount(theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED));

        /* Set the basic growth */
        setValue(MoneyWiseAnalysisSecurityAttr.LOCALMARKETGROWTH, myBaseValue);

        /* Obtain the foreign growth */
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUEDELTA);
        myValue = new OceanusMoney(myValue);

        /* Subtract the investment */
        myValue.subtractAmount(theValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED));

        /* Set the foreign growth */
        setValue(MoneyWiseAnalysisSecurityAttr.FOREIGNMARKETGROWTH, myValue);

        /* Calculate the local equivalent */
        final Currency myCurrency = theAnalysis.getCurrency().getCurrency();
        final OceanusRatio myRate = theValues.getRatioValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE);
        myValue = myValue.convertCurrency(myCurrency, myRate);

        /* Set the market growth */
        setValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH, myValue);

        /* Calculate the fluctuation */
        final OceanusMoney myFluct = new OceanusMoney(myBaseValue);
        myFluct.subtractAmount(myValue);
        setValue(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT, myFluct);
    }

    /**
     * Adjust security for natInsurance payments.
     * @param pTrans the transaction causing the payments
     */
    public void adjustForNIPayments(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Assume no NatInsurance */
        OceanusMoney myAmount = null;

        /* Access Employer NatInsurance */
        OceanusMoney myNatIns = pTrans.getEmployerNatIns();
        if (myNatIns != null
                && myNatIns.isNonZero()) {
            myAmount = new OceanusMoney(myNatIns);
        }

        /* Access Employee natInsurance */
        myNatIns = pTrans.getEmployeeNatIns();
        if (myNatIns != null
                && myNatIns.isNonZero()) {
            if (myAmount == null) {
                myAmount = new OceanusMoney(myNatIns);
            } else {
                myAmount.addAmount(myNatIns);
            }
        }

        /* If we have natInsurance */
        if (myAmount != null) {
            /* Handle autoUnits */
            OceanusUnits myUnits = getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
            if (myUnits.isZero()) {
                myUnits = OceanusUnits.getWholeUnits(theSecurity.getCategoryClass().getAutoUnits());
                setValue(MoneyWiseAnalysisSecurityAttr.UNITS, myUnits);
            }

            /* Adjust invested */
            adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myAmount);
            adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myAmount);

            /* Register the transaction in the history */
            registerTransaction(pTrans);
        }
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return theValues.isActive();
    }

    /**
     * SecurityBucket list class.
     */
    public static final class MoneyWiseAnalysisSecurityBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseAnalysisSecurityBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisSecurityBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisSecurityBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisSecurityBucketList::getAnalysis);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseAnalysisSecurityBucket> theList;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseAnalysisSecurityBucketList(final MoneyWiseAnalysis pAnalysis) {
            theAnalysis = pAnalysis;
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getSecurity().compareTo(r.getSecurity()));
        }

        /**
         * Construct a view List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        MoneyWiseAnalysisSecurityBucketList(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseAnalysisSecurityBucketList pBase) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisSecurityBucket myCurr = myIterator.next();

                /* Access the bucket */
                final MoneyWiseAnalysisSecurityBucket myBucket = new MoneyWiseAnalysisSecurityBucket(pAnalysis, myCurr);

                /*
                 * Ignore idle securities. Note that we must include securities that have been
                 * closed in order to adjust Market Growth.
                 */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
                    /* Add to the list */
                    theList.add(myBucket);
                }
            }
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        MoneyWiseAnalysisSecurityBucketList(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseAnalysisSecurityBucketList pBase,
                                            final OceanusDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisSecurityBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseAnalysisSecurityBucket myBucket = new MoneyWiseAnalysisSecurityBucket(pAnalysis, myCurr, pDate);

                /*
                 * Ignore idle securities. Note that we must include securities that have been
                 * closed in order to adjust Market Growth.
                 */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
                    /* Add to the list */
                    theList.add(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        MoneyWiseAnalysisSecurityBucketList(final MoneyWiseAnalysis pAnalysis,
                                            final MoneyWiseAnalysisSecurityBucketList pBase,
                                            final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisSecurityBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseAnalysisSecurityBucket myBucket = new MoneyWiseAnalysisSecurityBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || Boolean.TRUE.equals(!myBucket.isIdle())) {
                    /* Adjust to base and add to the list */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisSecurityBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisSecurityBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        MoneyWiseAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseAnalysisSecurityBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * SortBuckets.
         */
        void sortBuckets() {
            theList.sortList();
        }

        /**
         * Obtain the SecurityBucket for a given security holding.
         * @param pHolding the security holding
         * @return the bucket
         */
        public MoneyWiseAnalysisSecurityBucket getBucket(final MoneyWiseSecurityHolding pHolding) {
            /* Locate the bucket in the list */
            final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
            MoneyWiseAnalysisSecurityBucket myItem = findItemById(mySecurity.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisSecurityBucket(theAnalysis, pHolding);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Mark active securities.
         * @return true/false are there active securities?
         * @throws OceanusException on error
         */
        boolean markActiveSecurities() throws OceanusException {
            /* Loop through the buckets */
            boolean areActive = false;
            final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisSecurityBucket myCurr = myIterator.next();
                final MoneyWiseSecurity mySecurity = myCurr.getSecurity();

                /* If we are active */
                if (myCurr.isActive()) {
                    /* Set the security as relevant */
                    mySecurity.setRelevant();
                    areActive = true;
                }

                /* If we are closed */
                if (Boolean.TRUE.equals(mySecurity.isClosed())) {
                    /* Ensure that we have correct closed dates */
                    mySecurity.adjustClosed();

                    /* If we are Relevant */
                    if (mySecurity.isRelevant()
                            && theAnalysis.getData().checkClosedAccounts()) {
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
