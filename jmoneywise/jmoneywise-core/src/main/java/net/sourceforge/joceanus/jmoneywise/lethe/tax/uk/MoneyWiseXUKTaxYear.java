/*******************************************************************************
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
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import java.time.Month;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxResource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxSource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxYear;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * The UK Tax Year.
 */
public class MoneyWiseXUKTaxYear
        extends MoneyWiseXTaxYear {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXUKTaxYear> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKTaxYear.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXYEAR_ALLOWANCES, MoneyWiseXUKTaxYear::getAllowances);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXYEAR_BANDS, MoneyWiseXUKTaxYear::getTaxBands);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXYEAR_INTEREST, MoneyWiseXUKTaxYear::getInterestScheme);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXYEAR_DIVIDEND, MoneyWiseXUKTaxYear::getDividendScheme);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXYEAR_CAPITAL, MoneyWiseXUKTaxYear::getCapitalScheme);
    }

    /**
     * The Allowances.
     */
    private final MoneyWiseXUKBasicAllowance theAllowances;

    /**
     * The TaxBands.
     */
    private final MoneyWiseXUKTaxBands theTaxBands;

    /**
     * The Income Scheme.
     */
    private final MoneyWiseXUKIncomeScheme theIncomeScheme;

    /**
     * The Rental Scheme.
     */
    private final MoneyWiseXUKRoomRentalScheme theRentalScheme;

    /**
     * The Interest Scheme.
     */
    private final MoneyWiseXUKInterestScheme theInterestScheme;

    /**
     * The Dividends Scheme.
     */
    private final MoneyWiseXUKDividendScheme theDividendScheme;

    /**
     * The ChargeableGains Scheme.
     */
    private final MoneyWiseXUKChargeableGainsScheme theChargeableGainsScheme;

    /**
     * The Capital Gains Scheme.
     */
    private final MoneyWiseXUKCapitalScheme theCapitalScheme;

    /**
     * Constructor.
     * @param pDate the tax year end
     * @param pAllowances the allowances
     * @param pTaxBands the standard tax bands
     * @param pInterest the interest scheme
     * @param pDividend the dividend scheme
     * @param pCapital the capital gains scheme
     */
    protected MoneyWiseXUKTaxYear(final int pDate,
                                  final MoneyWiseXUKBasicAllowance pAllowances,
                                  final MoneyWiseXUKTaxBands pTaxBands,
                                  final MoneyWiseXUKInterestScheme pInterest,
                                  final MoneyWiseXUKDividendScheme pDividend,
                                  final MoneyWiseXUKCapitalScheme pCapital) {
        super(getDate(pDate));
        theAllowances = pAllowances;
        theTaxBands = pTaxBands;
        theIncomeScheme = new MoneyWiseXUKIncomeScheme();
        theRentalScheme = new MoneyWiseXUKRoomRentalScheme();
        theInterestScheme = pInterest;
        theDividendScheme = pDividend;
        theChargeableGainsScheme = new MoneyWiseXUKChargeableGainsScheme();
        theCapitalScheme = pCapital;
    }

    /**
     * Obtain the Allowances.
     * @return the allowances
     */
    public MoneyWiseXUKBasicAllowance getAllowances() {
        return theAllowances;
    }

    /**
     * Obtain the Standard taxBands.
     * @return the tax bands
     */
    public MoneyWiseXUKTaxBands getTaxBands() {
        return theTaxBands;
    }

    /**
     * Obtain the Interest TaxScheme.
     * @return the tax scheme
     */
    private MoneyWiseXUKInterestScheme getInterestScheme() {
        return theInterestScheme;
    }

    /**
     * Obtain the Dividend TaxScheme.
     * @return the tax scheme
     */
    private MoneyWiseXUKDividendScheme getDividendScheme() {
        return theDividendScheme;
    }

    /**
     * Obtain the Capital TaxScheme.
     * @return the tax scheme
     */
    private MoneyWiseXUKCapitalScheme getCapitalScheme() {
        return theCapitalScheme;
    }

    /**
     * Determine the taxYear end.
     * @param pYear the taxYear as an integer
     * @return the amount
     */
    private static TethysDate getDate(final int pYear) {
        final TethysDate myDate = new TethysDate(pYear, Month.JANUARY, 1);
        return TethysFiscalYear.UK.endOfYear(myDate);
    }

    @Override
    public MetisFieldSet<MoneyWiseXUKTaxYear> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isTaxCreditRequired() {
        return !(theAllowances instanceof MoneyWiseXUKSavingsAllowance);
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
    public MoneyWiseXUKTaxAnalysis analyseTaxYear(final MetisPreferenceManager pPreferences,
                                                  final MoneyWiseXTaxSource pTaxSource) {
        /* Create a new analysis */
        final MoneyWiseXUKTaxAnalysis myAnalysis = new MoneyWiseXUKTaxAnalysis(pTaxSource, pPreferences, this);

        /* Process the standard amounts */
        myAnalysis.processItem(TaxBasisClass.SALARY, theIncomeScheme);
        myAnalysis.processItem(TaxBasisClass.RENTALINCOME, theIncomeScheme);
        myAnalysis.processItem(TaxBasisClass.ROOMRENTAL, theRentalScheme);
        myAnalysis.processItem(TaxBasisClass.OTHERINCOME, theIncomeScheme);

        /* Process the interest */
        myAnalysis.processItem(TaxBasisClass.TAXEDINTEREST, theInterestScheme);
        myAnalysis.processItem(TaxBasisClass.UNTAXEDINTEREST, theInterestScheme);
        myAnalysis.processItem(TaxBasisClass.PEER2PEERINTEREST, theInterestScheme);

        /* Process the dividends */
        myAnalysis.processItem(TaxBasisClass.DIVIDEND, theDividendScheme);
        myAnalysis.processItem(TaxBasisClass.UNITTRUSTDIVIDEND, theDividendScheme);
        myAnalysis.processItem(TaxBasisClass.FOREIGNDIVIDEND, theDividendScheme);

        /* Process the chargeable Gains */
        myAnalysis.processItem(TaxBasisClass.CHARGEABLEGAINS, theChargeableGainsScheme);

        /* Process the capital Gains */
        myAnalysis.processItem(TaxBasisClass.RESIDENTIALGAINS, theCapitalScheme);
        myAnalysis.processItem(TaxBasisClass.CAPITALGAINS, theCapitalScheme);

        /* Calculate the totals */
        myAnalysis.calculateTaxDue();
        myAnalysis.calculateTaxProfit();

        /* Return the analysis */
        return myAnalysis;
    }
}
