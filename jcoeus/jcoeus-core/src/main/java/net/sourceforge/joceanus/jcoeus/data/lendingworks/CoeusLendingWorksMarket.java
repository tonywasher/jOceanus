/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.lendingworks;

import java.nio.file.Path;
import java.util.Iterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * LendingWorks Market.
 */
public class CoeusLendingWorksMarket
        extends CoeusMarket {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusLendingWorksMarket> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusLendingWorksMarket.class);

    /**
     * The Decimal size.
     */
    static final int DECIMAL_SIZE = 5;

    /**
     * The Transaction Parser.
     */
    private final CoeusLendingWorksTransactionParser theXactionParser;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    CoeusLendingWorksMarket(final MetisDataFormatter pFormatter) {
        /* Initialise underlying class */
        super(pFormatter, CoeusMarketProvider.LENDINGWORKS);

        /* Create the parsers */
        theXactionParser = new CoeusLendingWorksTransactionParser(this);
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
        final Iterator<CoeusLendingWorksTransaction> myIterator = theXactionParser.transactionIterator();
        while (myIterator.hasNext()) {
            final CoeusLendingWorksTransaction myTrans = myIterator.next();

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
        return false;
    }

    @Override
    public CoeusLendingWorksLoan findLoanById(final String pId) throws OceanusException {
        return (CoeusLendingWorksLoan) super.findLoanById(pId);
    }

    @Override
    public CoeusLendingWorksLoan getLoanById(final String pId) {
        return (CoeusLendingWorksLoan) super.getLoanById(pId);
    }

    @Override
    protected CoeusLendingWorksTotals newTotals() {
        return new CoeusLendingWorksTotals(this);
    }

    @Override
    protected CoeusLendingWorksHistory newHistory() {
        return new CoeusLendingWorksHistory(this);
    }

    @Override
    protected CoeusLendingWorksHistory viewHistory(final CoeusHistory pHistory,
                                                   final TethysDateRange pRange) {
        return new CoeusLendingWorksHistory(pHistory, pRange);
    }

    @Override
    protected CoeusLendingWorksLoan newLoan(final String pId) {
        return new CoeusLendingWorksLoan(this, pId);
    }

    @Override
    protected CoeusLendingWorksLoan viewLoan(final CoeusLoan pLoan,
                                             final TethysDateRange pRange) {
        return new CoeusLendingWorksLoan((CoeusLendingWorksLoan) pLoan, pRange);
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
    public MetisFieldSet<CoeusLendingWorksMarket> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
