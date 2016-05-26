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
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanRisk;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Zopa LoanBook Item.
 */
public class CoeusZopaLoanBookItem {
    /**
     * The loan Id.
     */
    private final String theLoanId;

    /**
     * The risk.
     */
    private final CoeusLoanRisk theRisk;

    /**
     * The rate.
     */
    private final TethysRate theRate;

    /**
     * The status.
     */
    private final CoeusLoanStatus theStatus;

    /**
     * The Total Lent.
     */
    private final TethysDecimal theLent;

    /**
     * The Outstanding Balance.
     */
    private final TethysDecimal theBalance;

    /**
     * The Total Repaid.
     */
    private final TethysDecimal theRepaid;

    /**
     * The Capital Repaid.
     */
    private final TethysDecimal theCapital;

    /**
     * The Interest Repaid.
     */
    private final TethysDecimal theInterest;

    /**
     * The Arrears.
     */
    private final TethysDecimal theArrears;

    /**
     * The portion repaid.
     */
    private final TethysRate thePortionRepaid;

    /**
     * Is the loan safeGuarded.
     */
    private final boolean isSafeGuarded;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    public CoeusZopaLoanBookItem(final CoeusZopaLoanBookParser pParser,
                                 final List<String> pFields) throws OceanusException {
        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

        /* Obtain IDs */
        theLoanId = myIterator.next();

        /* Skip Product and date acquired */
        myIterator.next();
        myIterator.next();

        /* Derive the risk */
        theRisk = determineRisk(myIterator.next());

        /* Skip Term and Loan Size */
        myIterator.next();
        myIterator.next();

        /* Derive the status */
        theStatus = determineStatus(myIterator.next());

        /* Parse the rate */
        theRate = pParser.parseRate(myIterator.next());

        /* Parse the outstanding balances */
        theLent = pParser.parseDecimal(myIterator.next());
        theBalance = pParser.parseDecimal(myIterator.next());
        theRepaid = pParser.parseDecimal(myIterator.next());
        theCapital = pParser.parseDecimal(myIterator.next());
        theInterest = pParser.parseDecimal(myIterator.next());
        theArrears = pParser.parseDecimal(myIterator.next());

        /* Skip Payment Day */
        myIterator.next();

        /* Determine whether the loan is safeGuarded */
        isSafeGuarded = Boolean.valueOf(myIterator.next());

        /* Skip Comment and loan start/end dates */
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Skip Monthly rePayment and Purpose */
        myIterator.next();
        myIterator.next();

        /* Parse the rate */
        thePortionRepaid = pParser.parseRate(myIterator.next());
    }

    /**
     * Obtain the loanId.
     * @return the loan id
     */
    public String getLoanId() {
        return theLoanId;
    }

    /**
     * Obtain the risk.
     * @return the auction id
     */
    public CoeusLoanRisk getRisk() {
        return theRisk;
    }

    /**
     * Obtain the rate.
     * @return the rate
     */
    public TethysRate getRate() {
        return theRate;
    }

    /**
     * Obtain the status.
     * @return the status
     */
    public CoeusLoanStatus getStatus() {
        return theStatus;
    }

    /**
     * Obtain the amount lent.
     * @return the balance
     */
    public TethysDecimal getLent() {
        return theLent;
    }

    /**
     * Obtain the balance.
     * @return the balance
     */
    public TethysDecimal getBalance() {
        return theBalance;
    }

    /**
     * Obtain the total repaid.
     * @return the repaid
     */
    public TethysDecimal getRepaid() {
        return theRepaid;
    }

    /**
     * Obtain the capital repaid.
     * @return the capital repaid
     */
    public TethysDecimal getCapitalRepaid() {
        return theCapital;
    }

    /**
     * Obtain the interest repaid.
     * @return the interest repaid
     */
    public TethysDecimal getInterestRepaid() {
        return theInterest;
    }

    /**
     * Obtain the arrears.
     * @return the arrears
     */
    public TethysDecimal getArrears() {
        return theArrears;
    }

    /**
     * Obtain the portion repaid.
     * @return the portion repaid
     */
    public TethysRate getPortionRepaid() {
        return thePortionRepaid;
    }

    /**
     * Is the loan safeguarded?
     * @return true/false
     */
    public boolean isSafeGuarded() {
        return isSafeGuarded;
    }

    /**
     * determine risk.
     * @param pRisk the risk description
     * @return the risk
     * @throws OceanusException on error
     */
    private CoeusLoanRisk determineRisk(final String pRisk) throws OceanusException {
        /* If the risk is empty, return unclassified */
        if (pRisk.length() == 0) {
            return CoeusLoanRisk.UNCLASSIFIED;
        }

        /* Look for A+ risk */
        if (pRisk.startsWith("A*")) {
            return CoeusLoanRisk.APLUS;
        }

        /* Look for A risk */
        if (pRisk.startsWith("A")) {
            return CoeusLoanRisk.A;
        }

        /* Look for B risk */
        if (pRisk.startsWith("B")) {
            return CoeusLoanRisk.B;
        }

        /* Look for C risk */
        if (pRisk.startsWith("C")) {
            return CoeusLoanRisk.C;
        }

        /* Look for D risk */
        if (pRisk.startsWith("D")) {
            return CoeusLoanRisk.D;
        }

        /* Look for E risk */
        if (pRisk.startsWith("E")) {
            return CoeusLoanRisk.E;
        }

        /* Look for S risk */
        if (pRisk.startsWith("S")) {
            return CoeusLoanRisk.S;
        }

        /* Reject the data */
        throw new CoeusDataException(pRisk, "Unrecognised Risk");
    }

    /**
     * determine status.
     * @param pStatus the status description
     * @return the status
     * @throws OceanusException on error
     */
    private CoeusLoanStatus determineStatus(final String pStatus) throws OceanusException {
        /* Look for Offered status */
        if ("WithdrawalPending".equals(pStatus)) {
            return CoeusLoanStatus.OFFERED;
        }

        /* Look for Active status */
        if ("Withdrawn".equals(pStatus)) {
            return CoeusLoanStatus.ACTIVE;
        }

        /* Look for Poorly status */
        if ("Arrangement".equals(pStatus)
            || "Collections".equals(pStatus)
            || "Hardship".equals(pStatus)) {
            return CoeusLoanStatus.ACTIVE;
        }

        /* Look for BadDebt */
        if ("Default".equals(pStatus)) {
            return CoeusLoanStatus.BADDEBT;
        }

        /* Look for Repaid */
        if ("Closed".equals(pStatus)) {
            return CoeusLoanStatus.REPAID;
        }

        /* Reject the data */
        throw new CoeusDataException(pStatus, "Unrecognised Status");
    }
}
