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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Security class.
 */
public class Security
        extends AssetBase
        implements InfoSetItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.SECURITY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.SECURITY.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * SecurityInfoSet field Id.
     */
    private static final MetisLetheField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseDataResource.SECURITY_NEWACCOUNT.getValue();

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
    private final SecurityInfoSet theInfoSet;

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pSecurity The Security to copy
     */
    protected Security(final SecurityList pList,
                       final Security pSecurity) {
        /* Set standard values */
        super(pList, pSecurity);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new SecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
                theInfoSet.cloneDataInfoSet(pSecurity.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new SecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
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
    private Security(final SecurityList pList,
                     final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Create the InfoSet */
        theInfoSet = new SecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Security(final SecurityList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new SecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
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
        final AccountInfoClass myClass = SecurityInfoSet.getClassForField(pField);
        if (theInfoSet != null
            && myClass != null) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public SecurityInfoSet getInfoSet() {
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
     * Obtain Symbol.
     * @return the symbol
     */
    public String getSymbol() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.SYMBOL, String.class)
                          : null;
    }

    /**
     * Obtain Region.
     * @return the region
     */
    public Region getRegion() {
        return hasInfoSet
                          ? theInfoSet.getRegion(AccountInfoClass.REGION)
                          : null;
    }

    /**
     * Obtain UnderlyingStock.
     * @return the stock
     */
    public Security getUnderlyingStock() {
        return hasInfoSet
                          ? theInfoSet.getSecurity(AccountInfoClass.UNDERLYINGSTOCK)
                          : null;
    }

    /**
     * Obtain OptionPrice.
     * @return the price
     */
    public TethysPrice getOptionPrice() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.OPTIONPRICE, TethysPrice.class)
                          : null;
    }

    @Override
    public SecurityType getCategory() {
        return getCategory(getValueSet());
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        final SecurityType myType = getCategory();
        return myType == null
                              ? null
                              : myType.getId();
    }

    /**
     * Obtain CategoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        final SecurityType myType = getCategory();
        return myType == null
                              ? null
                              : myType.getName();
    }

    /**
     * Obtain SecurityTypeClass.
     * @return the securityTypeClass
     */
    public SecurityTypeClass getCategoryClass() {
        final SecurityType myType = getCategory();
        return myType == null
                              ? null
                              : myType.getSecurityClass();
    }

    @Override
    public Boolean isForeign() {
        final AssetCurrency myDefault = getDataSet().getDefaultCurrency();
        return !myDefault.equals(getAssetCurrency());
    }

    /**
     * Obtain SecurityType.
     * @param pValueSet the valueSet
     * @return the SecurityType
     */
    public static SecurityType getCategory(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATEGORY, SecurityType.class);
    }

    @Override
    public Security getBase() {
        return (Security) super.getBase();
    }

    @Override
    public SecurityList getList() {
        return (SecurityList) super.getList();
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
        final AccountInfoClass myClass = SecurityInfoSet.getClassForField(pField);
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
     * @param pClass the required security class.
     * @return true/false
     */
    public boolean isSecurityClass(final SecurityTypeClass pClass) {
        /* Check for match */
        return getCategoryClass() == pClass;
    }

    @Override
    public boolean isShares() {
        return isSecurityClass(SecurityTypeClass.SHARES);
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
        final SecurityHoldingMap myMap = getDataSet().getSecurityHoldingsMap();
        myMap.deRegister(this);
    }

    /**
     * Set defaults.
     * @param pUpdateSet the update set
     * @throws OceanusException on error
     */
    public void setDefaults(final UpdateSet pUpdateSet) throws OceanusException {
        /* Set values */
        setName(getList().getUniqueName(NAME_NEWACCOUNT));
        setCategory(getDefaultSecurityType());
        setAssetCurrency(getDataSet().getDefaultCurrency());
        setSymbol(getName());
        setClosed(Boolean.FALSE);
        autoCorrect(pUpdateSet);
    }

    /**
     * autoCorrect values after change.
     * @param pUpdateSet the update set
     */
    public void autoCorrect(final UpdateSet pUpdateSet) {
        /* Access category class and parent */
        final SecurityTypeClass myClass = getCategoryClass();
        final Payee myParent = getParent();

        /* Ensure that we have a valid parent */
        if ((myParent == null)
            || myParent.getCategoryClass().canParentSecurity(myClass)) {
            setParent(getDefaultParent(pUpdateSet));
        }
    }

    /**
     * Obtain security type for new security account.
     * @return the security type
     */
    public SecurityType getDefaultSecurityType() {
        /* loop through the security types */
        final SecurityTypeList myTypes = getDataSet().getSecurityTypes();
        final Iterator<SecurityType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final SecurityType myType = myIterator.next();

            /* Ignore deleted types */
            if (!myType.isDeleted()) {
                return myType;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default parent for new security.
     * @param pUpdateSet the update set
     * @return the default parent
     */
    private Payee getDefaultParent(final UpdateSet pUpdateSet) {
        /* Access details */
        final PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        final SecurityTypeClass myClass = getCategoryClass();

        /* loop through the payees */
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (myPayee.isDeleted() || myPayee.isClosed()) {
                continue;
            }

            /* If the payee can parent */
            if (myPayee.getCategoryClass().canParentSecurity(myClass)) {
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
        if ((iDiff == 0)
            && (pThat instanceof Security)) {
            /* Check the security type */
            final Security myThat = (Security) pThat;
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
        resolveDataLink(FIELD_CATEGORY, myData.getSecurityTypes());
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
     * Set a new Notes.
     * @param pNotes the new notes
     * @throws OceanusException on error
     */
    public void setNotes(final char[] pNotes) throws OceanusException {
        setInfoSetValue(AccountInfoClass.NOTES, pNotes);
    }

    /**
     * Set a new symbol.
     * @param pSymbol the symbol
     * @throws OceanusException on error
     */
    public void setSymbol(final String pSymbol) throws OceanusException {
        setInfoSetValue(AccountInfoClass.SYMBOL, pSymbol);
    }

    /**
     * Set a new region.
     * @param pRegion the new region
     * @throws OceanusException on error
     */
    public void setRegion(final Region pRegion) throws OceanusException {
        setInfoSetValue(AccountInfoClass.REGION, pRegion);
    }

    /**
     * Set a new underlying stock.
     * @param pStock the new stock
     * @throws OceanusException on error
     */
    public void setUnderlyingStock(final Security pStock) throws OceanusException {
        setInfoSetValue(AccountInfoClass.UNDERLYINGSTOCK, pStock);
    }

    /**
     * Set a new option price.
     * @param pPrice the new price
     * @throws OceanusException on error
     */
    public void setOptionPrice(final TethysPrice pPrice) throws OceanusException {
        setInfoSetValue(AccountInfoClass.OPTIONPRICE, pPrice);
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
    public TransactionCategory getDetailedCategory(final TransactionCategory pCategory,
                                                   final MoneyWiseTaxCredit pYear) {
        /* Switch on category type */
        if (TransactionCategoryClass.DIVIDEND.equals(pCategory.getCategoryTypeClass())) {
            final TransactionCategoryList myCategories = getDataSet().getTransCategories();
            if (isForeign()) {
                return myCategories.getSingularClass(TransactionCategoryClass.FOREIGNDIVIDEND);
            }
            return myCategories.getSingularClass(getCategoryClass().isUnitTrust()
                                                                                 ? TransactionCategoryClass.UNITTRUSTDIVIDEND
                                                                                 : TransactionCategoryClass.SHAREDIVIDEND);
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
        clearTouches(MoneyWiseDataType.SECURITYPRICE);

        /* Touch parent */
        getParent().touchItem(this);
    }

    @Override
    public void validate() {
        final SecurityList myList = getList();
        final Payee myParent = getParent();
        final SecurityType mySecType = getCategory();
        final AssetCurrency myCurrency = getAssetCurrency();
        final String mySymbol = getSymbol();

        /* Validate base components */
        super.validate();

        /* SecurityType must be non-null */
        if (mySecType == null) {
            addError(ERROR_MISSING, FIELD_CATEGORY);
        } else {
            /* Access the class */
            final SecurityTypeClass myClass = mySecType.getSecurityClass();

            /* SecurityType must be enabled */
            if (!mySecType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_CATEGORY);
            }

            /* If the SecurityType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                final SecurityDataMap myMap = myList.getDataMap();
                if (!myMap.validSingularCount(myClass)) {
                    addError(ERROR_MULT, FIELD_CATEGORY);
                }
            }
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            addError(ERROR_MISSING, FIELD_CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_CURRENCY);
        }

        /* Parent must be non-null */
        if (myParent == null) {
            addError(ERROR_MISSING, FIELD_PARENT);
        } else {
            /* If we are open then parent must be open */
            if (!isClosed() && myParent.isClosed()) {
                addError(ERROR_PARCLOSED, FIELD_CLOSED);
            }

            /* Check class */
            if (mySecType != null) {
                /* Access the classes */
                final SecurityTypeClass myClass = mySecType.getSecurityClass();
                final PayeeTypeClass myParClass = myParent.getCategoryClass();

                /* Parent must be suitable */
                if (!myParClass.canParentSecurity(myClass)) {
                    addError(ERROR_BADPARENT, FIELD_PARENT);
                }
            }
        }

        /* If we have a securityType */
        if (mySecType != null) {
            /* Check symbol rules */
            if (mySecType.getSecurityClass().needsSymbol()) {
                if (mySymbol == null) {
                    addError(ERROR_MISSING, SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL));
                } else if (!getList().validSymbolCount(mySymbol)) {
                    addError(ERROR_DUPLICATE, SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL));
                }
            } else if (mySymbol != null) {
                addError(ERROR_EXIST, SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL));
            }
        }

        /* If we have an infoSet */
        if (theInfoSet != null) {
            /* Validate the InfoSet */
            theInfoSet.validate();
        }

        /* Set validation flag */
        if (!

        hasErrors()) {
            setValidEdit();
        }
    }

    @Override
    protected void validateName(final String pName) {
        /* Perform basic checks */
        super.validateName(pName);

        /* Check that the name is not a reserved name */
        if (pName.equals(SecurityHolding.SECURITYHOLDING_NEW)
            || pName.equals(Portfolio.NAME_CASHACCOUNT)) {
            addError(ERROR_RESERVED, FIELD_NAME);
        }
    }

    /**
     * Update base security from an edited security.
     * @param pSecurity the edited security
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pSecurity) {
        /* Can only update from a security */
        if (!(pSecurity instanceof Security)) {
            return false;
        }
        final Security mySecurity = (Security) pSecurity;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(mySecurity);

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final SecurityList myList = getList();
        final SecurityDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Security List class.
     */
    public static class SecurityList
            extends AssetBaseList<Security> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<SecurityList> FIELD_DEFS = MetisFieldSet.newFieldSet(SecurityList.class);

        /**
         * The SecurityInfo List.
         */
        private SecurityInfoList theInfoList;

        /**
         * The AccountInfoType list.
         */
        private AccountInfoTypeList theInfoTypeList;

        /**
         * Construct an empty CORE Security list.
         * @param pData the DataSet for the list
         */
        public SecurityList(final MoneyWiseData pData) {
            super(pData, Security.class, MoneyWiseDataType.SECURITY);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected SecurityList(final SecurityList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<SecurityList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return Security.FIELD_DEFS;
        }

        @Override
        protected SecurityDataMap getDataMap() {
            return (SecurityDataMap) super.getDataMap();
        }

        /**
         * Obtain the securityInfoList.
         * @return the security info list
         */
        public SecurityInfoList getSecurityInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getSecurityInfo();
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
        protected SecurityList getEmptyList(final ListStyle pStyle) {
            final SecurityList myList = new SecurityList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public SecurityList deriveEditList(final UpdateSet pUpdateSet) throws OceanusException {
            /* Build an empty List */
            final SecurityList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            final SecurityInfoList mySecInfo = getSecurityInfo();
            myList.theInfoList = mySecInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the securities */
            final Iterator<Security> myIterator = iterator();
            while (myIterator.hasNext()) {
                final Security myCurr = myIterator.next();

                /* Ignore deleted securities */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked security and add it to the list */
                final Security mySecurity = new Security(myList, myCurr);
                mySecurity.resolveUpdateSetLinks(pUpdateSet);
                myList.add(mySecurity);

                /* Adjust the map */
                mySecurity.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        @Override
        public Security findItemByName(final String pName) {
            /* look up the name in the map */
            return getDataMap().findItemByName(pName);
        }

        @Override
        protected boolean checkAvailableName(final String pName) {
            /* check availability */
            return findItemByName(pName) == null;
        }

        @Override
        protected boolean validNameCount(final String pName) {
            /* check availability in map */
            return getDataMap().validNameCount(pName);
        }

        /**
         * Find the item that uses the symbol.
         * @param pSymbol the symbol to lookup
         * @return the item (or null)
         */
        public Security findItemBySymbol(final String pSymbol) {
            /* look up the symbol in the map */
            return getDataMap().findItemBySymbol(pSymbol);
        }

        /**
         * check that symbol is unique.
         * @param pSymbol the symbol
         * @return true/false
         */
        protected boolean validSymbolCount(final String pSymbol) {
            /* check availability in map */
            return getDataMap().validSymbolCount(pSymbol);
        }

        /**
         * Add a new item to the core list.
         * @param pSecurity item
         * @return the newly added item
         */
        @Override
        public Security addCopyItem(final DataItem pSecurity) {
            /* Can only clone a Security */
            if (!(pSecurity instanceof Security)) {
                throw new UnsupportedOperationException();
            }

            final Security mySecurity = new Security(this, (Security) pSecurity);
            add(mySecurity);
            return mySecurity;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public Security addNewItem() {
            final Security mySecurity = new Security(this);
            add(mySecurity);
            return mySecurity;
        }

        /**
         * Obtain the first security for the specified class.
         * @param pClass the security class
         * @return the security
         */
        public Security getSingularClass(final SecurityTypeClass pClass) {
            /* Lookup in the map */
            return getDataMap().findSingularItem(pClass);
        }

        @Override
        public Security addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the security */
            final Security mySecurity = new Security(this, pValues);

            /* Check that this SecurityId has not been previously added */
            if (!isIdUnique(mySecurity.getId())) {
                mySecurity.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(mySecurity, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(mySecurity);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<InfoItem> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final InfoItem myItem = myIterator.next();

                    /* Build info */
                    final DataValues myValues = myItem.getValues(mySecurity);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return mySecurity;
        }

        @Override
        protected SecurityDataMap allocateDataMap() {
            return new SecurityDataMap();
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
    protected static class SecurityDataMap
            extends DataInstanceMap<Security, String> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<SecurityDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(SecurityDataMap.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_MAP_SINGULARMAP, SecurityDataMap::getSingularMap);
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_MAP_SINGULARCOUNTS, SecurityDataMap::getSingularCountMap);
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.SECURITY_SYMBOLMAP, SecurityDataMap::getSymbolMap);
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.SECURITY_SYMBOLCOUNTMAP, SecurityDataMap::getSymbolCountMap);
        }

        /**
         * Map of symbol counts.
         */
        private final Map<String, Integer> theSymbolCountMap;

        /**
         * Map of symbols.
         */
        private final Map<String, Security> theSymbolMap;

        /**
         * Map of category counts.
         */
        private final Map<Integer, Integer> theSecurityCountMap;

        /**
         * Map of singular categories.
         */
        private final Map<Integer, Security> theSecurityMap;

        /**
         * Constructor.
         */
        public SecurityDataMap() {
            /* Create the maps */
            theSecurityCountMap = new HashMap<>();
            theSecurityMap = new HashMap<>();
            theSymbolCountMap = new HashMap<>();
            theSymbolMap = new HashMap<>();
        }

        @Override
        public MetisFieldSet<SecurityDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain the securityMap.
         * @return the map
         */
        private Map<Integer, Security> getSingularMap() {
            return theSecurityMap;
        }

        /**
         * Obtain the securityCountMap.
         * @return the map
         */
        private Map<Integer, Integer> getSingularCountMap() {
            return theSecurityCountMap;
        }

        /**
         * Obtain the keyMap.
         * @return the map
         */
        private Map<String, Security> getSymbolMap() {
            return theSymbolMap;
        }

        /**
         * Obtain the keyCountMap.
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
        public void adjustForItem(final DataItem pItem) {
            /* If the class is singular */
            final Security myItem = (Security) pItem;
            final SecurityTypeClass myClass = myItem.getCategoryClass();
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
         * @param pSymbol the symbol to look up
         * @return the matching item
         */
        public Security findItemBySymbol(final String pSymbol) {
            return theSymbolMap.get(pSymbol);
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public Security findItemByName(final String pName) {
            return findItemByKey(pName);
        }

        /**
         * Check validity of symbol count.
         * @param pSymbol the symbol to look up
         * @return true/false
         */
        public boolean validSymbolCount(final String pSymbol) {
            final Integer myResult = theSymbolCountMap.get(pSymbol);
            return ONE.equals(myResult);
        }

        /**
         * Check validity of name.
         * @param pName the name to look up
         * @return true/false
         */
        public boolean validNameCount(final String pName) {
            return validKeyCount(pName);
        }

        /**
         * Check availability of name.
         * @param pName the key to look up
         * @return true/false
         */
        public boolean availableName(final String pName) {
            return availableKey(pName);
        }

        /**
         * find singular item.
         * @param pClass the class to look up
         * @return the matching item
         */
        public Security findSingularItem(final SecurityTypeClass pClass) {
            return theSecurityMap.get(pClass.getClassId());
        }

        /**
         * Check validity of singular count.
         * @param pClass the class to look up
         * @return true/false
         */
        public boolean validSingularCount(final SecurityTypeClass pClass) {
            final Integer myResult = theSecurityCountMap.get(pClass.getClassId());
            return ONE.equals(myResult);
        }
    }
}
