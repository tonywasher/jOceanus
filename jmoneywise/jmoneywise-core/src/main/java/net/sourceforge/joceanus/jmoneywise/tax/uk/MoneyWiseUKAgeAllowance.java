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

import java.time.Month;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseMarginalReduction;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * AgeAdapted UK Tax Allowance.
 */
public class MoneyWiseUKAgeAllowance
        extends MoneyWiseUKBasicAllowance {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKAgeAllowance.class.getSimpleName(), MoneyWiseUKBasicAllowance.getBaseFields());

    /**
     * LoAgeAllowance Field Id.
     */
    private static final MetisField FIELD_LOAGEALLOWANCE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.ALLOWANCE_LOAGE.getValue());

    /**
     * HiAgeAllowance Field Id.
     */
    private static final MetisField FIELD_HIAGEALLOWANCE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.ALLOWANCE_HIAGE.getValue());

    /**
     * AgeAllowanceLimit Field Id.
     */
    private static final MetisField FIELD_AGEALLOWLIMIT = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.LIMIT_AGEALLOWANCE.getValue());

    /**
     * Age Allowance minimum.
     */
    private static final TethysDate BIRTHDAY_MINIMUM = TethysFiscalYear.UK.endOfYear(new TethysDate(1948, Month.JANUARY, 1));

    /**
     * HiAge Allowance minimum.
     */
    private static final TethysDate HI_BIRTHDAY_MINIMUM = TethysFiscalYear.UK.endOfYear(new TethysDate(1938, Month.JANUARY, 1));

    /**
     * Low Age Limit.
     */
    private static final int LO_AGE_BOUNDARY = 65;

    /**
     * Hi Age Limit.
     */
    private static final int HI_AGE_BOUNDARY = 75;

    /**
     * Old Hi Age Limit.
     */
    private static final int OLD_HI_AGE_BOUNDARY = 80;

    /**
     * HiAgeBoundary.
     */
    private final int theHiAgeBoundary;

    /**
     * LoAgeAllowance.
     */
    private final TethysMoney theLoAgeAllowance;

    /**
     * HiAgeAllowance.
     */
    private final TethysMoney theHiAgeAllowance;

    /**
     * AgeAllowanceLimit.
     */
    private final TethysMoney theAgeAllowanceLimit;

    /**
     * Constructor.
     * @param pAllowance the allowance
     * @param pRentalAllowance the rental allowance
     * @param pCapitalAllowance the capital allowance
     * @param pLoAgeAllowance the low age allowance
     * @param pHiAgeAllowance the high age allowance
     * @param pAgeAllowanceLimit the age allowance limit
     * @param pReduction the marginal reduction
     */
    protected MoneyWiseUKAgeAllowance(final TethysMoney pAllowance,
                                      final TethysMoney pRentalAllowance,
                                      final TethysMoney pCapitalAllowance,
                                      final TethysMoney pLoAgeAllowance,
                                      final TethysMoney pHiAgeAllowance,
                                      final TethysMoney pAgeAllowanceLimit,
                                      final MoneyWiseMarginalReduction pReduction) {
        super(pAllowance, pRentalAllowance, pCapitalAllowance, pReduction);
        theLoAgeAllowance = pLoAgeAllowance;
        theHiAgeAllowance = pHiAgeAllowance;
        theAgeAllowanceLimit = pAgeAllowanceLimit;
        theHiAgeBoundary = MoneyWiseMarginalReduction.ONEINTWO.equals(pReduction)
                                                                                  ? HI_AGE_BOUNDARY
                                                                                  : OLD_HI_AGE_BOUNDARY;
    }

    /**
     * Constructor.
     * @param pAllowance the allowance
     * @param pRentalAllowance the rental allowance
     * @param pCapitalAllowance the capital allowance
     * @param pLoAgeAllowance the low age allowance
     * @param pHiAgeAllowance the high age allowance
     * @param pAgeAllowanceLimit the age allowance limit
     */
    protected MoneyWiseUKAgeAllowance(final TethysMoney pAllowance,
                                      final TethysMoney pRentalAllowance,
                                      final TethysMoney pCapitalAllowance,
                                      final TethysMoney pLoAgeAllowance,
                                      final TethysMoney pHiAgeAllowance,
                                      final TethysMoney pAgeAllowanceLimit) {
        this(pAllowance, pRentalAllowance, pCapitalAllowance, pLoAgeAllowance,
                pHiAgeAllowance, pAgeAllowanceLimit, MoneyWiseMarginalReduction.ONEINTWO);
    }

    /**
     * Constructor.
     * @param pAllowance the allowance
     * @param pRentalAllowance the rental allowance
     * @param pCapitalAllowance the capital allowance
     * @param pLoAgeAllowance the low age allowance
     * @param pAgeAllowanceLimit the age allowance limit
     */
    protected MoneyWiseUKAgeAllowance(final TethysMoney pAllowance,
                                      final TethysMoney pRentalAllowance,
                                      final TethysMoney pCapitalAllowance,
                                      final TethysMoney pLoAgeAllowance,
                                      final TethysMoney pAgeAllowanceLimit) {
        this(pAllowance, pRentalAllowance, pCapitalAllowance, pLoAgeAllowance, null, pAgeAllowanceLimit, MoneyWiseMarginalReduction.TWOINTHREE);
    }

    /**
     * Obtain the loAgeAllowance.
     * @return the Allowance
     */
    protected TethysMoney getLoAgeAllowance() {
        return theLoAgeAllowance;
    }

    /**
     * Obtain the hiAgeAllowance.
     * @return the Allowance
     */
    protected TethysMoney getHiAgeAllowance() {
        return theHiAgeAllowance;
    }

    /**
     * Obtain the ageAllowanceLimit.
     * @return the Limit
     */
    protected TethysMoney getAgeAllowanceLimit() {
        return theAgeAllowanceLimit;
    }

    @Override
    protected TethysMoney calculateBasicAllowance(final MoneyWiseUKTaxConfig pConfig) {
        /* Access the client age */
        TethysDate myBirthday = pConfig.getBirthday();
        Integer myAge = pConfig.getClientAge();
        boolean hasAgeAllowance = false;

        /* Determine the allowance */
        TethysMoney myBaseAllowance = getAllowance();
        TethysMoney myAllowance = myBaseAllowance;
        if ((myBirthday.compareTo(HI_BIRTHDAY_MINIMUM) < 0)
            && (theHiAgeAllowance != null)
            && (myAge >= theHiAgeBoundary)) {
            hasAgeAllowance = true;
            myAllowance = theHiAgeAllowance;
        } else if ((myBirthday.compareTo(BIRTHDAY_MINIMUM) < 0)
                   && (myAge >= LO_AGE_BOUNDARY)) {
            hasAgeAllowance = true;
            myAllowance = theLoAgeAllowance;
        }

        /* If we have an age related allowance and we are above the allowance limit */
        TethysMoney myGross = pConfig.getGrossTaxable();
        if (hasAgeAllowance
            && (myGross.compareTo(theAgeAllowanceLimit) > 0)) {
            /* Calculate and apply the reduction */
            TethysMoney myReduction = getMarginalReduction().calculateReduction(myGross, theAgeAllowanceLimit);
            myAllowance = new TethysMoney(myAllowance);
            myAllowance.subtractAmount(myReduction);

            /* If we have reduced below the Base Allowance */
            if (myAllowance.compareTo(myBaseAllowance) < 0) {
                /* Return to the basic allowance */
                hasAgeAllowance = true;
                myAllowance = myBaseAllowance;
            }
        }

        /* Set whether we have an age related allowance */
        pConfig.setHasAgeRelatedAllowance(hasAgeAllowance);

        /* return the allowance */
        return myAllowance;
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_LOAGEALLOWANCE.equals(pField)) {
            return theLoAgeAllowance;
        }
        if (FIELD_HIAGEALLOWANCE.equals(pField)) {
            return theHiAgeAllowance == null
                                             ? MetisFieldValue.SKIP
                                             : theHiAgeAllowance;
        }
        if (FIELD_AGEALLOWLIMIT.equals(pField)) {
            return theAgeAllowanceLimit;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }
}
