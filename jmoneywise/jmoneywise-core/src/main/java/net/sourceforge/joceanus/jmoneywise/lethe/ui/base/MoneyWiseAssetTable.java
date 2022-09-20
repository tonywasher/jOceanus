/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.base;

import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWiseAssetDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUICheckBox;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableColumn;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * MoneyWise Asset Table.
 * @param <T> the Asset Data type
 * @param <C> the Category Data type
 */
public abstract class MoneyWiseAssetTable<T extends AssetBase<T, C>, C>
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
    private TethysUITableColumn<Boolean, PrometheusDataFieldId, T> theClosedColumn;

    /**
     * Are we showing closed accounts?
     */
    private boolean doShowClosed;

    /**
     * Constructor.
     * @param pView      the view
     * @param pUpdateSet the updateSet
     * @param pError     the error panel
     * @param pDataType  the dataType
     * @param pCategoryClass the class of the category type
     */
    protected MoneyWiseAssetTable(final MoneyWiseView pView,
                                  final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                  final MetisErrorPanel pError,
                                  final MoneyWiseDataType pDataType,
                                  final Class<C> pCategoryClass) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, pDataType);

        /* Access Gui factory and table */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<PrometheusDataFieldId, T> myTable = getTable();

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
        myTable.setDisabled(AssetBase::isDisabled)
               .setComparator(AssetBase::compareTo);

        /* Create the name column */
        myTable.declareStringColumn(MoneyWiseAssetDataId.NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(AssetBase::getName)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(AssetBase::setName, r, v));

        /* Create the Category column */
        myTable.declareScrollColumn(MoneyWiseAssetDataId.CATEGORY, pCategoryClass)
                .setMenuConfigurator(this::buildCategoryMenu)
                .setCellValueFactory(AssetBase::getCategory)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(AssetBase::setCategory, r, v));

        /* Create the description column */
        myTable.declareStringColumn(MoneyWiseAssetDataId.DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(AssetBase::getDesc)
                .setEditable(true)
                .setColumnWidth(WIDTH_DESC)
                .setOnCommit((r, v) -> updateField(AssetBase::setDescription, r, v));

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
        final TethysUITableManager<PrometheusDataFieldId, T> myTable = getTable();

        /* Create the parent column */
        if (pParent) {
            myTable.declareScrollColumn(MoneyWiseAssetDataId.PARENT, Payee.class)
                    .setMenuConfigurator(this::buildParentMenu)
                    .setCellValueFactory(AssetBase::getParent)
                    .setEditable(true)
                    .setCellEditable(r -> !r.isActive())
                    .setColumnWidth(WIDTH_NAME)
                    .setOnCommit((r, v) -> updateField(AssetBase::setParent, r, v));
        }

        /* Create the currency column */
        if (pCurrency) {
            myTable.declareScrollColumn(MoneyWiseAssetDataId.CURRENCY, AssetCurrency.class)
                    .setMenuConfigurator(this::buildCurrencyMenu)
                    .setCellValueFactory(AssetBase::getAssetCurrency)
                    .setEditable(true)
                    .setCellEditable(r -> !r.isActive())
                    .setColumnWidth(WIDTH_CURR)
                    .setOnCommit((r, v) -> updateField(AssetBase::setAssetCurrency, r, v));
        }

        /* Create the Closed column */
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myClosedMapSets = MoneyWiseIcon.configureLockedIconButton(getView().getGuiFactory());
        final TethysUITableColumn<Boolean, PrometheusDataFieldId, T> myClosedColumn
                = myTable.declareIconColumn(MoneyWiseAssetDataId.CLOSED, Boolean.class)
                .setIconMapSet(r -> myClosedMapSets.get(determineClosedState(r)))
                .setCellValueFactory(AssetBase::isClosed)
                .setEditable(true)
                .setCellEditable(this::determineClosedState)
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(AssetBase::setClosed, r, v));
        declareClosedColumn(myClosedColumn);
        setShowAll(false);

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(getView().getGuiFactory());
        myTable.declareIconColumn(PrometheusDataId.TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Create the latest event column */
        if (pEvent) {
            myTable.declareDateColumn(MoneyWiseAssetDataId.EVTLAST)
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
    protected void declareClosedColumn(final TethysUITableColumn<Boolean, PrometheusDataFieldId, T> pColumn) {
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
    protected TethysDate getLatestTranDate(final T pAsset) {
        final Transaction myTran = pAsset.getLatest();
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
                                              TethysUIScrollMenu<C> pMenu);

    /**
     * Build the Parent list for the item.
     * @param pAsset the item
     * @param pMenu the menu to build
     */
    protected void buildParentMenu(final T pAsset,
                                   final TethysUIScrollMenu<Payee> pMenu) {
        /* No-Op */
    }

    /**
     * Build the currency list for the item.
     * @param pAsset the item
     * @param pMenu the menu to build
     */
    protected void buildCurrencyMenu(final T pAsset,
                                     final TethysUIScrollMenu<AssetCurrency> pMenu) {
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
}
