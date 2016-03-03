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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.threads.javafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatus;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusManager;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;

/**
 * JavaFX Thread Status Manager.
 */
public class MetisFXThreadStatusManager
        extends MetisThreadStatusManager<Node> {
    /**
     * Task Name.
     */
    private final Label theTaskField;

    /**
     * Task Progress Bar.
     */
    private final ProgressBar theTaskProgress;

    /**
     * Stage Name.
     */
    private final Label theStageField;

    /**
     * Steps Progress Bar.
     */
    private final ProgressBar theStageProgress;

    /**
     * Cancel Button.
     */
    private final Button theCancelButton;

    /**
     * Status Label.
     */
    private final Label theStatusField;

    /**
     * Clear Button.
     */
    private final Button theClearButton;

    /**
     * The Status Node.
     */
    private final StackPane theStatusNode;

    /**
     * The Progress Node.
     */
    private final StackPane theProgressNode;

    /**
     * The Node.
     */
    private final BorderPane theNode;

    /**
     * The Node Children.
     */
    private final ObservableList<Node> theChildren;

    /**
     * Timer.
     */
    private final Timeline theTimer;

    /**
     * is the progress pane showing?
     */
    private boolean progressShowing;

    /**
     * Constructor.
     */
    protected MetisFXThreadStatusManager() {
        /* Create components */
        theTaskField = new Label();
        theStageField = new Label();
        theStatusField = new Label();
        theTaskProgress = new ProgressBar();
        theStageProgress = new ProgressBar();
        theCancelButton = new Button(NLS_CANCEL);
        theClearButton = new Button(NLS_CLEAR);

        /* Create the status pane */
        BorderPane myStatusNode = new BorderPane();
        myStatusNode.setCenter(theStatusField);
        myStatusNode.setLeft(theClearButton);
        theStatusNode = TethysFXGuiUtils.getTitledPane(NLS_STATUS, myStatusNode);

        /* Create the task progress pane */
        BorderPane myTaskProgress = new BorderPane();
        myTaskProgress.setCenter(theTaskProgress);
        myTaskProgress.setLeft(theTaskField);

        /* Create the stage progress pane */
        BorderPane myStageProgress = new BorderPane();
        myStageProgress.setCenter(theStageProgress);
        myStageProgress.setLeft(theStageField);

        /* Create the stage progress pane */
        GridPane myProgressGrid = new GridPane();
        myProgressGrid.addRow(0, myTaskProgress, myStageProgress);

        /* Create the progress pane */
        BorderPane myProgressNode = new BorderPane();
        myProgressNode.setCenter(myProgressGrid);
        myProgressNode.setRight(theCancelButton);
        theProgressNode = TethysFXGuiUtils.getTitledPane(NLS_PROGRESS, myProgressNode);

        /* Create the basic Pane */
        theNode = new BorderPane();
        theChildren = theNode.getChildren();

        /* Create the timer */
        theTimer = new Timeline(new KeyFrame(Duration.millis(TIMER_DURATION), e -> handleClear()));

        /* Create listeners */
        theCancelButton.setOnAction(e -> handleCancel());
        theClearButton.setOnAction(e -> handleClear());
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    protected MetisFXThreadManager getThreadManager() {
        return (MetisFXThreadManager) super.getThreadManager();
    }

    @Override
    protected void setProgress(final MetisThreadStatus pStatus) {
        /* Set the task name */
        theTaskField.setText(pStatus.getTask());

        /* Set the task progress */
        theTaskProgress.setProgress(pStatus.getTaskProgress());

        /* Set the stage name */
        theStageField.setText(pStatus.getStage());

        /* Set the stage progress */
        theTaskProgress.setProgress(pStatus.getStageProgress());

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
        theTimer.play();
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
            theChildren.clear();
            theNode.setCenter(theProgressNode);
            progressShowing = true;
        }
    }

    /**
     * Show the status.
     */
    private void showStatus() {
        theChildren.clear();
        theNode.setCenter(theStatusNode);
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
}
