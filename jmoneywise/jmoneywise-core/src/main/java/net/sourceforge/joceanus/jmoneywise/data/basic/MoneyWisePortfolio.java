/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolioInfo.MoneyWisePortfolioInfoList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding.MoneyWiseSecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePortfolioType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePortfolioType.MoneyWisePortfolioTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataMapItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues.PrometheusInfoItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues.PrometheusInfoSetItem;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Portfolio class.
 */
public class MoneyWisePortfolio
        extends MoneyWiseAssetBase
        implements PrometheusInfoSetItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.PORTFOLIO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.PORTFOLIO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWisePortfolio> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWisePortfolio.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME, MoneyWisePortfolio::getInfoSet);
    }

    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseBasicResource.PORTFOLIO_NEWACCOUNT.getValue();

    /**
     * Portfolio Cash account.
     */
    public static final String NAME_CASHACCOUNT = MoneyWiseBasicResource.PORTFOLIO_CASHACCOUNT.getValue();

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * PortfolioInfoSet.
     */
    private final MoneyWisePortfolioInfoSet theInfoSet;

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPortfolio The Portfolio to copy
     */
    protected MoneyWisePortfolio(final MoneyWisePortfolioList pList,
                                 final MoneyWisePortfolio pPortfolio) {
        /* Set standard values */
        super(pList, pPortfolio);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new MoneyWisePortfolioInfoSet(this, pList.getActInfoTypes(), pList.getPortfolioInfo());
                theInfoSet.cloneDataInfoSet(pPortfolio.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new MoneyWisePortfolioInfoSet(this, pList.getActInfoTypes(), pList.getPortfolioInfo());
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
    private MoneyWisePortfolio(final MoneyWisePortfolioList pList,
                               final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Create the InfoSet */
        theInfoSet = new MoneyWisePortfolioInfoSet(this, pList.getActInfoTypes(), pList.getPortfolioInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public MoneyWisePortfolio(final MoneyWisePortfolioList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new MoneyWisePortfolioInfoSet(this, pList.getActInfoTypes(), pList.getPortfolioInfo());
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
        if (MoneyWiseBasicResource.ASSET_PARENT.equals(pField)) {
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
        return MoneyWiseAssetType.createExternalId(MoneyWiseAssetType.PORTFOLIO, getIndexedId());
    }

    @Override
    public MoneyWisePortfolioInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain WebSite.
     * @return the webSite
     */
    public char[] getWebSite() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.WEBSITE, char[].class)
                : null;
    }

    /**
     * Obtain CustNo.
     * @return the customer #
     */
    public char[] getCustNo() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.CUSTOMERNO, char[].class)
                : null;
    }

    /**
     * Obtain UserId.
     * @return the userId
     */
    public char[] getUserId() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.USERID, char[].class)
                : null;
    }

    /**
     * Obtain Password.
     * @return the password
     */
    public char[] getPassword() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseAccountInfoClass.PASSWORD, char[].class)
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
    public MoneyWisePortfolioType getCategory() {
        return getValues().getValue(MoneyWiseBasicResource.CATEGORY_NAME, MoneyWisePortfolioType.class);
    }

    /**
     * Obtain categoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        final MoneyWisePortfolioType myType = getCategory();
        return myType == null
                ? null
                : myType.getIndexedId();
    }

    /**
     * Obtain categoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        final MoneyWisePortfolioType myType = getCategory();
        return myType == null
                ? null
                : myType.getName();
    }

    /**
     * Obtain categoryClass.
     * @return the categoryClass
     */
    public MoneyWisePortfolioClass getCategoryClass() {
        final MoneyWisePortfolioType myType = getCategory();
        return myType == null
                ? null
                : myType.getPortfolioClass();
    }

    @Override
    public Boolean isTaxFree() {
        final MoneyWisePortfolioType myType = getCategory();
        return myType == null
                ? Boolean.FALSE
                : myType.getPortfolioClass().isTaxFree();
    }

    @Override
    public Boolean isForeign() {
        final MoneyWiseCurrency myDefault = getDataSet().getReportingCurrency();
        return !myDefault.equals(getAssetCurrency());
    }

    @Override
    public MoneyWisePortfolio getBase() {
        return (MoneyWisePortfolio) super.getBase();
    }

    @Override
    public MoneyWisePortfolioList getList() {
        return (MoneyWisePortfolioList) super.getList();
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
        final MoneyWiseAccountInfoClass myClass = MoneyWisePortfolioInfoSet.getClassForField(pField);
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
     * Is this portfolio the required class.
     * @param pClass the required portfolio class.
     * @return true/false
     */
    public boolean isPortfolioClass(final MoneyWisePortfolioClass pClass) {
        /* Check for match */
        return getCategoryClass() == pClass;
    }

    @Override
    public void deRegister() {
        final MoneyWiseSecurityHoldingMap myMap = getList().getSecurityHoldingsMap();
        myMap.deRegister(this);
    }

    /**
     * Set defaults.
     * @param pEditSet the edit set
     * @throws OceanusException on error
     */
    public void setDefaults(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Set values */
        setName(getList().getUniqueName(NAME_NEWACCOUNT));
        setCategory(getDefaultPortfolioType());
        setParent(getDefaultParent(pEditSet));
        setAssetCurrency(getDataSet().getReportingCurrency());
        setClosed(Boolean.FALSE);
    }

    /**
     * Obtain default parent for portfolio.
     * @param pEditSet the edit set
     * @return the default parent
     */
    private static MoneyWisePayee getDefaultParent(final PrometheusEditSet pEditSet) {
        /* loop through the payees */
        final MoneyWisePayeeList myPayees = pEditSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted and closed payees and those that cannot parent this portfolio */
            boolean bIgnore = myPayee.isDeleted() || myPayee.isClosed();
            bIgnore |= !myPayee.getCategoryClass().canParentPortfolio();
            if (!bIgnore) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }

    /**
     * Obtain portfolio type for new portfolio account.
     * @return the security type
     */
    public MoneyWisePortfolioType getDefaultPortfolioType() {
        /* loop through the portfolio types */
        final MoneyWisePortfolioTypeList myTypes = getDataSet().getPortfolioTypes();
        final Iterator<MoneyWisePortfolioType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePortfolioType myType = myIterator.next();

            /* Ignore deleted types */
            if (!myType.isDeleted()) {
                return myType;
            }
        }

        /* Return no category */
        return null;
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Check the category and then the name */
        final MoneyWisePortfolio myThat = (MoneyWisePortfolio) pThat;
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

        /* Resolve holding account */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(MoneyWiseBasicResource.ASSET_PARENT, myData.getPayees());
        resolveDataLink(MoneyWiseBasicResource.CATEGORY_NAME, myData.getPortfolioTypes());
        resolveDataLink(MoneyWiseStaticDataType.CURRENCY, myData.getAccountCurrencies());
    }

    @Override
    protected void resolveEditSetLinks() throws OceanusException {
        /* Access the editSet */
        final PrometheusEditSet myEditSet = getList().getEditSet();

        /* Resolve Parent/Category/Currency if required */
        resolveDataLink(MoneyWiseBasicResource.ASSET_PARENT, myEditSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class));
        if (myEditSet.hasDataType(MoneyWiseStaticDataType.PORTFOLIOTYPE)) {
            resolveDataLink(MoneyWiseBasicResource.CATEGORY_NAME, myEditSet.getDataList(MoneyWiseStaticDataType.PORTFOLIOTYPE, MoneyWisePortfolioTypeList.class));
        }
        if (myEditSet.hasDataType(MoneyWiseStaticDataType.CURRENCY)) {
            resolveDataLink(MoneyWiseStaticDataType.CURRENCY, myEditSet.getDataList(MoneyWiseStaticDataType.CURRENCY, MoneyWiseCurrencyList.class));
        }

        /* Resolve links in infoSet */
        theInfoSet.resolveEditSetLinks(myEditSet);
    }

    /**
     * Set a new WebSite.
     * @param pWebSite the new webSite
     * @throws OceanusException on error
     */
    public void setWebSite(final char[] pWebSite) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.WEBSITE, pWebSite);
    }

    /**
     * Set a new CustNo.
     * @param pCustNo the new custNo
     * @throws OceanusException on error
     */
    public void setCustNo(final char[] pCustNo) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.CUSTOMERNO, pCustNo);
    }

    /**
     * Set a new UserId.
     * @param pUserId the new userId
     * @throws OceanusException on error
     */
    public void setUserId(final char[] pUserId) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.USERID, pUserId);
    }

    /**
     * Set a new Password.
     * @param pPassword the new password
     * @throws OceanusException on error
     */
    public void setPassword(final char[] pPassword) throws OceanusException {
        setInfoSetValue(MoneyWiseAccountInfoClass.PASSWORD, pPassword);
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
    public MoneyWiseTransCategory getDetailedCategory(final MoneyWiseTransCategory pCategory,
                                                      final MoneyWiseTaxCredit pYear) {
        /* Switch on category type */
        final MoneyWiseTransCategoryList myCategories = getDataSet().getTransCategories();
        switch (pCategory.getCategoryTypeClass()) {
            case INTEREST:
                if (Boolean.TRUE.equals(isTaxFree())) {
                    return myCategories.getSingularClass(MoneyWiseTransCategoryClass.TAXFREEINTEREST);
                }
                return myCategories.getSingularClass(isGross()
                        || !pYear.isTaxCreditRequired()
                        ? MoneyWiseTransCategoryClass.GROSSINTEREST
                        : MoneyWiseTransCategoryClass.TAXEDINTEREST);
            case LOYALTYBONUS:
                if (Boolean.TRUE.equals(isTaxFree())) {
                    return myCategories.getSingularClass(MoneyWiseTransCategoryClass.TAXFREELOYALTYBONUS);
                }
                return myCategories.getSingularClass(Boolean.TRUE.equals(isGross())
                        ? MoneyWiseTransCategoryClass.GROSSLOYALTYBONUS
                        : MoneyWiseTransCategoryClass.TAXEDLOYALTYBONUS);
            case DIVIDEND:
                return Boolean.TRUE.equals(isTaxFree())
                        ? myCategories.getSingularClass(MoneyWiseTransCategoryClass.TAXFREEDIVIDEND)
                        : pCategory;
            default:
                return pCategory;
        }
    }

    @Override
    public void touchUnderlyingItems() {
        /* Touch parent and currency */
        getCategory().touchItem(this);
        getParent().touchItem(this);
        getAssetCurrency().touchItem(this);

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
        final MoneyWisePortfolioList myList = getList();
        final MoneyWisePayee myParent = getParent();
        final MoneyWisePortfolioType myPortType = getCategory();
        final MoneyWiseCurrency myCurrency = getAssetCurrency();

        /* Validate base components */
        super.validate();

        /* PortfolioType must be non-null */
        if (myPortType == null) {
            addError(ERROR_MISSING, MoneyWiseBasicResource.CATEGORY_NAME);
        } else {
            /* Access the class */
            final MoneyWisePortfolioClass myClass = myPortType.getPortfolioClass();

            /* PortfolioType must be enabled */
            if (!myPortType.getEnabled()) {
                addError(ERROR_DISABLED, MoneyWiseBasicResource.CATEGORY_NAME);
            }

            /* If the PortfolioType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                final MoneyWisePortfolioDataMap myMap = myList.getDataMap();
                if (!myMap.validSingularCount(myClass)) {
                    addError(ERROR_MULT, MoneyWiseBasicResource.CATEGORY_NAME);
                }
            }
        }

        /* Parent account must exist */
        if (myParent == null) {
            addError(ERROR_MISSING, MoneyWiseBasicResource.ASSET_PARENT);
        } else {
            /* Parent must be suitable */
            final MoneyWisePayeeClass myParClass = myParent.getCategoryClass();
            if (!myParClass.canParentPortfolio()) {
                addError(ERROR_BADPARENT, MoneyWiseBasicResource.ASSET_PARENT);
            }

            /* If we are open then parent must be open */
            if (!isClosed() && myParent.isClosed()) {
                addError(ERROR_PARCLOSED, MoneyWiseBasicResource.ASSET_CLOSED);
            }
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

    @Override
    protected void validateName(final String pName) {
        /* Perform basic checks */
        super.validateName(pName);

        /* Check that the name does not contain invalid characters */
        if (pName.contains(MoneyWiseSecurityHolding.SECURITYHOLDING_SEP)) {
            addError(ERROR_INVALIDCHAR, PrometheusDataResource.DATAITEM_FIELD_NAME);
        }
    }

    /**
     * Update base portfolio from an edited portfolio.
     * @param pPortfolio the edited portfolio
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pPortfolio) {
        /* Can only update from a portfolio */
        if (!(pPortfolio instanceof MoneyWisePortfolio)) {
            return false;
        }
        final MoneyWisePortfolio myPortfolio = (MoneyWisePortfolio) pPortfolio;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myPortfolio);

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final MoneyWisePortfolioList myList = getList();
        final MoneyWisePortfolioDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Portfolio List class.
     */
    public static class MoneyWisePortfolioList
            extends MoneyWiseAssetBaseList<MoneyWisePortfolio> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWisePortfolioList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePortfolioList.class);

        /*
         * FieldIds.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_HOLDINGSMAP, MoneyWisePortfolioList::getSecurityHoldingsMap);
        }

        /**
         * The PortfolioInfo List.
         */
        private MoneyWisePortfolioInfoList theInfoList;

        /**
         * The AccountInfoType list.
         */
        private MoneyWiseAccountInfoTypeList theInfoTypeList;

        /**
         * SecurityHoldings Map.
         */
        private MoneyWiseSecurityHoldingMap theSecurityHoldings;

        /**
         * Construct an empty CORE Portfolio list.
         * @param pData the DataSet for the list
         */
        public MoneyWisePortfolioList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWisePortfolio.class, MoneyWiseBasicDataType.PORTFOLIO);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWisePortfolioList(final MoneyWisePortfolioList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWisePortfolioList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWisePortfolio.FIELD_DEFS;
        }

        @Override
        protected MoneyWisePortfolioDataMap getDataMap() {
            return (MoneyWisePortfolioDataMap) super.getDataMap();
        }

        /**
         * Obtain the portfolioInfoList.
         * @return the portfolio info list
         */
        public MoneyWisePortfolioInfoList getPortfolioInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getPortfolioInfo();
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

        /**
         * Obtain security holdings map.
         * @return the holdings map
         */
        public MoneyWiseSecurityHoldingMap getSecurityHoldingsMap() {
            if (theSecurityHoldings == null) {
                theSecurityHoldings = getEditSet() == null
                        ? new MoneyWiseSecurityHoldingMap(getDataSet())
                        : new MoneyWiseSecurityHoldingMap(getEditSet());
            }
            return theSecurityHoldings;
        }

        @Override
        protected MoneyWisePortfolioList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWisePortfolioList myList = new MoneyWisePortfolioList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pEditSet the editSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public MoneyWisePortfolioList deriveEditList(final PrometheusEditSet pEditSet) throws OceanusException {
            /* Build an empty List */
            final MoneyWisePortfolioList myList = getEmptyList(PrometheusListStyle.EDIT);
            final MoneyWisePayeeList myPayees = pEditSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
            myList.ensureMap(myPayees);
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.PORTFOLIO, myList);

            /* Store InfoType list */
            myList.theInfoTypeList = pEditSet.getDataList(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, MoneyWiseAccountInfoTypeList.class);

            /* Create info List */
            final MoneyWisePortfolioInfoList myPortInfo = getPortfolioInfo();
            myList.theInfoList = myPortInfo.getEmptyList(PrometheusListStyle.EDIT);
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.PORTFOLIOINFO, myList.theInfoList);

            /* Store the editSet */
            myList.setEditSet(pEditSet);

            /* Loop through the portfolios */
            final Iterator<MoneyWisePortfolio> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWisePortfolio myCurr = myIterator.next();

                /* Ignore deleted portfolios */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked portfolio and add it to the list */
                final MoneyWisePortfolio myPortfolio = new MoneyWisePortfolio(myList, myCurr);
                myList.add(myPortfolio);
                myPortfolio.resolveEditSetLinks();

                /* Adjust the map */
                myPortfolio.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        @Override
        public MoneyWisePortfolio findItemByName(final String pName) {
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

        /**
         * Add a new item to the core list.
         * @param pPortfolio item
         * @return the newly added item
         */
        @Override
        public MoneyWisePortfolio addCopyItem(final PrometheusDataItem pPortfolio) {
            /* Can only clone a Portfolio */
            if (!(pPortfolio instanceof MoneyWisePortfolio)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWisePortfolio myPortfolio = new MoneyWisePortfolio(this, (MoneyWisePortfolio) pPortfolio);
            add(myPortfolio);
            return myPortfolio;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public MoneyWisePortfolio addNewItem() {
            final MoneyWisePortfolio myPortfolio = new MoneyWisePortfolio(this);
            add(myPortfolio);
            return myPortfolio;
        }

        /**
         * Obtain the first portfolio for the specified class.
         * @param pClass the portfolio class
         * @return the portfolio
         */
        public MoneyWisePortfolio getSingularClass(final MoneyWisePortfolioClass pClass) {
            /* Lookup in the map */
            return getDataMap().findSingularItem(pClass);
        }

        @Override
        public MoneyWisePortfolio addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the portfolio */
            final MoneyWisePortfolio myPortfolio = new MoneyWisePortfolio(this, pValues);

            /* Check that this PortfolioId has not been previously added */
            if (!isIdUnique(myPortfolio.getIndexedId())) {
                myPortfolio.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myPortfolio, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myPortfolio);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<PrometheusInfoItem> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final PrometheusInfoItem myItem = myIterator.next();

                    /* Build info */
                    final PrometheusDataValues myValues = myItem.getValues(myPortfolio);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myPortfolio;
        }

        /**
         * Ensure Map based on the payee list.
         * @param pPayees the payee list
         */
        private void ensureMap(final MoneyWisePayeeList pPayees) {
            setDataMap(new MoneyWisePortfolioDataMap(pPayees));
        }

        @Override
        protected MoneyWisePortfolioDataMap allocateDataMap() {
            return new MoneyWisePortfolioDataMap(getDataSet().getPayees());
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
    protected static class MoneyWisePortfolioDataMap
            implements PrometheusDataMapItem, MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWisePortfolioDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePortfolioDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_UNDERLYING, MoneyWisePortfolioDataMap::getUnderlyingMap);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_SINGULARMAP, MoneyWisePortfolioDataMap::getPortfolioMap);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_SINGULARCOUNTS, MoneyWisePortfolioDataMap::getPortfolioCountMap);
        }

        /**
         * The assetMap.
         */
        private MoneyWiseAssetDataMap theUnderlyingMap;

        /**
         * Map of category counts.
         */
        private final Map<Integer, Integer> thePortfolioCountMap;

        /**
         * Map of singular categories.
         */
        private final Map<Integer, MoneyWisePortfolio> thePortfolioMap;

        /**
         * Constructor.
         * @param pPayees the payee list
         */
        protected MoneyWisePortfolioDataMap(final MoneyWisePayeeList pPayees) {
            /* Access underlying nameMap */
            theUnderlyingMap = pPayees.getDataMap().getUnderlyingMap();

            /* Create the maps */
            thePortfolioCountMap = new HashMap<>();
            thePortfolioMap = new HashMap<>();
        }

        @Override
        public MetisFieldSet<MoneyWisePortfolioDataMap> getDataFieldSet() {
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

        /**
         * Obtain the underlying map.
         * @return the underlying map
         */
        private Map<Integer, MoneyWisePortfolio> getPortfolioMap() {
            return thePortfolioMap;
        }

        /**
         * Obtain the underlying map.
         * @return the underlying map
         */
        private Map<Integer, Integer> getPortfolioCountMap() {
            return thePortfolioCountMap;
        }

        @Override
        public void resetMap() {
            thePortfolioCountMap.clear();
            thePortfolioMap.clear();
        }

        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* If the class is singular */
            final MoneyWisePortfolio myItem = (MoneyWisePortfolio) pItem;
            final MoneyWisePortfolioClass myClass = myItem.getCategoryClass();
            if (myClass.isSingular()) {
                /* Adjust category count */
                final Integer myId = myClass.getClassId();
                final Integer myCount = thePortfolioCountMap.get(myId);
                if (myCount == null) {
                    thePortfolioCountMap.put(myId, PrometheusDataInstanceMap.ONE);
                } else {
                    thePortfolioCountMap.put(myId, myCount + 1);
                }

                /* Adjust portfolio map */
                thePortfolioMap.put(myId, myItem);
            }

            /* Adjust name count */
            theUnderlyingMap.adjustForItem(pItem);
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public MoneyWisePortfolio findItemByName(final String pName) {
            final MoneyWiseAssetBase myAsset = theUnderlyingMap.findAssetByName(pName);
            return myAsset instanceof MoneyWisePortfolio
                    ? (MoneyWisePortfolio) myAsset
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

        /**
         * find singular item.
         * @param pClass the class to look up
         * @return the matching item
         */
        public MoneyWisePortfolio findSingularItem(final MoneyWisePortfolioClass pClass) {
            return thePortfolioMap.get(pClass.getClassId());
        }

        /**
         * Check validity of singular count.
         * @param pClass the class to look up
         * @return true/false
         */
        public boolean validSingularCount(final MoneyWisePortfolioClass pClass) {
            final Integer myResult = thePortfolioCountMap.get(pClass.getClassId());
            return PrometheusDataInstanceMap.ONE.equals(myResult);
        }
    }
}