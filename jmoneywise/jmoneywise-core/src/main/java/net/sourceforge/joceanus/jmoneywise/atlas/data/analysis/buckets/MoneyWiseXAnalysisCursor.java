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

import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseNewDepositRate;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseNewDepositRate.MoneyWiseNewDepositRateList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase.MoneyWiseAssetBaseList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

/**
 * Analysis Cursor.
 */
public class MoneyWiseXAnalysisCursor {
    /**
     * The securityPrice iterator.
     */
    private final ListIterator<MoneyWiseSecurityPrice> thePriceIterator;

    /**
     * The exchangeRate iterator.
     */
    private final ListIterator<MoneyWiseExchangeRate> theXchgRateIterator;

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
    private final Map<MoneyWiseSecurity, TethysPrice> thePriceMap;

    /**
     * The xchgRate map.
     */
    private final Map<MoneyWiseCurrency, TethysRatio> theXchgRateMap;

    /**
     * The depositRate map.
     */
    private final Map<MoneyWiseDeposit, TethysRate> theDepRateMap;

    /**
     * The editSet.
     */
    private final PrometheusEditSet theEditSet;

    /**
     * The startDate.
     */
    private final TethysDate theStartDate;

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
     * @param pEditSet the editSet
     */
    MoneyWiseXAnalysisCursor(final PrometheusEditSet pEditSet) {
        /* Store parameters */
        theEditSet = pEditSet;

        final MoneyWiseSecurityPriceList myPrices = theEditSet.getDataList(MoneyWiseBasicDataType.SECURITYPRICE, MoneyWiseSecurityPriceList.class);
        thePriceIterator = myPrices.listIterator(myPrices.size());
        final MoneyWiseExchangeRateList myXchgRates = theEditSet.getDataList(MoneyWiseBasicDataType.EXCHANGERATE, MoneyWiseExchangeRateList.class);
        theXchgRateIterator = myXchgRates.listIterator(myXchgRates.size());
        theDepRateIterator = new MoneyWiseNewDepositRateList(theEditSet).iterator();
        theTransIterator = theEditSet.getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class).iterator();
        theStartDate = ((MoneyWiseDataSet) theEditSet.getDataSet()).getDateRange().getStart();
        balancesBuilt = false;

        /* Create the maps */
        thePriceMap = new HashMap<>();
        theXchgRateMap = new HashMap<>();
        theDepRateMap = new HashMap<>();

        /* Set up first elements */
        iteratePrice();
        iterateXchgRate();
        iterateDepRate();
        iterateTrans();
    }

    /**
     * Build next event.
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
     * @return the next eventTupe (or null)
     */
    public MoneyWiseXAnalysisEventType nextEventType() {
        /* Look for a price event */
        TethysDate myDate = null;
        MoneyWiseXAnalysisEventType myType = null;
        if (theNextPrice != null) {
            myDate = theNextPrice.getDate();
            myType = MoneyWiseXAnalysisEventType.SECURITYPRICE;

            /* Immediate return for starting prices */
            if (theStartDate.equals(myDate)) {
                return myType;
            }
        }

        /* Look for an earlier xchgRate event */
        if (theNextXchgRate != null
                && (myDate == null || myDate.compareTo(theNextXchgRate.getDate()) > 0)) {
            myDate = theNextXchgRate.getDate();
            myType = MoneyWiseXAnalysisEventType.XCHANGERATE;

            /* Immediate return for starting rates */
            if (theStartDate.equals(myDate)) {
                return myType;
            }
        }

        /* Look for an earlier depRate event */
        if (theNextDepRate != null
                && (myDate == null || myDate.compareTo(theNextDepRate.getDate()) > 0)) {
            myDate = theNextDepRate.getDate();
            myType = MoneyWiseXAnalysisEventType.DEPOSITRATE;

            /* Immediate return for starting rates */
            if (theStartDate.equals(myDate)) {
                return myType;
            }
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
        final TethysDate myDate = theNextPrice.getDate();
        theNextEvent = new MoneyWiseXAnalysisEvent(MoneyWiseXAnalysisEventType.SECURITYPRICE, myDate);
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
     * Build next xchgRate event.
     */
    private void nextXchgRate() {
        final TethysDate myDate = theNextXchgRate.getDate();
        theNextEvent = new MoneyWiseXAnalysisEvent(MoneyWiseXAnalysisEventType.XCHANGERATE, myDate);
        do {
            processXchgRate();
        } while (theNextXchgRate != null && myDate.equals(theNextXchgRate.getDate()));
    }

    /**
     * Process xchgRate.
     */
    private void processXchgRate() {
        theNextEvent.declareExchangeRate(theNextXchgRate);
        theXchgRateMap.put(theNextXchgRate.getFromCurrency(), theNextXchgRate.getExchangeRate());
        iterateXchgRate();
    }

    /**
     * Build next xchgRate event.
     */
    private void nextDepRate() {
        final TethysDate myDate = theNextDepRate.getDate();
        theNextEvent = new MoneyWiseXAnalysisEvent(MoneyWiseXAnalysisEventType.DEPOSITRATE, myDate);
        do {
            processDepRate();
        } while (theNextDepRate != null && myDate.equals(theNextDepRate.getDate()));
    }

    /**
     * Process xchgRate.
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
        theNextEvent = new MoneyWiseXAnalysisEvent(theNextTrans);
        iterateTrans();
    }

    /**
     * Obtain the current price for a security
     *
     * @param pSecurity the security
     */
    public TethysPrice getCurrentPrice(final MoneyWiseSecurity pSecurity) {
        final TethysPrice myPrice = thePriceMap.get(pSecurity);
        return myPrice == null
                ? TethysPrice.getWholeUnits(1, pSecurity.getCurrency())
                : myPrice;
    }

    /**
     * Obtain the current rate for a currency
     *
     * @param pCurrency the currency
     */
    public TethysRatio getCurrentXchgRate(final MoneyWiseCurrency pCurrency) {
        final TethysRatio myRate = theXchgRateMap.get(pCurrency);
        return myRate == null ? TethysRatio.ONE : myRate;
    }

    /**
     * Obtain the depositRate for a deposit
     *
     * @param pDeposit the deposit
     */
    public TethysRate getCurrentDepositRate(final MoneyWiseDeposit pDeposit) {
        return theDepRateMap.get(pDeposit);
    }

    /**
     * Iterate the SecurityPrices.
     */
    private void iteratePrice() {
        theNextPrice = thePriceIterator.hasPrevious() ? thePriceIterator.previous() : null;
    }

    /**
     * Iterate the ExchangeRates.
     */
    private void iterateXchgRate() {
        theNextXchgRate = theXchgRateIterator.hasPrevious() ? theXchgRateIterator.previous() : null;
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
        theNextTrans = theTransIterator.hasNext() ? theTransIterator.next() : null;
    }

    /**
     * Build opening balances.
     */
    private void buildBalances() {
        theNextEvent = new MoneyWiseXAnalysisEvent(MoneyWiseXAnalysisEventType.OPENINGBALANCE, theStartDate);
        buildBalances(MoneyWiseBasicDataType.DEPOSIT);
        buildBalances(MoneyWiseBasicDataType.CASH);
        buildBalances(MoneyWiseBasicDataType.LOAN);
    }

    /**
     * Build opening balances.
     */
    private void buildBalances(final MoneyWiseBasicDataType pType) {
        final MoneyWiseAssetBaseList<?> myList = theEditSet.getDataList(pType, MoneyWiseAssetBaseList.class);
        final Iterator<? extends MoneyWiseAssetBase> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAssetBase myAsset = myIterator.next();
            final TethysMoney myBalance = myAsset.getOpeningBalance();
            if (myBalance != null) {
                theNextEvent.declareOpeningBalance(myAsset);
            }
        }
    }
}
