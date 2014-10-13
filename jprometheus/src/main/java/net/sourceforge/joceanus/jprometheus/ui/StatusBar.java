/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicProgressBarUI;

import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmetis.viewer.JMetisExceptionWrapper;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.StatusData;
import net.sourceforge.joceanus.jprometheus.views.StatusDisplay;

/**
 * Status bar panel.
 * @author Tony Washer
 */
public class StatusBar
        implements StatusDisplay {
    /**
     * Maximum progress.
     */
    private static final int MAX_PROGRESS = 100;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Stage width.
     */
    private static final int STAGE_WIDTH = 130;

    /**
     * Stage character width.
     */
    private static final int STAGE_CHARS = 30;

    /**
     * Timer duration.
     */
    private static final int TIMER_DURATION = 5000;

    /**
     * Text for Clear Button.
     */
    private static final String NLS_CLEAR = PrometheusUIResource.STATUSBAR_BUTTON_CLEAR.getValue();

    /**
     * Text for Cancel Button.
     */
    private static final String NLS_CANCEL = PrometheusUIResource.STATUSBAR_BUTTON_CANCEL.getValue();

    /**
     * Text for Progress Title.
     */
    private static final String NLS_PROGRESS = PrometheusUIResource.STATUSBAR_TITLE_PROGRESS.getValue();

    /**
     * Text for Status Title.
     */
    private static final String NLS_STATUS = PrometheusUIResource.STATUSBAR_TITLE_STATUS.getValue();

    /**
     * Text for Succeeded.
     */
    private static final String NLS_SUCCESS = PrometheusUIResource.STATUSBAR_STATUS_SUCCESS.getValue();

    /**
     * Text for Failed.
     */
    private static final String NLS_FAIL = PrometheusUIResource.STATUSBAR_STATUS_FAIL.getValue();

    /**
     * Text for Cancelled.
     */
    private static final String NLS_CANCELLED = PrometheusUIResource.STATUSBAR_STATUS_CANCEL.getValue();

    /**
     * Progress panel.
     */
    private final JPanel theProgPanel;

    /**
     * Status panel.
     */
    private final JPanel theStatPanel;

    /**
     * Steps progress.
     */
    private final JProgressBar theSteps;

    /**
     * Stages progress.
     */
    private final JProgressBar theStages;

    /**
     * Cancel button.
     */
    private final JButton theCancel;

    /**
     * Clear button.
     */
    private final JButton theClear;

    /**
     * Stage label.
     */
    private final JLabel theStageLabel;

    /**
     * Task label.
     */
    private final JLabel theTaskLabel;

    /**
     * Status label.
     */
    private final JLabel theStatusLabel;

    /**
     * Thread control.
     */
    private final ThreadControl theControl;

    /**
     * Data control.
     */
    private final DataControl<?, ?> theData;

    /**
     * Errors.
     */
    private final DataErrorList<JMetisExceptionWrapper> theErrors;

    /**
     * Timer.
     */
    private Timer theTimer = null;

    /**
     * Data entry.
     */
    private final JDataEntry theDataEntry;

    /**
     * Current status.
     */
    private StatusData theCurrent = null;

    /**
     * Listener.
     */
    private final StatusListener theListener;

    /**
     * Get status panel.
     * @return the panel
     */
    public JPanel getStatusPanel() {
        return theStatPanel;
    }

    /**
     * Get progress panel.
     * @return the panel
     */
    public JPanel getProgressPanel() {
        return theProgPanel;
    }

    /**
     * Get error.
     * @return the error
     */
    public DataErrorList<JMetisExceptionWrapper> getErrors() {
        return theErrors;
    }

    /**
     * Constructor.
     * @param pThread the Thread control
     * @param pData the Data control
     */
    public StatusBar(final ThreadControl pThread,
                     final DataControl<?, ?> pData) {
        /* Record passed parameters */
        theControl = pThread;
        theData = pData;

        /* Store access to the Data Entry */
        theDataEntry = theData.getDataEntry(DataControl.DATA_ERROR);
        theErrors = new DataErrorList<JMetisExceptionWrapper>();

        /* Create the boxes */
        theCancel = new JButton(NLS_CANCEL);
        theClear = new JButton(NLS_CLEAR);
        theTaskLabel = new JLabel();
        theStageLabel = new JLabel();
        theStatusLabel = new JLabel();
        theStages = new JProgressBar();
        theSteps = new JProgressBar();

        /* Set backgrounds */
        theStages.setForeground(Color.green);
        theSteps.setForeground(Color.green);
        theSteps.setUI(new ProgressUI());
        theStages.setUI(new ProgressUI());

        /* Initialise progress bars */
        theStages.setMaximum(MAX_PROGRESS);
        theStages.setMinimum(0);
        theStages.setValue(0);
        theStages.setStringPainted(true);
        theSteps.setMaximum(MAX_PROGRESS);
        theSteps.setMinimum(0);
        theSteps.setValue(0);
        theSteps.setStringPainted(true);

        /* Add the listener for item changes */
        theListener = new StatusListener();
        theCancel.addActionListener(theListener);
        theClear.addActionListener(theListener);

        /* Set minimum width on stage label */
        theStageLabel.setMinimumSize(new Dimension(STAGE_WIDTH, 0));

        /* Create the progress panel */
        theProgPanel = new JPanel();
        theProgPanel.setBorder(BorderFactory.createTitledBorder(NLS_PROGRESS));

        /* Define the layout */
        theProgPanel.setLayout(new BoxLayout(theProgPanel, BoxLayout.X_AXIS));
        theProgPanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theProgPanel.add(theTaskLabel);
        theProgPanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theProgPanel.add(theStages);
        theProgPanel.add(Box.createHorizontalGlue());
        theProgPanel.add(theStageLabel);
        theProgPanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theProgPanel.add(theSteps);
        theProgPanel.add(Box.createHorizontalGlue());
        theProgPanel.add(theCancel);
        theProgPanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Create the status panel */
        theStatPanel = new JPanel();
        theStatPanel.setBorder(BorderFactory.createTitledBorder(NLS_STATUS));

        /* Define the layout */
        theStatPanel.setLayout(new BoxLayout(theStatPanel, BoxLayout.X_AXIS));
        theStatPanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theStatPanel.add(theClear);
        theStatPanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theStatPanel.add(theStatusLabel);
        theStatPanel.add(Box.createHorizontalGlue());
    }

    /**
     * ProgressBar UI.
     */
    private static final class ProgressUI
            extends BasicProgressBarUI {
        @Override
        protected Color getSelectionBackground() {
            return Color.black;
        }

        @Override
        protected Color getSelectionForeground() {
            return Color.black;
        }
    }

    @Override
    public void updateStatusBar(final StatusData pStatus) {
        /* Update Task if required */
        if (pStatus.differTask(theCurrent)) {
            /* Set Task */
            theTaskLabel.setText(pStatus.getTask());
        }

        /* Update Stage if required */
        if (pStatus.differStage(theCurrent)) {
            /* Expand stage text to 20 */
            String myStage = pStatus.getStage() + "                              ";
            myStage = myStage.substring(0, STAGE_CHARS);
            theStageLabel.setText(myStage);
        }

        /* Update NumStages if required */
        if (pStatus.differNumStages(theCurrent)) {
            /* Set the Stage progress */
            theStages.setMaximum(pStatus.getNumStages());
        }

        /* Update StagesDone if required */
        if (pStatus.differStagesDone(theCurrent)) {
            /* Set the Stage progress */
            theStages.setValue(pStatus.getStagesDone());
        }

        /* Update NumSteps if required */
        if (pStatus.differNumSteps(theCurrent)) {
            /* Set the Steps progress */
            theSteps.setMaximum(pStatus.getNumSteps());
        }

        /* Update StepsDone if required */
        if (pStatus.differStepsDone(theCurrent)) {
            /* Set the Steps progress */
            theSteps.setValue(pStatus.getStepsDone());
        }

        /* Record current status */
        theCurrent = pStatus;
    }

    @Override
    public void setSuccess(final String pOperation) {
        /* Stop the active task */
        theData.getActiveProfile().end();

        /* Set the status text field */
        theStatusLabel.setText(pOperation + " " + NLS_SUCCESS);

        /* Show the status window rather than the progress window */
        theStatPanel.setVisible(true);
        theProgPanel.setVisible(false);

        /* Set up a timer for 5 seconds and no repeats */
        if (theTimer == null) {
            theTimer = new Timer(TIMER_DURATION, theListener);
        }
        theTimer.setRepeats(false);
        theTimer.start();
    }

    @Override
    public void setFailure(final String pOperation,
                           final DataErrorList<JMetisExceptionWrapper> pErrors) {
        /* Stop the active task */
        theData.getActiveProfile().end();

        /* Initialise the message */
        String myText = pOperation + " " + NLS_FAIL;

        /* If there is an error detail */
        if (!pErrors.isEmpty()) {
            /* Add the first error detail */
            myText += ". " + pErrors.get(0).getMessage();

            /* else no failure - must have cancelled */
        } else {
            myText += ". " + NLS_CANCELLED;
        }

        /* Store the errors */
        theErrors.addList(pErrors);

        /* Enable data show for this error */
        theDataEntry.setObject(theErrors);
        theDataEntry.showPrimeEntry();
        theDataEntry.setFocus();

        /* Set the status text field */
        theStatusLabel.setText(myText);

        /* Show the status window rather than the progress window */
        theStatPanel.setVisible(true);
        theProgPanel.setVisible(false);
    }

    @Override
    public void showProgressPanel() {
        theProgPanel.setVisible(true);
    }

    /**
     * Listener class.
     */
    private class StatusListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the Cancel box */
            if (theCancel.equals(o)) {
                /* Pass command to the table */
                theControl.performCancel();

                /* If this event relates to the Clear box */
            } else if (theClear.equals(o)) {
                /* Stop any timer */
                if (theTimer != null) {
                    theTimer.stop();
                }

                /* Make the Status window invisible */
                theStatPanel.setVisible(false);

                /* Clear the error */
                theErrors.clear();
                theDataEntry.hideEntry();

                /* Finish the thread */
                theControl.finishThread();

                /* If this event relates to the Clear or the timer box */
            } else if (theTimer.equals(o)) {
                /* Make the Status window invisible */
                theStatPanel.setVisible(false);

                /* Clear the error */
                theErrors.clear();
                theDataEntry.hideEntry();

                /* Finish the thread */
                theControl.finishThread();
            }
        }
    }
}
