/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.zopa;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanRisk;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Zopa LoanBook Item.
 */
public class CoeusZopaLoanBookItem
        implements MetisDataFieldItem {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusZopaLoanBookItem.class);

    /**
     * Loan Id Field Id.
     */
    private static final MetisDataField FIELD_LOANID = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANID.getValue());

    /**
     * Risk Field Id.
     */
    private static final MetisDataField FIELD_RISK = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANRISK.getValue());

    /**
     * Rate Field Id.
     */
    private static final MetisDataField FIELD_RATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_RATE.getValue());

    /**
     * Status Field Id.
     */
    private static final MetisDataField FIELD_STATUS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANSTATUS.getValue());

    /**
     * Original Loan Field Id.
     */
    private static final MetisDataField FIELD_LENT = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LENT.getValue());

    /**
     * Balance Field Id.
     */
    private static final MetisDataField FIELD_BALANCE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BALANCE.getValue());

    /**
     * Repaid Field Id.
     */
    private static final MetisDataField FIELD_REPAID = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_REPAID.getValue());

    /**
     * Capital Field Id.
     */
    private static final MetisDataField FIELD_CAPITAL = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_CAPITAL.getValue());

    /**
     * Interest Field Id.
     */
    private static final MetisDataField FIELD_INTEREST = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_INTEREST.getValue());

    /**
     * Missing Field Id.
     */
    private static final MetisDataField FIELD_MISSING = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MISSING.getValue());

    /**
     * The loan Id.
     */
    private final String theLoanId;

    /**
     * The Acquired Date.
     */
    private final TethysDate theDate;

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
     * The Missing.
     */
    private final TethysDecimal theMissing;

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
    protected CoeusZopaLoanBookItem(final CoeusZopaLoanBookParser pParser,
                                    final List<String> pFields) throws OceanusException {
        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

        /* Obtain IDs */
        theLoanId = myIterator.next();

        /* Skip Product */
        myIterator.next();

        /* Obtain date acquired */
        theDate = pParser.parseDate(myIterator.next());

        /* Derive the risk */
        theRisk = determineRisk(myIterator.next());

        /* Skip Term and Loan Size */
        myIterator.next();
        myIterator.next();

        /* Derive the status */
        theStatus = determineStatus(myIterator.next());

        /* Parse the rate */
        theRate = pParser.parseRate(myIterator.next());

        /* Skip Borrower Rate */
        myIterator.next();

        /* Parse the outstanding balances */
        theLent = pParser.parseDecimal(myIterator.next());
        theBalance = pParser.parseDecimal(myIterator.next());
        theRepaid = pParser.parseDecimal(myIterator.next());
        theCapital = pParser.parseDecimal(myIterator.next());
        theInterest = pParser.parseDecimal(myIterator.next());
        theArrears = pParser.parseDecimal(myIterator.next());

        /* Determine any missing capital */
        theMissing = new TethysDecimal(theLent);
        theMissing.subtractValue(theCapital);
        theMissing.subtractValue(theBalance);

        /* Check that repaid is the total of capital and interest */
        TethysDecimal myRepaid = new TethysDecimal(theRepaid);
        myRepaid.subtractValue(theCapital);
        myRepaid.subtractValue(theInterest);
        if (myRepaid.isNonZero()) {
            throw new CoeusDataException("Repaid is not the sum of capital and interest");
        }

        /* Add to the total missing book */
        pParser.getMarket().recordMissingBook(theMissing);

        /* Skip Payment Day */
        myIterator.next();

        /* Determine whether the loan is safeGuarded */
        isSafeGuarded = Boolean.valueOf(myIterator.next());

        /* Skip Comment and loan start/end dates */
        myIterator.next();
        myIterator.next();
        myIterator.next();

        /* Skip the default date */
        myIterator.next();

        /* Skip Monthly rePayment and Purpose */
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
    protected CoeusZopaLoanBookItem(final CoeusZopaLoanBookItem pBase,
                                    final CoeusZopaLoanBookItem pNew) {
        /* Obtain IDs */
        theLoanId = pBase.getLoanId();

        /* Determine the most recent loan */
        CoeusZopaLoanBookItem myRecent = pBase.getDate().compareTo(pNew.getDate()) < 0
                                                                                       ? pNew
                                                                                       : pBase;
        /* Copy main details from the most recent loan */
        theDate = myRecent.getDate();
        theRisk = myRecent.getRisk();
        theStatus = myRecent.getStatus();
        theRate = myRecent.getRate();
        thePortionRepaid = myRecent.getPortionRepaid();
        isSafeGuarded = myRecent.isSafeGuarded();

        /* Parse the outstanding balances */
        theLent = new TethysDecimal(pBase.getLent());
        theBalance = new TethysDecimal(pBase.getBalance());
        theRepaid = new TethysDecimal(pBase.getRepaid());
        theCapital = new TethysDecimal(pBase.getCapitalRepaid());
        theInterest = new TethysDecimal(pBase.getInterestRepaid());
        theArrears = new TethysDecimal(pBase.getArrears());
        theMissing = new TethysDecimal(pBase.getMissing());

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
    public CoeusLoanRisk getRisk() {
        return theRisk;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public TethysDate getDate() {
        return theDate;
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
     * Obtain the missing.
     * @return the missing capital
     */
    public TethysDecimal getMissing() {
        return theMissing;
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
    private static CoeusLoanRisk determineRisk(final String pRisk) throws OceanusException {
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
    private static CoeusLoanStatus determineStatus(final String pStatus) throws OceanusException {
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
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theLoanId);
        myBuilder.append(' ');
        myBuilder.append(theStatus.toString());
        myBuilder.append(",B=");
        myBuilder.append(theBalance.toString());
        if (theMissing.isNonZero()) {
            myBuilder.append(",M=");
            myBuilder.append(theMissing.toString());
        }
        return myBuilder.toString();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_LOANID.equals(pField)) {
            return theLoanId;
        }
        if (FIELD_RISK.equals(pField)) {
            return theRisk;
        }
        if (FIELD_RATE.equals(pField)) {
            return theRate;
        }
        if (FIELD_STATUS.equals(pField)) {
            return theStatus;
        }
        if (FIELD_LENT.equals(pField)) {
            return theLent;
        }
        if (FIELD_BALANCE.equals(pField)) {
            return theBalance;
        }
        if (FIELD_REPAID.equals(pField)) {
            return theRepaid;
        }
        if (FIELD_CAPITAL.equals(pField)) {
            return theCapital;
        }
        if (FIELD_INTEREST.equals(pField)) {
            return theInterest;
        }
        if (FIELD_MISSING.equals(pField)) {
            return theMissing.isZero()
                                       ? MetisDataFieldValue.SKIP
                                       : theMissing;
        }

        /* Not recognised */
        return MetisDataFieldValue.UNKNOWN;
    }
}
