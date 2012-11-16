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
    GROSSSALARY(1, 0, TaxBucket.TRANSSUMM),

    /**
     * Gross Interest Income.
     */
    GROSSINTEREST(2, 1, TaxBucket.TRANSSUMM),

    /**
     * Gross Dividend Income.
     */
    GROSSDIVIDEND(3, 2, TaxBucket.TRANSSUMM),

    /**
     * Gross Unit Trust Dividend Income.
     */
    GROSSUTDIVS(4, 3, TaxBucket.TRANSSUMM),

    /**
     * Gross Rental Income.
     */
    GROSSRENTAL(5, 4, TaxBucket.TRANSSUMM),

    /**
     * Gross Taxable gains.
     */
    GROSSTAXGAINS(6, 5, TaxBucket.TRANSSUMM),

    /**
     * Gross Capital gains.
     */
    GROSSCAPGAINS(7, 6, TaxBucket.TRANSSUMM),

    /**
     * Total Tax Paid.
     */
    TAXPAID(8, 7, TaxBucket.TRANSSUMM),

    /**
     * Market Growth/Shrinkage.
     */
    MARKET(9, 8, TaxBucket.TRANSSUMM),

    /**
     * Tax Free Income.
     */
    TAXFREE(10, 9, TaxBucket.TRANSSUMM),

    /**
     * Gross Expense.
     */
    EXPENSE(11, 10, TaxBucket.TRANSSUMM),

    /**
     * Virtual Income.
     */
    VIRTUAL(12, 11, TaxBucket.TRANSSUMM),

    /**
     * Non-Core Income.
     */
    NONCORE(13, 12, TaxBucket.TRANSSUMM),

    /**
     * Profit on Year.
     */
    PROFITLOSS(14, 0, TaxBucket.TRANSTOTAL),

    /**
     * Core Income after tax ignoring market movements and inheritance.
     */
    COREINCOME(15, 1, TaxBucket.TRANSTOTAL),

    /**
     * Profit on year after ignoring market movements and inheritance.
     */
    COREPROFITLOSS(16, 2, TaxBucket.TRANSTOTAL),

    /**
     * Gross Income.
     */
    GROSSINCOME(17, 0, TaxBucket.TAXDETAIL),

    /**
     * Original Allowance.
     */
    ORIGALLOW(18, 1, TaxBucket.TAXDETAIL),

    /**
     * Adjusted Allowance.
     */
    ADJALLOW(19, 2, TaxBucket.TAXDETAIL),

    /**
     * High Tax Band.
     */
    HITAXBAND(20, 3, TaxBucket.TAXDETAIL),

    /**
     * Salary at nil-rate.
     */
    SALARYFREE(21, 4, TaxBucket.TAXDETAIL),

    /**
     * Salary at low-rate.
     */
    SALARYLO(22, 5, TaxBucket.TAXDETAIL),

    /**
     * Salary at basic-rate.
     */
    SALARYBASIC(23, 6, TaxBucket.TAXDETAIL),

    /**
     * Salary at high-rate.
     */
    SALARYHI(24, 7, TaxBucket.TAXDETAIL),

    /**
     * Salary at additional-rate.
     */
    SALARYADD(25, 8, TaxBucket.TAXDETAIL),

    /**
     * Rental at nil-rate.
     */
    RENTALFREE(26, 9, TaxBucket.TAXDETAIL),

    /**
     * Rental at low-rate.
     */
    RENTALLO(27, 10, TaxBucket.TAXDETAIL),

    /**
     * Rental at basic-rate.
     */
    RENTALBASIC(28, 11, TaxBucket.TAXDETAIL),

    /**
     * Rental at high-rate.
     */
    RENTALHI(29, 12, TaxBucket.TAXDETAIL),

    /**
     * Rental at additional-rate.
     */
    RENTALADD(30, 13, TaxBucket.TAXDETAIL),

    /**
     * Interest at nil-rate.
     */
    INTERESTFREE(31, 14, TaxBucket.TAXDETAIL),

    /**
     * Interest at low-rate.
     */
    INTERESTLO(32, 15, TaxBucket.TAXDETAIL),

    /**
     * Interest at basic-rate.
     */
    INTERESTBASIC(33, 16, TaxBucket.TAXDETAIL),

    /**
     * Interest at high-rate.
     */
    INTERESTHI(34, 17, TaxBucket.TAXDETAIL),

    /**
     * Interest at additional-rate.
     */
    INTERESTADD(35, 18, TaxBucket.TAXDETAIL),

    /**
     * Dividends at basic-rate.
     */
    DIVIDENDBASIC(36, 19, TaxBucket.TAXDETAIL),

    /**
     * Dividends at high-rate.
     */
    DIVIDENDHI(37, 20, TaxBucket.TAXDETAIL),

    /**
     * Dividends at additional-rate.
     */
    DIVIDENDADD(38, 21, TaxBucket.TAXDETAIL),

    /**
     * Slice at basic-rate.
     */
    SLICEBASIC(39, 22, TaxBucket.TAXDETAIL),

    /**
     * Slice at high-rate.
     */
    SLICEHI(40, 23, TaxBucket.TAXDETAIL),

    /**
     * Slice at additional-rate.
     */
    SLICEADD(41, 24, TaxBucket.TAXDETAIL),

    /**
     * Gains at basic-rate.
     */
    GAINSBASIC(42, 25, TaxBucket.TAXDETAIL),

    /**
     * Gains at high-rate.
     */
    GAINSHI(43, 26, TaxBucket.TAXDETAIL),

    /**
     * Gains at additional-rate.
     */
    GAINSADD(44, 27, TaxBucket.TAXDETAIL),

    /**
     * Capital at nil-rate.
     */
    CAPITALFREE(45, 28, TaxBucket.TAXDETAIL),

    /**
     * Capital at basic-rate.
     */
    CAPITALBASIC(46, 29, TaxBucket.TAXDETAIL),

    /**
     * Capital at high-rate.
     */
    CAPITALHI(47, 30, TaxBucket.TAXDETAIL),

    /**
     * Total Taxation Due on Salary.
     */
    TAXDUESALARY(48, 0, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Rental.
     */
    TAXDUERENTAL(49, 1, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Interest.
     */
    TAXDUEINTEREST(50, 2, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Dividends.
     */
    TAXDUEDIVIDEND(51, 3, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Taxable Gains.
     */
    TAXDUETAXGAINS(52, 4, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Slice.
     */
    TAXDUESLICE(53, 5, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due on Capital Gains.
     */
    TAXDUECAPGAINS(54, 6, TaxBucket.TAXSUMM),

    /**
     * Total Taxation Due.
     */
    TOTALTAXATION(55, 0, TaxBucket.TAXTOTAL),

    /**
     * Taxation Profit (TaxDue-TaxPaid).
     */
    TAXPROFITLOSS(56, 1, TaxBucket.TAXTOTAL);

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
