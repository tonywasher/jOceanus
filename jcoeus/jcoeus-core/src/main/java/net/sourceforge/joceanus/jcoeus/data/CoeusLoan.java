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
package net.sourceforge.joceanus.jcoeus.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;

/**
 * Coeus Loan.
 * @param <L> the loan type
 * @param <T> the transaction type
 */
public abstract class CoeusLoan<L extends CoeusLoan<L, T>, T extends CoeusTransaction<L, T>>
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusLoan.class.getSimpleName());

    /**
     * Market Field Id.
     */
    private static final MetisField FIELD_MARKET = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_MARKET.getValue());

    /**
     * LoanId Field Id.
     */
    private static final MetisField FIELD_LOANID = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANID.getValue());

    /**
     * Transactions Field Id.
     */
    private static final MetisField FIELD_TRANS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TRANSACTIONS.getValue());

    /**
     * Loan Market.
     */
    private final CoeusLoanMarket<L, T> theMarket;

    /**
     * Loan Id.
     */
    private final String theLoanId;

    /**
     * Loan Transactions.
     */
    private final List<T> theTransactions;

    /**
     * Constructor.
     * @param pMarket the loanMarket
     * @param pId the loan Id
     */
    protected CoeusLoan(final CoeusLoanMarket<L, T> pMarket,
                        final String pId) {
        /* Store parameters */
        theMarket = pMarket;
        theLoanId = pId;

        /* create the transaction list */
        theTransactions = new ArrayList<>();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusLoanMarket<L, T> getMarket() {
        return theMarket;
    }

    /**
     * Obtain the loan Id.
     * @return the id
     */
    public String getLoanId() {
        return theLoanId;
    }

    /**
     * Obtain the transaction iterator.
     * @return the iterator
     */
    public Iterator<T> transactionIterator() {
        return theTransactions.iterator();
    }

    /**
     * Add the transaction to the list.
     * @param pTrans the transaction
     */
    public void addTransaction(final T pTrans) {
        theTransactions.add(pTrans);
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return theLoanId;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_MARKET.equals(pField)) {
            return theMarket;
        }
        if (FIELD_LOANID.equals(pField)) {
            return theLoanId;
        }
        if (FIELD_TRANS.equals(pField)) {
            return theTransactions;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
