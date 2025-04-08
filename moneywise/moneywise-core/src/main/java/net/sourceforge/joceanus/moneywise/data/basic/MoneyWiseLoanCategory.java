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
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryType.MoneyWiseLoanCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Loan Category class.
 */
public final class MoneyWiseLoanCategory
        extends MoneyWiseCategoryBase
        implements MoneyWiseAssetCategory {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.LOANCATEGORY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.LOANCATEGORY.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseLoanCategory> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseLoanCategory.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(MoneyWiseStaticDataType.LOANTYPE);
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pCategory The Category to copy
     */
    MoneyWiseLoanCategory(final MoneyWiseLoanCategoryList pList,
                          final MoneyWiseLoanCategory pCategory) {
        /* Set standard values */
        super(pList, pCategory);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseLoanCategory(final MoneyWiseLoanCategoryList pList,
                                  final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Category Type */
        final Object myValue = pValues.getValue(MoneyWiseStaticDataType.LOANTYPE);
        if (myValue instanceof Integer i) {
            setValueType(i);
        } else if (myValue instanceof String s) {
            setValueType(s);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public MoneyWiseLoanCategory(final MoneyWiseLoanCategoryList pList) {
        super(pList);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseStaticDataType.LOANTYPE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public MoneyWiseLoanCategoryType getCategoryType() {
        return getValues().getValue(MoneyWiseStaticDataType.LOANTYPE, MoneyWiseLoanCategoryType.class);
    }

    @Override
    public MoneyWiseLoanCategoryClass getCategoryTypeClass() {
        final MoneyWiseLoanCategoryType myType = getCategoryType();
        return myType == null
                ? null
                : myType.getLoanClass();
    }

    @Override
    public MoneyWiseLoanCategory getParentCategory() {
        return getValues().getValue(PrometheusDataResource.DATAGROUP_PARENT, MoneyWiseLoanCategory.class);
    }


    /**
     * Set account type value.
     * @param pValue the value
     */
    private void setValueType(final MoneyWiseLoanCategoryType pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.LOANTYPE, pValue);
    }

    /**
     * Set account type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.LOANTYPE, pValue);
    }

    /**
     * Set account type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticDataType.LOANTYPE, pValue);
    }

    /**
     * Is this loan category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final MoneyWiseLoanCategoryClass pClass) {
        /* Check for match */
        return getCategoryTypeClass() == pClass;
    }

    @Override
    public MoneyWiseLoanCategoryList getList() {
        return (MoneyWiseLoanCategoryList) super.getList();
    }

    /**
     * Set defaults.
     * @param pParent the parent
     * @throws OceanusException on error
     */
    public void setDefaults(final MoneyWiseLoanCategory pParent) throws OceanusException {
        getList().getValidator().setDefaults(pParent, this);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Underlying details */
        super.resolveDataSetLinks();

        /* Resolve category type and parent */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(MoneyWiseStaticDataType.LOANTYPE, myData.getLoanCategoryTypes());
    }

    @Override
    protected void resolveEditSetLinks() throws OceanusException {
        /* Resolve parent within list */
        resolveDataLink(PrometheusDataResource.DATAGROUP_PARENT, getList());

        /* Resolve StaticType if required */
        final PrometheusEditSet myEditSet = getList().getEditSet();
        if (myEditSet.hasDataType(MoneyWiseStaticDataType.LOANTYPE)) {
            resolveDataLink(MoneyWiseStaticDataType.LOANTYPE, myEditSet.getDataList(MoneyWiseStaticDataType.LOANTYPE, MoneyWiseLoanCategoryTypeList.class));
        }
    }

    @Override
    public void setCategoryType(final PrometheusStaticDataItem pType) {
        setValueType((MoneyWiseLoanCategoryType) pType);
    }

    /**
     * Update base category from an edited category.
     * @param pCategory the edited category
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pCategory) {
        /* Can only update from a loan category */
        if (!(pCategory instanceof MoneyWiseLoanCategory)) {
            return false;
        }
        final MoneyWiseLoanCategory myCategory = (MoneyWiseLoanCategory) pCategory;

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
     * The Loan Category List class.
     */
    public static class MoneyWiseLoanCategoryList
            extends MoneyWiseCategoryBaseList<MoneyWiseLoanCategory> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseLoanCategoryList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseLoanCategoryList.class);

        /**
         * The EditSet.
         */
        private PrometheusEditSet theEditSet;

        /**
         * Construct an empty CORE Category list.
         * @param pData the DataSet for the list
         */
        protected MoneyWiseLoanCategoryList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWiseLoanCategory.class, MoneyWiseBasicDataType.LOANCATEGORY);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseLoanCategoryList(final MoneyWiseLoanCategoryList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseLoanCategoryList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseLoanCategory.FIELD_DEFS;
        }

        /**
         * Obtain editSet.
         * @return the editSet
         */
        public PrometheusEditSet getEditSet() {
            return theEditSet;
        }

        @Override
        protected MoneyWiseLoanCategoryList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseLoanCategoryList myList = new MoneyWiseLoanCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pEditSet the editSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public MoneyWiseLoanCategoryList deriveEditList(final PrometheusEditSet pEditSet) throws OceanusException {
            /* Build an empty List */
            final MoneyWiseLoanCategoryList myList = getEmptyList(PrometheusListStyle.EDIT);
            myList.ensureMap();
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.LOANCATEGORY, myList);
            myList.getValidator().setEditSet(pEditSet);

            /* Store the editSet */
            myList.theEditSet = pEditSet;

            /* Loop through the categories */
            final Iterator<MoneyWiseLoanCategory> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseLoanCategory myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked category and add it to the list */
                final MoneyWiseLoanCategory myCategory = new MoneyWiseLoanCategory(myList, myCurr);
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
         * @param pCategory item
         * @return the newly added item
         */
        @Override
        public MoneyWiseLoanCategory addCopyItem(final PrometheusDataItem pCategory) {
            /* Can only clone a LoanCategory */
            if (!(pCategory instanceof MoneyWiseLoanCategory)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseLoanCategory myCategory = new MoneyWiseLoanCategory(this, (MoneyWiseLoanCategory) pCategory);
            add(myCategory);
            return myCategory;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public MoneyWiseLoanCategory addNewItem() {
            final MoneyWiseLoanCategory myCategory = new MoneyWiseLoanCategory(this);
            add(myCategory);
            return myCategory;
        }

        @Override
        public MoneyWiseLoanCategory addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the category */
            final MoneyWiseLoanCategory myCategory = new MoneyWiseLoanCategory(this, pValues);

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
