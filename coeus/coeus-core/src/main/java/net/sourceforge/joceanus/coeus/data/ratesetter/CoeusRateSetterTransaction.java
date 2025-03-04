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
package net.sourceforge.joceanus.coeus.data.ratesetter;

import net.sourceforge.joceanus.coeus.data.CoeusTransaction;
import net.sourceforge.joceanus.coeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.coeus.exc.CoeusDataException;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

import java.util.Iterator;
import java.util.List;

/**
 * RateSetter Transaction.
 */
public class CoeusRateSetterTransaction
        extends CoeusTransaction {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusRateSetterTransaction> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusRateSetterTransaction.class);

    /**
     * Open prefix.
     */
    private static final String PFIX_OPEN = "Account open";

    /**
     * Transfer prefix.
     */
    private static final String PFIX_TRANSFER = "Bank transfer";

    /**
     * Withdrawal prefix.
     */
    private static final String PFIX_WITHDRAWAL = "Next Day Money Withdrawal request";

    /**
     * Loan prefix.
     */
    private static final String PFIX_LOAN = "Lend order";

    /**
     * Cancel prefix.
     */
    private static final String PFIX_CANCEL = "Cancellation of order";

    /**
     * Interest prefix.
     */
    private static final String PFIX_INTEREST = "Interest";

    /**
     * Full Interest prefix.
     */
    private static final String PFIX_FULLINTEREST = "Repaid loan interest";

    /**
     * Capital prefix.
     */
    private static final String PFIX_CAPITAL = "Monthly repayment";

    /**
     * Full RePayment prefix.
     */
    private static final String PFIX_FULLCAPITAL = "Repaid loan capital";

    /**
     * Fees prefix.
     */
    private static final String PFIX_FEES = "RateSetter lender fee";

    /**
     * Taxable CashBack prefix.
     */
    private static final String PFIX_CBACK = "TaxableCashBack";

    /**
     * RateSetter CashBack prefix2.
     */
    private static final String PFIX_CBACK2 = "RateSetter cash back";

    /**
     * ZERO for BadDebt/CashBack.
     */
    static final OceanusMoney ZERO_MONEY = new OceanusMoney();

    /**
     * Date of transaction.
     */
    private final OceanusDate theDate;

    /**
     * Type of Loan.
     */
    private final String theLoanType;

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
    private CoeusRateSetterLoan theLoan;

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
     * Fees.
     */
    private final OceanusMoney theFees;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    CoeusRateSetterTransaction(final CoeusRateSetterTransactionParser pParser,
                               final List<String> pFields) throws OceanusException {
        /* Initialise underlying class */
        super(pParser.getMarket());

        /* Iterate through the fields */
        final Iterator<String> myIterator = pFields.iterator();

        /* Parse the date */
        theDate = pParser.parseDate(myIterator.next());

        /* Store IDs */
        theLoanType = myIterator.next();
        theDesc = myIterator.next();

        /* Determine the transaction type */
        theTransType = determineTransactionType();
        theLoan = determineLoan(myIterator.next());

        /* Parse the monetary values */
        theHolding = pParser.parseMoney(myIterator.next());
        final OceanusMoney myCapital = pParser.parseMoney(myIterator.next());
        theInterest = CoeusTransactionType.TAXABLECASHBACK.equals(theTransType)
                      ? theHolding
                      : pParser.parseMoney(myIterator.next());

        /* Handle fees */
        theFees = pParser.parseMoney(myIterator.next());

        /* Handle Capital correctly */
        theLoanBook = CoeusTransactionType.CAPITALLOAN.equals(theTransType)
                                                                            ? new OceanusMoney(theHolding)
                                                                            : myCapital;
        theLoanBook.negate();

        /* Handle Invested correctly */
        theInvested = determineInvestedDelta();

        /* Check transaction validity */
        checkValidity();
    }

    /**
     * Constructor.
     * @param pBase the base transaction
     * @param pLoan the loan
     * @throws OceanusException on error
     */
    CoeusRateSetterTransaction(final CoeusRateSetterTransaction pBase,
                               final CoeusRateSetterLoan pLoan) throws OceanusException {
        /* Initialise underlying class */
        super(pBase.getMarket());

        /* Parse the date */
        theDate = pBase.getDate();

        /* Store IDs */
        theLoanType = pBase.getLoanType();
        theDesc = pBase.getDescription();

        /* Determine the transaction type */
        theTransType = pBase.getTransType();
        theLoan = pLoan;

        /* Handle fees */
        theInterest = new OceanusMoney();
        theFees = new OceanusMoney();
        theInvested = new OceanusMoney();

        /* Obtain Capital and holding */
        theLoanBook = new OceanusMoney(pLoan.getLoanBookItem().getLent());
        theHolding = new OceanusMoney(theLoanBook);
        theHolding.negate();

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

        /* Add Capital */
        myMoney.addAmount(theLoanBook);

        /* Subtract the invested, interest and fees */
        myMoney.subtractAmount(theInterest);
        myMoney.subtractAmount(theInvested);
        myMoney.subtractAmount(theFees);

        /* We should now be zero */
        if (myMoney.isNonZero()) {
            throw new CoeusDataException(this, "Invalid transaction");
        }

        /* Check that capital is only changed on a loan */
        if (theLoanBook.isNonZero()
            && theLoan == null
            && !CoeusTransactionType.CAPITALLOAN.equals(theTransType)) {
            throw new CoeusDataException(this, "Capital changed on non-loan");
        }
    }

    @Override
    public CoeusRateSetterMarket getMarket() {
        return (CoeusRateSetterMarket) super.getMarket();
    }

    @Override
    public OceanusDate getDate() {
        return theDate;
    }

    /**
     * Obtain the loanType.
     * @return the loanType
     */
    private String getLoanType() {
        return theLoanType;
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
    public CoeusRateSetterLoan getLoan() {
        return theLoan;
    }

    /**
     * Set the loan.
     * @param pLoan the loan
     */
    void setLoan(final CoeusRateSetterLoan pLoan) {
        theLoan = pLoan;
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
    public OceanusMoney getFees() {
        return theFees;
    }

    @Override
    public OceanusMoney getShield() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusMoney getBadDebtInterest() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusMoney getBadDebtCapital() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusMoney getCashBack() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusMoney getXferPayment() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusMoney getBadDebt() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusMoney getRecovered() {
        return ZERO_MONEY;
    }

    /**
     * Determine transaction Type.
     * @return the transaction type
     * @throws OceanusException on error
     */
    private CoeusTransactionType determineTransactionType() throws OceanusException {
        /* If the description is Lend Order/Cancel Order */
        if (PFIX_LOAN.equals(theDesc)
            || PFIX_CANCEL.equals(theDesc)) {
            return CoeusTransactionType.CAPITALLOAN;
        }

        /* If the description is BankTransfer/AccountOpen */
        if (PFIX_TRANSFER.equals(theDesc)
            || PFIX_OPEN.equals(theDesc)
            || PFIX_WITHDRAWAL.equals(theDesc)) {
            return CoeusTransactionType.TRANSFER;
        }

        /* If the description is Monthly Re-payment */
        if (PFIX_CAPITAL.equals(theDesc)
            || PFIX_FULLCAPITAL.equals(theDesc)) {
            return CoeusTransactionType.CAPITALREPAYMENT;
        }

        /* If the description is Interest */
        if (PFIX_INTEREST.equals(theDesc)
            || PFIX_FULLINTEREST.equals(theDesc)) {
            return CoeusTransactionType.INTEREST;
        }

        /* If the description is Interest */
        if (PFIX_CBACK.equals(theDesc)
            || PFIX_CBACK2.equals(theDesc)) {
            return CoeusTransactionType.TAXABLECASHBACK;
        }

        /* If the description is Fees */
        if (PFIX_FEES.equals(theDesc)) {
            return CoeusTransactionType.FEES;
        }

        /* Not recognised */
        throw new CoeusDataException("Unrecognised transaction");
    }

    /**
     * determine loan.
     * @param pId the loan id (potentially)
     * @return the loan
     * @throws OceanusException on error
     */
    private CoeusRateSetterLoan determineLoan(final String pId) throws OceanusException {
        /* Switch on transaction type */
        switch (theTransType) {
            case CAPITALREPAYMENT:
            case INTEREST:
            case FEES:
                return getMarket().findLoanById(pId);
            default:
                return null;
        }
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

    @Override
    public MetisFieldSet<CoeusRateSetterTransaction> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
