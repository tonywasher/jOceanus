/*******************************************************************************
 * JFinanceApp: Finance Application
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
package net.sourceforge.JFinanceApp.data.statics;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.data.StaticData;
import net.sourceforge.JFinanceApp.data.FinanceData;

/**
 * AccountType data type.
 * @author Tony Washer
 */
public class AccountType extends StaticData<AccountType, AccountClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountType.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Account class of the AccountType.
     * @return the class
     */
    public AccountClass getAccountClass() {
        return super.getStaticClass();
    }

    @Override
    public AccountType getBase() {
        return (AccountType) super.getBase();
    }

    /**
     * Construct a copy of an Account Type.
     * @param pList The list to associate the Account Type with
     * @param pAcType The Account Type to copy
     */
    protected AccountType(final AccountTypeList pList,
                          final AccountType pAcType) {
        super(pList, pAcType);
    }

    /**
     * Construct a standard account type on load.
     * @param pList The list to associate the Account Type with
     * @param sName Name of Account Type
     * @throws JDataException on error
     */
    private AccountType(final AccountTypeList pList,
                        final String sName) throws JDataException {
        super(pList, sName);
    }

    /**
     * Construct a standard account type on load.
     * @param pList The list to associate the Account Type with
     * @param uId the id
     * @param isEnabled is the account type enabled
     * @param uOrder the sort order
     * @param pName Name of Account Type
     * @param pDesc Description of Account Type
     * @throws JDataException on error
     */
    private AccountType(final AccountTypeList pList,
                        final int uId,
                        final boolean isEnabled,
                        final int uOrder,
                        final String pName,
                        final String pDesc) throws JDataException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Construct a standard account type on load.
     * @param pList The list to associate the Account Type with
     * @param uId ID of Account Type
     * @param uControlId the control id of the new item
     * @param isEnabled is the account type enabled
     * @param uOrder the sort order
     * @param pName Encrypted Name of Account Type
     * @param pDesc Encrypted Description of Account Type
     * @throws JDataException on error
     */
    private AccountType(final AccountTypeList pList,
                        final int uId,
                        final int uControlId,
                        final boolean isEnabled,
                        final int uOrder,
                        final byte[] pName,
                        final byte[] pDesc) throws JDataException {
        super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Determine whether the AccountType is external.
     * @return <code>true</code> if the account is external, <code>false</code> otherwise.
     */
    public boolean isExternal() {
        switch (getAccountClass()) {
            case EXTERNAL:
            case OWNER:
            case EMPLOYER:
            case INHERITANCE:
            case CASH:
            case WRITEOFF:
            case TAXMAN:
            case MARKET:
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
        switch (getAccountClass()) {
            case INHERITANCE:
            case CASH:
            case WRITEOFF:
            case TAXMAN:
            case MARKET:
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
        switch (getAccountClass()) {
            case HOUSE:
            case CAR:
            case SHARES:
            case LIFEBOND:
            case UNITTRUST:
            case UNITISA:
            case ENDOWMENT:
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
        switch (getAccountClass()) {
            case SHARES:
            case EMPLOYER:
            case UNITTRUST:
            case UNITISA:
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
        switch (getAccountClass()) {
            case UNITTRUST:
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
        switch (getAccountClass()) {
            case UNITISA:
            case CASHISA:
            case ISABOND:
            case TAXFREEBOND:
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
        switch (getAccountClass()) {
            case CURRENT:
            case INSTANT:
            case NOTICE:
            case BOND:
            case CASHISA:
            case ISABOND:
            case TAXFREEBOND:
            case EQUITYBOND:
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
        switch (getAccountClass()) {
            case BOND:
            case ISABOND:
            case TAXFREEBOND:
            case EQUITYBOND:
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
        switch (getAccountClass()) {
            case DEBTS:
            case CREDITCARD:
            case DEFERRED:
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
        switch (getAccountClass()) {
            case CURRENT:
            case INSTANT:
            case NOTICE:
            case CASHISA:
            case BOND:
            case ISABOND:
            case TAXFREEBOND:
            case EQUITYBOND:
            case SHARES:
            case UNITTRUST:
            case LIFEBOND:
            case UNITISA:
            case CREDITCARD:
            case ENDOWMENT:
            case DEBTS:
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
        switch (getAccountClass()) {
            case DEFERRED:
            case TAXMAN:
            case CASH:
            case WRITEOFF:
            case MARKET:
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
        switch (getAccountClass()) {
            case UNITISA:
            case UNITTRUST:
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
        switch (getAccountClass()) {
            case SHARES:
            case UNITTRUST:
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
        switch (getAccountClass()) {
            case SHARES:
            case LIFEBOND:
            case UNITTRUST:
            case UNITISA:
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
        switch (getAccountClass()) {
            case INHERITANCE:
            case OWNER:
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
        return (getAccountClass() == AccountClass.CASH);
    }

    /**
     * Determine whether the AccountType is inheritance.
     * @return <code>true</code> if the account is inheritance, <code>false</code> otherwise.
     */
    public boolean isInheritance() {
        return (getAccountClass() == AccountClass.INHERITANCE);
    }

    /**
     * Determine whether the AccountType is WriteOff.
     * @return <code>true</code> if the account is WriteOff, <code>false</code> otherwise.
     */
    public boolean isWriteOff() {
        return (getAccountClass() == AccountClass.WRITEOFF);
    }

    /**
     * Determine whether the AccountType is market.
     * @return <code>true</code> if the account is market, <code>false</code> otherwise.
     */
    public boolean isMarket() {
        return (getAccountClass() == AccountClass.MARKET);
    }

    /**
     * Determine whether the AccountType is TaxMan.
     * @return <code>true</code> if the account is TaxMan, <code>false</code> otherwise.
     */
    public boolean isTaxMan() {
        return (getAccountClass() == AccountClass.TAXMAN);
    }

    /**
     * Determine whether the AccountType is Employer.
     * @return <code>true</code> if the account is employer, <code>false</code> otherwise.
     */
    public boolean isEmployer() {
        return (getAccountClass() == AccountClass.EMPLOYER);
    }

    /**
     * Determine whether the AccountType is endowment.
     * @return <code>true</code> if the account is endowment, <code>false</code> otherwise.
     */
    public boolean isEndowment() {
        return (getAccountClass() == AccountClass.ENDOWMENT);
    }

    /**
     * Determine whether the AccountType is deferred.
     * @return <code>true</code> if the account is deferred, <code>false</code> otherwise.
     */
    public boolean isDeferred() {
        return (getAccountClass() == AccountClass.DEFERRED);
    }

    /**
     * Determine whether the AccountType is benefit.
     * @return <code>true</code> if the account is benefit, <code>false</code> otherwise.
     */
    public boolean isBenefit() {
        return (getAccountClass() == AccountClass.BENEFIT);
    }

    /**
     * Determine whether the AccountType is a Share.
     * @return <code>true</code> if the account is Share, <code>false</code> otherwise.
     */
    public boolean isShares() {
        return (getAccountClass() == AccountClass.SHARES);
    }

    /**
     * Determine whether the AccountType is a LifeBond.
     * @return <code>true</code> if the account is LifeBond, <code>false</code> otherwise.
     */
    public boolean isLifeBond() {
        return (getAccountClass() == AccountClass.LIFEBOND);
    }

    /**
     * Determine whether the AccountType is internal.
     * @return <code>true</code> if the account is internal, <code>false</code> otherwise.
     */
    public boolean isInternal() {
        return !isExternal();
    }

    /**
     * Represents a list of {@link AccountType} objects.
     */
    public static class AccountTypeList extends StaticList<AccountType, AccountClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                AccountTypeList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<AccountClass> getEnumClass() {
            return AccountClass.class;
        }

        /**
         * Construct an empty CORE account type list.
         * @param pData the DataSet for the list
         */
        public AccountTypeList(final FinanceData pData) {
            super(AccountType.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountTypeList(final AccountTypeList pSource) {
            super(pSource);
        }

        @Override
        protected AccountTypeList getEmptyList() {
            return new AccountTypeList(this);
        }

        @Override
        public AccountTypeList cloneList(final DataSet<?> pDataSet) {
            return (AccountTypeList) super.cloneList(pDataSet);
        }

        @Override
        public AccountTypeList deriveList(final ListStyle pStyle) {
            return (AccountTypeList) super.deriveList(pStyle);
        }

        @Override
        public AccountTypeList deriveDifferences(final DataList<AccountType> pOld) {
            return (AccountTypeList) super.deriveDifferences(pOld);
        }

        @Override
        public AccountType addNewItem(final DataItem pItem) {
            /* Can only clone an AccountType */
            if (!(pItem instanceof AccountType)) {
                return null;
            }

            AccountType myType = new AccountType(this, (AccountType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public AccountType addNewItem() {
            return null;
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add an AccountType to the list.
         * @param pActType the Name of the account type
         * @throws JDataException on error
         */
        public void addItem(final String pActType) throws JDataException {
            /* Create a new Account Type */
            AccountType myActType = new AccountType(this, pActType);

            /* Check that this AccountType has not been previously added */
            if (findItemByName(pActType) != null) {
                throw new JDataException(ExceptionClass.DATA, myActType, "Duplicate Account Type");
            }

            /* Check that this AccountTypeId has not been previously added */
            if (!isIdUnique(myActType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myActType, "Duplicate AccountTypeId");
            }

            /* Add the Account Type to the list */
            append(myActType);

            /* Validate the ActType */
            myActType.validate();

            /* Handle validation failure */
            if (myActType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myActType, "Failed validation");
            }
        }

        /**
         * Add an AccountType to the list.
         * @param uId the Id of the account type
         * @param isEnabled is the account type enabled
         * @param uOrder the sort order
         * @param pActType the Name of the account type
         * @param pDesc the Description of the account type
         * @throws JDataException on error
         */
        public void addOpenItem(final int uId,
                                final boolean isEnabled,
                                final int uOrder,
                                final String pActType,
                                final String pDesc) throws JDataException {
            /* Create a new Account Type */
            AccountType myActType = new AccountType(this, uId, isEnabled, uOrder, pActType, pDesc);

            /* Check that this AccountTypeId has not been previously added */
            if (!isIdUnique(myActType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myActType, "Duplicate AccountTypeId");
            }

            /* Add the Account Type to the list */
            append(myActType);

            /* Validate the AccountType */
            myActType.validate();

            /* Handle validation failure */
            if (myActType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myActType, "Failed validation");
            }
        }

        /**
         * Add an AccountType to the list.
         * @param uId the Id of the account type
         * @param uControlId the control id of the new item
         * @param isEnabled is the account type enabled
         * @param uOrder the sort order
         * @param pActType the encrypted Name of the account type
         * @param pDesc the Encrypted Description of the account type
         * @throws JDataException on error
         */
        public void addSecureItem(final int uId,
                                  final int uControlId,
                                  final boolean isEnabled,
                                  final int uOrder,
                                  final byte[] pActType,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new Account Type */
            AccountType myActType = new AccountType(this, uId, uControlId, isEnabled, uOrder, pActType, pDesc);

            /* Check that this AccountTypeId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myActType, "Duplicate AccountTypeId");
            }

            /* Add the Account Type to the list */
            append(myActType);

            /* Validate the AccountType */
            myActType.validate();

            /* Handle validation failure */
            if (myActType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myActType, "Failed validation");
            }
        }
    }
}
