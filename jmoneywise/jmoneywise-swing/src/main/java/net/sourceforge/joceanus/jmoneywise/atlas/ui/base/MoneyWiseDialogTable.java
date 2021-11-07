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
package net.sourceforge.joceanus.jmoneywise.atlas.ui.base;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise Dialog Base Table.
 * @param <T> the data type
 */
public abstract class MoneyWiseDialogTable<T extends DataItem<MoneyWiseDataType> & Comparable<? super T>>
        extends MoneyWiseBaseTable<T> {
    /**
     * The panel.
     */
    private final TethysBorderPaneManager thePanel;

    /**
     * The add panel.
     */
    private final TethysBoxPaneManager theAddPanel;

    /**
     * Constructor.
     * @param pView      the view
     * @param pUpdateSet the updateSet
     * @param pError     the error panel
     * @param pDataType  the dataType
     */
    protected MoneyWiseDialogTable(final MoneyWiseView pView,
                                   final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                   final MetisErrorPanel pError,
                                   final MoneyWiseDataType pDataType) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, pDataType);

        /* Access Gui factory */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, T> myTable = getTable();

        /* Create new button */
        final TethysButton myNewButton = myGuiFactory.newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create a filter panel */
        theAddPanel = myGuiFactory.newHBoxPane();
        theAddPanel.addSpacer();
        theAddPanel.addNode(myNewButton);

        /* Create panel */
        thePanel = myGuiFactory.newBorderPane();
        thePanel.setCentre(myTable);
        thePanel.setNorth(theAddPanel);

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public TethysNode getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * is the table empty?
     * @return true/false
     */
    public boolean isViewEmpty() {
        return !getTable().viewIterator().hasNext();
    }

    /**
     * Refresh data.
     */
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
        //theActiveColumn.setVisible(pEditable);
    }

    /**
     * Refresh the table after an updateSet reWind.
     */
    public void refreshAfterUpdate() {
        getTable().fireTableDataChanged();
    }
}
