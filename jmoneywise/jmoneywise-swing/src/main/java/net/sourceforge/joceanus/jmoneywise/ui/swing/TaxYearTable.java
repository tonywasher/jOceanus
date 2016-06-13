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
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseErrorPanel;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.swing.TaxYearPanel;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableSelection;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * TaxYear Table.
 */
public class TaxYearTable
        extends JDataTable<TaxYear, MoneyWiseDataType> {
    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = MoneyWiseUIResource.TAXYEAR_DATAENTRY.getValue();

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
    private final SwingView theView;

    /**
     * The field manager.
     */
    private final MetisFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final UpdateEntry<TaxYear, MoneyWiseDataType> theTaxYearEntry;

    /**
     * TaxYearInfo Update Entry.
     */
    private final UpdateEntry<TaxYearInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The error panel.
     */
    private final MoneyWiseErrorPanel<JComponent, Icon> theError;

    /**
     * Action Buttons.
     */
    private final PrometheusActionButtons<JComponent, Icon> theActionButtons;

    /**
     * The viewer entry.
     */
    private final MetisViewerEntry theViewerEntry;

    /**
     * The Column Model.
     */
    private final TaxYearColumnModel theColumns;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The TaxYear dialog.
     */
    private final TaxYearPanel theActiveYear;

    /**
     * The List Selection Model.
     */
    private final JDataTableSelection<TaxYear, MoneyWiseDataType> theSelectionModel;

    /**
     * TaxYears.
     */
    private TaxYearList theTaxYears;

    /**
     * Constructor.
     * @param pView the data view
     */
    public TaxYearTable(final SwingView pView) {
        /* initialise the underlying class */
        super(pView.getGuiFactory());

        /* Record the view */
        theView = pView;
        theFieldMgr = theView.getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Access the GUI Factory */
        TethysSwingGuiFactory myFactory = pView.getGuiFactory();
        MetisViewerManager myViewer = theView.getViewerManager();

        /* Build the Update set and Entry */
        theUpdateSet = new UpdateSet<>(theView, MoneyWiseDataType.class);
        theTaxYearEntry = theUpdateSet.registerType(MoneyWiseDataType.TAXYEAR);
        theInfoEntry = theUpdateSet.registerType(MoneyWiseDataType.TAXYEARINFO);
        setUpdateSet(theUpdateSet);

        /* Create the top level viewer entry for this view */
        MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.MAINTENANCE);
        theViewerEntry = myViewer.newEntry(mySection, NLS_DATAENTRY);
        theViewerEntry.setTreeObject(theUpdateSet);

        /* Create the error panel for this view */
        theError = new MoneyWiseErrorPanel<>(theView, theViewerEntry);

        /* Create the action buttons */
        theActionButtons = new PrometheusActionButtons<>(pView.getUtilitySet().getGuiFactory(), theUpdateSet, false);

        /* Create the table model */
        TaxYearTableModel myModel = new TaxYearTableModel(this);
        setModel(myModel);

        /* Create the data column model and declare it */
        theColumns = new TaxYearColumnModel(this);
        JTable myTable = getTable();
        myTable.setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the main panel */
        JPanel myMain = new TethysSwingEnablePanel();
        myMain.setLayout(new BorderLayout());
        myMain.add(super.getNode(), BorderLayout.CENTER);
        myMain.add(theActionButtons.getNode(), BorderLayout.LINE_END);

        /* Create the layout for the panel */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BorderLayout());
        thePanel.add(theError.getNode(), BorderLayout.PAGE_START);
        thePanel.add(myMain, BorderLayout.CENTER);

        /* Create a TaxYear panel */
        theActiveYear = new TaxYearPanel(myFactory, theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveYear.getNode(), BorderLayout.PAGE_END);

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Create the selection model */
        theSelectionModel = new JDataTableSelection<>(this, theActiveYear);

        /* Create listener */
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theActiveYear.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveYear.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, this::cascadeEvent);
    }

    @Override
    public JComponent getNode() {
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
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        getTable().requestFocusInWindow();

        /* Set the required focus */
        theViewerEntry.setFocus();
    }

    /**
     * Refresh data.
     */
    public void refreshData() {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
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

        /* Touch the updateSet */
        theViewerEntry.setTreeObject(theUpdateSet);

        /* Notify of the change */
        setList(theTaxYears);
        fireStateChanged();

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void setError(final OceanusException pError) {
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
    public void setEnabled(final boolean bEnable) {
        /* Ensure that we are disabled whilst editing */
        super.setEnabled(bEnable && !isItemEditing());
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
        myIndex = getTable().convertRowIndexToView(myIndex);
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
     * Handle updateSet rewind.
     */
    private void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveYear.isEditing()) {
            /* Handle the reWind */
            theSelectionModel.handleReWind();
        }

        /* Adjust for changes */
        notifyChanges();
    }

    /**
     * Handle panel state.
     */
    private void handlePanelState() {
        /* Only action if we are not editing */
        if (!theActiveYear.isEditing()) {
            /* handle the edit transition */
            theSelectionModel.handleEditTransition();
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * handleErrorPane.
     */
    private void handleErrorPane() {
        /* Determine whether we have an error */
        boolean isError = theError.hasError();

        /* Lock scroll area */
        super.getNode().setEnabled(!isError);

        /* Lock Action Buttons */
        theActionButtons.setEnabled(!isError);
    }

    /**
     * handle Action Buttons.
     * @param pEvent the event
     */
    private void handleActionButtons(final TethysEvent<PrometheusUIEvent> pEvent) {
        /* Cancel editing */
        cancelEditing();

        /* Perform the command */
        theUpdateSet.processCommand(pEvent.getEventId(), theError);

        /* Adjust for changes */
        notifyChanges();
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
        public MetisField getFieldForCell(final TaxYear pItem,
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
                                 final Object pValue) throws OceanusException {
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
            theRegimeEditor.getEventRegistrar().addEventListener(e -> buildRegimeMenu());
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
         * @throws OceanusException on error
         */
        private void setItemValue(final TaxYear pItem,
                                  final int pColIndex,
                                  final Object pValue) throws OceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_REGIME:
                    pItem.setTaxRegime((TaxRegime) pValue);
                    pItem.autoCorrect(theUpdateSet);
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
        protected MetisField getFieldForCell(final int pColIndex) {
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
