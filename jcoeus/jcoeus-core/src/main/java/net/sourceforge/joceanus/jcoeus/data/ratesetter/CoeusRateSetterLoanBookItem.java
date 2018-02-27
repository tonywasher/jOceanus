/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.ratesetter;

import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Element;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * RateSetter Loan Book Item.
 */
public class CoeusRateSetterLoanBookItem
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<CoeusRateSetterLoanBookItem> FIELD_DEFS = MetisFieldSet.newFieldSet(CoeusRateSetterLoanBookItem.class);

    /**
     * Builder buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANID, CoeusRateSetterLoanBookItem::getLoanId);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_STARTDATE, CoeusRateSetterLoanBookItem::getStartDate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LENT, CoeusRateSetterLoanBookItem::getLent);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_BALANCE, CoeusRateSetterLoanBookItem::getBalance);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_RATE, CoeusRateSetterLoanBookItem::getRate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LASTDATE, CoeusRateSetterLoanBookItem::getLastDate);
        FIELD_DEFS.declareLocalField(CoeusResource.DATA_LOANSTATUS, CoeusRateSetterLoanBookItem::getStatus);
    }

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
    private final TethysMoney theLent;

    /**
     * The Outstanding Balance.
     */
    private final TethysMoney theBalance;

    /**
     * The rate.
     */
    private final TethysRate theRate;

    /**
     * The LastDate.
     */
    private final TethysDate theLastDate;

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
    protected CoeusRateSetterLoanBookItem(final CoeusRateSetterLoanBookParser pParser,
                                          final boolean pRepaid,
                                          final List<Element> pCells) throws OceanusException {
        /* Iterate through the cells */
        final Iterator<Element> myIterator = pCells.iterator();

        /* Obtain the loanId */
        theLoanId = myIterator.next().text();

        /* Obtain the startDate */
        theStartDate = pParser.parseDate(myIterator.next().text());

        /* Obtain the amount */
        final String myAmountText = pParser.childElementText(myIterator.next());
        final TethysMoney myAmount = pParser.parseMoney(myAmountText);
        final TethysMoney myZero = new TethysMoney(myAmount);
        myZero.setZero();

        /* Set balance and loan */
        theLent = pRepaid
                          ? myAmount
                          : myZero;
        theBalance = pRepaid
                             ? myZero
                             : myAmount;

        /* adjust the lent to include the balance */
        theLent.addAmount(theBalance);

        /* Obtain the rate */
        theRate = pParser.parseRate(myIterator.next().text());

        /* Obtain the lastDate */
        theLastDate = pParser.parseDate(myIterator.next().text());

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
     * Obtain the lent amount.
     * @return the lent
     */
    public TethysMoney getLent() {
        return theLent;
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
     * Obtain the lastDate.
     * @return the startDate
     */
    public TethysDate getLastDate() {
        return theLastDate;
    }

    /**
     * Obtain the status.
     * @return the status
     */
    public CoeusLoanStatus getStatus() {
        return theStatus;
    }

    @Override
    public String toString() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append(theLoanId).append(' ').append(theStatus).append(' ').append(theBalance);
        return myBuilder.toString();
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public MetisFieldSet<CoeusRateSetterLoanBookItem> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
