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
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for Security.
 */
public class TableSecurity
        extends TableEncrypted<Security> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Security.LIST_NAME;

    /**
     * The security list.
     */
    private SecurityList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableSecurity(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myCatCol = myTableDef.addReferenceColumn(Security.FIELD_SECTYPE, TableSecurityType.TABLE_NAME);
        ColumnDefinition myParentCol = myTableDef.addNullIntegerColumn(Security.FIELD_PARENT);
        myTableDef.addEncryptedColumn(Security.FIELD_NAME, Security.NAMELEN);
        myTableDef.addNullEncryptedColumn(Security.FIELD_DESC, Security.DESCLEN);
        myTableDef.addEncryptedColumn(Security.FIELD_SYMBOL, Security.SYMBOLLEN);
        myTableDef.addBooleanColumn(Security.FIELD_CLOSED);

        /* Declare Sort Columns */
        myParentCol.setSortOrder(SortOrder.DESCENDING);
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getSecurities();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JOceanusException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Integer myTypeId = myTableDef.getIntegerValue(Security.FIELD_SECTYPE);
        Integer myParentId = myTableDef.getIntegerValue(Security.FIELD_PARENT);
        byte[] myName = myTableDef.getBinaryValue(Security.FIELD_NAME);
        byte[] myDesc = myTableDef.getBinaryValue(Security.FIELD_DESC);
        byte[] mySymbol = myTableDef.getBinaryValue(Security.FIELD_SYMBOL);
        Boolean isClosed = myTableDef.getBooleanValue(Security.FIELD_CLOSED);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myName, myDesc, myTypeId, myParentId, mySymbol, isClosed);
    }

    @Override
    protected void setFieldValue(final Security pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Security.FIELD_SECTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getSecurityTypeId());
        } else if (Security.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else if (Security.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (Security.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Security.FIELD_SYMBOL.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getSymbolBytes());
        } else if (Security.FIELD_CLOSED.equals(iField)) {
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
        DataErrorList<DataItem> myErrors = theList.validate();
        if (myErrors != null) {
            throw new JMoneyWiseDataException(myErrors, DataItem.ERROR_VALIDATION);
        }
    }
}
