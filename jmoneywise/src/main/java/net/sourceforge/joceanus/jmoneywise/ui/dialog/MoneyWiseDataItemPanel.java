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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.ui.MainTab;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jprometheus.ui.DataItemPanel;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;

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
     * The GoToMenuBuilder.
     */
    private JScrollMenuBuilder<ActionDetailEvent> theGoToBuilder;

    /**
     * The GoToMenuMap.
     */
    private final transient List<DataItem<MoneyWiseDataType>> theGoToList;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected MoneyWiseDataItemPanel(final JFieldManager pFieldMgr,
                                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                     final ErrorPanel pError) {
        super(pFieldMgr, pUpdateSet, pError);
        theGoToList = new ArrayList<DataItem<MoneyWiseDataType>>();
    }

    @Override
    protected void declareGoToMenuBuilder(final JScrollMenuBuilder<ActionDetailEvent> pBuilder) {
        theGoToBuilder = pBuilder;
    }

    @Override
    protected void buildGoToMenu() {
        /* Clear the goTo list */
        theGoToList.clear();

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
        if (theGoToList.contains(pItem)) {
            return;
        }

        /* remember the item */
        theGoToList.add(pItem);
    }

    /**
     * Process goTo items.
     * @param pItem
     */
    private void processGoToItems() {
        /* Create a simple map for top-level categories */
        Map<MoneyWiseDataType, JScrollMenu> myMap = new HashMap<MoneyWiseDataType, JScrollMenu>();

        /* Loop through the items */
        Iterator<DataItem<MoneyWiseDataType>> myIterator = theGoToList.iterator();
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
            ActionDetailEvent myEvent = new ActionDetailEvent(this, ActionEvent.ACTION_PERFORMED, myId, myItem);
            theGoToBuilder.addItem(myMenu, myEvent, myName);
        }
    }
}
