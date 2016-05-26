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

import net.sourceforge.joceanus.jcoeus.data.CoeusTransaction;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * RateSetter Transaction.
 */
public class CoeusRateSetterTransaction
        implements CoeusTransaction {
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
     * Full Repayment prefix.
     */
    private static final String PFIX_FULLCAPITAL = "Repaid loan capital";

    /**
     * Fees prefix.
     */
    private static final String PFIX_FEES = "RateSetter lender fee";

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
     * Loan Id.
     */
    private final String theLoanId;

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
     * Zero Delta.
     */
    private final TethysMoney theZeroDelta;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    public CoeusRateSetterTransaction(final CoeusRateSetterTransactionParser pParser,
                                      final List<String> pFields) throws OceanusException {
        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

        /* Protect against exceptions */
        try {
            /* Parse the date */
            theDate = pParser.parseDate(myIterator.next());

            /* Store IDs */
            theLoanType = myIterator.next();
            theDesc = myIterator.next();
            String myId = myIterator.next();
            theLoanId = myId.length() > 0
                                          ? myId
                                          : null;

            /* Determine the transaction type */
            theTransType = determineTransactionType();

            /* Parse the monetary values */
            theHoldingDelta = pParser.parseMoney(myIterator.next());
            TethysMoney myCapital = pParser.parseMoney(myIterator.next());
            theInterestDelta = pParser.parseMoney(myIterator.next());

            /* Handle fees delta */
            theFeesDelta = pParser.parseMoney(myIterator.next());
            theFeesDelta.negate();

            /* Handle CapitalDelta correctly */
            theCapitalDelta = CoeusTransactionType.CAPITALLOAN.equals(theTransType)
                                                                                    ? new TethysMoney(theHoldingDelta)
                                                                                    : myCapital;
            theCapitalDelta.negate();

            /* Handle ZeroDelta correctly */
            theZeroDelta = new TethysMoney(theHoldingDelta);
            theZeroDelta.setZero();

            /* Catch exceptions */
        } catch (IllegalArgumentException e) {
            throw new TethysDataException("Invalid Record", e);
        }
    }

    /**
     * Obtain the date.
     * @return the date
     */
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

    /**
     * Obtain the Description.
     * @return the description
     */
    public String getDescription() {
        return theDesc;
    }

    /**
     * Obtain the transactionType.
     * @return the transactionType
     */
    public CoeusTransactionType getTransType() {
        return theTransType;
    }

    /**
     * Obtain the loanId.
     * @return the loanId
     */
    public String getLoanId() {
        return theLoanId;
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
        return theZeroDelta;
    }

    @Override
    public TethysMoney getBadDebtDelta() {
        return theZeroDelta;
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
}
