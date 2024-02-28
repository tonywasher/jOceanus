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
package net.sourceforge.joceanus.jmoneywise.tax.uk;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * UK TaxBands.
 */
public class MoneyWiseUKTaxBands
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseUKTaxBands> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKTaxBands.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_STANDARD, MoneyWiseUKTaxBands::getStandardSet);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_HASLOTAXBAND, MoneyWiseUKTaxBands::hasLoTaxBand);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_LOSAVINGS, MoneyWiseUKTaxBands::getLoSavings);
    }

    /**
     * Standard TaxBandSet.
     */
    private final MoneyWiseTaxBandSet theStandard;

    /**
     * Low TaxBand present.
     */
    private final Boolean hasLoTaxBand;

    /**
     * Low Savings TaxBands.
     */
    private final MoneyWiseTaxBand theLoSavings;

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     */
    protected MoneyWiseUKTaxBands(final MoneyWiseTaxBandSet pStandard) {
        this(pStandard, Boolean.FALSE);
    }

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     * @param pHasLoTaxBand do we have a low tax band?
     */
    protected MoneyWiseUKTaxBands(final MoneyWiseTaxBandSet pStandard,
                                  final Boolean pHasLoTaxBand) {
        this(pStandard, pHasLoTaxBand, null);
    }

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     * @param pLoSavings the loSavings taxBand
     */
    protected MoneyWiseUKTaxBands(final MoneyWiseTaxBandSet pStandard,
                                  final MoneyWiseTaxBand pLoSavings) {
        this(pStandard, Boolean.FALSE, pLoSavings);
    }

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     * @param pHasLoTaxBand do we have a low tax band?
     * @param pLoSavings the loSavings taxBand
     */
    private MoneyWiseUKTaxBands(final MoneyWiseTaxBandSet pStandard,
                                final Boolean pHasLoTaxBand,
                                final MoneyWiseTaxBand pLoSavings) {
        theStandard = pStandard;
        hasLoTaxBand = pHasLoTaxBand;
        theLoSavings = pLoSavings;
    }

    /**
     * Constructor.
     * @param pSource the source taxBands
     */
    protected MoneyWiseUKTaxBands(final MoneyWiseUKTaxBands pSource) {
        theStandard = new MoneyWiseTaxBandSet(pSource.getStandardSet());
        hasLoTaxBand = pSource.hasLoTaxBand;
        final MoneyWiseTaxBand mySavings = pSource.getLoSavings();
        theLoSavings = mySavings == null
                ? null
                : new MoneyWiseTaxBand(mySavings);
    }

    /**
     * Obtain the standard taxBands.
     * @return the taxBands
     */
    public MoneyWiseTaxBandSet getStandardSet() {
        return theStandard;
    }

    /**
     * Do we have a Low taxBand?
     * @return true/false
     */
    public Boolean hasLoTaxBand() {
        return hasLoTaxBand;
    }

    /**
     * Obtain the low savings taxBand.
     * @return the taxBands
     */
    public MoneyWiseTaxBand getLoSavings() {
        return theLoSavings;
    }

    /**
     * Obtain the basic rate of income tax.
     * @return the rate
     */
    protected TethysRate getBasicTaxRate() {
        final Iterator<MoneyWiseTaxBand> myIterator = theStandard.iterator();
        if (hasLoTaxBand && myIterator.hasNext()) {
            myIterator.next();
        }
        return myIterator.hasNext()
                ? myIterator.next().getRate()
                : null;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    @Override
    public MetisFieldSet<MoneyWiseUKTaxBands> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
