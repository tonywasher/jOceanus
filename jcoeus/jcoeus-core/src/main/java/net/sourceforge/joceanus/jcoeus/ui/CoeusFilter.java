/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ui;

import java.time.Month;
import java.util.Iterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketAnnual;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketSnapShot;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketType;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Coeus Filter.
 */
public interface CoeusFilter {
    /**
     * Obtain the market provider.
     * @return the market provider
     */
    CoeusMarketProvider getProvider();

    /**
     * Obtain the market type.
     * @return the market type
     */
    CoeusMarketType getMarketType();

    /**
     * Obtain the totalSet.
     * @return the totalSet
     */
    CoeusTotalSet getTotalSet();

    /**
     * Set the TotalSet.
     * @param pTotalSet the totalSet
     */
    void setTotalSet(CoeusTotalSet pTotalSet);

    /**
     * Obtain the selected date.
     * @return the selected date
     */
    TethysDate getSelectedDate();

    /**
     * ,/ Obtain the history.
     * @return the history
     */
    CoeusHistory getHistory();

    /**
     * The Market SnapShot Filter.
     */
    class CoeusSnapShotFilter
            implements CoeusFilter {
        /**
         * The Market SnapShot.
         */
        private final CoeusMarketSnapShot theSnapShot;

        /**
         * The Loan.
         */
        private CoeusLoan theLoan;

        /**
         * The TotalSet.
         */
        private CoeusTotalSet theTotalSet;

        /**
         * Constructor.
         * @param pSnapShot the marketSnapShot.
         */
        public CoeusSnapShotFilter(final CoeusMarketSnapShot pSnapShot) {
            theSnapShot = pSnapShot;
        }

        /**
         * Obtain the loans iterator.
         * @return the iterator
         */
        public Iterator<CoeusLoan> loanIterator() {
            return theSnapShot.loanIterator();
        }

        @Override
        public CoeusMarketProvider getProvider() {
            return theSnapShot.getMarket().getProvider();
        }

        @Override
        public CoeusMarketType getMarketType() {
            return CoeusMarketType.SNAPSHOT;
        }

        /**
         * Obtain the loan.
         * @return the loan
         */
        public CoeusLoan getLoan() {
            return theLoan;
        }

        /**
         * Set the Loan.
         * @param pLoan the loan
         */
        public void setLoan(final CoeusLoan pLoan) {
            theLoan = pLoan;
        }

        @Override
        public CoeusTotalSet getTotalSet() {
            return theTotalSet;
        }

        @Override
        public void setTotalSet(final CoeusTotalSet pTotalSet) {
            theTotalSet = pTotalSet;
        }

        @Override
        public TethysDate getSelectedDate() {
            return theSnapShot.getDate();
        }

        @Override
        public CoeusHistory getHistory() {
            return theLoan == null
                                   ? theSnapShot.getHistory()
                                   : theLoan.getHistory();
        }
    }

    /**
     * The Market Annual Filter.
     */
    class CoeusAnnualFilter
            implements CoeusFilter {
        /**
         * The Market Annual.
         */
        private final CoeusMarketAnnual theAnnual;

        /**
         * The Selected Date.
         */
        private final TethysDate theSelectedDate;

        /**
         * The TotalSet.
         */
        private CoeusTotalSet theTotalSet;

        /**
         * The Market Month.
         */
        private Month theMonth;

        /**
         * Constructor.
         * @param pAnnual the marketAnnual.
         * @param pDate the selected date
         */
        public CoeusAnnualFilter(final CoeusMarketAnnual pAnnual,
                                 final TethysDate pDate) {
            theAnnual = pAnnual;
            theSelectedDate = pDate;
        }

        /**
         * is the month available?
         * @param pMonth the month
         * @return true/false
         */
        public boolean availableMonth(final Month pMonth) {
            return theAnnual.availableMonth(pMonth);
        }

        @Override
        public CoeusMarketProvider getProvider() {
            return theAnnual.getMarket().getProvider();
        }

        @Override
        public CoeusMarketType getMarketType() {
            return CoeusMarketType.ANNUAL;
        }

        @Override
        public CoeusTotalSet getTotalSet() {
            return theTotalSet;
        }

        @Override
        public void setTotalSet(final CoeusTotalSet pTotalSet) {
            theTotalSet = pTotalSet;
        }

        /**
         * Obtain the month.
         * @return the month
         */
        public Month getMonth() {
            return theMonth;
        }

        /**
         * Set the Month.
         * @param pMonth the month
         */
        public void setMonth(final Month pMonth) {
            theMonth = pMonth;
        }

        @Override
        public TethysDate getSelectedDate() {
            return theSelectedDate;
        }

        @Override
        public CoeusHistory getHistory() {
            return theMonth == null
                                    ? theAnnual.getHistory()
                                    : theAnnual.getMonthlyHistory(theMonth);
        }
    }
}
