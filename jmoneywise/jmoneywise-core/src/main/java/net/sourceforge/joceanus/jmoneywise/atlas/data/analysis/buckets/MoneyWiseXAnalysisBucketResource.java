/*******************************************************************************
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticResource;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for MoneyWise Analysis Fields.
 */
public enum MoneyWiseXAnalysisBucketResource
        implements TethysBundleId, MetisDataFieldId {
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
     * TaxCalculation.
     */
    TAX_CALCULATION("Tax.Calculation"),

    /**
     * TaxYears.
     */
    TAX_YEARS("Tax.Years");

    /**
     * The AnalysisType Map.
     */
    private static final Map<MoneyWiseXAnalysisType, TethysBundleId> ANALYSIS_MAP = buildAnalysisMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getLoader(MoneyWiseXAnalysisBucketResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

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
    MoneyWiseXAnalysisBucketResource(final String pKeyName) {
        theKeyName = pKeyName;
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
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }

    /**
     * Build analysis map.
     * @return the map
     */
    private static Map<MoneyWiseXAnalysisType, TethysBundleId> buildAnalysisMap() {
        /* Create the map and return it */
        final Map<MoneyWiseXAnalysisType, TethysBundleId> myMap = new EnumMap<>(MoneyWiseXAnalysisType.class);
        myMap.put(MoneyWiseXAnalysisType.DEPOSIT, MoneyWiseBasicResource.DEPOSIT_NAME);
        myMap.put(MoneyWiseXAnalysisType.CASH, MoneyWiseBasicResource.CASH_NAME);
        myMap.put(MoneyWiseXAnalysisType.LOAN, MoneyWiseBasicResource.LOAN_NAME);
        myMap.put(MoneyWiseXAnalysisType.PAYEE, MoneyWiseBasicResource.PAYEE_NAME);
        myMap.put(MoneyWiseXAnalysisType.SECURITY, MoneyWiseBasicResource.SECURITY_NAME);
        myMap.put(MoneyWiseXAnalysisType.PORTFOLIO, MoneyWiseBasicResource.PORTFOLIO_NAME);
        myMap.put(MoneyWiseXAnalysisType.CATEGORY, MoneyWiseBasicResource.TRANSCAT_NAME);
        myMap.put(MoneyWiseXAnalysisType.TAXBASIS, MoneyWiseStaticResource.TAXBASIS_NAME);
        myMap.put(MoneyWiseXAnalysisType.TRANSTAG, MoneyWiseBasicResource.TRANSTAG_NAME);
        myMap.put(MoneyWiseXAnalysisType.ALL, FILTER_ALL);
        return myMap;
    }

    /**
     * Obtain key for analysisType.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForAnalysisType(final MoneyWiseXAnalysisType pValue) {
        return TethysBundleLoader.getKeyForEnum(ANALYSIS_MAP, pValue);
    }
}
