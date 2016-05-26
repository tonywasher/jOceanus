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
package net.sourceforge.joceanus.jcoeus.fundingcircle;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * FundingCircle transaction.
 */
public class CoeusFundingCircleTransaction
        implements CoeusTransaction {
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
     * Auction Id.
     */
    private final String theAuctionId;

    /**
     * Loan Id.
     */
    private String theLoanId;

    /**
     * Prefix.
     */
    private String thePrefix;

    /**
     * Paid In.
     */
    private final TethysMoney thePaidIn;

    /**
     * Paid Out.
     */
    private final TethysMoney thePaidOut;

    /**
     * Holding Delta.
     */
    private final TethysMoney theHoldingDelta;

    /**
     * Capital Delta.
     */
    private final TethysMoney theCapitalDelta;

    /**
     * Interest Delta.
     */
    private final TethysMoney theInterestDelta;

    /**
     * Fee Delta.
     */
    private final TethysMoney theFeesDelta;

    /**
     * CashBack Delta.
     */
    private final TethysMoney theCashBackDelta;

    /**
     * BadDebt Delta.
     */
    private final TethysMoney theBadDebtDelta;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    public CoeusFundingCircleTransaction(final CoeusFundingCircleTransactionParser pParser,
                                         final List<String> pFields) throws OceanusException {
        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

        /* Parse the date */
        theDate = pParser.parseDate(myIterator.next());

        /* Obtain description */
        theDesc = myIterator.next();

        /* Determine the transaction type */
        theTransType = determineTransactionType();

        /* Determine the auction id */
        theAuctionId = determineAuctionId();

        /* Parse the values */
        thePaidIn = pParser.parseMoney(myIterator.next());
        thePaidOut = pParser.parseMoney(myIterator.next());

        /* Determine the HoldingDelta */
        theHoldingDelta = new TethysMoney(thePaidIn);
        theHoldingDelta.subtractAmount(thePaidOut);

        /* Determine the Deltas */
        theCapitalDelta = determineCapitalDelta();
        theInterestDelta = determineInterestDelta();
        theFeesDelta = determineFeesDelta();
        theCashBackDelta = determineCashBackDelta();
        theBadDebtDelta = determineBadDebtDelta();

        /* Determine loan ID */
        theLoanId = determineLoanId();
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
        return theLoanId;
    }

    /**
     * Set the loanId
     * @param pLoanId the loan id
     */
    protected void setLoanId(final String pLoanId) {
        theLoanId = pLoanId;
    }

    /**
     * Obtain the auctionId
     * @return the id
     */
    public String getAuctionId() {
        return theAuctionId;
    }

    @Override
    public TethysMoney getHoldingDelta() {
        return theHoldingDelta;
    }

    @Override
    public TethysMoney getCapitalDelta() {
        return theCapitalDelta;
    }

    @Override
    public TethysMoney getInterestDelta() {
        return theInterestDelta;
    }

    @Override
    public TethysMoney getFeesDelta() {
        return theFeesDelta;
    }

    @Override
    public TethysMoney getCashBackDelta() {
        return theCashBackDelta;
    }

    @Override
    public TethysMoney getBadDebtDelta() {
        return theBadDebtDelta;
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
            return CoeusTransactionType.INTEREST;
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
     * determine capital delta.
     * @return the delta
     */
    private TethysMoney determineCapitalDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case CAPITALLOAN:
                return new TethysMoney(thePaidOut);
            case CAPITALREPAYMENT:
                TethysMoney myCapital = new TethysMoney(thePaidIn);
                myCapital.negate();
                return myCapital;
            case BUYLOAN:
                return determineFCBuyLoan(true);
            default:
                myCapital = new TethysMoney(thePaidOut);
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
                return new TethysMoney(thePaidIn);
            case BUYLOAN:
                return determineFCBuyLoan(false);
            default:
                TethysMoney myInterest = new TethysMoney(thePaidOut);
                myInterest.setZero();
                return myInterest;
        }
    }

    /**
     * determine fees delta.
     * @return the delta
     */
    private TethysMoney determineFeesDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case FEES:
                return new TethysMoney(thePaidOut);
            default:
                TethysMoney myFees = new TethysMoney(thePaidOut);
                myFees.setZero();
                return myFees;
        }
    }

    /**
     * determine cashBack delta.
     * @return the delta
     */
    private TethysMoney determineCashBackDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case CASHBACK:
                return new TethysMoney(thePaidIn);
            default:
                TethysMoney myCash = new TethysMoney(thePaidOut);
                myCash.setZero();
                return myCash;
        }
    }

    /**
     * determine badDebt delta.
     * @return the delta
     */
    private TethysMoney determineBadDebtDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case RECOVERY:
                TethysMoney myDebt = new TethysMoney(thePaidIn);
                myDebt.negate();
                return myDebt;
            default:
                myDebt = new TethysMoney(thePaidOut);
                myDebt.setZero();
                return myDebt;
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
                return theDesc.substring(thePrefix.length());
            case RECOVERY:
                return theDesc.substring(thePrefix.length());
            case CAPITALREPAYMENT:
                return theDesc.substring(thePrefix.length());
            default:
                return null;
        }
    }

    /**
     * determine auctionId.
     * @return the id
     */
    private String determineAuctionId() {
        /* Switch on transactionType */
        switch (theTransType) {
            case CAPITALLOAN:
                String myId = theDesc.substring(thePrefix.length());
                int myIndex = myId.indexOf('-');
                return myId.substring(0, myIndex + 1);
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
        TethysMoney myCash = new TethysMoney(thePaidOut);
        String myValue = myCash.toString();

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
}
