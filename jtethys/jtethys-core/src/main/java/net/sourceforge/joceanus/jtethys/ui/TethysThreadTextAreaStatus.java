/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import net.sourceforge.joceanus.jtethys.ui.core.thread.TethysUIThreadResource;

/**
 * Thread TextArea Status Manager.
 */
public class TethysThreadTextAreaStatus
        implements TethysThreadStatusManager {
    /**
     * Cancel button text.
     */
    private static final String NLS_CANCEL = TethysUIThreadResource.STATUSBAR_BUTTON_CANCEL.getValue();

    /**
     * Clear button text.
     */
    private static final String NLS_CLEAR = TethysUIThreadResource.STATUSBAR_BUTTON_CLEAR.getValue();

    /**
     * Succeeded message.
     */
    private static final String NLS_SUCCEEDED = TethysUIThreadResource.STATUSBAR_STATUS_SUCCESS.getValue();

    /**
     * Cancelled message.
     */
    private static final String NLS_CANCELLED = TethysUIThreadResource.STATUSBAR_STATUS_CANCEL.getValue();

    /**
     * Failed message.
     */
    private static final String NLS_FAILED = TethysUIThreadResource.STATUSBAR_STATUS_FAIL.getValue();

    /**
     * The Thread Manager.
     */
    private final TethysThreadManager theThreadManager;

    /**
     * Pane.
     */
    private final TethysBorderPaneManager thePane;

    /**
     * TextArea.
     */
    private final TethysTextArea theTextArea;

    /**
     * Cancel Button.
     */
    private final TethysButton theCancelButton;

    /**
     * The current status.
     */
    private TethysThreadStatus theStatus;

    /**
     * The TextPosition.
     */
    private int thePosition;

    /**
     * The StatusPosition.
     */
    private int theStatusPosition;

    /**
     * Constructor.
     * @param pManager the Thread Manager
     * @param pFactory the GUI factory
     */
    TethysThreadTextAreaStatus(final TethysThreadManager pManager,
                               final TethysGuiFactory pFactory) {
        /* Store parameters */
        theThreadManager = pManager;

        /* Create textArea */
        theTextArea = pFactory.newTextArea();

        /* Create buttons */
        theCancelButton = pFactory.newButton();
        theCancelButton.setTextOnly();
        theCancelButton.setText(NLS_CANCEL);
        final TethysButton theClearButton = pFactory.newButton();
        theClearButton.setTextOnly();
        theClearButton.setText(NLS_CLEAR);
        theCancelButton.setVisible(false);

        /* Create a scroll manager */
        final TethysScrollPaneManager myScroll = pFactory.newScrollPane();
        myScroll.setContent(theTextArea);

        /* Create a new subPanel for the buttons */
        final TethysBoxPaneManager myButtonPanel = pFactory.newHBoxPane();
        myButtonPanel.addNode(theClearButton);
        myButtonPanel.addNode(theCancelButton);

        /* Add the components */
        thePane = pFactory.newBorderPane();
        thePane.setCentre(myScroll);
        thePane.setSouth(myButtonPanel);

        /* Initialise the status */
        theStatus = new TethysThreadStatus();

        /* Add button listeners */
        theClearButton.getEventRegistrar().addEventListener(e -> handleClear());
        theCancelButton.getEventRegistrar().addEventListener(e -> handleCancel());
    }

    @Override
    public Integer getId() {
        return thePane.getId();
    }

    @Override
    public TethysNode getNode() {
        return thePane.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePane.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePane.setVisible(pVisible);
    }

    @Override
    public void setProgress(final TethysThreadStatus pStatus) {
        /* Set new status */
        final TethysThreadStatus myOld = theStatus;
        theStatus = pStatus;

        /* Handle new task */
        final String myNewTask = pStatus.getTask();
        if ((myNewTask != null)
             && (!myNewTask.equals(myOld.getTask()))) {
            setNewStage(myNewTask);
            return;
        }

        /* Handle new stage */
        final int myStagesDone = pStatus.getStagesDone();
        if (myStagesDone != myOld.getStagesDone()) {
            setNewStage(pStatus.getStage());
            return;
        }

        /* Handle new step */
        final int myStepsDone = pStatus.getStepsDone();
        if (myStepsDone != myOld.getStepsDone()) {
            final String myDone = (myStepsDone + 1)
                    + " of "
                    + pStatus.getNumSteps()
                    + ": "
                    + pStatus.getStep();
            setNewStep(myDone);
        }

        /* Show the cancel button */
        theCancelButton.setVisible(true);
    }

    /**
     * Set new stage.
     * @param pStage the stage to set
     */
    private void setNewStage(final String pStage) {
        /* Handle null stage */
        final String myStage = pStage == null
                ? ""
                : pStage;

        /* Add status to text area */
        theTextArea.replaceText(myStage, theStatusPosition, thePosition);
        theTextArea.appendText("\n");
        thePosition = theStatusPosition + myStage.length() + 1;
        theStatusPosition = thePosition;
        theTextArea.setCaretPosition(thePosition);
    }

    /**
     * Set new step.
     * @param pStep the step to set
     */
    protected void setNewStep(final String pStep) {
        /* Replace any existing status */
        theTextArea.replaceText(pStep, theStatusPosition, thePosition);
        thePosition = theStatusPosition + pStep.length();
        theTextArea.appendText("\n");
        thePosition++;
        theTextArea.setCaretPosition(thePosition);
    }

    /**
     * Complete the status.
     * @param pStatus the final status
     */
    private void completeStatus(final String pStatus) {
        /* Append the text */
        theTextArea.appendText(pStatus);
        thePosition = theStatusPosition + pStatus.length();
        theTextArea.appendText("\n");
        thePosition++;
        theTextArea.setCaretPosition(thePosition);

        /* Hide the cancel button */
        theCancelButton.setVisible(false);
        theThreadManager.threadCompleted();
    }

    @Override
    public void setCompletion() {
        /* Obtain the task name */
        final String myTask = theThreadManager.getTaskName();

        /* Initialise the message */
        final String myText = myTask + " " + NLS_SUCCEEDED;

        /* Complete the status */
        completeStatus(myText);
    }

    @Override
    public void setFailure(final Throwable pException) {
        /* Obtain the task name */
        final String myTask = theThreadManager.getTaskName();

        /* Initialise the message */
        String myText = myTask + " " + NLS_FAILED;

        /* Add the first error detail */
        myText += " " + pException.getMessage();

        /* Complete the status */
        completeStatus(myText);
    }

    @Override
    public void setCancelled() {
        /* Obtain the task name */
        final String myTask = theThreadManager.getTaskName();

        /* Initialise the message */
        final String myText = myTask + " " + NLS_CANCELLED;

        /* Complete the status */
        completeStatus(myText);
    }

    /**
     * handle cancel.
     */
    protected void handleCancel() {
        theThreadManager.cancelWorker();
    }

    /**
     * Handle clear request.
     */
    protected void handleClear() {
        /* Clear contents */
        theTextArea.setText(null);
        thePosition = 0;
        theStatusPosition = 0;
    }
}
