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
package net.sourceforge.joceanus.jcoeus.ui.report;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarket;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketAnnual;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketSnapShot;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Coeus Filter.
 */
public interface CoeusFilter {
    /**
     * Obtain the market.
     * @return the market
     */
    CoeusMarket getMarket();

    /**
     * Obtain the totalSet.
     * @return the totalSet
     */
    CoeusTotalSet getTotalSet();

    /**
     * Obtain the history.
     * @return the history
     */
    CoeusHistory getHistory();

    /**
     * The Market SnapShot Filter.
     */
    class CoeusMarketSnapShotFilter
            implements CoeusFilter {
        /**
         * The Market Year.
         */
        private final CoeusMarketSnapShot theSnapShot;

        /**
         * The Loan.
         */
        private final CoeusLoan theLoan;

        /**
         * The TotalSet.
         */
        private final CoeusTotalSet theTotalSet;

        /**
         * Constructor.
         * @param pSnapShot the marketSnapShot.
         * @param pTotalSet the totalSet.
         */
        public CoeusMarketSnapShotFilter(final CoeusMarketSnapShot pSnapShot,
                                         final CoeusTotalSet pTotalSet) {
            this(pSnapShot, pTotalSet, null);
        }

        /**
         * Constructor.
         * @param pSnapShot the marketSnapShot.
         * @param pLoan the loan.
         */
        public CoeusMarketSnapShotFilter(final CoeusMarketSnapShot pSnapShot,
                                         final CoeusLoan pLoan) {
            this(pSnapShot, null, pLoan);
        }

        /**
         * Constructor.
         * @param pSnapShot the marketSnapShot.
         * @param pTotalSet the totalSet
         * @param pLoan the loan.
         */
        private CoeusMarketSnapShotFilter(final CoeusMarketSnapShot pSnapShot,
                                          final CoeusTotalSet pTotalSet,
                                          final CoeusLoan pLoan) {
            theSnapShot = pSnapShot;
            theTotalSet = pTotalSet;
            theLoan = pLoan;
        }

        @Override
        public CoeusMarket getMarket() {
            return theSnapShot.getMarket();
        }

        @Override
        public CoeusTotalSet getTotalSet() {
            return theTotalSet;
        }

        @Override
        public CoeusHistory getHistory() {
            return theLoan == null
                                   ? theSnapShot.getHistory()
                                   : theLoan.getHistory();
        }
    }

    /**
     * The Market Year Filter.
     */
    class CoeusMarketYearFilter
            implements CoeusFilter {
        /**
         * The Market Year.
         */
        private final CoeusMarketAnnual theYear;

        /**
         * The TotalSet.
         */
        private final CoeusTotalSet theTotalSet;

        /**
         * The Market Month.
         */
        private final TethysDate theMonth;

        /**
         * Constructor.
         * @param pYear the marketYear.
         * @param pTotalSet the totalSet
         * @param pMonth the month.
         */
        public CoeusMarketYearFilter(final CoeusMarketAnnual pYear,
                                     final CoeusTotalSet pTotalSet,
                                     final TethysDate pMonth) {
            theYear = pYear;
            theTotalSet = pTotalSet;
            theMonth = pMonth;
        }

        @Override
        public CoeusMarket getMarket() {
            return theYear.getMarket();
        }

        @Override
        public CoeusTotalSet getTotalSet() {
            return theTotalSet;
        }

        /**
         * Obtain the month.
         * @return the month
         */
        public TethysDate getMonth() {
            return theMonth;
        }

        @Override
        public CoeusHistory getHistory() {
            return theMonth == null
                                    ? theYear.getHistory()
                                    : theYear.getMonthlyHistory(theMonth);
        }
    }
}
