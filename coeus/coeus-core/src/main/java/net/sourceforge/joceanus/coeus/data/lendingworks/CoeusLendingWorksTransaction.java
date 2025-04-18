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
package net.sourceforge.joceanus.coeus.data.lendingworks;

import net.sourceforge.joceanus.coeus.data.CoeusTransaction;
import net.sourceforge.joceanus.coeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.coeus.exc.CoeusDataException;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;

import java.util.Iterator;
import java.util.List;

/**
 * LendingWorks Transaction.
 */
public final class CoeusLendingWorksTransaction
        extends CoeusTransaction {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusLendingWorksTransaction> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusLendingWorksTransaction.class);

    /**
     * Transfer prefix.
     */
    private static final String PFIX_TRANSFER = "Money transferred in";

    /**
     * Loan prefix.
     */
    private static final String PFIX_LOAN = "Loan chunk created";

    /**
     * Loan2 prefix.
     */
    private static final String PFIX_LOAN2 = "Loan chunk acquired via Quick Withdraw";

    /**
     * Loan3 prefix.
     */
    private static final String PFIX_LOAN3 = "Loan chunk increased via Quick Withdraw";

    /**
     * CancelLoan prefix.
     */
    private static final String PFIX_CANCELLOAN = "Right to withdraw: Chunk created";

    /**
     * Interest prefix.
     */
    private static final String PFIX_INTEREST = "Interest payment received";

    /**
     * Interest2 prefix.
     */
    private static final String PFIX_INTEREST2 = "Quick Withdraw loan interest shortfall received";

    /**
     * Capital prefix.
     */
    private static final String PFIX_CAPITAL = "Capital repayment received";

    /**
     * CashBack prefix.
     */
    private static final String PFIX_CASHBACK = "Other account credit";

    /**
     * Xfer out prefix.
     */
    private static final String PFIX_XFEROUT = "Money transferred out";

    /**
     * Shield payment prefix.
     */
    private static final String PFIX_SHIELD = "Shield contribution adjustment";

    /**
     * ZERO for BadDebt/CashBack.
     */
    static final OceanusDecimal ZERO_MONEY = new OceanusDecimal(0, CoeusLendingWorksMarket.DECIMAL_SIZE);

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
    private final CoeusLendingWorksLoan theLoan;

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
     * CashBack.
     */
    private final OceanusDecimal theCashBack;

    /**
     * Shield.
     */
    private final OceanusDecimal theShield;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    CoeusLendingWorksTransaction(final CoeusLendingWorksTransactionParser pParser,
                                 final List<String> pFields) throws OceanusException {
        /* Initialise underlying class */
        super(pParser.getMarket());

        /* Iterate through the fields */
        final Iterator<String> myIterator = pFields.iterator();

        /* Parse the date */
        theDate = pParser.parseDate(myIterator.next());

        /* Store Description and LoanId */
        theDesc = myIterator.next();
        final String myLoanId = myIterator.next();

        /* Skip Product */
        myIterator.next();

        /* Store LoanType */
        theLoanType = myIterator.next();

        /* Determine the transaction type */
        theTransType = determineTransactionType();

        /* Determine the loan */
        theLoan = determineLoan(myLoanId);

        /* Parse the monetary values */
        theHolding = pParser.parseDecimal(myIterator.next());

        /* Handle Capital correctly */
        theLoanBook = determineLoanBookDelta();

        /* Handle Interest correctly */
        theInterest = determineInterestDelta();

        /* Handle Shield correctly */
        theShield = determineShieldDelta();

        /* Handle Invested correctly */
        theInvested = determineInvestedDelta();

        /* Handle CashBack correctly */
        theCashBack = determineCashBackDelta();

        /* Check transaction validity */
        checkValidity();
    }

    /**
     * Check validity.
     * @throws OceanusException on error
     */
    private void checkValidity() throws OceanusException {
        /* Obtain the holding */
        final OceanusDecimal myMoney = new OceanusDecimal(theHolding);

        /* Add Capital */
        myMoney.addValue(theLoanBook);

        /* Subtract the invested and interest */
        myMoney.subtractValue(theInterest);
        myMoney.subtractValue(theCashBack);
        myMoney.subtractValue(theInvested);
        myMoney.subtractValue(theShield);

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
    public CoeusLendingWorksMarket getMarket() {
        return (CoeusLendingWorksMarket) super.getMarket();
    }

    @Override
    public OceanusDate getDate() {
        return theDate;
    }

    /**
     * Obtain the loanType.
     * @return the loanType
     */
    public String getLoanType() {
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
    public CoeusLendingWorksLoan getLoan() {
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
    public OceanusDecimal getInterest() {
        return theInterest;
    }

    @Override
    public OceanusDecimal getFees() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusDecimal getShield() {
        return theShield;
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
    public OceanusDecimal getBadDebtInterest() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusDecimal getBadDebtCapital() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusDecimal getBadDebt() {
        return ZERO_MONEY;
    }

    @Override
    public OceanusDecimal getRecovered() {
        return ZERO_MONEY;
    }

    /**
     * Determine transaction Type.
     * @return the transaction type
     * @throws OceanusException on error
     */
    private CoeusTransactionType determineTransactionType() throws OceanusException {
        /* If the description is Lend Order */
        if (PFIX_LOAN.equals(theDesc)
            || PFIX_LOAN2.equals(theDesc)
            || PFIX_LOAN3.equals(theDesc)) {
            return CoeusTransactionType.CAPITALLOAN;
        }

        /* If the description is BankTransfer */
        if (PFIX_TRANSFER.equals(theDesc)) {
            return CoeusTransactionType.TRANSFER;
        }

        /* If the description is Monthly Re-payment */
        if (PFIX_CAPITAL.equals(theDesc)
            || PFIX_CANCELLOAN.equals(theDesc)) {
            return CoeusTransactionType.CAPITALREPAYMENT;
        }

        /* If the description is Interest */
        if (PFIX_INTEREST.equals(theDesc)
             || PFIX_INTEREST2.equals(theDesc)) {
            return CoeusTransactionType.INTEREST;
        }

        /* If the description is Shield */
        if (PFIX_SHIELD.equals(theDesc)) {
            return CoeusTransactionType.SHIELD;
        }

        /* If the description is CashBack */
        if (PFIX_CASHBACK.equals(theDesc)) {
            return CoeusTransactionType.CASHBACK;
        }

        /* If the description is Xfer Out */
        if (PFIX_XFEROUT.equals(theDesc)) {
            return CoeusTransactionType.TRANSFER;
        }

        /* Not recognised */
        throw new CoeusDataException("Unrecognised transaction");
    }

    /**
     * determine loan.
     * @param pId the loan id
     * @return the loan
     * @throws OceanusException on error
     */
    private CoeusLendingWorksLoan determineLoan(final String pId) throws OceanusException {
        /* Switch on transaction type */
        switch (theTransType) {
            case CAPITALLOAN:
            case CAPITALREPAYMENT:
            case INTEREST:
            case SHIELD:
                return findLoan(pId);
            default:
                return null;
        }
    }

    /**
     * Obtain loan.
     * @param pId the loan id
     * @return the loan
     * @throws OceanusException on error
     */
    private CoeusLendingWorksLoan findLoan(final String pId) throws OceanusException {
        /* Look up existing loan */
        final CoeusLendingWorksMarket myMarket = getMarket();
        CoeusLendingWorksLoan myLoan = myMarket.getLoanById(pId);

        /* If this is a new loan */
        if (myLoan == null) {
            /* Allocate and record the loan */
            myLoan = new CoeusLendingWorksLoan(myMarket, pId);
            myMarket.recordLoan(myLoan);
        }

        /* return the loan */
        return myLoan;
    }

    /**
     * determine capital delta.
     * @return the delta
     */
    private OceanusDecimal determineLoanBookDelta() {
        /* Obtain change in holding account */
        final OceanusDecimal myInvested = new OceanusDecimal(theHolding);

        /* Capital are increased by any capitalLoan */
        if (CoeusTransactionType.CAPITALLOAN.equals(theTransType)) {
            theHolding.negate();
            return myInvested;
        }

        /* Capital are decreased by shield payment */
        if (CoeusTransactionType.SHIELD.equals(theTransType)) {
            theHolding.setZero();
            return myInvested;
        }

        /* Capital are reduced by any repayment */
        if (!CoeusTransactionType.CAPITALREPAYMENT.equals(theTransType)) {
            myInvested.setZero();
        }

        /* Return the Invested */
        myInvested.negate();
        return myInvested;
    }

    /**
     * determine interest delta.
     * @return the delta
     */
    private OceanusDecimal determineInterestDelta() {
        /* Obtain change in holding account */
        final OceanusDecimal myInvested = new OceanusDecimal(theHolding);

        /* Interest are increased by any increase the holding account */
        if (!CoeusTransactionType.INTEREST.equals(theTransType)) {
            myInvested.setZero();
        }

        /* Return the Invested */
        return myInvested;
    }

    /**
     * determine shield delta.
     * @return the delta
     */
    private OceanusDecimal determineShieldDelta() {
        /* Obtain change in loanBook */
        final OceanusDecimal myInvested = new OceanusDecimal(theLoanBook);

        /* Interest are increased by any increase the holding account */
        if (!CoeusTransactionType.SHIELD.equals(theTransType)) {
            myInvested.setZero();
        }

        /* Return the Invested */
        return myInvested;
    }

    /**
     * determine cashBack delta.
     * @return the delta
     */
    private OceanusDecimal determineCashBackDelta() {
        /* Obtain change in holding account */
        final OceanusDecimal myCashBack = new OceanusDecimal(theHolding);

        /* Interest are increased by any increase the holding account */
        if (!CoeusTransactionType.CASHBACK.equals(theTransType)) {
            myCashBack.setZero();
        }

        /* Return the Invested */
        return myCashBack;
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

    @Override
    public MetisFieldSet<CoeusLendingWorksTransaction> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
