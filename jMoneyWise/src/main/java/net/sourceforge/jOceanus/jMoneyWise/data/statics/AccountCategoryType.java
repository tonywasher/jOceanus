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
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;

/**
 * AccountCategoryType data type.
 * @author Tony Washer
 */
public class AccountCategoryType
        extends StaticData<AccountCategoryType, AccountCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountCategoryType.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Account class of the AccountCategoryType.
     * @return the class
     */
    public AccountCategoryClass getAccountClass() {
        return super.getStaticClass();
    }

    @Override
    public AccountCategoryType getBase() {
        return (AccountCategoryType) super.getBase();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Account Category with
     * @param pAcType The Account Category to copy
     */
    protected AccountCategoryType(final AccountCategoryTypeList pList,
                                  final AccountCategoryType pAcType) {
        super(pList, pAcType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Category with
     * @param sName Name of Account Category
     * @throws JDataException on error
     */
    private AccountCategoryType(final AccountCategoryTypeList pList,
                                final String sName) throws JDataException {
        super(pList, sName);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the Account Category with
     * @param uId the id
     * @param isEnabled is the account type enabled
     * @param uOrder the sort order
     * @param pName Name of Account Category
     * @param pDesc Description of Account Category
     * @throws JDataException on error
     */
    private AccountCategoryType(final AccountCategoryTypeList pList,
                                final Integer uId,
                                final Boolean isEnabled,
                                final Integer uOrder,
                                final String pName,
                                final String pDesc) throws JDataException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the Account Category with
     * @param uId ID of Account Category
     * @param uControlId the control id of the new item
     * @param isEnabled is the account type enabled
     * @param uOrder the sort order
     * @param pName Encrypted Name of Account Category
     * @param pDesc Encrypted Description of Account Category
     * @throws JDataException on error
     */
    private AccountCategoryType(final AccountCategoryTypeList pList,
                                final Integer uId,
                                final Integer uControlId,
                                final Boolean isEnabled,
                                final Integer uOrder,
                                final byte[] pName,
                                final byte[] pDesc) throws JDataException {
        super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Determine whether the AccountCategoryType is external.
     * @return <code>true</code> if the account is external, <code>false</code> otherwise.
     */
    public boolean isExternal() {
        return getAccountClass().isExternal();
    }

    /**
     * Determine whether the AccountCategoryType is special external.
     * @return <code>true</code> if the account is special external, <code>false</code> otherwise.
     */
    public boolean isSpecial() {
        return getAccountClass().isSpecial();
    }

    /**
     * Determine whether the AccountCategoryType is priced.
     * @return <code>true</code> if the account is priced, <code>false</code> otherwise.
     */
    public boolean isPriced() {
        return getAccountClass().isPriced();
    }

    /**
     * Determine whether the AccountCategoryType is dividend provider.
     * @return <code>true</code> if the account is a dividend provider, <code>false</code> otherwise.
     */
    public boolean isDividend() {
        return getAccountClass().isDividend();
    }

    /**
     * Determine whether the AccountCategoryType is unit dividend provider.
     * @return <code>true</code> if the account is a unit dividend provider, <code>false</code> otherwise.
     */
    public boolean isUnitTrust() {
        return getAccountClass().isUnitTrust();
    }

    /**
     * Determine whether the AccountCategoryType is tax-free provider.
     * @return <code>true</code> if the account is a tax free dividend provider, <code>false</code> otherwise.
     */
    public boolean isTaxFree() {
        return getAccountClass().isTaxFree();
    }

    /**
     * Determine whether the AccountCategoryType is savings.
     * @return <code>true</code> if the account is savings, <code>false</code> otherwise.
     */
    public boolean isMoney() {
        return getAccountClass().isMoney();
    }

    /**
     * Determine whether the AccountCategoryType is a bond.
     * @return <code>true</code> if the account is a bond, <code>false</code> otherwise.
     */
    public boolean isBond() {
        return getAccountClass().isBond();
    }

    /**
     * Determine whether the AccountCategoryType is debt.
     * @return <code>true</code> if the account is debt, <code>false</code> otherwise.
     */
    public boolean isDebt() {
        return getAccountClass().isDebt();
    }

    /**
     * Determine whether the AccountCategoryType is child.
     * @return <code>true</code> if the account is child, <code>false</code> otherwise.
     */
    public boolean isChild() {
        return getAccountClass().isChild();
    }

    /**
     * Determine whether the AccountCategoryType is reserved.
     * @return <code>true</code> if the account is reserved, <code>false</code> otherwise.
     */
    public boolean isReserved() {
        return getAccountClass().isReserved();
    }

    /**
     * Determine whether the AccountCategoryType can alias.
     * @return <code>true</code> if the account can alias, <code>false</code> otherwise.
     */
    public boolean canAlias() {
        return getAccountClass().canAlias();
    }

    /**
     * Determine whether the AccountCategoryType is subject to Capital Gains.
     * @return <code>true</code> if the account is subject to Capital Gains, <code>false</code> otherwise.
     */
    public boolean isCapitalGains() {
        return getAccountClass().isCapitalGains();
    }

    /**
     * Determine whether the AccountCategoryType is Capital.
     * @return <code>true</code> if the account is Capital, <code>false</code> otherwise.
     */
    public boolean isCapital() {
        return getAccountClass().isCapital();
    }

    /**
     * Determine whether the AccountCategoryType is Owner.
     * @return <code>true</code> if the account is Owner, <code>false</code> otherwise.
     */
    public boolean isOwner() {
        return getAccountClass().isOwner();
    }

    /**
     * Determine whether the AccountCategoryType is cash.
     * @return <code>true</code> if the account is cash, <code>false</code> otherwise.
     */
    public boolean isCash() {
        return getAccountClass().isCash();
    }

    /**
     * Determine whether the AccountCategoryType is inheritance.
     * @return <code>true</code> if the account is inheritance, <code>false</code> otherwise.
     */
    public boolean isInheritance() {
        return getAccountClass().isInheritance();
    }

    /**
     * Determine whether the AccountCategoryType is WriteOff.
     * @return <code>true</code> if the account is WriteOff, <code>false</code> otherwise.
     */
    public boolean isWriteOff() {
        return getAccountClass().isWriteOff();
    }

    /**
     * Determine whether the AccountCategoryType is market.
     * @return <code>true</code> if the account is market, <code>false</code> otherwise.
     */
    public boolean isMarket() {
        return getAccountClass().isMarket();
    }

    /**
     * Determine whether the AccountCategoryType is TaxMan.
     * @return <code>true</code> if the account is TaxMan, <code>false</code> otherwise.
     */
    public boolean isTaxMan() {
        return getAccountClass().isTaxMan();
    }

    /**
     * Determine whether the AccountCategoryType is Employer.
     * @return <code>true</code> if the account is employer, <code>false</code> otherwise.
     */
    public boolean isEmployer() {
        return getAccountClass().isEmployer();
    }

    /**
     * Determine whether the AccountCategoryType is endowment.
     * @return <code>true</code> if the account is endowment, <code>false</code> otherwise.
     */
    public boolean isEndowment() {
        return getAccountClass().isEndowment();
    }

    /**
     * Determine whether the AccountCategoryType is deferred.
     * @return <code>true</code> if the account is deferred, <code>false</code> otherwise.
     */
    public boolean isDeferred() {
        return getAccountClass().isDeferred();
    }

    /**
     * Determine whether the AccountCategoryType is benefit.
     * @return <code>true</code> if the account is benefit, <code>false</code> otherwise.
     */
    public boolean isBenefit() {
        return getAccountClass().isBenefit();
    }

    /**
     * Determine whether the AccountCategoryType is a Share.
     * @return <code>true</code> if the account is Share, <code>false</code> otherwise.
     */
    public boolean isShares() {
        return getAccountClass().isShares();
    }

    /**
     * Determine whether the AccountCategoryType is a LifeBond.
     * @return <code>true</code> if the account is LifeBond, <code>false</code> otherwise.
     */
    public boolean isLifeBond() {
        return getAccountClass().isLifeBond();
    }

    /**
     * Determine whether the AccountCategoryType is internal.
     * @return <code>true</code> if the account is internal, <code>false</code> otherwise.
     */
    public boolean isInternal() {
        return !isExternal();
    }

    /**
     * Represents a list of {@link AccountCategoryType} objects.
     */
    public static class AccountCategoryTypeList
            extends StaticList<AccountCategoryType, AccountCategoryClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountCategoryTypeList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<AccountCategoryClass> getEnumClass() {
            return AccountCategoryClass.class;
        }

        /**
         * Construct an empty CORE account category list.
         * @param pData the DataSet for the list
         */
        public AccountCategoryTypeList(final DataSet<?> pData) {
            super(AccountCategoryType.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountCategoryTypeList(final AccountCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        protected AccountCategoryTypeList getEmptyList(final ListStyle pStyle) {
            AccountCategoryTypeList myList = new AccountCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AccountCategoryTypeList cloneList(final DataSet<?> pDataSet) {
            return (AccountCategoryTypeList) super.cloneList(pDataSet);
        }

        @Override
        public AccountCategoryTypeList deriveList(final ListStyle pStyle) {
            return (AccountCategoryTypeList) super.deriveList(pStyle);
        }

        @Override
        public AccountCategoryTypeList deriveDifferences(final DataList<AccountCategoryType> pOld) {
            return (AccountCategoryTypeList) super.deriveDifferences(pOld);
        }

        @Override
        public AccountCategoryType addCopyItem(final DataItem pItem) {
            /* Can only clone an AccountCategoryType */
            if (!(pItem instanceof AccountCategoryType)) {
                return null;
            }

            AccountCategoryType myType = new AccountCategoryType(this, (AccountCategoryType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public AccountCategoryType addNewItem() {
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
         * Add an AccountCategoryType to the list.
         * @param pActType the Name of the account type
         * @throws JDataException on error
         */
        public void addBasicItem(final String pActType) throws JDataException {
            /* Create a new Account Category */
            AccountCategoryType myActType = new AccountCategoryType(this, pActType);

            /* Check that this AccountCategoryType has not been previously added */
            if (findItemByName(pActType) != null) {
                throw new JDataException(ExceptionClass.DATA, myActType, "Duplicate Account Category");
            }

            /* Check that this AccountCategoryTypeId has not been previously added */
            if (!isIdUnique(myActType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myActType, "Duplicate AccountCategoryTypeId");
            }

            /* Add the Account Category to the list */
            append(myActType);

            /* Validate the ActType */
            myActType.validate();

            /* Handle validation failure */
            if (myActType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myActType, "Failed validation");
            }
        }

        /**
         * Add an AccountCategoryType to the list.
         * @param uId the Id of the account type
         * @param isEnabled is the account type enabled
         * @param uOrder the sort order
         * @param pActType the Name of the account type
         * @param pDesc the Description of the account type
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer uId,
                                final Boolean isEnabled,
                                final Integer uOrder,
                                final String pActType,
                                final String pDesc) throws JDataException {
            /* Create a new Account Category */
            AccountCategoryType myActType = new AccountCategoryType(this, uId, isEnabled, uOrder, pActType, pDesc);

            /* Check that this AccountCategoryTypeId has not been previously added */
            if (!isIdUnique(myActType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myActType, "Duplicate AccountCategoryTypeId");
            }

            /* Add the Account Category to the list */
            append(myActType);

            /* Validate the AccountCategoryType */
            myActType.validate();

            /* Handle validation failure */
            if (myActType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myActType, "Failed validation");
            }
        }

        /**
         * Add an AccountCategoryType to the list.
         * @param uId the Id of the account type
         * @param uControlId the control id of the new item
         * @param isEnabled is the account type enabled
         * @param uOrder the sort order
         * @param pActType the encrypted Name of the account type
         * @param pDesc the Encrypted Description of the account type
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final Boolean isEnabled,
                                  final Integer uOrder,
                                  final byte[] pActType,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new Account Category */
            AccountCategoryType myActType = new AccountCategoryType(this, uId, uControlId, isEnabled, uOrder, pActType, pDesc);

            /* Check that this AccountCategoryTypeId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myActType, "Duplicate AccountCategoryTypeId");
            }

            /* Add the Account Category to the list */
            append(myActType);

            /* Validate the AccountCategoryType */
            myActType.validate();

            /* Handle validation failure */
            if (myActType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myActType, "Failed validation");
            }
        }
    }
}
