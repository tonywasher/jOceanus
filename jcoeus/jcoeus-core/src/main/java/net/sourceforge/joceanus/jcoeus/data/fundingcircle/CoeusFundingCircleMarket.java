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

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * FundingCircle Market.
 */
public class CoeusFundingCircleMarket
        extends CoeusMarket {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusFundingCircleMarket.class, CoeusMarket.getBaseFieldSet());

    /**
     * AuctionMap Field Id.
     */
    private static final MetisDataField FIELD_AUCTIONS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_AUCTIONMAP.getValue());

    /**
     * The LoanBook Parser.
     */
    private final CoeusFundingCircleLoanBookParser theBookParser;

    /**
     * The Bids Parser.
     */
    private final CoeusFundingCircleBidBookParser theBidsParser;

    /**
     * The BadDebt Parser.
     */
    private final CoeusFundingCircleBadDebtParser theDebtParser;

    /**
     * The Transaction Parser.
     */
    private final CoeusFundingCircleTransactionParser theXactionParser;

    /**
     * The Map of auctionId to BookItem.
     */
    private final Map<String, CoeusFundingCircleLoan> theAuctionMap;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public CoeusFundingCircleMarket(final MetisDataFormatter pFormatter) {
        /* Initialise underlying class */
        super(pFormatter, CoeusMarketProvider.FUNDINGCIRCLE);

        /* Create the parsers */
        theBookParser = new CoeusFundingCircleLoanBookParser(pFormatter);
        theBidsParser = new CoeusFundingCircleBidBookParser(this);
        theDebtParser = new CoeusFundingCircleBadDebtParser(this);
        theXactionParser = new CoeusFundingCircleTransactionParser(this);

        /* Create the bookItem map */
        theAuctionMap = new LinkedHashMap<>();
    }

    /**
     * Parse the loanBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseLoanBook(final Path pFile) throws OceanusException {
        /* Parse the file */
        theBookParser.parseFile(pFile);

        /* Loop through the loan book items */
        final Iterator<CoeusFundingCircleLoanBookItem> myIterator = theBookParser.loanIterator();
        while (myIterator.hasNext()) {
            final CoeusFundingCircleLoanBookItem myItem = myIterator.next();

            /* Create the loan and record it */
            final CoeusFundingCircleLoan myLoan = new CoeusFundingCircleLoan(this, myItem);
            recordLoan(myLoan);
            theAuctionMap.put(myItem.getAuctionId(), myLoan);
        }
    }

    /**
     * Parse the badDebtBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseBadDebtBook(final Path pFile) throws OceanusException {
        /* Parse the file */
        theDebtParser.parseFile(pFile);

        /* Loop through the loan book items */
        final Iterator<CoeusFundingCircleTransaction> myIterator = theDebtParser.badDebtIterator();
        while (myIterator.hasNext()) {
            final CoeusFundingCircleTransaction myTrans = myIterator.next();

            /* Add to the transactions */
            addTransaction(myTrans);
        }
    }

    /**
     * Parse the badDebtBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseBidBook(final Path pFile) throws OceanusException {
        /* Parse the file */
        theBidsParser.parseFile(pFile);

        /* Loop through the loan book items */
        final Iterator<CoeusFundingCircleLoanBookItem> myIterator = theBidsParser.bidIterator();
        while (myIterator.hasNext()) {
            final CoeusFundingCircleLoanBookItem myItem = myIterator.next();

            /* If the loan is not rejected, add to the list */
            if (myItem.getStatus() != CoeusLoanStatus.REJECTED) {
                /* Create the loan and record it */
                final CoeusFundingCircleLoan myLoan = new CoeusFundingCircleLoan(this, myItem);
                recordLoan(myLoan);
                theAuctionMap.put(myItem.getAuctionId(), myLoan);
            }
        }
    }

    /**
     * Parse the statement file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseStatement(final Path pFile) throws OceanusException {
        /* Parse the file */
        theXactionParser.parseFile(pFile);

        /* Loop through the transactions in normal order */
        final Iterator<CoeusFundingCircleTransaction> myIterator = theXactionParser.transactionIterator();
        while (myIterator.hasNext()) {
            final CoeusFundingCircleTransaction myTrans = myIterator.next();

            /* Add to the transactions */
            addTransaction(myTrans);
        }
    }

    /**
     * LookUp Loan by auction id.
     * @param pId the id of the loan
     * @return the loan
     * @throws OceanusException on error
     */
    protected CoeusFundingCircleLoan findLoanByAuctionId(final String pId) throws OceanusException {
        final CoeusFundingCircleLoan myLoan = theAuctionMap.get(pId);
        if (myLoan == null) {
            throw new CoeusDataException(pId, "Unrecognised AuctionId");
        }
        return myLoan;
    }

    @Override
    public boolean usesDecimalTotals() {
        return false;
    }

    @Override
    public boolean hasBadDebt() {
        return true;
    }

    @Override
    protected CoeusFundingCircleTotals newTotals() {
        return new CoeusFundingCircleTotals(this);
    }

    @Override
    protected CoeusFundingCircleHistory newHistory() {
        return new CoeusFundingCircleHistory(this);
    }

    @Override
    protected CoeusFundingCircleHistory newHistory(final TethysDate pDate) {
        return new CoeusFundingCircleHistory(this, pDate);
    }

    @Override
    protected CoeusFundingCircleLoan newLoan(final String pId) {
        return new CoeusFundingCircleLoan(this, pId);
    }

    @Override
    public CoeusFundingCircleLoan findLoanById(final String pId) throws OceanusException {
        return (CoeusFundingCircleLoan) super.findLoanById(pId);
    }

    @Override
    public void analyseMarket() throws OceanusException {
        /* create and check the analysis */
        createAnalysis();
        checkLoans();
    }

    @Override
    public String toString() {
        return FIELD_DEFS.getName();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_AUCTIONS.equals(pField)) {
            return theAuctionMap;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }
}
