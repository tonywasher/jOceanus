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
package net.sourceforge.joceanus.jmoneywise.database;

import javax.swing.SortOrder;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for Transaction Category.
 * @author Tony Washer
 */
public class TableTransCategory
        extends TableEncrypted<TransactionCategory, MoneyWiseDataType> {
    /**
     * The name of the Category table.
     */
    protected static final String TABLE_NAME = TransactionCategory.LIST_NAME;

    /**
     * The category list.
     */
    private TransactionCategoryList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableTransCategory(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myCatCol = myTableDef.addReferenceColumn(TransactionCategory.FIELD_CATTYPE, TableTransCategoryType.TABLE_NAME);
        ColumnDefinition myParentCol = myTableDef.addNullIntegerColumn(TransactionCategory.FIELD_PARENT);
        myTableDef.addEncryptedColumn(TransactionCategory.FIELD_NAME, StaticData.NAMELEN);
        myTableDef.addNullEncryptedColumn(TransactionCategory.FIELD_DESC, StaticData.DESCLEN);

        /* Declare Sort Columns */
        myParentCol.setSortOrder(SortOrder.DESCENDING);
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getTransCategories();
        setList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(TransactionCategory.OBJECT_NAME);
        myValues.addValue(TransactionCategory.FIELD_CATTYPE, myTableDef.getIntegerValue(TransactionCategory.FIELD_CATTYPE));
        myValues.addValue(TransactionCategory.FIELD_PARENT, myTableDef.getIntegerValue(TransactionCategory.FIELD_PARENT));
        myValues.addValue(TransactionCategory.FIELD_NAME, myTableDef.getBinaryValue(TransactionCategory.FIELD_NAME));
        myValues.addValue(TransactionCategory.FIELD_DESC, myTableDef.getBinaryValue(TransactionCategory.FIELD_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final TransactionCategory pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
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

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Validate the categories */
        theList.validateOnLoad();
    }
}