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
package net.sourceforge.joceanus.jcoeus.data.lendingworks;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jcoeus.data.ratesetter.CoeusRateSetterTransaction;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * LendingWorks Transaction.
 */
public class CoeusLendingWorksTransaction
        extends CoeusTransaction {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusRateSetterTransaction.class.getSimpleName(), CoeusTransaction.getBaseFields());

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
     * CancelLoan prefix.
     */
    private static final String PFIX_CANCELLOAN = "Right to withdraw: Chunk created";

    /**
     * Interest prefix.
     */
    private static final String PFIX_INTEREST = "Interest payment received";

    /**
     * Capital prefix.
     */
    private static final String PFIX_CAPITAL = "Capital repayment received";

    /**
     * ZERO for BadDebt/CashBack.
     */
    protected static final TethysDecimal ZERO_MONEY = new TethysDecimal(0, CoeusLendingWorksMarket.DECIMAL_SIZE);

    /**
     * Date of transaction.
     */
    private final TethysDate theDate;

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
    private final TethysDecimal theInvested;

    /**
     * Holding.
     */
    private final TethysDecimal theHolding;

    /**
     * LoanBook.
     */
    private final TethysDecimal theLoanBook;

    /**
     * Interest.
     */
    private final TethysDecimal theInterest;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    protected CoeusLendingWorksTransaction(final CoeusLendingWorksTransactionParser pParser,
                                           final List<String> pFields) throws OceanusException {
        /* Initialise underlying class */
        super(pParser.getMarket());

        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

        /* Parse the date */
        theDate = pParser.parseDate(myIterator.next());

        /* Store IDs */
        theDesc = myIterator.next();
        String myLoanId = myIterator.next();
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

        /* Handle Invested correctly */
        theInvested = determineInvestedDelta();

        /* Check transaction validity */
        checkValidity();
    }

    /**
     * Check validity.
     * @throws OceanusException on error
     */
    private void checkValidity() throws OceanusException {
        /* Obtain the holding */
        TethysDecimal myMoney = new TethysDecimal(theHolding);

        /* Add Capital */
        myMoney.addValue(theLoanBook);

        /* Subtract the invested and interest */
        myMoney.subtractValue(theInterest);
        myMoney.subtractValue(theInvested);

        /* We should now be zero */
        if (myMoney.isNonZero()) {
            throw new CoeusDataException(this, "Invalid transaction");
        }

        /* Check that capital is only changed on a loan */
        if (theLoanBook.isNonZero()
            && (theLoan == null
                && !CoeusTransactionType.CAPITALLOAN.equals(theTransType))) {
            throw new CoeusDataException(this, "Capital changed on non-loan");
        }
    }

    @Override
    public CoeusLendingWorksMarket getMarket() {
        return (CoeusLendingWorksMarket) super.getMarket();
    }

    @Override
    public TethysDate getDate() {
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
    public TethysDecimal getInvested() {
        return theInvested;
    }

    @Override
    public TethysDecimal getHolding() {
        return theHolding;
    }

    @Override
    public TethysDecimal getLoanBook() {
        return theLoanBook;
    }

    @Override
    public TethysDecimal getInterest() {
        return theInterest;
    }

    @Override
    public TethysDecimal getFees() {
        return ZERO_MONEY;
    }

    @Override
    public TethysDecimal getCashBack() {
        return ZERO_MONEY;
    }

    @Override
    public TethysDecimal getBadDebtInterest() {
        return ZERO_MONEY;
    }

    @Override
    public TethysDecimal getBadDebtCapital() {
        return ZERO_MONEY;
    }

    @Override
    public TethysDecimal getBadDebt() {
        return ZERO_MONEY;
    }

    @Override
    public TethysDecimal getRecovered() {
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
            || PFIX_LOAN2.equals(theDesc)) {
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
        if (PFIX_INTEREST.equals(theDesc)) {
            return CoeusTransactionType.INTEREST;
        }

        /* Not recognised */
        throw new TethysDataException("Unrecognised transaction");
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
        CoeusLendingWorksMarket myMarket = getMarket();
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
    private TethysDecimal determineLoanBookDelta() {
        /* Obtain change in holding account */
        TethysDecimal myInvested = new TethysDecimal(theHolding);

        /* Capital are increased by any capitalLoan */
        if (CoeusTransactionType.CAPITALLOAN.equals(theTransType)) {
            theHolding.negate();
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
    private TethysDecimal determineInterestDelta() {
        /* Obtain change in holding account */
        TethysDecimal myInvested = new TethysDecimal(theHolding);

        /* Interest are increased by any increase the holding account */
        if (!CoeusTransactionType.INTEREST.equals(theTransType)) {
            myInvested.setZero();
        }

        /* Return the Invested */
        return myInvested;
    }

    /**
     * determine invested delta.
     * @return the delta
     */
    private TethysDecimal determineInvestedDelta() {
        /* Obtain change in holding account */
        TethysDecimal myInvested = new TethysDecimal(theHolding);

        /* Invested are increased by any increase the holding account */
        if (!CoeusTransactionType.TRANSFER.equals(theTransType)) {
            myInvested.setZero();
        }

        /* Return the Invested */
        return myInvested;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Obtain field definitions statically.
     * @return the field definitions
     */
    public static MetisFields getStaticDataFields() {
        return FIELD_DEFS;
    }
}
