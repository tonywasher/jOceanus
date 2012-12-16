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
 * Enumeration of Account Type Classes.
 */
public enum AccountClass implements StaticInterface {
    /**
     * Current Banking Account.
     */
    Current(1, 0),

    /**
     * Savings Account with instant access.
     */
    Instant(2, 1),

    /**
     * Savings Account with limited access.
     */
    Restricted(3, 2),

    /**
     * Fixed Rate Savings Bond.
     */
    Bond(4, 3),

    /**
     * Instant Access Cash ISA Account.
     */
    CashISA(5, 4),

    /**
     * Fixed Rate Cash ISA Bond.
     */
    ISABond(6, 5),

    /**
     * Index Linked Bond.
     */
    TaxFreeBond(7, 6),

    /**
     * Equity Bond.
     */
    EquityBond(8, 7),

    /**
     * Shares.
     */
    Shares(9, 8),

    /**
     * Unit Trust or OEIC.
     */
    UnitTrust(10, 9),

    /**
     * Life Bond.
     */
    LifeBond(11, 10),

    /**
     * Unit Trust or OEIC in ISA wrapper.
     */
    UnitISA(12, 11),

    /**
     * Car.
     */
    Car(13, 12),

    /**
     * House.
     */
    House(14, 13),

    /**
     * Debts.
     */
    Debts(15, 16),

    /**
     * CreditCard.
     */
    CreditCard(16, 15),

    /**
     * WriteOff.
     */
    WriteOff(17, 22),

    /**
     * External Account.
     */
    External(18, 24),

    /**
     * Employer Account.
     */
    Employer(19, 18),

    /**
     * Asset Owner Account.
     */
    Owner(20, 25),

    /**
     * Market.
     */
    Market(21, 26),

    /**
     * Inland Revenue.
     */
    TaxMan(22, 20),

    /**
     * Cash.
     */
    Cash(23, 19),

    /**
     * Inheritance.
     */
    Inheritance(24, 21),

    /**
     * Endowment.
     */
    Endowment(25, 14),

    /**
     * Benefit.
     */
    Benefit(26, 23),

    /**
     * Deferred between tax years.
     */
    Deferred(27, 17);

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
    private AccountClass(final int uId,
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
    public static AccountClass fromId(final int id) throws JDataException {
        for (AccountClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Account Class Id: "
                                                      + id);
    }

    /**
     * Determine whether the AccountType is external.
     * @return <code>true</code> if the account is external, <code>false</code> otherwise.
     */
    public boolean isExternal() {
        switch (this) {
            case External:
            case Owner:
            case Employer:
            case Inheritance:
            case Cash:
            case WriteOff:
            case TaxMan:
            case Market:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is special external.
     * @return <code>true</code> if the account is special external, <code>false</code> otherwise.
     */
    public boolean isSpecial() {
        switch (this) {
            case Inheritance:
            case Cash:
            case WriteOff:
            case TaxMan:
            case Market:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is priced.
     * @return <code>true</code> if the account is priced, <code>false</code> otherwise.
     */
    public boolean isPriced() {
        switch (this) {
            case House:
            case Car:
            case Shares:
            case LifeBond:
            case UnitTrust:
            case UnitISA:
            case Endowment:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is dividend provider.
     * @return <code>true</code> if the account is a dividend provider, <code>false</code> otherwise.
     */
    public boolean isDividend() {
        switch (this) {
            case Shares:
            case Employer:
            case UnitTrust:
            case UnitISA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is unit dividend provider.
     * @return <code>true</code> if the account is a unit dividend provider, <code>false</code> otherwise.
     */
    public boolean isUnitTrust() {
        switch (this) {
            case UnitTrust:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is tax-free provider.
     * @return <code>true</code> if the account is a tax free dividend provider, <code>false</code> otherwise.
     */
    public boolean isTaxFree() {
        switch (this) {
            case UnitISA:
            case CashISA:
            case ISABond:
            case TaxFreeBond:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is savings.
     * @return <code>true</code> if the account is savings, <code>false</code> otherwise.
     */
    public boolean isMoney() {
        switch (this) {
            case Current:
            case Instant:
            case Restricted:
            case Bond:
            case CashISA:
            case ISABond:
            case TaxFreeBond:
            case EquityBond:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is a bond.
     * @return <code>true</code> if the account is a bond, <code>false</code> otherwise.
     */
    public boolean isBond() {
        switch (this) {
            case Bond:
            case ISABond:
            case TaxFreeBond:
            case EquityBond:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is debt.
     * @return <code>true</code> if the account is debt, <code>false</code> otherwise.
     */
    public boolean isDebt() {
        switch (this) {
            case Debts:
            case CreditCard:
            case Deferred:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is child.
     * @return <code>true</code> if the account is child, <code>false</code> otherwise.
     */
    public boolean isChild() {
        switch (this) {
            case Current:
            case Instant:
            case Restricted:
            case CashISA:
            case Bond:
            case ISABond:
            case TaxFreeBond:
            case EquityBond:
            case Shares:
            case UnitTrust:
            case LifeBond:
            case UnitISA:
            case CreditCard:
            case Endowment:
            case Debts:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is reserved.
     * @return <code>true</code> if the account is reserved, <code>false</code> otherwise.
     */
    public boolean isReserved() {
        switch (this) {
            case Deferred:
            case TaxMan:
            case Cash:
            case WriteOff:
            case Market:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType can alias.
     * @return <code>true</code> if the account can alias, <code>false</code> otherwise.
     */
    public boolean canAlias() {
        switch (this) {
            case UnitISA:
            case UnitTrust:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is subject to Capital Gains.
     * @return <code>true</code> if the account is subject to Capital Gains, <code>false</code> otherwise.
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
     * Determine whether the AccountType is Capital.
     * @return <code>true</code> if the account is Capital, <code>false</code> otherwise.
     */
    public boolean isCapital() {
        switch (this) {
            case Shares:
            case LifeBond:
            case UnitTrust:
            case UnitISA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is Owner.
     * @return <code>true</code> if the account is Owner, <code>false</code> otherwise.
     */
    public boolean isOwner() {
        switch (this) {
            case Inheritance:
            case Owner:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountType is cash.
     * @return <code>true</code> if the account is cash, <code>false</code> otherwise.
     */
    public boolean isCash() {
        return (this == Cash);
    }

    /**
     * Determine whether the AccountType is inheritance.
     * @return <code>true</code> if the account is inheritance, <code>false</code> otherwise.
     */
    public boolean isInheritance() {
        return (this == Inheritance);
    }

    /**
     * Determine whether the AccountType is WriteOff.
     * @return <code>true</code> if the account is WriteOff, <code>false</code> otherwise.
     */
    public boolean isWriteOff() {
        return (this == WriteOff);
    }

    /**
     * Determine whether the AccountType is market.
     * @return <code>true</code> if the account is market, <code>false</code> otherwise.
     */
    public boolean isMarket() {
        return (this == Market);
    }

    /**
     * Determine whether the AccountType is TaxMan.
     * @return <code>true</code> if the account is TaxMan, <code>false</code> otherwise.
     */
    public boolean isTaxMan() {
        return (this == TaxMan);
    }

    /**
     * Determine whether the AccountType is Employer.
     * @return <code>true</code> if the account is employer, <code>false</code> otherwise.
     */
    public boolean isEmployer() {
        return (this == Employer);
    }

    /**
     * Determine whether the AccountType is endowment.
     * @return <code>true</code> if the account is endowment, <code>false</code> otherwise.
     */
    public boolean isEndowment() {
        return (this == Endowment);
    }

    /**
     * Determine whether the AccountType is deferred.
     * @return <code>true</code> if the account is deferred, <code>false</code> otherwise.
     */
    public boolean isDeferred() {
        return (this == Deferred);
    }

    /**
     * Determine whether the AccountType is benefit.
     * @return <code>true</code> if the account is benefit, <code>false</code> otherwise.
     */
    public boolean isBenefit() {
        return (this == Benefit);
    }

    /**
     * Determine whether the AccountType is a Share.
     * @return <code>true</code> if the account is Share, <code>false</code> otherwise.
     */
    public boolean isShares() {
        return (this == Shares);
    }

    /**
     * Determine whether the AccountType is a LifeBond.
     * @return <code>true</code> if the account is LifeBond, <code>false</code> otherwise.
     */
    public boolean isLifeBond() {
        return (this == LifeBond);
    }
}
