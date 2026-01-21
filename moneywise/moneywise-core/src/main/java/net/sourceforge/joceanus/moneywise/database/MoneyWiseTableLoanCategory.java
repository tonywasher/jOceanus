/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.database;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusDataStore;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusTableDefinition;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusTableDefinition.PrometheusSortOrder;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusTableEncrypted;

/**
 * TableEncrypted extension for Loan Category.
 *
 * @author Tony Washer
 */
public class MoneyWiseTableLoanCategory
        extends PrometheusTableEncrypted<MoneyWiseLoanCategory> {
    /**
     * The name of the Category table.
     */
    protected static final String TABLE_NAME = MoneyWiseLoanCategory.LIST_NAME;

    /**
     * Constructor.
     *
     * @param pDatabase the database control
     */
    protected MoneyWiseTableLoanCategory(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myCatCol = myTableDef.addReferenceColumn(MoneyWiseStaticDataType.LOANTYPE, MoneyWiseTableLoanCategoryType.TABLE_NAME);
        final PrometheusColumnDefinition myParentCol = myTableDef.addNullIntegerColumn(PrometheusDataResource.DATAGROUP_PARENT);
        myTableDef.addEncryptedColumn(PrometheusDataResource.DATAITEM_FIELD_NAME, PrometheusDataItem.NAMELEN);
        myTableDef.addNullEncryptedColumn(PrometheusDataResource.DATAITEM_FIELD_DESC, PrometheusDataItem.DESCLEN);

        /* Declare Sort Columns */
        myParentCol.setSortOrder(PrometheusSortOrder.DESCENDING);
        myCatCol.setSortOrder(PrometheusSortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getLoanCategories());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseLoanCategory.OBJECT_NAME);
        myValues.addValue(MoneyWiseStaticDataType.LOANTYPE, myTableDef.getIntegerValue(MoneyWiseStaticDataType.LOANTYPE));
        myValues.addValue(PrometheusDataResource.DATAGROUP_PARENT, myTableDef.getIntegerValue(PrometheusDataResource.DATAGROUP_PARENT));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myTableDef.getBinaryValue(PrometheusDataResource.DATAITEM_FIELD_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, myTableDef.getBinaryValue(PrometheusDataResource.DATAITEM_FIELD_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final MoneyWiseLoanCategory pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (MoneyWiseStaticDataType.LOANTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryTypeId());
        } else if (PrometheusDataResource.DATAGROUP_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentCategoryId());
        } else if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
