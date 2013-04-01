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
     * Generic Asset Account.
     * <p>
     * This is an Asset account and represents items whose value is determined by the product of the number units held and the most recent unit price.
     */
    Asset(8, 7),

    /**
     * CreditCard.
     * <p>
     * This is a Credit Card account, which is a specialised form of a {@link #Loan} account. It simply differs in reporting treatment in that overall spend is
     * tracked.
     */
    CreditCard(9, 8),

    /**
     * PrivateLoan.
     * <p>
     * This is a PrivateLoan account, which is a specialised form of a {@link #Loan} account. It represents a loan to/from the client from/to an individual
     * represented by a {@link #LoanHolder} account.
     */
    PrivateLoan(10, 9),

    /**
     * Generic Loan Account.
     * <p>
     * This is a Loan account which represents a loan to/from the client from/to an {@link #Institution} account.
     */
    Loan(11, 10),

    /**
     * Inland Revenue.
     * <p>
     * This is a singular account category representing the tax authority. All TaxCredits etc. are deemed to have been paid to the single account of this type.
     */
    TaxMan(12, 11),

    /**
     * Market pseudo account.
     * <p>
     * This is a singular account category representing the market. All increases/decreases in value of an asset that are due to fluctuations in unit prices are
     * viewed as income/expense from the single account of this type.
     */
    Market(13, 12),

    /**
     * OpeningBalance pseudo account.
     * <p>
     * This is a singular account category representing the source of account opening balances. All accounts that are created with an opening balance are viewed
     * as having received the opening balance from this account.
     */
    OpeningBalance(14, 13),

    /**
     * Institution Account.
     * <p>
     * This is an institution (e.g. a bank) that holds another account of behalf of the client. It is a specialised form of the {@link #Payee} account.
     */
    Institution(15, 14),

    /**
     * LoanHolder Account.
     * <p>
     * This is an individual who owns a {@link #PrivateLoan} account. It is a specialised form of the {@link #Payee} account.
     */
    LoanHolder(16, 15),

    /**
     * Portfolio Account.
     * <p>
     * This is an institution (e.g. a bank) that holds another priced asset of behalf of the client. It is a specialised form of the {@link #Institution}
     * account, and is intended to ease management of priced assets.
     */
    Portfolio(17, 16),

    /**
     * Employer Account.
     * <p>
     * This is an employer account which is a specialised form of the {@link #Institution} account. It has the ability to pay dividends.
     */
    Employer(18, 17),

    /**
     * Generic Payee Account.
     * <p>
     * This is a simple account that represents an entity that monies are paid to.
     */
    Payee(19, 18),

    /**
     * Category Account.
     * <p>
     * This is used for categories which simply own a set of sub-categories and is used purely for reporting purposes.
     */
    Category(20, 19);

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
            case LoanHolder:
            case Institution:
            case Payee:
            case TaxMan:
            case Market:
            case OpeningBalance:
            case Category:
                return true;
            default:
                return false;
        }
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
     * Determine whether the AccountCategoryType has value.
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
            case Cash:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the category type is singular.
     * @return <code>true</code> if the account category type is singular, <code>false</code> otherwise.
     */
    public boolean isSingular() {
        switch (this) {
            case OpeningBalance:
            case TaxMan:
            case Market:
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
     * Determine whether the AccountCategoryType can parent.
     * @return <code>true</code> if the account category type can parent, <code>false</code> otherwise.
     */
    public boolean canParent() {
        switch (this) {
            case Institution:
            case Employer:
            case Portfolio:
            case LoanHolder:
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
}
