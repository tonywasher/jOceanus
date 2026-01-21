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
package net.sourceforge.joceanus.moneywise.tax;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTax.MoneyWiseTaxCredit;

/**
 * The Tax Year.
 */
public abstract class MoneyWiseTaxYear
        implements MetisFieldItem, MoneyWiseTaxCredit {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTaxYear> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTaxYear.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_END, MoneyWiseTaxYear::getYearEnd);
    }

    /**
     * The Date.
     */
    private final OceanusDate theYear;

    /**
     * Constructor.
     *
     * @param pDate the tax year end
     */
    protected MoneyWiseTaxYear(final OceanusDate pDate) {
        theYear = pDate;
    }

    @Override
    public OceanusDate getYearEnd() {
        return theYear;
    }

    /**
     * Obtain the taxAnalysis for the year.
     *
     * @param pPreferences the preference manager
     * @param pTaxSource   the tax source
     * @return the tax analysis
     */
    public abstract MoneyWiseTaxAnalysis analyseTaxYear(MetisPreferenceManager pPreferences,
                                                        MoneyWiseTaxSource pTaxSource);

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return Integer.toString(theYear.getYear());
    }
}
