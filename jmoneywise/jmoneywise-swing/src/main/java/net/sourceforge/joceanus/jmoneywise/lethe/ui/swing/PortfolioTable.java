/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldIconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldStringCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldCalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldIconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldStringCellRenderer;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PortfolioInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PortfolioInfo.PortfolioInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType;
import net.sourceforge.joceanus.jmoneywise.lethe.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.PortfolioPanel;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusAction;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTable;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn.PrometheusDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableSelection;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingButton;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingCheckBox;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Portfolio Table.
 */
public class PortfolioTable
        extends PrometheusDataTable<Portfolio, MoneyWiseDataType> {
    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = Portfolio.FIELD_NAME.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = Portfolio.FIELD_DESC.getName();

    /**
     * Type Column Title.
     */
    private static final String TITLE_TYPE = Portfolio.FIELD_PORTTYPE.getName();

    /**
     * Parent Column Title.
     */
    private static final String TITLE_PARENT = Portfolio.FIELD_PARENT.getName();

    /**
     * Currency Column Title.
     */
    private static final String TITLE_CURRENCY = Portfolio.FIELD_CURRENCY.getName();

    /**
     * Closed Column Title.
     */
    private static final String TITLE_CLOSED = Portfolio.FIELD_CLOSED.getName();

    /**
     * ShowClosed prompt.
     */
    private static final String PROMPT_CLOSED = MoneyWiseUIResource.UI_PROMPT_SHOWCLOSED.getValue();

    /**
     * Active Column Title.
     */
    private static final String TITLE_ACTIVE = PrometheusUIResource.STATIC_TITLE_ACTIVE.getValue();

    /**
     * LastTransaction Column Title.
     */
    private static final String TITLE_LASTTRAN = MoneyWiseUIResource.ASSET_COLUMN_LATEST.getValue();

    /**
     * The data view.
     */
    private final SwingView theView;

    /**
     * The field manager.
     */
    private final MetisSwingFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final UpdateEntry<Portfolio, MoneyWiseDataType> thePortfolioEntry;

    /**
     * PortfolioInfo Update Entry.
     */
    private final UpdateEntry<PortfolioInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel<JComponent, Icon> theError;

    /**
     * The Table Model.
     */
    private final PortfolioTableModel theModel;

    /**
     * The Column Model.
     */
    private final PortfolioColumnModel theColumns;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The filter panel.
     */
    private final TethysSwingBoxPaneManager theFilterPanel;

    /**
     * The locked check box.
     */
    private final TethysSwingCheckBox theLockedCheckBox;

    /**
     * The new button.
     */
    private final TethysSwingButton theNewButton;

    /**
     * The Portfolio dialog.
     */
    private final PortfolioPanel theActiveAccount;

    /**
     * The List Selection Model.
     */
    private final PrometheusDataTableSelection<Portfolio, MoneyWiseDataType> theSelectionModel;

    /**
     * Portfolios.
     */
    private PortfolioList thePortfolios;

    /**
     * Constructor.
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public PortfolioTable(final SwingView pView,
                          final UpdateSet<MoneyWiseDataType> pUpdateSet,
                          final MetisErrorPanel<JComponent, Icon> pError) {
        /* initialise the underlying class */
        super(pView.getGuiFactory());

        /* Access the GUI Factory */
        final TethysSwingGuiFactory myFactory = pView.getGuiFactory();

        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        thePortfolioEntry = theUpdateSet.registerType(MoneyWiseDataType.PORTFOLIO);
        theInfoEntry = theUpdateSet.registerType(MoneyWiseDataType.PORTFOLIOINFO);
        setUpdateSet(theUpdateSet);

        /* Create the table model */
        theModel = new PortfolioTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new PortfolioColumnModel(this);
        final JTable myTable = getTable();
        myTable.setColumnModel(theColumns);
        theColumns.setColumns();

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the CheckBox */
        theLockedCheckBox = myFactory.newCheckBox(PROMPT_CLOSED);

        /* Create new button */
        theNewButton = myFactory.newButton();
        PrometheusIcon.configureNewIconButton(theNewButton);

        /* Create the filter panel */
        theFilterPanel = myFactory.newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(theLockedCheckBox);
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(theNewButton);

        /* Create the layout for the panel */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BorderLayout());
        thePanel.add(super.getNode(), BorderLayout.CENTER);

        /* Create an account panel */
        theActiveAccount = new PortfolioPanel(myFactory, theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveAccount.getNode(), BorderLayout.PAGE_END);

        /* Create the selection model */
        theSelectionModel = new PrometheusDataTableSelection<>(this, theActiveAccount);

        /* Create listeners */
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theActiveAccount.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveAccount.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, this::cascadeEvent);

        /* Listen to swing events */
        theNewButton.getEventRegistrar().addEventListener(e -> theModel.addNewItem());
        theLockedCheckBox.getEventRegistrar().addEventListener(e -> setShowAll(theLockedCheckBox.isSelected()));
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected TethysSwingBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    /**
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return theActiveAccount.isEditing();
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        getTable().requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(thePortfolioEntry.getName());
    }

    /**
     * Refresh data.
     * @throws OceanusException on error
     */
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Portfolios");

        /* Get the Portfolios edit list */
        final MoneyWiseData myData = theView.getData();
        final PortfolioList myPortfolios = myData.getPortfolios();
        thePortfolios = myPortfolios.deriveEditList(theUpdateSet);
        thePortfolioEntry.setDataList(thePortfolios);
        final PortfolioInfoList myInfo = thePortfolios.getPortfolioInfo();
        theInfoEntry.setDataList(myInfo);

        /* Notify panel of refresh */
        theActiveAccount.refreshData();

        /* Notify of the change */
        setList(thePortfolios);

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void setShowAll(final boolean pShow) {
        super.setShowAll(pShow);
        theColumns.setColumns();
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
    public void cancelEditing() {
        /* Cancel editing on table */
        super.cancelEditing();

        /* Stop editing any item */
        theActiveAccount.setEditable(false);
    }

    /**
     * Select portfolio.
     * @param pPortfolio the portfolio to select
     */
    protected void selectPortfolio(final Portfolio pPortfolio) {
        /* If the item is closed, but we are not showing closed items */
        if (pPortfolio.isClosed()
            && !theLockedCheckBox.isSelected()) {
            theLockedCheckBox.setSelected(true);
            setShowAll(true);
        }

        /* Find the item in the list */
        int myIndex = thePortfolios.indexOf(pPortfolio);
        myIndex = getTable().convertRowIndexToView(myIndex);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    @Override
    protected void notifyChanges() {
        /* Adjust enable of the table */
        setEnabled(!theActiveAccount.isEditing());

        /* Pass call on */
        super.notifyChanges();
    }

    /**
     * Handle updateSet rewind.
     */
    private void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveAccount.isEditing()) {
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
        if (!theActiveAccount.isEditing()) {
            /* handle the edit transition */
            theSelectionModel.handleEditTransition();
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * JTable Data Model.
     */
    private final class PortfolioTableModel
            extends PrometheusDataTableModel<Portfolio, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -4173005216244148124L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private PortfolioTableModel(final PortfolioTable pTable) {
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
            return (thePortfolios == null)
                                           ? 0
                                           : thePortfolios.size();
        }

        @Override
        public MetisField getFieldForCell(final Portfolio pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final Portfolio pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Portfolio getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return thePortfolios.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final Portfolio pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final Portfolio pItem,
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
        public boolean includeRow(final Portfolio pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return showAll() || !pRow.isDisabled();
        }

        /**
         * New item.
         */
        private void addNewItem() {
            /* Protect against Exceptions */
            try {
                /* Create the new portfolio */
                final Portfolio myPortfolio = new Portfolio(thePortfolios);
                myPortfolio.setDefaults(theUpdateSet);

                /* Add the new item */
                myPortfolio.setNewVersion();
                thePortfolios.append(myPortfolio);

                /* Validate the new item and notify of the changes */
                myPortfolio.validate();
                incrementVersion();

                /* Lock the table */
                setEnabled(false);
                theActiveAccount.setNewItem(myPortfolio);

                /* Handle Exceptions */
            } catch (OceanusException e) {
                /* Build the error */
                final OceanusException myError = new MoneyWiseDataException("Failed to create new account", e);

                /* Show the error */
                setError(myError);
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class PortfolioColumnModel
            extends PrometheusDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -9205761275148585328L;

        /**
         * Name column id.
         */
        private static final int COLUMN_NAME = 0;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 1;

        /**
         * Type column id.
         */
        private static final int COLUMN_TYPE = 2;

        /**
         * Parent column id.
         */
        private static final int COLUMN_PARENT = 3;

        /**
         * Currency column id.
         */
        private static final int COLUMN_CURR = 4;

        /**
         * Closed column id.
         */
        private static final int COLUMN_CLOSED = 5;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 6;

        /**
         * LastTran column id.
         */
        private static final int COLUMN_LASTTRAN = 7;

        /**
         * Closed column.
         */
        private final PrometheusDataTableColumn theClosedColumn;

        /**
         * Constructor.
         * @param pTable the table
         */
        private PortfolioColumnModel(final PortfolioTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            final MetisFieldIconButtonCellEditor<Boolean> myClosedIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class);
            final MetisFieldIconButtonCellEditor<PrometheusAction> myStatusIconEditor = theFieldMgr.allocateIconButtonCellEditor(PrometheusAction.class);
            final MetisFieldStringCellEditor myStringEditor = theFieldMgr.allocateStringCellEditor();
            final MetisFieldScrollButtonCellEditor<PortfolioType> myTypeEditor = theFieldMgr.allocateScrollButtonCellEditor(PortfolioType.class);
            final MetisFieldScrollButtonCellEditor<Payee> myParentEditor = theFieldMgr.allocateScrollButtonCellEditor(Payee.class);
            final MetisFieldScrollButtonCellEditor<AssetCurrency> myCurrencyEditor = theFieldMgr.allocateScrollButtonCellEditor(AssetCurrency.class);
            final MetisFieldIconButtonCellRenderer<Boolean> myClosedIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(Boolean.class);
            final MetisFieldIconButtonCellRenderer<PrometheusAction> myStatusIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(PrometheusAction.class);
            final MetisFieldCalendarCellRenderer myDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            final MetisFieldStringCellRenderer myStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButtons */
            final TethysIconMapSet<PrometheusAction> myActionMapSet = PrometheusIcon.configureStatusIconButton();
            myStatusIconRenderer.setIconMapSet(r -> myActionMapSet);
            myStatusIconEditor.setIconMapSet(r -> myActionMapSet);
            final Map<Boolean, TethysIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton();
            myClosedIconEditor.setIconMapSet(r -> myMapSets.get(determineClosedState(r)));
            myClosedIconRenderer.setIconMapSet(r -> myMapSets.get(determineClosedState(r)));

            /* Create the columns */
            declareColumn(new PrometheusDataTableColumn(COLUMN_NAME, WIDTH_NAME, myStringRenderer, myStringEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_DESC, WIDTH_NAME, myStringRenderer, myStringEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_TYPE, WIDTH_NAME, myStringRenderer, myTypeEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_PARENT, WIDTH_NAME, myStringRenderer, myParentEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_CURR, WIDTH_CURR, myStringRenderer, myCurrencyEditor));
            theClosedColumn = new PrometheusDataTableColumn(COLUMN_CLOSED, WIDTH_ICON, myClosedIconRenderer, myClosedIconEditor);
            declareColumn(theClosedColumn);
            declareColumn(new PrometheusDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, myStatusIconRenderer, myStatusIconEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_LASTTRAN, WIDTH_DATE, myDateRenderer));

            /* Initialise the columns */
            setColumns();

            /* Add listeners */
            myTypeEditor.setMenuConfigurator(this::buildTypeMenu);
            myCurrencyEditor.setMenuConfigurator(this::buildCurrencyMenu);
            myParentEditor.setMenuConfigurator(this::buildParentMenu);
        }

        /**
         * Set visible columns according to the mode.
         */
        private void setColumns() {
            /* Switch on mode */
            if (showAll()) {
                revealColumn(theClosedColumn);
            } else {
                hideColumn(theClosedColumn);
            }
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                    return TITLE_NAME;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_TYPE:
                    return TITLE_TYPE;
                case COLUMN_PARENT:
                    return TITLE_PARENT;
                case COLUMN_CURR:
                    return TITLE_CURRENCY;
                case COLUMN_CLOSED:
                    return TITLE_CLOSED;
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                case COLUMN_LASTTRAN:
                    return TITLE_LASTTRAN;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the Portfolio column.
         * @param pPortfolio Portfolio
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final Portfolio pPortfolio,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return pPortfolio.getName();
                case COLUMN_TYPE:
                    return pPortfolio.getPortfolioType();
                case COLUMN_PARENT:
                    return pPortfolio.getParent();
                case COLUMN_DESC:
                    return pPortfolio.getDesc();
                case COLUMN_CURR:
                    return pPortfolio.getAssetCurrency();
                case COLUMN_CLOSED:
                    return pPortfolio.isClosed();
                case COLUMN_ACTIVE:
                    return pPortfolio.isActive()
                                                 ? PrometheusAction.ACTIVE
                                                 : PrometheusAction.DELETE;
                case COLUMN_LASTTRAN:
                    final Transaction myTran = pPortfolio.getLatest();
                    return (myTran == null)
                                            ? null
                                            : myTran.getDate();
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
        private void setItemValue(final Portfolio pItem,
                                  final int pColIndex,
                                  final Object pValue) throws OceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    pItem.setName((String) pValue);
                    break;
                case COLUMN_DESC:
                    pItem.setDescription((String) pValue);
                    break;
                case COLUMN_TYPE:
                    pItem.setPortfolioType((PortfolioType) pValue);
                    break;
                case COLUMN_PARENT:
                    pItem.setParent((Payee) pValue);
                    break;
                case COLUMN_CURR:
                    pItem.setAssetCurrency((AssetCurrency) pValue);
                    break;
                case COLUMN_CLOSED:
                    pItem.setClosed((Boolean) pValue);
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
        private boolean isCellEditable(final Portfolio pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_TYPE:
                case COLUMN_PARENT:
                case COLUMN_CURR:
                case COLUMN_ACTIVE:
                    return !pItem.isActive();
                case COLUMN_CLOSED:
                    return pItem.isClosed() || !pItem.isRelevant();
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
                case COLUMN_NAME:
                    return Portfolio.FIELD_NAME;
                case COLUMN_DESC:
                    return Portfolio.FIELD_DESC;
                case COLUMN_TYPE:
                    return Portfolio.FIELD_PORTTYPE;
                case COLUMN_PARENT:
                    return Portfolio.FIELD_PARENT;
                case COLUMN_CURR:
                    return Portfolio.FIELD_CURRENCY;
                case COLUMN_CLOSED:
                    return Portfolio.FIELD_CLOSED;
                case COLUMN_ACTIVE:
                    return Portfolio.FIELD_TOUCH;
                default:
                    return null;
            }
        }

        /**
         * Build the popUpMenu for types.
         * @param pRowIndex the rowIndex for the item
         * @param pMenu the menu to build
         */
        private void buildTypeMenu(final Integer pRowIndex,
                                   final TethysScrollMenu<PortfolioType, Icon> pMenu) {
            /* Record active item */
            final Portfolio myPortfolio = theModel.getItemAtIndex(pRowIndex);

            /* Build the menu */
            theActiveAccount.buildTypeMenu(pMenu, myPortfolio);
        }

        /**
         * Build the popUpMenu for parents.
         * @param pRowIndex the rowIndex for the item
         * @param pMenu the menu to build
         */
        private void buildParentMenu(final Integer pRowIndex,
                                     final TethysScrollMenu<Payee, Icon> pMenu) {
            /* Record active item */
            final Portfolio myPortfolio = theModel.getItemAtIndex(pRowIndex);

            /* Build the menu */
            theActiveAccount.buildParentMenu(pMenu, myPortfolio);
        }

        /**
         * Build the popUpMenu for currencies.
         * @param pRowIndex the rowIndex for the item
         * @param pMenu the menu to build
         */
        private void buildCurrencyMenu(final Integer pRowIndex,
                                       final TethysScrollMenu<AssetCurrency, Icon> pMenu) {
            /* Record active item */
            final Portfolio myPortfolio = theModel.getItemAtIndex(pRowIndex);

            /* Build the menu */
            theActiveAccount.buildCurrencyMenu(pMenu, myPortfolio);
        }

        /**
         * Determine closed state.
         * @param pRowIndex the row index
         * @return the state
         */
        private boolean determineClosedState(final int pRowIndex) {
            final Portfolio myPortfolio = theModel.getItemAtIndex(pRowIndex);
            return myPortfolio.isClosed() || !myPortfolio.isRelevant();
        }
    }
}
