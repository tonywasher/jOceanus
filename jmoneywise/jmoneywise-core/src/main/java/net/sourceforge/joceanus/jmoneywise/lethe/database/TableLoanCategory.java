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
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition.SortOrder;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for Loan Category.
 * @author Tony Washer
 */
public class TableLoanCategory
        extends PrometheusTableEncrypted<LoanCategory> {
    /**
     * The name of the Category table.
     */
    protected static final String TABLE_NAME = LoanCategory.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableLoanCategory(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myCatCol = myTableDef.addReferenceColumn(LoanCategory.FIELD_CATTYPE, TableLoanCategoryType.TABLE_NAME);
        final PrometheusColumnDefinition myParentCol = myTableDef.addNullIntegerColumn(LoanCategory.FIELD_PARENT);
        myTableDef.addEncryptedColumn(LoanCategory.FIELD_NAME, LoanCategory.NAMELEN);
        myTableDef.addNullEncryptedColumn(LoanCategory.FIELD_DESC, LoanCategory.DESCLEN);

        /* Declare Sort Columns */
        myParentCol.setSortOrder(SortOrder.DESCENDING);
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getLoanCategories());
    }

    @Override
    protected DataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues myValues = getRowValues(LoanCategory.OBJECT_NAME);
        myValues.addValue(LoanCategory.FIELD_CATTYPE, myTableDef.getIntegerValue(LoanCategory.FIELD_CATTYPE));
        myValues.addValue(LoanCategory.FIELD_PARENT, myTableDef.getIntegerValue(LoanCategory.FIELD_PARENT));
        myValues.addValue(LoanCategory.FIELD_NAME, myTableDef.getBinaryValue(LoanCategory.FIELD_NAME));
        myValues.addValue(LoanCategory.FIELD_DESC, myTableDef.getBinaryValue(LoanCategory.FIELD_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final LoanCategory pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
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
