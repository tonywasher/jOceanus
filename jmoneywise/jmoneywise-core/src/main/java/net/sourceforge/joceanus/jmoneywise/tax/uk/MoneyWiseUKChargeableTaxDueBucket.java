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

import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxConfig;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseUKChargeableGainsScheme.MoneyWiseUKSlicedGains;

/**
 * UK Tax Due Bucket holding chargeable Gains Slices.
 */
public class MoneyWiseUKChargeableTaxDueBucket
        extends MoneyWiseTaxDueBucket {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKChargeableTaxDueBucket.class.getSimpleName(), MoneyWiseTaxDueBucket.getBaseFields());

    /**
     * Slices Field Id.
     */
    private static final MetisField FIELD_SLICES = FIELD_DEFS.declareEqualityField("Slices");

    /**
     * The Slices.
     */
    private final MoneyWiseUKSlicedGains theSlices;

    /**
     * Constructor.
     * @param pBasis the tax basis
     * @param pBands the tax bands
     * @param pConfig the tax configuration
     * @param pSlices the slices list
     */
    public MoneyWiseUKChargeableTaxDueBucket(final TaxBasisClass pBasis,
                                             final MoneyWiseTaxBandSet pBands,
                                             final MoneyWiseTaxConfig pConfig,
                                             final MoneyWiseUKSlicedGains pSlices) {
        super(pBasis, pBands, pConfig);
        theSlices = pSlices;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_SLICES.equals(pField)) {
            return theSlices;
        }

        /* Pass through */
        return super.getFieldValue(pField);
    }
}
