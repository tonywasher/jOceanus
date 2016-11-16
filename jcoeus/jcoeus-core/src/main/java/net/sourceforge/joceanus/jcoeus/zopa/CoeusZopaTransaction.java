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
package net.sourceforge.joceanus.jcoeus.zopa;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Zopa transaction.
 */
public class CoeusZopaTransaction
        extends CoeusTransaction<CoeusZopaLoan, CoeusZopaTransaction, CoeusZopaTotals, CoeusZopaHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusZopaTransaction.class.getSimpleName(), CoeusTransaction.getBaseFields());

    /**
     * Transfer prefix.
     */
    private static final String PFIX_TRANSFER = "Funding Bacs";

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
    private final CoeusZopaLoan theLoan;

    /**
     * Prefix.
     */
    private String thePrefix;

    /**
     * Invested.
     */
    private final TethysDecimal theInvested;

    /**
     * Value.
     */
    private final TethysDecimal theValue;

    /**
     * Holding.
     */
    private final TethysDecimal theHolding;

    /**
     * Capital.
     */
    private final TethysDecimal theCapital;

    /**
     * NettInterest.
     */
    private final TethysDecimal theNettInterest;

    /**
     * Interest.
     */
    private final TethysDecimal theInterest;

    /**
     * Fees.
     */
    private final TethysDecimal theFees;

    /**
     * CashBack.
     */
    private final TethysDecimal theCashBack;

    /**
     * BadDebt.
     */
    private final TethysDecimal theBadDebt;

    /**
     * Recovered.
     */
    private final TethysDecimal theRecovered;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    protected CoeusZopaTransaction(final CoeusZopaTransactionParser pParser,
                                   final List<String> pFields) throws OceanusException {
        /* Initialise underlying class */
        super(pParser.getMarket());

        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

        /* Parse the date */
        theDate = pParser.parseDate(myIterator.next());

        /* Obtain description */
        theDesc = myIterator.next();

        /* Determine the transaction type */
        theTransType = determineTransactionType();

        /* Determine loan */
        theLoan = determineLoan();

        /* Parse the values */
        TethysDecimal myPaidIn = pParser.parseDecimal(myIterator.next());
        TethysDecimal myPaidOut = pParser.parseDecimal(myIterator.next());

        /* Determine the HoldingDelta */
        theHolding = new TethysDecimal(myPaidIn);
        theHolding.subtractValue(myPaidOut);

        /* Determine the Deltas */
        theInvested = determineInvestedDelta();
        theCapital = determineCapitalDelta();
        theInterest = determineInterestDelta();
        theFees = determineFeesDelta();
        theCashBack = determineCashBackDelta();
        theBadDebt = determineBadDebtDelta();
        theRecovered = determineRecoveredDelta();
        theValue = determineValueDelta();
        theNettInterest = determineNettInterestDelta();

        /* Check transaction validity */
        checkValidity();
    }

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    protected CoeusZopaTransaction(final CoeusZopaBadDebtParser pParser,
                                   final List<String> pFields) throws OceanusException {
        /* Initialise underlying class */
        super(pParser.getMarket());

        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

        /* Obtain LoanIDs */
        String myLoanId = myIterator.next();

        /* Skip Product/date acquired/risk */
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Skip Term and Loan Size */
        myIterator.next();
        myIterator.next();

        /* Skip status/rate/lent */
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Parse the outstanding balances */
        TethysDecimal myDebt = pParser.parseDecimal(myIterator.next());

        /* Skip rePaid/capital/interest/arrears */
        myIterator.next();
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Skip Payment Day/SafeGuard flag */
        myIterator.next();
        myIterator.next();

        /* Skip Comment and loan start/end dates */
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Skip Monthly rePayment/Purpose/portion rePaid */
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Parse the date */
        theDate = pParser.parseDate(myIterator.next());

        /* Obtain description */
        theDesc = CoeusTransactionType.BADDEBT.toString();

        /* Determine the transaction type */
        theTransType = CoeusTransactionType.BADDEBT;

        /* Determine the loan */
        theLoan = getMarket().findLoanById(myLoanId);

        /* Determine the BadDebt Deltas */
        theBadDebt = myDebt;
        theCapital = new TethysDecimal(myDebt);
        theCapital.negate();

        /* Set other values to zero */
        theHolding = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theInvested = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theNettInterest = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theInterest = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theFees = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theCashBack = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
        theRecovered = new TethysDecimal(0, CoeusZopaMarket.DECIMAL_SIZE);
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
        TethysDecimal myValue = new TethysDecimal(theHolding);

        /* Add Capital BadDebt and Fees */
        myValue.addValue(theCapital);
        myValue.addValue(theFees);
        myValue.addValue(theBadDebt);

        /* Subtract the invested cashBack and interest */
        myValue.subtractValue(theInterest);
        myValue.subtractValue(theInvested);
        myValue.subtractValue(theCashBack);

        /* We should now be zero */
        if (myValue.isNonZero()) {
            throw new CoeusDataException(this, "Invalid transaction");
        }

        /* Check that capital is only changed on a loan */
        if (theCapital.isNonZero()
            && theLoan == null) {
            throw new CoeusDataException(this, "Capital changed on non-loan");
        }
    }

    @Override
    public CoeusZopaMarket getMarket() {
        return (CoeusZopaMarket) super.getMarket();
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
    public CoeusZopaLoan getLoan() {
        return theLoan;
    }

    @Override
    public TethysDecimal getValue() {
        return theValue;
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
    public TethysDecimal getCapital() {
        return theCapital;
    }

    @Override
    public TethysDecimal getNettInterest() {
        return theNettInterest;
    }

    @Override
    public TethysDecimal getInterest() {
        return theInterest;
    }

    @Override
    public TethysDecimal getFees() {
        return theFees;
    }

    @Override
    public TethysDecimal getCashBack() {
        return theCashBack;
    }

    @Override
    public TethysDecimal getBadDebt() {
        return theBadDebt;
    }

    @Override
    public TethysDecimal getRecovered() {
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
            return CoeusTransactionType.CAPITALLOAN;
        }

        /* If the description is BankTransfer */
        if (PFIX_TRANSFER.equals(theDesc)) {
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
        if (PFIX_CASHBACK.equals(theDesc)) {
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
     * determine value delta.
     * @return the delta
     */
    private TethysDecimal determineValueDelta() {
        /* Obtain change in holding account plus change in capital */
        TethysDecimal myValue = new TethysDecimal(theHolding);
        myValue.addValue(theCapital);

        /* Return the Value */
        return myValue;
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

    /**
     * determine capital delta.
     * @return the delta
     */
    private TethysDecimal determineCapitalDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case CAPITALLOAN:
            case CAPITALREPAYMENT:
                TethysDecimal myCapital = new TethysDecimal(theHolding);
                myCapital.negate();
                return myCapital;
            default:
                myCapital = new TethysDecimal(theHolding);
                myCapital.setZero();
                return myCapital;
        }
    }

    /**
     * determine nettInterest delta.
     * @return the delta
     */
    private TethysDecimal determineNettInterestDelta() {
        /* Obtain change in holding account plus change in capital */
        TethysDecimal myValue = new TethysDecimal(theInterest);
        myValue.subtractValue(theFees);

        /* Return the Value */
        return myValue;
    }

    /**
     * determine interest delta.
     * @return the delta
     */
    private TethysDecimal determineInterestDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case INTEREST:
            case RATEPROMISE:
                return new TethysDecimal(theHolding);
            default:
                TethysDecimal myInterest = new TethysDecimal(theHolding);
                myInterest.setZero();
                return myInterest;
        }
    }

    /**
     * determine fees delta.
     * @return the delta
     */
    private TethysDecimal determineFeesDelta() {
        /* Obtain change in holding account */
        TethysDecimal myFees = new TethysDecimal(theHolding);

        /* Fees are increased by any decrease the holding account */
        if (CoeusTransactionType.FEES.equals(theTransType)) {
            myFees.negate();
        } else {
            myFees.setZero();
        }

        /* Return the Fees */
        return myFees;
    }

    /**
     * determine cashBack delta.
     * @return the delta
     */
    private TethysDecimal determineCashBackDelta() {
        /* Obtain change in holding account */
        TethysDecimal myCash = new TethysDecimal(theHolding);

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
    private TethysDecimal determineRecoveredDelta() {
        /* Obtain change in holding account */
        TethysDecimal myRecovered = new TethysDecimal(theHolding);

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
    private TethysDecimal determineBadDebtDelta() {
        TethysDecimal myDebt = new TethysDecimal(theHolding);
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
        CoeusZopaMarket myMarket = getMarket();

        /* Determine the loan id */
        String myLoanId = determineLoanId();
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
                String myValue = theDesc.substring(PFIX_LOAN.length());
                int myIndex = myValue.indexOf(" from lending ");
                return myIndex == -1
                                     ? myValue
                                     : myValue.substring(0, myIndex);
            default:
                return null;
        }
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Obtain field definitions statically
     * @return the field definitions
     */
    public static MetisFields getStaticDataFields() {
        return FIELD_DEFS;
    }
}
