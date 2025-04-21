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

import net.sourceforge.joceanus.coeus.data.CoeusLoanRisk;
import net.sourceforge.joceanus.coeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.coeus.data.CoeusResource;
import net.sourceforge.joceanus.coeus.exc.CoeusDataException;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Iterator;
import java.util.List;

/**
 * Zopa LoanBook Item.
 */
public class CoeusZopaLoanBookItem
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusZopaLoanBookItem> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusZopaLoanBookItem.class);

    /**
     * Builder buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /*
     * Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANID, CoeusZopaLoanBookItem::getLoanId);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANRISK, CoeusZopaLoanBookItem::getRisk);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_DATE, CoeusZopaLoanBookItem::getDate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_RATE, CoeusZopaLoanBookItem::getRate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANSTATUS, CoeusZopaLoanBookItem::getStatus);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LENT, CoeusZopaLoanBookItem::getLent);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BALANCE, CoeusZopaLoanBookItem::getBalance);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_REPAID, CoeusZopaLoanBookItem::getRepaid);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_CAPITAL, CoeusZopaLoanBookItem::getCapitalRepaid);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_INTEREST, CoeusZopaLoanBookItem::getInterestRepaid);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MISSING, CoeusZopaLoanBookItem::getMissing);
    }

    /**
     * The loan Id.
     */
    private final String theLoanId;

    /**
     * The Acquired Date.
     */
    private final OceanusDate theDate;

    /**
     * The risk.
     */
    private final CoeusLoanRisk theRisk;

    /**
     * The rate.
     */
    private final OceanusRate theRate;

    /**
     * The status.
     */
    private final CoeusLoanStatus theStatus;

    /**
     * The Total Lent.
     */
    private final OceanusDecimal theLent;

    /**
     * The Outstanding Balance.
     */
    private final OceanusDecimal theBalance;

    /**
     * The Total Repaid.
     */
    private final OceanusDecimal theRepaid;

    /**
     * The Capital Repaid.
     */
    private final OceanusDecimal theCapital;

    /**
     * The Interest Repaid.
     */
    private final OceanusDecimal theInterest;

    /**
     * The Arrears.
     */
    private final OceanusDecimal theArrears;

    /**
     * The Missing.
     */
    private final OceanusDecimal theMissing;

    /**
     * The portion repaid.
     */
    private final OceanusRate thePortionRepaid;

    /**
     * The badDebt date.
     */
    private final OceanusDate theBadDebtDate;

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
    CoeusZopaLoanBookItem(final CoeusZopaLoanBookParser pParser,
                          final List<String> pFields) throws OceanusException {
        /* Iterate through the fields */
        final Iterator<String> myIterator = pFields.iterator();

        /* Obtain IDs */
        theLoanId = myIterator.next();

        /* Skip Product */
        myIterator.next();

        /* Obtain date acquired */
        theDate = pParser.parseDate(myIterator.next());

        /* Derive the risk */
        theRisk = determineRisk(myIterator.next());

        /* Skip Type of Loan, Term and Loan Size */
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Derive the status */
        theStatus = determineStatus(myIterator.next());

        /* Skip Borrower Rate/Borrower Fee/Loan Servicing Fee */
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Parse the rate */
        theRate = pParser.parseRate(myIterator.next());

        /* Skip Transaction Price/Date */
        myIterator.next();
        myIterator.next();

        /* Parse the outstanding balances */
        theLent = pParser.parseDecimal(myIterator.next());
        theBalance = pParser.parseDecimal(myIterator.next());
        myIterator.next(); // Interest Outstanding
        theRepaid = pParser.parseDecimal(myIterator.next());
        theCapital = pParser.parseDecimal(myIterator.next());
        theInterest = pParser.parseDecimal(myIterator.next());
        theArrears = pParser.parseDecimal(myIterator.next());

        /* Determine any missing capital */
        theMissing = new OceanusDecimal(theLent);
        theMissing.subtractValue(theCapital);
        theMissing.subtractValue(theBalance);

        /* Check that repaid is the total of capital and interest */
        final OceanusDecimal myRepaid = new OceanusDecimal(theRepaid);
        myRepaid.subtractValue(theCapital);
        myRepaid.subtractValue(theInterest);
        if (myRepaid.isNonZero()) {
            throw new CoeusDataException("Repaid is not the sum of capital and interest");
        }

        /* Add to the total missing book */
        pParser.getMarket().recordMissingBook(theMissing);

        /* Skip Days In Arrears/Payment Day */
        myIterator.next();
        myIterator.next();

        /* Determine whether the loan is safeGuarded */
        isSafeGuarded = Boolean.parseBoolean(myIterator.next());

        /* Skip Comment and loan start/end dates/Default Price */
        myIterator.next();
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Skip the default date */
        final String myDate = myIterator.next();
        theBadDebtDate = myDate.isEmpty()
                ? null
                : pParser.parseDate(myDate);

        /* Skip Monthly rePayment/Type of Asset and Purpose */
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Parse the portion repaid */
        thePortionRepaid = pParser.parseRate(myIterator.next());
    }

    /**
     * Constructor for merged bookItem.
     * @param pBase the base item
     * @param pNew the new item
     */
    CoeusZopaLoanBookItem(final CoeusZopaLoanBookItem pBase,
                          final CoeusZopaLoanBookItem pNew) {
        /* Obtain IDs */
        theLoanId = pBase.getLoanId();

        /* Determine the most recent loan */
        final CoeusZopaLoanBookItem myRecent = pBase.getDate().compareTo(pNew.getDate()) < 0
                                                                                             ? pNew
                                                                                             : pBase;
        /* Copy main details from the most recent loan */
        theDate = myRecent.getDate();
        theRisk = myRecent.getRisk();
        theStatus = myRecent.getStatus();
        theRate = myRecent.getRate();
        thePortionRepaid = myRecent.getPortionRepaid();
        isSafeGuarded = myRecent.isSafeGuarded();
        theBadDebtDate = myRecent.getBadDebtDate();

        /* Parse the outstanding balances */
        theLent = new OceanusDecimal(pBase.getLent());
        theBalance = new OceanusDecimal(pBase.getBalance());
        theRepaid = new OceanusDecimal(pBase.getRepaid());
        theCapital = new OceanusDecimal(pBase.getCapitalRepaid());
        theInterest = new OceanusDecimal(pBase.getInterestRepaid());
        theArrears = new OceanusDecimal(pBase.getArrears());
        theMissing = new OceanusDecimal(pBase.getMissing());

        /* Add the new totals */
        theLent.addValue(pNew.getLent());
        theBalance.addValue(pNew.getBalance());
        theRepaid.addValue(pNew.getRepaid());
        theInterest.addValue(pNew.getInterestRepaid());
        theCapital.addValue(pNew.getCapitalRepaid());
        theArrears.addValue(pNew.getArrears());
        theMissing.addValue(pNew.getMissing());
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
    private CoeusLoanRisk getRisk() {
        return theRisk;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public OceanusDate getDate() {
        return theDate;
    }

    /**
     * Obtain the rate.
     * @return the rate
     */
    public OceanusRate getRate() {
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
    private OceanusDecimal getLent() {
        return theLent;
    }

    /**
     * Obtain the balance.
     * @return the balance
     */
    public OceanusDecimal getBalance() {
        return theBalance;
    }

    /**
     * Obtain the total repaid.
     * @return the repaid
     */
    public OceanusDecimal getRepaid() {
        return theRepaid;
    }

    /**
     * Obtain the capital repaid.
     * @return the capital repaid
     */
    private OceanusDecimal getCapitalRepaid() {
        return theCapital;
    }

    /**
     * Obtain the interest repaid.
     * @return the interest repaid
     */
    OceanusDecimal getInterestRepaid() {
        return theInterest;
    }

    /**
     * Obtain the arrears.
     * @return the arrears
     */
    private OceanusDecimal getArrears() {
        return theArrears;
    }

    /**
     * Obtain the missing.
     * @return the missing capital
     */
    OceanusDecimal getMissing() {
        return theMissing;
    }

    /**
     * Obtain the portion repaid.
     * @return the portion repaid
     */
    private OceanusRate getPortionRepaid() {
        return thePortionRepaid;
    }

    /**
     * Obtain the badDebt date.
     * @return the badDebt date
     */
    OceanusDate getBadDebtDate() {
        return theBadDebtDate;
    }

    /**
     * Is this loan a badDebt.
     * @return true/false
     */
    public boolean isBadDebt() {
        return theBadDebtDate != null;
    }

    /**
     * Is the loan safeguarded?
     * @return true/false
     */
    private boolean isSafeGuarded() {
        return isSafeGuarded;
    }

    /**
     * determine risk.
     * @param pRisk the risk description
     * @return the risk
     * @throws OceanusException on error
     */
    private static CoeusLoanRisk determineRisk(final String pRisk) throws OceanusException {
        /* If the risk is empty, return unclassified */
        if (pRisk.isEmpty()) {
            return CoeusLoanRisk.UNCLASSIFIED;
        }

        /* Look for A+ risk */
        if (pRisk.startsWith("A*")) {
            return CoeusLoanRisk.APLUS;
        }

        /* Look for A risk */
        if (pRisk.charAt(0) == 'A') {
            return CoeusLoanRisk.A;
        }

        /* Look for B risk */
        if (pRisk.charAt(0) == 'B') {
            return CoeusLoanRisk.B;
        }

        /* Look for C risk */
        if (pRisk.charAt(0) == 'C') {
            return CoeusLoanRisk.C;
        }

        /* Look for D risk */
        if (pRisk.charAt(0) == 'D') {
            return CoeusLoanRisk.D;
        }

        /* Look for E risk */
        if (pRisk.charAt(0) == 'E') {
            return CoeusLoanRisk.E;
        }

        /* Look for S risk */
        if (pRisk.charAt(0) == 'S') {
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
    private static CoeusLoanStatus determineStatus(final String pStatus) throws OceanusException {
        /* Look for Offered status */
        if ("WithdrawalPending".equals(pStatus)
            || "Pending".equals(pStatus)) {
            return CoeusLoanStatus.OFFERED;
        }

        /* Look for Active status */
        if ("Withdrawn".equals(pStatus)) {
            return CoeusLoanStatus.ACTIVE;
        }

        /* Look for Poorly status */
        if ("Arrangement".equals(pStatus)
            || "Deceased".equals(pStatus)
            || "Collections".equals(pStatus)
            || "DeferredRepayments".equals(pStatus)
            || "Hardship".equals(pStatus)) {
            return CoeusLoanStatus.ACTIVE;
        }

        /* Look for BadDebt */
        if ("Default".equals(pStatus)
            || "Settled".equals(pStatus)) {
            return CoeusLoanStatus.BADDEBT;
        }

        /* Look for Repaid */
        if ("Closed".equals(pStatus)) {
            return CoeusLoanStatus.REPAID;
        }

        /* Reject the data */
        throw new CoeusDataException(pStatus, "Unrecognised Status");
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append(theLoanId).append(' ').append(theStatus).append(",B=").append(theBalance);
        if (theMissing.isNonZero()) {
            myBuilder.append(",M=").append(theMissing);
        }
        return myBuilder.toString();
    }

    @Override
    public MetisFieldSet<CoeusZopaLoanBookItem> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
