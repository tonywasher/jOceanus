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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.CashInfo.CashInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Cash class.
 */
public class Cash
        extends AssetBase<Cash>
        implements InfoSetItem<MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.CASH.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.CASH.getListName();

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * AccountCategory Field Id.
     */
    public static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.CASHCATEGORY.getItemName());

    /**
     * Currency Field Id.
     */
    public static final JDataField FIELD_CURRENCY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.CURRENCY.getItemName());

    /**
     * PayeeInfoSet field Id.
     */
    private static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseDataResource.CASH_NEWACCOUNT.getValue();

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * CashInfoSet.
     */
    private final CashInfoSet theInfoSet;

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_CATEGORY.equals(pField)) {
            return true;
        }
        if (FIELD_CURRENCY.equals(pField)) {
            return getCashCurrency() != null;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet
                             : JDataFieldValue.SKIP;
        }

        /* Handle infoSet fields */
        AccountInfoClass myClass = CashInfoSet.getClassForField(pField);
        if ((theInfoSet != null) && (myClass != null)) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public CashInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Notes.
     * @return the notes
     */
    public char[] getNotes() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.NOTES, char[].class)
                         : null;
    }

    /**
     * Obtain AutoExpense.
     * @return the autoExpense category
     */
    public TransactionCategory getAutoExpense() {
        return hasInfoSet
                         ? theInfoSet.getEventCategory(AccountInfoClass.AUTOEXPENSE)
                         : null;
    }

    /**
     * Obtain AutoPayee.
     * @return the autoExpense category
     */
    public Payee getAutoPayee() {
        return hasInfoSet
                         ? theInfoSet.getPayee(AccountInfoClass.AUTOPAYEE)
                         : null;
    }

    /**
     * Obtain CashCategory.
     * @return the category
     */
    public CashCategory getCategory() {
        return getCategory(getValueSet());
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        CashCategory myCategory = getCategory();
        return (myCategory == null)
                                   ? null
                                   : myCategory.getId();
    }

    /**
     * Obtain CategoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        CashCategory myCategory = getCategory();
        return (myCategory == null)
                                   ? null
                                   : myCategory.getName();
    }

    /**
     * Obtain CashCategoryClass.
     * @return the categoryClass
     */
    public CashCategoryClass getCategoryClass() {
        CashCategory myCategory = getCategory();
        return (myCategory == null)
                                   ? null
                                   : myCategory.getCategoryTypeClass();
    }

    /**
     * Obtain Cash Currency.
     * @return the currency
     */
    public AccountCurrency getCashCurrency() {
        return getCashCurrency(getValueSet());
    }

    /**
     * Obtain DepositCurrencyId.
     * @return the currencyId
     */
    public Integer getCashCurrencyId() {
        AccountCurrency myCurrency = getCashCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getId();
    }

    /**
     * Obtain CashCurrencyName.
     * @return the currencyName
     */
    public String getCashCurrencyName() {
        AccountCurrency myCurrency = getCashCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getName();
    }

    /**
     * Obtain Category.
     * @param pValueSet the valueSet
     * @return the category
     */
    public static CashCategory getCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATEGORY, CashCategory.class);
    }

    /**
     * Obtain CashCurrency.
     * @param pValueSet the valueSet
     * @return the SecurityCurrency
     */
    public static AccountCurrency getCashCurrency(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CURRENCY, AccountCurrency.class);
    }

    /**
     * Set cash category value.
     * @param pValue the value
     */
    private void setValueCategory(final CashCategory pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set cash category id.
     * @param pValue the value
     */
    private void setValueCategory(final Integer pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set cash category name.
     * @param pValue the value
     */
    private void setValueCategory(final String pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set cash currency value.
     * @param pValue the value
     */
    private void setValueCurrency(final AccountCurrency pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set cash currency id.
     * @param pValue the value
     */
    private void setValueCurrency(final Integer pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set cash currency name.
     * @param pValue the value
     */
    private void setValueCurrency(final String pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    @Override
    public boolean isAutoExpense() {
        return CashCategoryClass.AUTOEXPENSE.equals(getCategoryClass());
    }

    @Override
    public AssetType getAssetType() {
        return isAutoExpense()
                              ? AssetType.AUTOEXPENSE
                              : AssetType.CASH;
    }

    @Override
    public Cash getBase() {
        return (Cash) super.getBase();
    }

    @Override
    public CashList getList() {
        return (CashList) super.getList();
    }

    @Override
    public DataState getState() {
        /* Pop history for self */
        DataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == DataState.CLEAN) && (useInfoSet)) {
            /* Get state for infoSet */
            myState = theInfoSet.getState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public EditState getEditState() {
        /* Pop history for self */
        EditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if ((myState == EditState.CLEAN) && (useInfoSet)) {
            /* Get state for infoSet */
            myState = theInfoSet.getEditState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public boolean hasHistory() {
        /* Check for history for self */
        boolean hasHistory = super.hasHistory();

        /* If we should use the InfoSet */
        if ((!hasHistory) && (useInfoSet)) {
            /* Check history for infoSet */
            hasHistory = theInfoSet.hasHistory();
        }

        /* Return details */
        return hasHistory;
    }

    @Override
    public void pushHistory() {
        /* Push history for self */
        super.pushHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Push history for infoSet */
            theInfoSet.pushHistory();
        }
    }

    @Override
    public void popHistory() {
        /* Pop history for self */
        super.popHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Pop history for infoSet */
            theInfoSet.popHistory();
        }
    }

    @Override
    public boolean checkForHistory() {
        /* Check for history for self */
        boolean bChanges = super.checkForHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Check for history for infoSet */
            bChanges |= theInfoSet.checkForHistory();
        }

        /* return result */
        return bChanges;
    }

    @Override
    public Difference fieldChanged(final JDataField pField) {
        /* Handle InfoSet fields */
        AccountInfoClass myClass = CashInfoSet.getClassForField(pField);
        if (myClass != null) {
            return (useInfoSet)
                               ? theInfoSet.fieldChanged(myClass)
                               : Difference.IDENTICAL;
        }

        /* Check super fields */
        return super.fieldChanged(pField);
    }

    @Override
    public void setDeleted(final boolean bDeleted) {
        /* Pass call to infoSet if required */
        if (useInfoSet) {
            theInfoSet.setDeleted(bDeleted);
        }

        /* Pass call onwards */
        super.setDeleted(bDeleted);
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCash The Cash to copy
     */
    protected Cash(final CashList pList,
                   final Cash pCash) {
        /* Set standard values */
        super(pList, pCash);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new CashInfoSet(this, pList.getActInfoTypes(), pList.getCashInfo());
                theInfoSet.cloneDataInfoSet(pCash.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new CashInfoSet(this, pList.getActInfoTypes(), pList.getCashInfo());
                hasInfoSet = true;
                useInfoSet = false;
                break;
            default:
                theInfoSet = null;
                hasInfoSet = false;
                useInfoSet = false;
                break;
        }
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private Cash(final CashList pList,
                 final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Category */
        Object myValue = pValues.getValue(FIELD_CATEGORY);
        if (myValue instanceof Integer) {
            setValueCategory((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueCategory((String) myValue);
        }

        /* Store the Currency */
        myValue = pValues.getValue(FIELD_CURRENCY);
        if (myValue instanceof Integer) {
            setValueCurrency((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueCurrency((String) myValue);
        } else if (myValue instanceof AccountCurrency) {
            setValueCurrency((AccountCurrency) myValue);
        }

        /* Create the InfoSet */
        theInfoSet = new CashInfoSet(this, pList.getActInfoTypes(), pList.getCashInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Cash(final CashList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new CashInfoSet(this, pList.getActInfoTypes(), pList.getCashInfo());
        hasInfoSet = true;
        useInfoSet = true;
    }

    /**
     * Set defaults.
     * @param pUpdateSet the update set
     * @throws JOceanusException on error
     */
    public void setDefaults(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* Set values */
        CashCategoryList myCategories = getDataSet().getCashCategories();
        setCashCategory(myCategories.getDefaultCategory());
        setName(getList().getUniqueName(NAME_NEWACCOUNT));
        setClosed(Boolean.FALSE);
        adjustForCategory(pUpdateSet);
    }

    /**
     * adjust values after category change.
     * @param pUpdateSet the update set
     * @throws JOceanusException on error
     */
    public void adjustForCategory(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* If we are autoExpense */
        if (isAutoExpense()) {
            /* Ensure that we have an autoExpense */
            if (getAutoExpense() == null) {
                TransactionCategoryList myCategories = getDataSet().getTransCategories();
                setAutoExpense(myCategories.getDefaultAutoExpense());
            }

            /* Ensure that we have an autoPayee */
            if (getAutoPayee() == null) {
                PayeeList myPayees = pUpdateSet.findDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
                setAutoPayee(myPayees.getDefaultAutoPayee());
            }

            /* No currency */
            setCashCurrency(null);

            /* Else standard cash account */
        } else {
            /* Ensure that autoPayee and Expense are null */
            setAutoExpense(null);
            setAutoPayee(null);

            /* Ensure that we have a currency */
            if (getCashCurrency() == null) {
                setCashCurrency(getDataSet().getDefaultCurrency());
            }
        }
    }

    @Override
    public int compareTo(final Cash pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the category */
        int iDiff = Difference.compareObject(getCategory(), pThat.getCategory());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying base */
        return super.compareTo(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_CATEGORY, myData.getCashCategories());
        resolveDataLink(FIELD_CURRENCY, myData.getAccountCurrencies());
    }

    /**
     * Set a new cash category.
     * @param pCategory the new category
     */
    public void setCashCategory(final CashCategory pCategory) {
        setValueCategory(pCategory);
    }

    /**
     * Set a new cash currency.
     * @param pCurrency the new currency
     */
    public void setCashCurrency(final AccountCurrency pCurrency) {
        setValueCurrency(pCurrency);
    }

    /**
     * Set a new Notes.
     * @param pNotes the new notes
     * @throws JOceanusException on error
     */
    public void setNotes(final char[] pNotes) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.NOTES, pNotes);
    }

    /**
     * Set a new autoExpense.
     * @param pCategory the new autoExpense
     * @throws JOceanusException on error
     */
    public void setAutoExpense(final TransactionCategory pCategory) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.AUTOEXPENSE, pCategory);
    }

    /**
     * Set a new autoPayee.
     * @param pPayee the new autoPayee
     * @throws JOceanusException on error
     */
    public void setAutoPayee(final Payee pPayee) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.AUTOPAYEE, pPayee);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws JOceanusException on error
     */
    private void setInfoSetValue(final AccountInfoClass pInfoClass,
                                 final Object pValue) throws JOceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new JMoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Set the value */
        theInfoSet.setValue(pInfoClass, pValue);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the category and currency */
        getCategory().touchItem(this);
        AccountCurrency myCurrency = getCashCurrency();
        if (myCurrency != null) {
            myCurrency.touchItem(this);
        }
    }

    @Override
    protected MoneyWiseDataType[] getUniqueSet() {
        return new MoneyWiseDataType[]
        { MoneyWiseDataType.LOAN, MoneyWiseDataType.DEPOSIT, MoneyWiseDataType.PORTFOLIO };
    }

    @Override
    public void validate() {
        CashCategory myCategory = getCategory();
        AccountCurrency myCurrency = getCashCurrency();

        /* Validate base components */
        super.validate();

        /* Category must be non-null */
        if (myCategory == null) {
            addError(ERROR_MISSING, FIELD_CATEGORY);
        } else if (myCategory.getCategoryTypeClass().isParentCategory()) {
            addError(ERROR_BADCATEGORY, FIELD_CATEGORY);
        }

        /* Currency must be non-null and enabled for nonAutoExpense and must be null otherwise */
        if (myCurrency == null) {
            if (!isAutoExpense()) {
                addError(ERROR_MISSING, FIELD_CURRENCY);
            }
        } else if (isAutoExpense()) {
            addError(ERROR_EXIST, FIELD_CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_CURRENCY);
        }

        /* If we have an infoSet */
        if (theInfoSet != null) {
            /* Validate the InfoSet */
            theInfoSet.validate();
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Update base cash from an edited cash.
     * @param pCash the edited cash
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pCash) {
        /* Can only update from a cash */
        if (!(pCash instanceof Cash)) {
            return false;
        }
        Cash myCash = (Cash) pCash;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myCash);

        /* Update the category if required */
        if (!Difference.isEqual(getCategory(), myCash.getCategory())) {
            setValueCategory(myCash.getCategory());
        }

        /* Update the deposit currency if required */
        if (!Difference.isEqual(getCashCurrency(), myCash.getCashCurrency())) {
            setValueCurrency(myCash.getCashCurrency());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Cash List class.
     */
    public static class CashList
            extends AssetBaseList<Cash> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The CashInfo List.
         */
        private CashInfoList theInfoList = null;

        /**
         * The AccountInfoType list.
         */
        private AccountInfoTypeList theInfoTypeList = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return Cash.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Obtain the depositInfoList.
         * @return the deposit info list
         */
        public CashInfoList getCashInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getCashInfo();
            }
            return theInfoList;
        }

        /**
         * Obtain the accountInfoTypeList.
         * @return the account info type list
         */
        public AccountInfoTypeList getActInfoTypes() {
            if (theInfoTypeList == null) {
                theInfoTypeList = getDataSet().getActInfoTypes();
            }
            return theInfoTypeList;
        }

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        public CashList(final MoneyWiseData pData) {
            super(pData, Cash.class, MoneyWiseDataType.CASH);
        }

        @Override
        protected CashList getEmptyList(final ListStyle pStyle) {
            CashList myList = new CashList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected CashList(final CashList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public CashList deriveEditList() {
            /* Build an empty List */
            CashList myList = getEmptyList(ListStyle.EDIT);

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            CashInfoList myDepInfo = getCashInfo();
            myList.theInfoList = myDepInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the cash */
            Iterator<Cash> myIterator = iterator();
            while (myIterator.hasNext()) {
                Cash myCurr = myIterator.next();

                /* Ignore deleted deposits */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked cash and add it to the list */
                Cash myCash = new Cash(myList, myCurr);
                myList.append(myCash);
            }

            /* Return the list */
            return myList;
        }

        @Override
        public Cash addCopyItem(final DataItem<?> pCash) {
            /* Can only clone a Cash */
            if (!(pCash instanceof Cash)) {
                throw new UnsupportedOperationException();
            }

            Cash myCash = new Cash(this, (Cash) pCash);
            add(myCash);
            return myCash;
        }

        @Override
        public Cash addNewItem() {
            Cash myCash = new Cash(this);
            add(myCash);
            return myCash;
        }

        @Override
        public Cash addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the deposit */
            Cash myCash = new Cash(this, pValues);

            /* Check that this CashId has not been previously added */
            if (!isIdUnique(myCash.getId())) {
                myCash.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCash, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myCash);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    DataValues<MoneyWiseDataType> myValues = myItem.getValues(myCash);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myCash;
        }
    }
}
