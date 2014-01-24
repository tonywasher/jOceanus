/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayButton;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;

/**
 * SpotPrice Date selection panel.
 * @author Tony Washer
 */
public class SpotSelect
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -361214955549174070L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(SpotSelect.class.getName());

    /**
     * Text for Date Label.
     */
    private static final String NLS_DATE = NLS_BUNDLE.getString("SelectDate");

    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORT = NLS_BUNDLE.getString("SelectPortfolio");

    /**
     * Text for Show Closed.
     */
    private static final String NLS_CLOSED = NLS_BUNDLE.getString("ShowClosed");

    /**
     * Text for Title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("SpotTitle");

    /**
     * Text for Next toolTip.
     */
    private static final String NLS_NEXTTIP = NLS_BUNDLE.getString("NextTip");

    /**
     * Text for Previous toolTip.
     */
    private static final String NLS_PREVTIP = NLS_BUNDLE.getString("PrevTip");

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The date button.
     */
    private final JDateDayButton theDateButton;

    /**
     * The showClosed checkBox.
     */
    private final JCheckBox theShowClosed;

    /**
     * The next button.
     */
    private final JButton theNext;

    /**
     * The previous button.
     */
    private final JButton thePrev;

    /**
     * The portfolio button.
     */
    private final JButton thePortButton;

    /**
     * The Account list.
     */
    private transient AccountList theAccounts = null;

    /**
     * The current state.
     */
    private transient SpotState theState = null;

    /**
     * The saved state.
     */
    private transient SpotState theSavePoint = null;

    /**
     * Do we show closed accounts.
     */
    private boolean doShowClosed = false;

    /**
     * Are we refreshing data?
     */
    private boolean refreshingData = false;

    /**
     * Get the selected date.
     * @return the date
     */
    public JDateDay getDate() {
        return theState.getDate();
    }

    /**
     * Get the selected portfolio.
     * @return the portfolio
     */
    public final Account getPortfolio() {
        return theState.getPortfolio();
    }

    /**
     * Do we show closed accounts?.
     * @return the date
     */
    public boolean getShowClosed() {
        return doShowClosed;
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public SpotSelect(final View pView) {
        /* Create listener */
        SpotListener myListener = new SpotListener();

        /* Store table and view details */
        theView = pView;

        /* Create Labels */
        JLabel myDate = new JLabel(NLS_DATE);
        JLabel myPort = new JLabel(NLS_PORT);

        /* Create the check box */
        theShowClosed = new JCheckBox(NLS_CLOSED);
        theShowClosed.setSelected(doShowClosed);

        /* Create the DateButton */
        theDateButton = new JDateDayButton();

        /* Create the Buttons */
        theNext = new JButton(ArrowIcon.RIGHT);
        thePrev = new JButton(ArrowIcon.LEFT);
        theNext.setToolTipText(NLS_NEXTTIP);
        thePrev.setToolTipText(NLS_PREVTIP);

        /* Create the portfolio button */
        thePortButton = new JButton(ArrowIcon.DOWN);
        thePortButton.setVerticalTextPosition(AbstractButton.CENTER);
        thePortButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create initial state */
        theState = new SpotState();

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(myDate);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(thePrev);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theDateButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theNext);
        add(Box.createHorizontalGlue());
        add(myPort);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(thePortButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theShowClosed);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Initialise the data from the view */
        refreshData();

        /* Apply the current state */
        theState.applyState();

        /* Add the listener for item changes */
        theDateButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, myListener);
        theShowClosed.addItemListener(myListener);
        theNext.addActionListener(myListener);
        thePrev.addActionListener(myListener);
        thePortButton.addActionListener(myListener);
    }

    /**
     * Refresh data.
     */
    public final void refreshData() {
        /* Access the data */
        JDateDayRange myRange = theView.getRange();

        /* Set the range for the Date Button */
        setRange(myRange);

        /* Access the data */
        MoneyWiseData myData = theView.getData();

        /* Access account list */
        theAccounts = myData.getAccounts();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* Obtain the current account */
        Account myPortfolio = theState.getPortfolio();

        /* If we have a selected Portfolio */
        if (myPortfolio != null) {
            /* Look for the equivalent bucket */
            myPortfolio = theAccounts.findItemById(myPortfolio.getOrderedId());
        }

        /* If we do not have an active portfolio and the list is non-empty */
        if ((myPortfolio == null)
            && (!theAccounts.isEmpty())) {
            /* Access the first portfolio */
            myPortfolio = getFirstPortfolio();
        }

        /* Set the portfolio */
        theState.setPortfolio(myPortfolio);
        theState.applyState();

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /**
     * Obtain first portfolio.
     * @return the first portfolio
     */
    private Account getFirstPortfolio() {
        /* Loop through the available account values */
        Iterator<Account> myIterator = theAccounts.iterator();
        while (myIterator.hasNext()) {
            Account myPortfolio = myIterator.next();

            /* Ignore if it is not a portfolio */
            if (!myPortfolio.isCategoryClass(AccountCategoryClass.PORTFOLIO)) {
                continue;
            }

            /* Return the bucket */
            return myPortfolio;
        }

        /* No such account */
        return null;
    }

    /**
     * Set the range for the date box.
     * @param pRange the Range to set
     */
    public final void setRange(final JDateDayRange pRange) {
        JDateDay myStart = (pRange == null)
                ? null
                : pRange.getStart();
        JDateDay myEnd = (pRange == null)
                ? null
                : pRange.getEnd();

        /* Set up range */
        theDateButton.setEarliestDateDay(myStart);
        theDateButton.setLatestDateDay(myEnd);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        theNext.setEnabled(bEnabled
                           && (theState.getNextDate() != null));
        thePrev.setEnabled(bEnabled
                           && (theState.getPrevDate() != null));
        theDateButton.setEnabled(bEnabled);
        thePortButton.setEnabled(bEnabled);
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new SpotState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new SpotState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    /**
     * Set Adjacent dates.
     * @param pPrev the previous Date
     * @param pNext the next Date
     */
    public void setAdjacent(final JDateDay pPrev,
                            final JDateDay pNext) {
        /* Record the dates */
        theState.setAdjacent(pPrev, pNext);
    }

    /**
     * Listener class.
     */
    private final class SpotListener
            implements ActionListener, PropertyChangeListener, ItemListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the Next button */
            if (theNext.equals(o)) {
                /* Set next and notify changes */
                theState.setNext();
                fireStateChanged();

                /* If this event relates to the previous button */
            } else if (thePrev.equals(o)) {
                /* Set previous and notify changes */
                theState.setPrev();
                fireStateChanged();

                /* If this event relates to the portfolio button */
            } else if (thePortButton.equals(o)) {
                showPortfolioMenu();
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            /* if this date relates to the Date button */
            if ((theDateButton.equals(evt.getSource()))
                && (theState.setDate(theDateButton))) {
                fireStateChanged();
            }
        }

        @Override
        public void itemStateChanged(final ItemEvent evt) {
            Object o = evt.getSource();

            /* Ignore selection if refreshing data */
            if (refreshingData) {
                return;
            }

            /* If this event relates to the showClosed box */
            if (theShowClosed.equals(o)) {
                /* Note the new criteria and re-build lists */
                doShowClosed = theShowClosed.isSelected();
                fireStateChanged();
            }
        }

        /**
         * Show Portfolio menu.
         */
        private void showPortfolioMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

            /* Access current portfolio and security */
            Account myPortfolio = theState.getPortfolio();

            /* Record active item */
            JMenuItem myActive = null;

            /* Loop through the available portfolio values */
            Iterator<Account> myIterator = theAccounts.iterator();
            while (myIterator.hasNext()) {
                Account myAccount = myIterator.next();

                /* Ignore if it is not a portfolio */
                if (!myAccount.isCategoryClass(AccountCategoryClass.PORTFOLIO)) {
                    continue;
                }

                /* Create a new JMenuItem and add it to the popUp */
                PortfolioAction myAction = new PortfolioAction(myAccount);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.addMenuItem(myItem);

                /* If this is the active portfolio */
                if (myPortfolio.equals(myAccount)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            myPopUp.showItem(myActive);

            /* Show the Account menu in the correct place */
            Rectangle myLoc = thePortButton.getBounds();
            myPopUp.show(thePortButton, 0, myLoc.height);
        }
    }

    /**
     * Portfolio action class.
     */
    private final class PortfolioAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6312490702258532708L;

        /**
         * Portfolio.
         */
        private final Account thePortfolio;

        /**
         * Constructor.
         * @param pPortfolio the portfolio
         */
        private PortfolioAction(final Account pPortfolio) {
            super(pPortfolio.getName());
            thePortfolio = pPortfolio;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new portfolio */
            if (theState.setPortfolio(thePortfolio)) {
                theState.applyState();
                fireStateChanged();
            }
        }
    }

    /**
     * SavePoint values.
     */
    private final class SpotState {
        /**
         * Portfolio.
         */
        private Account thePortfolio = null;

        /**
         * Selected date.
         */
        private JDateDay theDate = null;

        /**
         * Next date.
         */
        private JDateDay theNextDate = null;

        /**
         * Previous date.
         */
        private JDateDay thePrevDate = null;

        /**
         * Get the portfolio.
         * @return the portfolio
         */
        private Account getPortfolio() {
            return thePortfolio;
        }

        /**
         * Get the selected date.
         * @return the date
         */
        private JDateDay getDate() {
            return theDate;
        }

        /**
         * Get the next date.
         * @return the date
         */
        private JDateDay getNextDate() {
            return theNextDate;
        }

        /**
         * Get the previous date.
         * @return the date
         */
        private JDateDay getPrevDate() {
            return thePrevDate;
        }

        /**
         * Constructor.
         */
        private SpotState() {
            theDate = new JDateDay();
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private SpotState(final SpotState pState) {
            thePortfolio = pState.getPortfolio();
            theDate = new JDateDay(pState.getDate());
            if (pState.getNextDate() != null) {
                theNextDate = new JDateDay(pState.getNextDate());
            }
            if (pState.getPrevDate() != null) {
                thePrevDate = new JDateDay(pState.getPrevDate());
            }
        }

        /**
         * Set new Portfolio.
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final Account pPortfolio) {
            /* Adjust the selected portfolio */
            if (!Difference.isEqual(pPortfolio, thePortfolio)) {
                thePortfolio = pPortfolio;
                return true;
            }
            return false;
        }

        /**
         * Set new Date.
         * @param pButton the Button with the new date
         * @return true/false did a change occur
         */
        private boolean setDate(final JDateDayButton pButton) {
            /* Adjust the date and build the new range */
            JDateDay myDate = new JDateDay(pButton.getSelectedDate());
            if (!Difference.isEqual(myDate, theDate)) {
                theDate = myDate;
                return true;
            }
            return false;
        }

        /**
         * Set Next Date.
         */
        private void setNext() {
            /* Copy date */
            theDate = new JDateDay(theNextDate);
            applyState();
        }

        /**
         * Set Previous Date.
         */
        private void setPrev() {
            /* Copy date */
            theDate = new JDateDay(thePrevDate);
            applyState();
        }

        /**
         * Set Adjacent dates.
         * @param pPrev the previous Date
         * @param pNext the next Date
         */
        private void setAdjacent(final JDateDay pPrev,
                                 final JDateDay pNext) {
            /* Record the dates */
            thePrevDate = pPrev;
            theNextDate = pNext;

            /* Adjust values */
            setEnabled(true);
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theDateButton.setSelectedDateDay(theDate);
            thePortButton.setText((thePortfolio == null)
                    ? null
                    : thePortfolio.getName());
        }
    }
}