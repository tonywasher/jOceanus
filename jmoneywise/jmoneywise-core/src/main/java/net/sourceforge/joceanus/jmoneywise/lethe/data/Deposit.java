/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositInfo.DepositInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Deposit class.
 */
public class Deposit
        extends AssetBase<Deposit, DepositCategory>
        implements InfoSetItem<MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.DEPOSIT.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.DEPOSIT.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * DepositInfoSet field Id.
     */
    private static final MetisLetheField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseDataResource.DEPOSIT_NEWACCOUNT.getValue();

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * DepositInfoSet.
     */
    private final DepositInfoSet theInfoSet;

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pDeposit The Deposit to copy
     */
    protected Deposit(final DepositList pList,
                      final Deposit pDeposit) {
        /* Set standard values */
        super(pList, pDeposit);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new DepositInfoSet(this, pList.getActInfoTypes(), pList.getDepositInfo());
                theInfoSet.cloneDataInfoSet(pDeposit.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new DepositInfoSet(this, pList.getActInfoTypes(), pList.getDepositInfo());
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
    private Deposit(final DepositList pList,
                    final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Create the InfoSet */
        theInfoSet = new DepositInfoSet(this, pList.getActInfoTypes(), pList.getDepositInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Deposit(final DepositList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new DepositInfoSet(this, pList.getActInfoTypes(), pList.getDepositInfo());
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
        final AccountInfoClass myClass = DepositInfoSet.getClassForField(pField);
        if (theInfoSet != null
            && myClass != null) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public DepositInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Maturity.
     * @return the maturity date
     */
    public TethysDate getMaturity() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.MATURITY, TethysDate.class)
                          : null;
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
    public DepositCategory getCategory() {
        return getCategory(getValueSet());
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        final DepositCategory myCategory = getCategory();
        return myCategory == null
                                  ? null
                                  : myCategory.getId();
    }

    /**
     * Obtain CategoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        final DepositCategory myCategory = getCategory();
        return myCategory == null
                                  ? null
                                  : myCategory.getName();
    }

    /**
     * Obtain DepositCategoryClass.
     * @return the categoryClass
     */
    public DepositCategoryClass getCategoryClass() {
        final DepositCategory myCategory = getCategory();
        return myCategory == null
                                  ? null
                                  : myCategory.getCategoryTypeClass();
    }

    @Override
    public Boolean isGross() {
        final DepositCategory myCategory = getCategory();
        return myCategory == null
                                  ? Boolean.FALSE
                                  : myCategory.getCategoryTypeClass().isGross();
    }

    @Override
    public Boolean isTaxFree() {
        final DepositCategory myCategory = getCategory();
        return myCategory == null
                                  ? Boolean.FALSE
                                  : myCategory.getCategoryTypeClass().isTaxFree();
    }

    @Override
    public Boolean isForeign() {
        final AssetCurrency myDefault = getDataSet().getDefaultCurrency();
        return !myDefault.equals(getAssetCurrency());
    }

    /**
     * Obtain Deposit Category.
     * @param pValueSet the valueSet
     * @return the Deposit Category
     */
    public static DepositCategory getCategory(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATEGORY, DepositCategory.class);
    }

    @Override
    public Deposit getBase() {
        return (Deposit) super.getBase();
    }

    @Override
    public DepositList getList() {
        return (DepositList) super.getList();
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
        final AccountInfoClass myClass = DepositInfoSet.getClassForField(pField);
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
     * Is this deposit the required class.
     * @param pClass the required deposit class.
     * @return true/false
     */
    public boolean isDepositClass(final DepositCategoryClass pClass) {
        /* Check for match */
        return getCategoryClass() == pClass;
    }

    /**
     * Set defaults.
     * @param pUpdateSet the update set
     * @throws OceanusException on error
     */
    public void setDefaults(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* Set values */
        setName(getList().getUniqueName(NAME_NEWACCOUNT));
        setCategory(getDefaultCategory());
        setAssetCurrency(getDataSet().getDefaultCurrency());
        setClosed(Boolean.FALSE);
        autoCorrect(pUpdateSet);
    }

    /**
     * autoCorrect values after change.
     * @param pUpdateSet the update set
     * @throws OceanusException on error
     */
    public void autoCorrect(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* Access category class */
        final DepositCategoryClass myClass = getCategoryClass();
        final Payee myParent = getParent();

        /* Ensure that we have a valid parent */
        if ((myParent == null)
            || !myParent.getCategoryClass().canParentDeposit(myClass)) {
            setParent(getDefaultParent(pUpdateSet));
        }

        /* Adjust bond date if required */
        if (!DepositCategoryClass.BOND.equals(myClass)) {
            setMaturity(null);
        } else if (getMaturity() == null) {
            final TethysDate myDate = new TethysDate();
            myDate.adjustYear(1);
            setMaturity(myDate);
        }
    }

    /**
     * Obtain default category for new deposit account.
     * @return the default category
     */
    private DepositCategory getDefaultCategory() {
        /* loop through the categories */
        final DepositCategoryList myCategories = getDataSet().getDepositCategories();
        final Iterator<DepositCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final DepositCategory myCategory = myIterator.next();

            /* Ignore deleted categories */
            if (myCategory.isDeleted()) {
                continue;
            }

            /* If the category is not a parent */
            if (!myCategory.isCategoryClass(DepositCategoryClass.PARENT)) {
                return myCategory;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default parent for new deposit.
     * @param pUpdateSet the update set
     * @return the default parent
     */
    private Payee getDefaultParent(final UpdateSet<MoneyWiseDataType> pUpdateSet) {
        /* Access details */
        final PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        final DepositCategoryClass myClass = getCategoryClass();

        /* loop through the payees */
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (myPayee.isDeleted() || myPayee.isClosed()) {
                continue;
            }

            /* If the payee can parent */
            if (myPayee.getCategoryClass().canParentDeposit(myClass)) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
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

        /* Compare types of asset */
        int iDiff = super.compareTo(pThat);
        if (iDiff == 0
            && pThat instanceof Deposit) {
            /* Check the category */
            final Deposit myThat = (Deposit) pThat;
            iDiff = MetisDataDifference.compareObject(getCategory(), myThat.getCategory());
            if (iDiff == 0) {
                /* Check the underlying base */
                iDiff = super.compareAsset(myThat);
            }
        }

        /* Return the result */
        return iDiff;
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_CATEGORY, myData.getDepositCategories());
        resolveDataLink(FIELD_CURRENCY, myData.getAccountCurrencies());
        resolveDataLink(FIELD_PARENT, myData.getPayees());
    }

    @Override
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* Resolve parent within list */
        final PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        resolveDataLink(FIELD_PARENT, myPayees);
    }

    /**
     * Set a new Maturity.
     * @param pMaturity the new maturity
     * @throws OceanusException on error
     */
    public void setMaturity(final TethysDate pMaturity) throws OceanusException {
        setInfoSetValue(AccountInfoClass.MATURITY, pMaturity);
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
    public void adjustClosed() throws OceanusException {
        /* Adjust closed date */
        super.adjustClosed();

        /* If the maturity is null for a bond set it to close date */
        if (isDepositClass(DepositCategoryClass.BOND) && getMaturity() == null) {
            /* Record a date for maturity */
            setMaturity(getCloseDate());
        }
    }

    @Override
    public TransactionCategory getDetailedCategory(final TransactionCategory pCategory,
                                                   final MoneyWiseTaxCredit pYear) {
        /* Switch on category type */
        final TransactionCategoryList myCategories = getDataSet().getTransCategories();
        switch (pCategory.getCategoryTypeClass()) {
            case INTEREST:
                if (isTaxFree()) {
                    return myCategories.getSingularClass(TransactionCategoryClass.TAXFREEINTEREST);
                }
                if (isDepositClass(DepositCategoryClass.PEER2PEER)) {
                    return myCategories.getSingularClass(TransactionCategoryClass.PEER2PEERINTEREST);
                }
                return myCategories.getSingularClass(isGross()
                                                     || !pYear.isTaxCreditRequired()
                                                                                     ? TransactionCategoryClass.GROSSINTEREST
                                                                                     : TransactionCategoryClass.TAXEDINTEREST);
            case LOYALTYBONUS:
                if (isTaxFree()) {
                    return myCategories.getSingularClass(TransactionCategoryClass.TAXFREELOYALTYBONUS);
                }
                return myCategories.getSingularClass(isGross()
                                                     || !pYear.isTaxCreditRequired()
                                                                                     ? TransactionCategoryClass.GROSSLOYALTYBONUS
                                                                                     : TransactionCategoryClass.TAXEDLOYALTYBONUS);
            default:
                return pCategory;
        }
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
        /* Reset touches from update set */
        clearTouches(MoneyWiseDataType.DEPOSITRATE);
        clearTouches(MoneyWiseDataType.PORTFOLIO);

        /* Touch parent */
        getParent().touchItem(this);
    }

    @Override
    public void validate() {
        final Payee myParent = getParent();
        final DepositCategory myCategory = getCategory();
        final AssetCurrency myCurrency = getAssetCurrency();
        final DepositCategoryClass myClass = getCategoryClass();

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

        /* Deposit must be a child */
        if (!myClass.isChild()) {
            addError(ERROR_EXIST, FIELD_PARENT);

            /* Must have parent */
        } else if (myParent == null) {
            addError(ERROR_MISSING, FIELD_PARENT);
        } else {
            /* Parent must be suitable */
            if (!myParent.getCategoryClass().canParentDeposit(myClass)) {
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
     * Update base deposit from an edited deposit.
     * @param pDeposit the edited deposit
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pDeposit) {
        /* Can only update from a deposit */
        if (!(pDeposit instanceof Deposit)) {
            return false;
        }
        final Deposit myDeposit = (Deposit) pDeposit;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myDeposit);

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final DepositList myList = getList();
        final DepositDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Deposit List class.
     */
    public static class DepositList
            extends AssetBaseList<Deposit, DepositCategory> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DepositList> FIELD_DEFS = MetisFieldSet.newFieldSet(DepositList.class);

        /**
         * The DepositInfo List.
         */
        private DepositInfoList theInfoList;

        /**
         * The AccountInfoType list.
         */
        private AccountInfoTypeList theInfoTypeList;

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        public DepositList(final MoneyWiseData pData) {
            super(pData, Deposit.class, MoneyWiseDataType.DEPOSIT);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected DepositList(final DepositList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<DepositList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return Deposit.FIELD_DEFS;
        }

        @Override
        protected DepositDataMap getDataMap() {
            return (DepositDataMap) super.getDataMap();
        }

        /**
         * Obtain the depositInfoList.
         * @return the deposit info list
         */
        public DepositInfoList getDepositInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getDepositInfo();
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
        protected DepositList getEmptyList(final ListStyle pStyle) {
            final DepositList myList = new DepositList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public DepositList deriveEditList(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
            /* Build an empty List */
            final DepositList myList = getEmptyList(ListStyle.EDIT);
            final PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
            myList.ensureMap(myPayees);

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            final DepositInfoList myDepInfo = getDepositInfo();
            myList.theInfoList = myDepInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the deposits */
            final Iterator<Deposit> myIterator = iterator();
            while (myIterator.hasNext()) {
                final Deposit myCurr = myIterator.next();

                /* Ignore deleted deposits */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked deposit and add it to the list */
                final Deposit myDeposit = new Deposit(myList, myCurr);
                myDeposit.resolveUpdateSetLinks(pUpdateSet);
                myList.add(myDeposit);

                /* Adjust the map */
                myDeposit.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        @Override
        public Deposit findItemByName(final String pName) {
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
        public Deposit addCopyItem(final DataItem<?> pDeposit) {
            /* Can only clone a Deposit */
            if (!(pDeposit instanceof Deposit)) {
                throw new UnsupportedOperationException();
            }

            final Deposit myDeposit = new Deposit(this, (Deposit) pDeposit);
            add(myDeposit);
            return myDeposit;
        }

        @Override
        public Deposit addNewItem() {
            final Deposit myDeposit = new Deposit(this);
            add(myDeposit);
            return myDeposit;
        }

        @Override
        public Deposit addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the deposit */
            final Deposit myDeposit = new Deposit(this, pValues);

            /* Check that this DepositId has not been previously added */
            if (!isIdUnique(myDeposit.getId())) {
                myDeposit.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myDeposit, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myDeposit);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    final DataValues<MoneyWiseDataType> myValues = myItem.getValues(myDeposit);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myDeposit;
        }

        /**
         * Obtain default holding for portfolio.
         * @param pParent the parent
         * @param isTaxFree should holding be taxFree?
         * @return the default holding
         */
        public Deposit getDefaultHolding(final Payee pParent,
                                         final Boolean isTaxFree) {
            /* loop through the deposits */
            final Iterator<Deposit> myIterator = iterator();
            while (myIterator.hasNext()) {
                final Deposit myDeposit = myIterator.next();

                /* Ignore deleted and closed deposits and wrong taxFree status */
                boolean bIgnore = myDeposit.isDeleted() || myDeposit.isClosed();
                bIgnore |= !pParent.equals(myDeposit.getParent());
                bIgnore |= !isTaxFree.equals(myDeposit.isTaxFree());
                if (!bIgnore) {
                    return myDeposit;
                }
            }

            /* Return no deposit */
            return null;
        }

        /**
         * Ensure Map based on the payee list.
         * @param pPayees the payee list
         */
        private void ensureMap(final PayeeList pPayees) {
            setDataMap(new DepositDataMap(pPayees));
        }

        @Override
        protected DepositDataMap allocateDataMap() {
            return new DepositDataMap(getDataSet().getPayees());
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
    protected static class DepositDataMap
            implements DataMapItem<Deposit, MoneyWiseDataType>, MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DepositDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(DepositDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_MAP_UNDERLYING, DepositDataMap::getUnderlyingMap);
        }

        /**
         * The assetMap.
         */
        private AssetDataMap theUnderlyingMap;

        /**
         * Constructor.
         * @param pPayees the payee list
         */
        protected DepositDataMap(final PayeeList pPayees) {
            theUnderlyingMap = pPayees.getDataMap().getUnderlyingMap();
        }

        @Override
        public MetisFieldSet<DepositDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
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
        public void adjustForItem(final Deposit pItem) {
            /* Adjust name count */
            theUnderlyingMap.adjustForItem(pItem);
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public Deposit findItemByName(final String pName) {
            final AssetBase<?, ?> myAsset = theUnderlyingMap.findAssetByName(pName);
            return myAsset instanceof Deposit
                                              ? (Deposit) myAsset
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
