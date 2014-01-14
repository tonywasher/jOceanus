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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.StaticData;
import net.sourceforge.joceanus.jdatamodels.database.Database;
import net.sourceforge.joceanus.jdatamodels.database.TableDefinition;
import net.sourceforge.joceanus.jdatamodels.database.TableStaticData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;

/**
 * TableStaticData extension for AccountCategoryType.
 * @author Tony Washer
 */
public class TableAccountCurrency
        extends TableStaticData<AccountCurrency> {
    /**
     * The table name.
     */
    protected static final String TABLE_NAME = AccountCurrency.LIST_NAME;

    /**
     * The account currency list.
     */
    private AccountCurrencyList theList = null;

    /**
     * Constructors.
     * @param pDatabase the database control
     */
    protected TableAccountCurrency(final Database<MoneyWiseData> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();
        myTableDef.addBooleanColumn(AccountCurrency.FIELD_DEFAULT);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getAccountCurrencies();
        setList(theList);
    }

    @Override
    protected void loadTheItem(final Integer pId,
                               final Integer pControlId,
                               final Boolean isEnabled,
                               final Integer pOrder,
                               final byte[] pType,
                               final byte[] pDesc) throws JDataException {
        /* Note needed */
    }

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Boolean myEnabled = myTableDef.getBooleanValue(StaticData.FIELD_ENABLED);
        Integer myOrder = myTableDef.getIntegerValue(StaticData.FIELD_ORDER);
        byte[] myType = myTableDef.getBinaryValue(StaticData.FIELD_NAME);
        byte[] myDesc = myTableDef.getBinaryValue(StaticData.FIELD_DESC);
        Boolean myDefault = myTableDef.getBooleanValue(AccountCurrency.FIELD_DEFAULT);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myEnabled, myOrder, myType, myDesc, myDefault);
    }

    @Override
    protected void setFieldValue(final AccountCurrency pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (AccountCurrency.FIELD_DEFAULT.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isDefault());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
