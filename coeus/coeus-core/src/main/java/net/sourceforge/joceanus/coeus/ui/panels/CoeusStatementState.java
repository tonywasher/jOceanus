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
package net.sourceforge.joceanus.coeus.ui.panels;

import java.time.Month;
import java.util.Iterator;

import net.sourceforge.joceanus.coeus.data.CoeusCalendar;
import net.sourceforge.joceanus.coeus.data.CoeusLoan;
import net.sourceforge.joceanus.coeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.coeus.data.CoeusMarketType;
import net.sourceforge.joceanus.coeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter.CoeusAnnualFilter;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter.CoeusSnapShotFilter;
import net.sourceforge.joceanus.coeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;

/**
 * Statement Select State.
 */
public final class CoeusStatementState {
    /**
     * ReportSelect.
     */
    private final CoeusStatementSelect theSelect;

    /**
     * Calendar.
     */
    private CoeusCalendar theCalendar;

    /**
     * The filter.
     */
    private CoeusFilter theFilter;

    /**
     * The marketProvider.
     */
    private CoeusMarketProvider theProvider;

    /**
     * The marketType.
     */
    private CoeusMarketType theMarketType;

    /**
     * The selectedDate.
     */
    private OceanusDate theSelectedDate;

    /**
     * The loan.
     */
    private CoeusLoan theLoan;

    /**
     * The month.
     */
    private Month theMonth;

    /**
     * The totalSet.
     */
    private CoeusTotalSet theTotalSet;

    /**
     * Constructor.
     * @param pSelect the selector
     * @param pCalendar the Calendar
     */
    CoeusStatementState(final CoeusStatementSelect pSelect,
                        final CoeusCalendar pCalendar) {
        /* Store parameters */
        theSelect = pSelect;
        theCalendar = pCalendar;
    }

    /**
     * Constructor.
     * @param pState state to copy from
     */
    CoeusStatementState(final CoeusStatementState pState) {
        theSelect = pState.theSelect;
        theCalendar = pState.theCalendar;
        setFilter(pState.getFilter());
    }

    /**
     * Set Filter.
     * @param pFilter the filter to set
     */
    void setFilter(final CoeusFilter pFilter) {
        theFilter = pFilter;
        if (theFilter != null) {
            theProvider = theFilter.getProvider();
            theMarketType = theFilter.getMarketType();
            theSelectedDate = theFilter.getSelectedDate();
            theTotalSet = theFilter.getTotalSet();
        }
        if (pFilter instanceof CoeusAnnualFilter) {
            theMonth = ((CoeusAnnualFilter) pFilter).getMonth();
        }
        if (pFilter instanceof CoeusSnapShotFilter) {
            theLoan = ((CoeusSnapShotFilter) pFilter).getLoan();
        }
    }

    /**
     * Obtain the filter.
     * @return the market
     */
    CoeusFilter getFilter() {
        return theFilter;
    }

    /**
     * Obtain the selected market provider.
     * @return the market provider
     */
    CoeusMarketProvider getProvider() {
        return theProvider;
    }

    /**
     * Obtain the selected market type.
     * @return the market type
     */
    CoeusMarketType getMarketType() {
        return theMarketType;
    }

    /**
     * Obtain the selected totalSet.
     * @return the totalSet
     */
    CoeusTotalSet getTotalSet() {
        return theTotalSet;
    }

    /**
     * Obtain the selected Date.
     * @return the date
     */
    OceanusDate getSelectedDate() {
        return theSelectedDate;
    }

    /**
     * Obtain the Month.
     * @return the month
     */
    Month getMonth() {
        return theMonth;
    }

    /**
     * Obtain the Loan.
     * @return the loan
     */
    CoeusLoan getLoan() {
        return theLoan;
    }

    /**
     * Set the calendar.
     * @param pCalendar the calendar
     */
    void setCalendar(final CoeusCalendar pCalendar) {
        theCalendar = pCalendar;
    }

    /**
     * Set new Date.
     * @param pDate the date
     * @return true/false did a change occur
     */
    boolean setDate(final OceanusDate pDate) {
        /* Obtain the date and adjust it */
        final OceanusDate myDate = pDate == null
                                                ? null
                                                : new OceanusDate(pDate);

        /* Record any change and report change */
        if (!MetisDataDifference.isEqual(pDate, theSelectedDate)) {
            theSelectedDate = myDate;
            return allocateNewFilter();
        }
        return false;
    }

    /**
     * Set new MarketProvider.
     * @param pProvider the new market
     * @return true/false did a change occur
     */
    boolean setProvider(final CoeusMarketProvider pProvider) {
        if (!pProvider.equals(theProvider)) {
            /* Store the new provider */
            theProvider = pProvider;
            return allocateNewFilter();
        }
        return false;
    }

    /**
     * Set new MarketType.
     * @param pType the new marketType
     * @return true/false did a change occur
     */
    boolean setMarketType(final CoeusMarketType pType) {
        if (!pType.equals(theMarketType)) {
            /* Store the new marketType */
            theMarketType = pType;
            theSelect.setMarketType(theMarketType);
            return allocateNewFilter();
        }
        return false;
    }

    /**
     * Set new TotalSet.
     * @param pTotals the new totalSet
     * @return true/false did a change occur
     */
    boolean setTotalSet(final CoeusTotalSet pTotals) {
        if (!pTotals.equals(theTotalSet)) {
            /* Adjust the filter */
            theTotalSet = pTotals;
            if (theFilter != null) {
                theFilter.setTotalSet(pTotals);
                return true;
            }
        }
        return false;
    }

    /**
     * Set new Loan.
     * @param pLoan the new loan
     * @return true/false did a change occur
     */
    boolean setLoan(final CoeusLoan pLoan) {
        if (!MetisDataDifference.isEqual(pLoan, theLoan)) {
            /* Adjust the filter */
            theLoan = pLoan;
            if (theFilter instanceof CoeusSnapShotFilter) {
                ((CoeusSnapShotFilter) theFilter).setLoan(pLoan);
                return true;
            }
        }
        return false;
    }

    /**
     * Set new Month.
     * @param pMonth the new month
     * @return true/false did a change occur
     */
    boolean setMonth(final Month pMonth) {
        if (!MetisDataDifference.isEqual(pMonth, theMonth)) {
            /* Adjust the filter */
            theMonth = pMonth;
            if (theFilter instanceof CoeusAnnualFilter) {
                ((CoeusAnnualFilter) theFilter).setMonth(pMonth);
                return true;
            }
        }
        return false;
    }

    /**
     * Allocate new filter.
     * @return true/false did an allocation occur
     */
    boolean allocateNewFilter() {
        /* If there is an empty cache then no change */
        if (theSelect.getCache().isIdle()) {
            return false;
        }

        /* Switch on market type */
        switch (theMarketType) {
            case ANNUAL:
                allocateNewAnnualFilter();
                return true;

            case SNAPSHOT:
                allocateNewSnapShotFilter();
                return true;

            default:
                return false;
        }
    }

    /**
     * Allocate new annual filter.
     */
    void allocateNewAnnualFilter() {
        final OceanusDate myAnnualDate = theCalendar.getEndOfYear(theSelectedDate);
        final CoeusMarketCache myCache = theSelect.getCache();
        final CoeusAnnualFilter myFilter = new CoeusAnnualFilter(myCache.getAnnual(theProvider, myAnnualDate), theSelectedDate);
        if (theMonth != null
            && !myFilter.availableMonth(theMonth)) {
            theMonth = null;
        }
        myFilter.setMonth(theMonth);
        myFilter.setTotalSet(theTotalSet);
        theFilter = myFilter;
    }

    /**
     * available month?
     * @param pMonth the month
     * @return true/false
     */
    boolean availableMonth(final Month pMonth) {
        return theFilter instanceof CoeusAnnualFilter
               && ((CoeusAnnualFilter) theFilter).availableMonth(pMonth);
    }

    /**
     * Allocate new snapShot filter.
     */
    void allocateNewSnapShotFilter() {
        final CoeusMarketCache myCache = theSelect.getCache();
        final CoeusSnapShotFilter myFilter = new CoeusSnapShotFilter(myCache.getSnapShot(theProvider, theSelectedDate));
        myFilter.setLoan(theLoan);
        myFilter.setTotalSet(theTotalSet);
        theFilter = myFilter;
    }

    /**
     * Build loans menu.
     * @param pBuilder the menu builder
     */
    void buildLoansMenu(final TethysUIScrollMenu<CoeusLoan> pBuilder) {
        /* Only perform for snapShots */
        if (!(theFilter instanceof CoeusSnapShotFilter)) {
            return;
        }

        /* Loop through the loans */
        final Iterator<CoeusLoan> myIterator = ((CoeusSnapShotFilter) theFilter).loanIterator();
        while (myIterator.hasNext()) {
            final CoeusLoan myLoan = myIterator.next();
            /* Create a new MenuItem for the loan */
            pBuilder.addItem(myLoan);
        }
    }
}
