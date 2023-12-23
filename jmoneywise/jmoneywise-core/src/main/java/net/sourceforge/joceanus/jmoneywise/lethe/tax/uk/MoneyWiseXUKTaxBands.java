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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet.MoneyWiseXTaxBand;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * UK TaxBands.
 */
public class MoneyWiseXUKTaxBands
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXUKTaxBands> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKTaxBands.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_STANDARD, MoneyWiseXUKTaxBands::getStandardSet);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_HASLOTAXBAND, MoneyWiseXUKTaxBands::hasLoTaxBand);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_LOSAVINGS, MoneyWiseXUKTaxBands::getLoSavings);
    }

    /**
     * Standard TaxBandSet.
     */
    private final MoneyWiseXTaxBandSet theStandard;

    /**
     * Low TaxBand present.
     */
    private final Boolean hasLoTaxBand;

    /**
     * Low Savings TaxBands.
     */
    private final MoneyWiseXTaxBand theLoSavings;

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     */
    protected MoneyWiseXUKTaxBands(final MoneyWiseXTaxBandSet pStandard) {
        this(pStandard, Boolean.FALSE);
    }

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     * @param pHasLoTaxBand do we have a low tax band?
     */
    protected MoneyWiseXUKTaxBands(final MoneyWiseXTaxBandSet pStandard,
                                   final Boolean pHasLoTaxBand) {
        this(pStandard, pHasLoTaxBand, null);
    }

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     * @param pLoSavings the loSavings taxBand
     */
    protected MoneyWiseXUKTaxBands(final MoneyWiseXTaxBandSet pStandard,
                                   final MoneyWiseXTaxBand pLoSavings) {
        this(pStandard, Boolean.FALSE, pLoSavings);
    }

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     * @param pHasLoTaxBand do we have a low tax band?
     * @param pLoSavings the loSavings taxBand
     */
    private MoneyWiseXUKTaxBands(final MoneyWiseXTaxBandSet pStandard,
                                 final Boolean pHasLoTaxBand,
                                 final MoneyWiseXTaxBand pLoSavings) {
        theStandard = pStandard;
        hasLoTaxBand = pHasLoTaxBand;
        theLoSavings = pLoSavings;
    }

    /**
     * Constructor.
     * @param pSource the source taxBands
     */
    protected MoneyWiseXUKTaxBands(final MoneyWiseXUKTaxBands pSource) {
        theStandard = new MoneyWiseXTaxBandSet(pSource.getStandardSet());
        hasLoTaxBand = pSource.hasLoTaxBand;
        final MoneyWiseXTaxBand mySavings = pSource.getLoSavings();
        theLoSavings = mySavings == null
                                         ? null
                                         : new MoneyWiseXTaxBand(mySavings);
    }

    /**
     * Obtain the standard taxBands.
     * @return the taxBands
     */
    public MoneyWiseXTaxBandSet getStandardSet() {
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
    public MoneyWiseXTaxBand getLoSavings() {
        return theLoSavings;
    }

    /**
     * Obtain the basic rate of income tax.
     * @return the rate
     */
    protected TethysRate getBasicTaxRate() {
        final Iterator<MoneyWiseXTaxBand> myIterator = theStandard.iterator();
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
    public MetisFieldSet<MoneyWiseXUKTaxBands> getDataFieldSet() {
        return FIELD_DEFS;
    }
}
