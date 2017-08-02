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
package net.sourceforge.joceanus.jcoeus.data.fundingcircle;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanRisk;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * FundingCircle Loan Book Item.
 */
public class CoeusFundingCircleLoanBookItem
        implements MetisDataFieldItem {
    /**
     * Report fields.
     */
    private static final String BID_SEP = " - ";

    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusFundingCircleLoanBookItem.class);

    /**
     * Loan Id Field Id.
     */
    private static final MetisDataField FIELD_LOANID = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANID.getValue());

    /**
     * Description Field Id.
     */
    private static final MetisDataField FIELD_DESC = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_DESC.getValue());

    /**
     * AuctionId Field Id.
     */
    private static final MetisDataField FIELD_AUCTIONID = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_AUCTIONID.getValue());

    /**
     * Risk Field Id.
     */
    private static final MetisDataField FIELD_RISK = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANRISK.getValue());

    /**
     * Outstanding Balance Field Id.
     */
    private static final MetisDataField FIELD_BALANCE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BALANCE.getValue());

    /**
     * Rate Field Id.
     */
    private static final MetisDataField FIELD_RATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_RATE.getValue());

    /**
     * Status Field Id.
     */
    private static final MetisDataField FIELD_STATUS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANSTATUS.getValue());

    /**
     * The loan Id.
     */
    private final String theLoanId;

    /**
     * The description.
     */
    private final String theDesc;

    /**
     * The auction Id.
     */
    private final String theAuctionId;

    /**
     * The risk.
     */
    private final CoeusLoanRisk theRisk;

    /**
     * The Outstanding Balance.
     */
    private final TethysMoney theBalance;

    /**
     * The rate.
     */
    private final TethysRate theRate;

    /**
     * The status.
     */
    private final CoeusLoanStatus theStatus;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    protected CoeusFundingCircleLoanBookItem(final CoeusFundingCircleLoanBookParser pParser,
                                             final List<String> pFields) throws OceanusException {
        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

        /* Obtain part ID and description */
        theLoanId = myIterator.next();
        theDesc = myIterator.next();

        /* Skip the sector */
        myIterator.next();

        /* Obtain auction ID */
        theAuctionId = myIterator.next();

        /* Derive the risk */
        theRisk = determineRisk(myIterator.next());

        /* Not interested in number of payments left */
        myIterator.next();

        /* Parse the outstanding balance */
        theBalance = pParser.parseMoney(myIterator.next());

        /* Parse the rate */
        theRate = pParser.parseRate(myIterator.next());

        /* Not interested in date of next payment */
        myIterator.next();

        /* Derive the status */
        theStatus = determineStatus(myIterator.next());
    }

    /**
     * Constructor.
     * @param pParser the parser
     * @param pFields the fields
     * @throws OceanusException on error
     */
    protected CoeusFundingCircleLoanBookItem(final CoeusFundingCircleBidBookParser pParser,
                                             final List<String> pFields) throws OceanusException {
        /* Iterate through the fields */
        Iterator<String> myIterator = pFields.iterator();

        /* Obtain IDs */
        theLoanId = null;
        String myDesc = myIterator.next();
        int myIndex = myDesc.lastIndexOf(BID_SEP);
        theDesc = myDesc.substring(0, myIndex);
        theAuctionId = myDesc.substring(myIndex + BID_SEP.length());

        /* Derive the risk */
        theRisk = determineRisk(myIterator.next());

        /* Not interested in %Funded */
        myIterator.next();

        /* Parse the bid amount */
        theBalance = pParser.parseMoney(myIterator.next());

        /* Parse the rate */
        theRate = pParser.parseRate(myIterator.next());

        /* Not interested in bidTime/time left */
        myIterator.next();
        myIterator.next();

        /* Derive the status */
        theStatus = determineStatus(myIterator.next());
    }

    /**
     * Obtain the loanId.
     * @return the loan id
     */
    public String getLoanId() {
        return theLoanId;
    }

    /**
     * Obtain the title.
     * @return the title
     */
    public String getDescription() {
        return theDesc;
    }

    /**
     * Obtain the auctionId.
     * @return the auction id
     */
    public String getAuctionId() {
        return theAuctionId;
    }

    /**
     * Obtain the risk.
     * @return the auction id
     */
    public CoeusLoanRisk getRisk() {
        return theRisk;
    }

    /**
     * Obtain the outstanding balance.
     * @return the balance
     */
    public TethysMoney getBalance() {
        return theBalance;
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
     * determine risk.
     * @param pRisk the risk description
     * @return the risk
     * @throws OceanusException on error
     */
    private static CoeusLoanRisk determineRisk(final String pRisk) throws OceanusException {
        /* If the risk is empty, return unclassified */
        if ((pRisk.length() == 0)
            || "-".equals(pRisk)) {
            return CoeusLoanRisk.UNCLASSIFIED;
        }

        /* Look for A+ risk */
        if (pRisk.startsWith("A+")) {
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
        /* Look for Active status */
        if ("Live".equals(pStatus)
            || "Processing".equals(pStatus)) {
            return CoeusLoanStatus.ACTIVE;
        }

        /* Look for Poor Health */
        if ("Late".equals(pStatus)) {
            return CoeusLoanStatus.POORLY;
        }

        /* Look for BadDebt */
        if ("Bad Debt".equals(pStatus)) {
            return CoeusLoanStatus.BADDEBT;
        }

        /* Look for Repaid */
        if ("Repaid".equals(pStatus)) {
            return CoeusLoanStatus.REPAID;
        }

        /* Look for Rejected */
        if ("Rejected".equals(pStatus)) {
            return CoeusLoanStatus.REJECTED;
        }

        /* Reject the data */
        throw new CoeusDataException(pStatus, "Unrecognised Status");
    }

    @Override
    public String toString() {
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theLoanId);
        myBuilder.append(' ');
        myBuilder.append(theStatus.toString());
        myBuilder.append(' ');
        myBuilder.append(theBalance.toString());
        return myBuilder.toString();
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
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
        if (FIELD_DESC.equals(pField)) {
            return theDesc;
        }
        if (FIELD_AUCTIONID.equals(pField)) {
            return theAuctionId;
        }
        if (FIELD_RISK.equals(pField)) {
            return theRisk;
        }
        if (FIELD_BALANCE.equals(pField)) {
            return theBalance;
        }
        if (FIELD_RATE.equals(pField)) {
            return theRate;
        }
        if (FIELD_STATUS.equals(pField)) {
            return theStatus;
        }

        /* Not recognised */
        return MetisDataFieldValue.UNKNOWN;
    }
}
