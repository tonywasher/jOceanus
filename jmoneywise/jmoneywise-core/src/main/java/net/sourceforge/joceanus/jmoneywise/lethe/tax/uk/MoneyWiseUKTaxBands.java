/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * UK TaxBands.
 */
public class MoneyWiseUKTaxBands
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKTaxBands.class.getSimpleName());

    /**
     * StandardSet Field Id.
     */
    private static final MetisField FIELD_STANDARD = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_STANDARD.getValue());

    /**
     * Has Low Tax Band Field Id.
     */
    private static final MetisField FIELD_LOTAXBAND = FIELD_DEFS.declareEqualityField("hasLoTaxBand");

    /**
     * Low Savings Band Field Id.
     */
    private static final MetisField FIELD_LOSAVINGS = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_LOSAVINGS.getValue());

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
    public String formatObject(final MetisDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_STANDARD.equals(pField)) {
            return theStandard;
        }
        if (FIELD_LOTAXBAND.equals(pField)) {
            return hasLoTaxBand
                                ? hasLoTaxBand
                                : MetisDataFieldValue.SKIP;
        }
        if (FIELD_LOSAVINGS.equals(pField)) {
            return theLoSavings == null
                                        ? MetisDataFieldValue.SKIP
                                        : theLoSavings;
        }

        /* Not recognised */
        return MetisDataFieldValue.UNKNOWN;
    }
}
