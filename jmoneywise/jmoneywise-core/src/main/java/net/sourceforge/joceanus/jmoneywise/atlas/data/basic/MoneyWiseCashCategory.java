/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.basic;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCashCategoryType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCashCategoryType.MoneyWiseCashCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cash Category class.
 */
public final class MoneyWiseCashCategory
        extends MoneyWiseCategoryBase
        implements MoneyWiseAssetCategory {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.CASHCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.CASHCATEGORY.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseCashCategory> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseCashCategory.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(MoneyWiseStaticDataType.CASHTYPE);
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    MoneyWiseCashCategory(final MoneyWiseCashCategoryList pList,
                          final MoneyWiseCashCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseCashCategory(final MoneyWiseCashCategoryList pList,
                                  final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Category Type */
        final Object myValue = pValues.getValue(MoneyWiseStaticDataType.CASHTYPE);
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
    public MoneyWiseCashCategory(final MoneyWiseCashCategoryList pList) {
        super(pList);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseStaticDataType.CASHTYPE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public MoneyWiseCashCategoryType getCategoryType() {
        return getValues().getValue(MoneyWiseStaticDataType.CASHTYPE, MoneyWiseCashCategoryType.class);
    }

    @Override
    public MoneyWiseCashCategoryClass getCategoryTypeClass() {
        final MoneyWiseCashCategoryType myType = getCategoryType();
        return myType == null
                ? null
                : myType.getCashClass();
    }

    @Override
    public MoneyWiseCashCategory getParentCategory() {
        return getValues().getValue(PrometheusDataResource.DATAGROUP_PARENT, MoneyWiseCashCategory.class);
    }

    /**
     * Set category type value.
     * @param pValue the value
     */
    private void setValueType(final MoneyWiseCashCategoryType pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.CASHTYPE, pValue);
    }

    /**
     * Set category type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.CASHTYPE, pValue);
    }

    /**
     * Set category type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.CASHTYPE, pValue);
    }

    /**
     * Is this cash category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final MoneyWiseCashCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    @Override
    public MoneyWiseCashCategoryList getList() {
        return (MoneyWiseCashCategoryList) super.getList();
    }

    /**
     * Set defaults.
     * @param pParent the parent
     * @throws OceanusException on error
     */
    public void setDefaults(final MoneyWiseCashCategory pParent) throws OceanusException {
        /* Set values */
        final MoneyWiseCashCategoryTypeList myTypes = getDataSet().getCashCategoryTypes();
        setCategoryType(myTypes.findItemByClass(pParent == null
                ? MoneyWiseCashCategoryClass.PARENT
                : MoneyWiseCashCategoryClass.CASH));
        setParentCategory(pParent);
        setSubCategoryName(getList().getUniqueName(pParent));
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the underlying details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(MoneyWiseStaticDataType.CASHTYPE, myData.getCashCategoryTypes());
    }

    @Override
    protected void resolveEditSetLinks() throws OceanusException {
        /* Resolve parent within list */
        resolveDataLink(PrometheusDataResource.DATAGROUP_PARENT, getList());
    }

    @Override
    public void setCategoryType(final PrometheusStaticDataItem pType) {
        setValueType((MoneyWiseCashCategoryType) pType);
    }

    @Override
    public void validate() {
        /* Validate the base */
        super.validate();

        /* Access details */
        final MoneyWiseCashCategoryType myCatType = getCategoryType();
        final MoneyWiseCashCategory myParent = getParentCategory();

        /* CashCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, MoneyWiseStaticDataType.CASHTYPE);
        } else {
            /* Access the class */
            final MoneyWiseCashCategoryClass myClass = myCatType.getCashClass();

            /* CashCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                addError(ERROR_DISABLED, MoneyWiseStaticDataType.CASHTYPE);
            }

            /* Switch on the account class */
            switch (myClass) {
                case PARENT:
                    /* If parent exists */
                    if (myParent != null) {
                        addError(ERROR_EXIST, PrometheusDataResource.DATAGROUP_PARENT);
                    }
                    break;
                default:
                    /* Check parent */
                    if (myParent == null) {
                        addError(ERROR_MISSING, PrometheusDataResource.DATAGROUP_PARENT);
                    } else if (!myParent.isCategoryClass(MoneyWiseCashCategoryClass.PARENT)) {
                        addError(ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                    } else {
                        final String myName = getName();

                        /* Check validity of parent */
                        final MoneyWiseCashCategoryClass myParentClass = myParent.getCategoryTypeClass();
                        if (myParentClass != MoneyWiseCashCategoryClass.PARENT) {
                            addError(ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                        }
                        /* Check that name reflects parent */
                        if ((myName != null) && !myName.startsWith(myParent.getName() + STR_SEP)) {
                            addError(ERROR_MATCHPARENT, PrometheusDataResource.DATAGROUP_PARENT);
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
    public boolean applyChanges(final PrometheusDataItem pCategory) {
        /* Can only update from a cash category */
        if (!(pCategory instanceof MoneyWiseCashCategory)) {
            return false;
        }
        final MoneyWiseCashCategory myCategory = (MoneyWiseCashCategory) pCategory;

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
     * The Cash Category List class.
     */
    public static class MoneyWiseCashCategoryList
            extends MoneyWiseCategoryBaseList<MoneyWiseCashCategory> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseCashCategoryList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCashCategoryList.class);

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         */
        protected MoneyWiseCashCategoryList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWiseCashCategory.class, MoneyWiseBasicDataType.CASHCATEGORY);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseCashCategoryList(final MoneyWiseCashCategoryList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseCashCategoryList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseCashCategory.FIELD_DEFS;
        }

        @Override
        protected MoneyWiseCashCategoryList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseCashCategoryList myList = new MoneyWiseCashCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public MoneyWiseCashCategoryList deriveEditList() {
            /* Build an empty List */
            final MoneyWiseCashCategoryList myList = getEmptyList(PrometheusListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the categories */
            final Iterator<MoneyWiseCashCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseCashCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked cash category and add it to the list */
                final MoneyWiseCashCategory myCategory = new MoneyWiseCashCategory(myList, myCurr);
                myList.add(myCategory);

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
        public MoneyWiseCashCategory addCopyItem(final PrometheusDataItem pCategory) {
            /* Can only clone a CashCategory */
            if (!(pCategory instanceof MoneyWiseCashCategory)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseCashCategory myCategory = new MoneyWiseCashCategory(this, (MoneyWiseCashCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public MoneyWiseCashCategory addNewItem() {
            final MoneyWiseCashCategory myCategory = new MoneyWiseCashCategory(this);
            add(myCategory);
            return myCategory;
        }

        @Override
        public MoneyWiseCashCategory addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the category */
            final MoneyWiseCashCategory myCategory = new MoneyWiseCashCategory(this, pValues);

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
    }
}
