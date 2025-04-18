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
import net.sourceforge.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableManager;

/**
 * MoneyWise Dialog Base Table.
 * @param <T> the data type
 */
public abstract class MoneyWiseDialogTable<T extends PrometheusDataItem>
        extends MoneyWiseBaseTable<T> {
    /**
     * The panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The add panel.
     */
    private final TethysUIBoxPaneManager theAddPanel;

    /**
     * Constructor.
     * @param pView      the view
     * @param pEditSet   the editSet
     * @param pError     the error panel
     * @param pDataType  the dataType
     */
    protected MoneyWiseDialogTable(final MoneyWiseView pView,
                                   final PrometheusEditSet pEditSet,
                                   final MetisErrorPanel pError,
                                   final MetisListKey pDataType) {
        /* Store parameters */
        super(pView, pEditSet, pError, pDataType);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<MetisDataFieldId, T> myTable = getTable();

        /* Create new button */
        final TethysUIButton myNewButton = myGuiFactory.buttonFactory().newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create a filter panel */
        final TethysUIPaneFactory myPanes = myGuiFactory.paneFactory();
        theAddPanel = myPanes.newHBoxPane();
        theAddPanel.addSpacer();
        theAddPanel.addNode(myNewButton);

        /* Create panel */
        thePanel = myPanes.newBorderPane();
        thePanel.setCentre(myTable);
        thePanel.setNorth(theAddPanel);

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    /**
     * is the table active?
     * @return true/false
     */
    public boolean isViewActive() {
        return getTable().viewIterator().hasNext();
    }

    @Override
    public abstract void refreshData();

    /**
     * New item.
     */
    protected abstract void addNewItem();

    /**
     * Set whether the table is editable.
     * @param pEditable true/false
     */
    public void setEditable(final boolean pEditable) {
        /* Show/Hide columns/panels */
        theAddPanel.setVisible(pEditable);
    }

    /**
     * Refresh the table after an updateSet reWind.
     */
    public void refreshAfterUpdate() {
        updateTableData();
    }
}
