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
package net.sourceforge.joceanus.jmoneywise.lethe.tax;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

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

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return Integer.toString(theYear.getYear());
    }
}
