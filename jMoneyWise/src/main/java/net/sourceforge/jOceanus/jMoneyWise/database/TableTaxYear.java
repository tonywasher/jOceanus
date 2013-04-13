/*******************************************************************************
- * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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

import java.util.Date;

import javax.swing.SortOrder;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.database.ColumnDefinition;
import net.sourceforge.jOceanus.jDataModels.database.Database;
import net.sourceforge.jOceanus.jDataModels.database.DatabaseTable;
import net.sourceforge.jOceanus.jDataModels.database.TableDefinition;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear.TaxYearList;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearBase;

/**
 * DatabaseTable extension for TaxYear.
 * @author Tony Washer
 */
public class TableTaxYear
        extends DatabaseTable<TaxYear> {
    /**
     * The name of the TaxYears table.
     */
    protected static final String TABLE_NAME = TaxYear.LIST_NAME;

    /**
     * The TaxYear list.
     */
    private TaxYearList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableTaxYear(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* define the columns */
        ColumnDefinition myDateCol = myTableDef.addDateColumn(TaxYearBase.FIELD_TAXYEAR);
        myTableDef.addReferenceColumn(TaxYearBase.FIELD_REGIME, TableTaxRegime.TABLE_NAME);

        /* Declare the sort order */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getTaxYears();
        setList(theList);
    }

    @Override
    public void loadItem(final Integer pId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Date myYear = myTableDef.getDateValue(TaxYearBase.FIELD_TAXYEAR);
        Integer myRegime = myTableDef.getIntegerValue(TaxYearBase.FIELD_REGIME);

        /* Add into the list */
        theList.addSecureItem(pId, myRegime, myYear);
    }

    @Override
    protected void setFieldValue(final TaxYear pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (TaxYearBase.FIELD_TAXYEAR.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getTaxYear());
        } else if (TaxYearBase.FIELD_REGIME.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getTaxRegimeId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();
    }
}
