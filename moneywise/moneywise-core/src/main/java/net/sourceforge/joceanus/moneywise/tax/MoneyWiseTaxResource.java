/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
package net.sourceforge.joceanus.moneywise.tax;

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for MoneyWise Tax Fields.
 */
public enum MoneyWiseTaxResource
        implements OceanusBundleId, MetisDataFieldId {
    /**
     * Basic Allowance.
     */
    ALLOWANCE_BASIC("Allowance.Basic"),

    /**
     * Rental Allowance.
     */
    ALLOWANCE_RENTAL("Allowance.Rental"),

    /**
     * Capital Allowance.
     */
    ALLOWANCE_CAPITAL("Allowance.Capital"),

    /**
     * Savings Allowance.
     */
    ALLOWANCE_SAVINGS("Allowance.Savings"),

    /**
     * Dividend Allowance.
     */
    ALLOWANCE_DIVIDEND("Allowance.Dividend"),

    /**
     * LoAge Allowance.
     */
    ALLOWANCE_LOAGE("Allowance.LoAge"),

    /**
     * HiAge Allowance.
     */
    ALLOWANCE_HIAGE("Allowance.HiAge"),

    /**
     * AgeAllowanceLimit.
     */
    LIMIT_AGEALLOWANCE("Limit.AgeAllowance"),

    /**
     * AdditionalAllowanceLimit.
     */
    LIMIT_ADDALLOWANCE("Limit.AdditionalAllowance"),

    /**
     * Scheme BaseRate.
     */
    SCHEME_BASE_RATE("Scheme.BaseRate"),

    /**
     * Scheme HighRate.
     */
    SCHEME_HIGH_RATE("Scheme.HighRate"),

    /**
     * Scheme AdditionalRate.
     */
    SCHEME_ADDITIONAL_RATE("Scheme.AdditionalRate"),

    /**
     * Scheme Residential.
     */
    SCHEME_RESIDENTIAL("Scheme.Residential"),

    /**
     * Scheme Relief Available.
     */
    SCHEME_RELIEF_AVAILABLE("Scheme.ReliefAvailable"),

    /**
     * TaxBands Standard.
     */
    TAXBANDS_STANDARD("TaxBands.Standard"),

    /**
     * TaxBands LoSavings.
     */
    TAXBANDS_LOSAVINGS("TaxBands.LoSavings"),

    /**
     * TaxBands Amount.
     */
    TAXBANDS_AMOUNT("TaxBands.Amount"),

    /**
     * TaxBands Rate.
     */
    TAXBANDS_RATE("TaxBands.Rate"),

    /**
     * TaxBands TaxableIncome.
     */
    TAXBANDS_INCOME("TaxBands.Income"),

    /**
     * TaxBands TaxDue.
     */
    TAXBANDS_TAXDUE("TaxBands.TaxDue"),

    /**
     * TaxBands hasLoTaxBand.
     */
    TAXBANDS_HASLOTAXBAND("TaxBands.hasLoTaxBand"),

    /**
     * TaxYear Name.
     */
    TAXYEAR_NAME("TaxYear.Name"),

    /**
     * TaxYear End.
     */
    TAXYEAR_END("TaxYear.End"),

    /**
     * TaxYear Allowances.
     */
    TAXYEAR_ALLOWANCES("TaxYear.Allowances"),

    /**
     * TaxYear Bands.
     */
    TAXYEAR_BANDS("TaxYear.Bands"),

    /**
     * TaxYear Capital.
     */
    TAXYEAR_INTEREST("TaxYear.Interest"),

    /**
     * TaxYear Capital.
     */
    TAXYEAR_DIVIDEND("TaxYear.Dividend"),

    /**
     * TaxYear Capital.
     */
    TAXYEAR_CAPITAL("TaxYear.Capital"),

    /**
     * TaxYear Profit.
     */
    TAXYEAR_PROFIT("TaxYear.Profit"),

    /**
     * TaxConfig Name.
     */
    TAXCONFIG_NAME("TaxConfig.Name"),

    /**
     * TaxConfig GrossTaxable.
     */
    TAXCONFIG_GROSS("TaxConfig.GrossTaxable"),

    /**
     * TaxConfig GrossPreSavings.
     */
    TAXCONFIG_PRESAVINGS("TaxConfig.GrossPreSavings"),

    /**
     * TaxConfig Birthday.
     */
    TAXCONFIG_BIRTHDAY("TaxConfig.Birthday"),

    /**
     * TaxConfig Age.
     */
    TAXCONFIG_AGE("TaxConfig.Age"),

    /**
     * TaxConfig AgeAllowances.
     */
    TAXCONFIG_AGEALLOWANCE("TaxConfig.AgeAllowances"),

    /**
     * Analysis TaxBuckets.
     */
    TAXANALYSIS_TAXBUCKETS("TaxAnalysis.TaxBuckets"),

    /**
     * Analysis TaxProfit.
     */
    TAXANALYSIS_TAXPROFIT("TaxAnalysis.TaxProfit"),

    /**
     * TaxPreference Name.
     */
    TAXPREF_NAME("preference.display"),

    /**
     * TaxPreference Birthday.
     */
    TAXPREF_BIRTH("preference.birth"),

    /**
     * Marginal Reduction.
     */
    MARGINAL_REDUCTION("Marginal.Name"),

    /**
     * Marginal OneInTwo.
     */
    MARGINAL_ONEINTWO("Marginal.OneInTwo"),

    /**
     * Marginal TwoInThree.
     */
    MARGINAL_TWOINTHREE("Marginal.TwoInThree"),

    /**
     * CashType Large.
     */
    CASHTYPE_LARGE("CashType.Large"),

    /**
     * CashType Small.
     */
    CASHTYPE_SMALL("CashType.Small"),

    /**
     * ChargeableGain Gain.
     */
    CHARGEABLEGAIN_GAIN("ChargeableGain.Gain"),

    /**
     * ChargeableGain Slice.
     */
    CHARGEABLEGAIN_SLICE("ChargeableGain.Slice"),

    /**
     * ChargeableGain TotalGain.
     */
    CHARGEABLEGAIN_TOTALGAINS("ChargeableGain.TotalGains"),

    /**
     * ChargeableGain TotalSlices.
     */
    CHARGEABLEGAIN_TOTALSLICES("ChargeableGain.TotalSlices"),

    /**
     * ChargeableGain Ratio.
     */
    CHARGEABLEGAIN_RATIO("ChargeableGain.Ratio"),

    /**
     * ChargeableGain TaxedSlices.
     */
    CHARGEABLEGAIN_TAXEDSLICES("ChargeableGain.TaxedSlices"),

    /**
     * ChargeableGain NettTaxDue.
     */
    CHARGEABLEGAIN_NETTTAXDUE("ChargeableGain.NettTaxDue"),

    /**
     * ChargeableGain TaxRelief.
     */
    CHARGEABLEGAIN_TAXRELIEF("ChargeableGain.TaxRelief");

    /**
     * The Marginal Map.
     */
    private static final Map<MoneyWiseMarginalReduction, OceanusBundleId> MARGINAL_MAP = buildMarginalMap();

    /**
     * The CashType Map.
     */
    private static final Map<MoneyWiseCashType, OceanusBundleId> CASHTYPE_MAP = buildCashTypeMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MoneyWiseTaxResource.class.getCanonicalName(),
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
     *
     * @param pKeyName the key name
     */
    MoneyWiseTaxResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "MoneyWise.tax";
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
     * Build marginal map.
     *
     * @return the map
     */
    private static Map<MoneyWiseMarginalReduction, OceanusBundleId> buildMarginalMap() {
        /* Create the map and return it */
        final Map<MoneyWiseMarginalReduction, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseMarginalReduction.class);
        myMap.put(MoneyWiseMarginalReduction.ONEINTWO, MARGINAL_ONEINTWO);
        myMap.put(MoneyWiseMarginalReduction.TWOINTHREE, MARGINAL_TWOINTHREE);
        return myMap;
    }

    /**
     * Obtain key for marginal reduction type.
     *
     * @param pValue the Value
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForMarginalReduction(final MoneyWiseMarginalReduction pValue) {
        return OceanusBundleLoader.getKeyForEnum(MARGINAL_MAP, pValue);
    }

    /**
     * Build cashType map.
     *
     * @return the map
     */
    private static Map<MoneyWiseCashType, OceanusBundleId> buildCashTypeMap() {
        /* Create the map and return it */
        final Map<MoneyWiseCashType, OceanusBundleId> myMap = new EnumMap<>(MoneyWiseCashType.class);
        myMap.put(MoneyWiseCashType.LARGECASH, CASHTYPE_LARGE);
        myMap.put(MoneyWiseCashType.SMALLCASH, CASHTYPE_SMALL);
        return myMap;
    }

    /**
     * Obtain key for cash type.
     *
     * @param pValue the Value
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForCashType(final MoneyWiseCashType pValue) {
        return OceanusBundleLoader.getKeyForEnum(CASHTYPE_MAP, pValue);
    }
}
