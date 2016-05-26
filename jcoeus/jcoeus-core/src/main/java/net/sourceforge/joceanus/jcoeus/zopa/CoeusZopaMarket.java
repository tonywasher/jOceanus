/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.zopa;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Zopa Market.
 */
public class CoeusZopaMarket
        extends CoeusLoanMarket<CoeusZopaTransaction> {
    /**
     * The Decimal size.
     */
    protected static final int DECIMAL_SIZE = 8;

    /**
     * The LoanBook Parser.
     */
    private final CoeusZopaLoanBookParser theBookParser;

    /**
     * The Transaction Parser.
     */
    private final CoeusZopaTransactionParser theXactionParser;

    /**
     * The Map of loanId to BookItem.
     */
    private final Map<String, CoeusZopaLoanBookItem> theLoanIdMap;

    /**
     * The List of Transactions.
     */
    private final List<CoeusZopaTransaction> theTransactions;

    /**
     * The non-Loan Transactions.
     */
    private final List<CoeusZopaTransaction> theAdmin;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public CoeusZopaMarket(final MetisDataFormatter pFormatter) {
        /* Initialise underlying class */
        super(CoeusLoanMarketProvider.ZOPA);

        /* Create the parsers */
        theBookParser = new CoeusZopaLoanBookParser(pFormatter);
        theXactionParser = new CoeusZopaTransactionParser(pFormatter);

        /* Create the bookItem map */
        theLoanIdMap = new HashMap<>();

        /* Create the lists */
        theTransactions = new ArrayList<>();
        theAdmin = new ArrayList<>();
    }

    /**
     * Parse the loanBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseLoanBook(final File pFile) throws OceanusException {
        /* Parse the file */
        theBookParser.parseFile(pFile);

        /* Loop through the loan book items */
        Iterator<CoeusZopaLoanBookItem> myIterator = theBookParser.loanIterator();
        while (myIterator.hasNext()) {
            CoeusZopaLoanBookItem myLoan = myIterator.next();

            /* Add to the map */
            theLoanIdMap.put(myLoan.getLoanId(), myLoan);
        }
    }

    /**
     * Parse the statement file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseStatement(final File pFile) throws OceanusException {
        /* Parse the file */
        theXactionParser.parseFile(pFile);

        /* Loop through the transactions in reverse order */
        ListIterator<CoeusZopaTransaction> myIterator = theXactionParser.reverseTransactionIterator();
        while (myIterator.hasPrevious()) {
            CoeusZopaTransaction myTrans = myIterator.previous();
            CoeusTransactionType myTransType = myTrans.getTransType();
            String myId = myTrans.getLoanId();

            /* If we have a loanId */
            if (myId != null) {
                /* Access the loan */
                CoeusLoan<CoeusZopaTransaction> myLoan = findLoan(myTrans.getLoanId());
                myLoan.addTransaction(myTrans);

                /* else handle as administration transactions */
            } else {
                /* Add to adminList */
                theAdmin.add(myTrans);
            }

            /* Add to the transactions */
            theTransactions.add(myTrans);
        }
    }

    @Override
    protected CoeusZopaLoan newLoan(final String pId) {
        return new CoeusZopaLoan(this, pId);
    }
}
