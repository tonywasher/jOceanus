/*******************************************************************************
- * jMoneyWise: Finance Application
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
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearBase;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.DatabaseTable;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * DatabaseTable extension for TaxYear.
 * @author Tony Washer
 */
public class TableTaxYear extends DatabaseTable<TaxYear, MoneyWiseDataType> {
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
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getTaxYears();
        setList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(TaxYear.OBJECT_NAME);
        myValues.addValue(TaxYear.FIELD_TAXYEAR, myTableDef.getDateValue(TaxYear.FIELD_TAXYEAR));
        myValues.addValue(TaxYear.FIELD_REGIME, myTableDef.getIntegerValue(TaxYear.FIELD_REGIME));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final TaxYear pItem,
                                 final JDataField iField) throws JOceanusException {
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
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();
    }
}
