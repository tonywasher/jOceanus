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

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of EventCategory Classes.
 */
public enum EventCategoryClass implements StaticInterface {
    /**
     * Taxed Salary Income.
     */
    TAXEDINCOME(1, 0),

    /**
     * Rental Income.
     */
    RENTALINCOME(2, 1),

    /**
     * RoomRental Income.
     */
    ROOMRENTALINCOME(3, 2),

    /**
     * Interest Income.
     */
    INTEREST(4, 3),

    /**
     * Dividend Income.
     */
    DIVIDEND(5, 4),

    /**
     * Grant Income.
     */
    GRANTINCOME(6, 5),

    /**
     * Benefit Income.
     */
    BENEFITINCOME(7, 6),

    /**
     * Gifted Income.
     */
    GIFTEDINCOME(8, 7),

    /**
     * Inheritance.
     */
    INHERITED(9, 8),

    /**
     * Interest earned on Loans.
     */
    LOANINTERESTEARNED(10, 9),

    /**
     * Other Income.
     */
    OTHERINCOME(11, 10),

    /**
     * Transfer.
     */
    TRANSFER(12, 11),

    /**
     * Stock Adjustment.
     */
    STOCKADJUST(13, 12),

    /**
     * Stock Split.
     */
    STOCKSPLIT(14, 13),

    /**
     * Stock DeMerger.
     */
    STOCKDEMERGER(15, 14),

    /**
     * Stock Takeover.
     */
    STOCKTAKEOVER(16, 15),

    /**
     * Stock Rights Taken.
     */
    STOCKRIGHTSTAKEN(17, 16),

    /**
     * Stock Rights Waived.
     */
    STOCKRIGHTSWAIVED(18, 17),

    /**
     * Stock Options Granted.
     */
    OPTIONSGRANT(19, 18),

    /**
     * Stock Options Vested.
     */
    OPTIONSVEST(20, 19),

    /**
     * Stock Options Exercised.
     */
    OPTIONSEXERCISE(21, 20),

    /**
     * Stock Options Expired.
     */
    OPTIONSEXPIRE(22, 21),

    /**
     * Expense.
     */
    EXPENSE(23, 22),

    /**
     * LocalTaxes.
     */
    LOCALTAXES(24, 23),

    /**
     * Write Off.
     */
    WRITEOFF(25, 24),

    /**
     * Interest earned on Loans.
     */
    LOANINTERESTCHARGED(26, 25),

    /**
     * Tax Relief.
     */
    TAXRELIEF(27, 26),

    /**
     * Tax Settlement.
     */
    TAXSETTLEMENT(28, 27),

    /**
     * Taxed Interest.
     */
    TAXEDINTEREST(29, 28),

    /**
     * Gross Interest.
     */
    GROSSINTEREST(30, 29),

    /**
     * Tax Free Interest.
     */
    TAXFREEINTEREST(31, 30),

    /**
     * Share Dividend Income.
     */
    SHAREDIVIDEND(32, 31),

    /**
     * Unit Trust Dividend Income.
     */
    UNITTRUSTDIVIDEND(33, 32),

    /**
     * Tax Free Dividend.
     */
    TAXFREEDIVIDEND(34, 33),

    /**
     * Taxable Gain.
     */
    TAXABLEGAIN(35, 34),

    /**
     * Capital Gain.
     */
    CAPITALGAIN(36, 35),

    /**
     * TaxFreeCapital Gain.
     */
    TAXFREEGAIN(37, 36),

    /**
     * Market Growth.
     */
    MARKETGROWTH(38, 37),

    /**
     * CurrencyFluctuation.
     */
    CURRENCYFLUCTUATION(39, 38),

    /**
     * Tax Credit.
     * <p>
     * This is a singular category catching tax credits associated with an event.
     */
    TAXCREDIT(40, 39),

    /**
     * National Insurance.
     * <p>
     * This is a singular category catching national insurance payments associated with an event.
     */
    NATINSURANCE(41, 40),

    /**
     * Deemed Benefit.
     * <p>
     * This is a singular category catching deemed benefit payments associated with an event.
     */
    DEEMEDBENEFIT(42, 41),

    /**
     * CharityDonation.
     * <p>
     * This is a singular category catching charity donations associated with an event.
     */
    CHARITYDONATION(43, 42),

    /**
     * Income Totals.
     * <p>
     * This is used for categories which simply own a set of income sub-categories and is used purely for reporting purposes.
     */
    INCOMETOTALS(44, 43),

    /**
     * Expense Totals.
     * <p>
     * This is used for categories which simply own a set of expense sub-categories and is used purely for reporting purposes.
     */
    EXPENSETOTALS(45, 44),

    /**
     * Stock Parent.
     * <p>
     * This is used for categories which simply own a set of stock transfer sub-categories and is used purely for holding purposes.
     */
    STOCKPARENT(46, 45),

    /**
     * Totals.
     * <p>
     * This is used for the total of all non-transfer categories and is used purely for reporting purposes.
     */
    TOTALS(47, 46);

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventCategoryClass.class.getName());

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
    private EventCategoryClass(final int uId,
                               final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
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
    public static EventCategoryClass fromId(final int id) throws JOceanusException {
        for (EventCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid Transaction Class Id: "
                                          + id);
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
            case CAPITALGAIN:
            case TAXFREEGAIN:
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
     * Determine whether the CategoryType uses debit parent as payee.
     * @return <code>true</code> if the category uses parent payee, <code>false</code> otherwise.
     */
    public boolean isDebitParentPayee() {
        switch (this) {
            case INTEREST:
            case DIVIDEND:
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
            case OPTIONSGRANT:
            case OPTIONSVEST:
            case OPTIONSEXPIRE:
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
            case OPTIONSGRANT:
            case OPTIONSVEST:
            case OPTIONSEXPIRE:
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
            case STOCKADJUST:
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKTAKEOVER:
            case STOCKRIGHTSWAIVED:
            case STOCKRIGHTSTAKEN:
            case OPTIONSGRANT:
            case OPTIONSVEST:
            case OPTIONSEXERCISE:
            case OPTIONSEXPIRE:
                return true;
            default:
                return false;
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
            case OPTIONSGRANT:
            case OPTIONSVEST:
            case OPTIONSEXERCISE:
            case OPTIONSEXPIRE:
            case STOCKPARENT:
                return true;
            default:
                return false;
        }
    }
}
