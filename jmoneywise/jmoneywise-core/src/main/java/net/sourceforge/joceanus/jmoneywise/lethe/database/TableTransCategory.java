/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition.SortOrder;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for Transaction Category.
 * @author Tony Washer
 */
public class TableTransCategory
        extends PrometheusTableEncrypted<TransactionCategory, MoneyWiseDataType> {
    /**
     * The name of the Category table.
     */
    protected static final String TABLE_NAME = TransactionCategory.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableTransCategory(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myCatCol = myTableDef.addReferenceColumn(TransactionCategory.FIELD_CATTYPE, TableTransCategoryType.TABLE_NAME);
        final PrometheusColumnDefinition myParentCol = myTableDef.addNullIntegerColumn(TransactionCategory.FIELD_PARENT);
        myTableDef.addEncryptedColumn(TransactionCategory.FIELD_NAME, StaticData.NAMELEN);
        myTableDef.addNullEncryptedColumn(TransactionCategory.FIELD_DESC, StaticData.DESCLEN);

        /* Declare Sort Columns */
        myParentCol.setSortOrder(SortOrder.DESCENDING);
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getTransCategories());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(TransactionCategory.OBJECT_NAME);
        myValues.addValue(TransactionCategory.FIELD_CATTYPE, myTableDef.getIntegerValue(TransactionCategory.FIELD_CATTYPE));
        myValues.addValue(TransactionCategory.FIELD_PARENT, myTableDef.getIntegerValue(TransactionCategory.FIELD_PARENT));
        myValues.addValue(TransactionCategory.FIELD_NAME, myTableDef.getBinaryValue(TransactionCategory.FIELD_NAME));
        myValues.addValue(TransactionCategory.FIELD_DESC, myTableDef.getBinaryValue(TransactionCategory.FIELD_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final TransactionCategory pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
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
