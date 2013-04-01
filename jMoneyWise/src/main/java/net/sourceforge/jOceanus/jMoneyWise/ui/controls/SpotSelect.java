/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.ui.controls;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sourceforge.JDateButton.JDateButton;
import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayButton;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jEventManager.JEventPanel;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.views.View;

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
     * Field Height.
     */
    private static final int FIELD_HEIGHT = 20;

    /**
     * Field Width.
     */
    private static final int FIELD_WIDTH = 200;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(SpotSelect.class.getName());

    /**
     * Text for Date Label.
     */
    private static final String NLS_DATE = NLS_BUNDLE.getString("SelectDate");

    /**
     * Text for AccountType Label.
     */
    private static final String NLS_TYPE = NLS_BUNDLE.getString("SelectType");

    /**
     * Text for Show Closed.
     */
    private static final String NLS_CLOSED = NLS_BUNDLE.getString("ShowClosed");

    /**
     * Text for Next Button.
     */
    private static final String NLS_NEXT = NLS_BUNDLE.getString("NextButton");

    /**
     * Text for Prev Button.
     */
    private static final String NLS_PREV = NLS_BUNDLE.getString("PrevButton");

    /**
     * Text for Title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("SpotTitle");

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
     * The accountCategories comboBox.
     */
    private final JComboBox<AccountCategory> theCategoriesBox;

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
     * Get the selected account category.
     * @return the account category
     */
    public final AccountCategory getAccountCategory() {
        return theState.getAccountCategory();
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
        JLabel myAct = new JLabel(NLS_TYPE);

        /* Create the check box */
        theShowClosed = new JCheckBox(NLS_CLOSED);
        theShowClosed.setSelected(doShowClosed);

        /* Create the DateButton */
        theDateButton = new JDateDayButton();

        /* Create the Buttons */
        theNext = new JButton(NLS_NEXT);
        thePrev = new JButton(NLS_PREV);

        /* Create the Category box */
        theCategoriesBox = new JComboBox<AccountCategory>();
        theCategoriesBox.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));

        /* Create initial state */
        theState = new SpotState();

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(myDate);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theDateButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theNext);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(thePrev);
        add(Box.createHorizontalGlue());
        add(myAct);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theCategoriesBox);
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
        theCategoriesBox.addItemListener(myListener);
    }

    /**
     * Refresh data.
     */
    public final void refreshData() {
        AccountCategory myCategory = null;
        AccountCategory myFirst = null;

        /* Access the data */
        JDateDayRange myRange = theView.getRange();

        /* Set the range for the Date Button */
        setRange(myRange);

        /* Access the data */
        FinanceData myData = theView.getData();

        /* Access categories and accounts */
        AccountCategoryList myCategories = myData.getAccountCategories();
        AccountList myAccounts = myData.getAccounts();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* If we have categories already populated */
        if (theCategoriesBox.getItemCount() > 0) {
            /* If we have a selected category */
            if (getAccountCategory() != null) {
                /* Find it in the new list */
                theState.setCategory(myCategories.findItemByName(getAccountCategory().getName()));
            }

            /* Remove the categories */
            theCategoriesBox.removeAllItems();
        }

        /* Access the iterator */
        Iterator<Account> myIterator = myAccounts.iterator();

        /* Loop through the non-owner accounts */
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();

            /* Skip non-priced, deleted and alias items */
            if ((!myAccount.hasUnits())
                || (myAccount.isDeleted())
                || (myAccount.isAlias())) {
                continue;
            }

            /* Skip closed items if required */
            if ((!doShowClosed)
                && (myAccount.isClosed())) {
                continue;
            }

            /* If the category of this account is new */
            if (!Difference.isEqual(myCategory, myAccount.getAccountCategory())) {
                /* Note the category */
                myCategory = myAccount.getAccountCategory();
                if (myFirst == null) {
                    myFirst = myCategory;
                }

                /* Add the item to the list */
                theCategoriesBox.addItem(myCategory);
            }
        }

        /* If we have a selected type */
        if (getAccountCategory() != null) {
            /* Select it in the new list */
            theCategoriesBox.setSelectedItem(getAccountCategory());

            /* Else we have no type currently selected */
        } else if (theCategoriesBox.getItemCount() > 0) {
            /* Select the first account category */
            theCategoriesBox.setSelectedIndex(0);
            theState.setCategory(myFirst);
        }

        /* Note that we have finished refreshing data */
        refreshingData = false;
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
        theCategoriesBox.setEnabled(bEnabled);
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

                /* If this event relates to the Account Category box */
            } else if ((theCategoriesBox.equals(o))
                       && (evt.getStateChange() == ItemEvent.SELECTED)) {
                AccountCategory myCategory = (AccountCategory) evt.getItem();

                /* Select the new category */
                if (theState.setCategory(myCategory)) {
                    fireStateChanged();
                }
            }
        }
    }

    /**
     * SavePoint values.
     */
    private final class SpotState {
        /**
         * AccountCategory.
         */
        private AccountCategory theCategory = null;

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
         * Get the account category.
         * @return the account category
         */
        private AccountCategory getAccountCategory() {
            return theCategory;
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
            theCategory = pState.getAccountCategory();
            theDate = new JDateDay(pState.getDate());
            if (pState.getNextDate() != null) {
                theNextDate = new JDateDay(pState.getNextDate());
            }
            if (pState.getPrevDate() != null) {
                thePrevDate = new JDateDay(pState.getPrevDate());
            }
        }

        /**
         * Set new Account Category.
         * @param pCategory the AccountCategory
         * @return true/false did a change occur
         */
        private boolean setCategory(final AccountCategory pCategory) {
            /* Adjust the selected account */
            if (!Difference.isEqual(pCategory, theCategory)) {
                theCategory = pCategory;
                return true;
            }
            return false;
        }

        /**
         * Set new Date.
         * @param pButton the Button with the new date
         * @return true/false did a change occur
         */
        private boolean setDate(final JDateButton pButton) {
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
            theCategoriesBox.setSelectedItem(theCategory);
        }
    }
}
