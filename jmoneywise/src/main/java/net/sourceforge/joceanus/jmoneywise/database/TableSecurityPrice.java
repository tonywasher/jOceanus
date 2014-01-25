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

import net.sourceforge.joceanus.jmetis.viewer.EncryptedData;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * TableEncrypted extension for SecurityPrice.
 * @author Tony Washer
 */
public class TableSecurityPrice
        extends TableEncrypted<SecurityPrice, MoneyWiseDataType> {
    /**
     * The name of the Prices table.
     */
    protected static final String TABLE_NAME = SecurityPrice.LIST_NAME;

    /**
     * The price list.
     */
    private SecurityPriceList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableSecurityPrice(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myActCol = myTableDef.addReferenceColumn(SecurityPrice.FIELD_SECURITY, TableAccount.TABLE_NAME);
        ColumnDefinition myDateCol = myTableDef.addDateColumn(SecurityPrice.FIELD_DATE);
        myTableDef.addEncryptedColumn(SecurityPrice.FIELD_PRICE, EncryptedData.PRICELEN);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
        myActCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getPrices();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JOceanusException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Integer mySecurityId = myTableDef.getIntegerValue(SecurityPrice.FIELD_SECURITY);
        JDateDay myDate = myTableDef.getDateValue(SecurityPrice.FIELD_DATE);
        byte[] myPrice = myTableDef.getBinaryValue(SecurityPrice.FIELD_PRICE);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myDate, mySecurityId, myPrice);
    }

    @Override
    protected void setFieldValue(final SecurityPrice pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (SecurityPrice.FIELD_SECURITY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getSecurityId());
        } else if (SecurityPrice.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (SecurityPrice.FIELD_PRICE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getPriceBytes());
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

        /* Validate the security prices */
        DataErrorList<DataItem<MoneyWiseDataType>> myErrors = theList.validate();
        if (myErrors != null) {
            throw new JMoneyWiseDataException(myErrors, DataItem.ERROR_VALIDATION);
        }
    }
}
