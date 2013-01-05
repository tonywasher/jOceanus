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
package net.sourceforge.jOceanus.jMoneyWise.sheets;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo.AccountInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * SheetDataInfo extension for AccountInfo.
 * @author Tony Washer
 */
public class SheetAccountInfo
        extends SheetDataInfo<AccountInfo> {
    /**
     * NamedArea for AccountInfo.
     */
    private static final String AREA_ACCOUNTINFO = AccountInfo.LIST_NAME;

    /**
     * Accounts data list.
     */
    private AccountList theAccounts = null;

    /**
     * AccountInfo data list.
     */
    private final AccountInfoList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAccountInfo(final FinanceReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_ACCOUNTINFO);

        /* Access the InfoType list */
        FinanceData myData = pReader.getData();
        theAccounts = myData.getAccounts();
        theList = myData.getAccountInfo();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountInfo(final FinanceWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_ACCOUNTINFO);

        /* Access the InfoType list */
        FinanceData myData = pWriter.getData();
        theList = myData.getAccountInfo();
        setDataList(theList);
    }

    @Override
    protected void loadEncryptedItem(final Integer pId,
                                     final Integer pControlId,
                                     final Integer pInfoTypeId,
                                     final Integer pOwnerId,
                                     final byte[] pValue) throws JDataException {
        /* Create the item */
        theList.addSecureItem(pId, pControlId, pInfoTypeId, pOwnerId, pValue);
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Mark active items and validate */
        theAccounts.markActiveItems();
    }
}
