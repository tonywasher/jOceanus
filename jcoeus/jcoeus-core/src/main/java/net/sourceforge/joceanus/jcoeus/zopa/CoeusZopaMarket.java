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

import java.io.File;
import java.util.Iterator;
import java.util.ListIterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarketProvider;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Zopa Market.
 */
public class CoeusZopaMarket
        extends CoeusLoanMarket<CoeusZopaLoan, CoeusZopaTransaction> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusZopaMarket.class.getSimpleName(), CoeusLoanMarket.getBaseFields());

    /**
     * The Decimal size.
     */
    protected static final int DECIMAL_SIZE = 8;

    /**
     * The LoanBook Parser.
     */
    private final CoeusZopaLoanBookParser theBookParser;

    /**
     * The Transaction Parser.
     */
    private final CoeusZopaTransactionParser theXactionParser;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public CoeusZopaMarket(final MetisDataFormatter pFormatter) {
        /* Initialise underlying class */
        super(pFormatter, CoeusLoanMarketProvider.ZOPA);

        /* Create the parsers */
        theBookParser = new CoeusZopaLoanBookParser(pFormatter);
        theXactionParser = new CoeusZopaTransactionParser(this);
    }

    /**
     * Parse the loanBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseLoanBook(final File pFile) throws OceanusException {
        /* Parse the file */
        theBookParser.parseFile(pFile);

        /* Loop through the loan book items */
        Iterator<CoeusZopaLoanBookItem> myIterator = theBookParser.loanIterator();
        while (myIterator.hasNext()) {
            CoeusZopaLoanBookItem myItem = myIterator.next();

            /* Look for preExisting loan */
            CoeusZopaLoan myLoan = getLoanById(myItem.getLoanId());
            if (myLoan == null) {
                /* Create the loan and record it */
                recordLoan(new CoeusZopaLoan(this, myItem));
            } else {
                /* Add the bookItem to the loan */
                myLoan.addBookItem(myItem);
            }
        }
    }

    /**
     * Parse the statement file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseStatement(final File pFile) throws OceanusException {
        /* Parse the file */
        theXactionParser.parseFile(pFile);

        /* Loop through the transactions in reverse order */
        ListIterator<CoeusZopaTransaction> myIterator = theXactionParser.reverseTransactionIterator();
        while (myIterator.hasPrevious()) {
            CoeusZopaTransaction myTrans = myIterator.previous();
            CoeusZopaLoan myLoan = myTrans.getLoan();

            /* If we have a loan */
            if (myLoan != null) {
                /* Record the transaction */
                myLoan.addTransaction(myTrans);

                /* else handle as administration transactions */
            } else {
                /* Add to adminList */
                addAdminTransaction(myTrans);
            }

            /* Add to the transactions */
            addTransaction(myTrans);
        }
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }
}
