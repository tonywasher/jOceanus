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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.ui.panel.PrometheusDataItemPanel;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIGenericWrapper;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollSubMenu;

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
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected MoneyWiseItemPanel(final TethysUIFactory<?> pFactory,
                                 final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                 final MetisErrorPanel pError) {
        super(pFactory, pUpdateSet, pError);
        theGoToFilterList = new ArrayList<>();
        theGoToItemList = new ArrayList<>();
        getFieldSet().setChanged(this::isFieldChanged);
    }

    /**
     * is field changed?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldChanged(final T pItem,
                                   final PrometheusDataFieldId pField) {
        final MetisLetheField myField = pField.getLetheField();
        final MetisValueSet myBaseValues = getBaseValues();
        return myField != null
                && myBaseValues != null
                && pItem.getValueSet().fieldChanged(myField, myBaseValues).isDifferent();
    }

    @Override
    protected void buildGoToMenu(final TethysUIScrollMenu<TethysUIGenericWrapper> pMenu) {
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
    private void processGoToItems(final TethysUIScrollMenu<TethysUIGenericWrapper> pMenu) {
        /* Process goTo filters */
        processGoToFilters(pMenu);

        /* Create a simple map for top-level categories */
        final Map<MoneyWiseDataType, TethysUIScrollSubMenu<TethysUIGenericWrapper>> myMap = new EnumMap<>(MoneyWiseDataType.class);

        /* Loop through the items */
        for (DataItem<MoneyWiseDataType> myItem : theGoToItemList) {
            /* Determine DataType and obtain parent menu */
            final MoneyWiseDataType myType = myItem.getItemType();
            TethysUIScrollSubMenu<TethysUIGenericWrapper> myMenu = myMap.get(myType);

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
                final StaticData<?, ?, ?> myStatic = (StaticData<?, ?, ?>) myItem;
                myId = MoneyWiseGoToId.STATIC;
                myName = myStatic.getName();
            } else if (myItem instanceof AssetBase) {
                final AssetBase<?, ?> myAccount = (AssetBase<?, ?>) myItem;
                myId = MoneyWiseGoToId.ACCOUNT;
                myName = myAccount.getName();
            } else if (myItem instanceof CategoryBase) {
                final CategoryBase<?, ?, ?> myCategory = (CategoryBase<?, ?, ?>) myItem;
                myId = MoneyWiseGoToId.CATEGORY;
                myName = myCategory.getName();
            } else if (myItem instanceof Region) {
                final Region myRegion = (Region) myItem;
                myId = MoneyWiseGoToId.REGION;
                myName = myRegion.getName();
            } else if (myItem instanceof TransactionTag) {
                final TransactionTag myTag = (TransactionTag) myItem;
                myId = MoneyWiseGoToId.TAG;
                myName = myTag.getName();
            }

            /* Build the item */
            final PrometheusGoToEvent<MoneyWiseGoToId> myEvent = createGoToEvent(myId, myItem);
            myMenu.getSubMenu().addItem(new TethysUIGenericWrapper(myEvent), myName);
        }
    }

    /**
     * Process goTo filters.
     * @param pMenu the menu
     */
    private void processGoToFilters(final TethysUIScrollMenu<TethysUIGenericWrapper> pMenu) {
        /* Create a simple map for top-level categories */
        TethysUIScrollSubMenu<TethysUIGenericWrapper> myMenu = null;

        /* Loop through the items */
        for (AnalysisFilter<?, ?> myFilter : theGoToFilterList) {
            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(FILTER_MENU);
            }

            /* Determine action */
            final StatementSelect myStatement = new StatementSelect(null, myFilter);
            final MoneyWiseGoToId myId = MoneyWiseGoToId.STATEMENT;

            /* Build the item */
            final PrometheusGoToEvent<MoneyWiseGoToId> myEvent = createGoToEvent(myId, myStatement);
            myMenu.getSubMenu().addItem(new TethysUIGenericWrapper(myEvent), myFilter.getName());
        }
    }
}
