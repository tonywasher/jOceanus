/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.test.data;

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
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Categories Builder.
 */
public class MoneyWiseTestCategories {
    /**
     * Deposit ids.
     */
    final static String idDC_Main = "Banking";
    final static String idDC_Current = idDC_Main + ":Current";
    final static String idDC_Savings = idDC_Main + ":Savings";
    final static String idDC_TaxFreeSavings = idDC_Main + ":TaxFreeSavings";
    final static String idDC_Peer2Peer = idDC_Main + ":Peer2Peer";
    final static String idDC_Bonds = idDC_Main + ":Bonds";
    final static String idDC_TaxFreeBonds = idDC_Main + ":TaxFreeBonds";

    /**
     * Cash ids.
     */
    final static String idCC_Main = "Cash";
    final static String idCC_Cash = idCC_Main + ":Cash";
    final static String idCC_Foreign = idCC_Main + ":Foreign";

    /**
     * Loan ids.
     */
    final static String idLC_Main = "Loans";
    final static String idLC_Private = idLC_Main + ":Private";
    final static String idLC_Commercial = idLC_Main + ":Commercial";
    final static String idLC_Mortgage = idLC_Main + ":Mortgage";
    final static String idLC_CreditCards = idLC_Main + ":CreditCards";
    final static String idLC_Pending = idLC_Main + ":Pending";

    /**
     * Transaction ids.
     */
    final static String idTC_Totals = "Totals";
    final static String idTC_Transfer = "Transfer";
    final static String idTC_Income = "Income";
    final static String idTC_Interest = idTC_Income + ":Interest";
    final static String idTC_Dividend = idTC_Income + ":Dividend";
    final static String idTC_Salary = idTC_Income + ":Salary";
    final static String idTC_Benefit = idTC_Income + ":Benefit";
    final static String idTC_CashBack = idTC_Income + ":CashBack";
    final static String idTC_EmployeeNI = idTC_Income + ":EmployeeNatIns";
    final static String idTC_EmployerNI = idTC_Income + ":EmployerNatIns";
    final static String idTC_Inheritance = idTC_Income + ":Inheritance";
    final static String idTC_IncGifts = idTC_Income + ":Gifts";
    final static String idTC_LoanInterest = idTC_Income + ":LoanInterest";
    final static String idTC_OpeningBal = idTC_Income + ":OpeningBalance";
    final static String idTC_TaxedInterest = idTC_Income + ":TaxedInterest";
    final static String idTC_TaxFreeInt = idTC_Income + ":TaxFreeInterest";
    final static String idTC_TaxFreeDiv = idTC_Income + ":TaxFreeDividend";
    final static String idTC_ShareDiv = idTC_Income + ":ShareDividend";
    final static String idTC_UnitTrustDiv = idTC_Income + ":UnitTrustDividend";
    final static String idTC_Peer2PeerInterest = idTC_Income + ":Peer2PeerInterest";
    final static String idTC_IncExpenses = idTC_Income + ":Expenses";
    final static String idTC_ForeignDividend = idTC_Income + ":ForeignDividend";
    final static String idTC_Grant = idTC_Income + ":Grant";
    final static String idTC_GrossInterest = idTC_Income + ":GrossInterest";
    final static String idTC_GrossLoyaltyBonus = idTC_Income + ":GrossLoyaltyBonus";
    final static String idTC_LoyaltyBonus = idTC_Income + ":LoyaltyBonus";
    final static String idTC_TaxedLoyaltyBonus = idTC_Income + ":TaxedLoyaltyBonus";
    final static String idTC_TaxFreeLoyaltyBonus = idTC_Income + ":TaxFreeLoyaltyBonus";
    final static String idTC_OptionsExercise = idTC_Income + ":OptionsExercise";
    final static String idTC_RentalIncome = idTC_Income + ":RentalIncome";
    final static String idTC_RoomRental = idTC_Income + ":RoomRental";
    final static String idTC_Pension = idTC_Income + ":Pension";
    final static String idTC_StatePension = idTC_Income + ":StatePension";
    final static String idTC_PensionContrib = idTC_Income + ":PensionContribution";
    final static String idTC_SocialSecurity = idTC_Income + ":SocialSecurity";
    final static String idTC_Market = "Market";
    final static String idTC_MktGrowth = idTC_Market + ":Growth";
    final static String idTC_MktCurrAdjust = idTC_Market + ":CurrencyAdjust";
    final static String idTC_MktCapGain = idTC_Market + ":CapitalGain";
    final static String idTC_MktChgGain = idTC_Market + ":ChargeableGain";
    final static String idTC_MktResGain = idTC_Market + ":ResidentialGain";
    final static String idTC_MktTaxFreeGain = idTC_Market + ":TaxFreeGain";
    final static String idTC_Car = "Car";
    final static String idTC_CarPetrol = idTC_Car + ":Petrol";
    final static String idTC_CarParking = idTC_Car + ":Parking";
    final static String idTC_CarRental = idTC_Car + ":Rental";
    final static String idTC_CarService = idTC_Car + ":Service";
    final static String idTC_CarLease = idTC_Car + ":Lease";
    final static String idTC_CarElectric = idTC_Car + ":Electric";
    final static String idTC_Charges = "Charges";
    final static String idTC_ChgBadDebtInt = idTC_Charges + ":BadDebtInterest";
    final static String idTC_ChgBadDebtCap = idTC_Charges + ":BadDebtCapital";
    final static String idTC_ChgFees = idTC_Charges + ":Fees";
    final static String idTC_ChgFines = idTC_Charges + ":Fines";
    final static String idTC_ChgInterest = idTC_Charges + ":Interest";
    final static String idTC_Expenses = "Expenses";
    final static String idTC_ExpBusiness = idTC_Expenses + ":Business";
    final static String idTC_ExpCash = idTC_Expenses + ":Cash";
    final static String idTC_ExpEstate = idTC_Expenses + ":Estate";
    final static String idTC_ExpHotel = idTC_Expenses + ":Hotel";
    final static String idTC_ExpMisc = idTC_Expenses + ":Misc";
    final static String idTC_ExpNewspaper = idTC_Expenses + ":Newspaper";
    final static String idTC_ExpPets = idTC_Expenses + ":Pets";
    final static String idTC_ExpRent = idTC_Expenses + ":Rent";
    final static String idTC_ExpRental = idTC_Expenses + ":RentalExpense";
    final static String idTC_ExpTravel = idTC_Expenses + ":Travel";
    final static String idTC_ExpVirtual = idTC_Expenses + ":Virtual";
    final static String idTC_Gifts = "Gifts";
    final static String idTC_GiftXmas = idTC_Gifts + ":Christmas";
    final static String idTC_GiftBDay = idTC_Gifts + ":Birthday";
    final static String idTC_GiftCharity = idTC_Gifts + ":Charity";
    final static String idTC_GiftOther = idTC_Gifts + ":Other";
    final static String idTC_Health = "Health";
    final static String idTC_HealthDental = idTC_Health + ":Dental";
    final static String idTC_HealthHaircut = idTC_Health + ":Haircut";
    final static String idTC_HealthMedical = idTC_Health + ":Medical";
    final static String idTC_HealthOpticians = idTC_Health + ":Opticians";
    final static String idTC_Holiday = "Holiday";
    final static String idTC_HolHotels = idTC_Holiday + ":Hotels";
    final static String idTC_HolTravel = idTC_Holiday + ":Travel";
    final static String idTC_HolEntertainment = idTC_Holiday + ":Entertainment";
    final static String idTC_Household = "Household";
    final static String idTC_HouseCleaning = idTC_Household + ":Cleaning";
    final static String idTC_HouseLaundry = idTC_Household + ":Laundry";
    final static String idTC_HouseComputing = idTC_Household + ":Computing";
    final static String idTC_HouseElectric = idTC_Household + ":Electris";
    final static String idTC_HouseFurnishings = idTC_Household + ":Furnishings";
    final static String idTC_HouseFurniture = idTC_Household + ":Furniture";
    final static String idTC_HouseGarden = idTC_Household + ":Garden";
    final static String idTC_HouseWhiteGoods = idTC_Household + ":WhiteGoods";
    final static String idTC_HouseEstate = idTC_Household + ":EstateFees";
    final static String idTC_Insurance = "Insurance";
    final static String idTC_InsBreakdown = idTC_Insurance + ":Breakdown";
    final static String idTC_InsDriving = idTC_Insurance + ":Driving";
    final static String idTC_InsTravel = idTC_Insurance + ":Travel";
    final static String idTC_InsHouse = idTC_Insurance + ":House";
    final static String idTC_Leisure = "Leisure";
    final static String idTC_LeisAlcohol = idTC_Leisure + ":Alcohol";
    final static String idTC_LeisBooks = idTC_Leisure + ":Books";
    final static String idTC_LeisCinema = idTC_Leisure + ":Cinema";
    final static String idTC_LeisDining = idTC_Leisure + ":Dining";
    final static String idTC_LeisEBooks = idTC_Leisure + ":E-Books";
    final static String idTC_LeisEvents = idTC_Leisure + ":Events";
    final static String idTC_LeisGames = idTC_Leisure + ":Games";
    final static String idTC_LeisMisc = idTC_Leisure + ":Misc";
    final static String idTC_LeisMovies = idTC_Leisure + ":Movies";
    final static String idTC_LeisMusic = idTC_Leisure + ":Music";
    final static String idTC_LeisSports = idTC_Leisure + ":Sports";
    final static String idTC_LeisTheatre = idTC_Leisure + ":Theatre";
    final static String idTC_Loan = "Loan";
    final static String idTC_LoanWriteDown = idTC_Loan + ":WriteDown";
    final static String idTC_Mortgage = "Mortgage";
    final static String idTC_MortgageDeedAdmin = idTC_Mortgage + ":DeedAdmin";
    final static String idTC_MortgageInterest = idTC_Mortgage + ":Interest";
    final static String idTC_Security = "Security";
    final static String idTC_SecAdjust = idTC_Security + ":AdjustUnits";
    final static String idTC_SecClose = idTC_Security + ":Closure";
    final static String idTC_SecDeMerger = idTC_Security + ":DeMerger";
    final static String idTC_SecOptGrant = idTC_Security + ":OptionsGrant";
    final static String idTC_SecOptVest = idTC_Security + ":OptionsVest";
    final static String idTC_SecOptExpire = idTC_Security + ":OptionsExpire";
    final static String idTC_SecPortXfer = idTC_Security + ":PortfolioXfer";
    final static String idTC_SecReplace = idTC_Security + ":Replace";
    final static String idTC_SecRightsIssue = idTC_Security + ":RightsIssue";
    final static String idTC_SecStockSplit = idTC_Security + ":StockSplit";
    final static String idTC_SecTakeover = idTC_Security + ":Takeover";
    final static String idTC_Shopping = "Shopping";
    final static String idTC_ShopAlcohol = idTC_Shopping + ":Alcohol";
    final static String idTC_ShopFood = idTC_Shopping + ":Food";
    final static String idTC_ShopClothes = idTC_Shopping + ":Clothes";
    final static String idTC_ShopHousehold = idTC_Shopping + ":Household";
    final static String idTC_ShopPets = idTC_Shopping + ":Pets";
    final static String idTC_Taxes = "Taxes";
    final static String idTC_TaxCar = idTC_Taxes + ":Car";
    final static String idTC_TaxCouncil = idTC_Taxes + ":CouncilTax";
    final static String idTC_TaxIncome = idTC_Taxes + ":IncomeTax";
    final static String idTC_TaxInterest = idTC_Taxes + ":Interest";
    final static String idTC_TaxRelief = idTC_Taxes + ":Relief";
    final static String idTC_TaxTVLicence = idTC_Taxes + ":TVLicence";
    final static String idTC_Utilities = "Utilities";
    final static String idTC_UtilGas = idTC_Utilities + ":Gas";
    final static String idTC_UtilElectric = idTC_Utilities + ":Electric";
    final static String idTC_UtilWater = idTC_Utilities + ":Water";
    final static String idTC_UtilInternet = idTC_Utilities + ":Internet";
    final static String idTC_UtilPhone = idTC_Utilities + ":Phone";
    final static String idTC_UtilTV = idTC_Utilities + ":TV";

    /**
     * Region ids.
     */
    final static String idRG_UK = "UK";
    final static String idRG_US = "US";
    final static String idRG_Europe = "Europe";
    final static String idRG_Americas = "Americas";
    final static String idRG_Asia = "Asia";
    final static String idRG_Global = "Global";
    final static String idRG_Emerging = "Emerging";

    /**
     * TransactionTag ids.
     */
    final static String idTG_Important = "Important";
    final static String idTG_Work = "Work";
    final static String idTG_Personal = "Personal";

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
     * @param pDataSet the dataSet
     */
    MoneyWiseTestCategories(final MoneyWiseDataSet pDataSet) {
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
     * @throws OceanusException on error
     */
    private void buildStatic() throws OceanusException {
        theStaticBuilder.buildBasic();
        theStaticBuilder.buildCurrency(MoneyWiseCurrencyClass.EUR);
        theStaticBuilder.buildCurrency(MoneyWiseCurrencyClass.USD);
    }

    /**
     * build Deposits.
     * @throws OceanusException on error
     */
    private void buildDeposits() throws OceanusException {
        theDepositBuilder.name(idDC_Main).type(MoneyWiseDepositCategoryClass.PARENT).build();
        theDepositBuilder.name(idDC_Current).type(MoneyWiseDepositCategoryClass.CHECKING).build();
        theDepositBuilder.name(idDC_Savings).type(MoneyWiseDepositCategoryClass.SAVINGS).build();
        theDepositBuilder.name(idDC_TaxFreeSavings).type(MoneyWiseDepositCategoryClass.TAXFREESAVINGS).build();
        theDepositBuilder.name(idDC_Peer2Peer).type(MoneyWiseDepositCategoryClass.PEER2PEER).build();
        theDepositBuilder.name(idDC_Bonds).type(MoneyWiseDepositCategoryClass.BOND).build();
        theDepositBuilder.name(idDC_TaxFreeBonds).type(MoneyWiseDepositCategoryClass.TAXFREEBOND).build();
    }

    /**
     * build Cash.
     * @throws OceanusException on error
     */
    private void buildCash() throws OceanusException {
        theCashBuilder.name(idCC_Main).type(MoneyWiseCashCategoryClass.PARENT).build();
        theCashBuilder.name(idCC_Cash).type(MoneyWiseCashCategoryClass.AUTOEXPENSE).build();
        theCashBuilder.name(idCC_Foreign).type(MoneyWiseCashCategoryClass.CASH).build();
    }

    /**
     * build loans.
     * @throws OceanusException on error
     */
    private void buildLoans() throws OceanusException {
        theLoanBuilder.name(idLC_Main).type(MoneyWiseLoanCategoryClass.PARENT).build();
        theLoanBuilder.name(idLC_Private).type(MoneyWiseLoanCategoryClass.PRIVATELOAN).build();
        theLoanBuilder.name(idLC_Commercial).type(MoneyWiseLoanCategoryClass.LOAN).build();
        theLoanBuilder.name(idLC_Mortgage).type(MoneyWiseLoanCategoryClass.LOAN).build();
        theLoanBuilder.name(idLC_CreditCards).type(MoneyWiseLoanCategoryClass.CREDITCARD).build();
        theLoanBuilder.name(idLC_Pending).type(MoneyWiseLoanCategoryClass.LOAN).build();
    }

    /**
     * build Trans.
     * @throws OceanusException on error
     */
    private void buildTrans() throws OceanusException {
        /* Transfer */
        final MoneyWiseTransCategory myTotals = theTransBuilder.name(idTC_Totals).type(MoneyWiseTransCategoryClass.TOTALS).build();

        /* Transfer */
        theTransBuilder.name(idTC_Transfer).type(MoneyWiseTransCategoryClass.TRANSFER).build();

        /* Income */
        theTransBuilder.name(idTC_Income).parent(myTotals).type(MoneyWiseTransCategoryClass.INCOMETOTALS).build();
        theTransBuilder.name(idTC_Interest).type(MoneyWiseTransCategoryClass.INTEREST).build();
        theTransBuilder.name(idTC_Dividend).type(MoneyWiseTransCategoryClass.DIVIDEND).build();
        theTransBuilder.name(idTC_Salary).type(MoneyWiseTransCategoryClass.TAXEDINCOME).build();
        theTransBuilder.name(idTC_Benefit).type(MoneyWiseTransCategoryClass.VIRTUALINCOME).build();
        theTransBuilder.name(idTC_CashBack).type(MoneyWiseTransCategoryClass.CASHBACK).build();
        theTransBuilder.name(idTC_Peer2PeerInterest).type(MoneyWiseTransCategoryClass.PEER2PEERINTEREST).build();
        theTransBuilder.name(idTC_EmployeeNI).type(MoneyWiseTransCategoryClass.EMPLOYEENATINS).build();
        theTransBuilder.name(idTC_EmployerNI).type(MoneyWiseTransCategoryClass.EMPLOYERNATINS).build();
        theTransBuilder.name(idTC_Inheritance).type(MoneyWiseTransCategoryClass.INHERITED).build();
        theTransBuilder.name(idTC_IncGifts).type(MoneyWiseTransCategoryClass.GIFTEDINCOME).build();
        theTransBuilder.name(idTC_LoanInterest).type(MoneyWiseTransCategoryClass.LOANINTERESTEARNED).build();
        theTransBuilder.name(idTC_OpeningBal).type(MoneyWiseTransCategoryClass.OPENINGBALANCE).build();
        theTransBuilder.name(idTC_TaxedInterest).type(MoneyWiseTransCategoryClass.TAXEDINTEREST).build();
        theTransBuilder.name(idTC_TaxFreeInt).type(MoneyWiseTransCategoryClass.TAXFREEINTEREST).build();
        theTransBuilder.name(idTC_TaxFreeDiv).type(MoneyWiseTransCategoryClass.TAXFREEDIVIDEND).build();
        theTransBuilder.name(idTC_ShareDiv).type(MoneyWiseTransCategoryClass.SHAREDIVIDEND).build();
        theTransBuilder.name(idTC_UnitTrustDiv).type(MoneyWiseTransCategoryClass.UNITTRUSTDIVIDEND).build();
        theTransBuilder.name(idTC_IncExpenses).type(MoneyWiseTransCategoryClass.RECOVEREDEXPENSES).build();
        theTransBuilder.name(idTC_ForeignDividend).type(MoneyWiseTransCategoryClass.FOREIGNDIVIDEND).build();
        theTransBuilder.name(idTC_Grant).type(MoneyWiseTransCategoryClass.TAXEDINCOME).build();
        theTransBuilder.name(idTC_GrossInterest).type(MoneyWiseTransCategoryClass.GROSSINTEREST).build();
        theTransBuilder.name(idTC_GrossLoyaltyBonus).type(MoneyWiseTransCategoryClass.GROSSLOYALTYBONUS).build();
        theTransBuilder.name(idTC_LoyaltyBonus).type(MoneyWiseTransCategoryClass.LOYALTYBONUS).build();
        theTransBuilder.name(idTC_TaxedLoyaltyBonus).type(MoneyWiseTransCategoryClass.TAXEDLOYALTYBONUS).build();
        theTransBuilder.name(idTC_TaxFreeLoyaltyBonus).type(MoneyWiseTransCategoryClass.TAXFREELOYALTYBONUS).build();
        theTransBuilder.name(idTC_OptionsExercise).type(MoneyWiseTransCategoryClass.OPTIONSEXERCISE).build();
        theTransBuilder.name(idTC_RentalIncome).type(MoneyWiseTransCategoryClass.RENTALINCOME).build();
        theTransBuilder.name(idTC_RoomRental).type(MoneyWiseTransCategoryClass.ROOMRENTALINCOME).build();
        theTransBuilder.name(idTC_Pension).type(MoneyWiseTransCategoryClass.TAXEDINCOME).build();
        theTransBuilder.name(idTC_StatePension).type(MoneyWiseTransCategoryClass.GROSSINCOME).build();
        theTransBuilder.name(idTC_PensionContrib).type(MoneyWiseTransCategoryClass.PENSIONCONTRIB).build();
        theTransBuilder.name(idTC_SocialSecurity).type(MoneyWiseTransCategoryClass.GROSSINCOME).build();

        /* Market */
        theTransBuilder.name(idTC_Market).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_MktGrowth).type(MoneyWiseTransCategoryClass.MARKETGROWTH).build();
        theTransBuilder.name(idTC_MktCurrAdjust).type(MoneyWiseTransCategoryClass.CURRENCYFLUCTUATION).build();
        theTransBuilder.name(idTC_MktCapGain).type(MoneyWiseTransCategoryClass.CAPITALGAIN).build();
        theTransBuilder.name(idTC_MktChgGain).type(MoneyWiseTransCategoryClass.CHARGEABLEGAIN).build();
        theTransBuilder.name(idTC_MktResGain).type(MoneyWiseTransCategoryClass.RESIDENTIALGAIN).build();
        theTransBuilder.name(idTC_MktTaxFreeGain).type(MoneyWiseTransCategoryClass.TAXFREEGAIN).build();

        /* Car */
        theTransBuilder.name(idTC_Car).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_CarPetrol).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_CarParking).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_CarRental).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_CarService).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_CarLease).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_CarElectric).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Charges */
        theTransBuilder.name(idTC_Charges).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_ChgBadDebtCap).type(MoneyWiseTransCategoryClass.BADDEBTCAPITAL).build();
        theTransBuilder.name(idTC_ChgBadDebtInt).type(MoneyWiseTransCategoryClass.BADDEBTINTEREST).build();
        theTransBuilder.name(idTC_ChgFees).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ChgFines).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ChgInterest).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Expenses */
        theTransBuilder.name(idTC_Expenses).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_ExpBusiness).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ExpCash).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ExpEstate).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ExpHotel).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ExpMisc).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ExpNewspaper).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ExpPets).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ExpRent).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ExpRental).type(MoneyWiseTransCategoryClass.RENTALEXPENSE).build();
        theTransBuilder.name(idTC_ExpTravel).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ExpVirtual).type(MoneyWiseTransCategoryClass.WITHHELD).build();

        /* Gifts */
        theTransBuilder.name(idTC_Gifts).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_GiftBDay).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_GiftXmas).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_GiftCharity).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_GiftOther).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Health */
        theTransBuilder.name(idTC_Health).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_HealthDental).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HealthHaircut).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HealthMedical).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HealthOpticians).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Holiday */
        theTransBuilder.name(idTC_Holiday).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_HolHotels).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HolTravel).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HolEntertainment).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Household */
        theTransBuilder.name(idTC_Household).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_HouseCleaning).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HouseLaundry).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HouseComputing).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HouseElectric).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HouseFurnishings).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HouseFurniture).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HouseGarden).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HouseWhiteGoods).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_HouseEstate).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Insurance */
        theTransBuilder.name(idTC_Insurance).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_InsBreakdown).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_InsDriving).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_InsTravel).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_InsHouse).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Leisure */
        theTransBuilder.name(idTC_Leisure).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_LeisAlcohol).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisBooks).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisCinema).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisDining).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisEBooks).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisEvents).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisGames).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisMisc).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisMusic).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisMovies).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisSports).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_LeisTheatre).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Loans */
        theTransBuilder.name(idTC_Loan).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_LoanWriteDown).type(MoneyWiseTransCategoryClass.WRITEOFF).build();

        /* Loans */
        theTransBuilder.name(idTC_Mortgage).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_MortgageDeedAdmin).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_MortgageInterest).type(MoneyWiseTransCategoryClass.LOANINTERESTCHARGED).build();

        /* Security */
        theTransBuilder.name(idTC_Security).parent(myTotals).type(MoneyWiseTransCategoryClass.SECURITYPARENT).build();
        theTransBuilder.name(idTC_SecAdjust).type(MoneyWiseTransCategoryClass.UNITSADJUST).build();
        theTransBuilder.name(idTC_SecClose).type(MoneyWiseTransCategoryClass.SECURITYCLOSURE).build();
        theTransBuilder.name(idTC_SecDeMerger).type(MoneyWiseTransCategoryClass.STOCKDEMERGER).build();
        theTransBuilder.name(idTC_SecOptExpire).type(MoneyWiseTransCategoryClass.OPTIONSEXPIRE).build();
        theTransBuilder.name(idTC_SecOptGrant).type(MoneyWiseTransCategoryClass.OPTIONSGRANT).build();
        theTransBuilder.name(idTC_SecOptVest).type(MoneyWiseTransCategoryClass.OPTIONSVEST).build();
        theTransBuilder.name(idTC_SecReplace).type(MoneyWiseTransCategoryClass.SECURITYREPLACE).build();
        theTransBuilder.name(idTC_SecPortXfer).type(MoneyWiseTransCategoryClass.PORTFOLIOXFER).build();
        theTransBuilder.name(idTC_SecRightsIssue).type(MoneyWiseTransCategoryClass.STOCKRIGHTSISSUE).build();
        theTransBuilder.name(idTC_SecStockSplit).type(MoneyWiseTransCategoryClass.STOCKSPLIT).build();
        theTransBuilder.name(idTC_SecTakeover).type(MoneyWiseTransCategoryClass.STOCKTAKEOVER).build();

        /* Shopping */
        theTransBuilder.name(idTC_Shopping).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_ShopAlcohol).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ShopFood).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ShopClothes).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ShopHousehold).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_ShopPets).type(MoneyWiseTransCategoryClass.EXPENSE).build();

        /* Taxes */
        theTransBuilder.name(idTC_Taxes).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_TaxCar).type(MoneyWiseTransCategoryClass.LOCALTAXES).build();
        theTransBuilder.name(idTC_TaxCouncil).type(MoneyWiseTransCategoryClass.LOCALTAXES).build();
        theTransBuilder.name(idTC_TaxIncome).type(MoneyWiseTransCategoryClass.INCOMETAX).build();
        theTransBuilder.name(idTC_TaxInterest).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_TaxRelief).type(MoneyWiseTransCategoryClass.TAXRELIEF).build();
        theTransBuilder.name(idTC_TaxTVLicence).type(MoneyWiseTransCategoryClass.LOCALTAXES).build();

        /* Utilities */
        theTransBuilder.name(idTC_Utilities).parent(myTotals).type(MoneyWiseTransCategoryClass.EXPENSETOTALS).build();
        theTransBuilder.name(idTC_UtilElectric).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_UtilGas).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_UtilInternet).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_UtilPhone).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_UtilTV).type(MoneyWiseTransCategoryClass.EXPENSE).build();
        theTransBuilder.name(idTC_UtilWater).type(MoneyWiseTransCategoryClass.EXPENSE).build();
    }

    /**
     * build regions.
     * @throws OceanusException on error
     */
    private void buildRegions() throws OceanusException {
        theRegionBuilder.name(idRG_UK).build();
        theRegionBuilder.name(idRG_US).build();
        theRegionBuilder.name(idRG_Europe).build();
        theRegionBuilder.name(idRG_Americas).build();
        theRegionBuilder.name(idRG_Asia).build();
        theRegionBuilder.name(idRG_Global).build();
        theRegionBuilder.name(idRG_Emerging).build();
    }

    /**
     * build tags.
     * @throws OceanusException on error
     */
    private void buildTransactionTags() throws OceanusException {
        theTagBuilder.name(idTG_Important).build();
        theTagBuilder.name(idTG_Work).build();
        theTagBuilder.name(idTG_Personal).build();
    }
}
