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
 * Enumeration of Transaction Type Classes.
 */
public enum TransClass implements StaticInterface {
    /**
     * Taxed Salary Income.
     */
    TAXEDINCOME(1, 0),

    /**
     * Interest Income.
     */
    INTEREST(2, 1),

    /**
     * Dividend Income.
     */
    DIVIDEND(3, 2),

    /**
     * Unit Trust Dividend Income.
     */
    UNITTRUSTDIVIDEND(4, 33),

    /**
     * Taxable Gain.
     */
    TAXABLEGAIN(5, 18),

    /**
     * Capital Gain.
     */
    CAPITALGAIN(6, 36),

    /**
     * Capital Loss.
     */
    CAPITALLOSS(7, 37),

    /**
     * Tax Free Interest.
     */
    TAXFREEINTEREST(8, 34),

    /**
     * Tax Free Dividend.
     */
    TAXFREEDIVIDEND(9, 35),

    /**
     * Tax Free Income.
     */
    TAXFREEINCOME(10, 3),

    /**
     * Benefit.
     */
    BENEFIT(11, 7),

    /**
     * Inheritance.
     */
    INHERITED(12, 4),

    /**
     * Market Growth.
     */
    MARKETGROWTH(13, 31),

    /**
     * Market Shrinkage.
     */
    MARKETSHRINK(14, 32),

    /**
     * Expense.
     */
    EXPENSE(15, 22),

    /**
     * Recovered Expense.
     */
    RECOVERED(16, 9),

    /**
     * Transfer.
     */
    TRANSFER(17, 19),

    /**
     * Admin charge.
     */
    ADMINCHARGE(18, 12),

    /**
     * Stock Split.
     */
    STOCKSPLIT(19, 13),

    /**
     * Stock Demerger.
     */
    STOCKDEMERGER(20, 11),

    /**
     * Stock Rights Taken.
     */
    STOCKRIGHTTAKEN(21, 14),

    /**
     * Stock Rights Waived.
     */
    STOCKRIGHTWAIVED(22, 15),

    /**
     * CashTakeover (For the cash part of a stock and cash takeover).
     */
    CASHTAKEOVER(23, 16),

    /**
     * Stock Takeover (for the stock part of a stock and cash takeover).
     */
    STOCKTAKEOVER(24, 17),

    /**
     * Expense Recovered directly to Cash.
     */
    CASHRECOVERY(25, 20),

    /**
     * Expense paid directly from Cash.
     */
    CASHPAYMENT(26, 21),

    /**
     * Endowment payment.
     */
    ENDOWMENT(27, 23),

    /**
     * Mortgage charge.
     */
    MORTGAGE(28, 24),

    /**
     * Insurance payment.
     */
    INSURANCE(29, 25),

    /**
     * National Insurance.
     */
    NATINSURANCE(30, 28),

    /**
     * Tax Relief.
     */
    TAXRELIEF(31, 10),

    /**
     * Tax Owed.
     */
    TAXOWED(32, 29),

    /**
     * Tax Refund.
     */
    TAXREFUND(33, 8),

    /**
     * Additional taxation.
     */
    EXTRATAX(34, 26),

    /**
     * Interest on Debts.
     */
    DEBTINTEREST(35, 5),

    /**
     * Write Off.
     */
    WRITEOFF(36, 27),

    /**
     * Tax Credit.
     */
    TAXCREDIT(37, 30),

    /**
     * Rental Income.
     */
    RENTALINCOME(38, 6);

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

    @Override
    public int getClassId() {
        return theId;
    }

    @Override
    public int getOrder() {
        return theOrder;
    }

    /**
     * Constructor.
     * @param uId the id
     * @param uOrder the default order.
     */
    private TransClass(final int uId,
                       final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JDataException on error
     */
    public static TransClass fromId(final int id) throws JDataException {
        for (TransClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Transaction Class Id: "
                                                      + id);
    }
}
