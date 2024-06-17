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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisHistory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketPriced;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisCursor;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseSecurityType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * The Security Bucket class.
 */
public final class MoneyWiseXAnalysisSecurityBucket
        implements MetisFieldTableItem, MoneyWiseXAnalysisBucketPriced {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisSecurityBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisSecurityBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisSecurityBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.ASSETTYPE_SECURITYHOLDING, MoneyWiseXAnalysisSecurityBucket::getSecurityHolding);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.SECURITYTYPE, MoneyWiseXAnalysisSecurityBucket::getSecurityType);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.CURRENCY, MoneyWiseXAnalysisSecurityBucket::getCurrency);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_BASEVALUES, MoneyWiseXAnalysisSecurityBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_HISTORY, MoneyWiseXAnalysisSecurityBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseXAnalysisSecurityAttr.class, MoneyWiseXAnalysisSecurityBucket::getAttributeValue);
    }

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

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
    private final boolean isForeignCurrency;

    /**
     * Is this a stock option?
     */
    private final boolean isStockOption;

    /**
     * The security type.
     */
    private final MoneyWiseSecurityType theCategory;

    /**
     * Values.
     */
    private final MoneyWiseXAnalysisSecurityValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseXAnalysisSecurityValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisSecurityValues, MoneyWiseXAnalysisSecurityAttr> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pHolding the security holding
     */
    MoneyWiseXAnalysisSecurityBucket(final MoneyWiseXAnalysis pAnalysis,
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
        final Currency myCurrency = MoneyWiseXAnalysisAccountBucket.deriveCurrency(myHoldingCurrency);
        final Currency myRepCurrency = MoneyWiseXAnalysisAccountBucket.deriveCurrency(myReportingCurrency);

        /* Note stockOption */
        isStockOption = theSecurity.getUnderlyingStock() != null;

        /* Create the history map */
        final MoneyWiseXAnalysisSecurityValues myValues =  new MoneyWiseXAnalysisSecurityValues(myCurrency, myRepCurrency);
        theHistory = new MoneyWiseXAnalysisHistory<>(myValues);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();

        /* Register for price Updates */
        final MoneyWiseXAnalysisCursor myCursor = theAnalysis.getCursor();
        myCursor.registerForPriceUpdates(this);

        /* Store the security price */
        recordSecurityPrice();

        /* If this is a foreign currency account */
        if (isForeignCurrency) {
            /* Register for xchangeRate Updates */
            myCursor.registerForXchgRateUpdates(this);

            /* Record the exchangeRate and copy to base */
            recordExchangeRate();
            theBaseValues.setValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE, getValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE));

            /* Create a new reportedValuation */
            final TethysMoney myReported = new TethysMoney(theAnalysis.getCurrency().getCurrency());
            theValues.setValue(MoneyWiseXAnalysisSecurityAttr.VALUATION, myReported);
            theBaseValues.setValue(MoneyWiseXAnalysisSecurityAttr.VALUATION, myReported);
        }
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private MoneyWiseXAnalysisSecurityBucket(final MoneyWiseXAnalysis pAnalysis,
                                             final MoneyWiseXAnalysisSecurityBucket pBase,
                                             final TethysDate pDate) {
        /* Copy details from base */
        theHolding = pBase.getSecurityHolding();
        theCurrency = pBase.getCurrency();
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getSecurityType();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();
        isStockOption = pBase.isStockOption();

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pDate);

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
    private MoneyWiseXAnalysisSecurityBucket(final MoneyWiseXAnalysis pAnalysis,
                                             final MoneyWiseXAnalysisSecurityBucket pBase,
                                             final TethysDateRange pRange) {
        /* Copy details from base */
        theHolding = pBase.getSecurityHolding();
        theCurrency = pBase.getCurrency();
        theSecurity = pBase.getSecurity();
        thePortfolio = pBase.getPortfolio();
        theCategory = pBase.getSecurityType();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();
        isStockOption = pBase.isStockOption();

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisSecurityBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public Long getBucketId() {
        return theHolding.getExternalId();
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

    @Override
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

    @Override
    public MoneyWiseCurrency getCurrency() {
        return theCurrency;
    }

    /**
     * Is this a foreign currency?
     * @return true/false
     */
    public boolean isForeignCurrency() {
        return isForeignCurrency;
    }

    @Override
    public boolean isStockOption() {
        return isStockOption;
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
    public boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    MoneyWiseXAnalysis getAnalysis() {
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
    public MoneyWiseXAnalysisSecurityValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public MoneyWiseXAnalysisSecurityValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisSecurityValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        return theHistory.getValuesForEvent(pEvent);
    }

    /**
     * Obtain previous values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisSecurityValues getPreviousValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        return theHistory.getPreviousValuesForEvent(pEvent);
    }

    /**
     * Obtain delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
                                          final MoneyWiseXAnalysisSecurityAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pEvent, pAttr);
    }

    /**
     * Obtain money delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysMoney getMoneyDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
                                             final MoneyWiseXAnalysisSecurityAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaMoneyValue(pEvent, pAttr);
    }

    /**
     * Obtain units delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysUnits getUnitsDeltaForTransaction(final MoneyWiseXAnalysisEvent pEvent,
                                                   final MoneyWiseXAnalysisSecurityAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaUnitsValue(pEvent, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisSecurityValues, MoneyWiseXAnalysisSecurityAttr> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    public void setValue(final MoneyWiseXAnalysisSecurityAttr pAttr,
                         final Object pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseXAnalysisSecurityAttr pAttr) {
        /* Access value of object */
        final Object myValue = getValue(pAttr);

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
    private Object getValue(final MoneyWiseXAnalysisSecurityAttr pAttr) {
        /* Obtain the value */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust Units.
     * @param pDelta the delta
     */
    public void adjustUnits(final TethysUnits pDelta) {
        TethysUnits myValue = theValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        myValue = new TethysUnits(myValue);
        myValue.addUnits(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.UNITS, myValue);
    }

    /**
     * Adjust Invested.
     * @param pDelta the delta
     */
    public void adjustInvested(final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.INVESTED);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.INVESTED, myValue);
    }

    /**
     * Adjust ResidualCost.
     * @param pDelta the delta
     */
    public void adjustResidualCost(final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myValue);
    }

    /**
     * Adjust RealisedGains.
     * @param pDelta the delta
     */
    public void adjustRealisedGains(final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, myValue);
    }

    /**
     * Adjust Dividends.
     * @param pDelta the delta
     */
    public void adjustDividend(final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.DIVIDEND);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.DIVIDEND, myValue);
    }

    /**
     * Adjust Funded.
     * @param pDelta the delta
     */
    public void adjustFunded(final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.FUNDED);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.FUNDED, myValue);
    }

    /**
     * Set startDate.
     * @param pDate the startDate
     */
    public void setStartDate(final TethysDate pDate) {
        setValue(MoneyWiseXAnalysisSecurityAttr.STARTDATE, pDate);
    }

    /**
     * Calculate unrealisedGains.
     */
    public void calculateUnrealisedGains() {
        /* Unrealised gains is VALUATION - RESIDUALCOST */
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myValue = new TethysMoney(myValue);
        myValue.subtractAmount(theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST));
        setValue(MoneyWiseXAnalysisSecurityAttr.UNREALISEDGAINS, myValue);
    }

    @Override
    public void recordSecurityPrice() {
        final MoneyWiseXAnalysisCursor myCursor = theAnalysis.getCursor();
        TethysPrice myPrice = myCursor.getCurrentPrice(getSecurity());
        if (isStockOption) {
            myPrice = new TethysPrice(myPrice);
            myPrice.subtractPrice(getSecurity().getOptionPrice());
            if (!myPrice.isPositive()) {
                myPrice.setZero();
            }
        }
        theValues.setValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE, myPrice);
    }

    @Override
    public void recordExchangeRate() {
        final MoneyWiseXAnalysisCursor myCursor = theAnalysis.getCursor();
        final TethysRatio myRate = myCursor.getCurrentXchgRate(getSecurity().getAssetCurrency());
        theValues.setValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE, myRate);
    }

    @Override
    public void registerEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * value the asset.
     */
    public void valueAsset() {
        /* Access units and price */
        final TethysUnits myUnits = theValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        final TethysPrice myPrice = theValues.getPriceValue(MoneyWiseXAnalysisSecurityAttr.PRICE);

        /* Calculate the value */
        final TethysMoney myLocalValue = myUnits.valueAtPrice(myPrice);
        setValue(MoneyWiseXAnalysisSecurityAttr.VALUE, myLocalValue);

        /* Adjust the valuation */
        adjustValuation();
    }

    @Override
    public void adjustValuation() {
        /* Determine reported balance */
        TethysMoney myBalance = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUE);
        if (isForeignCurrency) {
            final TethysRatio myRate = theValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE);
            myBalance = myBalance.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate);
        }
        theValues.setValue(MoneyWiseXAnalysisSecurityAttr.VALUATION, myBalance);
    }

    @Override
    public TethysMoney getDeltaValuation() {
        /* Determine the delta */
        final TethysMoney myDelta = new TethysMoney(theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
        myDelta.subtractAmount(theHistory.getLastValues().getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
        return myDelta;
    }

    /**
     * calculate the profit for a priced asset.
     */
    private void calculateProfit() {
        /* Calculate the profit */
        final TethysMoney myValuation = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUEDELTA);
        final TethysMoney myProfit = new TethysMoney(myValuation);
        myProfit.subtractAmount(theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.INVESTED));
        myProfit.addAmount(theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.DIVIDEND));

        /* Set the attribute */
        setValue(MoneyWiseXAnalysisSecurityAttr.PROFIT, myProfit);

        /* Calculate the profit minus the dividend */
        final TethysMoney myMarketProfit = new TethysMoney(myProfit);
        myMarketProfit.subtractAmount(theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.DIVIDEND));
        setValue(MoneyWiseXAnalysisSecurityAttr.MARKETPROFIT, myMarketProfit);
    }

    /**
     * calculate the deltas for a priced asset.
     */
    private void calculateDeltas() {
        /* Obtain a copy of the value */
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myValue = new TethysMoney(myValue);

        /* Subtract any base value */
        myValue.subtractAmount(theBaseValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));

        /* Set the delta */
        setValue(MoneyWiseXAnalysisSecurityAttr.VALUEDELTA, myValue);
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
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return theValues.isActive();
    }

    /**
     * SecurityBucket list class.
     */
    public static final class MoneyWiseXAnalysisSecurityBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseXAnalysisSecurityBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisSecurityBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisSecurityBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisSecurityBucketList::getAnalysis);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseXAnalysisSecurityBucket> theList;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisSecurityBucketList(final MoneyWiseXAnalysis pAnalysis) {
            theAnalysis = pAnalysis;
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getSecurity().compareTo(r.getSecurity()));
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        MoneyWiseXAnalysisSecurityBucketList(final MoneyWiseXAnalysis pAnalysis,
                                             final MoneyWiseXAnalysisSecurityBucketList pBase,
                                             final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisSecurityBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseXAnalysisSecurityBucket myBucket = new MoneyWiseXAnalysisSecurityBucket(pAnalysis, myCurr, pDate);

                /*
                 * Ignore idle securities. Note that we must include securities that have been
                 * closed in order to adjust Market Growth.
                 */
                if (!myBucket.isIdle()) {
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
        MoneyWiseXAnalysisSecurityBucketList(final MoneyWiseXAnalysis pAnalysis,
                                             final MoneyWiseXAnalysisSecurityBucketList pBase,
                                             final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisSecurityBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseXAnalysisSecurityBucket myBucket = new MoneyWiseXAnalysisSecurityBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Adjust to base and add to the list */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisSecurityBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseXAnalysisSecurityBucket> getUnderlyingList() {
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
        MoneyWiseXAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseXAnalysisSecurityBucket findItemById(final Integer pId) {
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
        public MoneyWiseXAnalysisSecurityBucket getBucket(final MoneyWiseSecurityHolding pHolding) {
            /* Locate the bucket in the list */
            final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
            MoneyWiseXAnalysisSecurityBucket myItem = findItemById(mySecurity.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseXAnalysisSecurityBucket(theAnalysis, pHolding);

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
            final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisSecurityBucket myCurr = myIterator.next();
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
