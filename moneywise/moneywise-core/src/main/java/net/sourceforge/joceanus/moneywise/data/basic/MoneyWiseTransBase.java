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

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan.MoneyWiseLoanList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding.MoneyWiseSecurityHoldingMap;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataList.PrometheusDataListSet;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.data.PrometheusEncryptedDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusEncryptedFieldSet;
import net.sourceforge.joceanus.jprometheus.data.PrometheusEncryptedPair;
import net.sourceforge.joceanus.jprometheus.data.PrometheusEncryptedValues;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Transaction data type.
 * @author Tony Washer
 */
public abstract class MoneyWiseTransBase
        extends PrometheusEncryptedDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseTransBase.class.getSimpleName();

    /**
     * Blank character.
     */
    private static final char CHAR_BLANK = ' ';

    /**
     * Local Report fields.
     */
    private static final PrometheusEncryptedFieldSet<MoneyWiseTransBase> FIELD_DEFS = PrometheusEncryptedFieldSet.newEncryptedFieldSet(MoneyWiseTransBase.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(MoneyWiseBasicResource.TRANSACTION_ACCOUNT);
        FIELD_DEFS.declareEnumField(MoneyWiseBasicResource.TRANSACTION_DIRECTION);
        FIELD_DEFS.declareLinkField(MoneyWiseBasicResource.TRANSACTION_PARTNER);
        FIELD_DEFS.declareEncryptedMoneyField(MoneyWiseBasicResource.TRANSACTION_AMOUNT);
        FIELD_DEFS.declareLinkField(MoneyWiseBasicDataType.TRANSCATEGORY);
        FIELD_DEFS.declareBooleanField(MoneyWiseBasicResource.TRANSACTION_RECONCILED);
    }

    /**
     * Invalid Debit/Credit/Category Combination Error Text.
     */
    private static final String ERROR_COMBO = MoneyWiseBasicResource.TRANSACTION_ERROR_ASSETPAIR.getValue();

    /**
     * Zero Amount Error Text.
     */
    protected static final String ERROR_ZEROAMOUNT = MoneyWiseBasicResource.TRANSACTION_ERROR_ZERO.getValue();

    /**
     * Currency Error Text.
     */
    protected static final String ERROR_CURRENCY = MoneyWiseBasicResource.MONEYWISEDATA_ERROR_CURRENCY.getValue();

    /**
     * Copy Constructor.
     * @param pList the event list
     * @param pTrans The Transaction to copy
     */
    protected MoneyWiseTransBase(final MoneyWiseTransBaseList<?> pList,
                                 final MoneyWiseTransBase pTrans) {
        /* Set standard values */
        super(pList, pTrans);
    }

    /**
     * Edit constructor.
     * @param pList the list
     */
    protected MoneyWiseTransBase(final MoneyWiseTransBaseList<?> pList) {
        super(pList, 0);
        setNextDataKeySet();
        setValueReconciled(Boolean.FALSE);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    protected MoneyWiseTransBase(final MoneyWiseTransBaseList<?> pList,
                                 final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the AssetPair */
            Object myValue = pValues.getValue(MoneyWiseBasicResource.TRANSACTION_DIRECTION);
            if (myValue instanceof Boolean) {
                setValueDirection((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueDirection((String) myValue);
            } else if (myValue instanceof MoneyWiseAssetDirection) {
                setValueDirection((MoneyWiseAssetDirection) myValue);
            }

            /* Store the Account */
            myValue = pValues.getValue(MoneyWiseBasicResource.TRANSACTION_ACCOUNT);
            if (myValue instanceof Long) {
                setValueAccount((Long) myValue);
            } else if (myValue instanceof String) {
                setValueAccount((String) myValue);
            } else if (myValue instanceof MoneyWiseTransAsset) {
                setValueAccount((MoneyWiseTransAsset) myValue);
            }

            /* Store the Partner */
            myValue = pValues.getValue(MoneyWiseBasicResource.TRANSACTION_PARTNER);
            if (myValue instanceof Long) {
                setValuePartner((Long) myValue);
            } else if (myValue instanceof String) {
                setValuePartner((String) myValue);
            } else if (myValue instanceof MoneyWiseTransAsset) {
                setValuePartner((MoneyWiseTransAsset) myValue);
            }

            /* Store the Category */
            myValue = pValues.getValue(MoneyWiseBasicDataType.TRANSCATEGORY);
            if (myValue instanceof Integer) {
                setValueCategory((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueCategory((String) myValue);
            } else if (myValue instanceof MoneyWiseTransCategory) {
                setValueCategory((MoneyWiseTransCategory) myValue);
            }

            /* Store the Amount */
            myValue = pValues.getValue(MoneyWiseBasicResource.TRANSACTION_AMOUNT);
            if (myValue instanceof TethysMoney) {
                setValueAmount((TethysMoney) myValue);
            } else if (myValue instanceof byte[]) {
                setValueAmount((byte[]) myValue);
            } else if (myValue instanceof String) {
                final String myString = (String) myValue;
                setValueAmount(myString);
                setValueAmount(myFormatter.parseValue(myString, TethysMoney.class));
            }

            /* Store the reconciled flag */
            myValue = pValues.getValue(MoneyWiseBasicResource.TRANSACTION_RECONCILED);
            if (myValue instanceof Boolean) {
                setValueReconciled((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueReconciled(myFormatter.parseValue((String) myValue, Boolean.class));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException
                 | OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseBasicResource.TRANSACTION_DIRECTION.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicDataType.TRANSCATEGORY.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.TRANSACTION_ACCOUNT.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.TRANSACTION_PARTNER.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.TRANSACTION_AMOUNT.equals(pField)) {
            return getAmount() != null;
        }
        if (MoneyWiseBasicResource.TRANSACTION_RECONCILED.equals(pField)) {
            return isReconciled();
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        /* Handle header */
        if (isHeader()) {
            return PrometheusDataResource.DATAITEM_HEADER.getValue();
        }

        /* Access Key Values */
        final PrometheusEncryptedValues myValues = getValues();
        final Object myAccount = myValues.getValue(MoneyWiseBasicResource.TRANSACTION_ACCOUNT);
        final Object myPartner = myValues.getValue(MoneyWiseBasicResource.TRANSACTION_PARTNER);
        final Object myDir = myValues.getValue(MoneyWiseBasicResource.TRANSACTION_DIRECTION);
        final Object myCategory = myValues.getValue(MoneyWiseBasicDataType.TRANSCATEGORY);
        final Object myAmount = myValues.getValue(MoneyWiseBasicResource.TRANSACTION_AMOUNT);

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myFormatter.formatObject(myCategory));
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(myAmount == null
                ? ""
                : myFormatter.formatObject(myAmount));
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(myFormatter.formatObject(myAccount));
        if (myDir == null) {
            myBuilder.append("??");
        } else {
            myBuilder.append(Boolean.TRUE.equals(myDir)
                    ? "<-"
                    : "->");
        }
        myBuilder.append(myFormatter.formatObject(myPartner));

        /* return it */
        return myBuilder.toString();
    }

    /**
     * Obtain category.
     * @return the category
     */
    public final MoneyWiseTransCategory getCategory() {
        return getValues().getValue(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategory.class);
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        final MoneyWiseTransCategory myCategory = getCategory();
        return myCategory == null
                ? null
                : myCategory.getIndexedId();
    }

    /**
     * Obtain categoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        final MoneyWiseTransCategory myCategory = getCategory();
        return myCategory == null
                ? null
                : myCategory.getName();
    }

    /**
     * Obtain EventCategoryClass.
     * @return the eventCategoryClass
     */
    public MoneyWiseTransCategoryClass getCategoryClass() {
        final MoneyWiseTransCategory myCategory = getCategory();
        return myCategory == null
                ? null
                : myCategory.getCategoryTypeClass();
    }

    /**
     * Obtain Amount.
     * @return the amount
     */
    public TethysMoney getAmount() {
        return getValues().getValue(MoneyWiseBasicResource.TRANSACTION_AMOUNT, TethysMoney.class);
    }

    /**
     * Obtain Encrypted amount.
     * @return the bytes
     */
    public byte[] getAmountBytes() {
        return getValues().getEncryptedBytes(MoneyWiseBasicResource.TRANSACTION_AMOUNT);
    }

    /**
     * Obtain Encrypted Amount Field.
     * @return the Field
     */
    protected PrometheusEncryptedPair getAmountField() {
        return getValues().getEncryptedPair(MoneyWiseBasicResource.TRANSACTION_AMOUNT);
    }

    /**
     * Obtain Account asset.
     * @return the account
     */
    public MoneyWiseTransAsset getAccount() {
        return getValues().getValue(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, MoneyWiseTransAsset.class);
    }

    /**
     * Obtain AccountId.
     * @return the accountId
     */
    public Long getAccountId() {
        final MoneyWiseTransAsset myAccount = getAccount();
        return myAccount == null
                ? null
                : myAccount.getExternalId();
    }

    /**
     * Obtain Partner asset.
     * @return the partner
     */
    public MoneyWiseTransAsset getPartner() {
        return getValues().getValue(MoneyWiseBasicResource.TRANSACTION_PARTNER, MoneyWiseTransAsset.class);
    }

    /**
     * Obtain PartnerId.
     * @return the partnerId
     */
    public Long getPartnerId() {
        final MoneyWiseTransAsset myPartner = getPartner();
        return myPartner == null
                ? null
                : myPartner.getExternalId();
    }

    /**
     * Obtain Direction.
     * @return the direction
     */
    public MoneyWiseAssetDirection getDirection() {
        return getValues().getValue(MoneyWiseBasicResource.TRANSACTION_DIRECTION, MoneyWiseAssetDirection.class);
    }

    /**
     * Obtain Reconciled State.
     * @return the reconciled state
     */
    public Boolean isReconciled() {
        return getValues().getValue(MoneyWiseBasicResource.TRANSACTION_RECONCILED, Boolean.class);
    }

    /**
     * Set category value.
     * @param pValue the value
     */
    private void setValueCategory(final MoneyWiseTransCategory pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicDataType.TRANSCATEGORY, pValue);
    }

    /**
     * Set category id.
     * @param pId the id
     */
    private void setValueCategory(final Integer pId) {
        getValues().setUncheckedValue(MoneyWiseBasicDataType.TRANSCATEGORY, pId);
    }

    /**
     * Set category name.
     * @param pName the name
     */
    private void setValueCategory(final String pName) {
        getValues().setUncheckedValue(MoneyWiseBasicDataType.TRANSCATEGORY, pName);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueAmount(final TethysMoney pValue) throws OceanusException {
        setEncryptedValue(MoneyWiseBasicResource.TRANSACTION_AMOUNT, pValue);
    }

    /**
     * Set amount value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueAmount(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(MoneyWiseBasicResource.TRANSACTION_AMOUNT, pBytes, TethysMoney.class);
    }

    /**
     * Set amount value.
     * @param pValue the value
     */
    protected final void setValueAmount(final PrometheusEncryptedPair pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.TRANSACTION_AMOUNT, pValue);
    }

    /**
     * Set amount value.
     * @param pValue the value
     */
    private void setValueAmount(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.TRANSACTION_AMOUNT, pValue);
    }

    /**
     * Set account value.
     * @param pValue the value
     */
    protected final void setValueAccount(final MoneyWiseTransAsset pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, pValue);
    }

    /**
     * Set debit name.
     * @param pName the name
     */
    private void setValueAccount(final String pName) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, pName);
    }

    /**
     * Set debit id.
     * @param pId the value
     */
    private void setValueAccount(final Long pId) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, pId);
    }

    /**
     * Set partner value.
     * @param pValue the value
     */
    protected final void setValuePartner(final MoneyWiseTransAsset pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.TRANSACTION_PARTNER, pValue);
    }

    /**
     * Set partner id.
     * @param pId the id
     */
    private void setValuePartner(final Long pId) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.TRANSACTION_PARTNER, pId);
    }

    /**
     * Set partner name.
     * @param pName the name
     */
    private void setValuePartner(final String pName) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.TRANSACTION_PARTNER, pName);
    }

    /**
     * Set direction state.
     * @param pValue the value
     */
    protected final void setValueDirection(final MoneyWiseAssetDirection pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.TRANSACTION_DIRECTION, pValue);
    }

    /**
     * Set direction state.
     * @param pValue the value
     */
    private void setValueDirection(final Boolean pValue) {
        final MoneyWiseAssetDirection myValue = Boolean.TRUE.equals(pValue)
                ? MoneyWiseAssetDirection.FROM
                : MoneyWiseAssetDirection.TO;
        setValueDirection(myValue);
    }

    /**
     * Set direction state.
     * @param pValue the value
     */
    private void setValueDirection(final String pValue) {
        final MoneyWiseAssetDirection myValue = MoneyWiseAssetDirection.fromName(pValue);
        setValueDirection(myValue);
    }

    /**
     * Set reconciled state.
     * @param pValue the value
     */
    protected final void setValueReconciled(final Boolean pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.TRANSACTION_RECONCILED, pValue);
    }

    @Override
    public final MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseTransBaseList<?> getList() {
        return (MoneyWiseTransBaseList<?>) super.getList();
    }

    /**
     * Obtain portfolio for transaction.
     * @return the portfolio (or null)
     */
    public MoneyWisePortfolio getPortfolio() {
        /* Access account portfolio if it is a security holding */
        MoneyWiseTransAsset myAsset = getAccount();
        if (myAsset instanceof MoneyWiseSecurityHolding) {
            return ((MoneyWiseSecurityHolding) myAsset).getPortfolio();
        }

        /* Access partner portfolio if it is a security holding */
        myAsset = getPartner();
        if (myAsset instanceof MoneyWiseSecurityHolding) {
            return ((MoneyWiseSecurityHolding) myAsset).getPortfolio();
        }

        /* No portfolio */
        return null;
    }

    /**
     * Compare this event to another to establish sort order.
     * @param pThat The Event to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Check the category */
        final MoneyWiseTransBase myThat = (MoneyWiseTransBase) pThat;
        return MetisDataDifference.compareObject(getCategory(), myThat.getCategory());
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        final PrometheusEncryptedValues myValues = getValues();

        /* Adjust Reconciled */
        final Object myReconciled = myValues.getValue(MoneyWiseBasicResource.TRANSACTION_RECONCILED);
        if (myReconciled == null) {
            setValueReconciled(Boolean.FALSE);
        }

        /* Adjust Reconciled */
        final Object myDirection = myValues.getValue(MoneyWiseBasicResource.TRANSACTION_DIRECTION);
        if (myDirection == null) {
            setValueDirection(MoneyWiseAssetDirection.TO);
        }

        /* Resolve data links */
        resolveTransactionAsset(myData, this, MoneyWiseBasicResource.TRANSACTION_ACCOUNT);
        resolveTransactionAsset(myData, this, MoneyWiseBasicResource.TRANSACTION_PARTNER);
        resolveDataLink(MoneyWiseBasicDataType.TRANSCATEGORY, myData.getTransCategories());
    }

    @Override
    public boolean isLocked() {
        final MoneyWiseTransAsset myAccount = getAccount();
        final MoneyWiseTransAsset myPartner = getPartner();

        /* Check credit and debit accounts */
        return (myAccount != null && myAccount.isClosed())
                || (myPartner != null && myPartner.isClosed());
    }

    /**
     * Is this event category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final MoneyWiseTransCategoryClass pClass) {
        /* Check for match */
        return getCategoryClass().equals(pClass);
    }

    /**
     * Determines whether an event is a dividend re-investment.
     * @return dividend re-investment true/false
     */
    public boolean isDividendReInvestment() {
        /* Check for dividend re-investment */
        if (!isDividend()) {
            return false;
        }
        return getAccount() != null
                && MetisDataDifference.isEqual(getAccount(), getPartner());
    }

    /**
     * Determines whether an event is an interest payment.
     * @return interest true/false
     */
    public boolean isInterest() {
        /* Check for interest */
        final MoneyWiseTransCategoryClass myClass = getCategoryClass();
        return myClass != null
                && myClass.isInterest();
    }

    /**
     * Determines whether an event is a dividend payment.
     * @return dividend true/false
     */
    public boolean isDividend() {
        final MoneyWiseTransCategoryClass myClass = getCategoryClass();
        return myClass != null
                && myClass.isDividend();
    }

    /**
     * Determines whether an event needs a zero amount.
     * @return true/false
     */
    public boolean needsNullAmount() {
        final MoneyWiseTransCategoryClass myClass = getCategoryClass();
        return myClass != null
                && myClass.needsNullAmount();
    }

    /**
     * Determines whether we can switch direction.
     * @return true/false
     */
    public boolean canSwitchDirection() {
        return MoneyWiseTransValidator.isValidDirection(getAccount(), getCategory(), getDirection().reverse());
    }

    /**
     * Set a new account.
     * @param pAccount the account
     */
    public void setAccount(final MoneyWiseTransAsset pAccount) {
        /* Set account value */
        setValueAccount(pAccount);
    }

    /**
     * Set a new partner.
     * @param pPartner the partner
     */
    public void setPartner(final MoneyWiseTransAsset pPartner) {
        /* Set partner value */
        setValuePartner(pPartner);
    }

    /**
     * Set a direction.
     * @param pDirection the direction
     */
    public void setDirection(final MoneyWiseAssetDirection pDirection) {
        /* Set partner value */
        setValueDirection(pDirection);
    }

    /**
     * Switch direction.
     */
    public void switchDirection() {
         setValueDirection(getDirection().reverse());
    }

    /**
     * Flip assets.
     * @throws OceanusException on error
     */
    public void flipAssets() throws OceanusException {
        /* Flip details */
        final MoneyWiseTransAsset myAccount = getAccount();
        final MoneyWiseTransAsset myPartner = getPartner();
        setValueAccount(myPartner);
        setValuePartner(myAccount);
        switchDirection();
    }

    /**
     * Set a new category.
     * @param pCategory the category
     */
    public void setCategory(final MoneyWiseTransCategory pCategory) {
        setValueCategory(pCategory);
    }

    /**
     * Set a new amount.
     * @param pAmount the amount
     * @throws OceanusException on error
     */
    public void setAmount(final TethysMoney pAmount) throws OceanusException {
        setValueAmount(pAmount);
    }

    /**
     * Set a reconciled indication.
     * @param pReconciled the reconciled state
     */
    public void setReconciled(final Boolean pReconciled) {
        setValueReconciled(pReconciled);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the event category referred to */
        getCategory().touchItem(this);

        /* Touch the account and partner */
        getAccount().touchItem(this);
        getPartner().touchItem(this);
    }

    /**
     * Resolve transAsset.
     * @param pData the dataSet
     * @param pOwner the owning object
     * @param pField the fieldId
     * @throws OceanusException on error
     */
    public void resolveTransactionAsset(final PrometheusDataListSet pData,
                                        final PrometheusDataItem pOwner,
                                        final MetisDataFieldId pField) throws OceanusException {
        /* Obtain baseValue, and then resolve */
        final Object myBaseValue = pOwner.getValues().getValue(pField);
        final Object myValue = resolveTransactionAsset(pData, myBaseValue);
        if (myValue == null) {
            pOwner.addError(ERROR_UNKNOWN, pField);
            throw new PrometheusDataException(this, ERROR_RESOLUTION);
        }
        pOwner.getValues().setUncheckedValue(pField, myValue);
    }

    /**
     * Resolve transAsset.
     * @param pData the dataSet
     * @param pValue the value to convert
     * @return the asset
     * @throws OceanusException on error
     */
    private MoneyWiseTransAsset resolveTransactionAsset(final PrometheusDataListSet pData,
                                                        final Object pValue) throws OceanusException {
        /* If we are being passed a TransactionAsset, convert to Id */
        Object myValue = pValue;
        if (myValue instanceof MoneyWiseTransAsset) {
            myValue = ((MoneyWiseTransAsset) myValue).getExternalId();
        }

        /* Access the values */
        if (myValue instanceof String) {
            return resolveTransAsset(pData, (String) myValue);
        } else if (myValue instanceof Long) {
            return resolveTransAsset(pData, (Long) myValue);
        }
        return null;
    }

    /**
     * Resolve transAsset.
     * @param pData the owning dataSet
     * @param pId the item id
     * @return the asset
     * @throws OceanusException on error
     */
    private static MoneyWiseTransAsset resolveTransAsset(final PrometheusDataListSet pData,
                                                         final Long pId) throws OceanusException {
         /* Access the assetType */
        final MoneyWiseAssetType myAssetType = MoneyWiseAssetType.getAssetType(pId);

        /* If the name is a security holding */
        if (myAssetType.isSecurityHolding()) {
            final MoneyWiseSecurityHoldingMap myMap = pData.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class).getSecurityHoldingsMap();
            return myMap.findHoldingById(pId);
        } else if (myAssetType.isPayee()) {
            return pData.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class).findItemById(MoneyWiseAssetType.getBaseId(pId));
        } else if (myAssetType.isDeposit()) {
            return pData.getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class).findItemById(MoneyWiseAssetType.getBaseId(pId));
        } else if (myAssetType.isCash() || myAssetType.isAutoExpense()) {
            return pData.getDataList(MoneyWiseBasicDataType.CASH, MoneyWiseCashList.class).findItemById(MoneyWiseAssetType.getBaseId(pId));
        } else if (myAssetType.isLoan()) {
            return pData.getDataList(MoneyWiseBasicDataType.LOAN, MoneyWiseLoanList.class).findItemById(MoneyWiseAssetType.getBaseId(pId));
        } else if (myAssetType.isPortfolio()) {
            return pData.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class).findItemById(MoneyWiseAssetType.getBaseId(pId));
        } else {
            return null;
        }
    }

    /**
     * Resolve transAsset.
     * @param pData the owning dataSet
     * @param pName the item name
     * @return the asset
     */
    private static MoneyWiseTransAsset resolveTransAsset(final PrometheusDataListSet pData,
                                                         final String pName) {
        /* If the name is a security holding */
        if (pName.contains(MoneyWiseSecurityHolding.SECURITYHOLDING_SEP)) {
            final MoneyWiseSecurityHoldingMap myMap = pData.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class).getSecurityHoldingsMap();
            return myMap.findHoldingByName(pName);
        } else {
            MoneyWiseTransAsset myAsset = pData.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class).findItemByName(pName);
            if (myAsset == null) {
                myAsset = pData.getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class).findItemByName(pName);
            }
            if (myAsset == null) {
                myAsset = pData.getDataList(MoneyWiseBasicDataType.CASH, MoneyWiseCashList.class).findItemByName(pName);
            }
            if (myAsset == null) {
                myAsset = pData.getDataList(MoneyWiseBasicDataType.LOAN, MoneyWiseLoanList.class).findItemByName(pName);
            }
            if (myAsset == null) {
                myAsset = pData.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class).findItemByName(pName);
            }
            return myAsset;
        }
    }

    /**
     * Validate the event.
     */
    @Override
    public void validate() {
        final MoneyWiseTransAsset myAccount = getAccount();
        final MoneyWiseTransAsset myPartner = getPartner();
        final MoneyWiseAssetDirection myDir = getDirection();
        final TethysMoney myAmount = getAmount();
        final MoneyWiseTransCategory myCategory = getCategory();
        boolean doCheckCombo = true;

        /* Account must be non-null */
        if (myAccount == null) {
            addError(ERROR_MISSING, MoneyWiseBasicResource.TRANSACTION_ACCOUNT);
            doCheckCombo = false;

        } else {
            /* Account must be valid */
            if (!MoneyWiseTransValidator.isValidAccount(myAccount)) {
                addError(ERROR_COMBO, MoneyWiseBasicResource.TRANSACTION_ACCOUNT);
                doCheckCombo = false;
            }
        }

        /* Category must be non-null */
        if (myCategory == null) {
            addError(ERROR_MISSING, MoneyWiseBasicDataType.TRANSCATEGORY);
            doCheckCombo = false;

            /* Category must be valid for Account */
        } else if (doCheckCombo
                && !MoneyWiseTransValidator.isValidCategory(myAccount, myCategory)) {
            addError(ERROR_COMBO, MoneyWiseBasicDataType.TRANSCATEGORY);
            doCheckCombo = false;
        }

        /* Direction must be non-null */
        if (myDir == null) {
            addError(ERROR_MISSING, MoneyWiseBasicResource.TRANSACTION_DIRECTION);
            doCheckCombo = false;

            /* Direction must be valid for Account */
        } else if (doCheckCombo
                && !MoneyWiseTransValidator.isValidDirection(myAccount, myCategory, myDir)) {
            addError(ERROR_COMBO, MoneyWiseBasicResource.TRANSACTION_DIRECTION);
            doCheckCombo = false;
        }

        /* Partner must be non-null */
        if (myPartner == null) {
            addError(ERROR_MISSING, MoneyWiseBasicResource.TRANSACTION_PARTNER);

        } else {
            /* Partner must be valid for Account */
            if (doCheckCombo
                    && !MoneyWiseTransValidator.isValidPartner(myAccount, myCategory, myPartner)) {
                addError(ERROR_COMBO, MoneyWiseBasicResource.TRANSACTION_PARTNER);
            }
        }

        /* If money is null */
        if (myAmount == null) {
            /* Check that it must be null */
            if (!needsNullAmount()) {
                addError(ERROR_MISSING, MoneyWiseBasicResource.TRANSACTION_AMOUNT);
            }

            /* else non-null money */
        } else {
            /* Check that it must be null */
            if (needsNullAmount()) {
                addError(ERROR_EXIST, MoneyWiseBasicResource.TRANSACTION_AMOUNT);
            }

            /* Money must not be negative */
            if (!myAmount.isPositive()) {
                addError(ERROR_NEGATIVE, MoneyWiseBasicResource.TRANSACTION_AMOUNT);
            }

            /* Check that amount is correct currency */
            if (myAccount != null) {
                final Currency myCurrency = myAccount.getCurrency();
                if (!myAmount.getCurrency().equals(myCurrency)) {
                    addError(ERROR_CURRENCY, MoneyWiseBasicResource.TRANSACTION_AMOUNT);
                }
            }
        }
    }

    /**
     * Update base transaction from an edited transaction.
     * @param pTrans the edited transaction
     */
    protected void applyBasicChanges(final MoneyWiseTransBase pTrans) {
        /* Update the assetPair if required */
        if (!MetisDataDifference.isEqual(getDirection(), pTrans.getDirection())) {
            setValueDirection(pTrans.getDirection());
        }

        /* Update the category if required */
        if (!MetisDataDifference.isEqual(getCategory(), pTrans.getCategory())) {
            setValueCategory(pTrans.getCategory());
        }

        /* Update the account if required */
        if (!MetisDataDifference.isEqual(getAccount(), pTrans.getAccount())) {
            setValueAccount(pTrans.getAccount());
        }

        /* Update the partner if required */
        if (!MetisDataDifference.isEqual(getPartner(), pTrans.getPartner())) {
            setValuePartner(pTrans.getPartner());
        }

        /* Update the amount if required */
        if (!MetisDataDifference.isEqual(getAmount(), pTrans.getAmount())) {
            setValueAmount(pTrans.getAmountField());
        }

        /* Update the reconciled state if required */
        if (!MetisDataDifference.isEqual(isReconciled(), pTrans.isReconciled())) {
            setValueReconciled(pTrans.isReconciled());
        }
    }

    /**
     * The Event List class.
     * @param <T> the dataType
     */
    public abstract static class MoneyWiseTransBaseList<T extends MoneyWiseTransBase>
            extends PrometheusEncryptedList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(MoneyWiseTransBaseList.class);
        }

        /**
         * Construct an empty CORE Event list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected MoneyWiseTransBaseList(final MoneyWiseDataSet pData,
                                         final Class<T> pClass,
                                         final MoneyWiseBasicDataType pItemType) {
            /* Call super-constructor */
            super(pClass, pData, pItemType, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseTransBaseList(final MoneyWiseTransBaseList<T> pSource) {
            /* Call super-constructor */
            super(pSource);
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }
    }
}
