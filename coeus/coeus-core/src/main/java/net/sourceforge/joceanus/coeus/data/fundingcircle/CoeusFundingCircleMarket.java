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

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.coeus.CoeusDataException;
import net.sourceforge.joceanus.coeus.data.CoeusHistory;
import net.sourceforge.joceanus.coeus.data.CoeusLoan;
import net.sourceforge.joceanus.coeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.coeus.data.CoeusMarket;
import net.sourceforge.joceanus.coeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.coeus.data.CoeusResource;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * FundingCircle Market.
 */
public class CoeusFundingCircleMarket
        extends CoeusMarket {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusFundingCircleMarket> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusFundingCircleMarket.class);

    /*
     * AuctionMap Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_AUCTIONMAP, CoeusFundingCircleMarket::getAuctionMap);
    }

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
    CoeusFundingCircleMarket(final TethysUIDataFormatter pFormatter) {
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
    void parseLoanBook(final Path pFile) throws OceanusException {
        /* Parse the file */
        theBookParser.parseFile(pFile);

        /* Loop through the loan book items */
        final Iterator<CoeusFundingCircleLoanBookItem> myIterator = theBookParser.loanIterator();
        while (myIterator.hasNext()) {
            final CoeusFundingCircleLoanBookItem myItem = myIterator.next();

            /* Process the bookItem */
            processBookItem(myItem);
        }
    }

    /**
     * Parse the badDebtBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    void parseBadDebtBook(final Path pFile) throws OceanusException {
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
     * Parse the bidBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    void parseBidBook(final Path pFile) throws OceanusException {
        /* Parse the file */
        theBidsParser.parseFile(pFile);

        /* Loop through the loan book items */
        final Iterator<CoeusFundingCircleLoanBookItem> myIterator = theBidsParser.bidIterator();
        while (myIterator.hasNext()) {
            final CoeusFundingCircleLoanBookItem myItem = myIterator.next();

            /* If the loan is not rejected, add to the list */
            if (myItem.getStatus() != CoeusLoanStatus.REJECTED) {
                /* Process the bookItem */
                processBookItem(myItem);
            }
        }
    }

    /**
     * Process bookItem.
     * @param pItem the bookItem to process
     * @throws OceanusException on error
     */
    private void processBookItem(final CoeusFundingCircleLoanBookItem pItem) throws OceanusException {
        /* Check to see whether this is a second loanPart */
        final String myAuctionId = pItem.getAuctionId();
        CoeusFundingCircleLoan myLoan = theAuctionMap.get(myAuctionId);

        /* If this is a second loanPart */
        if (myLoan != null) {
            /* Merge the bookItems */
            myLoan.addBookItem(pItem);
            recordLoanIdMapping(pItem.getLoanId(), myLoan);

        } else {
            /* Create the loan and record it */
            myLoan = new CoeusFundingCircleLoan(this, pItem);
            recordLoan(myLoan);
            theAuctionMap.put(myAuctionId, myLoan);
        }
    }

    /**
     * Parse the statement file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    void parseStatement(final Path pFile) throws OceanusException {
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
    CoeusFundingCircleLoan findLoanByAuctionId(final String pId) throws OceanusException {
        final CoeusFundingCircleLoan myLoan = theAuctionMap.get(pId);
        if (myLoan == null) {
            throw new CoeusDataException(pId, "Unrecognised AuctionId");
        }
        return myLoan;
    }

    /**
     * Obtain the auctionMap.
     * @return the auctionMap
     */
    private Map<String, CoeusFundingCircleLoan> getAuctionMap() {
        return theAuctionMap;
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
    protected CoeusFundingCircleHistory viewHistory(final CoeusHistory pHistory,
                                                    final TethysDateRange pRange) {
        return new CoeusFundingCircleHistory(pHistory, pRange);
    }

    @Override
    protected CoeusFundingCircleLoan newLoan(final String pId) {
        return new CoeusFundingCircleLoan(this, pId);
    }

    @Override
    protected CoeusFundingCircleLoan viewLoan(final CoeusLoan pLoan,
                                              final TethysDateRange pRange) {
        return new CoeusFundingCircleLoan((CoeusFundingCircleLoan) pLoan, pRange);
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
    public MetisFieldSet<CoeusFundingCircleMarket> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
