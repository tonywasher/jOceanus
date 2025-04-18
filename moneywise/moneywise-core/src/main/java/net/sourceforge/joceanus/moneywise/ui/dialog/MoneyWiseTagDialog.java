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
package net.sourceforge.joceanus.moneywise.ui.dialog;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag.MoneyWiseTransTagList;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldFactory;

/**
 * Panel to display/edit/create a TransactionTag.
 */
public class MoneyWiseTagDialog
        extends MoneyWiseItemPanel<MoneyWiseTransTag> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pEditSet the edit set
     * @param pOwner the owning table
     */
    public MoneyWiseTagDialog(final TethysUIFactory<?> pFactory,
                              final PrometheusEditSet pEditSet,
                              final MoneyWiseBaseTable<MoneyWiseTransTag> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);

        /* Access the fieldSet */
        final PrometheusFieldSet<MoneyWiseTransTag> myFieldSet = getFieldSet();

        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Assign the fields to the panel */
        myFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWiseTransTag::getName);
        myFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWiseTransTag::getDesc);

        /* Configure name checks */
        myName.setValidator(this::isValidName);
        myName.setReporter(pOwner::showValidateError);

        /* Configure description checks */
        myDesc.setValidator(this::isValidDesc);
        myDesc.setReporter(pOwner::showValidateError);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final MoneyWiseTransTag myItem = getItem();
        if (myItem != null) {
            final MoneyWiseTransTagList myTags = getDataList(MoneyWiseBasicDataType.TRANSTAG, MoneyWiseTransTagList.class);
            setItem(myTags.findItemById(myItem.getIndexedId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final PrometheusFieldSet<MoneyWiseTransTag> myFieldSet = getFieldSet();

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || getItem().getDesc() != null;
        myFieldSet.setFieldVisible(PrometheusDataResource.DATAITEM_FIELD_DESC, bShowDesc);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWiseTransTag myTag = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the Name */
            myTag.setName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            myTag.setDescription(pUpdate.getValue(String.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        /* No GoTo items */
    }
}
