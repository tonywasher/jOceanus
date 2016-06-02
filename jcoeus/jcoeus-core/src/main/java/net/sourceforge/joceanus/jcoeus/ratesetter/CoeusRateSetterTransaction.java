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
package net.sourceforge.joceanus.jcoeus.ratesetter;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * RateSetter Transaction.
 */
public class CoeusRateSetterTransaction
        extends CoeusTransaction<CoeusRateSetterLoan, CoeusRateSetterTransaction, CoeusRateSetterTotals, CoeusRateSetterHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusRateSetterTransaction.class.getSimpleName(), CoeusTransaction.getBaseFields());

    /**
     * Open prefix.
     */
    private static final String PFIX_OPEN = "Account open";

    /**
     * Transfer prefix.
     */
    private static final String PFIX_TRANSFER = "Bank transfer";

    /**
     * Loan prefix.
     */
    private static final String PFIX_LOAN = "Lend order";

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
     * ZERO for BadDebt/CashBack.
     */
    private static final TethysMoney ZERO_MONEY = new TethysMoney();

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
    private CoeusRateSetterLoan theLoan;

    /**
     * Value.
     */
    private final TethysMoney theValue;

    /**
     * Invested.
     */
    private final TethysMoney theInvested;

    /**
     * Holding.
     */
    private final TethysMoney theHolding;

    /**
     * Capital.
     */
    private final TethysMoney theCapital;

    /**
     * NettInterest.
     */
    private final TethysMoney theNettInterest;

    /**
     * Interest.
     */
    private final TethysMoney theInterest;

    /**
     * Fees.
     */
    private final TethysMoney theFees;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    protected CoeusRateSetterTransaction(final CoeusRateSetterTransactionParser pParser,
                                         final List<String> pFields) throws OceanusException {
        /* Initialise underlying class */
        super(pParser.getMarket());

        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

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
        TethysMoney myCapital = pParser.parseMoney(myIterator.next());
        theInterest = pParser.parseMoney(myIterator.next());

        /* Handle fees */
        theFees = pParser.parseMoney(myIterator.next());
        theFees.negate();

        /* Handle Capital correctly */
        theCapital = CoeusTransactionType.CAPITALLOAN.equals(theTransType)
                                                                           ? new TethysMoney(theHolding)
                                                                           : myCapital;
        theCapital.negate();

        /* Handle Invested correctly */
        theInvested = determineInvestedDelta();

        /* Calculate nettInterest delta */
        theNettInterest = determineNettInterestDelta();

        /* Calculate value delta */
        theValue = determineValueDelta();

        /* Check transaction validity */
        checkValidity();
    }

    /**
     * Constructor.
     * @param pBase the base transaction
     * @param pLoan the loan
     * @throws OceanusException on error
     */
    protected CoeusRateSetterTransaction(final CoeusRateSetterTransaction pBase,
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
        theInterest = new TethysMoney();
        theFees = new TethysMoney();
        theInvested = new TethysMoney();
        theNettInterest = new TethysMoney();

        /* Obtain Capital and holding */
        theCapital = new TethysMoney(pLoan.getLoanBookItem().getLent());
        theHolding = new TethysMoney(theCapital);
        theHolding.negate();

        /* Calculate value delta */
        theValue = determineValueDelta();

        /* Check transaction validity */
        checkValidity();
    }

    /**
     * Check validity.
     * @throws OceanusException on error
     */
    private void checkValidity() throws OceanusException {
        /* Obtain the holding */
        TethysMoney myMoney = new TethysMoney(theHolding);

        /* Add Capital and Fees */
        myMoney.addAmount(theCapital);
        myMoney.addAmount(theFees);

        /* Subtract the invested and interest */
        myMoney.subtractAmount(theInterest);
        myMoney.subtractAmount(theInvested);

        /* We should now be zero */
        if (myMoney.isNonZero()) {
            throw new CoeusDataException(this, "Invalid transaction");
        }

        /* Check that capital is only changed on a loan */
        if (theCapital.isNonZero()
            && (theLoan == null
                && !CoeusTransactionType.CAPITALLOAN.equals(theTransType))) {
            throw new CoeusDataException(this, "Capital changed on non-loan");
        }
    }

    @Override
    public CoeusRateSetterMarket getMarket() {
        return (CoeusRateSetterMarket) super.getMarket();
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
    public CoeusRateSetterLoan getLoan() {
        return theLoan;
    }

    /**
     * Set the loan.
     * @param pLoan the loan
     */
    protected void setLoan(final CoeusRateSetterLoan pLoan) {
        theLoan = pLoan;
    }

    @Override
    public TethysMoney getValue() {
        return theValue;
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
    public TethysMoney getCapital() {
        return theCapital;
    }

    @Override
    public TethysMoney getNettInterest() {
        return theNettInterest;
    }

    @Override
    public TethysMoney getInterest() {
        return theInterest;
    }

    @Override
    public TethysMoney getFees() {
        return theFees;
    }

    @Override
    public TethysMoney getCashBack() {
        return ZERO_MONEY;
    }

    @Override
    public TethysMoney getBadDebt() {
        return ZERO_MONEY;
    }

    @Override
    public TethysMoney getRecovered() {
        return ZERO_MONEY;
    }

    /**
     * Determine transaction Type.
     * @return the transaction type
     * @throws OceanusException on error
     */
    private CoeusTransactionType determineTransactionType() throws OceanusException {
        /* If the description is Lend Order */
        if (PFIX_LOAN.equals(theDesc)) {
            return CoeusTransactionType.CAPITALLOAN;
        }

        /* If the description is BankTransfer/AccountOpen */
        if (PFIX_TRANSFER.equals(theDesc)
            || PFIX_OPEN.equals(theDesc)) {
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

        /* If the description is Fees */
        if (PFIX_FEES.equals(theDesc)) {
            return CoeusTransactionType.FEES;
        }

        /* Not recognised */
        throw new TethysDataException("Unrecognised transaction");
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
     * determine value delta.
     * @return the delta
     */
    private TethysMoney determineValueDelta() {
        /* Obtain change in holding account plus change in capital */
        TethysMoney myValue = new TethysMoney(theHolding);
        myValue.addAmount(theCapital);

        /* Return the Value */
        return myValue;
    }

    /**
     * determine invested delta.
     * @return the delta
     */
    private TethysMoney determineInvestedDelta() {
        /* Obtain change in holding account */
        TethysMoney myInvested = new TethysMoney(theHolding);

        /* Invested are increased by any increase the holding account */
        if (!CoeusTransactionType.TRANSFER.equals(theTransType)) {
            myInvested.setZero();
        }

        /* Return the Invested */
        return myInvested;
    }

    /**
     * determine nettInterest delta.
     * @return the delta
     */
    private TethysMoney determineNettInterestDelta() {
        /* Obtain change in interest minus change in fees */
        TethysMoney myValue = new TethysMoney(theInterest);
        myValue.subtractAmount(theFees);

        /* Return the Value */
        return myValue;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
