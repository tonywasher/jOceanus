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

import net.sourceforge.joceanus.jcoeus.CoeusResource;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * RateSetter Loan Book Item.
 */
public class CoeusRateSetterLoanBookItem
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(CoeusRateSetterLoanBookItem.class.getSimpleName());

    /**
     * Loan Id Field Id.
     */
    private static final MetisField FIELD_LOANID = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANID.getValue());

    /**
     * StartDate Field Id.
     */
    private static final MetisField FIELD_STARTDATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_STARTDATE.getValue());

    /**
     * Original Loan Field Id.
     */
    private static final MetisField FIELD_LENT = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LENT.getValue());

    /**
     * Outstanding Balance Field Id.
     */
    private static final MetisField FIELD_BALANCE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_BALANCE.getValue());

    /**
     * Rate Field Id.
     */
    private static final MetisField FIELD_RATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_RATE.getValue());

    /**
     * LastDate Field Id.
     */
    private static final MetisField FIELD_LASTDATE = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LASTDATE.getValue());

    /**
     * Status Field Id.
     */
    private static final MetisField FIELD_STATUS = FIELD_DEFS.declareEqualityField(CoeusResource.DATA_LOANSTATUS.getValue());

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
        return formatObject();
    }

    @Override
    public String formatObject() {
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theLoanId);
        myBuilder.append(' ');
        myBuilder.append(theStatus.toString());
        myBuilder.append(' ');
        myBuilder.append(theBalance.toString());
        return myBuilder.toString();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_LOANID.equals(pField)) {
            return theLoanId;
        }
        if (FIELD_STARTDATE.equals(pField)) {
            return theStartDate;
        }
        if (FIELD_LENT.equals(pField)) {
            return theLent;
        }
        if (FIELD_BALANCE.equals(pField)) {
            return theBalance;
        }
        if (FIELD_RATE.equals(pField)) {
            return theRate;
        }
        if (FIELD_LASTDATE.equals(pField)) {
            return theLastDate;
        }
        if (FIELD_STATUS.equals(pField)) {
            return theStatus;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }
}
