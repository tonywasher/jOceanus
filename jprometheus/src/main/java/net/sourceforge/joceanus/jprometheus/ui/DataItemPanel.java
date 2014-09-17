/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Class to enable display/editing of and individual dataItem.
 * @param <T> the item type
 * @param <E> the data type enum class
 */
public abstract class DataItemPanel<T extends DataItem<E> & Comparable<? super T>, E extends Enum<E>>
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7514751065536367674L;

    /**
     * Details Tab Title.
     */
    protected static final String TAB_DETAILS = PrometheusUIResource.PANEL_TAB_DETAILS.getValue();

    /**
     * ReadOnly EditVersion.
     */
    protected static final int VERSION_READONLY = -1;

    /**
     * Padding size.
     */
    protected static final int PADDING_SIZE = 5;

    /**
     * Field Height.
     */
    protected static final int FIELD_HEIGHT = 20;

    /**
     * The DataFormatter.
     */
    private final transient JDataFormatter theFormatter;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<T> theFieldSet;

    /**
     * The Update Set.
     */
    private final UpdateSet<E> theUpdateSet;

    /**
     * The ErrorPanel.
     */
    private final ErrorPanel theError;

    /**
     * The MainPanel.
     */
    private final JPanel theMainPanel;

    /**
     * The Item Actions.
     */
    private final ItemActions<E> theItemActions;

    /**
     * The Item Actions.
     */
    private final ItemEditActions<E> theEditActions;

    /**
     * The Item.
     */
    private transient T theItem;

    /**
     * The New Item.
     */
    private transient T theSelectedItem;

    /**
     * The EditVersion.
     */
    private transient int theEditVersion = VERSION_READONLY;

    /**
     * Is this a new item.
     */
    private transient boolean isNew = false;

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    protected JDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the field Set.
     * @return the FieldSet
     */
    protected JFieldSet<T> getFieldSet() {
        return theFieldSet;
    }

    /**
     * Obtain the Update Set.
     * @return the UpdateSet
     */
    protected UpdateSet<E> getUpdateSet() {
        return theUpdateSet;
    }

    /**
     * Obtain the main panel.
     * @return the main panel
     */
    protected JPanel getMainPanel() {
        return theMainPanel;
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

    /**
     * Is the item new?
     * @return true/false
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected DataItemPanel(final JFieldManager pFieldMgr,
                            final UpdateSet<E> pUpdateSet,
                            final ErrorPanel pError) {
        /* Store parameters */
        theUpdateSet = pUpdateSet;
        theError = pError;

        /* Access the formatter */
        theFormatter = pFieldMgr.getDataFormatter();

        /* Create the New FieldSet */
        theFieldSet = new JFieldSet<T>(pFieldMgr);

        /* Create listener */
        FieldListener myListener = new FieldListener();
        theFieldSet.addActionListener(myListener);
        theUpdateSet.addChangeListener(myListener);

        /* Create the main panel */
        theMainPanel = new JEnablePanel();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        /* create the action panels */
        theItemActions = new ItemActions<E>(this);
        theEditActions = new ItemEditActions<E>(this);
    }

    /**
     * Layout the panel.
     */
    protected void layoutPanel() {
        /* Layout the panel */
        add(theItemActions);
        add(theMainPanel);
        add(theEditActions);

        /* Set visibility */
        setVisible(false);
    }

    /**
     * Set editable item.
     * @param isEditable true/false
     */
    public void setEditable(final boolean isEditable) {
        /* If we have an item */
        if (theItem != null) {
            /* Determine EditVersion */
            theEditVersion = isEditable
                                       ? isEditing()
                                                    ? theEditVersion
                                                    : theUpdateSet.getVersion()
                                       : VERSION_READONLY;

            /* adjust fields */
            setVisible(true);
            theFieldSet.setEditable(isEditable);
            adjustFields(isEditable);

            /* Set panel visibility */
            theItemActions.setVisible(!isEditable);
            theEditActions.setVisible(isEditable);

            /* Render the FieldSet */
            theFieldSet.renderSet(theItem);

            /* ensure that the actions are updated */
            updateActions();

        } else {
            /* Set EditVersion */
            theEditVersion = VERSION_READONLY;
            isNew = false;

            /* Set visibility */
            setVisible(false);
        }
    }

    /**
     * update the actions.
     */
    protected void updateActions() {
        theEditActions.updateState();
        theItemActions.updateState();
    }

    /**
     * Set readOnly item.
     * @param pItem the item
     */
    public void setItem(final T pItem) {
        /* If we are not editing or the item is non-null */
        if ((pItem != null) || !isEditing()) {
            /* Store the element */
            theItem = pItem;

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

        /* Note status has changed */
        fireStateChanged();
    }

    /**
     * Is the item edit-able?
     * @return true/false
     */
    protected boolean isEditable() {
        return true;
    }

    /**
     * Is the item delete-able?
     * @return true/false
     */
    protected boolean isDeletable() {
        return theItem != null
               && !theItem.isActive();
    }

    /**
     * Refresh data.
     */
    public abstract void refreshData();

    /**
     * Adjust Fields.
     * @param isEditable is the item editable?
     */
    protected abstract void adjustFields(final boolean isEditable);

    /**
     * Update the field.
     * @param pUpdate the update
     * @throws JOceanusException on error
     */
    protected abstract void updateField(final FieldUpdate pUpdate) throws JOceanusException;

    /**
     * Obtain the list for a class in base updateSet.
     * @param <L> the list type
     * @param <X> the object type
     * @param pDataType the data type
     * @param pClass the list class
     * @return the list
     */
    public <L extends DataList<X, E>, X extends DataItem<E> & Comparable<? super X>>
            L
            findDataList(final E pDataType,
                         final Class<L> pClass) {
        /* Look up the base list */
        return theUpdateSet.findDataList(pDataType, pClass);
    }

    /**
     * Restrict field.
     * @param pComponent the component to restrict
     * @param pWidth field width in characters
     */
    protected void restrictField(final JComponent pComponent,
                                 final int pWidth) {
        /* Calculate the character width */
        int myCharWidth = pComponent.getFontMetrics(pComponent.getFont()).stringWidth("w");

        /* Allocate Dimension */
        Dimension myPrefDims = new Dimension(pWidth * myCharWidth, FIELD_HEIGHT);
        Dimension myMaxDims = new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT);

        /* Restrict the field */
        pComponent.setPreferredSize(myPrefDims);
        pComponent.setMaximumSize(myMaxDims);
    }

    /**
     * Are we editing?
     * @return true/false
     */
    public boolean isEditing() {
        return theEditVersion != VERSION_READONLY;
    }

    /**
     * Do we have any updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return isEditing()
               && theEditVersion < theUpdateSet.getVersion();
    }

    /**
     * Do we have any errors?
     * @return true/false
     */
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
    protected void requestCancel() {
        /* If we have any updates */
        if (isNew) {
            /* Rewind any changes to before the new item */
            theUpdateSet.processEditCommand(UpdateSet.CMD_REWIND, theEditVersion - 1, theError);
            theItem = null;
        } else if (hasUpdates()) {
            /* Rewind any changes that have been made */
            theUpdateSet.processEditCommand(UpdateSet.CMD_REWIND, theEditVersion, theError);
        }

        /* Stop element being editable */
        setEditable(false);

        /* Note status has changed */
        fireStateChanged();
    }

    /**
     * Request reset.
     */
    protected void requestReset() {
        /* If we have any updates */
        if (hasUpdates()) {
            /* Rewind any changes that have been made */
            theUpdateSet.processEditCommand(UpdateSet.CMD_REWIND, theEditVersion, theError);
        }
    }

    /**
     * Request cancel.
     */
    protected void requestUndo() {
        /* If we have any updates */
        if (hasUpdates()) {
            /* Undo the last change */
            theUpdateSet.processEditCommand(UpdateSet.CMD_UNDO, VERSION_READONLY, theError);
        }
    }

    /**
     * Request commit.
     */
    protected void requestCommit() {
        /* If we have any updates */
        if (isNew || hasUpdates()) {
            /* Condense history to a single update */
            theUpdateSet.processEditCommand(UpdateSet.CMD_OK, isNew
                                                                   ? theEditVersion
                                                                   : theEditVersion + 1, theError);
        }

        /* Stop element being editable */
        setEditable(false);
        theSelectedItem = theItem;

        /* Note status has changed */
        fireStateChanged();
    }

    /**
     * Request edit.
     */
    protected void requestEdit() {
        /* Start editing */
        setEditable(true);
        theSelectedItem = theItem;

        /* Note status has changed */
        fireStateChanged();
    }

    /**
     * Request delete.
     */
    protected void requestDelete() {
        /* Mark the item as deleted */
        theItem.setDeleted(true);

        /* Increment version */
        theUpdateSet.incrementVersion();

        /* Note status has changed */
        fireStateChanged();
    }

    /**
     * Process goTo request.
     * @param pEvent the goTo request event
     */
    protected void processGoToRequest(final ActionDetailEvent pEvent) {
        cascadeActionEvent(pEvent);
    }

    /**
     * Build goTo menu.
     * @param pBuilder the menu builder
     */
    protected abstract void declareGoToMenuBuilder(final JScrollMenuBuilder<ActionDetailEvent> pBuilder);

    /**
     * Build goTo menu.
     */
    protected void buildGoToMenu() {
        /* Default empty implementation */
    }

    /**
     * Build a GoTo entry.
     * @param pItem the item
     */
    protected abstract void buildGoToEvent(final DataItem<E> pItem);

    /**
     * FieldListener class.
     */
    private final class FieldListener
            implements ActionListener, ChangeListener {
        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Note refresh */
                refreshAfterUpdate();
            }
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If the event relates to the Field Set */
            if ((theFieldSet.equals(o)) && (e instanceof ActionDetailEvent)) {
                /* Access event and obtain details */
                ActionDetailEvent evt = (ActionDetailEvent) e;
                Object dtl = evt.getDetails();
                if (dtl instanceof FieldUpdate) {
                    /* Update the item */
                    updateItem((FieldUpdate) dtl);
                }
            }
        }

        /**
         * Update item.
         * @param pUpdate the update
         */
        private void updateItem(final FieldUpdate pUpdate) {
            /* Push history */
            theItem.pushHistory();

            /* Protect against exceptions */
            try {
                /* Update the field */
                updateField(pUpdate);

                /* Handle Exceptions */
            } catch (JOceanusException e) {
                /* Reset values */
                theItem.popHistory();

                /* Build the error */
                JOceanusException myError = new JPrometheusDataException("Failed to update field", e);

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
}