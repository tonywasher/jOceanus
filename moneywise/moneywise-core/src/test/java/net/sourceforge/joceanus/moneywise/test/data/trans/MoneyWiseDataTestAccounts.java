/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
    static final String IDPY_BARCLAYS = "Barclays";
    static final String IDPY_LLOYDS = "Lloyds";
    static final String IDPY_TSB = "TSB";
    static final String IDPY_HALIFAX = "Halifax";
    static final String IDPY_NATIONWIDE = "Nationwide";
    static final String IDPY_STARLING = "Starling";
    static final String IDPY_INTERACTIVE_INVESTOR = "InteractiveInvestor";
    static final String IDPY_AJ_BELL = "AJBell";
    static final String IDPY_STANDARD_LIFE = "StandardLife";
    static final String IDPY_FUNDING_CIRCLE = "FundingCircle";
    static final String IDPY_IBM = "IBM";
    static final String IDPY_PETROL = "Petrol";
    static final String IDPY_PARKING = "Parking";
    static final String IDPY_ASDA = "ASDA";
    static final String IDPY_POTTERS = "PottersHeron";
    static final String IDPY_GOVERNMENT = "Government";
    static final String IDPY_HMRC = "InlandRevenue";
    static final String IDPY_MARKET = "Market";
    static final String IDPY_CASH_EXPENSE = "CashExpense";
    static final String IDPY_DAMAGE = "Damage";
    static final String IDPY_PARENTS = "Parents";
    static final String IDPY_ASSET_HOLDER = "AssetHolder";

    /**
     * Deposit ids.
     */
    static final String IDDP_BARCLAYS_CURRENT = "BarclaysCurrent";
    static final String IDDP_NAT_WIDE_FLEX_DIRECT = "NatWideFlexDirect";
    static final String IDDP_NAT_WIDE_LOYALTY = "NatWideLoyalty";
    static final String IDDP_NAT_WIDE_ISA = "NatWideISA";
    static final String IDDP_STARLING_STERLING = "StarlingSterling";
    static final String IDDP_STARLING_EURO = "StarlingEuro";
    static final String IDDP_STARLING_EURO_ISA = "StarlingEuroISA";
    static final String IDDP_STARLING_DOLLAR = "StarlingDollar";
    static final String IDDP_FUNDING_CIRCLE_LOANS = "FundingCircleLoans";

    /**
     * Cash ids.
     */
    static final String IDCS_CASH = "Cash";
    static final String IDCS_EUROS_CASH = "EurosCash";
    static final String IDCS_CASH_WALLET = "CashWallet";
    static final String IDCS_EUROS_WALLET = "EurosWallet";

    /**
     * Loan ids.
     */
    static final String IDLN_BARCLAYCARD = "Barclaycard";
    static final String IDLN_DAMAGE_LOAN = "DamageLoan";
    static final String IDLN_BARCLAYS_MORTGAGE = "BarclaysMortgage";
    static final String IDLN_DEFERRED_TAX = "DeferredTax";

    /**
     * Portfolio ids.
     */
    static final String IDPF_AJ_BELL_STOCK = "AJBellStock";
    static final String IDPF_INTERACTIVE_INVESTOR_STOCK = "InteractiveInvestorStock";
    static final String IDPF_INTERACTIVE_INVESTOR_ISA = "InteractiveInvestorISA";
    static final String IDPF_INTERACTIVE_INVESTOR_SIPP = "InteractiveInvestorSIPP";
    static final String IDPF_ASSETS = "Assets";
    static final String IDPF_RESIDENCE = "Residence";
    static final String IDPF_PENSIONS = "Pensions";

    /**
     * Security ids.
     */
    static final String IDSC_BARCLAYS_SHARES = "BarclaysShares";
    static final String IDSC_LLOYDS_SHARES = "LloydsShares";
    static final String IDSC_HALIFAX_SHARES = "HalifaxShares";
    static final String IDSC_TSB_SHARES = "TSBShares";
    static final String IDSC_STARLING_SHARES = "StarlingShares";
    static final String IDSC_BARCLAYS_SHARES_US = "BarclaysSharesUS";
    static final String IDSC_LLOYDS_SHARES_US = "LloydsSharesUS";
    static final String IDSC_HALIFAX_SHARES_US = "HalifaxSharesUS";
    static final String IDSC_TSB_SHARES_US = "TSBSharesUS";
    static final String IDSC_STARLING_SHARES_US = "StarlingSharesUS";
    static final String IDSC_FORD_ESCORT = "FordEscort";
    static final String IDSC_MAZDA_6 = "Mazda6";
    static final String IDSC_12_MAIN_ST = "12MainSt";
    static final String IDSC_56_HIGH_ST = "56HighSt";
    static final String IDSC_41_KITE_ST = "41KiteSt";
    static final String IDSC_IBM_PENSION = "IBMPension";
    static final String IDSC_IBM_AVC = "IBMAvc";
    static final String IDSC_STD_LIFE_POLICY = "StdLifePolicy";
    static final String IDSC_STD_LIFE_BOND = "StdLifeBond";
    static final String IDSC_STATE_PENSION = "StatePension";

    /**
     * Security holding ids.
     */
    static final String IDSH_BARCLAYS_SHARES = IDPF_INTERACTIVE_INVESTOR_STOCK + ":" + IDSC_BARCLAYS_SHARES;
    static final String IDSH_LLOYDS_SHARES = IDPF_INTERACTIVE_INVESTOR_STOCK + ":" + IDSC_LLOYDS_SHARES;
    static final String IDSH_LLOYDS_SHARES_ISA = IDPF_INTERACTIVE_INVESTOR_ISA + ":" + IDSC_LLOYDS_SHARES;
    static final String IDSH_HALIFAX_SHARES = IDPF_INTERACTIVE_INVESTOR_STOCK + ":" + IDSC_HALIFAX_SHARES;
    static final String IDSH_HALIFAX_SHARES_US = IDPF_INTERACTIVE_INVESTOR_STOCK + ":" + IDSC_HALIFAX_SHARES_US;
    static final String IDSH_TSB_SHARES = IDPF_INTERACTIVE_INVESTOR_STOCK + ":" + IDSC_TSB_SHARES;
    static final String IDSH_STARLING_SHARES = IDPF_INTERACTIVE_INVESTOR_STOCK + ":" + IDSC_STARLING_SHARES;

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
        createPayees(IDPY_GOVERNMENT, IDPY_HMRC, IDPY_MARKET);

        /* Create the standard Pension portfolio */
        createPortfolios(IDPF_PENSIONS);

        /* Create the standard StatePension security */
        createSecurities(IDSC_STATE_PENSION);
    }

    /**
     * Create payees.
     * @param pPayees the payees to create
     * @throws OceanusException on error
     */
    void createPayees(final String... pPayees) throws OceanusException {
        for (final String myPayee : pPayees) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(myPayee);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.PAYEE.equals(myExisting)) {
                    continue;
                }
                throw new MoneyWiseLogicException("Payee name already exists");
            }

            /* Create the payee */
            switch (myPayee) {
                case IDPY_BARCLAYS:
                case IDPY_NATIONWIDE:
                case IDPY_STARLING:
                case IDPY_LLOYDS:
                case IDPY_HALIFAX:
                case IDPY_TSB:
                case IDPY_INTERACTIVE_INVESTOR:
                case IDPY_FUNDING_CIRCLE:
                case IDPY_AJ_BELL:
                case IDPY_STANDARD_LIFE:
                case IDPY_ASSET_HOLDER:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.INSTITUTION).build();
                    break;
                case IDPY_IBM:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.EMPLOYER).build();
                    break;
                case IDPY_GOVERNMENT:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.GOVERNMENT).build();
                    break;
                case IDPY_HMRC:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.TAXMAN).build();
                    break;
                case IDPY_MARKET:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.MARKET).build();
                    break;
                case IDPY_DAMAGE:
                case IDPY_PARENTS:
                    thePayeeBuilder.name(myPayee).type(MoneyWisePayeeClass.INDIVIDUAL).build();
                    break;
                case IDPY_ASDA:
                case IDPY_PETROL:
                case IDPY_PARKING:
                case IDPY_POTTERS:
                case IDPY_CASH_EXPENSE:
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
    void createDeposits(final String... pDeposits) throws OceanusException {
        for (final String myDeposit : pDeposits) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(myDeposit);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.DEPOSIT.equals(myExisting)) {
                    continue;
                }
                throw new MoneyWiseLogicException("Deposit name already exists");
            }

            /* Create the deposit */
            switch (myDeposit) {
                case IDDP_BARCLAYS_CURRENT:
                    createPayees(IDPY_BARCLAYS);
                    theDepositBuilder.name(myDeposit).parent(IDPY_BARCLAYS)
                            .category(MoneyWiseDataTestCategories.IDDC_CURRENT).openingBalance("10000").build();
                    break;
                case IDDP_NAT_WIDE_FLEX_DIRECT:
                    createPayees(IDPY_NATIONWIDE);
                    theDepositBuilder.name(myDeposit).parent(IDPY_NATIONWIDE)
                            .category(MoneyWiseDataTestCategories.IDDC_CURRENT).openingBalance("10000").build();
                    break;
                case IDDP_NAT_WIDE_LOYALTY:
                    createPayees(IDPY_NATIONWIDE);
                    theDepositBuilder.name(myDeposit).parent(IDPY_NATIONWIDE)
                            .category(MoneyWiseDataTestCategories.IDDC_SAVINGS).openingBalance("10000").build();
                    break;
                case IDDP_NAT_WIDE_ISA:
                    createPayees(IDPY_NATIONWIDE);
                    theDepositBuilder.name(myDeposit).parent(IDPY_NATIONWIDE)
                            .category(MoneyWiseDataTestCategories.IDDC_TAX_FREE_SAVINGS).openingBalance("10000").build();
                    break;
                case IDDP_STARLING_STERLING:
                    createPayees(IDPY_STARLING);
                    theDepositBuilder.name(myDeposit).parent(IDPY_STARLING)
                            .category(MoneyWiseDataTestCategories.IDDC_CURRENT).openingBalance("10000").build();
                    break;
                case IDDP_STARLING_EURO:
                    createPayees(IDPY_STARLING);
                    theDepositBuilder.name(myDeposit).parent(IDPY_STARLING)
                            .category(MoneyWiseDataTestCategories.IDDC_CURRENT)
                            .currency(MoneyWiseCurrencyClass.EUR).openingBalance("5000").build();
                    break;
                case IDDP_STARLING_EURO_ISA:
                    createPayees(IDPY_STARLING);
                    theDepositBuilder.name(myDeposit).parent(IDPY_STARLING)
                            .category(MoneyWiseDataTestCategories.IDDC_TAX_FREE_SAVINGS)
                            .currency(MoneyWiseCurrencyClass.EUR).openingBalance("5000").build();
                    break;
                case IDDP_STARLING_DOLLAR:
                    createPayees(IDPY_STARLING);
                    theDepositBuilder.name(myDeposit).parent(IDPY_STARLING)
                            .category(MoneyWiseDataTestCategories.IDDC_SAVINGS)
                            .currency(MoneyWiseCurrencyClass.USD).openingBalance("5000").build();
                    break;
                case IDDP_FUNDING_CIRCLE_LOANS:
                    createPayees(IDPY_FUNDING_CIRCLE);
                    theDepositBuilder.name(myDeposit).parent(IDPY_FUNDING_CIRCLE)
                            .category(MoneyWiseDataTestCategories.IDDC_PEER_2_PEER).build();
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
    void createCash(final String... pCash) throws OceanusException {
        for (final String myCash : pCash) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(myCash);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.CASH.equals(myExisting)) {
                    continue;
                }
                throw new MoneyWiseLogicException("Cash name already exists");
            }

            /* Create the cash */
            switch (myCash) {
                case IDCS_CASH:
                    createPayees(IDPY_CASH_EXPENSE);
                    theCashBuilder.name(myCash).category(MoneyWiseDataTestCategories.IDCC_CASH)
                            .autoExpense(MoneyWiseDataTestCategories.IDTC_EXP_CASH, IDPY_CASH_EXPENSE).build();
                    break;
                case IDCS_EUROS_CASH:
                    createPayees(IDPY_CASH_EXPENSE);
                    theCashBuilder.name(myCash).category(MoneyWiseDataTestCategories.IDCC_CASH).currency(MoneyWiseCurrencyClass.EUR)
                            .autoExpense(MoneyWiseDataTestCategories.IDTC_EXP_CASH, IDPY_CASH_EXPENSE).build();
                    break;
                case IDCS_CASH_WALLET:
                    theCashBuilder.name(myCash).category(MoneyWiseDataTestCategories.IDCC_WALLET)
                            .openingBalance("10").build();
                    break;
                case IDCS_EUROS_WALLET:
                    theCashBuilder.name(myCash).category(MoneyWiseDataTestCategories.IDCC_WALLET).currency(MoneyWiseCurrencyClass.EUR).build();
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
    void createLoans(final String... pLoans) throws OceanusException {
        for (final String myLoan : pLoans) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(myLoan);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.LOAN.equals(myExisting)) {
                    continue;
                }
                throw new MoneyWiseLogicException("Loan name already exists");
            }

            /* Create the loan */
            switch (myLoan) {
                case IDLN_BARCLAYCARD:
                    createPayees(IDPY_BARCLAYS);
                    theLoanBuilder.name(myLoan).parent(IDPY_BARCLAYS).category(MoneyWiseDataTestCategories.IDLC_CREDIT_CARDS)
                            .openingBalance("-100.00").build();
                    break;
                case IDLN_BARCLAYS_MORTGAGE:
                    createPayees(IDPY_BARCLAYS);
                    theLoanBuilder.name(myLoan).parent(IDPY_BARCLAYS).category(MoneyWiseDataTestCategories.IDLC_MORTGAGE).build();
                    break;
                case IDLN_DEFERRED_TAX:
                    theLoanBuilder.name(myLoan).parent(IDPY_HMRC).category(MoneyWiseDataTestCategories.IDLC_PENDING).build();
                    break;
                case IDLN_DAMAGE_LOAN:
                    createPayees(IDPY_DAMAGE);
                    theLoanBuilder.name(myLoan).parent(IDPY_DAMAGE).category(MoneyWiseDataTestCategories.IDLC_PRIVATE).build();
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
    void createPortfolios(final String... pPortfolios) throws OceanusException {
        for (final String myPortfolio : pPortfolios) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(myPortfolio);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.PORTFOLIO.equals(myExisting)) {
                    continue;
                }
                throw new MoneyWiseLogicException("Portfolio name already exists");
            }

            /* Create the portfolio */
            switch (myPortfolio) {
                case IDPF_INTERACTIVE_INVESTOR_STOCK:
                    createPayees(IDPY_INTERACTIVE_INVESTOR);
                    thePortfolioBuilder.name(myPortfolio).parent(IDPY_INTERACTIVE_INVESTOR).type(MoneyWisePortfolioClass.STANDARD).build();
                    break;
                case IDPF_INTERACTIVE_INVESTOR_ISA:
                    createPayees(IDPY_INTERACTIVE_INVESTOR);
                    thePortfolioBuilder.name(myPortfolio).parent(IDPY_INTERACTIVE_INVESTOR).type(MoneyWisePortfolioClass.TAXFREE).build();
                    break;
                case IDPF_INTERACTIVE_INVESTOR_SIPP:
                    createPayees(IDPY_INTERACTIVE_INVESTOR);
                    thePortfolioBuilder.name(myPortfolio).parent(IDPY_INTERACTIVE_INVESTOR).type(MoneyWisePortfolioClass.SIPP).build();
                    break;
                case IDPF_AJ_BELL_STOCK:
                    createPayees(IDPY_AJ_BELL);
                    thePortfolioBuilder.name(myPortfolio).parent(IDPY_AJ_BELL).type(MoneyWisePortfolioClass.STANDARD).build();
                    break;
                case IDPF_ASSETS:
                    thePortfolioBuilder.name(myPortfolio).parent(IDPY_ASSET_HOLDER).type(MoneyWisePortfolioClass.STANDARD).build();
                    break;
                case IDPF_RESIDENCE:
                    thePortfolioBuilder.name(myPortfolio).parent(IDPY_ASSET_HOLDER).type(MoneyWisePortfolioClass.TAXFREE).build();
                    break;
                case IDPF_PENSIONS:
                    thePortfolioBuilder.name(myPortfolio).parent(IDPY_GOVERNMENT).type(MoneyWisePortfolioClass.PENSION).build();
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
    void createSecurities(final String... pSecurities) throws OceanusException {
        for (final String mySecurity : pSecurities) {
            /* Check for already created */
            final MoneyWiseBasicDataType myExisting = theNameMap.get(mySecurity);
            if (myExisting != null) {
                if (MoneyWiseBasicDataType.SECURITY.equals(myExisting)) {
                    continue;
                }
                throw new MoneyWiseLogicException("Security name already exists");
            }

            /* Create the security */
            switch (mySecurity) {
                case IDSC_BARCLAYS_SHARES:
                    createPayees(IDPY_BARCLAYS);
                    theSecurityBuilder.name(mySecurity).parent(IDPY_BARCLAYS).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("BARC.L").build();
                    break;
                case IDSC_LLOYDS_SHARES:
                    createPayees(IDPY_LLOYDS);
                    theSecurityBuilder.name(mySecurity).parent(IDPY_LLOYDS).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("LLOY.L").build();
                    break;
                case IDSC_HALIFAX_SHARES:
                    createPayees(IDPY_HALIFAX);
                    theSecurityBuilder.name(mySecurity).parent(IDPY_HALIFAX).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("HLFX.L").build();
                    break;
                case IDSC_TSB_SHARES:
                    createPayees(IDPY_TSB);
                    theSecurityBuilder.name(mySecurity).parent(IDPY_TSB).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("TSB.L").build();
                    break;
                case IDSC_STARLING_SHARES:
                    createPayees(IDPY_STARLING);
                    theSecurityBuilder.name(mySecurity).parent(IDPY_STARLING).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("STAR.L").build();
                    break;
                case IDSC_BARCLAYS_SHARES_US:
                    createPayees(IDPY_BARCLAYS);
                    theSecurityBuilder.name(mySecurity).parent(IDPY_BARCLAYS).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("BARC.NY").currency(MoneyWiseCurrencyClass.USD).build();
                    break;
                case IDSC_LLOYDS_SHARES_US:
                    createPayees(IDPY_LLOYDS);
                    theSecurityBuilder.name(mySecurity).parent(IDPY_LLOYDS).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("LLOY.NY").currency(MoneyWiseCurrencyClass.USD).build();
                    break;
                case IDSC_HALIFAX_SHARES_US:
                    createPayees(IDPY_HALIFAX);
                    theSecurityBuilder.name(mySecurity).parent(IDPY_HALIFAX).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("HLFX.NY").currency(MoneyWiseCurrencyClass.USD).build();
                    break;
                case IDSC_TSB_SHARES_US:
                    createPayees(IDPY_TSB);
                    theSecurityBuilder.name(mySecurity).parent(IDPY_TSB).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("TSB.NY").currency(MoneyWiseCurrencyClass.USD).build();
                    break;
                case IDSC_STARLING_SHARES_US:
                    createPayees(IDPY_STARLING);
                    theSecurityBuilder.name(mySecurity).parent(IDPY_STARLING).type(MoneyWiseSecurityClass.SHARES)
                            .symbol("STAR.NY").currency(MoneyWiseCurrencyClass.USD).build();
                    break;
                case IDSC_FORD_ESCORT:
                    theSecurityBuilder.name(mySecurity).parent(IDPY_ASSET_HOLDER).type(MoneyWiseSecurityClass.VEHICLE).build();
                    break;
                case IDSC_MAZDA_6:
                    theSecurityBuilder.name(mySecurity).parent(IDPY_ASSET_HOLDER).type(MoneyWiseSecurityClass.VEHICLE).build();
                    break;
                case IDSC_12_MAIN_ST:
                    theSecurityBuilder.name(mySecurity).parent(IDPY_ASSET_HOLDER).type(MoneyWiseSecurityClass.PROPERTY).build();
                    break;
                case IDSC_56_HIGH_ST:
                    theSecurityBuilder.name(mySecurity).parent(IDPY_ASSET_HOLDER).type(MoneyWiseSecurityClass.PROPERTY).build();
                    break;
                case IDSC_41_KITE_ST:
                    theSecurityBuilder.name(mySecurity).parent(IDPY_ASSET_HOLDER).type(MoneyWiseSecurityClass.PROPERTY).build();
                    break;
                case IDSC_STD_LIFE_POLICY:
                    theSecurityBuilder.name(mySecurity).parent(IDPY_STANDARD_LIFE).type(MoneyWiseSecurityClass.ENDOWMENT).build();
                    break;
                case IDSC_STD_LIFE_BOND:
                    theSecurityBuilder.name(mySecurity).parent(IDPY_STANDARD_LIFE).type(MoneyWiseSecurityClass.LIFEBOND).build();
                    break;
                case IDSC_IBM_PENSION:
                    theSecurityBuilder.name(mySecurity).parent(IDPY_IBM).type(MoneyWiseSecurityClass.DEFINEDBENEFIT).build();
                    break;
                case IDSC_IBM_AVC:
                    theSecurityBuilder.name(mySecurity).parent(IDPY_STANDARD_LIFE).type(MoneyWiseSecurityClass.DEFINEDCONTRIBUTION).build();
                    break;
                case IDSC_STATE_PENSION:
                    theSecurityBuilder.name(mySecurity).parent(IDPY_GOVERNMENT).type(MoneyWiseSecurityClass.STATEPENSION).build();
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
