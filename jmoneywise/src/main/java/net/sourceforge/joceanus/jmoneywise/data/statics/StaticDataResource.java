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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for jMoneyWise DataType Fields.
 */
public enum StaticDataResource implements ResourceId {
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
     * TransType StockAdjust.
     */
    TRANSTYPE_STOCKADJUST("TransType.StockAdjust"),

    /**
     * TransType StockSplit.
     */
    TRANSTYPE_STOCKSPLIT("TransType.StockSplit"),

    /**
     * TransType StockDeMerger.
     */
    TRANSTYPE_STOCKDEMERGER("TransType.StockDeMerger"),

    /**
     * TransType StockTakeOver.
     */
    TRANSTYPE_STOCKTAKEOVER("TransType.StockTakeOver"),

    /**
     * TransType StockRightsTaken.
     */
    TRANSTYPE_STOCKRIGHTTAKE("TransType.StockRightsTaken"),

    /**
     * TransType StockRightsWaived.
     */
    TRANSTYPE_STOCKRIGHTWAIVE("TransType.StockRightsWaived"),

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
     * TransType LocalTaxes.
     */
    TRANSTYPE_LOCALTAXES("TransType.LocalTaxes"),

    /**
     * TransType WriteOff.
     */
    TRANSTYPE_WRITEOFF("TransType.WriteOff"),

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
     * TransType TaxFreeDividend.
     */
    TRANSTYPE_TAXFREEDIVIDEND("TransType.TaxFreeDividend"),

    /**
     * TransType TaxableGain.
     */
    TRANSTYPE_TAXABLEGAIN("TransType.TaxableGain"),

    /**
     * TransType TaxFreeGain.
     */
    TRANSTYPE_TAXFREEGAIN("TransType.TaxFreeGain"),

    /**
     * TransType CapitalGain.
     */
    TRANSTYPE_CAPITALGAIN("TransType.CapitalGain"),

    /**
     * TransType CapitalGain.
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
     * TransType IncomeTotals.
     */
    TRANSTYPE_INCOMETOTALS("TransType.IncomeTotals"),

    /**
     * TransType ExpenseTotals.
     */
    TRANSTYPE_EXPENSETOTALS("TransType.ExpenseTotals"),

    /**
     * TransType StockParent.
     */
    TRANSTYPE_STOCKPARENT("TransType.StockParent"),

    /**
     * TransType Totals.
     */
    TRANSTYPE_TOTALS("TransType.Totals"),

    /**
     * TaxBasis GrossSalary.
     */
    TAXBASIS_GROSSSALARY("TaxBasis.GrossSalary"),

    /**
     * TaxBasis GrossRental.
     */
    TAXBASIS_GROSSRENTAL("TaxBasis.GrossRental"),

    /**
     * TaxBasis GrossInterest.
     */
    TAXBASIS_GROSSINTEREST("TaxBasis.GrossInterest"),

    /**
     * TaxBasis GrossDividend.
     */
    TAXBASIS_GROSSDIVIDEND("TaxBasis.GrossDividend"),

    /**
     * TaxBasis GrossUnitTrustDividend.
     */
    TAXBASIS_GROSSUTDIVIDEND("TaxBasis.GrossUTDividend"),

    /**
     * TaxBasis TaxableGains.
     */
    TAXBASIS_TAXABLEGAINS("TaxBasis.GrossTaxableGains"),

    /**
     * TaxBasis CapitalGains.
     */
    TAXBASIS_CAPITALGAINS("TaxBasis.GrossCapitalGains"),

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
     * TaxRegime Archive.
     */
    TAXREGIME_ADDBAND("TaxRegime.AdditionalBand"),

    /**
     * Frequency Weekly.
     */
    FREQUENCY_WEEKLY("Frequency.Weekly"),

    /**
     * Frequency Fortnightly.
     */
    FREQUENCY_FORTNIGHTLY("Frequency.Fortnightly"),

    /**
     * Frequency Monthly.
     */
    FREQUENCY_MONTHLY("Frequency.Monthly"),

    /**
     * Frequency EndOfMonth.
     */
    FREQUENCY_ENDOFMONTH("Frequency.EndOfMonth"),

    /**
     * Frequency Quarterly.
     */
    FREQUENCY_QUARTERLY("Frequency.Quarterly"),

    /**
     * Frequency HalfYearly.
     */
    FREQUENCY_HALFYEARLY("Frequency.HalfYearly"),

    /**
     * Frequency Annually.
     */
    FREQUENCY_ANNUALLY("Frequency.Annually"),

    /**
     * Frequency Maturity.
     */
    FREQUENCY_MATURITY("Frequency.Maturity"),

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
     * TransInfo CreditAmount.
     */
    TRANSINFO_CREDITAMOUNT("TransInfoType.CreditAmount"),

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
     * TransInfo Portfolio.
     */
    TRANSINFO_PORTFOLIO(MoneyWiseDataTypeResource.PORTFOLIO_NAME);

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
    private StaticDataResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    private StaticDataResource(final ResourceId pResource) {
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
     * Obtain key for deposit type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForDepositType(final DepositCategoryClass pValue) {
        switch (pValue) {
            case CHECKING:
                return DEPOSITTYPE_CHECKING;
            case SAVINGS:
                return DEPOSITTYPE_SAVINGS;
            case BOND:
                return DEPOSITTYPE_BOND;
            case PARENT:
                return CATEGORYTYPE_PARENT;
            default:
                return null;
        }
    }

    /**
     * Obtain key for cash type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForCashType(final CashCategoryClass pValue) {
        switch (pValue) {
            case CASH:
                return CASHTYPE_CASH;
            case AUTOEXPENSE:
                return CASHTYPE_AUTO;
            case PARENT:
                return CATEGORYTYPE_PARENT;
            default:
                return null;
        }
    }

    /**
     * Obtain key for loan type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForLoanType(final LoanCategoryClass pValue) {
        switch (pValue) {
            case CREDITCARD:
                return LOANTYPE_CREDIT;
            case PRIVATELOAN:
                return LOANTYPE_PRIVATE;
            case LOAN:
                return LOANTYPE_LOAN;
            case PARENT:
                return CATEGORYTYPE_PARENT;
            default:
                return null;
        }
    }

    /**
     * Obtain key for security type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForSecurityType(final SecurityTypeClass pValue) {
        switch (pValue) {
            case SHARES:
                return SECURITYTYPE_SHARES;
            case UNITTRUST:
                return SECURITYTYPE_UNIT;
            case LIFEBOND:
                return SECURITYTYPE_LIFEBOND;
            case ENDOWMENT:
                return SECURITYTYPE_ENDOWMENT;
            case PROPERTY:
                return SECURITYTYPE_PROPERTY;
            case VEHICLE:
                return SECURITYTYPE_VEHICLE;
            case ASSET:
                return SECURITYTYPE_ASSET;
            default:
                return null;
        }
    }

    /**
     * Obtain key for payee type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForPayeeType(final PayeeTypeClass pValue) {
        switch (pValue) {
            case TAXMAN:
                return PAYEETYPE_TAXMAN;
            case GOVERNMENT:
                return PAYEETYPE_GOVERNMENT;
            case MARKET:
                return PAYEETYPE_MARKET;
            case EMPLOYER:
                return PAYEETYPE_EMPLOYER;
            case INSTITUTION:
                return PAYEETYPE_INSTITUTION;
            case INDIVIDUAL:
                return PAYEETYPE_INDIVIDUAL;
            case PAYEE:
                return PAYEETYPE_PAYEE;
            default:
                return null;
        }
    }

    /**
     * Obtain key for transaction type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForTransType(final TransactionCategoryClass pValue) {
        switch (pValue) {
            case TAXEDINCOME:
                return TRANSTYPE_TAXEDINCOME;
            case RENTALINCOME:
                return TRANSTYPE_RENTALINCOME;
            case ROOMRENTALINCOME:
                return TRANSTYPE_ROOMRENTINCOME;
            case INTEREST:
                return TRANSTYPE_INTEREST;
            case DIVIDEND:
                return TRANSTYPE_DIVIDEND;
            case GRANTINCOME:
                return TRANSTYPE_GRANTINCOME;
            case BENEFITINCOME:
                return TRANSTYPE_BENEFITINCOME;
            case GIFTEDINCOME:
                return TRANSTYPE_GIFTEDINCOME;
            case INHERITED:
                return TRANSTYPE_INHERITED;
            case LOANINTERESTEARNED:
                return TRANSTYPE_LOANINTEARNED;
            case CASHBACK:
                return TRANSTYPE_CASHBACK;
            case LOYALTYBONUS:
                return TRANSTYPE_LOYALTYBONUS;
            case OTHERINCOME:
                return TRANSTYPE_OTHERINCOME;
            case TRANSFER:
                return TRANSTYPE_TRANSFER;
            case STOCKADJUST:
                return TRANSTYPE_STOCKADJUST;
            case STOCKSPLIT:
                return TRANSTYPE_STOCKSPLIT;
            case STOCKDEMERGER:
                return TRANSTYPE_STOCKDEMERGER;
            case STOCKTAKEOVER:
                return TRANSTYPE_STOCKTAKEOVER;
            case STOCKRIGHTSTAKEN:
                return TRANSTYPE_STOCKRIGHTTAKE;
            case STOCKRIGHTSWAIVED:
                return TRANSTYPE_STOCKRIGHTWAIVE;
            case PORTFOLIOXFER:
                return TRANSTYPE_PORTFOLIOXFER;
            case OPTIONSVEST:
                return TRANSTYPE_OPTIONSVEST;
            case OPTIONSEXERCISE:
                return TRANSTYPE_OPTIONSEXERCISE;
            case EXPENSE:
                return TRANSTYPE_EXPENSE;
            case LOCALTAXES:
                return TRANSTYPE_LOCALTAXES;
            case WRITEOFF:
                return TRANSTYPE_WRITEOFF;
            case LOANINTERESTCHARGED:
                return TRANSTYPE_LOANINTCHARGE;
            case TAXRELIEF:
                return TRANSTYPE_TAXRELIEF;
            case TAXSETTLEMENT:
                return TRANSTYPE_TAXSETTLE;
            case TAXEDINTEREST:
                return TRANSTYPE_TAXEDINTEREST;
            case TAXFREEINTEREST:
                return TRANSTYPE_TAXFREEINTEREST;
            case GROSSINTEREST:
                return TRANSTYPE_GROSSINTEREST;
            case SHAREDIVIDEND:
                return TRANSTYPE_SHAREDIVIDEND;
            case UNITTRUSTDIVIDEND:
                return TRANSTYPE_UTDIVIDEND;
            case TAXFREEDIVIDEND:
                return TRANSTYPE_TAXFREEDIVIDEND;
            case TAXABLEGAIN:
                return TRANSTYPE_TAXABLEGAIN;
            case TAXFREEGAIN:
                return TRANSTYPE_TAXFREEGAIN;
            case CAPITALGAIN:
                return TRANSTYPE_CAPITALGAIN;
            case MARKETGROWTH:
                return TRANSTYPE_MARKETGROWTH;
            case CURRENCYFLUCTUATION:
                return TRANSTYPE_CURRFLUCT;
            case TAXCREDIT:
                return TRANSTYPE_TAXCREDIT;
            case NATINSURANCE:
                return TRANSTYPE_NATINS;
            case DEEMEDBENEFIT:
                return TRANSTYPE_BENEFIT;
            case CHARITYDONATION:
                return TRANSTYPE_CHARDONATION;
            case INCOMETOTALS:
                return TRANSTYPE_INCOMETOTALS;
            case EXPENSETOTALS:
                return TRANSTYPE_EXPENSETOTALS;
            case STOCKPARENT:
                return TRANSTYPE_STOCKPARENT;
            case TOTALS:
                return TRANSTYPE_TOTALS;
            default:
                return null;
        }
    }

    /**
     * Obtain key for tax basis.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForTaxBasis(final TaxBasisClass pValue) {
        switch (pValue) {
            case GROSSSALARY:
                return TAXBASIS_GROSSSALARY;
            case GROSSRENTAL:
                return TAXBASIS_GROSSRENTAL;
            case GROSSINTEREST:
                return TAXBASIS_GROSSINTEREST;
            case GROSSDIVIDEND:
                return TAXBASIS_GROSSDIVIDEND;
            case GROSSUTDIVIDEND:
                return TAXBASIS_GROSSUTDIVIDEND;
            case GROSSTAXABLEGAINS:
                return TAXBASIS_TAXABLEGAINS;
            case GROSSCAPITALGAINS:
                return TAXBASIS_CAPITALGAINS;
            case TAXPAID:
                return TAXBASIS_TAXPAID;
            case MARKET:
                return TAXBASIS_MARKET;
            case TAXFREE:
                return TAXBASIS_TAXFREE;
            case EXPENSE:
                return TAXBASIS_EXPENSE;
            case VIRTUAL:
                return TAXBASIS_VIRTUAL;
            default:
                return null;
        }
    }

    /**
     * Obtain key for tax category.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForTaxCategory(final TaxCategoryClass pValue) {
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
                return null;
        }
    }

    /**
     * Obtain key for tax regime.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForTaxRegime(final TaxRegimeClass pValue) {
        switch (pValue) {
            case ARCHIVE:
                return TAXREGIME_ARCHIVE;
            case STANDARD:
                return TAXREGIME_STANDARD;
            case LOINTEREST:
                return TAXREGIME_LOINTEREST;
            case ADDITIONALBAND:
                return TAXREGIME_ADDBAND;
            default:
                return null;
        }
    }

    /**
     * Obtain key for frequency.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForFrequency(final FrequencyClass pValue) {
        switch (pValue) {
            case WEEKLY:
                return FREQUENCY_WEEKLY;
            case FORTNIGHTLY:
                return FREQUENCY_FORTNIGHTLY;
            case MONTHLY:
                return FREQUENCY_MONTHLY;
            case ENDOFMONTH:
                return FREQUENCY_ENDOFMONTH;
            case QUARTERLY:
                return FREQUENCY_QUARTERLY;
            case HALFYEARLY:
                return FREQUENCY_HALFYEARLY;
            case ANNUALLY:
                return FREQUENCY_ANNUALLY;
            case MATURITY:
                return FREQUENCY_MATURITY;
            default:
                return null;
        }
    }

    /**
     * Obtain key for transInfoType.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForTaxInfo(final TaxYearInfoClass pValue) {
        switch (pValue) {
            case ALLOWANCE:
                return TAXINFO_ALLOWANCE;
            case LOTAXBAND:
                return TAXINFO_LOBAND;
            case BASICTAXBAND:
                return TAXINFO_BASICBAND;
            case RENTALALLOWANCE:
                return TAXINFO_RENTALALLOWANCE;
            case CAPITALALLOWANCE:
                return TAXINFO_CAPITALALLOWANCE;
            case LOAGEALLOWANCE:
                return TAXINFO_LOAGEALLOWANCE;
            case HIAGEALLOWANCE:
                return TAXINFO_HIAGEALLOWANCE;
            case AGEALLOWANCELIMIT:
                return TAXINFO_AGEALLOWLIMIT;
            case ADDITIONALALLOWANCELIMIT:
                return TAXINFO_ADDALLOWLIMIT;
            case ADDITIONALINCOMETHRESHOLD:
                return TAXINFO_ADDINCTHRES;
            case LOTAXRATE:
                return TAXINFO_LORATE;
            case BASICTAXRATE:
                return TAXINFO_BASICRATE;
            case HITAXRATE:
                return TAXINFO_HIRATE;
            case ADDITIONALTAXRATE:
                return TAXINFO_ADDRATE;
            case INTERESTTAXRATE:
                return TAXINFO_INTRATE;
            case DIVIDENDTAXRATE:
                return TAXINFO_DIVRATE;
            case HIDIVIDENDTAXRATE:
                return TAXINFO_HIDIVRATE;
            case ADDITIONALDIVIDENDTAXRATE:
                return TAXINFO_ADDDIVRATE;
            case CAPITALTAXRATE:
                return TAXINFO_CAPRATE;
            case HICAPITALTAXRATE:
                return TAXINFO_HICAPRATE;
            default:
                return null;
        }
    }

    /**
     * Obtain key for accountInfoType.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForAccountInfo(final AccountInfoClass pValue) {
        switch (pValue) {
            case MATURITY:
                return ACCOUNTINFO_MATURITY;
            case OPENINGBALANCE:
                return ACCOUNTINFO_OPENING;
            case AUTOEXPENSE:
                return ACCOUNTINFO_AUTOEXPENSE;
            case AUTOPAYEE:
                return ACCOUNTINFO_AUTOPAYEE;
            case WEBSITE:
                return ACCOUNTINFO_WEBSITE;
            case CUSTOMERNO:
                return ACCOUNTINFO_CUSTNO;
            case USERID:
                return ACCOUNTINFO_USERID;
            case PASSWORD:
                return ACCOUNTINFO_PASSWORD;
            case SORTCODE:
                return ACCOUNTINFO_SORTCODE;
            case ACCOUNT:
                return ACCOUNTINFO_ACCOUNT;
            case REFERENCE:
                return ACCOUNTINFO_REFERENCE;
            case NOTES:
                return ACCOUNTINFO_NOTES;
            default:
                return null;
        }
    }

    /**
     * Obtain key for transInfoType.
     * @param pValue the Value
     * @return the resource key
     */
    protected static StaticDataResource getKeyForTransInfo(final TransactionInfoClass pValue) {
        switch (pValue) {
            case TAXCREDIT:
                return TRANSTYPE_TAXCREDIT;
            case NATINSURANCE:
                return TRANSTYPE_NATINS;
            case DEEMEDBENEFIT:
                return TRANSTYPE_BENEFIT;
            case CHARITYDONATION:
                return TRANSTYPE_CHARDONATION;
            case PENSION:
                return TRANSINFO_PENSION;
            case CREDITUNITS:
                return TRANSINFO_CREDITUNITS;
            case DEBITUNITS:
                return TRANSINFO_DEBITUNITS;
            case CREDITAMOUNT:
                return TRANSINFO_CREDITAMOUNT;
            case CREDITDATE:
                return TRANSINFO_CREDITDATE;
            case DILUTION:
                return TRANSINFO_DILUTION;
            case QUALIFYYEARS:
                return TRANSINFO_QUALYEARS;
            case REFERENCE:
                return TRANSINFO_REFERENCE;
            case COMMENTS:
                return TRANSINFO_COMMENTS;
            case THIRDPARTY:
                return TRANSINFO_THIRDPARTY;
            case PRICE:
                return TRANSINFO_PRICE;
            case COMMISSION:
                return TRANSINFO_COMMISSION;
            case OPTIONSGRANT:
                return TRANSINFO_OPTIONS;
            case TRANSTAG:
                return TRANSINFO_TRANSTAG;
            case PORTFOLIO:
                return TRANSINFO_PORTFOLIO;
            default:
                return null;
        }
    }
}
