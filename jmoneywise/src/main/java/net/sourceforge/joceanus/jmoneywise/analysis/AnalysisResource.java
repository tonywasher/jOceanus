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
     * EventCategory Name.
     */
    EVENTCATEGORY_NAME("TransCategory.Name"),

    /**
     * EventCategory List.
     */
    EVENTCATEGORY_LIST("TransCategory.List"),

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
     * AccountAttr Rate.
     */
    ACCOUNTATTR_RATE(MoneyWiseDataResource.MONEYWISEDATA_FIELD_RATE),

    /**
     * AccountAttr Delta.
     */
    ACCOUNTATTR_DELTA("AccountAttr.Delta"),

    /**
     * AccountAttr Maturity.
     */
    ACCOUNTATTR_MATURITY("AccountAttr.Maturity"),

    /**
     * AccountAttr Valuation.
     */
    ACCOUNTATTR_SPEND("AccountAttr.Spend"),

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
     * SecurityAttr Invested.
     */
    SECURITYATTR_INVESTED("SecurityAttr.Invested"),

    /**
     * SecurityAttr Dividend.
     */
    SECURITYATTR_DIVIDEND("SecurityAttr.Dividend"),

    /**
     * SecurityAttr Market.
     */
    SECURITYATTR_MARKET("SecurityAttr.Market"),

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
     * TaxAttr Net.
     */
    TAXATTR_NET("TaxAttr.Net"),

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
    private AnalysisResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pResource the underlying resource
     */
    private AnalysisResource(final ResourceId pResource) {
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
    protected static AnalysisResource getKeyForAccountAttr(final AccountAttribute pValue) {
        switch (pValue) {
            case VALUATION:
                return ACCOUNTATTR_VALUATION;
            case RATE:
                return ACCOUNTATTR_RATE;
            case DELTA:
                return ACCOUNTATTR_DELTA;
            case MATURITY:
                return ACCOUNTATTR_MATURITY;
            case SPEND:
                return ACCOUNTATTR_SPEND;
            default:
                return null;
        }
    }

    /**
     * Obtain key for event attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static AnalysisResource getKeyForEventAttr(final EventAttribute pValue) {
        switch (pValue) {
            case INCOME:
                return PAYEEATTR_INCOME;
            case EXPENSE:
                return PAYEEATTR_EXPENSE;
            case DELTA:
                return ACCOUNTATTR_DELTA;
            default:
                return null;
        }
    }

    /**
     * Obtain key for payee attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static AnalysisResource getKeyForPayeeAttr(final PayeeAttribute pValue) {
        switch (pValue) {
            case INCOME:
                return PAYEEATTR_INCOME;
            case EXPENSE:
                return PAYEEATTR_EXPENSE;
            case DELTA:
                return ACCOUNTATTR_DELTA;
            default:
                return null;
        }
    }

    /**
     * Obtain key for security attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static AnalysisResource getKeyForSecurityAttr(final SecurityAttribute pValue) {
        switch (pValue) {
            case VALUATION:
                return ACCOUNTATTR_VALUATION;
            case DELTA:
                return ACCOUNTATTR_DELTA;
            case UNITS:
                return SECURITYATTR_UNITS;
            case COST:
                return SECURITYATTR_COST;
            case GAINS:
                return SECURITYATTR_GAINS;
            case INVESTED:
                return SECURITYATTR_INVESTED;
            case DIVIDEND:
                return SECURITYATTR_DIVIDEND;
            case MARKET:
                return SECURITYATTR_MARKET;
            case PROFIT:
                return SECURITYATTR_PROFIT;
            case PRICE:
                return SECURITYATTR_PRICE;
            default:
                return null;
        }
    }

    /**
     * Obtain key for tax attribute.
     * @param pValue the Value
     * @return the resource key
     */
    protected static AnalysisResource getKeyForTaxAttr(final TaxBasisAttribute pValue) {
        switch (pValue) {
            case GROSS:
                return TAXATTR_GROSS;
            case NET:
                return TAXATTR_NET;
            case TAXCREDIT:
                return TAXATTR_TAX;
            default:
                return null;
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
                return null;
        }
    }
}
