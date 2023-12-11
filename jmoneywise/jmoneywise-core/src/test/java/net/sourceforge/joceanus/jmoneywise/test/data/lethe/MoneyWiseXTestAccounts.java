/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.test.data.lethe;

import net.sourceforge.joceanus.jmoneywise.lethe.data.builder.MoneyWiseXCashBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.builder.MoneyWiseXDepositBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.builder.MoneyWiseXLoanBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.builder.MoneyWiseXPayeeBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.builder.MoneyWiseXPortfolioBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.builder.MoneyWiseXSecurityBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.builder.MoneyWiseXSecurityPriceBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrencyClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Accounts Builder.
 */
public class MoneyWiseXTestAccounts {
    /**
     * Payee ids.
     */
    final static String idPY_Barclays = "Barclays";
    final static String idPY_Nationwide = "Nationwide";
    final static String idPY_Starling = "Starling";
    final static String idPY_InteractiveInvestor = "InteractiveInvestor";
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

    /**
     * Deposit ids.
     */
    final static String idDP_BarclaysCurrent = "BarclaysCurrent";
    final static String idDP_NatWideFlexDirect = "NatWideFlexDirect";
    final static String idDP_NatWideLoyalty = "NatWideLoyalty";
    final static String idDP_StarlingSterling = "StarlingSterling";
    final static String idDP_StarlingEuro = "StarlingEuro";

    /**
     * Cash ids.
     */
    final static String idCS_Cash = "Cash";

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

    /**
     * Security ids.
     */
    final static String idSC_BarclaysShares = "BarclaysShares";
    final static String idSC_BallShares = "BallShares";

    /**
     * PayeeBuilder.
     */
    private final MoneyWiseXPayeeBuilder thePayeeBuilder;

    /**
     * DepositBuilder.
     */
    private final MoneyWiseXDepositBuilder theDepositBuilder;

    /**
     * CashBuilder.
     */
    private final MoneyWiseXCashBuilder theCashBuilder;

    /**
     * LoanBuilder.
     */
    private final MoneyWiseXLoanBuilder theLoanBuilder;

    /**
     * PortfolioBuilder.
     */
    private final MoneyWiseXPortfolioBuilder thePortfolioBuilder;

    /**
     * SecurityBuilder.
     */
    private final MoneyWiseXSecurityBuilder theSecurityBuilder;

    /**
     * SecurityPriceBuilder.
     */
    private final MoneyWiseXSecurityPriceBuilder theSecurityPriceBuilder;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    MoneyWiseXTestAccounts(final MoneyWiseData pDataSet) {
        /* Create the builders */
        thePayeeBuilder = new MoneyWiseXPayeeBuilder(pDataSet);
        theDepositBuilder = new MoneyWiseXDepositBuilder(pDataSet);
        theCashBuilder = new MoneyWiseXCashBuilder(pDataSet);
        theLoanBuilder = new MoneyWiseXLoanBuilder(pDataSet);
        thePortfolioBuilder = new MoneyWiseXPortfolioBuilder(pDataSet);
        theSecurityBuilder = new MoneyWiseXSecurityBuilder(pDataSet);
        theSecurityPriceBuilder = new MoneyWiseXSecurityPriceBuilder(pDataSet);
    }

    /**
     * Create accounts.
     * @throws OceanusException on error
     */
    public void createAccounts() throws OceanusException {
        createPayees();
        createDeposits();
        createCash();
        createLoans();
        createPortfolios();
        createSecurities();
        createSecurityPrices();
    }

    /**
     * Create payees.
     * @throws OceanusException on error
     */
    private void createPayees() throws OceanusException {
        thePayeeBuilder.name(idPY_Barclays).type(PayeeTypeClass.INSTITUTION).build();
        thePayeeBuilder.name(idPY_Nationwide).type(PayeeTypeClass.INSTITUTION).build();
        thePayeeBuilder.name(idPY_Starling).type(PayeeTypeClass.INSTITUTION).build();
        thePayeeBuilder.name(idPY_BallCorp).type(PayeeTypeClass.INSTITUTION).build();
        thePayeeBuilder.name(idPY_InteractiveInvestor).type(PayeeTypeClass.INSTITUTION).build();
        thePayeeBuilder.name(idPY_Government).type(PayeeTypeClass.GOVERNMENT).build();
        thePayeeBuilder.name(idPY_HMRC).type(PayeeTypeClass.TAXMAN).build();
        thePayeeBuilder.name(idPY_Market).type(PayeeTypeClass.MARKET).build();
        thePayeeBuilder.name(idPY_IBM).type(PayeeTypeClass.EMPLOYER).build();
        thePayeeBuilder.name(idPY_Damage).type(PayeeTypeClass.INDIVIDUAL).build();
        thePayeeBuilder.name(idPY_ASDA).type(PayeeTypeClass.PAYEE).build();
        thePayeeBuilder.name(idPY_CoOp).type(PayeeTypeClass.PAYEE).build();
        thePayeeBuilder.name(idPY_Tesco).type(PayeeTypeClass.PAYEE).build();
        thePayeeBuilder.name(idPY_Petrol).type(PayeeTypeClass.PAYEE).build();
        thePayeeBuilder.name(idPY_Parking).type(PayeeTypeClass.PAYEE).build();
        thePayeeBuilder.name(idPY_Potters).type(PayeeTypeClass.PAYEE).build();
        thePayeeBuilder.name(idPY_CashExpense).type(PayeeTypeClass.PAYEE).build();
    }

    /**
     * Create deposits.
     * @throws OceanusException on error
     */
    private void createDeposits() throws OceanusException {
        theDepositBuilder.name(idDP_BarclaysCurrent).parent(idPY_Barclays).category(MoneyWiseXTestCategories.idDC_Current).openingBalance("10000").build();
        theDepositBuilder.name(idDP_NatWideFlexDirect).parent(idPY_Nationwide).category(MoneyWiseXTestCategories.idDC_Current).openingBalance("10000").build();
        theDepositBuilder.name(idDP_NatWideLoyalty).parent(idPY_Nationwide).category(MoneyWiseXTestCategories.idDC_Savings).openingBalance("10000").build();
        theDepositBuilder.name(idDP_StarlingSterling).parent(idPY_Starling).category(MoneyWiseXTestCategories.idDC_Current).openingBalance("10000").build();
        theDepositBuilder.name(idDP_StarlingEuro).parent(idPY_Starling).category(MoneyWiseXTestCategories.idDC_Current).currency(AssetCurrencyClass.EUR).build();
    }

    /**
     * Create cash.
     * @throws OceanusException on error
     */
    private void createCash() throws OceanusException {
        theCashBuilder.name(idCS_Cash).category(MoneyWiseXTestCategories.idCC_Cash).autoExpense(MoneyWiseXTestCategories.idTC_ExpCash, idPY_CashExpense).build();
    }

    /**
     * Create loans.
     * @throws OceanusException on error
     */
    private void createLoans() throws OceanusException {
        theLoanBuilder.name(idLN_Barclaycard).parent(idPY_Barclays).category(MoneyWiseXTestCategories.idLC_CreditCards).build();
        theLoanBuilder.name(idLN_BarclaysMortgage).parent(idPY_Barclays).category(MoneyWiseXTestCategories.idLC_Mortgage).build();
        theLoanBuilder.name(idLN_DeferredTax).parent(idPY_HMRC).category(MoneyWiseXTestCategories.idLC_Pending).build();
        theLoanBuilder.name(idLN_DamageLoan).parent(idPY_Damage).category(MoneyWiseXTestCategories.idLC_Private).build();
    }

    /**
     * Create portfolios.
     * @throws OceanusException on error
     */
    private void createPortfolios() throws OceanusException {
        thePortfolioBuilder.name(idPF_InteractiveInvestorStock).parent(idPY_InteractiveInvestor).type(PortfolioTypeClass.TAXFREE).build();
    }

    /**
     * Create securities.
     * @throws OceanusException on error
     */
    private void createSecurities() throws OceanusException {
        theSecurityBuilder.name(idSC_BarclaysShares).parent(idPY_Barclays).type(SecurityTypeClass.SHARES).symbol("BARC.L").build();
        theSecurityBuilder.name(idSC_BallShares).parent(idPY_BallCorp).type(SecurityTypeClass.SHARES).symbol("BALL.NY").currency(AssetCurrencyClass.USD).build();
    }

    /**
     * Create securityPrices.
     * @throws OceanusException on error
     */
    private void createSecurityPrices() throws OceanusException {
        theSecurityPriceBuilder.security(idSC_BarclaysShares).date("01-Jun-1980").price("5.00").build();
        theSecurityPriceBuilder.security(idSC_BallShares).date("01-Jun-1980").price("15.00").build();
        theSecurityPriceBuilder.security(idSC_BarclaysShares).date("01-Jun-2010").price("10.00").build();
        theSecurityPriceBuilder.security(idSC_BallShares).date("01-Jun-2010").price("25.00").build();
    }
}
