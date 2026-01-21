/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.data.basic;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.data.MetisDataResource;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersionedSet;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType.MoneyWiseTransCategoryTypeList;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Transaction Category class.
 */
public final class MoneyWiseTransCategory
        extends MoneyWiseCategoryBase {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.TRANSCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.TRANSCATEGORY.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseTransCategory> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseTransCategory.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(MoneyWiseStaticDataType.TRANSTYPE);
    }

    /**
     * Copy Constructor.
     *
     * @param pList     the list
     * @param pCategory The Category to copy
     */
    MoneyWiseTransCategory(final MoneyWiseTransCategoryList pList,
                           final MoneyWiseTransCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     *
     * @param pList   the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseTransCategory(final MoneyWiseTransCategoryList pList,
                                   final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Category Type */
        final Object myValue = pValues.getValue(MoneyWiseStaticDataType.TRANSTYPE);
        if (myValue instanceof Integer i) {
            setValueType(i);
        } else if (myValue instanceof String s) {
            setValueType(s);
        }
    }

    /**
     * Edit Constructor.
     *
     * @param pList the list
     */
    public MoneyWiseTransCategory(final MoneyWiseTransCategoryList pList) {
        super(pList);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isActive() {
        return super.isActive() || isHidden();
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseStaticDataType.TRANSTYPE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public MoneyWiseTransCategoryType getCategoryType() {
        return getValues().getValue(MoneyWiseStaticDataType.TRANSTYPE, MoneyWiseTransCategoryType.class);
    }

    @Override
    public MoneyWiseTransCategoryClass getCategoryTypeClass() {
        final MoneyWiseTransCategoryType myType = getCategoryType();
        return myType == null
                ? null
                : myType.getCategoryClass();
    }

    @Override
    public MoneyWiseTransCategory getParentCategory() {
        return getValues().getValue(PrometheusDataResource.DATAGROUP_PARENT, MoneyWiseTransCategory.class);
    }

    /**
     * Set category type value.
     *
     * @param pValue the value
     */
    private void setValueType(final MoneyWiseTransCategoryType pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.TRANSTYPE, pValue);
    }

    /**
     * Set category type id.
     *
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.TRANSTYPE, pValue);
    }

    /**
     * Set category type name.
     *
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.TRANSTYPE, pValue);
    }

    @Override
    public MoneyWiseTransCategoryList getList() {
        return (MoneyWiseTransCategoryList) super.getList();
    }

    /**
     * Is this event category the required class.
     *
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final MoneyWiseTransCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    /**
     * Is this event category a transfer?
     *
     * @return true/false
     */
    public boolean isTransfer() {
        /* Check for match */
        final MoneyWiseTransCategoryClass myClass = getCategoryTypeClass();
        return myClass != null
                && myClass.isTransfer();
    }

    /**
     * Set defaults.
     *
     * @param pParent the parent
     * @throws OceanusException on error
     */
    public void setDefaults(final MoneyWiseTransCategory pParent) throws OceanusException {
        getList().getValidator().setDefaults(pParent, this);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Underlying details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(MoneyWiseStaticDataType.TRANSTYPE, myData.getTransCategoryTypes());
    }

    @Override
    protected void resolveEditSetLinks() throws OceanusException {
        /* Resolve parent within list */
        resolveDataLink(PrometheusDataResource.DATAGROUP_PARENT, getList());

        /* Resolve StaticType if required */
        final PrometheusEditSet myEditSet = getList().getEditSet();
        if (myEditSet.hasDataType(MoneyWiseStaticDataType.TRANSTYPE)) {
            resolveDataLink(MoneyWiseStaticDataType.TRANSTYPE, myEditSet.getDataList(MoneyWiseStaticDataType.TRANSTYPE, MoneyWiseTransCategoryTypeList.class));
        }
    }

    @Override
    public void setCategoryType(final PrometheusStaticDataItem pType) {
        setValueType((MoneyWiseTransCategoryType) pType);
    }

    /**
     * Update base category from an edited category.
     *
     * @param pCategory the edited category
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pCategory) {
        /* Can only update from a transaction category */
        if (!(pCategory instanceof MoneyWiseTransCategory)) {
            return false;
        }
        final MoneyWiseTransCategory myCategory = (MoneyWiseTransCategory) pCategory;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myCategory);

        /* Update the category type if required */
        if (!MetisDataDifference.isEqual(getCategoryType(), myCategory.getCategoryType())) {
            setValueType(myCategory.getCategoryType());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Is the category hidden?
     *
     * @return true/false
     */
    public boolean isHidden() {
        final MoneyWiseTransCategoryClass myClass = this.getCategoryTypeClass();
        return myClass != null
                && myClass.isHiddenType();
    }

    /**
     * The Transaction Category List class.
     */
    public static class MoneyWiseTransCategoryList
            extends MoneyWiseCategoryBaseList<MoneyWiseTransCategory> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTransCategoryList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransCategoryList.class);

        /**
         * The EditSet.
         */
        private PrometheusEditSet theEditSet;

        /**
         * Construct an empty CORE Category list.
         *
         * @param pData the DataSet for the list
         */
        public MoneyWiseTransCategoryList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWiseTransCategory.class, MoneyWiseBasicDataType.TRANSCATEGORY);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        protected MoneyWiseTransCategoryList(final MoneyWiseTransCategoryList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseTransCategoryList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseTransCategory.FIELD_DEFS;
        }

        /**
         * Obtain editSet.
         *
         * @return the editSet
         */
        public PrometheusEditSet getEditSet() {
            return theEditSet;
        }

        @Override
        public MoneyWiseTransCategoryDataMap getDataMap() {
            return (MoneyWiseTransCategoryDataMap) super.getDataMap();
        }

        @Override
        protected MoneyWiseTransCategoryList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseTransCategoryList myList = new MoneyWiseTransCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         *
         * @param pEditSet the editSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public MoneyWiseTransCategoryList deriveEditList(final PrometheusEditSet pEditSet) throws OceanusException {
            /* Build an empty List */
            final MoneyWiseTransCategoryList myList = getEmptyList(PrometheusListStyle.EDIT);
            myList.ensureMap();
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.TRANSCATEGORY, myList);
            myList.getValidator().setEditSet(pEditSet);

            /* Store the editSet */
            myList.theEditSet = pEditSet;

            /* Loop through the categories */
            final Iterator<MoneyWiseTransCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseTransCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked category and add it to the list */
                final MoneyWiseTransCategory myCategory = new MoneyWiseTransCategory(myList, myCurr);
                myList.add(myCategory);
                myCategory.resolveEditSetLinks();

                /* Adjust the map */
                myCategory.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        /**
         * Add a new item to the core list.
         *
         * @param pCategory item
         * @return the newly added item
         */
        @Override
        public MoneyWiseTransCategory addCopyItem(final PrometheusDataItem pCategory) {
            /* Can only clone a TransactionCategory */
            if (!(pCategory instanceof MoneyWiseTransCategory)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseTransCategory myCategory = new MoneyWiseTransCategory(this, (MoneyWiseTransCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         *
         * @return the new item
         */
        @Override
        public MoneyWiseTransCategory addNewItem() {
            final MoneyWiseTransCategory myCategory = new MoneyWiseTransCategory(this);
            add(myCategory);
            return myCategory;
        }

        /**
         * Obtain the first category for the specified class.
         *
         * @param pClass the category class
         * @return the category
         */
        public MoneyWiseTransCategory getSingularClass(final MoneyWiseTransCategoryClass pClass) {
            /* Lookup in the map */
            return getDataMap().findSingularItem(pClass);
        }

        /**
         * Obtain singular category for EventInfoClass.
         *
         * @param pInfoClass the Event info class
         * @return the corresponding category.
         */
        public MoneyWiseTransCategory getEventInfoCategory(final MoneyWiseTransInfoClass pInfoClass) {
            /* Switch on info class */
            switch (pInfoClass) {
                case TAXCREDIT:
                    return getSingularClass(MoneyWiseTransCategoryClass.INCOMETAX);
                case DEEMEDBENEFIT:
                    return getSingularClass(MoneyWiseTransCategoryClass.VIRTUALINCOME);
                case EMPLOYEENATINS:
                    return getSingularClass(MoneyWiseTransCategoryClass.EMPLOYEENATINS);
                case EMPLOYERNATINS:
                    return getSingularClass(MoneyWiseTransCategoryClass.EMPLOYERNATINS);
                case WITHHELD:
                    return getSingularClass(MoneyWiseTransCategoryClass.WITHHELD);
                default:
                    return null;
            }
        }

        @Override
        public MoneyWiseTransCategory addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the category */
            final MoneyWiseTransCategory myCategory = new MoneyWiseTransCategory(this, pValues);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(myCategory.getIndexedId())) {
                myCategory.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCategory, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myCategory);

            /* Return it */
            return myCategory;
        }

        @Override
        protected MoneyWiseTransCategoryDataMap allocateDataMap() {
            return new MoneyWiseTransCategoryDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    public static class MoneyWiseTransCategoryDataMap
            extends MoneyWiseCategoryDataMap<MoneyWiseTransCategory> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTransCategoryDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransCategoryDataMap.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_SINGULARMAP, MoneyWiseTransCategoryDataMap::getSingularMap);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_SINGULARCOUNTS, MoneyWiseTransCategoryDataMap::getSingularCountMap);
        }

        /**
         * Map of category counts.
         */
        private final Map<Integer, Integer> theCategoryCountMap;

        /**
         * Map of singular categories.
         */
        private final Map<Integer, MoneyWiseTransCategory> theCategoryMap;

        /**
         * Constructor.
         */
        public MoneyWiseTransCategoryDataMap() {
            /* Create the maps */
            theCategoryCountMap = new HashMap<>();
            theCategoryMap = new HashMap<>();
        }

        @Override
        public MetisFieldSet<MoneyWiseTransCategoryDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain the categoryMap.
         *
         * @return the map
         */
        private Map<Integer, MoneyWiseTransCategory> getSingularMap() {
            return theCategoryMap;
        }

        /**
         * Obtain the categoryCountMap.
         *
         * @return the map
         */
        private Map<Integer, Integer> getSingularCountMap() {
            return theCategoryCountMap;
        }

        @Override
        public void resetMap() {
            super.resetMap();
            theCategoryCountMap.clear();
            theCategoryMap.clear();
        }

        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Access item */
            final MoneyWiseTransCategory myItem = (MoneyWiseTransCategory) pItem;

            /* If the class is singular */
            final MoneyWiseTransCategoryClass myClass = Objects.requireNonNull(myItem.getCategoryTypeClass());
            if (myClass.isSingular()) {
                /* Adjust category count */
                final Integer myId = myClass.getClassId();
                final Integer myCount = theCategoryCountMap.get(myId);
                if (myCount == null) {
                    theCategoryCountMap.put(myId, ONE);
                } else {
                    theCategoryCountMap.put(myId, myCount + 1);
                }

                /* Adjust category map */
                theCategoryMap.put(myId, myItem);
            }

            /* Adjust name count */
            adjustForItem(myItem, myItem.getName());
        }

        /**
         * find singular item.
         *
         * @param pClass the class to look up
         * @return the matching item
         */
        public MoneyWiseTransCategory findSingularItem(final MoneyWiseTransCategoryClass pClass) {
            return theCategoryMap.get(pClass.getClassId());
        }

        /**
         * Check validity of singular count.
         *
         * @param pClass the class to look up
         * @return true/false
         */
        public boolean validSingularCount(final MoneyWiseTransCategoryClass pClass) {
            final Integer myResult = theCategoryCountMap.get(pClass.getClassId());
            return ONE.equals(myResult);
        }
    }
}
