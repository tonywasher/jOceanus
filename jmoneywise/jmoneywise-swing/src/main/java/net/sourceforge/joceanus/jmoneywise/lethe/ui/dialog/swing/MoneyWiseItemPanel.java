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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataItemPanel;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * MoneyWise Data Item Panel.
 * @param <T> the item type
 */
public abstract class MoneyWiseItemPanel<T extends DataItem<MoneyWiseDataType> & Comparable<? super T>>
        extends PrometheusDataItemPanel<T, MoneyWiseGoToId, MoneyWiseDataType> {
    /**
     * Filter text.
     */
    private static final String FILTER_MENU = "Filter";

    /**
     * The DataItem GoToMenuMap.
     */
    private final List<DataItem<MoneyWiseDataType>> theGoToItemList;

    /**
     * The Filter GoToMenuMap.
     */
    private final List<AnalysisFilter<?, ?>> theGoToFilterList;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected MoneyWiseItemPanel(final TethysSwingGuiFactory pFactory,
                                    final MetisFieldManager pFieldMgr,
                                    final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                    final MetisErrorPanel<JComponent, Icon> pError) {
        super(pFactory, pFieldMgr, pUpdateSet, pError);
        theGoToFilterList = new ArrayList<>();
        theGoToItemList = new ArrayList<>();
    }

    @Override
    protected void buildGoToMenu(final TethysScrollMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?> pMenu) {
        /* Clear the goTo lists */
        theGoToFilterList.clear();
        theGoToItemList.clear();
        pMenu.removeAllItems();

        /* Declare the goTo items */
        declareGoToItems(getUpdateSet().hasUpdates());

        /* Process the goTo items */
        processGoToItems(pMenu);
    }

    /**
     * Declare GoTo Items.
     * @param pUpdates are there active updates?
     */
    protected abstract void declareGoToItems(boolean pUpdates);

    /**
     * Declare GoTo Item.
     * @param pItem the item to declare
     */
    protected void declareGoToItem(final DataItem<MoneyWiseDataType> pItem) {
        /* Ignore null items */
        if (pItem == null) {
            return;
        }

        /* Ignore if the item is already listed */
        if (theGoToItemList.contains(pItem)) {
            return;
        }

        /* remember the item */
        theGoToItemList.add(pItem);
    }

    /**
     * Declare GoTo Filter.
     * @param pFilter the filter to declare
     */
    protected void declareGoToFilter(final AnalysisFilter<?, ?> pFilter) {
        /* Ignore null filters */
        if (pFilter == null) {
            return;
        }

        /* Ignore if the item is already listed */
        if (theGoToFilterList.contains(pFilter)) {
            return;
        }

        /* remember the item */
        theGoToFilterList.add(pFilter);
    }

    /**
     * Process goTo items.
     * @param pMenu the menu
     */
    private void processGoToItems(final TethysScrollMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?> pMenu) {
        /* Process goTo filters */
        processGoToFilters(pMenu);

        /* Create a simple map for top-level categories */
        Map<MoneyWiseDataType, TethysScrollSubMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?>> myMap = new EnumMap<>(MoneyWiseDataType.class);

        /* Loop through the items */
        Iterator<DataItem<MoneyWiseDataType>> myIterator = theGoToItemList.iterator();
        while (myIterator.hasNext()) {
            DataItem<MoneyWiseDataType> myItem = myIterator.next();

            /* Determine DataType and obtain parent menu */
            MoneyWiseDataType myType = myItem.getItemType();
            TethysScrollSubMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?> myMenu = myMap.get(myType);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(myType.getItemName());
                myMap.put(myType, myMenu);
            }

            /* set default values */
            MoneyWiseGoToId myId = null;
            String myName = null;

            /* Handle differing items */
            if (myItem instanceof StaticData) {
                StaticData<?, ?, ?> myStatic = (StaticData<?, ?, ?>) myItem;
                myId = MoneyWiseGoToId.STATIC;
                myName = myStatic.getName();
            } else if (myItem instanceof AssetBase) {
                AssetBase<?> myAccount = (AssetBase<?>) myItem;
                myId = MoneyWiseGoToId.ACCOUNT;
                myName = myAccount.getName();
            } else if (myItem instanceof CategoryBase) {
                CategoryBase<?, ?, ?> myCategory = (CategoryBase<?, ?, ?>) myItem;
                myId = MoneyWiseGoToId.CATEGORY;
                myName = myCategory.getName();
            } else if (myItem instanceof Region) {
                Region myRegion = (Region) myItem;
                myId = MoneyWiseGoToId.REGION;
                myName = myRegion.getName();
            } else if (myItem instanceof TransactionTag) {
                TransactionTag myTag = (TransactionTag) myItem;
                myId = MoneyWiseGoToId.TAG;
                myName = myTag.getName();
            }

            /* Build the item */
            PrometheusGoToEvent<MoneyWiseGoToId> myEvent = createGoToEvent(myId, myItem);
            myMenu.getSubMenu().addItem(myEvent, myName);
        }
    }

    /**
     * Process goTo filters.
     * @param pMenu the menu
     */
    private void processGoToFilters(final TethysScrollMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?> pMenu) {
        /* Create a simple map for top-level categories */
        TethysScrollSubMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?> myMenu = null;

        /* Loop through the items */
        Iterator<AnalysisFilter<?, ?>> myIterator = theGoToFilterList.iterator();
        while (myIterator.hasNext()) {
            AnalysisFilter<?, ?> myFilter = myIterator.next();

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(FILTER_MENU);
            }

            /* Determine action */
            StatementSelect<JComponent, Icon> myStatement = new StatementSelect<>(null, myFilter);
            MoneyWiseGoToId myId = MoneyWiseGoToId.STATEMENT;

            /* Build the item */
            PrometheusGoToEvent<MoneyWiseGoToId> myEvent = createGoToEvent(myId, myStatement);
            myMenu.getSubMenu().addItem(myEvent, myFilter.getName());
        }
    }
}
