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
package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.ResourceBundle;

/**
 * Resource IDs for MoneyWise Analysis Fields.
 */
public enum MoneyWiseXAnalysisValuesResource
        implements OceanusBundleId, MetisDataFieldId {
    /**
     * AccountAttr Balance.
     */
    ACCOUNTATTR_BALANCE("AccountAttr.Balance"),

    /**
     * AccountAttr ReportedBalance.
     */
    ACCOUNTATTR_VALUATION("AccountAttr.Valuation"),

    /**
     * AccountAttr Delta.
     */
    ACCOUNTATTR_VALUEDELTA("AccountAttr.ValueDelta"),

    /**
     * AccountAttr Profit.
     */
    ACCOUNTATTR_PROFIT("AccountAttr.Profit"),

    /**
     * AccountAttr DepositRate.
     */
    ACCOUNTATTR_DEPOSITRATE(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE),

    /**
     * AccountAttr ExchangeRate.
     */
    ACCOUNTATTR_EXCHANGERATE(MoneyWiseBasicResource.XCHGRATE_NAME),

    /**
     * AccountAttr Maturity.
     */
    ACCOUNTATTR_MATURITY("AccountAttr.Maturity"),

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
    SECURITYATTR_UNITS(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_UNITS),

    /**
     * SecurityAttr Value.
     */
    SECURITYATTR_VALUE("SecurityAttr.Value"),

    /**
     * SecurityAttr ResidualCost.
     */
    SECURITYATTR_RESIDUALCOST("SecurityAttr.ResidualCost"),

    /**
     * SecurityAttr RealisedGains.
     */
    SECURITYATTR_REALISEDGAINS("SecurityAttr.RealisedGains"),

    /**
     * SecurityAttr unrealisedGains.
     */
    SECURITYATTR_UNREALISEDGAINS("SecurityAttr.UnRealisedGains"),

    /**
     * SecurityAttr unrealisedGainsAdjust.
     */
    SECURITYATTR_GAINSADJUST("SecurityAttr.GainsAdjust"),

    /**
     * SecurityAttr Dividend.
     */
    SECURITYATTR_DIVIDEND("SecurityAttr.Dividend"),

    /**
     * SecurityAttr MarketProfit.
     */
    SECURITYATTR_MARKETPROFIT("SecurityAttr.MarketProfit"),

    /**
     * SecurityAttr Profit.
     */
    SECURITYATTR_PROFIT("SecurityAttr.Profit"),

    /**
     * SecurityAttr CashConsideration/ReturnedCash.
     */
    SECURITYATTR_RETURNEDCASH("SecurityAttr.ReturnedCash"),

    /**
     * SecurityAttr StockConsideration/XferredValue.
     */
    SECURITYATTR_XFERREDVALUE("SecurityAttr.XferredValue"),

    /**
     * SecurityAttr XferredCost.
     */
    SECURITYATTR_XFERREDCOST("SecurityAttr.XferredCost"),

    /**
     * SecurityAttr CashInvested.
     */
    SECURITYATTR_CASHINVESTED("SecurityAttr.CashInvested"),

    /**
     * SecurityAttr CapitalGain.
     */
    SECURITYATTR_CAPITALGAIN("SecurityAttr.CapitalGain"),

    /**
     * SecurityAttr AllowedCost.
     */
    SECURITYATTR_ALLOWEDCOST("SecurityAttr.AllowedCost"),

    /**
     * SecurityAttr Price.
     */
    SECURITYATTR_PRICE(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE),

    /**
     * SecurityAttr StartDate.
     */
    SECURITYATTR_STARTDATE("SecurityAttr.StartDate"),

    /**
     * SecurityAttr Funded.
     */
    SECURITYATTR_FUNDED("SecurityAttr.Funded"),

    /**
     * SecurityAttr SliceGain.
     */
    SECURITYATTR_SLICEGAIN("SecurityAttr.SliceGain"),

    /**
     * SecurityAttr SliceYears.
     */
    SECURITYATTR_SLICEYEARS("SecurityAttr.SliceYears"),

    /**
     * SecurityAttr Consideration.
     */
    SECURITYATTR_CONSIDERATION("SecurityAttr.Consideration"),

    /**
     * SecurityAttr CostDilution.
     */
    SECURITYATTR_COSTDILUTION("SecurityAttr.CostDilution"),

    /**
     * SecurityAttr CashType.
     */
    SECURITYATTR_CASHTYPE("SecurityAttr.CashType"),

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
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MoneyWiseXAnalysisValuesResource.class.getCanonicalName(),
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
    MoneyWiseXAnalysisValuesResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     *
     * @param pResource the underlying resource
     */
    MoneyWiseXAnalysisValuesResource(final OceanusBundleId pResource) {
        theKeyName = null;
        theValue = pResource.getValue();
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "MoneyWise.analysis";
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
}
