/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.fundingcircle;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * FundingCircle transaction.
 */
public final class CoeusFundingCircleTransaction
        extends CoeusTransaction {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusFundingCircleTransaction.class, CoeusTransaction.getBaseFieldSet());

    /**
     * Transfer prefix.
     */
    private static final String PFIX_OPENING = "Lender deposit made ";

    /**
     * Transfer prefix.
     */
    private static final String PFIX_TRANSFER = "Washer ";

    /**
     * Loan prefix.
     */
    private static final String PFIX_LOAN = "Loan offer on ";

    /**
     * Loan Buy prefix.
     */
    private static final String PFIX_LOANBUY = "Loan Part ID ";

    /**
     * Interest prefix.
     */
    private static final String PFIX_INTEREST = "Interest repayment for loan part ";

    /**
     * Early Interest prefix.
     */
    private static final String PFIX_EARLYINTEREST = "Early interest repayment for loan part ";

    /**
     * Capital prefix.
     */
    private static final String PFIX_CAPITAL = "Principal repayment for loan part ";

    /**
     * Early Capital prefix.
     */
    private static final String PFIX_EARLYCAPITAL = "Early principal repayment for loan part ";

    /**
     * Fees prefix.
     */
    private static final String PFIX_FEES = "Lender fee for loan part ";

    /**
     * Fees prefix2.
     */
    private static final String PFIX_FEES2 = "Lender Fee for Loan ID N/A; Loan Part ID ";

    /**
     * Fees prefix3.
     */
    private static final String PFIX_FEES3 = "Servicing fee for Loan ID N/A; Loan Part ID ";

    /**
     * Fees prefix4.
     */
    private static final String PFIX_FEES4 = "Servicing fee for loan part ";

    /**
     * CashBack prefix.
     */
    private static final String PFIX_CASHBACK = "Lender promotional cashback.";

    /**
     * Recovery prefix.
     */
    private static final String PFIX_RECOVERY = "Recovery payment for loan part ";

    /**
     * Recovery prefix2.
     */
    private static final String PFIX_RECOVERY2 = "Principal recovery repayment for loan part ";

    /**
     * Recovery prefix3.
     */
    private static final String PFIX_RECOVERY3 = "Interest recovery repayment for loan part ";

    /**
     * ZERO for BadDebt/CashBack.
     */
    static final TethysMoney ZERO_MONEY = new TethysMoney();

    /**
     * Date of transaction.
     */
    private final TethysDate theDate;

    /**
     * Description of Transaction.
     */
    private final String theDesc;

    /**
     * Type of Transaction.
     */
    private final CoeusTransactionType theTransType;

    /**
     * Loan.
     */
    private final CoeusFundingCircleLoan theLoan;

    /**
     * Prefix.
     */
    private String thePrefix;

    /**
     * Invested.
     */
    private final TethysMoney theInvested;

    /**
     * Holding.
     */
    private final TethysMoney theHolding;

    /**
     * LoanBook.
     */
    private final TethysMoney theLoanBook;

    /**
     * Interest.
     */
    private final TethysMoney theInterest;

    /**
     * Fees.
     */
    private final TethysMoney theFees;

    /**
     * CashBack.
     */
    private final TethysMoney theCashBack;

    /**
     * BadDebt.
     */
    private final TethysMoney theBadDebt;

    /**
     * Recovered.
     */
    private final TethysMoney theRecovered;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    protected CoeusFundingCircleTransaction(final CoeusFundingCircleTransactionParser pParser,
                                            final List<String> pFields) throws OceanusException {
        /* Initialise underlying class */
        super(pParser.getMarket());

        /* Iterate through the fields */
        final Iterator<String> myIterator = pFields.iterator();

        /* Parse the date */
        theDate = pParser.parseDate(myIterator.next());

        /* Obtain description */
        theDesc = myIterator.next();

        /* Determine the transaction type */
        theTransType = determineTransactionType();

        /* Determine the loan */
        theLoan = determineLoan();

        /* Parse the values */
        final TethysMoney myPaidIn = pParser.parseMoney(myIterator.next());
        final TethysMoney myPaidOut = pParser.parseMoney(myIterator.next());

        /* Determine the HoldingDelta */
        theHolding = new TethysMoney(myPaidIn);
        theHolding.subtractAmount(myPaidOut);

        /* Determine the Deltas */
        theInvested = determineInvestedDelta();
        theLoanBook = determineLoanBookDelta();
        theInterest = determineInterestDelta();
        theFees = determineFeesDelta();
        theCashBack = determineCashBackDelta();
        theBadDebt = determineBadDebtDelta();
        theRecovered = determineRecoveredDelta();

        /* Check transaction validity */
        checkValidity();
    }

    /**
     * BadDebt Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    protected CoeusFundingCircleTransaction(final CoeusFundingCircleBadDebtParser pParser,
                                            final List<String> pFields) throws OceanusException {
        /* Initialise underlying class */
        super(pParser.getMarket());

        /* Iterate through the fields */
        final Iterator<String> myIterator = pFields.iterator();

        /* Access loan id */
        final String myLoanId = myIterator.next();

        /* Skip description/sector/auctionID/risk/Payments left */
        myIterator.next();
        myIterator.next();
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Parse the outstanding balance */
        final TethysMoney myDebt = pParser.parseMoney(myIterator.next());
        myDebt.negate();

        /* Ignore the rate/date of next payment/status */
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Parse the date */
        theDate = pParser.parseDate(myIterator.next());

        /* Obtain description */
        theDesc = CoeusTransactionType.BADDEBT.toString();

        /* Determine the transaction type */
        theTransType = CoeusTransactionType.BADDEBT;

        /* Determine the loan and record the badDebt date */
        theLoan = getMarket().findLoanById(myLoanId);
        theLoan.setBadDebtDate(theDate);

        /* Determine the BadDebt Deltas */
        theBadDebt = myDebt;
        theLoanBook = myDebt;

        /* Set other values to zero */
        theHolding = ZERO_MONEY;
        theInvested = ZERO_MONEY;
        theInterest = ZERO_MONEY;
        theFees = ZERO_MONEY;
        theCashBack = ZERO_MONEY;
        theRecovered = ZERO_MONEY;

        /* Check transaction validity */
        checkValidity();
    }

    /**
     * Check validity.
     * @throws OceanusException on error
     */
    private void checkValidity() throws OceanusException {
        /* Obtain the holding */
        final TethysMoney myMoney = new TethysMoney(theHolding);

        /* Add LoanBook */
        myMoney.addAmount(theLoanBook);

        /* Subtract the invested, cashBack and interest etc. */
        myMoney.subtractAmount(theInterest);
        myMoney.subtractAmount(theFees);
        myMoney.subtractAmount(theInvested);
        myMoney.subtractAmount(theCashBack);
        myMoney.subtractAmount(theBadDebt);
        myMoney.subtractAmount(theRecovered);

        /* We should now be zero */
        if (myMoney.isNonZero()) {
            throw new CoeusDataException(this, "Invalid transaction");
        }

        /* Check that capital is only changed on a loan */
        if (theLoanBook.isNonZero()
            && theLoan == null) {
            throw new CoeusDataException(this, "Capital changed on non-loan");
        }
    }

    @Override
    public CoeusFundingCircleMarket getMarket() {
        return (CoeusFundingCircleMarket) super.getMarket();
    }

    @Override
    public TethysDate getDate() {
        return theDate;
    }

    @Override
    public String getDescription() {
        return theDesc;
    }

    @Override
    public CoeusTransactionType getTransType() {
        return theTransType;
    }

    @Override
    public String getLoanId() {
        return theLoan == null
                               ? null
                               : theLoan.getLoanId();
    }

    @Override
    public CoeusFundingCircleLoan getLoan() {
        return theLoan;
    }

    @Override
    public TethysMoney getInvested() {
        return theInvested;
    }

    @Override
    public TethysMoney getHolding() {
        return theHolding;
    }

    @Override
    public TethysMoney getLoanBook() {
        return theLoanBook;
    }

    @Override
    public TethysMoney getInterest() {
        return theInterest;
    }

    @Override
    public TethysMoney getBadDebtInterest() {
        if (theLoan == null
            || theLoan.isBadDebtCapital()) {
            return ZERO_MONEY;
        }
        return CoeusTransactionType.RECOVERY.equals(theTransType)
                                                                  ? theRecovered
                                                                  : theBadDebt;
    }

    @Override
    public TethysMoney getBadDebtCapital() {
        if (theLoan == null
            || theLoan.isBadDebtCapital()) {
            return ZERO_MONEY;
        }
        return CoeusTransactionType.RECOVERY.equals(theTransType)
                                                                  ? theRecovered
                                                                  : theBadDebt;
    }

    @Override
    public TethysMoney getFees() {
        return theFees;
    }

    @Override
    public TethysMoney getCashBack() {
        return theCashBack;
    }

    @Override
    public TethysMoney getBadDebt() {
        return theBadDebt;
    }

    @Override
    public TethysMoney getRecovered() {
        return theRecovered;
    }

    /**
     * Determine transaction Type.
     * @return the transaction type
     * @throws OceanusException on error
     */
    private CoeusTransactionType determineTransactionType() throws OceanusException {
        /* If the description is Loan disbursal */
        if (theDesc.startsWith(PFIX_LOAN)) {
            thePrefix = PFIX_LOAN;
            return CoeusTransactionType.CAPITALLOAN;
        }

        /* If the description is Loan Buy */
        if (theDesc.startsWith(PFIX_LOANBUY)) {
            thePrefix = PFIX_LOANBUY;
            return CoeusTransactionType.BUYLOAN;
        }

        /* If the description is BankTransfer */
        if (PFIX_OPENING.equalsIgnoreCase(theDesc.substring(0, PFIX_OPENING.length()))
            || PFIX_TRANSFER.equalsIgnoreCase(theDesc.substring(0, PFIX_TRANSFER.length()))) {
            return CoeusTransactionType.TRANSFER;
        }

        /* If the description is Capital payment */
        if (theDesc.startsWith(PFIX_CAPITAL)) {
            thePrefix = PFIX_CAPITAL;
            return CoeusTransactionType.CAPITALREPAYMENT;
        }

        /* If the description is Early Capital payment */
        if (theDesc.startsWith(PFIX_EARLYCAPITAL)) {
            thePrefix = PFIX_EARLYCAPITAL;
            return CoeusTransactionType.CAPITALREPAYMENT;
        }

        /* If the description is Interest */
        if (theDesc.startsWith(PFIX_INTEREST)) {
            thePrefix = PFIX_INTEREST;
            return CoeusTransactionType.INTEREST;
        }

        /* If the description is Early Interest */
        if (theDesc.startsWith(PFIX_EARLYINTEREST)) {
            thePrefix = PFIX_EARLYINTEREST;
            return CoeusTransactionType.INTEREST;
        }

        /* If the description is Recovery */
        if (theDesc.startsWith(PFIX_RECOVERY)) {
            thePrefix = PFIX_RECOVERY;
            return CoeusTransactionType.RECOVERY;
        }

        /* If the description is Recovery2 */
        if (theDesc.startsWith(PFIX_RECOVERY2)) {
            thePrefix = PFIX_RECOVERY2;
            return CoeusTransactionType.RECOVERY;
        }

        /* If the description is Recovery3 */
        if (theDesc.startsWith(PFIX_RECOVERY3)) {
            thePrefix = PFIX_RECOVERY3;
            return CoeusTransactionType.RECOVERY;
        }

        /* If the description is CashBack */
        if (PFIX_CASHBACK.equals(theDesc)) {
            return CoeusTransactionType.CASHBACK;
        }

        /* If the description is Fees */
        if (theDesc.startsWith(PFIX_FEES)) {
            thePrefix = PFIX_FEES;
            return CoeusTransactionType.FEES;
        }

        /* If the description is Fees2 */
        if (theDesc.startsWith(PFIX_FEES2)) {
            thePrefix = PFIX_FEES2;
            return CoeusTransactionType.FEES;
        }

        /* If the description is Fees3 */
        if (theDesc.startsWith(PFIX_FEES3)) {
            thePrefix = PFIX_FEES3;
            return CoeusTransactionType.FEES;
        }

        /* If the description is Fees4 */
        if (theDesc.startsWith(PFIX_FEES4)) {
            thePrefix = PFIX_FEES4;
            return CoeusTransactionType.FEES;
        }

        /* Not recognised */
        throw new CoeusDataException("Unrecognised transaction");
    }

    /**
     * determine invested delta.
     * @return the delta
     */
    private TethysMoney determineInvestedDelta() {
        /* Obtain change in holding account */
        final TethysMoney myInvested = new TethysMoney(theHolding);

        /* Invested are increased by any increase the holding account */
        if (!CoeusTransactionType.TRANSFER.equals(theTransType)) {
            myInvested.setZero();
        }

        /* Return the Invested */
        return myInvested;
    }

    /**
     * determine loanBook delta.
     * @return the delta
     */
    private TethysMoney determineLoanBookDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case CAPITALLOAN:
            case CAPITALREPAYMENT:
                TethysMoney myCapital = new TethysMoney(theHolding);
                myCapital.negate();
                return myCapital;
            case BUYLOAN:
                return determineFCBuyLoan(true);
            default:
                myCapital = new TethysMoney(theHolding);
                myCapital.setZero();
                return myCapital;
        }
    }

    /**
     * determine interest delta.
     * @return the delta
     */
    private TethysMoney determineInterestDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case INTEREST:
                return new TethysMoney(theHolding);
            case BUYLOAN:
                return determineFCBuyLoan(false);
            default:
                final TethysMoney myInterest = new TethysMoney(theHolding);
                myInterest.setZero();
                return myInterest;
        }
    }

    /**
     * determine fees delta.
     * @return the delta
     */
    private TethysMoney determineFeesDelta() {
        /* Obtain change in holding account */
        final TethysMoney myFees = new TethysMoney(theHolding);

        /* Fees are increased by any decrease the holding account */
        if (!CoeusTransactionType.FEES.equals(theTransType)) {
            myFees.setZero();
        }

        /* Return the Fees */
        return myFees;
    }

    /**
     * determine cashBack delta.
     * @return the delta
     */
    private TethysMoney determineCashBackDelta() {
        /* Obtain change in holding account */
        final TethysMoney myCash = new TethysMoney(theHolding);

        /* CashBack is increased by any increase in the holding account */
        if (!CoeusTransactionType.CASHBACK.equals(theTransType)) {
            myCash.setZero();
        }

        /* Return the CashBack */
        return myCash;
    }

    /**
     * determine badDebt delta.
     * @return the delta
     */
    private TethysMoney determineBadDebtDelta() {
        /* Obtain change in holding account */
        final TethysMoney myDebt = new TethysMoney(theHolding);
        myDebt.setZero();

        /* Return the debt */
        return myDebt;
    }

    /**
     * determine recovered delta.
     * @return the delta
     */
    private TethysMoney determineRecoveredDelta() {
        /* Obtain change in holding account */
        final TethysMoney myRecovered = new TethysMoney(theHolding);

        /* Recovery is increased by any increase in the holding account */
        if (!CoeusTransactionType.RECOVERY.equals(theTransType)) {
            myRecovered.setZero();
        }

        /* Return the Recovery */
        return myRecovered;
    }

    /**
     * determine loan.
     * @return the loan
     * @throws OceanusException on error
     */
    private CoeusFundingCircleLoan determineLoan() throws OceanusException {
        /* Obtain the market */
        final CoeusFundingCircleMarket myMarket = getMarket();

        /* If this is a CapitalLoan */
        if (CoeusTransactionType.CAPITALLOAN.equals(theTransType)) {
            /* Look up the loan via the auctionId */
            final String myAuctionId = theDesc.substring(thePrefix.length());
            final int myIndex = myAuctionId.lastIndexOf('-');
            return myMarket.findLoanByAuctionId(myAuctionId.substring(myIndex + 2));
        } else {
            /* Determine the loan id */
            final String myLoanId = determineLoanId();
            return myLoanId == null
                                    ? null
                                    : myMarket.findLoanById(myLoanId);
        }
    }

    /**
     * determine loanId.
     * @return the id
     */
    private String determineLoanId() {
        /* Switch on transactionType */
        switch (theTransType) {
            case FEES:
                String myId = theDesc.substring(thePrefix.length());
                int myIndex = myId.indexOf(';');
                return myIndex == -1
                                     ? myId
                                     : myId.substring(0, myIndex);
            case BUYLOAN:
                myId = theDesc.substring(thePrefix.length());
                myIndex = myId.indexOf(" : ");
                return myId.substring(0, myIndex);
            case INTEREST:
            case RECOVERY:
            case CAPITALREPAYMENT:
                return theDesc.substring(thePrefix.length());
            default:
                return null;
        }
    }

    /**
     * determine FCBuyLoan value.
     * @param pCapital are we looking for capital rather than interest
     * @return the value
     */
    private TethysMoney determineFCBuyLoan(final boolean pCapital) {
        /* Strip off the prefix */
        int myIndex = theDesc.indexOf(':');
        String myPrincipal = theDesc.substring(myIndex + 2);

        /* Strip off interest from Principal */
        myIndex = myPrincipal.indexOf(',');
        String myInterest = myPrincipal.substring(myIndex + 2);

        /* Isolate principal */
        myPrincipal = myPrincipal.substring(0, myIndex);
        myIndex = myPrincipal.indexOf(' ');
        myPrincipal = myPrincipal.substring(myIndex + 1);

        /* Isolate index */
        myIndex = myInterest.indexOf(',');
        myInterest = myInterest.substring(0, myIndex);
        myIndex = myInterest.indexOf(' ');
        myInterest = myInterest.substring(myIndex + 1);

        /* Access cash value */
        final TethysMoney myCash = new TethysMoney(theHolding);
        myCash.negate();
        final String myValue = myCash.toString();

        /* If we are looking at the Principal */
        if (pCapital) {
            /* Determine whether this is the principal entry */
            if (!myPrincipal.equals(myValue)) {
                myCash.setZero();
            }

            /* Else looking at interest */
        } else {
            /* Determine whether this is the interest entry */
            if (myInterest.equals(myValue)) {
                myCash.negate();
            } else {
                myCash.setZero();
            }
        }

        /* Return value */
        return myCash;
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }
}
