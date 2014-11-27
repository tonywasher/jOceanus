/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedUnits;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.StockOption.StockOptionList;
import net.sourceforge.joceanus.jprometheus.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

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
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * Option Field Id.
     */
    public static final JDataField FIELD_OPTION = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.STOCKOPTION.getItemName());

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

    /**
     * Units Field Id.
     */
    public static final JDataField FIELD_UNITS = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_UNITS.getValue());

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
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
        EncryptedValueSet myValues = getValueSet();
        Object myOption = myValues.getValue(FIELD_OPTION);
        Object myDate = myValues.getValue(FIELD_DATE);
        Object myUnits = myValues.getValue(FIELD_UNITS);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

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
    public JDateDay getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain Units.
     * @return the units
     */
    public JUnits getUnits() {
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
    private EncryptedUnits getUnitsField() {
        return getUnitsField(getValueSet());
    }

    /**
     * Obtain StockOption.
     * @param pValueSet the valueSet
     * @return the option
     */
    public static StockOption getStockOption(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_OPTION, StockOption.class);
    }

    /**
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static JDateDay getDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, JDateDay.class);
    }

    /**
     * Obtain Units.
     * @param pValueSet the valueSet
     * @return the symbol
     */
    public static JUnits getUnits(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_UNITS, JUnits.class);
    }

    /**
     * Obtain Encrypted units.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getUnitsBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_UNITS);
    }

    /**
     * Obtain Encrypted units field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedUnits getUnitsField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_UNITS, EncryptedUnits.class);
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
    private void setValueDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
    }

    /**
     * Set units value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValueUnits(final JUnits pValue) throws JOceanusException {
        setEncryptedValue(FIELD_UNITS, pValue);
    }

    /**
     * Set units value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValueUnits(final String pValue) throws JOceanusException {
        getValueSet().setValue(FIELD_UNITS, pValue);
    }

    /**
     * Set units value.
     * @param pValue the value
     */
    private void setValueUnits(final EncryptedUnits pValue) {
        getValueSet().setValue(FIELD_UNITS, pValue);
    }

    /**
     * Set units value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueUnits(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_UNITS, pBytes, JUnits.class);
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
     * @throws JOceanusException on error
     */
    private StockOptionVest(final StockOptionVestList pList,
                            final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

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
            if (myValue instanceof JDateDay) {
                setValueDate((JDateDay) myValue);
            } else if (myValue instanceof String) {
                JDateDayFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDateDay((String) myValue));
            }

            /* Store the Units */
            myValue = pValues.getValue(FIELD_UNITS);
            if (myValue instanceof JUnits) {
                setValueUnits((JUnits) myValue);
            } else if (myValue instanceof byte[]) {
                setValueUnits((byte[]) myValue);
            } else if (myValue instanceof String) {
                String myString = (String) myValue;
                setValueUnits(myString);
                setValueUnits(myFormatter.parseValue(myString, JUnits.class));
            }

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
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
        int iDiff = Difference.compareObject(getDate(), pThat.getDate());
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
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_OPTION, myData.getStockOptions());
    }

    /**
     * Resolve links in an updateSet.
     * @param pUpdateSet the update Set
     * @throws JOceanusException on error
     */
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* Resolve parent within list */
        StockOptionList myOptions = pUpdateSet.getDataList(MoneyWiseDataType.STOCKOPTION, StockOptionList.class);
        resolveDataLink(FIELD_OPTION, myOptions);
    }

    /**
     * Set a new option.
     * @param pOption the option
     * @throws JOceanusException on error
     */
    public void setStockOption(final StockOption pOption) throws JOceanusException {
        setValueStockOption(pOption);
    }

    /**
     * Set a new Date.
     * @param pDate the date
     * @throws JOceanusException on error
     */
    public void setDate(final JDateDay pDate) throws JOceanusException {
        setValueDate(pDate);
    }

    /**
     * Set a new units.
     * @param pUnits the units
     * @throws JOceanusException on error
     */
    public void setUnits(final JUnits pUnits) throws JOceanusException {
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
        JDateDay myDate = getDate();
        JUnits myUnits = getUnits();
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
        if (!Difference.isEqual(getStockOption(), myVest.getStockOption())) {
            setValueStockOption(myVest.getStockOption());
        }

        /* Update the Date if required */
        if (!Difference.isEqual(getDate(), myVest.getDate())) {
            setValueDate(myVest.getDate());
        }

        /* Update the units if required */
        if (!Difference.isEqual(getUnits(), myVest.getUnits())) {
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
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
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

        /**
         * Construct an empty CORE StockOptionVest list.
         * @param pData the DataSet for the list
         */
        public StockOptionVestList(final MoneyWiseData pData) {
            super(StockOptionVest.class, pData, MoneyWiseDataType.STOCKOPTIONVEST);
        }

        @Override
        protected StockOptionVestList getEmptyList(final ListStyle pStyle) {
            StockOptionVestList myList = new StockOptionVestList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected StockOptionVestList(final StockOptionVestList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws JOceanusException on error
         */
        public StockOptionVestList deriveEditList(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
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
        public StockOptionVest addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the vest */
            StockOptionVest myVest = new StockOptionVest(this, pValues);

            /* Check that this VestId has not been previously added */
            if (!isIdUnique(myVest.getId())) {
                myVest.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myVest, ERROR_VALIDATION);
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
            implements DataMapItem<StockOptionVest, MoneyWiseDataType>, JDataContents {
        /**
         * Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(MoneyWiseDataResource.MONEYWISEDATA_MAP_MULTIMAP.getValue());

        /**
         * CategoryMap Field Id.
         */
        public static final JDataField FIELD_MAPOFMAPS = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_MAP_MAPOFMAPS.getValue());

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_MAPOFMAPS.equals(pField)) {
                return theMapOfMaps;
            }

            /* Unknown */
            return JDataFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        /**
         * Map of Maps.
         */
        private final Map<StockOption, Map<JDateDay, Integer>> theMapOfMaps;

        /**
         * Constructor.
         */
        public StockOptionVestDataMap() {
            /* Create the maps */
            theMapOfMaps = new HashMap<StockOption, Map<JDateDay, Integer>>();
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
            Map<JDateDay, Integer> myMap = theMapOfMaps.get(myOption);
            if (myMap == null) {
                myMap = new HashMap<JDateDay, Integer>();
                theMapOfMaps.put(myOption, myMap);
            }

            /* Adjust vest count */
            JDateDay myDate = pItem.getDate();
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
            JDateDay myDate = pItem.getDate();

            /* Access the map */
            Map<JDateDay, Integer> myMap = theMapOfMaps.get(myOption);
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
                                     final JDateDay pDate) {
            /* Access the map */
            Map<JDateDay, Integer> myMap = theMapOfMaps.get(pOption);
            return myMap != null
                                ? myMap.get(pDate) == null
                                : true;
        }
    }
}
