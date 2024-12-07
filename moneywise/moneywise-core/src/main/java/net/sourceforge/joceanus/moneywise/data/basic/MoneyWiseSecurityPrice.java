/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInstanceMap;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataMapItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedFieldSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedPair;
import net.sourceforge.joceanus.prometheus.data.PrometheusEncryptedValues;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateFormatter;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * SecurityPrice data type.
 * @author Tony Washer
 */
public class MoneyWiseSecurityPrice
        extends PrometheusEncryptedDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.SECURITYPRICE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.SECURITYPRICE.getListName();

    /**
     * Report fields.
     */
    private static final PrometheusEncryptedFieldSet<MoneyWiseSecurityPrice> FIELD_DEFS = PrometheusEncryptedFieldSet.newEncryptedFieldSet(MoneyWiseSecurityPrice.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(MoneyWiseBasicDataType.SECURITY);
        FIELD_DEFS.declareDateField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        FIELD_DEFS.declareEncryptedPriceField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
    }

    /**
     * Invalid currency error.
     */
    private static final String ERROR_CURRENCY = MoneyWiseBasicResource.MONEYWISEDATA_ERROR_CURRENCY.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPrice The Price
     */
    protected MoneyWiseSecurityPrice(final MoneyWiseSecurityPriceBaseList<? extends MoneyWiseSecurityPrice> pList,
                                     final MoneyWiseSecurityPrice pPrice) {
        /* Set standard values */
        super(pList, pPrice);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public MoneyWiseSecurityPrice(final MoneyWiseSecurityPriceBaseList<? extends MoneyWiseSecurityPrice> pList) {
        super(pList, 0);
        setNextDataKeySet();
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseSecurityPrice(final MoneyWiseSecurityPriceList pList,
                                   final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            Object myValue = pValues.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            if (myValue instanceof OceanusDate) {
                setValueDate((OceanusDate) myValue);
            } else if (myValue instanceof String) {
                final OceanusDateFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDate((String) myValue));
            }

            /* Store the Security */
            myValue = pValues.getValue(MoneyWiseBasicDataType.SECURITY);
            if (myValue instanceof Integer) {
                setValueSecurity((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueSecurity((String) myValue);
            }

            /* Store the Price */
            myValue = pValues.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
            if (myValue instanceof OceanusPrice) {
                setValuePrice((OceanusPrice) myValue);
            } else if (myValue instanceof byte[]) {
                setValuePrice((byte[]) myValue);
            } else if (myValue instanceof String) {
                final String myString = (String) myValue;
                setValuePrice(myString);
                setValuePrice(myFormatter.parseValue(myString, OceanusPrice.class));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException
                 | OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseBasicDataType.SECURITY.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public String toString() {
        /* Access Key Values */
        final PrometheusEncryptedValues myValues = getValues();
        final Object mySecurity = myValues.getValue(MoneyWiseBasicDataType.SECURITY);
        final Object myDate = myValues.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        final Object myPrice = myValues.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE, OceanusPrice.class);

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myFormatter.formatObject(mySecurity));
        myBuilder.append(": ");
        myBuilder.append(myFormatter.formatObject(myPrice));
        myBuilder.append('@');
        myBuilder.append(myFormatter.formatObject(myDate));

        /* return it */
        return myBuilder.toString();
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    /**
     * Obtain Price.
     * @return the price
     */
    public OceanusPrice getPrice() {
        return getValues().getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE, OceanusPrice.class);
    }

    /**
     * Obtain Encrypted Price.
     * @return the Bytes
     */
    public byte[] getPriceBytes() {
        return getValues().getEncryptedBytes(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
    }

    /**
     * Obtain Encrypted Price Field.
     * @return the field
     */
    public PrometheusEncryptedPair getPriceField() {
        return getValues().getEncryptedPair(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
    }

    /**
     * Obtain Date.
     * @return the date
     */
    public OceanusDate getDate() {
        return getValues().getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, OceanusDate.class);
    }

    /**
     * Obtain Security.
     * @return the security
     */
    public MoneyWiseSecurity getSecurity() {
        return getValues().getValue(MoneyWiseBasicDataType.SECURITY, MoneyWiseSecurity.class);
    }

    /**
     * Obtain SecurityId.
     * @return the securityId
     */
    public Integer getSecurityId() {
        final MoneyWiseSecurity mySecurity = getSecurity();
        return mySecurity == null
                ? null
                : mySecurity.getIndexedId();
    }

    /**
     * Obtain SecurityName.
     * @return the securityName
     */
    public String getSecurityName() {
        final MoneyWiseSecurity mySecurity = getSecurity();
        return mySecurity == null
                ? null
                : mySecurity.getName();
    }

    /**
     * Set the security.
     * @param pValue the security
     */
    private void setValueSecurity(final MoneyWiseSecurity pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicDataType.SECURITY, pValue);
    }

    /**
     * Set the security id.
     * @param pId the security id
     */
    private void setValueSecurity(final Integer pId) {
        getValues().setUncheckedValue(MoneyWiseBasicDataType.SECURITY, pId);
    }

    /**
     * Set the security name.
     * @param pName the security name
     */
    private void setValueSecurity(final String pName) {
        getValues().setUncheckedValue(MoneyWiseBasicDataType.SECURITY, pName);
    }

    /**
     * Set the price.
     * @param pValue the price
     * @throws OceanusException on error
     */
    private void setValuePrice(final OceanusPrice pValue) throws OceanusException {
        setEncryptedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE, pValue);
    }

    /**
     * Set the encrypted price.
     * @param pBytes the encrypted price
     * @throws OceanusException on error
     */
    private void setValuePrice(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE, pBytes, OceanusPrice.class);
    }

    /**
     * Set the price.
     * @param pValue the price
     */
    private void setValuePrice(final String pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE, pValue);
    }

    /**
     * Set the price.
     * @param pValue the price
     */
    public void setValuePrice(final PrometheusEncryptedPair pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE, pValue);
    }

    /**
     * Set the date.
     * @param pValue the date
     */
    private void setValueDate(final OceanusDate pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, pValue);
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public MoneyWiseSecurityPriceBaseList<? extends MoneyWiseSecurityPrice> getList() {
        return (MoneyWiseSecurityPriceBaseList<? extends MoneyWiseSecurityPrice>) super.getList();
    }

    @Override
    public MoneyWiseSecurityPrice getBase() {
        return (MoneyWiseSecurityPrice) super.getBase();
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Access as SecurityPrice */
        final MoneyWiseSecurityPrice myThat = (MoneyWiseSecurityPrice) pThat;

        /* If header settings differ */
        if (isHeader() != pThat.isHeader()) {
            return isHeader()
                    ? -1
                    : 1;
        }

        /* If the date differs */
        final int iDiff = MetisDataDifference.compareObject(getDate(), myThat.getDate());
        if (iDiff != 0) {
            /* Sort in reverse date order !! */
            return -iDiff;
        }

        /* Compare the securities */
        return getSecurity().compareTo(myThat.getSecurity());
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(MoneyWiseBasicDataType.SECURITY, myData.getSecurities());
    }

    /**
     * Resolve links in an editSet.
     * @param pEditSet the edit Set
     * @throws OceanusException on error
     */
    protected void resolveEditSetLinks(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Resolve parent within list */
        final MoneyWiseSecurityList mySecurities = pEditSet.getDataList(MoneyWiseBasicDataType.SECURITY, MoneyWiseSecurityList.class);
        resolveDataLink(MoneyWiseBasicDataType.SECURITY, mySecurities);
    }

    /**
     * Validate the price.
     */
    @Override
    public void validate() {
        final MoneyWiseSecurity mySecurity = getSecurity();
        final OceanusDate myDate = getDate();
        final OceanusPrice myPrice = getPrice();
        final MoneyWiseSecurityPriceBaseList<? extends MoneyWiseSecurityPrice> myList = getList();
        final MoneyWiseDataSet mySet = getDataSet();

        /* The security must be non-null */
        if (mySecurity == null) {
            addError(ERROR_MISSING, MoneyWiseBasicDataType.SECURITY);

            /* The security must not be an option */
        } else if (mySecurity.getCategoryClass().isOption()) {
            addError("Options are priced by the underlying stock", MoneyWiseBasicDataType.SECURITY);
        }

        /* The date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this security */
            final MoneyWiseSecurityPriceDataMap myMap = myList.getDataMap();
            if (!myMap.validPriceCount(this)) {
                addError(ERROR_DUPLICATE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            }

            /* The date must be in-range */
            if (mySet.getDateRange().compareToDate(myDate) != 0) {
                addError(ERROR_RANGE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            }
        }

        /* The Price must be non-zero and greater than zero */
        if (myPrice == null) {
            addError(ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
        } else if (myPrice.isZero()) {
            addError(ERROR_ZERO, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
        } else if (!myPrice.isPositive()) {
            addError(ERROR_NEGATIVE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
        } else {
            /* Ensure that currency is correct */
            final MoneyWiseCurrency myCurrency = mySecurity == null
                    ? null
                    : mySecurity.getAssetCurrency();
            if ((myCurrency != null)
                    && !myPrice.getCurrency().equals(myCurrency.getCurrency())) {
                addError(ERROR_CURRENCY, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
            }
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Set the security.
     * @param pValue the security
     */
    public void setSecurity(final MoneyWiseSecurity pValue) {
        setValueSecurity(pValue);
    }

    /**
     * Set a new price.
     * @param pPrice the price
     * @throws OceanusException on error
     */
    public void setPrice(final OceanusPrice pPrice) throws OceanusException {
        setValuePrice(pPrice);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final OceanusDate pDate) {
        setValueDate(pDate);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the underlying security */
        getSecurity().touchItem(this);
    }

    @Override
    public void touchOnUpdate() {
        /* Touch security */
        getSecurity().touchItem(this);
    }

    @Override
    public boolean applyChanges(final PrometheusDataItem pPrice) {
        /* Can only update from a SecurityPrice */
        if (!(pPrice instanceof MoneyWiseSecurityPrice)) {
            return false;
        }
        final MoneyWiseSecurityPrice myPrice = (MoneyWiseSecurityPrice) pPrice;

        /* Store the current detail into history */
        pushHistory();

        /* Update the price if required */
        if (!MetisDataDifference.isEqual(getPrice(), myPrice.getPrice())) {
            setValuePrice(myPrice.getPriceField());
        }

        /* Update the date if required */
        if (!MetisDataDifference.isEqual(getDate(), myPrice.getDate())) {
            setValueDate(myPrice.getDate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final MoneyWiseSecurityPriceBaseList<? extends MoneyWiseSecurityPrice> myList = getList();
        final MoneyWiseSecurityPriceDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * Price List.
     * @param <T> the data type
     */
    public abstract static class MoneyWiseSecurityPriceBaseList<T extends MoneyWiseSecurityPrice>
            extends PrometheusEncryptedList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(MoneyWiseSecurityPriceBaseList.class);
        }

        /**
         * Construct an empty CORE Price list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected MoneyWiseSecurityPriceBaseList(final MoneyWiseDataSet pData,
                                                 final Class<T> pClass,
                                                 final MoneyWiseBasicDataType pItemType) {
            /* Call super-constructor */
            super(pClass, pData, pItemType, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseSecurityPriceBaseList(final MoneyWiseSecurityPriceBaseList<T> pSource) {
            /* Call super-constructor */
            super(pSource);
        }

        @Override
        protected MoneyWiseSecurityPriceDataMap getDataMap() {
            return (MoneyWiseSecurityPriceDataMap) super.getDataMap();
        }

        @Override
        protected MoneyWiseSecurityPriceDataMap allocateDataMap() {
            return new MoneyWiseSecurityPriceDataMap();
        }
    }

    /**
     * Price List.
     */
    public static class MoneyWiseSecurityPriceList
            extends MoneyWiseSecurityPriceBaseList<MoneyWiseSecurityPrice> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseSecurityPriceList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityPriceList.class);

        /**
         * Construct an empty CORE price list.
         * @param pData the DataSet for the list
         */
        protected MoneyWiseSecurityPriceList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWiseSecurityPrice.class, MoneyWiseBasicDataType.SECURITYPRICE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseSecurityPriceList(final MoneyWiseSecurityPriceList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseSecurityPriceList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseSecurityPrice.FIELD_DEFS;
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        @Override
        protected MoneyWiseSecurityPriceList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseSecurityPriceList myList = new MoneyWiseSecurityPriceList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Construct an edit extract of a Rate list.
         * @param pEditSet the editSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public MoneyWiseSecurityPriceList deriveEditList(final PrometheusEditSet pEditSet) throws OceanusException {
            /* Build an empty List */
            final MoneyWiseSecurityPriceList myList = getEmptyList(PrometheusListStyle.EDIT);
            myList.ensureMap();
            pEditSet.setEditEntryList(MoneyWiseBasicDataType.SECURITYPRICE, myList);

            /* Loop through the list */
            final Iterator<MoneyWiseSecurityPrice> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseSecurityPrice myCurr = myIterator.next();

                /* Copy the item */
                final MoneyWiseSecurityPrice myItem = new MoneyWiseSecurityPrice(myList, myCurr);
                myItem.resolveEditSetLinks(pEditSet);
                myList.add(myItem);

                /* Adjust the map */
                myItem.adjustMapForItem();
            }

            /* Return the List */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pPrice item
         * @return the newly added item
         */
        @Override
        public MoneyWiseSecurityPrice addCopyItem(final PrometheusDataItem pPrice) {
            if (pPrice instanceof MoneyWiseSecurityPrice) {
                final MoneyWiseSecurityPrice myPrice = new MoneyWiseSecurityPrice(this, (MoneyWiseSecurityPrice) pPrice);
                add(myPrice);
                return myPrice;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public MoneyWiseSecurityPrice addNewItem() {
            final MoneyWiseSecurityPrice myPrice = new MoneyWiseSecurityPrice(this);
            add(myPrice);
            return myPrice;
        }

        @Override
        public MoneyWiseSecurityPrice addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the price */
            final MoneyWiseSecurityPrice myPrice = new MoneyWiseSecurityPrice(this, pValues);

            /* Check that this PriceId has not been previously added */
            if (!isIdUnique(myPrice.getIndexedId())) {
                myPrice.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myPrice, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myPrice);

            /* Return it */
            return myPrice;
        }
    }

    /**
     * The dataMap class.
     */
    public static class MoneyWiseSecurityPriceDataMap
            implements PrometheusDataMapItem, MetisFieldItem {
        /**
         * Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<MoneyWiseSecurityPriceDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityPriceDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_MAP_MAPOFMAPS, MoneyWiseSecurityPriceDataMap::getMapOfMaps);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.SECURITYPRICE_MAP_MAPOFPRICES, MoneyWiseSecurityPriceDataMap::getMapOfPrices);
        }

        /**
         * Map of Maps.
         */
        private final Map<MoneyWiseSecurity, Map<OceanusDate, Integer>> theMapOfMaps;

        /**
         * Map of Prices.
         */
        private final Map<MoneyWiseSecurity, MoneyWiseSecurityPriceList> theMapOfPrices;

        /**
         * Constructor.
         */
        public MoneyWiseSecurityPriceDataMap() {
            /* Create the maps */
            theMapOfMaps = new HashMap<>();
            theMapOfPrices = new HashMap<>();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public MetisFieldSet<MoneyWiseSecurityPriceDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain mapOfMaps.
         * @return the map
         */
        private Map<MoneyWiseSecurity, Map<OceanusDate, Integer>> getMapOfMaps() {
            return theMapOfMaps;
        }

        /**
         * Obtain mapOfPrices.
         * @return the map
         */
        private Map<MoneyWiseSecurity, MoneyWiseSecurityPriceList> getMapOfPrices() {
            return theMapOfPrices;
        }

        @Override
        public void resetMap() {
            theMapOfMaps.clear();
            theMapOfPrices.clear();
        }

        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Access the Security Id */
            final MoneyWiseSecurityPrice myItem = (MoneyWiseSecurityPrice) pItem;
            final MoneyWiseSecurity mySecurity = myItem.getSecurity();
            if (mySecurity == null) {
                return;
            }

            /* Access the map */
            final Map<OceanusDate, Integer> myMap = theMapOfMaps.computeIfAbsent(mySecurity, s -> new HashMap<>());

            /* Adjust price count */
            final OceanusDate myDate = myItem.getDate();
            final Integer myCount = myMap.get(myDate);
            if (myCount == null) {
                myMap.put(myDate, PrometheusDataInstanceMap.ONE);
            } else {
                myMap.put(myDate, myCount + 1);
            }

            /* Access the list */
            final MoneyWiseSecurityPriceList myList = theMapOfPrices.computeIfAbsent(mySecurity, MoneyWiseSecurityPriceList::new);

            /* Add element to the list */
            myList.add(myItem);
        }

        /**
         * Check validity of Price.
         * @param pItem the price
         * @return true/false
         */
        public boolean validPriceCount(final MoneyWiseSecurityPrice pItem) {
            /* Access the Details */
            final MoneyWiseSecurity mySecurity = pItem.getSecurity();
            final OceanusDate myDate = pItem.getDate();

            /* Access the map */
            final Map<OceanusDate, Integer> myMap = theMapOfMaps.get(mySecurity);
            if (myMap != null) {
                final Integer myResult = myMap.get(myDate);
                return PrometheusDataInstanceMap.ONE.equals(myResult);
            }
            return false;
        }

        /**
         * Check availability of date for a security.
         * @param pSecurity the security
         * @param pDate the key to look up
         * @return true/false
         */
        public boolean availableDate(final MoneyWiseSecurity pSecurity,
                                     final OceanusDate pDate) {
            /* Access the map */
            final Map<OceanusDate, Integer> myMap = theMapOfMaps.get(pSecurity);
            return myMap == null
                    || myMap.get(pDate) == null;
        }

        /**
         * Obtain price for date.
         * @param pSecurity the security
         * @param pDate the date
         * @return the latest price for the date.
         */
        public OceanusPrice getPriceForDate(final MoneyWiseAssetBase pSecurity,
                                            final OceanusDate pDate) {
            /* Access as security */
            final MoneyWiseSecurity mySecurity = MoneyWiseSecurity.class.cast(pSecurity);

            /* Access list for security */
            final MoneyWiseSecurityPriceList myList = theMapOfPrices.get(mySecurity);
            if (myList != null) {
                /* Loop through the prices */
                final Iterator<MoneyWiseSecurityPrice> myIterator = myList.iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseSecurityPrice myCurr = myIterator.next();

                    /* Return this price if this is earlier or equal to the the date */
                    if (pDate.compareTo(myCurr.getDate()) >= 0) {
                        return myCurr.getPrice();
                    }
                }
            }

            /* return single unit price */
            final Currency myCurrency = mySecurity.getCurrency();
            return OceanusPrice.getWholeUnits(PrometheusDataInstanceMap.ONE, myCurrency);
        }

        /**
         * Obtain prices for range.
         * @param pSecurity the security
         * @param pRange the date range
         * @return the two deep array of prices for the range.
         */
        public OceanusPrice[] getPricesForRange(final MoneyWiseSecurity pSecurity,
                                                final OceanusDateRange pRange) {
            /* Set price */
            final Currency myCurrency = pSecurity.getCurrency();
            OceanusPrice myFirst = OceanusPrice.getWholeUnits(PrometheusDataInstanceMap.ONE, myCurrency);
            OceanusPrice myLatest = myFirst;
            final OceanusDate myStart = pRange.getStart();

            /* Access list for security */
            final MoneyWiseSecurityPriceList myList = theMapOfPrices.get(pSecurity);
            if (myList != null) {
                /* Loop through the prices */
                final ListIterator<MoneyWiseSecurityPrice> myIterator = myList.listIterator(myList.size());
                while (myIterator.hasPrevious()) {
                    final MoneyWiseSecurityPrice myCurr = myIterator.previous();

                    /* Check for the range of the date */
                    final OceanusDate myDate = myCurr.getDate();
                    final int iComp = pRange.compareToDate(myDate);

                    /* If this is later than the range we are finished */
                    if (iComp < 0) {
                        break;
                    }

                    /* Record as best price */
                    myLatest = myCurr.getPrice();

                    /* Record early price */
                    if (iComp > 0
                            || myDate.compareTo(myStart) == 0) {
                        myFirst = myLatest;
                    }
                }
            }

            /* Return the prices */
            return new OceanusPrice[]
                    { myFirst, myLatest };
        }

        /**
         * Obtain priceList cursor.
         * @param pSecurity the security
         * @return the latest price for the date.
         */
        public ListIterator<MoneyWiseSecurityPrice> priceIterator(final MoneyWiseSecurity pSecurity) {
            /* Access list for currency */
            final MoneyWiseSecurityPriceList myList = theMapOfPrices.get(pSecurity);
            return myList != null
                    ? myList.listIterator(myList.size())
                    : null;
        }

        /**
         * Price List class.
         */
        private static final class MoneyWiseSecurityPriceList
                implements MetisFieldItem, MetisDataList<MoneyWiseSecurityPrice> {
            /**
             * Report fields.
             */
            private static final MetisFieldSet<MoneyWiseSecurityPriceList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityPriceList.class);

            /*
             * UnderlyingMap Field Id.
             */
            static {
                FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE, MoneyWiseSecurityPriceList::size);
            }

            /**
             * The list.
             */
            private final List<MoneyWiseSecurityPrice> theList;

            /**
             * The security.
             */
            private final MoneyWiseSecurity theSecurity;

            /**
             * Constructor.
             * @param pSecurity the security
             */
            private MoneyWiseSecurityPriceList(final MoneyWiseSecurity pSecurity) {
                theSecurity = pSecurity;
                theList = new ArrayList<>();
            }

            @Override
            public MetisFieldSet<MoneyWiseSecurityPriceList> getDataFieldSet() {
                return FIELD_DEFS;
            }

            @Override
            public String formatObject(final TethysUIDataFormatter pFormatter) {
                return theSecurity.formatObject(pFormatter)
                        + "("
                        + size()
                        + ")";
            }

            @Override
            public String toString() {
                return theSecurity.toString()
                        + "("
                        + size()
                        + ")";
            }

            @Override
            public List<MoneyWiseSecurityPrice> getUnderlyingList() {
                return theList;
            }
        }
    }
}
