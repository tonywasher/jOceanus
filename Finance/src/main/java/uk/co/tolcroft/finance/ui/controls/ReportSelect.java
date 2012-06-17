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
package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.EventManager;
import net.sourceforge.JDateButton.JDateButton;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayButton;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JSortedList.OrderedListIterator;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TaxYear;
import uk.co.tolcroft.finance.data.TaxYear.TaxYearList;
import uk.co.tolcroft.finance.views.EventAnalysis;
import uk.co.tolcroft.finance.views.EventAnalysis.AnalysisYear;
import uk.co.tolcroft.finance.views.View;

/**
 * Report selection panel.
 * @author Tony Washer
 */
public class ReportSelect extends JPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 4943254899793653170L;

    /**
     * Print operation string.
     */
    public static final String ACTION_PRINT = "PrintRequest";

    /**
     * Data view.
     */
    private final View theView;

    /**
     * Date button.
     */
    private final DateDayButton theDateButton;

    /**
     * Reports comboBox.
     */
    private final JComboBox theReportBox;

    /**
     * Years comboBox.
     */
    private final JComboBox theYearsBox;

    /**
     * Years label.
     */
    private JLabel theYearLabel = null;

    /**
     * Date label.
     */
    private JLabel theDateLabel = null;

    /**
     * Print button.
     */
    private final JButton thePrintButton;

    /**
     * Current state.
     */
    private ReportState theState = null;

    /**
     * Saved state.
     */
    private ReportState theSavePoint = null;

    /**
     * Are we refreshing data?.
     */
    private boolean refreshingData = false;

    /**
     * Event Manager.
     */
    private final EventManager theManager;

    /**
     * Obtain the report type.
     * @return the report type
     */
    public ReportType getReportType() {
        return theState.getType();
    }

    /**
     * Obtain the selected taxYear.
     * @return the tax year
     */
    public TaxYear getTaxYear() {
        return theState.getYear();
    }

    /**
     * Obtain the report date.
     * @return the report date.
     */
    public DateDay getReportDate() {
        return theState.getDate();
    }

    /**
     * Obtain the event manager.
     * @return the event manager.
     */
    public EventManager getManager() {
        return theManager;
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public ReportSelect(final View pView) {
        ReportListener myListener = new ReportListener();

        /* Store table and view details */
        theView = pView;

        /* Create the Event Manager */
        theManager = new EventManager(this);

        /* Create the boxes */
        theReportBox = new JComboBox();
        theYearsBox = new JComboBox();

        /* Create the DateButton */
        theDateButton = new DateDayButton();

        /* Create initial state */
        theState = new ReportState();

        /* Initialise the data from the view */
        refreshData(null);

        /* Add the ReportTypes to the report box */
        theReportBox.addItem(ReportType.INSTANT);
        theReportBox.addItem(ReportType.ASSET);
        theReportBox.addItem(ReportType.INCOME);
        theReportBox.addItem(ReportType.TRANSACTION);
        theReportBox.addItem(ReportType.TAX);
        theReportBox.addItem(ReportType.BREAKDOWN);
        theReportBox.addItem(ReportType.MARKET);
        theReportBox.setSelectedItem(ReportType.INSTANT);

        /* Create the labels */
        JLabel myRepLabel = new JLabel("Report:");
        theYearLabel = new JLabel("Year:");
        theDateLabel = new JLabel("Date:");

        /* Create the print button */
        thePrintButton = new JButton("Print");
        thePrintButton.addActionListener(myListener);

        /* Create the selection panel */
        setBorder(BorderFactory.createTitledBorder("Report Selection"));

        /* Create the layout for the panel */
        GroupLayout panelLayout = new GroupLayout(this);
        setLayout(panelLayout);

        /* Set the layout */
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout
                                  .createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(myRepLabel)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theReportBox)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(theYearLabel)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theYearsBox)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(theDateLabel)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theDateButton, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(thePrintButton).addContainerGap()));
        panelLayout.setVerticalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout
                                  .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                  .addComponent(myRepLabel)
                                  .addComponent(theReportBox)
                                  .addComponent(theYearLabel)
                                  .addComponent(theYearsBox, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                  .addComponent(theDateLabel)
                                  .addComponent(theDateButton, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                  .addComponent(thePrintButton)));

        /* Apply the current state */
        theState.applyState();

        /* Add the listener for item changes */
        theReportBox.addItemListener(myListener);
        theYearsBox.addItemListener(myListener);
        theDateButton.addPropertyChangeListener(DateDayButton.PROPERTY_DATE, myListener);
    }

    /**
     * Refresh data.
     * @param pAnalysis the analysis.
     */
    public final void refreshData(final EventAnalysis pAnalysis) {
        /* Access the data */
        FinanceData myData = theView.getData();
        DateDayRange myRange = theView.getRange();
        TaxYear myTaxYear = theState.getYear();

        /* Access tax Years */
        TaxYearList myYears = myData.getTaxYears();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* Set the range for the Date Spinner */
        setRange(myRange);

        /* If we have years already populated */
        if (theYearsBox.getItemCount() > 0) {
            /* If we have a selected year */
            if (myTaxYear != null) {
                /* Find it in the new list */
                myTaxYear = myYears.findTaxYearForDate(myTaxYear.getTaxYear());
            }

            /* Remove the types */
            theYearsBox.removeAllItems();
        }

        /* If we have an analysis */
        if (pAnalysis != null) {
            /* Access the iterator */
            OrderedListIterator<AnalysisYear> myIterator = pAnalysis.getAnalysisYears().listIterator();

            /* Add the Year values to the years box in reverse order */
            while (myIterator.hasPrevious()) {
                AnalysisYear myYear = myIterator.previous();

                /* Add the item to the list */
                theYearsBox.addItem(myYear.getTaxYear());
            }

            /* If we have a selected year */
            if (myTaxYear != null) {
                /* Select it in the new list */
                theYearsBox.setSelectedItem(myTaxYear);

                /* Else we have no year currently selected */
            } else if (theYearsBox.getItemCount() > 0) {
                /* Select the first year */
                theYearsBox.setSelectedIndex(0);
                theState.setYear(myIterator.peekLast().getTaxYear());
            }
        }

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /**
     * Set the range for the date box.
     * @param pRange the date range
     */
    public final void setRange(final DateDayRange pRange) {
        DateDay myStart = (pRange == null) ? null : pRange.getStart();
        DateDay myEnd = (pRange == null) ? null : pRange.getEnd();

        /* Set up range */
        theDateButton.setEarliestDateDay(myStart);
        theDateButton.setLatestDateDay(myEnd);
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new ReportState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new ReportState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnable) {
        ReportType myType = theState.getType();

        boolean isDate = ((myType == ReportType.INSTANT) || (myType == ReportType.MARKET));
        boolean isNull = (myType == null);
        boolean isYear = (!isNull && !isDate);

        theDateButton.setVisible(isDate);
        theDateLabel.setVisible(isDate);
        theYearsBox.setVisible(isYear);
        theYearLabel.setVisible(isYear);

        theYearsBox.setEnabled(bEnable);
        theDateButton.setEnabled(bEnable);
        theReportBox.setEnabled(bEnable);
    }

    /**
     * Report Listener class.
     */
    private final class ReportListener implements ActionListener, PropertyChangeListener, ItemListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the Print button */
            if (thePrintButton.equals(o)) {
                /* Request a print operation */
                theManager.fireActionPerformed(ACTION_PRINT);
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            /* if this date relates to the Date button */
            if ((theDateButton.equals(evt.getSource())) && (theState.setDate(theDateButton))) {
                /* Notify that the state has changed */
                theManager.fireStateChanged();
            }
        }

        @Override
        public void itemStateChanged(final ItemEvent evt) {
            Object o = evt.getSource();

            /* Ignore selection if refreshing data */
            if (refreshingData) {
                return;
            }

            /* If this event relates to the years box */
            if ((theYearsBox.equals(o)) && (evt.getStateChange() == ItemEvent.SELECTED)) {
                TaxYear myYear = (TaxYear) evt.getItem();
                if (theState.setYear(myYear)) {
                    theManager.fireStateChanged();
                }

                /* If this event relates to the report box */
            } else if ((theReportBox.equals(o)) && (evt.getStateChange() == ItemEvent.SELECTED)) {
                /* Determine the new report */
                ReportType myType = (ReportType) evt.getItem();
                if (theState.setType(myType)) {
                    theManager.fireStateChanged();
                }
            }
        }
    }

    /**
     * SavePoint values.
     */
    private final class ReportState {
        /**
         * The selected date.
         */
        private DateDay theDate = null;

        /**
         * The selected tax year.
         */
        private TaxYear theYear = null;

        /**
         * The selected report type.
         */
        private ReportType theType = null;

        /**
         * Obtain the selected date.
         * @return the date
         */
        private DateDay getDate() {
            return theDate;
        }

        /**
         * Obtain the selected tax year.
         * @return the tax year
         */
        private TaxYear getYear() {
            return theYear;
        }

        /**
         * Obtain the selected report type.
         * @return the report type
         */
        private ReportType getType() {
            return theType;
        }

        /**
         * Constructor.
         */
        private ReportState() {
            theDate = new DateDay();
            theYear = null;
            theType = ReportType.INSTANT;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private ReportState(final ReportState pState) {
            theDate = new DateDay(pState.getDate());
            theYear = pState.getYear();
            theType = pState.getType();
        }

        /**
         * Set new Date.
         * @param pButton the Button with the new date
         * @return true/false did a change occur
         */
        private boolean setDate(final JDateButton pButton) {
            /* Adjust the date and build the new range */
            DateDay myDate = new DateDay(pButton.getSelectedDate());
            if (!Difference.isEqual(myDate, theDate)) {
                theDate = myDate;
                return true;
            }
            return false;
        }

        /**
         * Set new Tax Year.
         * @param pYear the new Tax Year
         * @return true/false did a change occur
         */
        private boolean setYear(final TaxYear pYear) {
            if (!Difference.isEqual(pYear, theYear)) {
                theYear = pYear;
                return true;
            }
            return false;
        }

        /**
         * Set new Report Type.
         * @param pType the new type
         * @return true/false did a change occur
         */
        private boolean setType(final ReportType pType) {
            if (!theType.equals(pType)) {
                /* Set the new type and apply State */
                theType = pType;
                applyState();
                return true;
            }
            return false;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theDateButton.setSelectedDateDay(theDate);
            theYearsBox.setSelectedItem(theYear);
        }
    }

    /**
     * Report Types.
     */
    public enum ReportType {
        /**
         * Asset Report.
         */
        ASSET("Assets"),

        /**
         * Income/Expense Report.
         */
        INCOME("Income/Expense"),

        /**
         * Tax Report.
         */
        TAX("Taxation"),

        /**
         * Transaction Report.
         */
        TRANSACTION("Transaction"),

        /**
         * Instant Asset Report.
         */
        INSTANT("Instant"),

        /**
         * IncomeBreakdown Report.
         */
        BREAKDOWN("IncomeBreakdown"),

        /**
         * Market Report.
         */
        MARKET("Market");

        /**
         * Report Name.
         */
        private final String theName;

        @Override
        public String toString() {
            return theName;
        }

        /**
         * Constructor.
         * @param pName the report name
         */
        private ReportType(final String pName) {
            theName = pName;
        }
    }
}
