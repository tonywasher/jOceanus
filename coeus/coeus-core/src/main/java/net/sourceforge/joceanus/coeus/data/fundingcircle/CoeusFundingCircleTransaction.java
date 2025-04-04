/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.coeus.data.fundingcircle;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.coeus.exc.CoeusDataException;
import net.sourceforge.joceanus.coeus.data.CoeusTransaction;
import net.sourceforge.joceanus.coeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * FundingCircle transaction.
 */
public final class CoeusFundingCircleTransaction
        extends CoeusTransaction {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusFundingCircleTransaction> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusFundingCircleTransaction.class);

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
     * Recovery prefix4.
     */
    private static final String PFIX_RECOVERY4 = "Additional principal recovery payment for loan part ";

    /**
     * XferIn Prefix.
     */
    private static final String PFIX_XFERPAY = "Transfer Payment ";

    /**
     * XferOut Prefix.
     */
    private static final String PFIX_XFEROUT = "FC Len Withdrawal";

    /**
     * DEBTSale Suffix.
     */
    private static final String SFIX_DEBTSALE = " DEBT SALE";

    /**
     * ZERO for BadDebt/CashBack.
     */
    static final OceanusMoney ZERO_MONEY = new OceanusMoney();

    /**
     * Date of transaction.
     */
    private final OceanusDate theDate;

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
    private final OceanusMoney theInvested;

    /**
     * Holding.
     */
    private final OceanusMoney theHolding;

    /**
     * LoanBook.
     */
    private final OceanusMoney theLoanBook;

    /**
     * Interest.
     */
    private final OceanusMoney theInterest;

    /**
     * XferPayment.
     */
    private final OceanusMoney theXferPayment;

    /**
     * Fees.
     */
    private final OceanusMoney theFees;

    /**
     * CashBack.
     */
    private final OceanusMoney theCashBack;

    /**
     * BadDebt.
     */
    private final OceanusMoney theBadDebt;

    /**
     * Recovered.
     */
    private final OceanusMoney theRecovered;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    CoeusFundingCircleTransaction(final CoeusFundingCircleTransactionParser pParser,
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
        final OceanusMoney myPaidIn = pParser.parseMoney(myIterator.next());
        final OceanusMoney myPaidOut = pParser.parseMoney(myIterator.next());

        /* Determine the HoldingDelta */
        theHolding = new OceanusMoney(myPaidIn);
        theHolding.subtractAmount(myPaidOut);

        /* Determine the Deltas */
        theInvested = determineInvestedDelta();
        theLoanBook = determineLoanBookDelta(pParser);
        theInterest = determineInterestDelta(pParser);
        theXferPayment = determineXferDelta(pParser);
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
    CoeusFundingCircleTransaction(final CoeusFundingCircleBadDebtParser pParser,
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
        final OceanusMoney myDebt = pParser.parseMoney(myIterator.next());
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
        theXferPayment = ZERO_MONEY;
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
        final OceanusMoney myMoney = new OceanusMoney(theHolding);

        /* Add LoanBook */
        myMoney.addAmount(theLoanBook);

        /* Subtract the invested, cashBack and interest etc. */
        myMoney.subtractAmount(theInterest);
        myMoney.subtractAmount(theFees);
        myMoney.subtractAmount(theInvested);
        myMoney.subtractAmount(theXferPayment);
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
    public OceanusDate getDate() {
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
    public OceanusMoney getInvested() {
        return theInvested;
    }

    @Override
    public OceanusMoney getHolding() {
        return theHolding;
    }

    @Override
    public OceanusMoney getLoanBook() {
        return theLoanBook;
    }

    @Override
    public OceanusMoney getInterest() {
        return theInterest;
    }

    @Override
    public OceanusMoney getBadDebtInterest() {
        if (theLoan == null
            || theLoan.isBadDebtCapital()) {
            return ZERO_MONEY;
        }
        return CoeusTransactionType.RECOVERY.equals(theTransType)
                                                                  ? theRecovered
                                                                  : theBadDebt;
    }

    @Override
    public OceanusMoney getBadDebtCapital() {
        if (theLoan == null
            || !theLoan.isBadDebtCapital()) {
            return ZERO_MONEY;
        }
        return CoeusTransactionType.RECOVERY.equals(theTransType)
                                                                  ? theRecovered
                                                                  : theBadDebt;
    }

    @Override
    public OceanusMoney getFees() {
        return theFees;
    }

    @Override
    public OceanusMoney getShield() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusMoney getCashBack() {
        return theCashBack;
    }

    @Override
    public OceanusMoney getXferPayment() {
        return theXferPayment;
    }

    @Override
    public OceanusMoney getBadDebt() {
        return theBadDebt;
    }

    @Override
    public OceanusMoney getRecovered() {
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
        if (theDesc.startsWith(PFIX_OPENING)
                || PFIX_XFEROUT.equals(theDesc)) {
            return CoeusTransactionType.TRANSFER;
        }
        if (theDesc.length() >= PFIX_TRANSFER.length()
            && PFIX_TRANSFER.equalsIgnoreCase(theDesc.substring(0, PFIX_TRANSFER.length()))) {
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

        /* If the description is Recovery4 */
        if (theDesc.startsWith(PFIX_RECOVERY4)) {
            thePrefix = PFIX_RECOVERY4;
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
    private OceanusMoney determineInvestedDelta() {
        /* Obtain change in holding account */
        final OceanusMoney myInvested = new OceanusMoney(theHolding);

        /* Invested are increased by any increase the holding account */
        if (!CoeusTransactionType.TRANSFER.equals(theTransType)) {
            myInvested.setZero();
        }

        /* Return the Invested */
        return myInvested;
    }

    /**
     * determine loanBook delta.
     * @param pParser the parser
     * @return the delta
     * @throws OceanusException on error
     */
    private OceanusMoney determineLoanBookDelta(final CoeusFundingCircleTransactionParser pParser) throws OceanusException {
        /* Switch on transactionType */
        switch (theTransType) {
            case CAPITALLOAN:
            case CAPITALREPAYMENT:
                OceanusMoney myCapital = new OceanusMoney(theHolding);
                myCapital.negate();
                return myCapital;
            case BUYLOAN:
                return determineFCBuyLoan(pParser, FCLoan.CAPITAL);
            default:
                myCapital = new OceanusMoney(theHolding);
                myCapital.setZero();
                return myCapital;
        }
    }

    /**
     * determine interest delta.
     * @param pParser the parser
     * @return the delta
     * @throws OceanusException on error
     */
    private OceanusMoney determineInterestDelta(final CoeusFundingCircleTransactionParser pParser) throws OceanusException {
        /* Switch on transactionType */
        switch (theTransType) {
            case INTEREST:
                return new OceanusMoney(theHolding);
            case BUYLOAN:
                return determineFCBuyLoan(pParser, FCLoan.INTEREST);
            default:
                final OceanusMoney myInterest = new OceanusMoney(theHolding);
                myInterest.setZero();
                return myInterest;
        }
    }

    /**
     * determine xfer delta.
     * @param pParser the parser
     * @return the delta
     * @throws OceanusException on error
     */
    private OceanusMoney determineXferDelta(final CoeusFundingCircleTransactionParser pParser) throws OceanusException {
        /* Switch on transactionType */
        if (theTransType == CoeusTransactionType.BUYLOAN) {
            return determineFCBuyLoan(pParser, FCLoan.XFER);
        } else {
            final OceanusMoney myXfer = new OceanusMoney(theHolding);
            myXfer.setZero();
            return myXfer;
        }
    }

    /**
     * determine fees delta.
     * @return the delta
     */
    private OceanusMoney determineFeesDelta() {
        /* Obtain change in holding account */
        final OceanusMoney myFees = new OceanusMoney(theHolding);

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
    private OceanusMoney determineCashBackDelta() {
        /* Obtain change in holding account */
        final OceanusMoney myCash = new OceanusMoney(theHolding);

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
    private OceanusMoney determineBadDebtDelta() {
        /* Obtain change in holding account */
        final OceanusMoney myDebt = new OceanusMoney(theHolding);
        myDebt.setZero();

        /* Return the debt */
        return myDebt;
    }

    /**
     * determine recovered delta.
     * @return the delta
     */
    private OceanusMoney determineRecoveredDelta() {
        /* Obtain change in holding account */
        final OceanusMoney myRecovered = new OceanusMoney(theHolding);

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
            case CAPITALREPAYMENT:
                return theDesc.substring(thePrefix.length());
            case RECOVERY:
                return theDesc.endsWith(SFIX_DEBTSALE)
                    ? theDesc.substring(thePrefix.length(), theDesc.length() - SFIX_DEBTSALE.length())
                    : theDesc.substring(thePrefix.length());
            default:
                return null;
        }
    }

    /**
     * determine FCBuyLoan value.
     * @param pParser the parser
     * @param pLoanPart the loan part that we are looking for
     * @return the value
     * @throws OceanusException on error
     */
    private OceanusMoney determineFCBuyLoan(final CoeusFundingCircleTransactionParser pParser,
                                            final FCLoan pLoanPart) throws OceanusException {
        /* Strip off the prefix */
        int myIndex = theDesc.indexOf(':');
        String myLine = theDesc.substring(myIndex + 2);

        /* Isolate Principal */
        myIndex = myLine.indexOf(',');
        String myItem = myLine.substring(0, myIndex);
        myLine = myLine.substring(myIndex + 2);
        myIndex = myItem.indexOf(' ');
        myItem = myItem.substring(myIndex + 1);
        final OceanusMoney myPrincipal = pParser.parseMoney(myItem);

        /* Isolate Interest */
        myIndex = myLine.indexOf(',');
        myItem = myLine.substring(0, myIndex);
        myLine = myLine.substring(myIndex + 2);
        myIndex = myItem.indexOf(' ');
        myItem = myItem.substring(myIndex + 1);
        final OceanusMoney myInterest = pParser.parseMoney(myItem);

        /* Isolate Xfer */
        myIndex = myLine.indexOf(',');
        myItem = myLine.substring(0, myIndex);
        OceanusMoney myXfer = new OceanusMoney(ZERO_MONEY);
        if (myItem.startsWith(PFIX_XFERPAY)) {
            myItem = myItem.substring(PFIX_XFERPAY.length() + 1);
            myXfer = pParser.parseMoney(myItem);
        }

        /* Determine combined value */
        final OceanusMoney myCombo = new OceanusMoney(myInterest);
        myCombo.addAmount(myXfer);

        /* Access cash value */
        final OceanusMoney myCash = new OceanusMoney(theHolding);
        myCash.negate();

        /* If we are looking at the Principal */
        switch (pLoanPart) {
            case CAPITAL:
                /* Determine whether this is the principal entry */
                if (!myPrincipal.equals(myCash)) {
                    myCash.addAmount(myPrincipal);
                    if (myCash.isZero()) {
                        myPrincipal.negate();
                    } else {
                        myPrincipal.setZero();
                    }
                }
                return myPrincipal;

            case INTEREST:
                /* Determine whether this is the interest entry */
                if (!myCombo.equals(myCash)) {
                    myCash.addAmount(myInterest);
                    if (!myCash.isZero()) {
                        myInterest.setZero();
                    }
                } else {
                    myInterest.negate();
                }
                return myInterest;

            case XFER:
                /* Determine whether this is the interest entry */
                if (!myCombo.equals(myCash)) {
                    myXfer.setZero();
                } else {
                    myXfer.negate();
                }
                return myXfer;

            /* Else throw exception */
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public MetisFieldSet<CoeusFundingCircleTransaction> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * FC Loan Type.
     */
    private enum FCLoan {
        /**
         * Capital.
         */
        CAPITAL,

        /**
         * Interest.
         */
        INTEREST,

        /**
         * Xfer.
         */
        XFER
    }
}
