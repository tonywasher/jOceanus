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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ids.MoneyWiseTagDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.jprometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;

/**
 * Panel to display/edit/create a TransactionTag.
 */
public class MoneyWiseTagPanel
        extends MoneyWiseItemPanel<TransactionTag> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public MoneyWiseTagPanel(final TethysUIFactory<?> pFactory,
                             final UpdateSet pUpdateSet,
                             final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pUpdateSet, pError);

        /* Access the fieldSet */
        final PrometheusFieldSet<TransactionTag> myFieldSet = getFieldSet();

        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Assign the fields to the panel */
        myFieldSet.addField(MoneyWiseTagDataId.NAME, myName, TransactionTag::getName);
        myFieldSet.addField(MoneyWiseTagDataId.DESC, myDesc, TransactionTag::getDesc);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final TransactionTag myItem = getItem();
        if (myItem != null) {
            final TransactionTagList myTags = getDataList(MoneyWiseDataType.TRANSTAG, TransactionTagList.class);
            setItem(myTags.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final PrometheusFieldSet<TransactionTag> myFieldSet = getFieldSet();

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || getItem().getDesc() != null;
        myFieldSet.setFieldVisible(MoneyWiseTagDataId.DESC, bShowDesc);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final PrometheusDataFieldId myField = pUpdate.getFieldId();
        final TransactionTag myTag = getItem();

        /* Process updates */
        if (MoneyWiseTagDataId.NAME.equals(myField)) {
            /* Update the Name */
            myTag.setName(pUpdate.getValue(String.class));
       } else if (MoneyWiseTagDataId.DESC.equals(myField)) {
            /* Update the Description */
            myTag.setDescription(pUpdate.getValue(String.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        /* No GoTo items */
    }
}
