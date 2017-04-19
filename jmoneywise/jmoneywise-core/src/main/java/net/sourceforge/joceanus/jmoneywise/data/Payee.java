/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEditState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.data.CategoryBase.CategoryDataMap;
import net.sourceforge.joceanus.jmoneywise.data.PayeeInfo.PayeeInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jprometheus.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Payee class.
 */
public class Payee
        extends AssetBase<Payee>
        implements InfoSetItem<MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.PAYEE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.PAYEE.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * PayeeType Field Id.
     */
    public static final MetisField FIELD_PAYEETYPE = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataType.PAYEETYPE.getItemName(), MetisDataType.LINK);

    /**
     * PayeeInfoSet field Id.
     */
    private static final MetisField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseDataResource.PAYEE_NEWACCOUNT.getValue();

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
    private final PayeeInfoSet theInfoSet;

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPayee The Payee to copy
     */
    protected Payee(final PayeeList pList,
                    final Payee pPayee) {
        /* Set standard values */
        super(pList, pPayee);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new PayeeInfoSet(this, pList.getActInfoTypes(), pList.getPayeeInfo());
                theInfoSet.cloneDataInfoSet(pPayee.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new PayeeInfoSet(this, pList.getActInfoTypes(), pList.getPayeeInfo());
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
    private Payee(final PayeeList pList,
                  final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the PayeeType */
        Object myValue = pValues.getValue(FIELD_PAYEETYPE);
        if (myValue instanceof Integer) {
            setValueType((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueType((String) myValue);
        }

        /* Create the InfoSet */
        theInfoSet = new PayeeInfoSet(this, pList.getActInfoTypes(), pList.getPayeeInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Payee(final PayeeList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new PayeeInfoSet(this, pList.getActInfoTypes(), pList.getPayeeInfo());
        hasInfoSet = true;
        useInfoSet = true;
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisField pField) {
        /* Determine whether fields should be included */
        if (FIELD_PAYEETYPE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                              ? theInfoSet
                              : MetisFieldValue.SKIP;
        }

        /* Handle infoSet fields */
        AccountInfoClass myClass = PayeeInfoSet.getClassForField(pField);
        if ((theInfoSet != null) && (myClass != null)) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public PayeeInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain WebSite.
     * @return the webSite
     */
    public char[] getWebSite() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.WEBSITE, char[].class)
                          : null;
    }

    /**
     * Obtain CustNo.
     * @return the customer #
     */
    public char[] getCustNo() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.CUSTOMERNO, char[].class)
                          : null;
    }

    /**
     * Obtain UserId.
     * @return the userId
     */
    public char[] getUserId() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.USERID, char[].class)
                          : null;
    }

    /**
     * Obtain Password.
     * @return the password
     */
    public char[] getPassword() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.PASSWORD, char[].class)
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
     * Obtain Payee Type.
     * @return the type
     */
    public PayeeType getPayeeType() {
        return getPayeeType(getValueSet());
    }

    /**
     * Obtain PayeeTypeId.
     * @return the categoryTypeId
     */
    public Integer getPayeeTypeId() {
        PayeeType myType = getPayeeType();
        return (myType == null)
                                ? null
                                : myType.getId();
    }

    /**
     * Obtain PayeeTypeName.
     * @return the payeeTypeName
     */
    public String getPayeeTypeName() {
        PayeeType myType = getPayeeType();
        return (myType == null)
                                ? null
                                : myType.getName();
    }

    /**
     * Obtain PayeeTypeClass.
     * @return the payeeTypeClass
     */
    public PayeeTypeClass getPayeeTypeClass() {
        PayeeType myType = getPayeeType();
        return (myType == null)
                                ? null
                                : myType.getPayeeClass();
    }

    /**
     * Obtain PayeeType.
     * @param pValueSet the valueSet
     * @return the PayeeType
     */
    public static PayeeType getPayeeType(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PAYEETYPE, PayeeType.class);
    }

    /**
     * Set payee type value.
     * @param pValue the value
     */
    private void setValueType(final PayeeType pValue) {
        getValueSet().setValue(FIELD_PAYEETYPE, pValue);
    }

    /**
     * Set payee type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_PAYEETYPE, pValue);
    }

    /**
     * Set payee type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValueSet().setValue(FIELD_PAYEETYPE, pValue);
    }

    @Override
    public Payee getBase() {
        return (Payee) super.getBase();
    }

    @Override
    public PayeeList getList() {
        return (PayeeList) super.getList();
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
    public MetisEditState getEditState() {
        /* Pop history for self */
        MetisEditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if ((myState == MetisEditState.CLEAN) && useInfoSet) {
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
    public MetisDifference fieldChanged(final MetisField pField) {
        /* Handle InfoSet fields */
        AccountInfoClass myClass = PayeeInfoSet.getClassForField(pField);
        if (myClass != null) {
            return useInfoSet
                              ? theInfoSet.fieldChanged(myClass)
                              : MetisDifference.IDENTICAL;
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
    public boolean isPayeeClass(final PayeeTypeClass pClass) {
        /* Check for match */
        return getPayeeTypeClass() == pClass;
    }

    /**
     * Is the category hidden?
     * @return true/false
     */
    @Override
    public boolean isHidden() {
        PayeeTypeClass myClass = this.getPayeeTypeClass();
        return myClass != null
               && myClass.isHiddenType();
    }

    /**
     * Set defaults.
     * @throws OceanusException on error
     */
    public void setDefaults() throws OceanusException {
        /* Set values */
        setPayeeType(getDefaultPayeeType());
        setName(getList().getUniqueName(NAME_NEWACCOUNT));
        setClosed(Boolean.FALSE);
    }

    /**
     * Obtain payee type for new payee account.
     * @return the payee type
     */
    private PayeeType getDefaultPayeeType() {
        /* Access payee types */
        PayeeTypeList myTypes = getDataSet().getPayeeTypes();

        /* loop through the payee types */
        Iterator<PayeeType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            PayeeType myType = myIterator.next();

            /* Ignore deleted and singular types */
            if (!myType.isDeleted() && !myType.getPayeeClass().isSingular()) {
                return myType;
            }
        }

        /* Return no category */
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
            && (pThat instanceof Payee)) {
            /* Check the payee type */
            Payee myThat = (Payee) pThat;
            iDiff = MetisDifference.compareObject(getPayeeType(), myThat.getPayeeType());
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
        /* Update the Base details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_PAYEETYPE, myData.getPayeeTypes());
    }

    /**
     * Set a new payee type.
     * @param pType the new type
     */
    public void setPayeeType(final PayeeType pType) {
        setValueType(pType);
    }

    /**
     * Set a new WebSite.
     * @param pWebSite the new webSite
     * @throws OceanusException on error
     */
    public void setWebSite(final char[] pWebSite) throws OceanusException {
        setInfoSetValue(AccountInfoClass.WEBSITE, pWebSite);
    }

    /**
     * Set a new CustNo.
     * @param pCustNo the new custNo
     * @throws OceanusException on error
     */
    public void setCustNo(final char[] pCustNo) throws OceanusException {
        setInfoSetValue(AccountInfoClass.CUSTOMERNO, pCustNo);
    }

    /**
     * Set a new UserId.
     * @param pUserId the new userId
     * @throws OceanusException on error
     */
    public void setUserId(final char[] pUserId) throws OceanusException {
        setInfoSetValue(AccountInfoClass.USERID, pUserId);
    }

    /**
     * Set a new Password.
     * @param pPassword the new password
     * @throws OceanusException on error
     */
    public void setPassword(final char[] pPassword) throws OceanusException {
        setInfoSetValue(AccountInfoClass.PASSWORD, pPassword);
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
        /* touch the payee type */
        getPayeeType().touchItem(this);

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    @Override
    public void touchOnUpdate() {
        /* Reset touches from update set */
        clearTouches(MoneyWiseDataType.CASHINFO);
        clearTouches(MoneyWiseDataType.SECURITY);
        clearTouches(MoneyWiseDataType.DEPOSIT);
        clearTouches(MoneyWiseDataType.LOAN);
        clearTouches(MoneyWiseDataType.PORTFOLIO);
    }

    @Override
    public void validate() {
        PayeeList myList = getList();
        PayeeType myPayeeType = getPayeeType();

        /* Validate base components */
        super.validate();

        /* PayeeType must be non-null */
        if (myPayeeType == null) {
            addError(ERROR_MISSING, FIELD_PAYEETYPE);
        } else {
            /* Access the class */
            PayeeTypeClass myClass = myPayeeType.getPayeeClass();

            /* PayeeType must be enabled */
            if (!myPayeeType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_PAYEETYPE);
            }

            /* If the PayeeType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                PayeeDataMap myMap = myList.getDataMap();
                if (!myMap.validSingularCount(myClass)) {
                    addError(ERROR_MULT, FIELD_PAYEETYPE);
                }
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
     * Update base payee from an edited payee.
     * @param pPayee the edited payee
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pPayee) {
        /* Can only update from a payee */
        if (!(pPayee instanceof Payee)) {
            return false;
        }
        Payee myPayee = (Payee) pPayee;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myPayee);

        /* Update the category type if required */
        if (!MetisDifference.isEqual(getPayeeType(), myPayee.getPayeeType())) {
            setValueType(myPayee.getPayeeType());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        PayeeList myList = getList();
        PayeeDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Payee List class.
     */
    public static class PayeeList
            extends AssetBaseList<Payee> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, DataList.FIELD_DEFS);

        /**
         * The PayeeInfo List.
         */
        private PayeeInfoList theInfoList = null;

        /**
         * The AccountInfoType list.
         */
        private AccountInfoTypeList theInfoTypeList = null;

        /**
         * Construct an empty CORE Payee list.
         * @param pData the DataSet for the list
         */
        public PayeeList(final MoneyWiseData pData) {
            super(pData, Payee.class, MoneyWiseDataType.PAYEE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected PayeeList(final PayeeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return Payee.FIELD_DEFS;
        }

        @Override
        protected PayeeDataMap getDataMap() {
            return (PayeeDataMap) super.getDataMap();
        }

        /**
         * Obtain the payeeInfoList.
         * @return the payee info list
         */
        public PayeeInfoList getPayeeInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getPayeeInfo();
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
        protected PayeeList getEmptyList(final ListStyle pStyle) {
            PayeeList myList = new PayeeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public PayeeList deriveEditList() {
            /* Build an empty List */
            PayeeList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            PayeeInfoList myPayeeInfo = getPayeeInfo();
            myList.theInfoList = myPayeeInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the payees */
            Iterator<Payee> myIterator = iterator();
            while (myIterator.hasNext()) {
                Payee myCurr = myIterator.next();

                /* Ignore deleted payees */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked payee and add it to the list */
                Payee myPayee = new Payee(myList, myCurr);
                myList.append(myPayee);

                /* Adjust the map */
                myPayee.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        @Override
        public Payee findItemByName(final String pName) {
            /* Look up in map */
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
         * Add a new item to the core list.
         * @param pPayee item
         * @return the newly added item
         */
        @Override
        public Payee addCopyItem(final DataItem<?> pPayee) {
            /* Can only clone a Payee */
            if (!(pPayee instanceof Payee)) {
                throw new UnsupportedOperationException();
            }

            Payee myPayee = new Payee(this, (Payee) pPayee);
            add(myPayee);
            return myPayee;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public Payee addNewItem() {
            Payee myPayee = new Payee(this);
            add(myPayee);
            return myPayee;
        }

        /**
         * Obtain the first payee for the specified class.
         * @param pClass the payee class
         * @return the payee
         */
        public Payee getSingularClass(final PayeeTypeClass pClass) {
            /* Lookup in the map */
            return getDataMap().findSingularItem(pClass);
        }

        @Override
        public Payee addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the payee */
            Payee myPayee = new Payee(this, pValues);

            /* Check that this PayeeId has not been previously added */
            if (!isIdUnique(myPayee.getId())) {
                myPayee.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myPayee, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPayee);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    DataValues<MoneyWiseDataType> myValues = myItem.getValues(myPayee);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myPayee;
        }

        @Override
        protected PayeeDataMap allocateDataMap() {
            return new PayeeDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    protected static class PayeeDataMap
            extends DataInstanceMap<Payee, MoneyWiseDataType, String> {
        /**
         * Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(PrometheusDataResource.DATAMAP_NAME.getValue(), CategoryDataMap.FIELD_DEFS);

        /**
         * CategoryMap Field Id.
         */
        public static final MetisField FIELD_CATMAP = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_MAP_SINGULARMAP.getValue());

        /**
         * CategoryCountMap Field Id.
         */
        public static final MetisField FIELD_CATCOUNT = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_MAP_SINGULARCOUNTS.getValue());

        /**
         * Map of category counts.
         */
        private final Map<Integer, Integer> thePayeeCountMap;

        /**
         * Map of singular categories.
         */
        private final Map<Integer, Payee> thePayeeMap;

        /**
         * Constructor.
         */
        public PayeeDataMap() {
            /* Create the maps */
            thePayeeCountMap = new HashMap<>();
            thePayeeMap = new HashMap<>();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_CATMAP.equals(pField)) {
                return thePayeeMap;
            }
            if (FIELD_CATCOUNT.equals(pField)) {
                return thePayeeCountMap;
            }

            /* Unknown */
            return super.getFieldValue(pField);
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public void resetMap() {
            super.resetMap();
            thePayeeCountMap.clear();
            thePayeeMap.clear();
        }

        @Override
        public void adjustForItem(final Payee pItem) {
            /* If the class is singular */
            PayeeTypeClass myClass = pItem.getPayeeTypeClass();
            if (myClass.isSingular()) {
                /* Adjust category count */
                Integer myId = myClass.getClassId();
                Integer myCount = thePayeeCountMap.get(myId);
                if (myCount == null) {
                    thePayeeCountMap.put(myId, ONE);
                } else {
                    thePayeeCountMap.put(myId, myCount + 1);
                }

                /* Adjust payee map */
                thePayeeMap.put(myId, pItem);
            }

            /* Adjust name count */
            adjustForItem(pItem, pItem.getName());
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public Payee findItemByName(final String pName) {
            return findItemByKey(pName);
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
        public Payee findSingularItem(final PayeeTypeClass pClass) {
            return thePayeeMap.get(pClass.getClassId());
        }

        /**
         * Check validity of singular count.
         * @param pClass the class to look up
         * @return true/false
         */
        public boolean validSingularCount(final PayeeTypeClass pClass) {
            Integer myResult = thePayeeCountMap.get(pClass.getClassId());
            return ONE.equals(myResult);
        }
    }
}
