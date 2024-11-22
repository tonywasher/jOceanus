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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisHistory;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketPriced;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisCursor;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
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

import java.util.Currency;
import java.util.Iterator;
import java.util.List;

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
            final OceanusMoney myReported = new OceanusMoney(theAnalysis.getCurrency().getCurrency());
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
                                             final OceanusDate pDate) {
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
                                             final OceanusDateRange pRange) {
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
    public OceanusDateRange getDateRange() {
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
    public OceanusDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
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
    public OceanusMoney getMoneyDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
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
    public OceanusUnits getUnitsDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
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
    public void adjustUnits(final OceanusUnits pDelta) {
        OceanusUnits myValue = theValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        myValue = new OceanusUnits(myValue);
        myValue.addUnits(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.UNITS, myValue);
    }

    /**
     * Adjust ResidualCost.
     * @param pDelta the delta
     */
    public void adjustResidualCost(final OceanusMoney pDelta) {
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        myValue = new OceanusMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, myValue);
    }

    /**
     * Adjust RealisedGains.
     * @param pDelta the delta
     */
    public void adjustRealisedGains(final OceanusMoney pDelta) {
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS);
        myValue = new OceanusMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, myValue);
    }

    /**
     * Adjust Dividends.
     * @param pDelta the delta
     */
    public void adjustDividend(final OceanusMoney pDelta) {
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.DIVIDEND);
        myValue = new OceanusMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.DIVIDEND, myValue);
    }

    /**
     * Adjust Funded.
     * @param pDelta the delta
     */
    public void adjustFunded(final OceanusMoney pDelta) {
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.FUNDED);
        myValue = new OceanusMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(MoneyWiseXAnalysisSecurityAttr.FUNDED, myValue);
    }

    /**
     * Ensure that start date is set.
     * @param pDate the startDate
     */
    public void ensureStartDate(final OceanusDate pDate) {
        if (theValues.getValue(MoneyWiseXAnalysisSecurityAttr.STARTDATE) == null) {
            setValue(MoneyWiseXAnalysisSecurityAttr.STARTDATE, pDate);
        }
    }

    /**
     * Calculate unrealisedGains.
     */
    public void calculateUnrealisedGains() {
        /* Unrealised gains is VALUATION - RESIDUALCOST */
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myValue = new OceanusMoney(myValue);
        myValue.subtractAmount(theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST));
        setValue(MoneyWiseXAnalysisSecurityAttr.UNREALISEDGAINS, myValue);
    }

    @Override
    public void recordSecurityPrice() {
        /* Access the current price */
        final MoneyWiseXAnalysisCursor myCursor = theAnalysis.getCursor();
        OceanusPrice myPrice = myCursor.getCurrentPrice(getSecurity());

        /* If this is a stockOption */
        if (isStockOption) {
            /* Price is any positive difference between stockPrice and optioonPrice */
            myPrice = new OceanusPrice(myPrice);
            myPrice.subtractPrice(getSecurity().getOptionPrice());
            if (!myPrice.isPositive()) {
                myPrice.setZero();
            }
        }

        /* If we are funded */
        final OceanusMoney myFunded = new OceanusMoney(theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.FUNDED));
        if (myFunded.isNonZero()) {
            /* Set funded to zero */
            theValues.setZeroMoney(MoneyWiseXAnalysisSecurityAttr.FUNDED);

            /* If we have zero units, honour autoUnits */
            final MoneyWiseSecurityClass mySecClass = getSecurity().getCategoryClass();
            final OceanusUnits myUnits = theValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
            if (myUnits.isZero() && mySecClass.isAutoUnits()) {
                final OceanusUnits myAutoUnits = OceanusUnits.getWholeUnits(mySecClass.getAutoUnits());
                theValues.setValue(MoneyWiseXAnalysisSecurityAttr.UNITS, myAutoUnits);
            }
        }

        /* Store the price */
        theValues.setValue(MoneyWiseXAnalysisSecurityAttr.PRICE, myPrice);
    }

    @Override
    public void recordExchangeRate() {
        final MoneyWiseXAnalysisCursor myCursor = theAnalysis.getCursor();
        final OceanusRatio myRate = myCursor.getCurrentXchgRate(getSecurity().getAssetCurrency());
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
        final OceanusUnits myUnits = theValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        final OceanusPrice myPrice = theValues.getPriceValue(MoneyWiseXAnalysisSecurityAttr.PRICE);

        /* Calculate the value */
        final OceanusMoney myLocalValue = myUnits.valueAtPrice(myPrice);
        setValue(MoneyWiseXAnalysisSecurityAttr.VALUE, myLocalValue);

        /* Adjust the valuation */
        adjustValuation();
    }

    @Override
    public void adjustValuation() {
        /* Calculate the value of the asset */
        OceanusMoney myBalance = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUE);

        /* If this is a foreign asset */
        if (isForeignCurrency) {
            /* Calculate the value in the local currency */
            final OceanusRatio myRate = theValues.getRatioValue(MoneyWiseXAnalysisSecurityAttr.EXCHANGERATE);
            myBalance = myBalance.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate);
        }

        /* If we have a funded value */
        final OceanusMoney myFunded = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.FUNDED);
        if (myFunded.isNonZero()) {
            /* Add to valuation */
            myBalance.addAmount(myFunded);
        }

        /* Record the valuation */
        theValues.setValue(MoneyWiseXAnalysisSecurityAttr.VALUATION, myBalance);
    }

    @Override
    public OceanusMoney getDeltaValuation() {
        /* Determine the delta */
        final OceanusMoney myDelta = new OceanusMoney(theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
        myDelta.subtractAmount(theHistory.getLastValues().getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
        return myDelta;
    }

    /**
     * Obtain the delta of unrealisedGains.
     * @return the delta
     */
    public OceanusMoney getDeltaUnrealisedGains() {
        /* Determine the delta */
        final OceanusMoney myDelta = new OceanusMoney(theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.UNREALISEDGAINS));
        myDelta.subtractAmount(theHistory.getLastValues().getMoneyValue(MoneyWiseXAnalysisSecurityAttr.UNREALISEDGAINS));
        return myDelta;
    }

    /**
     * calculate the deltas for a priced asset.
     */
    private void calculateDeltas() {
        /* Obtain a copy of the value */
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myValue = new OceanusMoney(myValue);

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
                                             final OceanusDate pDate) {
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
                                             final OceanusDateRange pRange) {
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
