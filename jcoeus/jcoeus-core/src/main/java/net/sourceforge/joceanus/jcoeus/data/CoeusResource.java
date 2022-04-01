/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jcoeus.data;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for Coeus.
 */
public enum CoeusResource
        implements TethysBundleId, MetisDataFieldId {
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
     * SnapShot Market.
     */
    MARKETTYPE_SNAPSHOT("marketType.snapShot"),

    /**
     * Annual Market.
     */
    MARKETTYPE_ANNUAL("marketType.annual"),

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
     * Transaction TaxableCashBack.
     */
    TRANS_TAXCASHBACK("trans.taxcashback"),

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
    DATA_LOANS("data.loans"),

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
     * Data MarketCache.
     */
    DATA_MARKETCACHE("data.marketCache"),

    /**
     * Data MarketSet.
     */
    DATA_MARKETSET("data.marketSet"),

    /**
     * Data SnapShotMap.
     */
    DATA_SNAPSHOTMAP("data.snapShotMap"),

    /**
     * Data MarketSet.
     */
    DATA_ANNUALMAP("data.annualMap"),

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
     * Data NettInterest.
     */
    DATA_NETTINTEREST("data.nettInterest"),

    /**
     * Data TaxableEarnings.
     */
    DATA_TAXABLEEARNINGS("data.taxableEarnings"),

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
     * Data Shield.
     */
    DATA_SHIELD("data.shield"),

    /**
     * Data CashBack.
     */
    DATA_CASHBACK("data.cashback"),

    /**
     * Data XferPayment.
     */
    DATA_XFERPAYMENT("data.xferpayment"),

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
     * Data Transaction.
     */
    DATA_TRANSACTION("data.transaction"),

    /**
     * Data SourceValue.
     */
    DATA_SOURCEVALUE("data.value.source"),

    /**
     * Data AssetValue.
     */
    DATA_ASSETVALUE("data.value.asset"),

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
     * Data Loan Rate Of Return.
     */
    DATA_LOANROR("data.loanror"),

    /**
     * Data Asset Rate Of Return.
     */
    DATA_ASSETROR("data.assetror"),

    /**
     * Data Delta.
     */
    DATA_DELTA("data.delta"),

    /**
     * Data Rate.
     */
    DATA_RATE("data.rate"),

    /**
     * Data LastDate.
     */
    DATA_LASTDATE("data.lastdate"),

    /**
     * Data Range.
     */
    DATA_RANGE("data.range"),

    /**
     * Initial.
     */
    DATA_INITIAL("data.initial"),

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
     * Data Capital.
     */
    DATA_CAPITAL("data.capital"),

    /**
     * Data Repaid.
     */
    DATA_REPAID("data.repaid"),

    /**
     * Data Missing.
     */
    DATA_MISSING("data.missing"),

    /**
     * Data Missing LoanBook.
     */
    DATA_MISSINGBOOK("data.missing.loanbook"),

    /**
     * Data Missing Capital.
     */
    DATA_MISSINGCAPITAL("data.missing.capital"),

    /**
     * Data Missing Payments.
     */
    DATA_MISSINGINTEREST("data.missing.interest"),

    /**
     * Data Zombie Loans.
     */
    DATA_ZOMBIELOANS("data.zombie.loans"),

    /**
     * Data Interesting Loans.
     */
    DATA_INTERESTINGLOANS("data.interesting.loans"),

    /**
     * Data Totals.
     */
    DATA_TOTALS("data.totals"),

    /**
     * Data History.
     */
    DATA_HISTORY("data.history"),

    /**
     * Data MonthlyTotals.
     */
    DATA_MONTHLYTOTALS("data.monthly");

    /**
     * The MarketProvider Map.
     */
    private static final Map<CoeusMarketProvider, TethysBundleId> MARKET_MAP = buildMarketMap();

    /**
     * The MarketType Map.
     */
    private static final Map<CoeusMarketType, TethysBundleId> MARKETTYPE_MAP = buildMarketTypeMap();

    /**
     * The LoanStatus Map.
     */
    private static final Map<CoeusLoanStatus, TethysBundleId> STATUS_MAP = buildStatusMap();

    /**
     * The LoanRisk Map.
     */
    private static final Map<CoeusLoanRisk, TethysBundleId> RISK_MAP = buildRiskMap();

    /**
     * The TotalSet Map.
     */
    private static final Map<CoeusTotalSet, TethysBundleId> TOTALSET_MAP = buildTotalSetMap();

    /**
     * The TransactionType Map.
     */
    private static final Map<CoeusTransactionType, TethysBundleId> TRANS_MAP = buildTransMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(CoeusDataException.class.getCanonicalName(),
            ResourceBundle::getBundle);

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
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }

    /**
     * Build market map.
     * @return the map
     */
    private static Map<CoeusMarketProvider, TethysBundleId> buildMarketMap() {
        /* Create the map and return it */
        final Map<CoeusMarketProvider, TethysBundleId> myMap = new EnumMap<>(CoeusMarketProvider.class);
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
    public static TethysBundleId getKeyForMarket(final CoeusMarketProvider pMarket) {
        return TethysBundleLoader.getKeyForEnum(MARKET_MAP, pMarket);
    }

    /**
     * Build marketType map.
     * @return the map
     */
    private static Map<CoeusMarketType, TethysBundleId> buildMarketTypeMap() {
        /* Create the map and return it */
        final Map<CoeusMarketType, TethysBundleId> myMap = new EnumMap<>(CoeusMarketType.class);
        myMap.put(CoeusMarketType.SNAPSHOT, MARKETTYPE_SNAPSHOT);
        myMap.put(CoeusMarketType.ANNUAL, MARKETTYPE_ANNUAL);
        return myMap;
    }

    /**
     * Obtain key for marketType.
     * @param pMarketType the marketType
     * @return the resource key
     */
    public static TethysBundleId getKeyForMarketType(final CoeusMarketType pMarketType) {
        return TethysBundleLoader.getKeyForEnum(MARKETTYPE_MAP, pMarketType);
    }

    /**
     * Build status map.
     * @return the map
     */
    private static Map<CoeusLoanStatus, TethysBundleId> buildStatusMap() {
        /* Create the map and return it */
        final Map<CoeusLoanStatus, TethysBundleId> myMap = new EnumMap<>(CoeusLoanStatus.class);
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
    public static TethysBundleId getKeyForLoanStatus(final CoeusLoanStatus pStatus) {
        return TethysBundleLoader.getKeyForEnum(STATUS_MAP, pStatus);
    }

    /**
     * Build transaction type map.
     * @return the map
     */
    private static Map<CoeusTransactionType, TethysBundleId> buildTransMap() {
        /* Create the map and return it */
        final Map<CoeusTransactionType, TethysBundleId> myMap = new EnumMap<>(CoeusTransactionType.class);
        myMap.put(CoeusTransactionType.TRANSFER, TRANS_TRANSFER);
        myMap.put(CoeusTransactionType.CAPITALLOAN, TRANS_LOAN);
        myMap.put(CoeusTransactionType.CAPITALREPAYMENT, TRANS_REPAYMENT);
        myMap.put(CoeusTransactionType.INTEREST, TRANS_INTEREST);
        myMap.put(CoeusTransactionType.FEES, TRANS_FEES);
        myMap.put(CoeusTransactionType.SHIELD, DATA_SHIELD);
        myMap.put(CoeusTransactionType.CASHBACK, TRANS_CASHBACK);
        myMap.put(CoeusTransactionType.RATEPROMISE, TRANS_RATEPROMISE);
        myMap.put(CoeusTransactionType.BUYLOAN, TRANS_BUYLOAN);
        myMap.put(CoeusTransactionType.BADDEBT, TRANS_BADDEBT);
        myMap.put(CoeusTransactionType.RECOVERY, TRANS_RECOVERY);
        myMap.put(CoeusTransactionType.TOTALS, TRANS_TOTALS);
        myMap.put(CoeusTransactionType.TAXABLECASHBACK, TRANS_TAXCASHBACK);
        return myMap;
    }

    /**
     * Obtain key for transaction type.
     * @param pTrans the transactionType
     * @return the resource key
     */
    public static TethysBundleId getKeyForTransType(final CoeusTransactionType pTrans) {
        return TethysBundleLoader.getKeyForEnum(TRANS_MAP, pTrans);
    }

    /**
     * Build risk map.
     * @return the map
     */
    private static Map<CoeusLoanRisk, TethysBundleId> buildRiskMap() {
        /* Create the map and return it */
        final Map<CoeusLoanRisk, TethysBundleId> myMap = new EnumMap<>(CoeusLoanRisk.class);
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
    public static TethysBundleId getKeyForRisk(final CoeusLoanRisk pRisk) {
        return TethysBundleLoader.getKeyForEnum(RISK_MAP, pRisk);
    }

    /**
     * Build totalSet map.
     * @return the map
     */
    private static Map<CoeusTotalSet, TethysBundleId> buildTotalSetMap() {
        /* Create the map and return it */
        final Map<CoeusTotalSet, TethysBundleId> myMap = new EnumMap<>(CoeusTotalSet.class);
        myMap.put(CoeusTotalSet.INVESTED, DATA_INVESTED);
        myMap.put(CoeusTotalSet.EARNINGS, DATA_EARNINGS);
        myMap.put(CoeusTotalSet.TAXABLEEARNINGS, DATA_TAXABLEEARNINGS);
        myMap.put(CoeusTotalSet.INTEREST, DATA_INTEREST);
        myMap.put(CoeusTotalSet.NETTINTEREST, DATA_NETTINTEREST);
        myMap.put(CoeusTotalSet.BADDEBTINTEREST, DATA_BADDEBTINTEREST);
        myMap.put(CoeusTotalSet.BADDEBTCAPITAL, DATA_BADDEBTCAPITAL);
        myMap.put(CoeusTotalSet.FEES, DATA_FEES);
        myMap.put(CoeusTotalSet.SHIELD, DATA_SHIELD);
        myMap.put(CoeusTotalSet.XFERPAYMENT, DATA_XFERPAYMENT);
        myMap.put(CoeusTotalSet.CASHBACK, DATA_CASHBACK);
        myMap.put(CoeusTotalSet.LOSSES, DATA_LOSSES);
        myMap.put(CoeusTotalSet.BADDEBT, DATA_BADDEBT);
        myMap.put(CoeusTotalSet.RECOVERED, DATA_RECOVERED);
        myMap.put(CoeusTotalSet.HOLDING, DATA_HOLDING);
        myMap.put(CoeusTotalSet.LOANBOOK, DATA_LOANBOOK);
        myMap.put(CoeusTotalSet.LOANROR, DATA_LOANROR);
        myMap.put(CoeusTotalSet.ASSETROR, DATA_ASSETROR);
        return myMap;
    }

    /**
     * Obtain key for totalSet.
     * @param pTotalSet the set
     * @return the resource key
     */
    public static TethysBundleId getKeyForTotalSet(final CoeusTotalSet pTotalSet) {
        return TethysBundleLoader.getKeyForEnum(TOTALSET_MAP, pTotalSet);
    }
}
