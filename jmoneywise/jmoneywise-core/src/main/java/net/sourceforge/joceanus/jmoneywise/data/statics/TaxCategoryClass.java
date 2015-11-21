/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of Tax Category Classes.
 */
public enum TaxCategoryClass implements StaticInterface {
    /**
     * Gross Income.
     */
    GROSSINCOME(1, 0, TaxCategorySection.TAXDETAIL),

    /**
     * Original Allowance.
     */
    ORIGINALALLOWANCE(2, 1, TaxCategorySection.TAXDETAIL),

    /**
     * Adjusted Allowance.
     */
    ADJUSTEDALLOWANCE(3, 2, TaxCategorySection.TAXDETAIL),

    /**
     * High Tax Band.
     */
    HITAXBAND(4, 3, TaxCategorySection.TAXDETAIL),

    /**
     * Salary at nil-rate.
     */
    SALARYNILRATE(5, 4, TaxCategorySection.TAXDETAIL),

    /**
     * Salary at low-rate.
     */
    SALARYLORATE(6, 5, TaxCategorySection.TAXDETAIL),

    /**
     * Salary at basic-rate.
     */
    SALARYBASICRATE(7, 6, TaxCategorySection.TAXDETAIL),

    /**
     * Salary at high-rate.
     */
    SALARYHIRATE(8, 7, TaxCategorySection.TAXDETAIL),

    /**
     * Salary at additional-rate.
     */
    SALARYADDRATE(9, 8, TaxCategorySection.TAXDETAIL),

    /**
     * Rental at nil-rate.
     */
    RENTALNILRATE(10, 9, TaxCategorySection.TAXDETAIL),

    /**
     * Rental at low-rate.
     */
    RENTALLORATE(11, 10, TaxCategorySection.TAXDETAIL),

    /**
     * Rental at basic-rate.
     */
    RENTALBASICRATE(12, 11, TaxCategorySection.TAXDETAIL),

    /**
     * Rental at high-rate.
     */
    RENTALHIRATE(13, 12, TaxCategorySection.TAXDETAIL),

    /**
     * Rental at additional-rate.
     */
    RENTALADDRATE(14, 13, TaxCategorySection.TAXDETAIL),

    /**
     * Interest at nil-rate.
     */
    INTERESTNILRATE(15, 14, TaxCategorySection.TAXDETAIL),

    /**
     * Interest at low-rate.
     */
    INTERESTLORATE(16, 15, TaxCategorySection.TAXDETAIL),

    /**
     * Interest at basic-rate.
     */
    INTERESTBASICRATE(17, 16, TaxCategorySection.TAXDETAIL),

    /**
     * Interest at high-rate.
     */
    INTERESTHIRATE(18, 17, TaxCategorySection.TAXDETAIL),

    /**
     * Interest at additional-rate.
     */
    INTERESTADDRATE(19, 18, TaxCategorySection.TAXDETAIL),

    /**
     * Dividends at basic-rate.
     */
    DIVIDENDBASICRATE(20, 19, TaxCategorySection.TAXDETAIL),

    /**
     * Dividends at high-rate.
     */
    DIVIDENDHIRATE(21, 20, TaxCategorySection.TAXDETAIL),

    /**
     * Dividends at additional-rate.
     */
    DIVIDENDADDRATE(22, 21, TaxCategorySection.TAXDETAIL),

    /**
     * Slice at basic-rate.
     */
    SLICEBASICRATE(23, 22, TaxCategorySection.TAXDETAIL),

    /**
     * Slice at high-rate.
     */
    SLICEHIRATE(24, 23, TaxCategorySection.TAXDETAIL),

    /**
     * Slice at additional-rate.
     */
    SLICEADDRATE(25, 24, TaxCategorySection.TAXDETAIL),

    /**
     * Gains at basic-rate.
     */
    GAINSBASICRATE(26, 25, TaxCategorySection.TAXDETAIL),

    /**
     * Gains at high-rate.
     */
    GAINSHIRATE(27, 26, TaxCategorySection.TAXDETAIL),

    /**
     * Gains at additional-rate.
     */
    GAINSADDRATE(28, 27, TaxCategorySection.TAXDETAIL),

    /**
     * Capital at nil-rate.
     */
    CAPITALNILRATE(29, 28, TaxCategorySection.TAXDETAIL),

    /**
     * Capital at basic-rate.
     */
    CAPITALBASICRATE(30, 29, TaxCategorySection.TAXDETAIL),

    /**
     * Capital at high-rate.
     */
    CAPITALHIRATE(31, 30, TaxCategorySection.TAXDETAIL),

    /**
     * Total Taxation Due on Salary.
     */
    TAXDUESALARY(32, 0, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Rental.
     */
    TAXDUERENTAL(33, 1, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Interest.
     */
    TAXDUEINTEREST(34, 2, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Dividends.
     */
    TAXDUEDIVIDEND(35, 3, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Taxable Gains.
     */
    TAXDUETAXGAINS(36, 4, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Slice.
     */
    TAXDUESLICE(37, 5, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Capital Gains.
     */
    TAXDUECAPITAL(38, 6, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due.
     */
    TOTALTAXATIONDUE(39, 0, TaxCategorySection.TAXTOTAL),

    /**
     * Taxation Profit (TaxDue-TaxPaid).
     */
    TAXPROFITLOSS(40, 1, TaxCategorySection.TAXTOTAL);

    /**
     * The String name.
     */
    private String theName;

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

    /**
     * Class Section.
     */
    private final TaxCategorySection theSection;

    /**
     * Constructor.
     * @param uId the id
     * @param uOrder the order
     * @param pSection the section
     */
    TaxCategoryClass(final int uId,
                     final int uOrder,
                     final TaxCategorySection pSection) {
        /* Set values */
        theId = uId;
        theOrder = pSection.getBase()
                   + uOrder;
        theSection = pSection;
    }

    @Override
    public int getClassId() {
        return theId;
    }

    /**
     * Obtain Class Section.
     * @return the class section
     */
    public TaxCategorySection getClassSection() {
        return theSection;
    }

    @Override
    public int getOrder() {
        return theOrder;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = StaticDataResource.getKeyForTaxCategory(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JOceanusException on error
     */
    public static TaxCategoryClass fromId(final int id) throws JOceanusException {
        for (TaxCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.TAXTYPE.toString() + ":" + id);
    }
}
