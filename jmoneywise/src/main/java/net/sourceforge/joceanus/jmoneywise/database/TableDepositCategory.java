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
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for Deposit Category.
 * @author Tony Washer
 */
public class TableDepositCategory
        extends TableEncrypted<DepositCategory, MoneyWiseDataType> {
    /**
     * The name of the Category table.
     */
    protected static final String TABLE_NAME = DepositCategory.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableDepositCategory(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myCatCol = myTableDef.addReferenceColumn(DepositCategory.FIELD_CATTYPE, TableDepositCategoryType.TABLE_NAME);
        ColumnDefinition myParentCol = myTableDef.addNullIntegerColumn(DepositCategory.FIELD_PARENT);
        myTableDef.addEncryptedColumn(DepositCategory.FIELD_NAME, DepositCategory.NAMELEN);
        myTableDef.addNullEncryptedColumn(DepositCategory.FIELD_DESC, DepositCategory.DESCLEN);

        /* Declare Sort Columns */
        myParentCol.setSortOrder(SortOrder.DESCENDING);
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getDepositCategories());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(DepositCategory.OBJECT_NAME);
        myValues.addValue(DepositCategory.FIELD_CATTYPE, myTableDef.getIntegerValue(DepositCategory.FIELD_CATTYPE));
        myValues.addValue(DepositCategory.FIELD_PARENT, myTableDef.getIntegerValue(DepositCategory.FIELD_PARENT));
        myValues.addValue(DepositCategory.FIELD_NAME, myTableDef.getBinaryValue(DepositCategory.FIELD_NAME));
        myValues.addValue(DepositCategory.FIELD_DESC, myTableDef.getBinaryValue(DepositCategory.FIELD_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final DepositCategory pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (DepositCategory.FIELD_CATTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryTypeId());
        } else if (DepositCategory.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentCategoryId());
        } else if (DepositCategory.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (DepositCategory.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
