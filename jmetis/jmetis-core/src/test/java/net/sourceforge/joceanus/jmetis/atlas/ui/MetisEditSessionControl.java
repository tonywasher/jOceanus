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
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.ui.MetisUIEvent;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Edit Session Control.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class MetisEditSessionControl<N, I>
        implements TethysNode<N>, TethysEventProvider<MetisUIEvent> {
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
     * The data panel.
     */
    private final MetisDataPanel theDataPanel;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisUIEvent> theEventManager;

    /**
     * The Edit Session.
     */
    private final MetisListEditSession theSession;

    /**
     * The Selected items.
     */
    private final Map<MetisListKey, MetisFieldVersionedItem> theSelectedMap;

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
     * Constructor.
     * @param pFactory the GUI factory
     * @param pSession the session
     */
    public MetisEditSessionControl(final TethysGuiFactory<N, I> pFactory,
                                   final MetisListEditSession pSession) {
        /* Record the session */
        theSession = pSession;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the selected Map */
        theSelectedMap = new HashMap<>();

        /* Create the panel */
        theCard = pFactory.newCardPane();

        /* Create Data panel */
        theDataPanel = new MetisDataPanel(pFactory);
        theCard.addCard(EditCard.DATA.toString(), theDataPanel);

        /* Create Edit panel */
        theEditPanel = new MetisEditPanel(pFactory);
        theCard.addCard(EditCard.EDIT.toString(), theEditPanel);

        /* Create the error panel */
        theErrorPanel = null;
        theCard.addCard(EditCard.ERROR.toString(), theErrorPanel);

        /* Buttons are initially disabled */
        setEnabled(false);

        /* Add the listeners for the session */
        theSession.getEventRegistrar().addEventListener(e -> flagError());
    }

    @Override
    public Integer getId() {
        return theCard.getId();
    }

    @Override
    public N getNode() {
        return theCard.getNode();
    }

    @Override
    public TethysEventRegistrar<MetisUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theCard.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        theDataPanel.setEnabled(bEnabled);
        theEditPanel.setEnabled(bEnabled);
    }

    /**
     * Do we have an error?
     * @return true/false
     */
    public boolean hasError() {
        return hasError;
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
     * Select dataMode.
     */
    void selectDataMode() {
        isEditing = false;
        if (!hasError) {
            theCard.selectCard(EditCard.DATA.toString());
        }
    }

    /**
     * Select editMode.
     */
    void selectEditMode() {
        isEditing = true;
        if (!hasError) {
            theCard.selectCard(EditCard.EDIT.toString());
        }
    }

    /**
     * Set the error mode.
     */
    void setErrorMode() {
        hasError = true;
        theCard.selectCard(EditCard.ERROR.toString());
    }

    /**
     * Clear the error mode.
     */
    void clearErrorMode() {
        hasError = false;
        EditCard myCard = isEditing
                                    ? EditCard.EDIT
                                    : EditCard.DATA;
        theCard.selectCard(myCard.EDIT.toString());
    }

    /**
     * Flag the error.
     */
    private void flagError() {
        theErrorPanel.setError(theSession.getError());
    }

    /**
     * Edit Session Control.
     */
    private class MetisEditPanel
            implements TethysNode<N> {
        /**
         * The panel.
         */
        private final TethysBoxPaneManager<N, I> thePanel;

        /**
         * The Commit button.
         */
        private final TethysButton<N, I> theCommitButton;

        /**
         * The Undo button.
         */
        private final TethysButton<N, I> theUndoButton;

        /**
         * The Reset button.
         */
        private final TethysButton<N, I> theResetButton;

        /**
         * The Cancel button.
         */
        private final TethysButton<N, I> theCancelButton;

        /**
         * The New button.
         */
        private final TethysButton<N, I> theNewButton;

        /**
         * The Delete button.
         */
        private final TethysButton<N, I> theDeleteButton;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pSession the session
         * @param pListKey the listKey
         */
        public MetisEditPanel(final TethysGuiFactory<N, I> pFactory) {
            /* Create the buttons */
            theCommitButton = pFactory.newButton();
            theUndoButton = pFactory.newButton();
            theResetButton = pFactory.newButton();
            theCancelButton = pFactory.newButton();
            theNewButton = pFactory.newButton();
            theDeleteButton = pFactory.newButton();

            /* Configure the buttons */
            MetisIcon.configureCommitIconButton(theCommitButton);
            MetisIcon.configureUndoIconButton(theUndoButton);
            MetisIcon.configureResetIconButton(theResetButton);
            MetisIcon.configureCancelIconButton(theCancelButton);
            MetisIcon.configureNewIconButton(theNewButton);
            MetisIcon.configureDeleteIconButton(theDeleteButton);

            /* Create the panel */
            thePanel = pFactory.newHBoxPane();
            thePanel.setBorderPadding(5);
            thePanel.setBorderTitle("Edit Session Control");

            /* Create the layout */
            thePanel.addNode(theCommitButton);
            thePanel.addNode(theUndoButton);
            thePanel.addNode(theResetButton);
            thePanel.addNode(theCancelButton);
            thePanel.addNode(theNewButton);
            thePanel.addNode(theDeleteButton);

            /* Add the listeners for the buttons */
            theCommitButton.getEventRegistrar().addEventListener(e -> commitEditSession());
            theUndoButton.getEventRegistrar().addEventListener(e -> undoLastUpdate());
            theResetButton.getEventRegistrar().addEventListener(e -> resetAllChanges());
            theCancelButton.getEventRegistrar().addEventListener(e -> cancelEditSession());
            theNewButton.getEventRegistrar().addEventListener(e -> createNewItem());
            theDeleteButton.getEventRegistrar().addEventListener(e -> deleteSelectedItem());

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
                theCommitButton.setEnabled(false);
                theUndoButton.setEnabled(false);
                theResetButton.setEnabled(false);
                theCancelButton.setEnabled(false);
                theNewButton.setEnabled(false);
                theDeleteButton.setEnabled(false);

                /* Else look at the edit state */
            } else {
                /* Determine whether we have changes */
                boolean hasUpdates = theSession.activeSession();
                theUndoButton.setEnabled(hasUpdates);
                theResetButton.setEnabled(hasUpdates);
                theCommitButton.setEnabled(hasUpdates);

                /* Enable the new and cancel button */
                theCancelButton.setEnabled(true);
                theNewButton.setEnabled(true);
                theDeleteButton.setEnabled(theSelectedItem != null);
            }
        }

        /**
         * Commit the edit Session.
         */
        private void commitEditSession() {
            theSession.commitEditSession();
            selectDataMode();
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
            selectDataMode();
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
     * Data Session Control.
     */
    private class MetisDataPanel
            implements TethysNode<N> {
        /**
         * The panel.
         */
        private final TethysBoxPaneManager<N, I> thePanel;

        /**
         * The Edit button.
         */
        private final TethysButton<N, I> theEditButton;

        /**
         * The Undo button.
         */
        private final TethysButton<N, I> theUndoButton;

        /**
         * The Reset button.
         */
        private final TethysButton<N, I> theResetButton;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pSession the session
         * @param pListKey the listKey
         */
        public MetisDataPanel(final TethysGuiFactory<N, I> pFactory) {
            /* Create the buttons */
            theEditButton = pFactory.newButton();
            theUndoButton = pFactory.newButton();
            theResetButton = pFactory.newButton();

            /* Configure the buttons */
            MetisIcon.configureEditIconButton(theEditButton);
            MetisIcon.configureUndoIconButton(theUndoButton);
            MetisIcon.configureResetIconButton(theResetButton);

            /* Create the panel */
            thePanel = pFactory.newHBoxPane();
            thePanel.setBorderPadding(5);
            thePanel.setBorderTitle("Data Session Control");

            /* Create the layout */
            thePanel.addNode(theEditButton);
            thePanel.addNode(theUndoButton);
            thePanel.addNode(theResetButton);

            /* Add the listeners for the buttons */
            theEditButton.getEventRegistrar().addEventListener(e -> startEditSession());
            theUndoButton.getEventRegistrar().addEventListener(e -> undoLastBaseUpdate());
            theResetButton.getEventRegistrar().addEventListener(e -> resetAllBaseChanges());

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
                theEditButton.setEnabled(false);
                theUndoButton.setEnabled(false);
                theResetButton.setEnabled(false);

                /* Else look at the edit state */
            } else {
                /* Determine whether we have changes */
                boolean hasUpdates = theSession.activeBaseSession();
                theUndoButton.setEnabled(hasUpdates);
                theResetButton.setEnabled(hasUpdates);

                /* Enable the new and cancel button */
                theEditButton.setEnabled(true);
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

            /* Add the listener for item changes */
            theClearButton.getEventRegistrar().addEventListener(e -> clearErrors());

            /* Create the error panel */
            thePanel = pFactory.newHBoxPane();
            thePanel.setBorderTitle("Error Detail");

            /* Define the layout */
            thePanel.addNode(theClearButton);
            thePanel.addNode(theErrorField);

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
         * Data.
         */
        DATA,

        /**
         * Edit.
         */
        EDIT;
    }
}
