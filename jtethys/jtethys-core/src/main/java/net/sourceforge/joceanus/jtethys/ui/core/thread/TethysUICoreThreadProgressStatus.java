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
package net.sourceforge.joceanus.jtethys.ui.core.thread;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUINode;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIProgressBar;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUICardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatus;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusManager;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Thread ProgressBar Status Manager.
 */
public abstract class TethysUICoreThreadProgressStatus
        extends TethysUICoreComponent
        implements TethysUIThreadStatusManager {
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
     * Progress title.
     */
    private static final String NLS_PROGRESS = TethysUIThreadResource.STATUSBAR_TITLE_PROGRESS.getValue();

    /**
     * Status title.
     */
    private static final String NLS_STATUS = TethysUIThreadResource.STATUSBAR_TITLE_STATUS.getValue();

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
    private final TethysUICoreThreadManager theThreadManager;

    /**
     * GUI Factory.
     */
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * Task Name.
     */
    private final TethysUILabel theTaskField;

    /**
     * Task Progress Bar.
     */
    private final TethysUIProgressBar theTaskProgress;

    /**
     * Stage Name.
     */
    private final TethysUILabel theStageField;

    /**
     * Steps Progress Bar.
     */
    private final TethysUIProgressBar theStageProgress;

    /**
     * Status Label.
     */
    private final TethysUILabel theStatusField;

    /**
     * The Stage Panel.
     */
    private final TethysUIBorderPaneManager theStagePanel;

    /**
     * The Node.
     */
    private final TethysUICardPaneManager<TethysUIBorderPaneManager> theNode;

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
    protected TethysUICoreThreadProgressStatus(final TethysUICoreThreadManager pManager,
                                               final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theThreadManager = pManager;
        theGuiFactory = pFactory;

        /* Create components */
        final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
        theTaskField = myControls.newLabel();
        theStageField = myControls.newLabel();
        theStatusField = myControls.newLabel();
        theTaskProgress = myControls.newProgressBar();
        theStageProgress = myControls.newProgressBar();

        /* Create buttons */
        final TethysUIButtonFactory<?> myButtons = theGuiFactory.buttonFactory();
        final TethysUIButton myCancelButton = myButtons.newButton();
        myCancelButton.setTextOnly();
        myCancelButton.setText(NLS_CANCEL);
        final TethysUIButton myClearButton = myButtons.newButton();
        myClearButton.setTextOnly();
        myClearButton.setText(NLS_CLEAR);

        /* Create the status pane */
        final TethysUIPaneFactory myPanes = theGuiFactory.paneFactory();
        final TethysUIBorderPaneManager myStatusNode = myPanes.newBorderPane();
        myStatusNode.setHGap(GAP_WIDTH);
        myStatusNode.setCentre(theStatusField);
        myStatusNode.setWest(myClearButton);
        myStatusNode.setBorderTitle(NLS_STATUS);

        /* Create the task progress pane */
        final TethysUIBorderPaneManager myTaskProgress = myPanes.newBorderPane();
        myTaskProgress.setHGap(GAP_WIDTH);
        myTaskProgress.setCentre(theTaskProgress);
        myTaskProgress.setWest(theTaskField);

        /* Create the stage progress pane */
        theStagePanel = myPanes.newBorderPane();
        theStagePanel.setHGap(GAP_WIDTH);
        theStagePanel.setCentre(theStageProgress);
        theStagePanel.setWest(theStageField);

        /* Create the stage progress pane */
        final TethysUIGridPaneManager myProgressGrid = myPanes.newGridPane();
        myProgressGrid.setHGap(GAP_WIDTH);
        myProgressGrid.addCell(myTaskProgress);
        myProgressGrid.allowCellGrowth(myTaskProgress);
        myProgressGrid.addCell(theStagePanel);
        myProgressGrid.allowCellGrowth(theStagePanel);

        /* Create the progress pane */
        final TethysUIBorderPaneManager myProgressNode = myPanes.newBorderPane();
        myProgressNode.setHGap(GAP_WIDTH);
        myProgressNode.setCentre(myProgressGrid);
        myProgressNode.setEast(myCancelButton);
        myProgressNode.setBorderTitle(NLS_PROGRESS);

        /* Create the basic Pane */
        theNode = myPanes.newCardPane();
        theNode.addCard(PANEL_STATUS, myStatusNode);
        theNode.addCard(PANEL_PROGRESS, myProgressNode);

        /* Create listeners */
        myCancelButton.getEventRegistrar().addEventListener(e -> handleCancel());
        myClearButton.getEventRegistrar().addEventListener(e -> handleClear());
    }

    @Override
    public Integer getId() {
        return theNode.getId();
    }

    @Override
    public TethysUINode getNode() {
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
    protected TethysUIThreadManager getThreadManager() {
        return theThreadManager;
    }

    /**
     * Obtain the GUI factory.
     * @return the factory
     */
    protected TethysUICoreFactory<?> getGuiFactory() {
        return theGuiFactory;
    }

    @Override
    public void setProgress(final TethysUIThreadStatus pStatus) {
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
