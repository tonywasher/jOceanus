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

import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of AccountCategory Type Classes.
 */
public enum AccountCategoryClass implements StaticInterface {
    /**
     * Banking/Savings Account.
     * <p>
     * These are standard bank/building society accounts that hold money on behalf of the client. There is no distinction as to whether they are easy access or
     * restricted access. Each such account must be owned by an {@link #INSTITUTION} account.
     */
    SAVINGS(1, 0),

    /**
     * Bond Account.
     * <p>
     * This a bond account which is a specialised form of an {@link #SAVINGS} account. It has an associated maturity date for the account.
     */
    BOND(2, 1),

    /**
     * Cash Account.
     * <p>
     * This is a cash account and represents cash that is held by the client outside of any institution.
     */
    CASH(3, 2),

    /**
     * Shares.
     * <p>
     * This is a share account and represents stock held in a company. It is a specialised form of an {@link #ASSET} account. Each such account must be owned by
     * a {@link #PORTFOLIO} account.
     */
    SHARES(4, 3),

    /**
     * Unit Trust or OEIC.
     * <p>
     * This is a UnitTrust account and represents a mutual fund. It is a specialised form of an {@link #ASSET} account. Each such account must be owned by a
     * {@link #PORTFOLIO} account.
     */
    UNITTRUST(5, 4),

    /**
     * Life Bond.
     * <p>
     * This is a LifeBond account, which is a specialised form of an {@link #UNITTRUST} account. It simply differs in tax treatment.
     */
    LIFEBOND(6, 5),

    /**
     * Endowment.
     * <p>
     * This is a Endowment account, which is a specialised form of an {@link #UNITTRUST} account. It simply differs in tax treatment.
     */
    ENDOWMENT(7, 6),

    /**
     * Property.
     * <p>
     * This is a Property account, which is a specialised form of an {@link #ASSET} account. It simply differs in tax treatment.
     */
    PROPERTY(8, 7),

    /**
     * Vehicle.
     * <p>
     * This is a Vehicle account, which is a specialised form of an {@link #ASSET} account. It simply differs in tax treatment.
     */
    VEHICLE(9, 8),

    /**
     * Generic Asset Account.
     * <p>
     * This is an Asset account and represents items whose value is determined by the product of the number units held and the most recent unit price.
     */
    ASSET(10, 9),

    /**
     * CreditCard.
     * <p>
     * This is a Credit Card account, which is a specialised form of a {@link #LOAN} account. It simply differs in reporting treatment in that overall spend is
     * tracked.
     */
    CREDITCARD(11, 10),

    /**
     * PrivateLoan.
     * <p>
     * This is a PrivateLoan account, which is a specialised form of a {@link #LOAN} account. It represents a loan to/from the client from/to an individual
     * represented by an {@link #INDIVIDUAL} account.
     */
    PRIVATELOAN(12, 11),

    /**
     * Generic Loan Account.
     * <p>
     * This is a Loan account which represents a loan to/from the client from/to an {@link #INSTITUTION} account.
     */
    LOAN(13, 12),

    /**
     * Inland Revenue.
     * <p>
     * This is a singular account category representing the tax authority. All TaxCredits etc. are deemed to have been paid to the single account of this type.
     */
    TAXMAN(14, 13),

    /**
     * Government.
     * <p>
     * This is a singular account category representing the government. All Local Taxes should be paid to the single account of this type.
     */
    GOVERNMENT(15, 14),

    /**
     * Market pseudo account.
     * <p>
     * This is a singular account category representing the market. All increases/decreases in value of an asset that are due to fluctuations in unit prices are
     * viewed as income/expense from the single account of this type.
     */
    MARKET(16, 15),

    /**
     * Institution Account.
     * <p>
     * This is an institution (e.g. a bank) that holds another account of behalf of the client. It is a specialised form of the {@link #PAYEE} account.
     */
    INSTITUTION(17, 16),

    /**
     * LoanHolder Account.
     * <p>
     * This is an individual who can own a {@link #PRIVATELOAN} account, and who can be inherited from. It is a specialised form of the {@link #PAYEE} account.
     */
    INDIVIDUAL(18, 17),

    /**
     * Portfolio Account.
     * <p>
     * This is an institution (e.g. a bank) that holds another priced asset of behalf of the client. It is a specialised form of the {@link #INSTITUTION}
     * account, and is intended to ease management of priced assets.
     */
    PORTFOLIO(19, 18),

    /**
     * Employer Account.
     * <p>
     * This is an employer account which is a specialised form of the {@link #INSTITUTION} account. It has the ability to pay dividends.
     */
    EMPLOYER(20, 19),

    /**
     * Generic Payee Account.
     * <p>
     * This is a simple account that represents an entity that monies are paid to.
     */
    PAYEE(21, 20),

    /**
     * Savings Totals.
     * <p>
     * This is used for categories which simply own a set of savings sub-categories and is used purely for reporting purposes.
     */
    SAVINGSTOTALS(22, 21),

    /**
     * Cash Totals.
     * <p>
     * This is used for categories which simply own a set of cash sub-categories and is used purely for reporting purposes.
     */
    CASHTOTALS(23, 22),

    /**
     * Priced Totals.
     * <p>
     * This is used for categories which simply own a set of priced sub-categories and is used purely for reporting purposes.
     */
    PRICEDTOTALS(24, 23),

    /**
     * Loan Totals.
     * <p>
     * This is used for categories which simply own a set of loan sub-categories and is used purely for reporting purposes.
     */
    LOANTOTALS(25, 24),

    /**
     * Total Account.
     * <p>
     * This is used for the total of all categories and is used purely for reporting purposes.
     */
    TOTALS(26, 27);

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountCategoryClass.class.getName());

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
     * @throws JOceanusException on error
     */
    public static AccountCategoryClass fromId(final int id) throws JOceanusException {
        for (AccountCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid Account Category Class Id: " + id);
    }

    /**
     * Determine whether the AccountCategory is nonAsset.
     * @return <code>true</code> if the account is nonAsset, <code>false</code> otherwise.
     */
    public boolean isNonAsset() {
        switch (this) {
            case EMPLOYER:
            case PORTFOLIO:
            case INDIVIDUAL:
            case INSTITUTION:
            case PAYEE:
            case TAXMAN:
            case GOVERNMENT:
            case MARKET:
            case SAVINGSTOTALS:
            case CASHTOTALS:
            case PRICEDTOTALS:
            case LOANTOTALS:
            case TOTALS:
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
            case ASSET:
            case SHARES:
            case LIFEBOND:
            case UNITTRUST:
            case ENDOWMENT:
            case PROPERTY:
            case VEHICLE:
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
            case SHARES:
            case UNITTRUST:
            case EMPLOYER:
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
            case SAVINGS:
            case BOND:
            case CASH:
            case LOAN:
            case PRIVATELOAN:
            case CREDITCARD:
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
            case LOAN:
            case PRIVATELOAN:
            case CREDITCARD:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is deposit.
     * @return <code>true</code> if the account category type is deposit, <code>false</code> otherwise.
     */
    public boolean isDeposit() {
        switch (this) {
            case SAVINGS:
            case BOND:
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
        return this == CASH;
    }

    /**
     * Determine whether the AccountCategoryType is shares.
     * @return <code>true</code> if the account category type is shares, <code>false</code> otherwise.
     */
    public boolean isShares() {
        return this == SHARES;
    }

    /**
     * Determine whether the category type is singular.
     * @return <code>true</code> if the account category type is singular, <code>false</code> otherwise.
     */
    public boolean isSingular() {
        switch (this) {
            case TAXMAN:
            case GOVERNMENT:
            case MARKET:
            case SAVINGSTOTALS:
            case CASHTOTALS:
            case PRICEDTOTALS:
            case LOANTOTALS:
            case TOTALS:
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
            case SAVINGS:
            case BOND:
            case ASSET:
            case SHARES:
            case UNITTRUST:
            case LIFEBOND:
            case ENDOWMENT:
            case PROPERTY:
            case VEHICLE:
            case LOAN:
            case PRIVATELOAN:
            case CREDITCARD:
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
            case SHARES:
            case UNITTRUST:
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
            case TAXMAN:
            case GOVERNMENT:
            case INSTITUTION:
            case EMPLOYER:
            case PORTFOLIO:
            case INDIVIDUAL:
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
            case ASSET:
            case PROPERTY:
            case VEHICLE:
            case ENDOWMENT:
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
            case SAVINGS:
            case BOND:
            case SHARES:
            case UNITTRUST:
            case PROPERTY:
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
            case INDIVIDUAL:
            case INSTITUTION:
            case GOVERNMENT:
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
            case SHARES:
            case UNITTRUST:
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
            case SHARES:
            case LIFEBOND:
            case UNITTRUST:
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
            case SAVINGSTOTALS:
            case CASHTOTALS:
            case PRICEDTOTALS:
            case LOANTOTALS:
            case TOTALS:
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
            case SAVINGSTOTALS:
            case CASHTOTALS:
            case PRICEDTOTALS:
            case LOANTOTALS:
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
            case SAVINGSTOTALS:
            case CASHTOTALS:
            case PRICEDTOTALS:
            case LOANTOTALS:
            case TAXMAN:
            case GOVERNMENT:
            case MARKET:
            case INSTITUTION:
            case INDIVIDUAL:
            case EMPLOYER:
            case PORTFOLIO:
            case PAYEE:
                return TOTALS;
            case SAVINGS:
            case BOND:
                return SAVINGSTOTALS;
            case CASH:
                return CASHTOTALS;
            case SHARES:
            case UNITTRUST:
            case LIFEBOND:
            case ENDOWMENT:
            case PROPERTY:
            case VEHICLE:
            case ASSET:
                return PRICEDTOTALS;
            case LOAN:
            case PRIVATELOAN:
            case CREDITCARD:
                return LOANTOTALS;
            case TOTALS:
            default:
                return null;
        }
    }
}
