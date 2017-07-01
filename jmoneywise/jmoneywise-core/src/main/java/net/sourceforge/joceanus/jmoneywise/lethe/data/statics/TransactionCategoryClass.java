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
     * BShare Dividend Income.
     */
    BSHAREDIVIDEND(6, 9),

    /**
     * Virtual Income.
     */
    VIRTUALINCOME(7, 10),

    /**
     * PensionPayment.
     */
    PENSIONPAYMENT(8, 11),

    /**
     * Gifted Income.
     */
    GIFTEDINCOME(9, 12),

    /**
     * Inheritance.
     */
    INHERITED(10, 13),

    /**
     * Interest earned on Loans.
     */
    LOANINTERESTEARNED(11, 14),

    /**
     * Loyalty Bonus.
     */
    LOYALTYBONUS(12, 15),

    /**
     * CashBack.
     */
    CASHBACK(13, 16),

    /**
     * Recovered Expenses.
     */
    RECOVEREDEXPENSES(14, 17),

    /**
     * Other Income.
     */
    OTHERINCOME(15, 18),

    /**
     * Stock Options Exercised.
     */
    OPTIONSEXERCISE(16, 19),

    /**
     * Stock Units Adjustment.
     */
    UNITSADJUST(17, 20),

    /**
     * Stock Split.
     */
    STOCKSPLIT(18, 21),

    /**
     * Stock DeMerger.
     */
    STOCKDEMERGER(19, 22),

    /**
     * Security Replacement.
     */
    SECURITYREPLACE(20, 23),

    /**
     * Stock TakeOver.
     */
    STOCKTAKEOVER(21, 24),

    /**
     * Stock Rights Issue.
     */
    STOCKRIGHTSISSUE(22, 25),

    /**
     * PortfolioXfer.
     */
    PORTFOLIOXFER(23, 26),

    /**
     * Stock Options Granted.
     */
    OPTIONSGRANT(24, 27),

    /**
     * Stock Options Vested.
     */
    OPTIONSVEST(25, 28),

    /**
     * Stock Options Expired.
     */
    OPTIONSEXPIRE(26, 29),

    /**
     * Transfer.
     */
    TRANSFER(27, 30),

    /**
     * Expense.
     */
    EXPENSE(28, 31),

    /**
     * BadDebtCapital.
     */
    BADDEBTCAPITAL(29, 32),

    /**
     * BadDebtInterest.
     */
    BADDEBTINTEREST(30, 33),

    /**
     * LocalTaxes.
     */
    LOCALTAXES(31, 34),

    /**
     * Write Off.
     */
    WRITEOFF(32, 35),

    /**
     * Interest charged on Loans.
     */
    LOANINTERESTCHARGED(33, 36),

    /**
     * Rental Expense.
     */
    RENTALEXPENSE(34, 37),

    /**
     * AnnuityPurchase.
     */
    ANNUITYPURCHASE(35, 38),

    /**
     * Tax Relief.
     */
    TAXRELIEF(36, 39),

    /**
     * IncomeTax.
     */
    INCOMETAX(37, 40),

    /**
     * Taxed Interest.
     */
    TAXEDINTEREST(38, 41),

    /**
     * Gross Interest.
     */
    GROSSINTEREST(39, 42),

    /**
     * Tax Free Interest.
     */
    TAXFREEINTEREST(40, 43),

    /**
     * Share Dividend Income.
     */
    SHAREDIVIDEND(41, 44),

    /**
     * Unit Trust Dividend Income.
     */
    UNITTRUSTDIVIDEND(42, 45),

    /**
     * Foreign Dividend.
     */
    FOREIGNDIVIDEND(43, 46),

    /**
     * Tax Free Dividend.
     */
    TAXFREEDIVIDEND(44, 47),

    /**
     * Taxed Loyalty Bonus.
     */
    TAXEDLOYALTYBONUS(45, 48),

    /**
     * Gross LoyaltyBonus.
     */
    GROSSLOYALTYBONUS(46, 49),

    /**
     * Tax Free LoyaltyBonus.
     */
    TAXFREELOYALTYBONUS(47, 50),

    /**
     * Chargeable Gain.
     */
    CHARGEABLEGAIN(48, 51),

    /**
     * Residential Gain.
     */
    RESIDENTIALGAIN(49, 52),

    /**
     * Capital Gain.
     */
    CAPITALGAIN(50, 53),

    /**
     * TaxFreeCapital Gain.
     */
    TAXFREEGAIN(51, 54),

    /**
     * Market Growth.
     */
    MARKETGROWTH(52, 55),

    /**
     * CurrencyFluctuation.
     */
    CURRENCYFLUCTUATION(53, 56),

    /**
     * National Insurance.
     * <p>
     * This is a singular category catching national insurance payments associated with an event.
     */
    NATINSURANCE(54, 57),

    /**
     * Withheld.
     * <p>
     * This is a singular category catching withheld items such as charity donations associated with
     * interest.
     */
    WITHHELD(55, 58),

    /**
     * OpeningBalance.
     * <p>
     * This is a singular category catching opening balances.
     */
    OPENINGBALANCE(56, 59),

    /**
     * Income Totals.
     * <p>
     * This is used for categories which simply own a set of income sub-categories and is used
     * purely for reporting purposes.
     */
    INCOMETOTALS(57, 1),

    /**
     * Expense Totals.
     * <p>
     * This is used for categories which simply own a set of expense sub-categories and is used
     * purely for reporting purposes.
     */
    EXPENSETOTALS(58, 2),

    /**
     * Security Parent.
     * <p>
     * This is used for categories which simply own a set of security transfer sub-categories and is
     * used purely for holding purposes.
     */
    SECURITYPARENT(59, 3),

    /**
     * Totals.
     * <p>
     * This is used for the total of all non-transfer categories and is used purely for reporting
     * purposes.
     */
    TOTALS(60, 0);

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
            case BSHAREDIVIDEND:
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
            case OPTIONSGRANT:
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
            case OPTIONSGRANT:
            case OPTIONSVEST:
            case OPTIONSEXPIRE:
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
            case OPTIONSGRANT:
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
