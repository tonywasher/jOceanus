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
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoLinkSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInstanceMap;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedFieldSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedPair;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Tag for a transaction.
 */
public class MoneyWiseTransTag
        extends PrometheusEncryptedDataItem
        implements MetisDataNamedItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.TRANSTAG.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.TRANSTAG.getListName();

    /**
     * Local Report fields.
     */
    private static final PrometheusEncryptedFieldSet<MoneyWiseTransTag> FIELD_DEFS = PrometheusEncryptedFieldSet.newEncryptedFieldSet(MoneyWiseTransTag.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareEncryptedStringField(PrometheusDataResource.DATAITEM_FIELD_NAME, NAMELEN);
        FIELD_DEFS.declareEncryptedStringField(PrometheusDataResource.DATAITEM_FIELD_DESC, DESCLEN);
    }

    /**
     * New Tag name.
     */
    private static final String NAME_NEWTAG = MoneyWiseBasicResource.TRANSTAG_NEWTAG.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pClass The Class to copy
     */
    protected MoneyWiseTransTag(final MoneyWiseTransTagList pList,
                                final MoneyWiseTransTag pClass) {
        /* Set standard values */
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseTransTag(final MoneyWiseTransTagList pList,
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
    public MoneyWiseTransTag(final MoneyWiseTransTagList pList) {
        super(pList, 0);
        setNextDataKeySet();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
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

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseTransTag getBase() {
        return (MoneyWiseTransTag) super.getBase();
    }

    @Override
    public MoneyWiseTransTagList getList() {
        return (MoneyWiseTransTagList) super.getList();
    }

    /**
     * Set defaults.
     * @throws OceanusException on error
     */
    public void setDefaults() throws OceanusException {
        /* Set values */
        setName(getList().getUniqueName());
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Check the names */
        final MoneyWiseTransTag myThat = (MoneyWiseTransTag) pThat;
        return MetisDataDifference.compareObject(getName(), myThat.getName());
    }

    /**
     * Set a new tag name.
     * @param pName the new name
     * @throws OceanusException on error
     */
    public void setName(final String pName) throws OceanusException {
        setValueName(pName);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws OceanusException on error
     */
    public void setDescription(final String pDesc) throws OceanusException {
        setValueDesc(pDesc);
    }

    //@Override
    public void valdddidate() {
        final MoneyWiseTransTagList myList = getList();
        final String myName = getName();
        final String myDesc = getDesc();
        final MoneyWiseTagDataMap myMap = myList.getDataMap();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, PrometheusDataResource.DATAITEM_FIELD_NAME);

            /* Else check the name */
        } else {
            /* The description must not be too long */
            if (myName.length() > NAMELEN) {
                addError(ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* Check that the name is unique */
            if (!myMap.validNameCount(myName)) {
                addError(ERROR_DUPLICATE, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }

            /* Check that the name does not contain invalid characters */
            if (myName.contains(PrometheusDataInfoLinkSet.ITEM_SEP)) {
                addError(ERROR_INVALIDCHAR, PrometheusDataResource.DATAITEM_FIELD_NAME);
            }
        }

        /* Check description length */
        if (myDesc != null
                && myDesc.length() > DESCLEN) {
            addError(ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_DESC);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Update base tag from an edited tag.
     * @param pTag the edited tag
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pTag) {
        /* Can only update from a transaction tag */
        if (!(pTag instanceof MoneyWiseTransTag)) {
            return false;
        }
        final MoneyWiseTransTag myTag = (MoneyWiseTransTag) pTag;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Name if required */
        if (!MetisDataDifference.isEqual(getName(), myTag.getName())) {
            setValueName(myTag.getNameField());
        }

        /* Update the description if required */
        if (!MetisDataDifference.isEqual(getDesc(), myTag.getDesc())) {
            setValueDesc(myTag.getDescField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final MoneyWiseTransTagList myList = getList();
        final MoneyWiseTagDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Transaction Tag List class.
     */
    public static class MoneyWiseTransTagList
            extends PrometheusEncryptedList<MoneyWiseTransTag> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTransTagList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransTagList.class);

        /**
         * Construct an empty CORE Tag list.
         *
         * @param pData the DataSet for the list
         */
        protected MoneyWiseTransTagList(final MoneyWiseDataSet pData) {
            super(MoneyWiseTransTag.class, pData, MoneyWiseBasicDataType.TRANSTAG, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseTransTagList(final MoneyWiseTransTagList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseTransTagList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseTransTag.FIELD_DEFS;
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        @Override
        public MoneyWiseTagDataMap getDataMap() {
            return (MoneyWiseTagDataMap) super.getDataMap();
        }

        @Override
        protected MoneyWiseTransTagList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseTransTagList myList = new MoneyWiseTransTagList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pEditSet the editSet
         * @return the edit list
         */
        public MoneyWiseTransTagList deriveEditList(final PrometheusEditSet pEditSet) {
            /* Build an empty List */
            final MoneyWiseTransTagList myList = getEmptyList(PrometheusListStyle.EDIT);
            myList.ensureMap();
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.TRANSTAG, myList);

            /* Loop through the tags */
            final Iterator<MoneyWiseTransTag> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseTransTag myCurr = myIterator.next();

                /* Ignore deleted tags */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked tag and add it to the list */
                final MoneyWiseTransTag myTag = new MoneyWiseTransTag(myList, myCurr);
                myList.add(myTag);

                /* Adjust the map */
                myTag.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pTag item
         * @return the newly added item
         */
        @Override
        public MoneyWiseTransTag addCopyItem(final PrometheusDataItem pTag) {
            /* Can only clone a TransactionTag */
            if (!(pTag instanceof MoneyWiseTransTag)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseTransTag myTag = new MoneyWiseTransTag(this, (MoneyWiseTransTag) pTag);
            add(myTag);
            return myTag;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public MoneyWiseTransTag addNewItem() {
            final MoneyWiseTransTag myTag = new MoneyWiseTransTag(this);
            add(myTag);
            return myTag;
        }

        @Override
        public MoneyWiseTransTag findItemByName(final String pName) {
            /* look up the name in the map */
            return getDataMap().findItemByName(pName);
        }

        /**
         * Obtain unique name for new tag.
         * @return The new name
         */
        public String getUniqueName() {
            /* Set up base constraints */
            final String myBase = NAME_NEWTAG;
            int iNextId = 1;

            /* Loop until we found a name */
            String myName = myBase;
            for (;;) {
                /* try out the name */
                if (findItemByName(myName) == null) {
                    return myName;
                }

                /* Build next name */
                myName = myBase.concat(Integer.toString(iNextId++));
            }
        }

        @Override
        public MoneyWiseTransTag addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the tag */
            final MoneyWiseTransTag myTag = new MoneyWiseTransTag(this, pValues);

            /* Check that this TagId has not been previously added */
            if (!isIdUnique(myTag.getIndexedId())) {
                myTag.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myTag, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myTag);

            /* Return it */
            return myTag;
        }

        @Override
        protected MoneyWiseTagDataMap allocateDataMap() {
            return new MoneyWiseTagDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    public static class MoneyWiseTagDataMap
            extends PrometheusDataInstanceMap<MoneyWiseTransTag, String> {
        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Adjust name count */
            final MoneyWiseTransTag myItem = (MoneyWiseTransTag) pItem;
            adjustForItem(myItem, myItem.getName());
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public MoneyWiseTransTag findItemByName(final String pName) {
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
