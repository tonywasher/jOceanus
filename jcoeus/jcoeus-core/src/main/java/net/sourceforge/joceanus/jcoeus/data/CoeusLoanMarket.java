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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loan Market.
 * @param <T> the transaction type
 */
public abstract class CoeusLoanMarket<T extends CoeusTransaction> {
    /**
     * Loan Market Provider.
     */
    private final CoeusLoanMarketProvider theProvider;

    /**
     * Loan List.
     */
    private final List<CoeusLoan<T>> theLoans;

    /**
     * Loan Map.
     */
    private final Map<String, CoeusLoan<T>> theLoanMap;

    /**
     * Constructor.
     * @param pMarket the loanMarket
     * @param pId the loan Id
     */
    protected CoeusLoanMarket(final CoeusLoanMarketProvider pProvider) {
        /* Store parameters */
        theProvider = pProvider;

        /* Create lists and maps */
        theLoans = new ArrayList<>();
        theLoanMap = new HashMap<>();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusLoanMarketProvider getProvider() {
        return theProvider;
    }

    /**
     * LookUp Loan.
     * @param pId the id of the loan
     * @return the loan
     */
    protected CoeusLoan<T> findLoan(final String pId) {
        CoeusLoan<T> myLoan = theLoanMap.get(pId);
        if (myLoan == null) {
            myLoan = newLoan(pId);
            theLoans.add(myLoan);
            theLoanMap.put(pId, myLoan);
        }
        return myLoan;
    }

    /**
     * Create a new loan.
     * @param pId the id of the loan
     * @return the loan
     */
    protected abstract CoeusLoan<T> newLoan(final String pId);
}
