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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.data.CoeusCSVParser;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Zopa LoanBook Parser.
 */
public class CoeusZopaLoanBookParser
        extends CoeusCSVParser {
    /**
     * Header fields.
     */
    private static final String[] HEADERS =
    { "Borrower", "Product", "Acquired", "Market", "Term", "Loan size", "Status", "Rate", "Lent", "Outstanding", "Repaid",
            "Capital repaid", "Interest repaid", "Arrears", "Repayment day", "Safeguard", "Comment", "Loan start date",
            "Scheduled end date", "Expected Repayment", "Loan purpose", "Repaid %" };

    /**
     * The market.
     */
    private final CoeusZopaMarket theMarket;

    /**
     * Parsed fields.
     */
    private final List<CoeusZopaLoanBookItem> theLoans;

    /**
     * Have we checked the header?
     */
    private boolean checkedHeader;

    /**
     * Constructor.
     * @param pMarket the market
     */
    protected CoeusZopaLoanBookParser(final CoeusZopaMarket pMarket) {
        /* Initialise the underlying class */
        super(pMarket.getFormatter(), HEADERS);
        theMarket = pMarket;

        /* Set the decimal size */
        setDecimalSize(CoeusZopaMarket.DECIMAL_SIZE);

        /* Create the loan list */
        theLoans = new ArrayList<>();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    protected CoeusZopaMarket getMarket() {
        return theMarket;
    }

    /**
     * Obtain the loans.
     * @return the loans
     */
    public Iterator<CoeusZopaLoanBookItem> loanIterator() {
        return theLoans.iterator();
    }

    @Override
    protected void resetFields() {
        /* Set the date format */
        setDateFormat("dd-MM-yyyy");

        /* Clear data */
        theLoans.clear();
        checkedHeader = false;
    }

    @Override
    protected void processFields(final List<String> pFields) throws OceanusException {
        /* If we have not yet checked the header */
        if (!checkedHeader) {
            /* Validate the header */
            checkHeaders(pFields);
            checkedHeader = true;

            /* else its a transaction */
        } else {
            /* Parse the loan and add to the list */
            theLoans.add(new CoeusZopaLoanBookItem(this, pFields));
        }
    }
}
