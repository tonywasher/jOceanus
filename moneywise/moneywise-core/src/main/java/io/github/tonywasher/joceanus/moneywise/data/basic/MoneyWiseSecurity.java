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
package io.github.tonywasher.joceanus.moneywise.data.basic;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.data.MetisDataEditState;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.data.MetisDataResource;
import io.github.tonywasher.joceanus.metis.data.MetisDataState;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersionedSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding.MoneyWiseSecurityHoldingMap;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityInfo.MoneyWiseSecurityInfoList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTax.MoneyWiseTaxCredit;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseSecurityType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseSecurityType.MoneyWiseSecurityTypeList;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseLogicException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataInstanceMap;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues.PrometheusInfoItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues.PrometheusInfoSetItem;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Security class.
 */
public class MoneyWiseSecurity
        extends MoneyWiseAssetBase
        implements PrometheusInfoSetItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.SECURITY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.SECURITY.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseSecurity> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseSecurity.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME, MoneyWiseSecurity::getInfoSet);
        FIELD_DEFS.buildFieldMap(MoneyWiseAccountInfoClass.class, MoneyWiseSecurity::getFieldValue);
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
     * SecurityInfoSet.
     */
    private final MoneyWiseSecurityInfoSet theInfoSet;

    /**
     * Copy Constructor.
     *
     * @param pList     the list
     * @param pSecurity The Security to copy
     */
    protected MoneyWiseSecurity(final MoneyWiseSecurityList pList,
                                final MoneyWiseSecurity pSecurity) {
        /* Set standard values */
        super(pList, pSecurity);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new MoneyWiseSecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
                theInfoSet.cloneDataInfoSet(pSecurity.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new MoneyWiseSecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
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
    private MoneyWiseSecurity(final MoneyWiseSecurityList pList,
                              final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Create the InfoSet */
        theInfoSet = new MoneyWiseSecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     *
     * @param pList the list
     */
    public MoneyWiseSecurity(final MoneyWiseSecurityList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new MoneyWiseSecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
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
        return MoneyWiseAssetType.createExternalId(MoneyWiseAssetType.SECURITY, getIndexedId());
    }

    @Override
    public MoneyWiseSecurityInfoSet getInfoSet() {
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
     * Obtain Notes.
     *
     * @return the notes
     */
    public char[] getNotes() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.NOTES, char[].class)
                : null;
    }

    /**
     * Obtain Symbol.
     *
     * @return the symbol
     */
    public String getSymbol() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.SYMBOL, String.class)
                : null;
    }

    /**
     * Obtain Region.
     *
     * @return the region
     */
    public MoneyWiseRegion getRegion() {
        return hasInfoSet
                ? theInfoSet.getRegion(MoneyWiseAccountInfoClass.REGION)
                : null;
    }

    /**
     * Obtain UnderlyingStock.
     *
     * @return the stock
     */
    public MoneyWiseSecurity getUnderlyingStock() {
        return hasInfoSet
                ? theInfoSet.getSecurity(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK)
                : null;
    }

    /**
     * Obtain OptionPrice.
     *
     * @return the price
     */
    public OceanusPrice getOptionPrice() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.OPTIONPRICE, OceanusPrice.class)
                : null;
    }

    @Override
    public MoneyWiseSecurityType getCategory() {
        return getValues().getValue(MoneyWiseBasicResource.CATEGORY_NAME, MoneyWiseSecurityType.class);
    }

    /**
     * Obtain CategoryId.
     *
     * @return the categoryId
     */
    public Integer getCategoryId() {
        final MoneyWiseSecurityType myType = getCategory();
        return myType == null
                ? null
                : myType.getIndexedId();
    }

    /**
     * Obtain CategoryName.
     *
     * @return the categoryName
     */
    public String getCategoryName() {
        final MoneyWiseSecurityType myType = getCategory();
        return myType == null
                ? null
                : myType.getName();
    }

    /**
     * Obtain SecurityClass.
     *
     * @return the securityClass
     */
    public MoneyWiseSecurityClass getCategoryClass() {
        final MoneyWiseSecurityType myType = getCategory();
        return myType == null
                ? null
                : myType.getSecurityClass();
    }

    @Override
    public boolean isForeign() {
        final MoneyWiseCurrency myDefault = getDataSet().getReportingCurrency();
        return !myDefault.equals(getAssetCurrency());
    }

    @Override
    public MoneyWiseSecurity getBase() {
        return (MoneyWiseSecurity) super.getBase();
    }

    @Override
    public MoneyWiseSecurityList getList() {
        return (MoneyWiseSecurityList) super.getList();
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
        final MoneyWiseAccountInfoClass myClass = MoneyWiseSecurityInfoSet.getClassForField(pField);
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
     * Is this security the required class.
     *
     * @param pClass the required security class.
     * @return true/false
     */
    public boolean isSecurityClass(final MoneyWiseSecurityClass pClass) {
        /* Check for match */
        return getCategoryClass() == pClass;
    }

    @Override
    public boolean isShares() {
        return isSecurityClass(MoneyWiseSecurityClass.SHARES);
    }

    @Override
    public boolean isCapital() {
        switch (getCategoryClass()) {
            case INCOMEUNITTRUST:
            case GROWTHUNITTRUST:
            case LIFEBOND:
            case SHARES:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void deRegister() {
        final PrometheusEditSet myEditSet = getList().getEditSet();
        final MoneyWisePortfolioList myPortfolios = myEditSet == null
                ? getDataSet().getPortfolios()
                : myEditSet.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class);
        final MoneyWiseSecurityHoldingMap myMap = myPortfolios.getSecurityHoldingsMap();
        myMap.deRegister(this);
    }

    /**
     * Set defaults.
     *
     * @throws OceanusException on error
     */
    public void setDefaults() throws OceanusException {
        getList().getValidator().setDefaults(this);
    }

    /**
     * autoCorrect values after change.
     *
     * @throws OceanusException on error
     */
    public void autoCorrect() throws OceanusException {
        getList().getValidator().autoCorrect(this);
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Check the category and then the name */
        final MoneyWiseSecurity myThat = (MoneyWiseSecurity) pThat;
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
        resolveDataLink(MoneyWiseBasicResource.CATEGORY_NAME, myData.getSecurityTypes());
        resolveDataLink(MoneyWiseStaticDataType.CURRENCY, myData.getAccountCurrencies());
        resolveDataLink(MoneyWiseBasicResource.ASSET_PARENT, myData.getPayees());
    }

    @Override
    protected void resolveEditSetLinks() throws OceanusException {
        /* Access the editSet */
        final PrometheusEditSet myEditSet = getList().getEditSet();

        /* Resolve Parent/Category/Currency if required */
        resolveDataLink(MoneyWiseBasicResource.ASSET_PARENT, myEditSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class));
        if (myEditSet.hasDataType(MoneyWiseStaticDataType.SECURITYTYPE)) {
            resolveDataLink(MoneyWiseBasicResource.CATEGORY_NAME, myEditSet.getDataList(MoneyWiseStaticDataType.SECURITYTYPE, MoneyWiseSecurityTypeList.class));
        }
        if (myEditSet.hasDataType(MoneyWiseStaticDataType.CURRENCY)) {
            resolveDataLink(MoneyWiseStaticDataType.CURRENCY, myEditSet.getDataList(MoneyWiseStaticDataType.CURRENCY, MoneyWiseCurrencyList.class));
        }

        /* Resolve links in infoSet */
        theInfoSet.resolveEditSetLinks(myEditSet);
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
     * Set a new symbol.
     *
     * @param pSymbol the symbol
     * @throws OceanusException on error
     */
    public void setSymbol(final String pSymbol) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.SYMBOL, pSymbol);
    }

    /**
     * Set a new region.
     *
     * @param pRegion the new region
     * @throws OceanusException on error
     */
    public void setRegion(final MoneyWiseRegion pRegion) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.REGION, pRegion);
    }

    /**
     * Set a new underlying stock.
     *
     * @param pStock the new stock
     * @throws OceanusException on error
     */
    public void setUnderlyingStock(final MoneyWiseSecurity pStock) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK, pStock);
    }

    /**
     * Set a new option price.
     *
     * @param pPrice the new price
     * @throws OceanusException on error
     */
    public void setOptionPrice(final OceanusPrice pPrice) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.OPTIONPRICE, pPrice);
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
    public MoneyWiseTransCategory getDetailedCategory(final MoneyWiseTransCategory pCategory,
                                                      final MoneyWiseTaxCredit pYear) {
        /* Switch on category type */
        if (MoneyWiseTransCategoryClass.DIVIDEND.equals(pCategory.getCategoryTypeClass())) {
            final MoneyWiseTransCategoryList myCategories = getDataSet().getTransCategories();
            if (isForeign()) {
                return myCategories.getSingularClass(MoneyWiseTransCategoryClass.FOREIGNDIVIDEND);
            }
            return myCategories.getSingularClass(getCategoryClass().isUnitTrust()
                    ? MoneyWiseTransCategoryClass.UNITTRUSTDIVIDEND
                    : MoneyWiseTransCategoryClass.SHAREDIVIDEND);
        }
        return pCategory;
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the security type, currency and parent */
        getCategory().touchItem(this);
        getAssetCurrency().touchItem(this);
        getParent().touchItem(this);

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    @Override
    public void touchOnUpdate() {
        /* Reset touches from update set */
        clearTouches(MoneyWiseBasicDataType.SECURITYPRICE);

        /* Touch parent */
        getParent().touchItem(this);
    }

    /**
     * Update base security from an edited security.
     *
     * @param pSecurity the edited security
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pSecurity) {
        /* Can only update from a security */
        if (!(pSecurity instanceof MoneyWiseSecurity)) {
            return false;
        }
        final MoneyWiseSecurity mySecurity = (MoneyWiseSecurity) pSecurity;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(mySecurity);

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final MoneyWiseSecurityList myList = getList();
        final MoneyWiseSecurityDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    @Override
    public void removeItem() {
        theInfoSet.removeItems();
        super.removeItem();
    }

    /**
     * The Security List class.
     */
    public static class MoneyWiseSecurityList
            extends MoneyWiseAssetBaseList<MoneyWiseSecurity> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseSecurityList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityList.class);

        /**
         * The SecurityInfo List.
         */
        private MoneyWiseSecurityInfoList theInfoList;

        /**
         * The AccountInfoType list.
         */
        private MoneyWiseAccountInfoTypeList theInfoTypeList;

        /**
         * Construct an empty CORE Security list.
         *
         * @param pData the DataSet for the list
         */
        public MoneyWiseSecurityList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWiseSecurity.class, MoneyWiseBasicDataType.SECURITY);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        protected MoneyWiseSecurityList(final MoneyWiseSecurityList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseSecurityList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseSecurity.FIELD_DEFS;
        }

        @Override
        public MoneyWiseSecurityDataMap getDataMap() {
            return (MoneyWiseSecurityDataMap) super.getDataMap();
        }

        /**
         * Obtain the securityInfoList.
         *
         * @return the security info list
         */
        public MoneyWiseSecurityInfoList getSecurityInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getSecurityInfo();
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
        protected MoneyWiseSecurityList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseSecurityList myList = new MoneyWiseSecurityList(this);
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
        public MoneyWiseSecurityList deriveEditList(final PrometheusEditSet pEditSet) throws OceanusException {
            /* Build an empty List */
            final MoneyWiseSecurityList myList = getEmptyList(PrometheusListStyle.EDIT);
            myList.ensureMap();
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.SECURITY, myList);
            myList.getValidator().setEditSet(pEditSet);

            /* Store InfoType list */
            myList.theInfoTypeList = pEditSet.getDataList(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, MoneyWiseAccountInfoTypeList.class);

            /* Create info List */
            final MoneyWiseSecurityInfoList mySecInfo = getSecurityInfo();
            myList.theInfoList = mySecInfo.getEmptyList(PrometheusListStyle.EDIT);
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.SECURITYINFO, myList.theInfoList);

            /* Store the editSet */
            myList.setEditSet(pEditSet);

            /* Loop through the securities */
            final Iterator<MoneyWiseSecurity> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseSecurity myCurr = myIterator.next();

                /* Ignore deleted securities */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked security and add it to the list */
                final MoneyWiseSecurity mySecurity = new MoneyWiseSecurity(myList, myCurr);
                myList.add(mySecurity);
                mySecurity.resolveEditSetLinks();

                /* Adjust the map */
                mySecurity.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        @Override
        public MoneyWiseSecurity findItemByName(final String pName) {
            /* look up the name in the map */
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
         * Find the item that uses the symbol.
         *
         * @param pSymbol the symbol to lookup
         * @return the item (or null)
         */
        public MoneyWiseSecurity findItemBySymbol(final String pSymbol) {
            /* look up the symbol in the map */
            return getDataMap().findItemBySymbol(pSymbol);
        }

        /**
         * check that symbol is unique.
         *
         * @param pSymbol the symbol
         * @return true/false
         */
        public boolean validSymbolCount(final String pSymbol) {
            /* check availability in map */
            return getDataMap().validSymbolCount(pSymbol);
        }

        /**
         * Add a new item to the core list.
         *
         * @param pSecurity item
         * @return the newly added item
         */
        @Override
        public MoneyWiseSecurity addCopyItem(final PrometheusDataItem pSecurity) {
            /* Can only clone a Security */
            if (!(pSecurity instanceof MoneyWiseSecurity)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseSecurity mySecurity = new MoneyWiseSecurity(this, (MoneyWiseSecurity) pSecurity);
            add(mySecurity);
            return mySecurity;
        }

        /**
         * Add a new item to the edit list.
         *
         * @return the new item
         */
        @Override
        public MoneyWiseSecurity addNewItem() {
            final MoneyWiseSecurity mySecurity = new MoneyWiseSecurity(this);
            add(mySecurity);
            return mySecurity;
        }

        /**
         * Obtain the first security for the specified class.
         *
         * @param pClass the security class
         * @return the security
         */
        public MoneyWiseSecurity getSingularClass(final MoneyWiseSecurityClass pClass) {
            /* Lookup in the map */
            return getDataMap().findSingularItem(pClass);
        }

        @Override
        public MoneyWiseSecurity addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the security */
            final MoneyWiseSecurity mySecurity = new MoneyWiseSecurity(this, pValues);

            /* Check that this SecurityId has not been previously added */
            if (!isIdUnique(mySecurity.getIndexedId())) {
                mySecurity.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(mySecurity, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(mySecurity);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<PrometheusInfoItem> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final PrometheusInfoItem myItem = myIterator.next();

                    /* Build info */
                    final PrometheusDataValues myValues = myItem.getValues(mySecurity);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return mySecurity;
        }

        @Override
        protected MoneyWiseSecurityDataMap allocateDataMap() {
            return new MoneyWiseSecurityDataMap();
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
    public static class MoneyWiseSecurityDataMap
            extends PrometheusDataInstanceMap<MoneyWiseSecurity, String> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseSecurityDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityDataMap.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_SINGULARMAP, MoneyWiseSecurityDataMap::getSingularMap);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_SINGULARCOUNTS, MoneyWiseSecurityDataMap::getSingularCountMap);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.SECURITY_SYMBOLMAP, MoneyWiseSecurityDataMap::getSymbolMap);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.SECURITY_SYMBOLCOUNTMAP, MoneyWiseSecurityDataMap::getSymbolCountMap);
        }

        /**
         * Map of symbol counts.
         */
        private final Map<String, Integer> theSymbolCountMap;

        /**
         * Map of symbols.
         */
        private final Map<String, MoneyWiseSecurity> theSymbolMap;

        /**
         * Map of category counts.
         */
        private final Map<Integer, Integer> theSecurityCountMap;

        /**
         * Map of singular categories.
         */
        private final Map<Integer, MoneyWiseSecurity> theSecurityMap;

        /**
         * Constructor.
         */
        public MoneyWiseSecurityDataMap() {
            /* Create the maps */
            theSecurityCountMap = new HashMap<>();
            theSecurityMap = new HashMap<>();
            theSymbolCountMap = new HashMap<>();
            theSymbolMap = new HashMap<>();
        }

        @Override
        public MetisFieldSet<MoneyWiseSecurityDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain the securityMap.
         *
         * @return the map
         */
        private Map<Integer, MoneyWiseSecurity> getSingularMap() {
            return theSecurityMap;
        }

        /**
         * Obtain the securityCountMap.
         *
         * @return the map
         */
        private Map<Integer, Integer> getSingularCountMap() {
            return theSecurityCountMap;
        }

        /**
         * Obtain the keyMap.
         *
         * @return the map
         */
        private Map<String, MoneyWiseSecurity> getSymbolMap() {
            return theSymbolMap;
        }

        /**
         * Obtain the keyCountMap.
         *
         * @return the map
         */
        private Map<String, Integer> getSymbolCountMap() {
            return theSymbolCountMap;
        }

        @Override
        public void resetMap() {
            super.resetMap();
            theSecurityCountMap.clear();
            theSecurityMap.clear();
            theSymbolCountMap.clear();
            theSymbolMap.clear();
        }

        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* If the class is singular */
            final MoneyWiseSecurity myItem = (MoneyWiseSecurity) pItem;
            final MoneyWiseSecurityClass myClass = myItem.getCategoryClass();
            if (myClass.isSingular()) {
                /* Adjust category count */
                final Integer myId = myClass.getClassId();
                final Integer myCount = theSecurityCountMap.get(myId);
                if (myCount == null) {
                    theSecurityCountMap.put(myId, ONE);
                } else {
                    theSecurityCountMap.put(myId, myCount + 1);
                }

                /* Adjust security map */
                theSecurityMap.put(myId, myItem);
            }

            /* Adjust symbol count */
            if (myClass.needsSymbol()) {
                final String mySymbol = myItem.getSymbol();
                final Integer myCount = theSymbolCountMap.get(mySymbol);
                if (myCount == null) {
                    theSymbolCountMap.put(mySymbol, ONE);
                } else {
                    theSymbolCountMap.put(mySymbol, myCount + 1);
                }

                /* Adjust symbol map */
                theSymbolMap.put(mySymbol, myItem);
            }

            /* Adjust name count */
            adjustForItem(myItem, myItem.getName());
        }

        /**
         * find item by symbol.
         *
         * @param pSymbol the symbol to look up
         * @return the matching item
         */
        public MoneyWiseSecurity findItemBySymbol(final String pSymbol) {
            return theSymbolMap.get(pSymbol);
        }

        /**
         * find item by name.
         *
         * @param pName the name to look up
         * @return the matching item
         */
        public MoneyWiseSecurity findItemByName(final String pName) {
            return findItemByKey(pName);
        }

        /**
         * Check validity of symbol count.
         *
         * @param pSymbol the symbol to look up
         * @return true/false
         */
        public boolean validSymbolCount(final String pSymbol) {
            final Integer myResult = theSymbolCountMap.get(pSymbol);
            return ONE.equals(myResult);
        }

        /**
         * Check validity of name.
         *
         * @param pName the name to look up
         * @return true/false
         */
        public boolean validNameCount(final String pName) {
            return validKeyCount(pName);
        }

        /**
         * Check availability of name.
         *
         * @param pName the key to look up
         * @return true/false
         */
        public boolean availableName(final String pName) {
            return availableKey(pName);
        }

        /**
         * find singular item.
         *
         * @param pClass the class to look up
         * @return the matching item
         */
        public MoneyWiseSecurity findSingularItem(final MoneyWiseSecurityClass pClass) {
            return theSecurityMap.get(pClass.getClassId());
        }

        /**
         * Check validity of singular count.
         *
         * @param pClass the class to look up
         * @return true/false
         */
        public boolean validSingularCount(final MoneyWiseSecurityClass pClass) {
            final Integer myResult = theSecurityCountMap.get(pClass.getClassId());
            return ONE.equals(myResult);
        }
    }
}
