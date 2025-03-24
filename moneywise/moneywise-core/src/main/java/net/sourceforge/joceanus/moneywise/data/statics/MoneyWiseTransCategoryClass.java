/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.data.statics;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Enumeration of TransactionCategory Classes.
 */
public enum MoneyWiseTransCategoryClass
        implements MoneyWiseCategoryInterface {
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
     * GrossIncome.
     */
    GROSSINCOME(7, 10),

    /**
     * PensionContribution.
     */
    PENSIONCONTRIB(8, 11),

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
     * Security Closure.
     */
    SECURITYCLOSURE(21, 24),

    /**
     * Stock TakeOver.
     */
    STOCKTAKEOVER(22, 25),

    /**
     * Stock Rights Issue.
     */
    STOCKRIGHTSISSUE(23, 26),

    /**
     * PortfolioXfer.
     */
    PORTFOLIOXFER(24, 27),

    /**
     * Stock Options Granted.
     */
    OPTIONSGRANT(25, 28),

    /**
     * Stock Options Vested.
     */
    OPTIONSVEST(26, 29),

    /**
     * Stock Options Expired.
     */
    OPTIONSEXPIRE(27, 30),

    /**
     * Pension Drawdown.
     */
    PENSIONDRAWDOWN(28, 31),

    /**
     * Pension TaxFree.
     */
    PENSIONTAXFREE(29, 32),

    /**
     * Transfer.
     */
    TRANSFER(30, 33),

    /**
     * Expense.
     */
    EXPENSE(31, 34),

    /**
     * BadDebtCapital.
     */
    BADDEBTCAPITAL(32, 35),

    /**
     * BadDebtInterest.
     */
    BADDEBTINTEREST(33, 36),

    /**
     * LocalTaxes.
     */
    LOCALTAXES(34, 37),

    /**
     * Write Off.
     */
    WRITEOFF(35, 38),

    /**
     * Interest charged on Loans.
     */
    LOANINTERESTCHARGED(36, 39),

    /**
     * Rental Expense.
     */
    RENTALEXPENSE(37, 40),

    /**
     * AnnuityPurchase.
     */
    ANNUITYPURCHASE(38, 41),

    /**
     * Tax Relief.
     */
    TAXRELIEF(39, 42),

    /**
     * IncomeTax.
     */
    INCOMETAX(40, 43),

    /**
     * Taxed Interest.
     */
    TAXEDINTEREST(41, 44),

    /**
     * Gross Interest.
     */
    GROSSINTEREST(42, 45),

    /**
     * Tax Free Interest.
     */
    TAXFREEINTEREST(43, 46),

    /**
     * Peer2Peer Interest.
     */
    PEER2PEERINTEREST(44, 47),

    /**
     * Share Dividend Income.
     */
    SHAREDIVIDEND(45, 48),

    /**
     * Unit Trust Dividend Income.
     */
    UNITTRUSTDIVIDEND(46, 49),

    /**
     * Foreign Dividend.
     */
    FOREIGNDIVIDEND(47, 50),

    /**
     * Tax Free Dividend.
     */
    TAXFREEDIVIDEND(48, 51),

    /**
     * Taxed Loyalty Bonus.
     */
    TAXEDLOYALTYBONUS(49, 52),

    /**
     * Gross LoyaltyBonus.
     */
    GROSSLOYALTYBONUS(50, 53),

    /**
     * Tax Free LoyaltyBonus.
     */
    TAXFREELOYALTYBONUS(51, 54),

    /**
     * Chargeable Gain.
     */
    CHARGEABLEGAIN(52, 55),

    /**
     * Residential Gain.
     */
    RESIDENTIALGAIN(53, 56),

    /**
     * Capital Gain.
     */
    CAPITALGAIN(54, 57),

    /**
     * TaxFreeCapital Gain.
     */
    TAXFREEGAIN(55, 58),

    /**
     * Market Growth.
     */
    MARKETGROWTH(56, 59),

    /**
     * CurrencyFluctuation.
     */
    CURRENCYFLUCTUATION(57, 60),

    /**
     * Withheld.
     * <p>
     * This is a singular category catching withheld items such as charity donations associated with
     * interest.
     */
    WITHHELD(58, 61),

    /**
     * OpeningBalance.
     * <p>
     * This is a singular category catching opening balances.
     */
    OPENINGBALANCE(59, 62),

    /**
     * EmployeeNatInsurance.
     * <p>
     * This is a singular category catching opening balances.
     */
    EMPLOYEENATINS(60, 63),

    /**
     * EmployeeNatInsurance.
     * <p>
     * This is a singular category catching opening balances.
     */
    EMPLOYERNATINS(61, 64),

    /**
     * Income Totals.
     * <p>
     * This is used for categories which simply own a set of income sub-categories and is used
     * purely for reporting purposes.
     */
    INCOMETOTALS(62, 1),

    /**
     * Expense Totals.
     * <p>
     * This is used for categories which simply own a set of expense sub-categories and is used
     * purely for reporting purposes.
     */
    EXPENSETOTALS(63, 2),

    /**
     * Security Parent.
     * <p>
     * This is used for categories which simply own a set of security transfer sub-categories and is
     * used purely for holding purposes.
     */
    SECURITYPARENT(64, 3),

    /**
     * Totals.
     * <p>
     * This is used for the total of all non-transfer categories and is used purely for reporting
     * purposes.
     */
    TOTALS(65, 0);

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
    MoneyWiseTransCategoryClass(final int uId,
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
            theName = MoneyWiseStaticResource.getKeyForTransType(this).getValue();
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
    public static MoneyWiseTransCategoryClass fromId(final int id) throws OceanusException {
        for (MoneyWiseTransCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseStaticDataType.TRANSTYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the CategoryType is hidden type.
     * @return <code>true</code> if the category is hidden, <code>false</code> otherwise.
     */
    public boolean isHiddenType() {
        switch (this) {
            case SHAREDIVIDEND:
            case UNITTRUSTDIVIDEND:
            case FOREIGNDIVIDEND:
            case TAXFREEDIVIDEND:
            case TAXEDINTEREST:
            case GROSSINTEREST:
            case TAXFREEINTEREST:
            case PEER2PEERINTEREST:
            case TAXEDLOYALTYBONUS:
            case GROSSLOYALTYBONUS:
            case TAXFREELOYALTYBONUS:
            case MARKETGROWTH:
            case CURRENCYFLUCTUATION:
            case WITHHELD:
            case TAXRELIEF:
            case CHARGEABLEGAIN:
            case RESIDENTIALGAIN:
            case CAPITALGAIN:
            case TAXFREEGAIN:
            case VIRTUALINCOME:
            case EMPLOYEENATINS:
            case EMPLOYERNATINS:
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
        return INCOMETAX.equals(this) || isHiddenType();
    }

    /**
     * Determine whether the CategoryType is an income.
     * @return <code>true</code> if the category is income, <code>false</code> otherwise.
     */
    public boolean isIncome() {
        switch (this) {
            case TAXEDINCOME:
            case VIRTUALINCOME:
            case GROSSINCOME:
            case PENSIONCONTRIB:
            case LOYALTYBONUS:
            case CASHBACK:
            case OTHERINCOME:
            case RECOVEREDEXPENSES:
            case GIFTEDINCOME:
            case INTEREST:
            case TAXEDINTEREST:
            case GROSSINTEREST:
            case TAXFREEINTEREST:
            case PEER2PEERINTEREST:
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
            case EMPLOYEENATINS:
            case EMPLOYERNATINS:
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
            case PEER2PEERINTEREST:
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
        return this == TRANSFER || isSecurityTransfer();
    }

    /**
     * Is this event category a NatInsurance event?
     * @return true/false
     */
    public boolean isNatInsurance() {
        switch (this) {
            case TAXEDINCOME:
            case PENSIONCONTRIB:
                return true;
            default:
                return false;
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
            case SECURITYCLOSURE:
            case SECURITYPARENT:
            case PENSIONDRAWDOWN:
            case PENSIONTAXFREE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this event category a security closure?
     * @return true/false
     */
    public boolean isSecurityClosure() {
        return this == SECURITYCLOSURE;
    }

    @Override
    public boolean isTotals() {
        return this == TOTALS;
    }
}
