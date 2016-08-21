/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for jMoneyWise DataType Fields.
 */
public enum StaticDataResource implements TethysResourceId {
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
     * DepositType Peer2Peer.
     */
    DEPOSITTYPE_PEER2PEER("DepositType.Peer2Peer"),

    /**
     * DepositType Bond.
     */
    DEPOSITTYPE_BOND("DepositType.Bond"),

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
     * SecurityType Shares.
     */
    SECURITYTYPE_SHARES("SecurityType.Shares"),

    /**
     * SecurityType UnitTrust.
     */
    SECURITYTYPE_UNIT("SecurityType.UnitTrust"),

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
     * TransType GrantIncome.
     */
    TRANSTYPE_GRANTINCOME("TransType.GrantIncome"),

    /**
     * TransType BenefitIncome.
     */
    TRANSTYPE_BENEFITINCOME("TransType.BenefitIncome"),

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
     * TransType TaxedIncome.
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
     * TransType OptionsVest.
     */
    TRANSTYPE_OPTIONSVEST("TransType.OptionsVest"),

    /**
     * TransType OptionsExercise.
     */
    TRANSTYPE_OPTIONSEXERCISE("TransType.OptionsExercise"),

    /**
     * TransType Expense.
     */
    TRANSTYPE_EXPENSE("TransType.Expense"),

    /**
     * TransType BadDebt.
     */
    TRANSTYPE_BADDEBT("TransType.BadDebt"),

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
     * TransType LoanInterestCharged.
     */
    TRANSTYPE_LOANINTCHARGE("TransType.LoanInterestCharged"),

    /**
     * TransType TaxRelief.
     */
    TRANSTYPE_TAXRELIEF("TransType.TaxRelief"),

    /**
     * TransType TaxSettlement.
     */
    TRANSTYPE_TAXSETTLE("TransType.TaxSettlement"),

    /**
     * TransType TaxedInterest.
     */
    TRANSTYPE_TAXEDINTEREST("TransType.TaxedInterest"),

    /**
     * TransType TaxFreeInterest.
     */
    TRANSTYPE_TAXFREEINTEREST("TransType.TaxFreeInterest"),

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
     * TransType TaxableGain.
     */
    TRANSTYPE_TAXABLEGAIN("TransType.TaxableGain"),

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
     * TransType TaxCredit.
     */
    TRANSTYPE_TAXCREDIT("TransType.TaxCredit"),

    /**
     * TransType NatInsurance.
     */
    TRANSTYPE_NATINS("TransType.NatInsurance"),

    /**
     * TransType Benefit.
     */
    TRANSTYPE_BENEFIT("TransType.DeemedBenefit"),

    /**
     * TransType CharityDonation.
     */
    TRANSTYPE_CHARDONATION("TransType.CharityDonation"),

    /**
     * TransType OpeningBalance.
     */
    TRANSTYPE_OPENINGBALANCE("TransType.OpeningBalance"),

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
     * TaxBasis TaxableGains.
     */
    TAXBASIS_TAXABLEGAINS("TaxBasis.TaxableGains"),

    /**
     * TaxBasis ResidentialGains.
     */
    TAXBASIS_RESIDENTIALGAINS("TaxBasis.ResidentialGains"),

    /**
     * TaxBasis CapitalGains.
     */
    TAXBASIS_CAPITALGAINS("TaxBasis.CapitalGains"),

    /**
     * TaxBasis BadDebt.
     */
    TAXBASIS_BADDEBT("TaxBasis.BadDebt"),

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
     * TaxType Gross Income.
     */
    TAXTYPE_BASE_INCOME("TaxType.Base.Income"),

    /**
     * TaxType Original Allowance.
     */
    TAXTYPE_BASE_ALLOWANCE("TaxType.Base.Allowance"),

    /**
     * TaxType Final Allowance.
     */
    TAXTYPE_FINAL_ALLOWANCE("TaxType.Final.Allowance"),

    /**
     * TaxType High Tax Band.
     */
    TAXTYPE_BAND_HIRATE("TaxType.Band.HiRate"),

    /**
     * TaxType Salary NilRate.
     */
    TAXTYPE_SALARY_NILRATE("TaxType.Salary.NilRate"),

    /**
     * TaxType Salary LoRate.
     */
    TAXTYPE_SALARY_LORATE("TaxType.Salary.LoRate"),

    /**
     * TaxType Salary BasicRate.
     */
    TAXTYPE_SALARY_BASICRATE("TaxType.Salary.BasicRate"),

    /**
     * TaxType Salary HiRate.
     */
    TAXTYPE_SALARY_HIRATE("TaxType.Salary.HiRate"),

    /**
     * TaxType Salary AdditionalRate.
     */
    TAXTYPE_SALARY_ADDRATE("TaxType.Salary.AddRate"),

    /**
     * TaxType Salary TaxDue.
     */
    TAXTYPE_SALARY_TAXDUE("TaxType.Salary.TaxDue"),

    /**
     * TaxType Rental NilRate.
     */
    TAXTYPE_RENTAL_NILRATE("TaxType.Rental.NilRate"),

    /**
     * TaxType Rental LoRate.
     */
    TAXTYPE_RENTAL_LORATE("TaxType.Rental.LoRate"),

    /**
     * TaxType Salary BasicRate.
     */
    TAXTYPE_RENTAL_BASICRATE("TaxType.Rental.BasicRate"),

    /**
     * TaxType Rental HiRate.
     */
    TAXTYPE_RENTAL_HIRATE("TaxType.Rental.HiRate"),

    /**
     * TaxType Rental AdditionalRate.
     */
    TAXTYPE_RENTAL_ADDRATE("TaxType.Rental.AddRate"),

    /**
     * TaxType Rental TaxDue.
     */
    TAXTYPE_RENTAL_TAXDUE("TaxType.Rental.TaxDue"),

    /**
     * TaxType Interest NilRate.
     */
    TAXTYPE_INTEREST_NILRATE("TaxType.Interest.NilRate"),

    /**
     * TaxType Interest LoRate.
     */
    TAXTYPE_INTEREST_LORATE("TaxType.Interest.LoRate"),

    /**
     * TaxType Interest BasicRate.
     */
    TAXTYPE_INTEREST_BASICRATE("TaxType.Interest.BasicRate"),

    /**
     * TaxType Interest HiRate.
     */
    TAXTYPE_INTEREST_HIRATE("TaxType.Interest.HiRate"),

    /**
     * TaxType Interest AdditionalRate.
     */
    TAXTYPE_INTEREST_ADDRATE("TaxType.Interest.AddRate"),

    /**
     * TaxType Interest TaxDue.
     */
    TAXTYPE_INTEREST_TAXDUE("TaxType.Interest.TaxDue"),

    /**
     * TaxType Dividend BasicRate.
     */
    TAXTYPE_DIVIDEND_BASICRATE("TaxType.Dividend.BasicRate"),

    /**
     * TaxType Dividend HiRate.
     */
    TAXTYPE_DIVIDEND_HIRATE("TaxType.Dividend.HiRate"),

    /**
     * TaxType Dividend AdditionalRate.
     */
    TAXTYPE_DIVIDEND_ADDRATE("TaxType.Dividend.AddRate"),

    /**
     * TaxType Dividend TaxDue.
     */
    TAXTYPE_DIVIDEND_TAXDUE("TaxType.Dividend.TaxDue"),

    /**
     * TaxType Slice BasicRate.
     */
    TAXTYPE_SLICE_BASICRATE("TaxType.Slice.BasicRate"),

    /**
     * TaxType Slice HiRate.
     */
    TAXTYPE_SLICE_HIRATE("TaxType.Slice.HiRate"),

    /**
     * TaxType Slice AdditionalRate.
     */
    TAXTYPE_SLICE_ADDRATE("TaxType.Slice.AddRate"),

    /**
     * TaxType Slice TaxDue.
     */
    TAXTYPE_SLICE_TAXDUE("TaxType.Slice.TaxDue"),

    /**
     * TaxType Gains BasicRate.
     */
    TAXTYPE_GAINS_BASICRATE("TaxType.Gains.BasicRate"),

    /**
     * TaxType Gains HiRate.
     */
    TAXTYPE_GAINS_HIRATE("TaxType.Gains.HiRate"),

    /**
     * TaxType Gains AdditionalRate.
     */
    TAXTYPE_GAINS_ADDRATE("TaxType.Gains.AddRate"),

    /**
     * TaxType Gains TaxDue.
     */
    TAXTYPE_GAINS_TAXDUE("TaxType.Gains.TaxDue"),

    /**
     * TaxType Capital NilRate.
     */
    TAXTYPE_CAPITAL_NILRATE("TaxType.Capital.NilRate"),

    /**
     * TaxType Capital BasicRate.
     */
    TAXTYPE_CAPITAL_BASICRATE("TaxType.Capital.BasicRate"),

    /**
     * TaxType Capital HiRate.
     */
    TAXTYPE_CAPITAL_HIRATE("TaxType.Capital.HiRate"),

    /**
     * TaxType Capital TaxDue.
     */
    TAXTYPE_CAPITAL_TAXDUE("TaxType.Capital.TaxDue"),

    /**
     * TaxType Total TaxDue.
     */
    TAXTYPE_TOTAL_TAXDUE("TaxType.Total.TaxDue"),

    /**
     * TaxType Capital TaxDue.
     */
    TAXTYPE_TOTAL_PROFITLOSS("TaxType.Total.ProfitLoss"),

    /**
     * TaxRegime Archive.
     */
    TAXREGIME_ARCHIVE("TaxRegime.Archive"),

    /**
     * TaxRegime Standard.
     */
    TAXREGIME_STANDARD("TaxRegime.Standard"),

    /**
     * TaxRegime LoInterest.
     */
    TAXREGIME_LOINTEREST("TaxRegime.LoInterest"),

    /**
     * TaxRegime Additional Band.
     */
    TAXREGIME_ADDBAND("TaxRegime.AdditionalBand"),

    /**
     * TaxRegime SavingsAllowance.
     */
    TAXREGIME_SAVINGSALLOW("TaxRegime.SavingsAllowance"),

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
     * TaxInfo Allowance.
     */
    TAXINFO_ALLOWANCE("TaxInfoType.Allowance"),

    /**
     * TaxInfo LoTaxBand.
     */
    TAXINFO_LOBAND("TaxInfoType.LoTaxBand"),

    /**
     * TaxInfo BasicTaxBand.
     */
    TAXINFO_BASICBAND("TaxInfoType.BasicTaxBand"),

    /**
     * TaxInfo Rental Allowance.
     */
    TAXINFO_RENTALALLOWANCE("TaxInfoType.RentalAllowance"),

    /**
     * TaxInfo Capital Allowance.
     */
    TAXINFO_CAPITALALLOWANCE("TaxInfoType.CapitalAllowance"),

    /**
     * TaxInfo Savings Allowance.
     */
    TAXINFO_SAVINGSALLOWANCE("TaxInfoType.SavingsAllowance"),

    /**
     * TaxInfo HiSavings Allowance.
     */
    TAXINFO_HISAVINGSALLOWANCE("TaxInfoType.HiSavingsAllowance"),

    /**
     * TaxInfo Dividend Allowance.
     */
    TAXINFO_DIVIDENDALLOWANCE("TaxInfoType.DividendAllowance"),

    /**
     * TaxInfo LoAgeAllowance.
     */
    TAXINFO_LOAGEALLOWANCE("TaxInfoType.LoAgeAllowance"),

    /**
     * TaxInfo HiAgeAllowance.
     */
    TAXINFO_HIAGEALLOWANCE("TaxInfoType.HiAgeAllowance"),

    /**
     * TaxInfo AgeAllowanceLimit.
     */
    TAXINFO_AGEALLOWLIMIT("TaxInfoType.AgeAllowanceLimit"),

    /**
     * TaxInfo AdditionalAllowanceLimit.
     */
    TAXINFO_ADDALLOWLIMIT("TaxInfoType.AdditionalAllowanceLimit"),

    /**
     * TaxInfo Additional Income Threshold.
     */
    TAXINFO_ADDINCTHRES("TaxInfoType.AdditionalIncomeThreshold"),

    /**
     * TaxInfo Low TaxRate.
     */
    TAXINFO_LORATE("TaxInfoType.LoTaxRate"),

    /**
     * TaxInfo Basic TaxRate.
     */
    TAXINFO_BASICRATE("TaxInfoType.BasicTaxRate"),

    /**
     * TaxInfo High TaxRate.
     */
    TAXINFO_HIRATE("TaxInfoType.HiTaxRate"),

    /**
     * TaxInfo Additional TaxRate.
     */
    TAXINFO_ADDRATE("TaxInfoType.AdditionalTaxRate"),

    /**
     * TaxInfo Interest TaxRate.
     */
    TAXINFO_INTRATE("TaxInfoType.InterestTaxRate"),

    /**
     * TaxInfo Dividend TaxRate.
     */
    TAXINFO_DIVRATE("TaxInfoType.DividendTaxRate"),

    /**
     * TaxInfo High Dividend TaxRate.
     */
    TAXINFO_HIDIVRATE("TaxInfoType.HiDividendTaxRate"),

    /**
     * TaxInfo Additional Dividend TaxRate.
     */
    TAXINFO_ADDDIVRATE("TaxInfoType.AdditionalDividendTaxRate"),

    /**
     * TaxInfo Capital TaxRate.
     */
    TAXINFO_CAPRATE("TaxInfoType.CapitalTaxRate"),

    /**
     * TaxInfo High Capital TaxRate.
     */
    TAXINFO_HICAPRATE("TaxInfoType.HiCapitalTaxRate"),

    /**
     * TaxInfo Residential TaxRate.
     */
    TAXINFO_RESRATE("TaxInfoType.ResidentialTaxRate"),

    /**
     * TaxInfo High Residential TaxRate.
     */
    TAXINFO_HIRESRATE("TaxInfoType.HiResidentialTaxRate"),

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
     * TransInfo Pension.
     */
    TRANSINFO_PENSION("TransInfoType.Pension"),

    /**
     * TransInfo Pension.
     */
    TRANSINFO_CREDITUNITS("TransInfoType.CreditUnits"),

    /**
     * TransInfo DebitUnits.
     */
    TRANSINFO_DEBITUNITS("TransInfoType.DebitUnits"),

    /**
     * TransInfo CreditDate.
     */
    TRANSINFO_CREDITDATE("TransInfoType.CreditDate"),

    /**
     * TransInfo PartnerAmount.
     */
    TRANSINFO_PARTNERAMOUNT("TransInfoType.PartnerAmount"),

    /**
     * TransInfo ThirdPartyAmount.
     */
    TRANSINFO_THIRDPARTYAMOUNT("TransInfoType.ThirdPartyAmount"),

    /**
     * TransInfo Dilution.
     */
    TRANSINFO_DILUTION("MoneyWiseData.Field.Dilution"),

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
     * TransInfo ThirdParty.
     */
    TRANSINFO_THIRDPARTY("TransInfoType.ThirdParty"),

    /**
     * TransInfo Price.
     */
    TRANSINFO_PRICE("MoneyWiseData.Field.Price"),

    /**
     * TransInfo Commission.
     */
    TRANSINFO_COMMISSION("TransInfoType.Commission"),

    /**
     * TransInfo OptionsGrant.
     */
    TRANSINFO_OPTIONS("TransInfoType.OptionsGrant"),

    /**
     * TransInfo TransactionTag.
     */
    TRANSINFO_TRANSTAG(MoneyWiseDataTypeResource.TRANSTAG_NAME),

    /**
     * TransInfo ExchangeRate.
     */
    TRANSINFO_XCHANGERATE("TransInfoType.XchangeRate");

    /**
     * The DepositType Map.
     */
    private static final Map<DepositCategoryClass, TethysResourceId> DEPOSIT_MAP = buildDepositMap();

    /**
     * The CashType Map.
     */
    private static final Map<CashCategoryClass, TethysResourceId> CASH_MAP = buildCashMap();

    /**
     * The LoanType Map.
     */
    private static final Map<LoanCategoryClass, TethysResourceId> LOAN_MAP = buildLoanMap();

    /**
     * The SecurityType Map.
     */
    private static final Map<SecurityTypeClass, TethysResourceId> SECURITY_MAP = buildSecurityMap();

    /**
     * The PayeeType Map.
     */
    private static final Map<PayeeTypeClass, TethysResourceId> PAYEE_MAP = buildPayeeMap();

    /**
     * The TransactionType Map.
     */
    private static final Map<TransactionCategoryClass, TethysResourceId> TRANSACTION_MAP = buildTransactionMap();

    /**
     * The TaxBasis Map.
     */
    private static final Map<TaxBasisClass, TethysResourceId> TAXBASIS_MAP = buildTaxBasisMap();

    /**
     * The TaxRegime Map.
     */
    private static final Map<TaxRegimeClass, TethysResourceId> TAXREGIME_MAP = buildTaxRegimeMap();

    /**
     * The Frequency Map.
     */
    private static final Map<FrequencyClass, TethysResourceId> FREQUENCY_MAP = buildFrequencyMap();

    /**
     * The TaxInfo Map.
     */
    private static final Map<TaxYearInfoClass, TethysResourceId> TAXINFO_MAP = buildTaxInfoMap();

    /**
     * The AccountInfo Map.
     */
    private static final Map<AccountInfoClass, TethysResourceId> ACCOUNTINFO_MAP = buildAccountInfoMap();

    /**
     * The TransInfo Map.
     */
    private static final Map<TransactionInfoClass, TethysResourceId> TRANSINFO_MAP = buildTransInfoMap();

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getResourceBuilder(MoneyWiseDataType.class.getCanonicalName());

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
    StaticDataResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    StaticDataResource(final TethysResourceId pResource) {
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
     * Build deposit type map.
     * @return the map
     */
    private static Map<DepositCategoryClass, TethysResourceId> buildDepositMap() {
        /* Create the map and return it */
        Map<DepositCategoryClass, TethysResourceId> myMap = new EnumMap<>(DepositCategoryClass.class);
        myMap.put(DepositCategoryClass.CHECKING, DEPOSITTYPE_CHECKING);
        myMap.put(DepositCategoryClass.SAVINGS, DEPOSITTYPE_SAVINGS);
        myMap.put(DepositCategoryClass.PEER2PEER, DEPOSITTYPE_PEER2PEER);
        myMap.put(DepositCategoryClass.BOND, DEPOSITTYPE_BOND);
        myMap.put(DepositCategoryClass.PARENT, CATEGORYTYPE_PARENT);
        return myMap;
    }

    /**
     * Obtain key for deposit type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForDepositType(final DepositCategoryClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(DEPOSIT_MAP, pValue);
    }

    /**
     * Build cash type map.
     * @return the map
     */
    private static Map<CashCategoryClass, TethysResourceId> buildCashMap() {
        /* Create the map and return it */
        Map<CashCategoryClass, TethysResourceId> myMap = new EnumMap<>(CashCategoryClass.class);
        myMap.put(CashCategoryClass.CASH, CASHTYPE_CASH);
        myMap.put(CashCategoryClass.AUTOEXPENSE, CASHTYPE_AUTO);
        myMap.put(CashCategoryClass.PARENT, CATEGORYTYPE_PARENT);
        return myMap;
    }

    /**
     * Obtain key for cash type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForCashType(final CashCategoryClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(CASH_MAP, pValue);
    }

    /**
     * Build loan type map.
     * @return the map
     */
    private static Map<LoanCategoryClass, TethysResourceId> buildLoanMap() {
        /* Create the map and return it */
        Map<LoanCategoryClass, TethysResourceId> myMap = new EnumMap<>(LoanCategoryClass.class);
        myMap.put(LoanCategoryClass.CREDITCARD, LOANTYPE_CREDIT);
        myMap.put(LoanCategoryClass.PRIVATELOAN, LOANTYPE_PRIVATE);
        myMap.put(LoanCategoryClass.LOAN, LOANTYPE_LOAN);
        myMap.put(LoanCategoryClass.PARENT, CATEGORYTYPE_PARENT);
        return myMap;
    }

    /**
     * Obtain key for loan type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForLoanType(final LoanCategoryClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(LOAN_MAP, pValue);
    }

    /**
     * Build security type map.
     * @return the map
     */
    private static Map<SecurityTypeClass, TethysResourceId> buildSecurityMap() {
        /* Create the map and return it */
        Map<SecurityTypeClass, TethysResourceId> myMap = new EnumMap<>(SecurityTypeClass.class);
        myMap.put(SecurityTypeClass.SHARES, SECURITYTYPE_SHARES);
        myMap.put(SecurityTypeClass.UNITTRUST, SECURITYTYPE_UNIT);
        myMap.put(SecurityTypeClass.LIFEBOND, SECURITYTYPE_LIFEBOND);
        myMap.put(SecurityTypeClass.ENDOWMENT, SECURITYTYPE_ENDOWMENT);
        myMap.put(SecurityTypeClass.PROPERTY, SECURITYTYPE_PROPERTY);
        myMap.put(SecurityTypeClass.VEHICLE, SECURITYTYPE_VEHICLE);
        myMap.put(SecurityTypeClass.ASSET, SECURITYTYPE_ASSET);
        return myMap;
    }

    /**
     * Obtain key for security type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForSecurityType(final SecurityTypeClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(SECURITY_MAP, pValue);
    }

    /**
     * Build payee type map.
     * @return the map
     */
    private static Map<PayeeTypeClass, TethysResourceId> buildPayeeMap() {
        /* Create the map and return it */
        Map<PayeeTypeClass, TethysResourceId> myMap = new EnumMap<>(PayeeTypeClass.class);
        myMap.put(PayeeTypeClass.TAXMAN, PAYEETYPE_TAXMAN);
        myMap.put(PayeeTypeClass.GOVERNMENT, PAYEETYPE_GOVERNMENT);
        myMap.put(PayeeTypeClass.MARKET, PAYEETYPE_MARKET);
        myMap.put(PayeeTypeClass.EMPLOYER, PAYEETYPE_EMPLOYER);
        myMap.put(PayeeTypeClass.INSTITUTION, PAYEETYPE_INSTITUTION);
        myMap.put(PayeeTypeClass.INDIVIDUAL, PAYEETYPE_INDIVIDUAL);
        myMap.put(PayeeTypeClass.PAYEE, PAYEETYPE_PAYEE);
        return myMap;
    }

    /**
     * Obtain key for payee type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForPayeeType(final PayeeTypeClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(PAYEE_MAP, pValue);
    }

    /**
     * Build transaction type map.
     * @return the map
     */
    private static Map<TransactionCategoryClass, TethysResourceId> buildTransactionMap() {
        /* Create the map and return it */
        Map<TransactionCategoryClass, TethysResourceId> myMap = new EnumMap<>(TransactionCategoryClass.class);
        myMap.put(TransactionCategoryClass.TAXEDINCOME, TRANSTYPE_TAXEDINCOME);
        myMap.put(TransactionCategoryClass.RENTALINCOME, TRANSTYPE_RENTALINCOME);
        myMap.put(TransactionCategoryClass.ROOMRENTALINCOME, TRANSTYPE_ROOMRENTINCOME);
        myMap.put(TransactionCategoryClass.INTEREST, TRANSTYPE_INTEREST);
        myMap.put(TransactionCategoryClass.DIVIDEND, TRANSTYPE_DIVIDEND);
        myMap.put(TransactionCategoryClass.GRANTINCOME, TRANSTYPE_GRANTINCOME);
        myMap.put(TransactionCategoryClass.BENEFITINCOME, TRANSTYPE_BENEFITINCOME);
        myMap.put(TransactionCategoryClass.GIFTEDINCOME, TRANSTYPE_GIFTEDINCOME);
        myMap.put(TransactionCategoryClass.INHERITED, TRANSTYPE_INHERITED);
        myMap.put(TransactionCategoryClass.LOANINTERESTEARNED, TRANSTYPE_LOANINTEARNED);
        myMap.put(TransactionCategoryClass.CASHBACK, TRANSTYPE_CASHBACK);
        myMap.put(TransactionCategoryClass.LOYALTYBONUS, TRANSTYPE_LOYALTYBONUS);
        myMap.put(TransactionCategoryClass.OTHERINCOME, TRANSTYPE_OTHERINCOME);
        myMap.put(TransactionCategoryClass.TRANSFER, TRANSTYPE_TRANSFER);
        myMap.put(TransactionCategoryClass.UNITSADJUST, TRANSTYPE_UNITSADJUST);
        myMap.put(TransactionCategoryClass.STOCKSPLIT, TRANSTYPE_STOCKSPLIT);
        myMap.put(TransactionCategoryClass.STOCKDEMERGER, TRANSTYPE_STOCKDEMERGER);
        myMap.put(TransactionCategoryClass.STOCKTAKEOVER, TRANSTYPE_STOCKTAKEOVER);
        myMap.put(TransactionCategoryClass.SECURITYREPLACE, TRANSTYPE_SECURITYREPLACE);
        myMap.put(TransactionCategoryClass.STOCKRIGHTSISSUE, TRANSTYPE_STOCKRIGHTSISSUE);
        myMap.put(TransactionCategoryClass.PORTFOLIOXFER, TRANSTYPE_PORTFOLIOXFER);
        myMap.put(TransactionCategoryClass.OPTIONSVEST, TRANSTYPE_OPTIONSVEST);
        myMap.put(TransactionCategoryClass.OPTIONSEXERCISE, TRANSTYPE_OPTIONSEXERCISE);
        myMap.put(TransactionCategoryClass.EXPENSE, TRANSTYPE_EXPENSE);
        myMap.put(TransactionCategoryClass.BADDEBT, TRANSTYPE_BADDEBT);
        myMap.put(TransactionCategoryClass.LOCALTAXES, TRANSTYPE_LOCALTAXES);
        myMap.put(TransactionCategoryClass.WRITEOFF, TRANSTYPE_WRITEOFF);
        myMap.put(TransactionCategoryClass.RENTALEXPENSE, TRANSTYPE_RENTALEXPENSE);
        myMap.put(TransactionCategoryClass.LOANINTERESTCHARGED, TRANSTYPE_LOANINTCHARGE);
        myMap.put(TransactionCategoryClass.TAXRELIEF, TRANSTYPE_TAXRELIEF);
        myMap.put(TransactionCategoryClass.TAXSETTLEMENT, TRANSTYPE_TAXSETTLE);
        myMap.put(TransactionCategoryClass.TAXEDINTEREST, TRANSTYPE_TAXEDINTEREST);
        myMap.put(TransactionCategoryClass.TAXFREEINTEREST, TRANSTYPE_TAXFREEINTEREST);
        myMap.put(TransactionCategoryClass.GROSSINTEREST, TRANSTYPE_GROSSINTEREST);
        myMap.put(TransactionCategoryClass.SHAREDIVIDEND, TRANSTYPE_SHAREDIVIDEND);
        myMap.put(TransactionCategoryClass.UNITTRUSTDIVIDEND, TRANSTYPE_UTDIVIDEND);
        myMap.put(TransactionCategoryClass.FOREIGNDIVIDEND, TRANSTYPE_FOREIGNDIVIDEND);
        myMap.put(TransactionCategoryClass.TAXFREEDIVIDEND, TRANSTYPE_TAXFREEDIVIDEND);
        myMap.put(TransactionCategoryClass.TAXEDLOYALTYBONUS, TRANSTYPE_TAXEDLOYALTYBONUS);
        myMap.put(TransactionCategoryClass.GROSSLOYALTYBONUS, TRANSTYPE_GROSSLOYALTYBONUS);
        myMap.put(TransactionCategoryClass.TAXFREELOYALTYBONUS, TRANSTYPE_TAXFREELOYALTYBONUS);
        myMap.put(TransactionCategoryClass.TAXABLEGAIN, TRANSTYPE_TAXABLEGAIN);
        myMap.put(TransactionCategoryClass.TAXFREEGAIN, TRANSTYPE_TAXFREEGAIN);
        myMap.put(TransactionCategoryClass.RESIDENTIALGAIN, TRANSTYPE_RESIDENTIALGAIN);
        myMap.put(TransactionCategoryClass.CAPITALGAIN, TRANSTYPE_CAPITALGAIN);
        myMap.put(TransactionCategoryClass.MARKETGROWTH, TRANSTYPE_MARKETGROWTH);
        myMap.put(TransactionCategoryClass.CURRENCYFLUCTUATION, TRANSTYPE_CURRFLUCT);
        myMap.put(TransactionCategoryClass.TAXCREDIT, TRANSTYPE_TAXCREDIT);
        myMap.put(TransactionCategoryClass.NATINSURANCE, TRANSTYPE_NATINS);
        myMap.put(TransactionCategoryClass.DEEMEDBENEFIT, TRANSTYPE_BENEFIT);
        myMap.put(TransactionCategoryClass.CHARITYDONATION, TRANSTYPE_CHARDONATION);
        myMap.put(TransactionCategoryClass.OPENINGBALANCE, TRANSTYPE_OPENINGBALANCE);
        myMap.put(TransactionCategoryClass.INCOMETOTALS, TRANSTYPE_INCOMETOTALS);
        myMap.put(TransactionCategoryClass.EXPENSETOTALS, TRANSTYPE_EXPENSETOTALS);
        myMap.put(TransactionCategoryClass.SECURITYPARENT, TRANSTYPE_SECURITYPARENT);
        myMap.put(TransactionCategoryClass.TOTALS, TRANSTYPE_TOTALS);
        return myMap;
    }

    /**
     * Obtain key for transaction type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForTransType(final TransactionCategoryClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(TRANSACTION_MAP, pValue);
    }

    /**
     * Build tax basis map.
     * @return the map
     */
    private static Map<TaxBasisClass, TethysResourceId> buildTaxBasisMap() {
        /* Create the map and return it */
        Map<TaxBasisClass, TethysResourceId> myMap = new EnumMap<>(TaxBasisClass.class);
        myMap.put(TaxBasisClass.SALARY, TAXBASIS_SALARY);
        myMap.put(TaxBasisClass.ROOMRENTAL, TAXBASIS_ROOMRENTAL);
        myMap.put(TaxBasisClass.RENTALINCOME, TAXBASIS_RENTALINCOME);
        myMap.put(TaxBasisClass.TAXEDINTEREST, TAXBASIS_TAXEDINTEREST);
        myMap.put(TaxBasisClass.UNTAXEDINTEREST, TAXBASIS_UNTAXEDINTEREST);
        myMap.put(TaxBasisClass.DIVIDEND, TAXBASIS_DIVIDEND);
        myMap.put(TaxBasisClass.UNITTRUSTDIVIDEND, TAXBASIS_UTDIVIDEND);
        myMap.put(TaxBasisClass.FOREIGNDIVIDEND, TAXBASIS_FOREIGNDIVIDEND);
        myMap.put(TaxBasisClass.TAXABLEGAINS, TAXBASIS_TAXABLEGAINS);
        myMap.put(TaxBasisClass.RESIDENTIALGAINS, TAXBASIS_RESIDENTIALGAINS);
        myMap.put(TaxBasisClass.CAPITALGAINS, TAXBASIS_CAPITALGAINS);
        myMap.put(TaxBasisClass.BADDEBT, TAXBASIS_BADDEBT);
        myMap.put(TaxBasisClass.TAXPAID, TAXBASIS_TAXPAID);
        myMap.put(TaxBasisClass.MARKET, TAXBASIS_MARKET);
        myMap.put(TaxBasisClass.TAXFREE, TAXBASIS_TAXFREE);
        myMap.put(TaxBasisClass.EXPENSE, TAXBASIS_EXPENSE);
        myMap.put(TaxBasisClass.VIRTUAL, TAXBASIS_VIRTUAL);
        return myMap;
    }

    /**
     * Obtain key for taxBasis.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForTaxBasis(final TaxBasisClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(TAXBASIS_MAP, pValue);
    }

    /**
     * Obtain key for tax category.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForTaxCategory(final TaxCategoryClass pValue) {
        switch (pValue) {
            case GROSSINCOME:
                return TAXTYPE_BASE_INCOME;
            case ORIGINALALLOWANCE:
                return TAXTYPE_BASE_ALLOWANCE;
            case ADJUSTEDALLOWANCE:
                return TAXTYPE_FINAL_ALLOWANCE;
            case HITAXBAND:
                return TAXTYPE_BAND_HIRATE;
            case SALARYNILRATE:
                return TAXTYPE_SALARY_NILRATE;
            case SALARYLORATE:
                return TAXTYPE_SALARY_LORATE;
            case SALARYBASICRATE:
                return TAXTYPE_SALARY_BASICRATE;
            case SALARYHIRATE:
                return TAXTYPE_SALARY_HIRATE;
            case SALARYADDRATE:
                return TAXTYPE_SALARY_ADDRATE;
            case TAXDUESALARY:
                return TAXTYPE_SALARY_TAXDUE;
            case RENTALNILRATE:
                return TAXTYPE_RENTAL_NILRATE;
            case RENTALLORATE:
                return TAXTYPE_RENTAL_LORATE;
            case RENTALBASICRATE:
                return TAXTYPE_RENTAL_BASICRATE;
            case RENTALHIRATE:
                return TAXTYPE_RENTAL_HIRATE;
            case RENTALADDRATE:
                return TAXTYPE_RENTAL_ADDRATE;
            case TAXDUERENTAL:
                return TAXTYPE_RENTAL_TAXDUE;
            case INTERESTNILRATE:
                return TAXTYPE_INTEREST_NILRATE;
            case INTERESTLORATE:
                return TAXTYPE_INTEREST_LORATE;
            case INTERESTBASICRATE:
                return TAXTYPE_INTEREST_BASICRATE;
            case INTERESTHIRATE:
                return TAXTYPE_INTEREST_HIRATE;
            case INTERESTADDRATE:
                return TAXTYPE_INTEREST_ADDRATE;
            case TAXDUEINTEREST:
                return TAXTYPE_INTEREST_TAXDUE;
            case DIVIDENDBASICRATE:
                return TAXTYPE_DIVIDEND_BASICRATE;
            case DIVIDENDHIRATE:
                return TAXTYPE_DIVIDEND_HIRATE;
            case DIVIDENDADDRATE:
                return TAXTYPE_DIVIDEND_ADDRATE;
            case TAXDUEDIVIDEND:
                return TAXTYPE_DIVIDEND_TAXDUE;
            case SLICEBASICRATE:
                return TAXTYPE_SLICE_BASICRATE;
            case SLICEHIRATE:
                return TAXTYPE_SLICE_HIRATE;
            case SLICEADDRATE:
                return TAXTYPE_SLICE_ADDRATE;
            case TAXDUESLICE:
                return TAXTYPE_SLICE_TAXDUE;
            case GAINSBASICRATE:
                return TAXTYPE_GAINS_BASICRATE;
            case GAINSHIRATE:
                return TAXTYPE_GAINS_HIRATE;
            case GAINSADDRATE:
                return TAXTYPE_GAINS_ADDRATE;
            case TAXDUETAXGAINS:
                return TAXTYPE_GAINS_TAXDUE;
            case CAPITALNILRATE:
                return TAXTYPE_CAPITAL_NILRATE;
            case CAPITALBASICRATE:
                return TAXTYPE_CAPITAL_BASICRATE;
            case CAPITALHIRATE:
                return TAXTYPE_CAPITAL_HIRATE;
            case TAXDUECAPITAL:
                return TAXTYPE_CAPITAL_TAXDUE;
            case TOTALTAXATIONDUE:
                return TAXTYPE_TOTAL_TAXDUE;
            case TAXPROFITLOSS:
                return TAXTYPE_TOTAL_PROFITLOSS;
            default:
                throw new IllegalArgumentException(TethysResourceBuilder.getErrorNoResource(pValue));
        }
    }

    /**
     * Build tax regime map.
     * @return the map
     */
    private static Map<TaxRegimeClass, TethysResourceId> buildTaxRegimeMap() {
        /* Create the map and return it */
        Map<TaxRegimeClass, TethysResourceId> myMap = new EnumMap<>(TaxRegimeClass.class);
        myMap.put(TaxRegimeClass.ARCHIVE, TAXREGIME_ARCHIVE);
        myMap.put(TaxRegimeClass.STANDARD, TAXREGIME_STANDARD);
        myMap.put(TaxRegimeClass.LOINTEREST, TAXREGIME_LOINTEREST);
        myMap.put(TaxRegimeClass.ADDITIONALBAND, TAXREGIME_ADDBAND);
        myMap.put(TaxRegimeClass.SAVINGSALLOWANCE, TAXREGIME_SAVINGSALLOW);
        return myMap;
    }

    /**
     * Obtain key for taxRegime.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForTaxRegime(final TaxRegimeClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(TAXREGIME_MAP, pValue);
    }

    /**
     * Build frequency map.
     * @return the map
     */
    private static Map<FrequencyClass, TethysResourceId> buildFrequencyMap() {
        /* Create the map and return it */
        Map<FrequencyClass, TethysResourceId> myMap = new EnumMap<>(FrequencyClass.class);
        myMap.put(FrequencyClass.ONCE, FREQUENCY_ONCE);
        myMap.put(FrequencyClass.DAILY, FREQUENCY_DAILY);
        myMap.put(FrequencyClass.WEEKLY, FREQUENCY_WEEKLY);
        myMap.put(FrequencyClass.MONTHLY, FREQUENCY_MONTHLY);
        myMap.put(FrequencyClass.ANNUALLY, FREQUENCY_ANNUALLY);
        myMap.put(FrequencyClass.EVERY, FREQUENCY_EVERY);
        myMap.put(FrequencyClass.ALTERNATE, FREQUENCY_ALTERNATE);
        myMap.put(FrequencyClass.EVERYTHIRD, FREQUENCY_EVERYTHIRD);
        myMap.put(FrequencyClass.EVERYFOURTH, FREQUENCY_EVERYFOURTH);
        myMap.put(FrequencyClass.EVERYSIXTH, FREQUENCY_EVERYSIXTH);
        myMap.put(FrequencyClass.FIRSTWEEK, FREQUENCY_FIRSTWEEK);
        myMap.put(FrequencyClass.SECONDWEEK, FREQUENCY_SECONDWEEK);
        myMap.put(FrequencyClass.THIRDWEEK, FREQUENCY_THIRDWEEK);
        myMap.put(FrequencyClass.FOURTHWEEK, FREQUENCY_FOURTHWEEK);
        myMap.put(FrequencyClass.LASTWEEK, FREQUENCY_LASTWEEK);
        return myMap;
    }

    /**
     * Obtain key for frequency.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForFrequency(final FrequencyClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(FREQUENCY_MAP, pValue);
    }

    /**
     * Build taxInfo map.
     * @return the map
     */
    private static Map<TaxYearInfoClass, TethysResourceId> buildTaxInfoMap() {
        /* Create the map and return it */
        Map<TaxYearInfoClass, TethysResourceId> myMap = new EnumMap<>(TaxYearInfoClass.class);
        myMap.put(TaxYearInfoClass.ALLOWANCE, TAXINFO_ALLOWANCE);
        myMap.put(TaxYearInfoClass.LOTAXBAND, TAXINFO_LOBAND);
        myMap.put(TaxYearInfoClass.BASICTAXBAND, TAXINFO_BASICBAND);
        myMap.put(TaxYearInfoClass.RENTALALLOWANCE, TAXINFO_RENTALALLOWANCE);
        myMap.put(TaxYearInfoClass.CAPITALALLOWANCE, TAXINFO_CAPITALALLOWANCE);
        myMap.put(TaxYearInfoClass.SAVINGSALLOWANCE, TAXINFO_SAVINGSALLOWANCE);
        myMap.put(TaxYearInfoClass.HISAVINGSALLOWANCE, TAXINFO_HISAVINGSALLOWANCE);
        myMap.put(TaxYearInfoClass.DIVIDENDALLOWANCE, TAXINFO_DIVIDENDALLOWANCE);
        myMap.put(TaxYearInfoClass.LOAGEALLOWANCE, TAXINFO_LOAGEALLOWANCE);
        myMap.put(TaxYearInfoClass.HIAGEALLOWANCE, TAXINFO_HIAGEALLOWANCE);
        myMap.put(TaxYearInfoClass.AGEALLOWANCELIMIT, TAXINFO_AGEALLOWLIMIT);
        myMap.put(TaxYearInfoClass.ADDITIONALALLOWANCELIMIT, TAXINFO_ADDALLOWLIMIT);
        myMap.put(TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD, TAXINFO_ADDINCTHRES);
        myMap.put(TaxYearInfoClass.LOTAXRATE, TAXINFO_LORATE);
        myMap.put(TaxYearInfoClass.BASICTAXRATE, TAXINFO_BASICRATE);
        myMap.put(TaxYearInfoClass.HITAXRATE, TAXINFO_HIRATE);
        myMap.put(TaxYearInfoClass.ADDITIONALTAXRATE, TAXINFO_ADDRATE);
        myMap.put(TaxYearInfoClass.INTERESTTAXRATE, TAXINFO_INTRATE);
        myMap.put(TaxYearInfoClass.DIVIDENDTAXRATE, TAXINFO_DIVRATE);
        myMap.put(TaxYearInfoClass.HIDIVIDENDTAXRATE, TAXINFO_HIDIVRATE);
        myMap.put(TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE, TAXINFO_ADDDIVRATE);
        myMap.put(TaxYearInfoClass.CAPITALTAXRATE, TAXINFO_CAPRATE);
        myMap.put(TaxYearInfoClass.HICAPITALTAXRATE, TAXINFO_HICAPRATE);
        myMap.put(TaxYearInfoClass.RESIDENTIALTAXRATE, TAXINFO_RESRATE);
        myMap.put(TaxYearInfoClass.HIRESIDENTIALTAXRATE, TAXINFO_HIRESRATE);
        return myMap;
    }

    /**
     * Obtain key for taxInfoType.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForTaxInfo(final TaxYearInfoClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(TAXINFO_MAP, pValue);
    }

    /**
     * Build accountInfo map.
     * @return the map
     */
    private static Map<AccountInfoClass, TethysResourceId> buildAccountInfoMap() {
        /* Create the map and return it */
        Map<AccountInfoClass, TethysResourceId> myMap = new EnumMap<>(AccountInfoClass.class);
        myMap.put(AccountInfoClass.MATURITY, ACCOUNTINFO_MATURITY);
        myMap.put(AccountInfoClass.OPENINGBALANCE, ACCOUNTINFO_OPENING);
        myMap.put(AccountInfoClass.AUTOEXPENSE, ACCOUNTINFO_AUTOEXPENSE);
        myMap.put(AccountInfoClass.AUTOPAYEE, ACCOUNTINFO_AUTOPAYEE);
        myMap.put(AccountInfoClass.WEBSITE, ACCOUNTINFO_WEBSITE);
        myMap.put(AccountInfoClass.CUSTOMERNO, ACCOUNTINFO_CUSTNO);
        myMap.put(AccountInfoClass.USERID, ACCOUNTINFO_USERID);
        myMap.put(AccountInfoClass.PASSWORD, ACCOUNTINFO_PASSWORD);
        myMap.put(AccountInfoClass.SORTCODE, ACCOUNTINFO_SORTCODE);
        myMap.put(AccountInfoClass.ACCOUNT, ACCOUNTINFO_ACCOUNT);
        myMap.put(AccountInfoClass.REFERENCE, ACCOUNTINFO_REFERENCE);
        myMap.put(AccountInfoClass.NOTES, ACCOUNTINFO_NOTES);
        return myMap;
    }

    /**
     * Obtain key for accountInfoType.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForAccountInfo(final AccountInfoClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(ACCOUNTINFO_MAP, pValue);
    }

    /**
     * Build transInfo map.
     * @return the map
     */
    private static Map<TransactionInfoClass, TethysResourceId> buildTransInfoMap() {
        /* Create the map and return it */
        Map<TransactionInfoClass, TethysResourceId> myMap = new EnumMap<>(TransactionInfoClass.class);
        myMap.put(TransactionInfoClass.TAXCREDIT, TRANSTYPE_TAXCREDIT);
        myMap.put(TransactionInfoClass.NATINSURANCE, TRANSTYPE_NATINS);
        myMap.put(TransactionInfoClass.DEEMEDBENEFIT, TRANSTYPE_BENEFIT);
        myMap.put(TransactionInfoClass.CHARITYDONATION, TRANSTYPE_CHARDONATION);
        myMap.put(TransactionInfoClass.PENSION, TRANSINFO_PENSION);
        myMap.put(TransactionInfoClass.CREDITUNITS, TRANSINFO_CREDITUNITS);
        myMap.put(TransactionInfoClass.DEBITUNITS, TRANSINFO_DEBITUNITS);
        myMap.put(TransactionInfoClass.PARTNERAMOUNT, TRANSINFO_PARTNERAMOUNT);
        myMap.put(TransactionInfoClass.THIRDPARTYAMOUNT, TRANSINFO_THIRDPARTYAMOUNT);
        myMap.put(TransactionInfoClass.CREDITDATE, TRANSINFO_CREDITDATE);
        myMap.put(TransactionInfoClass.DILUTION, TRANSINFO_DILUTION);
        myMap.put(TransactionInfoClass.QUALIFYYEARS, TRANSINFO_QUALYEARS);
        myMap.put(TransactionInfoClass.REFERENCE, TRANSINFO_REFERENCE);
        myMap.put(TransactionInfoClass.COMMENTS, TRANSINFO_COMMENTS);
        myMap.put(TransactionInfoClass.THIRDPARTY, TRANSINFO_THIRDPARTY);
        myMap.put(TransactionInfoClass.PRICE, TRANSINFO_PRICE);
        myMap.put(TransactionInfoClass.COMMISSION, TRANSINFO_COMMISSION);
        myMap.put(TransactionInfoClass.OPTIONSGRANT, TRANSINFO_OPTIONS);
        myMap.put(TransactionInfoClass.TRANSTAG, TRANSINFO_TRANSTAG);
        myMap.put(TransactionInfoClass.XCHANGERATE, TRANSINFO_XCHANGERATE);
        return myMap;
    }

    /**
     * Obtain key for transInfoType.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForTransInfo(final TransactionInfoClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(TRANSINFO_MAP, pValue);
    }
}
