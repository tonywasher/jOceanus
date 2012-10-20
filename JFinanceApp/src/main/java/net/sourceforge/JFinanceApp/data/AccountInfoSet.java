/*******************************************************************************
 * JFinanceApp: Finance Application
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
package net.sourceforge.JFinanceApp.data;

import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataModels.data.DataInfoSet;
import net.sourceforge.JFinanceApp.data.AccountInfo.AccountInfoList;
import net.sourceforge.JFinanceApp.data.statics.AccountInfoClass;
import net.sourceforge.JFinanceApp.data.statics.AccountInfoType;
import net.sourceforge.JFinanceApp.data.statics.AccountInfoType.AccountInfoTypeList;

/**
 * AccountInfoSet class.
 * @author Tony Washer
 */
public class AccountInfoSet extends DataInfoSet<AccountInfo, AccountNew, AccountInfoType, AccountInfoClass> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AccountInfoSet.class.getSimpleName(),
            DataInfoSet.FIELD_DEFS);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pInfoList the infoList for the info values
     * @param pTypeList the infoTypeList for the set
     */
    protected AccountInfoSet(final AccountNew pOwner,
                             final AccountInfoList pInfoList,
                             final AccountInfoTypeList pTypeList) {
        /* Store the Owner and Info List */
        super(pOwner, pInfoList, pTypeList);
    }

    /**
     * Constructor.
     * @param pSource source InfoSet
     */
    protected AccountInfoSet(final AccountInfoSet pSource) {
        super(pSource);
    }

    /**
     * Obtain the account for the infoClass.
     * @param pInfoClass the Info Class
     * @return the account
     */
    public AccountNew getAccount(final AccountInfoClass pInfoClass) {
        /* Access existing entry */
        AccountInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the account */
        return myValue.getAccount();
    }
}
