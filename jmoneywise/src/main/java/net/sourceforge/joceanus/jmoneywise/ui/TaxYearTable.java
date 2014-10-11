/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmetis.viewer.JDataProfile;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.TaxYearPanel;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ActionButtons;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableSelection;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * TaxYear Table.
 */
public class TaxYearTable
        extends JDataTable<TaxYear, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -9063059159264496070L;

    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = TaxYear.FIELD_TAXYEAR.getName();

    /**
     * TaxRegime Column Title.
     */
    private static final String TITLE_REGIME = TaxYear.FIELD_REGIME.getName();

    /**
     * Active Column Title.
     */
    private static final String TITLE_ACTIVE = PrometheusUIResource.STATIC_TITLE_ACTIVE.getValue();

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final transient UpdateEntry<TaxYear, MoneyWiseDataType> theTaxYearEntry;

    /**
     * TaxYearInfo Update Entry.
     */
    private final transient UpdateEntry<TaxYearInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * Action Buttons.
     */
    private final ActionButtons theActionButtons;

    /**
     * The data entry.
     */
    private final transient JDataEntry theDataEntry;

    /**
     * The Column Model.
     */
    private final TaxYearColumnModel theColumns;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * The TaxYear dialog.
     */
    private final TaxYearPanel theActiveYear;

    /**
     * The List Selection Model.
     */
    private final transient JDataTableSelection<TaxYear, MoneyWiseDataType> theSelectionModel;

    /**
     * TaxYears.
     */
    private transient TaxYearList theTaxYears = null;

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return theActiveYear.isEditing();
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public TaxYearTable(final View pView) {
        /* Record the view */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and Entry */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(theView, MoneyWiseDataType.class);
        theTaxYearEntry = theUpdateSet.registerType(MoneyWiseDataType.TAXYEAR);
        theInfoEntry = theUpdateSet.registerType(MoneyWiseDataType.TAXYEARINFO);
        setUpdateSet(theUpdateSet);

        /* Create the debug entry, attach to MaintenanceDebug entry and hide it */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.new JDataEntry(TaxYear.class.getSimpleName());
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataEntry);

        /* Create the action buttons */
        theActionButtons = new ActionButtons(theUpdateSet, false);

        /* Create the table model */
        TaxYearTableModel myModel = new TaxYearTableModel(this);
        setModel(myModel);

        /* Create the data column model and declare it */
        theColumns = new TaxYearColumnModel(this);
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the main panel */
        JPanel myMain = new JEnablePanel();
        myMain.setLayout(new BoxLayout(myMain, BoxLayout.X_AXIS));
        myMain.add(getScrollPane());
        myMain.add(theActionButtons);

        /* Create the layout for the panel */
        thePanel = new JEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theError);
        thePanel.add(myMain);

        /* Create a TaxYear panel */
        theActiveYear = new TaxYearPanel(theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveYear);

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Create the selection model */
        theSelectionModel = new JDataTableSelection<TaxYear, MoneyWiseDataType>(this, theActiveYear);

        /* Create listener */
        new TaxYearListener();
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        theDataEntry.setFocus();
    }

    /**
     * Refresh data.
     */
    public void refreshData() {
        /* Obtain the active profile */
        JDataProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("TaxYearTab");

        /* Access the various lists */
        MoneyWiseData myData = theView.getData();

        /* Get the TaxYears edit list */
        TaxYearList myTaxYears = myData.getTaxYears();
        theTaxYears = myTaxYears.deriveEditList();
        theTaxYearEntry.setDataList(theTaxYears);
        TaxInfoList myInfo = theTaxYears.getTaxInfo();
        theInfoEntry.setDataList(myInfo);

        /* Notify panel of refresh */
        theActiveYear.refreshData();

        /* Notify of the change */
        setList(theTaxYears);
        fireStateChanged();

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void setError(final JOceanusException pError) {
        theError.addError(pError);
    }

    @Override
    public boolean hasUpdates() {
        return theUpdateSet.hasUpdates();
    }

    @Override
    public boolean hasSession() {
        return hasUpdates() || isItemEditing();
    }

    @Override
    public boolean hasErrors() {
        return theUpdateSet.hasErrors();
    }

    @Override
    public void cancelEditing() {
        /* Cancel editing on table */
        super.cancelEditing();

        /* Stop editing any item */
        theActiveYear.setEditable(false);
    }

    /**
     * Select deposit.
     * @param pTaxYear the taxYear to select
     */
    protected void selectTaxYear(final TaxYear pTaxYear) {
        /* Find the item in the list */
        int myIndex = theTaxYears.indexOf(pTaxYear);
        myIndex = convertRowIndexToView(myIndex);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    @Override
    protected void notifyChanges() {
        /* Adjust enable of the table */
        setEnabled(!theActiveYear.isEditing());

        /* set Visibility */
        setVisibility();

        /* Pass call on */
        super.notifyChanges();
    }

    /**
     * Set Visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have updates */
        boolean hasUpdates = hasUpdates();
        boolean isItemEditing = isItemEditing();

        /* Update the action buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates && !isItemEditing);
    }

    /**
     * JTable Data Model.
     */
    private final class TaxYearTableModel
            extends JDataTableModel<TaxYear, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = 4053776178814946474L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private TaxYearTableModel(final TaxYearTable pTable) {
            /* call constructor */
            super(pTable);
        }

        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                       ? 0
                                       : theColumns.getDeclaredCount();
        }

        @Override
        public int getRowCount() {
            return (theTaxYears == null)
                                        ? 0
                                        : theTaxYears.size();
        }

        @Override
        public JDataField getFieldForCell(final TaxYear pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final TaxYear pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public TaxYear getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theTaxYears.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final TaxYear pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final TaxYear pItem,
                                 final int pColIndex,
                                 final Object pValue) throws JOceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public boolean includeRow(final TaxYear pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return showAll() || !pRow.isDisabled();
        }
    }

    /**
     * Listener class.
     */
    private final class TaxYearListener
            implements ActionListener, ChangeListener {
        /**
         * Constructor.
         */
        private TaxYearListener() {
            /* Listen to correct events */
            theUpdateSet.addChangeListener(this);
            theActionButtons.addActionListener(this);
            theError.addChangeListener(this);
            theActiveYear.addChangeListener(this);
            theActiveYear.addActionListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If this is the error panel */
            if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Lock scroll area */
                getScrollPane().setEnabled(!isError);

                /* Lock Action Buttons */
                theActionButtons.setEnabled(!isError);
            }

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Only action if we are not editing */
                if (!theActiveYear.isEditing()) {
                    /* Handle the reWind */
                    theSelectionModel.handleReWind();
                }

                /* Adjust for changes */
                notifyChanges();
            }

            /* If we are noting change of edit state */
            if (theActiveYear.equals(o)) {
                /* Only action if we are not editing */
                if (!theActiveYear.isEditing()) {
                    /* handle the edit transition */
                    theSelectionModel.handleEditTransition();
                }

                /* Note changes */
                notifyChanges();
            }
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            Object o = pEvent.getSource();

            /* If this event relates to the action buttons */
            if (theActionButtons.equals(o)) {
                /* Cancel any editing */
                cancelEditing();

                /* Perform the command */
                theUpdateSet.processCommand(pEvent.getActionCommand(), theError);

                /* Notify listeners of changes */
                notifyChanges();
            }

            /* Handle actions */
            if ((theActiveYear.equals(o))
                && (pEvent instanceof ActionDetailEvent)) {
                cascadeActionEvent((ActionDetailEvent) pEvent);
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class TaxYearColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6629043017566713861L;

        /**
         * TaxYear column id.
         */
        private static final int COLUMN_YEAR = 0;

        /**
         * Regime column id.
         */
        private static final int COLUMN_REGIME = 1;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 2;

        /**
         * Status Icon Renderer.
         */
        private final IconButtonCellRenderer<ActionType> theStatusIconRenderer;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * Status Icon editor.
         */
        private final IconButtonCellEditor<ActionType> theStatusIconEditor;

        /**
         * Regime ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<TaxRegime> theRegimeEditor;

        /**
         * Constructor.
         * @param pTable the table
         */
        private TaxYearColumnModel(final TaxYearTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theStatusIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theRegimeEditor = theFieldMgr.allocateScrollButtonCellEditor(TaxRegime.class);
            theStatusIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theStatusIconEditor);
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButtons */
            MoneyWiseIcons.buildStatusButton(theStatusIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_YEAR, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_REGIME, WIDTH_NAME, theStringRenderer, theRegimeEditor));
            declareColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theStatusIconRenderer, theStatusIconEditor));

            /* Add listeners */
            new EditorListener();
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_YEAR:
                    return TITLE_NAME;
                case COLUMN_REGIME:
                    return TITLE_REGIME;
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the taxYear column.
         * @param pYear taxYear
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final TaxYear pYear,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_YEAR:
                    return Integer.toString(pYear.getTaxYear().getYear());
                case COLUMN_REGIME:
                    return pYear.getTaxRegime();
                case COLUMN_ACTIVE:
                    return pYear.isActive()
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
        private void setItemValue(final TaxYear pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JOceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_REGIME:
                    pItem.setTaxRegime((TaxRegime) pValue);
                    pItem.adjustForTaxRegime();
                    break;
                case COLUMN_ACTIVE:
                    pItem.setDeleted(true);
                    break;
                default:
                    break;
            }
        }

        /**
         * Is EdgeOfList?
         * @param pItem the item
         * @return true/false
         */
        private boolean isEdgeOfList(final TaxYear pItem) {
            TaxYearList myList = pItem.getList();
            TaxYear myNeighbour = myList.peekPrevious(pItem);
            if ((myNeighbour == null) || myNeighbour.isDeleted()) {
                return true;
            }
            myNeighbour = myList.peekNext(pItem);
            return (myNeighbour == null) || myNeighbour.isDeleted();
        }

        /**
         * Is the cell editable?
         * @param pItem the item
         * @param pColIndex the column index
         * @return true/false
         */
        private boolean isCellEditable(final TaxYear pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_YEAR:
                    return false;
                case COLUMN_REGIME:
                    return true;
                case COLUMN_ACTIVE:
                    return !pItem.isActive() && isEdgeOfList(pItem);
                default:
                    return false;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        protected JDataField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_YEAR:
                    return TaxYear.FIELD_TAXYEAR;
                case COLUMN_REGIME:
                    return TaxYear.FIELD_REGIME;
                case COLUMN_ACTIVE:
                    return TaxYear.FIELD_TOUCH;
                default:
                    return null;
            }
        }

        /**
         * EditorListener.
         */
        private final class EditorListener
                implements ChangeListener {
            /**
             * Constructor.
             */
            private EditorListener() {
                theRegimeEditor.addChangeListener(this);
            }

            @Override
            public void stateChanged(final ChangeEvent pEvent) {
                Object o = pEvent.getSource();

                if (theRegimeEditor.equals(o)) {
                    buildRegimeMenu();
                }
            }

            /**
             * Build the popUpMenu for regimes.
             */
            private void buildRegimeMenu() {
                /* Access details */
                JScrollMenuBuilder<TaxRegime> myBuilder = theRegimeEditor.getMenuBuilder();

                /* Record active item */
                Point myCell = theRegimeEditor.getPoint();
                TaxYear myYear = theTaxYears.get(myCell.y);

                /* Build the menu */
                theActiveYear.buildRegimeMenu(myBuilder, myYear);
            }
        }
    }
}
