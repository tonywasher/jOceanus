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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Currency;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jmetis.data.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmetis.data.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * Class representing an account that can be part of a transaction.
 * @param <T> the actual dataType
 */
public abstract class AssetBase<T extends AssetBase<T>>
        extends EncryptedItem<MoneyWiseDataType>
        implements TransactionAsset {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AssetBase.class.getSimpleName(), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAITEM_FIELD_NAME.getValue());

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAITEM_FIELD_DESC.getValue());

    /**
     * isClosed Field Id.
     */
    public static final JDataField FIELD_CLOSED = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.ASSET_CLOSED.getValue());

    /**
     * CloseDate Field Id.
     */
    private static final JDataField FIELD_CLOSEDATE = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_CLOSEDATE.getValue());

    /**
     * firstEvent Field Id.
     */
    private static final JDataField FIELD_EVTFIRST = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_FIRSTEVENT.getValue());

    /**
     * lastEvent Field Id.
     */
    private static final JDataField FIELD_EVTLAST = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_LASTEVENT.getValue());

    /**
     * isRelevant Field Id.
     */
    private static final JDataField FIELD_ISRELEVANT = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_RELEVANT.getValue());

    /**
     * Bad category error.
     */
    protected static final String ERROR_BADCATEGORY = MoneyWiseDataResource.ASSET_ERROR_BADCAT.getValue();

    /**
     * Bad parent error.
     */
    protected static final String ERROR_BADPARENT = MoneyWiseDataResource.ASSET_ERROR_BADPARENT.getValue();

    /**
     * Bad InfoSet Error Text.
     */
    protected static final String ERROR_BADINFOSET = PrometheusDataResource.DATAINFOSET_ERROR_BADSET.getValue();

    /**
     * Parent Closed Error Text.
     */
    protected static final String ERROR_PARCLOSED = MoneyWiseDataResource.ASSET_ERROR_PARENTCLOSED.getValue();

    /**
     * Reserved name error.
     */
    protected static final String ERROR_RESERVED = MoneyWiseDataResource.ASSET_ERROR_RESERVED.getValue();

    /**
     * Close Date.
     */
    private JDateDay theCloseDate;

    /**
     * Earliest Transaction.
     */
    private Transaction theEarliest;

    /**
     * Latest Transaction.
     */
    private Transaction theLatest;

    /**
     * Is this relevant?
     */
    private boolean isRelevant;

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pAsset The Asset to copy
     */
    protected AssetBase(final AssetBaseList<T> pList,
                        final AssetBase<T> pAsset) {
        /* Set standard values */
        super(pList, pAsset);

        /* If we are creating an edit copy from core */
        ListStyle myBaseStyle = pAsset.getList().getStyle();
        if ((pList.getStyle() == ListStyle.EDIT)
            && (myBaseStyle == ListStyle.CORE)) {
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
     * @throws JOceanusException on error
     */
    protected AssetBase(final AssetBaseList<T> pList,
                        final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the name */
            Object myValue = pValues.getValue(FIELD_NAME);
            if (myValue instanceof String) {
                setValueName((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueName((byte[]) myValue);
            }

            /* Store the Description */
            myValue = pValues.getValue(FIELD_DESC);
            if (myValue instanceof String) {
                setValueDesc((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueDesc((byte[]) myValue);
            }

            /* Store the closed flag */
            myValue = pValues.getValue(FIELD_CLOSED);
            if (myValue instanceof Boolean) {
                setValueClosed((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueClosed(myFormatter.parseValue((String) myValue, Boolean.class));
            }

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public AssetBase(final AssetBaseList<T> pList) {
        super(pList, 0);
        setNextDataKeySet();
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle flags */
        if (FIELD_CLOSEDATE.equals(pField)) {
            return (theCloseDate != null)
                                         ? theCloseDate
                                         : JDataFieldValue.SKIP;
        }
        if (FIELD_EVTFIRST.equals(pField)) {
            return (theEarliest != null)
                                        ? theEarliest
                                        : JDataFieldValue.SKIP;
        }
        if (FIELD_EVTLAST.equals(pField)) {
            return (theLatest != null)
                                      ? theLatest
                                      : JDataFieldValue.SKIP;
        }
        if (FIELD_ISRELEVANT.equals(pField)) {
            return isRelevant;
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_NAME.equals(pField)) {
            return true;
        }
        if (FIELD_DESC.equals(pField)) {
            return getDesc() != null;
        }
        if (FIELD_CLOSED.equals(pField)) {
            return isClosed();
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public AssetBase<?> getParent() {
        return null;
    }

    @Override
    public AssetCurrency getAssetCurrency() {
        return null;
    }

    /**
     * Obtain CurrencyId.
     * @return the currencyId
     */
    public Integer getAssetCurrencyId() {
        AssetCurrency myCurrency = getAssetCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getId();
    }

    /**
     * Obtain CurrencyName.
     * @return the currencyName
     */
    public String getAssetCurrencyName() {
        AssetCurrency myCurrency = getAssetCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getName();
    }

    @Override
    public Currency getCurrency() {
        AssetCurrency myCurrency = getAssetCurrency();
        myCurrency = myCurrency == null
                                       ? getDataSet().getDefaultCurrency()
                                       : myCurrency;
        return myCurrency == null
                                 ? null
                                 : myCurrency.getCurrency();
    }

    @Override
    public Boolean isTaxFree() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean isGross() {
        return Boolean.FALSE;
    }

    /**
     * Get the close Date of the account.
     * @return the closeDate
     */
    public JDateDay getCloseDate() {
        return theCloseDate;
    }

    /**
     * Obtain Earliest transaction.
     * @return the event
     */
    public Transaction getEarliest() {
        return theEarliest;
    }

    /**
     * Obtain Latest Transaction.
     * @return the event
     */
    public Transaction getLatest() {
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
    public AssetType getAssetType() {
        switch (getItemType()) {
            case DEPOSIT:
                return AssetType.DEPOSIT;
            case CASH:
                return AssetType.CASH;
            case LOAN:
                return AssetType.LOAN;
            case PORTFOLIO:
                return AssetType.PORTFOLIO;
            case SECURITY:
                return AssetType.SECURITY;
            case PAYEE:
                return AssetType.PAYEE;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getName() {
        return getName(getValueSet());
    }

    /**
     * Obtain Encrypted name.
     * @return the bytes
     */
    public byte[] getNameBytes() {
        return getNameBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Name Field.
     * @return the Field
     */
    private EncryptedString getNameField() {
        return getNameField(getValueSet());
    }

    /**
     * Obtain Description.
     * @return the description
     */
    public String getDesc() {
        return getDesc(getValueSet());
    }

    /**
     * Obtain Encrypted description.
     * @return the bytes
     */
    public byte[] getDescBytes() {
        return getDescBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Description Field.
     * @return the Field
     */
    private EncryptedString getDescField() {
        return getDescField(getValueSet());
    }

    @Override
    public Boolean isClosed() {
        return isClosed(getValueSet());
    }

    @Override
    public boolean isDisabled() {
        return isClosed();
    }

    /**
     * Obtain Name.
     * @param pValueSet the valueSet
     * @return the Name
     */
    public static String getName(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    /**
     * Obtain Encrypted Name.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getNameBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    /**
     * Obtain Encrypted name field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedString getNameField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, EncryptedString.class);
    }

    /**
     * Obtain Description.
     * @param pValueSet the valueSet
     * @return the description
     */
    public static String getDesc(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    /**
     * Obtain Encrypted description.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getDescBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    /**
     * Obtain Encrypted description field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedString getDescField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, EncryptedString.class);
    }

    /**
     * Is the asset closed?
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isClosed(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CLOSED, Boolean.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValueName(final String pValue) throws JOceanusException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     */
    private void setValueName(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_NAME, pValue);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValueDesc(final String pValue) throws JOceanusException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set description value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueDesc(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set description value.
     * @param pValue the value
     */
    private void setValueDesc(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_DESC, pValue);
    }

    /**
     * Set closed indication.
     * @param pValue the value
     */
    private void setValueClosed(final Boolean pValue) {
        getValueSet().setValue(FIELD_CLOSED, pValue);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AssetBaseList<T> getList() {
        return (AssetBaseList<T>) super.getList();
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
     * @return detailed category
     */
    public TransactionCategory getDetailedCategory(final TransactionCategory pCategory) {
        /* return the unchanged category */
        return pCategory;
    }

    /**
     * Adjust closed date.
     * @throws JOceanusException on error
     */
    public void adjustClosed() throws JOceanusException {
        /* Access latest activity date */
        JDateDay myCloseDate = (theLatest == null)
                                                  ? null
                                                  : theLatest.getDate();

        /* Store the close date */
        theCloseDate = myCloseDate;
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
    public void touchItem(final DataItem<MoneyWiseDataType> pSource) {
        /* If we are being touched by a transaction */
        if (pSource instanceof Transaction) {
            /* Access as transaction */
            Transaction myTrans = (Transaction) pSource;

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
            AssetBase<?> myParent = getParent();
            if (myParent != null) {
                myParent.touchItem(pSource);
            }
        }

        /* If we are being touched by an asset */
        if (pSource instanceof AssetBase) {
            /* Access as assetBase */
            AssetBase<?> myAsset = (AssetBase<?>) pSource;

            /* Mark as relevant if child is open */
            if (!myAsset.isClosed()) {
                setRelevant();
            }
        }

        /* Pass call onwards */
        super.touchItem(pSource);
    }

    @Override
    public int compareTo(final TransactionAsset pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If the Asset is not an AssetBase we are first */
        if (pThat instanceof AssetBase) {
            return -1;
        }

        /* Access as AssetBase */
        AssetBase<?> myThat = (AssetBase<?>) pThat;

        /* Check data type */
        return getItemType().ordinal() - myThat.getItemType().ordinal();
    }

    /**
     * Compare like for like assets.
     * @param pThat the target asset
     * @return -1,0,1 as this is before, equal or after that
     */
    protected int compareAsset(final T pThat) {
        /* Check the names */
        int iDiff = Difference.compareObject(getName(), pThat.getName());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        ValueSet myValues = getValueSet();

        /* Adjust Closed */
        Object myClosed = myValues.getValue(FIELD_CLOSED);
        if (myClosed == null) {
            setValueClosed(Boolean.FALSE);
        }
    }

    /**
     * Resolve update Set links.
     * @param pUpdateSet the updateSet
     * @throws JOceanusException on error
     */
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* No action by default */
    }

    /**
     * Set a new name.
     * @param pName the new name
     * @throws JOceanusException on error
     */
    public void setName(final String pName) throws JOceanusException {
        setValueName(pName);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws JOceanusException on error
     */
    public void setDescription(final String pDesc) throws JOceanusException {
        setValueDesc(pDesc);
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
        String myName = getName();
        String myDesc = getDesc();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, FIELD_NAME);

            /* Check that the name is unique */
        } else {
            /* Validate the name */
            validateName(myName);
        }

        /* Check description length */
        if ((myDesc != null) && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_DESC);
        }
    }

    /**
     * Validate the name.
     * @param pName the name
     */
    protected void validateName(final String pName) {
        /* Access the list */
        AssetBaseList<T> myList = getList();

        /* The name must not be too long */
        if (pName.length() > NAMELEN) {
            addError(ERROR_LENGTH, FIELD_NAME);
        }

        /* Check name count */
        if (!myList.validNameCount(pName)) {
            addError(ERROR_DUPLICATE, FIELD_NAME);
        }
    }

    /**
     * Update base asset from an edited asset.
     * @param pAsset the edited asset
     */
    protected void applyBasicChanges(final AssetBase<T> pAsset) {
        /* Update the name if required */
        if (!Difference.isEqual(getName(), pAsset.getName())) {
            setValueName(pAsset.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), pAsset.getDesc())) {
            setValueDesc(pAsset.getDescField());
        }

        /* Update the closed indication if required */
        if (!Difference.isEqual(isClosed(), pAsset.isClosed())) {
            setValueClosed(pAsset.isClosed());
        }
    }

    /**
     * The Asset List class.
     * @param <T> the dataType
     */
    public abstract static class AssetBaseList<T extends AssetBase<T>>
            extends EncryptedList<T, MoneyWiseDataType> {
        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected AssetBaseList(final MoneyWiseData pData,
                                final Class<T> pClass,
                                final MoneyWiseDataType pItemType) {
            super(pClass, pData, pItemType, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected AssetBaseList(final AssetBaseList<T> pSource) {
            super(pSource);
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        public abstract T findItemByName(final String pName);

        /**
         * Check whether a name is available for use.
         * @param pName Name of item
         * @return true/false
         */
        protected abstract boolean checkAvailableName(final String pName);

        /**
         * Check whether a name is validly used.
         * @param pName Name of item
         * @return true/false
         */
        protected abstract boolean validNameCount(final String pName);

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
        public void postProcessOnLoad() throws JOceanusException {
            /* Resolve links and sort the data */
            resolveDataSetLinks();
            reSort();

            /* Map the data */
            mapData();
        }
    }

    /**
     * The dataMap class.
     */
    protected static class AssetDataMap
            extends DataInstanceMap<AssetBase<?>, MoneyWiseDataType, String> {
        @Override
        public void adjustForItem(final AssetBase<?> pItem) {
            /* Adjust name count */
            adjustForItem(pItem, pItem.getName());
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public AssetBase<?> findAssetByName(final String pName) {
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
