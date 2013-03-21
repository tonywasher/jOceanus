/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.StaticInterface;

/**
 * Enumeration of Tax Category Classes.
 */
public enum TaxCategoryClass implements StaticInterface {
    /**
     * Gross Salary Income.
     */
    GrossSalary(1, 0, TaxCategorySection.CATSUMM),

    /**
     * Gross Interest Income.
     */
    GrossInterest(2, 1, TaxCategorySection.CATSUMM),

    /**
     * Gross Dividend Income.
     */
    GrossDividend(3, 2, TaxCategorySection.CATSUMM),

    /**
     * Gross Unit Trust Dividend Income.
     */
    GrossUTDividend(4, 3, TaxCategorySection.CATSUMM),

    /**
     * Gross Rental Income.
     */
    GrossRental(5, 4, TaxCategorySection.CATSUMM),

    /**
     * Gross Taxable gains.
     */
    GrossTaxableGains(6, 5, TaxCategorySection.CATSUMM),

    /**
     * Gross Capital gains.
     */
    GrossCapitalGains(7, 6, TaxCategorySection.CATSUMM),

    /**
     * Total Tax Paid.
     */
    TaxPaid(8, 7, TaxCategorySection.CATSUMM),

    /**
     * Market Growth/Shrinkage.
     */
    Market(9, 8, TaxCategorySection.CATSUMM),

    /**
     * Tax Free Income.
     */
    TaxFree(10, 9, TaxCategorySection.CATSUMM),

    /**
     * Gross Expense.
     */
    Expense(11, 10, TaxCategorySection.CATSUMM),

    /**
     * Virtual Income.
     */
    Virtual(12, 11, TaxCategorySection.CATSUMM),

    /**
     * Non-Core Income.
     */
    NonCore(13, 12, TaxCategorySection.CATSUMM),

    /**
     * Profit on Year.
     */
    ProfitLoss(14, 0, TaxCategorySection.CATTOTAL),

    /**
     * Core Income after tax ignoring market movements and inheritance.
     */
    CoreIncome(15, 1, TaxCategorySection.CATTOTAL),

    /**
     * Profit on year after ignoring market movements and inheritance.
     */
    CoreProfitLoss(16, 2, TaxCategorySection.CATTOTAL),

    /**
     * Gross Income.
     */
    GrossIncome(17, 0, TaxCategorySection.TAXDETAIL),

    /**
     * Original Allowance.
     */
    OriginalAllowance(18, 1, TaxCategorySection.TAXDETAIL),

    /**
     * Adjusted Allowance.
     */
    AdjustedAllowance(19, 2, TaxCategorySection.TAXDETAIL),

    /**
     * High Tax Band.
     */
    HiTaxBand(20, 3, TaxCategorySection.TAXDETAIL),

    /**
     * Salary at nil-rate.
     */
    SalaryNilRate(21, 4, TaxCategorySection.TAXDETAIL),

    /**
     * Salary at low-rate.
     */
    SalaryLoRate(22, 5, TaxCategorySection.TAXDETAIL),

    /**
     * Salary at basic-rate.
     */
    SalaryBasicRate(23, 6, TaxCategorySection.TAXDETAIL),

    /**
     * Salary at high-rate.
     */
    SalaryHiRate(24, 7, TaxCategorySection.TAXDETAIL),

    /**
     * Salary at additional-rate.
     */
    SalaryAdditionalRate(25, 8, TaxCategorySection.TAXDETAIL),

    /**
     * Rental at nil-rate.
     */
    RentalNilRate(26, 9, TaxCategorySection.TAXDETAIL),

    /**
     * Rental at low-rate.
     */
    RentalLoRate(27, 10, TaxCategorySection.TAXDETAIL),

    /**
     * Rental at basic-rate.
     */
    RentalBasicRate(28, 11, TaxCategorySection.TAXDETAIL),

    /**
     * Rental at high-rate.
     */
    RentalHiRate(29, 12, TaxCategorySection.TAXDETAIL),

    /**
     * Rental at additional-rate.
     */
    RentalAdditionalRate(30, 13, TaxCategorySection.TAXDETAIL),

    /**
     * Interest at nil-rate.
     */
    InterestNilRate(31, 14, TaxCategorySection.TAXDETAIL),

    /**
     * Interest at low-rate.
     */
    InterestLoRate(32, 15, TaxCategorySection.TAXDETAIL),

    /**
     * Interest at basic-rate.
     */
    InterestBasicRate(33, 16, TaxCategorySection.TAXDETAIL),

    /**
     * Interest at high-rate.
     */
    InterestHiRate(34, 17, TaxCategorySection.TAXDETAIL),

    /**
     * Interest at additional-rate.
     */
    InterestAdditionalRate(35, 18, TaxCategorySection.TAXDETAIL),

    /**
     * Dividends at basic-rate.
     */
    DividendBasicRate(36, 19, TaxCategorySection.TAXDETAIL),

    /**
     * Dividends at high-rate.
     */
    DividendHiRate(37, 20, TaxCategorySection.TAXDETAIL),

    /**
     * Dividends at additional-rate.
     */
    DividendAdditionalRate(38, 21, TaxCategorySection.TAXDETAIL),

    /**
     * Slice at basic-rate.
     */
    SliceBasicRate(39, 22, TaxCategorySection.TAXDETAIL),

    /**
     * Slice at high-rate.
     */
    SliceHiRate(40, 23, TaxCategorySection.TAXDETAIL),

    /**
     * Slice at additional-rate.
     */
    SliceAdditionalRate(41, 24, TaxCategorySection.TAXDETAIL),

    /**
     * Gains at basic-rate.
     */
    GainsBasicRate(42, 25, TaxCategorySection.TAXDETAIL),

    /**
     * Gains at high-rate.
     */
    GainsHiRate(43, 26, TaxCategorySection.TAXDETAIL),

    /**
     * Gains at additional-rate.
     */
    GainsAdditionalRate(44, 27, TaxCategorySection.TAXDETAIL),

    /**
     * Capital at nil-rate.
     */
    CapitalNilRate(45, 28, TaxCategorySection.TAXDETAIL),

    /**
     * Capital at basic-rate.
     */
    CapitalBasicRate(46, 29, TaxCategorySection.TAXDETAIL),

    /**
     * Capital at high-rate.
     */
    CapitalHiRate(47, 30, TaxCategorySection.TAXDETAIL),

    /**
     * Total Taxation Due on Salary.
     */
    TaxDueSalary(48, 0, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Rental.
     */
    TaxDueRental(49, 1, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Interest.
     */
    TaxDueInterest(50, 2, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Dividends.
     */
    TaxDueDividend(51, 3, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Taxable Gains.
     */
    TaxDueTaxableGains(52, 4, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Slice.
     */
    TaxDueSlice(53, 5, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due on Capital Gains.
     */
    TaxDueCapitalGains(54, 6, TaxCategorySection.TAXSUMM),

    /**
     * Total Taxation Due.
     */
    TotalTaxationDue(55, 0, TaxCategorySection.TAXTOTAL),

    /**
     * Taxation Profit (TaxDue-TaxPaid).
     */
    TaxProfitLoss(56, 1, TaxCategorySection.TAXTOTAL);

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

    /**
     * Constructor.
     * @param uId the id
     * @param uOrder the order
     * @param pSection the section
     */
    private TaxCategoryClass(final int uId,
                             final int uOrder,
                             final TaxCategorySection pSection) {
        /* Set values */
        theId = uId;
        theOrder = pSection.getBase()
                   + uOrder;
        theSection = pSection;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JDataException on error
     */
    public static TaxCategoryClass fromId(final int id) throws JDataException {
        for (TaxCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Tax Class Id: "
                                                      + id);
    }
}
