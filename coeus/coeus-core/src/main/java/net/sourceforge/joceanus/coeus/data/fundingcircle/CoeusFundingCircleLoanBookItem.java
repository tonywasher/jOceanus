/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.coeus.data.fundingcircle;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.coeus.CoeusDataException;
import net.sourceforge.joceanus.coeus.data.CoeusLoanRisk;
import net.sourceforge.joceanus.coeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.coeus.data.CoeusResource;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * FundingCircle Loan Book Item.
 */
public class CoeusFundingCircleLoanBookItem
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final String BID_SEP = " - ";

    /**
     * Builder buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusFundingCircleLoanBookItem> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusFundingCircleLoanBookItem.class);

    /*
     * Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANID, CoeusFundingCircleLoanBookItem::getLoanId);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_DESC, CoeusFundingCircleLoanBookItem::getDescription);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_AUCTIONID, CoeusFundingCircleLoanBookItem::getAuctionId);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANRISK, CoeusFundingCircleLoanBookItem::getRisk);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BALANCE, CoeusFundingCircleLoanBookItem::getBalance);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_RATE, CoeusFundingCircleLoanBookItem::getRate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANSTATUS, CoeusFundingCircleLoanBookItem::getStatus);
    }

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
    private final OceanusMoney theBalance;

    /**
     * The rate.
     */
    private final OceanusRate theRate;

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
    CoeusFundingCircleLoanBookItem(final CoeusFundingCircleLoanBookParser pParser,
                                   final List<String> pFields) throws OceanusException {
        /* Iterate through the fields */
        final Iterator<String> myIterator = pFields.iterator();

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
    CoeusFundingCircleLoanBookItem(final CoeusFundingCircleBidBookParser pParser,
                                   final List<String> pFields) throws OceanusException {
        /* Iterate through the fields */
        final Iterator<String> myIterator = pFields.iterator();

        /* Obtain IDs */
        theLoanId = null;
        final String myDesc = myIterator.next();
        final int myIndex = myDesc.lastIndexOf(BID_SEP);
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
     * Constructor for merged bookItem.
     * @param pBase the base item
     * @param pNew the new item
     */
    CoeusFundingCircleLoanBookItem(final CoeusFundingCircleLoanBookItem pBase,
                                   final CoeusFundingCircleLoanBookItem pNew) {
        /* Obtain IDs */
        theLoanId = pBase.getLoanId();

        /* Obtain details from the base */
        theRisk = pBase.getRisk();
        theStatus = pBase.getStatus();
        theRate = pBase.getRate();
        theDesc = pBase.getDescription();
        theAuctionId = pBase.getAuctionId();

        /* Calculate the new balance */
        theBalance = new OceanusMoney(pBase.getBalance());
        theBalance.addAmount(pNew.getBalance());
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
    String getAuctionId() {
        return theAuctionId;
    }

    /**
     * Obtain the risk.
     * @return the auction id
     */
    CoeusLoanRisk getRisk() {
        return theRisk;
    }

    /**
     * Obtain the outstanding balance.
     * @return the balance
     */
    public OceanusMoney getBalance() {
        return theBalance;
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
     * determine risk.
     * @param pRisk the risk description
     * @return the risk
     * @throws OceanusException on error
     */
    private static CoeusLoanRisk determineRisk(final String pRisk) throws OceanusException {
        /* If the risk is empty, return unclassified */
        if (pRisk.length() == 0
            || "-".equals(pRisk)) {
            return CoeusLoanRisk.UNCLASSIFIED;
        }

        /* Look for A+ risk */
        if (pRisk.startsWith("A+")) {
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
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append(theLoanId).append(' ').append(theStatus).append(' ').append(theBalance);
        return myBuilder.toString();
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public MetisFieldSet<CoeusFundingCircleLoanBookItem> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
