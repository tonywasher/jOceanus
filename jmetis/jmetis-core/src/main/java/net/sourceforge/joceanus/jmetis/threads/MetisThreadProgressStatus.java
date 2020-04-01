/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2020 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.threads;

import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysProgressBar;

/**
 * Thread ProgressBar Status Manager.
 */
public abstract class MetisThreadProgressStatus
        implements MetisThreadStatusManager {
    /**
     * Timer duration.
     */
    protected static final int TIMER_DURATION = 5000;

    /**
     * Gap width.
     */
    protected static final int GAP_WIDTH = 20;

    /**
     * Blank character.
     */
    private static final char CHAR_BLANK = ' ';

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
     * Progress title.
     */
    private static final String NLS_PROGRESS = MetisThreadResource.STATUSBAR_TITLE_PROGRESS.getValue();

    /**
     * Status title.
     */
    private static final String NLS_STATUS = MetisThreadResource.STATUSBAR_TITLE_STATUS.getValue();

    /**
     * Status panel name.
     */
    private static final String PANEL_STATUS = "Status";

    /**
     * Progress panel name.
     */
    private static final String PANEL_PROGRESS = "Progress";

    /**
     * The Thread Manager.
     */
    private final MetisThreadManager theThreadManager;

    /**
     * GUI Factory.
     */
    private final TethysGuiFactory theGuiFactory;

    /**
     * Task Name.
     */
    private final TethysLabel theTaskField;

    /**
     * Task Progress Bar.
     */
    private final TethysProgressBar theTaskProgress;

    /**
     * Stage Name.
     */
    private final TethysLabel theStageField;

    /**
     * Steps Progress Bar.
     */
    private final TethysProgressBar theStageProgress;

    /**
     * Cancel Button.
     */
    private final TethysButton theCancelButton;

    /**
     * Status Label.
     */
    private final TethysLabel theStatusField;

    /**
     * Clear Button.
     */
    private final TethysButton theClearButton;

    /**
     * The Status Node.
     */
    private final TethysBorderPaneManager theStatusNode;

    /**
     * The Progress Node.
     */
    private final TethysBorderPaneManager theProgressNode;

    /**
     * The Stage Panel.
     */
    private final TethysBorderPaneManager theStagePanel;

    /**
     * The Node.
     */
    private final TethysCardPaneManager<TethysBorderPaneManager> theNode;

    /**
     * is the progress pane showing?
     */
    private boolean progressShowing;

    /**
     * have we completed?
     */
    private boolean haveCompleted;

    /**
     * Constructor.
     * @param pManager the Thread Manager
     * @param pFactory the GUI factory
     */
    protected MetisThreadProgressStatus(final MetisThreadManager pManager,
                                        final TethysGuiFactory pFactory) {
        /* Store parameters */
        theThreadManager = pManager;
        theGuiFactory = pFactory;

        /* Create components */
        theTaskField = theGuiFactory.newLabel();
        theStageField = theGuiFactory.newLabel();
        theStatusField = theGuiFactory.newLabel();
        theTaskProgress = theGuiFactory.newProgressBar();
        theStageProgress = theGuiFactory.newProgressBar();

        /* Create buttons */
        theCancelButton = theGuiFactory.newButton();
        theCancelButton.setTextOnly();
        theCancelButton.setText(NLS_CANCEL);
        theClearButton = theGuiFactory.newButton();
        theClearButton.setTextOnly();
        theClearButton.setText(NLS_CLEAR);

        /* Create the status pane */
        theStatusNode = theGuiFactory.newBorderPane();
        theStatusNode.setHGap(GAP_WIDTH);
        theStatusNode.setCentre(theStatusField);
        theStatusNode.setWest(theClearButton);
        theStatusNode.setBorderTitle(NLS_STATUS);

        /* Create the task progress pane */
        final TethysBorderPaneManager myTaskProgress = theGuiFactory.newBorderPane();
        myTaskProgress.setHGap(GAP_WIDTH);
        myTaskProgress.setCentre(theTaskProgress);
        myTaskProgress.setWest(theTaskField);

        /* Create the stage progress pane */
        theStagePanel = theGuiFactory.newBorderPane();
        theStagePanel.setHGap(GAP_WIDTH);
        theStagePanel.setCentre(theStageProgress);
        theStagePanel.setWest(theStageField);

        /* Create the stage progress pane */
        final TethysGridPaneManager myProgressGrid = theGuiFactory.newGridPane();
        myProgressGrid.setHGap(GAP_WIDTH);
        myProgressGrid.addCell(myTaskProgress);
        myProgressGrid.allowCellGrowth(myTaskProgress);
        myProgressGrid.addCell(theStagePanel);
        myProgressGrid.allowCellGrowth(theStagePanel);

        /* Create the progress pane */
        theProgressNode = theGuiFactory.newBorderPane();
        theProgressNode.setHGap(GAP_WIDTH);
        theProgressNode.setCentre(myProgressGrid);
        theProgressNode.setEast(theCancelButton);
        theProgressNode.setBorderTitle(NLS_PROGRESS);

        /* Create the basic Pane */
        theNode = theGuiFactory.newCardPane();
        theNode.addCard(PANEL_STATUS, theStatusNode);
        theNode.addCard(PANEL_PROGRESS, theProgressNode);

        /* Create listeners */
        theCancelButton.getEventRegistrar().addEventListener(e -> handleCancel());
        theClearButton.getEventRegistrar().addEventListener(e -> handleClear());
    }

    @Override
    public Integer getId() {
        return theNode.getId();
    }

    @Override
    public TethysNode getNode() {
        return theNode.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnable) {
        theNode.setEnabled(pEnable);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    /**
     * get Thread Manager.
     * @return the thread manager
     */
    protected MetisThreadManager getThreadManager() {
        return theThreadManager;
    }

    /**
     * Obtain the GUI factory.
     * @return the factory
     */
    protected TethysGuiFactory getGuiFactory() {
        return theGuiFactory;
    }

    @Override
    public void setProgress(final MetisThreadStatus pStatus) {
        /* Obtain the stage name */
        final String myStage = pStatus.getStage();

        /* Set the task name */
        theTaskField.setText(pStatus.getTask());

        /* Set the task progress */
        theTaskProgress.setProgress(pStatus.getTaskProgress());

        /* Set the stage name */
        theStageField.setText(myStage);

        /* Set the stage progress */
        theStageProgress.setProgress(pStatus.getStageProgress());

        /* Set stage visibility */
        theStagePanel.setVisible(myStage != null);

        /* Make sure that the progress node is visible */
        showProgress();
    }

    @Override
    public void setCompletion() {
        /* Obtain the task name */
        final String myTask = getThreadManager().getTaskName();

        /* Build the message */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myTask);
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(NLS_SUCCEEDED);

        /* Set the text */
        theStatusField.setText(myBuilder.toString());

        /* Show status */
        showStatus();
    }

    @Override
    public void setFailure(final Throwable pException) {
        /* Obtain the task name */
        final String myTask = getThreadManager().getTaskName();

        /* Build the message */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myTask);
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(NLS_FAILED);
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(pException.getClass().getSimpleName());
        myBuilder.append(": ");
        myBuilder.append(pException.getMessage());

        /* Set the text */
        theStatusField.setText(myBuilder.toString());

        /* Show status */
        showStatus();
    }

    @Override
    public void setCancelled() {
        /* Obtain the task name */
        final String myTask = getThreadManager().getTaskName();

        /* Build the message */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myTask);
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(NLS_CANCELLED);

        /* Set the text */
        theStatusField.setText(myBuilder.toString());

        /* Show status */
        showStatus();
    }

    /**
     * Show the progress.
     */
    private void showProgress() {
        /* Only bother if we need to */
        if (!progressShowing
            && !haveCompleted) {
            theNode.selectCard(PANEL_PROGRESS);
            progressShowing = true;
        }
    }

    /**
     * Show the status.
     */
    private void showStatus() {
        theNode.selectCard(PANEL_STATUS);
        progressShowing = false;
        haveCompleted = true;
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
        /* Note that the thread is completed */
        theThreadManager.threadCompleted();
        haveCompleted = false;
    }
}
