/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.dialog.swing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.AnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.ui.swing.MainTab;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.ui.swing.DataItemPanel;
import net.sourceforge.joceanus.jprometheus.ui.swing.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollMenu;

/**
 * MoneyWise Data Item Panel.
 * @param <T> the item type
 */
public abstract class MoneyWiseDataItemPanel<T extends DataItem<MoneyWiseDataType> & Comparable<? super T>>
        extends DataItemPanel<T, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5042288497641543026L;

    /**
     * Filter text.
     */
    private static final String FILTER_MENU = "Filter";

    /**
     * The GoToMenuBuilder.
     */
    private transient JScrollMenuBuilder<PrometheusGoToEvent> theGoToBuilder;

    /**
     * The DataItem GoToMenuMap.
     */
    private final transient List<DataItem<MoneyWiseDataType>> theGoToItemList;

    /**
     * The Filter GoToMenuMap.
     */
    private final transient List<AnalysisFilter<?, ?>> theGoToFilterList;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected MoneyWiseDataItemPanel(final MetisFieldManager pFieldMgr,
                                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                     final ErrorPanel pError) {
        super(pFieldMgr, pUpdateSet, pError);
        theGoToFilterList = new ArrayList<>();
        theGoToItemList = new ArrayList<>();
    }

    @Override
    protected void declareGoToMenuBuilder(final JScrollMenuBuilder<PrometheusGoToEvent> pBuilder) {
        theGoToBuilder = pBuilder;
    }

    @Override
    protected void buildGoToMenu() {
        /* Clear the goTo lists */
        theGoToFilterList.clear();
        theGoToItemList.clear();

        /* Declare the goTo items */
        declareGoToItems(getUpdateSet().hasUpdates());

        /* Process the goTo items */
        processGoToItems();
    }

    /**
     * Declare GoTo Items.
     * @param pUpdates are there active updates?
     */
    protected abstract void declareGoToItems(final boolean pUpdates);

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
     */
    private void processGoToItems() {
        /* Process goTo filters */
        processGoToFilters();

        /* Create a simple map for top-level categories */
        Map<MoneyWiseDataType, JScrollMenu> myMap = new EnumMap<>(MoneyWiseDataType.class);

        /* Loop through the items */
        Iterator<DataItem<MoneyWiseDataType>> myIterator = theGoToItemList.iterator();
        while (myIterator.hasNext()) {
            DataItem<MoneyWiseDataType> myItem = myIterator.next();

            /* Determine DataType and obtain parent menu */
            MoneyWiseDataType myType = myItem.getItemType();
            JScrollMenu myMenu = myMap.get(myType);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = theGoToBuilder.addSubMenu(myType.getItemName());
                myMap.put(myType, myMenu);
            }

            /* set default values */
            int myId = -1;
            String myName = null;

            /* Handle differing items */
            if (myItem instanceof StaticData) {
                StaticData<?, ?, ?> myStatic = (StaticData<?, ?, ?>) myItem;
                myId = MainTab.ACTION_VIEWSTATIC;
                myName = myStatic.getName();
            } else if (myItem instanceof AssetBase) {
                AssetBase<?> myAccount = (AssetBase<?>) myItem;
                myId = MainTab.ACTION_VIEWACCOUNT;
                myName = myAccount.getName();
            } else if (myItem instanceof CategoryBase) {
                CategoryBase<?, ?, ?> myCategory = (CategoryBase<?, ?, ?>) myItem;
                myId = MainTab.ACTION_VIEWCATEGORY;
                myName = myCategory.getName();
            } else if (myItem instanceof TaxYear) {
                TaxYear myYear = (TaxYear) myItem;
                myId = MainTab.ACTION_VIEWTAXYEAR;
                myName = myYear.getTaxYear().toString();
            } else if (myItem instanceof TransactionTag) {
                TransactionTag myTag = (TransactionTag) myItem;
                myId = MainTab.ACTION_VIEWTAG;
                myName = myTag.getName();
            }

            /* Build the item */
            PrometheusGoToEvent myEvent = createGoToEvent(myId, myItem);
            theGoToBuilder.addItem(myMenu, myEvent, myName);
        }
    }

    /**
     * Process goTo filters.
     * @param pItem
     */
    private void processGoToFilters() {
        /* Create a simple map for top-level categories */
        JScrollMenu myMenu = null;

        /* Loop through the items */
        Iterator<AnalysisFilter<?, ?>> myIterator = theGoToFilterList.iterator();
        while (myIterator.hasNext()) {
            AnalysisFilter<?, ?> myFilter = myIterator.next();

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = theGoToBuilder.addSubMenu(FILTER_MENU);
            }

            /* Determine action */
            StatementSelect myStatement = new StatementSelect(null, myFilter);
            int myId = MainTab.ACTION_VIEWSTATEMENT;

            /* Build the item */
            PrometheusGoToEvent myEvent = createGoToEvent(myId, myStatement);
            theGoToBuilder.addItem(myMenu, myEvent, myFilter.getName());
        }
    }
}
