/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for jMoneyWise Data Fields.
 */
public enum MoneyWiseDataResource implements TethysResourceId {
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
    MONEYWISEDATA_FIELD_PRICE(StaticDataResource.TRANSINFO_PRICE),

    /**
     * MoneyWiseData Units.
     */
    MONEYWISEDATA_FIELD_UNITS("MoneyWiseData.Field.Units"),

    /**
     * MoneyWiseData Rate.
     */
    MONEYWISEDATA_FIELD_RATE("MoneyWiseData.Field.Rate"),

    /**
     * MoneyWiseData TaxYear.
     */
    MONEYWISEDATA_FIELD_TAXYEAR("MoneyWiseData.Field.TaxYear"),

    /**
     * MoneyWiseData Dilution.
     */
    MONEYWISEDATA_FIELD_DILUTION(StaticDataResource.TRANSINFO_DILUTION),

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
     * ExchangeRate Map Rates.
     */
    XCHGRATE_MAP_MAPOFRATES("ExchangeRate.Map.MapOfRates"),

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
     * Region New region.
     */
    REGION_NEWREGION("Region.NewRegion"),

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
     * Asset Reserved valueParentClosed Error.
     */
    ASSET_ERROR_RESERVED("Asset.Error.Reserved"),

    /**
     * Asset Invalid Characters Error.
     */
    ASSET_ERROR_INVALIDCHAR("Asset.Error.InvalidChar"),

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
     * Security InfoSet.
     */
    SECURITY_INFOSET("Security.InfoSet"),

    /**
     * Security NewAccount.
     */
    SECURITY_NEWACCOUNT("Security.NewAccount"),

    /**
     * Security Symbol Map.
     */
    SECURITY_SYMBOLMAP("Security.Map.Symbol"),

    /**
     * Security Symbol Count Map.
     */
    SECURITY_SYMBOLCOUNTMAP("Security.Map.SymbolCount"),

    /**
     * SecurityPrice Map Prices.
     */
    SECURITYPRICE_MAP_MAPOFPRICES("SecurityPrice.Map.MapOfPrices"),

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
     * DepositRate Bonus.
     */
    DEPOSITRATE_BONUS("DepositRate.Bonus"),

    /**
     * DepositRate EndDate.
     */
    DEPOSITRATE_ENDDATE("DepositRate.EndDate"),

    /**
     * DepositRate Map Rates.
     */
    DEPOSITRATE_MAP_MAPOFRATES("DepositRate.Map.MapOfRates"),

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
     * Portfolio InfoSet.
     */
    PORTFOLIO_INFOSET("Portfolio.InfoSet"),

    /**
     * Portfolio NewAccount.
     */
    PORTFOLIO_NEWACCOUNT("Portfolio.NewAccount"),

    /**
     * Portfolio CashAccount.
     */
    PORTFOLIO_CASHACCOUNT("Portfolio.CashAccount"),

    /**
     * StockOption Error BadSecurity.
     */
    STOCKOPTION_ERROR_BADSECURITY("StockOption.Error.BadSecurity"),

    /**
     * StockOption Error BadExpiry.
     */
    STOCKOPTION_ERROR_BADEXPIRE("StockOption.Error.BadExpire"),

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
     * Transaction Split indication.
     */
    TRANSACTION_ID_SPLIT("Transaction.id.Split"),

    /**
     * Transaction InfoSet.
     */
    TRANSACTION_INFOSET("Transaction.InfoSet"),

    /**
     * Transaction Circular Error.
     */
    TRANSACTION_ERROR_CIRCLE("Transaction.Error.Circular"),

    /**
     * Transaction AssetPair Error.
     */
    TRANSACTION_ERROR_ASSETPAIR("Transaction.Error.AssetPair"),

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
     * Transaction PartialReconcile Error.
     */
    TRANSACTION_ERROR_PARTIALRECONCILE("Transaction.Error.PartialReconcile"),

    /**
     * Transaction MultiplePortfolios Error.
     */
    TRANSACTION_ERROR_MULTPORT("Transaction.Error.MultiplePortfolios"),

    /**
     * Schedule Start Date.
     */
    SCHEDULE_STARTDATE("Schedule.StartDate"),

    /**
     * Schedule End Date.
     */
    SCHEDULE_ENDDATE("Schedule.EndDate"),

    /**
     * Schedule Repeat Frequency.
     */
    SCHEDULE_REPEATFREQ("Schedule.RepeatFrequency"),

    /**
     * Schedule Pattern.
     */
    SCHEDULE_PATTERN("Schedule.Pattern"),

    /**
     * Schedule Pattern None.
     */
    SCHEDULE_PATTERN_NONE("Schedule.Pattern.None"),

    /**
     * Schedule Pattern Last.
     */
    SCHEDULE_PATTERN_LAST("Schedule.Pattern.Last"),

    /**
     * Schedule Next Date.
     */
    SCHEDULE_NEXTDATE("Schedule.NextDate"),

    /**
     * Schedule Invalid Frequency Error.
     */
    SCHEDULE_ERROR_FREQINVALID("Schedule.Error.BadFrequency"),

    /**
     * Schedule Before StartDate Error.
     */
    SCHEDULE_ERROR_BEFORESTARTDATE("Schedule.Error.BeforeStartDate"),

    /**
     * Schedule After EndDate Error.
     */
    SCHEDULE_ERROR_AFTERENDDATE("Schedule.Error.AfterEndDate");

    /**
     * The Asset Type Map.
     */
    private static final Map<AssetType, TethysResourceId> TYPE_MAP = buildTypeMap();

    /**
     * The Asset Direction Map.
     */
    private static final Map<AssetDirection, TethysResourceId> DIRECTION_MAP = buildDirectionMap();

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getResourceBuilder(MoneyWiseDataResource.class.getCanonicalName());

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
    MoneyWiseDataResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    MoneyWiseDataResource(final TethysResourceId pResource) {
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
     * Build asset type map.
     * @return the map
     */
    private static Map<AssetType, TethysResourceId> buildTypeMap() {
        /* Create the map and return it */
        Map<AssetType, TethysResourceId> myMap = new EnumMap<>(AssetType.class);
        myMap.put(AssetType.PAYEE, ASSETTYPE_PAYEE);
        myMap.put(AssetType.SECURITY, ASSETTYPE_SECURITY);
        myMap.put(AssetType.DEPOSIT, ASSETTYPE_DEPOSIT);
        myMap.put(AssetType.CASH, ASSETTYPE_CASH);
        myMap.put(AssetType.AUTOEXPENSE, ASSETTYPE_AUTOEXPENSE);
        myMap.put(AssetType.LOAN, ASSETTYPE_LOAN);
        myMap.put(AssetType.PORTFOLIO, ASSETTYPE_PORTFOLIO);
        myMap.put(AssetType.SECURITYHOLDING, ASSETTYPE_SECURITYHOLDING);
        return myMap;
    }

    /**
     * Obtain key for asset type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForAssetType(final AssetType pValue) {
        return TethysResourceBuilder.getKeyForEnum(TYPE_MAP, pValue);
    }

    /**
     * Build asset direction map.
     * @return the map
     */
    private static Map<AssetDirection, TethysResourceId> buildDirectionMap() {
        /* Create the map and return it */
        Map<AssetDirection, TethysResourceId> myMap = new EnumMap<>(AssetDirection.class);
        myMap.put(AssetDirection.TO, ASSETDIRECTION_TO);
        myMap.put(AssetDirection.FROM, ASSETDIRECTION_FROM);
        return myMap;
    }

    /**
     * Obtain key for asset direction.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForAssetDirection(final AssetDirection pValue) {
        return TethysResourceBuilder.getKeyForEnum(DIRECTION_MAP, pValue);
    }
}
