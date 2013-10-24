/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayButton;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jeventmanager.JEventPanel;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.reports.ReportType;
import net.sourceforge.joceanus.jmoneywise.views.DataAnalysis;
import net.sourceforge.joceanus.jmoneywise.views.DataAnalysis.AnalysisYear;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jsortedlist.OrderedListIterator;

/**
 * Report selection panel.
 * @author Tony Washer
 */
public class ReportSelect
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 4943254899793653170L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Box width.
     */
    private static final int BOX_WIDTH = 200;

    /**
     * Box height.
     */
    private static final int BOX_HEIGHT = 25;

    /**
     * Print operation string.
     */
    public static final String ACTION_PRINT = "PrintRequest";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(ReportSelect.class.getName());

    /**
     * Text for Report Label.
     */
    private static final String NLS_REPORT = NLS_BUNDLE.getString("SelectReport");

    /**
     * Text for Year Label.
     */
    private static final String NLS_YEAR = NLS_BUNDLE.getString("SelectYear");

    /**
     * Text for Date Label.
     */
    private static final String NLS_DATE = NLS_BUNDLE.getString("SelectDate");

    /**
     * Text for Print Button.
     */
    private static final String NLS_PRINT = NLS_BUNDLE.getString("PrintButton");

    /**
     * Text for Selection Title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("SelectTitle");

    /**
     * Data view.
     */
    private final transient View theView;

    /**
     * Date button.
     */
    private final JDateDayButton theDateButton;

    /**
     * Reports comboBox.
     */
    private final JComboBox<ReportType> theReportBox;

    /**
     * Years comboBox.
     */
    private final JComboBox<TaxYear> theYearsBox;

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
    private transient ReportState theState = null;

    /**
     * Saved state.
     */
    private transient ReportState theSavePoint = null;

    /**
     * Are we refreshing data?.
     */
    private boolean refreshingData = false;

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
    public JDateDay getReportDate() {
        return theState.getDate();
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public ReportSelect(final View pView) {
        ReportListener myListener = new ReportListener();

        /* Store table and view details */
        theView = pView;

        /* Create the boxes */
        theReportBox = new JComboBox<ReportType>();
        theYearsBox = new JComboBox<TaxYear>();
        theReportBox.setMaximumSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));
        theYearsBox.setMaximumSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));

        /* Create the DateButton */
        theDateButton = new JDateDayButton();

        /* Create initial state */
        theState = new ReportState();

        /* Initialise the data from the view */
        refreshData(null);

        /* Add the ReportTypes to the report box */
        theReportBox.addItem(ReportType.NetWorth);
        theReportBox.addItem(ReportType.BalanceSheet);
        theReportBox.addItem(ReportType.CashFlow);
        theReportBox.addItem(ReportType.IncomeExpense);
        theReportBox.addItem(ReportType.TaxationBasis);
        theReportBox.addItem(ReportType.TaxCalculation);
        theReportBox.addItem(ReportType.Portfolio);
        theReportBox.setSelectedItem(ReportType.NetWorth);

        /* Create the labels */
        JLabel myRepLabel = new JLabel(NLS_REPORT);
        theYearLabel = new JLabel(NLS_YEAR);
        theDateLabel = new JLabel(NLS_DATE);

        /* Create the print button */
        thePrintButton = new JButton(NLS_PRINT);
        thePrintButton.addActionListener(myListener);

        /* Create the selection panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(myRepLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theReportBox);
        add(Box.createHorizontalGlue());
        add(theYearLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theYearsBox);
        add(theDateLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theDateButton);
        add(Box.createHorizontalGlue());
        add(thePrintButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Apply the current state */
        theState.applyState();

        /* Add the listener for item changes */
        theReportBox.addItemListener(myListener);
        theYearsBox.addItemListener(myListener);
        theDateButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, myListener);
    }

    /**
     * Refresh data.
     * @param pAnalysis the analysis.
     */
    public final void refreshData(final DataAnalysis pAnalysis) {
        /* Access the data */
        FinanceData myData = theView.getData();
        JDateDayRange myRange = theView.getRange();
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

        boolean isDate = myType.isPointInTime();
        boolean isNull = (myType == null);
        boolean isYear = (!isNull && !isDate);

        theDateButton.setVisible(isDate);
        theDateLabel.setVisible(isDate);
        theYearsBox.setVisible(isYear);
        theYearLabel.setVisible(isYear);

        theYearsBox.setEnabled(bEnable);
        theDateButton.setEnabled(bEnable);
        theReportBox.setEnabled(bEnable);
        thePrintButton.setEnabled(bEnable);
    }

    /**
     * Report Listener class.
     */
    private final class ReportListener
            implements ActionListener, PropertyChangeListener, ItemListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the Print button */
            if (thePrintButton.equals(o)) {
                /* Request a print operation */
                fireActionPerformed(ACTION_PRINT);
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            /* if this date relates to the Date button */
            if ((theDateButton.equals(evt.getSource()))
                && (theState.setDate(theDateButton))) {
                /* Notify that the state has changed */
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

            /* If this event relates to the years box */
            if ((theYearsBox.equals(o))
                && (evt.getStateChange() == ItemEvent.SELECTED)) {
                TaxYear myYear = (TaxYear) evt.getItem();
                if (theState.setYear(myYear)) {
                    fireStateChanged();
                }

                /* If this event relates to the report box */
            } else if ((theReportBox.equals(o))
                       && (evt.getStateChange() == ItemEvent.SELECTED)) {
                /* Determine the new report */
                ReportType myType = (ReportType) evt.getItem();
                if (theState.setType(myType)) {
                    fireStateChanged();
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
        private JDateDay theDate = null;

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
        private JDateDay getDate() {
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
            theDate = new JDateDay();
            theYear = null;
            theType = ReportType.NetWorth;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private ReportState(final ReportState pState) {
            theDate = new JDateDay(pState.getDate());
            theYear = pState.getYear();
            theType = pState.getType();
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
}
