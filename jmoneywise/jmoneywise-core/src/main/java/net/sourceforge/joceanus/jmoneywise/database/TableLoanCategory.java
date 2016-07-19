/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for Loan Category.
 * @author Tony Washer
 */
public class TableLoanCategory
        extends PrometheusTableEncrypted<LoanCategory, MoneyWiseDataType> {
    /**
     * The name of the Category table.
     */
    protected static final String TABLE_NAME = LoanCategory.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableLoanCategory(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        PrometheusColumnDefinition myCatCol = myTableDef.addReferenceColumn(LoanCategory.FIELD_CATTYPE, TableLoanCategoryType.TABLE_NAME);
        PrometheusColumnDefinition myParentCol = myTableDef.addNullIntegerColumn(LoanCategory.FIELD_PARENT);
        myTableDef.addEncryptedColumn(LoanCategory.FIELD_NAME, LoanCategory.NAMELEN);
        myTableDef.addNullEncryptedColumn(LoanCategory.FIELD_DESC, LoanCategory.DESCLEN);

        /* Declare Sort Columns */
        myParentCol.setSortOrder(SortOrder.DESCENDING);
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getLoanCategories());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(LoanCategory.OBJECT_NAME);
        myValues.addValue(LoanCategory.FIELD_CATTYPE, myTableDef.getIntegerValue(LoanCategory.FIELD_CATTYPE));
        myValues.addValue(LoanCategory.FIELD_PARENT, myTableDef.getIntegerValue(LoanCategory.FIELD_PARENT));
        myValues.addValue(LoanCategory.FIELD_NAME, myTableDef.getBinaryValue(LoanCategory.FIELD_NAME));
        myValues.addValue(LoanCategory.FIELD_DESC, myTableDef.getBinaryValue(LoanCategory.FIELD_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final LoanCategory pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        PrometheusTableDefinition myTableDef = getTableDef();
        if (LoanCategory.FIELD_CATTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryTypeId());
        } else if (LoanCategory.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentCategoryId());
        } else if (LoanCategory.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (LoanCategory.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
