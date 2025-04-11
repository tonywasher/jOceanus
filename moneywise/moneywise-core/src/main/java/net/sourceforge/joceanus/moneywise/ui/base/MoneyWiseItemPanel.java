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
import net.sourceforge.joceanus.metis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.metis.list.MetisListKey;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.moneywise.lethe.ui.controls.MoneyWiseAnalysisSelect.MoneyWiseStatementSelect;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues.PrometheusInfoSetItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.prometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.prometheus.ui.panel.PrometheusDataItemPanel;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.base.TethysUIGenericWrapper;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollSubMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MoneyWise Data Item Panel.
 * @param <T> the item type
 */
public abstract class MoneyWiseItemPanel<T extends PrometheusDataItem>
        extends PrometheusDataItemPanel<T, MoneyWiseGoToId> {
    /**
     * Filter text.
     */
    private static final String FILTER_MENU = "Filter";

    /**
     * The Owning table.
     */
    private final MoneyWiseBaseTable<T> theOwner;

    /**
     * The DataItem GoToMenuMap.
     */
    private final List<PrometheusDataItem> theGoToItemList;

    /**
     * The Filter GoToMenuMap.
     */
    private final List<MoneyWiseAnalysisFilter<?, ?>> theGoToFilterList;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pEditSet the edit set
     * @param pOwner the owning table
     */
    protected MoneyWiseItemPanel(final TethysUIFactory<?> pFactory,
                                 final PrometheusEditSet pEditSet,
                                 final MoneyWiseBaseTable<T> pOwner) {
        super(pFactory, pEditSet, pOwner.getErrorPanel());
        theOwner = pOwner;
        theGoToFilterList = new ArrayList<>();
        theGoToItemList = new ArrayList<>();
        getFieldSet().setChanged(this::isFieldChanged);
    }

    /**
     * Obtain the owner.
     * @return the owner
     */
    protected MoneyWiseBaseTable<T> getOwner() {
        return theOwner;
    }

    /**
     * Set preferred Size.
     */
    void setPreferredSize() {
        /* we should take up a quarter if the standard panel dimensions */
        final int[] mySize = getFactory().getProgramDefinitions().getPanelDimensions();
        getFieldSet().setPreferredWidthAndHeight(mySize[0], mySize[1] >> 2);
    }

    /**
     * is field changed?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldChanged(final T pItem,
                                   final MetisDataFieldId pField) {
        /* If the field is a dataInfoClass as part of an infoSetItem */
        if (pField instanceof PrometheusDataInfoClass myClass
                && pItem instanceof PrometheusInfoSetItem myItem) {
            /* Check with the infoSet whether the field has changed */
            return myItem.getInfoSet().fieldChanged(myClass).isDifferent();
        }

        /* Look at the base values as a standard item */
        final MetisFieldVersionValues myBaseValues = getBaseValues();
        return pField != null
                && myBaseValues != null;
    }

    /**
     * is Valid name?
     * @param pNewName the new name
     * @return error message or null
     */
    public String isValidName(final String pNewName) {
        return theOwner.isValidName(pNewName, getItem());
    }

    /**
     * is Valid description?
     * @param pNewDesc the new description
     * @return error message or null
     */
    public String isValidDesc(final String pNewDesc) {
        return theOwner.isValidDesc(pNewDesc, getItem());
    }

    @Override
    protected void buildGoToMenu(final TethysUIScrollMenu<TethysUIGenericWrapper> pMenu) {
        /* Clear the goTo lists */
        theGoToFilterList.clear();
        theGoToItemList.clear();

        /* Declare the goTo items */
        declareGoToItems(getEditSet().hasUpdates());

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
    protected void declareGoToItem(final PrometheusDataItem pItem) {
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
    protected void declareGoToFilter(final MoneyWiseAnalysisFilter<?, ?> pFilter) {
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
     * Declare GoTo Filter.
     * @param pFilter the filter to declare
     */
    protected void declareGoToFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
        /* TODO */
    }

    /**
     * Process goTo items.
     * @param pMenu the menu
     */
    private void processGoToItems(final TethysUIScrollMenu<TethysUIGenericWrapper> pMenu) {
        /* Process goTo filters */
        processGoToFilters(pMenu);

        /* Create a simple map for top-level categories */
        final Map<MetisListKey, TethysUIScrollSubMenu<TethysUIGenericWrapper>> myMap = new HashMap<>();

        /* Loop through the items */
        for (PrometheusDataItem myItem : theGoToItemList) {
            /* Determine DataType and obtain parent menu */
            final MetisListKey myType = myItem.getItemType();
            final TethysUIScrollSubMenu<TethysUIGenericWrapper> myMenu = myMap.computeIfAbsent(myType, t -> pMenu.addSubMenu(myType.getItemName()));

            /* set default values */
            MoneyWiseGoToId myId = null;
            String myName = null;

            /* Handle differing items */
            if (myItem instanceof PrometheusStaticDataItem myStatic) {
                myId = MoneyWiseGoToId.STATIC;
                myName = myStatic.getName();
            } else if (myItem instanceof MoneyWiseAssetBase myAccount) {
                myId = MoneyWiseGoToId.ACCOUNT;
                myName = myAccount.getName();
            } else if (myItem instanceof MoneyWiseCategoryBase myCategory) {
                myId = MoneyWiseGoToId.CATEGORY;
                myName = myCategory.getName();
            } else if (myItem instanceof MoneyWiseRegion myRegion) {
                myId = MoneyWiseGoToId.REGION;
                myName = myRegion.getName();
            } else if (myItem instanceof MoneyWiseTransTag myTag) {
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
        for (MoneyWiseAnalysisFilter<?, ?> myFilter : theGoToFilterList) {
            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(FILTER_MENU);
            }

            /* Determine action */
            final MoneyWiseStatementSelect myStatement = new MoneyWiseStatementSelect(null, myFilter);
            final MoneyWiseGoToId myId = MoneyWiseGoToId.STATEMENT;

            /* Build the item */
            final PrometheusGoToEvent<MoneyWiseGoToId> myEvent = createGoToEvent(myId, myStatement);
            myMenu.getSubMenu().addItem(new TethysUIGenericWrapper(myEvent), myFilter.getName());
        }
    }
}
