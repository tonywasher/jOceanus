/*******************************************************************************
 * jMoneyWise: Finance Application
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

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.database.Database;
import net.sourceforge.jOceanus.jDataModels.database.TableDataInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo.AccountInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountNew.AccountNewList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * TableDataInfo extension for AccountInfo.
 * @author Tony Washer
 */
public class TableAccountInfo extends TableDataInfo<AccountInfo> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = AccountInfo.LIST_NAME;

    /**
     * Account data list.
     */
    private AccountNewList theAccounts = null;

    /**
     * The AccountInfo list.
     */
    private AccountInfoList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableAccountInfo(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME, TableAccountInfoType.TABLE_NAME, TableAccount.TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theAccounts = myData.getNewAccounts();
        theList = myData.getAccountInfo();
        setList(theList);
    }

    @Override
    protected void loadTheItem(final Integer pId,
                               final Integer pControlId,
                               final Integer pInfoTypeId,
                               final Integer pOwnerId,
                               final byte[] pValue) throws JDataException {
        /* Add into the list */
        theList.addSecureItem(pId, pControlId, pInfoTypeId, pOwnerId, pValue);
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Mark active items */
        theAccounts.markActiveItems();

        /* Validate the accounts */
        theAccounts.validate();
        if (theAccounts.hasErrors()) {
            throw new JDataException(ExceptionClass.VALIDATE, theAccounts, "Validation error");
        }
    }
}
