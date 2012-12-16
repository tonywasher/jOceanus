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
 * Enumeration of Tax Type Classes.
 */
public enum TaxClass implements StaticInterface {
    /**
     * Gross Salary Income.
     */
    GrossSalary(1, 0, TaxBucket.TRANSSUMM),

    /**
     * Gross Interest Income.
     */
    GrossInterest(2, 1, TaxBucket.TRANSSUMM),

    /**
     * Gross Dividend Income.
     */
    GrossDividend(3, 2, TaxBucket.TRANSSUMM),

    /**
     * Gross Unit Trust Dividend Income.
     */
    GrossUTDividend(4, 3, TaxBucket.TRANSSUMM),

    /**
     * Gross Rental Income.
     */
    GrossRental(5, 4, TaxBucket.TRANSSUMM),

    /**
     * Gross Taxable gains.
     */
    GrossTaxableGains(6, 5, TaxBucket.TRANSSUMM),

    /**
     * Gross Capital gains.
     */
    GrossCapitalGains(7, 6, TaxBucket.TRANSSUMM),

    /**
     * Total Tax Paid.
     */
    TaxPaid(8, 7, TaxBucket.TRANSSUMM),

    /**
     * Market Growth/Shrinkage.
     */
    Market(9, 8, TaxBucket.TRANSSUMM),

    /**
     * Tax Free Income.
     */
    TaxFree(10, 9, TaxBucket.TRANSSUMM),

    /**
     * Gross Expense.
     */
    Expense(11, 10, TaxBucket.TRANSSUMM),

    /**
     * Virtual Income.
     */
    Virtual(12, 11, TaxBucket.TRANSSUMM),

    /**
     * Non-Core Income.
     */
    NonCore(13, 12, TaxBucket.TRANSSUMM),

    /**
     * Profit on Year.
     */
    ProfitLoss(14, 0, TaxBucket.TRANSTOTAL),

    /**
     * Core Income after tax ignoring market movements and inheritance.
     */
    CoreIncome(15, 1, TaxBucket.TRANSTOTAL),

    /**
     * Profit on year after ignoring market movements and inheritance.
     */
    CoreProfitLoss(16, 2, TaxBucket.TRANSTOTAL),

    /**
     * Gross Income.
     */
    GrossIncome(17, 0, TaxBucket.TAXDETAIL),

    /**
     * Original Allowance.
     */
    OriginalAllowance(18, 1, TaxBucket.TAXDETAIL),

    /**
     * Adjusted Allowance.
     */
    AdjustedAllowance(19, 2, TaxBucket.TAXDETAIL),

    /**
     * High Tax Band.
     */
    HiTaxBand(20, 3, TaxBucket.TAXDETAIL),

    /**
     * Salary at nil-rate.
     */
    SalaryNilRate(21, 4, TaxBucket.TAXDETAIL),

    /**
     * Salary at low-rate.
     */
    SalaryLoRate(22, 5, TaxBucket.TAXDETAIL),

    /**
     * Salary at basic-rate.
     */
    SalaryBasicRate(23, 6, TaxBucket.TAXDETAIL),

    /**
     * Salary at high-rate.
     */
    SalaryHiRate(24, 7, TaxBucket.TAXDETAIL),

    /**
     * Salary at additional-rate.
     */
    SalaryAdditionalRate(25, 8, TaxBucket.TAXDETAIL),

    /**
     * Rental at nil-rate.
     */
    RentalNilRate(26, 9, TaxBucket.TAXDETAIL),

    /**
     * Rental at low-rate.
     */
    RentalLoRate(27, 10, TaxBucket.TAXDETAIL),

    /**
     * Rental at basic-rate.
     */
    RentalBasicRate(28, 11, TaxBucket.TAXDETAIL),

    /**
     * Rental at high-rate.
     */
    RentalHiRate(29, 12, TaxBucket.TAXDETAIL),

    /**
     * Rental at additional-rate.
     */
    RentalAdditionalRate(30, 13, TaxBucket.TAXDETAIL),

    /**
     * Interest at nil-rate.
     */
    InterestNilRate(31, 14, TaxBucket.TAXDETAIL),

    /**
     * Interest at low-rate.
     */
    InterestLoRate(32, 15, TaxBucket.TAXDETAIL),

    /**
     * Interest at basic-rate.
     */
    InterestBasicRate(33, 16, TaxBucket.TAXDETAIL),

    /**
     * Interest at high-rate.
     */
    InterestHiRate(34, 17, TaxBucket.TAXDETAIL),

    /**
     * Interest at additional-rate.
     */
    InterestAdditionalRate(35, 18, TaxBucket.TAXDETAIL),

    /**
     * Dividends at basic-rate.
     */
    DividendBasicRate(36, 19, TaxBucket.TAXDETAIL),

    /**
     * Dividends at high-rate.
     */
    DividendHiRate(37, 20, TaxBucket.TAXDETAIL),

    /**
     * Dividends at additional-rate.
     */
    DividendAdditionalRate(38, 21, TaxBucket.TAXDETAIL),

    /**
     * Slice at basic-rate.
     */
    SliceBasicRate(39, 22, TaxBucket.TAXDETAIL),

    /**
     * Slice at high-rate.
     */
    SliceHiRate(40, 23, TaxBucket.TAXDETAIL),

    /**
     * Slice at additional-rate.
     */
    SliceAdditionalRate(41, 24, TaxBucket.TAXDETAIL),

    /**
     * Gains at basic-rate.
     */
    GainsBasicRate(42, 25, TaxBucket.TAXDETAIL),

    /**
     * Gains at high-rate.
     */
    GainsHiRate(43, 26, TaxBucket.TAXDETAIL),

    /**
     * Gains at additional-rate.
     */
    GainsAdditionalRate(44, 27, TaxBucket.TAXDETAIL),

    /**
     * Capital at nil-rate.
     */
    CapitalNilRate(45, 28, TaxBucket.TAXDETAIL),

    /**
     * Capital at basic-rate.
     */
    CapitalBasicRate(46, 29, TaxBucket.TAXDETAIL),

    /**
     * Capital at high-rate.
     */
    CapitalHiRate(47, 30, TaxBucket.TAXDETAIL),

    /**
     * Total Taxation Due on Salary.
     */
    TaxDueSalary(48, 0, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Rental.
     */
    TaxDueRental(49, 1, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Interest.
     */
    TaxDueInterest(50, 2, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Dividends.
     */
    TaxDueDividend(51, 3, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Taxable Gains.
     */
    TaxDueTaxableGains(52, 4, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Slice.
     */
    TaxDueSlice(53, 5, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Capital Gains.
     */
    TaxDueCapitalGains(54, 6, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due.
     */
    TotalTaxationDue(55, 0, TaxBucket.TAXTOTAL),

    /**
     * Taxation Profit (TaxDue-TaxPaid).
     */
    TaxProfitLoss(56, 1, TaxBucket.TAXTOTAL);

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

    /**
     * Class Bucket.
     */
    private TaxBucket theBucket = null;

    @Override
    public int getClassId() {
        return theId;
    }

    /**
     * Obtain Class Bucket.
     * @return the class bucket
     */
    public TaxBucket getClassBucket() {
        return theBucket;
    }

    @Override
    public int getOrder() {
        return theOrder;
    }

    /**
     * Constructor.
     * @param uId the id
     * @param uOrder the order
     * @param pBucket the bucket
     */
    private TaxClass(final int uId,
                     final int uOrder,
                     final TaxBucket pBucket) {
        /* Set values */
        theId = uId;
        theOrder = pBucket.getBase()
                   + uOrder;
        theBucket = pBucket;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JDataException on error
     */
    public static TaxClass fromId(final int id) throws JDataException {
        for (TaxClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Tax Class Id: "
                                                      + id);
    }
}
