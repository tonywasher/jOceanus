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
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Loan class.
 */
public class Loan
        extends AssetBase<Loan>
        implements InfoSetItem<MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.LOAN.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.LOAN.getListName();

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * LoanCategory Field Id.
     */
    public static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.LOANCATEGORY.getItemName());

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.ASSET_PARENT.getValue());

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
    private static final String NAME_NEWACCOUNT = MoneyWiseDataResource.LOAN_NEWACCOUNT.getValue();

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * LoanInfoSet.
     */
    private final LoanInfoSet theInfoSet;

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
            return true;
        }
        if (FIELD_PARENT.equals(pField)) {
            return true;
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
        AccountInfoClass myClass = LoanInfoSet.getClassForField(pField);
        if ((theInfoSet != null) && (myClass != null)) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public LoanInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain SortCode.
     * @return the sort code
     */
    public char[] getSortCode() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.SORTCODE, char[].class)
                         : null;
    }

    /**
     * Obtain Reference.
     * @return the reference
     */
    public char[] getReference() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.REFERENCE, char[].class)
                         : null;
    }

    /**
     * Obtain Account.
     * @return the account
     */
    public char[] getAccount() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.ACCOUNT, char[].class)
                         : null;
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
     * Obtain Opening Balance.
     * @return the Opening balance
     */
    public JMoney getOpeningBalance() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.OPENINGBALANCE, JMoney.class)
                         : null;
    }

    @Override
    public Payee getParent() {
        return getParent(getValueSet());
    }

    /**
     * Obtain ParentId.
     * @return the parentId
     */
    public Integer getParentId() {
        Payee myParent = getParent();
        return (myParent == null)
                                 ? null
                                 : myParent.getId();
    }

    /**
     * Obtain ParentName.
     * @return the parentName
     */
    public String getParentName() {
        Payee myParent = getParent();
        return (myParent == null)
                                 ? null
                                 : myParent.getName();
    }

    /**
     * Obtain LoanCategory.
     * @return the category
     */
    public LoanCategory getCategory() {
        return getCategory(getValueSet());
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        LoanCategory myCategory = getCategory();
        return (myCategory == null)
                                   ? null
                                   : myCategory.getId();
    }

    /**
     * Obtain CategoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        LoanCategory myCategory = getCategory();
        return (myCategory == null)
                                   ? null
                                   : myCategory.getName();
    }

    /**
     * Obtain AccountCategoryClass.
     * @return the actCategoryClass
     */
    public LoanCategoryClass getCategoryClass() {
        LoanCategory myCategory = getCategory();
        return (myCategory == null)
                                   ? null
                                   : myCategory.getCategoryTypeClass();
    }

    /**
     * Obtain Deposit Currency.
     * @return the currency
     */
    public AccountCurrency getLoanCurrency() {
        return getLoanCurrency(getValueSet());
    }

    /**
     * Obtain LoanCurrencyId.
     * @return the currencyId
     */
    public Integer getLoanCurrencyId() {
        AccountCurrency myCurrency = getLoanCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getId();
    }

    /**
     * Obtain LoanCurrencyName.
     * @return the currencyName
     */
    public String getLoanCurrencyName() {
        AccountCurrency myCurrency = getLoanCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getName();
    }

    /**
     * Obtain Parent.
     * @param pValueSet the valueSet
     * @return the Parent
     */
    public static Payee getParent(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, Payee.class);
    }

    /**
     * Obtain Loan Category.
     * @param pValueSet the valueSet
     * @return the LoanCategory
     */
    public static LoanCategory getCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATEGORY, LoanCategory.class);
    }

    /**
     * Obtain LoanCurrency.
     * @param pValueSet the valueSet
     * @return the LoanCurrency
     */
    public static AccountCurrency getLoanCurrency(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CURRENCY, AccountCurrency.class);
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
     * Set loan category value.
     * @param pValue the value
     */
    private void setValueCategory(final LoanCategory pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set loan category id.
     * @param pValue the value
     */
    private void setValueCategory(final Integer pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set loan category name.
     * @param pValue the value
     */
    private void setValueCategory(final String pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set loan currency value.
     * @param pValue the value
     */
    private void setValueCurrency(final AccountCurrency pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set loan currency id.
     * @param pValue the value
     */
    private void setValueCurrency(final Integer pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set loan currency name.
     * @param pValue the value
     */
    private void setValueCurrency(final String pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    @Override
    public Loan getBase() {
        return (Loan) super.getBase();
    }

    @Override
    public LoanList getList() {
        return (LoanList) super.getList();
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
        AccountInfoClass myClass = LoanInfoSet.getClassForField(pField);
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
     * Is this payee the required class.
     * @param pClass the required payee class.
     * @return true/false
     */
    public boolean isLoanClass(final LoanCategoryClass pClass) {
        /* Check for match */
        return getCategoryClass() == pClass;
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pLoan The Loan to copy
     */
    protected Loan(final LoanList pList,
                   final Loan pLoan) {
        /* Set standard values */
        super(pList, pLoan);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new LoanInfoSet(this, pList.getActInfoTypes(), pList.getLoanInfo());
                theInfoSet.cloneDataInfoSet(pLoan.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new LoanInfoSet(this, pList.getActInfoTypes(), pList.getLoanInfo());
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
    private Loan(final LoanList pList,
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
        } else if (myValue instanceof AccountCurrency) {
            setValueCurrency((AccountCurrency) myValue);
        }

        /* Create the InfoSet */
        theInfoSet = new LoanInfoSet(this, pList.getActInfoTypes(), pList.getLoanInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Loan(final LoanList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new LoanInfoSet(this, pList.getActInfoTypes(), pList.getLoanInfo());
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
        setName(getList().getUniqueName(NAME_NEWACCOUNT));
        setLoanCategory(getDefaultCategory());
        setLoanCurrency(getDataSet().getDefaultCurrency());
        setClosed(Boolean.FALSE);
        autoCorrect(pUpdateSet);
    }

    /**
     * adjust values after change.
     * @param pUpdateSet the update set
     * @throws JOceanusException on error
     */
    public void autoCorrect(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* Access category class and parent */
        LoanCategoryClass myClass = getCategoryClass();
        Payee myParent = getParent();

        /* Ensure that we have valid parent */
        if ((myParent == null)
            || !myParent.getPayeeTypeClass().canParentLoan(myClass)) {
            setParent(getDefaultParent(pUpdateSet));
        }
    }

    /**
     * Obtain default category for new loan account.
     * @return the default category
     */
    public LoanCategory getDefaultCategory() {
        /* loop through the categories */
        LoanCategoryList myCategories = getDataSet().getLoanCategories();
        Iterator<LoanCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            LoanCategory myCategory = myIterator.next();

            /* Ignore deleted categories */
            if (myCategory.isDeleted()) {
                continue;
            }

            /* If the category is not a parent */
            if (!myCategory.isCategoryClass(LoanCategoryClass.PARENT)) {
                return myCategory;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default parent for new loan.
     * @param pUpdateSet the update set
     * @return the default parent
     */
    private Payee getDefaultParent(final UpdateSet<MoneyWiseDataType> pUpdateSet) {
        /* Access details */
        PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        LoanCategoryClass myClass = getCategoryClass();

        /* loop through the payees */
        Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            Payee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (myPayee.isDeleted() || myPayee.isClosed()) {
                continue;
            }

            /* If the payee can parent */
            if (myPayee.getPayeeTypeClass().canParentLoan(myClass)) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }

    @Override
    public int compareTo(final Loan pThat) {
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
        resolveDataLink(FIELD_CATEGORY, myData.getLoanCategories());
        resolveDataLink(FIELD_CURRENCY, myData.getAccountCurrencies());
        resolveDataLink(FIELD_PARENT, myData.getPayees());
    }

    @Override
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* Resolve parent within list */
        PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        resolveDataLink(FIELD_PARENT, myPayees);
    }

    /**
     * Set a new loan category.
     * @param pCategory the new category
     */
    public void setLoanCategory(final LoanCategory pCategory) {
        setValueCategory(pCategory);
    }

    /**
     * Set a new loan currency.
     * @param pCurrency the new currency
     */
    public void setLoanCurrency(final AccountCurrency pCurrency) {
        setValueCurrency(pCurrency);
    }

    /**
     * Set a new parent.
     * @param pParent the parent
     * @throws JOceanusException on error
     */
    public void setParent(final Payee pParent) throws JOceanusException {
        setValueParent(pParent);
    }

    /**
     * Set a new SortCode.
     * @param pSortCode the new sort code
     * @throws JOceanusException on error
     */
    public void setSortCode(final char[] pSortCode) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.SORTCODE, pSortCode);
    }

    /**
     * Set a new Account.
     * @param pAccount the new account
     * @throws JOceanusException on error
     */
    public void setAccount(final char[] pAccount) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.ACCOUNT, pAccount);
    }

    /**
     * Set a new Reference.
     * @param pReference the new reference
     * @throws JOceanusException on error
     */
    public void setReference(final char[] pReference) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.REFERENCE, pReference);
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
     * Set a new opening balance.
     * @param pBalance the new opening balance
     * @throws JOceanusException on error
     */
    public void setOpeningBalance(final JMoney pBalance) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.OPENINGBALANCE, pBalance);
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
        getLoanCurrency().touchItem(this);

        /* Touch parent */
        getParent().touchItem(this);

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    @Override
    public void touchOnUpdate() {
        /* Touch parent */
        getParent().touchItem(this);
    }

    @Override
    public void validate() {
        Payee myParent = getParent();
        LoanCategory myCategory = getCategory();
        AccountCurrency myCurrency = getLoanCurrency();
        LoanCategoryClass myClass = getCategoryClass();

        /* Validate base components */
        super.validate();

        /* Category must be non-null */
        if (myCategory == null) {
            addError(ERROR_MISSING, FIELD_CATEGORY);
        } else if (myCategory.getCategoryTypeClass().isParentCategory()) {
            addError(ERROR_BADCATEGORY, FIELD_CATEGORY);
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            addError(ERROR_MISSING, FIELD_CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_CURRENCY);
        }

        /* Loan must be a child */
        if (!myClass.isChild()) {
            addError(ERROR_EXIST, FIELD_PARENT);

            /* Must have parent */
        } else if (myParent == null) {
            addError(ERROR_MISSING, FIELD_PARENT);
        } else {
            /* Parent must be suitable */
            if (!myParent.getPayeeTypeClass().canParentLoan(myClass)) {
                addError(ERROR_BADPARENT, FIELD_PARENT);
            }

            /* If we are open then parent must be open */
            if (!isClosed() && myParent.isClosed()) {
                addError(ERROR_PARCLOSED, FIELD_CLOSED);
            }
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
     * Update base loan from an edited loan.
     * @param pLoan the edited loan
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pLoan) {
        /* Can only update from a loan */
        if (!(pLoan instanceof Loan)) {
            return false;
        }
        Loan myLoan = (Loan) pLoan;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myLoan);

        /* Update the category if required */
        if (!Difference.isEqual(getCategory(), myLoan.getCategory())) {
            setValueCategory(myLoan.getCategory());
        }

        /* Update the parent if required */
        if (!Difference.isEqual(getParent(), myLoan.getParent())) {
            setValueParent(myLoan.getParent());
        }

        /* Update the deposit currency if required */
        if (!Difference.isEqual(getLoanCurrency(), myLoan.getLoanCurrency())) {
            setValueCurrency(myLoan.getLoanCurrency());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        LoanList myList = getList();
        LoanDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Loan List class.
     */
    public static class LoanList
            extends AssetBaseList<Loan> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The LoanInfo List.
         */
        private LoanInfoList theInfoList = null;

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
            return Loan.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        protected LoanDataMap getDataMap() {
            return (LoanDataMap) super.getDataMap();
        }

        /**
         * Obtain the depositInfoList.
         * @return the deposit info list
         */
        public LoanInfoList getLoanInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getLoanInfo();
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
        public LoanList(final MoneyWiseData pData) {
            super(pData, Loan.class, MoneyWiseDataType.LOAN);
        }

        @Override
        protected LoanList getEmptyList(final ListStyle pStyle) {
            LoanList myList = new LoanList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected LoanList(final LoanList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws JOceanusException on error
         */
        public LoanList deriveEditList(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
            /* Build an empty List */
            LoanList myList = getEmptyList(ListStyle.EDIT);
            DepositList myDeposits = pUpdateSet.getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);
            myList.ensureMap(myDeposits);

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            LoanInfoList myDepInfo = getLoanInfo();
            myList.theInfoList = myDepInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the loans */
            Iterator<Loan> myIterator = iterator();
            while (myIterator.hasNext()) {
                Loan myCurr = myIterator.next();

                /* Ignore deleted deposits */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked loan and add it to the list */
                Loan myLoan = new Loan(myList, myCurr);
                myLoan.resolveUpdateSetLinks(pUpdateSet);
                myList.append(myLoan);

                /* Adjust the map */
                myLoan.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        @Override
        public Loan findItemByName(final String pName) {
            /* look up the name in the map */
            return getDataMap().findItemByName(pName);
        }

        @Override
        protected boolean checkAvailableName(final String pName) {
            /* check availability in map */
            return getDataMap().availableName(pName);
        }

        @Override
        protected boolean validNameCount(final String pName) {
            /* check availability in map */
            return getDataMap().validNameCount(pName);
        }

        @Override
        public Loan addCopyItem(final DataItem<?> pLoan) {
            /* Can only clone a Loan */
            if (!(pLoan instanceof Loan)) {
                throw new UnsupportedOperationException();
            }

            Loan myLoan = new Loan(this, (Loan) pLoan);
            add(myLoan);
            return myLoan;
        }

        @Override
        public Loan addNewItem() {
            Loan myLoan = new Loan(this);
            add(myLoan);
            return myLoan;
        }

        @Override
        public Loan addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the loan */
            Loan myLoan = new Loan(this, pValues);

            /* Check that this LoanId has not been previously added */
            if (!isIdUnique(myLoan.getId())) {
                myLoan.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myLoan, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myLoan);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    DataValues<MoneyWiseDataType> myValues = myItem.getValues(myLoan);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myLoan;
        }

        /**
         * Ensure Map based on the deposit list.
         * @param pDeposits the deposit list
         */
        private void ensureMap(final DepositList pDeposits) {
            setDataMap(new LoanDataMap(pDeposits));
        }

        @Override
        protected LoanDataMap allocateDataMap() {
            return new LoanDataMap(getDataSet().getDeposits());
        }
    }

    /**
     * The dataMap class.
     */
    protected static class LoanDataMap
            implements DataMapItem<Loan, MoneyWiseDataType>, JDataContents {
        /**
         * Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(PrometheusDataResource.DATAMAP_NAME.getValue());

        /**
         * UnderlyingMap Field Id.
         */
        public static final JDataField FIELD_UNDERLYINGMAP = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_MAP_UNDERLYING
                .getValue());

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_UNDERLYINGMAP.equals(pField)) {
                return theUnderlyingMap;
            }

            /* Unknown */
            return JDataFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        /**
         * The assetMap.
         */
        private AssetDataMap theUnderlyingMap;

        /**
         * Constructor.
         * @param pDeposits the deposits list
         */
        protected LoanDataMap(final DepositList pDeposits) {
            theUnderlyingMap = pDeposits.getDataMap().getUnderlyingMap();
        }

        @Override
        public void resetMap() {
            /* No action */
        }

        @Override
        public void adjustForItem(final Loan pItem) {
            /* Adjust name count */
            theUnderlyingMap.adjustForItem(pItem);
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public Loan findItemByName(final String pName) {
            AssetBase<?> myAsset = theUnderlyingMap.findAssetByName(pName);
            return myAsset instanceof Loan
                                          ? (Loan) myAsset
                                          : null;
        }

        /**
         * Check validity of name.
         * @param pName the name to look up
         * @return true/false
         */
        public boolean validNameCount(final String pName) {
            return theUnderlyingMap.validNameCount(pName);
        }

        /**
         * Check availability of name.
         * @param pName the key to look up
         * @return true/false
         */
        public boolean availableName(final String pName) {
            return theUnderlyingMap.availableKey(pName);
        }
    }
}
