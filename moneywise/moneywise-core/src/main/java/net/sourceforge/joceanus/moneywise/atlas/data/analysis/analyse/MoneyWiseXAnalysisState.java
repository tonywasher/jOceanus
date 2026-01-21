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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseNewDepositRate;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseNewDepositRate.MoneyWiseNewDepositRateList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventType;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketForeign;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketPriced;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketRegister;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisCursor;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase.MoneyWiseAssetBaseList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Analysis Cursor.
 */
public class MoneyWiseXAnalysisState
        implements MoneyWiseXAnalysisCursor {
    /**
     * The securityPrice iterator.
     */
    private final Iterator<MoneyWiseSecurityPrice> thePriceIterator;

    /**
     * The exchangeRate iterator.
     */
    private final Iterator<MoneyWiseExchangeRate> theXchgRateIterator;

    /**
     * The depositRate iterator.
     */
    private final Iterator<MoneyWiseNewDepositRate> theDepRateIterator;

    /**
     * The transaction iterator.
     */
    private final Iterator<MoneyWiseTransaction> theTransIterator;

    /**
     * The price map.
     */
    private final Map<MoneyWiseSecurity, OceanusPrice> thePriceMap;

    /**
     * The xchgRate map.
     */
    private final Map<MoneyWiseCurrency, OceanusRatio> theXchgRateMap;

    /**
     * The depositRate map.
     */
    private final Map<MoneyWiseDeposit, OceanusRate> theDepRateMap;

    /**
     * The pricedBucketMap.
     */
    private final Map<MoneyWiseSecurity, List<MoneyWiseXAnalysisBucketPriced>> thePricedBucketsMap;

    /**
     * The foreignBucketMap.
     */
    private final Map<MoneyWiseCurrency, List<MoneyWiseXAnalysisBucketForeign>> theForeignBucketsMap;

    /**
     * The eventBuckets that are to be registered to the event.
     */
    private final Map<Long, MoneyWiseXAnalysisBucketRegister> theEventBuckets;

    /**
     * The editSet.
     */
    private final PrometheusEditSet theEditSet;

    /**
     * The startDate.
     */
    private final OceanusDate theStartDate;

    /**
     * The editSet.
     */
    private final MoneyWiseXAnalysisEventList theEvents;

    /**
     * Have balances been built?.
     */
    private boolean balancesBuilt;

    /**
     * The next Price.
     */
    private MoneyWiseSecurityPrice theNextPrice;

    /**
     * The next xchgRate.
     */
    private MoneyWiseExchangeRate theNextXchgRate;

    /**
     * The next depRate.
     */
    private MoneyWiseNewDepositRate theNextDepRate;

    /**
     * The next Transaction.
     */
    private MoneyWiseTransaction theNextTrans;

    /**
     * The next Event.
     */
    private MoneyWiseXAnalysisEvent theNextEvent;

    /**
     * Constructor.
     *
     * @param pEditSet the editSet
     */
    public MoneyWiseXAnalysisState(final PrometheusEditSet pEditSet) {
        /* Store parameters */
        theEditSet = pEditSet;
        theEvents = new MoneyWiseXAnalysisEventList(theEditSet);

        final MoneyWiseSecurityPriceList myPrices = theEditSet.getDataList(MoneyWiseBasicDataType.SECURITYPRICE, MoneyWiseSecurityPriceList.class);
        thePriceIterator = myPrices.iterator();
        final MoneyWiseExchangeRateList myXchgRates = theEditSet.getDataList(MoneyWiseBasicDataType.EXCHANGERATE, MoneyWiseExchangeRateList.class);
        theXchgRateIterator = myXchgRates.iterator();
        theDepRateIterator = new MoneyWiseNewDepositRateList(theEditSet).iterator();
        theTransIterator = theEditSet.getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class).iterator();
        theStartDate = ((MoneyWiseDataSet) theEditSet.getDataSet()).getDateRange().getStart();
        balancesBuilt = false;

        /* Create the maps */
        thePriceMap = new HashMap<>();
        theXchgRateMap = new HashMap<>();
        theDepRateMap = new HashMap<>();
        thePricedBucketsMap = new HashMap<>();
        theForeignBucketsMap = new HashMap<>();
        theEventBuckets = new HashMap<>();

        /* Set up first elements */
        iteratePrice();
        iterateXchgRate();
        iterateDepRate();
        iterateTrans();
    }

    /**
     * Obtain the EventList.
     *
     * @return the list
     */
    public MoneyWiseXAnalysisEventList getEventList() {
        return theEvents;
    }

    /**
     * Build next event.
     *
     * @return the next event (or null)
     */
    public MoneyWiseXAnalysisEvent nextEvent() {
        /* Determine next eventType */
        final MoneyWiseXAnalysisEventType myType = nextEventType();

        /* Switch on the event Type */
        if (myType == null) {
            theNextEvent = null;
        } else {
            switch (myType) {
                case SECURITYPRICE:
                    nextSecurityPrice();
                    break;
                case XCHANGERATE:
                    nextXchgRate();
                    break;
                case DEPOSITRATE:
                    nextDepRate();
                    break;
                case OPENINGBALANCE:
                    buildBalances();
                    break;
                case TRANSACTION:
                default:
                    nextTransaction();
                    break;
            }
        }

        /* Return the new event */
        return theNextEvent;
    }

    /**
     * determine next eventType.
     *
     * @return the next eventTupe (or null)
     */
    private MoneyWiseXAnalysisEventType nextEventType() {
        /* Look for a price event */
        OceanusDate myDate = null;
        MoneyWiseXAnalysisEventType myType = null;
        if (theNextPrice != null) {
            myDate = theNextPrice.getDate();
            myType = MoneyWiseXAnalysisEventType.SECURITYPRICE;
        }

        /* Look for an earlier xchgRate event */
        if (theNextXchgRate != null
                && (myDate == null || myDate.compareTo(theNextXchgRate.getDate()) > 0)) {
            myDate = theNextXchgRate.getDate();
            myType = MoneyWiseXAnalysisEventType.XCHANGERATE;
        }

        /* Look for an earlier depRate event */
        if (theNextDepRate != null
                && (myDate == null || myDate.compareTo(theNextDepRate.getDate()) > 0)) {
            myDate = theNextDepRate.getDate();
            myType = MoneyWiseXAnalysisEventType.DEPOSITRATE;
        }

        /* Immediate return for starting prices */
        if (theStartDate.equals(myDate)) {
            return myType;
        }

        /* If we have not yet built opening balances */
        if (!balancesBuilt) {
            balancesBuilt = true;
            return MoneyWiseXAnalysisEventType.OPENINGBALANCE;
        }

        /* Look for an earlier transaction */
        if (theNextTrans != null
                && (myDate == null || myDate.compareTo(theNextTrans.getDate()) > 0)) {
            return MoneyWiseXAnalysisEventType.TRANSACTION;
        }

        /* Return the next eventType */
        return myType;
    }

    /**
     * Build next price event.
     */
    private void nextSecurityPrice() {
        final OceanusDate myDate = theNextPrice.getDate();
        theNextEvent = theEvents.newSecurityPrice(myDate);
        do {
            processSecurityPrice();
        } while (theNextPrice != null && myDate.equals(theNextPrice.getDate()));
    }

    /**
     * Process securityPrice.
     */
    private void processSecurityPrice() {
        theNextEvent.declareSecurityPrice(theNextPrice);
        thePriceMap.put(theNextPrice.getSecurity(), theNextPrice.getPrice());
        iteratePrice();
    }

    /**
     * Build next xchangeRate event.
     */
    private void nextXchgRate() {
        final OceanusDate myDate = theNextXchgRate.getDate();
        theNextEvent = theEvents.newXchangeRate(myDate);
        do {
            processXchgRate();
        } while (theNextXchgRate != null && myDate.equals(theNextXchgRate.getDate()));
    }

    /**
     * Process xchangeRate.
     */
    private void processXchgRate() {
        theNextEvent.declareExchangeRate(theNextXchgRate);
        theXchgRateMap.put(theNextXchgRate.getToCurrency(), theNextXchgRate.getExchangeRate());
        iterateXchgRate();
    }

    /**
     * Build next depositRate event.
     */
    private void nextDepRate() {
        final OceanusDate myDate = theNextDepRate.getDate();
        theNextEvent = theEvents.newDepositRate(myDate);
        do {
            processDepRate();
        } while (theNextDepRate != null && myDate.equals(theNextDepRate.getDate()));
    }

    /**
     * Process depositRate.
     */
    private void processDepRate() {
        theNextEvent.declareDepositRate(theNextDepRate);
        theDepRateMap.put(theNextDepRate.getDeposit(), theNextDepRate.getRate());
        iterateDepRate();
    }

    /**
     * Build next transaction event.
     */
    private void nextTransaction() {
        theNextEvent = theEvents.newTransaction(theNextTrans);
        iterateTrans();
    }

    @Override
    public OceanusPrice getCurrentPrice(final MoneyWiseSecurity pSecurity) {
        final OceanusPrice myPrice = thePriceMap.get(pSecurity);
        return myPrice == null
                ? OceanusPrice.getWholeUnits(1, pSecurity.getCurrency())
                : myPrice;
    }

    @Override
    public OceanusRatio getCurrentXchgRate(final MoneyWiseCurrency pCurrency) {
        final OceanusRatio myRate = theXchgRateMap.get(pCurrency);
        return myRate == null ? OceanusRatio.ONE : myRate;
    }

    @Override
    public OceanusRate getCurrentDepositRate(final MoneyWiseDeposit pDeposit) {
        return theDepRateMap.get(pDeposit);
    }

    @Override
    public void registerForPriceUpdates(final MoneyWiseXAnalysisBucketPriced pBucket) {
        /* Determine the security whose price we are tracking */
        MoneyWiseSecurity mySecurity = pBucket.getSecurity();
        if (pBucket.isStockOption()) {
            mySecurity = mySecurity.getUnderlyingStock();
        }

        /* Add to list */
        final List<MoneyWiseXAnalysisBucketPriced> myList = thePricedBucketsMap.computeIfAbsent(mySecurity, x -> new ArrayList<>());
        myList.add(pBucket);
    }

    @Override
    public void registerForXchgRateUpdates(final MoneyWiseXAnalysisBucketForeign pBucket) {
        final List<MoneyWiseXAnalysisBucketForeign> myList = theForeignBucketsMap.computeIfAbsent(pBucket.getCurrency(), x -> new ArrayList<>());
        myList.add(pBucket);
    }

    @Override
    public void registerBucketInterest(final MoneyWiseXAnalysisBucketRegister pBucket) {
        theEventBuckets.put(pBucket.getBucketId(), pBucket);
    }

    @Override
    public void registerInterestedBucketsForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        for (MoneyWiseXAnalysisBucketRegister myBucket : theEventBuckets.values()) {
            myBucket.registerEvent(pEvent);
        }
        theEventBuckets.clear();
    }

    /**
     * Set new price for security as part of a transaction.
     *
     * @param pSecurity the security
     * @param pPrice    the new price
     */
    public void setNewPriceViaTransaction(final MoneyWiseSecurity pSecurity,
                                          final OceanusPrice pPrice) {
        thePriceMap.put(pSecurity, pPrice);
    }

    /**
     * Obtain the pricedBuckets iterator for a security.
     *
     * @param pSecurity the security
     * @return the iterator
     */
    public Iterator<MoneyWiseXAnalysisBucketPriced> pricedBucketIterator(final MoneyWiseSecurity pSecurity) {
        final List<MoneyWiseXAnalysisBucketPriced> myList = thePricedBucketsMap.get(pSecurity);
        return myList != null ? myList.iterator() : Collections.emptyIterator();
    }

    /**
     * Obtain the foreignBuckets iterator for a currency.
     *
     * @param pCurrency the currency
     * @return the iterator
     */
    public Iterator<MoneyWiseXAnalysisBucketForeign> foreignBucketIterator(final MoneyWiseCurrency pCurrency) {
        final List<MoneyWiseXAnalysisBucketForeign> myList = theForeignBucketsMap.get(pCurrency);
        return myList != null ? myList.iterator() : Collections.emptyIterator();
    }

    /**
     * Iterate the SecurityPrices.
     */
    private void iteratePrice() {
        theNextPrice = thePriceIterator.hasNext() ? thePriceIterator.next() : null;
    }

    /**
     * Iterate the ExchangeRates.
     */
    private void iterateXchgRate() {
        theNextXchgRate = theXchgRateIterator.hasNext() ? theXchgRateIterator.next() : null;
    }

    /**
     * Iterate the ExchangeRates.
     */
    private void iterateDepRate() {
        theNextDepRate = theDepRateIterator.hasNext() ? theDepRateIterator.next() : null;
    }

    /**
     * Iterate the Transactions.
     */
    private void iterateTrans() {
        do {
            theNextTrans = theTransIterator.hasNext() ? theTransIterator.next() : null;
        } while (theNextTrans != null && theNextTrans.isDeleted());
    }

    /**
     * Build opening balances.
     */
    private void buildBalances() {
        theNextEvent = theEvents.newOpeningBalance(theStartDate);
        buildBalances(MoneyWiseBasicDataType.DEPOSIT);
        buildBalances(MoneyWiseBasicDataType.CASH);
        buildBalances(MoneyWiseBasicDataType.LOAN);
    }

    /**
     * Build opening balances.
     *
     * @param pType the data type
     */
    private void buildBalances(final MoneyWiseBasicDataType pType) {
        final MoneyWiseAssetBaseList<?> myList = theEditSet.getDataList(pType, MoneyWiseAssetBaseList.class);
        final Iterator<? extends MoneyWiseAssetBase> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAssetBase myAsset = myIterator.next();
            final OceanusMoney myBalance = myAsset.getOpeningBalance();
            if (myBalance != null) {
                theNextEvent.declareOpeningBalance(myAsset);
            }
        }
    }
}
