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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * RateSetter class to repair data structures.
 * <ol>
 * <li>Calculate the amount of the initial loan by adding the balance to the total repayments for
 * active loans
 * <li>Matches initial loan transactions to the one or more loans that it serviced
 * </ol>
 */
public class CoeusRateSetterRepair {
    /**
     * The Comparator.
     */
    private static final Comparator<CoeusRateSetterLoan> LOAN_COMPARATOR = (l, r) -> l.getLoanBookItem().getStartDate().compareTo(r.getLoanBookItem().getStartDate());

    /**
     * No delay.
     */
    private static final int NO_DELAY = 0;

    /**
     * The Short delay (3 days - enough for a weekend).
     */
    private static final int SHORT_DELAY = 3;

    /**
     * The Long delay (10 days - enough for a holiday).
     */
    private static final int LONG_DELAY = 10;

    /**
     * The Market.
     */
    private final CoeusRateSetterMarket theMarket;

    /**
     * The List of Initial Capital Loans.
     */
    private final List<CoeusRateSetterTransaction> theInitialLoans;

    /**
     * Constructor.
     * @param pMarket the market
     */
    protected CoeusRateSetterRepair(final CoeusRateSetterMarket pMarket) {
        theMarket = pMarket;
        theInitialLoans = new ArrayList<>();
    }

    /**
     * Record an initial loan.
     * @param pLoan the initial loan
     */
    protected void recordInitialLoan(final CoeusRateSetterTransaction pLoan) {
        theInitialLoans.add(pLoan);
    }

    /**
     * repair loans such that original loan is known and original payments is associated.
     * @throws OceanusException on error
     */
    protected void repairLoans() throws OceanusException {
        /* Obtain the list of repaired loans */
        List<CoeusRateSetterLoan> myLoans = getRepairedLoans();

        /* Handle Exact Matches first */
        handleExactMatches(myLoans, NO_DELAY);

        /* Handle Split Matches next */
        handleSplitMatches(myLoans, NO_DELAY);

        /* Handle delayed Exact Matches next */
        handleExactMatches(myLoans, SHORT_DELAY);

        /* Handle delayed Split Matches next */
        handleSplitMatches(myLoans, SHORT_DELAY);

        /* Handle severely delayed Exact Matches next */
        handleExactMatches(myLoans, LONG_DELAY);

        /* Handle severely delayed Split Matches next */
        handleSplitMatches(myLoans, LONG_DELAY);

        /* If we still have initial transactions */
        if (!theInitialLoans.isEmpty()) {
            throw new CoeusDataException("Orphan initial loans");
        }
    }

    /**
     * Get repaired loan list.
     * @return the sorted repaired loan list
     */
    private List<CoeusRateSetterLoan> getRepairedLoans() {
        /* Create a list of all the loans */
        List<CoeusRateSetterLoan> myLoans = new ArrayList<>();

        /* Loop through all the loans */
        Iterator<CoeusLoan> myIterator = theMarket.loanIterator();
        while (myIterator.hasNext()) {
            CoeusRateSetterLoan myLoan = (CoeusRateSetterLoan) myIterator.next();

            /* If the loan is active */
            CoeusRateSetterLoanBookItem myItem = myLoan.getLoanBookItem();
            if (CoeusLoanStatus.ACTIVE.equals(myItem.getStatus())) {
                /* Add repayments to the amount of the loan which is currently the balance */
                TethysMoney myLent = myItem.getLent();
                CoeusRateSetterTotals myTotals = myLoan.getTotals();
                myLent.subtractAmount(myTotals.getLoanBook());
            }

            /* Add the loan to the list */
            myLoans.add(myLoan);
        }

        /* Sort the loans */
        myLoans.sort(LOAN_COMPARATOR);

        /* Return the loans */
        return myLoans;
    }

    /**
     * Match loans where there is an exact match on amount and date.
     * @param pLoans the loans
     * @param pNumDays the number of days allowed between funding transaction and loan start date
     */
    private void handleExactMatches(final List<CoeusRateSetterLoan> pLoans,
                                    final int pNumDays) {
        /* Loop through all the loans */
        Iterator<CoeusRateSetterLoan> myLoanIterator = pLoans.iterator();
        while (myLoanIterator.hasNext()) {
            CoeusRateSetterLoan myLoan = myLoanIterator.next();
            CoeusRateSetterLoanBookItem myBookItem = myLoan.getLoanBookItem();
            TethysDate myDate = myBookItem.getStartDate();
            TethysMoney myLent = myBookItem.getLent();

            /* Loop through all the initial loans */
            boolean doLoop = true;
            Iterator<CoeusRateSetterTransaction> myInitIterator = theInitialLoans.iterator();
            while (doLoop && myInitIterator.hasNext()) {
                CoeusRateSetterTransaction myTrans = myInitIterator.next();
                long myDays = myDate.daysUntil(myTrans.getDate());

                /* Break loop if we are not going to get a match */
                if (myDays > 0) {
                    doLoop = false;

                    /* If we have a matching loan */
                } else if ((myDays >= -pNumDays)
                           && myLent.equals(myTrans.getLoanBook())) {
                    /* Match and remove them */
                    myTrans.setLoan(myLoan);
                    myInitIterator.remove();
                    myLoanIterator.remove();
                    doLoop = false;
                }
            }
        }
    }

    /**
     * Match loans where there is an initial loan that is split into multiple loans.
     * @param pLoans the loans list
     * @param pNumDays the number of days allowed between funding transaction and loan start date
     * @throws OceanusException on error
     */
    private void handleSplitMatches(final List<CoeusRateSetterLoan> pLoans,
                                    final int pNumDays) throws OceanusException {
        /* Create loan list */
        List<CoeusRateSetterLoan> myLoans = new ArrayList<>();

        /* Loop through all the initial loans */
        Iterator<CoeusRateSetterTransaction> myInitIterator = theInitialLoans.iterator();
        while (myInitIterator.hasNext()) {
            CoeusRateSetterTransaction myTrans = myInitIterator.next();
            TethysDate myDate = myTrans.getDate();

            /* Look for loans on the same date */
            myLoans.clear();
            TethysMoney myLoanAmount = new TethysMoney();
            Iterator<CoeusRateSetterLoan> myLoanIterator = pLoans.iterator();
            while (myLoanIterator.hasNext()) {
                CoeusRateSetterLoan myLoan = myLoanIterator.next();
                CoeusRateSetterLoanBookItem myBookItem = myLoan.getLoanBookItem();
                TethysDate myStartDate = myBookItem.getStartDate();
                long myDays = myDate.daysUntil(myStartDate);

                /* If there is a possible match */
                if (myDays >= 0) {
                    /* If we have a matching date */
                    if (myDays <= pNumDays) {
                        /* Switch it to the list */
                        myLoans.add(myLoan);
                        myLoanIterator.remove();
                        myLoanAmount.addAmount(myBookItem.getLent());

                        /* Break loop if we are not going to get a match */
                    } else {
                        break;
                    }
                }
            }

            /* If we have matched the amount */
            if (myLoanAmount.equals(myTrans.getLoanBook())) {
                /* Loop through the loans */
                for (CoeusRateSetterLoan myLoan : myLoans) {
                    /* Create a new transaction based on the loan */
                    CoeusRateSetterTransaction myNewTran = new CoeusRateSetterTransaction(myTrans, myLoan);
                    theMarket.addTheTransaction(myNewTran);
                }

                /* Remove the transaction from the lists */
                myInitIterator.remove();
                theMarket.removeTransaction(myTrans);

                /* else if we have removed elements from the list */
            } else if (!myLoans.isEmpty()) {
                /* Add the loans back into the list */
                pLoans.addAll(myLoans);

                /* Sort the loans */
                pLoans.sort(LOAN_COMPARATOR);
            }
        }
    }
}
