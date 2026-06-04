/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.data.statics;

import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

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
     *
     * @param uId    the id
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
            theName = bundleIdForCategoryClass(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     *
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
     *
     * @return <code>true</code> if the category is hidden, <code>false</code> otherwise.
     */
    public boolean isHiddenType() {
        return switch (this) {
            case SHAREDIVIDEND, UNITTRUSTDIVIDEND, FOREIGNDIVIDEND, TAXFREEDIVIDEND, TAXEDINTEREST, GROSSINTEREST,
                 TAXFREEINTEREST, PEER2PEERINTEREST, TAXEDLOYALTYBONUS, GROSSLOYALTYBONUS, TAXFREELOYALTYBONUS,
                 MARKETGROWTH, CURRENCYFLUCTUATION, WITHHELD, TAXRELIEF, CHARGEABLEGAIN, RESIDENTIALGAIN, CAPITALGAIN,
                 TAXFREEGAIN, VIRTUALINCOME, EMPLOYEENATINS, EMPLOYERNATINS, OPENINGBALANCE, TOTALS -> true;
            default -> false;
        };
    }

    /**
     * Determine whether the Account is a secret payee.
     *
     * @return <code>true</code> if the category uses parent payee, <code>false</code> otherwise.
     */
    public boolean isSecretPayee() {
        return switch (this) {
            case INTEREST, DIVIDEND, CASHBACK, LOYALTYBONUS, LOANINTERESTEARNED, RENTALINCOME, ROOMRENTALINCOME,
                 WRITEOFF, LOANINTERESTCHARGED, OPTIONSEXERCISE -> true;
            default -> false;
        };
    }

    /**
     * Determine whether the Account is a secret payee.
     *
     * @return <code>true</code> if the category uses parent payee, <code>false</code> otherwise.
     */
    public boolean isSwitchDirection() {
        return switch (this) {
            case INTEREST, DIVIDEND, CASHBACK, LOYALTYBONUS, RENTALINCOME, ROOMRENTALINCOME, WRITEOFF,
                 LOANINTERESTCHARGED, TRANSFER -> true;
            default -> false;
        };
    }

    /**
     * Determine whether the category type is singular.
     *
     * @return <code>true</code> if the event category type is singular, <code>false</code>
     * otherwise.
     */
    public boolean isSingular() {
        return INCOMETAX.equals(this) || isHiddenType();
    }

    /**
     * Determine whether the CategoryType is an income.
     *
     * @return <code>true</code> if the category is income, <code>false</code> otherwise.
     */
    public boolean isIncome() {
        return switch (this) {
            case TAXEDINCOME, VIRTUALINCOME, GROSSINCOME, PENSIONCONTRIB, LOYALTYBONUS, CASHBACK, OTHERINCOME,
                 RECOVEREDEXPENSES, GIFTEDINCOME, INTEREST, TAXEDINTEREST, GROSSINTEREST, TAXFREEINTEREST,
                 PEER2PEERINTEREST, DIVIDEND, SHAREDIVIDEND, UNITTRUSTDIVIDEND, FOREIGNDIVIDEND, TAXFREEDIVIDEND,
                 TAXEDLOYALTYBONUS, GROSSLOYALTYBONUS, TAXFREELOYALTYBONUS, INHERITED, LOANINTERESTEARNED, RENTALINCOME,
                 ROOMRENTALINCOME, OPTIONSEXERCISE, EMPLOYEENATINS, EMPLOYERNATINS, OPENINGBALANCE, INCOMETOTALS ->
                    true;
            default -> false;
        };
    }

    /**
     * Determine whether the CategoryType is an expense.
     *
     * @return <code>true</code> if the category is expense, <code>false</code> otherwise.
     */
    public boolean isExpense() {
        return !isIncome() && !isTransfer();
    }

    /**
     * Determine whether the CategoryType is dilutable.
     *
     * @return <code>true</code> if the category is dilutable, <code>false</code> otherwise.
     */
    public boolean isDilutable() {
        return switch (this) {
            case STOCKSPLIT, STOCKDEMERGER -> true;
            default -> false;
        };
    }

    /**
     * Determine whether the CategoryType is dividend.
     *
     * @return <code>true</code> if the category is dividend, <code>false</code> otherwise.
     */
    public boolean isDividend() {
        return switch (this) {
            case DIVIDEND, SHAREDIVIDEND, UNITTRUSTDIVIDEND, FOREIGNDIVIDEND, TAXFREEDIVIDEND -> true;
            default -> false;
        };
    }

    /**
     * Determine whether the CategoryType is interest.
     *
     * @return <code>true</code> if the category is interest, <code>false</code> otherwise.
     */
    public boolean isInterest() {
        return switch (this) {
            case INTEREST, TAXEDINTEREST, GROSSINTEREST, TAXFREEINTEREST, PEER2PEERINTEREST -> true;
            default -> false;
        };
    }

    /**
     * Determine whether the CategoryType needs debit and credit both to have units.
     *
     * @return <code>true</code> if the category needs dual units, <code>false</code> otherwise.
     */
    public boolean isStockAdjustment() {
        return switch (this) {
            case STOCKSPLIT, UNITSADJUST, STOCKDEMERGER, SECURITYREPLACE, STOCKTAKEOVER, PORTFOLIOXFER, OPTIONSGRANT,
                 OPTIONSVEST, OPTIONSEXPIRE -> true;
            default -> false;
        };
    }

    /**
     * Determine whether the CategoryType has null Amount.
     *
     * @return <code>true</code> if the category needs null amount, <code>false</code> otherwise.
     */
    public boolean needsNullAmount() {
        return switch (this) {
            case STOCKSPLIT, UNITSADJUST, STOCKDEMERGER, STOCKTAKEOVER, PORTFOLIOXFER, OPTIONSGRANT, OPTIONSVEST,
                 OPTIONSEXPIRE, SECURITYREPLACE -> true;
            default -> false;
        };
    }

    /**
     * Determine whether the EventCategoryType can be parent categories.
     *
     * @return <code>true</code> if the event category type can parent categories,
     * <code>false</code> otherwise.
     */
    public boolean canParentCategory() {
        return switch (this) {
            case INCOMETOTALS, EXPENSETOTALS, SECURITYPARENT, TOTALS -> true;
            default -> false;
        };
    }

    /**
     * Determine whether the EventCategoryType is a subTotal.
     *
     * @return <code>true</code> if the event category type is a subTotal, <code>false</code>
     * otherwise.
     */
    public boolean isSubTotal() {
        return switch (this) {
            case INCOMETOTALS, EXPENSETOTALS, SECURITYPARENT -> true;
            default -> false;
        };
    }

    /**
     * Is this event category a transfer?
     *
     * @return true/false
     */
    public boolean isTransfer() {
        return this == TRANSFER || isSecurityTransfer();
    }

    /**
     * Is this event category a NatInsurance event?
     *
     * @return true/false
     */
    public boolean isNatInsurance() {
        return switch (this) {
            case TAXEDINCOME, PENSIONCONTRIB -> true;
            default -> false;
        };
    }

    /**
     * Is this event category a security transfer?
     *
     * @return true/false
     */
    public boolean isSecurityTransfer() {
        return switch (this) {
            case UNITSADJUST, STOCKSPLIT, STOCKDEMERGER, SECURITYREPLACE, STOCKTAKEOVER, STOCKRIGHTSISSUE,
                 PORTFOLIOXFER, OPTIONSGRANT, OPTIONSVEST, OPTIONSEXPIRE, SECURITYCLOSURE, SECURITYPARENT,
                 PENSIONDRAWDOWN, PENSIONTAXFREE -> true;
            default -> false;
        };
    }

    /**
     * Is this event category a security closure?
     *
     * @return true/false
     */
    public boolean isSecurityClosure() {
        return this == SECURITYCLOSURE;
    }

    @Override
    public boolean isTotals() {
        return this == TOTALS;
    }

    /**
     * Obtain the resource bundleId for the category class.
     *
     * @param pClass the category class
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForCategoryClass(final MoneyWiseTransCategoryClass pClass) {
        /* Create the map and return it */
        return switch (pClass) {
            case TAXEDINCOME -> MoneyWiseStaticResource.TRANSTYPE_TAXEDINCOME;
            case RENTALINCOME -> MoneyWiseStaticResource.TRANSTYPE_RENTALINCOME;
            case ROOMRENTALINCOME -> MoneyWiseStaticResource.TRANSTYPE_ROOMRENTINCOME;
            case INTEREST -> MoneyWiseStaticResource.TRANSTYPE_INTEREST;
            case DIVIDEND -> MoneyWiseStaticResource.TRANSTYPE_DIVIDEND;
            case VIRTUALINCOME -> MoneyWiseStaticResource.TRANSTYPE_VIRTUALINCOME;
            case GROSSINCOME -> MoneyWiseStaticResource.TRANSTYPE_GROSSINCOME;
            case PENSIONCONTRIB -> MoneyWiseStaticResource.TRANSTYPE_PENSIONCONTRIB;
            case GIFTEDINCOME -> MoneyWiseStaticResource.TRANSTYPE_GIFTEDINCOME;
            case INHERITED -> MoneyWiseStaticResource.TRANSTYPE_INHERITED;
            case LOANINTERESTEARNED -> MoneyWiseStaticResource.TRANSTYPE_LOANINTEARNED;
            case CASHBACK -> MoneyWiseStaticResource.TRANSTYPE_CASHBACK;
            case LOYALTYBONUS -> MoneyWiseStaticResource.TRANSTYPE_LOYALTYBONUS;
            case RECOVEREDEXPENSES -> MoneyWiseStaticResource.TRANSTYPE_RECOVEREDEXPENSES;
            case OTHERINCOME -> MoneyWiseStaticResource.TRANSTYPE_OTHERINCOME;
            case TRANSFER -> MoneyWiseStaticResource.TRANSTYPE_TRANSFER;
            case UNITSADJUST -> MoneyWiseStaticResource.TRANSTYPE_UNITSADJUST;
            case STOCKSPLIT -> MoneyWiseStaticResource.TRANSTYPE_STOCKSPLIT;
            case STOCKDEMERGER -> MoneyWiseStaticResource.TRANSTYPE_STOCKDEMERGER;
            case STOCKTAKEOVER -> MoneyWiseStaticResource.TRANSTYPE_STOCKTAKEOVER;
            case SECURITYREPLACE -> MoneyWiseStaticResource.TRANSTYPE_SECURITYREPLACE;
            case SECURITYCLOSURE -> MoneyWiseStaticResource.TRANSTYPE_SECURITYCLOSURE;
            case STOCKRIGHTSISSUE -> MoneyWiseStaticResource.TRANSTYPE_STOCKRIGHTSISSUE;
            case PORTFOLIOXFER -> MoneyWiseStaticResource.TRANSTYPE_PORTFOLIOXFER;
            case OPTIONSGRANT -> MoneyWiseStaticResource.TRANSTYPE_OPTIONSGRANT;
            case OPTIONSVEST -> MoneyWiseStaticResource.TRANSTYPE_OPTIONSVEST;
            case OPTIONSEXPIRE -> MoneyWiseStaticResource.TRANSTYPE_OPTIONSEXPIRE;
            case OPTIONSEXERCISE -> MoneyWiseStaticResource.TRANSTYPE_OPTIONSEXERCISE;
            case PENSIONDRAWDOWN -> MoneyWiseStaticResource.TRANSTYPE_PENSIONDRAWDOWN;
            case PENSIONTAXFREE -> MoneyWiseStaticResource.TRANSTYPE_PENSIONTAXFREE;
            case EXPENSE -> MoneyWiseStaticResource.TRANSTYPE_EXPENSE;
            case BADDEBTCAPITAL -> MoneyWiseStaticResource.TRANSTYPE_BADDEBTCAPITAL;
            case BADDEBTINTEREST -> MoneyWiseStaticResource.TRANSTYPE_BADDEBTINTEREST;
            case LOCALTAXES -> MoneyWiseStaticResource.TRANSTYPE_LOCALTAXES;
            case WRITEOFF -> MoneyWiseStaticResource.TRANSTYPE_WRITEOFF;
            case RENTALEXPENSE -> MoneyWiseStaticResource.TRANSTYPE_RENTALEXPENSE;
            case ANNUITYPURCHASE -> MoneyWiseStaticResource.TRANSTYPE_ANNUITYPURCHASE;
            case LOANINTERESTCHARGED -> MoneyWiseStaticResource.TRANSTYPE_LOANINTCHARGE;
            case TAXRELIEF -> MoneyWiseStaticResource.TRANSTYPE_TAXRELIEF;
            case INCOMETAX -> MoneyWiseStaticResource.TRANSTYPE_INCOMETAX;
            case TAXEDINTEREST -> MoneyWiseStaticResource.TRANSTYPE_TAXEDINTEREST;
            case TAXFREEINTEREST -> MoneyWiseStaticResource.TRANSTYPE_TAXFREEINTEREST;
            case PEER2PEERINTEREST -> MoneyWiseStaticResource.TRANSTYPE_PEER2PEERINTEREST;
            case GROSSINTEREST -> MoneyWiseStaticResource.TRANSTYPE_GROSSINTEREST;
            case SHAREDIVIDEND -> MoneyWiseStaticResource.TRANSTYPE_SHAREDIVIDEND;
            case UNITTRUSTDIVIDEND -> MoneyWiseStaticResource.TRANSTYPE_UTDIVIDEND;
            case FOREIGNDIVIDEND -> MoneyWiseStaticResource.TRANSTYPE_FOREIGNDIVIDEND;
            case TAXFREEDIVIDEND -> MoneyWiseStaticResource.TRANSTYPE_TAXFREEDIVIDEND;
            case TAXEDLOYALTYBONUS -> MoneyWiseStaticResource.TRANSTYPE_TAXEDLOYALTYBONUS;
            case GROSSLOYALTYBONUS -> MoneyWiseStaticResource.TRANSTYPE_GROSSLOYALTYBONUS;
            case TAXFREELOYALTYBONUS -> MoneyWiseStaticResource.TRANSTYPE_TAXFREELOYALTYBONUS;
            case CHARGEABLEGAIN -> MoneyWiseStaticResource.TRANSTYPE_CHARGEABLEGAIN;
            case TAXFREEGAIN -> MoneyWiseStaticResource.TRANSTYPE_TAXFREEGAIN;
            case RESIDENTIALGAIN -> MoneyWiseStaticResource.TRANSTYPE_RESIDENTIALGAIN;
            case CAPITALGAIN -> MoneyWiseStaticResource.TRANSTYPE_CAPITALGAIN;
            case MARKETGROWTH -> MoneyWiseStaticResource.TRANSTYPE_MARKETGROWTH;
            case CURRENCYFLUCTUATION -> MoneyWiseStaticResource.TRANSTYPE_CURRFLUCT;
            case WITHHELD -> MoneyWiseStaticResource.TRANSTYPE_WITHHELD;
            case EMPLOYERNATINS -> MoneyWiseStaticResource.TRANSTYPE_EMPLOYERNATINS;
            case EMPLOYEENATINS -> MoneyWiseStaticResource.TRANSTYPE_EMPLOYEENATINS;
            case OPENINGBALANCE -> MoneyWiseStaticResource.TRANSTYPE_OPENINGBALANCE;
            case INCOMETOTALS -> MoneyWiseStaticResource.TRANSTYPE_INCOMETOTALS;
            case EXPENSETOTALS -> MoneyWiseStaticResource.TRANSTYPE_EXPENSETOTALS;
            case SECURITYPARENT -> MoneyWiseStaticResource.TRANSTYPE_SECURITYPARENT;
            case TOTALS -> MoneyWiseStaticResource.TRANSTYPE_TOTALS;
            default -> throw new IllegalArgumentException();
        };
    }
}
