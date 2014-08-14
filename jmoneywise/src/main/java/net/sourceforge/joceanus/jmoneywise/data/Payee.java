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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.PayeeInfo.PayeeInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Payee.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * PayeeType Field Id.
     */
    public static final JDataField FIELD_PAYEETYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.PAYEETYPE.getItemName());

    /**
     * PayeeInfoSet field Id.
     */
    private static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataInfoSet"));

    /**
     * Bad InfoSet Error Text.
     */
    private static final String ERROR_BADINFOSET = NLS_BUNDLE.getString("ErrorBadInfoSet");

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

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_PAYEETYPE.equals(pField)) {
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
    public static PayeeType getPayeeType(final ValueSet pValueSet) {
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
        AccountInfoClass myClass = PayeeInfoSet.getClassForField(pField);
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
    public boolean isPayeeClass(final PayeeTypeClass pClass) {
        /* Check for match */
        return getPayeeTypeClass() == pClass;
    }

    @Override
    public boolean canDividend() {
        return isPayeeClass(PayeeTypeClass.EMPLOYER);
    }

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
     * @throws JOceanusException on error
     */
    private Payee(final PayeeList pList,
                  final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
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
        setClosed(Boolean.FALSE);
    }

    @Override
    public int compareTo(final Payee pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the payee type */
        int iDiff = Difference.compareObject(getPayeeType(), pThat.getPayeeType());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying base */
        return super.compareTo(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    public void setWebSite(final char[] pWebSite) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.WEBSITE, pWebSite);
    }

    /**
     * Set a new CustNo.
     * @param pCustNo the new custNo
     * @throws JOceanusException on error
     */
    public void setCustNo(final char[] pCustNo) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.CUSTOMERNO, pCustNo);
    }

    /**
     * Set a new UserId.
     * @param pUserId the new userId
     * @throws JOceanusException on error
     */
    public void setUserId(final char[] pUserId) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.USERID, pUserId);
    }

    /**
     * Set a new Password.
     * @param pPassword the new password
     * @throws JOceanusException on error
     */
    public void setPassword(final char[] pPassword) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.PASSWORD, pPassword);
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
        /* touch the payee type */
        getPayeeType().touchItem(this);
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
                int myCount = myList.countInstances(myClass);
                if (myCount > 1) {
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
        if (!Difference.isEqual(getPayeeType(), myPayee.getPayeeType())) {
            setValueType(myPayee.getPayeeType());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Payee List class.
     */
    public static class PayeeList
            extends AssetBaseList<Payee> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The PayeeInfo List.
         */
        private PayeeInfoList theInfoList = null;

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
            return Payee.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
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

        /**
         * Construct an empty CORE Payee list.
         * @param pData the DataSet for the list
         */
        public PayeeList(final MoneyWiseData pData) {
            super(pData, Payee.class, MoneyWiseDataType.PAYEE);
        }

        @Override
        protected PayeeList getEmptyList(final ListStyle pStyle) {
            PayeeList myList = new PayeeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected PayeeList(final PayeeList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public PayeeList deriveEditList() {
            /* Build an empty List */
            PayeeList myList = getEmptyList(ListStyle.EDIT);

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
            }

            /* Return the list */
            return myList;
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
         * Count the instances of a class.
         * @param pClass the event category class
         * @return The # of instances of the class
         */
        protected int countInstances(final PayeeTypeClass pClass) {
            /* Access the iterator */
            Iterator<Payee> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Payee myCurr = myIterator.next();
                if (pClass == myCurr.getPayeeTypeClass()) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Obtain the first payee for the specified class.
         * @param pClass the payee class
         * @return the payee
         */
        public Payee getSingularClass(final PayeeTypeClass pClass) {
            /* Access the iterator */
            Iterator<Payee> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Payee myCurr = myIterator.next();
                if (myCurr.getPayeeTypeClass() == pClass) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        @Override
        public Payee addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the payee */
            Payee myPayee = new Payee(this, pValues);

            /* Check that this PayeeId has not been previously added */
            if (!isIdUnique(myPayee.getId())) {
                myPayee.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPayee, ERROR_VALIDATION);
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
    }
}
