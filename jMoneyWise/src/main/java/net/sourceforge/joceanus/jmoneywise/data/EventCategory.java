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
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryType.EventCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;

/**
 * Event Category class.
 */
public final class EventCategory
        extends EncryptedItem
        implements Comparable<EventCategory> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = EventCategory.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = "EventCategories";

    /**
     * Separator.
     */
    public static final String STR_SEP = ":";

    /**
     * EventCategory Name length.
     */
    public static final int NAMELEN = 30;

    /**
     * EventCategory Description length.
     */
    public static final int DESCLEN = 50;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventCategory.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataCatName"));

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDesc"));

    /**
     * Category Type Field Id.
     */
    public static final JDataField FIELD_CATTYPE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataCatType"));

    /**
     * Parent Category Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataParent"));

    /**
     * SubCategory Field Id.
     */
    public static final JDataField FIELD_SUBCAT = FIELD_DEFS.declareDerivedValueField(NLS_BUNDLE.getString("DataSubCat"));

    /**
     * Multiple instances Error.
     */
    private static final String ERROR_MULT = NLS_BUNDLE.getString("ErrorMultiple");

    /**
     * Invalid Parent Error.
     */
    private static final String ERROR_BADPARENT = NLS_BUNDLE.getString("ErrorBadParent");

    /**
     * Different Parent Error.
     */
    private static final String ERROR_DIFFPARENT = NLS_BUNDLE.getString("ErrorDiffParent");

    /**
     * NonMatching Parent Error.
     */
    private static final String ERROR_MATCHPARENT = NLS_BUNDLE.getString("ErrorMatchParent");

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
     * Obtain Event Category Parent.
     * @return the parent
     */
    public EventCategory getParentCategory() {
        return getParentCategory(getValueSet());
    }

    /**
     * Obtain parentId.
     * @return the parentId
     */
    public Integer getParentCategoryId() {
        EventCategory myParent = getParentCategory();
        return (myParent == null)
                ? null
                : myParent.getId();
    }

    /**
     * Obtain parentName.
     * @return the parentName
     */
    public String getParentCategoryName() {
        EventCategory myParent = getParentCategory();
        return (myParent == null)
                ? null
                : myParent.getName();
    }

    /**
     * Obtain subCategory.
     * @return the subCategory
     */
    public String getSubCategory() {
        return getSubCategory(getValueSet());
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
     * Obtain Parent EventCategory.
     * @param pValueSet the valueSet
     * @return the Parent AccountCategory
     */
    public static EventCategory getParentCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, EventCategory.class);
    }

    /**
     * Obtain SubCategory.
     * @param pValueSet the valueSet
     * @return the subCategory
     */
    public static String getSubCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_SUBCAT, String.class);
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
     * Set category type value.
     * @param pValue the value
     */
    private void setValueType(final EventCategoryType pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set category type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set category type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set parent value.
     * @param pValue the value
     */
    private void setValueParent(final EventCategory pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set parent id.
     * @param pValue the value
     */
    private void setValueParent(final Integer pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set parent name.
     * @param pValue the value
     */
    private void setValueParent(final String pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set subCategory name.
     * @param pValue the value
     */
    private void setValueSubCategory(final String pValue) {
        getValueSet().setValue(FIELD_SUBCAT, pValue);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    @Override
    public EventCategory getBase() {
        return (EventCategory) super.getBase();
    }

    @Override
    public EventCategoryList getList() {
        return (EventCategoryList) super.getList();
    }

    /**
     * Is this event category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final EventCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    /**
     * Is this event category a transfer?
     * @return true/false
     */
    public boolean isTransfer() {
        /* Check for match */
        EventCategoryClass myClass = getCategoryTypeClass();
        return (myClass == null)
                ? false
                : myClass.isTransfer();
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
     * @param pParentId the id of the parent category
     * @throws JDataException on error
     */
    protected EventCategory(final EventCategoryList pList,
                            final Integer pId,
                            final Integer pControlId,
                            final byte[] pName,
                            final byte[] pDesc,
                            final Integer pCatTypeId,
                            final Integer pParentId) throws JDataException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the IDs */
            setValueType(pCatTypeId);
            setValueParent(pParentId);

            /* Set ControlId */
            setControlKey(pControlId);

            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);

            /* Resolve the subCategory */
            resolveSubCategory();

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
     * @param pName the Name of the event category
     * @param pDesc the description of the category
     * @param pCategory the Category type
     * @param pParent the Parent Category
     * @throws JDataException on error
     */
    protected EventCategory(final EventCategoryList pList,
                            final Integer pId,
                            final String pName,
                            final String pDesc,
                            final String pCategory,
                            final String pParent) throws JDataException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the links */
            setValueType(pCategory);
            setValueParent(pParent);

            /* Record the string values */
            setValueName(pName);
            setValueDesc(pDesc);

            /* Resolve the subCategory */
            resolveSubCategory();

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

        /* Compare the hidden attribute */
        boolean isHidden = isHidden();
        if (isHidden != pThat.isHidden()) {
            return (isHidden)
                    ? 1
                    : -1;
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
    public void resolveDataSetLinks() throws JDataException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        FinanceData myData = getDataSet();
        EventCategoryTypeList myTypes = myData.getEventCategoryTypes();
        EventCategoryList myList = myData.getEventCategories();
        ValueSet myValues = getValueSet();

        /* Adjust Category type */
        Object myCatType = myValues.getValue(FIELD_CATTYPE);
        if (myCatType instanceof EventCategoryType) {
            myCatType = ((EventCategoryType) myCatType).getId();
        }
        if (myCatType instanceof Integer) {
            EventCategoryType myType = myTypes.findItemById((Integer) myCatType);
            if (myType == null) {
                addError(ERROR_UNKNOWN, FIELD_CATTYPE);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
            }
            setValueType(myType);
        } else if (myCatType instanceof String) {
            EventCategoryType myType = myTypes.findItemByName((String) myCatType);
            if (myType == null) {
                addError(ERROR_UNKNOWN, FIELD_CATTYPE);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
            }
            setValueType(myType);
        }

        /* Adjust Parent */
        Object myParent = myValues.getValue(FIELD_PARENT);
        if (myParent instanceof EventCategory) {
            myParent = ((EventCategory) myParent).getId();
        }
        if (myParent instanceof Integer) {
            EventCategory myCat = myList.findItemById((Integer) myParent);
            if (myCat == null) {
                addError(ERROR_UNKNOWN, FIELD_PARENT);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
            }
            setValueParent(myCat);
        } else if (myParent instanceof String) {
            EventCategory myCat = myList.findItemByName((String) myParent);
            if (myCat == null) {
                addError(ERROR_UNKNOWN, FIELD_PARENT);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
            }
            setValueParent(myCat);
        }
    }

    /**
     * Resolve subCategory name.
     */
    private void resolveSubCategory() {
        /* Set to null */
        setValueSubCategory(null);

        /* Obtain the name */
        String myName = getName();
        if (myName != null) {
            /* Look for separator */
            int iIndex = myName.indexOf(STR_SEP);
            if (iIndex != -1) {
                /* Access and set subCategory */
                String mySub = myName.substring(iIndex + 1);
                setValueSubCategory(mySub);
            }
        }
    }

    /**
     * Set a new category name.
     * @param pName the new name
     * @throws JDataException on error
     */
    public void setCategoryName(final String pName) throws JDataException {
        setValueName(pName);

        /* Resolve the subCategory */
        resolveSubCategory();
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
     * Set a new parent category.
     * @param pParent the new parent
     */
    public void setParentCategory(final EventCategory pParent) {
        setValueParent(pParent);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the category type referred to */
        getCategoryType().touchItem(this);

        /* Touch parent if it exists */
        EventCategory myParent = getParentCategory();
        if (myParent != null) {
            myParent.touchItem(this);
        }
    }

    @Override
    public void validate() {
        EventCategoryList myList = getList();
        EventCategoryType myCatType = getCategoryType();
        EventCategory myParent = getParentCategory();
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
        if ((myDesc != null)
            && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_DESC);
        }

        /* EventCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, FIELD_CATTYPE);
        } else {
            /* Access the class */
            EventCategoryClass myClass = myCatType.getCategoryClass();

            /* EventCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_CATTYPE);
            }

            /* If the CategoryType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                int myCount = myList.countInstances(myClass);
                if (myCount > 1) {
                    addError(ERROR_MULT, FIELD_CATTYPE);
                }
            }

            /* Switch on the category class */
            switch (myClass) {
                case TOTALS:
                    /* If parent exists */
                    if (myParent != null) {
                        addError(ERROR_EXIST, FIELD_PARENT);
                    }
                    break;
                case INCOMETOTALS:
                case EXPENSETOTALS:
                    /* Check parent */
                    if (myParent == null) {
                        addError(ERROR_MISSING, FIELD_PARENT);
                    } else if (!myParent.isCategoryClass(EventCategoryClass.TOTALS)) {
                        addError(ERROR_BADPARENT, FIELD_PARENT);
                    }
                    break;
                default:
                    /* Check parent requirement */
                    boolean isTransfer = isTransfer();
                    boolean hasParent = myParent != null;
                    if (hasParent == isTransfer) {
                        if (isTransfer) {
                            addError(ERROR_EXIST, FIELD_PARENT);
                        } else {
                            addError(ERROR_MISSING, FIELD_PARENT);
                        }
                    } else if (hasParent) {
                        /* Check validity of parent */
                        EventCategoryClass myParentClass = myParent.getCategoryTypeClass();
                        if (!myParentClass.canParentCategory()) {
                            addError(ERROR_BADPARENT, FIELD_PARENT);
                        }
                        if (myParentClass.isIncome() != myClass.isIncome()) {
                            addError(ERROR_DIFFPARENT, FIELD_PARENT);
                        }

                        /* Check that name reflects parent */
                        if ((myName != null)
                            && !myName.startsWith(myParent.getName()
                                                  + STR_SEP)) {
                            addError(ERROR_MATCHPARENT, FIELD_PARENT);
                        }
                    }
                    break;
            }

        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
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

        /* Update the parent category if required */
        if (!Difference.isEqual(getParentCategory(), myCategory.getParentCategory())) {
            setValueParent(myCategory.getParentCategory());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Is the category hidden?
     * @return true/false
     */
    public boolean isHidden() {
        EventCategoryClass myClass = this.getCategoryTypeClass();
        return (myClass == null)
                ? false
                : myClass.isHiddenType();
    }

    /**
     * The Event Category List class.
     */
    public static class EventCategoryList
            extends EncryptedList<EventCategory> {
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
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        /**
         * Construct an empty CORE Event Category list.
         * @param pData the DataSet for the list
         */
        public EventCategoryList(final FinanceData pData) {
            super(EventCategory.class, pData, ListStyle.CORE);
        }

        @Override
        protected EventCategoryList getEmptyList(final ListStyle pStyle) {
            EventCategoryList myList = new EventCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public EventCategoryList cloneList(final DataSet<?> pDataSet) throws JDataException {
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
        public EventCategoryList deriveList(final ListStyle pStyle) throws JDataException {
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
         * Count the instances of a class.
         * @param pClass the event category class
         * @return The # of instances of the name
         */
        protected int countInstances(final EventCategoryClass pClass) {
            /* Access the iterator */
            Iterator<EventCategory> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                EventCategory myCurr = myIterator.next();
                if (pClass == myCurr.getCategoryTypeClass()) {
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
         * Obtain singular category for EventInfoClass.
         * @param pInfoClass the Event info class
         * @return the corresponding category.
         */
        public EventCategory getEventInfoCategory(final EventInfoClass pInfoClass) {
            /* Switch on info class */
            switch (pInfoClass) {
                case TAXCREDIT:
                    return getSingularClass(EventCategoryClass.TAXCREDIT);
                case NATINSURANCE:
                    return getSingularClass(EventCategoryClass.NATINSURANCE);
                case DEEMEDBENEFIT:
                    return getSingularClass(EventCategoryClass.DEEMEDBENEFIT);
                case CHARITYDONATION:
                    return getSingularClass(EventCategoryClass.CHARITYDONATION);
                default:
                    return null;
            }
        }

        /**
         * Allow a category to be added.
         * @param pId the id
         * @param pName the name
         * @param pDesc the description
         * @param pCategoryType the category type
         * @param pParent the parent category
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer pId,
                                final String pName,
                                final String pDesc,
                                final String pCategoryType,
                                final String pParent) throws JDataException {
            /* Create the category */
            EventCategory myCategory = new EventCategory(this, pId, pName, pDesc, pCategoryType, pParent);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(pId)) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myCategory, ERROR_VALIDATION);
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
         * @param pParentId the parent id
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final byte[] pName,
                                  final byte[] pDesc,
                                  final Integer pCategoryId,
                                  final Integer pParentId) throws JDataException {
            /* Create the category */
            EventCategory myCategory = new EventCategory(this, pId, pControlId, pName, pDesc, pCategoryId, pParentId);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(pId)) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myCategory, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myCategory);
        }
    }
}
