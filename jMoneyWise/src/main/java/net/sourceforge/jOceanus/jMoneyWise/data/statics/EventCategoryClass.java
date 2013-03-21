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
 * Enumeration of EventCategory Classes.
 */
public enum EventCategoryClass implements StaticInterface {
    /**
     * Taxed Salary Income.
     */
    TaxedIncome(1, 0),

    /**
     * Interest Income.
     */
    Interest(2, 1),

    /**
     * Dividend Income.
     */
    Dividend(3, 2),

    /**
     * Unit Trust Dividend Income.
     */
    UnitTrustDividend(4, 33),

    /**
     * Taxable Gain.
     */
    TaxableGain(5, 18),

    /**
     * Capital Gain.
     */
    CapitalGain(6, 36),

    /**
     * Capital Loss.
     */
    CapitalLoss(7, 37),

    /**
     * Tax Free Interest.
     */
    TaxFreeInterest(8, 34),

    /**
     * Tax Free Dividend.
     */
    TaxFreeDividend(9, 35),

    /**
     * Tax Free Income.
     */
    TaxFreeIncome(10, 3),

    /**
     * Benefit.
     */
    Benefit(11, 7),

    /**
     * Inheritance.
     */
    Inherited(12, 4),

    /**
     * Market Growth.
     */
    MarketGrowth(13, 31),

    /**
     * Market Shrinkage.
     */
    MarketShrink(14, 32),

    /**
     * Expense.
     */
    Expense(15, 22),

    /**
     * Recovered Expense.
     */
    Recovered(16, 9),

    /**
     * Transfer.
     */
    Transfer(17, 19),

    /**
     * Admin charge.
     */
    AdminCharge(18, 12),

    /**
     * Stock Split.
     */
    StockSplit(19, 13),

    /**
     * Stock Demerger.
     */
    StockDeMerger(20, 11),

    /**
     * Stock Rights Taken.
     */
    StockRightsTaken(21, 14),

    /**
     * Stock Rights Waived.
     */
    StockRightsWaived(22, 15),

    /**
     * CashTakeover (For the cash part of a stock and cash takeover).
     */
    CashTakeOver(23, 16),

    /**
     * Stock Takeover (for the stock part of a stock and cash takeover).
     */
    StockTakeOver(24, 17),

    /**
     * Expense Recovered directly to Cash.
     */
    CashRecovery(25, 20),

    /**
     * Expense paid directly from Cash.
     */
    CashPayment(26, 21),

    /**
     * Endowment payment.
     */
    Endowment(27, 23),

    /**
     * Mortgage charge.
     */
    Mortgage(28, 24),

    /**
     * Insurance payment.
     */
    Insurance(29, 25),

    /**
     * National Insurance.
     */
    NatInsurance(30, 28),

    /**
     * Tax Relief.
     */
    TaxRelief(31, 10),

    /**
     * Tax Owed.
     */
    TaxOwed(32, 29),

    /**
     * Tax Refund.
     */
    TaxRefund(33, 8),

    /**
     * Additional taxation.
     */
    ExtraTax(34, 26),

    /**
     * Interest on Debts.
     */
    DebtInterest(35, 5),

    /**
     * Write Off.
     */
    WriteOff(36, 27),

    /**
     * Tax Credit.
     */
    TaxCredit(37, 30),

    /**
     * Rental Income.
     */
    RentalIncome(38, 6);

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
    private EventCategoryClass(final int uId,
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
    public static EventCategoryClass fromId(final int id) throws JDataException {
        for (EventCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Transaction Class Id: "
                                                      + id);
    }

    /**
     * Determine whether the TransactionType is a transfer.
     * @return <code>true</code> if the transaction is transfer, <code>false</code> otherwise.
     */
    public boolean isTransfer() {
        return (this == Transfer);
    }

    /**
     * Determine whether the TransactionType is a dividend.
     * @return <code>true</code> if the transaction is dividend, <code>false</code> otherwise.
     */
    public boolean isDividend() {
        return (this == Dividend);
    }

    /**
     * Determine whether the TransactionType is an interest.
     * @return <code>true</code> if the transaction is interest, <code>false</code> otherwise.
     */
    public boolean isInterest() {
        return (this == Interest);
    }

    /**
     * Determine whether the TransactionType is a cash payment.
     * @return <code>true</code> if the transaction is cash payment, <code>false</code> otherwise.
     */
    public boolean isCashPayment() {
        return (this == CashPayment);
    }

    /**
     * Determine whether the TransactionType is a cash recovery.
     * @return <code>true</code> if the transaction is cash recovery, <code>false</code> otherwise.
     */
    public boolean isCashRecovery() {
        return (this == CashRecovery);
    }

    /**
     * Determine whether the TransactionType is a write off.
     * @return <code>true</code> if the transaction is write off, <code>false</code> otherwise.
     */
    protected boolean isWriteOff() {
        return (this == WriteOff);
    }

    /**
     * Determine whether the TransactionType is a inheritance.
     * @return <code>true</code> if the transaction is inheritance, <code>false</code> otherwise.
     */
    protected boolean isInherited() {
        return (this == Inherited);
    }

    /**
     * Determine whether the TransactionType is a tax owed.
     * @return <code>true</code> if the transaction is tax owed, <code>false</code> otherwise.
     */
    protected boolean isTaxOwed() {
        return (this == TaxOwed);
    }

    /**
     * Determine whether the TransactionType is a tax refund.
     * @return <code>true</code> if the transaction is tax refund, <code>false</code> otherwise.
     */
    protected boolean isTaxRefund() {
        return (this == TaxRefund);
    }

    /**
     * Determine whether the TransactionType is a tax relief.
     * @return <code>true</code> if the transaction is tax relief, <code>false</code> otherwise.
     */
    protected boolean isTaxRelief() {
        return (this == TaxRelief);
    }

    /**
     * Determine whether the TransactionType is a debt interest.
     * @return <code>true</code> if the transaction is debt interest, <code>false</code> otherwise.
     */
    protected boolean isDebtInterest() {
        return (this == DebtInterest);
    }

    /**
     * Determine whether the TransactionType is a rental income.
     * @return <code>true</code> if the transaction is rental income, <code>false</code> otherwise.
     */
    protected boolean isRentalIncome() {
        return (this == RentalIncome);
    }

    /**
     * Determine whether the TransactionType is a benefit.
     * @return <code>true</code> if the transaction is benefit, <code>false</code> otherwise.
     */
    protected boolean isBenefit() {
        return (this == Benefit);
    }

    /**
     * Determine whether the TransactionType is a taxable gain.
     * @return <code>true</code> if the transaction is taxable gain, <code>false</code> otherwise.
     */
    public boolean isTaxableGain() {
        return (this == TaxableGain);
    }

    /**
     * Determine whether the TransactionType is a capital gain.
     * @return <code>true</code> if the transaction is capital gain, <code>false</code> otherwise.
     */
    public boolean isCapitalGain() {
        return (this == CapitalGain);
    }

    /**
     * Determine whether the TransactionType is a capital loss.
     * @return <code>true</code> if the transaction is capital loss, <code>false</code> otherwise.
     */
    public boolean isCapitalLoss() {
        return (this == CapitalLoss);
    }

    /**
     * Determine whether the TransactionType is a stock split.
     * @return <code>true</code> if the transaction is stock split, <code>false</code> otherwise.
     */
    public boolean isStockSplit() {
        return (this == StockSplit);
    }

    /**
     * Determine whether the TransactionType is an admin charge.
     * @return <code>true</code> if the transaction is admin charge, <code>false</code> otherwise.
     */
    public boolean isAdminCharge() {
        return (this == AdminCharge);
    }

    /**
     * Determine whether the TransactionType is a stock demerger.
     * @return <code>true</code> if the transaction is stock demerger, <code>false</code> otherwise.
     */
    public boolean isStockDemerger() {
        return (this == StockDeMerger);
    }

    /**
     * Determine whether the TransactionType is a stock right taken.
     * @return <code>true</code> if the transaction is stock right taken, <code>false</code> otherwise.
     */
    public boolean isStockRightTaken() {
        return (this == StockRightsTaken);
    }

    /**
     * Determine whether the TransactionType is a stock right waived.
     * @return <code>true</code> if the transaction is stock right waived, <code>false</code> otherwise.
     */
    public boolean isStockRightWaived() {
        return (this == StockRightsWaived);
    }

    /**
     * Determine whether the TransactionType is a cash takeover.
     * @return <code>true</code> if the transaction is cash takeover, <code>false</code> otherwise.
     */
    public boolean isCashTakeover() {
        return (this == CashTakeOver);
    }

    /**
     * Determine whether the TransactionType is a stock takeover.
     * @return <code>true</code> if the transaction is stock takeover, <code>false</code> otherwise.
     */
    public boolean isStockTakeover() {
        return (this == StockTakeOver);
    }

    /**
     * Determine whether the TransactionType is a recovery.
     * @return <code>true</code> if the transaction is recovery, <code>false</code> otherwise.
     */
    public boolean isRecovered() {
        switch (this) {
            case Recovered:
            case CashPayment:
            case CashRecovery:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the TransactionType is hidden type.
     * @return <code>true</code> if the transaction is hidden, <code>false</code> otherwise.
     */
    public boolean isHiddenType() {
        switch (this) {
            case UnitTrustDividend:
            case TaxFreeDividend:
            case TaxFreeInterest:
            case MarketShrink:
            case MarketGrowth:
            case TaxCredit:
            case CapitalGain:
            case CapitalLoss:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the TransactionType should have a tax credit.
     * @return <code>true</code> if the transaction should have a tax credit, <code>false</code> otherwise.
     */
    public boolean needsTaxCredit() {
        switch (this) {
            case TaxedIncome:
            case Interest:
            case Dividend:
            case UnitTrustDividend:
            case TaxableGain:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the TransactionType is an income.
     * @return <code>true</code> if the transaction is income, <code>false</code> otherwise.
     */
    protected boolean isIncome() {
        switch (this) {
            case TaxedIncome:
            case TaxFreeIncome:
            case Interest:
            case Dividend:
            case UnitTrustDividend:
            case Recovered:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the TransactionType is an expense.
     * @return <code>true</code> if the transaction is expense, <code>false</code> otherwise.
     */
    protected boolean isExpense() {
        switch (this) {
            case Mortgage:
            case Endowment:
            case ExtraTax:
            case Insurance:
            case Expense:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the TransactionType is dilutable.
     * @return <code>true</code> if the transaction is dilutable, <code>false</code> otherwise.
     */
    public boolean isDilutable() {
        switch (this) {
            case StockSplit:
            case StockDeMerger:
            case StockRightsWaived:
            case StockRightsTaken:
                return true;
            default:
                return false;
        }
    }
}
