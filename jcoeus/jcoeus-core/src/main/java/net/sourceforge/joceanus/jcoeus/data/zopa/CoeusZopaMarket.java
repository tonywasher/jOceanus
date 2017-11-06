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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Zopa Market.
 */
public class CoeusZopaMarket
        extends CoeusMarket {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusZopaMarket.class, CoeusMarket.getBaseFieldSet());

    /**
     * Missing LoanBook Field Id.
     */
    private static final MetisDataField FIELD_MISSINGBOOK = FIELD_DEFS.declareLocalField(CoeusResource.DATA_MISSINGBOOK);

    /**
     * Missing Capital Field Id.
     */
    private static final MetisDataField FIELD_MISSINGCAPITAL = FIELD_DEFS.declareLocalField(CoeusResource.DATA_MISSINGCAPITAL);

    /**
     * Missing Interest Field Id.
     */
    private static final MetisDataField FIELD_MISSINGINTEREST = FIELD_DEFS.declareLocalField(CoeusResource.DATA_MISSINGINTEREST);

    /**
     * Zombie Loans Field Id.
     */
    private static final MetisDataField FIELD_ZOMBIELOANS = FIELD_DEFS.declareLocalField(CoeusResource.DATA_ZOMBIELOANS);

    /**
     * Interesting loans Field Id.
     */
    private static final MetisDataField FIELD_INTERESTINGLOANS = FIELD_DEFS.declareLocalField(CoeusResource.DATA_INTERESTINGLOANS);

    /**
     * The Decimal size.
     */
    protected static final int DECIMAL_SIZE = 8;

    /**
     * The LoanBook Parser.
     */
    private final CoeusZopaLoanBookParser theBookParser;

    /**
     * The BadDebt Parser.
     */
    private final CoeusZopaBadDebtParser theDebtParser;

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
    public CoeusZopaMarket(final MetisDataFormatter pFormatter) {
        /* Initialise underlying class */
        super(pFormatter, CoeusMarketProvider.ZOPA);

        /* Create the parsers */
        theBookParser = new CoeusZopaLoanBookParser(this);
        theDebtParser = new CoeusZopaBadDebtParser(this);
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
    public void parseLoanBook(final Path pFile) throws OceanusException {
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
     * Parse the badDebtBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseBadDebtBook(final Path pFile) throws OceanusException {
        /* Parse the file */
        theDebtParser.parseFile(pFile);

        /* Loop through the loan book items */
        final Iterator<CoeusZopaTransaction> myIterator = theDebtParser.badDebtIterator();
        while (myIterator.hasNext()) {
            final CoeusZopaTransaction myTrans = myIterator.next();

            /* Add to the transactions */
            addTransaction(myTrans);
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

        /* Loop through the transactions in reverse order */
        final ListIterator<CoeusZopaTransaction> myIterator = theXactionParser.reverseTransactionIterator();
        while (myIterator.hasPrevious()) {
            final CoeusZopaTransaction myTrans = myIterator.previous();

            /* Add to the transactions */
            addTransaction(myTrans);
        }
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
    protected CoeusZopaHistory newHistory(final TethysDate pDate) {
        return new CoeusZopaHistory(this, pDate);
    }

    @Override
    protected CoeusZopaLoan newLoan(final String pId) {
        return new CoeusZopaLoan(this, pId);
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
    protected void recordMissingBook(final TethysDecimal pMissing) {
        theMissingLoanBook.addValue(pMissing);
    }

    /**
     * Record missing capital.
     * @param pMissing the missing amount
     */
    protected void recordMissingCapital(final TethysDecimal pMissing) {
        theMissingCapital.addValue(pMissing);
    }

    /**
     * Record missing loan interest.
     * @param pMissing the missing amount
     */
    protected void recordMissingInterest(final TethysDecimal pMissing) {
        theMissingInterest.addValue(pMissing);
    }

    /**
     * Record zombieLoan.
     * @param pZombie the zombie amount
     */
    protected void recordZombieLoan(final TethysDecimal pZombie) {
        theZombieLoans.addValue(pZombie);
    }

    /**
     * Record interestingLoan.
     * @param pLoan the loan
     */
    protected void recordInterestingLoan(final CoeusZopaLoan pLoan) {
        theInterestingLoans.add(pLoan);
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
        if (FIELD_MISSINGBOOK.equals(pField)) {
            return skipZero(theMissingLoanBook);
        }
        if (FIELD_MISSINGCAPITAL.equals(pField)) {
            return skipZero(theMissingCapital);
        }
        if (FIELD_MISSINGINTEREST.equals(pField)) {
            return skipZero(theMissingInterest);
        }
        if (FIELD_ZOMBIELOANS.equals(pField)) {
            return skipZero(theZombieLoans);
        }
        if (FIELD_INTERESTINGLOANS.equals(pField)) {
            return theInterestingLoans.isEmpty()
                                                 ? MetisDataFieldValue.SKIP
                                                 : theInterestingLoans;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }
}
