/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Transaction Category class.
 */
public final class TransactionCategory
        extends CategoryBase<TransactionCategory, TransactionCategoryType, TransactionCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TRANSCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TRANSCATEGORY.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, CategoryBase.FIELD_DEFS);

    /**
     * Category Type Field Id.
     */
    public static final MetisField FIELD_CATTYPE = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataType.TRANSTYPE.getItemName(), MetisDataType.LINK);

    /**
     * Different Parent Error.
     */
    private static final String ERROR_DIFFPARENT = MoneyWiseDataResource.TRANSCATEGORY_ERROR_DIFFPARENT.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    protected TransactionCategory(final TransactionCategoryList pList,
                                  final TransactionCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private TransactionCategory(final TransactionCategoryList pList,
                                final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Category Type */
        final Object myValue = pValues.getValue(FIELD_CATTYPE);
        if (myValue instanceof Integer) {
            setValueType((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueType((String) myValue);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public TransactionCategory(final TransactionCategoryList pList) {
        super(pList);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean isActive() {
        return super.isActive() || isHidden();
    }

    @Override
    public boolean includeXmlField(final MetisField pField) {
        /* Determine whether fields should be included */
        if (FIELD_CATTYPE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public TransactionCategoryType getCategoryType() {
        return getTransCategoryType(getValueSet());
    }

    @Override
    public TransactionCategoryClass getCategoryTypeClass() {
        final TransactionCategoryType myType = getCategoryType();
        return (myType == null)
                                ? null
                                : myType.getCategoryClass();
    }

    @Override
    public TransactionCategory getParentCategory() {
        return getParentCategory(getValueSet());
    }

    /**
     * Obtain CategoryType.
     * @param pValueSet the valueSet
     * @return the CategoryType
     */
    public static TransactionCategoryType getTransCategoryType(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYPE, TransactionCategoryType.class);
    }

    /**
     * Obtain Parent Category.
     * @param pValueSet the valueSet
     * @return the Parent Category
     */
    public static TransactionCategory getParentCategory(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, TransactionCategory.class);
    }

    /**
     * Set category type value.
     * @param pValue the value
     */
    private void setValueType(final TransactionCategoryType pValue) {
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

    @Override
    public TransactionCategoryList getList() {
        return (TransactionCategoryList) super.getList();
    }

    /**
     * Is this event category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final TransactionCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    /**
     * Is this event category a transfer?
     * @return true/false
     */
    public boolean isTransfer() {
        /* Check for match */
        final TransactionCategoryClass myClass = getCategoryTypeClass();
        return myClass != null
               && myClass.isTransfer();
    }

    /**
     * Set defaults.
     * @param pParent the parent
     * @throws OceanusException on error
     */
    public void setDefaults(final TransactionCategory pParent) throws OceanusException {
        /* Set values */
        final TransactionCategoryTypeList myTypes = getDataSet().getTransCategoryTypes();
        final TransactionCategoryClass myParentClass = pParent.getCategoryTypeClass();
        final TransactionCategoryClass myNewClass;
        if (myParentClass.isTotals()) {
            myNewClass = TransactionCategoryClass.EXPENSETOTALS;
        } else if (myParentClass.isIncome()) {
            myNewClass = TransactionCategoryClass.TAXEDINCOME;
        } else if (myParentClass.isTransfer()) {
            myNewClass = TransactionCategoryClass.STOCKSPLIT;
        } else {
            myNewClass = TransactionCategoryClass.EXPENSE;
        }
        setCategoryType(myTypes.findItemByClass(myNewClass));
        setParentCategory(pParent);
        setSubCategoryName(getList().getUniqueName(pParent));
    }

    @Override
    public int compareTo(final TransactionCategory pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the hidden attribute */
        final boolean isHidden = isHidden();
        if (isHidden != pThat.isHidden()) {
            return isHidden
                            ? 1
                            : -1;
        }

        /* Compare the underlying id */
        return super.compareTo(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Underlying details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_CATTYPE, myData.getTransCategoryTypes());
    }

    @Override
    protected void resolveUpdateSetLinks() throws OceanusException {
        /* Resolve parent within list */
        resolveDataLink(FIELD_PARENT, getList());
    }

    @Override
    public void setCategoryType(final TransactionCategoryType pType) {
        setValueType(pType);
    }

    @Override
    public void validate() {
        /* Validate the base */
        super.validate();

        /* Access details */
        final TransactionCategoryList myList = getList();
        final TransactionCategoryType myCatType = getCategoryType();
        final TransactionCategory myParent = getParentCategory();
        final String myName = getName();

        /* EventCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, FIELD_CATTYPE);
        } else {
            /* Access the class */
            final TransactionCategoryClass myClass = myCatType.getCategoryClass();

            /* EventCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_CATTYPE);
            }

            /* If the CategoryType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                final TransCategoryDataMap myMap = myList.getDataMap();
                if (!myMap.validSingularCount(myClass)) {
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
                case SECURITYPARENT:
                    /* Check parent */
                    if (myParent == null) {
                        addError(ERROR_MISSING, FIELD_PARENT);
                    } else if (!myParent.isCategoryClass(TransactionCategoryClass.TOTALS)) {
                        addError(ERROR_BADPARENT, FIELD_PARENT);
                    }
                    break;
                default:
                    /* Check parent requirement */
                    final boolean isTransfer = myClass == TransactionCategoryClass.TRANSFER;
                    final boolean hasParent = myParent != null;
                    if (hasParent == isTransfer) {
                        if (isTransfer) {
                            addError(ERROR_EXIST, FIELD_PARENT);
                        } else {
                            addError(ERROR_MISSING, FIELD_PARENT);
                        }
                    } else if (hasParent) {
                        /* Check validity of parent */
                        final TransactionCategoryClass myParentClass = myParent.getCategoryTypeClass();
                        if (!myParentClass.canParentCategory()) {
                            addError(ERROR_BADPARENT, FIELD_PARENT);
                        }
                        if (myParentClass.isIncome() != myClass.isIncome()
                            || myParentClass.isSecurityTransfer() != myClass.isSecurityTransfer()) {
                            addError(ERROR_DIFFPARENT, FIELD_PARENT);
                        }

                        /* Check that name reflects parent */
                        if (myName != null && !myName.startsWith(myParent.getName() + STR_SEP)) {
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
    public boolean applyChanges(final DataItem<?> pCategory) {
        /* Can only update from a transaction category */
        if (!(pCategory instanceof TransactionCategory)) {
            return false;
        }
        final TransactionCategory myCategory = (TransactionCategory) pCategory;

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
     * @return true/false
     */
    public boolean isHidden() {
        final TransactionCategoryClass myClass = this.getCategoryTypeClass();
        return myClass != null
               && myClass.isHiddenType();
    }

    /**
     * The Transaction Category List class.
     */
    public static class TransactionCategoryList
            extends CategoryBaseList<TransactionCategory, TransactionCategoryType, TransactionCategoryClass> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, CategoryBaseList.FIELD_DEFS);

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         */
        public TransactionCategoryList(final MoneyWiseData pData) {
            super(pData, TransactionCategory.class, MoneyWiseDataType.TRANSCATEGORY);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected TransactionCategoryList(final TransactionCategoryList pSource) {
            super(pSource);
        }

        @Override
        public MetisFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return TransactionCategory.FIELD_DEFS;
        }

        @Override
        protected TransCategoryDataMap getDataMap() {
            return (TransCategoryDataMap) super.getDataMap();
        }

        @Override
        protected TransactionCategoryList getEmptyList(final ListStyle pStyle) {
            final TransactionCategoryList myList = new TransactionCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public TransactionCategoryList deriveEditList() {
            /* Build an empty List */
            final TransactionCategoryList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the categories */
            final Iterator<TransactionCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                final TransactionCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked category and add it to the list */
                final TransactionCategory myCategory = new TransactionCategory(myList, myCurr);
                myList.append(myCategory);

                /* Adjust the map */
                myCategory.adjustMapForItem();
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
        public TransactionCategory addCopyItem(final DataItem<?> pCategory) {
            /* Can only clone a TransactionCategory */
            if (!(pCategory instanceof TransactionCategory)) {
                throw new UnsupportedOperationException();
            }

            final TransactionCategory myCategory = new TransactionCategory(this, (TransactionCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public TransactionCategory addNewItem() {
            final TransactionCategory myCategory = new TransactionCategory(this);
            add(myCategory);
            return myCategory;
        }

        /**
         * Obtain the first category for the specified class.
         * @param pClass the category class
         * @return the category
         */
        public TransactionCategory getSingularClass(final TransactionCategoryClass pClass) {
            /* Lookup in the map */
            return getDataMap().findSingularItem(pClass);
        }

        /**
         * Obtain singular category for EventInfoClass.
         * @param pInfoClass the Event info class
         * @return the corresponding category.
         */
        public TransactionCategory getEventInfoCategory(final TransactionInfoClass pInfoClass) {
            /* Switch on info class */
            switch (pInfoClass) {
                case TAXCREDIT:
                    return getSingularClass(TransactionCategoryClass.INCOMETAX);
                case DEEMEDBENEFIT:
                    return getSingularClass(TransactionCategoryClass.VIRTUALINCOME);
                case EMPLOYEENATINS:
                    return getSingularClass(TransactionCategoryClass.EMPLOYEENATINS);
                case EMPLOYERNATINS:
                    return getSingularClass(TransactionCategoryClass.EMPLOYERNATINS);
                case WITHHELD:
                    return getSingularClass(TransactionCategoryClass.WITHHELD);
                default:
                    return null;
            }
        }

        @Override
        public TransactionCategory addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the category */
            final TransactionCategory myCategory = new TransactionCategory(this, pValues);

            /* Check that this CategoryId has not been previously added */
            if (!isIdUnique(myCategory.getId())) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myCategory, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myCategory);

            /* Return it */
            return myCategory;
        }

        @Override
        protected TransCategoryDataMap allocateDataMap() {
            return new TransCategoryDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    protected static class TransCategoryDataMap
            extends CategoryDataMap<TransactionCategory, TransactionCategoryType, TransactionCategoryClass> {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(PrometheusDataResource.DATAMAP_NAME.getValue(), CategoryDataMap.FIELD_DEFS);

        /**
         * CategoryMap Field Id.
         */
        private static final MetisField FIELD_CATMAP = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_MAP_SINGULARMAP.getValue());

        /**
         * CategoryCountMap Field Id.
         */
        private static final MetisField FIELD_CATCOUNT = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_MAP_SINGULARCOUNTS.getValue());

        /**
         * Map of category counts.
         */
        private final Map<Integer, Integer> theCategoryCountMap;

        /**
         * Map of singular categories.
         */
        private final Map<Integer, TransactionCategory> theCategoryMap;

        /**
         * Constructor.
         */
        public TransCategoryDataMap() {
            /* Create the maps */
            theCategoryCountMap = new HashMap<>();
            theCategoryMap = new HashMap<>();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_CATMAP.equals(pField)) {
                return theCategoryMap;
            }
            if (FIELD_CATCOUNT.equals(pField)) {
                return theCategoryCountMap;
            }

            /* Unknown */
            return super.getFieldValue(pField);
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        @Override
        public void resetMap() {
            super.resetMap();
            theCategoryCountMap.clear();
            theCategoryMap.clear();
        }

        @Override
        public void adjustForItem(final TransactionCategory pItem) {
            /* If the class is singular */
            final TransactionCategoryClass myClass = pItem.getCategoryTypeClass();
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
                theCategoryMap.put(myId, pItem);
            }

            /* Adjust name count */
            adjustForItem(pItem, pItem.getName());
        }

        /**
         * find singular item.
         * @param pClass the class to look up
         * @return the matching item
         */
        public TransactionCategory findSingularItem(final TransactionCategoryClass pClass) {
            return theCategoryMap.get(pClass.getClassId());
        }

        /**
         * Check validity of singular count.
         * @param pClass the class to look up
         * @return true/false
         */
        public boolean validSingularCount(final TransactionCategoryClass pClass) {
            final Integer myResult = theCategoryCountMap.get(pClass.getClassId());
            return ONE.equals(myResult);
        }
    }
}
