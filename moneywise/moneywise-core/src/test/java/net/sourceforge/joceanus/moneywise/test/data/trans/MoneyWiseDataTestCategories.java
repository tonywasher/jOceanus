package net.sourceforge.joceanus.moneywise.test.data.trans;

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

public class MoneyWiseDataTestCategories {
    /**
     * Deposit ids.
     */
    static final String idDC_Main = "Banking";
    static final String idDC_Current = idDC_Main + ":Current";
    static final String idDC_Savings = idDC_Main + ":Savings";
    static final String idDC_TaxFreeSavings = idDC_Main + ":TaxFreeSavings";
    static final String idDC_Peer2Peer = idDC_Main + ":Peer2Peer";
    static final String idDC_Bonds = idDC_Main + ":Bonds";
    static final String idDC_TaxFreeBonds = idDC_Main + ":TaxFreeBonds";

    /**
     * Cash ids.
     */
    static final String idCC_Main = "Cash";
    static final String idCC_Cash = idCC_Main + ":Cash";
    static final String idCC_Wallet = idCC_Main + ":Wallet";

    /**
     * Loan ids.
     */
    static final String idLC_Main = "Loans";
    static final String idLC_Private = idLC_Main + ":Private";
    static final String idLC_Commercial = idLC_Main + ":Commercial";
    static final String idLC_Mortgage = idLC_Main + ":Mortgage";
    static final String idLC_CreditCards = idLC_Main + ":CreditCards";
    static final String idLC_Pending = idLC_Main + ":Pending";

    /**
     * Transaction ids.
     */
    static final String idTC_Totals = "Totals";
    static final String idTC_Transfer = "Transfer";
    static final String idTC_Income = "Income";
    static final String idTC_Interest = idTC_Income + ":Interest";
    static final String idTC_Dividend = idTC_Income + ":Dividend";
    static final String idTC_Salary = idTC_Income + ":Salary";
    static final String idTC_Benefit = idTC_Income + ":Benefit";
    static final String idTC_CashBack = idTC_Income + ":CashBack";
    static final String idTC_EmployeeNI = idTC_Income + ":EmployeeNatIns";
    static final String idTC_EmployerNI = idTC_Income + ":EmployerNatIns";
    static final String idTC_Inheritance = idTC_Income + ":Inheritance";
    static final String idTC_IncGifts = idTC_Income + ":Gifts";
    static final String idTC_LoanInterest = idTC_Income + ":LoanInterest";
    static final String idTC_OpeningBal = idTC_Income + ":OpeningBalance";
    static final String idTC_TaxedInterest = idTC_Income + ":TaxedInterest";
    static final String idTC_TaxFreeInt = idTC_Income + ":TaxFreeInterest";
    static final String idTC_TaxFreeDiv = idTC_Income + ":TaxFreeDividend";
    static final String idTC_ShareDiv = idTC_Income + ":ShareDividend";
    static final String idTC_UnitTrustDiv = idTC_Income + ":UnitTrustDividend";
    static final String idTC_Peer2PeerInterest = idTC_Income + ":Peer2PeerInterest";
    static final String idTC_IncExpenses = idTC_Income + ":Expenses";
    static final String idTC_ForeignDividend = idTC_Income + ":ForeignDividend";
    static final String idTC_Grant = idTC_Income + ":Grant";
    static final String idTC_GrossInterest = idTC_Income + ":GrossInterest";
    static final String idTC_GrossLoyaltyBonus = idTC_Income + ":GrossLoyaltyBonus";
    static final String idTC_LoyaltyBonus = idTC_Income + ":LoyaltyBonus";
    static final String idTC_TaxedLoyaltyBonus = idTC_Income + ":TaxedLoyaltyBonus";
    static final String idTC_TaxFreeLoyaltyBonus = idTC_Income + ":TaxFreeLoyaltyBonus";
    static final String idTC_OptionsExercise = idTC_Income + ":OptionsExercise";
    static final String idTC_RentalIncome = idTC_Income + ":RentalIncome";
    static final String idTC_RoomRental = idTC_Income + ":RoomRental";
    static final String idTC_Pension = idTC_Income + ":Pension";
    static final String idTC_StatePension = idTC_Income + ":StatePension";
    static final String idTC_PensionContrib = idTC_Income + ":PensionContribution";
    static final String idTC_SocialSecurity = idTC_Income + ":SocialSecurity";
    static final String idTC_Market = "Market";
    static final String idTC_MktGrowth = idTC_Market + ":Growth";
    static final String idTC_MktCurrAdjust = idTC_Market + ":CurrencyAdjust";
    static final String idTC_MktCapGain = idTC_Market + ":CapitalGain";
    static final String idTC_MktChgGain = idTC_Market + ":ChargeableGain";
    static final String idTC_MktResGain = idTC_Market + ":ResidentialGain";
    static final String idTC_MktTaxFreeGain = idTC_Market + ":TaxFreeGain";
    static final String idTC_Car = "Car";
    static final String idTC_CarPetrol = idTC_Car + ":Petrol";
    static final String idTC_CarParking = idTC_Car + ":Parking";
    static final String idTC_CarRental = idTC_Car + ":Rental";
    static final String idTC_CarService = idTC_Car + ":Service";
    static final String idTC_CarLease = idTC_Car + ":Lease";
    static final String idTC_CarElectric = idTC_Car + ":Electric";
    static final String idTC_Charges = "Charges";
    static final String idTC_ChgBadDebtInt = idTC_Charges + ":BadDebtInterest";
    static final String idTC_ChgBadDebtCap = idTC_Charges + ":BadDebtCapital";
    static final String idTC_ChgFees = idTC_Charges + ":Fees";
    static final String idTC_ChgFines = idTC_Charges + ":Fines";
    static final String idTC_ChgInterest = idTC_Charges + ":Interest";
    static final String idTC_Expenses = "Expenses";
    static final String idTC_ExpBusiness = idTC_Expenses + ":Business";
    static final String idTC_ExpCash = idTC_Expenses + ":Cash";
    static final String idTC_ExpEstate = idTC_Expenses + ":Estate";
    static final String idTC_ExpHotel = idTC_Expenses + ":Hotel";
    static final String idTC_ExpMisc = idTC_Expenses + ":Misc";
    static final String idTC_ExpNewspaper = idTC_Expenses + ":Newspaper";
    static final String idTC_ExpPets = idTC_Expenses + ":Pets";
    static final String idTC_ExpRent = idTC_Expenses + ":Rent";
    static final String idTC_ExpRental = idTC_Expenses + ":RentalExpense";
    static final String idTC_ExpTravel = idTC_Expenses + ":Travel";
    static final String idTC_ExpVirtual = idTC_Expenses + ":Virtual";
    static final String idTC_Gifts = "Gifts";
    static final String idTC_GiftXmas = idTC_Gifts + ":Christmas";
    static final String idTC_GiftBDay = idTC_Gifts + ":Birthday";
    static final String idTC_GiftCharity = idTC_Gifts + ":Charity";
    static final String idTC_GiftOther = idTC_Gifts + ":Other";
    static final String idTC_Health = "Health";
    static final String idTC_HealthDental = idTC_Health + ":Dental";
    static final String idTC_HealthHaircut = idTC_Health + ":Haircut";
    static final String idTC_HealthMedical = idTC_Health + ":Medical";
    static final String idTC_HealthOpticians = idTC_Health + ":Opticians";
    static final String idTC_Holiday = "Holiday";
    static final String idTC_HolHotels = idTC_Holiday + ":Hotels";
    static final String idTC_HolTravel = idTC_Holiday + ":Travel";
    static final String idTC_HolEntertainment = idTC_Holiday + ":Entertainment";
    static final String idTC_Household = "Household";
    static final String idTC_HouseCleaning = idTC_Household + ":Cleaning";
    static final String idTC_HouseLaundry = idTC_Household + ":Laundry";
    static final String idTC_HouseComputing = idTC_Household + ":Computing";
    static final String idTC_HouseElectric = idTC_Household + ":Electris";
    static final String idTC_HouseFurnishings = idTC_Household + ":Furnishings";
    static final String idTC_HouseFurniture = idTC_Household + ":Furniture";
    static final String idTC_HouseGarden = idTC_Household + ":Garden";
    static final String idTC_HouseWhiteGoods = idTC_Household + ":WhiteGoods";
    static final String idTC_HouseEstate = idTC_Household + ":EstateFees";
    static final String idTC_Insurance = "Insurance";
    static final String idTC_InsBreakdown = idTC_Insurance + ":Breakdown";
    static final String idTC_InsDriving = idTC_Insurance + ":Driving";
    static final String idTC_InsTravel = idTC_Insurance + ":Travel";
    static final String idTC_InsHouse = idTC_Insurance + ":House";
    static final String idTC_Leisure = "Leisure";
    static final String idTC_LeisAlcohol = idTC_Leisure + ":Alcohol";
    static final String idTC_LeisBooks = idTC_Leisure + ":Books";
    static final String idTC_LeisCinema = idTC_Leisure + ":Cinema";
    static final String idTC_LeisDining = idTC_Leisure + ":Dining";
    static final String idTC_LeisEBooks = idTC_Leisure + ":E-Books";
    static final String idTC_LeisEvents = idTC_Leisure + ":Events";
    static final String idTC_LeisGames = idTC_Leisure + ":Games";
    static final String idTC_LeisMisc = idTC_Leisure + ":Misc";
    static final String idTC_LeisMovies = idTC_Leisure + ":Movies";
    static final String idTC_LeisMusic = idTC_Leisure + ":Music";
    static final String idTC_LeisSports = idTC_Leisure + ":Sports";
    static final String idTC_LeisTheatre = idTC_Leisure + ":Theatre";
    static final String idTC_Loan = "Loan";
    static final String idTC_LoanWriteDown = idTC_Loan + ":WriteDown";
    static final String idTC_LoanInterestChg = idTC_Loan + ":InterestCharged";
    static final String idTC_Mortgage = "Mortgage";
    static final String idTC_MortgageDeedAdmin = idTC_Mortgage + ":DeedAdmin";
    static final String idTC_MortgageInterest = idTC_Mortgage + ":Interest";
    static final String idTC_Security = "Security";
    static final String idTC_SecAdjust = idTC_Security + ":AdjustUnits";
    static final String idTC_SecClose = idTC_Security + ":Closure";
    static final String idTC_SecDeMerger = idTC_Security + ":DeMerger";
    static final String idTC_SecOptGrant = idTC_Security + ":OptionsGrant";
    static final String idTC_SecOptVest = idTC_Security + ":OptionsVest";
    static final String idTC_SecOptExpire = idTC_Security + ":OptionsExpire";
    static final String idTC_SecPortXfer = idTC_Security + ":PortfolioXfer";
    static final String idTC_SecReplace = idTC_Security + ":Replace";
    static final String idTC_SecRightsIssue = idTC_Security + ":RightsIssue";
    static final String idTC_SecStockSplit = idTC_Security + ":StockSplit";
    static final String idTC_SecTakeover = idTC_Security + ":Takeover";
    static final String idTC_Shopping = "Shopping";
    static final String idTC_ShopAlcohol = idTC_Shopping + ":Alcohol";
    static final String idTC_ShopFood = idTC_Shopping + ":Food";
    static final String idTC_ShopClothes = idTC_Shopping + ":Clothes";
    static final String idTC_ShopHousehold = idTC_Shopping + ":Household";
    static final String idTC_ShopPets = idTC_Shopping + ":Pets";
    static final String idTC_Taxes = "Taxes";
    static final String idTC_TaxCar = idTC_Taxes + ":Car";
    static final String idTC_TaxCouncil = idTC_Taxes + ":CouncilTax";
    static final String idTC_TaxIncome = idTC_Taxes + ":IncomeTax";
    static final String idTC_TaxInterest = idTC_Taxes + ":Interest";
    static final String idTC_TaxRelief = idTC_Taxes + ":Relief";
    static final String idTC_TaxTVLicence = idTC_Taxes + ":TVLicence";
    static final String idTC_Utilities = "Utilities";
    static final String idTC_UtilGas = idTC_Utilities + ":Gas";
    static final String idTC_UtilElectric = idTC_Utilities + ":Electric";
    static final String idTC_UtilWater = idTC_Utilities + ":Water";
    static final String idTC_UtilInternet = idTC_Utilities + ":Internet";
    static final String idTC_UtilPhone = idTC_Utilities + ":Phone";
    static final String idTC_UtilTV = idTC_Utilities + ":TV";

    /**
     * Region ids.
     */
    static final String idRG_UK = "UK";
    static final String idRG_US = "US";
    static final String idRG_Europe = "Europe";
    static final String idRG_Americas = "Americas";
    static final String idRG_Asia = "Asia";
    static final String idRG_Global = "Global";
    static final String idRG_Emerging = "Emerging";

    /**
     * TransactionTag ids.
     */
    static final String idTG_Important = "Important";
    static final String idTG_Work = "Work";
    static final String idTG_Personal = "Personal";

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
        theCashBuilder.name(idCC_Wallet).type(MoneyWiseCashCategoryClass.CASH).build();
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
        theTransBuilder.name(idTC_LoanInterestChg).type(MoneyWiseTransCategoryClass.LOANINTERESTCHARGED).build();

        /* Mortgage */
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
