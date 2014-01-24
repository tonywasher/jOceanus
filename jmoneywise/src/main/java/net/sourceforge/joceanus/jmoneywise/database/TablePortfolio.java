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
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for Portfolio.
 */
public class TablePortfolio
        extends TableEncrypted<Portfolio, MoneyWiseList> {
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
        myTableDef.addReferenceColumn(Portfolio.FIELD_HOLDING, TableAccount.TABLE_NAME);
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
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JOceanusException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        byte[] myName = myTableDef.getBinaryValue(Portfolio.FIELD_NAME);
        byte[] myDesc = myTableDef.getBinaryValue(Portfolio.FIELD_DESC);
        Integer myHoldingId = myTableDef.getIntegerValue(Portfolio.FIELD_HOLDING);
        Boolean isTaxFree = myTableDef.getBooleanValue(Portfolio.FIELD_TAXFREE);
        Boolean isClosed = myTableDef.getBooleanValue(Portfolio.FIELD_CLOSED);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myName, myDesc, myHoldingId, isTaxFree, isClosed);
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

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Touch underlying items */
        theList.touchUnderlyingItems();

        /* Validate the account categories */
        DataErrorList<DataItem<MoneyWiseList>> myErrors = theList.validate();
        if (myErrors != null) {
            throw new JMoneyWiseDataException(myErrors, DataItem.ERROR_VALIDATION);
        }
    }
}
