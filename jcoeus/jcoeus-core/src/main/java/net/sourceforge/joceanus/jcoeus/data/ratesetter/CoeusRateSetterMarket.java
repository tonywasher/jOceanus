/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.ratesetter;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.ListIterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * RateSetter Market.
 */
public class CoeusRateSetterMarket
        extends CoeusMarket {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusRateSetterMarket> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusRateSetterMarket.class);

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
     */
    CoeusRateSetterMarket(final MetisDataFormatter pFormatter) {
        /* Initialise underlying class */
        super(pFormatter, CoeusMarketProvider.RATESETTER);

        /* Create the parsers */
        theBookParser = new CoeusRateSetterLoanBookParser(pFormatter);
        theXactionParser = new CoeusRateSetterTransactionParser(this);

        /* Create the repairer */
        theRepairer = new CoeusRateSetterRepair(this);
    }

    /**
     * Add transaction.
     * @param pTrans the transaction to add
     */
    void addTheTransaction(final CoeusRateSetterTransaction pTrans) {
        addTransaction(pTrans);
    }

    /**
     * Add transaction to list.
     * @param pTrans the transaction
     */
    void removeTransaction(final CoeusRateSetterTransaction pTrans) {
        getTransactions().remove(pTrans);
    }

    /**
     * Parse the loanBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    void parseLoanBook(final Path pFile) throws OceanusException {
        /* Parse the file */
        theBookParser.parseFile(pFile);

        /* Loop through the loanBook items */
        final Iterator<CoeusRateSetterLoanBookItem> myIterator = theBookParser.loanIterator();
        while (myIterator.hasNext()) {
            final CoeusRateSetterLoanBookItem myItem = myIterator.next();

            /* Create the loan and record it */
            recordLoan(new CoeusRateSetterLoan(this, myItem));
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
        final ListIterator<CoeusRateSetterTransaction> myIterator = theXactionParser.reverseTransactionIterator();
        while (myIterator.hasPrevious()) {
            final CoeusRateSetterTransaction myTrans = myIterator.previous();

            /* If we have a loan offer */
            if (myTrans.getLoan() == null
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
    void repairLoans() throws OceanusException {
        /* Repair the loans */
        theRepairer.repairLoans();
    }

    @Override
    public boolean usesDecimalTotals() {
        return false;
    }

    @Override
    public boolean hasBadDebt() {
        return false;
    }

    @Override
    public CoeusRateSetterLoan findLoanById(final String pId) throws OceanusException {
        return (CoeusRateSetterLoan) super.findLoanById(pId);
    }

    @Override
    protected CoeusRateSetterTotals newTotals() {
        return new CoeusRateSetterTotals(this);
    }

    @Override
    protected CoeusRateSetterHistory newHistory() {
        return new CoeusRateSetterHistory(this);
    }

    @Override
    protected CoeusRateSetterLoan newLoan(final String pId) {
        return new CoeusRateSetterLoan(this, pId);
    }

    @Override
    public void analyseMarket() throws OceanusException {
        /* Analyse the data and repair it */
        createAnalysis();
        repairLoans();

        /* Analyse repaired data and check it */
        createAnalysis();
        checkLoans();
    }

    @Override
    public String toString() {
        return FIELD_DEFS.getName();
    }

    @Override
    public MetisFieldSet<CoeusRateSetterMarket> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
