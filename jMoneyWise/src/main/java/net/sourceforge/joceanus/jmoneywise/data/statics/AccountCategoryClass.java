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
 * Enumeration of AccountCategory Type Classes.
 */
public enum AccountCategoryClass implements StaticInterface {
    /**
     * Banking/Savings Account.
     * <p>
     * These are standard bank/building society accounts that hold money on behalf of the client. There is no distinction as to whether they are easy access or
     * restricted access. Each such account must be owned by an {@link #Institution} account.
     */
    Savings(1, 0),

    /**
     * Bond Account.
     * <p>
     * This a bond account which is a specialised form of an {@link #Savings} account. It has an associated maturity date for the account.
     */
    Bond(2, 1),

    /**
     * Cash Account.
     * <p>
     * This is a cash account and represents cash that is held by the client outside of any institution.
     */
    Cash(3, 2),

    /**
     * Shares.
     * <p>
     * This is a share account and represents stock held in a company. It is a specialised form of an {@link #Asset} account. Each such account must be owned by
     * a {@link #Portfolio} account.
     */
    Shares(4, 3),

    /**
     * Unit Trust or OEIC.
     * <p>
     * This is a UnitTrust account and represents a mutual fund. It is a specialised form of an {@link #Asset} account. Each such account must be owned by a
     * {@link #Portfolio} account.
     */
    UnitTrust(5, 4),

    /**
     * Life Bond.
     * <p>
     * This is a LifeBond account, which is a specialised form of an {@link #UnitTrust} account. It simply differs in tax treatment.
     */
    LifeBond(6, 5),

    /**
     * Endowment.
     * <p>
     * This is a Endowment account, which is a specialised form of an {@link #UnitTrust} account. It simply differs in tax treatment.
     */
    Endowment(7, 6),

    /**
     * Property.
     * <p>
     * This is a Property account, which is a specialised form of an {@link #Asset} account. It simply differs in tax treatment.
     */
    Property(8, 7),

    /**
     * Vehicle.
     * <p>
     * This is a Vehicle account, which is a specialised form of an {@link #Asset} account. It simply differs in tax treatment.
     */
    Vehicle(9, 8),

    /**
     * Generic Asset Account.
     * <p>
     * This is an Asset account and represents items whose value is determined by the product of the number units held and the most recent unit price.
     */
    Asset(10, 9),

    /**
     * CreditCard.
     * <p>
     * This is a Credit Card account, which is a specialised form of a {@link #Loan} account. It simply differs in reporting treatment in that overall spend is
     * tracked.
     */
    CreditCard(11, 10),

    /**
     * PrivateLoan.
     * <p>
     * This is a PrivateLoan account, which is a specialised form of a {@link #Loan} account. It represents a loan to/from the client from/to an individual
     * represented by an {@link #Individual} account.
     */
    PrivateLoan(12, 11),

    /**
     * Generic Loan Account.
     * <p>
     * This is a Loan account which represents a loan to/from the client from/to an {@link #Institution} account.
     */
    Loan(13, 12),

    /**
     * Inland Revenue.
     * <p>
     * This is a singular account category representing the tax authority. All TaxCredits etc. are deemed to have been paid to the single account of this type.
     */
    TaxMan(14, 13),

    /**
     * Government.
     * <p>
     * This is a singular account category representing the government. All Local Taxes should be paid to the single account of this type.
     */
    Government(15, 14),

    /**
     * Market pseudo account.
     * <p>
     * This is a singular account category representing the market. All increases/decreases in value of an asset that are due to fluctuations in unit prices are
     * viewed as income/expense from the single account of this type.
     */
    Market(16, 15),

    /**
     * OpeningBalance pseudo account.
     * <p>
     * This is a singular account category representing the source of account opening balances. All accounts that are created with an opening balance are viewed
     * as having received the opening balance from this account.
     */
    OpeningBalance(17, 16),

    /**
     * Institution Account.
     * <p>
     * This is an institution (e.g. a bank) that holds another account of behalf of the client. It is a specialised form of the {@link #Payee} account.
     */
    Institution(18, 17),

    /**
     * LoanHolder Account.
     * <p>
     * This is an individual who can own a {@link #PrivateLoan} account, and who can be inherited from. It is a specialised form of the {@link #Payee} account.
     */
    Individual(19, 18),

    /**
     * Portfolio Account.
     * <p>
     * This is an institution (e.g. a bank) that holds another priced asset of behalf of the client. It is a specialised form of the {@link #Institution}
     * account, and is intended to ease management of priced assets.
     */
    Portfolio(20, 19),

    /**
     * Employer Account.
     * <p>
     * This is an employer account which is a specialised form of the {@link #Institution} account. It has the ability to pay dividends.
     */
    Employer(21, 20),

    /**
     * Generic Payee Account.
     * <p>
     * This is a simple account that represents an entity that monies are paid to.
     */
    Payee(22, 21),

    /**
     * Savings Totals.
     * <p>
     * This is used for categories which simply own a set of savings sub-categories and is used purely for reporting purposes.
     */
    SavingsTotals(23, 22),

    /**
     * Cash Totals.
     * <p>
     * This is used for categories which simply own a set of cash sub-categories and is used purely for reporting purposes.
     */
    CashTotals(24, 23),

    /**
     * Priced Totals.
     * <p>
     * This is used for categories which simply own a set of priced sub-categories and is used purely for reporting purposes.
     */
    PricedTotals(25, 24),

    /**
     * Loan Totals.
     * <p>
     * This is used for categories which simply own a set of loan sub-categories and is used purely for reporting purposes.
     */
    LoanTotals(26, 25),

    /**
     * Total Account.
     * <p>
     * This is used for the total of all categories and is used purely for reporting purposes.
     */
    Totals(27, 26);

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
     * @param uId the Id
     * @param uOrder the default order.
     */
    private AccountCategoryClass(final int uId,
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
    public static AccountCategoryClass fromId(final int id) throws JDataException {
        for (AccountCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Account Category Class Id: "
                                                      + id);
    }

    /**
     * Determine whether the AccountCategory is nonAsset.
     * @return <code>true</code> if the account is nonAsset, <code>false</code> otherwise.
     */
    public boolean isNonAsset() {
        switch (this) {
            case Employer:
            case Portfolio:
            case Individual:
            case Institution:
            case Payee:
            case TaxMan:
            case Government:
            case Market:
            case OpeningBalance:
            case SavingsTotals:
            case CashTotals:
            case PricedTotals:
            case LoanTotals:
            case Totals:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategory is Asset.
     * @return <code>true</code> if the account is Asset, <code>false</code> otherwise.
     */
    public boolean isAsset() {
        return !isNonAsset();
    }

    /**
     * Determine whether the AccountCategoryType has units.
     * @return <code>true</code> if the account category type has Units, <code>false</code> otherwise.
     */
    public boolean hasUnits() {
        switch (this) {
            case Asset:
            case Shares:
            case LifeBond:
            case UnitTrust:
            case Endowment:
            case Property:
            case Vehicle:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is a dividend provider.
     * @return <code>true</code> if the account category type is a dividend provider, <code>false</code> otherwise.
     */
    public boolean isDividend() {
        switch (this) {
            case Shares:
            case UnitTrust:
            case Employer:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType has explicit value.
     * @return <code>true</code> if the category account type has value, <code>false</code> otherwise.
     */
    public boolean hasValue() {
        switch (this) {
            case Savings:
            case Bond:
            case Cash:
            case Loan:
            case PrivateLoan:
            case CreditCard:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is a loan.
     * @return <code>true</code> if the account category type is a loan, <code>false</code> otherwise.
     */
    public boolean isLoan() {
        switch (this) {
            case Loan:
            case PrivateLoan:
            case CreditCard:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is savings.
     * @return <code>true</code> if the account category type is savings, <code>false</code> otherwise.
     */
    public boolean isSavings() {
        switch (this) {
            case Savings:
            case Bond:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is cash.
     * @return <code>true</code> if the account category type is cash, <code>false</code> otherwise.
     */
    public boolean isCash() {
        return (this == Cash);
    }

    /**
     * Determine whether the AccountCategoryType is shares.
     * @return <code>true</code> if the account category type is shares, <code>false</code> otherwise.
     */
    public boolean isShares() {
        return (this == Shares);
    }

    /**
     * Determine whether the category type is singular.
     * @return <code>true</code> if the account category type is singular, <code>false</code> otherwise.
     */
    public boolean isSingular() {
        switch (this) {
            case OpeningBalance:
            case TaxMan:
            case Government:
            case Market:
            case Totals:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is a child, and needs a parent.
     * @return <code>true</code> if the account category type is a child, <code>false</code> otherwise.
     */
    public boolean isChild() {
        switch (this) {
            case Savings:
            case Bond:
            case Asset:
            case Shares:
            case UnitTrust:
            case LifeBond:
            case Endowment:
            case Property:
            case Vehicle:
            case Loan:
            case PrivateLoan:
            case CreditCard:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType can alias.
     * @return <code>true</code> if the account category type can alias, <code>false</code> otherwise.
     */
    public boolean canAlias() {
        switch (this) {
            case Shares:
            case UnitTrust:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType can parent accounts.
     * @return <code>true</code> if the account category type can parent accounts, <code>false</code> otherwise.
     */
    public boolean canParentAccount() {
        switch (this) {
            case TaxMan:
            case Government:
            case Institution:
            case Employer:
            case Portfolio:
            case Individual:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType needs market as a parent.
     * @return <code>true</code> if the account category type needs market as a parent, <code>false</code> otherwise.
     */
    public boolean needsMarketParent() {
        switch (this) {
            case Asset:
            case Property:
            case Vehicle:
            case Endowment:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType can be tax free.
     * @return <code>true</code> if the account category type can be tax free, <code>false</code> otherwise.
     */
    public boolean canTaxFree() {
        switch (this) {
            case Savings:
            case Bond:
            case Shares:
            case UnitTrust:
            case Property:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType can issue grant income.
     * @return <code>true</code> if the account category type can grant income, <code>false</code> otherwise.
     */
    public boolean canGrant() {
        switch (this) {
            case Individual:
            case Institution:
            case Government:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is subject to Capital Gains.
     * @return <code>true</code> if the account category type is subject to Capital Gains, <code>false</code> otherwise.
     */
    public boolean isCapitalGains() {
        switch (this) {
            case Shares:
            case UnitTrust:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is Capital.
     * @return <code>true</code> if the account category type is Capital, <code>false</code> otherwise.
     */
    public boolean isCapital() {
        switch (this) {
            case Shares:
            case LifeBond:
            case UnitTrust:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is a parent category.
     * @return <code>true</code> if the account category type is a parent category, <code>false</code> otherwise.
     */
    public boolean isParentCategory() {
        switch (this) {
            case SavingsTotals:
            case CashTotals:
            case PricedTotals:
            case LoanTotals:
            case Totals:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is a subTotal.
     * @return <code>true</code> if the event category type is a subTotal, <code>false</code> otherwise.
     */
    public boolean isSubTotal() {
        switch (this) {
            case SavingsTotals:
            case CashTotals:
            case PricedTotals:
            case LoanTotals:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine the type of subTotal parent.
     * @return <code>true</code> if the event category type is a subTotal, <code>false</code> otherwise.
     */
    public AccountCategoryClass getParentClass() {
        switch (this) {
            case SavingsTotals:
            case CashTotals:
            case PricedTotals:
            case LoanTotals:
            case TaxMan:
            case Government:
            case Market:
            case Institution:
            case Individual:
            case Employer:
            case OpeningBalance:
            case Portfolio:
            case Payee:
                return Totals;
            case Savings:
            case Bond:
                return SavingsTotals;
            case Cash:
                return CashTotals;
            case Shares:
            case UnitTrust:
            case LifeBond:
            case Endowment:
            case Property:
            case Vehicle:
            case Asset:
                return PricedTotals;
            case Loan:
            case PrivateLoan:
            case CreditCard:
                return LoanTotals;
            case Totals:
            default:
                return null;
        }
    }
}
