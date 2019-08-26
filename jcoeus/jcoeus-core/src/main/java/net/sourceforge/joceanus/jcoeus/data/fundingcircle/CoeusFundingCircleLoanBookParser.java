/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.fundingcircle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.data.CoeusCSVParser;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * FundingCircle LoanBook Parser.
 */
public class CoeusFundingCircleLoanBookParser
        extends CoeusCSVParser {
    /**
     * Header fields.
     */
    private static final String[] HEADERS =
    { "Loan part ID", "Loan title", "Sector", "Loan ID", "Risk", "Repayments left", "Principal remaining", "Rate", "Next payment date", "Status", "Date acquired" };

    /**
     * Parsed fields.
     */
    private final List<CoeusFundingCircleLoanBookItem> theLoans;

    /**
     * Have we checked the header?
     */
    private boolean checkedHeader;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    CoeusFundingCircleLoanBookParser(final MetisDataFormatter pFormatter) {
        /* Initialise the underlying class */
        super(pFormatter, HEADERS);

        /* Create the loan list */
        theLoans = new ArrayList<>();
    }

    /**
     * Obtain the loans.
     * @return the loans
     */
    public Iterator<CoeusFundingCircleLoanBookItem> loanIterator() {
        return theLoans.iterator();
    }

    @Override
    protected void resetFields() {
        /* Set the date format */
        setDateFormat("yyyy-MM-dd");

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
            theLoans.add(new CoeusFundingCircleLoanBookItem(this, pFields));
        }
    }
}
