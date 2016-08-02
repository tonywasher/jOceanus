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
package net.sourceforge.joceanus.jcoeus.fundingcircle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.data.CoeusCSVParser;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * FundingCircle BadDebt Parser.
 */
public class CoeusFundingCircleBadDebtParser
        extends CoeusCSVParser {
    /**
     * Header fields.
     */
    private static final String[] HEADERS =
    { "Loan Part ID", "Loan title", "Risk", "Repayments left", "Principal", "Rate", "Date", "Seller", "Status", "DefaultDate" };

    /**
     * The market.
     */
    private final CoeusFundingCircleMarket theMarket;

    /**
     * Parsed fields.
     */
    private final List<CoeusFundingCircleTransaction> theBadDebts;

    /**
     * Have we checked the header?
     */
    private boolean checkedHeader;

    /**
     * Constructor.
     * @param pMarket the market
     */
    protected CoeusFundingCircleBadDebtParser(final CoeusFundingCircleMarket pMarket) {
        /* Initialise the underlying class */
        super(pMarket.getFormatter(), HEADERS);
        theMarket = pMarket;

        /* Create the badDebts list */
        theBadDebts = new ArrayList<>();
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
    public Iterator<CoeusFundingCircleTransaction> badDebtIterator() {
        return theBadDebts.iterator();
    }

    @Override
    protected void resetFields() {
        /* Set the date format */
        setDateFormat("yyyy-MM-dd");

        /* Clear data */
        theBadDebts.clear();
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
            theBadDebts.add(new CoeusFundingCircleTransaction(this, pFields));
        }
    }
}