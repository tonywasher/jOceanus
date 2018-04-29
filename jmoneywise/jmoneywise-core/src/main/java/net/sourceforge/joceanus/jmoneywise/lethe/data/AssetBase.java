/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Currency;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedString;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

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
    protected static final MetisFields FIELD_DEFS = new MetisFields(AssetBase.class.getSimpleName(), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final MetisField FIELD_NAME = FIELD_DEFS.declareComparisonEncryptedField(PrometheusDataResource.DATAITEM_FIELD_NAME.getValue(), MetisDataType.STRING, NAMELEN);

    /**
     * Description Field Id.
     */
    public static final MetisField FIELD_DESC = FIELD_DEFS.declareEqualityEncryptedField(PrometheusDataResource.DATAITEM_FIELD_DESC.getValue(), MetisDataType.STRING, DESCLEN);

    /**
     * isClosed Field Id.
     */
    public static final MetisField FIELD_CLOSED = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.ASSET_CLOSED.getValue(), MetisDataType.BOOLEAN);

    /**
     * CloseDate Field Id.
     */
    private static final MetisField FIELD_CLOSEDATE = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_CLOSEDATE.getValue());

    /**
     * firstEvent Field Id.
     */
    private static final MetisField FIELD_EVTFIRST = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_FIRSTEVENT.getValue());

    /**
     * lastEvent Field Id.
     */
    private static final MetisField FIELD_EVTLAST = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_LASTEVENT.getValue());

    /**
     * isRelevant Field Id.
     */
    private static final MetisField FIELD_ISRELEVANT = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_RELEVANT.getValue());

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
    private TethysDate theCloseDate;

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
        final ListStyle myBaseStyle = pAsset.getList().getStyle();
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
     * @throws OceanusException on error
     */
    protected AssetBase(final AssetBaseList<T> pList,
                        final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

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
        } catch (OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
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
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle flags */
        if (FIELD_CLOSEDATE.equals(pField)) {
            return theCloseDate != null
                                        ? theCloseDate
                                        : MetisDataFieldValue.SKIP;
        }
        if (FIELD_EVTFIRST.equals(pField)) {
            return theEarliest != null
                                       ? theEarliest
                                       : MetisDataFieldValue.SKIP;
        }
        if (FIELD_EVTLAST.equals(pField)) {
            return theLatest != null
                                     ? theLatest
                                     : MetisDataFieldValue.SKIP;
        }
        if (FIELD_ISRELEVANT.equals(pField)) {
            return isRelevant;
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public boolean includeXmlField(final MetisField pField) {
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
        final AssetCurrency myCurrency = getAssetCurrency();
        return (myCurrency == null)
                                    ? null
                                    : myCurrency.getId();
    }

    /**
     * Obtain CurrencyName.
     * @return the currencyName
     */
    public String getAssetCurrencyName() {
        final AssetCurrency myCurrency = getAssetCurrency();
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

    @Override
    public Boolean isForeign() {
        return Boolean.FALSE;
    }

    /**
     * Get the close Date of the account.
     * @return the closeDate
     */
    public TethysDate getCloseDate() {
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
    private MetisEncryptedString getNameField() {
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
    private MetisEncryptedString getDescField() {
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
    public static String getName(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    /**
     * Obtain Encrypted Name.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getNameBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    /**
     * Obtain Encrypted name field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static MetisEncryptedString getNameField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, MetisEncryptedString.class);
    }

    /**
     * Obtain Description.
     * @param pValueSet the valueSet
     * @return the description
     */
    public static String getDesc(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    /**
     * Obtain Encrypted description.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getDescBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    /**
     * Obtain Encrypted description field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static MetisEncryptedString getDescField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, MetisEncryptedString.class);
    }

    /**
     * Is the asset closed?
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isClosed(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CLOSED, Boolean.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueName(final String pValue) throws OceanusException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     */
    private void setValueName(final MetisEncryptedString pValue) {
        getValueSet().setValue(FIELD_NAME, pValue);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueDesc(final String pValue) throws OceanusException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set description value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueDesc(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set description value.
     * @param pValue the value
     */
    private void setValueDesc(final MetisEncryptedString pValue) {
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
     * @param pYear the taxYear
     * @return detailed category
     */
    public TransactionCategory getDetailedCategory(final TransactionCategory pCategory,
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
        final TethysDate myCloseDate = (theLatest == null)
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
            final Transaction myTrans = (Transaction) pSource;

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
            final AssetBase<?> myParent = getParent();
            if (myParent != null) {
                myParent.touchItem(pSource);
            }
        }

        /* If we are being touched by an asset */
        if (pSource instanceof AssetBase) {
            /* Access as assetBase */
            final AssetBase<?> myAsset = (AssetBase<?>) pSource;

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
        if (!(pThat instanceof AssetBase)) {
            return -1;
        }

        /* Access as AssetBase */
        final AssetBase<?> myThat = (AssetBase<?>) pThat;

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
        final int iDiff = MetisDataDifference.compareObject(getName(), pThat.getName());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MetisValueSet myValues = getValueSet();

        /* Adjust Closed */
        final Object myClosed = myValues.getValue(FIELD_CLOSED);
        if (myClosed == null) {
            setValueClosed(Boolean.FALSE);
        }
    }

    /**
     * Resolve update Set links.
     * @param pUpdateSet the updateSet
     * @throws OceanusException on error
     */
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* No action by default */
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
        final AssetBaseList<T> myList = getList();

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
        if (!MetisDataDifference.isEqual(getName(), pAsset.getName())) {
            setValueName(pAsset.getNameField());
        }

        /* Update the description if required */
        if (!MetisDataDifference.isEqual(getDesc(), pAsset.getDesc())) {
            setValueDesc(pAsset.getDescField());
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
    public abstract static class AssetBaseList<T extends AssetBase<T>>
            extends EncryptedList<T, MoneyWiseDataType> {
        /**
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(AssetBaseList.class);
        }

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
