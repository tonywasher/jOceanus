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
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected MoneyWiseDataItemPanel(final JFieldManager pFieldMgr,
                                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                     final ErrorPanel pError) {
        super(pFieldMgr, pUpdateSet, pError);
    }

    @Override
    protected void declareGoToMenuBuilder(final JScrollMenuBuilder<ActionDetailEvent> pBuilder) {
        theGoToBuilder = pBuilder;
    }

    @Override
    protected void buildGoToEvent(final DataItem<MoneyWiseDataType> pItem) {
        /* Ignore null items */
        if (pItem == null) {
            return;
        }

        /* set default values */
        int myId = -1;
        String myName = null;

        /* Handle differing items */
        if (pItem instanceof StaticData) {
            StaticData<?, ?, ?> myStatic = (StaticData<?, ?, ?>) pItem;
            myId = MainTab.ACTION_VIEWSTATIC;
            myName = myStatic.getName();
        } else if (pItem instanceof AssetBase) {
            AssetBase<?> myAccount = (AssetBase<?>) pItem;
            myId = MainTab.ACTION_VIEWACCOUNT;
            myName = myAccount.getName();
        } else if (pItem instanceof CategoryBase) {
            CategoryBase<?, ?, ?> myCategory = (CategoryBase<?, ?, ?>) pItem;
            myId = MainTab.ACTION_VIEWCATEGORY;
            myName = myCategory.getName();
        } else if (pItem instanceof TaxYear) {
            TaxYear myYear = (TaxYear) pItem;
            myId = MainTab.ACTION_VIEWTAXYEAR;
            myName = myYear.getTaxYear().toString();
        } else if (pItem instanceof TransactionTag) {
            TransactionTag myTag = (TransactionTag) pItem;
            myId = MainTab.ACTION_VIEWTAG;
            myName = myTag.getName();
        }

        /* Add a prefix */
        myName = pItem.getItemType().toString() + ": " + myName;

        /* Build the item */
        ActionDetailEvent myEvent = new ActionDetailEvent(this, ActionEvent.ACTION_PERFORMED, myId, pItem);
        theGoToBuilder.addItem(myEvent, myName);
    }
}
