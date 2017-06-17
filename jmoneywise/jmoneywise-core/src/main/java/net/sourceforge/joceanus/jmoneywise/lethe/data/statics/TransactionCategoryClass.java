/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * Virtual Income.
     */
    VIRTUALINCOME(6, 9),

    /**
     * PensionPayment.
     */
    PENSIONPAYMENT(7, 10),

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
     * Recovered Expenses.
     */
    RECOVEREDEXPENSES(13, 16),

    /**
     * Other Income.
     */
    OTHERINCOME(14, 17),

    /**
     * Stock Options Exercised.
     */
    OPTIONSEXERCISE(15, 18),

    /**
     * Stock Units Adjustment.
     */
    UNITSADJUST(16, 19),

    /**
     * Stock Split.
     */
    STOCKSPLIT(17, 20),

    /**
     * Stock DeMerger.
     */
    STOCKDEMERGER(18, 21),

    /**
     * Security Replacement.
     */
    SECURITYREPLACE(19, 22),

    /**
     * Stock TakeOver.
     */
    STOCKTAKEOVER(20, 23),

    /**
     * Stock Rights Issue.
     */
    STOCKRIGHTSISSUE(21, 24),

    /**
     * PortfolioXfer.
     */
    PORTFOLIOXFER(22, 25),

    /**
     * Stock Options Vested.
     */
    OPTIONSVEST(23, 26),

    /**
     * Stock Options Expired.
     */
    OPTIONSEXPIRE(24, 27),

    /**
     * Transfer.
     */
    TRANSFER(25, 28),

    /**
     * Expense.
     */
    EXPENSE(26, 29),

    /**
     * BadDebtCapital.
     */
    BADDEBTCAPITAL(27, 30),

    /**
     * BadDebtInterest.
     */
    BADDEBTINTEREST(28, 31),

    /**
     * LocalTaxes.
     */
    LOCALTAXES(29, 32),

    /**
     * Write Off.
     */
    WRITEOFF(30, 33),

    /**
     * Interest charged on Loans.
     */
    LOANINTERESTCHARGED(31, 34),

    /**
     * Rental Expense.
     */
    RENTALEXPENSE(32, 35),

    /**
     * Tax Relief.
     */
    TAXRELIEF(33, 36),

    /**
     * IncomeTax.
     */
    INCOMETAX(34, 37),

    /**
     * Taxed Interest.
     */
    TAXEDINTEREST(35, 38),

    /**
     * Gross Interest.
     */
    GROSSINTEREST(36, 39),

    /**
     * Tax Free Interest.
     */
    TAXFREEINTEREST(37, 40),

    /**
     * Share Dividend Income.
     */
    SHAREDIVIDEND(38, 41),

    /**
     * Unit Trust Dividend Income.
     */
    UNITTRUSTDIVIDEND(39, 42),

    /**
     * Foreign Dividend.
     */
    FOREIGNDIVIDEND(40, 43),

    /**
     * Tax Free Dividend.
     */
    TAXFREEDIVIDEND(41, 44),

    /**
     * Taxed Loyalty Bonus.
     */
    TAXEDLOYALTYBONUS(42, 45),

    /**
     * Gross LoyaltyBonus.
     */
    GROSSLOYALTYBONUS(43, 46),

    /**
     * Tax Free LoyaltyBonus.
     */
    TAXFREELOYALTYBONUS(44, 47),

    /**
     * Chargeable Gain.
     */
    CHARGEABLEGAIN(45, 48),

    /**
     * Residential Gain.
     */
    RESIDENTIALGAIN(46, 49),

    /**
     * Capital Gain.
     */
    CAPITALGAIN(47, 50),

    /**
     * TaxFreeCapital Gain.
     */
    TAXFREEGAIN(48, 51),

    /**
     * Market Growth.
     */
    MARKETGROWTH(49, 52),

    /**
     * CurrencyFluctuation.
     */
    CURRENCYFLUCTUATION(50, 53),

    /**
     * National Insurance.
     * <p>
     * This is a singular category catching national insurance payments associated with an event.
     */
    NATINSURANCE(51, 54),

    /**
     * Withheld.
     * <p>
     * This is a singular category catching withheld items such as charity donations associated with
     * interest.
     */
    WITHHELD(52, 55),

    /**
     * OpeningBalance.
     * <p>
     * This is a singular category catching opening balances.
     */
    OPENINGBALANCE(53, 56),

    /**
     * Income Totals.
     * <p>
     * This is used for categories which simply own a set of income sub-categories and is used
     * purely for reporting purposes.
     */
    INCOMETOTALS(54, 1),

    /**
     * Expense Totals.
     * <p>
     * This is used for categories which simply own a set of expense sub-categories and is used
     * purely for reporting purposes.
     */
    EXPENSETOTALS(55, 2),

    /**
     * Security Parent.
     * <p>
     * This is used for categories which simply own a set of security transfer sub-categories and is
     * used purely for holding purposes.
     */
    SECURITYPARENT(56, 3),

    /**
     * Totals.
     * <p>
     * This is used for the total of all non-transfer categories and is used purely for reporting
     * purposes.
     */
    TOTALS(57, 0);

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
     * Constructor.
     * @param uId the id
     * @param uOrder the default order.
     */
    TransactionCategoryClass(final int uId,
                             final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    @Override
    public int getClassId() {
        return theId;
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
            theName = StaticDataResource.getKeyForTransType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws OceanusException on error
     */
    public static TransactionCategoryClass fromId(final int id) throws OceanusException {
        for (TransactionCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.TRANSTYPE.toString() + ":" + id);
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
            case FOREIGNDIVIDEND:
            case TAXEDINTEREST:
            case GROSSINTEREST:
            case TAXFREEINTEREST:
            case TAXEDLOYALTYBONUS:
            case GROSSLOYALTYBONUS:
            case TAXFREELOYALTYBONUS:
            case MARKETGROWTH:
            case CURRENCYFLUCTUATION:
            case NATINSURANCE:
            case WITHHELD:
            case TAXRELIEF:
            case CHARGEABLEGAIN:
            case RESIDENTIALGAIN:
            case CAPITALGAIN:
            case TAXFREEGAIN:
            case OPENINGBALANCE:
            case TOTALS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the Account is a secret payee.
     * @return <code>true</code> if the category uses parent payee, <code>false</code> otherwise.
     */
    public boolean isSecretPayee() {
        switch (this) {
            case INTEREST:
            case DIVIDEND:
            case CASHBACK:
            case LOYALTYBONUS:
            case LOANINTERESTEARNED:
            case RENTALINCOME:
            case ROOMRENTALINCOME:
            case WRITEOFF:
            case LOANINTERESTCHARGED:
            case OPTIONSEXERCISE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the Account is a secret payee.
     * @return <code>true</code> if the category uses parent payee, <code>false</code> otherwise.
     */
    public boolean isSwitchDirection() {
        switch (this) {
            case INTEREST:
            case DIVIDEND:
            case CASHBACK:
            case LOYALTYBONUS:
            case RENTALINCOME:
            case ROOMRENTALINCOME:
            case WRITEOFF:
            case LOANINTERESTCHARGED:
            case TRANSFER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the category type is singular.
     * @return <code>true</code> if the event category type is singular, <code>false</code>
     * otherwise.
     */
    public boolean isSingular() {
        switch (this) {
            case SHAREDIVIDEND:
            case UNITTRUSTDIVIDEND:
            case FOREIGNDIVIDEND:
            case TAXFREEDIVIDEND:
            case TAXEDINTEREST:
            case GROSSINTEREST:
            case TAXFREEINTEREST:
            case TAXEDLOYALTYBONUS:
            case GROSSLOYALTYBONUS:
            case TAXFREELOYALTYBONUS:
            case MARKETGROWTH:
            case CURRENCYFLUCTUATION:
            case INCOMETAX:
            case NATINSURANCE:
            case VIRTUALINCOME:
            case WITHHELD:
            case RESIDENTIALGAIN:
            case CAPITALGAIN:
            case TAXFREEGAIN:
            case TAXRELIEF:
            case CHARGEABLEGAIN:
            case OPENINGBALANCE:
            case TOTALS:
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
            case VIRTUALINCOME:
            case PENSIONPAYMENT:
            case LOYALTYBONUS:
            case CASHBACK:
            case OTHERINCOME:
            case RECOVEREDEXPENSES:
            case GIFTEDINCOME:
            case INTEREST:
            case TAXEDINTEREST:
            case GROSSINTEREST:
            case TAXFREEINTEREST:
            case DIVIDEND:
            case SHAREDIVIDEND:
            case UNITTRUSTDIVIDEND:
            case FOREIGNDIVIDEND:
            case TAXFREEDIVIDEND:
            case TAXEDLOYALTYBONUS:
            case GROSSLOYALTYBONUS:
            case TAXFREELOYALTYBONUS:
            case INHERITED:
            case LOANINTERESTEARNED:
            case RENTALINCOME:
            case ROOMRENTALINCOME:
            case OPTIONSEXERCISE:
            case OPENINGBALANCE:
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
            case FOREIGNDIVIDEND:
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
            case UNITSADJUST:
            case STOCKDEMERGER:
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
            case PORTFOLIOXFER:
            case OPTIONSVEST:
            case OPTIONSEXPIRE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the CategoryType has null Amount.
     * @return <code>true</code> if the category needs null amount, <code>false</code> otherwise.
     */
    public boolean needsNullAmount() {
        switch (this) {
            case STOCKSPLIT:
            case UNITSADJUST:
            case STOCKDEMERGER:
            case STOCKTAKEOVER:
            case PORTFOLIOXFER:
            case OPTIONSVEST:
            case SECURITYREPLACE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the EventCategoryType can be parent categories.
     * @return <code>true</code> if the event category type can parent categories,
     * <code>false</code> otherwise.
     */
    public boolean canParentCategory() {
        switch (this) {
            case INCOMETOTALS:
            case EXPENSETOTALS:
            case SECURITYPARENT:
            case TOTALS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the EventCategoryType is a subTotal.
     * @return <code>true</code> if the event category type is a subTotal, <code>false</code>
     * otherwise.
     */
    public boolean isSubTotal() {
        switch (this) {
            case INCOMETOTALS:
            case EXPENSETOTALS:
            case SECURITYPARENT:
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
                return isSecurityTransfer();
        }
    }

    /**
     * Is this event category a security transfer?
     * @return true/false
     */
    public boolean isSecurityTransfer() {
        switch (this) {
            case UNITSADJUST:
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
            case STOCKRIGHTSISSUE:
            case PORTFOLIOXFER:
            case OPTIONSVEST:
            case OPTIONSEXPIRE:
            case SECURITYPARENT:
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
