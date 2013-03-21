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
 * EventCategoryType data type.
 * @author Tony Washer
 */
public class EventCategoryType
        extends StaticData<EventCategoryType, EventCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = EventCategoryType.class.getSimpleName();

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
     * Return the Category class of the Category Type.
     * @return the class
     */
    public EventCategoryClass getCategoryClass() {
        return super.getStaticClass();
    }

    @Override
    public boolean isActive() {
        return super.isActive()
               || isHiddenType();
    }

    @Override
    public EventCategoryType getBase() {
        return (EventCategoryType) super.getBase();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Category Type with
     * @param pTransType The Category Type to copy
     */
    protected EventCategoryType(final EventCategoryTypeList pList,
                                final EventCategoryType pTransType) {
        super(pList, pTransType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Category Type with
     * @param sName Name of Category Type
     * @throws JDataException on error
     */
    private EventCategoryType(final EventCategoryTypeList pList,
                              final String sName) throws JDataException {
        super(pList, sName);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the Category Type with
     * @param uId ID of Category Type
     * @param isEnabled is the EventCategoryType enabled
     * @param uOrder the sort order
     * @param pName Name of Category Type
     * @param pDesc Description of Category Type
     * @throws JDataException on error
     */
    private EventCategoryType(final EventCategoryTypeList pList,
                              final Integer uId,
                              final Boolean isEnabled,
                              final Integer uOrder,
                              final String pName,
                              final String pDesc) throws JDataException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the Category Type with
     * @param uId ID of Category Type
     * @param uControlId the control id of the new item
     * @param isEnabled is the EventCategoryType enabled
     * @param uOrder the sort order
     * @param pName Encrypted Name of Category Type
     * @param pDesc Encrypted Description of Category Type
     * @throws JDataException on error
     */
    private EventCategoryType(final EventCategoryTypeList pList,
                              final Integer uId,
                              final Integer uControlId,
                              final Boolean isEnabled,
                              final Integer uOrder,
                              final byte[] pName,
                              final byte[] pDesc) throws JDataException {
        super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Determine whether the TransactionType is a transfer.
     * @return <code>true</code> if the transaction is transfer, <code>false</code> otherwise.
     */
    public boolean isTransfer() {
        return getCategoryClass().isTransfer();
    }

    /**
     * Determine whether the TransactionType is a dividend.
     * @return <code>true</code> if the transaction is dividend, <code>false</code> otherwise.
     */
    public boolean isDividend() {
        return getCategoryClass().isDividend();
    }

    /**
     * Determine whether the TransactionType is an interest.
     * @return <code>true</code> if the transaction is interest, <code>false</code> otherwise.
     */
    public boolean isInterest() {
        return getCategoryClass().isInterest();
    }

    /**
     * Determine whether the TransactionType is a cash payment.
     * @return <code>true</code> if the transaction is cash payment, <code>false</code> otherwise.
     */
    public boolean isCashPayment() {
        return getCategoryClass().isCashPayment();
    }

    /**
     * Determine whether the TransactionType is a cash recovery.
     * @return <code>true</code> if the transaction is cash recovery, <code>false</code> otherwise.
     */
    public boolean isCashRecovery() {
        return getCategoryClass().isCashRecovery();
    }

    /**
     * Determine whether the TransactionType is a write off.
     * @return <code>true</code> if the transaction is write off, <code>false</code> otherwise.
     */
    protected boolean isWriteOff() {
        return getCategoryClass().isWriteOff();
    }

    /**
     * Determine whether the TransactionType is a inheritance.
     * @return <code>true</code> if the transaction is inheritance, <code>false</code> otherwise.
     */
    protected boolean isInherited() {
        return getCategoryClass().isInherited();
    }

    /**
     * Determine whether the TransactionType is a tax owed.
     * @return <code>true</code> if the transaction is tax owed, <code>false</code> otherwise.
     */
    protected boolean isTaxOwed() {
        return getCategoryClass().isTaxOwed();
    }

    /**
     * Determine whether the TransactionType is a tax refund.
     * @return <code>true</code> if the transaction is tax refund, <code>false</code> otherwise.
     */
    protected boolean isTaxRefund() {
        return getCategoryClass().isTaxRefund();
    }

    /**
     * Determine whether the TransactionType is a tax relief.
     * @return <code>true</code> if the transaction is tax relief, <code>false</code> otherwise.
     */
    protected boolean isTaxRelief() {
        return getCategoryClass().isTaxRelief();
    }

    /**
     * Determine whether the TransactionType is a debt interest.
     * @return <code>true</code> if the transaction is debt interest, <code>false</code> otherwise.
     */
    protected boolean isDebtInterest() {
        return getCategoryClass().isDebtInterest();
    }

    /**
     * Determine whether the TransactionType is a rental income.
     * @return <code>true</code> if the transaction is rental income, <code>false</code> otherwise.
     */
    protected boolean isRentalIncome() {
        return getCategoryClass().isRentalIncome();
    }

    /**
     * Determine whether the TransactionType is a benefit.
     * @return <code>true</code> if the transaction is benefit, <code>false</code> otherwise.
     */
    protected boolean isBenefit() {
        return getCategoryClass().isBenefit();
    }

    /**
     * Determine whether the TransactionType is a taxable gain.
     * @return <code>true</code> if the transaction is taxable gain, <code>false</code> otherwise.
     */
    public boolean isTaxableGain() {
        return getCategoryClass().isTaxableGain();
    }

    /**
     * Determine whether the TransactionType is a capital gain.
     * @return <code>true</code> if the transaction is capital gain, <code>false</code> otherwise.
     */
    public boolean isCapitalGain() {
        return getCategoryClass().isCapitalGain();
    }

    /**
     * Determine whether the TransactionType is a capital loss.
     * @return <code>true</code> if the transaction is capital loss, <code>false</code> otherwise.
     */
    public boolean isCapitalLoss() {
        return getCategoryClass().isCapitalLoss();
    }

    /**
     * Determine whether the TransactionType is a stock split.
     * @return <code>true</code> if the transaction is stock split, <code>false</code> otherwise.
     */
    public boolean isStockSplit() {
        return getCategoryClass().isStockSplit();
    }

    /**
     * Determine whether the TransactionType is an admin charge.
     * @return <code>true</code> if the transaction is admin charge, <code>false</code> otherwise.
     */
    public boolean isAdminCharge() {
        return getCategoryClass().isAdminCharge();
    }

    /**
     * Determine whether the TransactionType is a stock demerger.
     * @return <code>true</code> if the transaction is stock demerger, <code>false</code> otherwise.
     */
    public boolean isStockDemerger() {
        return getCategoryClass().isStockDemerger();
    }

    /**
     * Determine whether the TransactionType is a stock right taken.
     * @return <code>true</code> if the transaction is stock right taken, <code>false</code> otherwise.
     */
    public boolean isStockRightTaken() {
        return getCategoryClass().isStockRightTaken();
    }

    /**
     * Determine whether the TransactionType is a stock right waived.
     * @return <code>true</code> if the transaction is stock right waived, <code>false</code> otherwise.
     */
    public boolean isStockRightWaived() {
        return getCategoryClass().isStockRightWaived();
    }

    /**
     * Determine whether the TransactionType is a cash takeover.
     * @return <code>true</code> if the transaction is cash takeover, <code>false</code> otherwise.
     */
    public boolean isCashTakeover() {
        return getCategoryClass().isCashTakeover();
    }

    /**
     * Determine whether the TransactionType is a stock takeover.
     * @return <code>true</code> if the transaction is stock takeover, <code>false</code> otherwise.
     */
    public boolean isStockTakeover() {
        return getCategoryClass().isStockTakeover();
    }

    /**
     * Determine whether the TransactionType is a recovery.
     * @return <code>true</code> if the transaction is recovery, <code>false</code> otherwise.
     */
    public boolean isRecovered() {
        return getCategoryClass().isRecovered();
    }

    /**
     * Determine whether the TransactionType is hidden type.
     * @return <code>true</code> if the transaction is hidden, <code>false</code> otherwise.
     */
    public boolean isHiddenType() {
        return getCategoryClass().isHiddenType();
    }

    /**
     * Determine whether the TransactionType should have a tax credit.
     * @return <code>true</code> if the transaction should have a tax credit, <code>false</code> otherwise.
     */
    public boolean needsTaxCredit() {
        return getCategoryClass().needsTaxCredit();
    }

    /**
     * Determine whether the TransactionType is an income.
     * @return <code>true</code> if the transaction is income, <code>false</code> otherwise.
     */
    protected boolean isIncome() {
        return getCategoryClass().isIncome();
    }

    /**
     * Determine whether the TransactionType is an expense.
     * @return <code>true</code> if the transaction is expense, <code>false</code> otherwise.
     */
    protected boolean isExpense() {
        return getCategoryClass().isExpense();
    }

    /**
     * Determine whether the TransactionType is dilutable.
     * @return <code>true</code> if the transaction is dilutable, <code>false</code> otherwise.
     */
    public boolean isDilutable() {
        return getCategoryClass().isDilutable();
    }

    /**
     * Represents a list of {@link EventCategoryType} objects.
     */
    public static class EventCategoryTypeList
            extends StaticList<EventCategoryType, EventCategoryClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventCategoryTypeList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<EventCategoryClass> getEnumClass() {
            return EventCategoryClass.class;
        }

        /**
         * Construct an empty CORE category type list.
         * @param pData the DataSet for the list
         */
        public EventCategoryTypeList(final DataSet<?> pData) {
            super(EventCategoryType.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private EventCategoryTypeList(final EventCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        protected EventCategoryTypeList getEmptyList(final ListStyle pStyle) {
            EventCategoryTypeList myList = new EventCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public EventCategoryTypeList cloneList(final DataSet<?> pDataSet) {
            return (EventCategoryTypeList) super.cloneList(pDataSet);
        }

        @Override
        public EventCategoryTypeList deriveList(final ListStyle pStyle) {
            return (EventCategoryTypeList) super.deriveList(pStyle);
        }

        @Override
        public EventCategoryTypeList deriveDifferences(final DataList<EventCategoryType> pOld) {
            return (EventCategoryTypeList) super.deriveDifferences(pOld);
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public EventCategoryType addCopyItem(final DataItem pItem) {
            /* Can only clone a EventCategoryType */
            if (!(pItem instanceof EventCategoryType)) {
                return null;
            }

            EventCategoryType myType = new EventCategoryType(this, (EventCategoryType) pItem);
            add(myType);
            return myType;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public EventCategoryType addNewItem() {
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
         * Add a EventCategoryType.
         * @param pEventCategoryType the Name of the category type
         * @throws JDataException on error
         */
        public void addBasicItem(final String pEventCategoryType) throws JDataException {
            /* Create a new Category Type */
            EventCategoryType myCatType = new EventCategoryType(this, pEventCategoryType);

            /* Check that this EventCategoryTypeId has not been previously added */
            if (!isIdUnique(myCatType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myCatType, "Duplicate EventCategoryTypeId");
            }

            /* Check that this EventCategoryType has not been previously added */
            if (findItemByName(pEventCategoryType) != null) {
                throw new JDataException(ExceptionClass.DATA, myCatType, "Duplicate Category Type");
            }

            /* Add the Category Type to the list */
            append(myCatType);

            /* Validate the EventCategoryType */
            myCatType.validate();

            /* Handle validation failure */
            if (myCatType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCatType, "Failed validation");
            }
        }

        /**
         * Add a EventCategoryType to the list.
         * @param uId ID of Category Type
         * @param isEnabled is the EventCategoryType enabled
         * @param uOrder the sort order
         * @param pCatType the Name of the category type
         * @param pDesc the Description of the category type
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer uId,
                                final Boolean isEnabled,
                                final Integer uOrder,
                                final String pCatType,
                                final String pDesc) throws JDataException {
            /* Create a new Category Type */
            EventCategoryType myCatType = new EventCategoryType(this, uId, isEnabled, uOrder, pCatType, pDesc);

            /* Check that this EventCategoryTypeId has not been previously added */
            if (!isIdUnique(myCatType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myCatType, "Duplicate EventCategoryTypeId");
            }

            /* Add the Category Type to the list */
            append(myCatType);

            /* Validate the EventCategoryType */
            myCatType.validate();

            /* Handle validation failure */
            if (myCatType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCatType, "Failed validation");
            }
        }

        /**
         * Add a EventCategoryType.
         * @param uId the Id of the category type
         * @param uControlId the control id of the new item
         * @param isEnabled is the EventCategoryType enabled
         * @param uOrder the sort order
         * @param pCatType the Encrypted Name of the category type
         * @param pDesc the Encrypted Description of the category type
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final Boolean isEnabled,
                                  final Integer uOrder,
                                  final byte[] pCatType,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new Category Type */
            EventCategoryType myCatType = new EventCategoryType(this, uId, uControlId, isEnabled, uOrder, pCatType, pDesc);

            /* Check that this EventCategoryTypeId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myCatType, "Duplicate EventCategoryTypeId");
            }

            /* Add the Category Type to the list */
            append(myCatType);

            /* Validate the EventCategoryType */
            myCatType.validate();

            /* Handle validation failure */
            if (myCatType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCatType, "Failed validation");
            }
        }
    }
}
