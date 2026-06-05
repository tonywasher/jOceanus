/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.data.statics;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.ResourceBundle;

/**
 * Resource IDs for MoneyWise DataType Fields.
 */
public enum MoneyWiseStaticResource
        implements OceanusBundleId, MetisDataFieldId {
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
     * Currency Reporting.
     */
    CURRENCY_REPORTING("Currency.Reporting"),

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
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MoneyWiseStaticResource.class.getCanonicalName(),
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
     *
     * @param pKeyName the key name
     */
    MoneyWiseStaticResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "MoneyWise.static";
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
}
