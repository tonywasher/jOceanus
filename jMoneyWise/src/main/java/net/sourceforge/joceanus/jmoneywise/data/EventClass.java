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

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.EncryptedItem;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jgordianknot.EncryptedValueSet;

/**
 * Tag for an event.
 */
public class EventClass
        extends EncryptedItem
        implements Comparable<EventClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = EventClass.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "es";
    /**
     * EventTag Name length.
     */
    public static final int NAMELEN = 30;

    /**
     * EventTag Description length.
     */
    public static final int DESCLEN = 50;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventClass.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataClassName"));

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDesc"));

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
     * Set name value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueName(final String pValue) throws JDataException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws JDataException on error
     */
    private void setValueName(final byte[] pBytes) throws JDataException {
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
     * @throws JDataException on error
     */
    private void setValueDesc(final String pValue) throws JDataException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set description value.
     * @param pBytes the value
     * @throws JDataException on error
     */
    private void setValueDesc(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set description value.
     * @param pValue the value
     */
    private void setValueDesc(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_DESC, pValue);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public EventClass getBase() {
        return (EventClass) super.getBase();
    }

    @Override
    public EventClassList getList() {
        return (EventClassList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pClass The Class to copy
     */
    protected EventClass(final EventClassList pList,
                         final EventClass pClass) {
        /* Set standard values */
        super(pList, pClass);
    }

    /**
     * Secure constructor.
     * @param pList the List to add to
     * @param pId the Class id
     * @param pControlId the control id
     * @param pName the Encrypted Name of the event class
     * @param pDesc the Encrypted Description of the event class
     * @throws JDataException on error
     */
    protected EventClass(final EventClassList pList,
                         final Integer pId,
                         final Integer pControlId,
                         final byte[] pName,
                         final byte[] pDesc) throws JDataException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Set ControlId */
            setControlKey(pControlId);

            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param pId the id
     * @param pName the Name of the event class
     * @param pDesc the description of the event class
     * @throws JDataException on error
     */
    protected EventClass(final EventClassList pList,
                         final Integer pId,
                         final String pName,
                         final String pDesc) throws JDataException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public EventClass(final EventClassList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    @Override
    public int compareTo(final EventClass pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the names */
        int iDiff = Difference.compareObject(getName(), pThat.getName());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    /**
     * Set a new tag name.
     * @param pName the new name
     * @throws JDataException on error
     */
    public void setName(final String pName) throws JDataException {
        setValueName(pName);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws JDataException on error
     */
    public void setDescription(final String pDesc) throws JDataException {
        setValueDesc(pDesc);
    }

    @Override
    public void validate() {
        EventClassList myList = getList();
        String myName = getName();
        String myDesc = getDesc();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, FIELD_NAME);

            /* Else check the name */
        } else {
            /* The description must not be too long */
            if (myName.length() > NAMELEN) {
                addError(ERROR_LENGTH, FIELD_NAME);
            }

            /* Check that the name is unique */
            if (myList.countInstances(myName) > 1) {
                addError(ERROR_DUPLICATE, FIELD_NAME);
            }
        }

        /* Check description length */
        if ((myDesc != null)
            && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_NAME);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Update base tag from an edited tag.
     * @param pClass the edited class
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pClass) {
        /* Can only update from an event class */
        if (!(pClass instanceof EventClass)) {
            return false;
        }
        EventClass myClass = (EventClass) pClass;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Name if required */
        if (!Difference.isEqual(getName(), myClass.getName())) {
            setValueName(myClass.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myClass.getDesc())) {
            setValueDesc(myClass.getDescField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Event Tag List class.
     */
    public static class EventClassList
            extends EncryptedList<EventClass> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), DataList.FIELD_DEFS);

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
         * Construct an empty CORE EventClass list.
         * @param pData the DataSet for the list
         */
        protected EventClassList(final MoneyWiseData pData) {
            super(EventClass.class, pData, ListStyle.CORE);
        }

        @Override
        protected EventClassList getEmptyList(final ListStyle pStyle) {
            EventClassList myList = new EventClassList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public EventClassList cloneList(final DataSet<?, ?> pDataSet) throws JDataException {
            return (EventClassList) super.cloneList(pDataSet);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EventClassList(final EventClassList pSource) {
            super(pSource);
        }

        /**
         * Add a new item to the core list.
         * @param pClass item
         * @return the newly added item
         */
        @Override
        public EventClass addCopyItem(final DataItem pClass) {
            /* Can only clone an EventClass */
            if (!(pClass instanceof EventClass)) {
                return null;
            }

            EventClass myClass = new EventClass(this, (EventClass) pClass);
            add(myClass);
            return myClass;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public EventClass addNewItem() {
            EventClass myClass = new EventClass(this);
            add(myClass);
            return myClass;
        }

        /**
         * Count the instances of a string.
         * @param pName the string to check for
         * @return The Item if present (or null)
         */
        protected int countInstances(final String pName) {
            /* Access the iterator */
            Iterator<EventClass> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                EventClass myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
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
        public EventClass findItemByName(final String pName) {
            /* Access the iterator */
            Iterator<EventClass> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                EventClass myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Allow a class to be added.
         * @param pId the id
         * @param pName the name
         * @param pDesc the description
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer pId,
                                final String pName,
                                final String pDesc) throws JDataException {
            /* Create the tag */
            EventClass myClass = new EventClass(this, pId, pName, pDesc);

            /* Check that this ClassId has not been previously added */
            if (!isIdUnique(pId)) {
                myClass.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myClass, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myClass);
        }

        /**
         * Load an Encrypted Class.
         * @param pId the id
         * @param pControlId the control id
         * @param pName the encrypted name
         * @param pDesc the encrypted description
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final byte[] pName,
                                  final byte[] pDesc) throws JDataException {
            /* Create the tag */
            EventClass myClass = new EventClass(this, pId, pControlId, pName, pDesc);

            /* Check that this ClassId has not been previously added */
            if (!isIdUnique(pId)) {
                myClass.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myClass, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myClass);
        }
    }
}
