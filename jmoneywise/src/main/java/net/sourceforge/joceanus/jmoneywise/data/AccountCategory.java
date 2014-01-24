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
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryType.AccountCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Account Category class.
 */
public class AccountCategory
        extends EncryptedItem<MoneyWiseList>
        implements Comparable<AccountCategory> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountCategory.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = "AccountCategories";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountCategory.class.getName());

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
     * Obtain Account Category Type.
     * @return the type
     */
    public AccountCategoryType getCategoryType() {
        return getAccountCategoryType(getValueSet());
    }

    /**
     * Obtain categoryTypeId.
     * @return the categoryTypeId
     */
    public Integer getCategoryTypeId() {
        AccountCategoryType myType = getCategoryType();
        return (myType == null)
                               ? null
                               : myType.getId();
    }

    /**
     * Obtain CategoryTypeName.
     * @return the categoryTypeName
     */
    public String getCategoryTypeName() {
        AccountCategoryType myType = getCategoryType();
        return (myType == null)
                               ? null
                               : myType.getName();
    }

    /**
     * Obtain CategoryTypeClass.
     * @return the categoryTypeClass
     */
    public AccountCategoryClass getCategoryTypeClass() {
        AccountCategoryType myType = getCategoryType();
        return (myType == null)
                               ? null
                               : myType.getAccountClass();
    }

    /**
     * Obtain Account Category Parent.
     * @return the parent
     */
    public AccountCategory getParentCategory() {
        return getParentCategory(getValueSet());
    }

    /**
     * Obtain parentId.
     * @return the parentId
     */
    public Integer getParentCategoryId() {
        AccountCategory myParent = getParentCategory();
        return (myParent == null)
                                 ? null
                                 : myParent.getId();
    }

    /**
     * Obtain parentName.
     * @return the parentName
     */
    public String getParentCategoryName() {
        AccountCategory myParent = getParentCategory();
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
     * Obtain AccountCategoryType.
     * @param pValueSet the valueSet
     * @return the AccountCategoryType
     */
    public static AccountCategoryType getAccountCategoryType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYPE, AccountCategoryType.class);
    }

    /**
     * Obtain Parent AccountCategory.
     * @param pValueSet the valueSet
     * @return the Parent AccountCategory
     */
    public static AccountCategory getParentCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, AccountCategory.class);
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
    private void setValueType(final AccountCategoryType pValue) {
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
    private void setValueParent(final AccountCategory pValue) {
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
     * Is this account category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final AccountCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    @Override
    public AccountCategory getBase() {
        return (AccountCategory) super.getBase();
    }

    @Override
    public AccountCategoryList getList() {
        return (AccountCategoryList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected AccountCategory(final AccountCategoryList pList,
                              final AccountCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Secure constructor.
     * @param pList the List to add to
     * @param pId the Category id
     * @param pControlId the control id
     * @param pName the Encrypted Name of the account category
     * @param pDesc the Encrypted Description of the category
     * @param pCatTypeId the id of the category type
     * @param pParentId the id of the parent category
     * @throws JOceanusException on error
     */
    protected AccountCategory(final AccountCategoryList pList,
                              final Integer pId,
                              final Integer pControlId,
                              final byte[] pName,
                              final byte[] pDesc,
                              final Integer pCatTypeId,
                              final Integer pParentId) throws JOceanusException {
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
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param pId the id
     * @param pName the Name of the account category
     * @param pDesc the description of the category
     * @param pCatType the Category type name
     * @param pParent the Parent Category name
     * @throws JOceanusException on error
     */
    protected AccountCategory(final AccountCategoryList pList,
                              final Integer pId,
                              final String pName,
                              final String pDesc,
                              final String pCatType,
                              final String pParent) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the links */
            setValueType(pCatType);
            setValueParent(pParent);

            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);

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
    public AccountCategory(final AccountCategoryList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    @Override
    public int compareTo(final AccountCategory pThat) {
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

        /* Access Relevant lists */
        MoneyWiseData myData = getDataSet();
        AccountCategoryTypeList myTypes = myData.getAccountCategoryTypes();
        AccountCategoryList myList = getList();
        ValueSet myValues = getValueSet();

        /* Adjust Category type */
        Object myCatType = myValues.getValue(FIELD_CATTYPE);
        if (myCatType instanceof AccountCategoryType) {
            myCatType = ((AccountCategoryType) myCatType).getId();
        }
        if (myCatType instanceof Integer) {
            AccountCategoryType myType = myTypes.findItemById((Integer) myCatType);
            if (myType == null) {
                addError(ERROR_UNKNOWN, FIELD_CATTYPE);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueType(myType);
        } else if (myCatType instanceof String) {
            AccountCategoryType myType = myTypes.findItemByName((String) myCatType);
            if (myType == null) {
                addError(ERROR_UNKNOWN, FIELD_CATTYPE);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueType(myType);
        }

        /* Adjust Parent */
        Object myParent = myValues.getValue(FIELD_PARENT);
        if (myParent instanceof AccountCategory) {
            myParent = ((AccountCategory) myParent).getId();
        }
        if (myParent instanceof Integer) {
            AccountCategory myCat = myList.findItemById((Integer) myParent);
            if (myCat == null) {
                addError(ERROR_UNKNOWN, FIELD_PARENT);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueParent(myCat);
        } else if (myParent instanceof String) {
            AccountCategory myCat = myList.findItemByName((String) myParent);
            if (myCat == null) {
                addError(ERROR_UNKNOWN, FIELD_PARENT);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
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
    public void setCategoryType(final AccountCategoryType pType) {
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
    public void setParentCategory(final AccountCategory pParent) {
        setValueParent(pParent);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the category type referred to */
        getCategoryType().touchItem(this);

        /* Touch parent if it exists */
        AccountCategory myParent = getParentCategory();
        if (myParent != null) {
            myParent.touchItem(this);
        }
    }

    @Override
    public void validate() {
        AccountCategoryList myList = getList();
        AccountCategoryType myCatType = getCategoryType();
        AccountCategory myParent = getParentCategory();
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

        /* AccountCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, FIELD_CATTYPE);
        } else {
            /* Access the class */
            AccountCategoryClass myClass = myCatType.getAccountClass();

            /* AccountCategoryType must be enabled */
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

            /* Switch on the account class */
            switch (myClass) {
                case TOTALS:
                    /* If parent exists */
                    if (myParent != null) {
                        addError(ERROR_EXIST, FIELD_PARENT);
                    }
                    break;
                case SAVINGSTOTALS:
                case CASHTOTALS:
                case PRICEDTOTALS:
                case LOANTOTALS:
                    /* Check parent */
                    if (myParent == null) {
                        addError(ERROR_MISSING, FIELD_PARENT);
                    } else if (!myParent.isCategoryClass(AccountCategoryClass.TOTALS)) {
                        addError(ERROR_BADPARENT, FIELD_PARENT);
                    }
                    break;
                default:
                    /* Check parent */
                    if (myParent == null) {
                        addError(ERROR_MISSING, FIELD_PARENT);
                    } else if (myParent.getCategoryTypeClass() != myClass.getParentClass()) {
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
        /* Can only update from an account category */
        if (!(pCategory instanceof AccountCategory)) {
            return false;
        }
        AccountCategory myCategory = (AccountCategory) pCategory;

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
     * The Account Category List class.
     */
    public static class AccountCategoryList
            extends EncryptedList<AccountCategory, MoneyWiseList> {
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
         * Construct an empty CORE Account Category list.
         * @param pData the DataSet for the list
         */
        protected AccountCategoryList(final MoneyWiseData pData) {
            super(AccountCategory.class, pData, ListStyle.CORE);
        }

        @Override
        protected AccountCategoryList getEmptyList(final ListStyle pStyle) {
            AccountCategoryList myList = new AccountCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AccountCategoryList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (AccountCategoryList) super.cloneList(pDataSet);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected AccountCategoryList(final AccountCategoryList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public AccountCategoryList deriveEditList() {
            /* Build an empty List */
            AccountCategoryList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the categories */
            Iterator<AccountCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                AccountCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked event category and add it to the list */
                AccountCategory myCategory = new AccountCategory(myList, myCurr);
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
        public AccountCategory addCopyItem(final DataItem<?> pCategory) {
            /* Can only clone an AccountCategory */
            if (!(pCategory instanceof AccountCategory)) {
                return null;
            }

            AccountCategory myCategory = new AccountCategory(this, (AccountCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public AccountCategory addNewItem() {
            AccountCategory myCategory = new AccountCategory(this);
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
            Iterator<AccountCategory> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                AccountCategory myCurr = myIterator.next();
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
        protected int countInstances(final AccountCategoryClass pClass) {
            /* Access the iterator */
            Iterator<AccountCategory> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                AccountCategory myCurr = myIterator.next();
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
        public AccountCategory findItemByName(final String pName) {
            /* Access the iterator */
            Iterator<AccountCategory> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                AccountCategory myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Obtain the first account category for the specified class.
         * @param pClass the account category class
         * @return the category
         */
        public AccountCategory getSingularClass(final AccountCategoryClass pClass) {
            /* Access the iterator */
            Iterator<AccountCategory> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                AccountCategory myCurr = myIterator.next();
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
         * @param pCategoryType the category type
         * @param pParent the parent category
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final String pName,
                                final String pDesc,
                                final String pCategoryType,
                                final String pParent) throws JOceanusException {
            /* Create the category */
            AccountCategory myCategory = new AccountCategory(this, pId, pName, pDesc, pCategoryType, pParent);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(pId)) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCategory, ERROR_VALIDATION);
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
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final byte[] pName,
                                  final byte[] pDesc,
                                  final Integer pCategoryId,
                                  final Integer pParentId) throws JOceanusException {
            /* Create the category */
            AccountCategory myCategory = new AccountCategory(this, pId, pControlId, pName, pDesc, pCategoryId, pParentId);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(pId)) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCategory, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myCategory);
        }
    }
}
