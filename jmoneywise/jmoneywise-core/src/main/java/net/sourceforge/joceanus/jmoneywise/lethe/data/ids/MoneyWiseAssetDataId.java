/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data.ids;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PayeeInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PortfolioInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResourceX;

/**
 * Asset DataIds.
 */
public enum MoneyWiseAssetDataId
        implements PrometheusDataFieldId {
    /**
     * Name.
     */
    NAME(PrometheusDataResourceX.DATAITEM_FIELD_NAME, AssetBase.FIELD_NAME),

    /**
     * Description.
     */
    DESC(PrometheusDataResourceX.DATAITEM_FIELD_DESC, AssetBase.FIELD_DESC),

    /**
     * Category.
     */
    CATEGORY(MoneyWiseDataResource.CATEGORY_NAME, AssetBase.FIELD_CATEGORY),

    /**
     * Parent.
     */
    PARENT(MoneyWiseDataResource.ASSET_PARENT, AssetBase.FIELD_PARENT),

    /**
     * Currency.
     */
    CURRENCY(MoneyWiseDataType.CURRENCY, AssetBase.FIELD_CURRENCY),

    /**
     * Closed.
     */
    CLOSED(MoneyWiseDataResource.ASSET_CLOSED, AssetBase.FIELD_CLOSED),

    /**
     * CloseDate.
     */
    CLOSEDATE(MoneyWiseDataResource.ASSET_CLOSEDATE, AssetBase.FIELD_CLOSEDATE),

    /**
     * EventFirst.
     */
    EVTFIRST(MoneyWiseDataResource.ASSET_FIRSTEVENT, AssetBase.FIELD_EVTFIRST),

    /**
     * EventLast.
     */
    EVTLAST(MoneyWiseDataResource.ASSET_LASTEVENT, AssetBase.FIELD_EVTLAST),

    /**
     * Relevant.
     */
    RELEVANT(MoneyWiseDataResource.ASSET_RELEVANT, AssetBase.FIELD_ISRELEVANT),

    /**
     * PayeeWebSite.
     */
    PAYEEWEBSITE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.WEBSITE), PayeeInfoSet.getFieldForClass(AccountInfoClass.WEBSITE)),

    /**
     * PayeeSortCode.
     */
    PAYEESORTCODE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.SORTCODE), PayeeInfoSet.getFieldForClass(AccountInfoClass.SORTCODE)),

    /**
     * PayeeAccount.
     */
    PAYEEACCOUNT(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.ACCOUNT), PayeeInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT)),

    /**
     * PayeeReference.
     */
    PAYEEREFERENCE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.REFERENCE), PayeeInfoSet.getFieldForClass(AccountInfoClass.REFERENCE)),

    /**
     * PayeeNotes.
     */
    PAYEENOTES(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.NOTES), PayeeInfoSet.getFieldForClass(AccountInfoClass.NOTES)),

    /**
     * PayeeCustNo.
     */
    PAYEECUSTNO(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.CUSTOMERNO), PayeeInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO)),

    /**
     * PayeeUserId.
     */
    PAYEEUSERID(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.USERID), PayeeInfoSet.getFieldForClass(AccountInfoClass.REFERENCE)),

    /**
     * PayeePassword.
     */
    PAYEEPASSWORD(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.PASSWORD), PayeeInfoSet.getFieldForClass(AccountInfoClass.PASSWORD)),

    /**
     * CashAutoPayee.
     */
    CASHAUTOPAYEE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.AUTOPAYEE), CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE)),

    /**
     * CashAutoExpense.
     */
    CASHAUTOEXPENSE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.AUTOEXPENSE), CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE)),

    /**
     * CashOpeningBalance.
     */
    CASHOPENINGBALANCE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.OPENINGBALANCE), CashInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE)),

    /**
     * CashNotes.
     */
    CASHNOTES(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.NOTES), CashInfoSet.getFieldForClass(AccountInfoClass.NOTES)),

    /**
     * DepositSortCode.
     */
    DEPOSITSORTCODE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.SORTCODE), DepositInfoSet.getFieldForClass(AccountInfoClass.SORTCODE)),

    /**
     * DepositAccount.
     */
    DEPOSITACCOUNT(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.ACCOUNT), DepositInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT)),

    /**
     * DepositReference.
     */
    DEPOSITREFERENCE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.REFERENCE), DepositInfoSet.getFieldForClass(AccountInfoClass.REFERENCE)),

    /**
     * DepositOpeningBalance.
     */
    DEPOSITOPENINGBALANCE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.OPENINGBALANCE), DepositInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE)),

    /**
     * DepositMaturity.
     */
    DEPOSITMATURITY(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.MATURITY), DepositInfoSet.getFieldForClass(AccountInfoClass.MATURITY)),

    /**
     * DepositNotes.
     */
    DEPOSITNOTES(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.NOTES), DepositInfoSet.getFieldForClass(AccountInfoClass.NOTES)),

    /**
     * LoanSortCode.
     */
    LOANSORTCODE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.SORTCODE), LoanInfoSet.getFieldForClass(AccountInfoClass.SORTCODE)),

    /**
     * LoanAccount.
     */
    LOANACCOUNT(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.ACCOUNT), LoanInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT)),

    /**
     * LoanReference.
     */
    LOANREFERENCE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.REFERENCE), LoanInfoSet.getFieldForClass(AccountInfoClass.REFERENCE)),

    /**
     * LoanOpeningBalance.
     */
    LOANOPENINGBALANCE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.OPENINGBALANCE), LoanInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE)),

    /**
     * LoanNotes.
     */
    LOANNOTES(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.NOTES), LoanInfoSet.getFieldForClass(AccountInfoClass.NOTES)),

    /**
     * PortfolioWebSite.
     */
    PORTFOLIOWEBSITE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.WEBSITE), PortfolioInfoSet.getFieldForClass(AccountInfoClass.WEBSITE)),

    /**
     * PortfolioSortCode.
     */
    PORTFOLIOSORTCODE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.SORTCODE), PortfolioInfoSet.getFieldForClass(AccountInfoClass.SORTCODE)),

    /**
     * PortfolioAccount.
     */
    PORTFOLIOACCOUNT(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.ACCOUNT), PortfolioInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT)),

    /**
     * PortfolioReference.
     */
    PORTFOLIOREFERENCE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.REFERENCE), PortfolioInfoSet.getFieldForClass(AccountInfoClass.REFERENCE)),

    /**
     * PortfolioNotes.
     */
    PORTFOLIONOTES(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.NOTES), PortfolioInfoSet.getFieldForClass(AccountInfoClass.NOTES)),

    /**
     * PortfolioCustNo.
     */
    PORTFOLIOCUSTNO(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.CUSTOMERNO), PortfolioInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO)),

    /**
     * PortfolioUserId.
     */
    PORTFOLIOUSERID(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.USERID), PortfolioInfoSet.getFieldForClass(AccountInfoClass.REFERENCE)),

    /**
     * PortfolioPassword.
     */
    PORTFOLIOPASSWORD(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.PASSWORD), PortfolioInfoSet.getFieldForClass(AccountInfoClass.PASSWORD)),

    /**
     * SecuritySymbol.
     */
    SECURITYSYMBOL(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.SYMBOL), SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL)),

    /**
     * SecurityNotes.
     */
    SECURITYNOTES(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.NOTES), SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES)),

    /**
     * SecurityRegion.
     */
    SECURITYREGION(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.REGION), SecurityInfoSet.getFieldForClass(AccountInfoClass.REGION)),

    /**
     * SecurityUnderlying.
     */
    SECURITYUNDERLYING(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.UNDERLYINGSTOCK), SecurityInfoSet.getFieldForClass(AccountInfoClass.UNDERLYINGSTOCK)),

    /**
     * SecurityOptionPrice.
     */
    SECURITYOPTIONPRICE(StaticDataResource.getKeyForAccountInfo(AccountInfoClass.OPTIONPRICE), SecurityInfoSet.getFieldForClass(AccountInfoClass.OPTIONPRICE));

    /**
     * The Value.
     */
    private final String theValue;

    /**
     * The Lethe Field.
     */
    private final MetisLetheField theField;

    /**
     * Constructor.
     * @param pKeyName the key name
     * @param pField the lethe field
     */
    MoneyWiseAssetDataId(final MetisDataFieldId pKeyName,
                         final MetisLetheField pField) {
        theValue = pKeyName.getId();
        theField = pField;
    }

    @Override
    public String getId() {
        return theValue;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public MetisLetheField getLetheField() {
        return theField;
    }
}
