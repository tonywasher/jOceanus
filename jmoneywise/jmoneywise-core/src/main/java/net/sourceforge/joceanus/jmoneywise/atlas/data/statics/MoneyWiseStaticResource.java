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
package net.sourceforge.joceanus.jmoneywise.atlas.data.statics;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for jMoneyWise DataType Fields.
 */
public enum MoneyWiseStaticResource
        implements TethysBundleId, MetisDataFieldId {
    /**
     * DepositType Name.
     */
    DEPOSITTYPE_NAME("DepositType.Name"),

    /**
     * DepositType List.
     */
    DEPOSITTYPE_LIST("DepositType.List"),

    /**
     * CashType Name.
     */
    CASHTYPE_NAME("CashType.Name"),

    /**
     * CashType List.
     */
    CASHTYPE_LIST("CashType.List"),

    /**
     * LoanType Name.
     */
    LOANTYPE_NAME("LoanType.Name"),

    /**
     * LoanType List.
     */
    LOANTYPE_LIST("LoanType.List"),

    /**
     * PortfolioType Name.
     */
    PORTFOLIOTYPE_NAME("PortfolioType.Name"),

    /**
     * PortfolioType List.
     */
    PORTFOLIOTYPE_LIST("PortfolioType.List"),

    /**
     * SecurityType Name.
     */
    SECURITYTYPE_NAME("SecurityType.Name"),

    /**
     * SecurityType List.
     */
    SECURITYTYPE_LIST("SecurityType.List"),

    /**
     * PayeeType Name.
     */
    PAYEETYPE_NAME("PayeeType.Name"),

    /**
     * PayeeType List.
     */
    PAYEETYPE_LIST("PayeeType.List"),

    /**
     * TransType Name.
     */
    TRANSTYPE_NAME("TransType.Name"),

    /**
     * TransType List.
     */
    TRANSTYPE_LIST("TransType.List"),

    /**
     * TaxBasis Name.
     */
    TAXBASIS_NAME("TaxBasis.Name"),

    /**
     * TaxBasis List.
     */
    TAXBASIS_LIST("TaxBasis.List"),

    /**
     * TaxType Name.
     */
    TAXTYPE_NAME("TaxType.Name"),

    /**
     * TaxType List.
     */
    TAXTYPE_LIST("TaxType.List"),

    /**
     * TaxRegime Name.
     */
    TAXREGIME_NAME("TaxRegime.Name"),

    /**
     * TaxRegime List.
     */
    TAXREGIME_LIST("TaxRegime.List"),

    /**
     * Frequency Name.
     */
    FREQUENCY_NAME("Frequency.Name"),

    /**
     * Frequency List.
     */
    FREQUENCY_LIST("Frequency.List"),

    /**
     * Currency Name.
     */
    CURRENCY_NAME("Currency.Name"),

    /**
     * Currency List.
     */
    CURRENCY_LIST("Currency.List"),

    /**
     * TaxInfoType Name.
     */
    TAXINFOTYPE_NAME("TaxInfoType.Name"),

    /**
     * TaxInfoType List.
     */
    TAXINFOTYPE_LIST("TaxInfoType.List"),

    /**
     * AccountInfoType Name.
     */
    ACCOUNTINFOTYPE_NAME("AccountInfoType.Name"),

    /**
     * AccountInfoType List.
     */
    ACCOUNTINFOTYPE_LIST("AccountInfoType.List"),

    /**
     * TransInfoType Name.
     */
    TRANSINFOTYPE_NAME("TransInfoType.Name"),

    /**
     * TransInfoType List.
     */
    TRANSINFOTYPE_LIST("TransInfoType.List"),

    /**
     * CategoryType Parent.
     */
    CATEGORYTYPE_PARENT("CategoryType.Parent"),

    /**
     * DepositType Checking.
     */
    DEPOSITTYPE_CHECKING("DepositType.Checking"),

    /**
     * DepositType Savings.
     */
    DEPOSITTYPE_SAVINGS("DepositType.Savings"),

    /**
     * DepositType TaxFreeSavings.
     */
    DEPOSITTYPE_TAXFREESAVINGS("DepositType.TaxFreeSavings"),

    /**
     * DepositType Peer2Peer.
     */
    DEPOSITTYPE_PEER2PEER("DepositType.Peer2Peer"),

    /**
     * DepositType Bond.
     */
    DEPOSITTYPE_BOND("DepositType.Bond"),

    /**
     * DepositType TaxFreeBond.
     */
    DEPOSITTYPE_TAXFREEBOND("DepositType.TaxFreeBond"),

    /**
     * CashType Cash.
     */
    CASHTYPE_CASH("CashType.Cash"),

    /**
     * CashType AutoExpense.
     */
    CASHTYPE_AUTO("CashType.AutoExpense"),

    /**
     * LoanType CreditCard.
     */
    LOANTYPE_CREDIT("LoanType.CreditCard"),

    /**
     * LoanType Private.
     */
    LOANTYPE_PRIVATE("LoanType.Private"),

    /**
     * LoanType Loan.
     */
    LOANTYPE_LOAN("LoanType.Loan"),

    /**
     * PortfolioType Standard.
     */
    PORTFOLIOTYPE_STANDARD("PortfolioType.Standard"),

    /**
     * PortfolioType TaxFree.
     */
    PORTFOLIOTYPE_TAXFREE("PortfolioType.TaxFree"),

    /**
     * /** PortfolioType Pension.
     */
    PORTFOLIOTYPE_PENSION("PortfolioType.Pension"),

    /**
     * PortfolioType SIPP.
     */
    PORTFOLIOTYPE_SIPP("PortfolioType.SIPP"),

    /**
     * SecurityType Shares.
     */
    SECURITYTYPE_SHARES("SecurityType.Shares"),

    /**
     * SecurityType Income UnitTrust.
     */
    SECURITYTYPE_INCOMEUNIT("SecurityType.UnitTrust.Income"),

    /**
     * SecurityType Growth UnitTrust.
     */
    SECURITYTYPE_GROWTHUNIT("SecurityType.UnitTrust.Growth"),

    /**
     * SecurityType LifeBond.
     */
    SECURITYTYPE_LIFEBOND("SecurityType.LifeBond"),

    /**
     * SecurityType Endowment.
     */
    SECURITYTYPE_ENDOWMENT("SecurityType.Endowment"),

    /**
     * SecurityType Property.
     */
    SECURITYTYPE_PROPERTY("SecurityType.Property"),

    /**
     * SecurityType Vehicle.
     */
    SECURITYTYPE_VEHICLE("SecurityType.Vehicle"),

    /**
     * SecurityType StatePension.
     */
    SECURITYTYPE_STATEPENSION("SecurityType.StatePension"),

    /**
     * SecurityType DefinedBenefit.
     */
    SECURITYTYPE_BENEFIT("SecurityType.DefinedBenefit"),

    /**
     * SecurityType DefinedContribution.
     */
    SECURITYTYPE_CONTRIBUTION("SecurityType.DefinedContribution"),

    /**
     * SecurityType StockOption.
     */
    SECURITYTYPE_STOCKOPTION("SecurityType.StockOption"),

    /**
     * SecurityType Asset.
     */
    SECURITYTYPE_ASSET("SecurityType.Asset"),

    /**
     * PayeeType TaxMan.
     */
    PAYEETYPE_TAXMAN("PayeeType.TaxMan"),

    /**
     * PayeeType Government.
     */
    PAYEETYPE_GOVERNMENT("PayeeType.Government"),

    /**
     * PayeeType Market.
     */
    PAYEETYPE_MARKET("PayeeType.Market"),

    /**
     * PayeeType Employer.
     */
    PAYEETYPE_EMPLOYER("PayeeType.Employer"),

    /**
     * PayeeType Institution.
     */
    PAYEETYPE_INSTITUTION("PayeeType.Institution"),

    /**
     * PayeeType Individual.
     */
    PAYEETYPE_INDIVIDUAL("PayeeType.Individual"),

    /**
     * PayeeType Annuity.
     */
    PAYEETYPE_ANNUITY("PayeeType.Annuity"),

    /**
     * PayeeType Payee.
     */
    PAYEETYPE_PAYEE("PayeeType.Payee"),

    /**
     * TransType TaxedIncome.
     */
    TRANSTYPE_TAXEDINCOME("TransType.TaxedIncome"),

    /**
     * TransType RentalIncome.
     */
    TRANSTYPE_RENTALINCOME("TransType.RentalIncome"),

    /**
     * TransType TaxedIncome.
     */
    TRANSTYPE_ROOMRENTINCOME("TransType.RoomRentalIncome"),

    /**
     * TransType Interest.
     */
    TRANSTYPE_INTEREST("TransType.Interest"),

    /**
     * TransType Dividend.
     */
    TRANSTYPE_DIVIDEND("TransType.Dividend"),

    /**
     * TransType BShare Dividend.
     */
    TRANSTYPE_BSHAREDIVIDEND("TransType.Dividend.B"),

    /**
     * TransType VirtualIncome.
     */
    TRANSTYPE_VIRTUALINCOME("TransType.VirtualIncome"),

    /**
     * TransType GrossIncome.
     */
    TRANSTYPE_GROSSINCOME("TransType.GrossIncome"),

    /**
     * TransType PensionContribution.
     */
    TRANSTYPE_PENSIONCONTRIB("TransType.PensionContribution"),

    /**
     * TransType GiftedIncome.
     */
    TRANSTYPE_GIFTEDINCOME("TransType.GiftedIncome"),

    /**
     * TransType Inherited.
     */
    TRANSTYPE_INHERITED("TransType.Inherited"),

    /**
     * TransType LoanInterestEarned.
     */
    TRANSTYPE_LOANINTEARNED("TransType.LoanInterestEarned"),

    /**
     * TransType CashBack.
     */
    TRANSTYPE_CASHBACK("TransType.CashBack"),

    /**
     * TransType LoyaltyBonus.
     */
    TRANSTYPE_LOYALTYBONUS("TransType.LoyaltyBonus"),

    /**
     * TransType RecoveredExpenses.
     */
    TRANSTYPE_RECOVEREDEXPENSES("TransType.RecoveredExpenses"),

    /**
     * TransType OtherIncome.
     */
    TRANSTYPE_OTHERINCOME("TransType.OtherIncome"),

    /**
     * TransType Transfer.
     */
    TRANSTYPE_TRANSFER("TransType.Transfer"),

    /**
     * TransType Security UnitsAdjust.
     */
    TRANSTYPE_UNITSADJUST("TransType.UnitsAdjust"),

    /**
     * TransType StockSplit.
     */
    TRANSTYPE_STOCKSPLIT("TransType.StockSplit"),

    /**
     * TransType StockDeMerger.
     */
    TRANSTYPE_STOCKDEMERGER("TransType.StockDeMerger"),

    /**
     * TransType Security Replace.
     */
    TRANSTYPE_SECURITYREPLACE("TransType.SecurityReplace"),

    /**
     * TransType Security Closure.
     */
    TRANSTYPE_SECURITYCLOSURE("TransType.SecurityClosure"),

    /**
     * TransType Stock TakeOver.
     */
    TRANSTYPE_STOCKTAKEOVER("TransType.StockTakeOver"),

    /**
     * TransType StockRightsIssue.
     */
    TRANSTYPE_STOCKRIGHTSISSUE("TransType.StockRightsIssue"),

    /**
     * TransType PortfolioXfer.
     */
    TRANSTYPE_PORTFOLIOXFER("TransType.PortfolioXfer"),

    /**
     * TransType OptionsGrant.
     */
    TRANSTYPE_OPTIONSGRANT("TransType.OptionsGrant"),

    /**
     * TransType OptionsVest.
     */
    TRANSTYPE_OPTIONSVEST("TransType.OptionsVest"),

    /**
     * TransType OptionsExpire.
     */
    TRANSTYPE_OPTIONSEXPIRE("TransType.OptionsExpire"),

    /**
     * TransType OptionsExercise.
     */
    TRANSTYPE_OPTIONSEXERCISE("TransType.OptionsExercise"),

    /**
     * TransType PensionDrawdown.
     */
    TRANSTYPE_PENSIONDRAWDOWN("TransType.PensionDrawdown"),

    /**
     * TransType Pension TaxFree.
     */
    TRANSTYPE_PENSIONTAXFREE("TransType.PensionTaxFree"),

    /**
     * TransType Expense.
     */
    TRANSTYPE_EXPENSE("TransType.Expense"),

    /**
     * TransType BadDebtCapital.
     */
    TRANSTYPE_BADDEBTCAPITAL("TransType.BadDebt.Capital"),

    /**
     * TransType BadDebtInterest.
     */
    TRANSTYPE_BADDEBTINTEREST("TransType.BadDebt.Interest"),

    /**
     * TransType LocalTaxes.
     */
    TRANSTYPE_LOCALTAXES("TransType.LocalTaxes"),

    /**
     * TransType WriteOff.
     */
    TRANSTYPE_WRITEOFF("TransType.WriteOff"),

    /**
     * TransType RentalExpense.
     */
    TRANSTYPE_RENTALEXPENSE("TransType.RentalExpense"),

    /**
     * TransType AnnuityPurchase.
     */
    TRANSTYPE_ANNUITYPURCHASE("TransType.AnnuityPurchase"),

    /**
     * TransType LoanInterestCharged.
     */
    TRANSTYPE_LOANINTCHARGE("TransType.LoanInterestCharged"),

    /**
     * TransType TaxRelief.
     */
    TRANSTYPE_TAXRELIEF("TransType.TaxRelief"),

    /**
     * TransType IncomeTax.
     */
    TRANSTYPE_INCOMETAX("TransType.IncomeTax"),

    /**
     * TransType TaxedInterest.
     */
    TRANSTYPE_TAXEDINTEREST("TransType.TaxedInterest"),

    /**
     * TransType TaxFreeInterest.
     */
    TRANSTYPE_TAXFREEINTEREST("TransType.TaxFreeInterest"),

    /**
     * TransType Peer2PeerInterest.
     */
    TRANSTYPE_PEER2PEERINTEREST("TransType.Peer2PeerInterest"),

    /**
     * TransType GrossInterest.
     */
    TRANSTYPE_GROSSINTEREST("TransType.GrossInterest"),

    /**
     * TransType ShareDividend.
     */
    TRANSTYPE_SHAREDIVIDEND("TransType.ShareDividend"),

    /**
     * TransType UnitTrustDividend.
     */
    TRANSTYPE_UTDIVIDEND("TransType.UnitTrustDividend"),

    /**
     * TransType ForeignDividend.
     */
    TRANSTYPE_FOREIGNDIVIDEND("TransType.ForeignDividend"),

    /**
     * TransType TaxFreeDividend.
     */
    TRANSTYPE_TAXFREEDIVIDEND("TransType.TaxFreeDividend"),

    /**
     * TransType TaxedLoyaltyBonus.
     */
    TRANSTYPE_TAXEDLOYALTYBONUS("TransType.TaxedLoyaltyBonus"),

    /**
     * TransType TaxFreeLoyaltyBonus.
     */
    TRANSTYPE_TAXFREELOYALTYBONUS("TransType.TaxFreeLoyaltyBonus"),

    /**
     * TransType GrossLoyaltyBonus.
     */
    TRANSTYPE_GROSSLOYALTYBONUS("TransType.GrossLoyaltyBonus"),

    /**
     * TransType ChargeableGain.
     */
    TRANSTYPE_CHARGEABLEGAIN("TransType.ChargeableGain"),

    /**
     * TransType TaxFreeGain.
     */
    TRANSTYPE_TAXFREEGAIN("TransType.TaxFreeGain"),

    /**
     * TransType ResidentialGain.
     */
    TRANSTYPE_RESIDENTIALGAIN("TransType.ResidentialGain"),

    /**
     * TransType CapitalGain.
     */
    TRANSTYPE_CAPITALGAIN("TransType.CapitalGain"),

    /**
     * TransType MarketGrowth.
     */
    TRANSTYPE_MARKETGROWTH("TransType.MarketGrowth"),

    /**
     * TransType CurrencyFluctuation.
     */
    TRANSTYPE_CURRFLUCT("TransType.CurrencyFluctuation"),

    /**
     * TransType Withheld.
     */
    TRANSTYPE_WITHHELD("TransType.Withheld"),

    /**
     * TransType OpeningBalance.
     */
    TRANSTYPE_OPENINGBALANCE("TransType.OpeningBalance"),

    /**
     * TransInfo EmployerNatIns.
     */
    TRANSTYPE_EMPLOYERNATINS("TransType.EmployerNatIns"),

    /**
     * TransInfo EmployeeNatIns.
     */
    TRANSTYPE_EMPLOYEENATINS("TransType.EmployeeNatIns"),

    /**
     * TransType IncomeTotals.
     */
    TRANSTYPE_INCOMETOTALS("TransType.IncomeTotals"),

    /**
     * TransType ExpenseTotals.
     */
    TRANSTYPE_EXPENSETOTALS("TransType.ExpenseTotals"),

    /**
     * TransType SecurityParent.
     */
    TRANSTYPE_SECURITYPARENT("TransType.SecurityParent"),

    /**
     * TransType Totals.
     */
    TRANSTYPE_TOTALS("TransType.Totals"),

    /**
     * TaxBasis Salary.
     */
    TAXBASIS_SALARY("TaxBasis.Salary"),

    /**
     * TaxBasis RoomRental.
     */
    TAXBASIS_ROOMRENTAL("TaxBasis.RoomRental"),

    /**
     * TaxBasis RentalIncome.
     */
    TAXBASIS_RENTALINCOME("TaxBasis.RentalIncome"),

    /**
     * TaxBasis OtherIncome.
     */
    TAXBASIS_OTHERINCOME("TaxBasis.OtherIncome"),

    /**
     * TaxBasis Interest.
     */
    TAXBASIS_TAXEDINTEREST("TaxBasis.TaxedInterest"),

    /**
     * TaxBasis UnTaxedInterest.
     */
    TAXBASIS_UNTAXEDINTEREST("TaxBasis.UnTaxedInterest"),

    /**
     * TaxBasis Dividend.
     */
    TAXBASIS_DIVIDEND("TaxBasis.Dividend"),

    /**
     * TaxBasis UnitTrustDividend.
     */
    TAXBASIS_UTDIVIDEND("TaxBasis.UnitTrustDividend"),

    /**
     * TaxBasis ForeignDividend.
     */
    TAXBASIS_FOREIGNDIVIDEND("TaxBasis.ForeignDividend"),

    /**
     * TaxBasis ChargeableGains.
     */
    TAXBASIS_CHARGEABLEGAINS("TaxBasis.ChargeableGains"),

    /**
     * TaxBasis ResidentialGains.
     */
    TAXBASIS_RESIDENTIALGAINS("TaxBasis.ResidentialGains"),

    /**
     * TaxBasis CapitalGains.
     */
    TAXBASIS_CAPITALGAINS("TaxBasis.CapitalGains"),

    /**
     * TaxBasis TaxPaid.
     */
    TAXBASIS_TAXPAID("TaxBasis.TaxPaid"),

    /**
     * TaxBasis Market.
     */
    TAXBASIS_MARKET("TaxBasis.Market"),

    /**
     * TaxBasis TaxFree.
     */
    TAXBASIS_TAXFREE("TaxBasis.TaxFree"),

    /**
     * TaxBasis Expenses.
     */
    TAXBASIS_EXPENSE("TaxBasis.Expense"),

    /**
     * TaxBasis Virtual.
     */
    TAXBASIS_VIRTUAL("TaxBasis.Virtual"),

    /**
     * Frequency Once.
     */
    FREQUENCY_ONCE("Frequency.Once"),

    /**
     * Frequency Daily.
     */
    FREQUENCY_DAILY("Frequency.Daily"),

    /**
     * Frequency Weekly.
     */
    FREQUENCY_WEEKLY("Frequency.Weekly"),

    /**
     * Frequency Monthly.
     */
    FREQUENCY_MONTHLY("Frequency.Monthly"),

    /**
     * Frequency Annually.
     */
    FREQUENCY_ANNUALLY("Frequency.Annually"),

    /**
     * Repeat Frequency Every.
     */
    FREQUENCY_EVERY("Frequency.Repeat.Every"),

    /**
     * Repeat Frequency Alternate.
     */
    FREQUENCY_ALTERNATE("Frequency.Repeat.Alternate"),

    /**
     * Repeat Frequency Every Third.
     */
    FREQUENCY_EVERYTHIRD("Frequency.Repeat.EveryThird"),

    /**
     * Repeat Frequency Every Fourth.
     */
    FREQUENCY_EVERYFOURTH("Frequency.Repeat.EveryFourth"),

    /**
     * Repeat Frequency Every Sixth.
     */
    FREQUENCY_EVERYSIXTH("Frequency.Repeat.EverySixth"),

    /**
     * Repeat Frequency FirstWeek.
     */
    FREQUENCY_FIRSTWEEK("Frequency.Week.First"),

    /**
     * Repeat Frequency SecondWeek.
     */
    FREQUENCY_SECONDWEEK("Frequency.Week.Second"),

    /**
     * Repeat Frequency ThirdWeek.
     */
    FREQUENCY_THIRDWEEK("Frequency.Week.Third"),

    /**
     * Repeat Frequency FourthWeek.
     */
    FREQUENCY_FOURTHWEEK("Frequency.Week.Fourth"),

    /**
     * Repeat Frequency LastWeek.
     */
    FREQUENCY_LASTWEEK("Frequency.Week.Last"),

    /**
     * Currency Default.
     */
    CURRENCY_DEFAULT("Currency.Default"),

    /**
     * AccountInfo Maturity.
     */
    ACCOUNTINFO_MATURITY("AccountInfoType.Maturity"),

    /**
     * AccountInfo OpeningBalance.
     */
    ACCOUNTINFO_OPENING("AccountInfoType.OpeningBalance"),

    /**
     * AccountInfo AutoExpense.
     */
    ACCOUNTINFO_AUTOEXPENSE("AccountInfoType.AutoExpense"),

    /**
     * AccountInfo AutoPayee.
     */
    ACCOUNTINFO_AUTOPAYEE("AccountInfoType.AutoPayee"),

    /**
     * AccountInfo WebSite.
     */
    ACCOUNTINFO_WEBSITE("AccountInfoType.WebSite"),

    /**
     * AccountInfo CustomerNo.
     */
    ACCOUNTINFO_CUSTNO("AccountInfoType.CustomerNo"),

    /**
     * AccountInfo UserId.
     */
    ACCOUNTINFO_USERID("AccountInfoType.UserId"),

    /**
     * AccountInfo Password.
     */
    ACCOUNTINFO_PASSWORD("AccountInfoType.Password"),

    /**
     * AccountInfo SortCode.
     */
    ACCOUNTINFO_SORTCODE("AccountInfoType.SortCode"),

    /**
     * AccountInfo Account.
     */
    ACCOUNTINFO_ACCOUNT("AccountInfoType.Account"),

    /**
     * AccountInfo Reference.
     */
    ACCOUNTINFO_REFERENCE("AccountInfoType.Reference"),

    /**
     * AccountInfo Notes.
     */
    ACCOUNTINFO_NOTES("AccountInfoType.Notes"),

    /**
     * AccountInfo Region.
     */
    ACCOUNTINFO_REGION("AccountInfoType.Region"),

    /**
     * AccountInfo Symbol.
     */
    ACCOUNTINFO_SYMBOL("AccountInfoType.Symbol"),

    /**
     * AccountInfo UnderlyingStock.
     */
    ACCOUNTINFO_UNDERLYINGSTOCK("AccountInfoType.UnderlyingStock"),

    /**
     * AccountInfo OptionPrice.
     */
    ACCOUNTINFO_OPTIONPRICE("AccountInfoType.OptionPrice"),

    /**
     * TransInfo AccountDeltaUnits.
     */
    TRANSINFO_ACCOUNTDELTAUNITS("TransInfoType.AccountDeltaUnits"),

    /**
     * TransInfo PartnerDeltaUnits.
     */
    TRANSINFO_PARTNERDELTAUNITS("TransInfoType.PartnerDeltaUnits"),

    /**
     * TransInfo PartnerAmount.
     */
    TRANSINFO_PARTNERAMOUNT("TransInfoType.PartnerAmount"),

    /**
     * TransInfo ReturnedCash.
     */
    TRANSINFO_RETURNEDCASH("TransInfoType.ReturnedCash"),

    /**
     * TransInfo Dilution.
     */
    TRANSINFO_DILUTION("TransInfoType.Dilution"),

    /**
     * TransInfo QualifyYears.
     */
    TRANSINFO_QUALYEARS("TransInfoType.QualifyYears"),

    /**
     * TransInfo Reference.
     */
    TRANSINFO_REFERENCE("TransInfoType.Reference"),

    /**
     * TransInfo Comments.
     */
    TRANSINFO_COMMENTS("TransInfoType.Comments"),

    /**
     * TransInfo TaxCredit.
     */
    TRANSINFO_TAXCREDIT("TransInfoType.TaxCredit"),

    /**
     * TransInfo Benefit.
     */
    TRANSINFO_BENEFIT("TransInfoType.Benefit"),

    /**
     * TransInfo ReturnedCashAccount.
     */
    TRANSINFO_RETURNEDCASHACCOUNT("TransInfoType.ReturnedCashAccount"),

    /**
     * TransInfo Price.
     */
    TRANSINFO_PRICE("TransInfoType.Price"),

    /**
     * TransInfo XchangeRate.
     */
    TRANSINFO_XCHANGERATE("TransInfoType.XchangeRate"),

    /**
     * TransInfo Commission.
     */
    TRANSINFO_COMMISSION("TransInfoType.Commission"),

    /**
     * TransInfo TransactionTag.
     */
    TRANSINFO_TRANSTAG("TransInfoType.TransTag");

    /**
     * The Name Map.
     */
    private static final Map<MoneyWiseStaticDataType, TethysBundleId> NAME_MAP = buildNameMap();

    /**
     * The List Map.
     */
    private static final Map<MoneyWiseStaticDataType, TethysBundleId> LIST_MAP = buildListMap();

    /**
     * The DepositType Map.
     */
    private static final Map<MoneyWiseDepositCategoryClass, TethysBundleId> DEPOSIT_MAP = buildDepositMap();

    /**
     * The CashType Map.
     */
    private static final Map<MoneyWiseCashCategoryClass, TethysBundleId> CASH_MAP = buildCashMap();

    /**
     * The LoanType Map.
     */
    private static final Map<MoneyWiseLoanCategoryClass, TethysBundleId> LOAN_MAP = buildLoanMap();

    /**
     * The PortfolioType Map.
     */
    private static final Map<MoneyWisePortfolioClass, TethysBundleId> PORTFOLIO_MAP = buildPortfolioMap();

    /**
     * The SecurityType Map.
     */
    private static final Map<MoneyWiseSecurityClass, TethysBundleId> SECURITY_MAP = buildSecurityMap();

    /**
     * The PayeeType Map.
     */
    private static final Map<MoneyWisePayeeClass, TethysBundleId> PAYEE_MAP = buildPayeeMap();

    /**
     * The TransactionType Map.
     */
    private static final Map<MoneyWiseTransCategoryClass, TethysBundleId> TRANSACTION_MAP = buildTransactionMap();

    /**
     * The TaxBasis Map.
     */
    private static final Map<MoneyWiseTaxClass, TethysBundleId> TAXBASIS_MAP = buildTaxBasisMap();

    /**
     * The AccountInfo Map.
     */
    private static final Map<MoneyWiseAccountInfoClass, TethysBundleId> ACCOUNTINFO_MAP = buildAccountInfoMap();

    /**
     * The TransInfo Map.
     */
    private static final Map<MoneyWiseTransInfoClass, TethysBundleId> TRANSINFO_MAP = buildTransInfoMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getLoader(MoneyWiseStaticResource.class.getCanonicalName(),
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
    MoneyWiseStaticResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    MoneyWiseStaticResource(final TethysBundleId pResource) {
        theKeyName = null;
        theValue = pResource.getValue();
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMoneyWise.static";
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
     * Build deposit type map.
     * @return the map
     */
    private static Map<MoneyWiseDepositCategoryClass, TethysBundleId> buildDepositMap() {
        /* Create the map and return it */
        final Map<MoneyWiseDepositCategoryClass, TethysBundleId> myMap = new EnumMap<>(MoneyWiseDepositCategoryClass.class);
        myMap.put(MoneyWiseDepositCategoryClass.CHECKING, DEPOSITTYPE_CHECKING);
        myMap.put(MoneyWiseDepositCategoryClass.SAVINGS, DEPOSITTYPE_SAVINGS);
        myMap.put(MoneyWiseDepositCategoryClass.TAXFREESAVINGS, DEPOSITTYPE_TAXFREESAVINGS);
        myMap.put(MoneyWiseDepositCategoryClass.PEER2PEER, DEPOSITTYPE_PEER2PEER);
        myMap.put(MoneyWiseDepositCategoryClass.BOND, DEPOSITTYPE_BOND);
        myMap.put(MoneyWiseDepositCategoryClass.TAXFREEBOND, DEPOSITTYPE_TAXFREEBOND);
        myMap.put(MoneyWiseDepositCategoryClass.PARENT, CATEGORYTYPE_PARENT);
        return myMap;
    }

    /**
     * Obtain key for deposit type.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForDepositType(final MoneyWiseDepositCategoryClass pValue) {
        return TethysBundleLoader.getKeyForEnum(DEPOSIT_MAP, pValue);
    }

    /**
     * Build cash type map.
     * @return the map
     */
    private static Map<MoneyWiseCashCategoryClass, TethysBundleId> buildCashMap() {
        /* Create the map and return it */
        final Map<MoneyWiseCashCategoryClass, TethysBundleId> myMap = new EnumMap<>(MoneyWiseCashCategoryClass.class);
        myMap.put(MoneyWiseCashCategoryClass.CASH, CASHTYPE_CASH);
        myMap.put(MoneyWiseCashCategoryClass.AUTOEXPENSE, CASHTYPE_AUTO);
        myMap.put(MoneyWiseCashCategoryClass.PARENT, CATEGORYTYPE_PARENT);
        return myMap;
    }

    /**
     * Obtain key for cash type.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForCashType(final MoneyWiseCashCategoryClass pValue) {
        return TethysBundleLoader.getKeyForEnum(CASH_MAP, pValue);
    }

    /**
     * Build loan type map.
     * @return the map
     */
    private static Map<MoneyWiseLoanCategoryClass, TethysBundleId> buildLoanMap() {
        /* Create the map and return it */
        final Map<MoneyWiseLoanCategoryClass, TethysBundleId> myMap = new EnumMap<>(MoneyWiseLoanCategoryClass.class);
        myMap.put(MoneyWiseLoanCategoryClass.CREDITCARD, LOANTYPE_CREDIT);
        myMap.put(MoneyWiseLoanCategoryClass.PRIVATELOAN, LOANTYPE_PRIVATE);
        myMap.put(MoneyWiseLoanCategoryClass.LOAN, LOANTYPE_LOAN);
        myMap.put(MoneyWiseLoanCategoryClass.PARENT, CATEGORYTYPE_PARENT);
        return myMap;
    }

    /**
     * Obtain key for loan type.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForLoanType(final MoneyWiseLoanCategoryClass pValue) {
        return TethysBundleLoader.getKeyForEnum(LOAN_MAP, pValue);
    }

    /**
     * Build portfolio type map.
     * @return the map
     */
    private static Map<MoneyWisePortfolioClass, TethysBundleId> buildPortfolioMap() {
        /* Create the map and return it */
        final Map<MoneyWisePortfolioClass, TethysBundleId> myMap = new EnumMap<>(MoneyWisePortfolioClass.class);
        myMap.put(MoneyWisePortfolioClass.STANDARD, PORTFOLIOTYPE_STANDARD);
        myMap.put(MoneyWisePortfolioClass.TAXFREE, PORTFOLIOTYPE_TAXFREE);
        myMap.put(MoneyWisePortfolioClass.PENSION, PORTFOLIOTYPE_PENSION);
        myMap.put(MoneyWisePortfolioClass.SIPP, PORTFOLIOTYPE_SIPP);
        return myMap;
    }

    /**
     * Obtain key for security type.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForPortfolioType(final MoneyWisePortfolioClass pValue) {
        return TethysBundleLoader.getKeyForEnum(PORTFOLIO_MAP, pValue);
    }

    /**
     * Build security type map.
     * @return the map
     */
    private static Map<MoneyWiseSecurityClass, TethysBundleId> buildSecurityMap() {
        /* Create the map and return it */
        final Map<MoneyWiseSecurityClass, TethysBundleId> myMap = new EnumMap<>(MoneyWiseSecurityClass.class);
        myMap.put(MoneyWiseSecurityClass.SHARES, SECURITYTYPE_SHARES);
        myMap.put(MoneyWiseSecurityClass.INCOMEUNITTRUST, SECURITYTYPE_INCOMEUNIT);
        myMap.put(MoneyWiseSecurityClass.GROWTHUNITTRUST, SECURITYTYPE_GROWTHUNIT);
        myMap.put(MoneyWiseSecurityClass.LIFEBOND, SECURITYTYPE_LIFEBOND);
        myMap.put(MoneyWiseSecurityClass.ENDOWMENT, SECURITYTYPE_ENDOWMENT);
        myMap.put(MoneyWiseSecurityClass.PROPERTY, SECURITYTYPE_PROPERTY);
        myMap.put(MoneyWiseSecurityClass.VEHICLE, SECURITYTYPE_VEHICLE);
        myMap.put(MoneyWiseSecurityClass.STATEPENSION, SECURITYTYPE_STATEPENSION);
        myMap.put(MoneyWiseSecurityClass.DEFINEDBENEFIT, SECURITYTYPE_BENEFIT);
        myMap.put(MoneyWiseSecurityClass.DEFINEDCONTRIBUTION, SECURITYTYPE_CONTRIBUTION);
        myMap.put(MoneyWiseSecurityClass.STOCKOPTION, SECURITYTYPE_STOCKOPTION);
        myMap.put(MoneyWiseSecurityClass.ASSET, SECURITYTYPE_ASSET);
        return myMap;
    }

    /**
     * Obtain key for security type.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForSecurityType(final MoneyWiseSecurityClass pValue) {
        return TethysBundleLoader.getKeyForEnum(SECURITY_MAP, pValue);
    }

    /**
     * Build payee type map.
     * @return the map
     */
    private static Map<MoneyWisePayeeClass, TethysBundleId> buildPayeeMap() {
        /* Create the map and return it */
        final Map<MoneyWisePayeeClass, TethysBundleId> myMap = new EnumMap<>(MoneyWisePayeeClass.class);
        myMap.put(MoneyWisePayeeClass.TAXMAN, PAYEETYPE_TAXMAN);
        myMap.put(MoneyWisePayeeClass.GOVERNMENT, PAYEETYPE_GOVERNMENT);
        myMap.put(MoneyWisePayeeClass.MARKET, PAYEETYPE_MARKET);
        myMap.put(MoneyWisePayeeClass.EMPLOYER, PAYEETYPE_EMPLOYER);
        myMap.put(MoneyWisePayeeClass.INSTITUTION, PAYEETYPE_INSTITUTION);
        myMap.put(MoneyWisePayeeClass.INDIVIDUAL, PAYEETYPE_INDIVIDUAL);
        myMap.put(MoneyWisePayeeClass.ANNUITY, PAYEETYPE_ANNUITY);
        myMap.put(MoneyWisePayeeClass.PAYEE, PAYEETYPE_PAYEE);
        return myMap;
    }

    /**
     * Obtain key for payee type.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForPayeeType(final MoneyWisePayeeClass pValue) {
        return TethysBundleLoader.getKeyForEnum(PAYEE_MAP, pValue);
    }

    /**
     * Build transaction type map.
     * @return the map
     */
    private static Map<MoneyWiseTransCategoryClass, TethysBundleId> buildTransactionMap() {
        /* Create the map and return it */
        final Map<MoneyWiseTransCategoryClass, TethysBundleId> myMap = new EnumMap<>(MoneyWiseTransCategoryClass.class);
        myMap.put(MoneyWiseTransCategoryClass.TAXEDINCOME, TRANSTYPE_TAXEDINCOME);
        myMap.put(MoneyWiseTransCategoryClass.RENTALINCOME, TRANSTYPE_RENTALINCOME);
        myMap.put(MoneyWiseTransCategoryClass.ROOMRENTALINCOME, TRANSTYPE_ROOMRENTINCOME);
        myMap.put(MoneyWiseTransCategoryClass.INTEREST, TRANSTYPE_INTEREST);
        myMap.put(MoneyWiseTransCategoryClass.DIVIDEND, TRANSTYPE_DIVIDEND);
        myMap.put(MoneyWiseTransCategoryClass.VIRTUALINCOME, TRANSTYPE_VIRTUALINCOME);
        myMap.put(MoneyWiseTransCategoryClass.GROSSINCOME, TRANSTYPE_GROSSINCOME);
        myMap.put(MoneyWiseTransCategoryClass.PENSIONCONTRIB, TRANSTYPE_PENSIONCONTRIB);
        myMap.put(MoneyWiseTransCategoryClass.GIFTEDINCOME, TRANSTYPE_GIFTEDINCOME);
        myMap.put(MoneyWiseTransCategoryClass.INHERITED, TRANSTYPE_INHERITED);
        myMap.put(MoneyWiseTransCategoryClass.LOANINTERESTEARNED, TRANSTYPE_LOANINTEARNED);
        myMap.put(MoneyWiseTransCategoryClass.CASHBACK, TRANSTYPE_CASHBACK);
        myMap.put(MoneyWiseTransCategoryClass.LOYALTYBONUS, TRANSTYPE_LOYALTYBONUS);
        myMap.put(MoneyWiseTransCategoryClass.RECOVEREDEXPENSES, TRANSTYPE_RECOVEREDEXPENSES);
        myMap.put(MoneyWiseTransCategoryClass.OTHERINCOME, TRANSTYPE_OTHERINCOME);
        myMap.put(MoneyWiseTransCategoryClass.TRANSFER, TRANSTYPE_TRANSFER);
        myMap.put(MoneyWiseTransCategoryClass.UNITSADJUST, TRANSTYPE_UNITSADJUST);
        myMap.put(MoneyWiseTransCategoryClass.STOCKSPLIT, TRANSTYPE_STOCKSPLIT);
        myMap.put(MoneyWiseTransCategoryClass.STOCKDEMERGER, TRANSTYPE_STOCKDEMERGER);
        myMap.put(MoneyWiseTransCategoryClass.STOCKTAKEOVER, TRANSTYPE_STOCKTAKEOVER);
        myMap.put(MoneyWiseTransCategoryClass.SECURITYREPLACE, TRANSTYPE_SECURITYREPLACE);
        myMap.put(MoneyWiseTransCategoryClass.SECURITYCLOSURE, TRANSTYPE_SECURITYCLOSURE);
        myMap.put(MoneyWiseTransCategoryClass.STOCKRIGHTSISSUE, TRANSTYPE_STOCKRIGHTSISSUE);
        myMap.put(MoneyWiseTransCategoryClass.PORTFOLIOXFER, TRANSTYPE_PORTFOLIOXFER);
        myMap.put(MoneyWiseTransCategoryClass.OPTIONSGRANT, TRANSTYPE_OPTIONSGRANT);
        myMap.put(MoneyWiseTransCategoryClass.OPTIONSVEST, TRANSTYPE_OPTIONSVEST);
        myMap.put(MoneyWiseTransCategoryClass.OPTIONSEXPIRE, TRANSTYPE_OPTIONSEXPIRE);
        myMap.put(MoneyWiseTransCategoryClass.OPTIONSEXERCISE, TRANSTYPE_OPTIONSEXERCISE);
        myMap.put(MoneyWiseTransCategoryClass.PENSIONDRAWDOWN, TRANSTYPE_PENSIONDRAWDOWN);
        myMap.put(MoneyWiseTransCategoryClass.PENSIONTAXFREE, TRANSTYPE_PENSIONTAXFREE);
        myMap.put(MoneyWiseTransCategoryClass.EXPENSE, TRANSTYPE_EXPENSE);
        myMap.put(MoneyWiseTransCategoryClass.BADDEBTCAPITAL, TRANSTYPE_BADDEBTCAPITAL);
        myMap.put(MoneyWiseTransCategoryClass.BADDEBTINTEREST, TRANSTYPE_BADDEBTINTEREST);
        myMap.put(MoneyWiseTransCategoryClass.LOCALTAXES, TRANSTYPE_LOCALTAXES);
        myMap.put(MoneyWiseTransCategoryClass.WRITEOFF, TRANSTYPE_WRITEOFF);
        myMap.put(MoneyWiseTransCategoryClass.RENTALEXPENSE, TRANSTYPE_RENTALEXPENSE);
        myMap.put(MoneyWiseTransCategoryClass.ANNUITYPURCHASE, TRANSTYPE_ANNUITYPURCHASE);
        myMap.put(MoneyWiseTransCategoryClass.LOANINTERESTCHARGED, TRANSTYPE_LOANINTCHARGE);
        myMap.put(MoneyWiseTransCategoryClass.TAXRELIEF, TRANSTYPE_TAXRELIEF);
        myMap.put(MoneyWiseTransCategoryClass.INCOMETAX, TRANSTYPE_INCOMETAX);
        myMap.put(MoneyWiseTransCategoryClass.TAXEDINTEREST, TRANSTYPE_TAXEDINTEREST);
        myMap.put(MoneyWiseTransCategoryClass.TAXFREEINTEREST, TRANSTYPE_TAXFREEINTEREST);
        myMap.put(MoneyWiseTransCategoryClass.PEER2PEERINTEREST, TRANSTYPE_PEER2PEERINTEREST);
        myMap.put(MoneyWiseTransCategoryClass.GROSSINTEREST, TRANSTYPE_GROSSINTEREST);
        myMap.put(MoneyWiseTransCategoryClass.SHAREDIVIDEND, TRANSTYPE_SHAREDIVIDEND);
        myMap.put(MoneyWiseTransCategoryClass.UNITTRUSTDIVIDEND, TRANSTYPE_UTDIVIDEND);
        myMap.put(MoneyWiseTransCategoryClass.FOREIGNDIVIDEND, TRANSTYPE_FOREIGNDIVIDEND);
        myMap.put(MoneyWiseTransCategoryClass.TAXFREEDIVIDEND, TRANSTYPE_TAXFREEDIVIDEND);
        myMap.put(MoneyWiseTransCategoryClass.TAXEDLOYALTYBONUS, TRANSTYPE_TAXEDLOYALTYBONUS);
        myMap.put(MoneyWiseTransCategoryClass.GROSSLOYALTYBONUS, TRANSTYPE_GROSSLOYALTYBONUS);
        myMap.put(MoneyWiseTransCategoryClass.TAXFREELOYALTYBONUS, TRANSTYPE_TAXFREELOYALTYBONUS);
        myMap.put(MoneyWiseTransCategoryClass.CHARGEABLEGAIN, TRANSTYPE_CHARGEABLEGAIN);
        myMap.put(MoneyWiseTransCategoryClass.TAXFREEGAIN, TRANSTYPE_TAXFREEGAIN);
        myMap.put(MoneyWiseTransCategoryClass.RESIDENTIALGAIN, TRANSTYPE_RESIDENTIALGAIN);
        myMap.put(MoneyWiseTransCategoryClass.CAPITALGAIN, TRANSTYPE_CAPITALGAIN);
        myMap.put(MoneyWiseTransCategoryClass.MARKETGROWTH, TRANSTYPE_MARKETGROWTH);
        myMap.put(MoneyWiseTransCategoryClass.CURRENCYFLUCTUATION, TRANSTYPE_CURRFLUCT);
        myMap.put(MoneyWiseTransCategoryClass.WITHHELD, TRANSTYPE_WITHHELD);
        myMap.put(MoneyWiseTransCategoryClass.EMPLOYERNATINS, TRANSTYPE_EMPLOYERNATINS);
        myMap.put(MoneyWiseTransCategoryClass.EMPLOYEENATINS, TRANSTYPE_EMPLOYEENATINS);
        myMap.put(MoneyWiseTransCategoryClass.OPENINGBALANCE, TRANSTYPE_OPENINGBALANCE);
        myMap.put(MoneyWiseTransCategoryClass.INCOMETOTALS, TRANSTYPE_INCOMETOTALS);
        myMap.put(MoneyWiseTransCategoryClass.EXPENSETOTALS, TRANSTYPE_EXPENSETOTALS);
        myMap.put(MoneyWiseTransCategoryClass.SECURITYPARENT, TRANSTYPE_SECURITYPARENT);
        myMap.put(MoneyWiseTransCategoryClass.TOTALS, TRANSTYPE_TOTALS);
        return myMap;
    }

    /**
     * Obtain key for transaction type.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForTransType(final MoneyWiseTransCategoryClass pValue) {
        return TethysBundleLoader.getKeyForEnum(TRANSACTION_MAP, pValue);
    }

    /**
     * Build tax basis map.
     * @return the map
     */
    private static Map<MoneyWiseTaxClass, TethysBundleId> buildTaxBasisMap() {
        /* Create the map and return it */
        final Map<MoneyWiseTaxClass, TethysBundleId> myMap = new EnumMap<>(MoneyWiseTaxClass.class);
        myMap.put(MoneyWiseTaxClass.SALARY, TAXBASIS_SALARY);
        myMap.put(MoneyWiseTaxClass.ROOMRENTAL, TAXBASIS_ROOMRENTAL);
        myMap.put(MoneyWiseTaxClass.RENTALINCOME, TAXBASIS_RENTALINCOME);
        myMap.put(MoneyWiseTaxClass.OTHERINCOME, TAXBASIS_OTHERINCOME);
        myMap.put(MoneyWiseTaxClass.TAXEDINTEREST, TAXBASIS_TAXEDINTEREST);
        myMap.put(MoneyWiseTaxClass.UNTAXEDINTEREST, TAXBASIS_UNTAXEDINTEREST);
        myMap.put(MoneyWiseTaxClass.DIVIDEND, TAXBASIS_DIVIDEND);
        myMap.put(MoneyWiseTaxClass.UNITTRUSTDIVIDEND, TAXBASIS_UTDIVIDEND);
        myMap.put(MoneyWiseTaxClass.FOREIGNDIVIDEND, TAXBASIS_FOREIGNDIVIDEND);
        myMap.put(MoneyWiseTaxClass.CHARGEABLEGAINS, TAXBASIS_CHARGEABLEGAINS);
        myMap.put(MoneyWiseTaxClass.RESIDENTIALGAINS, TAXBASIS_RESIDENTIALGAINS);
        myMap.put(MoneyWiseTaxClass.CAPITALGAINS, TAXBASIS_CAPITALGAINS);
        myMap.put(MoneyWiseTaxClass.PEER2PEERINTEREST, TRANSTYPE_PEER2PEERINTEREST);
        myMap.put(MoneyWiseTaxClass.TAXPAID, TAXBASIS_TAXPAID);
        myMap.put(MoneyWiseTaxClass.MARKET, TAXBASIS_MARKET);
        myMap.put(MoneyWiseTaxClass.TAXFREE, TAXBASIS_TAXFREE);
        myMap.put(MoneyWiseTaxClass.EXPENSE, TAXBASIS_EXPENSE);
        myMap.put(MoneyWiseTaxClass.VIRTUAL, TAXBASIS_VIRTUAL);
        return myMap;
    }

    /**
     * Obtain key for taxBasis.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForTaxBasis(final MoneyWiseTaxClass pValue) {
        return TethysBundleLoader.getKeyForEnum(TAXBASIS_MAP, pValue);
    }

    /**
     * Build accountInfo map.
     * @return the map
     */
    private static Map<MoneyWiseAccountInfoClass, TethysBundleId> buildAccountInfoMap() {
        /* Create the map and return it */
        final Map<MoneyWiseAccountInfoClass, TethysBundleId> myMap = new EnumMap<>(MoneyWiseAccountInfoClass.class);
        myMap.put(MoneyWiseAccountInfoClass.MATURITY, ACCOUNTINFO_MATURITY);
        myMap.put(MoneyWiseAccountInfoClass.OPENINGBALANCE, ACCOUNTINFO_OPENING);
        myMap.put(MoneyWiseAccountInfoClass.AUTOEXPENSE, ACCOUNTINFO_AUTOEXPENSE);
        myMap.put(MoneyWiseAccountInfoClass.AUTOPAYEE, ACCOUNTINFO_AUTOPAYEE);
        myMap.put(MoneyWiseAccountInfoClass.WEBSITE, ACCOUNTINFO_WEBSITE);
        myMap.put(MoneyWiseAccountInfoClass.CUSTOMERNO, ACCOUNTINFO_CUSTNO);
        myMap.put(MoneyWiseAccountInfoClass.USERID, ACCOUNTINFO_USERID);
        myMap.put(MoneyWiseAccountInfoClass.PASSWORD, ACCOUNTINFO_PASSWORD);
        myMap.put(MoneyWiseAccountInfoClass.SORTCODE, ACCOUNTINFO_SORTCODE);
        myMap.put(MoneyWiseAccountInfoClass.ACCOUNT, ACCOUNTINFO_ACCOUNT);
        myMap.put(MoneyWiseAccountInfoClass.REFERENCE, ACCOUNTINFO_REFERENCE);
        myMap.put(MoneyWiseAccountInfoClass.NOTES, ACCOUNTINFO_NOTES);
        myMap.put(MoneyWiseAccountInfoClass.REGION, ACCOUNTINFO_REGION);
        myMap.put(MoneyWiseAccountInfoClass.SYMBOL, ACCOUNTINFO_SYMBOL);
        myMap.put(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK, ACCOUNTINFO_UNDERLYINGSTOCK);
        myMap.put(MoneyWiseAccountInfoClass.OPTIONPRICE, ACCOUNTINFO_OPTIONPRICE);
        return myMap;
    }

    /**
     * Obtain key for accountInfoType.
     * @param pValue the Value
     * @return the resource key
     */
    public static MoneyWiseStaticResource getKeyForAccountInfo(final MoneyWiseAccountInfoClass pValue) {
        return (MoneyWiseStaticResource) TethysBundleLoader.getKeyForEnum(ACCOUNTINFO_MAP, pValue);
    }

    /**
     * Build transInfo map.
     * @return the map
     */
    private static Map<MoneyWiseTransInfoClass, TethysBundleId> buildTransInfoMap() {
        /* Create the map and return it */
        final Map<MoneyWiseTransInfoClass, TethysBundleId> myMap = new EnumMap<>(MoneyWiseTransInfoClass.class);
        myMap.put(MoneyWiseTransInfoClass.TAXCREDIT, TRANSINFO_TAXCREDIT);
        myMap.put(MoneyWiseTransInfoClass.EMPLOYERNATINS, TRANSTYPE_EMPLOYERNATINS);
        myMap.put(MoneyWiseTransInfoClass.EMPLOYEENATINS, TRANSTYPE_EMPLOYEENATINS);
        myMap.put(MoneyWiseTransInfoClass.DEEMEDBENEFIT, TRANSINFO_BENEFIT);
        myMap.put(MoneyWiseTransInfoClass.WITHHELD, TRANSTYPE_WITHHELD);
        myMap.put(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS, TRANSINFO_ACCOUNTDELTAUNITS);
        myMap.put(MoneyWiseTransInfoClass.PARTNERDELTAUNITS, TRANSINFO_PARTNERDELTAUNITS);
        myMap.put(MoneyWiseTransInfoClass.PARTNERAMOUNT, TRANSINFO_PARTNERAMOUNT);
        myMap.put(MoneyWiseTransInfoClass.RETURNEDCASH, TRANSINFO_RETURNEDCASH);
        myMap.put(MoneyWiseTransInfoClass.DILUTION, TRANSINFO_DILUTION);
        myMap.put(MoneyWiseTransInfoClass.QUALIFYYEARS, TRANSINFO_QUALYEARS);
        myMap.put(MoneyWiseTransInfoClass.REFERENCE, TRANSINFO_REFERENCE);
        myMap.put(MoneyWiseTransInfoClass.COMMENTS, TRANSINFO_COMMENTS);
        myMap.put(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT, TRANSINFO_RETURNEDCASHACCOUNT);
        myMap.put(MoneyWiseTransInfoClass.PRICE, TRANSINFO_PRICE);
        myMap.put(MoneyWiseTransInfoClass.XCHANGERATE, TRANSINFO_XCHANGERATE);
        myMap.put(MoneyWiseTransInfoClass.COMMISSION, TRANSINFO_COMMISSION);
        myMap.put(MoneyWiseTransInfoClass.TRANSTAG, TRANSINFO_TRANSTAG);
        return myMap;
    }

    /**
     * Obtain key for transInfoType.
     * @param pValue the Value
     * @return the resource key
     */
    public static MoneyWiseStaticResource getKeyForTransInfo(final MoneyWiseTransInfoClass pValue) {
        return (MoneyWiseStaticResource) TethysBundleLoader.getKeyForEnum(TRANSINFO_MAP, pValue);
    }
    /**
     * Build name map.
     * @return the map
     */
    private static Map<MoneyWiseStaticDataType, TethysBundleId> buildNameMap() {
        /* Create the map and return it */
        final Map<MoneyWiseStaticDataType, TethysBundleId> myMap = new EnumMap<>(MoneyWiseStaticDataType.class);
        myMap.put(MoneyWiseStaticDataType.DEPOSITTYPE, DEPOSITTYPE_NAME);
        myMap.put(MoneyWiseStaticDataType.CASHTYPE, CASHTYPE_NAME);
        myMap.put(MoneyWiseStaticDataType.LOANTYPE, LOANTYPE_NAME);
        myMap.put(MoneyWiseStaticDataType.PORTFOLIOTYPE, PORTFOLIOTYPE_NAME);
        myMap.put(MoneyWiseStaticDataType.SECURITYTYPE, SECURITYTYPE_NAME);
        myMap.put(MoneyWiseStaticDataType.PAYEETYPE, PAYEETYPE_NAME);
        myMap.put(MoneyWiseStaticDataType.TRANSTYPE, TRANSTYPE_NAME);
        myMap.put(MoneyWiseStaticDataType.TAXBASIS, TAXBASIS_NAME);
        myMap.put(MoneyWiseStaticDataType.CURRENCY, CURRENCY_NAME);
        myMap.put(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, ACCOUNTINFOTYPE_NAME);
        myMap.put(MoneyWiseStaticDataType.TRANSINFOTYPE, TRANSINFOTYPE_NAME);
        return myMap;
    }

    /**
     * Obtain key for data item.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForDataType(final MoneyWiseStaticDataType pValue) {
        return TethysBundleLoader.getKeyForEnum(NAME_MAP, pValue);
    }

    /**
     * Build list map.
     * @return the map
     */
    private static Map<MoneyWiseStaticDataType, TethysBundleId> buildListMap() {
        /* Create the map and return it */
        final Map<MoneyWiseStaticDataType, TethysBundleId> myMap = new EnumMap<>(MoneyWiseStaticDataType.class);
        myMap.put(MoneyWiseStaticDataType.DEPOSITTYPE, DEPOSITTYPE_LIST);
        myMap.put(MoneyWiseStaticDataType.CASHTYPE, CASHTYPE_LIST);
        myMap.put(MoneyWiseStaticDataType.LOANTYPE, LOANTYPE_LIST);
        myMap.put(MoneyWiseStaticDataType.PORTFOLIOTYPE, PORTFOLIOTYPE_LIST);
        myMap.put(MoneyWiseStaticDataType.SECURITYTYPE, SECURITYTYPE_LIST);
        myMap.put(MoneyWiseStaticDataType.PAYEETYPE, PAYEETYPE_LIST);
        myMap.put(MoneyWiseStaticDataType.TRANSTYPE, TRANSTYPE_LIST);
        myMap.put(MoneyWiseStaticDataType.TAXBASIS, TAXBASIS_LIST);
        myMap.put(MoneyWiseStaticDataType.CURRENCY, CURRENCY_LIST);
        myMap.put(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, ACCOUNTINFOTYPE_LIST);
        myMap.put(MoneyWiseStaticDataType.TRANSINFOTYPE, TRANSINFOTYPE_LIST);
        return myMap;
    }

    /**
     * Obtain key for data list.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForDataList(final MoneyWiseStaticDataType pValue) {
        return TethysBundleLoader.getKeyForEnum(LIST_MAP, pValue);
    }
}
