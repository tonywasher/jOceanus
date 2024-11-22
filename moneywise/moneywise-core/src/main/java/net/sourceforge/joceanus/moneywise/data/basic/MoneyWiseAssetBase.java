/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.data.basic;

import java.util.Currency;
import java.util.Iterator;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInstanceMap;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList.PrometheusListStyle;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedFieldSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedPair;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedValues;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * Class representing an account that can be part of a transaction.
 */
public abstract class MoneyWiseAssetBase
        extends PrometheusEncryptedDataItem
        implements MoneyWiseTransAsset {
    /**
     * Report fields.
     */
    private static final PrometheusEncryptedFieldSet<MoneyWiseAssetBase> FIELD_DEFS = PrometheusEncryptedFieldSet.newEncryptedFieldSet(MoneyWiseAssetBase.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareEncryptedStringField(PrometheusDataResource.DATAITEM_FIELD_NAME, NAMELEN);
        FIELD_DEFS.declareEncryptedStringField(PrometheusDataResource.DATAITEM_FIELD_DESC, DESCLEN);
        FIELD_DEFS.declareLinkField(MoneyWiseBasicResource.CATEGORY_NAME);
        FIELD_DEFS.declareLinkField(MoneyWiseBasicResource.ASSET_PARENT);
        FIELD_DEFS.declareLinkField(MoneyWiseStaticDataType.CURRENCY);
        FIELD_DEFS.declareBooleanField(MoneyWiseBasicResource.ASSET_CLOSED);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.ASSET_CLOSEDATE, MoneyWiseAssetBase::getCloseDate);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.ASSET_FIRSTEVENT, MoneyWiseAssetBase::getEarliest);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.ASSET_LASTEVENT, MoneyWiseAssetBase::getLatest);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.ASSET_RELEVANT, MoneyWiseAssetBase::isRelevant);
    }

    /**
     * Bad category error.
     */
    protected static final String ERROR_BADCATEGORY = MoneyWiseBasicResource.ASSET_ERROR_BADCAT.getValue();

    /**
     * Bad parent error.
     */
    protected static final String ERROR_BADPARENT = MoneyWiseBasicResource.ASSET_ERROR_BADPARENT.getValue();

    /**
     * Bad InfoSet Error Text.
     */
    protected static final String ERROR_BADINFOSET = PrometheusDataResource.DATAINFOSET_ERROR_BADSET.getValue();

    /**
     * Parent Closed Error Text.
     */
    protected static final String ERROR_PARCLOSED = MoneyWiseBasicResource.ASSET_ERROR_PARENTCLOSED.getValue();

    /**
     * Reserved name error.
     */
    protected static final String ERROR_RESERVED = MoneyWiseBasicResource.ASSET_ERROR_RESERVED.getValue();

    /**
     * Close Date.
     */
    private OceanusDate theCloseDate;

    /**
     * Earliest Transaction.
     */
    private MoneyWiseTransaction theEarliest;

    /**
     * Latest Transaction.
     */
    private MoneyWiseTransaction theLatest;

    /**
     * Is this relevant?
     */
    private boolean isRelevant;

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pAsset The Asset to copy
     */
    protected MoneyWiseAssetBase(final MoneyWiseAssetBaseList<?> pList,
                                 final MoneyWiseAssetBase pAsset) {
        /* Set standard values */
        super(pList, pAsset);

        /* If we are creating an edit copy from core */
        final PrometheusListStyle myBaseStyle = pAsset.getList().getStyle();
        if (pList.getStyle() == PrometheusListStyle.EDIT
                && myBaseStyle == PrometheusListStyle.CORE) {
            /* Update underlying flags */
            theCloseDate = pAsset.getCloseDate();
            theEarliest = pAsset.getEarliest();
            theLatest = pAsset.getLatest();
            isRelevant = pAsset.isRelevant();
        }
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    protected MoneyWiseAssetBase(final MoneyWiseAssetBaseList<?> pList,
                                 final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the name */
            Object myValue = pValues.getValue(PrometheusDataResource.DATAITEM_FIELD_NAME);
            if (myValue instanceof String) {
                setValueName((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueName((byte[]) myValue);
            }

            /* Store the Description */
            myValue = pValues.getValue(PrometheusDataResource.DATAITEM_FIELD_DESC);
            if (myValue instanceof String) {
                setValueDesc((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueDesc((byte[]) myValue);
            }

            /* Store the Category */
            myValue = pValues.getValue(MoneyWiseBasicResource.CATEGORY_NAME);
            if (myValue instanceof Integer) {
                setValueCategory((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueCategory((String) myValue);
            }

            /* Store the Parent */
            myValue = pValues.getValue(MoneyWiseBasicResource.ASSET_PARENT);
            if (myValue instanceof Integer) {
                setValueParent((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueParent((String) myValue);
            }

            /* Store the Currency */
            myValue = pValues.getValue(MoneyWiseStaticDataType.CURRENCY);
            if (myValue instanceof Integer) {
                setValueCurrency((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueCurrency((String) myValue);
            } else if (myValue instanceof MoneyWiseCurrency) {
                setValueCurrency((MoneyWiseCurrency) myValue);
            }

            /* Store the closed flag */
            myValue = pValues.getValue(MoneyWiseBasicResource.ASSET_CLOSED);
            if (myValue instanceof Boolean) {
                setValueClosed((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueClosed(myFormatter.parseValue((String) myValue, Boolean.class));
            }

            /* Catch Exceptions */
        } catch (OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    protected MoneyWiseAssetBase(final MoneyWiseAssetBaseList<?> pList) {
        super(pList, 0);
        setNextDataKeySet();
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(pField)) {
            return true;
        }
        if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(pField)) {
            return getDesc() != null;
        }
        if (MoneyWiseBasicResource.ASSET_CLOSED.equals(pField)) {
            return isClosed();
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain Category.
     * @return the category
     */
    public abstract MoneyWiseAssetCategory getCategory();

    @Override
    public MoneyWisePayee getParent() {
        return getValues().getValue(MoneyWiseBasicResource.ASSET_PARENT, MoneyWisePayee.class);
    }

    /**
     * Obtain ParentId.
     * @return the parentId
     */
    public Integer getParentId() {
        final MoneyWisePayee myParent = getParent();
        return myParent == null
                ? null
                : myParent.getIndexedId();
    }

    /**
     * Obtain ParentName.
     * @return the parentName
     */
    public String getParentName() {
        final MoneyWisePayee myParent = getParent();
        return myParent == null
                ? null
                : myParent.getName();
    }

    @Override
    public MoneyWiseCurrency getAssetCurrency() {
        return getValues().getValue(MoneyWiseStaticDataType.CURRENCY, MoneyWiseCurrency.class);
     }

    /**
     * Obtain CurrencyId.
     * @return the currencyId
     */
    public Integer getAssetCurrencyId() {
        final MoneyWiseCurrency myCurrency = getAssetCurrency();
        return myCurrency == null ? null : myCurrency.getIndexedId();
    }

    /**
     * Obtain CurrencyName.
     * @return the currencyName
     */
    public String getAssetCurrencyName() {
        final MoneyWiseCurrency myCurrency = getAssetCurrency();
        return myCurrency == null ? null : myCurrency.getName();
    }

    @Override
    public Currency getCurrency() {
        MoneyWiseCurrency myCurrency = getAssetCurrency();
        myCurrency = myCurrency == null
                ? getDataSet().getReportingCurrency()
                : myCurrency;
        return myCurrency == null ? null : myCurrency.getCurrency();
    }

    /**
     * Obtain Opening Balance.
     * @return the Opening balance
     */
    public OceanusMoney getOpeningBalance() {
        return null;
    }

    @Override
    public boolean isTaxFree() {
        return false;
    }

    @Override
    public boolean isGross() {
        return false;
    }

    @Override
    public boolean isForeign() {
        return false;
    }

    /**
     * Get the close Date of the account.
     * @return the closeDate
     */
    public OceanusDate getCloseDate() {
        return theCloseDate;
    }

    /**
     * Obtain Earliest transaction.
     * @return the event
     */
    public MoneyWiseTransaction getEarliest() {
        return theEarliest;
    }

    /**
     * Obtain Latest Transaction.
     * @return the event
     */
    public MoneyWiseTransaction getLatest() {
        return theLatest;
    }

    /**
     * Is the account relevant (i.e. non-closeable)?
     * @return true/false
     */
    public boolean isRelevant() {
        return isRelevant;
    }

    @Override
    public boolean isAutoExpense() {
        return false;
    }

    @Override
    public boolean isShares() {
        return false;
    }

    @Override
    public boolean isCapital() {
        return false;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public MoneyWiseAssetType getAssetType() {
        switch ((MoneyWiseBasicDataType) getItemType()) {
            case DEPOSIT:
                return MoneyWiseAssetType.DEPOSIT;
            case CASH:
                return MoneyWiseAssetType.CASH;
            case LOAN:
                return MoneyWiseAssetType.LOAN;
            case PORTFOLIO:
                return MoneyWiseAssetType.PORTFOLIO;
            case SECURITY:
                return MoneyWiseAssetType.SECURITY;
            case PAYEE:
                return MoneyWiseAssetType.PAYEE;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getName() {
        return getValues().getValue(PrometheusDataResource.DATAITEM_FIELD_NAME, String.class);
    }

    /**
     * Obtain Encrypted name.
     * @return the bytes
     */
    public byte[] getNameBytes() {
        return getValues().getEncryptedBytes(PrometheusDataResource.DATAITEM_FIELD_NAME);
    }

    /**
     * Obtain Encrypted Name Field.
     * @return the Field
     */
    private PrometheusEncryptedPair getNameField() {
        return getValues().getEncryptedPair(PrometheusDataResource.DATAITEM_FIELD_NAME);
    }

    /**
     * Obtain Description.
     * @return the description
     */
    public String getDesc() {
        return getValues().getValue(PrometheusDataResource.DATAITEM_FIELD_DESC, String.class);
    }

    /**
     * Obtain Encrypted description.
     * @return the bytes
     */
    public byte[] getDescBytes() {
        return getValues().getEncryptedBytes(PrometheusDataResource.DATAITEM_FIELD_DESC);
    }

    /**
     * Obtain Encrypted Description Field.
     * @return the Field
     */
    private PrometheusEncryptedPair getDescField() {
        return getValues().getEncryptedPair(PrometheusDataResource.DATAITEM_FIELD_DESC);
    }

    @Override
    public Boolean isClosed() {
        return getValues().getValue(MoneyWiseBasicResource.ASSET_CLOSED, Boolean.class);
    }

    @Override
    public boolean isDisabled() {
        return isClosed();
    }

    /**
     * Set name value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueName(final String pValue) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     */
    private void setValueName(final PrometheusEncryptedPair pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAITEM_FIELD_NAME, pValue);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueDesc(final String pValue) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_DESC, pValue);
    }

    /**
     * Set description value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueDesc(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set description value.
     * @param pValue the value
     */
    private void setValueDesc(final PrometheusEncryptedPair pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAITEM_FIELD_DESC, pValue);
    }

    /**
     * Set category value.
     * @param pValue the value
     */
    private void setValueCategory(final MoneyWiseAssetCategory pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.CATEGORY_NAME, pValue);
    }

    /**
     * Set category id.
     * @param pValue the value
     */
    private void setValueCategory(final Integer pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.CATEGORY_NAME, pValue);
    }

    /**
     * Set category name.
     * @param pValue the value
     */
    private void setValueCategory(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.CATEGORY_NAME, pValue);
    }

    /**
     * Set parent value.
     * @param pValue the value
     */
    private void setValueParent(final MoneyWisePayee pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.ASSET_PARENT, pValue);
    }

    /**
     * Set parent id.
     * @param pValue the value
     */
    private void setValueParent(final Integer pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.ASSET_PARENT, pValue);
    }

    /**
     * Set parent name.
     * @param pValue the value
     */
    private void setValueParent(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.ASSET_PARENT, pValue);
    }

    /**
     * Set currency value.
     * @param pValue the value
     */
    private void setValueCurrency(final MoneyWiseCurrency pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.CURRENCY, pValue);
    }

    /**
     * Set currency id.
     * @param pValue the value
     */
    private void setValueCurrency(final Integer pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.CURRENCY, pValue);
    }

    /**
     * Set currency name.
     * @param pValue the value
     */
    private void setValueCurrency(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.CURRENCY, pValue);
    }

    /**
     * Set closed indication.
     * @param pValue the value
     */
    private void setValueClosed(final Boolean pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.ASSET_CLOSED, pValue);
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseAssetBaseList<?> getList() {
        return (MoneyWiseAssetBaseList<?>) super.getList();
    }

    @Override
    public boolean isLocked() {
        return isClosed();
    }

    /**
     * Set relevant.
     */
    public void setRelevant() {
        isRelevant = true;
    }

    /**
     * Obtain detailed category.
     * @param pCategory current category
     * @param pYear the taxYear
     * @return detailed category
     */
    public MoneyWiseTransCategory getDetailedCategory(final MoneyWiseTransCategory pCategory,
                                                      final MoneyWiseTaxCredit pYear) {
        /* return the unchanged category */
        return pCategory;
    }

    /**
     * Adjust closed date.
     * @throws OceanusException on error
     */
    public void adjustClosed() throws OceanusException {
        /* Access latest activity date */
        theCloseDate = theLatest == null ? null : theLatest.getDate();
    }

    @Override
    public void clearActive() {
        /* Reset flags */
        theCloseDate = null;
        theEarliest = null;
        theLatest = null;
        isRelevant = false;

        /* Pass call onwards */
        super.clearActive();
    }

    @Override
    public void touchItem(final PrometheusDataItem pSource) {
        /* If we are being touched by a transaction */
        if (pSource instanceof MoneyWiseTransaction) {
            /* Access as transaction */
            final MoneyWiseTransaction myTrans = (MoneyWiseTransaction) pSource;

            /* Record the transaction */
            if (theEarliest == null) {
                theEarliest = myTrans;
            }
            theLatest = myTrans;

            /* if this transaction is not reconciled */
            if (!myTrans.isReconciled()) {
                /* Mark account as relevant */
                setRelevant();
            }

            /* Touch parent if it exists */
            final MoneyWiseAssetBase myParent = getParent();
            if (myParent != null) {
                myParent.touchItem(pSource);
            }
        }

        /* If we are being touched by an asset */
        if (pSource instanceof MoneyWiseAssetBase) {
            /* Access as assetBase */
            final MoneyWiseAssetBase myAsset = (MoneyWiseAssetBase) pSource;

            /* Mark as relevant if child is open */
            if (!myAsset.isClosed()) {
                setRelevant();
            }
        }

        /* Pass call onwards */
        super.touchItem(pSource);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final PrometheusEncryptedValues myValues = getValues();

        /* Adjust Closed */
        final Object myClosed = myValues.getValue(MoneyWiseBasicResource.ASSET_CLOSED);
        if (myClosed == null) {
            setValueClosed(Boolean.FALSE);
        }
    }

    /**
     * resolve EditSet links.
     * @throws OceanusException on error
     */
    protected abstract void resolveEditSetLinks() throws OceanusException;

    /**
     * Resolve late edit Set links.
     * @throws OceanusException on error
     */
    public void resolveLateEditSetLinks() throws OceanusException {
        /* Access the editSet */
        final PrometheusEditSet myEditSet = getList().getEditSet();

        /* Resolve First/LastEvent if required */
        final MoneyWiseTransactionList myTransList = myEditSet.getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class);
        if (theEarliest != null) {
            theEarliest = myTransList.findItemById(theEarliest.getIndexedId());
        }
        if (theLatest != null) {
            theLatest = myTransList.findItemById(theLatest.getIndexedId());
        }
    }

    /**
     * Set a new name.
     * @param pName the new name
     * @throws OceanusException on error
     */
    public void setName(final String pName) throws OceanusException {
        setValueName(pName);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws OceanusException on error
     */
    public void setDescription(final String pDesc) throws OceanusException {
        setValueDesc(pDesc);
    }

    /**
     * Set a new category.
     * @param pCategory the new category
     */
    public void setCategory(final MoneyWiseAssetCategory pCategory) {
        setValueCategory(pCategory);
    }

    /**
     * Set a new currency.
     * @param pCurrency the new currency
     */
    public void setAssetCurrency(final MoneyWiseCurrency pCurrency) {
        setValueCurrency(pCurrency);
    }

    /**
     * Set a new parent.
     * @param pParent the parent
     */
    public void setParent(final MoneyWisePayee pParent) {
        setValueParent(pParent);
    }

    /**
     * Set a new closed indication.
     * @param isClosed the new closed indication
     */
    public void setClosed(final Boolean isClosed) {
        setValueClosed(isClosed);
    }

    @Override
    public void validate() {
        final String myName = getName();
        final String myDesc = getDesc();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, PrometheusDataResource.DATAITEM_FIELD_NAME);

            /* Check that the name is unique */
        } else {
            /* Validate the name */
            validateName(myName);
        }

        /* Check description length */
        if ((myDesc != null) && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_DESC);
        }
    }

    /**
     * Validate the name.
     * @param pName the name
     */
    protected void validateName(final String pName) {
        /* Access the list */
        final MoneyWiseAssetBaseList<?> myList = getList();

        /* The name must not be too long */
        if (pName.length() > NAMELEN) {
            addError(ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_NAME);
        }

        /* Check name count */
        if (!myList.validNameCount(pName)) {
            addError(ERROR_DUPLICATE, PrometheusDataResource.DATAITEM_FIELD_NAME);
        }

        /* Check that the name does not contain invalid characters */
        if (pName.contains(MoneyWiseSecurityHolding.SECURITYHOLDING_SEP)) {
            addError(ERROR_INVALIDCHAR, PrometheusDataResource.DATAITEM_FIELD_NAME);
        }
    }

    /**
     * Update base asset from an edited asset.
     * @param pAsset the edited asset
     */
    protected void applyBasicChanges(final MoneyWiseAssetBase pAsset) {
        /* Update the name if required */
        if (!MetisDataDifference.isEqual(getName(), pAsset.getName())) {
            setValueName(pAsset.getNameField());
        }

        /* Update the description if required */
        if (!MetisDataDifference.isEqual(getDesc(), pAsset.getDesc())) {
            setValueDesc(pAsset.getDescField());
        }

        /* Update the category if required */
        if (!MetisDataDifference.isEqual(getCategory(), pAsset.getCategory())) {
            setValueCategory(pAsset.getCategory());
        }

        /* Update the parent if required */
        if (!MetisDataDifference.isEqual(getParent(), pAsset.getParent())) {
            setValueParent(pAsset.getParent());
        }

        /* Update the currency if required */
        if (!MetisDataDifference.isEqual(getAssetCurrency(), pAsset.getAssetCurrency())) {
            setValueCurrency(pAsset.getAssetCurrency());
        }

        /* Update the closed indication if required */
        if (!MetisDataDifference.isEqual(isClosed(), pAsset.isClosed())) {
            setValueClosed(pAsset.isClosed());
        }
    }

    /**
     * The Asset List class.
     * @param <T> the dataType
     */
    public abstract static class MoneyWiseAssetBaseList<T extends MoneyWiseAssetBase>
            extends PrometheusEncryptedList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(MoneyWiseAssetBaseList.class);
        }

        /**
         * The EditSet.
         */
        private PrometheusEditSet theEditSet;

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected MoneyWiseAssetBaseList(final MoneyWiseDataSet pData,
                                         final Class<T> pClass,
                                         final MoneyWiseBasicDataType pItemType) {
            super(pClass, pData, pItemType, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseAssetBaseList(final MoneyWiseAssetBaseList<T> pSource) {
            super(pSource);
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        /**
         * Obtain editSet.
         * @return the editSet
         */
        public PrometheusEditSet getEditSet() {
            return theEditSet;
        }

        /**
         * Set editSet.
         * @param pEditSet the editSet
         */
        protected void setEditSet(final PrometheusEditSet pEditSet) {
            theEditSet = pEditSet;
        }

        @Override
        public abstract T findItemByName(String pName);

        /**
         * Check whether a name is available for use.
         * @param pName Name of item
         * @return true/false
         */
        protected abstract boolean checkAvailableName(String pName);

        /**
         * Check whether a name is validly used.
         * @param pName Name of item
         * @return true/false
         */
        protected abstract boolean validNameCount(String pName);

        /**
         * Obtain unique name for new account.
         * @param pBase the base name
         * @return The new name
         */
        public String getUniqueName(final String pBase) {
            /* Set up base constraints */
            int iNextId = 1;

            /* Loop until we found a name */
            String myName = pBase;
            for (;;) {
                /* try out the name */
                if (checkAvailableName(myName)) {
                    return myName;
                }

                /* Build next name */
                myName = pBase.concat(Integer.toString(iNextId++));
            }
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Resolve links and sort the data */
            super.resolveDataSetLinks();
            reSort();

            /* Map the data */
            mapData();
        }

        /**
         * Resolve late edit Set links.
         * @throws OceanusException on error
         */
        public void resolveLateEditSetLinks() throws OceanusException {
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                final T myItem = myIterator.next();
                myItem.resolveLateEditSetLinks();
            }
        }
    }

    /**
     * The dataMap class.
     */
    protected static class MoneyWiseAssetDataMap
            extends PrometheusDataInstanceMap<MoneyWiseAssetBase, String> {
        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Access item */
            final MoneyWiseAssetBase myItem = (MoneyWiseAssetBase) pItem;

            /* Adjust name count */
            adjustForItem(myItem, myItem.getName());
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public MoneyWiseAssetBase findAssetByName(final String pName) {
            return findItemByKey(pName);
        }

        /**
         * Check validity of name.
         * @param pName the name to look up
         * @return true/false
         */
        public boolean validNameCount(final String pName) {
            return validKeyCount(pName);
        }
    }
}
