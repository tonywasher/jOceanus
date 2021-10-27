/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedString;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfoLinkSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Tag for a transaction.
 */
public class TransactionTag
        extends EncryptedItem<MoneyWiseDataType>
        implements MetisDataNamedItem, Comparable<TransactionTag> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TRANSTAG.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TRANSTAG.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final MetisLetheField FIELD_NAME = FIELD_DEFS.declareComparisonEncryptedField(PrometheusDataResource.DATAITEM_FIELD_NAME.getValue(), MetisDataType.STRING, NAMELEN);

    /**
     * Description Field Id.
     */
    public static final MetisLetheField FIELD_DESC = FIELD_DEFS.declareEqualityEncryptedField(PrometheusDataResource.DATAITEM_FIELD_DESC.getValue(), MetisDataType.STRING, DESCLEN);

    /**
     * New Tag name.
     */
    private static final String NAME_NEWTAG = MoneyWiseDataResource.TRANSTAG_NEWTAG.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pClass The Class to copy
     */
    protected TransactionTag(final TransactionTagList pList,
                             final TransactionTag pClass) {
        /* Set standard values */
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private TransactionTag(final TransactionTagList pList,
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
    public TransactionTag(final TransactionTagList pList) {
        super(pList, 0);
        setNextDataKeySet();
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
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

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain Name.
     * @return the name
     */
    public String getName() {
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

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public TransactionTag getBase() {
        return (TransactionTag) super.getBase();
    }

    @Override
    public TransactionTagList getList() {
        return (TransactionTagList) super.getList();
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
    public int compareTo(final TransactionTag pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the names */
        final int iDiff = MetisDataDifference.compareObject(getName(), pThat.getName());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
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

    @Override
    public void validate() {
        final TransactionTagList myList = getList();
        final String myName = getName();
        final String myDesc = getDesc();
        final TagDataMap myMap = myList.getDataMap();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, FIELD_NAME);

            /* Else check the name */
        } else {
            /* The description must not be too long */
            if (myName.length() > NAMELEN) {
                addError(ERROR_LENGTH, FIELD_NAME);
            }

            /* Check that the name is unique */
            if (!myMap.validNameCount(myName)) {
                addError(ERROR_DUPLICATE, FIELD_NAME);
            }

            /* Check that the name does not contain invalid characters */
            if (myName.contains(DataInfoLinkSet.ITEM_SEP)) {
                addError(ERROR_INVALIDCHAR, FIELD_NAME);
            }
        }

        /* Check description length */
        if ((myDesc != null) && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_NAME);
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
    public boolean applyChanges(final DataItem<?> pTag) {
        /* Can only update from a transaction tag */
        if (!(pTag instanceof TransactionTag)) {
            return false;
        }
        final TransactionTag myTag = (TransactionTag) pTag;

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
        final TransactionTagList myList = getList();
        final TagDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Transaction Tag List class.
     */
    public static class TransactionTagList
            extends EncryptedList<TransactionTag, MoneyWiseDataType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<TransactionTagList> FIELD_DEFS = MetisFieldSet.newFieldSet(TransactionTagList.class);

        /**
         * Construct an empty CORE Tag list.
         *
         * @param pData the DataSet for the list
         */
        protected TransactionTagList(final MoneyWiseData pData) {
            super(TransactionTag.class, pData, MoneyWiseDataType.TRANSTAG, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected TransactionTagList(final TransactionTagList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<TransactionTagList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return TransactionTag.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        protected TagDataMap getDataMap() {
            return (TagDataMap) super.getDataMap();
        }

        @Override
        protected TransactionTagList getEmptyList(final ListStyle pStyle) {
            final TransactionTagList myList = new TransactionTagList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public TransactionTagList deriveEditList() {
            /* Build an empty List */
            final TransactionTagList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the tags */
            final Iterator<TransactionTag> myIterator = iterator();
            while (myIterator.hasNext()) {
                final TransactionTag myCurr = myIterator.next();

                /* Ignore deleted tags */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked tag and add it to the list */
                final TransactionTag myTag = new TransactionTag(myList, myCurr);
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
        public TransactionTag addCopyItem(final DataItem<?> pTag) {
            /* Can only clone a TransactionTag */
            if (!(pTag instanceof TransactionTag)) {
                throw new UnsupportedOperationException();
            }

            final TransactionTag myTag = new TransactionTag(this, (TransactionTag) pTag);
            add(myTag);
            return myTag;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public TransactionTag addNewItem() {
            final TransactionTag myTag = new TransactionTag(this);
            add(myTag);
            return myTag;
        }

        @Override
        public TransactionTag findItemByName(final String pName) {
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
        public TransactionTag addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the tag */
            final TransactionTag myTag = new TransactionTag(this, pValues);

            /* Check that this TagId has not been previously added */
            if (!isIdUnique(myTag.getId())) {
                myTag.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myTag, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myTag);

            /* Return it */
            return myTag;
        }

        @Override
        protected TagDataMap allocateDataMap() {
            return new TagDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    protected static class TagDataMap
            extends DataInstanceMap<TransactionTag, MoneyWiseDataType, String> {
        @Override
        public void adjustForItem(final TransactionTag pItem) {
            /* Adjust name count */
            adjustForItem(pItem, pItem.getName());
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public TransactionTag findItemByName(final String pName) {
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
