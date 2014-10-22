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

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedMoney;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetPairManager;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.GroupedItem;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Transaction data type.
 * @author Tony Washer
 * @param <T> the transaction data type
 */
public abstract class TransactionBase<T extends TransactionBase<T>>
        extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<T>, GroupedItem<MoneyWiseDataType> {
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
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

    /**
     * AssetPair Field Id.
     */
    public static final JDataField FIELD_PAIR = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_ASSETPAIR.getValue());

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_ACCOUNT.getValue());

    /**
     * Partner Field Id.
     */
    public static final JDataField FIELD_PARTNER = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_PARTNER.getValue());

    /**
     * Direction Field Id.
     */
    public static final JDataField FIELD_DIRECTION = FIELD_DEFS.declareDerivedValueField(MoneyWiseDataResource.TRANSACTION_DIRECTION.getValue());

    /**
     * Amount Field Id.
     */
    public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_AMOUNT.getValue());

    /**
     * Category Field Id.
     */
    public static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.TRANSCATEGORY.getItemName());

    /**
     * Reconciled Field Id.
     */
    public static final JDataField FIELD_RECONCILED = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_RECONCILED.getValue());

    /**
     * Split Event Field Id.
     */
    public static final JDataField FIELD_SPLIT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_SPLIT.getValue());

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAGROUP_PARENT.getValue());

    /**
     * Hidden Category Error Text.
     */
    private static final String ERROR_HIDDEN = MoneyWiseDataResource.TRANSACTION_ERROR_HIDDEN.getValue();

    /**
     * Invalid Debit/Credit/Category Combination Error Text.
     */
    private static final String ERROR_COMBO = MoneyWiseDataResource.TRANSACTION_ERROR_ASSETPAIR.getValue();

    /**
     * Invalid Parent Error Text.
     */
    private static final String ERROR_BADPARENT = MoneyWiseDataResource.TRANSACTION_ERROR_BADPARENT.getValue();

    /**
     * Parent Date Error Text.
     */
    private static final String ERROR_PARENTDATE = MoneyWiseDataResource.TRANSACTION_ERROR_PARENTDATE.getValue();

    /**
     * Zero Amount Error Text.
     */
    private static final String ERROR_ZEROAMOUNT = MoneyWiseDataResource.TRANSACTION_ERROR_ZERO.getValue();

    @Override
    public boolean skipField(final JDataField pField) {
        if ((FIELD_SPLIT.equals(pField)) && !isSplit()) {
            return true;
        }
        if ((FIELD_PARENT.equals(pField)) && (!isSplit() || (!isChild()))) {
            return true;
        }
        return super.skipField(pField);
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_DATE.equals(pField)) {
            return !isChild();
        }
        if (FIELD_PAIR.equals(pField)) {
            return true;
        }
        if (FIELD_CATEGORY.equals(pField)) {
            return true;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return !isChild();
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
        EncryptedValueSet myValues = getValueSet();
        Object myAccount = myValues.getValue(FIELD_ACCOUNT, Object.class);
        Object myPartner = myValues.getValue(FIELD_PARTNER, Object.class);
        AssetPair myPair = myValues.getValue(FIELD_PAIR, AssetPair.class);
        AssetDirection myDir = myPair == null
                                             ? null
                                             : myPair.getDirection();
        Object myCategory = myValues.getValue(FIELD_CATEGORY, Object.class);
        Object myAmount = myValues.getValue(FIELD_AMOUNT, Object.class);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myFormatter.formatObject(myCategory));
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(myFormatter.formatObject(myAmount));
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(myFormatter.formatObject(myAccount));
        myBuilder.append(AssetDirection.FROM.equals(myDir)
                                                          ? "<-"
                                                          : "->");
        myBuilder.append(myFormatter.formatObject(myPartner));

        /* return it */
        return myBuilder.toString();
    }

    /**
     * Obtain Date.
     * @return the date
     */
    public JDateDay getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain AssetPair.
     * @return the pair
     */
    protected AssetPair getAssetPair() {
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
    public JMoney getAmount() {
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
    protected EncryptedMoney getAmountField() {
        return getAmountField(getValueSet());
    }

    /**
     * Obtain Account asset.
     * @return the account
     */
    public AssetBase<?> getAccount() {
        return getAccount(getValueSet());
    }

    /**
     * Obtain AccountId.
     * @return the accountId
     */
    public Integer getAccountId() {
        AssetBase<?> myAccount = getAccount();
        return (myAccount == null)
                                  ? null
                                  : myAccount.getId();
    }

    /**
     * Obtain AccountName.
     * @return the accountName
     */
    public String getAccountName() {
        AssetBase<?> myAccount = getAccount();
        return (myAccount == null)
                                  ? null
                                  : myAccount.getName();
    }

    /**
     * Obtain Partner asset.
     * @return the partner
     */
    public AssetBase<?> getPartner() {
        return getPartner(getValueSet());
    }

    /**
     * Obtain PartnerId.
     * @return the partnerId
     */
    public Integer getPartnerId() {
        AssetBase<?> myPartner = getPartner();
        return (myPartner == null)
                                  ? null
                                  : myPartner.getId();
    }

    /**
     * Obtain PartnerName.
     * @return the partnerName
     */
    public String getPartnerName() {
        AssetBase<?> myPartner = getPartner();
        return (myPartner == null)
                                  ? null
                                  : myPartner.getName();
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
     * Obtain Split State.
     * @return the split state
     */
    public Boolean isSplit() {
        Boolean isSplit = isSplit(getValueSet());
        return isSplit != null
                              ? isSplit
                              : Boolean.FALSE;
    }

    /**
     * Obtain Parent.
     * @return the parent
     */
    @SuppressWarnings("unchecked")
    public T getParent() {
        return (T) getParent(getValueSet());
    }

    @Override
    public boolean isChild() {
        return getParent() != null;
    }

    /**
     * Obtain ParentId.
     * @return the parentId
     */
    @SuppressWarnings("unchecked")
    public Integer getParentId() {
        T myParent = (T) getParent(getValueSet());
        return (myParent == null)
                                 ? null
                                 : myParent.getId();
    }

    /**
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static JDateDay getDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, JDateDay.class);
    }

    /**
     * Obtain AssetPair.
     * @param pValueSet the valueSet
     * @return the AssetPair
     */
    protected static AssetPair getAssetPair(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PAIR, AssetPair.class);
    }

    /**
     * Obtain Reconciled State.
     * @param pValueSet the valueSet
     * @return the Reconciled State
     */
    public static Boolean isReconciled(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RECONCILED, Boolean.class);
    }

    /**
     * Obtain Split State.
     * @param pValueSet the valueSet
     * @return the Split State
     */
    public static Boolean isSplit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_SPLIT, Boolean.class);
    }

    /**
     * Obtain Category.
     * @param pValueSet the valueSet
     * @return the category
     */
    public static TransactionCategory getCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATEGORY, TransactionCategory.class);
    }

    /**
     * Obtain Amount.
     * @param pValueSet the valueSet
     * @return the Amount
     */
    public static JMoney getAmount(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_AMOUNT, JMoney.class);
    }

    /**
     * Obtain Encrypted Amount.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getAmountBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_AMOUNT);
    }

    /**
     * Obtain Encrypted amount field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedMoney getAmountField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_AMOUNT, EncryptedMoney.class);
    }

    /**
     * Obtain Account Asset.
     * @param pValueSet the valueSet
     * @return the Account Asset
     */
    public static AssetBase<?> getAccount(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ACCOUNT, AssetBase.class);
    }

    /**
     * Obtain Partner Asset.
     * @param pValueSet the valueSet
     * @return the Partner Asset
     */
    public static AssetBase<?> getPartner(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARTNER, AssetBase.class);
    }

    /**
     * Obtain Parent Event.
     * @param pValueSet the valueSet
     * @param <T> the transaction data type
     * @return the Parent Event
     */
    @SuppressWarnings("unchecked")
    public static <T extends TransactionBase<T>> TransactionBase<T> getParent(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, TransactionBase.class);
    }

    /**
     * Set date value.
     * @param pValue the value
     */
    private void setValueDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
    }

    /**
     * Set assetPair.
     * @param pValue the value
     */
    protected final void setValueAssetPair(final AssetPair pValue) {
        EncryptedValueSet myValues = getValueSet();
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
     * Set reconciled state.
     * @param pValue the value
     */
    protected final void setValueReconciled(final Boolean pValue) {
        getValueSet().setValue(FIELD_RECONCILED, pValue);
    }

    /**
     * Set split state.
     * @param pValue the value
     */
    protected final void setValueSplit(final Boolean pValue) {
        getValueSet().setValue(FIELD_SPLIT, pValue);
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
     * @throws JOceanusException on error
     */
    private void setValueAmount(final JMoney pValue) throws JOceanusException {
        setEncryptedValue(FIELD_AMOUNT, pValue);
    }

    /**
     * Set amount value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueAmount(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_AMOUNT, pBytes, JMoney.class);
    }

    /**
     * Set amount value.
     * @param pValue the value
     */
    protected final void setValueAmount(final EncryptedMoney pValue) {
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
    protected final void setValueAccount(final AssetBase<?> pValue) {
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
    protected final void setValuePartner(final AssetBase<?> pValue) {
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
     * Set parent value.
     * @param pValue the value
     */
    protected final void setValueParent(final T pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set parent id.
     * @param pId the value
     */
    private void setValueParent(final Integer pId) {
        getValueSet().setValue(FIELD_PARENT, pId);
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
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    @SuppressWarnings("unchecked")
    protected TransactionBase(final TransactionBaseList<T> pList,
                              final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            Object myValue = pValues.getValue(FIELD_DATE);
            if (myValue instanceof JDateDay) {
                setValueDate((JDateDay) myValue);
            } else if (myValue instanceof String) {
                JDateDayFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDateDay((String) myValue));
            }

            /* Store the AssetPair */
            myValue = pValues.getValue(FIELD_PAIR);
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
            } else if (myValue instanceof AssetBase) {
                setValueAccount((AssetBase<?>) myValue);
            }

            /* Store the Partner */
            myValue = pValues.getValue(FIELD_PARTNER);
            if (myValue instanceof Integer) {
                setValuePartner((Integer) myValue);
            } else if (myValue instanceof String) {
                setValuePartner((String) myValue);
            } else if (myValue instanceof AssetBase) {
                setValuePartner((AssetBase<?>) myValue);
            }

            /* Store the Category */
            myValue = pValues.getValue(FIELD_CATEGORY);
            if (myValue instanceof Integer) {
                setValueCategory((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueCategory((String) myValue);
            }

            /* Store the Amount */
            myValue = pValues.getValue(FIELD_AMOUNT);
            if (myValue instanceof JMoney) {
                setValueAmount((JMoney) myValue);
            } else if (myValue instanceof byte[]) {
                setValueAmount((byte[]) myValue);
            } else if (myValue instanceof String) {
                String myString = (String) myValue;
                setValueAmount(myString);
                setValueAmount(myFormatter.parseValue(myString, JMoney.class));
            }

            /* Store the Parent */
            myValue = pValues.getValue(FIELD_PARENT);
            if (myValue instanceof Integer) {
                setValueParent((Integer) myValue);
            } else if (myValue instanceof TransactionBase) {
                setValueParent((T) myValue);
            }

            /* Store the reconciled flag */
            myValue = pValues.getValue(FIELD_RECONCILED);
            if (myValue instanceof Boolean) {
                setValueReconciled((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueReconciled(myFormatter.parseValue((String) myValue, Boolean.class));
            }

            /* Store the split flag */
            myValue = pValues.getValue(FIELD_SPLIT);
            if (myValue instanceof Boolean) {
                setValueSplit((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueSplit(myFormatter.parseValue((String) myValue, Boolean.class));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException
                | JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Compare this event to another to establish sort order.
     * @param pThat The Event to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the sort order
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

        /* If header settings differ */
        if (isHeader() != pThat.isHeader()) {
            return isHeader()
                             ? -1
                             : 1;
        }

        /* If the dates differ */
        int iDiff = Difference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Access parents */
        T myParent = getParent();
        T myAltParent = pThat.getParent();

        /* If we are a child */
        if (myParent != null) {
            /* If we are both children */
            if (myAltParent != null) {
                /* Compare parents */
                iDiff = Difference.compareObject(myParent, myAltParent);
                if (iDiff != 0) {
                    return iDiff;
                }

                /* Same parent so compare directly */

                /* else we are comparing against a parent */
            } else {
                /* Compare parent with target */
                iDiff = Difference.compareObject(myParent, pThat);
                if (iDiff != 0) {
                    return iDiff;
                }

                /* We are comparing against our parent, so always later */
                return 1;
            }

            /* else if we are comparing against a child */
        } else if (myAltParent != null) {
            /* Compare with targets parent */
            @SuppressWarnings("unchecked")
            int iDiff1 = Difference.compareObject((T) this, myAltParent);
            if (iDiff1 != 0) {
                return iDiff1;
            }

            /* We are comparing against our parent, so always later */
            return -1;
        }

        /* If the categories differ */
        iDiff = Difference.compareObject(getCategory(), pThat.getCategory());
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
        MoneyWiseData myData = getDataSet();
        ValueSet myValues = getValueSet();
        AssetPairManager myManager = getAssetPairManager();

        /* Adjust Split */
        Object mySplit = myValues.getValue(FIELD_SPLIT);
        if (mySplit == null) {
            setValueSplit(Boolean.FALSE);
        }

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
            throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
        }

        /* Store value and access as pair */
        myValues.setValue(FIELD_PAIR, myValue);
        AssetPair myPair = (AssetPair) myValue;

        /* Resolve data links */
        myPair.resolveDataLink(myData, this, FIELD_ACCOUNT);
        myPair.resolveDataLink(myData, this, FIELD_PARTNER);
        resolveDataLink(FIELD_CATEGORY, myData.getTransCategories());
    }

    /**
     * Determine transaction Type according to category.
     * @return transaction type
     */
    public TransactionType deriveCategoryTranType() {
        /* Analyse the components */
        return TransactionType.deriveType(getCategory());
    }

    /**
     * Determine transaction Type according to accounts.
     * @return transaction type
     */
    public TransactionType deriveAccountTranType() {
        return getAssetPair().deriveAccountTranType();
    }

    /**
     * Determine validity of an event between the two assets, for the given category.
     * @param pCategory The category of the event
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @return true/false
     */
    public static boolean isValidEvent(final TransactionCategory pCategory,
                                       final AssetBase<?> pDebit,
                                       final AssetBase<?> pCredit) {
        /* Analyse the components */
        boolean isRecursive = Difference.isEqual(pDebit, pCredit);
        AssetBase<?> myDebit = pDebit;
        AssetBase<?> myCredit = pCredit;
        AssetType myDebitType = pDebit.getAssetType();
        AssetType myCreditType = pCredit.getAssetType();
        TransactionType myCatTran = TransactionType.deriveType(pCategory);
        TransactionType myActTran = myDebitType.getTransactionType(myCreditType);

        /* Handle illegal setups */
        if (myCatTran.isIllegal() || myActTran.isIllegal()) {
            return false;
        }

        /* Access account category classes */
        TransactionCategoryClass myCatClass = pCategory.getCategoryTypeClass();

        /* If the transaction involves autoExpense */
        if (myActTran.isAutoExpense()) {
            /* Special processing */
            switch (myCatClass) {
                case TRANSFER:
                    /* Transfer must be to/from deposit/cash/loan */
                    return (myDebitType.isAutoExpense())
                                                        ? myCreditType.isValued()
                                                        : myDebitType.isValued();
                case EXPENSE:
                    /* Transfer must be to/from payee */
                    return (myDebitType.isAutoExpense())
                                                        ? myCreditType.isPayee()
                                                        : myDebitType.isPayee();

                    /* Auto Expense cannot be used for other categories */
                default:
                    return false;
            }
        }

        /* If this is an non-recursive expense (i.e. decreases the value of assets) */
        if (myActTran.isExpense() && !isRecursive) {
            /* Switch Debit and Credit so that this look like an income */
            AssetBase<?> myAsset = myDebit;
            myDebit = myCredit;
            myCredit = myAsset;

            /* Switch debit and credit types */
            AssetType myType = myDebitType;
            myDebitType = myCreditType;
            myCreditType = myType;
        }

        /* Switch on the CategoryClass */
        switch (myCatClass) {
            case TAXEDINCOME:
                /* Cannot refund Taxed Income */
                if (myActTran.isExpense()) {
                    return false;
                }

                /* Taxed income must be from employer to deposit/cash/loan (as expense) */
                return (myDebit instanceof Payee)
                       && ((Payee) myDebit).isPayeeClass(PayeeTypeClass.EMPLOYER)
                       && myCreditType.isValued();

            case GRANTINCOME:
                /* Cannot refund Grant Income */
                if (myActTran.isExpense()) {
                    return false;
                }

                /* Grant income must be from grant-able to deposit account */
                return (myDebit instanceof Payee)
                       && ((Payee) myDebit).getPayeeTypeClass().canGrant()
                       && myCreditType.isDeposit();

            case BENEFITINCOME:
                /* Cannot refund Benefit Income */
                if (myActTran.isExpense()) {
                    return false;
                }

                /* Benefit income must be from government to deposit account */
                return (myDebit instanceof Payee)
                       && ((Payee) myDebit).isPayeeClass(PayeeTypeClass.GOVERNMENT)
                       && myCreditType.isDeposit();

            case OTHERINCOME:
                /* Other income is from payee to deposit/cash/loan */
                return myDebitType.isPayee() && myCreditType.isValued();

            case GIFTEDINCOME:
            case INHERITED:
                /* Cannot refund Gifted Income/Inheritance */
                if (myActTran.isExpense()) {
                    return false;
                }

                /* Inheritance must be from individual to asset */
                return (myDebit instanceof Payee)
                       && ((Payee) myDebit).isPayeeClass(PayeeTypeClass.INDIVIDUAL)
                       && myCreditType.isAsset();

            case INTEREST:
                /* Debit must be able to generate interest */
                if (!myDebitType.isDeposit()) {
                    return false;
                }

                /* Interest must be paid to valued account */
                return myCreditType.isValued();

            case DIVIDEND:
                /* Debit must be able to generate dividend */
                if (!myDebit.canDividend()) {
                    return false;
                }

                /* Dividend must be paid to valued account or else re-invested into capital */
                return myCreditType.isValued() || (isRecursive && myDebit.isCapital());

            case CASHBACK:
                /* CashBack must be from creditCard to Asset */
                return (myDebit instanceof Loan)
                       && ((Loan) myDebit).isLoanClass(LoanCategoryClass.CREDITCARD)
                       && myCreditType.isAsset();

            case LOYALTYBONUS:
                /* LoyaltyBonus must be from portfolio to Security/Deposit */
                return (myDebit instanceof Portfolio)
                       && ((myCredit instanceof Security) || (myCredit instanceof Deposit));

            case STOCKRIGHTSTAKEN:
                /* Stock rights taken is a transfer from a valued account to shares */
                return myDebitType.isValued() && myCredit.isShares();

            case STOCKRIGHTSWAIVED:
                /* Stock rights taken is a transfer to a valued account from shares */
                return myCreditType.isValued() && myDebit.isShares();

            case STOCKSPLIT:
                /* Stock adjust is only valid for shares and must be recursive */
                return isRecursive && myDebit.isShares();

            case STOCKADJUST:
                /* Stock adjust is only valid for capital and must be recursive */
                return isRecursive && myDebit.isCapital();

            case STOCKDEMERGER:
            case STOCKTAKEOVER:
                /* Stock DeMerger/TakeOver must be between different capital shares */
                return !isRecursive && myDebit.isShares() && myCredit.isShares();

            case RENTALINCOME:
            case ROOMRENTALINCOME:
                /* Rental Income must be from property to loan */
                return (myDebit instanceof Security)
                       && ((Security) myDebit).isSecurityClass(SecurityTypeClass.PROPERTY)
                       && myCreditType.isLoan();

            case WRITEOFF:
            case LOANINTERESTEARNED:
            case LOANINTERESTCHARGED:
                /* Must be recursive on loan */
                return myDebitType.isLoan() && isRecursive;

            case LOCALTAXES:
                /* Local taxes must be from government to valued account */
                return (myDebit instanceof Payee)
                       && ((Payee) myDebit).isPayeeClass(PayeeTypeClass.GOVERNMENT)
                       && myCreditType.isValued();

            case CHARITYDONATION:
                /* CharityDonation is from payee to Valued */
                return myDebitType.isPayee() && myCreditType.isValued();

            case TAXRELIEF:
                /* Tax Relief is from TaxMan to loan */
                return (myDebit instanceof Payee)
                       && ((Payee) myDebit).isPayeeClass(PayeeTypeClass.TAXMAN)
                       && myCreditType.isLoan();

            case TAXSETTLEMENT:
                /* Settlement income is from TaxMan to valued */
                return (myDebit instanceof Payee)
                       && ((Payee) myDebit).isPayeeClass(PayeeTypeClass.TAXMAN)
                       && myCreditType.isValued();

            case TRANSFER:
                /* transfer is nonRecursive and from Asset to Asset */
                return !isRecursive && myDebitType.isAsset() && myCreditType.isAsset();

            case EXPENSE:
                /* Recovered expense is from nonAsset to Asset */
                return !myDebitType.isValued() && myCreditType.isValued();

            case PORTFOLIOXFER:
                /* portfolioXfer is nonRecursive and from Portfolio/Security to Portfolio */
                return !isRecursive
                       && (myCredit instanceof Portfolio)
                       && ((myDebit instanceof Security) || (myDebit instanceof Portfolio));

            default:
                return false;
        }
    }

    /**
     * Determines whether an event relates to an asset.
     * @param pAsset The asset to check relations with
     * @return related to the account true/false
     */
    public boolean relatesTo(final AssetBase<?> pAsset) {
        boolean myResult = false;

        /* Check account and partner accounts */
        if (getAccount().equals(pAsset)) {
            myResult = true;
        } else if (getPartner().equals(pAsset)) {
            myResult = true;
        }

        /* Return the result */
        return myResult;
    }

    @Override
    public boolean isLocked() {
        AssetBase<?> myAccount = getAccount();
        AssetBase<?> myPartner = getPartner();

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
        return (getAccount() != null) && Difference.isEqual(getAccount(), getPartner());
    }

    /**
     * Determines whether an event is an interest payment.
     * @return interest true/false
     */
    public boolean isInterest() {
        /* Check for interest */
        return (getCategory() != null) && getCategory().getCategoryTypeClass().isInterest();
    }

    /**
     * Determines whether an event is a dividend payment.
     * @return dividend true/false
     */
    public boolean isDividend() {
        /* Check for dividend */
        return (getCategory() != null) && getCategory().getCategoryTypeClass().isDividend();
    }

    /**
     * Set a new account.
     * @param pAccount the account
     */
    public void setAccount(final AssetBase<?> pAccount) {
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
    public void setPartner(final AssetBase<?> pPartner) {
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
        AssetBase<?> myAccount = getAccount();
        AssetBase<?> myPartner = getPartner();
        setValueAccount(myPartner);
        setValuePartner(myAccount);
    }

    /**
     * Set a new parent event.
     * @param pParent the parent event
     */
    public void setParent(final T pParent) {
        setValueParent(pParent);
    }

    /**
     * Set a new category.
     * @param pCategory the category
     */
    public void setCategory(final TransactionCategory pCategory) {
        setValueCategory(pCategory);
    }

    /**
     * Set a reconciled indication.
     * @param pReconciled the reconciled state
     */
    public void setReconciled(final Boolean pReconciled) {
        setValueReconciled(pReconciled);
    }

    /**
     * Set a split indication.
     * @param pSplit the reconciled state
     */
    public void setSplit(final Boolean pSplit) {
        setValueSplit(pSplit);
    }

    /**
     * Set a new amount.
     * @param pAmount the amount
     * @throws JOceanusException on error
     */
    public void setAmount(final JMoney pAmount) throws JOceanusException {
        setValueAmount(pAmount);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final JDateDay pDate) {
        setValueDate((pDate == null)
                                    ? null
                                    : new JDateDay(pDate));
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the event category referred to */
        getCategory().touchItem(this);

        /* Touch the account and partner */
        getAccount().touchItem(this);
        getPartner().touchItem(this);

        /* Touch parent */
        T myParent = getParent();
        if (myParent != null) {
            myParent.touchItem(this);
        }
    }

    /**
     * Validate the event.
     */
    @Override
    public void validate() {
        JDateDay myDate = getDate();
        AssetBase<?> myAccount = getAccount();
        AssetBase<?> myPartner = getPartner();
        JMoney myAmount = getAmount();
        T myParent = getParent();
        TransactionCategory myCategory = getCategory();
        boolean doCheckCombo = true;

        /* Determine date range to check for */
        TransactionBaseList<T> myList = getList();
        JDateDayRange myRange = myList.getValidDateRange();

        /* The date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);
            /* The date must be in-range */
        } else if (myRange.compareTo(myDate) != 0) {
            addError(ERROR_RANGE, FIELD_DATE);
        }

        /* Category must be non-null */
        if (myCategory == null) {
            addError(ERROR_MISSING, FIELD_CATEGORY);
            doCheckCombo = false;
            /* Must not be hidden */
        } else if (myCategory.getCategoryTypeClass().isHiddenType()) {
            addError(ERROR_HIDDEN, FIELD_CATEGORY);
        }

        /* Account must be non-null */
        if (myAccount == null) {
            addError(ERROR_MISSING, FIELD_ACCOUNT);
            doCheckCombo = false;
        }

        /* Partner must be non-null */
        if (myPartner == null) {
            addError(ERROR_MISSING, FIELD_PARTNER);
            doCheckCombo = false;
        }

        /* Check combinations TODO */
        // if ((doCheckCombo) && (!isValidEvent(myCategory, myAccount, myPartner))) {
        // addError(ERROR_COMBO, FIELD_ACCOUNT);
        // addError(ERROR_COMBO, FIELD_PARTNER);
        // }

        /* If we have a parent */
        if (myParent != null) {
            /* Parent must not be child */
            if (myParent.isChild()) {
                addError(ERROR_BADPARENT, FIELD_PARENT);
            }

            /* Parent must have same date */
            if (!Difference.isEqual(myDate, myParent.getDate())) {
                addError(ERROR_PARENTDATE, FIELD_PARENT);
            }
        }

        /* Money must not be null/negative */
        if (myAmount == null) {
            addError(ERROR_MISSING, FIELD_AMOUNT);
        } else if (!myAmount.isPositive()) {
            addError(ERROR_NEGATIVE, FIELD_AMOUNT);
        }

        /* Money must be zero for stock split/adjust/deMerger */
        if ((myAmount != null) && (myAmount.isNonZero())
            && (myCategory != null) && (myCategory.getCategoryTypeClass().needsZeroAmount())) {
            addError(ERROR_ZEROAMOUNT, FIELD_AMOUNT);
        }
    }

    /**
     * Update base transaction from an edited transaction.
     * @param pTrans the edited transaction
     */
    protected void applyBasicChanges(final T pTrans) {
        /* Update the Date if required */
        if (!Difference.isEqual(getDate(), pTrans.getDate())) {
            setValueDate(pTrans.getDate());
        }

        /* Update the assetPair if required */
        if (!Difference.isEqual(getAssetPair(), pTrans.getAssetPair())) {
            setValueAssetPair(pTrans.getAssetPair());
        }

        /* Update the category if required */
        if (!Difference.isEqual(getCategory(), pTrans.getCategory())) {
            setValueCategory(pTrans.getCategory());
        }

        /* Update the account if required */
        if (!Difference.isEqual(getAccount(), pTrans.getAccount())) {
            setValueAccount(pTrans.getAccount());
        }

        /* Update the partner if required */
        if (!Difference.isEqual(getPartner(), pTrans.getPartner())) {
            setValuePartner(pTrans.getPartner());
        }

        /* Update the parent transaction if required */
        if (!Difference.isEqual(getParent(), pTrans.getParent())) {
            setValueParent(pTrans.getParent());
        }

        /* Update the amount if required */
        if (!Difference.isEqual(getAmount(), pTrans.getAmount())) {
            setValueAmount(pTrans.getAmountField());
        }

        /* Update the reconciled state if required */
        if (!Difference.isEqual(isReconciled(), pTrans.isReconciled())) {
            setValueReconciled(pTrans.isReconciled());
        }

        /* Update the split state if required */
        if (!Difference.isEqual(isSplit(), pTrans.isSplit())) {
            setValueSplit(pTrans.isSplit());
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
        protected static final JDataFields FIELD_DEFS = new JDataFields(TransactionBaseList.class.getSimpleName(), DataList.FIELD_DEFS);

        /**
         * Range field id.
         */
        private static final JDataField FIELD_RANGE = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_RANGE.getValue());

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_RANGE.equals(pField)) {
                return theRange;
            }
            return super.getFieldValue(pField);
        }

        /**
         * DataSet range.
         */
        private JDateDayRange theRange = null;

        /**
         * AssetPair Manager.
         */
        private final AssetPairManager theManager;

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Obtain valid date range.
         * @return the valid range
         */
        public JDateDayRange getValidDateRange() {
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
        protected void setRange(final JDateDayRange pRange) {
            theRange = pRange;
        }

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
    }
}
