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
package net.sourceforge.jOceanus.jMoneyWise.data;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataModels.data.DataInfoSet;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo.AccountInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType.AccountInfoTypeList;

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
     * @param pTypeList the infoTypeList for the set
     */
    protected AccountInfoSet(final AccountNew pOwner,
                             final AccountInfoTypeList pTypeList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList);
    }

    /**
     * Set the infoList.
     * @param pInfoList source InfoSet
     */
    protected void setInfoList(final AccountInfoList pInfoList) {
        super.setInfoList(pInfoList);
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final AccountInfoSet pSource) {
        /* Clone the dataInfoSet */
        super.cloneDataInfoSet(pSource);
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
