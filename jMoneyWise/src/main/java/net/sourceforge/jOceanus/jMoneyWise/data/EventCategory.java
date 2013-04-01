/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.data;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.EncryptedItem;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedValueSet;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType.EventCategoryTypeList;

/**
 * Event Category class.
 */
public class EventCategory
        extends EncryptedItem
        implements Comparable<EventCategory> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountCategory.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(EventCategory.class.getSimpleName(), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField("Name");

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField("Description");

    /**
     * Category Type Field Id.
     */
    public static final JDataField FIELD_CATTYPE = FIELD_DEFS.declareEqualityValueField("CategoryType");

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
     * Obtain Category Type.
     * @return the type
     */
    public EventCategoryType getCategoryType() {
        return getEventCategoryType(getValueSet());
    }

    /**
     * Obtain CategoryTypeId.
     * @return the categoryTypeId
     */
    public Integer getCategoryTypeId() {
        EventCategoryType myType = getCategoryType();
        return (myType == null)
                ? null
                : myType.getId();
    }

    /**
     * Obtain CategoryTypeName.
     * @return the categoryTypeName
     */
    public String getCategoryTypeName() {
        EventCategoryType myType = getCategoryType();
        return (myType == null)
                ? null
                : myType.getName();
    }

    /**
     * Obtain CategoryTypeClass.
     * @return the categoryTypeClass
     */
    public EventCategoryClass getCategoryTypeClass() {
        EventCategoryType myType = getCategoryType();
        return (myType == null)
                ? null
                : myType.getCategoryClass();
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
     * Obtain EventCategoryType.
     * @param pValueSet the valueSet
     * @return the EventCategoryType
     */
    public static EventCategoryType getEventCategoryType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYPE, EventCategoryType.class);
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

    /**
     * Set account type value.
     * @param pValue the value
     */
    private void setValueType(final EventCategoryType pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set account type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected EventCategory(final EventCategoryList pList,
                            final EventCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Secure constructor.
     * @param pList the List to add to
     * @param pId the Category id
     * @param pControlId the control id
     * @param pName the Encrypted Name of the event category
     * @param pDesc the Encrypted Description of the category
     * @param pCatTypeId the id of the category type
     * @throws JDataException on error
     */
    protected EventCategory(final EventCategoryList pList,
                            final Integer pId,
                            final Integer pControlId,
                            final byte[] pName,
                            final byte[] pDesc,
                            final Integer pCatTypeId) throws JDataException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the IDs */
            setValueType(pCatTypeId);

            /* Set ControlId */
            setControlKey(pControlId);

            /* Look up the Category Type */
            FinanceData myData = getDataSet();
            EventCategoryTypeList myTypes = myData.getEventCategoryTypes();
            EventCategoryType myCatType = myTypes.findItemById(pCatTypeId);
            if (myCatType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Event Category Type Id");
            }
            setValueType(myCatType);

            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param pId the id
     * @param pName the Name of the event category
     * @param pDesc the description of the category
     * @param pCategory the Category type
     * @throws JDataException on error
     */
    protected EventCategory(final EventCategoryList pList,
                            final Integer pId,
                            final String pName,
                            final String pDesc,
                            final EventCategoryType pCategory) throws JDataException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);
            setValueType(pCategory);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public EventCategory(final EventCategoryList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    @Override
    public int compareTo(final EventCategory pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the category type */
        int iDiff = Difference.compareObject(getCategoryType(), pThat.getCategoryType());
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
    public void relinkToDataSet() {
        /* Update the Encryption details */
        super.relinkToDataSet();

        /* Access Account types */
        FinanceData myData = getDataSet();
        EventCategoryTypeList myTypes = myData.getEventCategoryTypes();

        /* Update to use the local copy of the EventCategoryTypes */
        EventCategoryType myType = getCategoryType();
        EventCategoryType myNewType = myTypes.findItemById(myType.getId());
        setValueType(myNewType);
    }

    /**
     * Set a new category name.
     * @param pName the new name
     * @throws JDataException on error
     */
    public void setCategoryName(final String pName) throws JDataException {
        setValueName(pName);
    }

    /**
     * Set a new category type.
     * @param pType the new type
     */
    public void setCategoryType(final EventCategoryType pType) {
        setValueType(pType);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws JDataException on error
     */
    public void setDescription(final String pDesc) throws JDataException {
        setValueDesc(pDesc);
    }

    /**
     * Mark active items.
     */
    protected void markActiveItems() {
        /* mark the category type referred to */
        getCategoryType().touchItem(this);
    }

    /**
     * Update base category from an edited category.
     * @param pCategory the edited category
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pCategory) {
        /* Can only update from an event category */
        if (!(pCategory instanceof EventCategory)) {
            return false;
        }

        EventCategory myCategory = (EventCategory) pCategory;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Name if required */
        if (!Difference.isEqual(getName(), myCategory.getName())) {
            setValueName(myCategory.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myCategory.getDesc())) {
            setValueDesc(myCategory.getDescField());
        }

        /* Update the category type if required */
        if (!Difference.isEqual(getCategoryType(), myCategory.getCategoryType())) {
            setValueType(myCategory.getCategoryType());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Event Category List class.
     */
    public static class EventCategoryList
            extends EncryptedList<EventCategory> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventCategoryList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        /**
         * Construct an empty CORE Event Category list.
         * @param pData the DataSet for the list
         */
        protected EventCategoryList(final FinanceData pData) {
            super(EventCategory.class, pData, ListStyle.CORE);
        }

        @Override
        protected EventCategoryList getEmptyList(final ListStyle pStyle) {
            EventCategoryList myList = new EventCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public EventCategoryList cloneList(final DataSet<?> pDataSet) {
            return (EventCategoryList) super.cloneList(pDataSet);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EventCategoryList(final EventCategoryList pSource) {
            super(pSource);
        }

        @Override
        public EventCategoryList deriveList(final ListStyle pStyle) {
            return (EventCategoryList) super.deriveList(pStyle);
        }

        @Override
        public EventCategoryList deriveDifferences(final DataList<EventCategory> pOld) {
            return (EventCategoryList) super.deriveDifferences(pOld);
        }

        /**
         * Add a new item to the core list.
         * @param pCategory item
         * @return the newly added item
         */
        @Override
        public EventCategory addCopyItem(final DataItem pCategory) {
            /* Can only clone an EventCategory */
            if (!(pCategory instanceof EventCategory)) {
                return null;
            }

            EventCategory myCategory = new EventCategory(this, (EventCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public EventCategory addNewItem() {
            EventCategory myCategory = new EventCategory(this);
            add(myCategory);
            return myCategory;
        }

        /**
         * Count the instances of a string.
         * @param pName the string to check for
         * @return The Item if present (or null)
         */
        protected int countInstances(final String pName) {
            /* Access the iterator */
            Iterator<EventCategory> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                EventCategory myCurr = myIterator.next();
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
        public EventCategory findItemByName(final String pName) {
            /* Access the iterator */
            Iterator<EventCategory> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                EventCategory myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Obtain the first event category for the specified class.
         * @param pClass the event category class
         * @return the category
         */
        public EventCategory getSingularClass(final EventCategoryClass pClass) {
            /* Access the iterator */
            Iterator<EventCategory> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                EventCategory myCurr = myIterator.next();
                if (myCurr.getCategoryTypeClass() == pClass) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Allow a category to be added.
         * @param pId the id
         * @param pName the name
         * @param pDesc the description
         * @param pCategory the category
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer pId,
                                final String pName,
                                final String pDesc,
                                final String pCategory) throws JDataException {
            /* Access the Accounts */
            FinanceData myData = getDataSet();
            EventCategoryTypeList myCategories = myData.getEventCategoryTypes();

            /* Look up the Category */
            EventCategoryType myCategoryType = myCategories.findItemByName(pCategory);
            if (myCategoryType == null) {
                throw new JDataException(ExceptionClass.DATA, "Category ["
                                                              + pName
                                                              + "] has invalid Event Category ["
                                                              + pCategory
                                                              + "]");
            }

            /* Create the category */
            EventCategory myCategory = new EventCategory(this, pId, pName, pDesc, myCategoryType);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(pId)) {
                throw new JDataException(ExceptionClass.DATA, myCategory, "Duplicate CategoryId");
            }

            /* Validate the category */
            myCategory.validate();

            /* Handle validation failure */
            if (myCategory.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCategory, "Failed validation");
            }

            /* Add to the list */
            append(myCategory);
        }

        /**
         * Load an Encrypted Category.
         * @param pId the id
         * @param pControlId the control id
         * @param pName the encrypted name
         * @param pDesc the encrypted description
         * @param pCategoryId the category id
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final byte[] pName,
                                  final byte[] pDesc,
                                  final Integer pCategoryId) throws JDataException {
            /* Create the category */
            EventCategory myCategory = new EventCategory(this, pId, pControlId, pName, pDesc, pCategoryId);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(pId)) {
                throw new JDataException(ExceptionClass.DATA, myCategory, "Duplicate CategoryId");
            }

            /* Validate the category */
            myCategory.validate();

            /* Handle validation failure */
            if (myCategory.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCategory, "Failed validation");
            }

            /* Add to the list */
            append(myCategory);
        }
    }
}
