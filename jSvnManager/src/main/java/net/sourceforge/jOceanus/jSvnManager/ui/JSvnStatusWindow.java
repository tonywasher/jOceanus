/*******************************************************************************
 * jSvnManager: Java SubVersion Management
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
package net.sourceforge.jOceanus.jSvnManager.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import net.sourceforge.jOceanus.jSvnManager.data.JSvnReporter.ReportTask;

/**
 * Status window for SubVersion operations.
 * @author Tony Washer
 */
public class JSvnStatusWindow extends JPanel implements ReportTask {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -97846502218500569L;

    /**
     * SvnManager.
     */
    private final transient JSvnManager theManager;

    /**
     * The scroll pane.
     */
    private final JScrollPane theScroll;

    /**
     * The TextArea.
     */
    private final JTextArea theText;

    /**
     * The TextPosition.
     */
    private int thePosition = 0;

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
    private final transient ExecutorService theExecutor = Executors.newSingleThreadExecutor();

    /**
     * The active thread.
     */
    private transient SwingWorker<Void, String> theThread = null;

    /**
     * Constructor.
     * @param pManager the manager
     */
    protected JSvnStatusWindow(final JSvnManager pManager) {
        /* Store the manager */
        theManager = pManager;

        /* Create text area and button */
        theText = new JTextArea();
        theText.setEditable(false);
        theClearButton = new JButton("Clear");
        theCancelButton = new JButton("Cancel");

        /* Create the scrollPane */
        theScroll = new JScrollPane();
        theScroll.setViewportView(theText);
        theScroll.setPreferredSize(new Dimension(600, 400));

        /* Add listener */
        StatusListener myListener = new StatusListener();
        theClearButton.addActionListener(myListener);
        theCancelButton.addActionListener(myListener);
        theCancelButton.setVisible(false);

        /* Add the components */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theScroll);
        add(theClearButton);
        add(theCancelButton);
    }

    /**
     * Run thread.
     * @param pThread the thread to run
     */
    protected void runThread(final SwingWorker<Void, String> pThread) {
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

    @Override
    public boolean initTask(final String pTask) {
        /* Add status to text area */
        return setNewStage(pTask);
    }

    @Override
    public boolean setNewStage(final String pStage) {
        /* Add status to text area */
        theText.append(pStage);
        theText.append("\n");
        thePosition += pStage.length() + 1;
        theText.setCaretPosition(thePosition);
        return true;
    }

    @Override
    public boolean setNumStages(final int pNumStages) {
        return true;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void completeTask(final Object pTask) {
        /* Let the manager know about the completion */
        theManager.completeTask(pTask);

        /* Clear the thread indication and hide the cancel button */
        theThread = null;
        theCancelButton.setVisible(false);
    }

    /**
     * Status Listener class.
     */
    private final class StatusListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this is the clear button */
            if (theClearButton.equals(o)) {
                /* Clear contents */
                theText.setText(null);
                thePosition = 0;

                /* If this is the clear button */
            } else if (theCancelButton.equals(o)) {
                /* Cancel any running thread */
                if (theThread != null) {
                    theThread.cancel(true);
                }
            }
        }
    }
}
