/*******************************************************************************
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

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDateButton.JDateButton;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayButton;
import net.sourceforge.JDateDay.DateDayRange;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TaxYear;
import uk.co.tolcroft.finance.views.EventAnalysis;
import uk.co.tolcroft.finance.views.EventAnalysis.AnalysisYear;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.DataList.DataListIterator;
import uk.co.tolcroft.models.ui.StdInterfaces.StdPanel;

public class ReportSelect extends JPanel {
    private static final long serialVersionUID = 4943254899793653170L;

    /* Members */
    private ReportSelect theSelf = this;
    private StdPanel theParent = null;
    private View theView = null;
    private DateDayButton theDateButton = null;
    private JComboBox theReportBox = null;
    private JComboBox theYearsBox = null;
    private JLabel theRepLabel = null;
    private JLabel theYearLabel = null;
    private JLabel theDateLabel = null;
    private JButton thePrintButton = null;
    private TaxYear.TaxYearList theYears = null;
    private ReportState theState = null;
    private ReportState theSavePoint = null;
    private boolean yearsPopulated = false;
    private boolean refreshingData = false;

    /* Access methods */
    public ReportType getReportType() {
        return theState.getType();
    }

    public TaxYear getTaxYear() {
        return theState.getYear();
    }

    public DateDay getReportDate() {
        return theState.getDate();
    }

    /* Report descriptions */
    private static final String Assets = "Asset";
    private static final String IncomeExp = "Income/Expense";
    private static final String Transaction = "Transaction";
    private static final String Taxation = "Taxation";
    private static final String Instant = "Instant";
    private static final String Market = "Market";
    private static final String Breakdown = "Breakdown";

    /* Constructor */
    public ReportSelect(View pView,
                        StdPanel pReport) {
        ReportListener myListener = new ReportListener();

        /* Store table and view details */
        theView = pView;
        theParent = pReport;

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
        theReportBox.addItem(Instant);
        theReportBox.addItem(Assets);
        theReportBox.addItem(IncomeExp);
        theReportBox.addItem(Transaction);
        theReportBox.addItem(Taxation);
        theReportBox.addItem(Breakdown);
        theReportBox.addItem(Market);
        theReportBox.setSelectedItem(Instant);

        /* Create the labels */
        theRepLabel = new JLabel("Report:");
        theYearLabel = new JLabel("Year:");
        theDateLabel = new JLabel("Date:");

        /* Create the print button */
        thePrintButton = new JButton("Print");
        thePrintButton.addActionListener(myListener);

        /* Create the selection panel */
        setBorder(javax.swing.BorderFactory.createTitledBorder("Report Selection"));

        /* Create the layout for the panel */
        GroupLayout panelLayout = new GroupLayout(this);
        setLayout(panelLayout);

        /* Set the layout */
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout
                                  .createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(theRepLabel)
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
                                  .addComponent(theRepLabel)
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

    /* refresh data */
    public void refreshData(EventAnalysis pAnalysis) {
        FinanceData myData;
        AnalysisYear myYear;
        DateDayRange myRange;
        TaxYear myTaxYear = theState.getYear();

        DataListIterator<AnalysisYear> myIterator;

        /* Access the data */
        myData = theView.getData();
        myRange = theView.getRange();

        /* Access tax Years */
        theYears = myData.getTaxYears();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* Set the range for the Date Spinner */
        setRange(myRange);

        /* If we have years already populated */
        if (yearsPopulated) {
            /* If we have a selected year */
            if (myTaxYear != null) {
                /* Find it in the new list */
                myTaxYear = theYears.searchFor(myTaxYear.getTaxYear());
            }

            /* Remove the types */
            theYearsBox.removeAllItems();
            yearsPopulated = false;
        }

        /* If we have an analysis */
        if (pAnalysis != null) {
            /* Access the iterator */
            myIterator = pAnalysis.getAnalysisYears().listIterator();

            /* Add the Year values to the years box in reverse order */
            while ((myYear = myIterator.previous()) != null) {
                /* Add the item to the list */
                theYearsBox.addItem(Integer.toString(myYear.getDate().getYear()));
                yearsPopulated = true;
            }

            /* If we have a selected year */
            if (myTaxYear != null) {
                /* Select it in the new list */
                theYearsBox.setSelectedItem(Integer.toString(myTaxYear.getTaxYear().getYear()));
            }

            /* Else we have no year currently selected */
            else if (yearsPopulated) {
                /* Select the first year */
                theYearsBox.setSelectedIndex(0);
                theState.setYear(myIterator.peekLast().getTaxYear());
            }
        }

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /* Set the range for the date box */
    public void setRange(DateDayRange pRange) {
        DateDay myStart = (pRange == null) ? null : pRange.getStart();
        DateDay myEnd = (pRange == null) ? null : pRange.getEnd();

        /* Set up range */
        theDateButton.setEarliestDateDay(myStart);
        theDateButton.setLatestDateDay(myEnd);
    }

    /**
     * Create SavePoint
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new ReportState(theState);
    }

    /**
     * Restore SavePoint
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new ReportState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    /* Lock/Unlock the selection */
    public void setLockDown() {
        ReportType myType = theState.getType();

        boolean isDate = ((myType == ReportType.INSTANT) || (myType == ReportType.MARKET));
        boolean isNull = (myType == null);
        boolean isYear = (!isNull && !isDate);

        theDateButton.setVisible(isDate);
        theDateLabel.setVisible(isDate);
        theYearsBox.setVisible(isYear);
        theYearLabel.setVisible(isYear);
    }

    /**
     * Report Listener class
     */
    private class ReportListener implements ActionListener, PropertyChangeListener, ItemListener {
        /* actionPerformed listener event */
        @Override
        public void actionPerformed(ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the Print button */
            if (o == thePrintButton) {
                /* Pass command to the table */
                theParent.printIt();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            /* if this date relates to the Date button */
            if (evt.getSource() == theDateButton) {
                /* Access the value */
                if (theState.setDate(theDateButton))
                    theParent.notifySelection(theSelf);
            }
        }

        /* ItemStateChanged listener event */
        @Override
        public void itemStateChanged(ItemEvent evt) {
            String myName;
            ReportType myType = null;
            boolean bChange = false;
            Object o = evt.getSource();

            /* Ignore selection if refreshing data */
            if (refreshingData)
                return;

            /* If this event relates to the years box */
            if (o == theYearsBox) {
                myName = (String) evt.getItem();
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    /* Select the new year */
                    theState.setYear(theYears.searchFor(myName));
                    bChange = true;
                }
            }

            /* If this event relates to the report box */
            else if (o == theReportBox) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    /* Determine the new report */
                    myName = (String) evt.getItem();
                    bChange = true;
                    if (myName == Assets)
                        myType = ReportType.ASSET;
                    else if (myName == IncomeExp)
                        myType = ReportType.INCOME;
                    else if (myName == Transaction)
                        myType = ReportType.TRANSACTION;
                    else if (myName == Taxation)
                        myType = ReportType.TAX;
                    else if (myName == Instant)
                        myType = ReportType.INSTANT;
                    else if (myName == Market)
                        myType = ReportType.MARKET;
                    else if (myName == Breakdown)
                        myType = ReportType.BREAKDOWN;
                    else
                        bChange = false;

                    /* Update state if we have a change */
                    if (bChange)
                        theState.setType(myType);
                }
            }

            /* If we have a change, notify the main program */
            if (bChange) {
                theParent.notifySelection(theSelf);
            }
        }
    }

    /* SavePoint values */
    private class ReportState {
        /* Members */
        private DateDay theDate = null;
        private TaxYear theYear = null;
        private ReportType theType = null;

        /* Access methods */
        private DateDay getDate() {
            return theDate;
        }

        private TaxYear getYear() {
            return theYear;
        }

        private ReportType getType() {
            return theType;
        }

        /**
         * Constructor
         */
        private ReportState() {
            theDate = new DateDay();
            theYear = null;
            theType = ReportType.INSTANT;
        }

        /**
         * Constructor
         * @param pState state to copy from
         */
        private ReportState(ReportState pState) {
            theDate = new DateDay(pState.getDate());
            theYear = pState.getYear();
            theType = pState.getType();
        }

        /**
         * Set new Date
         * @param pButton the Button with the new date
         * @return true/false did a change occur
         */
        private boolean setDate(JDateButton pButton) {
            /* Adjust the date and build the new range */
            DateDay myDate = new DateDay(pButton.getSelectedDate());
            if (!Difference.isEqual(myDate, theDate)) {
                theDate = myDate;
                return true;
            }
            return false;
        }

        /**
         * Set new Tax Year
         * @param pYear the new Tax Year
         */
        private void setYear(TaxYear pYear) {
            /* Set the new year and apply State */
            theYear = pYear;
            applyState();
        }

        /**
         * Set new Report Type
         * @param pType the new type
         */
        private void setType(ReportType pType) {
            /* Set the new type and apply State */
            theType = pType;
            applyState();
        }

        /**
         * Apply the State
         */
        private void applyState() {
            /* Adjust the lock-down */
            setLockDown();
            theDateButton.setSelectedDateDay(theDate);
            if (theYear != null)
                theYearsBox.setSelectedItem(Integer.toString(theYear.getTaxYear().getYear()));
            else
                theYearsBox.setSelectedItem(null);
        }
    }

    /* Report Types */
    public enum ReportType {
        ASSET, INCOME, TAX, TRANSACTION, INSTANT, BREAKDOWN, MARKET;
    }
}
