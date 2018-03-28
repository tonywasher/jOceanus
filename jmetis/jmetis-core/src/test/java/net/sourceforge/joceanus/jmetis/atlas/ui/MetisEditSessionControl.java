/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.ui;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jmetis.list.MetisListEditSession;
import net.sourceforge.joceanus.jmetis.list.MetisListKey;
import net.sourceforge.joceanus.jmetis.list.MetisListSetVersioned;
import net.sourceforge.joceanus.jmetis.list.MetisListUpdateManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.ui.MetisUIEvent;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysToolBarManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Edit Session Control.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class MetisEditSessionControl<N, I>
        implements TethysNode<N>, TethysEventProvider<MetisUIEvent> {
    /**
     * The overall pane.
     */
    private final TethysBorderPaneManager<N, I> thePane;

    /**
     * The data panel.
     */
    private final TethysNode<N> theDataPanel;

    /**
     * The card panel.
     */
    private final TethysCardPaneManager<N, I, TethysNode<N>> theCard;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theErrorPanel;

    /**
     * The edit panel.
     */
    private final MetisEditPanel theEditPanel;

    /**
     * The browse panel.
     */
    private final MetisBrowsePanel theBrowsePanel;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisUIEvent> theEventManager;

    /**
     * The Edit Session.
     */
    private final MetisListEditSession theSession;

    /**
     * The UpdateSet.
     */
    private final MetisListSetVersioned theUpdateSet;

    /**
     * The Selected items.
     */
    private final Map<MetisListKey, MetisFieldVersionedItem> theSelectedMap;

    /**
     * The viewer window.
     */
    private final MetisViewerWindow<N, I> theViewer;

    /**
     * The ListKey.
     */
    private MetisListKey theListKey;

    /**
     * The Selected row.
     */
    private MetisFieldVersionedItem theSelectedItem;

    /**
     * Do we have an error?
     */
    private boolean hasError;

    /**
     * Are we editing?
     */
    private boolean isEditing;

    /**
     * Are we enabled?
     */
    private boolean isEnabled;

    /**
     * Are we showing the viewer?
     */
    private boolean viewerShowing;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @param pSession the session
     * @param pUpdateSet the updateSet
     * @param pDataPanel the dataPanel
     * @throws OceanusException on error
     */
    public MetisEditSessionControl(final MetisToolkit<N, I> pToolkit,
                                   final MetisListEditSession pSession,
                                   final MetisListSetVersioned pUpdateSet,
                                   final TethysNode<N> pDataPanel) throws OceanusException {
        /* Record the parameters */
        theSession = pSession;
        theUpdateSet = pUpdateSet;
        theDataPanel = pDataPanel;

        /* Access the GUI factory */
        TethysGuiFactory<N, I> myFactory = pToolkit.getGuiFactory();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the selected Map */
        theSelectedMap = new HashMap<>();

        /* Create the panel */
        theCard = myFactory.newCardPane();

        /* Create Browse panel */
        theBrowsePanel = new MetisBrowsePanel(myFactory);
        theCard.addCard(EditCard.BROWSE.toString(), theBrowsePanel);

        /* Create Edit panel */
        theEditPanel = new MetisEditPanel(myFactory);
        theCard.addCard(EditCard.EDIT.toString(), theEditPanel);

        /* Create the error panel */
        theErrorPanel = new MetisErrorPanel(myFactory);
        theCard.addCard(EditCard.ERROR.toString(), theErrorPanel);

        /* Create the pane */
        thePane = myFactory.newBorderPane();
        thePane.setCentre(theDataPanel);
        thePane.setNorth(theCard);

        /* Buttons are initially disabled */
        setEnabled(true);

        /* Add the listeners for the session */
        theSession.getEventRegistrar().addEventListener(e -> flagError());

        /* Create the viewer window */
        theViewer = pToolkit.newViewerWindow();
        theViewer.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> clearViewer());
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
    public TethysEventRegistrar<MetisUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePane.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        isEnabled = bEnabled;
        if (hasError()) {
            theErrorPanel.setEnabled(bEnabled);
            theDataPanel.setEnabled(false);
        } else {
            theDataPanel.setEnabled(bEnabled);
            theBrowsePanel.setEnabled(bEnabled);
            theEditPanel.setEnabled(bEnabled);
        }
    }

    /**
     * Do we have an error?
     * @return true/false
     */
    public boolean hasError() {
        return hasError;
    }

    /**
     * Are we editing?
     * @return true/false
     */
    public boolean isEditing() {
        return isEditing
               && !hasError;
    }

    /**
     * Set the activeKey.
     * @param pListKey the listKey
     */
    public void setActiveKey(final MetisListKey pListKey) {
        theListKey = pListKey;
        theSelectedItem = theSelectedMap.get(theListKey);
        setEnabled(true);
    }

    /**
     * Set the selected item.
     * @param pListKey the listKey
     * @param pItem the selected item
     */
    public void setSelectedItem(final MetisListKey pListKey,
                                final MetisFieldVersionedItem pItem) {
        theSelectedMap.put(pListKey, pItem);
        if (pListKey.equals(theListKey)) {
            theSelectedItem = pItem;
            setEnabled(true);
        }
    }

    /**
     * Select browseMode.
     */
    void selectBrowseMode() {
        isEditing = false;
        if (!hasError) {
            theCard.selectCard(EditCard.BROWSE.toString());
            setEnabled(isEnabled);
            theEventManager.fireEvent(MetisUIEvent.VISIBILITY);
        }
    }

    /**
     * Select editMode.
     */
    void selectEditMode() {
        isEditing = true;
        if (!hasError) {
            theCard.selectCard(EditCard.EDIT.toString());
            setEnabled(isEnabled);
            theEventManager.fireEvent(MetisUIEvent.VISIBILITY);
        }
    }

    /**
     * Set the error mode.
     */
    void setErrorMode() {
        hasError = true;
        theCard.selectCard(EditCard.ERROR.toString());
        setEnabled(isEnabled);
        theEventManager.fireEvent(MetisUIEvent.VISIBILITY);
    }

    /**
     * Clear the error mode.
     */
    void clearErrorMode() {
        hasError = false;
        final EditCard myCard = isEditing
                                          ? EditCard.EDIT
                                          : EditCard.BROWSE;
        theCard.selectCard(myCard.toString());
        setEnabled(isEnabled);
        theEventManager.fireEvent(MetisUIEvent.VISIBILITY);
    }

    /**
     * Flag the error.
     */
    private void flagError() {
        theErrorPanel.setError(theSession.getError());
    }

    /**
     * Clear the viewer.
     */
    private void showViewer() {
        /* If we are not currently showing */
        if (!viewerShowing) {
            /* Show the dialog */
            theViewer.showDialog();
            viewerShowing = true;
            setEnabled(isEnabled);
        }
    }

    /**
     * Clear the viewer.
     */
    private void clearViewer() {
        viewerShowing = false;
        setEnabled(isEnabled);
    }

    /**
     * Save updates to file.
     */
    private void saveToFile() {
        while (!theUpdateSet.isEmpty()) {
            MetisListUpdateManager.commitUpdateBatch(theUpdateSet, 50);
        }
    }

    /**
     * Edit Session Control.
     */
    private class MetisEditPanel
            implements TethysNode<N> {
        /**
         * The panel.
         */
        private final TethysToolBarManager<N, I> thePanel;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pSession the session
         * @param pListKey the listKey
         */
        public MetisEditPanel(final TethysGuiFactory<N, I> pFactory) {
            /* Create the toolBar Manager */
            thePanel = pFactory.newToolBar();
            thePanel.setIconWidth(MetisIcon.ICON_SIZE);

            /* Create the buttons */
            thePanel.newIcon(MetisIcon.COMMIT, MetisIcon.TIP_COMMIT, e -> commitEditSession());
            thePanel.newIcon(MetisIcon.UNDO, MetisIcon.TIP_UNDO, e -> undoLastUpdate());
            thePanel.newIcon(MetisIcon.RESET, MetisIcon.TIP_RESET, e -> resetAllChanges());
            thePanel.newIcon(MetisIcon.CANCEL, MetisIcon.TIP_CANCEL, e -> cancelEditSession());
            thePanel.newSeparator();
            thePanel.newIcon(MetisIcon.NEW, MetisIcon.TIP_NEW, e -> createNewItem());
            thePanel.newIcon(MetisIcon.DELETE, MetisIcon.TIP_DELETE, e -> deleteSelectedItem());
            thePanel.newSeparator();
            thePanel.newIcon(MetisIcon.VIEWER, MetisIcon.TIP_VIEWER, e -> showViewer());

            /* Buttons are initially disabled */
            setEnabled(false);
        }

        @Override
        public Integer getId() {
            return thePanel.getId();
        }

        @Override
        public N getNode() {
            return thePanel.getNode();
        }

        @Override
        public void setVisible(final boolean pVisible) {
            thePanel.setVisible(pVisible);
        }

        @Override
        public void setEnabled(final boolean bEnabled) {
            /* If the table is locked clear the buttons */
            if (!bEnabled) {
                thePanel.setEnabled(MetisIcon.COMMIT, false);
                thePanel.setEnabled(MetisIcon.UNDO, false);
                thePanel.setEnabled(MetisIcon.RESET, false);
                thePanel.setEnabled(MetisIcon.CANCEL, false);
                thePanel.setEnabled(MetisIcon.NEW, false);
                thePanel.setEnabled(MetisIcon.DELETE, false);
                thePanel.setEnabled(MetisIcon.VIEWER, false);

                /* Else look at the edit state */
            } else {
                /* Determine whether we have changes */
                boolean hasUpdates = theSession.activeSession();
                thePanel.setEnabled(MetisIcon.UNDO, hasUpdates);
                thePanel.setEnabled(MetisIcon.RESET, hasUpdates);
                thePanel.setEnabled(MetisIcon.COMMIT, hasUpdates);

                /* Enable the new and cancel button */
                thePanel.setEnabled(MetisIcon.CANCEL, true);
                thePanel.setEnabled(MetisIcon.NEW, true);
                thePanel.setEnabled(MetisIcon.DELETE, theSelectedItem != null);

                /* Show the viewer button */
                thePanel.setEnabled(MetisIcon.VIEWER, !viewerShowing);
            }
        }

        /**
         * Commit the edit Session.
         */
        private void commitEditSession() {
            theSession.commitEditSession();
            selectBrowseMode();
        }

        /**
         * Undo the last update.
         */
        private void undoLastUpdate() {
            theSession.undoLastChange();
        }

        /**
         * Reset all changes.
         */
        private void resetAllChanges() {
            theSession.reset();
        }

        /**
         * Cancel the edit session.
         */
        private void cancelEditSession() {
            resetAllChanges();
            selectBrowseMode();
        }

        /**
         * Create new item.
         */
        private void createNewItem() {
            MetisFieldVersionedItem myItem = theSession.createNewItem(theListKey);
            if (myItem != null) {
                theEventManager.fireEvent(MetisUIEvent.NEWITEM, myItem);
            }
        }

        /**
         * Delete the selected item.
         */
        private void deleteSelectedItem() {
            theSession.deleteItem(theSelectedItem);
        }
    }

    /**
     * Browse Session Control.
     */
    private class MetisBrowsePanel
            implements TethysNode<N> {
        /**
         * The panel.
         */
        private final TethysToolBarManager<N, I> thePanel;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pSession the session
         * @param pListKey the listKey
         */
        public MetisBrowsePanel(final TethysGuiFactory<N, I> pFactory) {
            /* Create the toolBar Manager */
            thePanel = pFactory.newToolBar();
            thePanel.setIconWidth(MetisIcon.ICON_SIZE);

            /* Create the buttons */
            thePanel.newIcon(MetisIcon.EDIT, MetisIcon.TIP_EDIT, e -> startEditSession());
            thePanel.newSeparator();
            thePanel.newIcon(MetisIcon.UNDO, MetisIcon.TIP_UNDO, e -> undoLastBaseUpdate());
            thePanel.newIcon(MetisIcon.RESET, MetisIcon.TIP_RESET, e -> resetAllBaseChanges());
            thePanel.newSeparator();
            thePanel.newIcon(MetisIcon.SAVE, MetisIcon.TIP_SAVE, e -> saveToFile());
            thePanel.newIcon(MetisIcon.VIEWER, MetisIcon.TIP_VIEWER, e -> showViewer());

            /* Buttons are initially disabled */
            setEnabled(false);
        }

        @Override
        public Integer getId() {
            return thePanel.getId();
        }

        @Override
        public N getNode() {
            return thePanel.getNode();
        }

        @Override
        public void setVisible(final boolean pVisible) {
            thePanel.setVisible(pVisible);
        }

        @Override
        public void setEnabled(final boolean bEnabled) {
            /* If the table is locked clear the buttons */
            if (!bEnabled) {
                thePanel.setEnabled(MetisIcon.EDIT, false);
                thePanel.setEnabled(MetisIcon.UNDO, false);
                thePanel.setEnabled(MetisIcon.RESET, false);
                thePanel.setEnabled(MetisIcon.SAVE, false);
                thePanel.setEnabled(MetisIcon.VIEWER, false);

                /* Else look at the edit state */
            } else {
                /* Determine whether we have changes */
                boolean hasUpdates = theSession.activeBaseSession();
                thePanel.setEnabled(MetisIcon.UNDO, hasUpdates);
                thePanel.setEnabled(MetisIcon.RESET, hasUpdates);
                thePanel.setEnabled(MetisIcon.SAVE, hasUpdates);

                /* Enable the edit button */
                thePanel.setEnabled(MetisIcon.EDIT, true);

                /* Show the viewer button */
                thePanel.setEnabled(MetisIcon.VIEWER, !viewerShowing);
            }
        }

        /**
         * Start the edit Session.
         */
        private void startEditSession() {
            selectEditMode();
        }

        /**
         * Undo the last update.
         */
        private void undoLastBaseUpdate() {
            theSession.undoLastBaseChange();
        }

        /**
         * Reset all changes.
         */
        private void resetAllBaseChanges() {
            theSession.resetBase();
        }
    }

    /**
     * Error panel.
     */
    private class MetisErrorPanel
            implements TethysNode<N> {
        /**
         * The Panel.
         */
        private final TethysBoxPaneManager<N, I> thePanel;

        /**
         * The error field.
         */
        private final TethysLabel<N, I> theErrorField;

        /**
         * The clear button.
         */
        private final TethysButton<N, I> theClearButton;

        /**
         * The Viewer button.
         */
        private final TethysButton<N, I> theViewerButton;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        public MetisErrorPanel(final TethysGuiFactory<N, I> pFactory) {
            /* Create the error field */
            theErrorField = pFactory.newLabel();
            theErrorField.setErrorText();

            /* Create the clear button */
            theClearButton = pFactory.newButton();
            theClearButton.setTextOnly();
            theClearButton.setText("Clear");

            /* Create the viewer button */
            theViewerButton = pFactory.newButton();
            MetisIcon.configureViewerIconButton(theViewerButton);

            /* Add the listener for item changes */
            theClearButton.getEventRegistrar().addEventListener(e -> clearErrors());
            theViewerButton.getEventRegistrar().addEventListener(e -> showViewer());

            /* Create the error panel */
            thePanel = pFactory.newHBoxPane();
            thePanel.setBorderTitle("Error Detail");

            /* Define the layout */
            thePanel.addNode(theClearButton);
            thePanel.addNode(theErrorField);
            thePanel.addNode(theViewerButton);

            /* Set the Error panel to be red and invisible */
            thePanel.setVisible(false);
        }

        @Override
        public Integer getId() {
            return thePanel.getId();
        }

        @Override
        public N getNode() {
            return thePanel.getNode();
        }

        @Override
        public void setVisible(final boolean bVisible) {
            thePanel.setVisible(bVisible);
        }

        @Override
        public void setEnabled(final boolean bEnabled) {
            /* Pass on to important elements */
            theClearButton.setEnabled(bEnabled);

            /* Show the viewer button */
            theViewerButton.setEnabled(bEnabled && !viewerShowing);
        }

        /**
         * Set error indication for window.
         * @param pException the exception
         */
        void setError(final OceanusException pException) {
            /* Set the error text and display the panel */
            setErrorText(pException.getMessage());
            setErrorMode();

            /* Notify listeners */
            theEventManager.fireEvent(MetisUIEvent.VISIBILITY);
        }

        /**
         * Set error text for window.
         * @param pText the text
         */
        private void setErrorText(final String pText) {
            /* Set the string for the error field */
            theErrorField.setText(pText);

            /* Make the panel visible */
            thePanel.setVisible(true);
        }

        /**
         * Clear error indication for this window.
         */
        private void clearErrors() {
            /* Clear the error indication */
            clearErrorMode();

            /* Notify listeners */
            theEventManager.fireEvent(MetisUIEvent.VISIBILITY);
        }
    }

    /**
     * The card panels.
     */
    private enum EditCard {
        /**
         * Error.
         */
        ERROR,

        /**
         * Browse.
         */
        BROWSE,

        /**
         * Edit.
         */
        EDIT;
    }
}
