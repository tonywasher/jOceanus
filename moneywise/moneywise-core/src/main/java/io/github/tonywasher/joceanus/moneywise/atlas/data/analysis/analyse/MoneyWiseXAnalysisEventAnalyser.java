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
package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.analyse;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceManager;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseNewDepositRate;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisAccountBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketForeign;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketPriced;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding.MoneyWiseSecurityHoldingMap;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Evebt Analyser.
 */
public class MoneyWiseXAnalysisEventAnalyser {
    /**
     * The profile.
     */
    private final OceanusProfile theProfile;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The security holding map.
     */
    private final MoneyWiseSecurityHoldingMap theHoldingMap;

    /**
     * The State.
     */
    private final MoneyWiseXAnalysisState theState;

    /**
     * The market.
     */
    private final MoneyWiseXAnalysisMarket theMarket;

    /**
     * The tax.
     */
    private final MoneyWiseXAnalysisTax theTax;

    /**
     * The basic trans analyser.
     */
    private final MoneyWiseXAnalysisTransAnalyser theTrans;

    /**
     * The list of events.
     */
    private final MoneyWiseXAnalysisEventList theEvents;

    /**
     * Constructor.
     *
     * @param pTask          the task
     * @param pEditSet       the editSet
     * @param pPreferenceMgr the preference manager
     * @throws OceanusException on error
     */
    public MoneyWiseXAnalysisEventAnalyser(final OceanusProfile pTask,
                                           final PrometheusEditSet pEditSet,
                                           final MetisPreferenceManager pPreferenceMgr) throws OceanusException {
        /* Initialise the task */
        theProfile = pTask;
        final OceanusProfile myTask = theProfile.startTask("analyseEvents");

        /* Create the new Analysis */
        myTask.startTask("Initialise");
        theState = new MoneyWiseXAnalysisState(pEditSet);
        theAnalysis = new MoneyWiseXAnalysis(pEditSet, theState, pPreferenceMgr);
        theHoldingMap = pEditSet.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class).getSecurityHoldingsMap();

        /* Create the analysers */
        theMarket = new MoneyWiseXAnalysisMarket(this);
        theTax = new MoneyWiseXAnalysisTax(this);
        theTrans = new MoneyWiseXAnalysisTransAnalyser(this);

        /* Loop through the Events */
        for (; ; ) {
            /* Access next event */
            final MoneyWiseXAnalysisEvent myEvent = theState.nextEvent();
            if (myEvent == null) {
                break;
            }

            /* Switch on eventType */
            switch (myEvent.getEventType()) {
                case SECURITYPRICE:
                    processSecurityPrice(myEvent);
                    break;
                case XCHANGERATE:
                    processExchangeRate(myEvent);
                    break;
                case DEPOSITRATE:
                    processDepositRate(myEvent);
                    break;
                case OPENINGBALANCE:
                    processOpeningBalance(myEvent);
                    break;
                case TRANSACTION:
                default:
                    theTrans.processTransaction(myEvent);
                    break;
            }
        }

        /* Obtain the eventList */
        theEvents = theState.getEventList();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Obtain the analysis.
     *
     * @return the analysis
     */
    MoneyWiseXAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the securityHoldingMap.
     *
     * @return the map
     */
    MoneyWiseSecurityHoldingMap getSecurityHoldingMap() {
        return theHoldingMap;
    }

    /**
     * Obtain the state.
     *
     * @return the state
     */
    MoneyWiseXAnalysisState getState() {
        return theState;
    }

    /**
     * Obtain the market analysis.
     *
     * @return the market
     */
    MoneyWiseXAnalysisMarket getMarket() {
        return theMarket;
    }

    /**
     * Obtain the tax analysis.
     *
     * @return the tax
     */
    MoneyWiseXAnalysisTax getTax() {
        return theTax;
    }

    /**
     * Obtain the transaction analyser.
     *
     * @return the analyser
     */
    MoneyWiseXAnalysisTransAnalyser getTransAnalyser() {
        return theTrans;
    }

    /**
     * Obtain the events iterator.
     *
     * @return the events iterator
     */
    Iterator<MoneyWiseXAnalysisEvent> eventsIterator() {
        return theEvents.iterator();
    }

    /**
     * Mark active accounts.
     *
     * @throws OceanusException on error
     */
    public void postProcessAnalysis() throws OceanusException {
        /* Start a new task */
        final OceanusProfile myTask = theProfile.startTask("postProcessAnalysis1");
        myTask.startTask("markActiveAccounts");

        /* Mark relevant accounts */
        theAnalysis.getDeposits().markActiveAccounts();
        theAnalysis.getCash().markActiveAccounts();
        theAnalysis.getLoans().markActiveAccounts();

        /* Mark relevant securities */
        theAnalysis.getPortfolios().markActiveSecurities();

        /* Record the events */
        theAnalysis.setEvents(theEvents);

        /* Complete the task */
        myTask.end();
    }

    /**
     * process securityPrice.
     *
     * @param pEvent the event
     */
    private void processSecurityPrice(final MoneyWiseXAnalysisEvent pEvent) {
        /* Loop through the prices for this event */
        final Iterator<MoneyWiseSecurityPrice> myPriceIterator = pEvent.priceIterator();
        while (myPriceIterator.hasNext()) {
            final MoneyWiseSecurityPrice myPrice = myPriceIterator.next();

            /* Loop through the registered buckets */
            final Iterator<MoneyWiseXAnalysisBucketPriced> myBucketIterator = theState.pricedBucketIterator(myPrice.getSecurity());
            while (myBucketIterator.hasNext()) {
                final MoneyWiseXAnalysisBucketPriced myBucket = myBucketIterator.next();

                /* update the price and determine the value delta */
                myBucket.recordSecurityPrice();
                myBucket.valueAsset();
                myBucket.calculateUnrealisedGains();

                final OceanusMoney myDelta = myBucket.getDeltaValuation();

                /* Register the bucket for the event */
                theState.registerBucketInterest(myBucket);

                /* Record the delta as a market growth */
                theMarket.adjustTotalsForMarketGrowth(pEvent, myDelta);
            }
        }

        /* Register all the buckets */
        theMarket.adjustMarketTotals(pEvent);
        theState.registerInterestedBucketsForEvent(pEvent);
    }

    /**
     * process ExchangeRate.
     *
     * @param pEvent the event
     */
    private void processExchangeRate(final MoneyWiseXAnalysisEvent pEvent) {
        /* Loop through the rates for this event */
        final Iterator<MoneyWiseExchangeRate> myRateIterator = pEvent.xchgRateIterator();
        while (myRateIterator.hasNext()) {
            final MoneyWiseExchangeRate myRate = myRateIterator.next();

            /* Loop through the registered buckets */
            final Iterator<MoneyWiseXAnalysisBucketForeign> myBucketIterator = theState.foreignBucketIterator(myRate.getToCurrency());
            while (myBucketIterator.hasNext()) {
                final MoneyWiseXAnalysisBucketForeign myBucket = myBucketIterator.next();

                /* update the rate and determine the value delta */
                myBucket.recordExchangeRate();
                myBucket.adjustValuation();
                final OceanusMoney myDelta = myBucket.getDeltaValuation();

                /* Register the bucket for the event */
                theState.registerBucketInterest(myBucket);

                /* Record the delta as a currency fluctuation */
                theMarket.adjustTotalsForCurrencyFluctuation(pEvent, myDelta);
            }
        }

        /* Register all the buckets */
        theMarket.adjustMarketTotals(pEvent);
        theState.registerInterestedBucketsForEvent(pEvent);
    }

    /**
     * process depositRate.
     *
     * @param pEvent the event
     */
    private void processDepositRate(final MoneyWiseXAnalysisEvent pEvent) {
        /* Loop through the prices for this event */
        final Iterator<MoneyWiseNewDepositRate> myRateIterator = pEvent.depRateIterator();
        while (myRateIterator.hasNext()) {
            final MoneyWiseNewDepositRate myRate = myRateIterator.next();

            /* Access the bucket and update the depositRate */
            final MoneyWiseXAnalysisDepositBucket myBucket = theAnalysis.getDeposits().getBucket(myRate.getDeposit());
            myBucket.recordDepositRate();
            theState.registerBucketInterest(myBucket);
        }

        /* Register all the buckets */
        theState.registerInterestedBucketsForEvent(pEvent);
    }

    /**
     * process OpeningBalance.
     *
     * @param pEvent the event
     */
    private void processOpeningBalance(final MoneyWiseXAnalysisEvent pEvent) {
        /* Loop through the prices for this event */
        final Iterator<MoneyWiseAssetBase> myBalanceIterator = pEvent.balanceIterator();
        while (myBalanceIterator.hasNext()) {
            final MoneyWiseAssetBase myAsset = myBalanceIterator.next();

            /* Loop through the registered buckets */
            final MoneyWiseXAnalysisAccountBucket<?> myBucket = theTrans.getAccountBucket(myAsset);
            myBucket.recordOpeningBalance();
            theState.registerBucketInterest(myBucket);
        }

        /* Register all the buckets */
        theState.registerInterestedBucketsForEvent(pEvent);
    }
}
