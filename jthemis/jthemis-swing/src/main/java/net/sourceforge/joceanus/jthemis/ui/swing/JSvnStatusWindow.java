/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.ui.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.scm.tasks.ScmStatus;
import net.sourceforge.joceanus.jthemis.threads.swing.ScmThread;

/**
 * Status window for SubVersion operations.
 * @author Tony Washer
 */
public class JSvnStatusWindow
        implements ReportTask, TethysNode<JComponent> {
    /**
     * Window Height.
     */
    private static final int WINDOW_HEIGHT = 400;

    /**
     * Window Width.
     */
    private static final int WINDOW_WIDTH = 600;

    /**
     * Panel.
     */
    private final JPanel thePanel;

    /**
     * Id.
     */
    private final Integer theId;

    /**
     * SvnManager.
     */
    private final JSvnManager theManager;

    /**
     * The TextArea.
     */
    private final JTextArea theText;

    /**
     * The TextPosition.
     */
    private int thePosition = 0;

    /**
     * The StatusPosition.
     */
    private int theStatusPosition = 0;

    /**
     * The Clear button.
     */
    private final JButton theClearButton;

    /**
     * The Cancel button.
     */
    private final JButton theCancelButton;

    /**
     * The Thread executor.
     */
    private final ExecutorService theExecutor = Executors.newSingleThreadExecutor();

    /**
     * The active thread.
     */
    private ScmThread theThread = null;

    /**
     * The current status.
     */
    private ScmStatus theStatus = new ScmStatus();

    /**
     * Constructor.
     * @param pManager the manager
     */
    protected JSvnStatusWindow(final JSvnManager pManager) {
        /* Store the manager */
        theManager = pManager;
        theId = pManager.getGuiFactory().getNextId();

        /* Create text area and button */
        theText = new JTextArea();
        theText.setEditable(false);
        theClearButton = new JButton("Clear");
        theCancelButton = new JButton("Cancel");

        /* Create the scrollPane */
        JScrollPane myScroll = new JScrollPane();
        myScroll.setViewportView(theText);
        myScroll.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        /* Add listener */
        StatusListener myListener = new StatusListener();
        theClearButton.addActionListener(myListener);
        theCancelButton.addActionListener(myListener);
        theCancelButton.setVisible(false);

        /* Create a new subPanel for the buttons */
        JPanel myButtonPanel = new JPanel();
        myButtonPanel.setLayout(new BoxLayout(myButtonPanel, BoxLayout.X_AXIS));
        myButtonPanel.add(theClearButton);
        myButtonPanel.add(theCancelButton);

        /* Add the components */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(myScroll);
        thePanel.add(myButtonPanel);
        thePanel.setOpaque(true);
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public MetisPreferenceManager getPreferenceMgr() {
        return theManager.getPreferenceMgr();
    }

    @Override
    public GordianHashManager getSecureMgr() {
        return theManager.getSecureMgr();
    }

    @Override
    public JFrame getFrame() {
        return theManager.getFrame();
    }

    /**
     * Run thread.
     * @param pThread the thread to run
     */
    protected void runThread(final ScmThread pThread) {
        /* Store thread and enable cancel button */
        theThread = pThread;
        theCancelButton.setVisible(true);

        /* Run the thread */
        theExecutor.execute(theThread);
    }

    /**
     * Shutdown.
     */
    protected void shutdown() {
        /* Shutdown the executor */
        theExecutor.shutdown();
    }

    /**
     * Set new stage.
     * @param pStage the stage to set
     */
    private void setNewStage(final String pStage) {
        /* Add status to text area */
        theText.replaceRange(pStage, theStatusPosition, thePosition);
        theText.append("\n");
        thePosition = theStatusPosition + pStage.length() + 1;
        theStatusPosition = thePosition;
        theText.setCaretPosition(thePosition);
    }

    /**
     * Set new step.
     * @param pStep the step to set
     */
    public void setNewStep(final String pStep) {
        /* Replace any existing status */
        theText.replaceRange(pStep, theStatusPosition, thePosition);
        thePosition = theStatusPosition + pStep.length();
        theText.insert("\n", thePosition);
        thePosition++;
        theText.setCaretPosition(thePosition);
    }

    @Override
    public void completeTask(final Object pTask) {
        /* Let the manager know about the completion */
        theManager.completeTask(pTask);

        /* Clear the thread indication and hide the cancel button */
        theThread = null;
        theCancelButton.setVisible(false);
    }

    @Override
    public void setNewStatus(final ScmStatus pStatus) {
        /* Set new status */
        ScmStatus myOld = theStatus;
        theStatus = pStatus;

        /* Handle new task */
        String myNewTask = pStatus.getTask();
        if ((myNewTask != null)
            && (!myNewTask.equals(myOld.getTask()))) {
            setNewStage(myNewTask);
            return;
        }

        /* Handle new stage */
        int myStagesDone = pStatus.getStagesDone();
        if (myStagesDone != myOld.getStagesDone()) {
            setNewStage(pStatus.getStage());
            return;
        }

        /* Handle new step */
        int myStepsDone = pStatus.getStepsDone();
        if (myStepsDone != myOld.getStepsDone()) {
            StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(myStepsDone + 1);
            myBuilder.append(" of ");
            myBuilder.append(pStatus.getNumSteps());
            myBuilder.append(": ");
            myBuilder.append(pStatus.getStep());
            setNewStep(myBuilder.toString());
        }
    }

    /**
     * Status Listener class.
     */
    private final class StatusListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this is the clear button */
            if (theClearButton.equals(o)) {
                /* Clear contents */
                theText.setText(null);
                thePosition = 0;
                theStatusPosition = 0;

                /* If this is the cancel button */
            } else if (theCancelButton.equals(o)) {
                /* Cancel any running thread */
                if (theThread != null) {
                    theThread.cancel(true);
                }
            }
        }
    }
}
