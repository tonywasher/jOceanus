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
package net.sourceforge.JFinanceApp.data;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.StaticData;
import net.sourceforge.JFinanceApp.data.StaticClass.TransClass;

/**
 * TransactionType data type.
 * @author Tony Washer
 */
public class TransactionType extends StaticData<TransactionType, TransClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TransactionType.class.getSimpleName();

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
     * Return the Transaction class of the Transaction Type.
     * @return the class
     */
    public TransClass getTranClass() {
        return super.getStaticClass();
    }

    @Override
    public boolean isActive() {
        return super.isActive() || isHiddenType();
    }

    @Override
    public TransactionType getBase() {
        return (TransactionType) super.getBase();
    }

    /**
     * Construct a copy of a Transaction Type.
     * @param pList The list to associate the Transaction Type with
     * @param pTransType The Transaction Type to copy
     */
    protected TransactionType(final TransTypeList pList,
                              final TransactionType pTransType) {
        super(pList, pTransType);
    }

    /**
     * Construct a standard Transaction type on load.
     * @param pList The list to associate the Transaction Type with
     * @param sName Name of Transaction Type
     * @throws JDataException on error
     */
    private TransactionType(final TransTypeList pList,
                            final String sName) throws JDataException {
        super(pList, sName);
    }

    /**
     * Construct a standard transaction type on load.
     * @param pList The list to associate the Transaction Type with
     * @param uId ID of Transaction Type
     * @param isEnabled is the TransType enabled
     * @param uOrder the sort order
     * @param pName Name of Transaction Type
     * @param pDesc Description of Transaction Type
     * @throws JDataException on error
     */
    private TransactionType(final TransTypeList pList,
                            final int uId,
                            final boolean isEnabled,
                            final int uOrder,
                            final String pName,
                            final String pDesc) throws JDataException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Construct a standard Transaction type on load.
     * @param pList The list to associate the Transaction Type with
     * @param uId ID of Transaction Type
     * @param uControlId the control id of the new item
     * @param isEnabled is the TransType enabled
     * @param uOrder the sort order
     * @param pName Encrypted Name of Transaction Type
     * @param pDesc Encrypted Description of Transaction Type
     * @throws JDataException on error
     */
    private TransactionType(final TransTypeList pList,
                            final int uId,
                            final int uControlId,
                            final boolean isEnabled,
                            final int uOrder,
                            final byte[] pName,
                            final byte[] pDesc) throws JDataException {
        super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Determine whether the TransactionType is a transfer.
     * @return <code>true</code> if the transaction is transfer, <code>false</code> otherwise.
     */
    public boolean isTransfer() {
        return (getTranClass() == TransClass.TRANSFER);
    }

    /**
     * Determine whether the TransactionType is a dividend.
     * @return <code>true</code> if the transaction is dividend, <code>false</code> otherwise.
     */
    public boolean isDividend() {
        return (getTranClass() == TransClass.DIVIDEND);
    }

    /**
     * Determine whether the TransactionType is an interest.
     * @return <code>true</code> if the transaction is interest, <code>false</code> otherwise.
     */
    public boolean isInterest() {
        return (getTranClass() == TransClass.INTEREST);
    }

    /**
     * Determine whether the TransactionType is a cash payment.
     * @return <code>true</code> if the transaction is cash payment, <code>false</code> otherwise.
     */
    public boolean isCashPayment() {
        return (getTranClass() == TransClass.CASHPAYMENT);
    }

    /**
     * Determine whether the TransactionType is a cash recovery.
     * @return <code>true</code> if the transaction is cash recovery, <code>false</code> otherwise.
     */
    public boolean isCashRecovery() {
        return (getTranClass() == TransClass.CASHRECOVERY);
    }

    /**
     * Determine whether the TransactionType is a write off.
     * @return <code>true</code> if the transaction is write off, <code>false</code> otherwise.
     */
    protected boolean isWriteOff() {
        return (getTranClass() == TransClass.WRITEOFF);
    }

    /**
     * Determine whether the TransactionType is a inheritance.
     * @return <code>true</code> if the transaction is inheritance, <code>false</code> otherwise.
     */
    protected boolean isInherited() {
        return (getTranClass() == TransClass.INHERITED);
    }

    /**
     * Determine whether the TransactionType is a tax owed.
     * @return <code>true</code> if the transaction is tax owed, <code>false</code> otherwise.
     */
    protected boolean isTaxOwed() {
        return (getTranClass() == TransClass.TAXOWED);
    }

    /**
     * Determine whether the TransactionType is a tax refund.
     * @return <code>true</code> if the transaction is tax refund, <code>false</code> otherwise.
     */
    protected boolean isTaxRefund() {
        return (getTranClass() == TransClass.TAXREFUND);
    }

    /**
     * Determine whether the TransactionType is a tax relief.
     * @return <code>true</code> if the transaction is tax relief, <code>false</code> otherwise.
     */
    protected boolean isTaxRelief() {
        return (getTranClass() == TransClass.TAXRELIEF);
    }

    /**
     * Determine whether the TransactionType is a debt interest.
     * @return <code>true</code> if the transaction is debt interest, <code>false</code> otherwise.
     */
    protected boolean isDebtInterest() {
        return (getTranClass() == TransClass.DEBTINTEREST);
    }

    /**
     * Determine whether the TransactionType is a rental income.
     * @return <code>true</code> if the transaction is rental income, <code>false</code> otherwise.
     */
    protected boolean isRentalIncome() {
        return (getTranClass() == TransClass.RENTALINCOME);
    }

    /**
     * Determine whether the TransactionType is a benefit.
     * @return <code>true</code> if the transaction is benefit, <code>false</code> otherwise.
     */
    protected boolean isBenefit() {
        return (getTranClass() == TransClass.BENEFIT);
    }

    /**
     * Determine whether the TransactionType is a taxable gain.
     * @return <code>true</code> if the transaction is taxable gain, <code>false</code> otherwise.
     */
    public boolean isTaxableGain() {
        return (getTranClass() == TransClass.TAXABLEGAIN);
    }

    /**
     * Determine whether the TransactionType is a capital gain.
     * @return <code>true</code> if the transaction is capital gain, <code>false</code> otherwise.
     */
    public boolean isCapitalGain() {
        return (getTranClass() == TransClass.CAPITALGAIN);
    }

    /**
     * Determine whether the TransactionType is a capital loss.
     * @return <code>true</code> if the transaction is capital loss, <code>false</code> otherwise.
     */
    public boolean isCapitalLoss() {
        return (getTranClass() == TransClass.CAPITALLOSS);
    }

    /**
     * Determine whether the TransactionType is a stock split.
     * @return <code>true</code> if the transaction is stock split, <code>false</code> otherwise.
     */
    public boolean isStockSplit() {
        return (getTranClass() == TransClass.STOCKSPLIT);
    }

    /**
     * Determine whether the TransactionType is an admin charge.
     * @return <code>true</code> if the transaction is admin charge, <code>false</code> otherwise.
     */
    public boolean isAdminCharge() {
        return (getTranClass() == TransClass.ADMINCHARGE);
    }

    /**
     * Determine whether the TransactionType is a stock demerger.
     * @return <code>true</code> if the transaction is stock demerger, <code>false</code> otherwise.
     */
    public boolean isStockDemerger() {
        return (getTranClass() == TransClass.STOCKDEMERGER);
    }

    /**
     * Determine whether the TransactionType is a stock right taken.
     * @return <code>true</code> if the transaction is stock right taken, <code>false</code> otherwise.
     */
    public boolean isStockRightTaken() {
        return (getTranClass() == TransClass.STOCKRIGHTTAKEN);
    }

    /**
     * Determine whether the TransactionType is a stock right waived.
     * @return <code>true</code> if the transaction is stock right waived, <code>false</code> otherwise.
     */
    public boolean isStockRightWaived() {
        return (getTranClass() == TransClass.STOCKRIGHTWAIVED);
    }

    /**
     * Determine whether the TransactionType is a cash takeover.
     * @return <code>true</code> if the transaction is cash takeover, <code>false</code> otherwise.
     */
    public boolean isCashTakeover() {
        return (getTranClass() == TransClass.CASHTAKEOVER);
    }

    /**
     * Determine whether the TransactionType is a stock takeover.
     * @return <code>true</code> if the transaction is stock takeover, <code>false</code> otherwise.
     */
    public boolean isStockTakeover() {
        return (getTranClass() == TransClass.STOCKTAKEOVER);
    }

    /**
     * Determine whether the TransactionType is a recovery.
     * @return <code>true</code> if the transaction is recovery, <code>false</code> otherwise.
     */
    public boolean isRecovered() {
        switch (getTranClass()) {
            case RECOVERED:
            case CASHPAYMENT:
            case CASHRECOVERY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the TransactionType is hidden type.
     * @return <code>true</code> if the transaction is hidden, <code>false</code> otherwise.
     */
    public boolean isHiddenType() {
        switch (getTranClass()) {
            case UNITTRUSTDIVIDEND:
            case TAXFREEDIVIDEND:
            case TAXFREEINTEREST:
            case MARKETSHRINK:
            case MARKETGROWTH:
            case TAXCREDIT:
            case CAPITALGAIN:
            case CAPITALLOSS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the TransactionType should have a tax credit.
     * @return <code>true</code> if the transaction should have a tax credit, <code>false</code> otherwise.
     */
    public boolean needsTaxCredit() {
        switch (getTranClass()) {
            case TAXEDINCOME:
            case INTEREST:
            case DIVIDEND:
            case UNITTRUSTDIVIDEND:
            case TAXABLEGAIN:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the TransactionType is an income.
     * @return <code>true</code> if the transaction is income, <code>false</code> otherwise.
     */
    protected boolean isIncome() {
        switch (getTranClass()) {
            case TAXEDINCOME:
            case TAXFREEINCOME:
            case INTEREST:
            case DIVIDEND:
            case UNITTRUSTDIVIDEND:
            case RECOVERED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the TransactionType is an expense.
     * @return <code>true</code> if the transaction is expense, <code>false</code> otherwise.
     */
    protected boolean isExpense() {
        switch (getTranClass()) {
            case MORTGAGE:
            case ENDOWMENT:
            case EXTRATAX:
            case INSURANCE:
            case EXPENSE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the TransactionType is dilutable.
     * @return <code>true</code> if the transaction is dilutable, <code>false</code> otherwise.
     */
    public boolean isDilutable() {
        switch (getTranClass()) {
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKRIGHTWAIVED:
            case STOCKRIGHTTAKEN:
                return true;
            default:
                return false;
        }
    }

    /**
     * Represents a list of {@link TransactionType} objects.
     */
    public static class TransTypeList extends StaticList<TransTypeList, TransactionType, TransClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TransTypeList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<TransClass> getEnumClass() {
            return TransClass.class;
        }

        /**
         * Construct an empty CORE transaction type list.
         * @param pData the DataSet for the list
         */
        protected TransTypeList(final FinanceData pData) {
            super(TransTypeList.class, TransactionType.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TransTypeList(final TransTypeList pSource) {
            super(pSource);
        }

        @Override
        protected TransTypeList getEmptyList() {
            return new TransTypeList(this);
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public TransactionType addNewItem(final DataItem pItem) {
            /* Can only clone a TransactionType */
            if (!(pItem instanceof TransactionType)) {
                return null;
            }

            TransactionType myType = new TransactionType(this, (TransactionType) pItem);
            add(myType);
            return myType;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public TransactionType addNewItem() {
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
         * Add a TransactionType.
         * @param pTransType the Name of the transaction type
         * @throws JDataException on error
         */
        public void addItem(final String pTransType) throws JDataException {
            /* Create a new Transaction Type */
            TransactionType myTransType = new TransactionType(this, pTransType);

            /* Check that this TransTypeId has not been previously added */
            if (!isIdUnique(myTransType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myTransType, "Duplicate TranTypeId");
            }

            /* Check that this TransactionType has not been previously added */
            if (findItemByName(pTransType) != null) {
                throw new JDataException(ExceptionClass.DATA, myTransType, "Duplicate Transaction Type");
            }

            /* Add the Transaction Type to the list */
            add(myTransType);

            /* Validate the TransType */
            myTransType.validate();

            /* Handle validation failure */
            if (myTransType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTransType, "Failed validation");
            }
        }

        /**
         * Add a TransactionType to the list.
         * @param uId ID of Transaction Type
         * @param isEnabled is the TransType enabled
         * @param uOrder the sort order
         * @param pTranType the Name of the transaction type
         * @param pDesc the Description of the transaction type
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final boolean isEnabled,
                            final int uOrder,
                            final String pTranType,
                            final String pDesc) throws JDataException {
            /* Create a new Transaction Type */
            TransactionType myTransType = new TransactionType(this, uId, isEnabled, uOrder, pTranType, pDesc);

            /* Check that this TransTypeId has not been previously added */
            if (!isIdUnique(myTransType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myTransType, "Duplicate TranTypeId");
            }

            /* Add the Transaction Type to the list */
            add(myTransType);

            /* Validate the TransType */
            myTransType.validate();

            /* Handle validation failure */
            if (myTransType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTransType, "Failed validation");
            }
        }

        /**
         * Add a TransactionType.
         * @param uId the Id of the transaction type
         * @param uControlId the control id of the new item
         * @param isEnabled is the TransType enabled
         * @param uOrder the sort order
         * @param pTransType the Encrypted Name of the transaction type
         * @param pDesc the Encrypted Description of the transaction type
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final int uControlId,
                            final boolean isEnabled,
                            final int uOrder,
                            final byte[] pTransType,
                            final byte[] pDesc) throws JDataException {
            /* Create a new Transaction Type */
            TransactionType myTransType = new TransactionType(this, uId, uControlId, isEnabled, uOrder,
                    pTransType, pDesc);

            /* Check that this TransTypeId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myTransType, "Duplicate TransTypeId");
            }

            /* Add the Transaction Type to the list */
            addAtEnd(myTransType);

            /* Validate the TransType */
            myTransType.validate();

            /* Handle validation failure */
            if (myTransType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTransType, "Failed validation");
            }
        }
    }
}
