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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.analyse;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseNewDepositRate;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisAccountBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketForeign;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketPriced;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;

/**
 * Evebt Analyser.
 */
public class MoneyWiseXAnalysisEventAnalyser {
    /**
     * The profile.
     */
    private final TethysProfile theProfile;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

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
     * Constructor.
     * @param pTask the task
     * @param pEditSet the editSet
     * @param pPreferenceMgr the preference manager
     * @throws OceanusException on error
     */
    public MoneyWiseXAnalysisEventAnalyser(final TethysProfile pTask,
                                           final PrometheusEditSet pEditSet,
                                           final MetisPreferenceManager pPreferenceMgr) throws OceanusException {
        /* Initialise the task */
        theProfile = pTask;
        final TethysProfile myTask = theProfile.startTask("analyseTransactions");

        /* Create the new Analysis */
        myTask.startTask("Initialise");
        theState = new MoneyWiseXAnalysisState(pEditSet);
        theAnalysis = new MoneyWiseXAnalysis(pEditSet, theState, pPreferenceMgr);

        /* Create the analysers */
        theMarket = new MoneyWiseXAnalysisMarket(theAnalysis);
        theTax = new MoneyWiseXAnalysisTax(theAnalysis);
        theTrans = new MoneyWiseXAnalysisTransAnalyser(this);

        /* Loop through the Events */
        for(;;) {
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

        /* Complete the task */
        myTask.end();
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    MoneyWiseXAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the state.
     * @return the state
     */
    MoneyWiseXAnalysisState getState() {
        return theState;
    }

    /**
     * Obtain the market analysis.
     * @return the market
     */
    MoneyWiseXAnalysisMarket getMarket() {
        return theMarket;
    }

    /**
     * Obtain the tax analysis.
     * @return the tax
     */
    MoneyWiseXAnalysisTax getTax() {
        return theTax;
    }

    /**
     * Mark active accounts.
     * @throws OceanusException on error
     */
    public void postProcessAnalysis() throws OceanusException {
        /* Start a new task */
        final TethysProfile myTask = theProfile.startTask("postProcessAnalysis");
        myTask.startTask("markActiveAccounts");

        /* Mark relevant accounts */
        theAnalysis.getDeposits().markActiveAccounts();
        theAnalysis.getCash().markActiveAccounts();
        theAnalysis.getLoans().markActiveAccounts();

        /* Mark relevant securities */
        theAnalysis.getPortfolios().markActiveSecurities();

        /* Complete the task */
        myTask.end();
    }

    /**
     * process securityPrice
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

                /* update the rate and determine the value delta */
                myBucket.recordSecurityPrice();
                myBucket.adjustValuation();
                final TethysMoney myDelta = myBucket.getDeltaValuation();

                /* Register the bucket for the event */
                theState.registerBucketForEvent(myBucket);

                /* Record the delta as a market growth */
                theMarket.adjustTotalsForMarketGrowth(pEvent, myDelta);
            }
        }

        /* Register all the buckets */
        theMarket.adjustMarketTotals(pEvent);
        theState.registerBucketsForEvent(pEvent);
    }

    /**
     * process ExchangeRate
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
                final TethysMoney myDelta = myBucket.getDeltaValuation();

                /* Register the bucket for the event */
                theState.registerBucketForEvent(myBucket);

                /* Record the delta as a currency fluctuation */
                theMarket.adjustTotalsForCurrencyFluctuation(pEvent, myDelta);
            }
        }

        /* Register all the buckets */
        theMarket.adjustMarketTotals(pEvent);
        theState.registerBucketsForEvent(pEvent);
    }

    /**
     * process depositRate
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
            theState.registerBucketForEvent(myBucket);
        }

        /* Register all the buckets */
        theState.registerBucketsForEvent(pEvent);
    }

    /**
     * process OpeningBalance
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
            theState.registerBucketForEvent(myBucket);
        }

        /* Register all the buckets */
        theState.registerBucketsForEvent(pEvent);
    }
}
