/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.basic;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCategoryInterface;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInstanceMap;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedFieldSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedPair;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;

import java.util.Iterator;

/**
 * Category Base class.
 */
public abstract class MoneyWiseCategoryBase
        extends PrometheusEncryptedDataItem
        implements MetisDataNamedItem {
    /**
     * Separator.
     */
    public static final String STR_SEP = ":";

    /**
     * Local Report fields.
     */
    private static final PrometheusEncryptedFieldSet<MoneyWiseCategoryBase> FIELD_DEFS = PrometheusEncryptedFieldSet.newEncryptedFieldSet(MoneyWiseCategoryBase.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareEncryptedStringField(PrometheusDataResource.DATAITEM_FIELD_NAME, NAMELEN);
        FIELD_DEFS.declareEncryptedStringField(PrometheusDataResource.DATAITEM_FIELD_DESC, DESCLEN);
        FIELD_DEFS.declareLinkField(PrometheusDataResource.DATAGROUP_PARENT);
        FIELD_DEFS.declareDerivedVersionedField(MoneyWiseBasicResource.CATEGORY_SUBCAT);
    }

    /**
     * New parent name.
     */
    private static final String NAME_NEWPARENT = MoneyWiseBasicResource.CATEGORY_NEWPARENT.getValue();

    /**
     * New Category name.
     */
    private static final String NAME_NEWCATEGORY = MoneyWiseBasicResource.CATEGORY_NEWCAT.getValue();

    /**
     * Invalid Parent Error.
     */
    protected static final String ERROR_BADPARENT = MoneyWiseBasicResource.CATEGORY_ERROR_BADPARENT.getValue();

    /**
     * NonMatching Parent Error.
     */
    protected static final String ERROR_MATCHPARENT = MoneyWiseBasicResource.CATEGORY_ERROR_MATCHPARENT.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected MoneyWiseCategoryBase(final MoneyWiseCategoryBaseList<?> pList,
                                    final MoneyWiseCategoryBase pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    protected MoneyWiseCategoryBase(final MoneyWiseCategoryBaseList<?> pList,
                                    final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Store the Name */
            Object myValue = pValues.getValue(PrometheusDataResource.DATAITEM_FIELD_NAME);
            if (myValue instanceof String) {
                setValueName((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueName((byte[]) myValue);
            }

            /* Store the Description */
            myValue = pValues.getValue(PrometheusDataResource.DATAITEM_FIELD_DESC);
            if (myValue instanceof String) {
                setValueDesc((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueDesc((byte[]) myValue);
            }

            /* Store the Parent */
            myValue = pValues.getValue(PrometheusDataResource.DATAGROUP_PARENT);
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
    protected MoneyWiseCategoryBase(final MoneyWiseCategoryBaseList<?> pList) {
        super(pList, 0);
        setNextDataKeySet();
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(pField)) {
            return true;
        }
        if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(pField)) {
            return getDesc() != null;
        }
        if (PrometheusDataResource.DATAGROUP_PARENT.equals(pField)) {
            return getParentCategory() != null;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public String getName() {
        return getValues().getValue(PrometheusDataResource.DATAITEM_FIELD_NAME, String.class);
    }

    /**
     * Obtain Encrypted name.
     * @return the bytes
     */
    public byte[] getNameBytes() {
        return getValues().getEncryptedBytes(PrometheusDataResource.DATAITEM_FIELD_NAME);
    }

    /**
     * Obtain Encrypted Name Field.
     * @return the Field
     */
    private PrometheusEncryptedPair getNameField() {
        return getValues().getEncryptedPair(PrometheusDataResource.DATAITEM_FIELD_NAME);
    }

    /**
     * Obtain Description.
     * @return the description
     */
    public String getDesc() {
        return getValues().getValue(PrometheusDataResource.DATAITEM_FIELD_DESC, String.class);
    }

    /**
     * Obtain Encrypted description.
     * @return the bytes
     */
    public byte[] getDescBytes() {
        return getValues().getEncryptedBytes(PrometheusDataResource.DATAITEM_FIELD_DESC);
    }

    /**
     * Obtain Encrypted Description Field.
     * @return the Field
     */
    private PrometheusEncryptedPair getDescField() {
        return getValues().getEncryptedPair(PrometheusDataResource.DATAITEM_FIELD_DESC);
    }

    /**
     * Obtain Category Type.
     * @return the type
     */
    public abstract PrometheusStaticDataItem getCategoryType();

    /**
     * Obtain categoryTypeId.
     * @return the categoryTypeId
     */
    public Integer getCategoryTypeId() {
        final PrometheusStaticDataItem myType = getCategoryType();
        return (myType == null)
                ? null
                : myType.getIndexedId();
    }

    /**
     * Obtain CategoryTypeName.
     * @return the categoryTypeName
     */
    public String getCategoryTypeName() {
        final PrometheusStaticDataItem myType = getCategoryType();
        return myType == null
                ? null
                : myType.getName();
    }

    /**
     * Obtain CategoryTypeClass.
     * @return the categoryTypeClass
     */
    public abstract MoneyWiseCategoryInterface getCategoryTypeClass();

    /**
     * Obtain Cash Category Parent.
     * @return the parent
     */
    public abstract MoneyWiseCategoryBase getParentCategory();

    /**
     * Obtain parentId.
     * @return the parentId
     */
    public Integer getParentCategoryId() {
        final MoneyWiseCategoryBase myParent = getParentCategory();
        return myParent == null
                ? null
                : myParent.getIndexedId();
    }

    /**
     * Obtain parentName.
     * @return the parentName
     */
    public String getParentCategoryName() {
        final MoneyWiseCategoryBase myParent = getParentCategory();
        return myParent == null
                ? null
                : myParent.getName();
    }

    /**
     * Obtain subCategory.
     * @return the subCategory
     */
    public String getSubCategory() {
        return getValues().getValue(MoneyWiseBasicResource.CATEGORY_SUBCAT, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueName(final String pValue) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     */
    private void setValueName(final PrometheusEncryptedPair pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAITEM_FIELD_NAME, pValue);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueDesc(final String pValue) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_DESC, pValue);
    }

    /**
     * Set description value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueDesc(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set description value.
     * @param pValue the value
     */
    private void setValueDesc(final PrometheusEncryptedPair pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAITEM_FIELD_DESC, pValue);
    }

    /**
     * Set parent value.
     * @param pValue the value
     */
    private void setValueParent(final MoneyWiseCategoryBase pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAGROUP_PARENT, pValue);
    }

    /**
     * Set parent id.
     * @param pValue the value
     */
    private void setValueParent(final Integer pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAGROUP_PARENT, pValue);
     }

    /**
     * Set parent name.
     * @param pValue the value
     */
    private void setValueParent(final String pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAGROUP_PARENT, pValue);
    }

    /**
     * Set subCategory name.
     * @param pValue the value
     */
    private void setValueSubCategory(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.CATEGORY_SUBCAT, pValue);
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseCategoryBaseList<?> getList() {
        return (MoneyWiseCategoryBaseList<?>) super.getList();
    }

    @Override
    public MoneyWiseCategoryBase getBase() {
        return (MoneyWiseCategoryBase) super.getBase();
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Check the category and then the name */
        final MoneyWiseCategoryBase myThat = (MoneyWiseCategoryBase) pThat;
        int iDiff = MetisDataDifference.compareObject(getCategoryType(), myThat.getCategoryType());
        if (iDiff == 0) {
            iDiff = MetisDataDifference.compareObject(getName(), myThat.getName());
        }
        return iDiff;
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve parent */
        resolveDataLink(PrometheusDataResource.DATAGROUP_PARENT, getList());
    }

    /**
     * Resolve links within an edit set.
     * @throws OceanusException on error
     */
    protected abstract void resolveEditSetLinks() throws OceanusException;

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
        final MoneyWiseCategoryBase myParent = getParentCategory();
        final String myName = getName();
        boolean updateChildren = false;

        /* Set name appropriately */
        if (myParent != null) {
            /* Access class of parent */
            final MoneyWiseCategoryInterface myClass = myParent.getCategoryTypeClass();

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
            final MoneyWiseCategoryBaseList<?> myList = getList();
            myList.updateChildren(myList.getBaseClass().cast(this));
        }
    }

    /**
     * Set a new category type.
     * @param pType the new type
     */
    public abstract void setCategoryType(PrometheusStaticDataItem pType);

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
    public void setParentCategory(final MoneyWiseCategoryBase pParent) throws OceanusException {
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
        final MoneyWiseCategoryBase myParent = getParentCategory();
        if (myParent != null) {
            myParent.touchItem(this);
        }
    }

    @Override
    public void validate() {
        final MoneyWiseCategoryBaseList<?> myList = getList();
        final String myName = getName();
        final String myDesc = getDesc();
        final MoneyWiseCategoryDataMap<?> myMap = myList.getDataMap();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, PrometheusDataResource.DATAITEM_FIELD_NAME);

            /* Check that the name is valid */
        } else {
            /* The name must not be too long */
            if (myName.length() > NAMELEN) {
                addError(ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* The name must be unique */
            if (!myMap.validNameCount(myName)) {
                final String mySubName = getSubCategory();
                addError(ERROR_DUPLICATE, (mySubName == null)
                        ? PrometheusDataResource.DATAITEM_FIELD_NAME
                        : MoneyWiseBasicResource.CATEGORY_SUBCAT);
            }
        }

        /* Check description length */
        if ((myDesc != null) && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_DESC);
        }
    }

    /**
     * Update base category from an edited category.
     * @param pCategory the edited category
     */
    public void applyBasicChanges(final MoneyWiseCategoryBase pCategory) {
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
        final MoneyWiseCategoryBaseList<?> myList = getList();
        final MoneyWiseCategoryDataMap<?> myMap = myList.getDataMap();
        myMap.adjustForItem(myList.getBaseClass().cast(this));
    }

    @Override
    public void touchOnUpdate() {
        /* Reset self-touches */
        clearTouches(getItemType());

        /* Touch parent if it exists */
        final MoneyWiseCategoryBase myParent = getParentCategory();
        if (myParent != null) {
            myParent.touchItem(this);
        }
    }

    /**
     * The Category Base List class.
     * @param <T> the Category Data type
     */
    public abstract static class MoneyWiseCategoryBaseList<T extends MoneyWiseCategoryBase>
            extends PrometheusEncryptedList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(MoneyWiseCategoryBaseList.class);
        }

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected MoneyWiseCategoryBaseList(final MoneyWiseDataSet pData,
                                            final Class<T> pClass,
                                            final MoneyWiseBasicDataType pItemType) {
            super(pClass, pData, pItemType, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseCategoryBaseList(final MoneyWiseCategoryBaseList<T> pSource) {
            super(pSource);
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected MoneyWiseCategoryDataMap<T> getDataMap() {
            return (MoneyWiseCategoryDataMap<T>) super.getDataMap();
        }

        @Override
        public T findItemByName(final String pName) {
            /* Access the dataMap */
            final MoneyWiseCategoryDataMap<T> myMap = getDataMap();

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
                myName = myCore.concat(Integer.toString(iNextId++));
            }
        }

        /**
         * Update Children.
         * @param pParent the parent item
         * @throws OceanusException on error
         */
        private void updateChildren(final MoneyWiseCategoryBase pParent) throws OceanusException {
            /* Determine the id */
            final Integer myId = pParent.getIndexedId();
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
                myCurr.resolveEditSetLinks();
            }
        }

        @Override
        protected MoneyWiseCategoryDataMap<T> allocateDataMap() {
            return new MoneyWiseCategoryDataMap<>();
        }
    }

    /**
     * The dataMap class.
     * @param <T> the Category Data type
     */
    protected static class MoneyWiseCategoryDataMap<T extends MoneyWiseCategoryBase>
            extends PrometheusDataInstanceMap<T, String> {
        @Override
        @SuppressWarnings("unchecked")
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Access item */
            final T myItem = (T) pItem;

            /* Adjust name count */
            adjustForItem((T) pItem, myItem.getName());
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
