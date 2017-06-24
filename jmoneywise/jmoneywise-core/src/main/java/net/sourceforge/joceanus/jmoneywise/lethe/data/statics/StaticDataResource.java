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
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import java.util.EnumMap;
import java.util.Map;

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
     * PortfolioType SIPP.
     */
    PORTFOLIOTYPE_SIPP("PortfolioType.SIPP"),

    /**
     * PortfolioType Pension.
     */
    PORTFOLIOTYPE_PENSION("PortfolioType.Pension"),

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
     * SecurityType PensionPot.
     */
    SECURITYTYPE_PENSIONPOT("SecurityType.PensionPot"),

    /**
     * SecurityType AnnuityPot.
     */
    SECURITYTYPE_ANNUITYPOT("SecurityType.AnnuityPot"),

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
     * TransType Pension Payment.
     */
    TRANSTYPE_PENSIONPAYMENT("TransType.PensionPayment"),

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
     * TransType OptionsExpire.
     */
    TRANSTYPE_OPTIONSEXPIRE("TransType.OptionsExpire"),

    /**
     * TransType OptionsExercise.
     */
    TRANSTYPE_OPTIONSEXERCISE("TransType.OptionsExercise"),

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
     * TransType NatInsurance.
     */
    TRANSTYPE_NATINS("TransType.NatInsurance"),

    /**
     * TransType Withheld.
     */
    TRANSTYPE_WITHHELD("TransType.Withheld"),

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
     * AccountInfo GrantDate.
     */
    ACCOUNTINFO_GRANTDATE("AccountInfoType.GrantDate"),

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
     * TransInfo TransactionTag.
     */
    TRANSINFO_TRANSTAG(MoneyWiseDataTypeResource.TRANSTAG_NAME);

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
     * The PortfolioType Map.
     */
    private static final Map<PortfolioTypeClass, TethysResourceId> PORTFOLIO_MAP = buildPortfolioMap();

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
     * The Frequency Map.
     */
    private static final Map<FrequencyClass, TethysResourceId> FREQUENCY_MAP = buildFrequencyMap();

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
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getResourceBuilder(StaticDataResource.class.getCanonicalName());

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
        return "jMoneyWise.static";
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
        myMap.put(DepositCategoryClass.TAXFREESAVINGS, DEPOSITTYPE_TAXFREESAVINGS);
        myMap.put(DepositCategoryClass.PEER2PEER, DEPOSITTYPE_PEER2PEER);
        myMap.put(DepositCategoryClass.BOND, DEPOSITTYPE_BOND);
        myMap.put(DepositCategoryClass.TAXFREEBOND, DEPOSITTYPE_TAXFREEBOND);
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
     * Build portfolio type map.
     * @return the map
     */
    private static Map<PortfolioTypeClass, TethysResourceId> buildPortfolioMap() {
        /* Create the map and return it */
        Map<PortfolioTypeClass, TethysResourceId> myMap = new EnumMap<>(PortfolioTypeClass.class);
        myMap.put(PortfolioTypeClass.STANDARD, PORTFOLIOTYPE_STANDARD);
        myMap.put(PortfolioTypeClass.TAXFREE, PORTFOLIOTYPE_TAXFREE);
        myMap.put(PortfolioTypeClass.SIPP, PORTFOLIOTYPE_SIPP);
        myMap.put(PortfolioTypeClass.PENSION, PORTFOLIOTYPE_PENSION);
        return myMap;
    }

    /**
     * Obtain key for security type.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForPortfolioType(final PortfolioTypeClass pValue) {
        return TethysResourceBuilder.getKeyForEnum(PORTFOLIO_MAP, pValue);
    }

    /**
     * Build security type map.
     * @return the map
     */
    private static Map<SecurityTypeClass, TethysResourceId> buildSecurityMap() {
        /* Create the map and return it */
        Map<SecurityTypeClass, TethysResourceId> myMap = new EnumMap<>(SecurityTypeClass.class);
        myMap.put(SecurityTypeClass.SHARES, SECURITYTYPE_SHARES);
        myMap.put(SecurityTypeClass.INCOMEUNITTRUST, SECURITYTYPE_INCOMEUNIT);
        myMap.put(SecurityTypeClass.GROWTHUNITTRUST, SECURITYTYPE_GROWTHUNIT);
        myMap.put(SecurityTypeClass.LIFEBOND, SECURITYTYPE_LIFEBOND);
        myMap.put(SecurityTypeClass.ENDOWMENT, SECURITYTYPE_ENDOWMENT);
        myMap.put(SecurityTypeClass.PROPERTY, SECURITYTYPE_PROPERTY);
        myMap.put(SecurityTypeClass.VEHICLE, SECURITYTYPE_VEHICLE);
        myMap.put(SecurityTypeClass.PENSIONPOT, SECURITYTYPE_PENSIONPOT);
        myMap.put(SecurityTypeClass.ANNUITYPOT, SECURITYTYPE_ANNUITYPOT);
        myMap.put(SecurityTypeClass.STOCKOPTION, SECURITYTYPE_STOCKOPTION);
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
        myMap.put(TransactionCategoryClass.BSHAREDIVIDEND, TRANSTYPE_BSHAREDIVIDEND);
        myMap.put(TransactionCategoryClass.VIRTUALINCOME, TRANSTYPE_VIRTUALINCOME);
        myMap.put(TransactionCategoryClass.PENSIONPAYMENT, TRANSTYPE_PENSIONPAYMENT);
        myMap.put(TransactionCategoryClass.GIFTEDINCOME, TRANSTYPE_GIFTEDINCOME);
        myMap.put(TransactionCategoryClass.INHERITED, TRANSTYPE_INHERITED);
        myMap.put(TransactionCategoryClass.LOANINTERESTEARNED, TRANSTYPE_LOANINTEARNED);
        myMap.put(TransactionCategoryClass.CASHBACK, TRANSTYPE_CASHBACK);
        myMap.put(TransactionCategoryClass.LOYALTYBONUS, TRANSTYPE_LOYALTYBONUS);
        myMap.put(TransactionCategoryClass.RECOVEREDEXPENSES, TRANSTYPE_RECOVEREDEXPENSES);
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
        myMap.put(TransactionCategoryClass.OPTIONSEXPIRE, TRANSTYPE_OPTIONSEXPIRE);
        myMap.put(TransactionCategoryClass.OPTIONSEXERCISE, TRANSTYPE_OPTIONSEXERCISE);
        myMap.put(TransactionCategoryClass.EXPENSE, TRANSTYPE_EXPENSE);
        myMap.put(TransactionCategoryClass.BADDEBTCAPITAL, TRANSTYPE_BADDEBTCAPITAL);
        myMap.put(TransactionCategoryClass.BADDEBTINTEREST, TRANSTYPE_BADDEBTINTEREST);
        myMap.put(TransactionCategoryClass.LOCALTAXES, TRANSTYPE_LOCALTAXES);
        myMap.put(TransactionCategoryClass.WRITEOFF, TRANSTYPE_WRITEOFF);
        myMap.put(TransactionCategoryClass.RENTALEXPENSE, TRANSTYPE_RENTALEXPENSE);
        myMap.put(TransactionCategoryClass.LOANINTERESTCHARGED, TRANSTYPE_LOANINTCHARGE);
        myMap.put(TransactionCategoryClass.TAXRELIEF, TRANSTYPE_TAXRELIEF);
        myMap.put(TransactionCategoryClass.INCOMETAX, TRANSTYPE_INCOMETAX);
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
        myMap.put(TransactionCategoryClass.CHARGEABLEGAIN, TRANSTYPE_CHARGEABLEGAIN);
        myMap.put(TransactionCategoryClass.TAXFREEGAIN, TRANSTYPE_TAXFREEGAIN);
        myMap.put(TransactionCategoryClass.RESIDENTIALGAIN, TRANSTYPE_RESIDENTIALGAIN);
        myMap.put(TransactionCategoryClass.CAPITALGAIN, TRANSTYPE_CAPITALGAIN);
        myMap.put(TransactionCategoryClass.MARKETGROWTH, TRANSTYPE_MARKETGROWTH);
        myMap.put(TransactionCategoryClass.CURRENCYFLUCTUATION, TRANSTYPE_CURRFLUCT);
        myMap.put(TransactionCategoryClass.NATINSURANCE, TRANSTYPE_NATINS);
        myMap.put(TransactionCategoryClass.WITHHELD, TRANSTYPE_WITHHELD);
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
        myMap.put(TaxBasisClass.OTHERINCOME, TAXBASIS_OTHERINCOME);
        myMap.put(TaxBasisClass.TAXEDINTEREST, TAXBASIS_TAXEDINTEREST);
        myMap.put(TaxBasisClass.UNTAXEDINTEREST, TAXBASIS_UNTAXEDINTEREST);
        myMap.put(TaxBasisClass.DIVIDEND, TAXBASIS_DIVIDEND);
        myMap.put(TaxBasisClass.UNITTRUSTDIVIDEND, TAXBASIS_UTDIVIDEND);
        myMap.put(TaxBasisClass.FOREIGNDIVIDEND, TAXBASIS_FOREIGNDIVIDEND);
        myMap.put(TaxBasisClass.CHARGEABLEGAINS, TAXBASIS_CHARGEABLEGAINS);
        myMap.put(TaxBasisClass.RESIDENTIALGAINS, TAXBASIS_RESIDENTIALGAINS);
        myMap.put(TaxBasisClass.CAPITALGAINS, TAXBASIS_CAPITALGAINS);
        myMap.put(TaxBasisClass.BADDEBTCAPITAL, TRANSTYPE_BADDEBTCAPITAL);
        myMap.put(TaxBasisClass.BADDEBTINTEREST, TRANSTYPE_BADDEBTINTEREST);
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
        myMap.put(AccountInfoClass.REGION, ACCOUNTINFO_REGION);
        myMap.put(AccountInfoClass.SYMBOL, ACCOUNTINFO_SYMBOL);
        myMap.put(AccountInfoClass.GRANTDATE, ACCOUNTINFO_GRANTDATE);
        myMap.put(AccountInfoClass.UNDERLYINGSTOCK, ACCOUNTINFO_UNDERLYINGSTOCK);
        myMap.put(AccountInfoClass.OPTIONPRICE, ACCOUNTINFO_OPTIONPRICE);
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
        myMap.put(TransactionInfoClass.TAXCREDIT, TRANSINFO_TAXCREDIT);
        myMap.put(TransactionInfoClass.NATINSURANCE, TRANSTYPE_NATINS);
        myMap.put(TransactionInfoClass.DEEMEDBENEFIT, TRANSINFO_BENEFIT);
        myMap.put(TransactionInfoClass.WITHHELD, TRANSTYPE_WITHHELD);
        myMap.put(TransactionInfoClass.ACCOUNTDELTAUNITS, TRANSINFO_ACCOUNTDELTAUNITS);
        myMap.put(TransactionInfoClass.PARTNERDELTAUNITS, TRANSINFO_PARTNERDELTAUNITS);
        myMap.put(TransactionInfoClass.PARTNERAMOUNT, TRANSINFO_PARTNERAMOUNT);
        myMap.put(TransactionInfoClass.RETURNEDCASH, TRANSINFO_RETURNEDCASH);
        myMap.put(TransactionInfoClass.DILUTION, TRANSINFO_DILUTION);
        myMap.put(TransactionInfoClass.QUALIFYYEARS, TRANSINFO_QUALYEARS);
        myMap.put(TransactionInfoClass.REFERENCE, TRANSINFO_REFERENCE);
        myMap.put(TransactionInfoClass.COMMENTS, TRANSINFO_COMMENTS);
        myMap.put(TransactionInfoClass.RETURNEDCASHACCOUNT, TRANSINFO_RETURNEDCASHACCOUNT);
        myMap.put(TransactionInfoClass.TRANSTAG, TRANSINFO_TRANSTAG);
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
