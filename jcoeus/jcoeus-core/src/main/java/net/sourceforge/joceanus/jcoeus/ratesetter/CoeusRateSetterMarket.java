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
package net.sourceforge.joceanus.jcoeus.ratesetter;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.ListIterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * RateSetter Market.
 */
public class CoeusRateSetterMarket
        extends CoeusLoanMarket<CoeusRateSetterLoan, CoeusRateSetterTransaction, CoeusRateSetterTotals, CoeusRateSetterHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusRateSetterMarket.class.getSimpleName(), CoeusLoanMarket.getBaseFields());

    /**
     * The LoanBook Parser.
     */
    private final CoeusRateSetterLoanBookParser theBookParser;

    /**
     * The Transaction Parser.
     */
    private final CoeusRateSetterTransactionParser theXactionParser;

    /**
     * The Repairer.
     */
    private final CoeusRateSetterRepair theRepairer;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @throws OceanusException on error
     */
    public CoeusRateSetterMarket(final MetisDataFormatter pFormatter) throws OceanusException {
        /* Initialise underlying class */
        super(pFormatter, CoeusLoanMarketProvider.RATESETTER);

        /* Create the parsers */
        theBookParser = new CoeusRateSetterLoanBookParser(pFormatter);
        theXactionParser = new CoeusRateSetterTransactionParser(this);

        /* Create the repairer */
        theRepairer = new CoeusRateSetterRepair(this);
    }

    @Override
    protected void addTransaction(final CoeusRateSetterTransaction pTrans) {
        super.addTransaction(pTrans);
    }

    /**
     * Add transaction to list.
     * @param pTrans the transaction
     */
    protected void removeTransaction(final CoeusRateSetterTransaction pTrans) {
        getTransactions().remove(pTrans);
    }

    /**
     * Parse the loanBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseLoanBook(final Path pFile) throws OceanusException {
        /* Parse the file */
        theBookParser.parseFile(pFile);

        /* Loop through the loanBook items */
        Iterator<CoeusRateSetterLoanBookItem> myIterator = theBookParser.loanIterator();
        while (myIterator.hasNext()) {
            CoeusRateSetterLoanBookItem myItem = myIterator.next();

            /* Create the loan and record it */
            recordLoan(new CoeusRateSetterLoan(this, myItem));
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
        ListIterator<CoeusRateSetterTransaction> myIterator = theXactionParser.reverseTransactionIterator();
        while (myIterator.hasPrevious()) {
            CoeusRateSetterTransaction myTrans = myIterator.previous();

            /* If we have a loan offer */
            if ((myTrans.getLoan() == null)
                && CoeusTransactionType.CAPITALLOAN.equals(myTrans.getTransType())) {
                /* Add to set of initial loans for later resolution */
                theRepairer.recordInitialLoan(myTrans);
            }

            /* Add to the transactions */
            addTransaction(myTrans);
        }
    }

    /**
     * Repair loans such that original loan is known and original payments is associated.
     * @throws OceanusException on error
     */
    public void repairLoans() throws OceanusException {
        /* Repair the loans */
        theRepairer.repairLoans();
    }

    @Override
    protected CoeusRateSetterTotals newTotals() {
        return new CoeusRateSetterTotals(this);
    }

    @Override
    protected CoeusRateSetterTotals newTotals(final TethysDate pDate,
                                              final CoeusRateSetterTotals pTotals) {
        return new CoeusRateSetterTotals(pDate, pTotals);
    }

    @Override
    protected CoeusRateSetterHistory newHistory() {
        return new CoeusRateSetterHistory(this);
    }

    @Override
    protected CoeusRateSetterHistory newHistory(final TethysDate pDate) {
        return new CoeusRateSetterHistory(this, pDate);
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
