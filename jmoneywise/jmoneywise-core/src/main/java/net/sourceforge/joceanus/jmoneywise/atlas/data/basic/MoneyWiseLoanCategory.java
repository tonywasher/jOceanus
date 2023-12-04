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
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseLoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseLoanCategoryType.MoneyWiseLoanCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
        /* Set values */
        final MoneyWiseLoanCategoryTypeList myTypes = getDataSet().getLoanCategoryTypes();
        setCategoryType(myTypes.findItemByClass(pParent == null
                ? MoneyWiseLoanCategoryClass.PARENT
                : MoneyWiseLoanCategoryClass.LOAN));
        setParentCategory(pParent);
        setSubCategoryName(getList().getUniqueName(pParent));
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
    protected void resolveUpdateSetLinks() throws OceanusException {
        /* Resolve parent within list */
        resolveDataLink(PrometheusDataResource.DATAGROUP_PARENT, getList());
    }

    @Override
    public void setCategoryType(final PrometheusStaticDataItem pType) {
        setValueType((MoneyWiseLoanCategoryType) pType);
    }

    @Override
    public void validate() {
        /* Validate the base */
        super.validate();

        /* Access details */
        final MoneyWiseLoanCategoryType myCatType = getCategoryType();
        final MoneyWiseLoanCategory myParent = getParentCategory();

        /* LoanCategoryType must be non-null */
        if (myCatType == null) {
            addError(ERROR_MISSING, MoneyWiseStaticDataType.LOANTYPE);
        } else {
            /* Access the class */
            final MoneyWiseLoanCategoryClass myClass = myCatType.getLoanClass();

            /* AccountCategoryType must be enabled */
            if (!myCatType.getEnabled()) {
                addError(ERROR_DISABLED, MoneyWiseStaticDataType.LOANTYPE);
            }

            /* Switch on the loan class */
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
                    } else if (!myParent.isCategoryClass(MoneyWiseLoanCategoryClass.PARENT)) {
                        addError(ERROR_BADPARENT, PrometheusDataResource.DATAGROUP_PARENT);
                    } else {
                        final String myName = getName();

                        /* Check validity of parent */
                        final MoneyWiseLoanCategoryClass myParentClass = myParent.getCategoryTypeClass();
                        if (myParentClass != MoneyWiseLoanCategoryClass.PARENT) {
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

        @Override
        protected MoneyWiseLoanCategoryList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseLoanCategoryList myList = new MoneyWiseLoanCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public MoneyWiseLoanCategoryList deriveEditList() {
            /* Build an empty List */
            final MoneyWiseLoanCategoryList myList = getEmptyList(PrometheusListStyle.EDIT);
            myList.ensureMap();

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
