/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.basic;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticResource;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

/**
 * Resource IDs for jMoneyWise Data Fields.
 */
public enum MoneyWiseBasicResource
        implements OceanusBundleId, MetisDataFieldId {
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
    MONEYWISEDATA_FIELD_PRICE(MoneyWiseStaticResource.TRANSINFO_PRICE),

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
    MONEYWISEDATA_FIELD_DILUTION(MoneyWiseStaticResource.TRANSINFO_DILUTION),

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
     * Category Name.
     */
    CATEGORY_NAME("Category.Name"),

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
    ASSETTYPE_PAYEE("Payee.Name"),

    /**
     * AssetType Security.
     */
    ASSETTYPE_SECURITY("Security.Name"),

    /**
     * AssetType Deposit.
     */
    ASSETTYPE_DEPOSIT("Deposit.Name"),

    /**
     * AssetType Cash.
     */
    ASSETTYPE_CASH("Cash.Name"),

    /**
     * AssetType AutoExpense.
     */
    ASSETTYPE_AUTOEXPENSE(MoneyWiseStaticResource.CASHTYPE_AUTO),

    /**
     * AssetType Loan.
     */
    ASSETTYPE_LOAN("Loan.Name"),

    /**
     * AssetType Portfolio.
     */
    ASSETTYPE_PORTFOLIO("Portfolio.Name"),

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
    SCHEDULE_ERROR_AFTERENDDATE("Schedule.Error.AfterEndDate"),

    /**
     * DepositCategory Name.
     */
    DEPOSITCAT_NAME("DepositCategory.Name"),

    /**
     * DepositCategory List.
     */
    DEPOSITCAT_LIST("DepositCategory.List"),

    /**
     * CashCategory Name.
     */
    CASHCAT_NAME("CashCategory.Name"),

    /**
     * CashCategory List.
     */
    CASHCAT_LIST("CashCategory.List"),

    /**
     * LoanCategory Name.
     */
    LOANCAT_NAME("LoanCategory.Name"),

    /**
     * LoanCategory List.
     */
    LOANCAT_LIST("LoanCategory.List"),

    /**
     * TransCategory Name.
     */
    TRANSCAT_NAME("TransCategory.Name"),

    /**
     * TransCategory List.
     */
    TRANSCAT_LIST("TransCategory.List"),

    /**
     * ExchangeRate Name.
     */
    XCHGRATE_NAME("ExchangeRate.Name"),

    /**
     * ExchangeRate List.
     */
    XCHGRATE_LIST("ExchangeRate.List"),

    /**
     * TransTag Name.
     */
    TRANSTAG_NAME("TransTag.Name"),

    /**
     * TransTag List.
     */
    TRANSTAG_LIST("TransTag.List"),

    /**
     * Region Name.
     */
    REGION_NAME("Region.Name"),

    /**
     * Region List.
     */
    REGION_LIST("Region.List"),

    /**
     * TaxInfo Name.
     */
    TAXINFO_NAME("TaxInfo.Name"),

    /**
     * TaxInfo List.
     */
    TAXINFO_LIST("TaxInfo.List"),

    /**
     * TaxYear Name.
     */
    TAXYEAR_NAME("TaxYear.Name"),

    /**
     * TaxYear List.
     */
    TAXYEAR_LIST("TaxYear.List"),

    /**
     * Payee Name.
     */
    PAYEE_NAME("Payee.Name"),

    /**
     * Payee List.
     */
    PAYEE_LIST("Payee.List"),

    /**
     * PayeeInfo Name.
     */
    PAYEEINFO_NAME("PayeeInfo.Name"),

    /**
     * PayeeInfo List.
     */
    PAYEEINFO_LIST("PayeeInfo.List"),

    /**
     * Security Name.
     */
    SECURITY_NAME("Security.Name"),

    /**
     * Security List.
     */
    SECURITY_LIST("Security.List"),

    /**
     * SecurityPrice Name.
     */
    SECURITYPRICE_NAME("SecurityPrice.Name"),

    /**
     * SecurityPrice List.
     */
    SECURITYPRICE_LIST("SecurityPrice.List"),

    /**
     * SecurityInfo Name.
     */
    SECURITYINFO_NAME("SecurityInfo.Name"),

    /**
     * SecurityInfo List.
     */
    SECURITYINFO_LIST("SecurityInfo.List"),

    /**
     * Deposit Name.
     */
    DEPOSIT_NAME("Deposit.Name"),

    /**
     * Deposit List.
     */
    DEPOSIT_LIST("Deposit.List"),

    /**
     * DepositRate Name.
     */
    DEPOSITRATE_NAME("DepositRate.Name"),

    /**
     * DepositRate List.
     */
    DEPOSITRATE_LIST("DepositRate.List"),

    /**
     * DepositInfo Name.
     */
    DEPOSITINFO_NAME("DepositInfo.Name"),

    /**
     * PayeeInfo List.
     */
    DEPOSITINFO_LIST("DepositInfo.List"),

    /**
     * Cash Name.
     */
    CASH_NAME("Cash.Name"),

    /**
     * Cash List.
     */
    CASH_LIST("Cash.List"),

    /**
     * CashInfo Name.
     */
    CASHINFO_NAME("CashInfo.Name"),

    /**
     * CashInfo List.
     */
    CASHINFO_LIST("CashInfo.List"),

    /**
     * Loan Name.
     */
    LOAN_NAME("Loan.Name"),

    /**
     * Loan List.
     */
    LOAN_LIST("Loan.List"),

    /**
     * LoanInfo Name.
     */
    LOANINFO_NAME("LoanInfo.Name"),

    /**
     * LoanInfo List.
     */
    LOANINFO_LIST("LoanInfo.List"),

    /**
     * Portfolio Name.
     */
    PORTFOLIO_NAME("Portfolio.Name"),

    /**
     * Portfolio List.
     */
    PORTFOLIO_LIST("Portfolio.List"),

    /**
     * PortfolioInfo Name.
     */
    PORTFOLIOINFO_NAME("PortfolioInfo.Name"),

    /**
     * PortfolioInfo List.
     */
    PORTFOLIOINFO_LIST("PortfolioInfo.List"),

    /**
     * Transaction Name.
     */
    TRANSACTION_NAME("Transaction.Name"),

    /**
     * Transaction List.
     */
    TRANSACTION_LIST("Transaction.List"),

    /**
     * TransInfo Name.
     */
    TRANSINFO_NAME("TransactionInfo.Name"),

    /**
     * TransInfo List.
     */
    TRANSINFO_LIST("TransactionInfo.List"),

    /**
     * Schedule Name.
     */
    SCHEDULE_NAME("Schedule.Name"),

    /**
     * Schedule List.
     */
    SCHEDULE_LIST("Schedule.List");

    /**
     * The Name Map.
     */
    private static final Map<MoneyWiseBasicDataType, OceanusBundleId> NAME_MAP = buildNameMap();

    /**
     * The List Map.
     */
    private static final Map<MoneyWiseBasicDataType, OceanusBundleId> LIST_MAP = buildListMap();

    /**
     * The Asset Type Map.
     */
    private static final Map<MoneyWiseAssetType, OceanusBundleId> TYPE_MAP = buildTypeMap();

    /**
     * The Asset Direction Map.
     */
    private static final Map<MoneyWiseAssetDirection, OceanusBundleId> DIRECTION_MAP = buildDirectionMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MoneyWiseBasicResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

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
    MoneyWiseBasicResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    MoneyWiseBasicResource(final OceanusBundleId pResource) {
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
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }

    @Override
    public String toString() {
        return getValue();
    }

    /**
     * Build asset type map.
     * @return the map
     */
    private static Map<MoneyWiseAssetType, OceanusBundleId> buildTypeMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAssetType, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseAssetType.class);
        myMap.put(MoneyWiseAssetType.PAYEE, ASSETTYPE_PAYEE);
        myMap.put(MoneyWiseAssetType.SECURITY, ASSETTYPE_SECURITY);
        myMap.put(MoneyWiseAssetType.DEPOSIT, ASSETTYPE_DEPOSIT);
        myMap.put(MoneyWiseAssetType.CASH, ASSETTYPE_CASH);
        myMap.put(MoneyWiseAssetType.AUTOEXPENSE, ASSETTYPE_AUTOEXPENSE);
        myMap.put(MoneyWiseAssetType.LOAN, ASSETTYPE_LOAN);
        myMap.put(MoneyWiseAssetType.PORTFOLIO, ASSETTYPE_PORTFOLIO);
        myMap.put(MoneyWiseAssetType.SECURITYHOLDING, ASSETTYPE_SECURITYHOLDING);
        return myMap;
    }

    /**
     * Obtain key for asset type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForAssetType(final MoneyWiseAssetType pValue) {
        return OceanusBundleLoader.getKeyForEnum(TYPE_MAP, pValue);
    }

    /**
     * Build asset direction map.
     * @return the map
     */
    private static Map<MoneyWiseAssetDirection, OceanusBundleId> buildDirectionMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAssetDirection, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseAssetDirection.class);
        myMap.put(MoneyWiseAssetDirection.TO, ASSETDIRECTION_TO);
        myMap.put(MoneyWiseAssetDirection.FROM, ASSETDIRECTION_FROM);
        return myMap;
    }

    /**
     * Obtain key for asset direction.
     * @param pValue the Value
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForAssetDirection(final MoneyWiseAssetDirection pValue) {
        return OceanusBundleLoader.getKeyForEnum(DIRECTION_MAP, pValue);
    }

    /**
     * Build name map.
     * @return the map
     */
    private static Map<MoneyWiseBasicDataType, OceanusBundleId> buildNameMap() {
        /* Create the map and return it */
        final Map<MoneyWiseBasicDataType, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseBasicDataType.class);
        myMap.put(MoneyWiseBasicDataType.DEPOSITCATEGORY, DEPOSITCAT_NAME);
        myMap.put(MoneyWiseBasicDataType.CASHCATEGORY, CASHCAT_NAME);
        myMap.put(MoneyWiseBasicDataType.LOANCATEGORY, LOANCAT_NAME);
        myMap.put(MoneyWiseBasicDataType.TRANSCATEGORY, TRANSCAT_NAME);
        myMap.put(MoneyWiseBasicDataType.EXCHANGERATE, XCHGRATE_NAME);
        myMap.put(MoneyWiseBasicDataType.TRANSTAG, TRANSTAG_NAME);
        myMap.put(MoneyWiseBasicDataType.REGION, REGION_NAME);
        myMap.put(MoneyWiseBasicDataType.PAYEE, PAYEE_NAME);
        myMap.put(MoneyWiseBasicDataType.PAYEEINFO, PAYEEINFO_NAME);
        myMap.put(MoneyWiseBasicDataType.SECURITY, SECURITY_NAME);
        myMap.put(MoneyWiseBasicDataType.SECURITYPRICE, SECURITYPRICE_NAME);
        myMap.put(MoneyWiseBasicDataType.SECURITYINFO, SECURITYINFO_NAME);
        myMap.put(MoneyWiseBasicDataType.DEPOSIT, DEPOSIT_NAME);
        myMap.put(MoneyWiseBasicDataType.DEPOSITRATE, DEPOSITRATE_NAME);
        myMap.put(MoneyWiseBasicDataType.DEPOSITINFO, DEPOSITINFO_NAME);
        myMap.put(MoneyWiseBasicDataType.CASH, CASH_NAME);
        myMap.put(MoneyWiseBasicDataType.CASHINFO, CASHINFO_NAME);
        myMap.put(MoneyWiseBasicDataType.LOAN, LOAN_NAME);
        myMap.put(MoneyWiseBasicDataType.LOANINFO, LOANINFO_NAME);
        myMap.put(MoneyWiseBasicDataType.PORTFOLIO, PORTFOLIO_NAME);
        myMap.put(MoneyWiseBasicDataType.PORTFOLIOINFO, PORTFOLIOINFO_NAME);
        myMap.put(MoneyWiseBasicDataType.TRANSACTION, TRANSACTION_NAME);
        myMap.put(MoneyWiseBasicDataType.TRANSACTIONINFO, TRANSINFO_NAME);
        return myMap;
    }

    /**
     * Obtain key for data item.
     * @param pValue the Value
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForDataType(final MoneyWiseBasicDataType pValue) {
        return OceanusBundleLoader.getKeyForEnum(NAME_MAP, pValue);
    }

    /**
     * Build list map.
     * @return the map
     */
    private static Map<MoneyWiseBasicDataType, OceanusBundleId> buildListMap() {
        /* Create the map and return it */
        final Map<MoneyWiseBasicDataType, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseBasicDataType.class);
        myMap.put(MoneyWiseBasicDataType.DEPOSITCATEGORY, DEPOSITCAT_LIST);
        myMap.put(MoneyWiseBasicDataType.CASHCATEGORY, CASHCAT_LIST);
        myMap.put(MoneyWiseBasicDataType.LOANCATEGORY, LOANCAT_LIST);
        myMap.put(MoneyWiseBasicDataType.TRANSCATEGORY, TRANSCAT_LIST);
        myMap.put(MoneyWiseBasicDataType.EXCHANGERATE, XCHGRATE_LIST);
        myMap.put(MoneyWiseBasicDataType.TRANSTAG, TRANSTAG_LIST);
        myMap.put(MoneyWiseBasicDataType.REGION, REGION_LIST);
        myMap.put(MoneyWiseBasicDataType.PAYEE, PAYEE_LIST);
        myMap.put(MoneyWiseBasicDataType.PAYEEINFO, PAYEEINFO_LIST);
        myMap.put(MoneyWiseBasicDataType.SECURITY, SECURITY_LIST);
        myMap.put(MoneyWiseBasicDataType.SECURITYPRICE, SECURITYPRICE_LIST);
        myMap.put(MoneyWiseBasicDataType.SECURITYINFO, SECURITYINFO_LIST);
        myMap.put(MoneyWiseBasicDataType.DEPOSIT, DEPOSIT_LIST);
        myMap.put(MoneyWiseBasicDataType.DEPOSITRATE, DEPOSITRATE_LIST);
        myMap.put(MoneyWiseBasicDataType.DEPOSITINFO, DEPOSITINFO_LIST);
        myMap.put(MoneyWiseBasicDataType.CASH, CASH_LIST);
        myMap.put(MoneyWiseBasicDataType.CASHINFO, CASHINFO_LIST);
        myMap.put(MoneyWiseBasicDataType.LOAN, LOAN_LIST);
        myMap.put(MoneyWiseBasicDataType.LOANINFO, LOANINFO_LIST);
        myMap.put(MoneyWiseBasicDataType.PORTFOLIO, PORTFOLIO_LIST);
        myMap.put(MoneyWiseBasicDataType.PORTFOLIOINFO, PORTFOLIOINFO_LIST);
        myMap.put(MoneyWiseBasicDataType.TRANSACTION, TRANSACTION_LIST);
        myMap.put(MoneyWiseBasicDataType.TRANSACTIONINFO, TRANSINFO_LIST);
        return myMap;
    }

    /**
     * Obtain key for data list.
     * @param pValue the Value
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForDataList(final MoneyWiseBasicDataType pValue) {
        return OceanusBundleLoader.getKeyForEnum(LIST_MAP, pValue);
    }
}
