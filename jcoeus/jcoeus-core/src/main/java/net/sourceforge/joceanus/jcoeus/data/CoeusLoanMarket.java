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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.CoeusResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Loan Market.
 * @param <L> the loan type
 * @param <T> the transaction type
 */
public abstract class CoeusLoanMarket<L extends CoeusLoan<L, T>, T extends CoeusTransaction<L, T>>
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusLoanMarket.class.getSimpleName());

    /**
     * Provider Field Id.
     */
    private static final MetisField FIELD_PROVIDER = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_PROVIDER.getValue());

    /**
     * LoanMap Field Id.
     */
    private static final MetisField FIELD_LOANS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANMAP.getValue());

    /**
     * Transactions Field Id.
     */
    private static final MetisField FIELD_TRANS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_TRANSACTIONS.getValue());

    /**
     * AdminItems Field Id.
     */
    private static final MetisField FIELD_ADMIN = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_ADMINITEMS.getValue());

    /**
     * Loan Market Provider.
     */
    private final CoeusLoanMarketProvider theProvider;

    /**
     * Data Formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Loan Map.
     */
    private final Map<String, L> theLoanMap;

    /**
     * The List of Transactions.
     */
    private final List<T> theTransactions;

    /**
     * The Transactions that are not specific to a loan.
     */
    private final List<T> theAdmin;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     * @param pProvider the loanMarket provider
     */
    protected CoeusLoanMarket(final MetisDataFormatter pFormatter,
                              final CoeusLoanMarketProvider pProvider) {
        /* Store parameters */
        theFormatter = pFormatter;
        theProvider = pProvider;

        /* Create maps */
        theLoanMap = new LinkedHashMap<>();

        /* Create lists */
        theTransactions = new ArrayList<>();
        theAdmin = new ArrayList<>();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusLoanMarketProvider getProvider() {
        return theProvider;
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    public MetisDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the loan iterator.
     * @return the iterator
     */
    public Iterator<L> loanIterator() {
        return theLoanMap.values().iterator();
    }

    /**
     * Obtain the transaction iterator.
     * @return the iterator
     */
    public Iterator<T> transactionIterator() {
        return theTransactions.iterator();
    }

    /**
     * Obtain the administration iterator.
     * @return the iterator
     */
    public Iterator<T> adminIterator() {
        return theTransactions.iterator();
    }

    /**
     * LookUp Loan by loanId.
     * @param pId the id of the loan
     * @return the loan
     * @throws OceanusException on error
     */
    public L findLoanById(final String pId) throws OceanusException {
        L myLoan = getLoanById(pId);
        if (myLoan == null) {
            throw new CoeusDataException(pId, "Unrecognised LoanId");
        }
        return myLoan;
    }

    /**
     * Obtain pre-existing loan.
     * @param pId the id of the loan
     * @return the loan
     * @throws OceanusException on error
     */
    protected L getLoanById(final String pId) throws OceanusException {
        return theLoanMap.get(pId);
    }

    /**
     * Record loan.
     * @param pLoan the loan
     * @throws OceanusException on error
     */
    protected void recordLoan(final L pLoan) throws OceanusException {
        /* Ensure that the id is unique */
        String myId = pLoan.getLoanId();
        if (theLoanMap.get(myId) != null) {
            throw new CoeusDataException(myId, "Duplicate LoanId");
        }

        /* Record the loan */
        theLoanMap.put(myId, pLoan);
    }

    /**
     * Add transaction to list.
     * @param pTrans the transaction
     */
    protected void addTransaction(final T pTrans) {
        theTransactions.add(pTrans);
    }

    /**
     * Add administration transaction to list.
     * @param pTrans the transaction
     */
    protected void addAdminTransaction(final T pTrans) {
        theAdmin.add(pTrans);
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_PROVIDER.equals(pField)) {
            return theProvider;
        }
        if (FIELD_LOANS.equals(pField)) {
            return theLoanMap;
        }
        if (FIELD_TRANS.equals(pField)) {
            return theTransactions;
        }
        if (FIELD_ADMIN.equals(pField)) {
            return theAdmin;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
