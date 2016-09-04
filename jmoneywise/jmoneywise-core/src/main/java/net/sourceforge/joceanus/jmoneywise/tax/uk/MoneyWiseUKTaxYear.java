/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.tax.uk;

import java.time.Month;

import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxYear;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * The UK Tax Year.
 */
public class MoneyWiseUKTaxYear
        extends MoneyWiseTaxYear {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKTaxYear.class.getSimpleName(), MoneyWiseTaxYear.getBaseFields());

    /**
     * Allowances Field Id.
     */
    private static final MetisField FIELD_ALLOWANCES = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXYEAR_ALLOWANCES.getValue());

    /**
     * Bands Field Id.
     */
    private static final MetisField FIELD_BANDS = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXYEAR_BANDS.getValue());

    /**
     * InterestScheme Field Id.
     */
    private static final MetisField FIELD_INTEREST = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXYEAR_INTEREST.getValue());

    /**
     * DividendScheme Field Id.
     */
    private static final MetisField FIELD_DIVIDEND = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXYEAR_DIVIDEND.getValue());

    /**
     * CapitalScheme Field Id.
     */
    private static final MetisField FIELD_CAPITAL = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXYEAR_CAPITAL.getValue());

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
     * The TaxableGains Scheme.
     */
    private final MoneyWiseUKTaxableGainsScheme theTaxableGainsScheme;

    /**
     * The Capital Gains Scheme.
     */
    private final MoneyWiseUKCapitalScheme theCapitalScheme;

    /**
     * Constructor.
     * @param pDate the tax year end
     * @param pAllowances the allowances
     * @param pTaxBands the standard tax bands
     * @param pInterest the interest scheme
     * @param pDividend the dividend scheme
     * @param pCapital the capital gains scheme
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
        theTaxableGainsScheme = new MoneyWiseUKTaxableGainsScheme();
        theCapitalScheme = pCapital;
    }

    /**
     * Obtain the Allowances.
     * @return the allowances
     */
    public MoneyWiseUKBasicAllowance getAllowances() {
        return theAllowances;
    }

    /**
     * Obtain the Standard taxBands.
     * @return the tax bands
     */
    public MoneyWiseUKTaxBands getTaxBands() {
        return theTaxBands;
    }

    /**
     * Obtain the Income Scheme.
     * @return the scheme
     */
    public MoneyWiseUKIncomeScheme getIncomeScheme() {
        return theIncomeScheme;
    }

    /**
     * Obtain the Rental Scheme.
     * @return the scheme
     */
    public MoneyWiseUKRoomRentalScheme getRentalScheme() {
        return theRentalScheme;
    }

    /**
     * Obtain the Interest Scheme.
     * @return the scheme
     */
    public MoneyWiseUKInterestScheme getInterestScheme() {
        return theInterestScheme;
    }

    /**
     * Obtain the Dividend Scheme.
     * @return the scheme
     */
    public MoneyWiseUKDividendScheme getDividendScheme() {
        return theDividendScheme;
    }

    /**
     * Obtain the TaxableGains Scheme.
     * @return the scheme
     */
    public MoneyWiseUKTaxableGainsScheme getTaxableGainsScheme() {
        return theTaxableGainsScheme;
    }

    /**
     * Obtain the Capital Scheme.
     * @return the scheme
     */
    public MoneyWiseUKCapitalScheme getCapitalScheme() {
        return theCapitalScheme;
    }

    /**
     * Determine the taxYear end.
     * @param pYear the taxYear as an integer
     * @return the amount
     */
    private static TethysDate getDate(final int pYear) {
        TethysDate myDate = new TethysDate(pYear, Month.JANUARY, 1);
        return TethysFiscalYear.UK.endOfYear(myDate);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_ALLOWANCES.equals(pField)) {
            return theAllowances;
        }
        if (FIELD_BANDS.equals(pField)) {
            return theTaxBands;
        }
        if (FIELD_INTEREST.equals(pField)) {
            return theInterestScheme;
        }
        if (FIELD_DIVIDEND.equals(pField)) {
            return theDividendScheme;
        }
        if (FIELD_CAPITAL.equals(pField)) {
            return theCapitalScheme;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }

    @Override
    public boolean isTaxCreditRequired() {
        return !(theAllowances instanceof MoneyWiseUKSavingsAllowance);
    }

    @Override
    public TethysRate getTaxCreditRateForInterest() {
        return theInterestScheme.getTaxCreditRate(this);
    }

    @Override
    public TethysRate getTaxCreditRateForDividend() {
        return theDividendScheme.getTaxCreditRate(this);
    }

    @Override
    public MoneyWiseUKTaxAnalysis analyseTaxYear(final MetisPreferenceManager pPreferences,
                                                 final TaxBasisBucketList pTaxBasis) {
        return null;
    }
}
