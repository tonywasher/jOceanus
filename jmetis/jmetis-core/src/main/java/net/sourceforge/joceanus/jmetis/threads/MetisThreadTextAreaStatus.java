/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.threads;

import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTextArea;

/**
 * Thread TextArea Status Manager.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class MetisThreadTextAreaStatus<N, I>
        implements MetisThreadStatusManager<N> {
    /**
     * Cancel button text.
     */
    private static final String NLS_CANCEL = MetisThreadResource.STATUSBAR_BUTTON_CANCEL.getValue();

    /**
     * Clear button text.
     */
    private static final String NLS_CLEAR = MetisThreadResource.STATUSBAR_BUTTON_CLEAR.getValue();

    /**
     * Succeeded message.
     */
    private static final String NLS_SUCCEEDED = MetisThreadResource.STATUSBAR_STATUS_SUCCESS.getValue();

    /**
     * Cancelled message.
     */
    private static final String NLS_CANCELLED = MetisThreadResource.STATUSBAR_STATUS_CANCEL.getValue();

    /**
     * Failed message.
     */
    private static final String NLS_FAILED = MetisThreadResource.STATUSBAR_STATUS_FAIL.getValue();

    /**
     * The Thread Manager.
     */
    private final MetisThreadManager<N, I> theThreadManager;

    /**
     * GUI Factory.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * Pane.
     */
    private final TethysBorderPaneManager<N, I> thePane;

    /**
     * TextArea.
     */
    private final TethysTextArea<N, I> theTextArea;

    /**
     * Cancel Button.
     */
    private final TethysButton<N, I> theCancelButton;

    /**
     * Clear Button.
     */
    private final TethysButton<N, I> theClearButton;

    /**
     * The current status.
     */
    private MetisThreadStatus theStatus;

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
    protected MetisThreadTextAreaStatus(final MetisThreadManager<N, I> pManager,
                                        final TethysGuiFactory<N, I> pFactory) {
        /* Store parameters */
        theThreadManager = pManager;
        theGuiFactory = pFactory;

        /* Create textArea */
        theTextArea = theGuiFactory.newTextArea();

        /* Create buttons */
        theCancelButton = theGuiFactory.newButton();
        theCancelButton.setTextOnly();
        theCancelButton.setText(NLS_CANCEL);
        theClearButton = theGuiFactory.newButton();
        theClearButton.setTextOnly();
        theClearButton.setText(NLS_CLEAR);
        theCancelButton.setVisible(false);

        /* Create a scroll manager */
        TethysScrollPaneManager<N, I> myScroll = theGuiFactory.newScrollPane();
        myScroll.setContent(theTextArea);

        /* Create a new subPanel for the buttons */
        TethysBoxPaneManager<N, I> myButtonPanel = theGuiFactory.newHBoxPane();
        myButtonPanel.addNode(theClearButton);
        myButtonPanel.addNode(theCancelButton);

        /* Add the components */
        thePane = theGuiFactory.newBorderPane();
        thePane.setCentre(myScroll);
        thePane.setSouth(myButtonPanel);

        /* Initialise the status */
        theStatus = new MetisThreadStatus();

        /* Add button listeners */
        theClearButton.getEventRegistrar().addEventListener(e -> handleClear());
        theCancelButton.getEventRegistrar().addEventListener(e -> handleCancel());
    }

    @Override
    public Integer getId() {
        return thePane.getId();
    }

    @Override
    public N getNode() {
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
    public void setProgress(final MetisThreadStatus pStatus) {
        /* Set new status */
        MetisThreadStatus myOld = theStatus;
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

        /* Show the cancel button */
        theCancelButton.setVisible(true);
    }

    /**
     * Set new stage.
     * @param pStage the stage to set
     */
    private void setNewStage(final String pStage) {
        /* Add status to text area */
        theTextArea.replaceText(pStage, theStatusPosition, thePosition);
        theTextArea.appendText("\n");
        thePosition = theStatusPosition + pStage.length() + 1;
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
        String myTask = theThreadManager.getTaskName();

        /* Initialise the message */
        String myText = myTask + " " + NLS_SUCCEEDED;

        /* Complete the status */
        completeStatus(myText);
    }

    @Override
    public void setFailure(final Throwable pException) {
        /* Obtain the task name */
        String myTask = theThreadManager.getTaskName();

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
        String myTask = theThreadManager.getTaskName();

        /* Initialise the message */
        String myText = myTask + " " + NLS_CANCELLED;

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
