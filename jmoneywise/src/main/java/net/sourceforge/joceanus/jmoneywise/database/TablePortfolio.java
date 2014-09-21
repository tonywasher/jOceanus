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

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for Portfolio.
 */
public class TablePortfolio
        extends TableEncrypted<Portfolio, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Portfolio.LIST_NAME;

    /**
     * The portfolio list.
     */
    private PortfolioList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TablePortfolio(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        myTableDef.addEncryptedColumn(Portfolio.FIELD_NAME, Portfolio.NAMELEN);
        myTableDef.addReferenceColumn(Portfolio.FIELD_PARENT, TablePayee.TABLE_NAME);
        myTableDef.addReferenceColumn(Portfolio.FIELD_HOLDING, TableDeposit.TABLE_NAME);
        myTableDef.addNullEncryptedColumn(Portfolio.FIELD_DESC, Portfolio.DESCLEN);
        myTableDef.addBooleanColumn(Portfolio.FIELD_TAXFREE);
        myTableDef.addBooleanColumn(Portfolio.FIELD_CLOSED);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getPortfolios();
        setList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Portfolio.OBJECT_NAME);
        myValues.addValue(Portfolio.FIELD_NAME, myTableDef.getBinaryValue(Portfolio.FIELD_NAME));
        myValues.addValue(Portfolio.FIELD_DESC, myTableDef.getBinaryValue(Portfolio.FIELD_DESC));
        myValues.addValue(Portfolio.FIELD_PARENT, myTableDef.getIntegerValue(Portfolio.FIELD_PARENT));
        myValues.addValue(Portfolio.FIELD_HOLDING, myTableDef.getIntegerValue(Portfolio.FIELD_HOLDING));
        myValues.addValue(Portfolio.FIELD_TAXFREE, myTableDef.getBooleanValue(Portfolio.FIELD_TAXFREE));
        myValues.addValue(Portfolio.FIELD_CLOSED, myTableDef.getBooleanValue(Portfolio.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Portfolio pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Portfolio.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (Portfolio.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Portfolio.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else if (Portfolio.FIELD_HOLDING.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getHoldingId());
        } else if (Portfolio.FIELD_TAXFREE.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isTaxFree());
        } else if (Portfolio.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
