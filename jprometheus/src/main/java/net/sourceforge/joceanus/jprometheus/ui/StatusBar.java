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
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicProgressBarUI;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmetis.viewer.JMetisExceptionWrapper;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.views.DataControl;

/**
 * Status bar panel.
 * @author Tony Washer
 */
public class StatusBar {
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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(StatusBar.class.getName());

    /**
     * Text for Clear Button.
     */
    private static final String NLS_CLEAR = NLS_BUNDLE.getString("ClearButton");

    /**
     * Text for Cancel Button.
     */
    private static final String NLS_CANCEL = NLS_BUNDLE.getString("CancelButton");

    /**
     * Text for Progress Title.
     */
    private static final String NLS_PROGRESS = NLS_BUNDLE.getString("ProgressTitle");

    /**
     * Text for Status Title.
     */
    private static final String NLS_STATUS = NLS_BUNDLE.getString("StatusTitle");

    /**
     * Text for Succeeded.
     */
    private static final String NLS_SUCCESS = NLS_BUNDLE.getString("Succeeded");

    /**
     * Text for Failed.
     */
    private static final String NLS_FAIL = NLS_BUNDLE.getString("Failed");

    /**
     * Text for Cancelled.
     */
    private static final String NLS_CANCELLED = NLS_BUNDLE.getString("Cancelled");

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
     * Get progress panel.
     * @return the panel
     */
    public JPanel getProgressPanel() {
        return theProgPanel;
    }

    /**
     * Get status panel.
     * @return the panel
     */
    public JPanel getStatusPanel() {
        return theStatPanel;
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
                     final DataControl<?> pData) {
        /* Record passed parameters */
        theControl = pThread;

        /* Store access to the Data Entry */
        theDataEntry = pData.getDataEntry(DataControl.DATA_ERROR);
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

    /**
     * Update StatusBar.
     * @param pStatus the status data
     */
    public void updateStatusBar(final StatusData pStatus) {
        /* Update Task if required */
        if (pStatus.differTask(theCurrent)) {
            /* Set Task */
            theTaskLabel.setText(pStatus.getTask());
        }

        /* Update Stage if required */
        if (pStatus.differStage(theCurrent)) {
            /* Expand stage text to 20 */
            String myStage = pStatus.getStage()
                             + "                              ";
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

    /**
     * Set Success string.
     * @param pOperation the operation
     */
    public void setSuccess(final String pOperation) {
        /* Set the status text field */
        theStatusLabel.setText(pOperation
                               + " "
                               + NLS_SUCCESS);

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

    /**
     * Set Failure string.
     * @param pOperation the operation
     * @param pErrors the error list
     */
    public void setFailure(final String pOperation,
                           final DataErrorList<JMetisExceptionWrapper> pErrors) {
        /* Initialise the message */
        String myText = pOperation
                        + " "
                        + NLS_FAIL;

        /* If there is an error detail */
        if (!pErrors.isEmpty()) {
            /* Add the first error detail */
            myText += ". "
                      + pErrors.get(0).getMessage();

            /* else no failure - must have cancelled */
        } else {
            myText += ". "
                      + NLS_CANCELLED;
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

    /**
     * Status Data object that is used to report the status of a thread.
     * @author Tony Washer
     */
    public static class StatusData {
        /**
         * Default Number of steps/stages.
         */
        private static final int DEFAULT_NUMBER = 100;

        /**
         * Number of steps.
         */
        private int theNumSteps = DEFAULT_NUMBER;

        /**
         * Steps performed.
         */
        private int theStepsDone = 0;

        /**
         * Number of stages.
         */
        private int theNumStages = DEFAULT_NUMBER;

        /**
         * Stages performed.
         */
        private int theStagesDone = 0;

        /**
         * Current stage.
         */
        private String theStage = "";

        /**
         * Current task.
         */
        private String theTask = "";

        /**
         * Get number of steps.
         * @return number of steps
         */
        public int getNumSteps() {
            return theNumSteps;
        }

        /**
         * Get number of steps done.
         * @return number of steps done
         */
        public int getStepsDone() {
            return theStepsDone;
        }

        /**
         * Get number of stages.
         * @return number of stages
         */
        public int getNumStages() {
            return theNumStages;
        }

        /**
         * Get number of stages done.
         * @return number of stages done
         */
        public int getStagesDone() {
            return theStagesDone;
        }

        /**
         * Get name of stage.
         * @return name of stage
         */
        public String getStage() {
            return theStage;
        }

        /**
         * Get name of task.
         * @return name of task
         */
        public String getTask() {
            return theTask;
        }

        /**
         * Set Number of steps in this stage.
         * @param pValue the value
         */
        public void setNumSteps(final int pValue) {
            theNumSteps = pValue;
        }

        /**
         * Set Number of steps completed in this stage.
         * @param pValue the value
         */
        public void setStepsDone(final int pValue) {
            theStepsDone = pValue;
        }

        /**
         * Set Number of stages in this task.
         * @param pValue the value
         */
        public void setNumStages(final int pValue) {
            theNumStages = pValue;
        }

        /**
         * Set Number of stages completed in this task.
         * @param pValue the value
         */
        public void setStagesDone(final int pValue) {
            theStagesDone = pValue;
        }

        /**
         * Set name of stage in this task.
         * @param pValue the value
         */
        public void setStage(final String pValue) {
            theStage = pValue;
        }

        /**
         * Set Name of task.
         * @param pValue the value
         */
        public void setTask(final String pValue) {
            theTask = pValue;
        }

        /**
         * Has the number of steps changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differNumSteps(final StatusData pData) {
            return (pData == null)
                   || (theNumSteps != pData.getNumSteps());
        }

        /**
         * Has the number of stages changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differNumStages(final StatusData pData) {
            return (pData == null)
                   || (theNumStages != pData.getNumStages());
        }

        /**
         * Has the number of steps completed changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differStepsDone(final StatusData pData) {
            return (pData == null)
                   || (theStepsDone != pData.getStepsDone());
        }

        /**
         * Has the number of stages completed changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differStagesDone(final StatusData pData) {
            return (pData == null)
                   || (theStagesDone != pData.getStagesDone());
        }

        /**
         * Has the stage name changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differStage(final StatusData pData) {
            return (pData == null)
                   || !Difference.isEqual(theStage, pData.getStage());
        }

        /**
         * Has the task name changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differTask(final StatusData pData) {
            return (pData == null)
                   || !Difference.isEqual(theTask, pData.getTask());
        }

        /**
         * Constructor.
         */
        public StatusData() {
        }

        /**
         * Constructor.
         * @param pStatus the source status
         */
        public StatusData(final StatusData pStatus) {
            theNumSteps = pStatus.getNumSteps();
            theNumStages = pStatus.getNumStages();
            theStepsDone = pStatus.getStepsDone();
            theStagesDone = pStatus.getStagesDone();
            theStage = pStatus.getStage();
            theTask = pStatus.getTask();
        }
    }
}
