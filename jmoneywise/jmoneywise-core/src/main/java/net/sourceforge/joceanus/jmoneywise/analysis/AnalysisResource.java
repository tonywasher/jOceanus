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
package net.sourceforge.joceanus.jmoneywise.analysis;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for jMoneyWise Analysis Fields.
 */
public enum AnalysisResource implements ResourceId {
    /**
     * Analysis Name.
     */
    ANALYSIS_NAME("Analysis.Name"),

    /**
     * Analysis Analyser Name.
     */
    ANALYSIS_ANALYSER("Analysis.Analyser"),

    /**
     * Analysis Manager Name.
     */
    ANALYSIS_MANAGER("Analysis.Manager"),

    /**
     * Analysis TaxCalculation.
     */
    ANALYSIS_TAXCALC("Analysis.TaxCalc"),

    /**
     * Analysis Chargeable Events.
     */
    ANALYSIS_CHARGES("Analysis.Charges"),

    /**
     * Analysis Dilution Events.
     */
    ANALYSIS_DILUTIONS("Analysis.Dilutions"),

    /**
     * Analysis Totals.
     */
    ANALYSIS_TOTALS("Analysis.Totals"),

    /**
     * Bucket Account.
     */
    BUCKET_ACCOUNT("Bucket.Account"),

    /**
     * Bucket BaseValues.
     */
    BUCKET_BASEVALUES("Bucket.BaseValues"),

    /**
     * Bucket History.
     */
    BUCKET_HISTORY("Bucket.History"),

    /**
     * Bucket SnapShot.
     */
    BUCKET_SNAPSHOT("Bucket.SnapShot"),

    /**
     * Bucket Values.
     */
    BUCKET_VALUES("Bucket.Values"),

    /**
     * Bucket Previous Values.
     */
    BUCKET_PREVIOUS("Bucket.Previous"),

    /**
     * Filter All.
     */
    FILTER_ALL("Filter.All"),

    /**
     * TransTag Name.
     */
    TRANSTAG_NAME("TransTag.Name"),

    /**
     * TransTag List.
     */
    TRANSTAG_LIST("TransTag.List"),

    /**
     * Cash Name.
     */
    CASH_NAME("Cash.Name"),

    /**
     * Cash List.
     */
    CASH_LIST("Cash.List"),

    /**
     * CashCategory Name.
     */
    CASHCATEGORY_NAME("CashCategory.Name"),

    /**
     * CashCategory List.
     */
    CASHCATEGORY_LIST("CashCategory.List"),

    /**
     * Deposit Name.
     */
    DEPOSIT_NAME("Deposit.Name"),

    /**
     * Deposit List.
     */
    DEPOSIT_LIST("Deposit.List"),

    /**
     * DepositCategory Name.
     */
    DEPOSITCATEGORY_NAME("DepositCategory.Name"),

    /**
     * DepositCategory List.
     */
    DEPOSITCATEGORY_LIST("DepositCategory.List"),

    /**
     * Loan Name.
     */
    LOAN_NAME("Loan.Name"),

    /**
     * Loan List.
     */
    LOAN_LIST("Loan.List"),

    /**
     * Loan isCreditCard.
     */
    LOAN_CREDITCARD("Loan.isCreditCard"),

    /**
     * LoanCategory Name.
     */
    LOANCATEGORY_NAME("LoanCategory.Name"),

    /**
     * LoanCategory List.
     */
    LOANCATEGORY_LIST("LoanCategory.List"),

    /**
     * TransactionCategory Name.
     */
    TRANSCATEGORY_NAME("TransCategory.Name"),

    /**
     * TransactionCategory List.
     */
    TRANSCATEGORY_LIST("TransCategory.List"),

    /**
     * Payee Name.
     */
    PAYEE_NAME("Payee.Name"),

    /**
     * Payee List.
     */
    PAYEE_LIST("Payee.List"),

    /**
     * Portfolio Name.
     */
    PORTFOLIO_NAME("Portfolio.Name"),

    /**
     * Portfolio List.
     */
    PORTFOLIO_LIST("Portfolio.List"),

    /**
     * Portfolio Cash Name.
     */
    PORTFOLIOCASH_NAME("Portfolio.Cash.Name"),

    /**
     * Security Name.
     */
    SECURITY_NAME("Security.Name"),

    /**
     * Security List.
     */
    SECURITY_LIST("Security.List"),

    /**
     * TaxBasis Name.
     */
    TAXBASIS_NAME("TaxBasis.Name"),

    /**
     * TaxBasis List.
     */
    TAXBASIS_LIST("TaxBasis.List"),

    /**
     * TaxBasisAccount Name.
     */
    TAXBASIS_ACCOUNTNAME("TaxBasis.AccountName"),

    /**
     * TaxBasisAccount List.
     */
    TAXBASIS_ACCOUNTLIST("TaxBasis.AccountList"),

    /**
     * TaxCalc Name.
     */
    TAXCALC_NAME("TaxCalc.Name"),

    /**
     * TaxCalc List.
     */
    TAXCALC_LIST("TaxCalc.List"),

    /**
     * TaxCalc Section.
     */
    TAXCALC_SECTION("TaxCalc.Section"),

    /**
     * TaxCalc Parent.
     */
    TAXCALC_PARENT("TaxCalc.Parent"),

    /**
     * TaxCalc Slices.
     */
    TAXCALC_SLICES("TaxCalc.Slices"),

    /**
     * TaxCalc List.
     */
    TAXCALC_YEAR("TaxCalc.Year"),

    /**
     * TaxCalc Age.
     */
    TAXCALC_AGE("TaxCalc.Age"),

    /**
     * TaxCalc Allowances.
     */
    TAXCALC_ALLOW("TaxCalc.Allow"),

    /**
     * Dilution Name.
     */
    DILUTION_NAME("Dilution.Name"),

    /**
     * Dilution List.
     */
    DILUTION_LIST("Dilution.List"),

    /**
     * Charge Name.
     */
    CHARGE_NAME("Charge.Name"),

    /**
     * Charge List.
     */
    CHARGE_LIST("Charge.List"),

    /**
     * Charge Slice.
     */
    CHARGE_SLICE("Charge.Slice"),

    /**
     * Charge Tax.
     */
    CHARGE_TAX("Charge.Tax"),

    /**
     * AccountAttr Valuation.
     */
    ACCOUNTATTR_VALUATION("AccountAttr.Valuation"),

    /**
     * AccountAttr ForeignValuation.
     */
    ACCOUNTATTR_FOREIGNVALUE("AccountAttr.ForeignValue"),

    /**
     * AccountAttr DepositRate.
     */
    ACCOUNTATTR_DEPOSITRATE(MoneyWiseDataResource.MONEYWISEDATA_FIELD_RATE),

    /**
     * AccountAttr Delta.
     */
    ACCOUNTATTR_VALUEDELTA("AccountAttr.ValueDelta"),

    /**
     * AccountAttr Profit.
     */
    ACCOUNTATTR_PROFIT("AccountAttr.Profit"),

    /**
     * AccountAttr ForeignDelta.
     */
    ACCOUNTATTR_FOREIGNVALUEDELTA("AccountAttr.ForeignValueDelta"),

    /**
     * AccountAttr Local Valuation.
     */
    ACCOUNTATTR_LOCALVALUE("AccountAttr.LocalValue"),

    /**
     * AccountAttr CurrencyFluctuation.
     */
    ACCOUNTATTR_CURRENCYFLUCT("AccountAttr.CurrencyFluct"),

    /**
     * AccountAttr ExchangeRate.
     */
    ACCOUNTATTR_EXCHANGERATE(MoneyWiseDataTypeResource.XCHGRATE_NAME),

    /**
     * AccountAttr Maturity.
     */
    ACCOUNTATTR_MATURITY("AccountAttr.Maturity"),

    /**
     * AccountAttr Valuation.
     */
    ACCOUNTATTR_SPEND("AccountAttr.Spend"),

    /**
     * AccountAttr BadDebt.
     */
    ACCOUNTATTR_BADDEBT("AccountAttr.BadDebt"),

    /**
     * PayeeAttr Valuation.
     */
    PAYEEATTR_INCOME("PayeeAttr.Income"),

    /**
     * PayeeAttr Valuation.
     */
    PAYEEATTR_EXPENSE("PayeeAttr.Expense"),

    /**
     * SecurityAttr Units.
     */
    SECURITYATTR_UNITS(MoneyWiseDataResource.MONEYWISEDATA_FIELD_UNITS),

    /**
     * SecurityAttr Cost.
     */
    SECURITYATTR_COST("SecurityAttr.Cost"),

    /**
     * SecurityAttr Gains.
     */
    SECURITYATTR_GAINS("SecurityAttr.Gains"),

    /**
     * SecurityAttr Local Gains.
     */
    SECURITYATTR_LOCALGAINS("SecurityAttr.LocalGains"),

    /**
     * SecurityAttr Foreign Gains.
     */
    SECURITYATTR_FOREIGNGAINS("SecurityAttr.ForeignGains"),

    /**
     * SecurityAttr GrowthAdjustment.
     */
    SECURITYATTR_GROWTHADJUST("SecurityAttr.GrowthAdjust"),

    /**
     * SecurityAttr Invested.
     */
    SECURITYATTR_INVESTED("SecurityAttr.Invested"),

    /**
     * SecurityAttr Local Invested.
     */
    SECURITYATTR_LOCALINVESTED("SecurityAttr.LocalInvested"),

    /**
     * SecurityAttr Foreign Invested.
     */
    SECURITYATTR_FOREIGNINVESTED("SecurityAttr.ForeignInvested"),

    /**
     * SecurityAttr Dividend.
     */
    SECURITYATTR_DIVIDEND("SecurityAttr.Dividend"),

    /**
     * SecurityAttr Local Dividend.
     */
    SECURITYATTR_LOCALDIVIDEND("SecurityAttr.LocalDividend"),

    /**
     * SecurityAttr Foreign Dividend.
     */
    SECURITYATTR_FOREIGNDIVIDEND("SecurityAttr.ForeignDividend"),

    /**
     * SecurityAttr Market.
     */
    SECURITYATTR_MARKET("SecurityAttr.Market"),

    /**
     * SecurityAttr MarketProfit.
     */
    SECURITYATTR_MARKETPROFIT("SecurityAttr.MarketProfit"),

    /**
     * SecurityAttr Profit.
     */
    SECURITYATTR_PROFIT("SecurityAttr.Profit"),

    /**
     * SecurityAttr Price.
     */
    SECURITYATTR_PRICE(MoneyWiseDataResource.MONEYWISEDATA_FIELD_PRICE),

    /**
     * TaxAttr Gross.
     */
    TAXATTR_GROSS("TaxAttr.Gross"),

    /**
     * TaxAttr Nett.
     */
    TAXATTR_NETT("TaxAttr.Nett"),

    /**
     * TaxAttr Tax.
     */
    TAXATTR_TAX("TaxAttr.Tax");

    /**
     * The Resource Builder.
     */
    private static final ResourceBuilder BUILDER = ResourceBuilder.getResourceBuilder(Analysis.class.getCanonicalName());

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
    AnalysisResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    AnalysisResource(final ResourceId pResource) {
        theKeyName = null;
        theValue = pResource.getValue();
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMoneyWise.analysis";
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
     * Obtain key for account attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static ResourceId getKeyForAccountAttr(final AccountAttribute pValue) {
        switch (pValue) {
            case VALUATION:
                return ACCOUNTATTR_VALUATION;
            case FOREIGNVALUE:
                return ACCOUNTATTR_FOREIGNVALUE;
            case LOCALVALUE:
                return ACCOUNTATTR_LOCALVALUE;
            case DEPOSITRATE:
                return ACCOUNTATTR_DEPOSITRATE;
            case EXCHANGERATE:
                return ACCOUNTATTR_EXCHANGERATE;
            case VALUEDELTA:
                return ACCOUNTATTR_VALUEDELTA;
            case FOREIGNVALUEDELTA:
                return ACCOUNTATTR_FOREIGNVALUEDELTA;
            case CURRENCYFLUCT:
                return ACCOUNTATTR_CURRENCYFLUCT;
            case MATURITY:
                return ACCOUNTATTR_MATURITY;
            case SPEND:
                return ACCOUNTATTR_SPEND;
            case BADDEBT:
                return ACCOUNTATTR_BADDEBT;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pValue));
        }
    }

    /**
     * Obtain key for event attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static ResourceId getKeyForEventAttr(final TransactionAttribute pValue) {
        switch (pValue) {
            case INCOME:
                return PAYEEATTR_INCOME;
            case EXPENSE:
                return PAYEEATTR_EXPENSE;
            case PROFIT:
                return ACCOUNTATTR_PROFIT;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pValue));
        }
    }

    /**
     * Obtain key for payee attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static ResourceId getKeyForPayeeAttr(final PayeeAttribute pValue) {
        switch (pValue) {
            case INCOME:
                return PAYEEATTR_INCOME;
            case EXPENSE:
                return PAYEEATTR_EXPENSE;
            case PROFIT:
                return ACCOUNTATTR_PROFIT;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pValue));
        }
    }

    /**
     * Obtain key for security attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static ResourceId getKeyForSecurityAttr(final SecurityAttribute pValue) {
        switch (pValue) {
            case VALUATION:
                return ACCOUNTATTR_VALUATION;
            case FOREIGNVALUE:
                return ACCOUNTATTR_FOREIGNVALUE;
            case VALUEDELTA:
                return ACCOUNTATTR_VALUEDELTA;
            case FOREIGNVALUEDELTA:
                return ACCOUNTATTR_FOREIGNVALUEDELTA;
            case EXCHANGERATE:
                return ACCOUNTATTR_EXCHANGERATE;
            case UNITS:
                return SECURITYATTR_UNITS;
            case COST:
                return SECURITYATTR_COST;
            case GAINS:
                return SECURITYATTR_GAINS;
            case LOCALGAINS:
                return SECURITYATTR_LOCALGAINS;
            case FOREIGNGAINS:
                return SECURITYATTR_FOREIGNGAINS;
            case GROWTHADJUST:
                return SECURITYATTR_GROWTHADJUST;
            case INVESTED:
                return SECURITYATTR_INVESTED;
            case LOCALINVESTED:
                return SECURITYATTR_LOCALINVESTED;
            case FOREIGNINVESTED:
                return SECURITYATTR_FOREIGNINVESTED;
            case DIVIDEND:
                return SECURITYATTR_DIVIDEND;
            case LOCALDIVIDEND:
                return SECURITYATTR_LOCALDIVIDEND;
            case FOREIGNDIVIDEND:
                return SECURITYATTR_FOREIGNDIVIDEND;
            case MARKET:
                return SECURITYATTR_MARKET;
            case MARKETPROFIT:
                return SECURITYATTR_MARKETPROFIT;
            case PROFIT:
                return SECURITYATTR_PROFIT;
            case PRICE:
                return SECURITYATTR_PRICE;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pValue));
        }
    }

    /**
     * Obtain key for tax attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static ResourceId getKeyForTaxAttr(final TaxBasisAttribute pValue) {
        switch (pValue) {
            case GROSS:
                return TAXATTR_GROSS;
            case NETT:
                return TAXATTR_NETT;
            case TAXCREDIT:
                return TAXATTR_TAX;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pValue));
        }
    }

    /**
     * Obtain key for analysisType.
     * @param pValue the Value
     * @return the resource key
     */
    protected static ResourceId getKeyForAnalysisType(final AnalysisType pValue) {
        switch (pValue) {
            case DEPOSIT:
                return MoneyWiseDataTypeResource.DEPOSIT_NAME;
            case CASH:
                return MoneyWiseDataTypeResource.CASH_NAME;
            case LOAN:
                return MoneyWiseDataTypeResource.LOAN_NAME;
            case PAYEE:
                return MoneyWiseDataTypeResource.PAYEE_NAME;
            case SECURITY:
                return MoneyWiseDataTypeResource.SECURITY_NAME;
            case PORTFOLIO:
                return MoneyWiseDataTypeResource.PORTFOLIO_NAME;
            case CATEGORY:
                return MoneyWiseDataTypeResource.TRANSCAT_NAME;
            case TAXBASIS:
                return MoneyWiseDataTypeResource.TAXBASIS_NAME;
            case TRANSTAG:
                return MoneyWiseDataTypeResource.TRANSTAG_NAME;
            case ALL:
                return FILTER_ALL;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pValue));
        }
    }
}
