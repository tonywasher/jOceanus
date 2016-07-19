/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisEncryptedData.MetisEncryptedMoney;
import net.sourceforge.joceanus.jmetis.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetPairManager;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Transaction data type.
 * @author Tony Washer
 * @param <T> the transaction data type
 */
public abstract class TransactionBase<T extends TransactionBase<T>>
        extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<T> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TransactionBase.class.getSimpleName();

    /**
     * Blank character.
     */
    private static final char CHAR_BLANK = ' ';

    /**
     * Local Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    /**
     * AssetPair Field Id.
     */
    public static final MetisField FIELD_PAIR = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_ASSETPAIR.getValue());

    /**
     * Account Field Id.
     */
    public static final MetisField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_ACCOUNT.getValue());

    /**
     * Partner Field Id.
     */
    public static final MetisField FIELD_PARTNER = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_PARTNER.getValue());

    /**
     * Direction Field Id.
     */
    public static final MetisField FIELD_DIRECTION = FIELD_DEFS.declareDerivedValueField(MoneyWiseDataResource.TRANSACTION_DIRECTION.getValue());

    /**
     * Amount Field Id.
     */
    public static final MetisField FIELD_AMOUNT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_AMOUNT.getValue());

    /**
     * Category Field Id.
     */
    public static final MetisField FIELD_CATEGORY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.TRANSCATEGORY.getItemName());

    /**
     * Reconciled Field Id.
     */
    public static final MetisField FIELD_RECONCILED = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_RECONCILED.getValue());

    /**
     * Invalid Debit/Credit/Category Combination Error Text.
     */
    private static final String ERROR_COMBO = MoneyWiseDataResource.TRANSACTION_ERROR_ASSETPAIR.getValue();

    /**
     * Zero Amount Error Text.
     */
    private static final String ERROR_ZEROAMOUNT = MoneyWiseDataResource.TRANSACTION_ERROR_ZERO.getValue();

    /**
     * Currency Error Text.
     */
    protected static final String ERROR_CURRENCY = MoneyWiseDataResource.MONEYWISEDATA_ERROR_CURRENCY.getValue();

    /**
     * Copy Constructor.
     * @param pList the event list
     * @param pTrans The Transaction to copy
     */
    protected TransactionBase(final TransactionBaseList<T> pList,
                              final T pTrans) {
        /* Set standard values */
        super(pList, pTrans);
    }

    /**
     * Edit constructor.
     * @param pList the list
     */
    protected TransactionBase(final TransactionBaseList<T> pList) {
        super(pList, 0);
        setNextDataKeySet();
        setValueAssetPair(getAssetPairManager().getDefaultPair());
        setValueReconciled(Boolean.FALSE);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    protected TransactionBase(final TransactionBaseList<T> pList,
                              final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the AssetPair */
            Object myValue = pValues.getValue(FIELD_PAIR);
            if (myValue instanceof Integer) {
                setValueAssetPair((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueAssetPair((String) myValue);
            } else if (myValue instanceof AssetPair) {
                setValueAssetPair((AssetPair) myValue);
            }

            /* Store the Account */
            myValue = pValues.getValue(FIELD_ACCOUNT);
            if (myValue instanceof Integer) {
                setValueAccount((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueAccount((String) myValue);
            } else if (myValue instanceof TransactionAsset) {
                setValueAccount((TransactionAsset) myValue);
            }

            /* Store the Partner */
            myValue = pValues.getValue(FIELD_PARTNER);
            if (myValue instanceof Integer) {
                setValuePartner((Integer) myValue);
            } else if (myValue instanceof String) {
                setValuePartner((String) myValue);
            } else if (myValue instanceof TransactionAsset) {
                setValuePartner((TransactionAsset) myValue);
            }

            /* Store the Category */
            myValue = pValues.getValue(FIELD_CATEGORY);
            if (myValue instanceof Integer) {
                setValueCategory((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueCategory((String) myValue);
            } else if (myValue instanceof TransactionCategory) {
                setValueCategory((TransactionCategory) myValue);
            }

            /* Store the Amount */
            myValue = pValues.getValue(FIELD_AMOUNT);
            if (myValue instanceof TethysMoney) {
                setValueAmount((TethysMoney) myValue);
            } else if (myValue instanceof byte[]) {
                setValueAmount((byte[]) myValue);
            } else if (myValue instanceof String) {
                String myString = (String) myValue;
                setValueAmount(myString);
                setValueAmount(myFormatter.parseValue(myString, TethysMoney.class));
            }

            /* Store the reconciled flag */
            myValue = pValues.getValue(FIELD_RECONCILED);
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
    public boolean includeXmlField(final MetisField pField) {
        /* Determine whether fields should be included */
        if (FIELD_PAIR.equals(pField)) {
            return true;
        }
        if (FIELD_CATEGORY.equals(pField)) {
            return true;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return true;
        }
        if (FIELD_PARTNER.equals(pField)) {
            return true;
        }
        if (FIELD_AMOUNT.equals(pField)) {
            return true;
        }
        if (FIELD_RECONCILED.equals(pField)) {
            return isReconciled();
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public String formatObject() {
        /* Access Key Values */
        MetisEncryptedValueSet myValues = getValueSet();
        Object myAccount = myValues.getValue(FIELD_ACCOUNT);
        Object myPartner = myValues.getValue(FIELD_PARTNER);
        Object myPair = myValues.getValue(FIELD_PAIR);
        AssetDirection myDir = myPair instanceof AssetPair
                                                           ? ((AssetPair) myPair).getDirection()
                                                           : null;
        Object myCategory = myValues.getValue(FIELD_CATEGORY);
        Object myAmount = myValues.getValue(FIELD_AMOUNT);

        /* Access formatter */
        MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myFormatter.formatObject(myCategory));
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(myFormatter.formatObject(myAmount));
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(myFormatter.formatObject(myAccount));
        myBuilder.append(myDir == null
                                       ? "??"
                                       : myDir.isFrom()
                                                        ? "<-"
                                                        : "->");
        myBuilder.append(myFormatter.formatObject(myPartner));

        /* return it */
        return myBuilder.toString();
    }

    /**
     * Obtain AssetPair.
     * @return the pair
     */
    public AssetPair getAssetPair() {
        return getAssetPair(getValueSet());
    }

    /**
     * Obtain AssetPairId.
     * @return the assetPairId
     */
    public Integer getAssetPairId() {
        AssetPair myPair = getAssetPair();
        return (myPair == null)
                                ? null
                                : myPair.getEncodedId();
    }

    /**
     * Obtain AssetPairManager.
     * @return the pair manager
     */
    protected AssetPairManager getAssetPairManager() {
        return getList().getAssetPairManager();
    }

    /**
     * Obtain category.
     * @return the category
     */
    public final TransactionCategory getCategory() {
        return getCategory(getValueSet());
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        TransactionCategory myCategory = getCategory();
        return (myCategory == null)
                                    ? null
                                    : myCategory.getId();
    }

    /**
     * Obtain categoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        TransactionCategory myCategory = getCategory();
        return (myCategory == null)
                                    ? null
                                    : myCategory.getName();
    }

    /**
     * Obtain EventCategoryClass.
     * @return the eventCategoryClass
     */
    public TransactionCategoryClass getCategoryClass() {
        TransactionCategory myCategory = getCategory();
        return (myCategory == null)
                                    ? null
                                    : myCategory.getCategoryTypeClass();
    }

    /**
     * Obtain Amount.
     * @return the amount
     */
    public TethysMoney getAmount() {
        return getAmount(getValueSet());
    }

    /**
     * Obtain Encrypted amount.
     * @return the bytes
     */
    public byte[] getAmountBytes() {
        return getAmountBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Amount Field.
     * @return the Field
     */
    protected MetisEncryptedMoney getAmountField() {
        return getAmountField(getValueSet());
    }

    /**
     * Obtain Account asset.
     * @return the account
     */
    public TransactionAsset getAccount() {
        return getAccount(getValueSet());
    }

    /**
     * Obtain AccountId.
     * @return the accountId
     */
    public Integer getAccountId() {
        TransactionAsset myAccount = getAccount();
        return (myAccount == null)
                                   ? null
                                   : myAccount.getId();
    }

    /**
     * Obtain Partner asset.
     * @return the partner
     */
    public TransactionAsset getPartner() {
        return getPartner(getValueSet());
    }

    /**
     * Obtain PartnerId.
     * @return the partnerId
     */
    public Integer getPartnerId() {
        TransactionAsset myPartner = getPartner();
        return (myPartner == null)
                                   ? null
                                   : myPartner.getId();
    }

    /**
     * Obtain Direction.
     * @return the direction
     */
    public AssetDirection getDirection() {
        AssetPair myPair = getAssetPair();
        return myPair == null
                              ? null
                              : myPair.getDirection();
    }

    /**
     * Obtain Reconciled State.
     * @return the reconciled state
     */
    public Boolean isReconciled() {
        return isReconciled(getValueSet());
    }

    /**
     * Obtain AssetPair.
     * @param pValueSet the valueSet
     * @return the AssetPair
     */
    protected static AssetPair getAssetPair(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PAIR, AssetPair.class);
    }

    /**
     * Obtain Category.
     * @param pValueSet the valueSet
     * @return the category
     */
    public static TransactionCategory getCategory(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATEGORY, TransactionCategory.class);
    }

    /**
     * Obtain Amount.
     * @param pValueSet the valueSet
     * @return the Amount
     */
    public static TethysMoney getAmount(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_AMOUNT, TethysMoney.class);
    }

    /**
     * Obtain Encrypted Amount.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getAmountBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_AMOUNT);
    }

    /**
     * Obtain Encrypted amount field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static MetisEncryptedMoney getAmountField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_AMOUNT, MetisEncryptedMoney.class);
    }

    /**
     * Obtain Account Asset.
     * @param pValueSet the valueSet
     * @return the Account Asset
     */
    public static TransactionAsset getAccount(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ACCOUNT, TransactionAsset.class);
    }

    /**
     * Obtain Partner Asset.
     * @param pValueSet the valueSet
     * @return the Partner Asset
     */
    public static TransactionAsset getPartner(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARTNER, TransactionAsset.class);
    }

    /**
     * Obtain Reconciled State.
     * @param pValueSet the valueSet
     * @return the Reconciled State
     */
    public static Boolean isReconciled(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RECONCILED, Boolean.class);
    }

    /**
     * Set assetPair.
     * @param pValue the value
     */
    protected final void setValueAssetPair(final AssetPair pValue) {
        MetisEncryptedValueSet myValues = getValueSet();
        myValues.setValue(FIELD_PAIR, pValue);
        myValues.setValue(FIELD_DIRECTION, pValue.getDirection());
    }

    /**
     * Set assetPair.
     * @param pValue the value
     */
    private void setValueAssetPair(final Integer pValue) {
        getValueSet().setValue(FIELD_PAIR, pValue);
    }

    /**
     * Set assetPair.
     * @param pValue the value
     */
    private void setValueAssetPair(final String pValue) {
        getValueSet().setValue(FIELD_PAIR, pValue);
    }

    /**
     * Set category value.
     * @param pValue the value
     */
    private void setValueCategory(final TransactionCategory pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set category id.
     * @param pId the id
     */
    private void setValueCategory(final Integer pId) {
        getValueSet().setValue(FIELD_CATEGORY, pId);
    }

    /**
     * Set category name.
     * @param pName the name
     */
    private void setValueCategory(final String pName) {
        getValueSet().setValue(FIELD_CATEGORY, pName);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueAmount(final TethysMoney pValue) throws OceanusException {
        setEncryptedValue(FIELD_AMOUNT, pValue);
    }

    /**
     * Set amount value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueAmount(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_AMOUNT, pBytes, TethysMoney.class);
    }

    /**
     * Set amount value.
     * @param pValue the value
     */
    protected final void setValueAmount(final MetisEncryptedMoney pValue) {
        getValueSet().setValue(FIELD_AMOUNT, pValue);
    }

    /**
     * Set amount value.
     * @param pValue the value
     */
    private void setValueAmount(final String pValue) {
        getValueSet().setValue(FIELD_AMOUNT, pValue);
    }

    /**
     * Set account value.
     * @param pValue the value
     */
    protected final void setValueAccount(final TransactionAsset pValue) {
        getValueSet().setValue(FIELD_ACCOUNT, pValue);
    }

    /**
     * Set debit name.
     * @param pName the name
     */
    private void setValueAccount(final String pName) {
        getValueSet().setValue(FIELD_ACCOUNT, pName);
    }

    /**
     * Set debit id.
     * @param pId the value
     */
    private void setValueAccount(final Integer pId) {
        getValueSet().setValue(FIELD_ACCOUNT, pId);
    }

    /**
     * Set partner value.
     * @param pValue the value
     */
    protected final void setValuePartner(final TransactionAsset pValue) {
        getValueSet().setValue(FIELD_PARTNER, pValue);
    }

    /**
     * Set partner id.
     * @param pId the id
     */
    private void setValuePartner(final Integer pId) {
        getValueSet().setValue(FIELD_PARTNER, pId);
    }

    /**
     * Set partner name.
     * @param pName the name
     */
    private void setValuePartner(final String pName) {
        getValueSet().setValue(FIELD_PARTNER, pName);
    }

    /**
     * Set reconciled state.
     * @param pValue the value
     */
    protected final void setValueReconciled(final Boolean pValue) {
        getValueSet().setValue(FIELD_RECONCILED, pValue);
    }

    @Override
    public final MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TransactionBaseList<T> getList() {
        return (TransactionBaseList<T>) super.getList();
    }

    /**
     * Obtain portfolio for transaction.
     * @return the portfolio (or null)
     */
    public Portfolio getPortfolio() {
        /* Access account portfolio if it is a security holding */
        TransactionAsset myAsset = getAccount();
        if (myAsset instanceof SecurityHolding) {
            return ((SecurityHolding) myAsset).getPortfolio();
        }

        /* Access partner portfolio if it is a security holding */
        myAsset = getPartner();
        if (myAsset instanceof SecurityHolding) {
            return ((SecurityHolding) myAsset).getPortfolio();
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
    public int compareTo(final T pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If the categories differ */
        int iDiff = MetisDifference.compareObject(getCategory(), pThat.getCategory());
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
        MoneyWiseData myData = getDataSet();
        MetisValueSet myValues = getValueSet();
        AssetPairManager myManager = getAssetPairManager();

        /* Adjust Reconciled */
        Object myReconciled = myValues.getValue(FIELD_RECONCILED);
        if (myReconciled == null) {
            setValueReconciled(Boolean.FALSE);
        }

        /* Resolve AssetPair */
        Object myValue = myValues.getValue(FIELD_PAIR);
        if (myValue instanceof Integer) {
            myValue = myManager.lookUpPair((Integer) myValue);
        } else if (myValue instanceof String) {
            myValue = myManager.lookUpName((String) myValue);
        }

        /* Access AssetPair */
        if (!(myValue instanceof AssetPair)) {
            /* report the error */
            addError(ERROR_UNKNOWN, FIELD_PAIR);
            throw new MoneyWiseDataException(this, ERROR_RESOLUTION);
        }

        /* Store value and access as pair */
        myValues.setValue(FIELD_PAIR, myValue);
        AssetPair myPair = (AssetPair) myValue;
        myValues.setValue(FIELD_DIRECTION, myPair.getDirection());

        /* Resolve data links */
        myPair.resolveDataLink(myData, this, FIELD_ACCOUNT);
        myPair.resolveDataLink(myData, this, FIELD_PARTNER);
        resolveDataLink(FIELD_CATEGORY, myData.getTransCategories());
    }

    @Override
    public boolean isLocked() {
        TransactionAsset myAccount = getAccount();
        TransactionAsset myPartner = getPartner();

        /* Check credit and debit accounts */
        return ((myAccount != null) && myAccount.isClosed())
               || ((myPartner != null) && myPartner.isClosed());
    }

    /**
     * Is this event category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final TransactionCategoryClass pClass) {
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
        return (getAccount() != null) && MetisDifference.isEqual(getAccount(), getPartner());
    }

    /**
     * Determines whether an event is an interest payment.
     * @return interest true/false
     */
    public boolean isInterest() {
        /* Check for interest */
        TransactionCategoryClass myClass = getCategoryClass();
        return myClass == null
                               ? false
                               : myClass.isInterest();
    }

    /**
     * Determines whether an event is a dividend payment.
     * @return dividend true/false
     */
    public boolean isDividend() {
        TransactionCategoryClass myClass = getCategoryClass();
        return myClass == null
                               ? false
                               : myClass.isDividend();
    }

    /**
     * Determines whether an event needs a zero amount.
     * @return true/false
     */
    public boolean needsZeroAmount() {
        TransactionCategoryClass myClass = getCategoryClass();
        return myClass == null
                               ? false
                               : myClass.needsZeroAmount();
    }

    /**
     * Determines whether we can switch direction.
     * @return true/false
     */
    public boolean canSwitchDirection() {
        return TransactionValidator.isValidDirection(getAccount(), getCategory(), getDirection().reverse());
    }

    /**
     * Set a new account.
     * @param pAccount the account
     */
    public void setAccount(final TransactionAsset pAccount) {
        /* Adjust pair */
        AssetPair myPair = getAssetPair();
        myPair = getAssetPairManager().adjustAccount(myPair, pAccount);
        setValueAssetPair(myPair);

        /* Set account value */
        setValueAccount(pAccount);
    }

    /**
     * Set a new partner.
     * @param pPartner the partner
     */
    public void setPartner(final TransactionAsset pPartner) {
        /* Adjust pair */
        AssetPair myPair = getAssetPair();
        myPair = getAssetPairManager().adjustPartner(myPair, pPartner);
        setValueAssetPair(myPair);

        /* Set partner value */
        setValuePartner(pPartner);
    }

    /**
     * Switch direction.
     */
    public void switchDirection() {
        /* Adjust pair */
        AssetPair myPair = getAssetPair();
        myPair = getAssetPairManager().switchDirection(myPair);
        setValueAssetPair(myPair);
    }

    /**
     * Flip assets.
     */
    public void flipAssets() {
        /* Adjust pair */
        AssetPair myPair = getAssetPair();
        myPair = getAssetPairManager().flipAssets(myPair);
        setValueAssetPair(myPair);

        /* Flip details */
        TransactionAsset myAccount = getAccount();
        TransactionAsset myPartner = getPartner();
        setValueAccount(myPartner);
        setValuePartner(myAccount);
    }

    /**
     * Set a new category.
     * @param pCategory the category
     */
    public void setCategory(final TransactionCategory pCategory) {
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
     * Validate the event.
     */
    @Override
    public void validate() {
        TransactionAsset myAccount = getAccount();
        TransactionAsset myPartner = getPartner();
        AssetDirection myDir = getDirection();
        TethysMoney myAmount = getAmount();
        TransactionCategory myCategory = getCategory();
        boolean doCheckCombo = true;

        /* Account must be non-null */
        if (myAccount == null) {
            addError(ERROR_MISSING, FIELD_ACCOUNT);
            doCheckCombo = false;

        } else {
            /* Holding currency combo must be valid */
            if ((myAccount instanceof SecurityHolding)
                && !((SecurityHolding) myAccount).validCurrencies()) {
                addError(SecurityHolding.ERROR_CURRENCYCOMBO, FIELD_ACCOUNT);
            }

            /* Account must be valid */
            if (!TransactionValidator.isValidAccount(myAccount)) {
                addError(ERROR_COMBO, FIELD_ACCOUNT);
                doCheckCombo = false;
            }
        }

        /* Category must be non-null */
        if (myCategory == null) {
            addError(ERROR_MISSING, FIELD_CATEGORY);
            doCheckCombo = false;

            /* Category must be valid for Account */
        } else if (doCheckCombo && !TransactionValidator.isValidCategory(myAccount, myCategory)) {
            addError(ERROR_COMBO, FIELD_CATEGORY);
            doCheckCombo = false;
        }

        /* Direction must be non-null */
        if (myDir == null) {
            addError(ERROR_MISSING, FIELD_DIRECTION);
            doCheckCombo = false;

            /* Direction must be valid for Account */
        } else if (doCheckCombo && !TransactionValidator.isValidDirection(myAccount, myCategory, myDir)) {
            addError(ERROR_COMBO, FIELD_DIRECTION);
            doCheckCombo = false;
        }

        /* Partner must be non-null */
        if (myPartner == null) {
            addError(ERROR_MISSING, FIELD_PARTNER);
            doCheckCombo = false;

        } else {
            /* Holding currency combo must be valid */
            if ((myPartner instanceof SecurityHolding)
                && !((SecurityHolding) myPartner).validCurrencies()) {
                addError(SecurityHolding.ERROR_CURRENCYCOMBO, FIELD_PARTNER);
            }

            /* Partner must be valid for Account */
            if (doCheckCombo && !TransactionValidator.isValidPartner(myAccount, myCategory, myPartner)) {
                addError(ERROR_COMBO, FIELD_PARTNER);
                doCheckCombo = false;
            }
        }

        /* Money must not be null */
        if (myAmount == null) {
            addError(ERROR_MISSING, FIELD_AMOUNT);
        } else {
            /* Money must not be negative */
            if (!myAmount.isPositive()) {
                addError(ERROR_NEGATIVE, FIELD_AMOUNT);
            }

            /* Check that if money needs to be zero it is */
            if (needsZeroAmount()
                && (myAmount.isNonZero())) {
                addError(ERROR_ZEROAMOUNT, FIELD_AMOUNT);
            }

            /* Check that amount is correct currency */
            if (myAccount != null) {
                Currency myCurrency = myAccount.getCurrency();
                if (!myAmount.getCurrency().equals(myCurrency)) {
                    addError(ERROR_CURRENCY, FIELD_AMOUNT);
                }
            }
        }
    }

    /**
     * Update base transaction from an edited transaction.
     * @param pTrans the edited transaction
     */
    protected void applyBasicChanges(final T pTrans) {
        /* Update the assetPair if required */
        if (!MetisDifference.isEqual(getAssetPair(), pTrans.getAssetPair())) {
            setValueAssetPair(pTrans.getAssetPair());
        }

        /* Update the category if required */
        if (!MetisDifference.isEqual(getCategory(), pTrans.getCategory())) {
            setValueCategory(pTrans.getCategory());
        }

        /* Update the account if required */
        if (!MetisDifference.isEqual(getAccount(), pTrans.getAccount())) {
            setValueAccount(pTrans.getAccount());
        }

        /* Update the partner if required */
        if (!MetisDifference.isEqual(getPartner(), pTrans.getPartner())) {
            setValuePartner(pTrans.getPartner());
        }

        /* Update the amount if required */
        if (!MetisDifference.isEqual(getAmount(), pTrans.getAmount())) {
            setValueAmount(pTrans.getAmountField());
        }

        /* Update the reconciled state if required */
        if (!MetisDifference.isEqual(isReconciled(), pTrans.isReconciled())) {
            setValueReconciled(pTrans.isReconciled());
        }
    }

    /**
     * The Event List class.
     * @param <T> the dataType
     */
    public abstract static class TransactionBaseList<T extends TransactionBase<T>>
            extends EncryptedList<T, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(TransactionBaseList.class.getSimpleName(), DataList.FIELD_DEFS);

        /**
         * Range field id.
         */
        private static final MetisField FIELD_RANGE = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_RANGE.getValue());

        /**
         * DataSet range.
         */
        private TethysDateRange theRange = null;

        /**
         * AssetPair Manager.
         */
        private final AssetPairManager theManager;

        /**
         * Construct an empty CORE Event list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected TransactionBaseList(final MoneyWiseData pData,
                                      final Class<T> pClass,
                                      final MoneyWiseDataType pItemType) {
            /* Call super-constructor */
            super(pClass, pData, pItemType, ListStyle.CORE);

            /* Allocate new manager */
            theManager = new AssetPairManager();
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected TransactionBaseList(final TransactionBaseList<T> pSource) {
            /* Call super-constructor */
            super(pSource);

            /* Copy the Manager */
            theManager = pSource.getAssetPairManager();
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_RANGE.equals(pField)) {
                return theRange;
            }
            return super.getFieldValue(pField);
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Obtain valid date range.
         * @return the valid range
         */
        public TethysDateRange getValidDateRange() {
            return theRange;
        }

        /**
         * Obtain assetPair Manager.
         * @return the manager
         */
        public AssetPairManager getAssetPairManager() {
            return theManager;
        }

        /**
         * Set the range.
         * @param pRange the range
         */
        protected void setRange(final TethysDateRange pRange) {
            theRange = pRange;
        }
    }
}
