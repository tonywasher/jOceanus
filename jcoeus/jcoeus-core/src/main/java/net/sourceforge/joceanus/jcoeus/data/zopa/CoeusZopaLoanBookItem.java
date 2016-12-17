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
package net.sourceforge.joceanus.jcoeus.data.zopa;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanRisk;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Zopa LoanBook Item.
 */
public class CoeusZopaLoanBookItem
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusZopaLoanBookItem.class.getSimpleName());

    /**
     * Loan Id Field Id.
     */
    private static final MetisField FIELD_LOANID = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANID.getValue());

    /**
     * Risk Field Id.
     */
    private static final MetisField FIELD_RISK = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANRISK.getValue());

    /**
     * Rate Field Id.
     */
    private static final MetisField FIELD_RATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_RATE.getValue());

    /**
     * Status Field Id.
     */
    private static final MetisField FIELD_STATUS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANSTATUS.getValue());

    /**
     * Original Loan Field Id.
     */
    private static final MetisField FIELD_LENT = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LENT.getValue());

    /**
     * Balance Field Id.
     */
    private static final MetisField FIELD_BALANCE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BALANCE.getValue());

    /**
     * Missing Field Id.
     */
    private static final MetisField FIELD_MISSING = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MISSING.getValue());

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

        /* Determine any missing capital */
        theMissing = new TethysDecimal(theLent);
        theMissing.subtractValue(theCapital);
        theMissing.subtractValue(theBalance);

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

        /* Skip Monthly rePayment and Purpose */
        myIterator.next();
        myIterator.next();

        /* Parse the rate */
        thePortionRepaid = pParser.parseRate(myIterator.next());
    }

    /**
     * Constructor.
     * @param pSource the source item
     */
    protected CoeusZopaLoanBookItem(final CoeusZopaLoanBookItem pSource) {
        /* Obtain IDs */
        theLoanId = pSource.getLoanId();

        /* Risk varies */
        theRisk = pSource.getRisk();

        /* Derive the status */
        theStatus = pSource.getStatus();

        /* Parse the rate */
        theRate = pSource.getRate();

        /* Parse the outstanding balances */
        theLent = new TethysDecimal(pSource.getLent());
        theBalance = new TethysDecimal(pSource.getBalance());
        theRepaid = new TethysDecimal(pSource.getRepaid());
        theCapital = new TethysDecimal(pSource.getCapitalRepaid());
        theInterest = new TethysDecimal(pSource.getInterestRepaid());
        theArrears = new TethysDecimal(pSource.getArrears());
        theMissing = new TethysDecimal(pSource.getMissing());

        /* Determine whether the loan is safeGuarded */
        isSafeGuarded = false;

        /* Parse the rate */
        thePortionRepaid = null;
    }

    /**
     * Add bookItem.
     * @param pSource the source item
     */
    protected void addBookItem(final CoeusZopaLoanBookItem pSource) {
        theLent.addValue(pSource.getLent());
        theBalance.addValue(pSource.getBalance());
        theRepaid.addValue(pSource.getRepaid());
        theInterest.addValue(pSource.getInterestRepaid());
        theCapital.addValue(pSource.getCapitalRepaid());
        theArrears.addValue(pSource.getArrears());
        theMissing.addValue(pSource.getMissing());
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
    public String formatObject() {
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
    public String toString() {
        return formatObject();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
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
        if (FIELD_MISSING.equals(pField)) {
            return theMissing.isZero()
                                       ? MetisFieldValue.SKIP
                                       : theMissing;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
