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
package net.sourceforge.joceanus.jmoneywise.tax;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * The Tax Year.
 */
public abstract class MoneyWiseTaxYear
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseTaxYear.class.getSimpleName());

    /**
     * Date Field Id.
     */
    private static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXYEAR_END.getValue());

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

    /**
     * Obtain the Year.
     * @return the tax year end
     */
    public TethysDate getYear() {
        return theYear;
    }

    /**
     * Is a taxCredit required for interest/dividend?
     * @return true/false
     */
    public abstract boolean isTaxCreditRequired();

    /**
     * Obtain the taxCredit rate for interest.
     * @return the rate
     */
    public abstract TethysRate getTaxCreditRateForInterest();

    /**
     * Obtain the taxCredit rate for dividend.
     * @return the rate
     */
    public abstract TethysRate getTaxCreditRateForDividend();

    /**
     * Obtain the taxAnalysis for the year.
     * @param pPreferences the preference manager
     * @param pTaxSource the tax source
     * @return the tax analysis
     */
    public abstract MoneyWiseTaxAnalysis analyseTaxYear(final MetisPreferenceManager pPreferences,
                                                        final MoneyWiseTaxSource pTaxSource);

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_DATE.equals(pField)) {
            return theYear;
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
        return Integer.toString(theYear.getYear());
    }
}
