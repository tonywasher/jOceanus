/*******************************************************************************
 * jMetis: Java Data Framework
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/sheet/MetisWorkBookType.java $
 * $Revision: 655 $
 * $Author: Tony $
 * $Date: 2015-12-02 14:34:04 +0000 (Wed, 02 Dec 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.threads.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicProgressBarUI;

import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatus;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusManager;

/**
 * Swing Thread Status Manager.
 */
public class MetisSwingThreadStatusManager
        extends MetisThreadStatusManager<JComponent> {
    /**
     * Status panel name.
     */
    private static final String STATUS_PANEL = "Status";

    /**
     * Progress panel name.
     */
    private static final String PROGRESS_PANEL = "Progress";

    /**
     * Task Name.
     */
    private final JLabel theTaskField;

    /**
     * Task Progress Bar.
     */
    private final JProgressBar theTaskProgress;

    /**
     * Stage Name.
     */
    private final JLabel theStageField;

    /**
     * Steps Progress Bar.
     */
    private final JProgressBar theStageProgress;

    /**
     * Cancel Button.
     */
    private final JButton theCancelButton;

    /**
     * Status Label.
     */
    private final JLabel theStatusField;

    /**
     * Clear Button.
     */
    private final JButton theClearButton;

    /**
     * The Status Node.
     */
    private final JPanel theStatusNode;

    /**
     * The Progress Node.
     */
    private final JPanel theProgressNode;

    /**
     * The Card Layout.
     */
    private final CardLayout theCardLayout;

    /**
     * The Node.
     */
    private final JPanel theNode;

    /**
     * Timer.
     */
    private final Timer theTimer;

    /**
     * is the progress pane showing?
     */
    private boolean progressShowing;

    /**
     * Constructor.
     */
    protected MetisSwingThreadStatusManager() {
        /* Create components */
        theTaskField = new JLabel();
        theStageField = new JLabel();
        theStatusField = new JLabel();
        theTaskProgress = new JProgressBar();
        theStageProgress = new JProgressBar();
        theCancelButton = new JButton(NLS_CANCEL);
        theClearButton = new JButton(NLS_CLEAR);

        /* Initialise the progress bar */
        theTaskProgress.setMinimum(0);
        theStageProgress.setMinimum(0);
        theTaskProgress.setStringPainted(true);
        theStageProgress.setStringPainted(true);

        /* Set backgrounds */
        theTaskProgress.setForeground(Color.green);
        theStageProgress.setForeground(Color.green);
        theTaskProgress.setUI(new ProgressUI());
        theStageProgress.setUI(new ProgressUI());

        /* Create the status pane */
        theStatusNode = new JPanel();
        theStatusNode.setLayout(new BorderLayout());
        theStatusNode.setBorder(BorderFactory.createTitledBorder(NLS_STATUS));
        theStatusNode.add(theStatusField, BorderLayout.CENTER);
        theStatusNode.add(theClearButton, BorderLayout.LINE_START);

        /* Create the task progress pane */
        JPanel myTaskProgress = new JPanel();
        myTaskProgress.setLayout(new BorderLayout());
        myTaskProgress.add(theTaskProgress, BorderLayout.CENTER);
        myTaskProgress.add(theTaskField, BorderLayout.LINE_START);

        /* Create the stage progress pane */
        JPanel myStageProgress = new JPanel();
        myStageProgress.setLayout(new BorderLayout());
        myStageProgress.add(theStageProgress, BorderLayout.CENTER);
        myStageProgress.add(theStageField, BorderLayout.LINE_START);

        /* Create the stage progress pane */
        JPanel myProgressGrid = new JPanel();
        myProgressGrid.setLayout(new GridLayout(1, 2));
        myProgressGrid.add(theTaskProgress);
        myProgressGrid.add(theStageProgress);

        /* Create the progress pane */
        theProgressNode = new JPanel();
        theProgressNode.setLayout(new BorderLayout());
        theProgressNode.setBorder(BorderFactory.createTitledBorder(NLS_PROGRESS));
        theProgressNode.add(myProgressGrid, BorderLayout.CENTER);
        theProgressNode.add(theCancelButton, BorderLayout.LINE_END);

        /* Create the basic Pane */
        theNode = new JPanel();
        theCardLayout = new CardLayout();
        theNode.setLayout(theCardLayout);
        theNode.add(theStatusNode, STATUS_PANEL);
        theNode.add(theProgressNode, PROGRESS_PANEL);

        /* Create the timer */
        theTimer = new Timer(TIMER_DURATION, e -> handleClear());
        theTimer.setRepeats(false);

        /* Create listeners */
        theCancelButton.addActionListener(e -> handleCancel());
        theClearButton.addActionListener(e -> handleClear());
    }

    @Override
    public JComponent getNode() {
        return theNode;
    }

    /**
     * get Thread Manager.
     * @return the thread manager
     */
    protected MetisSwingThreadManager getThreadManager() {
        return (MetisSwingThreadManager) super.getThreadManager();
    }

    @Override
    protected void setProgress(final MetisThreadStatus pStatus) {
        /* Set the task name */
        theTaskField.setText(pStatus.getTask());

        /* Set the task progress */
        theTaskProgress.setMaximum(pStatus.getNumStages());
        theTaskProgress.setValue(pStatus.getStagesDone());

        /* Set the stage name */
        theStageField.setText(pStatus.getStage());

        /* Set the stage progress */
        theStageProgress.setMaximum(pStatus.getNumSteps());
        theStageProgress.setValue(pStatus.getStepsDone());

        /* Make sure that the progress node is visible */
        showProgress();
    }

    @Override
    protected void setCompletion() {
        /* Obtain the task name */
        String myTask = getThreadManager().getTaskName();

        /* Initialise the message */
        String myText = myTask + " " + NLS_SUCCEEDED;

        /* Set the text */
        theStatusField.setText(myText);

        /* Show status */
        showStatus();

        /* Start the timer */
        theTimer.restart();
    }

    @Override
    protected void setFailure(final Throwable pException) {
        /* Obtain the task name */
        String myTask = getThreadManager().getTaskName();

        /* Initialise the message */
        String myText = myTask + " " + NLS_FAILED;

        /* Add the first error detail */
        myText += " " + pException.getMessage();

        /* Set the text */
        theStatusField.setText(myText);

        /* Show status */
        showStatus();
    }

    @Override
    protected void setCancelled() {
        /* Obtain the task name */
        String myTask = getThreadManager().getTaskName();

        /* Initialise the message */
        String myText = myTask + " " + NLS_CANCELLED;

        /* Set the text */
        theStatusField.setText(myText);

        /* Show status */
        showStatus();
    }

    /**
     * Show the progress.
     */
    private void showProgress() {
        /* Only bother if we need to */
        if (!progressShowing) {
            theCardLayout.show(theNode, PROGRESS_PANEL);
            progressShowing = true;
        }
    }

    /**
     * Show the status.
     */
    private void showStatus() {
        theCardLayout.show(theNode, STATUS_PANEL);
        progressShowing = false;
    }

    /**
     * Handle clear request.
     */
    private void handleClear() {
        /* cancel any existing task */
        theTimer.stop();

        /* Note that the thread is completed */
        getThreadManager().threadCompleted();
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
}
