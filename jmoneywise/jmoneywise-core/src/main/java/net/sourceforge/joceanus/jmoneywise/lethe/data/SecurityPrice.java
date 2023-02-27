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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedPrice;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * SecurityPrice data type.
 * @author Tony Washer
 */
public class SecurityPrice
        extends EncryptedItem
        implements Comparable<SecurityPrice> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.SECURITYPRICE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.SECURITYPRICE.getListName();

    /**
     * Local Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    /**
     * Security Field Id.
     */
    public static final MetisLetheField FIELD_SECURITY = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataType.SECURITY.getItemName(), MetisDataType.LINK);

    /**
     * Date Field Id.
     */
    public static final MetisLetheField FIELD_DATE = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue(), MetisDataType.DATE);

    /**
     * Price Field Id.
     */
    public static final MetisLetheField FIELD_PRICE = FIELD_DEFS.declareEqualityEncryptedField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_PRICE.getValue(), MetisDataType.PRICE);

    /**
     * Invalid currency error.
     */
    private static final String ERROR_CURRENCY = MoneyWiseDataResource.MONEYWISEDATA_ERROR_CURRENCY.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPrice The Price
     */
    protected SecurityPrice(final SecurityPriceBaseList<? extends SecurityPrice> pList,
                            final SecurityPrice pPrice) {
        /* Set standard values */
        super(pList, pPrice);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public SecurityPrice(final SecurityPriceBaseList<? extends SecurityPrice> pList) {
        super(pList, 0);
        setNextDataKeySet();
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private SecurityPrice(final SecurityPriceList pList,
                          final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            Object myValue = pValues.getValue(FIELD_DATE);
            if (myValue instanceof TethysDate) {
                setValueDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                final TethysDateFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDate((String) myValue));
            }

            /* Store the Security */
            myValue = pValues.getValue(FIELD_SECURITY);
            if (myValue instanceof Integer) {
                setValueSecurity((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueSecurity((String) myValue);
            }

            /* Store the Price */
            myValue = pValues.getValue(FIELD_PRICE);
            if (myValue instanceof TethysPrice) {
                setValuePrice((TethysPrice) myValue);
            } else if (myValue instanceof byte[]) {
                setValuePrice((byte[]) myValue);
            } else if (myValue instanceof String) {
                final String myString = (String) myValue;
                setValuePrice(myString);
                setValuePrice(myFormatter.parseValue(myString, TethysPrice.class));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException
                | OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisLetheField pField) {
        /* Determine whether fields should be included */
        if (FIELD_SECURITY.equals(pField)) {
            return true;
        }
        if (FIELD_DATE.equals(pField)) {
            return true;
        }
        if (FIELD_PRICE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public String toString() {
        /* Access Key Values */
        final MetisEncryptedValueSet myValues = getValueSet();
        final Object mySecurity = myValues.getValue(FIELD_SECURITY);
        final Object myDate = myValues.getValue(FIELD_DATE);
        final Object myPrice = myValues.getValue(FIELD_PRICE);

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
    public TethysPrice getPrice() {
        return getPrice(getValueSet());
    }

    /**
     * Obtain Encrypted Price.
     * @return the Bytes
     */
    public byte[] getPriceBytes() {
        return getPriceBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Price Field.
     * @return the field
     */
    public MetisEncryptedPrice getPriceField() {
        return getPriceField(getValueSet());
    }

    /**
     * Obtain Date.
     * @return the date
     */
    public TethysDate getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain Security.
     * @return the security
     */
    public Security getSecurity() {
        return getSecurity(getValueSet());
    }

    /**
     * Obtain SecurityId.
     * @return the securityId
     */
    public Integer getSecurityId() {
        final Security mySecurity = getSecurity();
        return (mySecurity == null)
                                    ? null
                                    : mySecurity.getId();
    }

    /**
     * Obtain SecurityName.
     * @return the securityName
     */
    public String getSecurityName() {
        final Security mySecurity = getSecurity();
        return (mySecurity == null)
                                    ? null
                                    : mySecurity.getName();
    }

    /**
     * Obtain Security.
     * @param pValueSet the valueSet
     * @return the Security
     */
    public static Security getSecurity(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_SECURITY, Security.class);
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
     * Obtain Price.
     * @param pValueSet the valueSet
     * @return the Price
     */
    public static TethysPrice getPrice(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_PRICE, TethysPrice.class);
    }

    /**
     * Obtain Encrypted Price.
     * @param pValueSet the valueSet
     * @return the Price
     */
    public static byte[] getPriceBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_PRICE);
    }

    /**
     * Obtain Price Field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static MetisEncryptedPrice getPriceField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PRICE, MetisEncryptedPrice.class);
    }

    /**
     * Set the security.
     * @param pValue the security
     */
    private void setValueSecurity(final Security pValue) {
        getValueSet().setValue(FIELD_SECURITY, pValue);
    }

    /**
     * Set the security id.
     * @param pId the security id
     */
    private void setValueSecurity(final Integer pId) {
        getValueSet().setValue(FIELD_SECURITY, pId);
    }

    /**
     * Set the security name.
     * @param pName the security name
     */
    private void setValueSecurity(final String pName) {
        getValueSet().setValue(FIELD_SECURITY, pName);
    }

    /**
     * Set the price.
     * @param pValue the price
     * @throws OceanusException on error
     */
    private void setValuePrice(final TethysPrice pValue) throws OceanusException {
        setEncryptedValue(FIELD_PRICE, pValue);
    }

    /**
     * Set the encrypted price.
     * @param pBytes the encrypted price
     * @throws OceanusException on error
     */
    private void setValuePrice(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_PRICE, pBytes, TethysPrice.class);
    }

    /**
     * Set the price.
     * @param pValue the price
     */
    private void setValuePrice(final String pValue) {
        getValueSet().setValue(FIELD_PRICE, pValue);
    }

    /**
     * Set the price.
     * @param pValue the price
     */
    public void setValuePrice(final MetisEncryptedPrice pValue) {
        getValueSet().setValue(FIELD_PRICE, pValue);
    }

    /**
     * Set the date.
     * @param pValue the date
     */
    private void setValueDate(final TethysDate pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SecurityPriceBaseList<? extends SecurityPrice> getList() {
        return (SecurityPriceBaseList<? extends SecurityPrice>) super.getList();
    }

    @Override
    public SecurityPrice getBase() {
        return (SecurityPrice) super.getBase();
    }

    @Override
    public int compareTo(final SecurityPrice pThat) {
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
        int iDiff = MetisDataDifference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            /* Sort in reverse date order !! */
            return -iDiff;
        }

        /* Compare the securities */
        iDiff = getSecurity().compareTo(pThat.getSecurity());
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
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_SECURITY, myData.getSecurities());
    }

    /**
     * Resolve links in an updateSet.
     * @param pUpdateSet the update Set
     * @throws OceanusException on error
     */
    protected void resolveUpdateSetLinks(final UpdateSet pUpdateSet) throws OceanusException {
        /* Resolve parent within list */
        final SecurityList mySecurities = pUpdateSet.getDataList(MoneyWiseDataType.SECURITY, SecurityList.class);
        resolveDataLink(FIELD_SECURITY, mySecurities);
    }

    /**
     * Validate the price.
     */
    @Override
    public void validate() {
        final Security mySecurity = getSecurity();
        final TethysDate myDate = getDate();
        final TethysPrice myPrice = getPrice();
        final SecurityPriceBaseList<? extends SecurityPrice> myList = getList();
        final MoneyWiseData mySet = getDataSet();

        /* The security must be non-null */
        if (mySecurity == null) {
            addError(ERROR_MISSING, FIELD_SECURITY);

            /* The security must not be an option */
        } else if (mySecurity.getCategoryClass().isOption()) {
            addError("Options are priced by the underlying stock", FIELD_SECURITY);
        }

        /* The date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this security */
            final SecurityPriceDataMap<? extends SecurityPrice> myMap = myList.getDataMap();
            if (!myMap.validPriceCount(this)) {
                addError(ERROR_DUPLICATE, FIELD_DATE);
            }

            /* The date must be in-range */
            if (mySet.getDateRange().compareToDate(myDate) != 0) {
                addError(ERROR_RANGE, FIELD_DATE);
            }
        }

        /* The Price must be non-zero and greater than zero */
        if (myPrice == null) {
            addError(ERROR_MISSING, FIELD_PRICE);
        } else if (myPrice.isZero()) {
            addError(ERROR_ZERO, FIELD_PRICE);
        } else if (!myPrice.isPositive()) {
            addError(ERROR_NEGATIVE, FIELD_PRICE);
        } else {
            /* Ensure that currency is correct */
            final AssetCurrency myCurrency = mySecurity == null
                                                                ? null
                                                                : mySecurity.getAssetCurrency();
            if ((myCurrency != null)
                && !myPrice.getCurrency().equals(myCurrency.getCurrency())) {
                addError(ERROR_CURRENCY, FIELD_PRICE);
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
    public void setSecurity(final Security pValue) {
        setValueSecurity(pValue);
    }

    /**
     * Set a new price.
     * @param pPrice the price
     * @throws OceanusException on error
     */
    public void setPrice(final TethysPrice pPrice) throws OceanusException {
        setValuePrice(pPrice);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final TethysDate pDate) {
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
    public boolean applyChanges(final DataItem pPrice) {
        /* Can only update from a SecurityPrice */
        if (!(pPrice instanceof SecurityPrice)) {
            return false;
        }
        final SecurityPrice myPrice = (SecurityPrice) pPrice;

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
    @SuppressWarnings("unchecked")
    public void adjustMapForItem() {
        final SecurityPriceBaseList<? extends SecurityPrice> myList = getList();
        final SecurityPriceDataMap<SecurityPrice> myMap = (SecurityPriceDataMap<SecurityPrice>) myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * Price List.
     * @param <T> the data type
     */
    public abstract static class SecurityPriceBaseList<T extends SecurityPrice>
            extends EncryptedList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(SecurityPriceBaseList.class);
        }

        /**
         * Construct an empty CORE Price list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected SecurityPriceBaseList(final MoneyWiseData pData,
                                        final Class<T> pClass,
                                        final MoneyWiseDataType pItemType) {
            /* Call super-constructor */
            super(pClass, pData, pItemType, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected SecurityPriceBaseList(final SecurityPriceBaseList<T> pSource) {
            /* Call super-constructor */
            super(pSource);
        }

        @Override
        protected SecurityPriceDataMap<T> getDataMap() {
            return (SecurityPriceDataMap<T>) super.getDataMap();
        }

        @Override
        protected SecurityPriceDataMap<T> allocateDataMap() {
            return new SecurityPriceDataMap<>();
        }
    }

    /**
     * Price List.
     */
    public static class SecurityPriceList
            extends SecurityPriceBaseList<SecurityPrice> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<SecurityPriceList> FIELD_DEFS = MetisFieldSet.newFieldSet(SecurityPriceList.class);

        /**
         * Construct an empty CORE price list.
         * @param pData the DataSet for the list
         */
        protected SecurityPriceList(final MoneyWiseData pData) {
            super(pData, SecurityPrice.class, MoneyWiseDataType.SECURITYPRICE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private SecurityPriceList(final SecurityPriceList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<SecurityPriceList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return SecurityPrice.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        protected SecurityPriceList getEmptyList(final ListStyle pStyle) {
            final SecurityPriceList myList = new SecurityPriceList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Construct an edit extract of a Rate list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public SecurityPriceList deriveEditList(final UpdateSet pUpdateSet) throws OceanusException {
            /* Build an empty List */
            final SecurityPriceList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Loop through the list */
            final Iterator<SecurityPrice> myIterator = iterator();
            while (myIterator.hasNext()) {
                final SecurityPrice myCurr = myIterator.next();

                /* Copy the item */
                final SecurityPrice myItem = new SecurityPrice(myList, myCurr);
                myItem.resolveUpdateSetLinks(pUpdateSet);
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
        public SecurityPrice addCopyItem(final DataItem pPrice) {
            if (pPrice instanceof SecurityPrice) {
                final SecurityPrice myPrice = new SecurityPrice(this, (SecurityPrice) pPrice);
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
        public SecurityPrice addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SecurityPrice addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the price */
            final SecurityPrice myPrice = new SecurityPrice(this, pValues);

            /* Check that this PriceId has not been previously added */
            if (!isIdUnique(myPrice.getId())) {
                myPrice.addError(ERROR_DUPLICATE, FIELD_ID);
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
     * @param <T> the data type
     */
    public static class SecurityPriceDataMap<T extends SecurityPrice>
            implements DataMapItem<T>, MetisFieldItem {
        /**
         * Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<SecurityPriceDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(SecurityPriceDataMap.class);

        /*
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_MAP_MAPOFMAPS, SecurityPriceDataMap::getMapOfMaps);
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.SECURITYPRICE_MAP_MAPOFPRICES, SecurityPriceDataMap::getMapOfPrices);
        }

        /**
         * Map of Maps.
         */
        private final Map<Security, Map<TethysDate, Integer>> theMapOfMaps;

        /**
         * Map of Prices.
         */
        private final Map<Security, PriceList> theMapOfPrices;

        /**
         * Constructor.
         */
        public SecurityPriceDataMap() {
            /* Create the maps */
            theMapOfMaps = new HashMap<>();
            theMapOfPrices = new HashMap<>();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public MetisFieldSet<SecurityPriceDataMap> getDataFieldSet() {
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
        private Map<Security, Map<TethysDate, Integer>> getMapOfMaps() {
            return theMapOfMaps;
        }

        /**
         * Obtain mapOfPrices.
         * @return the map
         */
        private Map<Security, PriceList> getMapOfPrices() {
            return theMapOfPrices;
        }

        @Override
        public void resetMap() {
            theMapOfMaps.clear();
            theMapOfPrices.clear();
        }

        @Override
        public void adjustForItem(final DataItem pItem) {
            /* Access the Security Id */
            final SecurityPrice myItem = (SecurityPrice) pItem;
            final Security mySecurity = myItem.getSecurity();
            if (mySecurity == null) {
                return;
            }

            /* Access the map */
            final Map<TethysDate, Integer> myMap = theMapOfMaps.computeIfAbsent(mySecurity, s -> new HashMap<>());

            /* Adjust price count */
            final TethysDate myDate = myItem.getDate();
            final Integer myCount = myMap.get(myDate);
            if (myCount == null) {
                myMap.put(myDate, DataInstanceMap.ONE);
            } else {
                myMap.put(myDate, myCount + 1);
            }

            /* Access the list */
            final PriceList myList = theMapOfPrices.computeIfAbsent(mySecurity, PriceList::new);

            /* Add element to the list */
            myList.add(myItem);
        }

        /**
         * Check validity of Price.
         * @param pItem the price
         * @return true/false
         */
        public boolean validPriceCount(final SecurityPrice pItem) {
            /* Access the Details */
            final Security mySecurity = pItem.getSecurity();
            final TethysDate myDate = pItem.getDate();

            /* Access the map */
            final Map<TethysDate, Integer> myMap = theMapOfMaps.get(mySecurity);
            if (myMap != null) {
                final Integer myResult = myMap.get(myDate);
                return DataInstanceMap.ONE.equals(myResult);
            }
            return false;
        }

        /**
         * Check availability of date for a security.
         * @param pSecurity the security
         * @param pDate the key to look up
         * @return true/false
         */
        public boolean availableDate(final Security pSecurity,
                                     final TethysDate pDate) {
            /* Access the map */
            final Map<TethysDate, Integer> myMap = theMapOfMaps.get(pSecurity);
            return myMap == null
                   || myMap.get(pDate) == null;
        }

        /**
         * Obtain price for date.
         * @param pSecurity the security
         * @param pDate the date
         * @return the latest price for the date.
         */
        public TethysPrice getPriceForDate(final AssetBase pSecurity,
                                           final TethysDate pDate) {
            /* Access as security */
            final Security mySecurity = Security.class.cast(pSecurity);

            /* Access list for security */
            final PriceList myList = theMapOfPrices.get(mySecurity);
            if (myList != null) {
                /* Loop through the prices */
                final Iterator<SecurityPrice> myIterator = myList.iterator();
                while (myIterator.hasNext()) {
                    final SecurityPrice myCurr = myIterator.next();

                    /* Return this price if this is earlier or equal to the the date */
                    if (pDate.compareTo(myCurr.getDate()) >= 0) {
                        return myCurr.getPrice();
                    }
                }
            }

            /* return single unit price */
            final Currency myCurrency = mySecurity.getCurrency();
            return TethysPrice.getWholeUnits(DataInstanceMap.ONE, myCurrency);
        }

        /**
         * Obtain prices for range.
         * @param pSecurity the security
         * @param pRange the date range
         * @return the two deep array of prices for the range.
         */
        public TethysPrice[] getPricesForRange(final Security pSecurity,
                                               final TethysDateRange pRange) {
            /* Set price */
            final Currency myCurrency = pSecurity.getCurrency();
            TethysPrice myFirst = TethysPrice.getWholeUnits(DataInstanceMap.ONE, myCurrency);
            TethysPrice myLatest = myFirst;
            final TethysDate myStart = pRange.getStart();

            /* Access list for security */
            final PriceList myList = theMapOfPrices.get(pSecurity);
            if (myList != null) {
                /* Loop through the prices */
                final ListIterator<SecurityPrice> myIterator = myList.listIterator(myList.size());
                while (myIterator.hasPrevious()) {
                    final SecurityPrice myCurr = myIterator.previous();

                    /* Check for the range of the date */
                    final TethysDate myDate = myCurr.getDate();
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
            return new TethysPrice[]
            { myFirst, myLatest };
        }

        /**
         * Obtain priceList cursor.
         * @param pSecurity the security
         * @return the latest price for the date.
         */
        public ListIterator<SecurityPrice> priceIterator(final Security pSecurity) {
            /* Access list for currency */
            final PriceList myList = theMapOfPrices.get(pSecurity);
            return myList != null
                                  ? myList.listIterator(myList.size())
                                  : null;
        }

        /**
         * Price List class.
         */
        private static final class PriceList
                implements MetisFieldItem, MetisDataList<SecurityPrice> {
            /**
             * Report fields.
             */
            private static final MetisFieldSet<PriceList> FIELD_DEFS = MetisFieldSet.newFieldSet(PriceList.class);

            /*
             * UnderlyingMap Field Id.
             */
            static {
                FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE, PriceList::size);
            }

            /**
             * The list.
             */
            private final List<SecurityPrice> theList;

            /**
             * The security.
             */
            private final Security theSecurity;

            /**
             * Constructor.
             * @param pSecurity the security
             */
            private PriceList(final Security pSecurity) {
                theSecurity = pSecurity;
                theList = new ArrayList<>();
            }

            @Override
            public MetisFieldSet<PriceList> getDataFieldSet() {
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
            public List<SecurityPrice> getUnderlyingList() {
                return theList;
            }
        }
    }
}
