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
package net.sourceforge.joceanus.coeus.data.lendingworks;

import net.sourceforge.joceanus.coeus.exc.CoeusDataException;
import net.sourceforge.joceanus.coeus.data.CoeusLoan;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * LendingWorks Loan.
 */
public class CoeusLendingWorksLoan
        extends CoeusLoan {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusLendingWorksLoan> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusLendingWorksLoan.class);

    /**
     * The loanId.
     */
    private final Integer theLoanIdNo;

    /**
     * Constructor.
     * @param pMarket the market
     * @param pLoanId the loan id
     */
    CoeusLendingWorksLoan(final CoeusLendingWorksMarket pMarket,
                          final String pLoanId) {
        super(pMarket, pLoanId);
        theLoanIdNo = Integer.parseInt(pLoanId);
    }

    /**
     * Constructor.
     * @param pLoan the loan
     * @param pRange the dateRange
     */
    CoeusLendingWorksLoan(final CoeusLendingWorksLoan pLoan,
                          final OceanusDateRange pRange) {
        super(pLoan, pRange);
        theLoanIdNo = pLoan.theLoanIdNo;
    }

    @Override
    public CoeusLendingWorksMarket getMarket() {
        return (CoeusLendingWorksMarket) super.getMarket();
    }

    @Override
    protected CoeusLendingWorksHistory newHistory() {
        return new CoeusLendingWorksHistory(this);
    }

    @Override
    public int compareTo(final CoeusLoan pThat) {
        return theLoanIdNo.compareTo(((CoeusLendingWorksLoan) pThat).theLoanIdNo);
    }

    @Override
    protected void checkLoan() throws CoeusDataException {
        /* NoOp */
    }

    @Override
    public OceanusMoney getBalance() {
        return null;
    }

    @Override
    public MetisFieldSet<CoeusLendingWorksLoan> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
