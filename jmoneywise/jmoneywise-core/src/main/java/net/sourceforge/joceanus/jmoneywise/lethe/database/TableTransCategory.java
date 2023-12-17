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
package net.sourceforge.joceanus.jmoneywise.lethe.database;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition.SortOrder;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for Transaction Category.
 * @author Tony Washer
 */
public class TableTransCategory
        extends PrometheusXTableEncrypted<TransactionCategory> {
    /**
     * The name of the Category table.
     */
    protected static final String TABLE_NAME = TransactionCategory.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableTransCategory(final PrometheusXDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusXColumnDefinition myCatCol = myTableDef.addReferenceColumn(TransactionCategory.FIELD_CATTYPE, TableTransCategoryType.TABLE_NAME);
        final PrometheusXColumnDefinition myParentCol = myTableDef.addNullIntegerColumn(TransactionCategory.FIELD_PARENT);
        myTableDef.addEncryptedColumn(TransactionCategory.FIELD_NAME, StaticDataItem.NAMELEN);
        myTableDef.addNullEncryptedColumn(TransactionCategory.FIELD_DESC, StaticDataItem.DESCLEN);

        /* Declare Sort Columns */
        myParentCol.setSortOrder(SortOrder.DESCENDING);
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getTransCategories());
    }

    @Override
    protected DataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues myValues = getRowValues(TransactionCategory.OBJECT_NAME);
        myValues.addValue(TransactionCategory.FIELD_CATTYPE, myTableDef.getIntegerValue(TransactionCategory.FIELD_CATTYPE));
        myValues.addValue(TransactionCategory.FIELD_PARENT, myTableDef.getIntegerValue(TransactionCategory.FIELD_PARENT));
        myValues.addValue(TransactionCategory.FIELD_NAME, myTableDef.getBinaryValue(TransactionCategory.FIELD_NAME));
        myValues.addValue(TransactionCategory.FIELD_DESC, myTableDef.getBinaryValue(TransactionCategory.FIELD_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final TransactionCategory pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusXTableDefinition myTableDef = getTableDef();
        if (TransactionCategory.FIELD_CATTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryTypeId());
        } else if (TransactionCategory.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentCategoryId());
        } else if (TransactionCategory.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (TransactionCategory.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
