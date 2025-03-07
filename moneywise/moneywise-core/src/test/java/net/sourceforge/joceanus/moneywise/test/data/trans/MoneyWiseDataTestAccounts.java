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
package net.sourceforge.joceanus.moneywise.test.data.trans;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseCashBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseDepositBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseLoanBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWisePayeeBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWisePortfolioBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseSecurityBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseSecurityPriceBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseTransactionBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseXchgRateBuilder;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseLogicException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Accounts Builder.
 */
public class MoneyWiseDataTestAccounts {
    /**
     * Payee ids.
     */
    final static String idPY_Barclays = "Barclays";
    final static String idPY_Lloyds = "Lloyds";
    final static String idPY_TSB = "TSB";
    final static String idPY_Halifax = "Halifax";
    final static String idPY_Nationwide = "Nationwide";
    final static String idPY_Starling = "Starling";
    final static String idPY_InteractiveInvestor = "InteractiveInvestor";
    final static String idPY_AJBell = "AJBell";
    final static String idPY_StandardLife = "StandardLife";
    final static String idPY_FundingCircle = "FundingCircle";
    final static String idPY_IBM = "IBM";
    final static String idPY_Petrol = "Petrol";
    final static String idPY_Parking = "Parking";
    final static String idPY_ASDA = "ASDA";
    final static String idPY_Potters = "PottersHeron";
    final static String idPY_Government = "Government";
    final static String idPY_HMRC = "InlandRevenue";
    final static String idPY_Market = "Market";
    final static String idPY_CashExpense = "CashExpense";
    final static String idPY_Damage = "Damage";
    final static String idPY_Parents = "Parents";
    final static String idPY_AssetHolder = "AssetHolder";

    /**
     * Deposit ids.
     */
    final static String idDP_BarclaysCurrent = "BarclaysCurrent";
    final static String idDP_NatWideFlexDirect = "NatWideFlexDirect";
    final static String idDP_NatWideLoyalty = "NatWideLoyalty";
    final static String idDP_NatWideISA = "NatWideISA";
    final static String idDP_StarlingSterling = "StarlingSterling";
    final static String idDP_StarlingEuro = "StarlingEuro";
    final static String idDP_StarlingEuroISA = "StarlingEuroISA";
    final static String idDP_StarlingDollar = "StarlingDollar";
    final static String idDP_FundingCircleLoans = "FundingCircleLoans";

    /**
     * Cash ids.
     */
    final static String idCS_Cash = "Cash";
    final static String idCS_EurosCash = "EurosCash";
    final static String idCS_CashWallet = "CashWallet";
    final static String idCS_EurosWallet = "EurosWallet";

    /**
     * Loan ids.
     */
    final static String idLN_Barclaycard = "Barclaycard";
    final static String idLN_DamageLoan = "DamageLoan";
    final static String idLN_BarclaysMortgage = "BarclaysMortgage";
    final static String idLN_DeferredTax = "DeferredTax";

    /**
     * Portfolio ids.
     */
    final static String idPF_AJBellStock = "AJBellStock";
    final static String idPF_InteractiveInvestorStock = "InteractiveInvestorStock";
    final static String idPF_InteractiveInvestorISA = "InteractiveInvestorISA";
    final static String idPF_InteractiveInvestorSIPP = "InteractiveInvestorSIPP";
    final static String idPF_Assets = "Assets";
    final static String idPF_Residence = "Residence";
    final static String idPF_Pensions = "Pensions";

    /**
     * Security ids.
     */
    final static String idSC_BarclaysShares = "BarclaysShares";
    final static String idSC_LloydsShares = "LloydsShares";
    final static String idSC_HalifaxShares = "HalifaxShares";
    final static String idSC_TSBShares = "TSBShares";
    final static String idSC_StarlingShares = "StarlingShares";
    final static String idSC_BarclaysSharesUS = "BarclaysSharesUS";
    final static String idSC_LloydsSharesUS = "LloydsSharesUS";
    final static String idSC_HalifaxSharesUS = "HalifaxSharesUS";
    final static String idSC_TSBSharesUS = "TSBSharesUS";
    final static String idSC_StarlingSharesUS = "StarlingSharesUS";
    final static String idSC_FordEscort = "FordEscort";
    final static String idSC_Mazda6 = "Mazda6";
    final static String idSC_12MainSt = "12MainSt";
    final static String idSC_56HighSt= "56HighSt";
    final static String idSC_41KiteSt = "41KiteSt";
    final static String idSC_IBMPension = "IBMPension";
    final static String idSC_IBMAvc = "IBMAvc";
    final static String idSC_StdLifePolicy = "StdLifePolicy";
    final static String idSC_StdLifeBond = "StdLifeBond";
    final static String idSC_StatePension = "StatePension";

    /**
     * Security holding ids.
     */
    final static String idSH_BarclaysShares = idPF_InteractiveInvestorStock + ":" + idSC_BarclaysShares;
    final static String idSH_LloydsShares = idPF_InteractiveInvestorStock + ":" + idSC_LloydsShares;
    final static String idSH_LloydsSharesISA = idPF_InteractiveInvestorISA + ":" + idSC_LloydsShares;
    final static String idSH_HalifaxShares = idPF_InteractiveInvestorStock + ":" + idSC_HalifaxShares;
    final static String idSH_TSBShares = idPF_InteractiveInvestorStock + ":" + idSC_TSBShares;
    final static String idSH_StarlingShares = idPF_InteractiveInvestorStock + ":" + idSC_StarlingShares;

    /**
     * The dataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * PayeeBuilder.
     */
    private final MoneyWisePayeeBuilder thePayeeBuilder;

    /**
     * DepositBuilder.
     */
    private final MoneyWiseDepositBuilder theDepositBuilder;

    /**
     * CashBuilder.
     */
    private final MoneyWiseCashBuilder theCashBuilder;

    /**
     * LoanBuilder.
     */
    private final MoneyWiseLoanBuilder theLoanBuilder;

    /**
     * PortfolioBuilder.
     */
    private final MoneyWisePortfolioBuilder thePortfolioBuilder;

    /**
     * SecurityBuilder.
     */
    private final MoneyWiseSecurityBuilder theSecurityBuilder;

    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * XchgRateBuilder.
     */
    private final MoneyWiseXchgRateBuilder theXchgRateBuilder;

    /**
     * SecurityPriceBuilder.
     */
    private final MoneyWiseSecurityPriceBuilder theSecurityPriceBuilder;

    /**
     * The created accounts.
     */
    private final Map<String, MoneyWiseBasicDataType> theNameMap;

    /**
     * The created prices.
     */
    private final Map<String, Map<String, String>> thePriceMap;

    /**
     * The created rates.
     */
    private final Map<MoneyWiseCurrencyClass, Map<String, String>> theRateMap;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     * @throws OceanusException on error
     */
    public MoneyWiseDataTestAccounts(final MoneyWiseDataSet pDataSet) throws OceanusException {
        /* Store the dataSet */
        theDataSet = pDataSet;

        /* Create the static and categories */
        final MoneyWiseDataTestCategories myCategories = new MoneyWiseDataTestCategories(theDataSet);
        myCategories.buildBasic();

        /* Create the builders */
        thePayeeBuilder = new MoneyWisePayeeBuilder(theDataSet);
        theDepositBuilder = new MoneyWiseDepositBuilder(theDataSet);
        theCashBuilder = new MoneyWiseCashBuilder(theDataSet);
        theLoanBuilder = new MoneyWiseLoanBuilder(theDataSet);
        thePortfolioBuilder = new MoneyWisePortfolioBuilder(theDataSet);
        theSecurityBuilder = new MoneyWiseSecurityBuilder(theDataSet);
        theTransBuilder = new MoneyWiseTransactionBuilder(theDataSet);
        theSecurityPriceBuilder = new MoneyWiseSecurityPriceBuilder(theDataSet);
        theXchgRateBuilder = new MoneyWiseXchgRateBuilder(theDataSet);

        /* Create the maps*/
        theNameMap = new HashMap<>();
        thePriceMap = new HashMap<>();
        theRateMap = new EnumMap<>(MoneyWiseCurrencyClass.class);

        /* Create the base accounts */
        createBaseAccounts();
    }

    /**
     * Obtain the transaction builder.
     * @return the transaction builder
     */
    MoneyWiseTransactionBuilder getTransBuilder() {
        return theTransBuilder;
    }

    /**
     * Create base accounts.
     * @throws OceanusException on error
     */
    private void createBaseAccounts() throws OceanusException {
        /* Create the standard payees */
        createPayees(idPY_Government, idPY_HMRC, idPY_Market);

        /* Create the standard Pension portfolio */
        createPortfolios(idPF_Pensions);

        /* Create the standard StatePension security */
        createSecurities(idSC_StatePension);
    }

    /**
     * Create payees.
     * @param pPayees the payees to create
     * @throws OceanusException on error
     */
    void createPayees(final String ...pPayees) throws OceanusException {
        for (final String myPayee : pPayees) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(myPayee);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.PAYEE.equals(myExisting)){
                    continue;
                }
                throw new MoneyWiseLogicException("Payee name already exists");
            }

            /* Create the payee */
            switch (myPayee) {
                case idPY_Barclays:
                case idPY_Nationwide:
                case idPY_Starling:
                case idPY_Lloyds:
                case idPY_Halifax:
                case idPY_TSB:
                case idPY_InteractiveInvestor:
                case idPY_FundingCircle:
                case idPY_AJBell:
                case idPY_StandardLife:
                case idPY_AssetHolder:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.INSTITUTION).build();
                    break;
                case idPY_IBM:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.EMPLOYER).build();
                    break;
                case idPY_Government:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.GOVERNMENT).build();
                    break;
                case idPY_HMRC:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.TAXMAN).build();
                    break;
                case idPY_Market:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.MARKET).build();
                    break;
                case idPY_Damage:
                case idPY_Parents:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.INDIVIDUAL).build();
                    break;
                case idPY_ASDA:
                case idPY_Petrol:
                case idPY_Parking:
                case idPY_Potters:
                case idPY_CashExpense:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.PAYEE).build();
                    break;
                default:
                    throw new MoneyWiseDataException("Unexpected Payee:- " + myPayee);
            }

            /* Record in nameMap */
            theNameMap.put(myPayee, MoneyWiseBasicDataType.PAYEE);
        }
    }

    /**
     * Create deposits.
     * @param pDeposits the deposits to create
     * @throws OceanusException on error
     */
    void createDeposits(final String ...pDeposits) throws OceanusException {
        for (final String myDeposit : pDeposits) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(myDeposit);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.DEPOSIT.equals(myExisting)){
                    continue;
                }
                throw new MoneyWiseLogicException("Deposit name already exists");
            }

            /* Create the deposit */
            switch (myDeposit) {
                case idDP_BarclaysCurrent:
                    createPayees(idPY_Barclays);
                    theDepositBuilder.name(myDeposit).parent(idPY_Barclays)
                            .category(MoneyWiseDataTestCategories.idDC_Current).openingBalance("10000").build();
                    break;
                case idDP_NatWideFlexDirect:
                    createPayees(idPY_Nationwide);
                    theDepositBuilder.name(myDeposit).parent(idPY_Nationwide)
                            .category(MoneyWiseDataTestCategories.idDC_Current).openingBalance("10000").build();
                    break;
                case idDP_NatWideLoyalty:
                    createPayees(idPY_Nationwide);
                    theDepositBuilder.name(myDeposit).parent(idPY_Nationwide)
                            .category(MoneyWiseDataTestCategories.idDC_Savings).openingBalance("10000").build();
                    break;
                case idDP_NatWideISA:
                    createPayees(idPY_Nationwide);
                    theDepositBuilder.name(myDeposit).parent(idPY_Nationwide)
                            .category(MoneyWiseDataTestCategories.idDC_TaxFreeSavings).openingBalance("10000").build();
                    break;
                case idDP_StarlingSterling:
                    createPayees(idPY_Starling);
                    theDepositBuilder.name(myDeposit).parent(idPY_Starling)
                            .category(MoneyWiseDataTestCategories.idDC_Current).openingBalance("10000").build();
                    break;
                case idDP_StarlingEuro:
                    createPayees(idPY_Starling);
                    theDepositBuilder.name(myDeposit).parent(idPY_Starling)
                            .category(MoneyWiseDataTestCategories.idDC_Current)
                            .currency(MoneyWiseCurrencyClass.EUR).openingBalance("5000").build();
                    break;
                case idDP_StarlingEuroISA:
                    createPayees(idPY_Starling);
                    theDepositBuilder.name(myDeposit).parent(idPY_Starling)
                            .category(MoneyWiseDataTestCategories.idDC_TaxFreeSavings)
                            .currency(MoneyWiseCurrencyClass.EUR).openingBalance("5000").build();
                    break;
                case idDP_StarlingDollar:
                    createPayees(idPY_Starling);
                    theDepositBuilder.name(myDeposit).parent(idPY_Starling)
                            .category(MoneyWiseDataTestCategories.idDC_Savings)
                            .currency(MoneyWiseCurrencyClass.USD).openingBalance("5000").build();
                    break;
                case idDP_FundingCircleLoans:
                    createPayees(idPY_FundingCircle);
                    theDepositBuilder.name(myDeposit).parent(idPY_FundingCircle)
                            .category(MoneyWiseDataTestCategories.idDC_Peer2Peer).build();
                    break;
                default:
                    throw new MoneyWiseDataException("Unexpected Deposit:- " + myDeposit);
            }

            /* Record in nameMap */
            theNameMap.put(myDeposit, MoneyWiseBasicDataType.DEPOSIT);
        }
    }

    /**
     * Create cash.
     * @param pCash the cash to create
     * @throws OceanusException on error
     */
    void createCash(final String ...pCash) throws OceanusException {
        for (final String myCash : pCash) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(myCash);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.CASH.equals(myExisting)){
                    continue;
                }
                throw new MoneyWiseLogicException("Cash name already exists");
            }

            /* Create the cash */
            switch (myCash) {
                case idCS_Cash:
                    createPayees(idPY_CashExpense);
                    theCashBuilder.name(myCash).category(MoneyWiseDataTestCategories.idCC_Cash)
                            .autoExpense(MoneyWiseDataTestCategories.idTC_ExpCash, idPY_CashExpense).build();
                    break;
                case idCS_EurosCash:
                    createPayees(idPY_CashExpense);
                    theCashBuilder.name(myCash).category(MoneyWiseDataTestCategories.idCC_Cash).currency(MoneyWiseCurrencyClass.EUR)
                            .autoExpense(MoneyWiseDataTestCategories.idTC_ExpCash, idPY_CashExpense).build();
                    break;
                case idCS_CashWallet:
                    theCashBuilder.name(myCash).category(MoneyWiseDataTestCategories.idCC_Wallet)
                            .openingBalance("10").build();
                    break;
                case idCS_EurosWallet:
                    theCashBuilder.name(myCash).category(MoneyWiseDataTestCategories.idCC_Wallet).currency(MoneyWiseCurrencyClass.EUR).build();
                    break;
                default:
                    throw new MoneyWiseDataException("Unexpected Cash:- " + myCash);
            }

            /* Record in nameMap */
            theNameMap.put(myCash, MoneyWiseBasicDataType.CASH);
        }
    }

    /**
     * Create loans.
     * @param pLoans the loans to create
     * @throws OceanusException on error
     */
    void createLoans(final String ...pLoans) throws OceanusException {
        for (final String myLoan : pLoans) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(myLoan);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.LOAN.equals(myExisting)){
                    continue;
                }
                throw new MoneyWiseLogicException("Loan name already exists");
            }

            /* Create the loan */
            switch (myLoan) {
                case idLN_Barclaycard:
                    createPayees(idPY_Barclays);
                    theLoanBuilder.name(myLoan).parent(idPY_Barclays).category(MoneyWiseDataTestCategories.idLC_CreditCards)
                            .openingBalance("-100.00").build();
                    break;
                case idLN_BarclaysMortgage:
                    createPayees(idPY_Barclays);
                    theLoanBuilder.name(myLoan).parent(idPY_Barclays).category(MoneyWiseDataTestCategories.idLC_Mortgage).build();
                    break;
                case idLN_DeferredTax:
                    theLoanBuilder.name(myLoan).parent(idPY_HMRC).category(MoneyWiseDataTestCategories.idLC_Pending).build();
                    break;
                case idLN_DamageLoan:
                    createPayees(idPY_Damage);
                    theLoanBuilder.name(myLoan).parent(idPY_Damage).category(MoneyWiseDataTestCategories.idLC_Private).build();
                    break;
                default:
                    throw new MoneyWiseDataException("Unexpected Loan:- " + myLoan);
            }

            /* Record in nameMap */
            theNameMap.put(myLoan, MoneyWiseBasicDataType.LOAN);
        }
    }

    /**
     * Create portfolios.
     * @param pPortfolios the portfolios to create
     * @throws OceanusException on error
     */
    void createPortfolios(final String ...pPortfolios) throws OceanusException {
        for (final String myPortfolio : pPortfolios) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(myPortfolio);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.PORTFOLIO.equals(myExisting)){
                    continue;
                }
                throw new MoneyWiseLogicException("Portfolio name already exists");
            }

            /* Create the portfolio */
            switch (myPortfolio) {
                case idPF_InteractiveInvestorStock:
                    createPayees(idPY_InteractiveInvestor);
                    thePortfolioBuilder.name(myPortfolio).parent(idPY_InteractiveInvestor).type(MoneyWisePortfolioClass.STANDARD).build();
                    break;
                case idPF_InteractiveInvestorISA:
                    createPayees(idPY_InteractiveInvestor);
                    thePortfolioBuilder.name(myPortfolio).parent(idPY_InteractiveInvestor).type(MoneyWisePortfolioClass.TAXFREE).build();
                    break;
                case idPF_InteractiveInvestorSIPP:
                    createPayees(idPY_InteractiveInvestor);
                    thePortfolioBuilder.name(myPortfolio).parent(idPY_InteractiveInvestor).type(MoneyWisePortfolioClass.SIPP).build();
                    break;
                case idPF_AJBellStock:
                    createPayees(idPY_AJBell);
                    thePortfolioBuilder.name(myPortfolio).parent(idPY_AJBell).type(MoneyWisePortfolioClass.STANDARD).build();
                    break;
                case idPF_Assets:
                    thePortfolioBuilder.name(myPortfolio).parent(idPY_AssetHolder).type(MoneyWisePortfolioClass.STANDARD).build();
                    break;
                case idPF_Residence:
                    thePortfolioBuilder.name(myPortfolio).parent(idPY_AssetHolder).type(MoneyWisePortfolioClass.TAXFREE).build();
                    break;
                case idPF_Pensions:
                    thePortfolioBuilder.name(myPortfolio).parent(idPY_Government).type(MoneyWisePortfolioClass.PENSION).build();
                    break;
                default:
                    throw new MoneyWiseDataException("Unexpected Portfolio:- " + myPortfolio);
            }

            /* Record in nameMap */
            theNameMap.put(myPortfolio, MoneyWiseBasicDataType.PORTFOLIO);
        }
    }

    /**
     * Create securities.
     * @param pSecurities the securities to create
     * @throws OceanusException on error
     */
    void createSecurities(final String ...pSecurities) throws OceanusException {
        for (final String mySecurity : pSecurities) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(mySecurity);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.SECURITY.equals(myExisting)){
                    continue;
                }
                throw new MoneyWiseLogicException("Security name already exists");
            }

            /* Create the security */
            switch (mySecurity) {
                case idSC_BarclaysShares:
                    createPayees(idPY_Barclays);
                    theSecurityBuilder.name(mySecurity).parent(idPY_Barclays).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("BARC.L").build();
                    break;
                case idSC_LloydsShares:
                    createPayees(idPY_Lloyds);
                    theSecurityBuilder.name(mySecurity).parent(idPY_Lloyds).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("LLOY.L").build();
                    break;
                case idSC_HalifaxShares:
                    createPayees(idPY_Halifax);
                    theSecurityBuilder.name(mySecurity).parent(idPY_Halifax).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("HLFX.L").build();
                    break;
                case idSC_TSBShares:
                    createPayees(idPY_TSB);
                    theSecurityBuilder.name(mySecurity).parent(idPY_TSB).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("TSB.L").build();
                    break;
                case idSC_StarlingShares:
                    createPayees(idPY_Starling);
                    theSecurityBuilder.name(mySecurity).parent(idPY_Starling).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("STAR.L").build();
                    break;
                case idSC_BarclaysSharesUS:
                    createPayees(idPY_Barclays);
                    theSecurityBuilder.name(mySecurity).parent(idPY_Barclays).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("BARC.NY").currency(MoneyWiseCurrencyClass.USD).build();
                    break;
                case idSC_LloydsSharesUS:
                    createPayees(idPY_Lloyds);
                    theSecurityBuilder.name(mySecurity).parent(idPY_Lloyds).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("LLOY.NY").currency(MoneyWiseCurrencyClass.USD).build();
                    break;
                case idSC_HalifaxSharesUS:
                    createPayees(idPY_Halifax);
                    theSecurityBuilder.name(mySecurity).parent(idPY_Halifax).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("HLFX.NY").currency(MoneyWiseCurrencyClass.USD).build();
                    break;
                case idSC_TSBSharesUS:
                    createPayees(idPY_TSB);
                    theSecurityBuilder.name(mySecurity).parent(idPY_TSB).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("TSB.NY").currency(MoneyWiseCurrencyClass.USD).build();
                    break;
                case idSC_StarlingSharesUS:
                    createPayees(idPY_Starling);
                    theSecurityBuilder.name(mySecurity).parent(idPY_Starling).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("STAR.NY").currency(MoneyWiseCurrencyClass.USD).build();
                    break;
                case idSC_FordEscort:
                    theSecurityBuilder.name(mySecurity).parent(idPY_AssetHolder).type(MoneyWiseSecurityClass.VEHICLE).build();
                    break;
                case idSC_Mazda6:
                    theSecurityBuilder.name(mySecurity).parent(idPY_AssetHolder).type(MoneyWiseSecurityClass.VEHICLE).build();
                    break;
                case idSC_12MainSt:
                    theSecurityBuilder.name(mySecurity).parent(idPY_AssetHolder).type(MoneyWiseSecurityClass.PROPERTY).build();
                    break;
                case idSC_56HighSt:
                    theSecurityBuilder.name(mySecurity).parent(idPY_AssetHolder).type(MoneyWiseSecurityClass.PROPERTY).build();
                    break;
                case idSC_41KiteSt:
                    theSecurityBuilder.name(mySecurity).parent(idPY_AssetHolder).type(MoneyWiseSecurityClass.PROPERTY).build();
                    break;
                case idSC_StdLifePolicy:
                    theSecurityBuilder.name(mySecurity).parent(idPY_StandardLife).type(MoneyWiseSecurityClass.ENDOWMENT).build();
                    break;
                case idSC_StdLifeBond:
                    theSecurityBuilder.name(mySecurity).parent(idPY_StandardLife).type(MoneyWiseSecurityClass.LIFEBOND).build();
                    break;
                case idSC_IBMPension:
                    theSecurityBuilder.name(mySecurity).parent(idPY_IBM).type(MoneyWiseSecurityClass.DEFINEDBENEFIT).build();
                    break;
                case idSC_IBMAvc:
                    theSecurityBuilder.name(mySecurity).parent(idPY_StandardLife).type(MoneyWiseSecurityClass.DEFINEDCONTRIBUTION).build();
                    break;
                case idSC_StatePension:
                    theSecurityBuilder.name(mySecurity).parent(idPY_Government).type(MoneyWiseSecurityClass.STATEPENSION).build();
                    break;
                default:
                    throw new MoneyWiseDataException("Unexpected Security:- " + mySecurity);
            }

            /* Record in nameMap */
            theNameMap.put(mySecurity, MoneyWiseBasicDataType.SECURITY);
        }
    }

    /**
     * Create secPrice.
     * @param pSecurity the currency
     * @param pDate the date
     * @param pPrice the price
     * @throws OceanusException on error
     */
    void createSecPrice(final String pSecurity,
                        final String pDate,
                        final String pPrice) throws OceanusException {
        /* Check for existing price */
        final Map<String, String> myMap = thePriceMap.computeIfAbsent(pSecurity, c -> new HashMap<>());
        final String myExisting = myMap.get(pDate);
        if (myExisting != null) {
            if (myExisting.equals(pPrice)) {
                return;
            }
            throw new MoneyWiseLogicException("Conflicting securityPrice");
        }

        /* Create new rate */
        theSecurityPriceBuilder.security(pSecurity).date(pDate).price(pPrice).build();
        myMap.put(pDate, pPrice);
    }

    /**
     * Create xchgRate.
     * @param pCurrency the currency
     * @param pDate the date
     * @param pRate the rate
     * @throws OceanusException on error
     */
    void createXchgRate(final MoneyWiseCurrencyClass pCurrency,
                        final String pDate,
                        final String pRate) throws OceanusException {
        /* Check for existing rate */
        final Map<String, String> myMap = theRateMap.computeIfAbsent(pCurrency, c -> new HashMap<>());
        final String myExisting = myMap.get(pDate);
        if (myExisting != null) {
            if (myExisting.equals(pRate)) {
                return;
            }
            throw new MoneyWiseLogicException("Conflicting xchgRate");
        }

        /* Create new rate */
        theXchgRateBuilder.currency(pCurrency).date(pDate).rate(pRate).build();
        myMap.put(pDate, pRate);
    }

    /**
     * Reset data.
     * @throws OceanusException on error
     */
    public void resetData() throws OceanusException {
        /* Clear all exchangeRate details */
        theDataSet.getExchangeRates().clear();
        theDataSet.getExchangeRates().updateMaps();

        /* Clear all securityPrice details */
        theDataSet.getSecurityPrices().clear();
        theDataSet.getSecurityPrices().updateMaps();

        /* Clear all depositRate details */
        theDataSet.getDepositRates().clear();
        theDataSet.getDepositRates().updateMaps();

        /* Clear all payee details */
        theDataSet.getPayeeInfo().clear();
        theDataSet.getPayees().clear();
        theDataSet.getPayees().updateMaps();

        /* Clear all deposit details */
        theDataSet.getDepositInfo().clear();
        theDataSet.getDeposits().clear();
        theDataSet.getDeposits().updateMaps();

        /* Clear all cash details */
        theDataSet.getCashInfo().clear();
        theDataSet.getCash().clear();
        theDataSet.getCash().updateMaps();

        /* Clear all loan details */
        theDataSet.getLoanInfo().clear();
        theDataSet.getLoans().clear();
        theDataSet.getLoans().updateMaps();

        /* Clear all portfolio details */
        theDataSet.getPortfolioInfo().clear();
        theDataSet.getPortfolios().clear();
        theDataSet.getPortfolios().updateMaps();

        /* Clear all security details */
        theDataSet.getSecurityInfo().clear();
        theDataSet.getSecurities().clear();
        theDataSet.getSecurities().updateMaps();

        /* Clear all transaction details */
        theDataSet.getTransactions().clear();
        theDataSet.getTransactionInfo().clear();

        /* Clear maps */
        theNameMap.clear();
        thePriceMap.clear();
        theRateMap.clear();

        /* recreate the base accounts */
        createBaseAccounts();
    }
}
