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

package io.github.tonywasher.joceanus.moneywise.quicken.builder.atlas;

import io.github.tonywasher.joceanus.moneywise.analysis.atlas.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets.MoneyWiseXAnalysisPortfolioCashBucket;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets.MoneyWiseXAnalysisSecurityBucket;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.values.MoneyWiseXAnalysisAccountAttr;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.values.MoneyWiseXAnalysisSecurityAttr;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.values.MoneyWiseXAnalysisSecurityValues;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceDataMap;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogManager;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogger;

import java.util.Objects;

/**
 * Portfolio Builder class for QIF File.
 */
public class MoneyWiseXQIFPortfolioBuilder
        implements MoneyWiseXQIFPortfolioHelper {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MoneyWiseXQIFPortfolioBuilder.class);

    /**
     * The QIF File Type.
     */
    private final MoneyWiseQIFType theFileType;

    /**
     * The Builder.
     */
    private final MoneyWiseXQIFHelper theHelper;

    /**
     * The Data.
     */
    private final MoneyWiseDataSet theData;

    /**
     * The Analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The Portfolio Adjust engine.
     */
    private final MoneyWiseXQIFPortfolioAdjust thePortAdjust;

    /**
     * The Portfolio Income engine.
     */
    private final MoneyWiseXQIFPortfolioIncome thePortIncome;

    /**
     * The Portfolio Expense engine.
     */
    private final MoneyWiseXQIFPortfolioExpense thePortExpense;

    /**
     * The Portfolio Dividend engine.
     */
    private final MoneyWiseXQIFPortfolioDividend thePortDividend;

    /**
     * The Portfolio TakeOver engine.
     */
    private final MoneyWiseXQIFPortfolioTakeOver thePortTakeOver;

    /**
     * The Portfolio Xfer engine.
     */
    private final MoneyWiseXQIFPortfolioXfer thePortXfer;

    /**
     * Constructor.
     *
     * @param pHelper   the builder helper
     * @param pData     the data
     * @param pAnalysis the analysis
     */
    protected MoneyWiseXQIFPortfolioBuilder(final MoneyWiseXQIFHelper pHelper,
                                            final MoneyWiseDataSet pData,
                                            final MoneyWiseXAnalysis pAnalysis) {
        /* Store parameters */
        theHelper = pHelper;
        theFileType = theHelper.getRegister().getFileType();
        theData = pData;
        theAnalysis = pAnalysis;

        /* Create subBuilders */
        thePortAdjust = new MoneyWiseXQIFPortfolioAdjust(this);
        thePortIncome = new MoneyWiseXQIFPortfolioIncome(this);
        thePortExpense = new MoneyWiseXQIFPortfolioExpense(this);
        thePortDividend = new MoneyWiseXQIFPortfolioDividend(this);
        thePortTakeOver = new MoneyWiseXQIFPortfolioTakeOver(this);
        thePortXfer = new MoneyWiseXQIFPortfolioXfer(theAnalysis, this);
    }

    @Override
    public MoneyWiseXQIFHelper getHelper() {
        return theHelper;
    }

    @Override
    public OceanusPrice getPriceForDate(final MoneyWiseSecurity pSecurity,
                                        final OceanusDate pDate) {
        /* Add the price */
        final MoneyWiseSecurityPriceDataMap myPriceMap = theData.getSecurityPriceDataMap();
        return myPriceMap.getPriceForDate(pSecurity, pDate);
    }

    @Override
    public OceanusUnits getUnitsForHoldingEvent(final MoneyWiseSecurityHolding pHolding,
                                                final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseXAnalysisSecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Access the resulting values */
        final MoneyWiseXAnalysisSecurityValues myValues = myBucket.getValuesForEvent(pTrans);
        return myValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
    }

    @Override
    public OceanusUnits getBaseUnitsForHolding(final MoneyWiseSecurityHolding pHolding,
                                               final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseXAnalysisSecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Access the base values */
        final MoneyWiseXAnalysisSecurityValues myValues = myBucket.getValuesForEvent(pTrans);
        if (myValues != null) {
            OceanusUnits myUnits = myValues.getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
            myUnits = new OceanusUnits(myUnits);

            /* Determine the delta in units */
            final OceanusUnits myDelta = myBucket.getUnitsDeltaForEvent(pTrans, MoneyWiseXAnalysisSecurityAttr.UNITS);
            if (myDelta != null) {
                myUnits.subtractUnits(myDelta);
            }
            return myUnits;
        } else {
            return OceanusUnits.getWholeUnits(0);
        }
    }

    @Override
    public OceanusMoney getDeltaCostForHolding(final MoneyWiseSecurityHolding pHolding,
                                               final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseXAnalysisSecurityBucket myBucket = myPortfolios.getBucket(pHolding);

        /* Obtain the cost delta for the transaction */
        return myBucket.getMoneyDeltaForEvent(pTrans, MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
    }

    /**
     * Process income to a security.
     *
     * @param pPayee   the payee
     * @param pHolding the security holding
     * @param pTrans   the transaction
     */
    void processIncomeToSecurity(final MoneyWisePayee pPayee,
                                 final MoneyWiseSecurityHolding pHolding,
                                 final MoneyWiseXAnalysisEvent pTrans) {
        thePortIncome.processIncomeToSecurity(pPayee, pHolding, pTrans);
    }

    /**
     * Process income to a portfolio.
     *
     * @param pDebit     the source payee
     * @param pPortfolio the portfolio
     * @param pTrans     the transaction
     */
    void processIncomeToPortfolio(final MoneyWisePayee pDebit,
                                  final MoneyWisePortfolio pPortfolio,
                                  final MoneyWiseXAnalysisEvent pTrans) {
        thePortIncome.processIncomeToPortfolio(pDebit, pPortfolio, pTrans);
    }

    /**
     * Process expense from a security.
     *
     * @param pPayee   the payee
     * @param pHolding the security holding
     * @param pTrans   the transaction
     */
    public void processExpenseFromSecurity(final MoneyWisePayee pPayee,
                                           final MoneyWiseSecurityHolding pHolding,
                                           final MoneyWiseXAnalysisEvent pTrans) {
        thePortExpense.processExpenseFromSecurity(pPayee, pHolding, pTrans);
    }

    /**
     * Process expense from a portfolio.
     *
     * @param pCredit    the target payee
     * @param pPortfolio the portfolio
     * @param pTrans     the transaction
     */
    void processExpenseFromPortfolio(final MoneyWisePayee pCredit,
                                     final MoneyWisePortfolio pPortfolio,
                                     final MoneyWiseXAnalysisEvent pTrans) {
        thePortExpense.processExpenseFromPortfolio(pCredit, pPortfolio, pTrans);
    }

    @Override
    public OceanusMoney getPortfolioCashValue(final MoneyWisePortfolio pPortfolio,
                                              final MoneyWiseXAnalysisEvent pTrans) {
        /* Access the relevant bucket */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final MoneyWiseXAnalysisPortfolioCashBucket myBucket = myPortfolios.getCashBucket(pPortfolio);

        /* Obtain the value delta for the transaction */
        OceanusMoney myValue = myBucket.getMoneyDeltaForEvent(pTrans, MoneyWiseXAnalysisAccountAttr.VALUATION);
        if (myValue != null) {
            myValue = new OceanusMoney(myValue);
            myValue.negate();
        }
        return myValue;
    }

    /**
     * Process transfer to a security.
     * <p>
     * Note that the source cannot be a Security, since that case is handled by
     * {@link #processTransferFromSecurity}
     *
     * @param pHolding the security holding
     * @param pDebit   the debit account
     * @param pTrans   the transaction
     */
    protected void processTransferToSecurity(final MoneyWiseSecurityHolding pHolding,
                                             final MoneyWiseTransAsset pDebit,
                                             final MoneyWiseXAnalysisEvent pTrans) {
        /* Switch on transaction type */
        switch (Objects.requireNonNull(pTrans.getCategory().getCategoryTypeClass())) {
            case LOYALTYBONUS:
                thePortIncome.processIncomeToSecurity((MoneyWisePayee) pDebit.getParent(), pHolding, pTrans);
                break;
            default:
                thePortXfer.processTransferTosSecurity(pHolding, pDebit, pTrans);
                break;
        }
    }

    /**
     * Process transfer between securities.
     *
     * @param pSource the source security holding
     * @param pTarget the target security holding
     * @param pTrans  the transaction
     */
    protected void processTransferBetweenSecurities(final MoneyWiseSecurityHolding pSource,
                                                    final MoneyWiseSecurityHolding pTarget,
                                                    final MoneyWiseXAnalysisEvent pTrans) {
        /* Switch on transaction type */
        switch (Objects.requireNonNull(pTrans.getCategory().getCategoryTypeClass())) {
            case STOCKSPLIT:
                if (theFileType.useStockSplit()) {
                    thePortAdjust.processStockSplit(pSource, pTrans);
                } else {
                    thePortAdjust.processSecurityAdjust(pSource, pTrans);
                }
                break;
            case UNITSADJUST:
                thePortAdjust.processSecurityAdjust(pSource, pTrans);
                break;
            case DIVIDEND:
                thePortDividend.processReinvestDividend(pSource, pTrans);
                break;
            case STOCKDEMERGER:
                thePortTakeOver.processStockDeMerger(pSource, pTarget, pTrans);
                break;
            case STOCKTAKEOVER, SECURITYREPLACE:
                thePortTakeOver.processStockTakeOver(pSource, pTarget, pTrans);
                break;
            case TRANSFER:
                thePortTakeOver.processSecurityExchange(pSource, pTarget, pTrans);
                break;
            default:
                LOGGER.error("Unsupported TransferBetweenSecurities Category: <%s>", pTrans.getCategory().getCategoryTypeClass());
                break;
        }
    }

    /**
     * Process transfer from a security.
     *
     * @param pHolding the security holding
     * @param pCredit  the credit account
     * @param pTrans   the transaction
     */
    protected void processTransferFromSecurity(final MoneyWiseSecurityHolding pHolding,
                                               final MoneyWiseTransAsset pCredit,
                                               final MoneyWiseXAnalysisEvent pTrans) {
        /* Switch on transaction type */
        switch (Objects.requireNonNull(pTrans.getCategory().getCategoryTypeClass())) {
            case DIVIDEND -> thePortDividend.processStockDividend(pHolding, pCredit, pTrans);
            case PORTFOLIOXFER ->
                    thePortXfer.processPortfolioXferForHolding(pHolding, (MoneyWisePortfolio) pCredit, pTrans);
            default -> thePortXfer.processTransferOut(pHolding, pCredit, pTrans);
        }
    }

    /**
     * Process transfer between portfolios.
     *
     * @param pSource the source portfolio
     * @param pTarget the target portfolio
     * @param pTrans  the transaction
     */
    protected void processTransferBetweenPortfolios(final MoneyWisePortfolio pSource,
                                                    final MoneyWisePortfolio pTarget,
                                                    final MoneyWiseXAnalysisEvent pTrans) {
        /* Switch on transaction type */
        switch (Objects.requireNonNull(pTrans.getCategory().getCategoryTypeClass())) {
            case INTEREST, LOYALTYBONUS:
                thePortIncome.processIncomeToPortfolio(pSource.getParent(), pTarget, pTrans);
                break;
            case PORTFOLIOXFER:
                thePortXfer.processPortfolioXferBetweenPortfolios(pSource, pTarget, pTrans);
                break;
            case TRANSFER:
                thePortXfer.processCashTransferBetweenPortfolios(pSource, pTarget, pTrans);
                break;
            default:
                LOGGER.error("Unsupported TransferBetweenPortfolios Category: <%s>", pTrans.getCategory().getCategoryTypeClass());
                break;
        }
    }

    /**
     * Process transfer to a portfolio.
     *
     * @param pPortfolio the portfolio
     * @param pDebit     the source account
     * @param pTrans     the transaction
     */
    protected void processTransferToPortfolio(final MoneyWisePortfolio pPortfolio,
                                              final MoneyWiseTransAsset pDebit,
                                              final MoneyWiseXAnalysisEvent pTrans) {
        /* Switch on transaction type */
        switch (Objects.requireNonNull(pTrans.getCategory().getCategoryTypeClass())) {
            case TRANSFER:
                thePortXfer.processCashTransferToPortfolio(pPortfolio, pDebit, pTrans);
                break;
            default:
                LOGGER.error("Unsupported TransferToPortfolio Category: <%s>", pTrans.getCategory().getCategoryTypeClass());
                break;
        }
    }

    /**
     * Process transfer from a portfolio.
     *
     * @param pPortfolio the portfolio
     * @param pCredit    the target account
     * @param pTrans     the transaction
     */
    protected void processTransferFromPortfolio(final MoneyWisePortfolio pPortfolio,
                                                final MoneyWiseTransAsset pCredit,
                                                final MoneyWiseXAnalysisEvent pTrans) {
        /* Switch on transaction type */
        switch (Objects.requireNonNull(pTrans.getCategory().getCategoryTypeClass())) {
            case TRANSFER:
                thePortXfer.processCashTransferFromPortfolio(pPortfolio, pCredit, pTrans);
                break;
            default:
                LOGGER.error("Unsupported TransferFromPortfolio Category: <%s>", pTrans.getCategory().getCategoryTypeClass());
                break;
        }
    }
}
