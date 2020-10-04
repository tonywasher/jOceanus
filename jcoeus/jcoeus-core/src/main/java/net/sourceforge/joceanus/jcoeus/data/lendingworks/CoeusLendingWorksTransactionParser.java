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
package net.sourceforge.joceanus.jcoeus.data.lendingworks;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.parser.TethysCSVParser;

/**
 * LendingWorks parser.
 */
public class CoeusLendingWorksTransactionParser
        extends TethysCSVParser {
    /**
     * Header fields.
     */
    private static final String[] HEADERS = {
            "Date", "Type", "Chunk Id", "Product", "Paid By", "Amount"
    };

    /**
     * The market.
     */
    private final CoeusLendingWorksMarket theMarket;

    /**
     * Parsed fields.
     */
    private final List<CoeusLendingWorksTransaction> theTransactions;

    /**
     * Constructor.
     * @param pMarket the market
     */
    CoeusLendingWorksTransactionParser(final CoeusLendingWorksMarket pMarket) {
        /* Initialise the underlying class */
        super(pMarket.getFormatter(), HEADERS);
        theMarket = pMarket;

        /* Establish decimal size */
        setDecimalSize(CoeusLendingWorksMarket.DECIMAL_SIZE);

        /* Create the transaction list */
        theTransactions = new ArrayList<>();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    protected CoeusLendingWorksMarket getMarket() {
        return theMarket;
    }

    /**
     * Obtain the transactions.
     * @return the transactions
     */
    ListIterator<CoeusLendingWorksTransaction> reverseTransactionIterator() {
        return theTransactions.listIterator(theTransactions.size());
    }

    @Override
    protected void resetFields() {
        /* Set the date format */
        setDateFormat("dd/MM/yyyy");

        /* Clear data */
        theTransactions.clear();
    }

    @Override
    protected void processFields(final List<String> pFields) throws OceanusException {
        /* Parse the transaction and add to the list */
        theTransactions.add(new CoeusLendingWorksTransaction(this, pFields));
    }
}
