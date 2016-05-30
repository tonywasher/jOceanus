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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusResource;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;

/**
 * RateSetter Loan.
 */
public class CoeusRateSetterLoan
        extends CoeusLoan<CoeusRateSetterLoan, CoeusRateSetterTransaction, CoeusRateSetterTotals, CoeusRateSetterHistory> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusRateSetterLoan.class.getSimpleName(), CoeusLoan.getBaseFields());

    /**
     * LoanBookItem Field Id.
     */
    private static final MetisField FIELD_BOOKITEM = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BOOKITEM.getValue());

    /**
     * The market.
     */
    private final List<CoeusRateSetterLoanBookItem> theBookItems;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pBookItem the loan book item
     */
    protected CoeusRateSetterLoan(final CoeusRateSetterMarket pMarket,
                                  final CoeusRateSetterLoanBookItem pBookItem) {
        super(pMarket, pBookItem.getLoanId());
        theBookItems = new ArrayList<>();
        addBookItem(pBookItem);
    }

    @Override
    public CoeusRateSetterMarket getMarket() {
        return (CoeusRateSetterMarket) super.getMarket();
    }

    /**
     * Add a book item.
     * @param pBookItem the book item
     */
    protected void addBookItem(final CoeusRateSetterLoanBookItem pBookItem) {
        theBookItems.add(pBookItem);
    }

    /**
     * Obtain the book item iterator.
     * @return the iterator
     */
    public Iterator<CoeusRateSetterLoanBookItem> bookItemIterator() {
        return theBookItems.iterator();
    }

    @Override
    protected CoeusRateSetterHistory newHistory() {
        return new CoeusRateSetterHistory(this);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_BOOKITEM.equals(pField)) {
            return theBookItems;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }
}
