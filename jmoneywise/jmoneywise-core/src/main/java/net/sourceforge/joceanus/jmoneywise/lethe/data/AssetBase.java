/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.atlas.field.PrometheusEncryptedPair;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedValueSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResourceX;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Class representing an account that can be part of a transaction.
 */
public abstract class AssetBase
        extends EncryptedItem
        implements MetisDataNamedItem, TransactionAsset {
    /**
     * Local Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(AssetBase.class.getSimpleName(), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final MetisLetheField FIELD_NAME = FIELD_DEFS.declareComparisonEncryptedField(PrometheusDataResourceX.DATAITEM_FIELD_NAME.getValue(), MetisDataType.STRING, NAMELEN);

    /**
     * Description Field Id.
     */
    public static final MetisLetheField FIELD_DESC = FIELD_DEFS.declareEqualityEncryptedField(PrometheusDataResourceX.DATAITEM_FIELD_DESC.getValue(), MetisDataType.STRING, DESCLEN);

    /**
     * AccountCategory Field Id.
     */
    public static final MetisLetheField FIELD_CATEGORY = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataResource.CATEGORY_NAME.getValue(), MetisDataType.LINK);

    /**
     * Parent Field Id.
     */
    public static final MetisLetheField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.ASSET_PARENT.getValue(), MetisDataType.LINK);

    /**
     * Currency Field Id.
     */
    public static final MetisLetheField FIELD_CURRENCY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.CURRENCY.getItemName(), MetisDataType.LINK);

    /**
     * isClosed Field Id.
     */
    public static final MetisLetheField FIELD_CLOSED = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.ASSET_CLOSED.getValue(), MetisDataType.BOOLEAN);

    /**
     * CloseDate Field Id.
     */
    public static final MetisLetheField FIELD_CLOSEDATE = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_CLOSEDATE.getValue());

    /**
     * firstEvent Field Id.
     */
    public static final MetisLetheField FIELD_EVTFIRST = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_FIRSTEVENT.getValue());

    /**
     * lastEvent Field Id.
     */
    public static final MetisLetheField FIELD_EVTLAST = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_LASTEVENT.getValue());

    /**
     * isRelevant Field Id.
     */
    public static final MetisLetheField FIELD_ISRELEVANT = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.ASSET_RELEVANT.getValue());

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
    protected static final String ERROR_BADINFOSET = PrometheusDataResourceX.DATAINFOSET_ERROR_BADSET.getValue();

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
    protected AssetBase(final AssetBaseList<?> pList,
                        final AssetBase pAsset) {
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
    protected AssetBase(final AssetBaseList<?> pList,
                        final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

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

            /* Store the Category */
            myValue = pValues.getValue(FIELD_CATEGORY);
            if (myValue instanceof Integer) {
                setValueCategory((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueCategory((String) myValue);
            }

            /* Store the Parent */
            myValue = pValues.getValue(FIELD_PARENT);
            if (myValue instanceof Integer) {
                setValueParent((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueParent((String) myValue);
            }

            /* Store the Currency */
            myValue = pValues.getValue(FIELD_CURRENCY);
            if (myValue instanceof Integer) {
                setValueCurrency((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueCurrency((String) myValue);
            } else if (myValue instanceof AssetCurrency) {
                setValueCurrency((AssetCurrency) myValue);
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
    protected AssetBase(final AssetBaseList<?> pList) {
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
    public Object getFieldValue(final MetisLetheField pField) {
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
    public boolean includeXmlField(final MetisLetheField pField) {
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

    /**
     * Obtain Category.
     * @return the category
     */
    public abstract AssetCategory getCategory();

    @Override
    public Payee getParent() {
        return getParent(getValueSet());
    }

    /**
     * Obtain ParentId.
     * @return the parentId
     */
    public Integer getParentId() {
        final Payee myParent = getParent();
        return myParent == null
                ? null
                : myParent.getId();
    }

    /**
     * Obtain ParentName.
     * @return the parentName
     */
    public String getParentName() {
        final Payee myParent = getParent();
        return myParent == null
                ? null
                : myParent.getName();
    }

    @Override
    public AssetCurrency getAssetCurrency() {
        return getCurrency(getValueSet());
    }

    /**
     * Obtain CurrencyId.
     * @return the currencyId
     */
    public Integer getAssetCurrencyId() {
        final AssetCurrency myCurrency = getAssetCurrency();
        return myCurrency == null ? null : myCurrency.getId();
    }

    /**
     * Obtain CurrencyName.
     * @return the currencyName
     */
    public String getAssetCurrencyName() {
        final AssetCurrency myCurrency = getAssetCurrency();
        return myCurrency == null ? null : myCurrency.getName();
    }

    @Override
    public Currency getCurrency() {
        AssetCurrency myCurrency = getAssetCurrency();
        myCurrency = myCurrency == null
                                        ? getDataSet().getDefaultCurrency()
                                        : myCurrency;
        return myCurrency == null ? null : myCurrency.getCurrency();
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
        switch ((MoneyWiseDataType) getItemType()) {
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
    private PrometheusEncryptedPair getNameField() {
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
    private PrometheusEncryptedPair getDescField() {
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
    private static PrometheusEncryptedPair getNameField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, PrometheusEncryptedPair.class);
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
    private static PrometheusEncryptedPair getDescField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, PrometheusEncryptedPair.class);
    }

    /**
     * Obtain Parent.
     * @param pValueSet the valueSet
     * @return the Parent
     */
    public static Payee getParent(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, Payee.class);
    }

    /**
     * Obtain Currency.
     * @param pValueSet the valueSet
     * @return the Currency
     */
    public static AssetCurrency getCurrency(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CURRENCY, AssetCurrency.class);
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
    private void setValueName(final PrometheusEncryptedPair pValue) {
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
    private void setValueDesc(final PrometheusEncryptedPair pValue) {
        getValueSet().setValue(FIELD_DESC, pValue);
    }

    /**
     * Set category value.
     * @param pValue the value
     */
    private void setValueCategory(final AssetCategory pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set category id.
     * @param pValue the value
     */
    private void setValueCategory(final Integer pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set category name.
     * @param pValue the value
     */
    private void setValueCategory(final String pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set parent value.
     * @param pValue the value
     */
    private void setValueParent(final Payee pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set parent id.
     * @param pValue the value
     */
    private void setValueParent(final Integer pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set parent name.
     * @param pValue the value
     */
    private void setValueParent(final String pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set currency value.
     * @param pValue the value
     */
    private void setValueCurrency(final AssetCurrency pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set currency id.
     * @param pValue the value
     */
    private void setValueCurrency(final Integer pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set currency name.
     * @param pValue the value
     */
    private void setValueCurrency(final String pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
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
    public AssetBaseList<?> getList() {
        return (AssetBaseList<?>) super.getList();
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
    public void touchItem(final DataItem pSource) {
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
            final AssetBase myParent = getParent();
            if (myParent != null) {
                myParent.touchItem(pSource);
            }
        }

        /* If we are being touched by an asset */
        if (pSource instanceof AssetBase) {
            /* Access as assetBase */
            final AssetBase myAsset = (AssetBase) pSource;

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
    protected void resolveUpdateSetLinks(final UpdateSet pUpdateSet) throws OceanusException {
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
     * Set a new category.
     * @param pCategory the new category
     */
    public void setCategory(final AssetCategory pCategory) {
        setValueCategory(pCategory);
    }

    /**
     * Set a new currency.
     * @param pCurrency the new currency
     */
    public void setAssetCurrency(final AssetCurrency pCurrency) {
        setValueCurrency(pCurrency);
    }

    /**
     * Set a new parent.
     * @param pParent the parent
     */
    public void setParent(final Payee pParent) {
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
        final AssetBaseList<?> myList = getList();

        /* The name must not be too long */
        if (pName.length() > NAMELEN) {
            addError(ERROR_LENGTH, FIELD_NAME);
        }

        /* Check name count */
        if (!myList.validNameCount(pName)) {
            addError(ERROR_DUPLICATE, FIELD_NAME);
        }

        /* Check that the name does not contain invalid characters */
        if (pName.contains(SecurityHolding.SECURITYHOLDING_SEP)) {
            addError(ERROR_INVALIDCHAR, FIELD_NAME);
        }
    }

    /**
     * Update base asset from an edited asset.
     * @param pAsset the edited asset
     */
    protected void applyBasicChanges(final AssetBase pAsset) {
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
    public abstract static class AssetBaseList<T extends AssetBase>
            extends EncryptedList<T> {
        /*
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
            extends DataInstanceMap<AssetBase, String> {
        @Override
        public void adjustForItem(final DataItem pItem) {
            /* Access item */
            final AssetBase myItem = (AssetBase) pItem;

            /* Adjust name count */
            adjustForItem(myItem, myItem.getName());
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public AssetBase findAssetByName(final String pName) {
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
