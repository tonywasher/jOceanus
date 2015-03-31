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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmoneywise.reports.ReportType;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.dateday.JDatePeriod;
import net.sourceforge.joceanus.jtethys.dateday.swing.JDateDayRangeSelect;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Report selection panel.
 * @author Tony Washer
 */
public class ReportSelect
        extends JPanel
        implements JOceanusEventProvider {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 4943254899793653170L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Text for Report Label.
     */
    private static final String NLS_REPORT = MoneyWiseUIControlResource.REPORT_PROMPT.getValue();

    /**
     * Text for Print Button.
     */
    private static final String NLS_PRINT = MoneyWiseUIControlResource.REPORT_PRINT.getValue();

    /**
     * Text for Selection Title.
     */
    private static final String NLS_TITLE = MoneyWiseUIControlResource.REPORT_TITLE.getValue();

    /**
     * The Event Manager.
     */
    private final transient JOceanusEventManager theEventManager;

    /**
     * Reports scroll button.
     */
    private final JScrollButton<ReportType> theReportButton;

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
     * Constructor.
     */
    public ReportSelect() {
        /* Create the report button */
        theReportButton = new JScrollButton<ReportType>();
        buildReportMenu();

        /* Create the Range Select and disable its border */
        theRangeSelect = new JDateDayRangeSelect();
        theRangeSelect.setBorder(BorderFactory.createEmptyBorder());

        /* Create initial state */
        theState = new ReportState();
        theState.setRange(theRangeSelect);

        /* Create the labels */
        JLabel myRepLabel = new JLabel(NLS_REPORT);

        /* Create the print button */
        thePrintButton = new JButton(NLS_PRINT);

        /* Create Event Manager */
        theEventManager = new JOceanusEventManager();

        /* Create the selection panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(myRepLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theReportButton);
        add(Box.createHorizontalGlue());
        add(theRangeSelect);
        add(Box.createHorizontalGlue());
        add(thePrintButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Apply the current state */
        theState.setType(ReportType.NETWORTH);

        /* Add the listener for item changes */
        new ReportListener();
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

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
     * Build report menu.
     */
    private void buildReportMenu() {
        /* Create builder */
        JScrollMenuBuilder<ReportType> myBuilder = theReportButton.getMenuBuilder();

        /* Loop through the reports */
        for (ReportType myType : ReportType.values()) {
            /* Create a new JMenuItem for the report type */
            myBuilder.addItem(myType);
        }
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
        theReportButton.setEnabled(bEnable);
        thePrintButton.setEnabled(bEnable);
    }

    /**
     * Report Listener class.
     */
    private final class ReportListener
            implements ActionListener, PropertyChangeListener {
        /**
         * Active flag.
         */
        private boolean isActive = false;

        /**
         * Constructor.
         */
        private ReportListener() {
            thePrintButton.addActionListener(this);
            theReportButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
            theRangeSelect.addPropertyChangeListener(JDateDayRangeSelect.PROPERTY_RANGE, this);
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the Print button */
            if (thePrintButton.equals(o)) {
                /* Request a print operation */
                theEventManager.fireActionEvent();
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            Object o = evt.getSource();

            /* if this event relates to the report button */
            if (theReportButton.equals(o)) {
                /* Set active flag */
                isActive = true;

                /* Look for a changed report type */
                if (theState.setType(theReportButton.getValue())) {
                    /* Notify that the state has changed */
                    theEventManager.fireStateChanged();
                }

                /* Clear active flag */
                isActive = false;
            }

            /* if this event relates to the Range Select */
            if (theRangeSelect.equals(o)
                && theState.setRange(theRangeSelect)
                && !isActive) {
                /* Notify that the state has changed */
                theEventManager.fireStateChanged();
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
            theReportButton.setText((theType == null)
                                                     ? null
                                                     : theType.toString());
        }
    }
}
