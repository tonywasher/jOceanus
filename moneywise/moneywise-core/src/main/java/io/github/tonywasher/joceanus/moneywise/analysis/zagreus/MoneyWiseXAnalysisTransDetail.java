/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.moneywise.analysis.zagreus;

import io.github.tonywasher.joceanus.moneywise.analysis.atlas.analyse.MoneyWiseXAnalyseState;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;

import java.util.Currency;
import java.util.EnumMap;
import java.util.Map;

/**
 * Transaction Analysis detail.
 */
public class MoneyWiseXAnalysisTransDetail {
    /**
     * The State.
     */
    private final MoneyWiseXAnalyseState theState;

    /**
     * The Reporting Currency.
     */
    private final Currency theCurrency;

    /**
     * The detail map.
     */
    private final Map<MoneyWiseXAnalysisTransItem, MoneyWiseZAnalysisValuePair> theMap;

    /* Is the direction From */
    private boolean isFrom;

    /**
     * Constructor.
     *
     * @param pState the state.
     */
    MoneyWiseXAnalysisTransDetail(final MoneyWiseXAnalyseState pState,
                                  final MoneyWiseCurrency pReportingCurrency) {
        theState = pState;
        theCurrency = pReportingCurrency.getCurrency();
        theMap = new EnumMap<>(MoneyWiseXAnalysisTransItem.class);
    }

    /**
     * Analyse transaction.
     *
     * @param pTrans the transaction
     */
    void analyseTransaction(final MoneyWiseTransaction pTrans) {
        /* Clear the map */
        theMap.clear();

        /* Note direction of transaction */
        isFrom = pTrans.getDirection().isFrom();

        /* Access the standard amounts */
        processAmount(pTrans.getAmount(), MoneyWiseXAnalysisTransItem.ACCOUNTDELTA);
        processAmount(pTrans.getPartnerAmount(), MoneyWiseXAnalysisTransItem.PARTNERDELTA);
        processAmount(pTrans.getReturnedCash(), MoneyWiseXAnalysisTransItem.RETURNEDDELTA);
        processAmount(pTrans.getTaxCredit(), MoneyWiseXAnalysisTransItem.TAXCREDITDELTA);
        processAmount(pTrans.getEmployerNatIns(), MoneyWiseXAnalysisTransItem.ERNATINSDELTA);
        processAmount(pTrans.getEmployeeNatIns(), MoneyWiseXAnalysisTransItem.EENATINSDELTA);
        processAmount(pTrans.getDeemedBenefit(), MoneyWiseXAnalysisTransItem.BENEFITDELTA);
        processAmount(pTrans.getWithheld(), MoneyWiseXAnalysisTransItem.WITHHELDDELTA);
    }

    /**
     * Process amount.
     *
     * @param pAmount the amount
     */
    void processAmount(final OceanusMoney pAmount,
                       final MoneyWiseXAnalysisTransItem pItem) {
        /* If we have a non-zero amount */
        OceanusMoney myAmount = pAmount;
        if (myAmount != null) {
            /* Store the delta value */
            OceanusMoney myReporting = myAmount;

            /* If the amount is not in the reporting currency */
            if (!myAmount.getCurrency().equals(theCurrency)) {
                /* Convert to reporting currency */
                final OceanusRatio myXchangeRate = theState.getCurrentXchgRate(myAmount.getCurrency());
                myReporting = myAmount.convertCurrency(myAmount.getCurrency(), myXchangeRate);
            }

            /* Store reporting currency */
            theMap.put(pItem, new MoneyWiseZAnalysisValuePair(myAmount, myReporting));
        }
    }

    /**
     * Obtain the reporting item
     *
     * @param pItem the item
     * @return the reporting item
     */
    private MoneyWiseXAnalysisTransItem reporting4(final MoneyWiseXAnalysisTransItem pItem) {
        return switch (pItem) {
            case ACCOUNTDELTA -> MoneyWiseXAnalysisTransItem.ACCOUNTREPORTING;
            case PARTNERDELTA -> MoneyWiseXAnalysisTransItem.PARTNERREPORTING;
            case RETURNEDDELTA -> MoneyWiseXAnalysisTransItem.RETURNEDREPORTING;
            case TAXCREDITDELTA -> MoneyWiseXAnalysisTransItem.TAXCREDITREPORTING;
            case ERNATINSDELTA -> MoneyWiseXAnalysisTransItem.ERNATINSDELTA;
            case EENATINSDELTA -> MoneyWiseXAnalysisTransItem.EENATINSREPORTING;
            case BENEFITDELTA -> MoneyWiseXAnalysisTransItem.BENEFITREPORTING;
            case WITHHELDDELTA -> MoneyWiseXAnalysisTransItem.WITHHELDREPORTING;
            default -> pItem;
        };
    }

    /**
     * Value Pair
     */
    public final class MoneyWiseZAnalysisValuePair {
        /**
         * The value.
         */
        private final OceanusMoney theValue;

        /**
         * The reporting value.
         */
        private final OceanusMoney theReporting;

        /**
         * Constructor.
         *
         * @param pValue the value
         */
        MoneyWiseZAnalysisValuePair(final OceanusMoney pValue) {
            this(pValue, pValue);
        }

        /**
         * Constructor.
         *
         * @param pValue     the value
         * @param pReporting the reporting value
         */
        MoneyWiseZAnalysisValuePair(final OceanusMoney pValue,
                                    final OceanusMoney pReporting) {
            theValue = pValue;
            theReporting = pReporting;
        }

        /**
         * Obtain value.
         *
         * @return the value
         */
        public OceanusMoney getValue() {
            return theValue;
        }

        /**
         * Obtain reporting value.
         *
         * @return the value
         */
        public OceanusMoney getReporting() {
            return theReporting;
        }
    }
}
