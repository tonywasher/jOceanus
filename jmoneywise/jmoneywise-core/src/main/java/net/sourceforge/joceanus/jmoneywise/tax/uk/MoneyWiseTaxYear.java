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

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;

/**
 * The Tax Year.
 */
public class MoneyWiseTaxYear
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseTaxYear.class.getSimpleName());

    /**
     * Date Field Id.
     */
    private static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityField("EndOfTaxYear");

    /**
     * Allowances Field Id.
     */
    private static final MetisField FIELD_ALLOWANCES = FIELD_DEFS.declareEqualityField("Allowances");

    /**
     * StandardBands Field Id.
     */
    private static final MetisField FIELD_STANDARD = FIELD_DEFS.declareEqualityField("StandardBands");

    /**
     * InterestScheme Field Id.
     */
    private static final MetisField FIELD_INTEREST = FIELD_DEFS.declareEqualityField("InterestScheme");

    /**
     * DividendScheme Field Id.
     */
    private static final MetisField FIELD_DIVIDEND = FIELD_DEFS.declareEqualityField("DividendScheme");

    /**
     * CapitalScheme Field Id.
     */
    private static final MetisField FIELD_CAPITAL = FIELD_DEFS.declareEqualityField("CapitalScheme");

    /**
     * The Date.
     */
    private final TethysDate theYear;

    /**
     * The Allowances.
     */
    private final MoneyWiseBasicAllowance theAllowances;

    /**
     * The StandardTaxBands.
     */
    private final MoneyWiseTaxBands theStandardBands;

    /**
     * The Income Scheme.
     */
    private final MoneyWiseIncomeScheme theIncomeScheme;

    /**
     * The Rental Scheme.
     */
    private final MoneyWiseRentalScheme theRentalScheme;

    /**
     * The Interest Scheme.
     */
    private final MoneyWiseInterestScheme theInterestScheme;

    /**
     * The Dividends Scheme.
     */
    private final MoneyWiseDividendScheme theDividendScheme;

    /**
     * The TaxableGains Scheme.
     */
    private final MoneyWiseTaxableGainsScheme theTaxableGainsScheme;

    /**
     * The Capital Gains Scheme.
     */
    private final MoneyWiseCapitalScheme theCapitalScheme;

    /**
     * Constructor.
     * @param pDate the tax year end
     * @param pAllowances the allowances
     * @param pStandard the standard tax bands
     * @param pInterest the interest scheme
     * @param pDividend the dividend scheme
     * @param pCapital the capital gains scheme
     */
    protected MoneyWiseTaxYear(final int pDate,
                               final MoneyWiseBasicAllowance pAllowances,
                               final MoneyWiseTaxBands pStandard,
                               final MoneyWiseInterestScheme pInterest,
                               final MoneyWiseDividendScheme pDividend,
                               final MoneyWiseCapitalScheme pCapital) {
        theYear = getDate(pDate);
        theAllowances = pAllowances;
        theStandardBands = pStandard;
        theIncomeScheme = new MoneyWiseIncomeScheme();
        theRentalScheme = new MoneyWiseRentalScheme();
        theInterestScheme = pInterest;
        theDividendScheme = pDividend;
        theTaxableGainsScheme = new MoneyWiseTaxableGainsScheme();
        theCapitalScheme = pCapital;
    }

    /**
     * Obtain the Year.
     * @return the tax year end
     */
    public TethysDate getYear() {
        return theYear;
    }

    /**
     * Obtain the Allowances.
     * @return the allowances
     */
    public MoneyWiseBasicAllowance getAllowances() {
        return theAllowances;
    }

    /**
     * Obtain the Standard Bands.
     * @return the standard bands
     */
    public MoneyWiseTaxBands getStandardBands() {
        return theStandardBands;
    }

    /**
     * Obtain the Income Scheme.
     * @return the scheme
     */
    public MoneyWiseIncomeScheme getIncomeScheme() {
        return theIncomeScheme;
    }

    /**
     * Obtain the Rental Scheme.
     * @return the scheme
     */
    public MoneyWiseRentalScheme getRentalScheme() {
        return theRentalScheme;
    }

    /**
     * Obtain the Interest Scheme.
     * @return the scheme
     */
    public MoneyWiseInterestScheme getInterestScheme() {
        return theInterestScheme;
    }

    /**
     * Obtain the Dividend Scheme.
     * @return the scheme
     */
    public MoneyWiseDividendScheme getDividendScheme() {
        return theDividendScheme;
    }

    /**
     * Obtain the TaxableGains Scheme.
     * @return the scheme
     */
    public MoneyWiseTaxableGainsScheme getTaxableGainsScheme() {
        return theTaxableGainsScheme;
    }

    /**
     * Obtain the Capital Scheme.
     * @return the scheme
     */
    public MoneyWiseCapitalScheme getCapitalScheme() {
        return theCapitalScheme;
    }

    /**
     * Determine the taxYear end.
     * @param pYear the taxYear as an integer
     * @return the amount
     */
    private static TethysDate getDate(final int pYear) {
        TethysDate myDate = new TethysDate(1, 1, pYear);
        return TethysFiscalYear.UK.endOfYear(myDate);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_DATE.equals(pField)) {
            return theYear;
        }
        if (FIELD_ALLOWANCES.equals(pField)) {
            return theAllowances;
        }
        if (FIELD_STANDARD.equals(pField)) {
            return theStandardBands;
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

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return toString();
    }

    @Override
    public String toString() {
        return Integer.toString(getYear().getYear());
    }
}
