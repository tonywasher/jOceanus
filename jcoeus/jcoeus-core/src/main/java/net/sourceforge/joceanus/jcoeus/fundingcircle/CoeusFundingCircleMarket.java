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
package net.sourceforge.joceanus.jcoeus.fundingcircle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * FundingCircle Market.
 */
public class CoeusFundingCircleMarket
        extends CoeusLoanMarket<CoeusFundingCircleTransaction> {
    /**
     * The LoanBook Parser.
     */
    private final CoeusFundingCircleLoanBookParser theBookParser;

    /**
     * The Transaction Parser.
     */
    private final CoeusFundingCircleTransactionParser theXactionParser;

    /**
     * The Map of auctionId to BookItem.
     */
    private final Map<String, CoeusFundingCircleLoanBookItem> theAuctionIdMap;

    /**
     * The Map of loanId to BookItem.
     */
    private final Map<String, CoeusFundingCircleLoanBookItem> theLoanIdMap;

    /**
     * The List of Transactions.
     */
    private final List<CoeusFundingCircleTransaction> theTransactions;

    /**
     * The non-loan Transactions.
     */
    private final List<CoeusFundingCircleTransaction> theAdmin;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public CoeusFundingCircleMarket(final MetisDataFormatter pFormatter) {
        /* Initialise underlying class */
        super(CoeusLoanMarketProvider.FUNDINGCIRCLE);

        /* Create the parsers */
        theBookParser = new CoeusFundingCircleLoanBookParser(pFormatter);
        theXactionParser = new CoeusFundingCircleTransactionParser(pFormatter);

        /* Create the bookItem maps */
        theAuctionIdMap = new HashMap<>();
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
        Iterator<CoeusFundingCircleLoanBookItem> myIterator = theBookParser.loanIterator();
        while (myIterator.hasNext()) {
            CoeusFundingCircleLoanBookItem myLoan = myIterator.next();

            /* Add to the maps */
            theAuctionIdMap.put(myLoan.getAuctionId(), myLoan);
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

        /* Loop through the transactions in normal order */
        Iterator<CoeusFundingCircleTransaction> myIterator = theXactionParser.transactionIterator();
        while (myIterator.hasNext()) {
            CoeusFundingCircleTransaction myTrans = myIterator.next();
            CoeusTransactionType myTransType = myTrans.getTransType();
            String myId = myTrans.getLoanId();

            /* If we have a loanId */
            if (myId != null) {
                /* Access the loan */
                CoeusLoan<CoeusFundingCircleTransaction> myLoan = findLoan(myTrans.getLoanId());
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
    protected CoeusFundingCircleLoan newLoan(final String pId) {
        return new CoeusFundingCircleLoan(this, pId);
    }
}
