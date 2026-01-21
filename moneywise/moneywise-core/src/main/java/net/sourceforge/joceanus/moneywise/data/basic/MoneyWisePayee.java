/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.data.basic;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.data.MetisDataEditState;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.data.MetisDataResource;
import io.github.tonywasher.joceanus.metis.data.MetisDataState;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayeeInfo.MoneyWisePayeeInfoList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeType.MoneyWisePayeeTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseLogicException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataInstanceMap;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataMapItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues.PrometheusInfoItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues.PrometheusInfoSetItem;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Payee class.
 */
public class MoneyWisePayee
        extends MoneyWiseAssetBase
        implements PrometheusInfoSetItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.PAYEE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.PAYEE.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWisePayee> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWisePayee.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME, MoneyWisePayee::getInfoSet);
        FIELD_DEFS.buildFieldMap(MoneyWiseAccountInfoClass.class, MoneyWisePayee::getFieldValue);
    }

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * PayeeInfoSet.
     */
    private final MoneyWisePayeeInfoSet theInfoSet;

    /**
     * Copy Constructor.
     *
     * @param pList  the list
     * @param pPayee The Payee to copy
     */
    protected MoneyWisePayee(final MoneyWisePayeeList pList,
                             final MoneyWisePayee pPayee) {
        /* Set standard values */
        super(pList, pPayee);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new MoneyWisePayeeInfoSet(this, pList.getActInfoTypes(), pList.getPayeeInfo());
                theInfoSet.cloneDataInfoSet(pPayee.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new MoneyWisePayeeInfoSet(this, pList.getActInfoTypes(), pList.getPayeeInfo());
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
     *
     * @param pList   the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWisePayee(final MoneyWisePayeeList pList,
                           final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Create the InfoSet */
        theInfoSet = new MoneyWisePayeeInfoSet(this, pList.getActInfoTypes(), pList.getPayeeInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     *
     * @param pList the list
     */
    public MoneyWisePayee(final MoneyWisePayeeList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new MoneyWisePayeeInfoSet(this, pList.getActInfoTypes(), pList.getPayeeInfo());
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

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public Long getExternalId() {
        return MoneyWiseAssetType.createExternalId(MoneyWiseAssetType.PAYEE, getIndexedId());
    }

    @Override
    public MoneyWisePayeeInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain fieldValue for infoSet.
     *
     * @param pFieldId the fieldId
     * @return the value
     */
    private Object getFieldValue(final MetisDataFieldId pFieldId) {
        return theInfoSet != null ? theInfoSet.getFieldValue(pFieldId) : null;
    }

    /**
     * Obtain WebSite.
     *
     * @return the webSite
     */
    public char[] getWebSite() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.WEBSITE, char[].class)
                : null;
    }

    /**
     * Obtain CustNo.
     *
     * @return the customer #
     */
    public char[] getCustNo() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.CUSTOMERNO, char[].class)
                : null;
    }

    /**
     * Obtain UserId.
     *
     * @return the userId
     */
    public char[] getUserId() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.USERID, char[].class)
                : null;
    }

    /**
     * Obtain Password.
     *
     * @return the password
     */
    public char[] getPassword() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.PASSWORD, char[].class)
                : null;
    }

    /**
     * Obtain SortCode.
     *
     * @return the sort code
     */
    public char[] getSortCode() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.SORTCODE, char[].class)
                : null;
    }

    /**
     * Obtain Reference.
     *
     * @return the reference
     */
    public char[] getReference() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.REFERENCE, char[].class)
                : null;
    }

    /**
     * Obtain Account.
     *
     * @return the account
     */
    public char[] getAccount() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.ACCOUNT, char[].class)
                : null;
    }

    /**
     * Obtain Notes.
     *
     * @return the notes
     */
    public char[] getNotes() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.NOTES, char[].class)
                : null;
    }

    @Override
    public MoneyWisePayeeType getCategory() {
        return getValues().getValue(MoneyWiseBasicResource.CATEGORY_NAME, MoneyWisePayeeType.class);
    }

    /**
     * Obtain categoryId.
     *
     * @return the categoryTypeId
     */
    public Integer getCategoryId() {
        final MoneyWisePayeeType myType = getCategory();
        return (myType == null)
                ? null
                : myType.getIndexedId();
    }

    /**
     * Obtain categoryName.
     *
     * @return the categoryName
     */
    public String getCategoryName() {
        final MoneyWisePayeeType myType = getCategory();
        return myType == null
                ? null
                : myType.getName();
    }

    /**
     * Obtain categoryClass.
     *
     * @return the categoryClass
     */
    public MoneyWisePayeeClass getCategoryClass() {
        final MoneyWisePayeeType myType = getCategory();
        return (myType == null)
                ? null
                : myType.getPayeeClass();
    }

    @Override
    public MoneyWisePayee getBase() {
        return (MoneyWisePayee) super.getBase();
    }

    @Override
    public MoneyWisePayeeList getList() {
        return (MoneyWisePayeeList) super.getList();
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
        final MoneyWiseAccountInfoClass myClass = MoneyWisePayeeInfoSet.getClassForField(pField);
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
     *
     * @param pClass the required payee class.
     * @return true/false
     */
    public boolean isPayeeClass(final MoneyWisePayeeClass pClass) {
        /* Check for match */
        return getCategoryClass() == pClass;
    }

    /**
     * Is the category hidden?
     *
     * @return true/false
     */
    @Override
    public boolean isHidden() {
        final MoneyWisePayeeClass myClass = this.getCategoryClass();
        return myClass != null
                && myClass.isHiddenType();
    }

    /**
     * Set defaults.
     *
     * @throws OceanusException on error
     */
    public void setDefaults() throws OceanusException {
        getList().getValidator().setDefaults(this);
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Check the category and then the name */
        final MoneyWisePayee myThat = (MoneyWisePayee) pThat;
        int iDiff = MetisDataDifference.compareObject(getCategory(), myThat.getCategory());
        if (iDiff == 0) {
            iDiff = MetisDataDifference.compareObject(getName(), myThat.getName());
        }
        return iDiff;
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Base details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(MoneyWiseBasicResource.CATEGORY_NAME, myData.getPayeeTypes());
    }

    @Override
    protected void resolveEditSetLinks() throws OceanusException {
        /* Access the editSet */
        final PrometheusEditSet myEditSet = getList().getEditSet();

        /* Resolve Parent/Category/Currency if required */
        if (myEditSet.hasDataType(MoneyWiseStaticDataType.PAYEETYPE)) {
            resolveDataLink(MoneyWiseBasicResource.CATEGORY_NAME, myEditSet.getDataList(MoneyWiseStaticDataType.PAYEETYPE, MoneyWisePayeeTypeList.class));
        }

        /* Resolve links in infoSet */
        theInfoSet.resolveEditSetLinks(myEditSet);
    }

    /**
     * Set a new WebSite.
     *
     * @param pWebSite the new webSite
     * @throws OceanusException on error
     */
    public void setWebSite(final char[] pWebSite) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.WEBSITE, pWebSite);
    }

    /**
     * Set a new CustNo.
     *
     * @param pCustNo the new custNo
     * @throws OceanusException on error
     */
    public void setCustNo(final char[] pCustNo) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.CUSTOMERNO, pCustNo);
    }

    /**
     * Set a new UserId.
     *
     * @param pUserId the new userId
     * @throws OceanusException on error
     */
    public void setUserId(final char[] pUserId) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.USERID, pUserId);
    }

    /**
     * Set a new Password.
     *
     * @param pPassword the new password
     * @throws OceanusException on error
     */
    public void setPassword(final char[] pPassword) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.PASSWORD, pPassword);
    }

    /**
     * Set a new SortCode.
     *
     * @param pSortCode the new sort code
     * @throws OceanusException on error
     */
    public void setSortCode(final char[] pSortCode) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.SORTCODE, pSortCode);
    }

    /**
     * Set a new Account.
     *
     * @param pAccount the new account
     * @throws OceanusException on error
     */
    public void setAccount(final char[] pAccount) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.ACCOUNT, pAccount);
    }

    /**
     * Set a new Reference.
     *
     * @param pReference the new reference
     * @throws OceanusException on error
     */
    public void setReference(final char[] pReference) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.REFERENCE, pReference);
    }

    /**
     * Set a new Notes.
     *
     * @param pNotes the new notes
     * @throws OceanusException on error
     */
    public void setNotes(final char[] pNotes) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.NOTES, pNotes);
    }

    /**
     * Set an infoSet value.
     *
     * @param pInfoClass the class of info to set
     * @param pValue     the value to set
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
        /* touch the payee type */
        getCategory().touchItem(this);

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    @Override
    public void touchOnUpdate() {
        /* Reset touches from update set */
        clearTouches(MoneyWiseBasicDataType.CASHINFO);
        clearTouches(MoneyWiseBasicDataType.SECURITY);
        clearTouches(MoneyWiseBasicDataType.DEPOSIT);
        clearTouches(MoneyWiseBasicDataType.LOAN);
        clearTouches(MoneyWiseBasicDataType.PORTFOLIO);
    }

    /**
     * Update base payee from an edited payee.
     *
     * @param pPayee the edited payee
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pPayee) {
        /* Can only update from a payee */
        if (!(pPayee instanceof MoneyWisePayee)) {
            return false;
        }
        final MoneyWisePayee myPayee = (MoneyWisePayee) pPayee;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myPayee);

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final MoneyWisePayeeList myList = getList();
        final MoneyWisePayeeDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    @Override
    public void removeItem() {
        theInfoSet.removeItems();
        super.removeItem();
    }

    /**
     * The Payee List class.
     */
    public static class MoneyWisePayeeList
            extends MoneyWiseAssetBaseList<MoneyWisePayee> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWisePayeeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePayeeList.class);

        /**
         * The PayeeInfo List.
         */
        private MoneyWisePayeeInfoList theInfoList;

        /**
         * The AccountInfoType list.
         */
        private MoneyWiseAccountInfoTypeList theInfoTypeList;

        /**
         * Construct an empty CORE Payee list.
         *
         * @param pData the DataSet for the list
         */
        public MoneyWisePayeeList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWisePayee.class, MoneyWiseBasicDataType.PAYEE);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        protected MoneyWisePayeeList(final MoneyWisePayeeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWisePayeeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWisePayee.FIELD_DEFS;
        }

        @Override
        public MoneyWisePayeeDataMap getDataMap() {
            return (MoneyWisePayeeDataMap) super.getDataMap();
        }

        /**
         * Obtain the payeeInfoList.
         *
         * @return the payee info list
         */
        public MoneyWisePayeeInfoList getPayeeInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getPayeeInfo();
            }
            return theInfoList;
        }

        /**
         * Obtain the accountInfoTypeList.
         *
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
        protected MoneyWisePayeeList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWisePayeeList myList = new MoneyWisePayeeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         *
         * @param pEditSet the editSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public MoneyWisePayeeList deriveEditList(final PrometheusEditSet pEditSet) throws OceanusException {
            /* Build an empty List */
            final MoneyWisePayeeList myList = getEmptyList(PrometheusListStyle.EDIT);
            myList.ensureMap();
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.PAYEE, myList);
            myList.getValidator().setEditSet(pEditSet);

            /* Store InfoType list */
            myList.theInfoTypeList = pEditSet.getDataList(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, MoneyWiseAccountInfoTypeList.class);

            /* Create info List */
            final MoneyWisePayeeInfoList myPayeeInfo = getPayeeInfo();
            myList.theInfoList = myPayeeInfo.getEmptyList(PrometheusListStyle.EDIT);
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.PAYEEINFO, myList.theInfoList);

            /* Store the editSet */
            myList.setEditSet(pEditSet);

            /* Loop through the payees */
            final Iterator<MoneyWisePayee> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWisePayee myCurr = myIterator.next();

                /* Ignore deleted payees */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked payee and add it to the list */
                final MoneyWisePayee myPayee = new MoneyWisePayee(myList, myCurr);
                myList.add(myPayee);
                myPayee.resolveEditSetLinks();

                /* Adjust the map */
                myPayee.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        @Override
        public MoneyWisePayee findItemByName(final String pName) {
            /* Look up in map */
            return getDataMap().findItemByName(pName);
        }

        @Override
        public boolean checkAvailableName(final String pName) {
            /* check availability */
            return findItemByName(pName) == null;
        }

        @Override
        public boolean validNameCount(final String pName) {
            /* check availability in map */
            return getDataMap().validNameCount(pName);
        }

        /**
         * Add a new item to the core list.
         *
         * @param pPayee item
         * @return the newly added item
         */
        @Override
        public MoneyWisePayee addCopyItem(final PrometheusDataItem pPayee) {
            /* Can only clone a Payee */
            if (!(pPayee instanceof MoneyWisePayee)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWisePayee myPayee = new MoneyWisePayee(this, (MoneyWisePayee) pPayee);
            add(myPayee);
            return myPayee;
        }

        /**
         * Add a new item to the edit list.
         *
         * @return the new item
         */
        @Override
        public MoneyWisePayee addNewItem() {
            final MoneyWisePayee myPayee = new MoneyWisePayee(this);
            add(myPayee);
            return myPayee;
        }

        /**
         * Obtain the first payee for the specified class.
         *
         * @param pClass the payee class
         * @return the payee
         */
        public MoneyWisePayee getSingularClass(final MoneyWisePayeeClass pClass) {
            /* Lookup in the map */
            return getDataMap().findSingularItem(pClass);
        }

        @Override
        public MoneyWisePayee addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the payee */
            final MoneyWisePayee myPayee = new MoneyWisePayee(this, pValues);

            /* Check that this PayeeId has not been previously added */
            if (!isIdUnique(myPayee.getIndexedId())) {
                myPayee.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myPayee, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myPayee);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<PrometheusInfoItem> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final PrometheusInfoItem myItem = myIterator.next();

                    /* Build info */
                    final PrometheusDataValues myValues = myItem.getValues(myPayee);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myPayee;
        }

        @Override
        protected MoneyWisePayeeDataMap allocateDataMap() {
            return new MoneyWisePayeeDataMap();
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
    public static class MoneyWisePayeeDataMap
            implements PrometheusDataMapItem, MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWisePayeeDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePayeeDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_UNDERLYING, MoneyWisePayeeDataMap::getUnderlyingMap);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_SINGULARMAP, MoneyWisePayeeDataMap::getPayeeMap);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_SINGULARCOUNTS, MoneyWisePayeeDataMap::getPayeeCountMap);
        }

        /**
         * The assetMap.
         */
        private final MoneyWiseAssetDataMap theUnderlyingMap;

        /**
         * Map of category counts.
         */
        private final Map<Integer, Integer> thePayeeCountMap;

        /**
         * Map of singular categories.
         */
        private final Map<Integer, MoneyWisePayee> thePayeeMap;

        /**
         * Constructor.
         */
        public MoneyWisePayeeDataMap() {
            /* Create underlying map */
            theUnderlyingMap = new MoneyWiseAssetDataMap();

            /* Create the maps */
            thePayeeCountMap = new HashMap<>();
            thePayeeMap = new HashMap<>();
        }

        @Override
        public MetisFieldSet<MoneyWisePayeeDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain the underlying map.
         *
         * @return the underlying map
         */
        public MoneyWiseAssetDataMap getUnderlyingMap() {
            return theUnderlyingMap;
        }

        /**
         * Obtain the underlying map.
         *
         * @return the underlying map
         */
        private Map<Integer, MoneyWisePayee> getPayeeMap() {
            return thePayeeMap;
        }

        /**
         * Obtain the underlying map.
         *
         * @return the underlying map
         */
        private Map<Integer, Integer> getPayeeCountMap() {
            return thePayeeCountMap;
        }

        @Override
        public void resetMap() {
            theUnderlyingMap.resetMap();
            thePayeeCountMap.clear();
            thePayeeMap.clear();
        }

        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Access item */
            final MoneyWisePayee myItem = (MoneyWisePayee) pItem;

            /* If the class is singular */
            final MoneyWisePayeeClass myClass = myItem.getCategoryClass();
            if (myClass.isSingular()) {
                /* Adjust category count */
                final Integer myId = myClass.getClassId();
                final Integer myCount = thePayeeCountMap.get(myId);
                if (myCount == null) {
                    thePayeeCountMap.put(myId, PrometheusDataInstanceMap.ONE);
                } else {
                    thePayeeCountMap.put(myId, myCount + 1);
                }

                /* Adjust payee map */
                thePayeeMap.put(myId, myItem);
            }

            /* Adjust name count */
            theUnderlyingMap.adjustForItem(pItem);
        }

        /**
         * find item by name.
         *
         * @param pName the name to look up
         * @return the matching item
         */
        public MoneyWisePayee findItemByName(final String pName) {
            final MoneyWiseAssetBase myAsset = theUnderlyingMap.findAssetByName(pName);
            return myAsset instanceof MoneyWisePayee myPayee
                    ? myPayee
                    : null;
        }

        /**
         * Check validity of name.
         *
         * @param pName the name to look up
         * @return true/false
         */
        public boolean validNameCount(final String pName) {
            return theUnderlyingMap.validKeyCount(pName);
        }

        /**
         * Check availability of name.
         *
         * @param pName the key to look up
         * @return true/false
         */
        public boolean availableName(final String pName) {
            return theUnderlyingMap.availableKey(pName);
        }

        /**
         * find singular item.
         *
         * @param pClass the class to look up
         * @return the matching item
         */
        public MoneyWisePayee findSingularItem(final MoneyWisePayeeClass pClass) {
            return thePayeeMap.get(pClass.getClassId());
        }

        /**
         * Check validity of singular count.
         *
         * @param pClass the class to look up
         * @return true/false
         */
        public boolean validSingularCount(final MoneyWisePayeeClass pClass) {
            final Integer myResult = thePayeeCountMap.get(pClass.getClassId());
            return PrometheusDataInstanceMap.ONE.equals(myResult);
        }
    }
}
