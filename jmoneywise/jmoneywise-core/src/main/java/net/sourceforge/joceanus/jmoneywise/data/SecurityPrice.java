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

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisEncryptedData.MetisEncryptedPrice;
import net.sourceforge.joceanus.jmetis.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * SecurityPrice data type.
 * @author Tony Washer
 */
public class SecurityPrice
        extends EncryptedItem<MoneyWiseDataType>
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
    public static final MetisField FIELD_SECURITY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.SECURITY.getItemName());

    /**
     * Date Field Id.
     */
    public static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

    /**
     * Price Field Id.
     */
    public static final MetisField FIELD_PRICE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_PRICE.getValue());

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
                          final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            Object myValue = pValues.getValue(FIELD_DATE);
            if (myValue instanceof TethysDate) {
                setValueDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                TethysDateFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDateDay((String) myValue));
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
                String myString = (String) myValue;
                setValuePrice(myString);
                setValuePrice(myFormatter.parseValue(myString, TethysPrice.class));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException
                | OceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisField pField) {
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
        return formatObject();
    }

    @Override
    public String formatObject() {
        /* Access Key Values */
        MetisEncryptedValueSet myValues = getValueSet();
        Object mySecurity = myValues.getValue(FIELD_SECURITY);
        Object myDate = myValues.getValue(FIELD_DATE);
        Object myPrice = myValues.getValue(FIELD_PRICE);

        /* Access formatter */
        MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myFormatter.formatObject(mySecurity));
        myBuilder.append(": ");
        myBuilder.append(myFormatter.formatObject(myPrice));
        myBuilder.append('@');
        myBuilder.append(myFormatter.formatObject(myDate));

        /* return it */
        return myBuilder.toString();
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
        Security mySecurity = getSecurity();
        return (mySecurity == null)
                                    ? null
                                    : mySecurity.getId();
    }

    /**
     * Obtain SecurityName.
     * @return the securityName
     */
    public String getSecurityName() {
        Security mySecurity = getSecurity();
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
     * @throws OceanusException on error
     */
    private void setValuePrice(final String pValue) throws OceanusException {
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
        int iDiff = MetisDifference.compareObject(getDate(), pThat.getDate());
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
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_SECURITY, myData.getSecurities());
    }

    /**
     * Resolve links in an updateSet.
     * @param pUpdateSet the update Set
     * @throws OceanusException on error
     */
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* Resolve parent within list */
        SecurityList mySecurities = pUpdateSet.getDataList(MoneyWiseDataType.SECURITY, SecurityList.class);
        resolveDataLink(FIELD_SECURITY, mySecurities);
    }

    /**
     * Validate the price.
     */
    @Override
    public void validate() {
        Security mySecurity = getSecurity();
        TethysDate myDate = getDate();
        TethysPrice myPrice = getPrice();
        SecurityPriceBaseList<? extends SecurityPrice> myList = getList();
        MoneyWiseData mySet = getDataSet();

        /* The security must be non-null */
        if (mySecurity == null) {
            addError(ERROR_MISSING, FIELD_SECURITY);
        }

        /* The date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this security */
            SecurityPriceDataMap<? extends SecurityPrice> myMap = myList.getDataMap();
            if (!myMap.validPriceCount(this)) {
                addError(ERROR_DUPLICATE, FIELD_DATE);
            }

            /* The date must be in-range */
            if (mySet.getDateRange().compareTo(myDate) != 0) {
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
            AssetCurrency myCurrency = mySecurity == null
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
    public boolean applyChanges(final DataItem<?> pPrice) {
        /* Can only update from a SecurityPrice */
        if (!(pPrice instanceof SecurityPrice)) {
            return false;
        }
        SecurityPrice myPrice = (SecurityPrice) pPrice;

        /* Store the current detail into history */
        pushHistory();

        /* Update the price if required */
        if (!MetisDifference.isEqual(getPrice(), myPrice.getPrice())) {
            setValuePrice(myPrice.getPriceField());
        }

        /* Update the date if required */
        if (!MetisDifference.isEqual(getDate(), myPrice.getDate())) {
            setValueDate(myPrice.getDate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void adjustMapForItem() {
        SecurityPriceBaseList<? extends SecurityPrice> myList = getList();
        SecurityPriceDataMap<SecurityPrice> myMap = (SecurityPriceDataMap<SecurityPrice>) myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * Price List.
     * @param <T> the data type
     */
    public abstract static class SecurityPriceBaseList<T extends SecurityPrice>
            extends EncryptedList<T, MoneyWiseDataType> {
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
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, DataList.FIELD_DEFS);

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
        public MetisFields declareFields() {
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
            SecurityPriceList myList = new SecurityPriceList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pPrice item
         * @return the newly added item
         */
        @Override
        public SecurityPrice addCopyItem(final DataItem<?> pPrice) {
            if (pPrice instanceof SecurityPrice) {
                SecurityPrice myPrice = new SecurityPrice(this, (SecurityPrice) pPrice);
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
        public SecurityPrice addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the price */
            SecurityPrice myPrice = new SecurityPrice(this, pValues);

            /* Check that this PriceId has not been previously added */
            if (!isIdUnique(myPrice.getId())) {
                myPrice.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPrice, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPrice);

            /* Return it */
            return myPrice;
        }
    }

    /**
     * The dataMap class.
     * @param <T> the data type
     */
    public static class SecurityPriceDataMap<T extends SecurityPrice>
            implements DataMapItem<T, MoneyWiseDataType>, MetisDataContents {
        /**
         * Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDataResource.MONEYWISEDATA_MAP_MULTIMAP.getValue());

        /**
         * InstanceMap Field Id.
         */
        private static final MetisField FIELD_MAPOFMAPS = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_MAP_MAPOFMAPS.getValue());

        /**
         * PriceMap Field Id.
         */
        private static final MetisField FIELD_MAPOFPRICES = FIELD_DEFS
                .declareEqualityValueField(MoneyWiseDataResource.SECURITYPRICE_MAP_MAPOFPRICES.getValue());

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
            if (FIELD_MAPOFPRICES.equals(pField)) {
                return theMapOfPrices;
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
            theMapOfPrices.clear();
        }

        @Override
        public void adjustForItem(final T pItem) {
            /* Access the Security Id */
            Security mySecurity = pItem.getSecurity();
            if (mySecurity == null) {
                return;
            }

            /* Access the map */
            Map<TethysDate, Integer> myMap = theMapOfMaps.get(mySecurity);
            if (myMap == null) {
                myMap = new HashMap<>();
                theMapOfMaps.put(mySecurity, myMap);
            }

            /* Adjust price count */
            TethysDate myDate = pItem.getDate();
            Integer myCount = myMap.get(myDate);
            if (myCount == null) {
                myMap.put(myDate, DataInstanceMap.ONE);
            } else {
                myMap.put(myDate, myCount + 1);
            }

            /* Access the list */
            PriceList myList = theMapOfPrices.get(mySecurity);
            if (myList == null) {
                myList = new PriceList(mySecurity);
                theMapOfPrices.put(mySecurity, myList);
            }

            /* Add element to the list */
            myList.add(pItem);
        }

        /**
         * Check validity of Price.
         * @param pItem the price
         * @return true/false
         */
        public boolean validPriceCount(final SecurityPrice pItem) {
            /* Access the Details */
            Security mySecurity = pItem.getSecurity();
            TethysDate myDate = pItem.getDate();

            /* Access the map */
            Map<TethysDate, Integer> myMap = theMapOfMaps.get(mySecurity);
            if (myMap != null) {
                Integer myResult = myMap.get(myDate);
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
            Map<TethysDate, Integer> myMap = theMapOfMaps.get(pSecurity);
            return myMap != null
                                 ? myMap.get(pDate) == null
                                 : true;
        }

        /**
         * Obtain price for date.
         * @param pSecurity the security
         * @param pDate the date
         * @return the latest price for the date.
         */
        public TethysPrice getPriceForDate(final AssetBase<?> pSecurity,
                                           final TethysDate pDate) {
            /* Access as security */
            Security mySecurity = Security.class.cast(pSecurity);

            /* Access list for security */
            PriceList myList = theMapOfPrices.get(mySecurity);
            if (myList != null) {
                /* Loop through the prices */
                ListIterator<SecurityPrice> myIterator = myList.listIterator();
                while (myIterator.hasNext()) {
                    SecurityPrice myCurr = myIterator.next();

                    /* Return this price if this is earlier or equal to the the date */
                    if (pDate.compareTo(myCurr.getDate()) >= 0) {
                        return myCurr.getPrice();
                    }
                }
            }

            /* return single unit price */
            Currency myCurrency = mySecurity.getCurrency();
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
            Currency myCurrency = pSecurity.getCurrency();
            TethysPrice myFirst = TethysPrice.getWholeUnits(DataInstanceMap.ONE, myCurrency);
            TethysPrice myLatest = null;

            /* Access list for security */
            PriceList myList = theMapOfPrices.get(pSecurity);
            if (myList != null) {
                /* Loop through the prices */
                ListIterator<SecurityPrice> myIterator = myList.listIterator(myList.size());
                while (myIterator.hasPrevious()) {
                    SecurityPrice myCurr = myIterator.previous();

                    /* Check for the range of the date */
                    int iComp = pRange.compareTo(myCurr.getDate());

                    /* If this is later than the range we are finished */
                    if (iComp < 0) {
                        break;
                    }

                    /* Record as best price */
                    myLatest = myCurr.getPrice();

                    /* Record early price */
                    if (iComp > 0) {
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
            PriceList myList = theMapOfPrices.get(pSecurity);
            return (myList != null)
                                    ? myList.listIterator(myList.size())
                                    : null;
        }

        /**
         * Price List class.
         */
        private static final class PriceList
                extends ArrayList<SecurityPrice>
                implements MetisDataContents {
            /**
             * Serial Id.
             */
            private static final long serialVersionUID = -44781825426553991L;

            /**
             * Report fields.
             */
            private static final MetisFields FIELD_DEFS = new MetisFields(PriceList.class.getSimpleName());

            /**
             * Size Field Id.
             */
            private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

            /**
             * The security.
             */
            private final transient Security theSecurity;

            /**
             * Constructor.
             * @param pSecurity the security
             */
            private PriceList(final Security pSecurity) {
                theSecurity = pSecurity;
            }

            @Override
            public MetisFields getDataFields() {
                return FIELD_DEFS;
            }

            @Override
            public Object getFieldValue(final MetisField pField) {
                if (FIELD_SIZE.equals(pField)) {
                    return size();
                }
                return MetisFieldValue.UNKNOWN;
            }

            @Override
            public String formatObject() {
                return theSecurity.formatObject()
                       + "("
                       + size()
                       + ")";
            }

            @Override
            public String toString() {
                return formatObject();
            }
        }
    }
}
