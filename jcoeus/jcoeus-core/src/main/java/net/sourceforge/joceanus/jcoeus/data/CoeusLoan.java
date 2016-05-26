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
package net.sourceforge.joceanus.jcoeus.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Coeus Loan.
 * @param <T> the transaction type
 */
public abstract class CoeusLoan<T extends CoeusTransaction> {
    /**
     * Loan Market.
     */
    private final CoeusLoanMarket<T> theMarket;

    /**
     * Loan Id.
     */
    private final String theId;

    /**
     * Loan Transactions.
     */
    private final List<T> theTransactions;

    /**
     * Constructor.
     * @param pMarket the loanMarket
     * @param pId the loan Id
     */
    protected CoeusLoan(final CoeusLoanMarket<T> pMarket,
                        final String pId) {
        /* Store parameters */
        theMarket = pMarket;
        theId = pId;

        /* create the transaction list */
        theTransactions = new ArrayList<>();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusLoanMarket<T> getMarket() {
        return theMarket;
    }

    /**
     * Obtain the loan Id.
     * @return the id
     */
    public String getLoanId() {
        return theId;
    }

    /**
     * Add the transaction to the list.
     * @param pTrans the transaction
     */
    public void addTransaction(final T pTrans) {
        theTransactions.add(pTrans);
    }
}
