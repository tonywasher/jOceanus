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
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdateday.JDateDayRangeSelect;
import net.sourceforge.joceanus.jdateday.JDatePeriod;
import net.sourceforge.joceanus.jeventmanager.JEventPanel;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.reports.ReportType;
import net.sourceforge.joceanus.jmoneywise.views.View;

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
     * Reports comboBox.
     */
    private final JComboBox<ReportType> theReportBox;

    /**
     * Range select.
     */
    private final JDateDayRangeSelect theRangeSelect;

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
     * Obtain the selected date range.
     * @return the date range
     */
    public JDateDayRange getDateRange() {
        return theState.getRange();
    }

    /**
     * Obtain the date range selection control.
     * @return the date range selection
     */
    public JDateDayRangeSelect getDateRangeSelect() {
        return theRangeSelect;
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public ReportSelect(final View pView) {
        ReportListener myListener = new ReportListener();

        /* Store table and view details */
        theView = pView;

        /* Create the report box */
        theReportBox = new JComboBox<ReportType>();
        theReportBox.setMaximumSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));

        /* Create the Range Select and disable its border */
        theRangeSelect = new JDateDayRangeSelect();
        theRangeSelect.setBorder(BorderFactory.createEmptyBorder());

        /* Create initial state */
        theState = new ReportState();
        theState.setRange(theRangeSelect);

        /* Initialise the data from the view */
        refreshData(null);

        /* Add the ReportTypes to the report box */
        theReportBox.addItem(ReportType.NETWORTH);
        theReportBox.addItem(ReportType.BALANCESHEET);
        theReportBox.addItem(ReportType.CASHFLOW);
        theReportBox.addItem(ReportType.INCOMEEXPENSE);
        theReportBox.addItem(ReportType.TAXBASIS);
        theReportBox.addItem(ReportType.TAXCALC);
        theReportBox.addItem(ReportType.PORTFOLIO);
        theReportBox.addItem(ReportType.MARKETGROWTH);

        /* Create the labels */
        JLabel myRepLabel = new JLabel(NLS_REPORT);

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
        add(theRangeSelect);
        add(Box.createHorizontalGlue());
        add(thePrintButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Apply the current state */
        theState.setType(ReportType.NETWORTH);

        /* Add the listener for item changes */
        theReportBox.addItemListener(myListener);
        theRangeSelect.addPropertyChangeListener(JDateDayRangeSelect.PROPERTY_RANGE, myListener);
    }

    /**
     * Refresh data.
     * @param pAnalysis the analysis.
     */
    public final void refreshData(final AnalysisManager pAnalysis) {
        /* Access the range */
        JDateDayRange myRange = theView.getRange();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* Set the range for the DateButton and RangeSelect */
        setRange(myRange);

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /**
     * Set the range for the date box.
     * @param pRange the date range
     */
    public final void setRange(final JDateDayRange pRange) {
        /* Set up range */
        theRangeSelect.setOverallRange(pRange);
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
        theRangeSelect.setEnabled(bEnable);
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
            Object o = evt.getSource();

            /* if this date relates to the Range Select */
            if ((theRangeSelect.equals(o))
                && (theState.setRange(theRangeSelect))) {
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

            /* If this event relates to the report box */
            if ((theReportBox.equals(o))
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
         * The selected range.
         */
        private JDateDayRange theRange = null;

        /**
         * The selected report type.
         */
        private ReportType theType = null;

        /**
         * Obtain the selected range.
         * @return the range
         */
        private JDateDayRange getRange() {
            return theRange;
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
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private ReportState(final ReportState pState) {
            theRange = pState.getRange();
            theType = pState.getType();
        }

        /**
         * Set new Range.
         * @param pSelect the Panel with the new range
         * @return true/false did a change occur
         */
        private boolean setRange(final JDateDayRangeSelect pSelect) {
            /* Adjust the date and build the new range */
            JDateDayRange myRange = new JDateDayRange(pSelect.getRange());
            if (!Difference.isEqual(myRange, theRange)) {
                theRange = myRange;
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
            if (!pType.equals(theType)) {
                /* Are we currently point in time */
                boolean isPointInTime = (theType != null)
                                        && (theType.isPointInTime());

                /* Store the new type */
                theType = pType;

                /* If we need to switch point in time */
                if (theType.isPointInTime() != isPointInTime) {
                    /* Switch it appropriately */
                    theRangeSelect.setPeriod(isPointInTime
                            ? JDatePeriod.FISCALYEAR
                            : JDatePeriod.DATESUPTO);
                    theRangeSelect.lockPeriod(!isPointInTime);

                    /* else if we are switching to tax calculation */
                } else if (theType == ReportType.TAXCALC) {
                    /* Switch explicitly to Fiscal Year */
                    theRangeSelect.setPeriod(JDatePeriod.FISCALYEAR);
                }

                /* Apply the state */
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
        }
    }
}
