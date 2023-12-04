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
package net.sourceforge.joceanus.jmoneywise.atlas.data.basic;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCashCategory.MoneyWiseCashCategoryList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCashInfo.MoneyWiseCashInfoList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataMapItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues.PrometheusInfoItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues.PrometheusInfoSetItem;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusUpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Cash class.
 */
public class MoneyWiseCash
        extends MoneyWiseAssetBase
        implements PrometheusInfoSetItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.CASH.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.CASH.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseCash> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseCash.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME, MoneyWiseCash::getInfoSet);
    }

    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseBasicResource.CASH_NEWACCOUNT.getValue();

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
    private final MoneyWiseCashInfoSet theInfoSet;

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCash The Cash to copy
     */
    protected MoneyWiseCash(final MoneyWiseCashList pList,
                            final MoneyWiseCash pCash) {
        /* Set standard values */
        super(pList, pCash);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new MoneyWiseCashInfoSet(this, pList.getActInfoTypes(), pList.getCashInfo());
                theInfoSet.cloneDataInfoSet(pCash.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new MoneyWiseCashInfoSet(this, pList.getActInfoTypes(), pList.getCashInfo());
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
    private MoneyWiseCash(final MoneyWiseCashList pList,
                          final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Create the InfoSet */
        theInfoSet = new MoneyWiseCashInfoSet(this, pList.getActInfoTypes(), pList.getCashInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public MoneyWiseCash(final MoneyWiseCashList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new MoneyWiseCashInfoSet(this, pList.getActInfoTypes(), pList.getCashInfo());
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

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public Long getExternalId() {
        return MoneyWiseAssetType.createExternalId(isAutoExpense() ? MoneyWiseAssetType.AUTOEXPENSE : MoneyWiseAssetType.CASH, getIndexedId());
    }

    @Override
    public MoneyWiseCashInfoSet getInfoSet() {
        return theInfoSet;
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

    /**
     * Obtain AutoExpense.
     * @return the autoExpense category
     */
    public MoneyWiseTransCategory getAutoExpense() {
        return hasInfoSet
                ? theInfoSet.getEventCategory(MoneyWiseAccountInfoClass.AUTOEXPENSE)
                : null;
    }

    /**
     * Obtain AutoPayee.
     * @return the autoExpense category
     */
    public MoneyWisePayee getAutoPayee() {
        return hasInfoSet
                ? theInfoSet.getPayee(MoneyWiseAccountInfoClass.AUTOPAYEE)
                : null;
    }

    /**
     * Obtain Opening Balance.
     * @return the Opening balance
     */
    public TethysMoney getOpeningBalance() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.OPENINGBALANCE, TethysMoney.class)
                : null;
    }

    @Override
    public MoneyWiseCashCategory getCategory() {
        return getValues().getValue(MoneyWiseBasicResource.CATEGORY_NAME, MoneyWiseCashCategory.class);
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        final MoneyWiseCashCategory myCategory = getCategory();
        return myCategory == null
                ? null
                : myCategory.getIndexedId();
    }

    /**
     * Obtain CategoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        final MoneyWiseCashCategory myCategory = getCategory();
        return myCategory == null
                ? null
                : myCategory.getName();
    }

    /**
     * Obtain CashCategoryClass.
     * @return the categoryClass
     */
    public MoneyWiseCashCategoryClass getCategoryClass() {
        final MoneyWiseCashCategory myCategory = getCategory();
        return myCategory == null
                ? null
                : myCategory.getCategoryTypeClass();
    }

    @Override
    public Boolean isForeign() {
        final MoneyWiseCurrency myDefault = getDataSet().getDefaultCurrency();
        return !isAutoExpense() && !myDefault.equals(getAssetCurrency());
    }

    @Override
    public boolean isAutoExpense() {
        return MoneyWiseCashCategoryClass.AUTOEXPENSE.equals(getCategoryClass());
    }

    @Override
    public MoneyWiseAssetType getAssetType() {
        return isAutoExpense()
                ? MoneyWiseAssetType.AUTOEXPENSE
                : MoneyWiseAssetType.CASH;
    }

    @Override
    public MoneyWiseCash getBase() {
        return (MoneyWiseCash) super.getBase();
    }

    @Override
    public MoneyWiseCashList getList() {
        return (MoneyWiseCashList) super.getList();
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
        final MoneyWiseAccountInfoClass myClass = MoneyWiseCashInfoSet.getClassForField(pField);
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
     * Set defaults.
     * @param pUpdateSet the update set
     * @throws OceanusException on error
     */
    public void setDefaults(final PrometheusUpdateSet pUpdateSet) throws OceanusException {
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
    public void autoCorrect(final PrometheusUpdateSet pUpdateSet) throws OceanusException {
        /* autoCorrect the infoSet */
        theInfoSet.autoCorrect(pUpdateSet);
    }

    /**
     * Obtain default category for new cash account.
     * @return the default category
     */
    private MoneyWiseCashCategory getDefaultCategory() {
        /* loop through the categories */
        final MoneyWiseCashCategoryList myCategories = getDataSet().getCashCategories();
        final Iterator<MoneyWiseCashCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseCashCategory myCategory = myIterator.next();

            /* Ignore deleted categories */
            if (myCategory.isDeleted()) {
                continue;
            }

            /* If the category is not a parent */
            if (!myCategory.isCategoryClass(MoneyWiseCashCategoryClass.PARENT)) {
                return myCategory;
            }
        }

        /* Return no category */
        return null;
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Check the category and then the name */
        final MoneyWiseCash myThat = (MoneyWiseCash) pThat;
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
        resolveDataLink(MoneyWiseBasicResource.CATEGORY_NAME, myData.getCashCategories());
        resolveDataLink(MoneyWiseStaticDataType.CURRENCY, myData.getAccountCurrencies());
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
     * Set a new autoExpense.
     * @param pCategory the new autoExpense
     * @throws OceanusException on error
     */
    public void setAutoExpense(final MoneyWiseTransCategory pCategory) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.AUTOEXPENSE, pCategory);
    }

    /**
     * Set a new autoPayee.
     * @param pPayee the new autoPayee
     * @throws OceanusException on error
     */
    public void setAutoPayee(final MoneyWisePayee pPayee) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.AUTOPAYEE, pPayee);
    }

    /**
     * Set a new opening balance.
     * @param pBalance the new opening balance
     * @throws OceanusException on error
     */
    public void setOpeningBalance(final TethysMoney pBalance) throws OceanusException {
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
    public void touchUnderlyingItems() {
        /* touch the category and currency */
        getCategory().touchItem(this);
        getAssetCurrency().touchItem(this);

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    @Override
    public void touchOnUpdate() {
        /* Adjust infoSet update touches */
        theInfoSet.touchOnUpdate();
    }

    @Override
    public void validate() {
        final MoneyWisePayee myParent = getParent();
        final MoneyWiseCashCategory myCategory = getCategory();
        final MoneyWiseCurrency myCurrency = getAssetCurrency();

        /* Validate base components */
        super.validate();

        /* Category must be non-null */
        if (myCategory == null) {
            addError(ERROR_MISSING, MoneyWiseBasicResource.CATEGORY_NAME);
        } else if (myCategory.getCategoryTypeClass().isParentCategory()) {
            addError(ERROR_BADCATEGORY, MoneyWiseBasicResource.CATEGORY_NAME);
        }

        /* Parent must be null */
        if (myParent != null) {
            addError(ERROR_EXIST, MoneyWiseBasicResource.ASSET_PARENT);
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            addError(ERROR_MISSING, MoneyWiseStaticDataType.CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            addError(ERROR_DISABLED, MoneyWiseStaticDataType.CURRENCY);
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
    public boolean applyChanges(final PrometheusDataItem pCash) {
        /* Can only update from a cash */
        if (!(pCash instanceof MoneyWiseCash)) {
            return false;
        }
        final MoneyWiseCash myCash = (MoneyWiseCash) pCash;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myCash);

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final MoneyWiseCashList myList = getList();
        final MoneyWiseCashDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Cash List class.
     */
    public static class MoneyWiseCashList
            extends MoneyWiseAssetBaseList<MoneyWiseCash> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseCashList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCashList.class);

        /**
         * The CashInfo List.
         */
        private MoneyWiseCashInfoList theInfoList;

        /**
         * The AccountInfoType list.
         */
        private MoneyWiseAccountInfoTypeList theInfoTypeList;

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        public MoneyWiseCashList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWiseCash.class, MoneyWiseBasicDataType.CASH);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseCashList(final MoneyWiseCashList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseCashList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseCash.FIELD_DEFS;
        }

        @Override
        protected MoneyWiseCashDataMap getDataMap() {
            return (MoneyWiseCashDataMap) super.getDataMap();
        }

        /**
         * Obtain the depositInfoList.
         * @return the deposit info list
         */
        public MoneyWiseCashInfoList getCashInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getCashInfo();
            }
            return theInfoList;
        }

        /**
         * Obtain the accountInfoTypeList.
         * @return the account info type list
         */
        public MoneyWiseAccountInfoTypeList getActInfoTypes() {
            if (theInfoTypeList == null) {
                theInfoTypeList = getDataSet().getActInfoTypes();
            }
            return theInfoTypeList;
        }

        @Override
        protected MoneyWiseCashList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseCashList myList = new MoneyWiseCashList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         */
        public MoneyWiseCashList deriveEditList(final PrometheusUpdateSet pUpdateSet) {
            /* Build an empty List */
            final MoneyWiseCashList myList = getEmptyList(PrometheusListStyle.EDIT);
            final MoneyWisePayeeList myPayees = pUpdateSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
            myList.ensureMap(myPayees);

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            final MoneyWiseCashInfoList myCashInfo = getCashInfo();
            myList.theInfoList = myCashInfo.getEmptyList(PrometheusListStyle.EDIT);

            /* Loop through the cash */
            final Iterator<MoneyWiseCash> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseCash myCurr = myIterator.next();

                /* Ignore deleted deposits */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked cash and add it to the list */
                final MoneyWiseCash myCash = new MoneyWiseCash(myList, myCurr);
                myList.add(myCash);

                /* Adjust the map */
                myCash.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        /**
         * Set map.
         * @param pMap the map
         */
        @Override
        protected void setDataMap(final PrometheusDataMapItem pMap) {
            super.setDataMap(pMap);
        }

        @Override
        public MoneyWiseCash findItemByName(final String pName) {
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
        public MoneyWiseCash addCopyItem(final PrometheusDataItem pCash) {
            /* Can only clone a Cash */
            if (!(pCash instanceof MoneyWiseCash)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseCash myCash = new MoneyWiseCash(this, (MoneyWiseCash) pCash);
            add(myCash);
            return myCash;
        }

        @Override
        public MoneyWiseCash addNewItem() {
            final MoneyWiseCash myCash = new MoneyWiseCash(this);
            add(myCash);
            return myCash;
        }

        @Override
        public MoneyWiseCash addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the deposit */
            final MoneyWiseCash myCash = new MoneyWiseCash(this, pValues);

            /* Check that this CashId has not been previously added */
            if (!isIdUnique(myCash.getIndexedId())) {
                myCash.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCash, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myCash);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<PrometheusInfoItem> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final PrometheusInfoItem myItem = myIterator.next();

                    /* Build info */
                    final PrometheusDataValues myValues = myItem.getValues(myCash);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myCash;
        }

        /**
         * Ensure Map based on the payee list.
         * @param pPayees the payee list
         */
        private void ensureMap(final MoneyWisePayeeList pPayees) {
            setDataMap(new MoneyWiseCashDataMap(pPayees));
        }

        @Override
        protected MoneyWiseCashDataMap allocateDataMap() {
            return new MoneyWiseCashDataMap(getDataSet().getPayees());
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
    protected static class MoneyWiseCashDataMap
            implements PrometheusDataMapItem, MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseCashDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCashDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_UNDERLYING, MoneyWiseCashDataMap::getUnderlyingMap);
        }

        /**
         * The assetMap.
         */
        private final MoneyWiseAssetDataMap theUnderlyingMap;

        /**
         * Constructor.
         * @param pPayees the payee list
         */
        protected MoneyWiseCashDataMap(final MoneyWisePayeeList pPayees) {
            theUnderlyingMap = pPayees.getDataMap().getUnderlyingMap();
        }

        @Override
        public MetisFieldSet<MoneyWiseCashDataMap> getDataFieldSet() {
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
        public MoneyWiseCash findItemByName(final String pName) {
            final MoneyWiseAssetBase myAsset = theUnderlyingMap.findAssetByName(pName);
            return myAsset instanceof MoneyWiseCash
                    ? (MoneyWiseCash) myAsset
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
