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

import java.time.Month;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXMarginalReduction;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxResource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * AgeAdapted UK Tax Allowance.
 */
public class MoneyWiseXUKAgeAllowance
        extends MoneyWiseXUKBasicAllowance {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXUKAgeAllowance> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKAgeAllowance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.ALLOWANCE_LOAGE, MoneyWiseXUKAgeAllowance::getLoAgeAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.ALLOWANCE_HIAGE, MoneyWiseXUKAgeAllowance::getHiAgeAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.LIMIT_AGEALLOWANCE, MoneyWiseXUKAgeAllowance::getAgeAllowanceLimit);
    }

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
    protected MoneyWiseXUKAgeAllowance(final TethysMoney pAllowance,
                                       final TethysMoney pRentalAllowance,
                                       final TethysMoney pCapitalAllowance,
                                       final TethysMoney pLoAgeAllowance,
                                       final TethysMoney pHiAgeAllowance,
                                       final TethysMoney pAgeAllowanceLimit,
                                       final MoneyWiseXMarginalReduction pReduction) {
        super(pAllowance, pRentalAllowance, pCapitalAllowance, pReduction);
        theLoAgeAllowance = pLoAgeAllowance;
        theHiAgeAllowance = pHiAgeAllowance;
        theAgeAllowanceLimit = pAgeAllowanceLimit;
        theHiAgeBoundary = MoneyWiseXMarginalReduction.ONEINTWO.equals(pReduction)
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
    protected MoneyWiseXUKAgeAllowance(final TethysMoney pAllowance,
                                       final TethysMoney pRentalAllowance,
                                       final TethysMoney pCapitalAllowance,
                                       final TethysMoney pLoAgeAllowance,
                                       final TethysMoney pHiAgeAllowance,
                                       final TethysMoney pAgeAllowanceLimit) {
        this(pAllowance, pRentalAllowance, pCapitalAllowance, pLoAgeAllowance,
                pHiAgeAllowance, pAgeAllowanceLimit, MoneyWiseXMarginalReduction.ONEINTWO);
    }

    /**
     * Constructor.
     * @param pAllowance the allowance
     * @param pRentalAllowance the rental allowance
     * @param pCapitalAllowance the capital allowance
     * @param pLoAgeAllowance the low age allowance
     * @param pAgeAllowanceLimit the age allowance limit
     */
    protected MoneyWiseXUKAgeAllowance(final TethysMoney pAllowance,
                                       final TethysMoney pRentalAllowance,
                                       final TethysMoney pCapitalAllowance,
                                       final TethysMoney pLoAgeAllowance,
                                       final TethysMoney pAgeAllowanceLimit) {
        this(pAllowance, pRentalAllowance, pCapitalAllowance, pLoAgeAllowance, null, pAgeAllowanceLimit, MoneyWiseXMarginalReduction.TWOINTHREE);
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
    protected TethysMoney calculateBasicAllowance(final MoneyWiseXUKTaxConfig pConfig) {
        /* Access the client age */
        final TethysDate myBirthday = pConfig.getBirthday();
        final Integer myAge = pConfig.getClientAge();
        boolean hasAgeAllowance = false;

        /* Determine the allowance */
        final TethysMoney myBaseAllowance = getAllowance();
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
        final TethysMoney myGross = pConfig.getGrossTaxable();
        if (hasAgeAllowance
            && (myGross.compareTo(theAgeAllowanceLimit) > 0)) {
            /* Calculate and apply the reduction */
            final TethysMoney myReduction = getMarginalReduction().calculateReduction(myGross, theAgeAllowanceLimit);
            myAllowance = new TethysMoney(myAllowance);
            myAllowance.subtractAmount(myReduction);

            /* If we have reduced below the Base Allowance */
            if (myAllowance.compareTo(myBaseAllowance) < 0) {
                /* Return to the basic allowance */
                hasAgeAllowance = false;
                myAllowance = myBaseAllowance;
            }
        }

        /* Set whether we have an age related allowance */
        pConfig.setHasAgeRelatedAllowance(hasAgeAllowance);

        /* return the allowance */
        return myAllowance;
    }

    @Override
    public MetisFieldSet<? extends MoneyWiseXUKAgeAllowance> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }
}