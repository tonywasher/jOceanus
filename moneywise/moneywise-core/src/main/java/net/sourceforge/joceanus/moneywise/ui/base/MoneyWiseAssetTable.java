/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.ui.base;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.list.MetisListKey;
import net.sourceforge.joceanus.metis.ui.MetisAction;
import net.sourceforge.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.control.TethysUICheckBox;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableManager;

import java.util.Iterator;
import java.util.Map;

/**
 * MoneyWise Asset Table.
 * @param <T> the Asset Data type
 */
public abstract class MoneyWiseAssetTable<T extends MoneyWiseAssetBase>
        extends MoneyWiseBaseTable<T> {
    /**
     * ShowClosed prompt.
     */
    private static final String PROMPT_CLOSED = MoneyWiseUIResource.UI_PROMPT_SHOWCLOSED.getValue();

    /**
     * The filter panel.
     */
    private final TethysUIBoxPaneManager theFilterPanel;

    /**
     * The locked check box.
     */
    private final TethysUICheckBox theLockedCheckBox;

    /**
     * The closed column.
     */
    private TethysUITableColumn<Boolean, MetisDataFieldId, T> theClosedColumn;

    /**
     * Are we showing closed accounts?
     */
    private boolean doShowClosed;

    /**
     * Constructor.
     * @param pView      the view
     * @param pEditSet   the editSet
     * @param pError     the error panel
     * @param pDataType  the dataType
     */
    protected MoneyWiseAssetTable(final MoneyWiseView pView,
                                  final PrometheusEditSet pEditSet,
                                  final MetisErrorPanel pError,
                                  final MetisListKey pDataType) {
        /* Store parameters */
        super(pView, pEditSet, pError, pDataType);

        /* Access Gui factory and table */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<MetisDataFieldId, T> myTable = getTable();

        /* Create new button */
        final TethysUIButton myNewButton = myGuiFactory.buttonFactory().newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create the CheckBox */
        theLockedCheckBox = myGuiFactory.controlFactory().newCheckBox(PROMPT_CLOSED);

        /* Create a filter panel */
        theFilterPanel = myGuiFactory.paneFactory().newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(theLockedCheckBox);
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myNewButton);

        /* Set table configuration */
        myTable.setDisabled(MoneyWiseAssetBase::isDisabled)
                .setComparator(MoneyWiseAssetBase::compareTo);

        /* Create the name column */
        myTable.declareStringColumn(PrometheusDataResource.DATAITEM_FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(MoneyWiseAssetBase::getName)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseAssetBase::setName, r, v));

        /* Create the Category column */
        myTable.declareScrollColumn(MoneyWiseBasicResource.CATEGORY_NAME, MoneyWiseAssetCategory.class)
                .setMenuConfigurator(this::buildCategoryMenu)
                .setCellValueFactory(MoneyWiseAssetBase::getCategory)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseAssetBase::setCategory, r, v));

        /* Create the description column */
        myTable.declareStringColumn(PrometheusDataResource.DATAITEM_FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(MoneyWiseAssetBase::getDesc)
                .setEditable(true)
                .setColumnWidth(WIDTH_DESC)
                .setOnCommit((r, v) -> updateField(MoneyWiseAssetBase::setDescription, r, v));

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theLockedCheckBox.getEventRegistrar().addEventListener(e -> setShowAll(theLockedCheckBox.isSelected()));
    }

    /**
     * finish the table.
     * @param pParent create parent column
     * @param pCurrency create currency column
     * @param pEvent create event column
     */
    protected void finishTable(final boolean pParent,
                               final boolean pCurrency,
                               final boolean pEvent) {
        /* Access Table */
        final TethysUITableManager<MetisDataFieldId, T> myTable = getTable();

        /* Create the parent column */
        if (pParent) {
            myTable.declareScrollColumn(MoneyWiseBasicResource.ASSET_PARENT, MoneyWisePayee.class)
                    .setMenuConfigurator(this::buildParentMenu)
                    .setCellValueFactory(MoneyWiseAssetBase::getParent)
                    .setEditable(true)
                    .setCellEditable(r -> !r.isActive())
                    .setColumnWidth(WIDTH_NAME)
                    .setOnCommit((r, v) -> updateField(MoneyWiseAssetBase::setParent, r, v));
        }

        /* Create the currency column */
        if (pCurrency) {
            myTable.declareScrollColumn(MoneyWiseStaticDataType.CURRENCY, MoneyWiseCurrency.class)
                    .setMenuConfigurator(this::buildCurrencyMenu)
                    .setCellValueFactory(MoneyWiseAssetBase::getAssetCurrency)
                    .setEditable(true)
                    .setCellEditable(r -> !r.isActive())
                    .setColumnWidth(WIDTH_CURR)
                    .setOnCommit((r, v) -> updateField(MoneyWiseAssetBase::setAssetCurrency, r, v));
        }

        /* Create the Closed column */
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myClosedMapSets = MoneyWiseIcon.configureLockedIconButton(getView().getGuiFactory());
        final TethysUITableColumn<Boolean, MetisDataFieldId, T> myClosedColumn
                = myTable.declareIconColumn(MoneyWiseBasicResource.ASSET_CLOSED, Boolean.class)
                .setIconMapSet(r -> myClosedMapSets.get(determineClosedState(r)))
                .setCellValueFactory(MoneyWiseAssetBase::isClosed)
                .setEditable(true)
                .setCellEditable(this::determineClosedState)
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(MoneyWiseAssetBase::setClosed, r, v));
        declareClosedColumn(myClosedColumn);
        setShowAll(false);

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(getView().getGuiFactory());
        myTable.declareIconColumn(PrometheusDataResource.DATAITEM_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Create the latest event column */
        if (pEvent) {
            myTable.declareDateColumn(MoneyWiseBasicResource.ASSET_LASTEVENT)
                    .setCellValueFactory(this::getLatestTranDate)
                    .setName(MoneyWiseUIResource.ASSET_COLUMN_LATEST.getValue())
                    .setEditable(false);
        }
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    public TethysUIBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    /**
     * Declare the closed column.
     * @param pColumn the column
     */
    protected void declareClosedColumn(final TethysUITableColumn<Boolean, MetisDataFieldId, T> pColumn) {
        theClosedColumn = pColumn;
    }

    /**
     * Determine closed state.
     * @param pAsset the asset
     * @return the state
     */
    protected boolean determineClosedState(final T pAsset) {
        return pAsset.isClosed() || !pAsset.isRelevant();
    }

    /**
     * Obtain the date of the latest transaction.
     * @param pAsset the asset
     * @return the date or null
     */
    protected OceanusDate getLatestTranDate(final T pAsset) {
        final MoneyWiseTransaction myTran = pAsset.getLatest();
        return myTran == null ? null : myTran.getDate();
    }

    /**
     * Set the showAll indicator.
     * @param pShowAll show closed accounts?
     */
    public void setShowAll(final boolean pShowAll) {
        doShowClosed = pShowAll;
        cancelEditing();
        getTable().setFilter(this::isFiltered);
        theClosedColumn.setVisible(pShowAll);
        restoreSelected();
    }

    /**
     * check whether we need to enable showAll.
     * @param pAsset the asset to check
     */
    protected void checkShowAll(final T pAsset) {
        /* If the item is closed, but we are not showing closed items */
        if (pAsset != null
                && pAsset.isClosed()
                && !theLockedCheckBox.isSelected()) {
            theLockedCheckBox.setSelected(true);
            setShowAll(true);
        }
    }

    /**
     * Build the Category list for the item.
     * @param pAsset the item
     * @param pMenu the menu to build
     */
    protected abstract void buildCategoryMenu(T pAsset,
                                              TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu);

    /**
     * Build the Parent list for the item.
     * @param pAsset the item
     * @param pMenu the menu to build
     */
    protected void buildParentMenu(final T pAsset,
                                   final TethysUIScrollMenu<MoneyWisePayee> pMenu) {
        /* No-Op */
    }

    /**
     * Build the currency list for the item.
     * @param pAsset the item
     * @param pMenu the menu to build
     */
    protected void buildCurrencyMenu(final T pAsset,
                                     final TethysUIScrollMenu<MoneyWiseCurrency> pMenu) {
        /* No-Op */
    }

    /**
     * New item.
     */
    protected abstract void addNewItem();

    @Override
    protected boolean isFiltered(final T pRow) {
        /* Handle filter */
        return super.isFiltered(pRow) && (doShowClosed || !pRow.isDisabled());
    }

    @Override
    protected String getInvalidNameChars() {
        return ":";
    }

    /**
     * Obtain a new asset nameSpaceIterator.
     * @return the iterator
     */
    protected Iterator<PrometheusDataItem> assetNameSpaceIterator() {
        return new MoneyWiseNameSpaceIterator(getEditSet(),
                MoneyWiseBasicDataType.PAYEE,
                MoneyWiseBasicDataType.DEPOSIT,
                MoneyWiseBasicDataType.CASH,
                MoneyWiseBasicDataType.LOAN,
                MoneyWiseBasicDataType.PORTFOLIO);
    }

    /**
     * is Valid data?
     * @param pNewData the new data
     * @param pClazz the class
     * @return error message or null
     */
    public String isValidData(final char[] pNewData,
                              final MoneyWiseAccountInfoClass pClazz) {
        /* Reject data that is too long */
        if (pNewData.length > pClazz.getMaximumLength()) {
            return "Data too long";
        }

        /* Valid notes */
        return null;
    }

    /**
     * is Valid symbol?
     * @param pNewSymbol the new symbol
     * @param pRow the row
     * @return error message or null
     */
    public String isValidSymbol(final String pNewSymbol,
                                final T pRow) {
        /* Reject null symbol */
        if (pNewSymbol == null) {
            return "Null Symbol not allowed";
        }

        /* Reject invalid symbol */
        if (!PrometheusDataItem.validString(pNewSymbol, getInvalidNameChars())) {
            return "Invalid characters in symbol";
        }

        /* Reject symbol that is too long */
        if (PrometheusDataItem.byteLength(pNewSymbol) > MoneyWiseSecurityType.SYMBOL_LEN) {
            return "Symbol too long";
        }

        /* Loop through the existing values */
        final Iterator<PrometheusDataItem> myIterator = nameSpaceIterator();
        while (myIterator.hasNext()) {
            final PrometheusDataItem myValue = myIterator.next();

            /* Ignore self and deleted */
            if (!(myValue instanceof MoneyWiseSecurity)
                    || myValue.isDeleted()
                    || myValue.equals(pRow)) {
                continue;
            }

            /* Check for duplicate */
            final MoneyWiseSecurity mySecurity = (MoneyWiseSecurity) myValue;
            if (pNewSymbol.equals(mySecurity.getSymbol())) {
                return "Duplicate symbol";
            }
        }

        /* Valid symbol */
        return null;
    }
}
