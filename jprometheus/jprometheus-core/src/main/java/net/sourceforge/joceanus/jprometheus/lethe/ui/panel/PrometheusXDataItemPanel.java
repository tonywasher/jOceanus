/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.ui.panel;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusListKeyX;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSet;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusTableItemX;
import net.sourceforge.joceanus.jprometheus.atlas.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.atlas.ui.PrometheusItemActions;
import net.sourceforge.joceanus.jprometheus.atlas.ui.PrometheusItemEditActions;
import net.sourceforge.joceanus.jprometheus.atlas.ui.PrometheusItemEditActions.PrometheusItemEditParent;
import net.sourceforge.joceanus.jprometheus.atlas.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIGenericWrapper;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;

/**
 * Class to enable display/editing of and individual dataItem.
 * @param <T> the item type
 * @param <G> the goto id type
 */
public abstract class PrometheusXDataItemPanel<T extends PrometheusTableItemX & Comparable<? super T>, G extends Enum<G>>
        implements TethysEventProvider<PrometheusDataEvent>, TethysUIComponent, PrometheusItemEditParent {
    /**
     * Details Tab Title.
     */
    protected static final String TAB_DETAILS = PrometheusUIResource.PANEL_TAB_DETAILS.getValue();

    /**
     * Account Tab Title.
     */
    protected static final String TAB_ACCOUNT = PrometheusUIResource.PANEL_TAB_ACCOUNT.getValue();

    /**
     * Web Tab Title.
     */
    protected static final String TAB_WEB = PrometheusUIResource.PANEL_TAB_WEB.getValue();

    /**
     * Notes Tab Title.
     */
    protected static final String TAB_NOTES = PrometheusUIResource.PANEL_TAB_NOTES.getValue();

    /**
     * ReadOnly EditVersion.
     */
    protected static final int VERSION_READONLY = -1;

    /**
     * The Panel.
     */
    private final TethysUIFactory<?> theFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The Panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The DataFormatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * The Field Set.
     */
    private final PrometheusXFieldSet<T> theFieldSet;

    /**
     * The Update Set.
     */
    private final UpdateSet theUpdateSet;

    /**
     * The ErrorPanel.
     */
    private final MetisErrorPanel theError;

    /**
     * The MainPanel.
     */
    private final TethysUIComponent theMainPanel;

    /**
     * The Item Actions.
     */
    private final PrometheusItemActions<G> theItemActions;

    /**
     * The Item Actions.
     */
    private final PrometheusItemEditActions theEditActions;

    /**
     * The Item.
     */
    private T theItem;

    /**
     * The New Item.
     */
    private T theSelectedItem;

    /**
     * The EditVersion.
     */
    private int theEditVersion = VERSION_READONLY;

    /**
     * The BaseValues.
     */
    private MetisValueSet theBaseValues;

    /**
     * Is this a new item.
     */
    private boolean isNew;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    @SuppressWarnings("unchecked")
    protected PrometheusXDataItemPanel(final TethysUIFactory<?> pFactory,
                                       final UpdateSet pUpdateSet,
                                       final MetisErrorPanel pError) {
        /* Store parameters */
        theFactory = pFactory;
        theUpdateSet = pUpdateSet;
        theError = pError;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Access the formatter */
        theFormatter = pFactory.getDataFormatter();

        /* Create the New FieldSet */
        theFieldSet = new PrometheusXFieldSet<>(pFactory);

        /* Create the main panel */
        thePanel = pFactory.paneFactory().newBorderPane();
        theMainPanel = theFieldSet.getComponent();

        /* create the action panels */
        theItemActions = new PrometheusItemActions<>(pFactory, this);
        theEditActions = new PrometheusItemEditActions(pFactory, this);

        /* Create listener */
        theUpdateSet.getEventRegistrar().addEventListener(e -> refreshAfterUpdate());
        theFieldSet.getEventRegistrar().addEventListener(e -> updateItem(e.getDetails(PrometheusXFieldSetEvent.class)));

        /* Layout the panel */
        thePanel.setWest(theItemActions);
        thePanel.setCentre(theMainPanel);
        thePanel.setEast(theEditActions);

        /* Set visibility */
        thePanel.setVisible(false);

        /* Listen to the EditActions */
        TethysEventRegistrar<PrometheusUIEvent> myRegistrar = theEditActions.getEventRegistrar();
        myRegistrar.addEventListener(PrometheusUIEvent.OK, e -> requestCommit());
        myRegistrar.addEventListener(PrometheusUIEvent.UNDO, e -> requestUndo());
        myRegistrar.addEventListener(PrometheusUIEvent.RESET, e -> requestReset());
        myRegistrar.addEventListener(PrometheusUIEvent.CANCEL, e -> requestCancel());

        /* Listen to the Actions */
        myRegistrar = theItemActions.getEventRegistrar();
        myRegistrar.addEventListener(PrometheusUIEvent.BUILDGOTO, e -> buildGoToMenu(e.getDetails(TethysUIScrollMenu.class)));
        myRegistrar.addEventListener(PrometheusUIEvent.GOTO, e -> processGoToRequest(e.getDetails(PrometheusGoToEvent.class)));
        myRegistrar.addEventListener(PrometheusUIEvent.EDIT, e -> requestEdit());
        myRegistrar.addEventListener(PrometheusUIEvent.DELETE, e -> requestDelete());
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected TethysUIFactory<?> getFactory() {
        return theFactory;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theItemActions.setEnabled(pEnabled);
        theMainPanel.setEnabled(pEnabled);
        theEditActions.setEnabled(pEnabled);
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    protected TethysUIDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the field Set.
     * @return the FieldSet
     */
    protected PrometheusXFieldSet<T> getFieldSet() {
        return theFieldSet;
    }

    /**
     * Obtain the Update Set.
     * @return the UpdateSet
     */
    protected UpdateSet getUpdateSet() {
        return theUpdateSet;
    }

    /**
     * Obtain the item.
     * @return the item
     */
    protected T getItem() {
        return theItem;
    }

    /**
     * Obtain the selected item.
     * @return the selected item
     */
    public T getSelectedItem() {
        return theSelectedItem;
    }

    /**
     * Obtain the edit version.
     * @return the edit version
     */
    protected int getEditVersion() {
        return theEditVersion;
    }

    /**
     * Is the active item deleted.
     * @return true/false
     */
    public boolean isItemDeleted() {
        return theItem != null
                && theItem.isDeleted();
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * Obtain the base Values.
     * @return the values
     */
    protected MetisValueSet getBaseValues() {
        return theBaseValues;
    }

    /**
     * Set editable item.
     * @param isEditable true/false
     */
    public void setEditable(final boolean isEditable) {
        /* If we have an item */
        if (theItem != null) {
            /* Determine EditVersion */
            if (isEditable) {
                if (!isEditing()) {
                    theEditVersion = theUpdateSet.getVersion();
                    theBaseValues = theItem.getValueSet();
                }
            } else {
                theEditVersion = VERSION_READONLY;
                theBaseValues = null;
            }

            /* adjust fields */
            thePanel.setVisible(true);
            theFieldSet.setEditable(isEditable);
            adjustFields(isEditable);

            /* Set panel visibility */
            theItemActions.setVisible(!isEditable);
            theEditActions.setVisible(isEditable);

            /* Render the FieldSet */
            theFieldSet.setItem(theItem);
            theFieldSet.adjustTabVisibility();

            /* ensure that the actions are updated */
            updateActions();

        } else {
            /* Set EditVersion */
            theEditVersion = VERSION_READONLY;
            theBaseValues = null;
            isNew = false;

            /* Set visibility */
            thePanel.setVisible(false);
        }
    }

    /**
     * update the actions.
     */
    protected void updateActions() {
        theEditActions.setEnabled(true);
        theItemActions.setEnabled(true);
    }

    /**
     * Set readOnly item.
     * @param pItem the item
     */
    public void setItem(final T pItem) {
        /* If we are not editing or the item is non-null */
        if (pItem != null || !isEditing()) {
            /* Store the element */
            theItem = pItem;
            theSelectedItem = pItem;

            /* Set readOnly */
            setEditable(false);
        }
    }

    /**
     * Set new item.
     * @param pItem the item
     */
    public void setNewItem(final T pItem) {
        /* Store the element */
        theSelectedItem = theItem;
        theItem = pItem;
        isNew = true;

        /* Set editable */
        setEditable(true);
        theUpdateSet.setEditing(Boolean.TRUE);

        /* Note status has changed */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean isDeletable() {
        return theItem != null
                && !theItem.isActive();
    }

    /**
     * Refresh data.
     */
    public abstract void refreshData();

    /**
     * Adjust Editable Fields.
     * @param isEditable is the item editable?
     */
    protected abstract void adjustFields(boolean isEditable);

    /**
     * Update the field.
     * @param pUpdate the update
     * @throws OceanusException on error
     */
    protected abstract void updateField(PrometheusXFieldSetEvent pUpdate) throws OceanusException;

    /**
     * Obtain the list for a class in base updateSet.
     * @param <L> the list type
     * @param <X> the object type
     * @param pDataType the data type
     * @param pClass the list class
     * @return the list
     */
    public <L extends DataList<X>, X extends DataItem> L getDataList(final PrometheusListKeyX pDataType,
                                                                     final Class<L> pClass) {
        /* Look up the base list */
        return theUpdateSet.getDataList(pDataType, pClass);
    }

    /**
     * Are we editing?
     * @return true/false
     */
    public boolean isEditing() {
        return theEditVersion != VERSION_READONLY;
    }

    @Override
    public boolean hasUpdates() {
        return isEditing()
                && theEditVersion < theUpdateSet.getVersion();
    }

    @Override
    public boolean hasErrors() {
        return theUpdateSet.hasErrors();
    }

    /**
     * Refresh the item after an updateSet reWind.
     */
    protected void refreshAfterUpdate() {
        setEditable(isEditing());
    }

    /**
     * Request cancel.
     */
    private void requestCancel() {
        /* If we have any updates */
        if (isNew) {
            /* Rewind any changes to before the new item */
            theUpdateSet.processEditCommand(PrometheusUIEvent.REWIND, theEditVersion - 1, theError);
            theItem = null;
        } else if (hasUpdates()) {
            /* Rewind any changes that have been made */
            theUpdateSet.processEditCommand(PrometheusUIEvent.REWIND, theEditVersion, theError);
        }

        /* Stop element being editable */
        setEditable(false);
        theUpdateSet.setEditing(Boolean.FALSE);

        /* Note status has changed */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Request reset.
     */
    private void requestReset() {
        /* If we have any updates */
        if (hasUpdates()) {
            /* Rewind any changes that have been made */
            theUpdateSet.processEditCommand(PrometheusUIEvent.REWIND, theEditVersion, theError);
        }
    }

    /**
     * Request cancel.
     */
    private void requestUndo() {
        /* If we have any updates */
        if (hasUpdates()) {
            /* Undo the last change */
            theUpdateSet.processEditCommand(PrometheusUIEvent.UNDO, VERSION_READONLY, theError);
        }
    }

    /**
     * Request commit.
     */
    private void requestCommit() {
        /* Allow analysis */
        theUpdateSet.setEditing(Boolean.FALSE);

        /* If we have any updates */
        if (isNew || hasUpdates()) {
            /* Condense history to a single update */
            theUpdateSet.processEditCommand(PrometheusUIEvent.OK, isNew
                    ? theEditVersion
                    : theEditVersion + 1, theError);
        }

        /* Stop element being editable */
        setEditable(false);
        theSelectedItem = theItem;

        /* Note status has changed */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Request edit.
     */
    private void requestEdit() {
        /* Start editing */
        setEditable(true);
        theUpdateSet.setEditing(Boolean.TRUE);
        theSelectedItem = theItem;

        /* Note status has changed */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Request delete.
     */
    private void requestDelete() {
        /* Mark the item as deleted */
        theItem.setDeleted(true);
        theItem = null;

        /* Increment version */
        theUpdateSet.incrementVersion();

        /* Note status has changed */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Process goTo request.
     * @param pEvent the goTo request event
     */
    private void processGoToRequest(final PrometheusGoToEvent<?> pEvent) {
        theEventManager.fireEvent(PrometheusDataEvent.GOTOWINDOW, pEvent);
    }

    /**
     * Build goTo menu.
     * @param pMenu the menu to build
     */
    protected abstract void buildGoToMenu(TethysUIScrollMenu<TethysUIGenericWrapper> pMenu);

    /**
     * Create a GoTo event.
     * @param pGoToId the Id of the event
     * @param pDetails the details of the event
     * @return the action event
     */
    protected PrometheusGoToEvent<G> createGoToEvent(final G pGoToId,
                                                     final Object pDetails) {
        return new PrometheusGoToEvent<>(pGoToId, pDetails);
    }

    /**
     * fire a state changed event.
     */
    protected void fireStateChanged() {
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Update item.
     * @param pUpdate the update
     */
    private void updateItem(final PrometheusXFieldSetEvent pUpdate) {
        /* Push history */
        theItem.pushHistory();

        /* Protect against exceptions */
        try {
            /* Update the field */
            updateField(pUpdate);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Reset values */
            theItem.popHistory();

            /* Build the error */
            final OceanusException myError = new PrometheusDataException("Failed to update field", e);

            /* Show the error */
            theError.addError(myError);
            return;
        }

        /* Check for changes */
        if (theItem.checkForHistory()) {
            /* Increment the update version */
            theUpdateSet.incrementVersion();
            refreshAfterUpdate();

            /* Update according to the details */
            setEditable(true);
        }
    }
}
