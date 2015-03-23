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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jprometheus.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jprometheus.data.StaticData.StaticList;
import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.swing.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Static Data Table.
 * @author Tony Washer
 * @param <L> the list type
 * @param <T> the data type
 * @param <S> the static class
 * @param <E> the data type enum class
 */
public class StaticDataTable<L extends StaticList<T, S, E>, T extends StaticData<T, S, E>, S extends Enum<S> & StaticInterface, E extends Enum<E>>
        extends JDataTable<T, E> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8747707037700378702L;

    /**
     * Class column title.
     */
    private static final String TITLE_CLASS = StaticData.FIELD_CLASS.getName();

    /**
     * Name column title.
     */
    private static final String TITLE_NAME = StaticData.FIELD_NAME.getName();

    /**
     * Description column title.
     */
    private static final String TITLE_DESC = StaticData.FIELD_DESC.getName();

    /**
     * Enabled column title.
     */
    private static final String TITLE_ENABLED = StaticData.FIELD_ENABLED.getName();

    /**
     * Active column title.
     */
    private static final String TITLE_ACTIVE = PrometheusUIResource.STATIC_TITLE_ACTIVE.getValue();

    /**
     * The Data view.
     */
    private final transient DataControl<?, E> theControl;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The Panel.
     */
    private final JEnablePanel thePanel;

    /**
     * The new button.
     */
    private final JScrollButton<S> theNewButton;

    /**
     * The Data class.
     */
    private final Class<L> theClass;

    /**
     * The List.
     */
    private transient L theStatic = null;

    /**
     * The Table Model.
     */
    private final StaticModel theModel;

    /**
     * The Columns model.
     */
    private final StaticColumnModel theColumns;

    /**
     * The UpdateSet.
     */
    private final transient UpdateSet<E> theUpdateSet;

    /**
     * The UpdateEntry.
     */
    private final transient UpdateEntry<T, E> theUpdateEntry;

    /**
     * The Error panel.
     */
    private final ErrorPanel theError;

    /**
     * Constructor.
     * @param pControl the data control
     * @param pUpdateSet the update set
     * @param pError the error panel
     * @param pItemType the item type
     * @param pListClass the list class
     */
    public StaticDataTable(final DataControl<?, E> pControl,
                           final UpdateSet<E> pUpdateSet,
                           final ErrorPanel pError,
                           final E pItemType,
                           final Class<L> pListClass) {
        /* Record the passed details */
        theError = pError;
        theClass = pListClass;
        theControl = pControl;
        theFieldMgr = theControl.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and List */
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerType(pItemType);
        setUpdateSet(theUpdateSet);

        /* Set the table model */
        theModel = new StaticModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new StaticColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create new button */
        theNewButton = PrometheusIcons.getNewScrollButton();

        /* Create the panel */
        thePanel = new JEnablePanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(getScrollPane());

        /* Create the listener */
        StaticListener myListener = new StaticListener();
        theUpdateSet.addChangeListener(myListener);
        theNewButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
    }

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Obtain the new button.
     * @return the new Button
     */
    public JScrollButton<S> getNewButton() {
        return theNewButton;
    }

    @Override
    protected void setError(final JOceanusException pError) {
        theError.addError(pError);
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final JDataEntry pEntry) {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theUpdateEntry.getName());
    }

    @Override
    public void notifyChanges() {
        /* Notify that there have been changes */
        fireStateChanged();
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @throws JOceanusException on error
     */
    protected void refreshData() throws JOceanusException {
        /* Access data */
        DataSet<?, E> myData = theControl.getData();

        /* Access edit list and map it */
        StaticList<T, S, E> myStatic = myData.getDataList(theClass);
        theStatic = theClass.cast(myStatic.deriveList(ListStyle.EDIT));
        theStatic.mapData();

        /* Update the Data View */
        setList(theStatic);
        theUpdateEntry.setDataList(theStatic);
    }

    /**
     * Select static data.
     * @param pStatic the static data
     */
    protected void selectStatic(final StaticData<?, ?, E> pStatic) {
        /* Find the item in the list */
        int myIndex = theStatic.indexOf(pStatic);
        myIndex = convertRowIndexToView(myIndex);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    @Override
    public void setShowAll(final boolean pShow) {
        super.setShowAll(pShow);
        theColumns.setColumns();
    }

    /**
     * Is the static table full?
     * @return true/false
     */
    protected boolean isFull() {
        return theStatic == null
               || theStatic.isFull();
    }

    /**
     * The listener class.
     */
    private final class StaticListener
            implements PropertyChangeListener, ChangeListener {
        /**
         * MenuBuilder.
         */
        private final JScrollMenuBuilder<S> theMenuBuilder;

        /**
         * Constructor.
         */
        private StaticListener() {
            /* Access the builder */
            theMenuBuilder = theNewButton.getMenuBuilder();
            theMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireNewDataEvents();
            }

            /* If this is the menu builder */
            if (theMenuBuilder.equals(o)) {
                /* Create the new menu */
                buildNewMenu();
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Access the new class */
            S myClass = theNewButton.getValue();

            /* Protect the action */
            try {
                /* Look to find a deleted value */
                T myValue = theStatic.findItemByClass(myClass);

                /* If we found a deleted value */
                if (myValue != null) {
                    /* reinstate it */
                    myValue.setDeleted(false);

                    /* else we have no existing value */
                } else {
                    /* Create the new value */
                    myValue = theStatic.addNewItem(myClass);
                    myValue.setNewVersion();
                }

                /* Update the table */
                incrementVersion();
                theModel.fireNewDataEvents();
                notifyChanges();

                /* Handle exceptions */
            } catch (JOceanusException e) {
                setError(e);
            }
        }

        /**
         * Build the menu of available new items.
         */
        protected void buildNewMenu() {
            /* Reset the menu popUp */
            theMenuBuilder.clearMenu();

            /* Loop through the missing classes */
            for (S myValue : theStatic.getMissingClasses()) {
                /* Create a new JMenuItem and add it to the popUp */
                theMenuBuilder.addItem(myValue);
            }
        }
    }

    /**
     * Static table model.
     */
    public final class StaticModel
            extends JDataTableModel<T, E> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6428052539280821038L;

        /**
         * Constructor.
         */
        private StaticModel() {
            /* call constructor */
            super(StaticDataTable.this);
        }

        @Override
        public T getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theStatic.get(pRowIndex);
        }

        /**
         * Get the number of display columns.
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                       ? 0
                                       : theColumns.getDeclaredCount();
        }

        /**
         * Get the number of rows in the current table.
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (theStatic == null)
                                      ? 0
                                      : theStatic.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public JDataField getFieldForCell(final T pItem,
                                          final int pColIndex) {
            /* Obtain the column field */
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final T pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Object getItemValue(final T pItem,
                                   final int pColIndex) {
            /* Obtain the item value for the column */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final T pItem,
                                 final int pColIndex,
                                 final Object pValue) throws JOceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }

        @Override
        public boolean includeRow(final T pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return showAll() || !pRow.isDisabled();
        }
    }

    /**
     * Column Model class.
     */
    private final class StaticColumnModel
            extends JDataTableColumnModel<E> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 676363206266447113L;

        /**
         * Class column id.
         */
        private static final int COLUMN_CLASS = 0;

        /**
         * Name column id.
         */
        private static final int COLUMN_NAME = 1;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 2;

        /**
         * Enabled column id.
         */
        private static final int COLUMN_ENABLED = 3;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 4;

        /**
         * Class column width.
         */
        private static final int WIDTH_CLASS = 90;

        /**
         * String renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * String editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * Icon Status Renderer.
         */
        private final IconButtonCellRenderer<ActionType> theStatusIconRenderer;

        /**
         * Icon Enabled Renderer.
         */
        private final IconButtonCellRenderer<Boolean> theEnabledIconRenderer;

        /**
         * Icon editor.
         */
        private final IconButtonCellEditor<ActionType> theStatusIconEditor;

        /**
         * Icon editor.
         */
        private final IconButtonCellEditor<Boolean> theEnabledIconEditor;

        /**
         * Enabled column.
         */
        private final JDataTableColumn theEnabledColumn;

        /**
         * Constructor.
         */
        private StaticColumnModel() {
            /* call constructor */
            super(StaticDataTable.this);

            /* Create the relevant renderers/editors */
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theStatusIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theStatusIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theStatusIconEditor);
            theEnabledIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, false);
            theEnabledIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theEnabledIconEditor);

            /* Configure the iconButtons */
            PrometheusIcons.buildStatusButton(theStatusIconEditor.getState());
            PrometheusIcons.buildEnabledButton(theEnabledIconEditor.getState());

            /* Create and declare the columns */
            declareColumn(new JDataTableColumn(COLUMN_CLASS, WIDTH_CLASS, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            theEnabledColumn = new JDataTableColumn(COLUMN_ENABLED, WIDTH_ICON, theEnabledIconRenderer, theEnabledIconEditor);
            declareColumn(theEnabledColumn);
            declareColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theStatusIconRenderer, theStatusIconEditor));

            /* Initialise the columns display */
            setColumns();
        }

        /**
         * Set visible columns according to the mode.
         */
        private void setColumns() {
            /* Switch on mode */
            if (showAll()) {
                revealColumn(theEnabledColumn);
            } else {
                hideColumn(theEnabledColumn);
            }
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_CLASS:
                    return TITLE_CLASS;
                case COLUMN_NAME:
                    return TITLE_NAME;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_ENABLED:
                    return TITLE_ENABLED;
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @return the value
         */
        private Object getItemValue(final T pItem,
                                    final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_CLASS:
                    return pItem.getStaticClass().toString();
                case COLUMN_NAME:
                    return pItem.getName();
                case COLUMN_DESC:
                    return pItem.getDesc();
                case COLUMN_ENABLED:
                    return pItem.getEnabled();
                case COLUMN_ACTIVE:
                    return pItem.isActive()
                                           ? ActionType.ACTIVE
                                           : ActionType.DELETE;
                default:
                    return null;
            }
        }

        /**
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value to set
         * @throws JOceanusException on error
         */
        private void setItemValue(final T pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JOceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    pItem.setName((String) pValue);
                    break;
                case COLUMN_DESC:
                    pItem.setDescription((String) pValue);
                    break;
                case COLUMN_ENABLED:
                    pItem.setEnabled((Boolean) pValue);
                    break;
                case COLUMN_ACTIVE:
                    pItem.setDeleted(true);
                    break;
                default:
                    break;
            }
        }

        /**
         * Is the cell editable?
         * @param pItem the item
         * @param pColIndex the column index
         * @return true/false
         */
        private boolean isCellEditable(final T pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_ACTIVE:
                case COLUMN_ENABLED:
                    return !pItem.isActive();
                default:
                    return false;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        private JDataField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_CLASS:
                    return StaticData.FIELD_CLASS;
                case COLUMN_NAME:
                    return StaticData.FIELD_NAME;
                case COLUMN_DESC:
                    return StaticData.FIELD_DESC;
                case COLUMN_ENABLED:
                    return StaticData.FIELD_ENABLED;
                case COLUMN_ACTIVE:
                    return StaticData.FIELD_TOUCH;
                default:
                    return null;
            }
        }
    }
}
