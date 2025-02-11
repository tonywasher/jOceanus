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
    final static String idPY_Nationwide = "Nationwide";
    final static String idPY_Starling = "Starling";
    final static String idPY_InteractiveInvestor = "InteractiveInvestor";
    final static String idPY_FundingCircle = "FundingCircle";
    final static String idPY_BallCorp = "BallCorp";
    final static String idPY_IBM = "IBM";
    final static String idPY_Petrol = "Petrol";
    final static String idPY_Parking = "Parking";
    final static String idPY_ASDA = "ASDA";
    final static String idPY_CoOp = "CoOp";
    final static String idPY_Tesco = "Tesco";
    final static String idPY_Potters = "PottersHeron";
    final static String idPY_Government = "Government";
    final static String idPY_HMRC = "InlandRevenue";
    final static String idPY_Market = "Market";
    final static String idPY_CashExpense = "CashExpense";
    final static String idPY_Damage = "Damage";
    final static String idPY_Parents = "Parents";

    /**
     * Deposit ids.
     */
    final static String idDP_BarclaysCurrent = "BarclaysCurrent";
    final static String idDP_NatWideFlexDirect = "NatWideFlexDirect";
    final static String idDP_NatWideLoyalty = "NatWideLoyalty";
    final static String idDP_StarlingSterling = "StarlingSterling";
    final static String idDP_StarlingEuro = "StarlingEuro";
    final static String idDP_StarlingDollar = "StarlingDollar";
    final static String idDP_FundingCircleLoans = "FundingCircleLoans";

    /**
     * Cash ids.
     */
    final static String idCS_Cash = "Cash";
    final static String idCS_EurosCash = "EurosCash";

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
    final static String idPF_InteractiveInvestorStock = "InteractiveInvestorStock";
    final static String idPF_Pensions = "Pensions";

    /**
     * Security ids.
     */
    final static String idSC_BarclaysShares = "BarclaysShares";
    final static String idSC_BallShares = "BallShares";
    final static String idSC_StatePension = "StatePension";

    /**
     * Security holding ids.
     */
    final static String idSH_BarclaysShares = idPF_InteractiveInvestorStock + ":" + idSC_BarclaysShares;
    final static String idSH_BallShares = idPF_InteractiveInvestorStock + ":" + idSC_BallShares;

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
    MoneyWiseDataTestAccounts(final MoneyWiseDataSet pDataSet) throws OceanusException {
        /* Store the dataSet */
        theDataSet = pDataSet;

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

        /* Create the static and categories */
        final MoneyWiseDataTestCategories myCategories = new MoneyWiseDataTestCategories(theDataSet);
        myCategories.buildBasic();

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
                case idPY_BallCorp:
                case idPY_InteractiveInvestor:
                case idPY_FundingCircle:
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
                case idPY_CoOp:
                case idPY_Tesco:
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
                case idDP_StarlingSterling:
                    createPayees(idPY_Starling);
                    theDepositBuilder.name(myDeposit).parent(idPY_Starling)
                            .category(MoneyWiseDataTestCategories.idDC_Current).openingBalance("10000").build();
                    break;
                case idDP_StarlingEuro:
                    createPayees(idPY_Starling);
                    theDepositBuilder.name(myDeposit).parent(idPY_Starling)
                            .category(MoneyWiseDataTestCategories.idDC_Savings)
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
                    theCashBuilder.name(myCash).category(MoneyWiseDataTestCategories.idCC_Cash)
                            .autoExpense(MoneyWiseDataTestCategories.idTC_ExpCash, idPY_CashExpense).build();
                    break;
                case idCS_EurosCash:
                    theCashBuilder.name(myCash).category(MoneyWiseDataTestCategories.idCC_Cash).currency(MoneyWiseCurrencyClass.EUR)
                            .autoExpense(MoneyWiseDataTestCategories.idTC_ExpCash, idPY_CashExpense).build();
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
                    theLoanBuilder.name(myLoan).parent(idPY_Barclays).category(MoneyWiseDataTestCategories.idLC_CreditCards).build();
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
                    thePortfolioBuilder.name(myPortfolio).parent(idPY_InteractiveInvestor).type(MoneyWisePortfolioClass.TAXFREE).build();
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
                    theSecurityBuilder.name(mySecurity).parent(idPY_Barclays).type(MoneyWiseSecurityClass.SHARES).symbol("BARC.L").build();
                    break;
                case idSC_BallShares:
                    createPayees(idPY_BallCorp);
                    theSecurityBuilder.name(mySecurity).parent(idPY_BallCorp).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("BALL.NY").currency(MoneyWiseCurrencyClass.USD).build();
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
    void resetData() throws OceanusException {
        /* Clear all exchangeRate details */
        theDataSet.getExchangeRates().clear();

        /* Clear all securityPrice details */
        theDataSet.getSecurityPrices().clear();

        /* Clear all depositRate details */
        theDataSet.getDepositRates().clear();

        /* Clear all payee details */
        theDataSet.getPayees().clear();
        theDataSet.getPayeeInfo().clear();

        /* Clear all deposit details */
        theDataSet.getDeposits().clear();
        theDataSet.getDepositInfo().clear();

        /* Clear all cash details */
        theDataSet.getCash().clear();
        theDataSet.getCashInfo().clear();

        /* Clear all loan details */
        theDataSet.getLoans().clear();
        theDataSet.getLoanInfo().clear();

        /* Clear all portfolio details */
        theDataSet.getPortfolios().clear();
        theDataSet.getPortfolioInfo().clear();

        /* Clear all security details */
        theDataSet.getSecurities().clear();
        theDataSet.getSecurityInfo().clear();

        /* Clear all transaction details */
        theDataSet.getTransactions().clear();
        theDataSet.getTransactionInfo().clear();

        /* recreate the base accounts */
        createBaseAccounts();
    }
}
