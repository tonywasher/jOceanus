/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.ui;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;

/**
 * MoneyWise Asset Table.
 * @param <T> the Category Data type
 */
public abstract class MoneyWiseAssetTable<T extends AssetBase<T>>
        extends MoneyWiseBaseTable<T> {
    /**
     * ShowClosed prompt.
     */
    private static final String PROMPT_CLOSED = MoneyWiseUIResource.UI_PROMPT_SHOWCLOSED.getValue();

    /**
     * The filter panel.
     */
    private final TethysBoxPaneManager theFilterPanel;

    /**
     * The locked check box.
     */
    private final TethysCheckBox theLockedCheckBox;

    /**
     * The closed column.
     */
    private TethysTableColumn<Boolean, MetisLetheField, T> theClosedColumn;

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
     */
    MoneyWiseAssetTable(final MoneyWiseView pView,
                        final UpdateSet<MoneyWiseDataType> pUpdateSet,
                        final MetisErrorPanel pError,
                        final MoneyWiseDataType pDataType) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, pDataType);

        /* Access Gui factory */
        final TethysGuiFactory myGuiFactory = pView.getGuiFactory();

        /* Create new button */
        final TethysButton myNewButton = myGuiFactory.newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create the CheckBox */
        theLockedCheckBox = myGuiFactory.newCheckBox(PROMPT_CLOSED);

        /* Create a filter panel */
        theFilterPanel = myGuiFactory.newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(theLockedCheckBox);
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myNewButton);

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theLockedCheckBox.getEventRegistrar().addEventListener(e -> setShowAll(theLockedCheckBox.isSelected()));
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected TethysBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    /**
     * Declare the closed column.
     * @param pColumn the column
     */
    protected void declareClosedColumn(final TethysTableColumn<Boolean, MetisLetheField, T> pColumn) {
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
    protected void setShowAll(final boolean pShowAll) {
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
        if (pAsset.isClosed()
                && !theLockedCheckBox.isSelected()) {
            theLockedCheckBox.setSelected(true);
            setShowAll(true);
        }
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
