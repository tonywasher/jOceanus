/*******************************************************************************
 * JFinanceApp: Finance Application
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
package net.sourceforge.JFinanceApp.ui.controls;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ListIterator;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JPanelWithEvents;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.data.TaxYear;
import net.sourceforge.JFinanceApp.data.TaxYear.TaxYearList;
import net.sourceforge.JFinanceApp.views.View;

/**
 * TaxYear selection panel.
 * @author Tony Washer
 */
public class TaxYearSelect extends JPanelWithEvents {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1313452754119158982L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(TaxYearSelect.class.getName());

    /**
     * Text for Selection Title.
     */
    private static final String NLS_YEAR = NLS_BUNDLE.getString("SelectYear");

    /**
     * Text for Selection Title.
     */
    private static final String NLS_DELETED = NLS_BUNDLE.getString("ShowDeleted");

    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = NLS_BUNDLE.getString("SelectTitle");

    /**
     * Data view.
     */
    private final transient View theView;

    /**
     * Years box.
     */
    private final JComboBox theYearsBox;

    /**
     * Show Deleted check box.
     */
    private final JCheckBox theShowDeleted;

    /**
     * Current state.
     */
    private transient YearState theState = null;

    /**
     * Saved state.
     */
    private transient YearState theSavePoint = null;

    /**
     * Are we refreshing data?
     */
    private boolean refreshingData = false;

    /**
     * Get the selected TaxYear.
     * @return the tax year
     */
    public final TaxYear getTaxYear() {
        return theState.getTaxYear();
    }

    /**
     * Are we showing deleted Years?
     * @return true/false
     */
    public final boolean doShowDeleted() {
        return theState.doShowDeleted();
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public TaxYearSelect(final View pView) {
        JLabel mySelect;
        TaxYearListener myListener = new TaxYearListener();

        /* Store table and view details */
        theView = pView;

        /* Create initial state */
        theState = new YearState();

        /* Create the labels */
        mySelect = new JLabel(NLS_YEAR);

        /* Create the combo boxes */
        theYearsBox = new JComboBox();

        /* Create the combo boxes */
        theShowDeleted = new JCheckBox(NLS_DELETED);
        theShowDeleted.setSelected(theState.doShowDeleted());

        /* Add item listeners */
        theYearsBox.addItemListener(myListener);
        theShowDeleted.addItemListener(myListener);

        /* Create the selection panel */
        setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(mySelect);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theYearsBox);
        add(Box.createHorizontalGlue());
        add(theShowDeleted);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Initialise the data from the view */
        refreshData();
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new YearState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new YearState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    /**
     * refresh Data.
     */
    public final void refreshData() {
        /* Access the data */
        FinanceData myData = theView.getData();

        /* Access years and regimes */
        TaxYearList myTaxYears = myData.getTaxYears();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* If we have years already populated */
        if (theYearsBox.getItemCount() > 0) {
            /* If we have a selected year */
            if (getTaxYear() != null) {
                /* Find it in the new list */
                theState.setTaxYear(myTaxYears.findTaxYearForDate(getTaxYear().getTaxYear()));
            }

            /* Remove the years */
            theYearsBox.removeAllItems();
        }

        /* Create a Tax Year iterator */
        ListIterator<TaxYear> myYearIterator = myTaxYears.listIterator();
        TaxYear myFirst = null;

        /* Add the Tax Years to the years box in reverse order */
        while (myYearIterator.hasPrevious()) {
            TaxYear myYear = myYearIterator.previous();

            /* If the year is not deleted */
            if ((!doShowDeleted()) && (myYear.isDeleted())) {
                continue;
            }

            /* Note the first in the list */
            if (myFirst == null) {
                myFirst = myYear;
            }

            /* Add the item to the list */
            theYearsBox.addItem(myYear);
        }

        /* If we have a selected year */
        if (getTaxYear() != null) {
            /* Select it in the new list */
            theYearsBox.setSelectedItem(getTaxYear());

            /* Else we have no year currently selected */
        } else if (theYearsBox.getItemCount() > 0) {
            /* Select the first account */
            theYearsBox.setSelectedIndex(0);
            theState.setTaxYear(myFirst);
        }

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /**
     * TaxYear Listener class.
     */
    private final class TaxYearListener implements ItemListener {
        /* ItemStateChanged listener event */
        @Override
        public void itemStateChanged(final ItemEvent evt) {
            Object o = evt.getSource();

            /* Ignore selection if refreshing data */
            if (refreshingData) {
                return;
            }

            /* If this event relates to the years box */
            if ((theYearsBox.equals(o)) && (evt.getStateChange() == ItemEvent.SELECTED)) {
                /* Select the new year and notify the change */
                TaxYear myYear = (TaxYear) evt.getItem();
                if (theState.setTaxYear(myYear)) {
                    fireStateChanged();
                }

                /* If this event relates to the showDeleted box */
            } else if ((theShowDeleted.equals(o)) && (theState.setDoShowDeleted(theShowDeleted.isSelected()))) {
                /* Rebuild lists */
                refreshData();
            }
        }
    }

    /**
     * SavePoint class.
     */
    private final class YearState {
        /**
         * Selected tax year.
         */
        private TaxYear theTaxYear = null;

        /**
         * Are we showing deleted items?
         */
        private boolean doShowDeleted = false;

        /**
         * Get selected tax year.
         * @return the tax year
         */
        private TaxYear getTaxYear() {
            return theTaxYear;
        }

        /**
         * Are we showing deleted items?
         * @return true/false
         */
        private boolean doShowDeleted() {
            return doShowDeleted;
        }

        /**
         * Constructor.
         */
        private YearState() {
            theTaxYear = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private YearState(final YearState pState) {
            theTaxYear = pState.getTaxYear();
            doShowDeleted = pState.doShowDeleted();
        }

        /**
         * Set new Tax Year.
         * @param pYear the new Tax Year
         * @return true/false
         */
        private boolean setTaxYear(final TaxYear pYear) {
            /* Adjust the selected account */
            if (!Difference.isEqual(pYear, theTaxYear)) {
                theTaxYear = pYear;
                return true;
            }
            return false;
        }

        /**
         * Set doShowDeleted indication.
         * @param pShowDeleted true/false
         * @return true/false
         */
        private boolean setDoShowDeleted(final boolean pShowDeleted) {
            /* Adjust the flag */
            if (doShowDeleted != pShowDeleted) {
                doShowDeleted = pShowDeleted;
                return true;
            }
            return false;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            theYearsBox.setSelectedItem(theTaxYear);
            theShowDeleted.setSelected(doShowDeleted);
        }
    }
}
