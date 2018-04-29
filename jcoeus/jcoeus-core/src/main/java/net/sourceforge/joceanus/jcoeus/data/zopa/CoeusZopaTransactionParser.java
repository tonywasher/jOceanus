/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2018 Tony Washer
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
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusCSVParser;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Zopa parser.
 */
public class CoeusZopaTransactionParser
        extends CoeusCSVParser {
    /**
     * Header fields.
     */
    private static final String[] HEADERS =
    { "Date", "Description", "Paid In", "Paid Out" };

    /**
     * The market.
     */
    private final CoeusZopaMarket theMarket;

    /**
     * Parsed fields.
     */
    private final List<CoeusZopaTransaction> theTransactions;

    /**
     * Have we checked the header?
     */
    private boolean checkedHeader;

    /**
     * Constructor.
     * @param pMarket the market
     */
    protected CoeusZopaTransactionParser(final CoeusZopaMarket pMarket) {
        /* Initialise the underlying class */
        super(pMarket.getFormatter(), HEADERS);
        theMarket = pMarket;

        /* Establish decimal size */
        setDecimalSize(CoeusZopaMarket.DECIMAL_SIZE);

        /* Create the transaction list */
        theTransactions = new ArrayList<>();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    protected CoeusZopaMarket getMarket() {
        return theMarket;
    }

    /**
     * Obtain the transactions.
     * @return the transactions
     */
    public ListIterator<CoeusZopaTransaction> reverseTransactionIterator() {
        return theTransactions.listIterator(theTransactions.size());
    }

    @Override
    protected void resetFields() {
        /* Set the date format */
        setDateFormat("dd/MM/yyyy HH:mm");

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
            theTransactions.add(new CoeusZopaTransaction(this, pFields));
        }
    }
}
