/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
     * Other Income.
     */
    OtherIncome(4, 3),

    /**
     * Rental Income.
     */
    RentalIncome(5, 4),

    /**
     * Inheritance.
     */
    Inherited(6, 5),

    /**
     * Interest on Loans.
     */
    LoanInterest(7, 6),

    /**
     * Transfer.
     */
    Transfer(8, 7),

    /**
     * Stock Adjustment.
     */
    StockAdjust(9, 8),

    /**
     * Stock Split.
     */
    StockSplit(10, 9),

    /**
     * Stock Demerger.
     */
    StockDeMerger(11, 10),

    /**
     * Stock Takeover.
     */
    StockTakeOver(12, 11),

    /**
     * Stock Rights Taken.
     */
    StockRightsTaken(13, 12),

    /**
     * Stock Rights Waived.
     */
    StockRightsWaived(14, 13),

    /**
     * Expense.
     */
    Expense(15, 14),

    /**
     * Write Off.
     */
    WriteOff(16, 15),

    /**
     * Tax Relief.
     */
    TaxRelief(17, 16),

    /**
     * Tax Settlement.
     */
    TaxSettlement(18, 17),

    /**
     * Opening Balance.
     */
    OpeningBalance(19, 18),

    /**
     * Tax Free Interest.
     */
    TaxFreeInterest(20, 19),

    /**
     * Tax Free Dividend.
     */
    TaxFreeDividend(21, 20),

    /**
     * Unit Trust Dividend Income.
     */
    UnitTrustDividend(22, 21),

    /**
     * Taxable Gain.
     */
    TaxableGain(23, 22),

    /**
     * Capital Gain.
     */
    CapitalGain(24, 23),

    /**
     * Capital Loss.
     */
    CapitalLoss(25, 24),

    /**
     * Market Growth.
     */
    MarketGrowth(26, 25),

    /**
     * Market Shrinkage.
     */
    MarketShrink(27, 26),

    /**
     * Endowment payment.
     */
    Endowment(28, 27),

    /**
     * Tax Credit.
     * <p>
     * This is a singular category catching tax credits associated with an event.
     */
    TaxCredit(29, 28),

    /**
     * National Insurance.
     * <p>
     * This is a singular category catching national insurance payments associated with an event.
     */
    NatInsurance(30, 29),

    /**
     * Benefit.
     * <p>
     * This is a singular category catching benefit payments associated with an event.
     */
    Benefit(31, 30),

    /**
     * CharityDonation.
     * <p>
     * This is a singular category catching charity donations associated with an event.
     */
    CharityDonation(32, 31),

    /**
     * Category.
     * <p>
     * This is used for categories which simply own a set of sub-categories and is used purely for reporting purposes.
     */
    Category(33, 32),

    /**
     * Totals.
     * <p>
     * This is used for the total of all non-transfer categories and is used purely for reporting purposes.
     */
    Totals(34, 33);

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
     * Determine whether the CategoryType is hidden type.
     * @return <code>true</code> if the category is hidden, <code>false</code> otherwise.
     */
    public boolean isHiddenType() {
        switch (this) {
            case UnitTrustDividend:
            case TaxFreeDividend:
            case TaxFreeInterest:
            case MarketShrink:
            case MarketGrowth:
            case TaxCredit:
            case NatInsurance:
            case Benefit:
            case CapitalGain:
            case CapitalLoss:
            case TaxableGain:
            case Endowment:
            case OpeningBalance:
            case Category:
            case Totals:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the category type is singular.
     * @return <code>true</code> if the event category type is singular, <code>false</code> otherwise.
     */
    public boolean isSingular() {
        switch (this) {
            case UnitTrustDividend:
            case TaxFreeDividend:
            case TaxFreeInterest:
            case OpeningBalance:
            case MarketShrink:
            case MarketGrowth:
            case TaxCredit:
            case NatInsurance:
            case Benefit:
            case CharityDonation:
            case CapitalGain:
            case CapitalLoss:
            case TaxableGain:
            case Endowment:
            case Totals:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the CategoryType should have a tax credit.
     * @return <code>true</code> if the event should have a tax credit, <code>false</code> otherwise.
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
     * Determine whether the CategoryType is an income.
     * @return <code>true</code> if the category is income, <code>false</code> otherwise.
     */
    protected boolean isIncome() {
        switch (this) {
            case TaxedIncome:
            case OtherIncome:
            case Interest:
            case Dividend:
            case UnitTrustDividend:
            case Inherited:
            case RentalIncome:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the CategoryType is dilutable.
     * @return <code>true</code> if the category is dilutable, <code>false</code> otherwise.
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

    /**
     * Determine whether the CategoryType is dividend.
     * @return <code>true</code> if the category is dividend, <code>false</code> otherwise.
     */
    public boolean isDividend() {
        switch (this) {
            case Dividend:
            case UnitTrustDividend:
            case TaxFreeDividend:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the CategoryType is interest.
     * @return <code>true</code> if the category is interest, <code>false</code> otherwise.
     */
    public boolean isInterest() {
        switch (this) {
            case Interest:
            case TaxFreeInterest:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the CategoryType needs debit and credit both to have units.
     * @return <code>true</code> if the category needs dual units, <code>false</code> otherwise.
     */
    public boolean isStockAdjustment() {
        switch (this) {
            case StockSplit:
            case StockAdjust:
            case StockDeMerger:
            case StockTakeOver:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the CategoryType needs zero Amount.
     * @return <code>true</code> if the category needs zero amount, <code>false</code> otherwise.
     */
    public boolean needsZeroAmount() {
        switch (this) {
            case StockSplit:
            case StockAdjust:
            case StockDeMerger:
            case StockTakeOver:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the EventCategoryType can be parent categories.
     * @return <code>true</code> if the event category type can parent categories, <code>false</code> otherwise.
     */
    public boolean canParentCategory() {
        switch (this) {
            case Category:
            case Totals:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this event category a transfer?
     * @return true/false
     */
    public boolean isTransfer() {
        switch (this) {
            case Transfer:
            case Endowment:
            case StockAdjust:
            case StockSplit:
            case StockDeMerger:
            case StockTakeOver:
            case StockRightsWaived:
            case StockRightsTaken:
                return true;
            default:
                return false;
        }
    }

}
