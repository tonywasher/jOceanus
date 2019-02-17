/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2019 Tony Washer
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
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
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
 * Region for a Unit Trust.
 */
public class Region
        extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<Region> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.REGION.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.REGION.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final MetisField FIELD_NAME = FIELD_DEFS.declareComparisonEncryptedField(PrometheusDataResource.DATAITEM_FIELD_NAME.getValue(), MetisDataType.STRING, NAMELEN);

    /**
     * Description Field Id.
     */
    public static final MetisField FIELD_DESC = FIELD_DEFS.declareEqualityEncryptedField(PrometheusDataResource.DATAITEM_FIELD_DESC.getValue(), MetisDataType.STRING, DESCLEN);

    /**
     * New Region name.
     */
    private static final String NAME_NEWREGION = MoneyWiseDataResource.REGION_NEWREGION.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pRegion The region to copy
     */
    protected Region(final RegionList pList,
                     final Region pRegion) {
        /* Set standard values */
        super(pList, pRegion);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private Region(final RegionList pList,
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
    public Region(final RegionList pList) {
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
    public boolean includeXmlField(final MetisField pField) {
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
    public Region getBase() {
        return (Region) super.getBase();
    }

    @Override
    public RegionList getList() {
        return (RegionList) super.getList();
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
    public int compareTo(final Region pThat) {
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
        final RegionList myList = getList();
        final String myName = getName();
        final String myDesc = getDesc();
        final RegionDataMap myMap = myList.getDataMap();

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
        /* Can only update from a region */
        if (!(pTag instanceof Region)) {
            return false;
        }
        final Region myRegion = (Region) pTag;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Name if required */
        if (!MetisDataDifference.isEqual(getName(), myRegion.getName())) {
            setValueName(myRegion.getNameField());
        }

        /* Update the description if required */
        if (!MetisDataDifference.isEqual(getDesc(), myRegion.getDesc())) {
            setValueDesc(myRegion.getDescField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final RegionList myList = getList();
        final RegionDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Region List class.
     */
    public static class RegionList
            extends EncryptedList<Region, MoneyWiseDataType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<RegionList> FIELD_DEFS = MetisFieldSet.newFieldSet(RegionList.class);

        /**
         * Construct an empty CORE Tag list.
         * <p>
         * @param pData the DataSet for the list
         */
        protected RegionList(final MoneyWiseData pData) {
            super(Region.class, pData, MoneyWiseDataType.REGION, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected RegionList(final RegionList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<RegionList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return Region.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        protected RegionDataMap getDataMap() {
            return (RegionDataMap) super.getDataMap();
        }

        @Override
        protected RegionList getEmptyList(final ListStyle pStyle) {
            final RegionList myList = new RegionList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public RegionList deriveEditList() {
            /* Build an empty List */
            final RegionList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the regions */
            final Iterator<Region> myIterator = iterator();
            while (myIterator.hasNext()) {
                final Region myCurr = myIterator.next();

                /* Ignore deleted regions */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked region and add it to the list */
                final Region myRegion = new Region(myList, myCurr);
                myList.add(myRegion);

                /* Adjust the map */
                myRegion.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pRegion item
         * @return the newly added item
         */
        @Override
        public Region addCopyItem(final DataItem<?> pRegion) {
            /* Can only clone a Region */
            if (!(pRegion instanceof Region)) {
                throw new UnsupportedOperationException();
            }

            final Region myRegion = new Region(this, (Region) pRegion);
            add(myRegion);
            return myRegion;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public Region addNewItem() {
            final Region myRegion = new Region(this);
            add(myRegion);
            return myRegion;
        }

        @Override
        public Region findItemByName(final String pName) {
            /* look up the name in the map */
            return getDataMap().findItemByName(pName);
        }

        /**
         * Obtain unique name for new tag.
         * @return The new name
         */
        public String getUniqueName() {
            /* Set up base constraints */
            final String myBase = NAME_NEWREGION;
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
        public Region addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the region */
            final Region myRegion = new Region(this, pValues);

            /* Check that this regionId has not been previously added */
            if (!isIdUnique(myRegion.getId())) {
                myRegion.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myRegion, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myRegion);

            /* Return it */
            return myRegion;
        }

        @Override
        protected RegionDataMap allocateDataMap() {
            return new RegionDataMap();
        }

        @Override
        public void resolveDataSetLinks() throws OceanusException {
            /* We have no links so disable this */
        }
    }

    /**
     * The dataMap class.
     */
    protected static class RegionDataMap
            extends DataInstanceMap<Region, MoneyWiseDataType, String> {
        @Override
        public void adjustForItem(final Region pItem) {
            /* Adjust name count */
            adjustForItem(pItem, pItem.getName());
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public Region findItemByName(final String pName) {
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
