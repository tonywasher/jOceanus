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
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of TransactionCategory Classes.
 */
public enum TransactionCategoryClass implements CategoryInterface {
    /**
     * Taxed Salary Income.
     */
    TAXEDINCOME(1, 4),

    /**
     * Rental Income.
     */
    RENTALINCOME(2, 5),

    /**
     * RoomRental Income.
     */
    ROOMRENTALINCOME(3, 6),

    /**
     * Interest Income.
     */
    INTEREST(4, 7),

    /**
     * Dividend Income.
     */
    DIVIDEND(5, 8),

    /**
     * Grant Income.
     */
    GRANTINCOME(6, 9),

    /**
     * Benefit Income.
     */
    BENEFITINCOME(7, 10),

    /**
     * Gifted Income.
     */
    GIFTEDINCOME(8, 11),

    /**
     * Inheritance.
     */
    INHERITED(9, 12),

    /**
     * Interest earned on Loans.
     */
    LOANINTERESTEARNED(10, 13),

    /**
     * Loyalty Bonus.
     */
    LOYALTYBONUS(11, 14),

    /**
     * CashBack.
     */
    CASHBACK(12, 15),

    /**
     * Other Income.
     */
    OTHERINCOME(13, 16),

    /**
     * Transfer.
     */
    TRANSFER(14, 17),

    /**
     * Stock Adjustment.
     */
    STOCKADJUST(15, 18),

    /**
     * Stock Split.
     */
    STOCKSPLIT(16, 19),

    /**
     * Stock DeMerger.
     */
    STOCKDEMERGER(17, 20),

    /**
     * Stock Takeover.
     */
    STOCKTAKEOVER(18, 21),

    /**
     * Stock Rights Taken.
     */
    STOCKRIGHTSTAKEN(19, 22),

    /**
     * Stock Rights Waived.
     */
    STOCKRIGHTSWAIVED(20, 23),

    /**
     * PortfolioXfer.
     */
    PORTFOLIOXFER(21, 24),

    /**
     * Stock Options Vested.
     */
    OPTIONSVEST(22, 25),

    /**
     * Stock Options Exercised.
     */
    OPTIONSEXERCISE(23, 26),

    /**
     * Expense.
     */
    EXPENSE(24, 27),

    /**
     * LocalTaxes.
     */
    LOCALTAXES(25, 28),

    /**
     * Write Off.
     */
    WRITEOFF(26, 29),

    /**
     * Interest earned on Loans.
     */
    LOANINTERESTCHARGED(27, 30),

    /**
     * Tax Relief.
     */
    TAXRELIEF(28, 31),

    /**
     * Tax Settlement.
     */
    TAXSETTLEMENT(29, 32),

    /**
     * Taxed Interest.
     */
    TAXEDINTEREST(30, 33),

    /**
     * Gross Interest.
     */
    GROSSINTEREST(31, 34),

    /**
     * Tax Free Interest.
     */
    TAXFREEINTEREST(32, 35),

    /**
     * Share Dividend Income.
     */
    SHAREDIVIDEND(33, 36),

    /**
     * Unit Trust Dividend Income.
     */
    UNITTRUSTDIVIDEND(34, 37),

    /**
     * Tax Free Dividend.
     */
    TAXFREEDIVIDEND(35, 38),

    /**
     * Taxable Gain.
     */
    TAXABLEGAIN(36, 39),

    /**
     * Capital Gain.
     */
    CAPITALGAIN(37, 40),

    /**
     * TaxFreeCapital Gain.
     */
    TAXFREEGAIN(38, 41),

    /**
     * Market Growth.
     */
    MARKETGROWTH(39, 42),

    /**
     * CurrencyFluctuation.
     */
    CURRENCYFLUCTUATION(40, 43),

    /**
     * Tax Credit.
     * <p>
     * This is a singular category catching tax credits associated with an event.
     */
    TAXCREDIT(41, 44),

    /**
     * National Insurance.
     * <p>
     * This is a singular category catching national insurance payments associated with an event.
     */
    NATINSURANCE(42, 45),

    /**
     * Deemed Benefit.
     * <p>
     * This is a singular category catching deemed benefit payments associated with an event.
     */
    DEEMEDBENEFIT(43, 46),

    /**
     * CharityDonation.
     * <p>
     * This is a singular category catching charity donations associated with an event.
     */
    CHARITYDONATION(44, 47),

    /**
     * Income Totals.
     * <p>
     * This is used for categories which simply own a set of income sub-categories and is used purely for reporting purposes.
     */
    INCOMETOTALS(45, 1),

    /**
     * Expense Totals.
     * <p>
     * This is used for categories which simply own a set of expense sub-categories and is used purely for reporting purposes.
     */
    EXPENSETOTALS(46, 2),

    /**
     * Stock Parent.
     * <p>
     * This is used for categories which simply own a set of stock transfer sub-categories and is used purely for holding purposes.
     */
    STOCKPARENT(47, 3),

    /**
     * Totals.
     * <p>
     * This is used for the total of all non-transfer categories and is used purely for reporting purposes.
     */
    TOTALS(48, 0);

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
    private TransactionCategoryClass(final int uId,
                                     final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = StaticDataResource.getKeyForTransType(this).getValue();
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
    public static TransactionCategoryClass fromId(final int id) throws JOceanusException {
        for (TransactionCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.TRANSTYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the CategoryType is hidden type.
     * @return <code>true</code> if the category is hidden, <code>false</code> otherwise.
     */
    public boolean isHiddenType() {
        switch (this) {
            case SHAREDIVIDEND:
            case UNITTRUSTDIVIDEND:
            case TAXFREEDIVIDEND:
            case TAXEDINTEREST:
            case GROSSINTEREST:
            case TAXFREEINTEREST:
            case MARKETGROWTH:
            case CURRENCYFLUCTUATION:
            case TAXCREDIT:
            case NATINSURANCE:
            case DEEMEDBENEFIT:
            case TAXRELIEF:
            case TAXABLEGAIN:
            case CAPITALGAIN:
            case TAXFREEGAIN:
            case TOTALS:
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
            case INTEREST:
            case DIVIDEND:
            case CASHBACK:
            case LOYALTYBONUS:
            case LOANINTERESTEARNED:
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
            case RENTALINCOME:
            case ROOMRENTALINCOME:
            case WRITEOFF:
            case LOANINTERESTCHARGED:
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
            case SHAREDIVIDEND:
            case UNITTRUSTDIVIDEND:
            case TAXFREEDIVIDEND:
            case TAXEDINTEREST:
            case GROSSINTEREST:
            case TAXFREEINTEREST:
            case MARKETGROWTH:
            case CURRENCYFLUCTUATION:
            case TAXCREDIT:
            case NATINSURANCE:
            case DEEMEDBENEFIT:
            case CHARITYDONATION:
            case CAPITALGAIN:
            case TAXFREEGAIN:
            case TAXRELIEF:
            case TAXSETTLEMENT:
            case TAXABLEGAIN:
            case TOTALS:
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
            case TAXEDINCOME:
            case BENEFITINCOME:
            case INTEREST:
            case TAXEDINTEREST:
            case DIVIDEND:
            case SHAREDIVIDEND:
            case UNITTRUSTDIVIDEND:
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
            case TAXEDINCOME:
            case BENEFITINCOME:
            case GRANTINCOME:
            case LOYALTYBONUS:
            case CASHBACK:
            case OTHERINCOME:
            case GIFTEDINCOME:
            case INTEREST:
            case TAXEDINTEREST:
            case GROSSINTEREST:
            case TAXFREEINTEREST:
            case DIVIDEND:
            case SHAREDIVIDEND:
            case UNITTRUSTDIVIDEND:
            case TAXFREEDIVIDEND:
            case INHERITED:
            case RENTALINCOME:
            case ROOMRENTALINCOME:
            case INCOMETOTALS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the CategoryType is an expense.
     * @return <code>true</code> if the category is expense, <code>false</code> otherwise.
     */
    public boolean isExpense() {
        return !isIncome() && !isTransfer();
    }

    /**
     * Determine whether the CategoryType is dilutable.
     * @return <code>true</code> if the category is dilutable, <code>false</code> otherwise.
     */
    public boolean isDilutable() {
        switch (this) {
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKRIGHTSWAIVED:
            case STOCKRIGHTSTAKEN:
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
            case DIVIDEND:
            case SHAREDIVIDEND:
            case UNITTRUSTDIVIDEND:
            case TAXFREEDIVIDEND:
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
            case INTEREST:
            case TAXEDINTEREST:
            case GROSSINTEREST:
            case TAXFREEINTEREST:
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
            case STOCKSPLIT:
            case STOCKADJUST:
            case STOCKDEMERGER:
            case STOCKTAKEOVER:
            case PORTFOLIOXFER:
            case OPTIONSVEST:
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
            case STOCKSPLIT:
            case STOCKADJUST:
            case STOCKDEMERGER:
            case PORTFOLIOXFER:
            case OPTIONSVEST:
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
            case INCOMETOTALS:
            case EXPENSETOTALS:
            case STOCKPARENT:
            case TOTALS:
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
            case INCOMETOTALS:
            case EXPENSETOTALS:
            case STOCKPARENT:
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
            case TRANSFER:
                return true;
            default:
                return isStockTransfer();
        }
    }

    /**
     * Is this event category a stock transfer?
     * @return true/false
     */
    public boolean isStockTransfer() {
        switch (this) {
            case STOCKADJUST:
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKTAKEOVER:
            case STOCKRIGHTSWAIVED:
            case STOCKRIGHTSTAKEN:
            case PORTFOLIOXFER:
            case OPTIONSVEST:
            case OPTIONSEXERCISE:
            case STOCKPARENT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isTotals() {
        return this == TransactionCategoryClass.TOTALS;
    }
}
