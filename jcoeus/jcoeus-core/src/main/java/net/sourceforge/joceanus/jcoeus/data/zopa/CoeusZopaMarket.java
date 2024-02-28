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
package net.sourceforge.joceanus.jcoeus.data.zopa;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Zopa Market.
 */
public class CoeusZopaMarket
        extends CoeusMarket {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusZopaMarket> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusZopaMarket.class);

    /*
     * Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MISSINGBOOK, CoeusZopaMarket::getMissingLoanBook);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MISSINGCAPITAL, CoeusZopaMarket::getMissingCapital);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_MISSINGINTEREST, CoeusZopaMarket::getMissingInterest);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_ZOMBIELOANS, CoeusZopaMarket::getZombieLoans);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_INTERESTINGLOANS, CoeusZopaMarket::getInterestingLoans);
    }

    /**
     * The Decimal size.
     */
    static final int DECIMAL_SIZE = 8;

    /**
     * The LoanBook Parser.
     */
    private final CoeusZopaLoanBookParser theBookParser;

    /**
     * The Transaction Parser.
     */
    private final CoeusZopaTransactionParser theXactionParser;

    /**
     * The Missing LoanBook Balance.
     */
    private final TethysDecimal theMissingLoanBook;

    /**
     * The Missing Capital.
     */
    private final TethysDecimal theMissingCapital;

    /**
     * The Missing Interest.
     */
    private final TethysDecimal theMissingInterest;

    /**
     * The ZombieLoans.
     */
    private final TethysDecimal theZombieLoans;

    /**
     * The Interesting loans.
     */
    private final List<CoeusZopaLoan> theInterestingLoans;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    CoeusZopaMarket(final TethysUIDataFormatter pFormatter) {
        /* Initialise underlying class */
        super(pFormatter, CoeusMarketProvider.ZOPA);

        /* Create the parsers */
        theBookParser = new CoeusZopaLoanBookParser(this);
        theXactionParser = new CoeusZopaTransactionParser(this);

        /* Create missing counters */
        theMissingLoanBook = new TethysDecimal(0, DECIMAL_SIZE);
        theMissingCapital = new TethysDecimal(0, DECIMAL_SIZE);
        theMissingInterest = new TethysDecimal(0, DECIMAL_SIZE);
        theZombieLoans = new TethysDecimal(0, DECIMAL_SIZE);
        theInterestingLoans = new ArrayList<>();
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
        final Iterator<CoeusZopaLoanBookItem> myIterator = theBookParser.loanIterator();
        while (myIterator.hasNext()) {
            final CoeusZopaLoanBookItem myItem = myIterator.next();

            /* Look for preExisting loan */
            final CoeusZopaLoan myLoan = getLoanById(myItem.getLoanId());
            if (myLoan == null) {
                /* Create the loan and record it */
                recordLoan(new CoeusZopaLoan(this, myItem));
            } else {
                /* Add the bookItem to the loan */
                myLoan.addBookItem(myItem);
            }
        }
    }

    /**
     * Process the badDebt.
     * @throws OceanusException on error
     */
    void processBadDebt() throws OceanusException {
        /* Loop through the loans */
        final Iterator<CoeusLoan> myIterator = loanIterator();
        while (myIterator.hasNext()) {
            final CoeusZopaLoan myLoan = (CoeusZopaLoan) myIterator.next();

            /* Ignore loans that are not badDebt */
            if (myLoan.getBadDebtDate() == null) {
                continue;
            }

            /* Add to the transactions */
            final CoeusZopaTransaction myTrans = new CoeusZopaTransaction(myLoan);
            addTransaction(myTrans);
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

        /* Loop through the transactions in reverse order */
        final ListIterator<CoeusZopaTransaction> myIterator = theXactionParser.reverseTransactionIterator();
        while (myIterator.hasPrevious()) {
            final CoeusZopaTransaction myTrans = myIterator.previous();

            /* Add to the transactions */
            addTransaction(myTrans);
        }
    }

    /**
     * Obtain the missing LoanBook.
     * @return the missing loanBook
     */
    private TethysDecimal getMissingLoanBook() {
        return theMissingLoanBook;
    }

    /**
     * Obtain the missing Capital.
     * @return the missing Capital
     */
    private TethysDecimal getMissingCapital() {
        return theMissingCapital;
    }

    /**
     * Obtain the missing Interest.
     * @return the missing Interest
     */
    private TethysDecimal getMissingInterest() {
        return theMissingInterest;
    }

    /**
     * Obtain the zombieLoans.
     * @return the zombieLoans
     */
    private TethysDecimal getZombieLoans() {
        return theZombieLoans;
    }

    /**
     * Obtain the interestingLoans.
     * @return the interestingLoans
     */
    private List<CoeusZopaLoan> getInterestingLoans() {
        return theInterestingLoans;
    }

    @Override
    public boolean usesDecimalTotals() {
        return true;
    }

    @Override
    public boolean hasBadDebt() {
        return true;
    }

    @Override
    public CoeusZopaLoan findLoanById(final String pId) throws OceanusException {
        return (CoeusZopaLoan) super.findLoanById(pId);
    }

    @Override
    public CoeusZopaLoan getLoanById(final String pId) {
        return (CoeusZopaLoan) super.getLoanById(pId);
    }

    @Override
    protected CoeusZopaTotals newTotals() {
        return new CoeusZopaTotals(this);
    }

    @Override
    protected CoeusZopaHistory newHistory() {
        return new CoeusZopaHistory(this);
    }

    @Override
    protected CoeusZopaHistory viewHistory(final CoeusHistory pHistory,
                                           final TethysDateRange pRange) {
        return new CoeusZopaHistory(pHistory, pRange);
    }

    @Override
    protected CoeusZopaLoan newLoan(final String pId) {
        return new CoeusZopaLoan(this, pId);
    }

    @Override
    protected CoeusZopaLoan viewLoan(final CoeusLoan pLoan,
                                     final TethysDateRange pRange) {
        return new CoeusZopaLoan((CoeusZopaLoan) pLoan, pRange);
    }

    @Override
    public void analyseMarket() throws OceanusException {
        /* create and check the analysis */
        createAnalysis();
        checkLoans();
    }

    /**
     * Record missing book details.
     * @param pMissing the missing amount
     */
    void recordMissingBook(final TethysDecimal pMissing) {
        theMissingLoanBook.addValue(pMissing);
    }

    /**
     * Record missing capital.
     * @param pMissing the missing amount
     */
    void recordMissingCapital(final TethysDecimal pMissing) {
        theMissingCapital.addValue(pMissing);
    }

    /**
     * Record missing loan interest.
     * @param pMissing the missing amount
     */
    void recordMissingInterest(final TethysDecimal pMissing) {
        theMissingInterest.addValue(pMissing);
    }

    /**
     * Record zombieLoan.
     * @param pZombie the zombie amount
     */
    void recordZombieLoan(final TethysDecimal pZombie) {
        theZombieLoans.addValue(pZombie);
    }

    /**
     * Record interestingLoan.
     * @param pLoan the loan
     */
    void recordInterestingLoan(final CoeusZopaLoan pLoan) {
        theInterestingLoans.add(pLoan);
    }

    @Override
    public String toString() {
        return FIELD_DEFS.getName();
    }

    @Override
    public MetisFieldSet<CoeusZopaMarket> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
