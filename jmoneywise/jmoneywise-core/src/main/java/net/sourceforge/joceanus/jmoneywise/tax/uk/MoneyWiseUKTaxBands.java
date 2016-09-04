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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
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
     * Low Savings Band Field Id.
     */
    private static final MetisField FIELD_LOSAVINGS = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_LOSAVINGS.getValue());

    /**
     * Standard TaxBandSet.
     */
    private final MoneyWiseTaxBandSet theStandard;

    /**
     * Low Savings TaxBands.
     */
    private final MoneyWiseTaxBand theLoSavings;

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     */
    protected MoneyWiseUKTaxBands(final MoneyWiseTaxBandSet pStandard) {
        this(pStandard, null);
    }

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     * @param pLoSavings the loSavings taxBand
     */
    protected MoneyWiseUKTaxBands(final MoneyWiseTaxBandSet pStandard,
                                  final MoneyWiseTaxBand pLoSavings) {
        theStandard = pStandard;
        theLoSavings = pLoSavings;
    }

    /**
     * Constructor.
     * @param pSource the source taxBands
     */
    protected MoneyWiseUKTaxBands(final MoneyWiseUKTaxBands pSource) {
        theStandard = new MoneyWiseTaxBandSet(pSource.getStandardSet());
        MoneyWiseTaxBand mySavings = pSource.getLoSavings();
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
     * Obtain the low savings taxBand.
     * @return the taxBands
     */
    public MoneyWiseTaxBand getLoSavings() {
        return theLoSavings;
    }

    /**
     * Obtain the base rate of income tax.
     * @return the rate
     */
    protected TethysRate getTaxCreditRate() {
        Iterator<MoneyWiseTaxBand> myIterator = theStandard.iterator();
        return myIterator.hasNext()
                                    ? myIterator.next().getRate()
                                    : null;
    }

    @Override
    public String formatObject() {
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
        if (FIELD_LOSAVINGS.equals(pField)) {
            return theLoSavings == null
                                        ? MetisFieldValue.SKIP
                                        : theLoSavings;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }

}
