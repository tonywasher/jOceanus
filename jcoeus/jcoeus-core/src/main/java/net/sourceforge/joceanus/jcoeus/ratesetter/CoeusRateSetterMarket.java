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
package net.sourceforge.joceanus.jcoeus.ratesetter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jcoeus.CoeusResource;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusTransactionType;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * RateSetter Market.
 */
public class CoeusRateSetterMarket
        extends CoeusLoanMarket<CoeusRateSetterLoan, CoeusRateSetterTransaction> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusRateSetterMarket.class.getSimpleName(), CoeusLoanMarket.getBaseFields());

    /**
     * InitialLoans Field Id.
     */
    private static final MetisField FIELD_INITIALLOANS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_INITIALLOANS.getValue());

    /**
     * The LoanBook Parser.
     */
    private final CoeusRateSetterLoanBookParser theBookParser;

    /**
     * The Transaction Parser.
     */
    private final CoeusRateSetterTransactionParser theXactionParser;

    /**
     * The List of Initial Capital Loans.
     */
    private final List<CoeusRateSetterTransaction> theInitialLoans;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @throws OceanusException on error
     */
    public CoeusRateSetterMarket(final MetisDataFormatter pFormatter) throws OceanusException {
        /* Initialise underlying class */
        super(pFormatter, CoeusLoanMarketProvider.RATESETTER);

        /* Create the parsers */
        theBookParser = new CoeusRateSetterLoanBookParser(pFormatter);
        theXactionParser = new CoeusRateSetterTransactionParser(this);

        /* Create the lists */
        theInitialLoans = new ArrayList<>();
    }

    /**
     * Parse the loanBook file.
     * @param pFile the file to parse
     * @throws OceanusException on error
     */
    public void parseLoanBook(final File pFile) throws OceanusException {
        /* Parse the file */
        theBookParser.parseFile(pFile);

        /* Loop through the loanBook items */
        Iterator<CoeusRateSetterLoanBookItem> myIterator = theBookParser.loanIterator();
        while (myIterator.hasNext()) {
            CoeusRateSetterLoanBookItem myItem = myIterator.next();

            /* Look for preExisting loan */
            CoeusRateSetterLoan myLoan = getLoanById(myItem.getLoanId());
            if (myLoan == null) {
                /* Create the loan and record it */
                recordLoan(new CoeusRateSetterLoan(this, myItem));
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
        ListIterator<CoeusRateSetterTransaction> myIterator = theXactionParser.reverseTransactionIterator();
        while (myIterator.hasPrevious()) {
            CoeusRateSetterTransaction myTrans = myIterator.previous();
            CoeusRateSetterLoan myLoan = myTrans.getLoan();

            /* If we have a loan */
            if (myLoan != null) {
                /* Record the transaction */
                myLoan.addTransaction(myTrans);

                /* If this is a CapitalLoan */
            } else if (CoeusTransactionType.CAPITALLOAN.equals(myTrans.getTransType())) {
                /* Add to set of initial loans for later resolution */
                theInitialLoans.add(myTrans);

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

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_INITIALLOANS.equals(pField)) {
            return theInitialLoans.isEmpty()
                                             ? MetisFieldValue.SKIP
                                             : theInitialLoans;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }
}
