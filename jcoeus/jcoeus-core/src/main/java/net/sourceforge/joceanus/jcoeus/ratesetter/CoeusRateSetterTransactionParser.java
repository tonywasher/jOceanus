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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusCSVParser;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * RateSetter parser.
 */
public class CoeusRateSetterTransactionParser
        extends CoeusCSVParser {
    /**
     * Header fields.
     */
    private static final String[] HEADERS =
    { "Date", "Market", "Type", "Item", "Amount", "Capital", "Interest", "Fee" };

    /**
     * The market.
     */
    private final CoeusRateSetterMarket theMarket;

    /**
     * Parsed fields.
     */
    private final List<CoeusRateSetterTransaction> theTransactions;

    /**
     * Have we checked the header?
     */
    private boolean checkedHeader;

    /**
     * Constructor.
     * @param pMarket the market
     */
    protected CoeusRateSetterTransactionParser(final CoeusRateSetterMarket pMarket) {
        /* Initialise the underlying class */
        super(pMarket.getFormatter(), HEADERS);
        theMarket = pMarket;

        /* Create the transaction list */
        theTransactions = new ArrayList<>();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    protected CoeusRateSetterMarket getMarket() {
        return theMarket;
    }

    /**
     * Obtain the transactions.
     * @return the transactions
     */
    public ListIterator<CoeusRateSetterTransaction> reverseTransactionIterator() {
        return theTransactions.listIterator(theTransactions.size());
    }

    @Override
    protected void resetFields() {
        /* Set the date format */
        setDateFormat("dd/MM/yyyy");

        /* Clear data */
        theTransactions.clear();
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
            /* Parse the transaction and add to the list */
            theTransactions.add(new CoeusRateSetterTransaction(this, pFields));
        }
    }
}