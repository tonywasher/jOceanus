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

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Payee class.
 */
public class Payee
        extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<Payee> {
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
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataName"));

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDesc"));

    /**
     * PayeeType Field Id.
     */
    public static final JDataField FIELD_PAYEETYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.PAYEETYPE.getItemName());

    /**
     * isClosed Field Id.
     */
    public static final JDataField FIELD_CLOSED = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataClosed"));

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_NAME.equals(pField)) {
            return true;
        }
        if (FIELD_DESC.equals(pField)) {
            return getDesc() != null;
        }
        if (FIELD_PAYEETYPE.equals(pField)) {
            return true;
        }
        if (FIELD_CLOSED.equals(pField)) {
            return isClosed();
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain Name.
     * @return the name
     */
    public String getName() {
        return getName(getValueSet());
    }

    /**
     * Obtain Encrypted name.
     * @return the bytes
     */
    public byte[] getNameBytes() {
        return getNameBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Name Field.
     * @return the Field
     */
    private EncryptedString getNameField() {
        return getNameField(getValueSet());
    }

    /**
     * Obtain Description.
     * @return the description
     */
    public String getDesc() {
        return getDesc(getValueSet());
    }

    /**
     * Obtain Encrypted description.
     * @return the bytes
     */
    public byte[] getDescBytes() {
        return getDescBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Description Field.
     * @return the Field
     */
    private EncryptedString getDescField() {
        return getDescField(getValueSet());
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
     * Is the payee closed?
     * @return true/false
     */
    public Boolean isClosed() {
        return isClosed(getValueSet());
    }

    /**
     * Obtain Name.
     * @param pValueSet the valueSet
     * @return the Name
     */
    public static String getName(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    /**
     * Obtain Encrypted Name.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getNameBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    /**
     * Obtain Encrypted name field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedString getNameField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, EncryptedString.class);
    }

    /**
     * Obtain Description.
     * @param pValueSet the valueSet
     * @return the description
     */
    public static String getDesc(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    /**
     * Obtain Encrypted description.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getDescBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    /**
     * Obtain Encrypted description field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedString getDescField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, EncryptedString.class);
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
     * Is the payee closed?
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isClosed(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CLOSED, Boolean.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValueName(final String pValue) throws JOceanusException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     */
    private void setValueName(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_NAME, pValue);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValueDesc(final String pValue) throws JOceanusException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set description value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueDesc(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set description value.
     * @param pValue the value
     */
    private void setValueDesc(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_DESC, pValue);
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

    /**
     * Set closed indication.
     * @param pValue the value
     */
    private void setValueClosed(final Boolean pValue) {
        getValueSet().setValue(FIELD_CLOSED, (pValue != null)
                                                             ? pValue
                                                             : Boolean.FALSE);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public Payee getBase() {
        return (Payee) super.getBase();
    }

    @Override
    public PayeeList getList() {
        return (PayeeList) super.getList();
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
     * Copy Constructor.
     * @param pList the list
     * @param pPayee The Payee to copy
     */
    protected Payee(final PayeeList pList,
                    final Payee pPayee) {
        /* Set standard values */
        super(pList, pPayee);
    }

    /**
     * Secure constructor.
     * @param pList the List to add to
     * @param pId the Category id
     * @param pControlId the control id
     * @param pName the Encrypted Name of the payee
     * @param pDesc the Encrypted Description of the payee
     * @param pPayeeTypeId the id of the payee type
     * @param pClosed is the payee closed?
     * @throws JOceanusException on error
     */
    protected Payee(final PayeeList pList,
                    final Integer pId,
                    final Integer pControlId,
                    final byte[] pName,
                    final byte[] pDesc,
                    final Integer pPayeeTypeId,
                    final Boolean pClosed) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the IDs */
            setValueType(pPayeeTypeId);

            /* Set ControlId */
            setControlKey(pControlId);

            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);

            /* Store closed flag */
            setValueClosed(pClosed);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param pId the id
     * @param pName the Name of the event payee
     * @param pDesc the description of the payee
     * @param pPayeeType the Payee type
     * @param pClosed is the payee closed?
     * @throws JOceanusException on error
     */
    protected Payee(final PayeeList pList,
                    final Integer pId,
                    final String pName,
                    final String pDesc,
                    final String pPayeeType,
                    final Boolean pClosed) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the links */
            setValueType(pPayeeType);

            /* Record the string values */
            setValueName(pName);
            setValueDesc(pDesc);

            /* Store closed flag */
            setValueClosed(pClosed);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Payee(final PayeeList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    @Override
    public int compareTo(final Payee pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
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

        /* Check the names */
        iDiff = Difference.compareObject(getName(), pThat.getName());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        MoneyWiseData myData = getDataSet();
        PayeeTypeList myTypes = myData.getPayeeTypes();
        ValueSet myValues = getValueSet();

        /* Adjust Category type */
        Object myPayeeType = myValues.getValue(FIELD_PAYEETYPE);
        if (myPayeeType instanceof EventCategoryType) {
            myPayeeType = ((EventCategoryType) myPayeeType).getId();
        }
        if (myPayeeType instanceof Integer) {
            PayeeType myType = myTypes.findItemById((Integer) myPayeeType);
            if (myType == null) {
                addError(ERROR_UNKNOWN, FIELD_PAYEETYPE);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueType(myType);
        } else if (myPayeeType instanceof String) {
            PayeeType myType = myTypes.findItemByName((String) myPayeeType);
            if (myType == null) {
                addError(ERROR_UNKNOWN, FIELD_PAYEETYPE);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueType(myType);
        }
    }

    /**
     * Set a new payee name.
     * @param pName the new name
     * @throws JOceanusException on error
     */
    public void setPayeeName(final String pName) throws JOceanusException {
        setValueName(pName);
    }

    /**
     * Set a new payee type.
     * @param pType the new type
     */
    public void setPayeeType(final PayeeType pType) {
        setValueType(pType);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws JOceanusException on error
     */
    public void setDescription(final String pDesc) throws JOceanusException {
        setValueDesc(pDesc);
    }

    /**
     * Set a new closed indication.
     * @param isClosed the new closed indication
     */
    public void setClosed(final Boolean isClosed) {
        setValueClosed(isClosed);
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
        String myName = getName();
        String myDesc = getDesc();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, FIELD_NAME);

            /* Check that the name is valid */
        } else {
            /* The name must not be too long */
            if (myName.length() > NAMELEN) {
                addError(ERROR_LENGTH, FIELD_NAME);
            }

            /* The name must be unique */
            if (myList.countInstances(myName) > 1) {
                addError(ERROR_DUPLICATE, FIELD_NAME);
            }
        }

        /* Check description length */
        if ((myDesc != null) && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_DESC);
        }

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

        /* Update the Name if required */
        if (!Difference.isEqual(getName(), myPayee.getName())) {
            setValueName(myPayee.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myPayee.getDesc())) {
            setValueDesc(myPayee.getDescField());
        }

        /* Update the category type if required */
        if (!Difference.isEqual(getPayeeType(), myPayee.getPayeeType())) {
            setValueType(myPayee.getPayeeType());
        }

        /* Update the closed status if required */
        if (!Difference.isEqual(isClosed(), myPayee.isClosed())) {
            setValueClosed(myPayee.isClosed());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Payee List class.
     */
    public static class PayeeList
            extends EncryptedList<Payee, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE Payee list.
         * @param pData the DataSet for the list
         */
        public PayeeList(final MoneyWiseData pData) {
            super(Payee.class, pData, MoneyWiseDataType.PAYEE, ListStyle.CORE);
        }

        @Override
        protected PayeeList getEmptyList(final ListStyle pStyle) {
            PayeeList myList = new PayeeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PayeeList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (PayeeList) super.cloneList(pDataSet);
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

            /* Loop through the payees */
            Iterator<Payee> myIterator = iterator();
            while (myIterator.hasNext()) {
                Payee myCurr = myIterator.next();

                /* Ignore deleted events */
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
                return null;
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
         * Count the instances of a string.
         * @param pName the string to check for
         * @return The # of instances of the name
         */
        protected int countInstances(final String pName) {
            /* Access the iterator */
            Iterator<Payee> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Payee myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
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
         * Search for a particular item by Name.
         * @param pName Name of item
         * @return The Item if present (or null)
         */
        public Payee findItemByName(final String pName) {
            /* Access the iterator */
            Iterator<Payee> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Payee myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
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

        /**
         * Allow a payee to be added.
         * @param pId the id
         * @param pName the name
         * @param pDesc the description
         * @param pPayeeType the payee type
         * @param pClosed is the payee closed
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final String pName,
                                final String pDesc,
                                final String pPayeeType,
                                final Boolean pClosed) throws JOceanusException {
            /* Create the payee */
            Payee myPayee = new Payee(this, pId, pName, pDesc, pPayeeType, pClosed);

            /* Check that this PayeeId has not been previously added */
            if (!isIdUnique(pId)) {
                myPayee.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPayee, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPayee);
        }

        /**
         * Load an Encrypted Payee.
         * @param pId the id
         * @param pControlId the control id
         * @param pName the encrypted name
         * @param pDesc the encrypted description
         * @param pPayeeTypeId the payee id
         * @param pClosed is the payee closed
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final byte[] pName,
                                  final byte[] pDesc,
                                  final Integer pPayeeTypeId,
                                  final Boolean pClosed) throws JOceanusException {
            /* Create the payee */
            Payee myPayee = new Payee(this, pId, pControlId, pName, pDesc, pPayeeTypeId, pClosed);

            /* Check that this PayeeId has not been previously added */
            if (!isIdUnique(pId)) {
                myPayee.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPayee, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPayee);
        }
    }
}
