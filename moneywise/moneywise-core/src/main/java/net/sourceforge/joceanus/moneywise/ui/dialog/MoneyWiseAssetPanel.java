/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.ui.dialog;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;

/**
 * Panel to display/edit/create an asset.
 * @param <T> the Asset type
 */
public abstract class MoneyWiseAssetPanel<T extends MoneyWiseAssetBase>
        extends MoneyWiseItemPanel<T> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pEditSet the edit set
     * @param pOwner the owning table
     */
    protected MoneyWiseAssetPanel(final TethysUIFactory<?> pFactory,
                                  final PrometheusEditSet pEditSet,
                                  final MoneyWiseAssetTable<T> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);
    }

    @Override
    protected MoneyWiseAssetTable<T> getOwner() {
        return (MoneyWiseAssetTable<T>) super.getOwner();
    }

    /**
     * is Valid webSite?
     * @param pNewWebSite the new webSite
     * @return error message or null
     */
    public String isValidWebSite(final char[] pNewWebSite) {
        return getOwner().isValidData(pNewWebSite, MoneyWiseAccountInfoClass.WEBSITE);
    }

    /**
     * is Valid customerNo?
     * @param pNewCustNo the new customerNo
     * @return error message or null
     */
    public String isValidCustNo(final char[] pNewCustNo) {
        return getOwner().isValidData(pNewCustNo, MoneyWiseAccountInfoClass.CUSTOMERNO);
    }

    /**
     * is Valid userId?
     * @param pNewUserId the new userId
     * @return error message or null
     */
    public String isValidUserId(final char[] pNewUserId) {
        return getOwner().isValidData(pNewUserId, MoneyWiseAccountInfoClass.USERID);
    }

    /**
     * is Valid password?
     * @param pNewPassword the new password
     * @return error message or null
     */
    public String isValidPassword(final char[] pNewPassword) {
        return getOwner().isValidData(pNewPassword, MoneyWiseAccountInfoClass.PASSWORD);
    }

    /**
     * is Valid sortCode?
     * @param pNewSortCode the new sortCode
     * @return error message or null
     */
    public String isValidSortCode(final char[] pNewSortCode) {
        return getOwner().isValidData(pNewSortCode, MoneyWiseAccountInfoClass.SORTCODE);
    }

    /**
     * is Valid account?
     * @param pNewAccount the new account
     * @return error message or null
     */
    public String isValidAccount(final char[] pNewAccount) {
        return getOwner().isValidData(pNewAccount, MoneyWiseAccountInfoClass.ACCOUNT);
    }

    /**
     * is Valid reference?
     * @param pNewRef the new reference
     * @return error message or null
     */
    public String isValidReference(final char[] pNewRef) {
        return getOwner().isValidData(pNewRef, MoneyWiseAccountInfoClass.REFERENCE);
    }

    /**
     * is Valid notes?
     * @param pNewNotes the new notes
     * @return error message or null
     */
    public String isValidNotes(final char[] pNewNotes) {
        return getOwner().isValidData(pNewNotes, MoneyWiseAccountInfoClass.NOTES);
    }

    /**
     * is Valid symbol?
     * @param pNewSymbol the new symbol
     * @return error message or null
     */
    public String isValidSymbol(final String pNewSymbol) {
        return getOwner().isValidSymbol(pNewSymbol, getItem());
    }
}
