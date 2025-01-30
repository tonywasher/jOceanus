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
package net.sourceforge.joceanus.coeus.data.zopa;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.coeus.exc.CoeusDataException;
import net.sourceforge.joceanus.coeus.data.CoeusTransaction;
import net.sourceforge.joceanus.coeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;

/**
 * Zopa transaction.
 */
public final class CoeusZopaTransaction
        extends CoeusTransaction {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusZopaTransaction> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusZopaTransaction.class);

    /**
     * Date length.
     */
    private static final int DATE_LEN = 10;

    /**
     * Transfer prefix.
     */
    private static final String PFIX_TRANSFER = "Funding Bacs";

    /**
     * Transfer prefix.
     */
    private static final String PFIX_XFEROUT = "Lender Withdrawal Request";

    /**
     * Loan prefix.
     */
    private static final String PFIX_LOAN = "Loan disbursal to ";

    /**
     * Interest prefix.
     */
    private static final String PFIX_INTEREST = "Interest payment from ";

    /**
     * Manual Interest payment correction.
     */
    private static final String PFIX_INTEREST2 = "Manual Interest Payment Correction";

    /**
     * SafeGuard Interest prefix.
     */
    private static final String PFIX_SAFEINTEREST = "Safeguard interest payment for ";

    /**
     * UpFront Interest prefix.
     */
    private static final String PFIX_UPFRONTINTEREST = "RR Upfront Interest for ";

    /**
     * Capital prefix.
     */
    private static final String PFIX_CAPITAL = "Capital payment from ";

    /**
     * Capital prefix2.
     */
    private static final String PFIX_CAPITAL2 = "Capital payment";

    /**
     * Safeguard Capital prefix.
     */
    private static final String PFIX_SAFECAPITAL = "Safeguard capital payment for ";

    /**
     * Safeguard Final prefix.
     */
    private static final String PFIX_SAFEFINAL = "Final Safeguard Payout";

    /**
     * Fees Rebate prefix.
     */
    private static final String PFIX_FEESREBATE = "Monthly Lender Fees Rebate";

    /**
     * Fees prefix.
     */
    private static final String PFIX_FEES = "Monthly Lender Fees";

    /**
     * Rate Promise prefix.
     */
    private static final String PFIX_RATEPROMISE = "Rate promise for period";

    /**
     * Rate Promise prefix2.
     */
    private static final String PFIX_RATEPROMISE2 = "Rate Promise";

    /**
     * CashBack prefix.
     */
    private static final String PFIX_CASHBACK = "Cashback Promotion";

    /**
     * Wrongly Priced.
     */
    private static final String PFIX_BADPRICE = "Bought wrongly priced loans";

    /**
     * Goodwill Payment.
     */
    private static final String PFIX_GOODWILL = "Goodwill Payment";

    /**
     * Capital Adjustment Debit.
     */
    private static final String PFIX_CAPADJUSTDBT = "Capital Adjustment Debit";

    /**
     * Capital Adjustment Credit.
     */
    private static final String PFIX_CAPADJUSTCDT = "Capital Adjustment Credit";

    /**
     * ZERO for BadDebt/CashBack.
     */
    static final OceanusDecimal ZERO_MONEY = new OceanusDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);

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
    private final CoeusZopaLoan theLoan;

    /**
     * Invested.
     */
    private final OceanusDecimal theInvested;

    /**
     * Holding.
     */
    private final OceanusDecimal theHolding;

    /**
     * LoanBook.
     */
    private final OceanusDecimal theLoanBook;

    /**
     * Interest.
     */
    private final OceanusDecimal theInterest;

    /**
     * Fees.
     */
    private final OceanusDecimal theFees;

    /**
     * CashBack.
     */
    private final OceanusDecimal theCashBack;

    /**
     * BadDebt.
     */
    private final OceanusDecimal theBadDebt;

    /**
     * Recovered.
     */
    private final OceanusDecimal theRecovered;

    /**
     * Prefix.
     */
    private String thePrefix;

    /**
     * is upFront interest.
     */
    private boolean isUpFront;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    CoeusZopaTransaction(final CoeusZopaTransactionParser pParser,
                         final List<String> pFields) throws OceanusException {
        /* Initialise underlying class */
        super(pParser.getMarket());

        /* Iterate through the fields */
        final Iterator<String> myIterator = pFields.iterator();

        /* Parse the date */
        String myDate = myIterator.next();
        myDate = myDate.substring(0, DATE_LEN);
        theDate = pParser.parseDate(myDate);

        /* Obtain description */
        theDesc = myIterator.next();

        /* Determine the transaction type */
        theTransType = determineTransactionType();

        /* Determine loan */
        theLoan = determineLoan();

        /* Parse the values */
        final OceanusDecimal myPaidIn = pParser.parseDecimal(myIterator.next());
        final OceanusDecimal myPaidOut = pParser.parseDecimal(myIterator.next());

        /* Determine the HoldingDelta */
        theHolding = new OceanusDecimal(myPaidIn);
        theHolding.subtractValue(myPaidOut);

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

        /* If this is an upFront interest payment */
        if (isUpFront && theLoan != null) {
            theLoan.addUpFrontInterest(theInterest);
        }
    }

    /**
     * Constructor.
     * @param pLoan the loan
     * @throws OceanusException on error
     */
    CoeusZopaTransaction(final CoeusZopaLoan pLoan) throws OceanusException {
        /* Initialise underlying class */
        super(pLoan.getMarket());

        /* Determine the debt */
        final OceanusDecimal myDebt = new OceanusDecimal(pLoan.getBalance());
        myDebt.negate();

        /* Access the default date */
        theDate = pLoan.getBadDebtDate();

        /* Obtain description */
        theDesc = CoeusTransactionType.BADDEBT.toString();

        /* Determine the transaction type */
        theTransType = CoeusTransactionType.BADDEBT;

        /* Record the loan */
        theLoan = pLoan;

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
        final OceanusDecimal myValue = new OceanusDecimal(theHolding);

        /* Add Capital BadDebt and Fees */
        myValue.addValue(theLoanBook);

        /* Subtract the invested cashBack and interest etc. */
        myValue.subtractValue(theInterest);
        myValue.subtractValue(theInvested);
        myValue.subtractValue(theFees);
        myValue.subtractValue(theCashBack);
        myValue.subtractValue(theBadDebt);
        myValue.subtractValue(theRecovered);

        /* We should now be zero */
        if (myValue.isNonZero()) {
            throw new CoeusDataException(this, "Invalid transaction");
        }

        /* Check that capital is only changed on a loan */
        if (theLoanBook.isNonZero()
            && theLoan == null) {
            throw new CoeusDataException(this, "Capital changed on non-loan");
        }
    }

    @Override
    public CoeusZopaMarket getMarket() {
        return (CoeusZopaMarket) super.getMarket();
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
    public CoeusZopaLoan getLoan() {
        return theLoan;
    }

    @Override
    public OceanusDecimal getInvested() {
        return theInvested;
    }

    @Override
    public OceanusDecimal getHolding() {
        return theHolding;
    }

    @Override
    public OceanusDecimal getLoanBook() {
        return theLoanBook;
    }

    @Override
    public OceanusDecimal getBadDebtInterest() {
        if (theLoan == null
            || theLoan.isBadDebtCapital()) {
            return ZERO_MONEY;
        }
        return CoeusTransactionType.RECOVERY.equals(theTransType)
                                                                  ? theRecovered
                                                                  : theBadDebt;
    }

    @Override
    public OceanusDecimal getBadDebtCapital() {
        if (theLoan == null
            || !theLoan.isBadDebtCapital()) {
            return ZERO_MONEY;
        }
        return CoeusTransactionType.RECOVERY.equals(theTransType)
                                                                  ? theRecovered
                                                                  : theBadDebt;
    }

    @Override
    public OceanusDecimal getInterest() {
        return theInterest;
    }

    @Override
    public OceanusDecimal getFees() {
        return theFees;
    }

    @Override
    public OceanusDecimal getShield() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusDecimal getCashBack() {
        return theCashBack;
    }

    @Override
    public OceanusDecimal getXferPayment() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusDecimal getBadDebt() {
        return theBadDebt;
    }

    @Override
    public OceanusDecimal getRecovered() {
        return theRecovered;
    }

    /**
     * Is this transaction upFront interest?
     * @return true/false
     */
    protected boolean isUpFront() {
        return isUpFront;
    }

    /**
     * Determine transaction Type.
     * @return the transaction type
     * @throws OceanusException on error
     */
    private CoeusTransactionType determineTransactionType() throws OceanusException {
        /* If the description is Loan disbursal */
        if (theDesc.startsWith(PFIX_LOAN)) {
            return CoeusTransactionType.CAPITALLOAN;
        }

        /* If the description is BankTransfer */
        if (PFIX_TRANSFER.equals(theDesc)
            || PFIX_XFEROUT.equals(theDesc)) {
            return CoeusTransactionType.TRANSFER;
        }

        /* If the description is Capital payment */
        if (theDesc.startsWith(PFIX_CAPITAL)) {
            thePrefix = PFIX_CAPITAL;
            return CoeusTransactionType.CAPITALREPAYMENT;
        }

        /* If the description is Capital payment2 */
        if (PFIX_CAPITAL2.equals(theDesc)) {
            return CoeusTransactionType.INTEREST;
        }

        /* If the description is Capital payment */
        if (theDesc.startsWith(PFIX_SAFECAPITAL)) {
            thePrefix = PFIX_SAFECAPITAL;
            return CoeusTransactionType.CAPITALREPAYMENT;
        }

        /* If the description is Interest */
        if (theDesc.startsWith(PFIX_INTEREST)) {
            thePrefix = PFIX_INTEREST;
            return CoeusTransactionType.INTEREST;
        }

        /* If the description is Manual interest correction */
        if (PFIX_INTEREST2.equals(theDesc)) {
            return CoeusTransactionType.INTEREST;
        }

        /* If the description is UpFront Interest */
        if (theDesc.startsWith(PFIX_UPFRONTINTEREST)) {
            thePrefix = PFIX_UPFRONTINTEREST;
            isUpFront = true;
            return CoeusTransactionType.INTEREST;
        }

        /* If the description is Safeguard Interest */
        if (theDesc.startsWith(PFIX_SAFEINTEREST)) {
            thePrefix = PFIX_SAFEINTEREST;
            return CoeusTransactionType.INTEREST;
        }

        /* If the description is Rate Promise */
        if (theDesc.startsWith(PFIX_RATEPROMISE)
            || PFIX_RATEPROMISE2.equals(theDesc)) {
            return CoeusTransactionType.RATEPROMISE;
        }

        /* If the description is CashBack */
        if (PFIX_CASHBACK.equals(theDesc)
            || PFIX_BADPRICE.equals(theDesc)
            || PFIX_SAFEFINAL.equals(theDesc)) {
            return CoeusTransactionType.CASHBACK;
        }

        /* If the description is Goodwill/CapitalAdjust */
        if (PFIX_GOODWILL.equals(theDesc)
            || PFIX_CAPADJUSTDBT.equals(theDesc)
            || PFIX_CAPADJUSTCDT.equals(theDesc)) {
            return CoeusTransactionType.CASHBACK;
        }

        /* If the description is FeesRebate */
        if (PFIX_FEESREBATE.equals(theDesc)) {
            return CoeusTransactionType.INTEREST;
        }

        /* If the description is Fees */
        if (theDesc.startsWith(PFIX_FEES)) {
            return CoeusTransactionType.FEES;
        }

        /* Not recognised */
        throw new CoeusDataException("Unrecognised transaction");
    }

    /**
     * determine invested delta.
     * @return the delta
     */
    private OceanusDecimal determineInvestedDelta() {
        /* Obtain change in holding account */
        final OceanusDecimal myInvested = new OceanusDecimal(theHolding);

        /* Invested are increased by any increase the holding account */
        if (!CoeusTransactionType.TRANSFER.equals(theTransType)) {
            myInvested.setZero();
        }

        /* Return the Invested */
        return myInvested;
    }

    /**
     * determine capital delta.
     * @return the delta
     */
    private OceanusDecimal determineLoanBookDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case CAPITALLOAN:
            case CAPITALREPAYMENT:
                OceanusDecimal myCapital = new OceanusDecimal(theHolding);
                myCapital.negate();
                return myCapital;
            default:
                myCapital = new OceanusDecimal(theHolding);
                myCapital.setZero();
                return myCapital;
        }
    }

    /**
     * determine interest delta.
     * @return the delta
     */
    private OceanusDecimal determineInterestDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case INTEREST:
            case RATEPROMISE:
                return new OceanusDecimal(theHolding);
            default:
                final OceanusDecimal myInterest = new OceanusDecimal(theHolding);
                myInterest.setZero();
                return myInterest;
        }
    }

    /**
     * determine fees delta.
     * @return the delta
     */
    private OceanusDecimal determineFeesDelta() {
        /* Obtain change in holding account */
        final OceanusDecimal myFees = new OceanusDecimal(theHolding);

        /* Set zero if not fees */
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
    private OceanusDecimal determineCashBackDelta() {
        /* Obtain change in holding account */
        final OceanusDecimal myCash = new OceanusDecimal(theHolding);

        /* CashBack is increased by any increase in the holding account */
        if (!CoeusTransactionType.CASHBACK.equals(theTransType)) {
            myCash.setZero();
        }

        /* Return the CashBack */
        return myCash;
    }

    /**
     * determine recovered delta.
     * @return the delta
     */
    private OceanusDecimal determineRecoveredDelta() {
        /* Obtain change in holding account */
        final OceanusDecimal myRecovered = new OceanusDecimal(theHolding);

        /* recovered is increased by any increase in the holding account */
        if (!CoeusTransactionType.RECOVERY.equals(theTransType)) {
            myRecovered.setZero();
        }

        /* Return the Recovered */
        return myRecovered;
    }

    /**
     * determine badDebt delta.
     * @return the delta
     */
    private OceanusDecimal determineBadDebtDelta() {
        final OceanusDecimal myDebt = new OceanusDecimal(theHolding);
        myDebt.setZero();
        return myDebt;
    }

    /**
     * determine loan.
     * @return the loan
     * @throws OceanusException on error
     */
    private CoeusZopaLoan determineLoan() throws OceanusException {
        /* Obtain the market */
        final CoeusZopaMarket myMarket = getMarket();

        /* Determine the loan id */
        final String myLoanId = determineLoanId();
        return myLoanId == null
                                ? null
                                : myMarket.findLoanById(myLoanId);
    }

    /**
     * determine loanId.
     * @return the id
     */
    private String determineLoanId() {
        /* Switch on transactionType */
        switch (theTransType) {
            case INTEREST:
            case CAPITALREPAYMENT:
                return thePrefix == null
                                         ? null
                                         : theDesc.substring(thePrefix.length());
            case CAPITALLOAN:
                final String myValue = theDesc.substring(PFIX_LOAN.length());
                final int myIndex = myValue.indexOf(" from lending ");
                return myIndex == -1
                                     ? myValue
                                     : myValue.substring(0, myIndex);
            default:
                return null;
        }
    }

    @Override
    public MetisFieldSet<CoeusZopaTransaction> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
