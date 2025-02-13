/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.coeus.data.fundingcircle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.metis.parser.MetisCSVParser;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * FundingCircle Bids Parser.
 */
public class CoeusFundingCircleBidBookParser
        extends MetisCSVParser {
    /**
     * Header fields.
     */
    private static final String[] HEADERS = {
            "Loan title", "Risk", "%Funded", "Bid Amount", "Rate", "Bid Time", "Time left", "Status"
    };

    /**
     * The market.
     */
    private final CoeusFundingCircleMarket theMarket;

    /**
     * Parsed fields.
     */
    private final List<CoeusFundingCircleLoanBookItem> theBids;

    /**
     * Constructor.
     * @param pMarket the market
     */
    CoeusFundingCircleBidBookParser(final CoeusFundingCircleMarket pMarket) {
        /* Initialise the underlying class */
        super(pMarket.getFormatter(), HEADERS);
        theMarket = pMarket;

        /* Create the bids list */
        theBids = new ArrayList<>();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    protected CoeusFundingCircleMarket getMarket() {
        return theMarket;
    }

    /**
     * Obtain the badDebts.
     * @return the badDebts
     */
    Iterator<CoeusFundingCircleLoanBookItem> bidIterator() {
        return theBids.iterator();
    }

    @Override
    protected void resetFields() {
        /* Set the date format */
        setDateFormat("dd/MM/yyyy");

        /* Clear data */
        theBids.clear();
    }

    @Override
    protected void processFields(final List<String> pFields) throws OceanusException {
        /* Parse the loan and add to the list */
        theBids.add(new CoeusFundingCircleLoanBookItem(this, pFields));
    }
}
