/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCategoryBase;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseRegion;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.controls.MoneyWiseAnalysisSelect.MoneyWiseStatementSelect;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusListKey;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.ui.panel.PrometheusDataItemPanel;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusEditSet;
import net.sourceforge.joceanus.jprometheus.atlas.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIGenericWrapper;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollSubMenu;

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
     * @param pError the error panel
     */
    protected MoneyWiseItemPanel(final TethysUIFactory<?> pFactory,
                                 final PrometheusEditSet pEditSet,
                                 final MetisErrorPanel pError) {
        super(pFactory, pEditSet, pError);
        theGoToFilterList = new ArrayList<>();
        theGoToItemList = new ArrayList<>();
        getFieldSet().setChanged(this::isFieldChanged);
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
        final MetisFieldVersionValues myBaseValues = getBaseValues();
        return pField != null
                && myBaseValues != null
                && pItem.getValues().fieldChanged(pField, myBaseValues).isDifferent();
    }

    @Override
    protected void buildGoToMenu(final TethysUIScrollMenu<TethysUIGenericWrapper> pMenu) {
        /* Clear the goTo lists */
        theGoToFilterList.clear();
        theGoToItemList.clear();
        pMenu.removeAllItems();

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
     * Process goTo items.
     * @param pMenu the menu
     */
    private void processGoToItems(final TethysUIScrollMenu<TethysUIGenericWrapper> pMenu) {
        /* Process goTo filters */
        processGoToFilters(pMenu);

        /* Create a simple map for top-level categories */
        final Map<PrometheusListKey, TethysUIScrollSubMenu<TethysUIGenericWrapper>> myMap = new HashMap<>();

        /* Loop through the items */
        for (PrometheusDataItem myItem : theGoToItemList) {
            /* Determine DataType and obtain parent menu */
            final PrometheusListKey myType = myItem.getItemType();
            final TethysUIScrollSubMenu<TethysUIGenericWrapper> myMenu = myMap.computeIfAbsent(myType, t -> pMenu.addSubMenu(myType.getItemName()));

            /* set default values */
            MoneyWiseGoToId myId = null;
            String myName = null;

            /* Handle differing items */
            if (myItem instanceof PrometheusStaticDataItem) {
                final PrometheusStaticDataItem myStatic = (PrometheusStaticDataItem) myItem;
                myId = MoneyWiseGoToId.STATIC;
                myName = myStatic.getName();
            } else if (myItem instanceof MoneyWiseAssetBase) {
                final MoneyWiseAssetBase myAccount = (MoneyWiseAssetBase) myItem;
                myId = MoneyWiseGoToId.ACCOUNT;
                myName = myAccount.getName();
            } else if (myItem instanceof MoneyWiseCategoryBase) {
                final MoneyWiseCategoryBase myCategory = (MoneyWiseCategoryBase) myItem;
                myId = MoneyWiseGoToId.CATEGORY;
                myName = myCategory.getName();
            } else if (myItem instanceof MoneyWiseRegion) {
                final MoneyWiseRegion myRegion = (MoneyWiseRegion) myItem;
                myId = MoneyWiseGoToId.REGION;
                myName = myRegion.getName();
            } else if (myItem instanceof MoneyWiseTransTag) {
                final MoneyWiseTransTag myTag = (MoneyWiseTransTag) myItem;
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
