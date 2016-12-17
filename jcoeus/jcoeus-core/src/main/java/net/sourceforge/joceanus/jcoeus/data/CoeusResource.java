/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.jcoeus.data;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for Coeus.
 */
public enum CoeusResource
        implements TethysResourceId {
    /**
     * FundingCircle Market.
     */
    MARKET_FUNDINGCIRCLE("market.fundingcircle"),

    /**
     * LendingWorks Market.
     */
    MARKET_LENDINGWORKS("market.lendingworks"),

    /**
     * RateSetter Market.
     */
    MARKET_RATESETTER("market.ratesetter"),

    /**
     * Zopa Market.
     */
    MARKET_ZOPA("market.zopa"),

    /**
     * Loan Offered.
     */
    LOAN_OFFERED("loan.offered"),

    /**
     * Loan Active.
     */
    LOAN_ACTIVE("loan.active"),

    /**
     * Loan in Poor Health.
     */
    LOAN_POORLY("loan.poorly"),

    /**
     * Loan badDebt.
     */
    LOAN_BADDEBT("loan.badDebt"),

    /**
     * Loan RePaid.
     */
    LOAN_REPAID("loan.repaid"),

    /**
     * Loan Rejected.
     */
    LOAN_REJECTED("loan.rejected"),

    /**
     * Transaction Transfer.
     */
    TRANS_TRANSFER("trans.xfer"),

    /**
     * Transaction Loan.
     */
    TRANS_LOAN("trans.loan"),

    /**
     * Transaction RePayment.
     */
    TRANS_REPAYMENT("trans.repay"),

    /**
     * Transaction Interest.
     */
    TRANS_INTEREST("trans.interest"),

    /**
     * Transaction Fees.
     */
    TRANS_FEES("trans.fees"),

    /**
     * Transaction CashBack.
     */
    TRANS_CASHBACK("trans.cashback"),

    /**
     * Transaction Rate Promise.
     */
    TRANS_RATEPROMISE("trans.ratepromise"),

    /**
     * Transaction BuyLoan.
     */
    TRANS_BUYLOAN("trans.buyloan"),

    /**
     * Transaction BadDebt.
     */
    TRANS_BADDEBT("trans.baddebt"),

    /**
     * Transaction Recovery.
     */
    TRANS_RECOVERY("trans.recovery"),

    /**
     * Transaction Totals.
     */
    TRANS_TOTALS("trans.totals"),

    /**
     * Risk Band A+.
     */
    RISK_APLUS("risk.aplus"),

    /**
     * Risk Band A.
     */
    RISK_A("risk.a"),

    /**
     * Risk Band B.
     */
    RISK_B("risk.b"),

    /**
     * Risk Band C.
     */
    RISK_C("risk.c"),

    /**
     * Risk Band D.
     */
    RISK_D("risk.d"),

    /**
     * Risk Band E.
     */
    RISK_E("risk.e"),

    /**
     * Risk Band S.
     */
    RISK_S("risk.s"),

    /**
     * Risk Band Unclassified.
     */
    RISK_UNCLASSIFIED("risk.a"),

    /**
     * Data Provider.
     */
    DATA_PROVIDER("data.provider"),

    /**
     * Data AuctionIdMap.
     */
    DATA_AUCTIONMAP("data.auctionmap"),

    /**
     * Data LoanMap.
     */
    DATA_LOANMAP("data.loanmap"),

    /**
     * Data Transactions.
     */
    DATA_TRANSACTIONS("data.transactions"),

    /**
     * Data InitialLoans.
     */
    DATA_INITIALLOANS("data.initialloans"),

    /**
     * Data ID.
     */
    DATA_ID("data.id"),

    /**
     * Data Market.
     */
    DATA_MARKET("data.market"),

    /**
     * Data LoanId.
     */
    DATA_LOANID("data.loanid"),

    /**
     * Data Loan.
     */
    DATA_LOAN("data.loan"),

    /**
     * Data Date.
     */
    DATA_DATE("data.date"),

    /**
     * Data Description.
     */
    DATA_DESC("data.desc"),

    /**
     * Data TransactionType.
     */
    DATA_TRANSTYPE("data.transtype"),

    /**
     * Data Invested.
     */
    DATA_INVESTED("data.invested"),

    /**
     * Data Earnings.
     */
    DATA_EARNINGS("data.earnings"),

    /**
     * Data Holding.
     */
    DATA_HOLDING("data.holding"),

    /**
     * Data LoanBook.
     */
    DATA_LOANBOOK("data.loanBook"),

    /**
     * Data Interest.
     */
    DATA_INTEREST("data.interest"),

    /**
     * Data TaxableEarnings.
     */
    DATA_TAXABLEEARNINGS("data.taxableEarning"),

    /**
     * Data BadDebtInterest.
     */
    DATA_BADDEBTINTEREST("data.badDebtInterest"),

    /**
     * Data BadDebtCapital.
     */
    DATA_BADDEBTCAPITAL("data.badDebtCapital"),

    /**
     * Data Fees.
     */
    DATA_FEES("data.fees"),

    /**
     * Data CashBack.
     */
    DATA_CASHBACK("data.cashback"),

    /**
     * Data Losses.
     */
    DATA_LOSSES("data.losses"),

    /**
     * Data BadDebt.
     */
    DATA_BADDEBT("data.baddebt"),

    /**
     * Data Recovered.
     */
    DATA_RECOVERED("data.recovered"),

    /**
     * Data Total Value.
     */
    DATA_TOTALSOURCEVALUE("data.total.value.source"),

    /**
     * Data Total Value.
     */
    DATA_TOTALASSETVALUE("data.total.value.asset"),

    /**
     * Data Total Invested.
     */
    DATA_TOTALINVESTED("data.total.invested"),

    /**
     * Data Total Holding.
     */
    DATA_TOTALHOLDING("data.total.holding"),

    /**
     * Data Total LoanBook.
     */
    DATA_TOTALLOANBOOK("data.total.loanBook"),

    /**
     * Data Total Earnings.
     */
    DATA_TOTALEARNINGS("data.total.earnings"),

    /**
     * Data Total taxableInterest.
     */
    DATA_TOTALTAXABLEEARNINGS("data.total.earnings.tax"),

    /**
     * Data Total Interest.
     */
    DATA_TOTALINTEREST("data.total.interest"),

    /**
     * Data Total badDebtInterest.
     */
    DATA_TOTALBADDEBTINTEREST("data.total.badDebt.interest"),

    /**
     * Data Total badDebtCapital.
     */
    DATA_TOTALBADDEBTCAPITAL("data.total.badDebt.capital"),

    /**
     * Data Total Fees.
     */
    DATA_TOTALFEES("data.total.fees"),

    /**
     * Data Total CashBack.
     */
    DATA_TOTALCASHBACK("data.total.cashback"),

    /**
     * Data Total Losses.
     */
    DATA_TOTALLOSSES("data.total.losses"),

    /**
     * Data Total BadDebt.
     */
    DATA_TOTALBADDEBT("data.total.baddebt"),

    /**
     * Data Total Recovered.
     */
    DATA_TOTALRECOVERED("data.total.recovered"),

    /**
     * Data StartDate.
     */
    DATA_STARTDATE("data.startdate"),

    /**
     * Data BadDebtDate.
     */
    DATA_BADDEBTDATE("data.baddebtdate"),

    /**
     * Data OriginalLoan.
     */
    DATA_LENT("data.lent"),

    /**
     * Data Balance.
     */
    DATA_BALANCE("data.balance"),

    /**
     * Data Rate.
     */
    DATA_RATE("data.rate"),

    /**
     * Data LastDate.
     */
    DATA_LASTDATE("data.lastdate"),

    /**
     * Data Status.
     */
    DATA_LOANSTATUS("data.loanstatus"),

    /**
     * Data AuctionId.
     */
    DATA_AUCTIONID("data.auctionid"),

    /**
     * Data LoanRisk.
     */
    DATA_LOANRISK("data.loanrisk"),

    /**
     * Data BookItem.
     */
    DATA_BOOKITEM("data.bookitem"),

    /**
     * Data BookItems.
     */
    DATA_BOOKITEMS("data.bookitems"),

    /**
     * Data Missing.
     */
    DATA_MISSING("data.missing"),

    /**
     * Data Missing LoanBook.
     */
    DATA_MISSINGBOOK("data.missing.loanbook"),

    /**
     * Data Missing Payments.
     */
    DATA_MISSINGPAY("data.missing.payments"),

    /**
     * Data Totals.
     */
    DATA_TOTALS("data.totals"),

    /**
     * Data History.
     */
    DATA_HISTORY("data.history"),

    /**
     * Data MonthlyDeltas.
     */
    DATA_MONTHLYDELTAS("data.deltas.monthly"),

    /**
     * Data AnnualDeltas.
     */
    DATA_ANNUALDELTAS("data.deltas.annually"),

    /**
     * Data MonthlyTotals.
     */
    DATA_MONTHLYTOTALS("data.totals.monthly");

    /**
     * The MarketProvider Map.
     */
    private static final Map<CoeusMarketProvider, TethysResourceId> MARKET_MAP = buildMarketMap();

    /**
     * The LoanStatus Map.
     */
    private static final Map<CoeusLoanStatus, TethysResourceId> STATUS_MAP = buildStatusMap();

    /**
     * The LoanRisk Map.
     */
    private static final Map<CoeusLoanRisk, TethysResourceId> RISK_MAP = buildRiskMap();

    /**
     * The TotalSet Map.
     */
    private static final Map<CoeusTotalSet, TethysResourceId> TOTALSET_MAP = buildTotalSetMap();

    /**
     * The TransactionType Map.
     */
    private static final Map<CoeusTransactionType, TethysResourceId> TRANS_MAP = buildTransMap();

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getPackageResourceBuilder(CoeusDataException.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    CoeusResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "coeus";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Build market map.
     * @return the map
     */
    private static Map<CoeusMarketProvider, TethysResourceId> buildMarketMap() {
        /* Create the map and return it */
        Map<CoeusMarketProvider, TethysResourceId> myMap = new EnumMap<>(CoeusMarketProvider.class);
        myMap.put(CoeusMarketProvider.FUNDINGCIRCLE, MARKET_FUNDINGCIRCLE);
        myMap.put(CoeusMarketProvider.LENDINGWORKS, MARKET_LENDINGWORKS);
        myMap.put(CoeusMarketProvider.RATESETTER, MARKET_RATESETTER);
        myMap.put(CoeusMarketProvider.ZOPA, MARKET_ZOPA);
        return myMap;
    }

    /**
     * Obtain key for market.
     * @param pMarket the market
     * @return the resource key
     */
    public static TethysResourceId getKeyForMarket(final CoeusMarketProvider pMarket) {
        return TethysResourceBuilder.getKeyForEnum(MARKET_MAP, pMarket);
    }

    /**
     * Build status map.
     * @return the map
     */
    private static Map<CoeusLoanStatus, TethysResourceId> buildStatusMap() {
        /* Create the map and return it */
        Map<CoeusLoanStatus, TethysResourceId> myMap = new EnumMap<>(CoeusLoanStatus.class);
        myMap.put(CoeusLoanStatus.OFFERED, LOAN_OFFERED);
        myMap.put(CoeusLoanStatus.ACTIVE, LOAN_ACTIVE);
        myMap.put(CoeusLoanStatus.POORLY, LOAN_POORLY);
        myMap.put(CoeusLoanStatus.BADDEBT, LOAN_BADDEBT);
        myMap.put(CoeusLoanStatus.REPAID, LOAN_REPAID);
        myMap.put(CoeusLoanStatus.REJECTED, LOAN_REJECTED);
        return myMap;
    }

    /**
     * Obtain key for loan status.
     * @param pStatus the status
     * @return the resource key
     */
    public static TethysResourceId getKeyForLoanStatus(final CoeusLoanStatus pStatus) {
        return TethysResourceBuilder.getKeyForEnum(STATUS_MAP, pStatus);
    }

    /**
     * Build transaction type map.
     * @return the map
     */
    private static Map<CoeusTransactionType, TethysResourceId> buildTransMap() {
        /* Create the map and return it */
        Map<CoeusTransactionType, TethysResourceId> myMap = new EnumMap<>(CoeusTransactionType.class);
        myMap.put(CoeusTransactionType.TRANSFER, TRANS_TRANSFER);
        myMap.put(CoeusTransactionType.CAPITALLOAN, TRANS_LOAN);
        myMap.put(CoeusTransactionType.CAPITALREPAYMENT, TRANS_REPAYMENT);
        myMap.put(CoeusTransactionType.INTEREST, TRANS_INTEREST);
        myMap.put(CoeusTransactionType.FEES, TRANS_FEES);
        myMap.put(CoeusTransactionType.CASHBACK, TRANS_CASHBACK);
        myMap.put(CoeusTransactionType.RATEPROMISE, TRANS_RATEPROMISE);
        myMap.put(CoeusTransactionType.BUYLOAN, TRANS_BUYLOAN);
        myMap.put(CoeusTransactionType.BADDEBT, TRANS_BADDEBT);
        myMap.put(CoeusTransactionType.RECOVERY, TRANS_RECOVERY);
        myMap.put(CoeusTransactionType.TOTALS, TRANS_TOTALS);
        return myMap;
    }

    /**
     * Obtain key for transaction type.
     * @param pTrans the transactionType
     * @return the resource key
     */
    public static TethysResourceId getKeyForTransType(final CoeusTransactionType pTrans) {
        return TethysResourceBuilder.getKeyForEnum(TRANS_MAP, pTrans);
    }

    /**
     * Build risk map.
     * @return the map
     */
    private static Map<CoeusLoanRisk, TethysResourceId> buildRiskMap() {
        /* Create the map and return it */
        Map<CoeusLoanRisk, TethysResourceId> myMap = new EnumMap<>(CoeusLoanRisk.class);
        myMap.put(CoeusLoanRisk.APLUS, RISK_APLUS);
        myMap.put(CoeusLoanRisk.A, RISK_A);
        myMap.put(CoeusLoanRisk.B, RISK_B);
        myMap.put(CoeusLoanRisk.C, RISK_C);
        myMap.put(CoeusLoanRisk.D, RISK_D);
        myMap.put(CoeusLoanRisk.E, RISK_E);
        myMap.put(CoeusLoanRisk.S, RISK_S);
        myMap.put(CoeusLoanRisk.UNCLASSIFIED, RISK_UNCLASSIFIED);
        return myMap;
    }

    /**
     * Obtain key for risk.
     * @param pRisk the risk
     * @return the resource key
     */
    public static TethysResourceId getKeyForRisk(final CoeusLoanRisk pRisk) {
        return TethysResourceBuilder.getKeyForEnum(RISK_MAP, pRisk);
    }

    /**
     * Build totalSet map.
     * @return the map
     */
    private static Map<CoeusTotalSet, TethysResourceId> buildTotalSetMap() {
        /* Create the map and return it */
        Map<CoeusTotalSet, TethysResourceId> myMap = new EnumMap<>(CoeusTotalSet.class);
        myMap.put(CoeusTotalSet.INVESTED, DATA_INVESTED);
        myMap.put(CoeusTotalSet.EARNINGS, DATA_EARNINGS);
        myMap.put(CoeusTotalSet.TAXABLEEARNINGS, DATA_TAXABLEEARNINGS);
        myMap.put(CoeusTotalSet.INTEREST, DATA_INTEREST);
        myMap.put(CoeusTotalSet.BADDEBTINTEREST, DATA_BADDEBTINTEREST);
        myMap.put(CoeusTotalSet.BADDEBTCAPITAL, DATA_BADDEBTCAPITAL);
        myMap.put(CoeusTotalSet.FEES, DATA_FEES);
        myMap.put(CoeusTotalSet.CASHBACK, DATA_CASHBACK);
        myMap.put(CoeusTotalSet.LOSSES, DATA_LOSSES);
        myMap.put(CoeusTotalSet.BADDEBT, DATA_BADDEBT);
        myMap.put(CoeusTotalSet.RECOVERED, DATA_RECOVERED);
        myMap.put(CoeusTotalSet.HOLDING, DATA_HOLDING);
        myMap.put(CoeusTotalSet.LOANBOOK, DATA_LOANBOOK);
        return myMap;
    }

    /**
     * Obtain key for totalSet.
     * @param pTotalSet the set
     * @return the resource key
     */
    public static TethysResourceId getKeyForTotalSet(final CoeusTotalSet pTotalSet) {
        return TethysResourceBuilder.getKeyForEnum(TOTALSET_MAP, pTotalSet);
    }
}
