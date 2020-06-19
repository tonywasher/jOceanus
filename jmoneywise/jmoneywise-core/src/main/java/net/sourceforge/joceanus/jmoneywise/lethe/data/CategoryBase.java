/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2020 Tony Washer
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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedString;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CategoryInterface;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Category Base class.
 * @param <T> the Category Data type
 * @param <S> the Static Data type
 * @param <C> the Static Data class
 */
public abstract class CategoryBase<T extends CategoryBase<T, S, C>, S extends StaticData<S, C, MoneyWiseDataType>, C extends Enum<C> & CategoryInterface>
        extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<T> {
    /**
     * Separator.
     */
    public static final String STR_SEP = ":";

    /**
     * Local Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(CategoryBase.class.getSimpleName(), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final MetisLetheField FIELD_NAME = FIELD_DEFS.declareComparisonEncryptedField(PrometheusDataResource.DATAITEM_FIELD_NAME.getValue(), MetisDataType.STRING, NAMELEN);

    /**
     * Description Field Id.
     */
    public static final MetisLetheField FIELD_DESC = FIELD_DEFS.declareEqualityEncryptedField(PrometheusDataResource.DATAITEM_FIELD_DESC.getValue(), MetisDataType.STRING, DESCLEN);

    /**
     * Parent Category Field Id.
     */
    public static final MetisLetheField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAGROUP_PARENT.getValue(), MetisDataType.LINK);

    /**
     * SubCategory Field Id.
     */
    public static final MetisLetheField FIELD_SUBCAT = FIELD_DEFS.declareDerivedValueField(MoneyWiseDataResource.CATEGORY_SUBCAT.getValue());

    /**
     * New parent name.
     */
    private static final String NAME_NEWPARENT = MoneyWiseDataResource.CATEGORY_NEWPARENT.getValue();

    /**
     * New Category name.
     */
    private static final String NAME_NEWCATEGORY = MoneyWiseDataResource.CATEGORY_NEWCAT.getValue();

    /**
     * Invalid Parent Error.
     */
    protected static final String ERROR_BADPARENT = MoneyWiseDataResource.CATEGORY_ERROR_BADPARENT.getValue();

    /**
     * NonMatching Parent Error.
     */
    protected static final String ERROR_MATCHPARENT = MoneyWiseDataResource.CATEGORY_ERROR_MATCHPARENT.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected CategoryBase(final CategoryBaseList<T, S, C> pList,
                           final T pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    protected CategoryBase(final CategoryBaseList<T, S, C> pList,
                           final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
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
        } catch (OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public CategoryBase(final CategoryBaseList<T, S, C> pList) {
        super(pList, 0);
        setNextDataKeySet();
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean includeXmlField(final MetisLetheField pField) {
        /* Determine whether fields should be included */
        if (FIELD_NAME.equals(pField)) {
            return true;
        }
        if (FIELD_DESC.equals(pField)) {
            return getDesc() != null;
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
    private MetisEncryptedString getNameField() {
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
    private MetisEncryptedString getDescField() {
        return getDescField(getValueSet());
    }

    /**
     * Obtain Category Type.
     * @return the type
     */
    public abstract S getCategoryType();

    /**
     * Obtain categoryTypeId.
     * @return the categoryTypeId
     */
    public Integer getCategoryTypeId() {
        final S myType = getCategoryType();
        return (myType == null)
                                ? null
                                : myType.getId();
    }

    /**
     * Obtain CategoryTypeName.
     * @return the categoryTypeName
     */
    public String getCategoryTypeName() {
        final S myType = getCategoryType();
        return (myType == null)
                                ? null
                                : myType.getName();
    }

    /**
     * Obtain CategoryTypeClass.
     * @return the categoryTypeClass
     */
    public abstract C getCategoryTypeClass();

    /**
     * Obtain Cash Category Parent.
     * @return the parent
     */
    public abstract T getParentCategory();

    /**
     * Obtain parentId.
     * @return the parentId
     */
    public Integer getParentCategoryId() {
        final T myParent = getParentCategory();
        return (myParent == null)
                                  ? null
                                  : myParent.getId();
    }

    /**
     * Obtain parentName.
     * @return the parentName
     */
    public String getParentCategoryName() {
        final T myParent = getParentCategory();
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
    public static String getName(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    /**
     * Obtain Encrypted Name.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getNameBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    /**
     * Obtain Encrypted name field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static MetisEncryptedString getNameField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, MetisEncryptedString.class);
    }

    /**
     * Obtain Description.
     * @param pValueSet the valueSet
     * @return the description
     */
    public static String getDesc(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    /**
     * Obtain Encrypted description.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getDescBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    /**
     * Obtain Encrypted description field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static MetisEncryptedString getDescField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, MetisEncryptedString.class);
    }

    /**
     * Obtain SubCategory.
     * @param pValueSet the valueSet
     * @return the subCategory
     */
    public static String getSubCategory(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_SUBCAT, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueName(final String pValue) throws OceanusException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     */
    private void setValueName(final MetisEncryptedString pValue) {
        getValueSet().setValue(FIELD_NAME, pValue);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueDesc(final String pValue) throws OceanusException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set description value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueDesc(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set description value.
     * @param pValue the value
     */
    private void setValueDesc(final MetisEncryptedString pValue) {
        getValueSet().setValue(FIELD_DESC, pValue);
    }

    /**
     * Set parent value.
     * @param pValue the value
     */
    private void setValueParent(final T pValue) {
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

    @Override
    @SuppressWarnings("unchecked")
    public CategoryBaseList<T, S, C> getList() {
        return (CategoryBaseList<T, S, C>) super.getList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getBase() {
        return (T) super.getBase();
    }

    @Override
    public int compareTo(final T pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the category type */
        int iDiff = MetisDataDifference.compareObject(getCategoryType(), pThat.getCategoryType());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the names */
        iDiff = MetisDataDifference.compareObject(getName(), pThat.getName());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve parent */
        resolveDataLink(FIELD_PARENT, getList());
    }

    /**
     * Resolve links within an update set.
     * @throws OceanusException on error
     */
    protected abstract void resolveUpdateSetLinks() throws OceanusException;

    /**
     * Resolve subCategory name.
     */
    private void resolveSubCategory() {
        /* Set to null */
        setValueSubCategory(null);

        /* Obtain the name */
        final String myName = getName();
        if (myName != null) {
            /* Look for separator */
            final int iIndex = myName.indexOf(STR_SEP);
            if (iIndex != -1) {
                /* Access and set subCategory */
                final String mySub = myName.substring(iIndex + 1);
                setValueSubCategory(mySub);
            }
        }
    }

    /**
     * Set a new category name.
     * @param pName the new name
     * @throws OceanusException on error
     */
    public void setCategoryName(final String pName) throws OceanusException {
        setValueName(pName);

        /* Resolve the subCategory */
        resolveSubCategory();
    }

    /**
     * Set a new category name.
     * @param pParentName the parent name
     * @param pSubCatName the subCategory name
     * @throws OceanusException on error
     */
    public void setCategoryName(final String pParentName,
                                final String pSubCatName) throws OceanusException {
        setCategoryName(pParentName + STR_SEP + pSubCatName);
    }

    /**
     * Set a new category name.
     * @param pName the new name
     * @throws OceanusException on error
     */
    public void setSubCategoryName(final String pName) throws OceanusException {
        /* Obtain parent */
        final T myParent = getParentCategory();
        final String myName = getName();
        boolean updateChildren = false;

        /* Set name appropriately */
        if (myParent != null) {
            /* Access class of parent */
            final C myClass = myParent.getCategoryTypeClass();

            /* Handle subTotals separately */
            if (myClass.isTotals()) {
                setCategoryName(pName);
                updateChildren = !pName.equals(myName);
            } else {
                setCategoryName(myParent.getName(), pName);
            }

            /* else this is a parent */
        } else {
            setCategoryName(pName);
            if (!getCategoryTypeClass().isTotals()) {
                updateChildren = !pName.equals(myName);
            }
        }

        /* If we should update the children */
        if (updateChildren) {
            final CategoryBaseList<T, S, C> myList = getList();
            myList.updateChildren(myList.getBaseClass().cast(this));
        }
    }

    /**
     * Set a new category type.
     * @param pType the new type
     */
    public abstract void setCategoryType(S pType);

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws OceanusException on error
     */
    public void setDescription(final String pDesc) throws OceanusException {
        setValueDesc(pDesc);
    }

    /**
     * Set a new parent category.
     * @param pParent the new parent
     * @throws OceanusException on error
     */
    public void setParentCategory(final T pParent) throws OceanusException {
        setValueParent(pParent);
        final String mySubName = getSubCategory();
        if (mySubName != null) {
            setSubCategoryName(mySubName);
        }
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the category type referred to */
        getCategoryType().touchItem(this);

        /* Touch parent if it exists */
        final T myParent = getParentCategory();
        if (myParent != null) {
            myParent.touchItem(this);
        }
    }

    @Override
    public void validate() {
        final CategoryBaseList<T, S, C> myList = getList();
        final String myName = getName();
        final String myDesc = getDesc();
        final CategoryDataMap<T, S, C> myMap = myList.getDataMap();

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
            if (!myMap.validNameCount(myName)) {
                final String mySubName = getSubCategory();
                addError(ERROR_DUPLICATE, (mySubName == null)
                                                              ? FIELD_NAME
                                                              : FIELD_SUBCAT);
            }
        }

        /* Check description length */
        if ((myDesc != null) && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_DESC);
        }
    }

    /**
     * Update base category from an edited category.
     * @param pCategory the edited category
     */
    public void applyBasicChanges(final CategoryBase<T, S, C> pCategory) {
        /* Update the Name if required */
        if (!MetisDataDifference.isEqual(getName(), pCategory.getName())) {
            setValueName(pCategory.getNameField());
        }

        /* Update the description if required */
        if (!MetisDataDifference.isEqual(getDesc(), pCategory.getDesc())) {
            setValueDesc(pCategory.getDescField());
        }

        /* Update the parent category if required */
        if (!MetisDataDifference.isEqual(getParentCategory(), pCategory.getParentCategory())) {
            /* Set value */
            setValueParent(pCategory.getParentCategory());
        }
    }

    @Override
    public void adjustMapForItem() {
        final CategoryBaseList<T, S, C> myList = getList();
        final CategoryDataMap<T, S, C> myMap = myList.getDataMap();
        myMap.adjustForItem(myList.getBaseClass().cast(this));
    }

    @Override
    public void touchOnUpdate() {
        /* Reset self-touches */
        clearTouches(getItemType());

        /* Touch parent if it exists */
        final T myParent = getParentCategory();
        if (myParent != null) {
            myParent.touchItem(this);
        }
    }

    /**
     * The Category Base List class.
     * @param <T> the Category Data type
     * @param <S> the Static Data type
     * @param <C> the Static Data class
     */
    public abstract static class CategoryBaseList<T extends CategoryBase<T, S, C>, S extends StaticData<S, C, MoneyWiseDataType>, C extends Enum<C> & CategoryInterface>
            extends EncryptedList<T, MoneyWiseDataType> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(CategoryBaseList.class);
        }

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected CategoryBaseList(final MoneyWiseData pData,
                                   final Class<T> pClass,
                                   final MoneyWiseDataType pItemType) {
            super(pClass, pData, pItemType, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected CategoryBaseList(final CategoryBaseList<T, S, C> pSource) {
            super(pSource);
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected CategoryDataMap<T, S, C> getDataMap() {
            return (CategoryDataMap<T, S, C>) super.getDataMap();
        }

        @Override
        public T findItemByName(final String pName) {
            /* Access the dataMap */
            final CategoryDataMap<T, S, C> myMap = getDataMap();

            /* Use it if we have it */
            if (myMap != null) {
                return myMap.findItemByName(pName);
            }

            /* No map so we must do a slow lookUp */
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                final T myItem = myIterator.next();

                /* If this is not deleted and matches */
                if (!myItem.isDeleted()
                    && MetisDataDifference.isEqual(pName, myItem.getName())) {
                    /* found it */
                    return myItem;
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Obtain unique name for new category.
         * @param pParent the parent category
         * @return The new name
         */
        public String getUniqueName(final T pParent) {
            /* Set up base constraints */
            final String myBase = pParent == null
                                                  ? ""
                                                  : pParent.getName() + STR_SEP;
            final String myCore = pParent == null
                                                  ? NAME_NEWPARENT
                                                  : NAME_NEWCATEGORY;
            int iNextId = 1;

            /* Loop until we found a name */
            String myName = myCore;
            for (;;) {
                /* try out the name */
                if (findItemByName(myBase + myName) == null) {
                    return myName;
                }

                /* Build next name */
                myName = myBase.concat(Integer.toString(iNextId++));
            }
        }

        /**
         * Update Children.
         * @param pParent the parent item
         * @throws OceanusException on error
         */
        private void updateChildren(final T pParent) throws OceanusException {
            /* Determine the id */
            final Integer myId = pParent.getId();
            final String myName = pParent.getName();

            /* Loop through the items */
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                final T myCurr = myIterator.next();

                /* If we have a child of the parent */
                if (myId.equals(myCurr.getParentCategoryId())) {
                    /* Update name and point to edit parent */
                    myCurr.pushHistory();
                    myCurr.setParentCategory(pParent);
                    myCurr.setCategoryName(myName, myCurr.getSubCategory());
                    myCurr.checkForHistory();
                }
            }
        }

        /**
         * Resolve update set links.
         * @throws OceanusException on error
         */
        public void resolveUpdateSetLinks() throws OceanusException {
            /* Loop through the items */
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                final T myCurr = myIterator.next();
                myCurr.resolveUpdateSetLinks();
            }
        }

        @Override
        protected CategoryDataMap<T, S, C> allocateDataMap() {
            return new CategoryDataMap<>();
        }
    }

    /**
     * The dataMap class.
     * @param <T> the Category Data type
     * @param <S> the Static Data type
     * @param <C> the Static Data class
     */
    protected static class CategoryDataMap<T extends CategoryBase<T, S, C>, S extends StaticData<S, C, MoneyWiseDataType>, C extends Enum<C> & CategoryInterface>
            extends DataInstanceMap<T, MoneyWiseDataType, String> {
        @Override
        public void adjustForItem(final T pItem) {
            /* Adjust name count */
            adjustForItem(pItem, pItem.getName());
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public T findItemByName(final String pName) {
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
    }
}
