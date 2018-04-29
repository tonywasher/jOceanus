/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2018 Tony Washer
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

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

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
    protected CoeusLendingWorksLoan(final CoeusLendingWorksMarket pMarket,
                                    final String pLoanId) {
        super(pMarket, pLoanId);
        theLoanIdNo = Integer.parseInt(pLoanId);
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
    protected CoeusLendingWorksHistory newHistory(final TethysDate pDate) {
        return new CoeusLendingWorksHistory(this, pDate);
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
    public TethysMoney getBalance() {
        return null;
    }

    @Override
    public MetisFieldSet<CoeusLendingWorksLoan> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
