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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Loan class.
 */
public class Loan
        extends AssetBase
        implements InfoSetItem {
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
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * LoanInfoSet field Id.
     */
    private static final MetisLetheField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

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
     * @throws OceanusException on error
     */
    private Loan(final LoanList pList,
                 final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

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

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisLetheField pField) {
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
    public Object getFieldValue(final MetisLetheField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                              ? theInfoSet
                              : MetisDataFieldValue.SKIP;
        }

        /* Handle infoSet fields */
        final AccountInfoClass myClass = LoanInfoSet.getClassForField(pField);
        if (theInfoSet != null
            && myClass != null) {
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
    public TethysMoney getOpeningBalance() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.OPENINGBALANCE, TethysMoney.class)
                          : null;
    }

    @Override
    public LoanCategory getCategory() {
        return getCategory(getValueSet());
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        final LoanCategory myCategory = getCategory();
        return myCategory == null
                                  ? null
                                  : myCategory.getId();
    }

    /**
     * Obtain CategoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        final LoanCategory myCategory = getCategory();
        return myCategory == null
                                  ? null
                                  : myCategory.getName();
    }

    /**
     * Obtain AccountCategoryClass.
     * @return the actCategoryClass
     */
    public LoanCategoryClass getCategoryClass() {
        final LoanCategory myCategory = getCategory();
        return myCategory == null
                                  ? null
                                  : myCategory.getCategoryTypeClass();
    }

    @Override
    public Boolean isForeign() {
        final AssetCurrency myDefault = getDataSet().getDefaultCurrency();
        return !myDefault.equals(getAssetCurrency());
    }

    /**
     * Obtain Loan Category.
     * @param pValueSet the valueSet
     * @return the LoanCategory
     */
    public static LoanCategory getCategory(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATEGORY, LoanCategory.class);
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
    public MetisDataState getState() {
        /* Pop history for self */
        MetisDataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == MetisDataState.CLEAN) && useInfoSet) {
            /* Get state for infoSet */
            myState = theInfoSet.getState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public MetisDataEditState getEditState() {
        /* Pop history for self */
        MetisDataEditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if (myState == MetisDataEditState.CLEAN
            && useInfoSet) {
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
        if (!hasHistory && useInfoSet) {
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
    public MetisDataDifference fieldChanged(final MetisLetheField pField) {
        /* Handle InfoSet fields */
        final AccountInfoClass myClass = LoanInfoSet.getClassForField(pField);
        if (myClass != null) {
            return useInfoSet
                              ? theInfoSet.fieldChanged(myClass)
                              : MetisDataDifference.IDENTICAL;
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
     * Set defaults.
     * @param pUpdateSet the update set
     * @throws OceanusException on error
     */
    public void setDefaults(final UpdateSet pUpdateSet) throws OceanusException {
        /* Set values */
        setName(getList().getUniqueName(NAME_NEWACCOUNT));
        setCategory(getDefaultCategory());
        setAssetCurrency(getDataSet().getDefaultCurrency());
        setClosed(Boolean.FALSE);
        autoCorrect(pUpdateSet);
    }

    /**
     * adjust values after change.
     * @param pUpdateSet the update set
     * @throws OceanusException on error
     */
    public void autoCorrect(final UpdateSet pUpdateSet) throws OceanusException {
        /* Access category class and parent */
        final LoanCategoryClass myClass = getCategoryClass();
        final Payee myParent = getParent();

        /* Ensure that we have valid parent */
        if ((myParent == null)
            || !myParent.getCategoryClass().canParentLoan(myClass)) {
            setParent(getDefaultParent(pUpdateSet));
        }
    }

    /**
     * Obtain default category for new loan account.
     * @return the default category
     */
    public LoanCategory getDefaultCategory() {
        /* loop through the categories */
        final LoanCategoryList myCategories = getDataSet().getLoanCategories();
        final Iterator<LoanCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final LoanCategory myCategory = myIterator.next();

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
    private Payee getDefaultParent(final UpdateSet pUpdateSet) {
        /* Access details */
        final PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        final LoanCategoryClass myClass = getCategoryClass();

        /* loop through the payees */
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (myPayee.isDeleted() || myPayee.isClosed()) {
                continue;
            }

            /* If the payee can parent */
            if (myPayee.getCategoryClass().canParentLoan(myClass)) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }

    @Override
    public int compareValues(final DataItem pThat) {
        /* Check the category and then the name */
        final Loan myThat = (Loan) pThat;
        int iDiff = MetisDataDifference.compareObject(getCategory(), myThat.getCategory());
        if (iDiff == 0) {
            iDiff = MetisDataDifference.compareObject(getName(), myThat.getName());
        }
        return iDiff;
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_CATEGORY, myData.getLoanCategories());
        resolveDataLink(FIELD_CURRENCY, myData.getAccountCurrencies());
        resolveDataLink(FIELD_PARENT, myData.getPayees());
    }

    @Override
    protected void resolveUpdateSetLinks(final UpdateSet pUpdateSet) throws OceanusException {
        /* Resolve parent within list */
        final PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        resolveDataLink(FIELD_PARENT, myPayees);
    }

    /**
     * Set a new SortCode.
     * @param pSortCode the new sort code
     * @throws OceanusException on error
     */
    public void setSortCode(final char[] pSortCode) throws OceanusException {
        setInfoSetValue(AccountInfoClass.SORTCODE, pSortCode);
    }

    /**
     * Set a new Account.
     * @param pAccount the new account
     * @throws OceanusException on error
     */
    public void setAccount(final char[] pAccount) throws OceanusException {
        setInfoSetValue(AccountInfoClass.ACCOUNT, pAccount);
    }

    /**
     * Set a new Reference.
     * @param pReference the new reference
     * @throws OceanusException on error
     */
    public void setReference(final char[] pReference) throws OceanusException {
        setInfoSetValue(AccountInfoClass.REFERENCE, pReference);
    }

    /**
     * Set a new Notes.
     * @param pNotes the new notes
     * @throws OceanusException on error
     */
    public void setNotes(final char[] pNotes) throws OceanusException {
        setInfoSetValue(AccountInfoClass.NOTES, pNotes);
    }

    /**
     * Set a new opening balance.
     * @param pBalance the new opening balance
     * @throws OceanusException on error
     */
    public void setOpeningBalance(final TethysMoney pBalance) throws OceanusException {
        setInfoSetValue(AccountInfoClass.OPENINGBALANCE, pBalance);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws OceanusException on error
     */
    private void setInfoSetValue(final AccountInfoClass pInfoClass,
                                 final Object pValue) throws OceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new MoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Set the value */
        theInfoSet.setValue(pInfoClass, pValue);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the category and currency */
        getCategory().touchItem(this);
        getAssetCurrency().touchItem(this);

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
        final Payee myParent = getParent();
        final LoanCategory myCategory = getCategory();
        final AssetCurrency myCurrency = getAssetCurrency();
        final LoanCategoryClass myClass = getCategoryClass();

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
            if (!myParent.getCategoryClass().canParentLoan(myClass)) {
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
    public boolean applyChanges(final DataItem pLoan) {
        /* Can only update from a loan */
        if (!(pLoan instanceof Loan)) {
            return false;
        }
        final Loan myLoan = (Loan) pLoan;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myLoan);

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final LoanList myList = getList();
        final LoanDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Loan List class.
     */
    public static class LoanList
            extends AssetBaseList<Loan> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<LoanList> FIELD_DEFS = MetisFieldSet.newFieldSet(LoanList.class);

        /**
         * The LoanInfo List.
         */
        private LoanInfoList theInfoList;

        /**
         * The AccountInfoType list.
         */
        private AccountInfoTypeList theInfoTypeList;

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        public LoanList(final MoneyWiseData pData) {
            super(pData, Loan.class, MoneyWiseDataType.LOAN);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected LoanList(final LoanList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<LoanList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return Loan.FIELD_DEFS;
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

        @Override
        protected LoanList getEmptyList(final ListStyle pStyle) {
            final LoanList myList = new LoanList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public LoanList deriveEditList(final UpdateSet pUpdateSet) throws OceanusException {
            /* Build an empty List */
            final LoanList myList = getEmptyList(ListStyle.EDIT);
            final PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
            myList.ensureMap(myPayees);

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            final LoanInfoList myDepInfo = getLoanInfo();
            myList.theInfoList = myDepInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the loans */
            final Iterator<Loan> myIterator = iterator();
            while (myIterator.hasNext()) {
                final Loan myCurr = myIterator.next();

                /* Ignore deleted deposits */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked loan and add it to the list */
                final Loan myLoan = new Loan(myList, myCurr);
                myLoan.resolveUpdateSetLinks(pUpdateSet);
                myList.add(myLoan);

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
        public Loan addCopyItem(final DataItem pLoan) {
            /* Can only clone a Loan */
            if (!(pLoan instanceof Loan)) {
                throw new UnsupportedOperationException();
            }

            final Loan myLoan = new Loan(this, (Loan) pLoan);
            add(myLoan);
            return myLoan;
        }

        @Override
        public Loan addNewItem() {
            final Loan myLoan = new Loan(this);
            add(myLoan);
            return myLoan;
        }

        @Override
        public Loan addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the loan */
            final Loan myLoan = new Loan(this, pValues);

            /* Check that this LoanId has not been previously added */
            if (!isIdUnique(myLoan.getId())) {
                myLoan.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myLoan, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myLoan);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<InfoItem> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final InfoItem myItem = myIterator.next();

                    /* Build info */
                    final DataValues myValues = myItem.getValues(myLoan);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myLoan;
        }

        /**
         * Ensure Map based on the payee list.
         * @param pPayees the payee list
         */
        private void ensureMap(final PayeeList pPayees) {
            setDataMap(new LoanDataMap(pPayees));
        }

        @Override
        protected LoanDataMap allocateDataMap() {
            return new LoanDataMap(getDataSet().getPayees());
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Resolve links and sort the data */
            super.resolveDataSetLinks();
            reSort();
        }
    }

    /**
     * The dataMap class.
     */
    protected static class LoanDataMap
            implements DataMapItem, MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<LoanDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(LoanDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_MAP_UNDERLYING, LoanDataMap::getUnderlyingMap);
        }

        /**
         * The assetMap.
         */
        private AssetDataMap theUnderlyingMap;

        /**
         * Constructor.
         * @param pPayees the payee list
         */
        protected LoanDataMap(final PayeeList pPayees) {
            theUnderlyingMap = pPayees.getDataMap().getUnderlyingMap();
        }

        @Override
        public MetisFieldSet<LoanDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain the underlying map.
         * @return the underlying map
         */
        private AssetDataMap getUnderlyingMap() {
            return theUnderlyingMap;
        }

        @Override
        public void resetMap() {
            /* No action */
        }

        @Override
        public void adjustForItem(final DataItem pItem) {
            /* Adjust name count */
            theUnderlyingMap.adjustForItem(pItem);
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public Loan findItemByName(final String pName) {
            final AssetBase myAsset = theUnderlyingMap.findAssetByName(pName);
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
