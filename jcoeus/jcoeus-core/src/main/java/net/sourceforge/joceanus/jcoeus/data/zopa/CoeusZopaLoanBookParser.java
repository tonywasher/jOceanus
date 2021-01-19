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
package net.sourceforge.joceanus.jcoeus.data.zopa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.parser.TethysCSVParser;

/**
 * Zopa LoanBook Parser.
 */
public class CoeusZopaLoanBookParser
        extends TethysCSVParser {
    /**
     * Header fields.
     */
    private static final String[] HEADERS = {
            "Borrower ID", "Product", "Acquired", "Market", "Type of loan", "Term", "Loan size", "Loan status", "Borrower origination fee", "Borrower rate",
            "Loan servicing fee", "Investor rate", "Transaction price", "Transaction date", "Amount invested", "Capital outstanding",
            "Interest outstanding", "Amount repaid", "Capital repaid", "Interest repaid", "Amount in arrears", "Days in arrears", "Repayment day",
            "Covered by Safeguard", "Comment", "Loan start date", "Last repayment date", "Default price", "Default date", "Monthly repayment amount",
            "Type of asset", "Loan purpose", "Percentage repaid", "Covid Arrangements"
    };

    /**
     * The market.
     */
    private final CoeusZopaMarket theMarket;

    /**
     * Parsed loans.
     */
    private final List<CoeusZopaLoanBookItem> theLoans;

    /**
     * Constructor.
     * @param pMarket the market
     */
    CoeusZopaLoanBookParser(final CoeusZopaMarket pMarket) {
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
    }

    @Override
    protected void processFields(final List<String> pFields) throws OceanusException {
        /* Parse the loan and add to the list */
        final CoeusZopaLoanBookItem myBookItem = new CoeusZopaLoanBookItem(this, pFields);
        theLoans.add(myBookItem);
    }
}
