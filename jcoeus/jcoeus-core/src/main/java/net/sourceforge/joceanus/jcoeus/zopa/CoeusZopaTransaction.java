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
package net.sourceforge.joceanus.jcoeus.zopa;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Zopa transaction.
 */
public class CoeusZopaTransaction
        implements CoeusTransaction {
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
     * Upfront Interest prefix.
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
     * Loan Id.
     */
    private final String theLoanId;

    /**
     * Prefix.
     */
    private String thePrefix;

    /**
     * Paid In.
     */
    private final TethysDecimal thePaidIn;

    /**
     * Paid Out.
     */
    private final TethysDecimal thePaidOut;

    /**
     * Holding Delta.
     */
    private final TethysDecimal theHoldingDelta;

    /**
     * Capital Delta.
     */
    private final TethysDecimal theCapitalDelta;

    /**
     * Interest Delta.
     */
    private final TethysDecimal theInterestDelta;

    /**
     * Fee Delta.
     */
    private final TethysDecimal theFeesDelta;

    /**
     * CashBack Delta.
     */
    private final TethysDecimal theCashBackDelta;

    /**
     * BadDebt Delta.
     */
    private final TethysDecimal theBadDebtDelta;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    public CoeusZopaTransaction(final CoeusZopaTransactionParser pParser,
                                final List<String> pFields) throws OceanusException {
        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

        /* Parse the date */
        theDate = pParser.parseDate(myIterator.next());

        /* Obtain description */
        theDesc = myIterator.next();

        /* Determine the transaction type */
        theTransType = determineTransactionType();

        /* Parse the values */
        thePaidIn = pParser.parseDecimal(myIterator.next());
        thePaidOut = pParser.parseDecimal(myIterator.next());

        /* Determine the HoldingDelta */
        theHoldingDelta = new TethysDecimal(thePaidIn);
        theHoldingDelta.subtractValue(thePaidOut);

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

    @Override
    public TethysDecimal getHoldingDelta() {
        return theHoldingDelta;
    }

    @Override
    public TethysDecimal getCapitalDelta() {
        return theCapitalDelta;
    }

    @Override
    public TethysDecimal getInterestDelta() {
        return theInterestDelta;
    }

    @Override
    public TethysDecimal getFeesDelta() {
        return theFeesDelta;
    }

    @Override
    public TethysDecimal getCashBackDelta() {
        return theCashBackDelta;
    }

    @Override
    public TethysDecimal getBadDebtDelta() {
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
            return CoeusTransactionType.CAPITALREPAYMENT;
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

        /* If the description is Fees */
        if (theDesc.startsWith(PFIX_FEES)) {
            return CoeusTransactionType.FEES;
        }

        /* Not recognised */
        throw new CoeusDataException("Unrecognised transaction");
    }

    /**
     * determine capital delta.
     * @return the delta
     */
    private TethysDecimal determineCapitalDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case CAPITALLOAN:
                return new TethysDecimal(thePaidOut);
            case CAPITALREPAYMENT:
                TethysDecimal myCapital = new TethysDecimal(thePaidIn);
                myCapital.negate();
                return myCapital;
            default:
                myCapital = new TethysDecimal(thePaidOut);
                myCapital.setZero();
                return myCapital;
        }
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
                return new TethysDecimal(theHoldingDelta);
            default:
                TethysDecimal myInterest = new TethysDecimal(thePaidOut);
                myInterest.setZero();
                return myInterest;
        }
    }

    /**
     * determine fees delta.
     * @return the delta
     */
    private TethysDecimal determineFeesDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case FEES:
                TethysDecimal myFees = new TethysDecimal(theHoldingDelta);
                myFees.negate();
                return myFees;
            default:
                myFees = new TethysDecimal(thePaidOut);
                myFees.setZero();
                return myFees;
        }
    }

    /**
     * determine cashBack delta.
     * @return the delta
     */
    private TethysDecimal determineCashBackDelta() {
        /* Switch on transactionType */
        switch (theTransType) {
            case CASHBACK:
                return new TethysDecimal(theHoldingDelta);
            default:
                TethysDecimal myCash = new TethysDecimal(thePaidOut);
                myCash.setZero();
                return myCash;
        }
    }

    /**
     * determine badDebt delta.
     * @return the delta
     */
    private TethysDecimal determineBadDebtDelta() {
        TethysDecimal myDebt = new TethysDecimal(thePaidOut);
        myDebt.setZero();
        return myDebt;
    }

    /**
     * determine loanId.
     * @return the id
     */
    private String determineLoanId() {
        /* Switch on transactionType */
        switch (theTransType) {
            case INTEREST:
                return thePrefix == null
                                         ? null
                                         : theDesc.substring(thePrefix.length());
            case CAPITALREPAYMENT:
                return thePrefix == null
                                         ? null
                                         : theDesc.substring(thePrefix.length());
            case CAPITALLOAN:
                String myValue = theDesc.substring(PFIX_LOAN.length());
                int myIndex = myValue.indexOf(' ');
                return myIndex == -1
                                     ? myValue
                                     : myValue.substring(0, myIndex);
            default:
                return null;
        }
    }
}
