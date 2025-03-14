/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataEditState;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.data.MetisDataState;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory.MoneyWiseDepositCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfo.MoneyWiseDepositInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseLogicException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataMapItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues.PrometheusInfoItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues.PrometheusInfoSetItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Deposit class.
 */
public class MoneyWiseDeposit
        extends MoneyWiseAssetBase
        implements PrometheusInfoSetItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.DEPOSIT.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.DEPOSIT.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseDeposit> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseDeposit.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME, MoneyWiseDeposit::getInfoSet);
    }

    /**
     * New Account name.
     */
    public static final String NAME_NEWACCOUNT = MoneyWiseBasicResource.DEPOSIT_NEWACCOUNT.getValue();

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
    private final MoneyWiseDepositInfoSet theInfoSet;

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pDeposit The Deposit to copy
     */
    protected MoneyWiseDeposit(final MoneyWiseDepositList pList,
                               final MoneyWiseDeposit pDeposit) {
        /* Set standard values */
        super(pList, pDeposit);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new MoneyWiseDepositInfoSet(this, pList.getActInfoTypes(), pList.getDepositInfo());
                theInfoSet.cloneDataInfoSet(pDeposit.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new MoneyWiseDepositInfoSet(this, pList.getActInfoTypes(), pList.getDepositInfo());
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
    private MoneyWiseDeposit(final MoneyWiseDepositList pList,
                             final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Create the InfoSet */
        theInfoSet = new MoneyWiseDepositInfoSet(this, pList.getActInfoTypes(), pList.getDepositInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public MoneyWiseDeposit(final MoneyWiseDepositList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new MoneyWiseDepositInfoSet(this, pList.getActInfoTypes(), pList.getDepositInfo());
        hasInfoSet = true;
        useInfoSet = true;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseBasicResource.CATEGORY_NAME.equals(pField)) {
            return true;
        }
        if (MoneyWiseStaticDataType.CURRENCY.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.ASSET_PARENT.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public Long getExternalId() {
        return MoneyWiseAssetType.createExternalId(MoneyWiseAssetType.DEPOSIT, getIndexedId());
    }

    @Override
    public MoneyWiseDepositInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Maturity.
     * @return the maturity date
     */
    public OceanusDate getMaturity() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.MATURITY, OceanusDate.class)
                : null;
    }

    /**
     * Obtain SortCode.
     * @return the sort code
     */
    public char[] getSortCode() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.SORTCODE, char[].class)
                : null;
    }

    /**
     * Obtain Reference.
     * @return the reference
     */
    public char[] getReference() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.REFERENCE, char[].class)
                : null;
    }

    /**
     * Obtain Account.
     * @return the account
     */
    public char[] getAccount() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.ACCOUNT, char[].class)
                : null;
    }

    /**
     * Obtain Notes.
     * @return the notes
     */
    public char[] getNotes() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.NOTES, char[].class)
                : null;
    }

    @Override
    public OceanusMoney getOpeningBalance() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.OPENINGBALANCE, OceanusMoney.class)
                : null;
    }

    @Override
    public MoneyWiseDepositCategory getCategory() {
        return getValues().getValue(MoneyWiseBasicResource.CATEGORY_NAME, MoneyWiseDepositCategory.class);
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        final MoneyWiseDepositCategory myCategory = getCategory();
        return myCategory == null
                ? null
                : myCategory.getIndexedId();
    }

    /**
     * Obtain CategoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        final MoneyWiseDepositCategory myCategory = getCategory();
        return myCategory == null
                ? null
                : myCategory.getName();
    }

    /**
     * Obtain DepositCategoryClass.
     * @return the categoryClass
     */
    public MoneyWiseDepositCategoryClass getCategoryClass() {
        final MoneyWiseDepositCategory myCategory = getCategory();
        return myCategory == null
                ? null
                : myCategory.getCategoryTypeClass();
    }

    @Override
    public boolean isGross() {
        final MoneyWiseDepositCategory myCategory = getCategory();
        final MoneyWiseDepositCategoryClass myClass = myCategory == null ? null : myCategory.getCategoryTypeClass();
        return myClass != null && myClass.isGross();
    }

    @Override
    public boolean isTaxFree() {
        final MoneyWiseDepositCategory myCategory = getCategory();
        final MoneyWiseDepositCategoryClass myClass = myCategory == null ? null : myCategory.getCategoryTypeClass();
        return myClass != null && myClass.isTaxFree();
    }

    @Override
    public boolean isForeign() {
        final MoneyWiseCurrency myDefault = getDataSet().getReportingCurrency();
        return !myDefault.equals(getAssetCurrency());
    }

    @Override
    public MoneyWiseDeposit getBase() {
        return (MoneyWiseDeposit) super.getBase();
    }

    @Override
    public MoneyWiseDepositList getList() {
        return (MoneyWiseDepositList) super.getList();
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
    public MetisDataDifference fieldChanged(final MetisDataFieldId pField) {
        /* Handle InfoSet fields */
        final MoneyWiseAccountInfoClass myClass = MoneyWiseDepositInfoSet.getClassForField(pField);
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
    public boolean isDepositClass(final MoneyWiseDepositCategoryClass pClass) {
        /* Check for match */
        return getCategoryClass() == pClass;
    }

    /**
     * Set defaults.
     * @param pEditSet the edit set
     * @throws OceanusException on error
     */
    public void setDefaults(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Set values */
        setName(getList().getUniqueName(NAME_NEWACCOUNT));
        setCategory(getDefaultCategory());
        setAssetCurrency(getDataSet().getReportingCurrency());
        setClosed(Boolean.FALSE);
        autoCorrect(pEditSet);
    }

    /**
     * autoCorrect values after change.
     * @param pEditSet the update set
     * @throws OceanusException on error
     */
    public void autoCorrect(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Access category class */
        final MoneyWiseDepositCategoryClass myClass = getCategoryClass();
        final MoneyWisePayee myParent = getParent();

        /* Ensure that we have a valid parent */
        if ((myParent == null)
                || !myParent.getCategoryClass().canParentDeposit(myClass)) {
            setParent(getDefaultParent(pEditSet));
        }

        /* Adjust bond date if required */
        if (!MoneyWiseDepositCategoryClass.BOND.equals(myClass)) {
            setMaturity(null);
        } else if (getMaturity() == null) {
            final OceanusDate myDate = new OceanusDate();
            myDate.adjustYear(1);
            setMaturity(myDate);
        }
    }

    /**
     * Obtain default category for new deposit account.
     * @return the default category
     */
    private MoneyWiseDepositCategory getDefaultCategory() {
        /* loop through the categories */
        final MoneyWiseDepositCategoryList myCategories = getDataSet().getDepositCategories();
        final Iterator<MoneyWiseDepositCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseDepositCategory myCategory = myIterator.next();

            /* Ignore deleted categories */
            if (myCategory.isDeleted()) {
                continue;
            }

            /* If the category is not a parent */
            if (!myCategory.isCategoryClass(MoneyWiseDepositCategoryClass.PARENT)) {
                return myCategory;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default parent for new deposit.
     * @param pEditSet the edit set
     * @return the default parent
     */
    private MoneyWisePayee getDefaultParent(final PrometheusEditSet pEditSet) {
        /* Access details */
        final MoneyWisePayeeList myPayees = pEditSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
        final MoneyWiseDepositCategoryClass myClass = getCategoryClass();

        /* loop through the payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (myPayee.isDeleted() || Boolean.TRUE.equals(myPayee.isClosed())) {
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
    public int compareValues(final PrometheusDataItem pThat) {
        /* Check the category and then the name */
        final MoneyWiseDeposit myThat = (MoneyWiseDeposit) pThat;
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
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(MoneyWiseBasicResource.CATEGORY_NAME, myData.getDepositCategories());
        resolveDataLink(MoneyWiseStaticDataType.CURRENCY, myData.getAccountCurrencies());
        resolveDataLink(MoneyWiseBasicResource.ASSET_PARENT, myData.getPayees());
    }

    @Override
    protected void resolveEditSetLinks() throws OceanusException {
        /* Access the editSet */
        final PrometheusEditSet myEditSet = getList().getEditSet();

        /* Resolve Parent/Category/Currency if required */
        resolveDataLink(MoneyWiseBasicResource.ASSET_PARENT, myEditSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class));
        if (myEditSet.hasDataType(MoneyWiseBasicDataType.DEPOSITCATEGORY)) {
            resolveDataLink(MoneyWiseBasicResource.CATEGORY_NAME, myEditSet.getDataList(MoneyWiseBasicDataType.DEPOSITCATEGORY, MoneyWiseDepositCategoryList.class));
        }
        if (myEditSet.hasDataType(MoneyWiseStaticDataType.CURRENCY)) {
            resolveDataLink(MoneyWiseStaticDataType.CURRENCY, myEditSet.getDataList(MoneyWiseStaticDataType.CURRENCY, MoneyWiseCurrencyList.class));
        }

        /* Resolve links in infoSet */
        theInfoSet.resolveEditSetLinks(myEditSet);
    }

    /**
     * Set a new Maturity.
     * @param pMaturity the new maturity
     * @throws OceanusException on error
     */
    public void setMaturity(final OceanusDate pMaturity) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.MATURITY, pMaturity);
    }

    /**
     * Set a new SortCode.
     * @param pSortCode the new sort code
     * @throws OceanusException on error
     */
    public void setSortCode(final char[] pSortCode) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.SORTCODE, pSortCode);
    }

    /**
     * Set a new Account.
     * @param pAccount the new account
     * @throws OceanusException on error
     */
    public void setAccount(final char[] pAccount) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.ACCOUNT, pAccount);
    }

    /**
     * Set a new Reference.
     * @param pReference the new reference
     * @throws OceanusException on error
     */
    public void setReference(final char[] pReference) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.REFERENCE, pReference);
    }

    /**
     * Set a new Notes.
     * @param pNotes the new notes
     * @throws OceanusException on error
     */
    public void setNotes(final char[] pNotes) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.NOTES, pNotes);
    }

    /**
     * Set a new opening balance.
     * @param pBalance the new opening balance
     * @throws OceanusException on error
     */
    public void setOpeningBalance(final OceanusMoney pBalance) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.OPENINGBALANCE, pBalance);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws OceanusException on error
     */
    private void setInfoSetValue(final MoneyWiseAccountInfoClass pInfoClass,
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
        if (isDepositClass(MoneyWiseDepositCategoryClass.BOND) && getMaturity() == null) {
            /* Record a date for maturity */
            setMaturity(getCloseDate());
        }
    }

    @Override
    public MoneyWiseTransCategory getDetailedCategory(final MoneyWiseTransCategory pCategory,
                                                      final MoneyWiseTaxCredit pYear) {
        /* Switch on category type */
        final MoneyWiseTransCategoryList myCategories = getDataSet().getTransCategories();
        switch (pCategory.getCategoryTypeClass()) {
            case INTEREST:
                if (isTaxFree()) {
                    return myCategories.getSingularClass(MoneyWiseTransCategoryClass.TAXFREEINTEREST);
                }
                if (isDepositClass(MoneyWiseDepositCategoryClass.PEER2PEER)) {
                    return myCategories.getSingularClass(MoneyWiseTransCategoryClass.PEER2PEERINTEREST);
                }
                return myCategories.getSingularClass(isGross()
                        || !pYear.isTaxCreditRequired()
                        ? MoneyWiseTransCategoryClass.GROSSINTEREST
                        : MoneyWiseTransCategoryClass.TAXEDINTEREST);
            case LOYALTYBONUS:
                if (isTaxFree()) {
                    return myCategories.getSingularClass(MoneyWiseTransCategoryClass.TAXFREELOYALTYBONUS);
                }
                return myCategories.getSingularClass(isGross()
                        || !pYear.isTaxCreditRequired()
                        ? MoneyWiseTransCategoryClass.GROSSLOYALTYBONUS
                        : MoneyWiseTransCategoryClass.TAXEDLOYALTYBONUS);
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
        clearTouches(MoneyWiseBasicDataType.DEPOSITRATE);
        clearTouches(MoneyWiseBasicDataType.PORTFOLIO);

        /* Touch parent */
        getParent().touchItem(this);
    }

    /**
     * Update base deposit from an edited deposit.
     * @param pDeposit the edited deposit
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pDeposit) {
        /* Can only update from a deposit */
        if (!(pDeposit instanceof MoneyWiseDeposit)) {
            return false;
        }
        final MoneyWiseDeposit myDeposit = (MoneyWiseDeposit) pDeposit;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myDeposit);

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final MoneyWiseDepositList myList = getList();
        final MoneyWiseDepositDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    @Override
    public void removeItem() {
        theInfoSet.removeItems();
        super.removeItem();
    }

    /**
     * The Deposit List class.
     */
    public static class MoneyWiseDepositList
            extends MoneyWiseAssetBaseList<MoneyWiseDeposit> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseDepositList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositList.class);

        /**
         * The DepositInfo List.
         */
        private MoneyWiseDepositInfoList theInfoList;

        /**
         * The AccountInfoType list.
         */
        private MoneyWiseAccountInfoTypeList theInfoTypeList;

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        public MoneyWiseDepositList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWiseDeposit.class, MoneyWiseBasicDataType.DEPOSIT);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseDepositList(final MoneyWiseDepositList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseDepositList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseDeposit.FIELD_DEFS;
        }

        @Override
        protected MoneyWiseDepositDataMap getDataMap() {
            return (MoneyWiseDepositDataMap) super.getDataMap();
        }

        /**
         * Obtain the depositInfoList.
         * @return the deposit info list
         */
        public MoneyWiseDepositInfoList getDepositInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getDepositInfo();
            }
            return theInfoList;
        }

        /**
         * Obtain the accountInfoTypeList.
         * @return the account info type list
         */
        public MoneyWiseAccountInfoTypeList getActInfoTypes() {
            if (theInfoTypeList == null) {
                theInfoTypeList = getEditSet() == null
                        ? getDataSet().getActInfoTypes()
                        : getEditSet().getDataList(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, MoneyWiseAccountInfoTypeList.class);
            }
            return theInfoTypeList;
        }

        @Override
        protected MoneyWiseDepositList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseDepositList myList = new MoneyWiseDepositList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pEditSet the editSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public MoneyWiseDepositList deriveEditList(final PrometheusEditSet pEditSet) throws OceanusException {
            /* Build an empty List */
            final MoneyWiseDepositList myList = getEmptyList(PrometheusListStyle.EDIT);
            final MoneyWisePayeeList myPayees = pEditSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
            myList.ensureMap(myPayees);
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.DEPOSIT, myList);

            /* Store InfoType list */
            myList.theInfoTypeList = pEditSet.getDataList(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, MoneyWiseAccountInfoTypeList.class);

            /* Create info List */
            final MoneyWiseDepositInfoList myDepInfo = getDepositInfo();
            myList.theInfoList = myDepInfo.getEmptyList(PrometheusListStyle.EDIT);
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.DEPOSITINFO, myList.theInfoList);

            /* Store the editSet */
            myList.setEditSet(pEditSet);

            /* Loop through the deposits */
            final Iterator<MoneyWiseDeposit> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseDeposit myCurr = myIterator.next();

                /* Ignore deleted deposits */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked deposit and add it to the list */
                final MoneyWiseDeposit myDeposit = new MoneyWiseDeposit(myList, myCurr);
                myList.add(myDeposit);
                myDeposit.resolveEditSetLinks();

                /* Adjust the map */
                myDeposit.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        @Override
        public MoneyWiseDeposit findItemByName(final String pName) {
            /* look up the name in the map */
            return getDataMap().findItemByName(pName);
        }

        @Override
        protected boolean checkAvailableName(final String pName) {
            /* check availability in map */
            return getDataMap().availableName(pName);
        }

        @Override
        public boolean validNameCount(final String pName) {
            /* check availability in map */
            return getDataMap().validNameCount(pName);
        }

        @Override
        public MoneyWiseDeposit addCopyItem(final PrometheusDataItem pDeposit) {
            /* Can only clone a Deposit */
            if (!(pDeposit instanceof MoneyWiseDeposit)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseDeposit myDeposit = new MoneyWiseDeposit(this, (MoneyWiseDeposit) pDeposit);
            add(myDeposit);
            return myDeposit;
        }

        @Override
        public MoneyWiseDeposit addNewItem() {
            final MoneyWiseDeposit myDeposit = new MoneyWiseDeposit(this);
            add(myDeposit);
            return myDeposit;
        }

        @Override
        public MoneyWiseDeposit addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the deposit */
            final MoneyWiseDeposit myDeposit = new MoneyWiseDeposit(this, pValues);

            /* Check that this DepositId has not been previously added */
            if (!isIdUnique(myDeposit.getIndexedId())) {
                myDeposit.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myDeposit, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myDeposit);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<PrometheusInfoItem> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final PrometheusInfoItem myItem = myIterator.next();

                    /* Build info */
                    final PrometheusDataValues myValues = myItem.getValues(myDeposit);
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
        public MoneyWiseDeposit getDefaultHolding(final MoneyWisePayee pParent,
                                                  final boolean isTaxFree) {
            /* loop through the deposits */
            final Iterator<MoneyWiseDeposit> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseDeposit myDeposit = myIterator.next();

                /* Ignore deleted and closed deposits and wrong taxFree status */
                boolean bIgnore = myDeposit.isDeleted() || myDeposit.isClosed();
                bIgnore |= !pParent.equals(myDeposit.getParent());
                bIgnore |= !isTaxFree == myDeposit.isTaxFree();
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
        private void ensureMap(final MoneyWisePayeeList pPayees) {
            setDataMap(new MoneyWiseDepositDataMap(pPayees));
        }

        @Override
        protected MoneyWiseDepositDataMap allocateDataMap() {
            return new MoneyWiseDepositDataMap(getDataSet().getPayees());
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
    protected static class MoneyWiseDepositDataMap
            implements PrometheusDataMapItem, MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseDepositDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_UNDERLYING, MoneyWiseDepositDataMap::getUnderlyingMap);
        }

        /**
         * The assetMap.
         */
        private final MoneyWiseAssetDataMap theUnderlyingMap;

        /**
         * Constructor.
         * @param pPayees the payee list
         */
        protected MoneyWiseDepositDataMap(final MoneyWisePayeeList pPayees) {
            theUnderlyingMap = pPayees.getDataMap().getUnderlyingMap();
        }

        @Override
        public MetisFieldSet<MoneyWiseDepositDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain the underlying map.
         * @return the underlying map
         */
        private MoneyWiseAssetDataMap getUnderlyingMap() {
            return theUnderlyingMap;
        }

        @Override
        public void resetMap() {
            /* No action */
        }

        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Adjust name count */
            theUnderlyingMap.adjustForItem(pItem);
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public MoneyWiseDeposit findItemByName(final String pName) {
            final MoneyWiseAssetBase myAsset = theUnderlyingMap.findAssetByName(pName);
            return myAsset instanceof MoneyWiseDeposit
                    ? (MoneyWiseDeposit) myAsset
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
