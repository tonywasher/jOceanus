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

import net.sourceforge.joceanus.jmetis.viewer.EncryptedData;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.StockOption;
import net.sourceforge.joceanus.jmoneywise.data.StockOption.StockOptionList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for StockOption.
 */
public class TableStockOption
        extends TableEncrypted<StockOption, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = StockOption.LIST_NAME;

    /**
     * The option list.
     */
    private StockOptionList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableStockOption(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        myTableDef.addReferenceColumn(StockOption.FIELD_PORTFOLIO, TablePortfolio.TABLE_NAME);
        myTableDef.addReferenceColumn(StockOption.FIELD_SECURITY, TableSecurity.TABLE_NAME);
        myTableDef.addDateColumn(StockOption.FIELD_GRANTDATE);
        myTableDef.addDateColumn(StockOption.FIELD_EXPIREDATE);
        myTableDef.addEncryptedColumn(StockOption.FIELD_UNITS, EncryptedData.UNITSLEN);
        myTableDef.addEncryptedColumn(StockOption.FIELD_NAME, StockOption.NAMELEN);
        myTableDef.addNullEncryptedColumn(StockOption.FIELD_DESC, StockOption.DESCLEN);
        myTableDef.addBooleanColumn(StockOption.FIELD_CLOSED);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getStockOptions();
        setList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(StockOption.OBJECT_NAME);
        myValues.addValue(StockOption.FIELD_NAME, myTableDef.getBinaryValue(StockOption.FIELD_NAME));
        myValues.addValue(StockOption.FIELD_DESC, myTableDef.getBinaryValue(StockOption.FIELD_DESC));
        myValues.addValue(StockOption.FIELD_PORTFOLIO, myTableDef.getIntegerValue(StockOption.FIELD_PORTFOLIO));
        myValues.addValue(StockOption.FIELD_SECURITY, myTableDef.getIntegerValue(StockOption.FIELD_SECURITY));
        myValues.addValue(StockOption.FIELD_GRANTDATE, myTableDef.getDateValue(StockOption.FIELD_GRANTDATE));
        myValues.addValue(StockOption.FIELD_EXPIREDATE, myTableDef.getDateValue(StockOption.FIELD_EXPIREDATE));
        myValues.addValue(StockOption.FIELD_UNITS, myTableDef.getBinaryValue(StockOption.FIELD_UNITS));
        myValues.addValue(StockOption.FIELD_CLOSED, myTableDef.getBooleanValue(StockOption.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final StockOption pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (StockOption.FIELD_PORTFOLIO.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getPortfolioId());
        } else if (StockOption.FIELD_SECURITY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getSecurityId());
        } else if (StockOption.FIELD_GRANTDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getGrantDate());
        } else if (StockOption.FIELD_EXPIREDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getExpiryDate());
        } else if (StockOption.FIELD_UNITS.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getUnitsBytes());
        } else if (StockOption.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (StockOption.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (StockOption.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Validate the list */
        theList.validateOnLoad();
    }
}
