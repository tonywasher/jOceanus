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
package net.sourceforge.joceanus.moneywise.tax.uk;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusFiscalYear;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxYear;

import java.time.Month;

/**
 * The UK Tax Year.
 */
public class MoneyWiseUKTaxYear
        extends MoneyWiseTaxYear {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseUKTaxYear> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKTaxYear.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_ALLOWANCES, MoneyWiseUKTaxYear::getAllowances);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_BANDS, MoneyWiseUKTaxYear::getTaxBands);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_INTEREST, MoneyWiseUKTaxYear::getInterestScheme);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_DIVIDEND, MoneyWiseUKTaxYear::getDividendScheme);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_CAPITAL, MoneyWiseUKTaxYear::getCapitalScheme);
    }

    /**
     * The Allowances.
     */
    private final MoneyWiseUKBasicAllowance theAllowances;

    /**
     * The TaxBands.
     */
    private final MoneyWiseUKTaxBands theTaxBands;

    /**
     * The Income Scheme.
     */
    private final MoneyWiseUKIncomeScheme theIncomeScheme;

    /**
     * The Rental Scheme.
     */
    private final MoneyWiseUKRoomRentalScheme theRentalScheme;

    /**
     * The Interest Scheme.
     */
    private final MoneyWiseUKInterestScheme theInterestScheme;

    /**
     * The Dividends Scheme.
     */
    private final MoneyWiseUKDividendScheme theDividendScheme;

    /**
     * The ChargeableGains Scheme.
     */
    private final MoneyWiseUKChargeableGainsScheme theChargeableGainsScheme;

    /**
     * The Capital Gains Scheme.
     */
    private final MoneyWiseUKCapitalScheme theCapitalScheme;

    /**
     * Constructor.
     *
     * @param pDate       the tax year end
     * @param pAllowances the allowances
     * @param pTaxBands   the standard tax bands
     * @param pInterest   the interest scheme
     * @param pDividend   the dividend scheme
     * @param pCapital    the capital gains scheme
     */
    protected MoneyWiseUKTaxYear(final int pDate,
                                 final MoneyWiseUKBasicAllowance pAllowances,
                                 final MoneyWiseUKTaxBands pTaxBands,
                                 final MoneyWiseUKInterestScheme pInterest,
                                 final MoneyWiseUKDividendScheme pDividend,
                                 final MoneyWiseUKCapitalScheme pCapital) {
        super(getDate(pDate));
        theAllowances = pAllowances;
        theTaxBands = pTaxBands;
        theIncomeScheme = new MoneyWiseUKIncomeScheme();
        theRentalScheme = new MoneyWiseUKRoomRentalScheme();
        theInterestScheme = pInterest;
        theDividendScheme = pDividend;
        theChargeableGainsScheme = new MoneyWiseUKChargeableGainsScheme();
        theCapitalScheme = pCapital;
    }

    /**
     * Obtain the Allowances.
     *
     * @return the allowances
     */
    public MoneyWiseUKBasicAllowance getAllowances() {
        return theAllowances;
    }

    /**
     * Obtain the Standard taxBands.
     *
     * @return the tax bands
     */
    public MoneyWiseUKTaxBands getTaxBands() {
        return theTaxBands;
    }

    /**
     * Obtain the Interest TaxScheme.
     *
     * @return the tax scheme
     */
    private MoneyWiseUKInterestScheme getInterestScheme() {
        return theInterestScheme;
    }

    /**
     * Obtain the Dividend TaxScheme.
     *
     * @return the tax scheme
     */
    private MoneyWiseUKDividendScheme getDividendScheme() {
        return theDividendScheme;
    }

    /**
     * Obtain the Capital TaxScheme.
     *
     * @return the tax scheme
     */
    private MoneyWiseUKCapitalScheme getCapitalScheme() {
        return theCapitalScheme;
    }

    /**
     * Determine the taxYear end.
     *
     * @param pYear the taxYear as an integer
     * @return the amount
     */
    private static OceanusDate getDate(final int pYear) {
        final OceanusDate myDate = new OceanusDate(pYear, Month.JANUARY, 1);
        return OceanusFiscalYear.UK.endOfYear(myDate);
    }

    @Override
    public MetisFieldSet<MoneyWiseUKTaxYear> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isTaxCreditRequired() {
        return !(theAllowances instanceof MoneyWiseUKSavingsAllowance);
    }

    @Override
    public OceanusRate getTaxCreditRateForInterest() {
        return theInterestScheme.getTaxCreditRate(this);
    }

    @Override
    public OceanusRate getTaxCreditRateForDividend() {
        return theDividendScheme.getTaxCreditRate(this);
    }

    @Override
    public MoneyWiseUKTaxAnalysis analyseTaxYear(final MetisPreferenceManager pPreferences,
                                                 final MoneyWiseTaxSource pTaxSource) {
        /* Create a new analysis */
        final MoneyWiseUKTaxAnalysis myAnalysis = new MoneyWiseUKTaxAnalysis(pTaxSource, pPreferences, this);

        /* Process the standard amounts */
        myAnalysis.processItem(MoneyWiseTaxClass.SALARY, theIncomeScheme);
        myAnalysis.processItem(MoneyWiseTaxClass.RENTALINCOME, theIncomeScheme);
        myAnalysis.processItem(MoneyWiseTaxClass.ROOMRENTAL, theRentalScheme);
        myAnalysis.processItem(MoneyWiseTaxClass.OTHERINCOME, theIncomeScheme);

        /* Process the interest */
        myAnalysis.processItem(MoneyWiseTaxClass.TAXEDINTEREST, theInterestScheme);
        myAnalysis.processItem(MoneyWiseTaxClass.UNTAXEDINTEREST, theInterestScheme);
        myAnalysis.processItem(MoneyWiseTaxClass.PEER2PEERINTEREST, theInterestScheme);

        /* Process the dividends */
        myAnalysis.processItem(MoneyWiseTaxClass.DIVIDEND, theDividendScheme);
        myAnalysis.processItem(MoneyWiseTaxClass.UNITTRUSTDIVIDEND, theDividendScheme);
        myAnalysis.processItem(MoneyWiseTaxClass.FOREIGNDIVIDEND, theDividendScheme);

        /* Process the chargeable Gains */
        myAnalysis.processItem(MoneyWiseTaxClass.CHARGEABLEGAINS, theChargeableGainsScheme);

        /* Process the capital Gains */
        myAnalysis.processItem(MoneyWiseTaxClass.RESIDENTIALGAINS, theCapitalScheme);
        myAnalysis.processItem(MoneyWiseTaxClass.CAPITALGAINS, theCapitalScheme);

        /* Calculate the totals */
        myAnalysis.calculateTaxDue();
        myAnalysis.calculateTaxProfit();

        /* Return the analysis */
        return myAnalysis;
    }
}
