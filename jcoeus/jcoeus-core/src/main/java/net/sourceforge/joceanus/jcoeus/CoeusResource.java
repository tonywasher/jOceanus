/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jcoeus;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanRisk;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
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
     * Loan is BadDebt.
     */
    LOAN_BADDEBT("loan.baddebt"),

    /**
     * Loan RePaid.
     */
    LOAN_REPAID("loan.repaid"),

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
     * Transaction Recovery.
     */
    TRANS_RECOVERY("trans.recovery"),

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
    RISK_UNCLASSIFIED("risk.a");

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
     * Obtain key for market.
     * @param pMarket the market
     * @return the resource key
     */
    public static TethysResourceId getKeyForMarket(final CoeusLoanMarketProvider pMarket) {
        switch (pMarket) {
            case FUNDINGCIRCLE:
                return MARKET_FUNDINGCIRCLE;
            case RATESETTER:
                return MARKET_RATESETTER;
            case ZOPA:
                return MARKET_ZOPA;
            default:
                throw new IllegalArgumentException(TethysResourceBuilder.getErrorNoResource(pMarket));
        }
    }

    /**
     * Obtain key for loan status.
     * @param pStatus the status
     * @return the resource key
     */
    public static TethysResourceId getKeyForLoanStatus(final CoeusLoanStatus pStatus) {
        switch (pStatus) {
            case ACTIVE:
                return LOAN_ACTIVE;
            case BADDEBT:
                return LOAN_BADDEBT;
            case REPAID:
                return LOAN_REPAID;
            default:
                throw new IllegalArgumentException(TethysResourceBuilder.getErrorNoResource(pStatus));
        }
    }

    /**
     * Obtain key for transaction type.
     * @param pTrans the transactionType
     * @return the resource key
     */
    public static TethysResourceId getKeyForTransType(final CoeusTransactionType pTrans) {
        switch (pTrans) {
            case TRANSFER:
                return TRANS_TRANSFER;
            case CAPITALLOAN:
                return TRANS_LOAN;
            case CAPITALREPAYMENT:
                return TRANS_REPAYMENT;
            case INTEREST:
                return TRANS_INTEREST;
            case FEES:
                return TRANS_FEES;
            case CASHBACK:
                return TRANS_CASHBACK;
            case RATEPROMISE:
                return TRANS_RATEPROMISE;
            case BUYLOAN:
                return TRANS_BUYLOAN;
            case RECOVERY:
                return TRANS_RECOVERY;
            default:
                throw new IllegalArgumentException(TethysResourceBuilder.getErrorNoResource(pTrans));
        }
    }

    /**
     * Obtain key for risk.
     * @param pRisk the risk
     * @return the resource key
     */
    public static TethysResourceId getKeyForRisk(final CoeusLoanRisk pRisk) {
        switch (pRisk) {
            case APLUS:
                return RISK_APLUS;
            case A:
                return RISK_A;
            case B:
                return RISK_B;
            case C:
                return RISK_C;
            case D:
                return RISK_D;
            case E:
                return RISK_E;
            case S:
                return RISK_S;
            case UNCLASSIFIED:
                return RISK_UNCLASSIFIED;
            default:
                throw new IllegalArgumentException(TethysResourceBuilder.getErrorNoResource(pRisk));
        }
    }
}
