/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisEncryptedData.MetisEncryptedUnits;
import net.sourceforge.joceanus.jmetis.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.StockOption.StockOptionList;
import net.sourceforge.joceanus.jprometheus.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * StockOptionVest class.
 */
public class StockOptionVest
        extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<StockOptionVest> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.STOCKOPTIONVEST.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.STOCKOPTIONVEST.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * Option Field Id.
     */
    public static final MetisField FIELD_OPTION = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.STOCKOPTION.getItemName(), MetisDataType.LINK);

    /**
     * Date Field Id.
     */
    public static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue(), MetisDataType.DATE);

    /**
     * Units Field Id.
     */
    public static final MetisField FIELD_UNITS = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_UNITS.getValue(), MetisDataType.UNITS);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pVest The vest to copy
     */
    protected StockOptionVest(final StockOptionVestList pList,
                              final StockOptionVest pVest) {
        /* Set standard values */
        super(pList, pVest);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private StockOptionVest(final StockOptionVestList pList,
                            final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Security */
            Object myValue = pValues.getValue(FIELD_OPTION);
            if (myValue instanceof Integer) {
                setValueStockOption((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueStockOption((String) myValue);
            }

            /* Store GrantDate */
            myValue = pValues.getValue(FIELD_DATE);
            if (myValue instanceof TethysDate) {
                setValueDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                TethysDateFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDateDay((String) myValue));
            }

            /* Store the Units */
            myValue = pValues.getValue(FIELD_UNITS);
            if (myValue instanceof TethysUnits) {
                setValueUnits((TethysUnits) myValue);
            } else if (myValue instanceof byte[]) {
                setValueUnits((byte[]) myValue);
            } else if (myValue instanceof String) {
                String myString = (String) myValue;
                setValueUnits(myString);
                setValueUnits(myFormatter.parseValue(myString, TethysUnits.class));
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
    public StockOptionVest(final StockOptionVestList pList) {
        super(pList, 0);
        setNextDataKeySet();
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisField pField) {
        /* Determine whether fields should be included */
        if (FIELD_OPTION.equals(pField)) {
            return true;
        }
        if (FIELD_DATE.equals(pField)) {
            return true;
        }
        if (FIELD_UNITS.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public String formatObject() {
        /* Access Key Values */
        MetisEncryptedValueSet myValues = getValueSet();
        Object myOption = myValues.getValue(FIELD_OPTION);
        Object myDate = myValues.getValue(FIELD_DATE);
        Object myUnits = myValues.getValue(FIELD_UNITS);

        /* Access formatter */
        MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myFormatter.formatObject(myOption));
        myBuilder.append(": ");
        myBuilder.append(myFormatter.formatObject(myUnits));
        myBuilder.append('@');
        myBuilder.append(myFormatter.formatObject(myDate));

        /* return it */
        return myBuilder.toString();
    }

    /**
     * Obtain Option.
     * @return the option
     */
    public StockOption getStockOption() {
        return getStockOption(getValueSet());
    }

    /**
     * Obtain StockOptionId.
     * @return the OptionId
     */
    public Integer getStockOptionId() {
        StockOption myOption = getStockOption();
        return (myOption == null)
                                  ? null
                                  : myOption.getId();
    }

    /**
     * Obtain StockOptionName.
     * @return the optionName
     */
    public String getStockOptionName() {
        StockOption myOption = getStockOption();
        return (myOption == null)
                                  ? null
                                  : myOption.getName();
    }

    /**
     * Obtain Date.
     * @return the Date
     */
    public TethysDate getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain Units.
     * @return the units
     */
    public TethysUnits getUnits() {
        return getUnits(getValueSet());
    }

    /**
     * Obtain Encrypted units.
     * @return the bytes
     */
    public byte[] getUnitsBytes() {
        return getUnitsBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Units Field.
     * @return the Field
     */
    private MetisEncryptedUnits getUnitsField() {
        return getUnitsField(getValueSet());
    }

    /**
     * Obtain StockOption.
     * @param pValueSet the valueSet
     * @return the option
     */
    public static StockOption getStockOption(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_OPTION, StockOption.class);
    }

    /**
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static TethysDate getDate(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, TethysDate.class);
    }

    /**
     * Obtain Units.
     * @param pValueSet the valueSet
     * @return the symbol
     */
    public static TethysUnits getUnits(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_UNITS, TethysUnits.class);
    }

    /**
     * Obtain Encrypted units.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getUnitsBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_UNITS);
    }

    /**
     * Obtain Encrypted units field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static MetisEncryptedUnits getUnitsField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_UNITS, MetisEncryptedUnits.class);
    }

    /**
     * Set StockOption value.
     * @param pValue the value
     */
    private void setValueStockOption(final StockOption pValue) {
        getValueSet().setValue(FIELD_OPTION, pValue);
    }

    /**
     * Set StockOption id.
     * @param pValue the value
     */
    private void setValueStockOption(final Integer pValue) {
        getValueSet().setValue(FIELD_OPTION, pValue);
    }

    /**
     * Set StockOption name.
     * @param pValue the value
     */
    private void setValueStockOption(final String pValue) {
        getValueSet().setValue(FIELD_OPTION, pValue);
    }

    /**
     * Set Date value.
     * @param pValue the value
     */
    private void setValueDate(final TethysDate pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
    }

    /**
     * Set units value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueUnits(final TethysUnits pValue) throws OceanusException {
        setEncryptedValue(FIELD_UNITS, pValue);
    }

    /**
     * Set units value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueUnits(final String pValue) throws OceanusException {
        getValueSet().setValue(FIELD_UNITS, pValue);
    }

    /**
     * Set units value.
     * @param pValue the value
     */
    private void setValueUnits(final MetisEncryptedUnits pValue) {
        getValueSet().setValue(FIELD_UNITS, pValue);
    }

    /**
     * Set units value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueUnits(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_UNITS, pBytes, TethysUnits.class);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public StockOptionVestList getList() {
        return (StockOptionVestList) super.getList();
    }

    @Override
    public StockOptionVest getBase() {
        return (StockOptionVest) super.getBase();
    }

    @Override
    public int compareTo(final StockOptionVest pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If header settings differ */
        if (isHeader() != pThat.isHeader()) {
            return isHeader()
                              ? -1
                              : 1;
        }

        /* Compare the dates */
        int iDiff = MetisDifference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            /* Sort in reverse date order !! */
            return -iDiff;
        }

        /* Compare the options */
        iDiff = getStockOption().compareTo(pThat.getStockOption());
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

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_OPTION, myData.getStockOptions());
    }

    /**
     * Resolve links in an updateSet.
     * @param pUpdateSet the update Set
     * @throws OceanusException on error
     */
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* Resolve parent within list */
        StockOptionList myOptions = pUpdateSet.getDataList(MoneyWiseDataType.STOCKOPTION, StockOptionList.class);
        resolveDataLink(FIELD_OPTION, myOptions);
    }

    /**
     * Set a new option.
     * @param pOption the option
     * @throws OceanusException on error
     */
    public void setStockOption(final StockOption pOption) throws OceanusException {
        setValueStockOption(pOption);
    }

    /**
     * Set a new Date.
     * @param pDate the date
     * @throws OceanusException on error
     */
    public void setDate(final TethysDate pDate) throws OceanusException {
        setValueDate(pDate);
    }

    /**
     * Set a new units.
     * @param pUnits the units
     * @throws OceanusException on error
     */
    public void setUnits(final TethysUnits pUnits) throws OceanusException {
        setValueUnits(pUnits);
    }

    @Override
    public void touchUnderlyingItems() {
        /* Touch stockOption */
        getStockOption().touchItem(this);
    }

    @Override
    public void touchOnUpdate() {
        /* Touch stockOption */
        getStockOption().touchItem(this);
    }

    /**
     * Validate the vest.
     */
    @Override
    public void validate() {
        StockOption myOption = getStockOption();
        TethysDate myDate = getDate();
        TethysUnits myUnits = getUnits();
        StockOptionVestList myList = getList();
        MoneyWiseData mySet = getDataSet();

        /* The option must be non-null */
        if (myOption == null) {
            addError(ERROR_MISSING, FIELD_OPTION);
        }

        /* The date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this option */
            StockOptionVestDataMap myMap = myList.getDataMap();
            if (!myMap.validVestCount(this)) {
                addError(ERROR_DUPLICATE, FIELD_DATE);
            }

            /* The date must be in-range */
            if (mySet.getDateRange().compareTo(myDate) != 0) {
                addError(ERROR_RANGE, FIELD_DATE);
            }
        }

        /* The Units must be non-zero and greater than zero */
        if (myUnits == null) {
            addError(ERROR_MISSING, FIELD_UNITS);
        } else if (myUnits.isZero()) {
            addError(ERROR_ZERO, FIELD_UNITS);
        } else if (!myUnits.isPositive()) {
            addError(ERROR_NEGATIVE, FIELD_UNITS);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Update base Vest from an edited Vest.
     * @param pVest the edited Vest
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pVest) {
        /* Can only update from a vest */
        if (!(pVest instanceof StockOptionVest)) {
            return false;
        }
        StockOptionVest myVest = (StockOptionVest) pVest;

        /* Store the current detail into history */
        pushHistory();

        /* Update the option if required */
        if (!MetisDifference.isEqual(getStockOption(), myVest.getStockOption())) {
            setValueStockOption(myVest.getStockOption());
        }

        /* Update the Date if required */
        if (!MetisDifference.isEqual(getDate(), myVest.getDate())) {
            setValueDate(myVest.getDate());
        }

        /* Update the units if required */
        if (!MetisDifference.isEqual(getUnits(), myVest.getUnits())) {
            setValueUnits(myVest.getUnitsField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        StockOptionVestList myList = getList();
        StockOptionVestDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The StockOption Vest List class.
     */
    public static class StockOptionVestList
            extends EncryptedList<StockOptionVest, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, DataList.FIELD_DEFS);

        /**
         * Construct an empty CORE StockOptionVest list.
         * @param pData the DataSet for the list
         */
        public StockOptionVestList(final MoneyWiseData pData) {
            super(StockOptionVest.class, pData, MoneyWiseDataType.STOCKOPTIONVEST);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected StockOptionVestList(final StockOptionVestList pSource) {
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
            return StockOptionVest.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        protected StockOptionVestDataMap getDataMap() {
            return (StockOptionVestDataMap) super.getDataMap();
        }

        @Override
        protected StockOptionVestList getEmptyList(final ListStyle pStyle) {
            StockOptionVestList myList = new StockOptionVestList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public StockOptionVestList deriveEditList(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
            /* Build an empty List */
            StockOptionVestList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the vests */
            Iterator<StockOptionVest> myIterator = iterator();
            while (myIterator.hasNext()) {
                StockOptionVest myCurr = myIterator.next();

                /* Ignore deleted options */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked vest and add it to the list */
                StockOptionVest myVest = new StockOptionVest(myList, myCurr);
                myVest.resolveUpdateSetLinks(pUpdateSet);
                myList.append(myVest);

                /* Adjust the map */
                myVest.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pVest item
         * @return the newly added item
         */
        @Override
        public StockOptionVest addCopyItem(final DataItem<?> pVest) {
            /* Can only clone a Vest */
            if (!(pVest instanceof StockOptionVest)) {
                throw new UnsupportedOperationException();
            }

            StockOptionVest myVest = new StockOptionVest(this, (StockOptionVest) pVest);
            add(myVest);
            return myVest;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public StockOptionVest addNewItem() {
            StockOptionVest myVest = new StockOptionVest(this);
            add(myVest);
            return myVest;
        }

        @Override
        public StockOptionVest addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the vest */
            StockOptionVest myVest = new StockOptionVest(this, pValues);

            /* Check that this VestId has not been previously added */
            if (!isIdUnique(myVest.getId())) {
                myVest.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myVest, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myVest);

            /* Return it */
            return myVest;
        }

        @Override
        protected StockOptionVestDataMap allocateDataMap() {
            return new StockOptionVestDataMap();
        }

        @Override
        public void prepareForAnalysis() {
            /* Just ensure the map */
            ensureMap();
        }
    }

    /**
     * The dataMap class.
     */
    protected static class StockOptionVestDataMap
            implements DataMapItem<StockOptionVest, MoneyWiseDataType>, MetisDataContents {
        /**
         * Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDataResource.MONEYWISEDATA_MAP_MULTIMAP.getValue());

        /**
         * CategoryMap Field Id.
         */
        public static final MetisField FIELD_MAPOFMAPS = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_MAP_MAPOFMAPS.getValue());

        /**
         * Map of Maps.
         */
        private final Map<StockOption, Map<TethysDate, Integer>> theMapOfMaps;

        /**
         * Constructor.
         */
        public StockOptionVestDataMap() {
            /* Create the maps */
            theMapOfMaps = new HashMap<>();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_MAPOFMAPS.equals(pField)) {
                return theMapOfMaps;
            }

            /* Unknown */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public void resetMap() {
            theMapOfMaps.clear();
        }

        @Override
        public void adjustForItem(final StockOptionVest pItem) {
            /* Access the StockOption Id */
            StockOption myOption = pItem.getStockOption();
            if (myOption == null) {
                return;
            }

            /* Access the map */
            Map<TethysDate, Integer> myMap = theMapOfMaps.get(myOption);
            if (myMap == null) {
                myMap = new HashMap<>();
                theMapOfMaps.put(myOption, myMap);
            }

            /* Adjust vest count */
            TethysDate myDate = pItem.getDate();
            Integer myCount = myMap.get(myDate);
            if (myCount == null) {
                myMap.put(myDate, DataInstanceMap.ONE);
            } else {
                myMap.put(myDate, myCount + 1);
            }
        }

        /**
         * Check validity of Vest.
         * @param pItem the vest
         * @return true/false
         */
        public boolean validVestCount(final StockOptionVest pItem) {
            /* Access the Details */
            StockOption myOption = pItem.getStockOption();
            TethysDate myDate = pItem.getDate();

            /* Access the map */
            Map<TethysDate, Integer> myMap = theMapOfMaps.get(myOption);
            if (myMap != null) {
                Integer myResult = myMap.get(myDate);
                return DataInstanceMap.ONE.equals(myResult);
            }
            return false;
        }

        /**
         * Check availability of date for an option.
         * @param pOption the option
         * @param pDate the key to look up
         * @return true/false
         */
        public boolean availableDate(final StockOption pOption,
                                     final TethysDate pDate) {
            /* Access the map */
            Map<TethysDate, Integer> myMap = theMapOfMaps.get(pOption);
            return myMap == null
                   || myMap.get(pDate) == null;
        }
    }
}
