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
package net.sourceforge.joceanus.jmoneywise.data;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for jMoneyWise Data Fields.
 */
public enum MoneyWiseDataResource implements ResourceId {
    /**
     * MoneyWiseData Name.
     */
    MONEYWISEDATA_NAME("MoneyWiseData.Name"),

    /**
     * MoneyWiseData Name.
     */
    MONEYWISEDATA_RANGE("MoneyWiseData.DateRange"),

    /**
     * MoneyWiseData Holdings Map.
     */
    MONEYWISEDATA_HOLDINGSMAP("MoneyWiseData.SecurityHoldingsMap"),

    /**
     * MoneyWiseData Date.
     */
    MONEYWISEDATA_FIELD_DATE("MoneyWiseData.Field.Date"),

    /**
     * MoneyWiseData Price.
     */
    MONEYWISEDATA_FIELD_PRICE("MoneyWiseData.Field.Price"),

    /**
     * MoneyWiseData Units.
     */
    MONEYWISEDATA_FIELD_UNITS("MoneyWiseData.Field.Units"),

    /**
     * MoneyWiseData Rate.
     */
    MONEYWISEDATA_FIELD_RATE("MoneyWiseData.Field.Rate"),

    /**
     * MoneyWiseData Dilution.
     */
    MONEYWISEDATA_FIELD_DILUTION("MoneyWiseData.Field.Dilution"),

    /**
     * MoneyWiseData InvalidCurrency Error.
     */
    MONEYWISEDATA_ERROR_CURRENCY("MoneyWiseData.Error.Currency"),

    /**
     * MoneyWiseData Map MultiMap.
     */
    MONEYWISEDATA_MAP_MULTIMAP("MoneyWiseData.Map.MultiMap"),

    /**
     * MoneyWiseData Map Underlying.
     */
    MONEYWISEDATA_MAP_UNDERLYING("MoneyWiseData.Map.Underlying"),

    /**
     * MoneyWiseData Map MultiMap.
     */
    MONEYWISEDATA_MAP_MAPOFMAPS("MoneyWiseData.Map.MapOfMaps"),

    /**
     * MoneyWiseData Map SingularMap.
     */
    MONEYWISEDATA_MAP_SINGULARMAP("MoneyWiseData.Map.SingularMap"),

    /**
     * MoneyWiseData Map SingularCounts.
     */
    MONEYWISEDATA_MAP_SINGULARCOUNTS("MoneyWiseData.Map.SingularCounts"),

    /**
     * Category SubCategory.
     */
    CATEGORY_SUBCAT("Category.SubCategory"),

    /**
     * Category New Parent.
     */
    CATEGORY_NEWPARENT("Category.NewParent"),

    /**
     * Category New Category.
     */
    CATEGORY_NEWCAT("Category.NewCategory"),

    /**
     * Category Error BadParent.
     */
    CATEGORY_ERROR_BADPARENT("Category.Error.BadParent"),

    /**
     * Category Error BadParent.
     */
    CATEGORY_ERROR_MATCHPARENT("Category.Error.MatchParent"),

    /**
     * TransCategory Error DiffParent.
     */
    TRANSCATEGORY_ERROR_DIFFPARENT("TransCategory.Error.DiffParent"),

    /**
     * ExchangeRate From Currency.
     */
    XCHGRATE_FROM("ExchangeRate.FromCurrency"),

    /**
     * ExchangeRate To Currency.
     */
    XCHGRATE_TO("ExchangeRate.ToCurrency"),

    /**
     * ExchangeRate Rate.
     */
    XCHGRATE_RATE("ExchangeRate.Rate"),

    /**
     * ExchangeRate Circular Error.
     */
    XCHGRATE_ERROR_CIRCLE("ExchangeRate.Error.Circle"),

    /**
     * ExchangeRate Default Error.
     */
    XCHGRATE_ERROR_DEFAULT("ExchangeRate.Error.Default"),

    /**
     * TransactionTag New Tag.
     */
    TRANSTAG_NEWTAG("TransTag.NewTag"),

    /**
     * TaxYear InfoSet.
     */
    TAXYEAR_INFOSET("TaxYear.InfoSet"),

    /**
     * TaxYear Bad Date Error.
     */
    TAXYEAR_ERROR_BADDATE("TaxYear.Error.BadDate"),

    /**
     * TaxYear List Gap Error.
     */
    TAXYEAR_ERROR_LISTGAP("TaxYear.Error.ListGap"),

    /**
     * TaxYear Allowance Gap Error.
     */
    TAXYEAR_ERROR_ALLOWANCE("TaxYear.Error.Allowance"),

    /**
     * TaxYear LoAgeAllowance Error.
     */
    TAXYEAR_ERROR_LOALLOWANCE("TaxYear.Error.LoAllowance"),

    /**
     * Asset Closed.
     */
    ASSET_CLOSED("Asset.Closed"),

    /**
     * Asset CloseDate.
     */
    ASSET_CLOSEDATE("Asset.CloseDate"),

    /**
     * Asset Parent.
     */
    ASSET_PARENT("Asset.Parent"),

    /**
     * Asset TaxFree.
     */
    ASSET_TAXFREE("Asset.TaxFree"),

    /**
     * Asset FirstEvent.
     */
    ASSET_FIRSTEVENT("Asset.FirstEvent"),

    /**
     * Asset LastEvent.
     */
    ASSET_LASTEVENT("Asset.LastEvent"),

    /**
     * Asset Relevant.
     */
    ASSET_RELEVANT("Asset.Relevant"),

    /**
     * Asset BadParent Error.
     */
    ASSET_ERROR_BADPARENT("Asset.Error.BadParent"),

    /**
     * Asset BadCategory Error.
     */
    ASSET_ERROR_BADCAT("Asset.Error.BadCategory"),

    /**
     * Asset ParentClosed Error.
     */
    ASSET_ERROR_PARENTCLOSED("Asset.Error.ParentClosed"),

    /**
     * AssetType Payee.
     */
    ASSETTYPE_PAYEE(MoneyWiseDataTypeResource.PAYEE_NAME),

    /**
     * AssetType Security.
     */
    ASSETTYPE_SECURITY(MoneyWiseDataTypeResource.SECURITY_NAME),

    /**
     * AssetType Deposit.
     */
    ASSETTYPE_DEPOSIT(MoneyWiseDataTypeResource.DEPOSIT_NAME),

    /**
     * AssetType Cash.
     */
    ASSETTYPE_CASH(MoneyWiseDataTypeResource.CASH_NAME),

    /**
     * AssetType AutoExpense.
     */
    ASSETTYPE_AUTOEXPENSE(StaticDataResource.CASHTYPE_AUTO),

    /**
     * AssetType Loan.
     */
    ASSETTYPE_LOAN(MoneyWiseDataTypeResource.LOAN_NAME),

    /**
     * AssetType Portfolio.
     */
    ASSETTYPE_PORTFOLIO(MoneyWiseDataTypeResource.PORTFOLIO_NAME),

    /**
     * AssetType SecurityHolding.
     */
    ASSETTYPE_SECURITYHOLDING("AssetType.SecurityHolding"),

    /**
     * AssetDirection To.
     */
    ASSETDIRECTION_TO("AssetDirection.To"),

    /**
     * AssetType Portfolio.
     */
    ASSETDIRECTION_FROM("AssetDirection.From"),

    /**
     * Payee InfoSet.
     */
    PAYEE_INFOSET("Payee.InfoSet"),

    /**
     * Payee NewAccount.
     */
    PAYEE_NEWACCOUNT("Payee.NewAccount"),

    /**
     * Security Symbol.
     */
    SECURITY_SYMBOL("Security.Symbol"),

    /**
     * Security InitialPrice.
     */
    SECURITY_INITIALPRICE("Security.InitialPrice"),

    /**
     * Security InfoSet.
     */
    SECURITY_INFOSET("Security.InfoSet"),

    /**
     * Security NewAccount.
     */
    SECURITY_NEWACCOUNT("Security.NewAccount"),

    /**
     * Deposit Gross.
     */
    DEPOSIT_GROSS("Deposit.Gross"),

    /**
     * Deposit InfoSet.
     */
    DEPOSIT_INFOSET("Deposit.InfoSet"),

    /**
     * Deposit NewAccount.
     */
    DEPOSIT_NEWACCOUNT("Deposit.NewAccount"),

    /**
     * Deposit TaxFree Error.
     */
    DEPOSIT_ERROR_TAXFREE("Deposit.Error.TaxFree"),

    /**
     * Deposit Gross Error.
     */
    DEPOSIT_ERROR_GROSS("Deposit.Error.Gross"),

    /**
     * Deposit TaxFree/Gross conflict Error.
     */
    DEPOSIT_ERROR_TAXFREEGROSS("Deposit.Error.TaxFreeGross"),

    /**
     * DepositRate Bonus.
     */
    DEPOSITRATE_BONUS("DepositRate.Bonus"),

    /**
     * DepositRate EndDate.
     */
    DEPOSITRATE_ENDDATE("DepositRate.EndDate"),

    /**
     * DepositRate NulDate Error.
     */
    DEPOSITRATE_ERROR_NULLDATE("DepositRate.Error.NullDate"),

    /**
     * Cash InfoSet.
     */
    CASH_INFOSET("Cash.InfoSet"),

    /**
     * Cash NewAccount.
     */
    CASH_NEWACCOUNT("Cash.NewAccount"),

    /**
     * Cash Bad AutoExpense Error.
     */
    CASH_ERROR_AUTOEXPENSE("Cash.Error.AutoExpense"),

    /**
     * Loan InfoSet.
     */
    LOAN_INFOSET("Loan.InfoSet"),

    /**
     * Loan NewAccount.
     */
    LOAN_NEWACCOUNT("Loan.NewAccount"),

    /**
     * Portfolio Holding.
     */
    PORTFOLIO_HOLDING("Portfolio.Holding"),

    /**
     * Portfolio InfoSet.
     */
    PORTFOLIO_INFOSET("Portfolio.InfoSet"),

    /**
     * Portfolio NewAccount.
     */
    PORTFOLIO_NEWACCOUNT("Portfolio.NewAccount"),

    /**
     * StockOption StockHolding.
     */
    STOCKOPTION_STOCKHOLDING("StockOption.StockHolding"),

    /**
     * StockOption Grant Date.
     */
    STOCKOPTION_GRANTDATE("StockOption.GrantDate"),

    /**
     * StockOption Expiry Date.
     */
    STOCKOPTION_EXPIRYDATE("StockOption.ExpiryDate"),

    /**
     * StockOption InfoSet.
     */
    STOCKOPTION_INFOSET("StockOption.InfoSet"),

    /**
     * StockOption New Account.
     */
    STOCKOPTION_NEWACCOUNT("StockOption.NewAccount"),

    /**
     * StockOption Error BadSecurity.
     */
    STOCKOPTION_ERROR_BADSECURITY("StockOption.Error.BadSecurity"),

    /**
     * StockOption Error BadExpiry.
     */
    STOCKOPTION_ERROR_BADEXPIRE("StockOption.Error.BadExpire"),

    /**
     * Portfolio HoldingClosed Error.
     */
    PORTFOLIO_ERROR_HOLDCLOSED("Portfolio.Error.HoldClosed"),

    /**
     * Portfolio HoldingTax Error.
     */
    PORTFOLIO_ERROR_HOLDTAX("Portfolio.Error.HoldTax"),

    /**
     * Portfolio HoldingParent Error.
     */
    PORTFOLIO_ERROR_HOLDPARENT("Portfolio.Error.HoldParent"),

    /**
     * SecurityHolding New Menu.
     */
    SECURITYHOLDING_NEW("SecurityHolding.Menu.New"),

    /**
     * Transaction AssetPair.
     */
    TRANSACTION_ASSETPAIR("Transaction.AssetPair"),

    /**
     * Transaction Account.
     */
    TRANSACTION_ACCOUNT("Transaction.Account"),

    /**
     * Transaction Partner.
     */
    TRANSACTION_PARTNER("Transaction.Partner"),

    /**
     * Transaction Direction.
     */
    TRANSACTION_DIRECTION("Transaction.Direction"),

    /**
     * Transaction Amount.
     */
    TRANSACTION_AMOUNT("Transaction.Amount"),

    /**
     * Transaction Reconciled.
     */
    TRANSACTION_RECONCILED("Transaction.Reconciled"),

    /**
     * Transaction Split.
     */
    TRANSACTION_SPLIT("Transaction.Split"),

    /**
     * Transaction Split indication.
     */
    TRANSACTION_ID_SPLIT("Transaction.id.Split"),

    /**
     * Transaction InfoSet.
     */
    TRANSACTION_INFOSET("Transaction.InfoSet"),

    /**
     * Transaction Groups.
     */
    TRANSACTION_GROUPS("Transaction.Groups"),

    /**
     * Transaction Bad Date Error.
     */
    TRANSACTION_ERROR_BADPRICEDATE("Transaction.Error.BadPriceDate"),

    /**
     * Transaction Circular Error.
     */
    TRANSACTION_ERROR_CIRCLE("Transaction.Error.Circular"),

    /**
     * Transaction AssetPair Error.
     */
    TRANSACTION_ERROR_ASSETPAIR("Transaction.Error.AssetPair"),

    /**
     * Transaction BadParent Error.
     */
    TRANSACTION_ERROR_BADPARENT("Transaction.Error.BadParent"),

    /**
     * Transaction ParentDate Error.
     */
    TRANSACTION_ERROR_PARENTDATE("Transaction.Error.ParentDate"),

    /**
     * Transaction ZeroAmount Error.
     */
    TRANSACTION_ERROR_ZERO("Transaction.Error.ZeroAmount"),

    /**
     * Transaction BadCreditDate Error.
     */
    TRANSACTION_ERROR_BADCREDITDATE("Transaction.Error.BadCreditDate"),

    /**
     * Transaction BadOwner Error.
     */
    TRANSACTION_ERROR_BADOWNER("Transaction.Error.BadOwner"),

    /**
     * Transaction MultiplePayees Error.
     */
    TRANSACTION_ERROR_MULTPAYEES("Transaction.Error.MultiplePayees"),

    /**
     * Transaction MultipleDates Error.
     */
    TRANSACTION_ERROR_MULTDATES("Transaction.Error.MultipleDates"),

    /**
     * Transaction MultiplePortfolios Error.
     */
    TRANSACTION_ERROR_MULTPORT("Transaction.Error.MultiplePortfolios");

    /**
     * The Resource Builder.
     */
    private static final ResourceBuilder BUILDER = ResourceBuilder.getResourceBuilder(MoneyWiseDataType.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private MoneyWiseDataResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    private MoneyWiseDataResource(final ResourceId pResource) {
        theKeyName = null;
        theValue = pResource.getValue();
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMoneyWise.data";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Obtain key for asset type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static MoneyWiseDataResource getKeyForAssetType(final AssetType pValue) {
        switch (pValue) {
            case PAYEE:
                return ASSETTYPE_PAYEE;
            case SECURITY:
                return ASSETTYPE_SECURITY;
            case DEPOSIT:
                return ASSETTYPE_DEPOSIT;
            case CASH:
                return ASSETTYPE_CASH;
            case AUTOEXPENSE:
                return ASSETTYPE_AUTOEXPENSE;
            case LOAN:
                return ASSETTYPE_LOAN;
            case PORTFOLIO:
                return ASSETTYPE_PORTFOLIO;
            case SECURITYHOLDING:
                return ASSETTYPE_SECURITYHOLDING;
            default:
                return null;
        }
    }

    /**
     * Obtain key for asset direction.
     * @param pValue the Value
     * @return the resource key
     */
    protected static MoneyWiseDataResource getKeyForAssetDirection(final AssetDirection pValue) {
        switch (pValue) {
            case TO:
                return ASSETDIRECTION_TO;
            case FROM:
                return ASSETDIRECTION_FROM;
            default:
                return null;
        }
    }
}
