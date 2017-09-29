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
package net.sourceforge.joceanus.jmoneywise.lethe.tax;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * The Tax Year.
 */
public abstract class MoneyWiseTaxYear
        implements MetisDataFieldItem, MoneyWiseTaxCredit {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MoneyWiseTaxYear.class);

    /**
     * Date Field Id.
     */
    private static final MetisDataField FIELD_DATE = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_END.getValue());

    /**
     * The Date.
     */
    private final TethysDate theYear;

    /**
     * Constructor.
     * @param pDate the tax year end
     */
    protected MoneyWiseTaxYear(final TethysDate pDate) {
        theYear = pDate;
    }

    @Override
    public TethysDate getYearEnd() {
        return theYear;
    }

    /**
     * Obtain the taxAnalysis for the year.
     * @param pPreferences the preference manager
     * @param pTaxSource the tax source
     * @return the tax analysis
     */
    public abstract MoneyWiseTaxAnalysis analyseTaxYear(MetisPreferenceManager pPreferences,
                                                        MoneyWiseTaxSource pTaxSource);

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisDataFieldSet getBaseFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_DATE.equals(pField)) {
            return theYear;
        }

        /* Not recognised */
        return MetisDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return Integer.toString(theYear.getYear());
    }
}
