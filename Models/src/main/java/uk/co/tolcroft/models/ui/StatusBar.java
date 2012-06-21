/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicProgressBarUI;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import uk.co.tolcroft.models.views.DataControl;

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
     * Stage width.
     */
    private static final int STAGE_WIDTH = 130;

    /**
     * Stage width.
     */
    private static final int STAGE_CHARS = 20;

    /**
     * Timer duration.
     */
    private static final int TIMER_DURATION = 5000;

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
     * Error.
     */
    private JDataException theError = null;

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
    public JDataException getError() {
        return theError;
    }

    /**
     * Constructor.
     * @param pThread the Thread control
     * @param pData the Data control
     */
    public StatusBar(final ThreadControl pThread,
                     final DataControl<?> pData) {
        GroupLayout panelLayout;

        /* Record passed parameters */
        theControl = pThread;

        /* Store access to the Data Entry */
        theDataEntry = pData.getDataEntry(DataControl.DATA_ERROR);

        /* Create the boxes */
        theCancel = new JButton("Cancel");
        theClear = new JButton("Clear");
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

        /* Create the progress panel */
        theProgPanel = new JPanel();
        theProgPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Progress"));

        /* Create the layout for the save panel */
        panelLayout = new GroupLayout(theProgPanel);
        theProgPanel.setLayout(panelLayout);

        /* Set the layout */
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout
                                  .createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(theTaskLabel)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theStages)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addComponent(theStageLabel, GroupLayout.PREFERRED_SIZE, STAGE_WIDTH,
                                                GroupLayout.PREFERRED_SIZE)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theSteps)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addComponent(theCancel).addContainerGap()));
        panelLayout
                .setVerticalGroup(panelLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout
                                          .createSequentialGroup()
                                          .addGroup(panelLayout
                                                            .createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                                 false)
                                                            .addComponent(theTaskLabel,
                                                                          GroupLayout.Alignment.LEADING,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          Short.MAX_VALUE)
                                                            .addComponent(theStages,
                                                                          GroupLayout.Alignment.LEADING,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          Short.MAX_VALUE)
                                                            .addComponent(theStageLabel,
                                                                          GroupLayout.Alignment.LEADING,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          Short.MAX_VALUE)
                                                            .addComponent(theSteps,
                                                                          GroupLayout.Alignment.LEADING,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          Short.MAX_VALUE)
                                                            .addComponent(theCancel,
                                                                          GroupLayout.Alignment.LEADING,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          Short.MAX_VALUE))));

        /* Create the status panel */
        theStatPanel = new JPanel();
        theStatPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        /* Create the layout for the save panel */
        panelLayout = new GroupLayout(theStatPanel);
        theStatPanel.setLayout(panelLayout);

        /* Set the layout */
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          panelLayout.createSequentialGroup().addContainerGap().addComponent(theClear)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theStatusLabel)));
        panelLayout
                .setVerticalGroup(panelLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout
                                          .createSequentialGroup()
                                          .addGroup(panelLayout
                                                            .createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                                 false)
                                                            .addComponent(theStatusLabel,
                                                                          GroupLayout.Alignment.LEADING,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          Short.MAX_VALUE)
                                                            .addComponent(theClear,
                                                                          GroupLayout.Alignment.LEADING,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          GroupLayout.DEFAULT_SIZE,
                                                                          Short.MAX_VALUE))));
    }

    /**
     * ProgressBar UI.
     */
    private static final class ProgressUI extends BasicProgressBarUI {
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

    /**
     * Set Success string.
     * @param pOperation the operation
     */
    public void setSuccess(final String pOperation) {
        /* Set the status text field */
        theStatusLabel.setText(pOperation + " succeeded");

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
     * @param pError the error
     */
    public void setFailure(final String pOperation,
                           final JDataException pError) {
        /* Initialise the message */
        String myText = pOperation + " failed";

        /* If there is an error detail */
        if (pError != null) {
            /* Add the error detail */
            myText += ". " + pError.getMessage();

            /* else no failure - must have cancelled */
        } else {
            myText += ". Operation cancelled";
        }

        /* Store the error */
        theError = pError;

        /* Enable data show for this error */
        theDataEntry.setObject(theError);
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
    private class StatusListener implements ActionListener {
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
                theError = null;

                /* Finish the thread */
                theControl.finishThread();

                /* If this event relates to the Clear or the timer box */
            } else if (theTimer.equals(o)) {
                /* Make the Status window invisible */
                theStatPanel.setVisible(false);

                /* Clear the error */
                theError = null;
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
            return (pData == null) || (theNumSteps != pData.getNumSteps());
        }

        /**
         * Has the number of stages changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differNumStages(final StatusData pData) {
            return (pData == null) || (theNumStages != pData.getNumStages());
        }

        /**
         * Has the number of steps completed changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differStepsDone(final StatusData pData) {
            return (pData == null) || (theStepsDone != pData.getStepsDone());
        }

        /**
         * Has the number of stages completed changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differStagesDone(final StatusData pData) {
            return (pData == null) || (theStagesDone != pData.getStagesDone());
        }

        /**
         * Has the stage name changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differStage(final StatusData pData) {
            return (pData == null) || !Difference.isEqual(theStage, pData.getStage());
        }

        /**
         * Has the task name changed?
         * @param pData the new status
         * @return true/false
         */
        public boolean differTask(final StatusData pData) {
            return (pData == null) || !Difference.isEqual(theTask, pData.getTask());
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
