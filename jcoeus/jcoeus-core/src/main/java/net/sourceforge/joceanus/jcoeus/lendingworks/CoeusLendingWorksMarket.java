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
package net.sourceforge.joceanus.jcoeus.lendingworks;

import java.nio.file.Path;
import java.util.ListIterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarketProvider;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * LendingWorks Market.
 */
public class CoeusLendingWorksMarket
        extends CoeusLoanMarket<CoeusLendingWorksLoan, CoeusLendingWorksTransaction, CoeusLendingWorksTotals, CoeusLendingWorksHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusLendingWorksMarket.class.getSimpleName(), CoeusLoanMarket.getBaseFields());

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
     * @throws OceanusException on error
     */
    public CoeusLendingWorksMarket(final MetisDataFormatter pFormatter) throws OceanusException {
        /* Initialise underlying class */
        super(pFormatter, CoeusLoanMarketProvider.LENDINGWORKS, CoeusLendingWorksTransaction.class);

        /* Create the parsers */
        theXactionParser = new CoeusLendingWorksTransactionParser(this);
    }

    @Override
    protected void addTransaction(final CoeusLendingWorksTransaction pTrans) {
        super.addTransaction(pTrans);
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
        ListIterator<CoeusLendingWorksTransaction> myIterator = theXactionParser.reverseTransactionIterator();
        while (myIterator.hasPrevious()) {
            CoeusLendingWorksTransaction myTrans = myIterator.previous();

            /* Add to the transactions */
            addTransaction(myTrans);
        }
    }

    @Override
    protected CoeusLendingWorksTotals newTotals() {
        return new CoeusLendingWorksTotals(this);
    }

    @Override
    protected CoeusLendingWorksTotals newTotals(final TethysDate pDate,
                                                final CoeusLendingWorksTotals pTotals) {
        return new CoeusLendingWorksTotals(pDate, pTotals);
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
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
