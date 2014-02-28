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
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Cash Category class.
 */
public class CashCategory
        extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<CashCategory> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.CASHCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.CASHCATEGORY.getListName();

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountCategory.class.getName());

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
     * Category Type Field Id.
     */
    public static final JDataField FIELD_CATTYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.CASHTYPE.getItemName());

    /**
     * Parent Category Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataParent"));

    /**
     * SubCategory Field Id.
     */
    public static final JDataField FIELD_SUBCAT = FIELD_DEFS.declareDerivedValueField(NLS_BUNDLE.getString("DataSubCat"));

    /**
     * Invalid Parent Error.
     */
    private static final String ERROR_BADPARENT = NLS_BUNDLE.getString("ErrorBadParent");

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
        if (FIELD_CATTYPE.equals(pField)) {
            return true;
        }
        if (FIELD_PARENT.equals(pField)) {
            return getParentCategory() != null;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain Name.
     * @return the name
     */
    public final String getName() {
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
     * Obtain Cash Category Type.
     * @return the type
     */
    public CashCategoryType getCategoryType() {
        return getCashCategoryType(getValueSet());
    }

    /**
     * Obtain categoryTypeId.
     * @return the categoryTypeId
     */
    public Integer getCategoryTypeId() {
        CashCategoryType myType = getCategoryType();
        return (myType == null)
                               ? null
                               : myType.getId();
    }

    /**
     * Obtain CategoryTypeName.
     * @return the categoryTypeName
     */
    public String getCategoryTypeName() {
        CashCategoryType myType = getCategoryType();
        return (myType == null)
                               ? null
                               : myType.getName();
    }

    /**
     * Obtain CategoryTypeClass.
     * @return the categoryTypeClass
     */
    public CashCategoryClass getCategoryTypeClass() {
        CashCategoryType myType = getCategoryType();
        return (myType == null)
                               ? null
                               : myType.getCashClass();
    }

    /**
     * Obtain Cash Category Parent.
     * @return the parent
     */
    public CashCategory getParentCategory() {
        return getParentCategory(getValueSet());
    }

    /**
     * Obtain parentId.
     * @return the parentId
     */
    public Integer getParentCategoryId() {
        CashCategory myParent = getParentCategory();
        return (myParent == null)
                                 ? null
                                 : myParent.getId();
    }

    /**
     * Obtain parentName.
     * @return the parentName
     */
    public String getParentCategoryName() {
        CashCategory myParent = getParentCategory();
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
     * Obtain CashCategoryType.
     * @param pValueSet the valueSet
     * @return the CashCategoryType
     */
    public static CashCategoryType getCashCategoryType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYPE, CashCategoryType.class);
    }

    /**
     * Obtain Parent CashCategory.
     * @param pValueSet the valueSet
     * @return the Parent CashCategory
     */
    public static CashCategory getParentCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, CashCategory.class);
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
     * Set account type value.
     * @param pValue the value
     */
    private void setValueType(final CashCategoryType pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set account type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set account type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValueSet().setValue(FIELD_CATTYPE, pValue);
    }

    /**
     * Set parent value.
     * @param pValue the value
     */
    private void setValueParent(final CashCategory pValue) {
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
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    /**
     * Is this cash category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final CashCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    @Override
    public CashCategory getBase() {
        return (CashCategory) super.getBase();
    }

    @Override
    public CashCategoryList getList() {
        return (CashCategoryList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected CashCategory(final CashCategoryList pList,
                           final CashCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private CashCategory(final CashCategoryList pList,
                         final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Store the Name */
            Object myValue = pValues.getValue(FIELD_NAME);
            if (myValue instanceof String) {
                setValueName((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueName((byte[]) myValue);
            }

            /* Store the Description */
            myValue = pValues.getValue(FIELD_DESC);
            if (myValue instanceof String) {
                setValueDesc((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueDesc((byte[]) myValue);
            }

            /* Store the Category Type */
            myValue = pValues.getValue(FIELD_CATTYPE);
            if (myValue instanceof Integer) {
                setValueType((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueType((String) myValue);
            }

            /* Store the Parent */
            myValue = pValues.getValue(FIELD_PARENT);
            if (myValue instanceof Integer) {
                setValueParent((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueParent((String) myValue);
            }

            /* Resolve the subCategory */
            resolveSubCategory();

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
    public CashCategory(final CashCategoryList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    @Override
    public int compareTo(final CashCategory pThat) {
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
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_CATTYPE, myData.getCashCategoryTypes());
        resolveDataLink(FIELD_PARENT, getList());
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
            int iIndex = myName.indexOf(EventCategory.STR_SEP);
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
     * @throws JOceanusException on error
     */
    public void setCategoryName(final String pName) throws JOceanusException {
        setValueName(pName);

        /* Resolve the subCategory */
        resolveSubCategory();
    }

    /**
     * Set a new category type.
     * @param pType the new type
     */
    public void setCategoryType(final CashCategoryType pType) {
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
     * Set a new parent category.
     * @param pParent the new parent
     */
    public void setParentCategory(final CashCategory pParent) {
        setValueParent(pParent);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the category type referred to */
        getCategoryType().touchItem(this);

        /* Touch parent if it exists */
        CashCategory myParent = getParentCategory();
        if (myParent != null) {
            myParent.touchItem(this);
        }
    }

    @Override
    public void validate() {
        CashCategoryList myList = getList();
        CashCategoryType myCatType = getCategoryType();
        CashCategory myParent = getParentCategory();
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

        /* CashCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, FIELD_CATTYPE);
        } else {
            /* Access the class */
            CashCategoryClass myClass = myCatType.getCashClass();

            /* CashCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_CATTYPE);
            }

            /* Switch on the account class */
            switch (myClass) {
                case PARENT:
                    /* If parent exists */
                    if (myParent != null) {
                        addError(ERROR_EXIST, FIELD_PARENT);
                    }
                    break;
                default:
                    /* Check parent */
                    if (myParent == null) {
                        addError(ERROR_MISSING, FIELD_PARENT);
                    } else if (!myParent.isCategoryClass(CashCategoryClass.PARENT)) {
                        addError(ERROR_BADPARENT, FIELD_PARENT);
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
    public boolean applyChanges(final DataItem<?> pCategory) {
        /* Can only update from a cash category */
        if (!(pCategory instanceof CashCategory)) {
            return false;
        }
        CashCategory myCategory = (CashCategory) pCategory;

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
     * The Cash Category List class.
     */
    public static class CashCategoryList
            extends EncryptedList<CashCategory, MoneyWiseDataType> {
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
        public JDataFields getItemFields() {
            return CashCategory.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE Account Category list.
         * @param pData the DataSet for the list
         */
        protected CashCategoryList(final MoneyWiseData pData) {
            super(CashCategory.class, pData, MoneyWiseDataType.CASHCATEGORY, ListStyle.CORE);
        }

        @Override
        protected CashCategoryList getEmptyList(final ListStyle pStyle) {
            CashCategoryList myList = new CashCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public CashCategoryList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (CashCategoryList) super.cloneList(pDataSet);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected CashCategoryList(final CashCategoryList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public CashCategoryList deriveEditList() {
            /* Build an empty List */
            CashCategoryList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the categories */
            Iterator<CashCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                CashCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked cash category and add it to the list */
                CashCategory myCategory = new CashCategory(myList, myCurr);
                myList.append(myCategory);
            }

            /* Return the list */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pCategory item
         * @return the newly added item
         */
        @Override
        public CashCategory addCopyItem(final DataItem<?> pCategory) {
            /* Can only clone a CashCategory */
            if (!(pCategory instanceof CashCategory)) {
                throw new UnsupportedOperationException();
            }

            CashCategory myCategory = new CashCategory(this, (CashCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public CashCategory addNewItem() {
            CashCategory myCategory = new CashCategory(this);
            add(myCategory);
            return myCategory;
        }

        /**
         * Count the instances of a string.
         * @param pName the string to check for
         * @return The # of instances of the name
         */
        protected int countInstances(final String pName) {
            /* Access the iterator */
            Iterator<CashCategory> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                CashCategory myCurr = myIterator.next();
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
        public CashCategory findItemByName(final String pName) {
            /* Access the iterator */
            Iterator<CashCategory> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                CashCategory myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        @Override
        public CashCategory addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the category */
            CashCategory myCategory = new CashCategory(this, pValues);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(myCategory.getId())) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCategory, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myCategory);

            /* Return it */
            return myCategory;
        }
    }
}
