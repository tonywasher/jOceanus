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

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedMoney;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetPairManager;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.GroupedItem;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(TransactionBase.class.getName());

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDate"));

    /**
     * AssetPair Field Id.
     */
    public static final JDataField FIELD_PAIR = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataPair"));

    /**
     * Debit Field Id.
     */
    public static final JDataField FIELD_DEBIT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDebit"));

    /**
     * Credit Field Id.
     */
    public static final JDataField FIELD_CREDIT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataCredit"));

    /**
     * Amount Field Id.
     */
    public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataAmount"));

    /**
     * Category Field Id.
     */
    public static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.TRANSCATEGORY.getItemName());

    /**
     * Reconciled Field Id.
     */
    public static final JDataField FIELD_RECONCILED = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataReconciled"));

    /**
     * Split Event Field Id.
     */
    public static final JDataField FIELD_SPLIT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataSplit"));

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataParent"));

    /**
     * Hidden Category Error Text.
     */
    private static final String ERROR_HIDDEN = NLS_BUNDLE.getString("ErrorHidden");

    /**
     * Invalid Debit/Credit/Category Combination Error Text.
     */
    private static final String ERROR_COMBO = NLS_BUNDLE.getString("ErrorCombo");

    /**
     * Invalid Parent Error Text.
     */
    private static final String ERROR_BADPARENT = NLS_BUNDLE.getString("ErrorBadParent");

    /**
     * Parent Date Error Text.
     */
    private static final String ERROR_PARENTDATE = NLS_BUNDLE.getString("ErrorParentDate");

    /**
     * Zero Amount Error Text.
     */
    private static final String ERROR_ZEROAMOUNT = NLS_BUNDLE.getString("ErrorZeroAmount");

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
        if (FIELD_DEBIT.equals(pField)) {
            return !isChild() || !Difference.isEqual(getDebit(), getParent().getDebit());
        }
        if (FIELD_CREDIT.equals(pField)) {
            return !isChild() || !Difference.isEqual(getCredit(), getParent().getCredit());
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
        Object myDebit = myValues.getValue(FIELD_DEBIT, Object.class);
        Object myCredit = myValues.getValue(FIELD_CREDIT, Object.class);
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
        myBuilder.append(myFormatter.formatObject(myDebit));
        myBuilder.append("->");
        myBuilder.append(myFormatter.formatObject(myCredit));

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
     * Obtain Debit asset.
     * @return the debit
     */
    public AssetBase<?> getDebit() {
        return getDebit(getValueSet());
    }

    /**
     * Obtain DebitId.
     * @return the debitId
     */
    public Integer getDebitId() {
        AssetBase<?> myDebit = getDebit();
        return (myDebit == null)
                                ? null
                                : myDebit.getId();
    }

    /**
     * Obtain DebitName.
     * @return the debitName
     */
    public String getDebitName() {
        AssetBase<?> myDebit = getDebit();
        return (myDebit == null)
                                ? null
                                : myDebit.getName();
    }

    /**
     * Obtain Credit asset.
     * @return the credit
     */
    public AssetBase<?> getCredit() {
        return getCredit(getValueSet());
    }

    /**
     * Obtain CreditId.
     * @return the creditId
     */
    public Integer getCreditId() {
        AssetBase<?> myCredit = getCredit();
        return (myCredit == null)
                                 ? null
                                 : myCredit.getId();
    }

    /**
     * Obtain CreditName.
     * @return the creditName
     */
    public String getCreditName() {
        AssetBase<?> myCredit = getCredit();
        return (myCredit == null)
                                 ? null
                                 : myCredit.getName();
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
     * Obtain Debit Asset.
     * @param pValueSet the valueSet
     * @return the Debit Asset
     */
    public static AssetBase<?> getDebit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DEBIT, AssetBase.class);
    }

    /**
     * Obtain Credit Asset.
     * @param pValueSet the valueSet
     * @return the Credit Asset
     */
    public static AssetBase<?> getCredit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CREDIT, AssetBase.class);
    }

    /**
     * Obtain Parent Event.
     * @param pValueSet the valueSet
     * @param <T> the transaction data type
     * @return the Parent Event
     */
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
        getValueSet().setValue(FIELD_PAIR, pValue);
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
     * Set debit value.
     * @param pValue the value
     */
    protected final void setValueDebit(final AssetBase<?> pValue) {
        getValueSet().setValue(FIELD_DEBIT, pValue);
    }

    /**
     * Set debit name.
     * @param pName the name
     */
    private void setValueDebit(final String pName) {
        getValueSet().setValue(FIELD_DEBIT, pName);
    }

    /**
     * Set debit id.
     * @param pId the value
     */
    private void setValueDebit(final Integer pId) {
        getValueSet().setValue(FIELD_DEBIT, pId);
    }

    /**
     * Set credit value.
     * @param pValue the value
     */
    protected final void setValueCredit(final AssetBase<?> pValue) {
        getValueSet().setValue(FIELD_CREDIT, pValue);
    }

    /**
     * Set credit id.
     * @param pId the id
     */
    private void setValueCredit(final Integer pId) {
        getValueSet().setValue(FIELD_CREDIT, pId);
    }

    /**
     * Set credit name.
     * @param pName the name
     */
    private void setValueCredit(final String pName) {
        getValueSet().setValue(FIELD_CREDIT, pName);
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
            }

            /* Store the Debit */
            myValue = pValues.getValue(FIELD_DEBIT);
            if (myValue instanceof Integer) {
                setValueDebit((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueDebit((String) myValue);
            }

            /* Store the Credit */
            myValue = pValues.getValue(FIELD_CREDIT);
            if (myValue instanceof Integer) {
                setValueCredit((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueCredit((String) myValue);
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
        if (this == pThat) {
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
            iDiff = Difference.compareObject((T) this, myAltParent);
            if (iDiff != 0) {
                return iDiff;
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
        myPair.resolveDataLink(myData, this, FIELD_DEBIT);
        myPair.resolveDataLink(myData, this, FIELD_CREDIT);
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
        /* Analyse the components */
        AssetType myDebitType = getDebit().getAssetType();
        AssetType myCreditType = getCredit().getAssetType();
        return myDebitType.getTransactionType(myCreditType);
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

        /* Check credit and debit accounts */
        if (getCredit().equals(pAsset)) {
            myResult = true;
        } else if (getDebit().equals(pAsset)) {
            myResult = true;
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Determines whether a line is locked to updates.
     * @return true/false
     */
    @Override
    public boolean isLocked() {
        AssetBase<?> myCredit = getCredit();
        AssetBase<?> myDebit = getDebit();

        /* Check credit and debit accounts */
        return ((myCredit != null) && myCredit.isClosed()) || ((myDebit != null) && myDebit.isClosed());
    }

    /**
     * Is this event category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final TransactionCategoryClass pClass) {
        /* Check for match */
        return getCategoryClass() == pClass;
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
        return (getCredit() != null) && Difference.isEqual(getDebit(), getCredit());
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
     * Determines whether an event needs a tax credit.
     * @param pCategory the category
     * @param pDebit the debit account
     * @return needs tax credit true/false
     */
    public static boolean needsTaxCredit(final TransactionCategory pCategory,
                                         final AssetBase<?> pDebit) {
        /* Handle null category */
        if (pCategory == null) {
            return false;
        }

        /* Switch on category class */
        switch (pCategory.getCategoryTypeClass()) {
        /* If this is a Taxable Gain/TaxedIncome we need a tax credit */
        // case TaxableGain:
            case TAXEDINCOME:
                return true;
                /* Check for interest */
            case INTEREST:
                return (pDebit instanceof Deposit) && !((Deposit) pDebit).isTaxFree();
                /* Check for interest */
            case DIVIDEND:
                if (!(pDebit instanceof Security)) {
                    return true;
                }
                return false; // TODO check portfolio tax status
            default:
                return false;
        }
    }

    /**
     * Determines whether an event needs a dilution factor.
     * @param pCategory the category
     * @return needs dilution factor true/false
     */
    public static boolean needsDilution(final TransactionCategory pCategory) {
        /* Handle null category */
        if (pCategory == null) {
            return false;
        }

        /* Switch on category type */
        switch (pCategory.getCategoryType().getCategoryClass()) {
        /* If this is a Stock Operation we need a dilution factor */
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKRIGHTSTAKEN:
            case STOCKRIGHTSWAIVED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Set a new debit account.
     * @param pDebit the debit account
     */
    public void setDebit(final AssetBase<?> pDebit) {
        /* Adjust pair */
        AssetPair myPair = getAssetPair();
        getAssetPairManager().adjustDebit(myPair, pDebit);
        setValueAssetPair(myPair);

        /* Set credit value */
        setValueDebit(pDebit);
    }

    /**
     * Set a new credit account.
     * @param pCredit the credit account
     */
    public void setCredit(final AssetBase<?> pCredit) {
        /* Adjust pair */
        AssetPair myPair = getAssetPair();
        getAssetPairManager().adjustCredit(myPair, pCredit);
        setValueAssetPair(myPair);

        /* Set credit value */
        setValueCredit(pCredit);
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

        /* Touch the credit and debit accounts */
        getDebit().touchItem(this);
        getCredit().touchItem(this);

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
        AssetBase<?> myDebit = getDebit();
        AssetBase<?> myCredit = getCredit();
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

        /* Credit account must be non-null */
        if (myCredit == null) {
            addError(ERROR_MISSING, FIELD_CREDIT);
            doCheckCombo = false;
        }

        /* Debit account must be non-null */
        if (myDebit == null) {
            addError(ERROR_MISSING, FIELD_DEBIT);
            doCheckCombo = false;
        }

        /* Check combinations */
        if ((doCheckCombo) && (!isValidEvent(myCategory, myDebit, myCredit))) {
            addError(ERROR_COMBO, FIELD_DEBIT);
            addError(ERROR_COMBO, FIELD_CREDIT);
        }

        /* If we have a parent */
        if (myParent != null) {
            /* Parent must not be child */
            if (myParent.isChild()) {
                addError(ERROR_BADPARENT, FIELD_PARENT);
            }

            /* Parent must not be child */
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
        if ((myAmount != null) && (myAmount.isNonZero()) && (myCategory != null) && (myCategory.getCategoryTypeClass().needsZeroAmount())) {
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

        /* Update the debit account if required */
        if (!Difference.isEqual(getDebit(), pTrans.getDebit())) {
            setValueDebit(pTrans.getDebit());
        }

        /* Update the credit account if required */
        if (!Difference.isEqual(getCredit(), pTrans.getCredit())) {
            setValueCredit(pTrans.getCredit());
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
        private static final JDataField FIELD_RANGE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataRange"));

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
        protected AssetPairManager getAssetPairManager() {
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
