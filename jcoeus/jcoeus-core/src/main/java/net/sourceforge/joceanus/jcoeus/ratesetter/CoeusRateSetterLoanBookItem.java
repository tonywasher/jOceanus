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

import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Element;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * FundingCircle Loan Book Item.
 */
public class CoeusRateSetterLoanBookItem {
    /**
     * The loan Id.
     */
    private final String theLoanId;

    /**
     * The StartDate.
     */
    private final TethysDate theStartDate;

    /**
     * The Original Loan.
     */
    private final TethysMoney theLoan;

    /**
     * The Outstanding Balance.
     */
    private final TethysMoney theBalance;

    /**
     * The rate.
     */
    private final TethysRate theRate;

    /**
     * The status.
     */
    private final CoeusLoanStatus theStatus;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pRepaid is this an rePaid loan?
     * @param pCells the cells
     * @throws OceanusException on error
     */
    public CoeusRateSetterLoanBookItem(final CoeusRateSetterLoanBookParser pParser,
                                       final boolean pRepaid,
                                       final List<Element> pCells) throws OceanusException {
        /* Iterate through the cells */
        Iterator<Element> myIterator = pCells.iterator();

        /* Obtain the loanId */
        theLoanId = myIterator.next().text();

        /* Obtain the startDate */
        theStartDate = pParser.parseDate(myIterator.next().text());

        /* Obtain the amount */
        String myAmountText = pParser.childElementText(myIterator.next());
        TethysMoney myAmount = pParser.parseMoney(myAmountText);
        TethysMoney myZero = new TethysMoney(myAmount);
        myZero.setZero();

        /* Set balance and loan */
        theLoan = pRepaid
                          ? myAmount
                          : myZero;
        theBalance = pRepaid
                             ? myZero
                             : myAmount;

        /* Obtain the rate */
        theRate = pParser.parseRate(myIterator.next().text());

        /* Set the status */
        theStatus = pRepaid
                            ? CoeusLoanStatus.REPAID
                            : CoeusLoanStatus.ACTIVE;

    }

    /**
     * Obtain the loanId.
     * @return the loan id
     */
    public String getLoanId() {
        return theLoanId;
    }

    /**
     * Obtain the startDate.
     * @return the startDate
     */
    public TethysDate getStartDate() {
        return theStartDate;
    }

    /**
     * Obtain the loan.
     * @return the loan
     */
    public TethysMoney getLoan() {
        return theLoan;
    }

    /**
     * Obtain the loan.
     * @return the loan
     */
    public TethysMoney getBalance() {
        return theBalance;
    }

    /**
     * Obtain the rate.
     * @return the rate
     */
    public TethysRate getRate() {
        return theRate;
    }

    /**
     * Obtain the status.
     * @return the status
     */
    public CoeusLoanStatus getStatus() {
        return theStatus;
    }
}
