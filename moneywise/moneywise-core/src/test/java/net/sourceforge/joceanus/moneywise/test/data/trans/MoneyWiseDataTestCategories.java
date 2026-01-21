/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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

package net.sourceforge.joceanus.moneywise.test.data.trans;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseCashCategoryBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseDepositCategoryBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseLoanCategoryBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseRegionBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseStaticBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseTagBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseTransCategoryBuilder;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;

public class MoneyWiseDataTestCategories {
    /**
     * Deposit ids.
     */
    static final String IDDC_MAIN = "Banking";
    static final String IDDC_CURRENT = IDDC_MAIN + ":Current";
    static final String IDDC_SAVINGS = IDDC_MAIN + ":Savings";
    static final String IDDC_TAX_FREE_SAVINGS = IDDC_MAIN + ":TaxFreeSavings";
    static final String IDDC_PEER_2_PEER = IDDC_MAIN + ":Peer2Peer";
    static final String IDDC_BONDS = IDDC_MAIN + ":Bonds";
    static final String IDDC_TAX_FREE_BONDS = IDDC_MAIN + ":TaxFreeBonds";

    /**
     * Cash ids.
     */
    static final String IDCC_MAIN = "Cash";
    static final String IDCC_CASH = IDCC_MAIN + ":Cash";
    static final String IDCC_WALLET = IDCC_MAIN + ":Wallet";

    /**
     * Loan ids.
     */
    static final String IDLC_MAIN = "Loans";
    static final String IDLC_PRIVATE = IDLC_MAIN + ":Private";
    static final String IDLC_COMMERCIAL = IDLC_MAIN + ":Commercial";
    static final String IDLC_MORTGAGE = IDLC_MAIN + ":Mortgage";
    static final String IDLC_CREDIT_CARDS = IDLC_MAIN + ":CreditCards";
    static final String IDLC_PENDING = IDLC_MAIN + ":Pending";

    /**
     * Transaction ids.
     */
    static final String IDTC_TOTALS = "Totals";
    static final String IDTC_TRANSFER = "Transfer";
    static final String IDTC_INCOME = "Income";
    static final String IDTC_INTEREST = IDTC_INCOME + ":Interest";
    static final String IDTC_DIVIDEND = IDTC_INCOME + ":Dividend";
    static final String IDTC_SALARY = IDTC_INCOME + ":Salary";
    static final String IDTC_BENEFIT = IDTC_INCOME + ":Benefit";
    static final String IDTC_CASH_BACK = IDTC_INCOME + ":CashBack";
    static final String IDTC_EMPLOYEE_NI = IDTC_INCOME + ":EmployeeNatIns";
    static final String IDTC_EMPLOYER_NI = IDTC_INCOME + ":EmployerNatIns";
    static final String IDTC_INHERITANCE = IDTC_INCOME + ":Inheritance";
    static final String IDTC_INC_GIFTS = IDTC_INCOME + ":Gifts";
    static final String IDTC_LOAN_INTEREST = IDTC_INCOME + ":LoanInterest";
    static final String IDTC_OPENING_BAL = IDTC_INCOME + ":OpeningBalance";
    static final String IDTC_TAXED_INTEREST = IDTC_INCOME + ":TaxedInterest";
    static final String IDTC_TAX_FREE_INT = IDTC_INCOME + ":TaxFreeInterest";
    static final String IDTC_TAX_FREE_DIV = IDTC_INCOME + ":TaxFreeDividend";
    static final String IDTC_SHARE_DIV = IDTC_INCOME + ":ShareDividend";
    static final String IDTC_UNIT_TRUST_DIV = IDTC_INCOME + ":UnitTrustDividend";
    static final String IDTC_PEER_2_PEER_INTEREST = IDTC_INCOME + ":Peer2PeerInterest";
    static final String IDTC_INC_EXPENSES = IDTC_INCOME + ":Expenses";
    static final String IDTC_FOREIGN_DIVIDEND = IDTC_INCOME + ":ForeignDividend";
    static final String IDTC_GRANT = IDTC_INCOME + ":Grant";
    static final String IDTC_GROSS_INTEREST = IDTC_INCOME + ":GrossInterest";
    static final String IDTC_GROSS_LOYALTY_BONUS = IDTC_INCOME + ":GrossLoyaltyBonus";
    static final String IDTC_LOYALTY_BONUS = IDTC_INCOME + ":LoyaltyBonus";
    static final String IDTC_TAXED_LOYALTY_BONUS = IDTC_INCOME + ":TaxedLoyaltyBonus";
    static final String IDTC_TAX_FREE_LOYALTY_BONUS = IDTC_INCOME + ":TaxFreeLoyaltyBonus";
    static final String IDTC_OPTIONS_EXERCISE = IDTC_INCOME + ":OptionsExercise";
    static final String IDTC_RENTAL_INCOME = IDTC_INCOME + ":RentalIncome";
    static final String IDTC_ROOM_RENTAL = IDTC_INCOME + ":RoomRental";
    static final String IDTC_PENSION = IDTC_INCOME + ":Pension";
    static final String IDTC_STATE_PENSION = IDTC_INCOME + ":StatePension";
    static final String IDTC_PENSION_CONTRIB = IDTC_INCOME + ":PensionContribution";
    static final String IDTC_SOCIAL_SECURITY = IDTC_INCOME + ":SocialSecurity";
    static final String IDTC_MARKET = "Market";
    static final String IDTC_MKT_GROWTH = IDTC_MARKET + ":Growth";
    static final String IDTC_MKT_CURR_ADJUST = IDTC_MARKET + ":CurrencyAdjust";
    static final String IDTC_MKT_CAP_GAIN = IDTC_MARKET + ":CapitalGain";
    static final String IDTC_MKT_CHG_GAIN = IDTC_MARKET + ":ChargeableGain";
    static final String IDTC_MKT_RES_GAIN = IDTC_MARKET + ":ResidentialGain";
    static final String IDTC_MKT_TAX_FREE_GAIN = IDTC_MARKET + ":TaxFreeGain";
    static final String IDTC_CAR = "Car";
    static final String IDTC_CAR_PETROL = IDTC_CAR + ":Petrol";
    static final String IDTC_CAR_PARKING = IDTC_CAR + ":Parking";
    static final String IDTC_CAR_RENTAL = IDTC_CAR + ":Rental";
    static final String IDTC_CAR_SERVICE = IDTC_CAR + ":Service";
    static final String IDTC_CAR_LEASE = IDTC_CAR + ":Lease";
    static final String IDTC_CAR_ELECTRIC = IDTC_CAR + ":Electric";
    static final String IDTC_CHARGES = "Charges";
    static final String IDTC_CHG_BAD_DEBT_INT = IDTC_CHARGES + ":BadDebtInterest";
    static final String IDTC_CHG_BAD_DEBT_CAP = IDTC_CHARGES + ":BadDebtCapital";
    static final String IDTC_CHG_FEES = IDTC_CHARGES + ":Fees";
    static final String IDTC_CHG_FINES = IDTC_CHARGES + ":Fines";
    static final String IDTC_CHG_INTEREST = IDTC_CHARGES + ":Interest";
    static final String IDTC_EXPENSES = "Expenses";
    static final String IDTC_EXP_BUSINESS = IDTC_EXPENSES + ":Business";
    static final String IDTC_EXP_CASH = IDTC_EXPENSES + ":Cash";
    static final String IDTC_EXP_ESTATE = IDTC_EXPENSES + ":Estate";
    static final String IDTC_EXP_HOTEL = IDTC_EXPENSES + ":Hotel";
    static final String IDTC_EXP_MISC = IDTC_EXPENSES + ":Misc";
    static final String IDTC_EXP_NEWSPAPER = IDTC_EXPENSES + ":Newspaper";
    static final String IDTC_EXP_PETS = IDTC_EXPENSES + ":Pets";
    static final String IDTC_EXP_RENT = IDTC_EXPENSES + ":Rent";
    static final String IDTC_EXP_RENTAL = IDTC_EXPENSES + ":RentalExpense";
    static final String IDTC_EXP_TRAVEL = IDTC_EXPENSES + ":Travel";
    static final String IDTC_EXP_VIRTUAL = IDTC_EXPENSES + ":Virtual";
    static final String IDTC_GIFTS = "Gifts";
    static final String IDTC_GIFT_XMAS = IDTC_GIFTS + ":Christmas";
    static final String IDTC_GIFT_B_DAY = IDTC_GIFTS + ":Birthday";
    static final String IDTC_GIFT_CHARITY = IDTC_GIFTS + ":Charity";
    static final String IDTC_GIFT_OTHER = IDTC_GIFTS + ":Other";
    static final String IDTC_HEALTH = "Health";
    static final String IDTC_HEALTH_DENTAL = IDTC_HEALTH + ":Dental";
    static final String IDTC_HEALTH_HAIRCUT = IDTC_HEALTH + ":Haircut";
    static final String IDTC_HEALTH_MEDICAL = IDTC_HEALTH + ":Medical";
    static final String IDTC_HEALTH_OPTICIANS = IDTC_HEALTH + ":Opticians";
    static final String IDTC_HOLIDAY = "Holiday";
    static final String IDTC_HOL_HOTELS = IDTC_HOLIDAY + ":Hotels";
    static final String IDTC_HOL_TRAVEL = IDTC_HOLIDAY + ":Travel";
    static final String IDTC_HOL_ENTERTAINMENT = IDTC_HOLIDAY + ":Entertainment";
    static final String IDTC_HOUSEHOLD = "Household";
    static final String IDTC_HOUSE_CLEANING = IDTC_HOUSEHOLD + ":Cleaning";
    static final String IDTC_HOUSE_LAUNDRY = IDTC_HOUSEHOLD + ":Laundry";
    static final String IDTC_HOUSE_COMPUTING = IDTC_HOUSEHOLD + ":Computing";
    static final String IDTC_HOUSE_ELECTRIC = IDTC_HOUSEHOLD + ":Electris";
    static final String IDTC_HOUSE_FURNISHINGS = IDTC_HOUSEHOLD + ":Furnishings";
    static final String IDTC_HOUSE_FURNITURE = IDTC_HOUSEHOLD + ":Furniture";
    static final String IDTC_HOUSE_GARDEN = IDTC_HOUSEHOLD + ":Garden";
    static final String IDTC_HOUSE_WHITE_GOODS = IDTC_HOUSEHOLD + ":WhiteGoods";
    static final String IDTC_HOUSE_ESTATE = IDTC_HOUSEHOLD + ":EstateFees";
    static final String IDTC_INSURANCE = "Insurance";
    static final String IDTC_INS_BREAKDOWN = IDTC_INSURANCE + ":Breakdown";
    static final String IDTC_INS_DRIVING = IDTC_INSURANCE + ":Driving";
    static final String IDTC_INS_TRAVEL = IDTC_INSURANCE + ":Travel";
    static final String IDTC_INS_HOUSE = IDTC_INSURANCE + ":House";
    static final String IDTC_LEISURE = "Leisure";
    static final String IDTC_LEIS_ALCOHOL = IDTC_LEISURE + ":Alcohol";
    static final String IDTC_LEIS_BOOKS = IDTC_LEISURE + ":Books";
    static final String IDTC_LEIS_CINEMA = IDTC_LEISURE + ":Cinema";
    static final String IDTC_LEIS_DINING = IDTC_LEISURE + ":Dining";
    static final String IDTC_LEIS_E_BOOKS = IDTC_LEISURE + ":E-Books";
    static final String IDTC_LEIS_EVENTS = IDTC_LEISURE + ":Events";
    static final String IDTC_LEIS_GAMES = IDTC_LEISURE + ":Games";
    static final String IDTC_LEIS_MISC = IDTC_LEISURE + ":Misc";
    static final String IDTC_LEIS_MOVIES = IDTC_LEISURE + ":Movies";
    static final String IDTC_LEIS_MUSIC = IDTC_LEISURE + ":Music";
    static final String IDTC_LEIS_SPORTS = IDTC_LEISURE + ":Sports";
    static final String IDTC_LEIS_THEATRE = IDTC_LEISURE + ":Theatre";
    static final String IDTC_LOAN = "Loan";
    static final String IDTC_LOAN_WRITE_DOWN = IDTC_LOAN + ":WriteDown";
    static final String IDTC_LOAN_INTEREST_CHG = IDTC_LOAN + ":InterestCharged";
    static final String IDTC_MORTGAGE = "Mortgage";
    static final String IDTC_MORTGAGE_DEED_ADMIN = IDTC_MORTGAGE + ":DeedAdmin";
    static final String IDTC_MORTGAGE_INTEREST = IDTC_MORTGAGE + ":Interest";
    static final String IDTC_SECURITY = "Security";
    static final String IDTC_SEC_ADJUST = IDTC_SECURITY + ":AdjustUnits";
    static final String IDTC_SEC_CLOSE = IDTC_SECURITY + ":Closure";
    static final String IDTC_SEC_DE_MERGER = IDTC_SECURITY + ":DeMerger";
    static final String IDTC_SEC_OPT_GRANT = IDTC_SECURITY + ":OptionsGrant";
    static final String IDTC_SEC_OPT_VEST = IDTC_SECURITY + ":OptionsVest";
    static final String IDTC_SEC_OPT_EXPIRE = IDTC_SECURITY + ":OptionsExpire";
    static final String IDTC_SEC_PORT_XFER = IDTC_SECURITY + ":PortfolioXfer";
    static final String IDTC_SEC_REPLACE = IDTC_SECURITY + ":Replace";
    static final String IDTC_SEC_RIGHTS_ISSUE = IDTC_SECURITY + ":RightsIssue";
    static final String IDTC_SEC_STOCK_SPLIT = IDTC_SECURITY + ":StockSplit";
    static final String IDTC_SEC_TAKEOVER = IDTC_SECURITY + ":Takeover";
    static final String IDTC_SHOPPING = "Shopping";
    static final String IDTC_SHOP_ALCOHOL = IDTC_SHOPPING + ":Alcohol";
    static final String IDTC_SHOP_FOOD = IDTC_SHOPPING + ":Food";
    static final String IDTC_SHOP_CLOTHES = IDTC_SHOPPING + ":Clothes";
    static final String IDTC_SHOP_HOUSEHOLD = IDTC_SHOPPING + ":Household";
    static final String IDTC_SHOP_PETS = IDTC_SHOPPING + ":Pets";
    static final String IDTC_TAXES = "Taxes";
    static final String IDTC_TAX_CAR = IDTC_TAXES + ":Car";
    static final String IDTC_TAX_COUNCIL = IDTC_TAXES + ":CouncilTax";
    static final String IDTC_TAX_INCOME = IDTC_TAXES + ":IncomeTax";
    static final String IDTC_TAX_INTEREST = IDTC_TAXES + ":Interest";
    static final String IDTC_TAX_RELIEF = IDTC_TAXES + ":Relief";
    static final String IDTC_TAX_TV_LICENCE = IDTC_TAXES + ":TVLicence";
    static final String IDTC_UTILITIES = "Utilities";
    static final String IDTC_UTIL_GAS = IDTC_UTILITIES + ":Gas";
    static final String IDTC_UTIL_ELECTRIC = IDTC_UTILITIES + ":Electric";
    static final String IDTC_UTIL_WATER = IDTC_UTILITIES + ":Water";
    static final String IDTC_UTIL_INTERNET = IDTC_UTILITIES + ":Internet";
    static final String IDTC_UTIL_PHONE = IDTC_UTILITIES + ":Phone";
    static final String IDTC_UTIL_TV = IDTC_UTILITIES + ":TV";

    /**
     * Region ids.
     */
    static final String IDRG_UK = "UK";
    static final String IDRG_US = "US";
    static final String IDRG_EUROPE = "Europe";
    static final String IDRG_AMERICAS = "Americas";
    static final String IDRG_ASIA = "Asia";
    static final String IDRG_GLOBAL = "Global";
    static final String IDRG_EMERGING = "Emerging";

    /**
     * TransactionTag ids.
     */
    static final String IDTG_IMPORTANT = "Important";
    static final String IDTG_WORK = "Work";
    static final String IDTG_PERSONAL = "Personal";

    /**
     * StaticBuilder.
     */
    private final MoneyWiseStaticBuilder theStaticBuilder;

    /**
     * DepositCAtegoryBuilder.
     */
    private final MoneyWiseDepositCategoryBuilder theDepositBuilder;

    /**
     * CashCategoryBuilder.
     */
    private final MoneyWiseCashCategoryBuilder theCashBuilder;

    /**
     * LoanCategoryBuilder.
     */
    private final MoneyWiseLoanCategoryBuilder theLoanBuilder;

    /**
     * TransCategoryBuilder.
     */
    private final MoneyWiseTransCategoryBuilder theTransBuilder;

    /**
     * RegionBuilder.
     */
    private final MoneyWiseRegionBuilder theRegionBuilder;

    /**
     * TagBuilder.
     */
    private final MoneyWiseTagBuilder theTagBuilder;

    /**
     * Constructor.
     *
     * @param pDataSet the dataSet
     */
    MoneyWiseDataTestCategories(final MoneyWiseDataSet pDataSet) {
        /* Create the builders */
        theStaticBuilder = new MoneyWiseStaticBuilder(pDataSet);
        theDepositBuilder = new MoneyWiseDepositCategoryBuilder(pDataSet);
        theCashBuilder = new MoneyWiseCashCategoryBuilder(pDataSet);
        theLoanBuilder = new MoneyWiseLoanCategoryBuilder(pDataSet);
        theTransBuilder = new MoneyWiseTransCategoryBuilder(pDataSet);
        theRegionBuilder = new MoneyWiseRegionBuilder(pDataSet);
        theTagBuilder = new MoneyWiseTagBuilder(pDataSet);
    }

    /**
     * build basic.
     *
     * @throws OceanusException on error
     */
    public void buildBasic() throws OceanusException {
        /* Build static data */
        buildStatic();

        /* Build account categories */
        buildDeposits();
        buildCash();
        buildLoans();
        buildTrans();

        /* Build regions and tags */
        buildRegions();
        buildTransactionTags();
    }

    /**
     * build Static.
     *
     * @throws OceanusException on error
     */
    private void buildStatic() throws OceanusException {
        theStaticBuilder.buildBasic(MoneyWiseCurrencyClass.GBP);
        theStaticBuilder.buildCurrency(MoneyWiseCurrencyClass.EUR);
        theStaticBuilder.buildCurrency(MoneyWiseCurrencyClass.USD);
    }

    /**
     * build Deposits.
     *
     * @throws OceanusException on error
     */
    private void buildDeposits() throws OceanusException {
        theDepositBuilder.name(IDDC_MAIN).type(MoneyWiseDepositCategoryClass.PARENT).build();
        theDepositBuilder.name(IDDC_CURRENT).type(MoneyWiseDepositCategoryClass.CHECKING).build();
        theDepositBuilder.name(IDDC_SAVINGS).type(MoneyWiseDepositCategoryClass.SAVINGS).build();
        theDepositBuilder.name(IDDC_TAX_FREE_SAVINGS).type(MoneyWiseDepositCategoryClass.TAXFREESAVINGS).build();
        theDepositBuilder.name(IDDC_PEER_2_PEER).type(MoneyWiseDepositCategoryClass.PEER2PEER).build();
        theDepositBuilder.name(IDDC_BONDS).type(MoneyWiseDepositCategoryClass.BOND).build();
        theDepositBuilder.name(IDDC_TAX_FREE_BONDS).type(MoneyWiseDepositCategoryClass.TAXFREEBOND).build();
    }

    /**
     * build Cash.
     *
     * @throws OceanusException on error
     */
    private void buildCash() throws OceanusException {
        theCashBuilder.name(IDCC_MAIN).type(MoneyWiseCashCategoryClass.PARENT).build();
        theCashBuilder.name(IDCC_CASH).type(MoneyWiseCashCategoryClass.AUTOEXPENSE).build();
        theCashBuilder.name(IDCC_WALLET).type(MoneyWiseCashCategoryClass.CASH).build();
    }

    /**
     * build loans.
     *
     * @throws OceanusException on error
     */
    private void buildLoans() throws OceanusException {
        theLoanBuilder.name(IDLC_MAIN).type(MoneyWiseLoanCategoryClass.PARENT).build();
        theLoanBuilder.name(IDLC_PRIVATE).type(MoneyWiseLoanCategoryClass.PRIVATELOAN).build();
        theLoanBuilder.name(IDLC_COMMERCIAL).type(MoneyWiseLoanCategoryClass.LOAN).build();
        theLoanBuilder.name(IDLC_MORTGAGE).type(MoneyWiseLoanCategoryClass.LOAN).build();
        theLoanBuilder.name(IDLC_CREDIT_CARDS).type(MoneyWiseLoanCategoryClass.CREDITCARD).build();
        theLoanBuilder.name(IDLC_PENDING).type(MoneyWiseLoanCategoryClass.LOAN).build();
    }

    /**
     * build Trans.
     *
     * @throws OceanusException on error
     */
    private void buildTrans() throws OceanusException {
        /* Transfer */
        final MoneyWiseTransCategory myTotals = theTransBuilder.name(IDTC_TOTALS).type(MoneyWiseTransCategoryClass.TOTALS).build();

        /* Transfer */
        theTransBuilder.name(IDTC_TRANSFER).type(MoneyWiseTransCategoryClass.TRANSFER).build();

        /* Income */
        theTransBuilder.name(IDTC_INCOME).parent(myTotals).type(MoneyWiseTransCategoryClass.INCOMETOTALS).build();
        theTransBuilder.name(IDTC_INTEREST).type(MoneyWiseTransCategoryClass.INTEREST).build();
        theTransBuilder.name(IDTC_DIVIDEND).type(MoneyWiseTransCategoryClass.DIVIDEND).build();
        theTransBuilder.name(IDTC_SALARY).type(MoneyWiseTransCategoryClass.TAXEDINCOME).build();
        theTransBuilder.name(IDTC_BENEFIT).type(MoneyWiseTransCategoryClass.VIRTUALINCOME).build();
        theTransBuilder.name(IDTC_CASH_BACK).type(MoneyWiseTransCategoryClass.CASHBACK).build();
        theTransBuilder.name(IDTC_PEER_2_PEER_INTEREST).type(MoneyWiseTransCategoryClass.PEER2PEERINTEREST).build();
        theTransBuilder.name(IDTC_EMPLOYEE_NI).type(MoneyWiseTransCategoryClass.EMPLOYEENATINS).build();
        theTransBuilder.name(IDTC_EMPLOYER_NI).type(MoneyWiseTransCategoryClass.EMPLOYERNATINS).build();
        theTransBuilder.name(IDTC_INHERITANCE).type(MoneyWiseTransCategoryClass.INHERITED).build();
        theTransBuilder.name(IDTC_INC_GIFTS).type(MoneyWiseTransCategoryClass.GIFTEDINCOME).build();
        theTransBuilder.name(IDTC_LOAN_INTEREST).type(MoneyWiseTransCategoryClass.LOANINTERESTEARNED).build();
        theTransBuilder.name(IDTC_OPENING_BAL).type(MoneyWiseTransCategoryClass.OPENINGBALANCE).build();
        theTransBuilder.name(IDTC_TAXED_INTEREST).type(MoneyWiseTransCategoryClass.TAXEDINTEREST).build();
        theTransBuilder.name(IDTC_TAX_FREE_INT).type(MoneyWiseTransCategoryClass.TAXFREEINTEREST).build();
        theTransBuilder.name(IDTC_TAX_FREE_DIV).type(MoneyWiseTransCategoryClass.TAXFREEDIVIDEND).build();
        theTransBuilder.name(IDTC_SHARE_DIV).type(MoneyWiseTransCategoryClass.SHAREDIVIDEND).build();
        theTransBuilder.name(IDTC_UNIT_TRUST_DIV).type(MoneyWiseTransCategoryClass.UNITTRUSTDIVIDEND).build();
        theTransBuilder.name(IDTC_INC_EXPENSES).type(MoneyWiseTransCategoryClass.RECOVEREDEXPENSES).build();
        theTransBuilder.name(IDTC_FOREIGN_DIVIDEND).type(MoneyWiseTransCategoryClass.FOREIGNDIVIDEND).build();
        theTransBuilder.name(IDTC_GRANT).type(MoneyWiseTransCategoryClass.TAXEDINCOME).build();
        theTransBuilder.name(IDTC_GROSS_INTEREST).type(MoneyWiseTransCategoryClass.GROSSINTEREST).build();
        theTransBuilder.name(IDTC_GROSS_LOYALTY_BONUS).type(MoneyWiseTransCategoryClass.GROSSLOYALTYBONUS).build();
        theTransBuilder.name(IDTC_LOYALTY_BONUS).type(MoneyWiseTransCategoryClass.LOYALTYBONUS).build();
        theTransBuilder.name(IDTC_TAXED_LOYALTY_BONUS).type(MoneyWiseTransCategoryClass.TAXEDLOYALTYBONUS).build();
        theTransBuilder.name(IDTC_TAX_FREE_LOYALTY_BONUS).type(MoneyWiseTransCategoryClass.TAXFREELOYALTYBONUS).build();
        theTransBuilder.name(IDTC_OPTIONS_EXERCISE).type(MoneyWiseTransCategoryClass.OPTIONSEXERCISE).build();
        theTransBuilder.name(IDTC_RENTAL_INCOME).type(MoneyWiseTransCategoryClass.RENTALINCOME).build();
        theTransBuilder.name(IDTC_ROOM_RENTAL).type(MoneyWiseTransCategoryClass.ROOMRENTALINCOME).build();
        theTransBuilder.name(IDTC_PENSION).type(MoneyWiseTransCategoryClass.TAXEDINCOME).build();
        theTransBuilder.name(IDTC_STATE_PENSION).type(MoneyWiseTransCategoryClass.GROSSINCOME).build();
        theTransBuilder.name(IDTC_PENSION_CONTRIB).type(MoneyWiseTransCategoryClass.PENSIONCONTRIB).build();
        theTransBuilder.name(IDTC_SOCIAL_SECURITY).type(MoneyWiseTransCategoryClass.GROSSINCOME).build();

        /* Market */
        theTransBuilder.name(IDTC_MARKET).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_MKT_GROWTH).type(MoneyWiseTransCategoryClass.MARKETGROWTH).build();
        theTransBuilder.name(IDTC_MKT_CURR_ADJUST).type(MoneyWiseTransCategoryClass.CURRENCYFLUCTUATION).build();
        theTransBuilder.name(IDTC_MKT_CAP_GAIN).type(MoneyWiseTransCategoryClass.CAPITALGAIN).build();
        theTransBuilder.name(IDTC_MKT_CHG_GAIN).type(MoneyWiseTransCategoryClass.CHARGEABLEGAIN).build();
        theTransBuilder.name(IDTC_MKT_RES_GAIN).type(MoneyWiseTransCategoryClass.RESIDENTIALGAIN).build();
        theTransBuilder.name(IDTC_MKT_TAX_FREE_GAIN).type(MoneyWiseTransCategoryClass.TAXFREEGAIN).build();

        /* Car */
        theTransBuilder.name(IDTC_CAR).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_CAR_PETROL).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_CAR_PARKING).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_CAR_RENTAL).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_CAR_SERVICE).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_CAR_LEASE).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_CAR_ELECTRIC).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Charges */
        theTransBuilder.name(IDTC_CHARGES).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_CHG_BAD_DEBT_CAP).type(MoneyWiseTransCategoryClass.BADDEBTCAPITAL).build();
        theTransBuilder.name(IDTC_CHG_BAD_DEBT_INT).type(MoneyWiseTransCategoryClass.BADDEBTINTEREST).build();
        theTransBuilder.name(IDTC_CHG_FEES).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_CHG_FINES).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_CHG_INTEREST).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Expenses */
        theTransBuilder.name(IDTC_EXPENSES).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_EXP_BUSINESS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_EXP_CASH).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_EXP_ESTATE).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_EXP_HOTEL).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_EXP_MISC).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_EXP_NEWSPAPER).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_EXP_PETS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_EXP_RENT).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_EXP_RENTAL).type(MoneyWiseTransCategoryClass.RENTALEXPENSE).build();
        theTransBuilder.name(IDTC_EXP_TRAVEL).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_EXP_VIRTUAL).type(MoneyWiseTransCategoryClass.WITHHELD).build();

        /* Gifts */
        theTransBuilder.name(IDTC_GIFTS).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_GIFT_B_DAY).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_GIFT_XMAS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_GIFT_CHARITY).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_GIFT_OTHER).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Health */
        theTransBuilder.name(IDTC_HEALTH).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_HEALTH_DENTAL).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HEALTH_HAIRCUT).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HEALTH_MEDICAL).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HEALTH_OPTICIANS).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Holiday */
        theTransBuilder.name(IDTC_HOLIDAY).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_HOL_HOTELS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HOL_TRAVEL).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HOL_ENTERTAINMENT).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Household */
        theTransBuilder.name(IDTC_HOUSEHOLD).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_HOUSE_CLEANING).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HOUSE_LAUNDRY).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HOUSE_COMPUTING).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HOUSE_ELECTRIC).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HOUSE_FURNISHINGS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HOUSE_FURNITURE).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HOUSE_GARDEN).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HOUSE_WHITE_GOODS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_HOUSE_ESTATE).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Insurance */
        theTransBuilder.name(IDTC_INSURANCE).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_INS_BREAKDOWN).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_INS_DRIVING).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_INS_TRAVEL).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_INS_HOUSE).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Leisure */
        theTransBuilder.name(IDTC_LEISURE).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_LEIS_ALCOHOL).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_BOOKS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_CINEMA).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_DINING).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_E_BOOKS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_EVENTS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_GAMES).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_MISC).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_MUSIC).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_MOVIES).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_SPORTS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_LEIS_THEATRE).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Loans */
        theTransBuilder.name(IDTC_LOAN).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_LOAN_WRITE_DOWN).type(MoneyWiseTransCategoryClass.WRITEOFF).build();
        theTransBuilder.name(IDTC_LOAN_INTEREST_CHG).type(MoneyWiseTransCategoryClass.LOANINTERESTCHARGED).build();

        /* Mortgage */
        theTransBuilder.name(IDTC_MORTGAGE).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_MORTGAGE_DEED_ADMIN).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_MORTGAGE_INTEREST).type(MoneyWiseTransCategoryClass.LOANINTERESTCHARGED).build();

        /* Security */
        theTransBuilder.name(IDTC_SECURITY).parent(myTotals).type(MoneyWiseTransCategoryClass.SECURITYPARENT).build();
        theTransBuilder.name(IDTC_SEC_ADJUST).type(MoneyWiseTransCategoryClass.UNITSADJUST).build();
        theTransBuilder.name(IDTC_SEC_CLOSE).type(MoneyWiseTransCategoryClass.SECURITYCLOSURE).build();
        theTransBuilder.name(IDTC_SEC_DE_MERGER).type(MoneyWiseTransCategoryClass.STOCKDEMERGER).build();
        theTransBuilder.name(IDTC_SEC_OPT_EXPIRE).type(MoneyWiseTransCategoryClass.OPTIONSEXPIRE).build();
        theTransBuilder.name(IDTC_SEC_OPT_GRANT).type(MoneyWiseTransCategoryClass.OPTIONSGRANT).build();
        theTransBuilder.name(IDTC_SEC_OPT_VEST).type(MoneyWiseTransCategoryClass.OPTIONSVEST).build();
        theTransBuilder.name(IDTC_SEC_REPLACE).type(MoneyWiseTransCategoryClass.SECURITYREPLACE).build();
        theTransBuilder.name(IDTC_SEC_PORT_XFER).type(MoneyWiseTransCategoryClass.PORTFOLIOXFER).build();
        theTransBuilder.name(IDTC_SEC_RIGHTS_ISSUE).type(MoneyWiseTransCategoryClass.STOCKRIGHTSISSUE).build();
        theTransBuilder.name(IDTC_SEC_STOCK_SPLIT).type(MoneyWiseTransCategoryClass.STOCKSPLIT).build();
        theTransBuilder.name(IDTC_SEC_TAKEOVER).type(MoneyWiseTransCategoryClass.STOCKTAKEOVER).build();

        /* Shopping */
        theTransBuilder.name(IDTC_SHOPPING).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_SHOP_ALCOHOL).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_SHOP_FOOD).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_SHOP_CLOTHES).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_SHOP_HOUSEHOLD).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_SHOP_PETS).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Taxes */
        theTransBuilder.name(IDTC_TAXES).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_TAX_CAR).type(MoneyWiseTransCategoryClass.LOCALTAXES).build();
        theTransBuilder.name(IDTC_TAX_COUNCIL).type(MoneyWiseTransCategoryClass.LOCALTAXES).build();
        theTransBuilder.name(IDTC_TAX_INCOME).type(MoneyWiseTransCategoryClass.INCOMETAX).build();
        theTransBuilder.name(IDTC_TAX_INTEREST).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_TAX_RELIEF).type(MoneyWiseTransCategoryClass.TAXRELIEF).build();
        theTransBuilder.name(IDTC_TAX_TV_LICENCE).type(MoneyWiseTransCategoryClass.LOCALTAXES).build();

        /* Utilities */
        theTransBuilder.name(IDTC_UTILITIES).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(IDTC_UTIL_ELECTRIC).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_UTIL_GAS).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_UTIL_INTERNET).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_UTIL_PHONE).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_UTIL_TV).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(IDTC_UTIL_WATER).type(MoneyWiseTransCategoryClass.EXPENSE).build();
    }

    /**
     * build regions.
     *
     * @throws OceanusException on error
     */
    private void buildRegions() throws OceanusException {
        theRegionBuilder.name(IDRG_UK).build();
        theRegionBuilder.name(IDRG_US).build();
        theRegionBuilder.name(IDRG_EUROPE).build();
        theRegionBuilder.name(IDRG_AMERICAS).build();
        theRegionBuilder.name(IDRG_ASIA).build();
        theRegionBuilder.name(IDRG_GLOBAL).build();
        theRegionBuilder.name(IDRG_EMERGING).build();
    }

    /**
     * build tags.
     *
     * @throws OceanusException on error
     */
    private void buildTransactionTags() throws OceanusException {
        theTagBuilder.name(IDTG_IMPORTANT).build();
        theTagBuilder.name(IDTG_WORK).build();
        theTagBuilder.name(IDTG_PERSONAL).build();
    }
}
