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
package net.sourceforge.joceanus.jcoeus.data.lendingworks;

import java.nio.file.Path;
import java.util.ListIterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * LendingWorks Market.
 */
public class CoeusLendingWorksMarket
        extends CoeusMarket {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CoeusLendingWorksMarket.class, CoeusMarket.getBaseFieldSet());

    /**
     * The Decimal size.
     */
    protected static final int DECIMAL_SIZE = 5;

    /**
     * The Transaction Parser.
     */
    private final CoeusLendingWorksTransactionParser theXactionParser;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public CoeusLendingWorksMarket(final MetisDataFormatter pFormatter) {
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
    public void parseStatement(final Path pFile) throws OceanusException {
        /* Parse the file */
        theXactionParser.parseFile(pFile);

        /* Loop through the transactions in reverse order */
        final ListIterator<CoeusLendingWorksTransaction> myIterator = theXactionParser.reverseTransactionIterator();
        while (myIterator.hasPrevious()) {
            final CoeusLendingWorksTransaction myTrans = myIterator.previous();

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
    protected CoeusLendingWorksHistory newHistory(final TethysDate pDate) {
        return new CoeusLendingWorksHistory(this, pDate);
    }

    @Override
    protected CoeusLendingWorksLoan newLoan(final String pId) {
        return new CoeusLendingWorksLoan(this, pId);
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
}
