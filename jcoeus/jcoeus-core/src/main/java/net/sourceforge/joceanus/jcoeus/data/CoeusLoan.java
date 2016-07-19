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
package net.sourceforge.joceanus.jcoeus.data;

import net.sourceforge.joceanus.jcoeus.CoeusResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Coeus Loan.
 * @param <L> the loan type
 * @param <T> the transaction type
 * @param <S> the totals type
 * @param <H> the history type
 */
public abstract class CoeusLoan<L extends CoeusLoan<L, T, S, H>, T extends CoeusTransaction<L, T, S, H>, S extends CoeusTotals<L, T, S, H>, H extends CoeusHistory<L, T, S, H>>
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
     * History Field Id.
     */
    private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_HISTORY.getValue());

    /**
     * Loan Market.
     */
    private final CoeusLoanMarket<L, T, S, H> theMarket;

    /**
     * Loan Id.
     */
    private final String theLoanId;

    /**
     * The TotalsHistory.
     */
    private final H theHistory;

    /**
     * Constructor.
     * @param pMarket the loanMarket
     * @param pId the loan Id
     */
    protected CoeusLoan(final CoeusLoanMarket<L, T, S, H> pMarket,
                        final String pId) {
        /* Store parameters */
        theMarket = pMarket;
        theLoanId = pId;

        /* Create the histories */
        theHistory = newHistory();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusLoanMarket<L, T, S, H> getMarket() {
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
     * Obtain the history.
     * @return the history
     */
    public H getHistory() {
        return theHistory;
    }

    /**
     * Obtain the totals.
     * @return the totals
     */
    public S getTotals() {
        return theHistory.getTotals();
    }

    /**
     * Obtain the balance.
     * @return the balance
     */
    public abstract TethysDecimal getBalance();

    /**
     * Clear the history.
     */
    protected void clearHistory() {
        theHistory.clear();
    }

    /**
     * Add the transaction to the history.
     * @param pTrans the transaction
     */
    protected void addTransactionToHistory(final T pTrans) {
        theHistory.addTransactionToHistory(pTrans);
    }

    /**
     * New history.
     * @return the history
     */
    protected abstract H newHistory();

    /**
     * CheckLoan.
     * @throws OceanusException on error
     */
    protected abstract void checkLoan() throws OceanusException;

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        return formatObject();
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
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
