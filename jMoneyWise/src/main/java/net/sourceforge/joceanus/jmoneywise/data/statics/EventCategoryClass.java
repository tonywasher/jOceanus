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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamodels.data.StaticInterface;

/**
 * Enumeration of EventCategory Classes.
 */
public enum EventCategoryClass implements StaticInterface {
    /**
     * Taxed Salary Income.
     */
    TaxedIncome(1, 0),

    /**
     * Rental Income.
     */
    RentalIncome(2, 1),

    /**
     * RoomRental Income.
     */
    RoomRentalIncome(3, 2),

    /**
     * Interest Income.
     */
    Interest(4, 3),

    /**
     * Dividend Income.
     */
    Dividend(5, 4),

    /**
     * Grant Income.
     */
    GrantIncome(6, 5),

    /**
     * Benefit Income.
     */
    BenefitIncome(7, 6),

    /**
     * Gifted Income.
     */
    GiftedIncome(8, 7),

    /**
     * Inheritance.
     */
    Inherited(9, 8),

    /**
     * Interest earned on Loans.
     */
    LoanInterestEarned(10, 9),

    /**
     * Other Income.
     */
    OtherIncome(11, 10),

    /**
     * Transfer.
     */
    Transfer(12, 11),

    /**
     * Stock Adjustment.
     */
    StockAdjust(13, 12),

    /**
     * Stock Split.
     */
    StockSplit(14, 13),

    /**
     * Stock Demerger.
     */
    StockDeMerger(15, 14),

    /**
     * Stock Takeover.
     */
    StockTakeOver(16, 15),

    /**
     * Stock Rights Taken.
     */
    StockRightsTaken(17, 16),

    /**
     * Stock Rights Waived.
     */
    StockRightsWaived(18, 17),

    /**
     * Stock Options Granted.
     */
    OptionsGrant(19, 18),

    /**
     * Stock Options Vested.
     */
    OptionsVest(20, 19),

    /**
     * Stock Options Exercised.
     */
    OptionsExercise(21, 20),

    /**
     * Stock Options Expired.
     */
    OptionsExpire(22, 21),

    /**
     * Expense.
     */
    Expense(23, 22),

    /**
     * LocalTaxes.
     */
    LocalTaxes(24, 23),

    /**
     * Write Off.
     */
    WriteOff(25, 24),

    /**
     * Interest earned on Loans.
     */
    LoanInterestCharged(26, 25),

    /**
     * Tax Relief.
     */
    TaxRelief(27, 26),

    /**
     * Tax Settlement.
     */
    TaxSettlement(28, 27),

    /**
     * Opening Balance.
     */
    OpeningBalance(29, 28),

    /**
     * Taxed Interest.
     */
    TaxedInterest(30, 29),

    /**
     * Gross Interest.
     */
    GrossInterest(31, 30),

    /**
     * Tax Free Interest.
     */
    TaxFreeInterest(32, 31),

    /**
     * Share Dividend Income.
     */
    ShareDividend(33, 32),

    /**
     * Unit Trust Dividend Income.
     */
    UnitTrustDividend(34, 33),

    /**
     * Tax Free Dividend.
     */
    TaxFreeDividend(35, 34),

    /**
     * Taxable Gain.
     */
    TaxableGain(36, 35),

    /**
     * Capital Gain.
     */
    CapitalGain(37, 36),

    /**
     * TaxFreeCapital Gain.
     */
    TaxFreeGain(38, 37),

    /**
     * Market Growth.
     */
    MarketGrowth(39, 38),

    /**
     * CurrencyFluctuation.
     */
    CurrencyFluctuation(40, 39),

    /**
     * Tax Credit.
     * <p>
     * This is a singular category catching tax credits associated with an event.
     */
    TaxCredit(41, 40),

    /**
     * National Insurance.
     * <p>
     * This is a singular category catching national insurance payments associated with an event.
     */
    NatInsurance(42, 41),

    /**
     * Deemed Benefit.
     * <p>
     * This is a singular category catching deemed benefit payments associated with an event.
     */
    DeemedBenefit(43, 42),

    /**
     * CharityDonation.
     * <p>
     * This is a singular category catching charity donations associated with an event.
     */
    CharityDonation(44, 43),

    /**
     * Income Totals.
     * <p>
     * This is used for categories which simply own a set of income sub-categories and is used purely for reporting purposes.
     */
    IncomeTotals(45, 44),

    /**
     * Expense Totals.
     * <p>
     * This is used for categories which simply own a set of expense sub-categories and is used purely for reporting purposes.
     */
    ExpenseTotals(46, 45),

    /**
     * Totals.
     * <p>
     * This is used for the total of all non-transfer categories and is used purely for reporting purposes.
     */
    Totals(47, 46);

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
            case ShareDividend:
            case UnitTrustDividend:
            case TaxFreeDividend:
            case TaxedInterest:
            case GrossInterest:
            case TaxFreeInterest:
            case MarketGrowth:
            case CurrencyFluctuation:
            case TaxCredit:
            case NatInsurance:
            case DeemedBenefit:
            case CapitalGain:
            case TaxFreeGain:
            case OpeningBalance:
            case IncomeTotals:
            case ExpenseTotals:
            case Totals:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the CategoryType uses debit parent as payee.
     * @return <code>true</code> if the category uses parent payee, <code>false</code> otherwise.
     */
    public boolean isDebitParentPayee() {
        switch (this) {
            case Interest:
            case Dividend:
            case LoanInterestEarned:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the CategoryType uses credit parent as payee.
     * @return <code>true</code> if the category uses parent payee, <code>false</code> otherwise.
     */
    public boolean isCreditParentPayee() {
        switch (this) {
            case RentalIncome:
            case RoomRentalIncome:
            case WriteOff:
            case LoanInterestCharged:
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
            case ShareDividend:
            case UnitTrustDividend:
            case TaxFreeDividend:
            case TaxedInterest:
            case GrossInterest:
            case TaxFreeInterest:
            case OpeningBalance:
            case MarketGrowth:
            case CurrencyFluctuation:
            case TaxCredit:
            case NatInsurance:
            case DeemedBenefit:
            case CharityDonation:
            case CapitalGain:
            case TaxFreeGain:
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
            case BenefitIncome:
            case Interest:
            case TaxedInterest:
            case Dividend:
            case ShareDividend:
            case UnitTrustDividend:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the CategoryType is an income.
     * @return <code>true</code> if the category is income, <code>false</code> otherwise.
     */
    public boolean isIncome() {
        switch (this) {
            case TaxedIncome:
            case BenefitIncome:
            case GrantIncome:
            case OtherIncome:
            case GiftedIncome:
            case Interest:
            case TaxedInterest:
            case GrossInterest:
            case TaxFreeInterest:
            case Dividend:
            case ShareDividend:
            case UnitTrustDividend:
            case TaxFreeDividend:
            case Inherited:
            case RentalIncome:
            case RoomRentalIncome:
            case OpeningBalance:
            case IncomeTotals:
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
            case ShareDividend:
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
            case TaxedInterest:
            case GrossInterest:
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
            case OptionsGrant:
            case OptionsVest:
            case OptionsExpire:
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
            case OptionsGrant:
            case OptionsVest:
            case OptionsExpire:
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
            case IncomeTotals:
            case ExpenseTotals:
            case Totals:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the EventCategoryType is a subTotal.
     * @return <code>true</code> if the event category type is a subTotal, <code>false</code> otherwise.
     */
    public boolean isSubTotal() {
        switch (this) {
            case IncomeTotals:
            case ExpenseTotals:
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
            case StockAdjust:
            case StockSplit:
            case StockDeMerger:
            case StockTakeOver:
            case StockRightsWaived:
            case StockRightsTaken:
            case OptionsGrant:
            case OptionsVest:
            case OptionsExercise:
            case OptionsExpire:
                return true;
            default:
                return false;
        }
    }
}
