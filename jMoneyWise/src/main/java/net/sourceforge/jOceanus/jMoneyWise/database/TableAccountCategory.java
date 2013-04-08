/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.database;

import javax.swing.SortOrder;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.database.ColumnDefinition;
import net.sourceforge.jOceanus.jDataModels.database.Database;
import net.sourceforge.jOceanus.jDataModels.database.TableDefinition;
import net.sourceforge.jOceanus.jDataModels.database.TableEncrypted;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * TableEncrypted extension for Account Category.
 * @author Tony Washer
 */
public class TableAccountCategory
        extends TableEncrypted<AccountCategory> {
    /**
     * The name of the Category table.
     */
    protected static final String TABLE_NAME = AccountCategory.LIST_NAME;

    /**
     * The category list.
     */
    private AccountCategoryList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableAccountCategory(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myCatCol = myTableDef.addReferenceColumn(AccountCategory.FIELD_CATTYPE, TableAccountCategoryType.TABLE_NAME);
        ColumnDefinition myParentCol = myTableDef.addNullIntegerColumn(AccountCategory.FIELD_PARENT);
        myTableDef.addEncryptedColumn(AccountCategory.FIELD_NAME, AccountCategory.NAMELEN);
        myTableDef.addNullEncryptedColumn(AccountCategory.FIELD_DESC, AccountCategory.DESCLEN);

        /* Declare Sort Columns */
        myParentCol.setSortOrder(SortOrder.DESCENDING);
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getAccountCategories();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Integer myCategoryId = myTableDef.getIntegerValue(AccountCategory.FIELD_CATTYPE);
        Integer myParentId = myTableDef.getIntegerValue(AccountCategory.FIELD_PARENT);
        byte[] myName = myTableDef.getBinaryValue(AccountCategory.FIELD_NAME);
        byte[] myDesc = myTableDef.getBinaryValue(AccountCategory.FIELD_DESC);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myName, myDesc, myCategoryId, myParentId);
    }

    @Override
    protected void setFieldValue(final AccountCategory pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (AccountCategory.FIELD_CATTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryTypeId());
        } else if (AccountCategory.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentCategoryId());
        } else if (AccountCategory.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (AccountCategory.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Validate the account categories */
        theList.validate();
        if (theList.hasErrors()) {
            throw new JDataException(ExceptionClass.VALIDATE, theList, "Validation error");
        }
    }
}
