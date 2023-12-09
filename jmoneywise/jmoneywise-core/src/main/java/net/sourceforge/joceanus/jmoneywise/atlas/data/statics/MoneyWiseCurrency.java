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
package net.sourceforge.joceanus.jmoneywise.atlas.data.statics;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * AssetCurrency data type.
 * @author Tony Washer
 */
public class MoneyWiseCurrency
        extends PrometheusStaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.CURRENCY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.CURRENCY.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseCurrency> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseCurrency.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareBooleanField(MoneyWiseStaticResource.CURRENCY_DEFAULT);
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Account Currency with
     * @param pCurrency The Account Currency to copy
     */
    protected MoneyWiseCurrency(final MoneyWiseCurrencyList pList,
                                final MoneyWiseCurrency pCurrency) {
        super(pList, pCurrency);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Currency with
     * @param pName Name of Account Currency
     * @throws OceanusException on error
     */
    private MoneyWiseCurrency(final MoneyWiseCurrencyList pList,
                              final String pName) throws OceanusException {
        super(pList, pName);
        setValueDefault(Boolean.FALSE);
        setValueEnabled(Boolean.TRUE);
        setValueDesc(getCurrencyClass().getCurrency().getDisplayName());
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Currency with
     * @param pClass Class of Account Currency
     * @throws OceanusException on error
     */
    private MoneyWiseCurrency(final MoneyWiseCurrencyList pList,
                              final MoneyWiseCurrencyClass pClass) throws OceanusException {
        super(pList, pClass);
        setValueDefault(Boolean.FALSE);
        setValueEnabled(Boolean.TRUE);
        setValueDesc(pClass.getCurrency().getDisplayName());
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseCurrency(final MoneyWiseCurrencyList pList,
                              final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);

        /* Store the Default */
        final Object myValue = pValues.getValue(MoneyWiseStaticResource.CURRENCY_DEFAULT);
        if (myValue instanceof Boolean) {
            setValueDefault((Boolean) myValue);
        } else if (myValue instanceof String) {
            final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();
            setValueDefault(myFormatter.parseValue((String) myValue, Boolean.class));
        } else {
            setValueDefault(Boolean.FALSE);
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseStaticResource.CURRENCY_DEFAULT.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Is this the default currency.
     * @return true/false
     */
    public Boolean isDefault() {
        return getValues().getValue(MoneyWiseStaticResource.CURRENCY_DEFAULT, Boolean.class);
   }

    /**
     * Set default indication.
     * @param pValue the value
     */
    private void setValueDefault(final Boolean pValue) {
        getValues().setUncheckedValue(MoneyWiseStaticResource.CURRENCY_DEFAULT, pValue);
    }

    /**
     * Return the Currency class of the AccountCurrency.
     * @return the class
     */
    public MoneyWiseCurrencyClass getCurrencyClass() {
        return (MoneyWiseCurrencyClass) super.getStaticClass();
    }

    @Override
    public MoneyWiseCurrency getBase() {
        return (MoneyWiseCurrency) super.getBase();
    }

    @Override
    public MoneyWiseCurrencyList getList() {
        return (MoneyWiseCurrencyList) super.getList();
    }

    /**
     * Return the Currency of the AccountCurrency.
     * @return the currency
     */
    public Currency getCurrency() {
        return getCurrencyClass().getCurrency();
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Handle differences in default value */
        final MoneyWiseCurrency myThat = (MoneyWiseCurrency) pThat;
        if (!isDefault().equals(myThat.isDefault())) {
            return Boolean.TRUE.equals(isDefault())
                    ? -1
                    : 1;
        }

        /* Handle normally */
        return super.compareValues(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        final MetisFieldVersionValues myValues = getValues();

        /* Adjust Default */
        final Object myDefault = myValues.getValue(MoneyWiseStaticResource.CURRENCY_DEFAULT);
        if (myDefault == null) {
            setValueDefault(Boolean.FALSE);
        }
    }

    /**
     * Set default indication.
     * @param pDefault the new indication
     */
    public void setDefault(final Boolean pDefault) {
        setValueDefault(pDefault);
    }

    @Override
    public void validate() {
        final MoneyWiseCurrencyList myList = getList();
        final MoneyWiseCurrencyDataMap myMap = myList.getDataMap();

        /* Check that default is non-null */
        if (isDefault() == null) {
            addError(ERROR_MISSING, MoneyWiseStaticResource.CURRENCY_DEFAULT);

            /* else check various things for a default currency */
        } else if (Boolean.TRUE.equals(isDefault())) {
            /* Check that default is enabled */
            if (!getEnabled()) {
                addError(ERROR_DISABLED, MoneyWiseStaticResource.CURRENCY_DEFAULT);
            }

            /* Check for multiple defaults */
            if (!myMap.validDefaultCount()) {
                addError("Multiple default currencies", MoneyWiseStaticResource.CURRENCY_DEFAULT);
            }
        }

        /* Validate it */
        super.validate();
    }

    @Override
    public boolean applyChanges(final PrometheusDataItem pData) {
        /* Can only apply changes for AccountCurrency */
        if (!(pData instanceof MoneyWiseCurrency)) {
            return false;
        }

        /* Access the data */
        final MoneyWiseCurrency myData = (MoneyWiseCurrency) pData;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myData);

        /* Update the default indication if required */
        if (!isDefault().equals(myData.isDefault())) {
            setDefault(myData.isDefault());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Represents a list of {@link MoneyWiseCurrency} objects.
     */
    public static class MoneyWiseCurrencyList
            extends PrometheusStaticList<MoneyWiseCurrency> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseCurrencyList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCurrencyList.class);

        /**
         * Construct an empty CORE account currency list.
         * @param pData the DataSet for the list
         */
        public MoneyWiseCurrencyList(final PrometheusDataSet pData) {
            super(MoneyWiseCurrency.class, pData, MoneyWiseStaticDataType.CURRENCY, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseCurrencyList(final MoneyWiseCurrencyList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseCurrencyList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseCurrency.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseCurrencyClass> getEnumClass() {
            return MoneyWiseCurrencyClass.class;
        }

        @Override
        protected MoneyWiseCurrencyDataMap getDataMap() {
            return (MoneyWiseCurrencyDataMap) super.getDataMap();
        }

        @Override
        protected MoneyWiseCurrencyList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseCurrencyList myList = new MoneyWiseCurrencyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseCurrency addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone an AccountCurrency */
            if (!(pItem instanceof MoneyWiseCurrency)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseCurrency myCurr = new MoneyWiseCurrency(this, (MoneyWiseCurrency) pItem);
            add(myCurr);
            return myCurr;
        }

        @Override
        public MoneyWiseCurrency addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add an AccountCurrency to the list.
         * @param pCurrency the Name of the account currency
         * @return the new currency
         * @throws OceanusException on error
         */
        public MoneyWiseCurrency addBasicItem(final String pCurrency) throws OceanusException {
            /* Create a new Account Currency */
            final MoneyWiseCurrency myCurr = new MoneyWiseCurrency(this, pCurrency);

            /* Check that this AccountCurrencyId has not been previously added */
            if (!isIdUnique(myCurr.getIndexedId())) {
                myCurr.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }

            /* Add the Account Currency to the list */
            add(myCurr);
            return myCurr;
        }

        @Override
        public MoneyWiseCurrency addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the currency */
            final MoneyWiseCurrency myCurrency = new MoneyWiseCurrency(this, pValues);

            /* Check that this CurrencyId has not been previously added */
            if (!isIdUnique(myCurrency.getIndexedId())) {
                myCurrency.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCurrency, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myCurrency);

            /* Return it */
            return myCurrency;
        }

        @Override
        public void populateDefaults() throws OceanusException {
            /* Initialise the default currency */
            initialiseDefault();

            /* Ensure that the list is sorted */
            reSort();
        }

        /**
         * Initialise the default currency.
         */
        public void initialiseDefault() {
            /* Determine the locale currency */
            final Locale myLocale = Locale.getDefault();
            final DecimalFormatSymbols mySymbols = DecimalFormatSymbols.getInstance(myLocale);
            final Currency myCurrency = mySymbols.getCurrency();

            /* Find the currency in the list */
            MoneyWiseCurrency myCurr = findCurrency(myCurrency);
            if (myCurr == null) {
                /* Default to GBP if local currency not found */
                myCurr = findItemByClass(MoneyWiseCurrencyClass.GBP);
            }

            /* If we have a currency */
            if (myCurr != null) {
                /* Set it as the default */
                myCurr.setDefault(Boolean.TRUE);
                myCurr.setValueEnabled(Boolean.TRUE);
            }
        }

        /**
         * find a currency in the list.
         * @param pCurrency the currency to find
         * @return The currency
         */
        public MoneyWiseCurrency findCurrency(final Currency pCurrency) {
            /* Look up the currency */
            final MoneyWiseCurrencyClass myClass = MoneyWiseCurrencyClass.fromCurrency(pCurrency);
            return findItemByClass(myClass);
        }

        /**
         * Find the default currency.
         * @return The default currency
         */
        public MoneyWiseCurrency findDefault() {
            /* look up the default in the map */
            final MoneyWiseCurrencyDataMap myMap = getDataMap();
            return myMap == null
                    ? null
                    : myMap.getDefault();
        }

        @Override
        protected MoneyWiseCurrency newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the currency */
            final MoneyWiseCurrency myCurr = new MoneyWiseCurrency(this, (MoneyWiseCurrencyClass) pClass);

            /* Check that this CurrId has not been previously added */
            if (!isIdUnique(myCurr.getIndexedId())) {
                myCurr.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myCurr);

            /* Return it */
            return myCurr;
        }

        /**
         * Set default currency.
         * @param pCurrency the new default currency.
         */
        public void setDefaultCurrency(final MoneyWiseCurrency pCurrency) {
            /* Find the default currency */
            final MoneyWiseCurrency myCurr = findDefault();

            /* If we are changing the currency */
            if (!pCurrency.equals(myCurr)) {
                /* If we have a default value */
                if (myCurr != null) {
                    /* Clear default value */
                    myCurr.pushHistory();
                    myCurr.setDefault(Boolean.FALSE);
                }

                /* Set new currency */
                pCurrency.pushHistory();
                pCurrency.setDefault(Boolean.TRUE);
            }
        }

        @Override
        protected MoneyWiseCurrencyDataMap allocateDataMap() {
            return new MoneyWiseCurrencyDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    protected static final class MoneyWiseCurrencyDataMap
            extends PrometheusStaticDataMap<MoneyWiseCurrency> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseCurrencyDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCurrencyDataMap.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseStaticResource.CURRENCY_DEFAULT, MoneyWiseCurrencyDataMap::getDefault);
        }

        /**
         * Default value.
         */
        private MoneyWiseCurrency theDefault;

        /**
         * Default count.
         */
        private Integer theDefaultCount;

        /**
         * Constructor.
         */
        private MoneyWiseCurrencyDataMap() {
        }

        @Override
        public MetisFieldSet<MoneyWiseCurrencyDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        @Override
        public void resetMap() {
            super.resetMap();
            theDefault = null;
            theDefaultCount = null;
        }

        @Override
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Adjust order count */
            final MoneyWiseCurrency myItem = (MoneyWiseCurrency) pItem;
            if (Boolean.TRUE.equals(myItem.isDefault())) {
                theDefault = myItem;
                theDefaultCount = theDefaultCount == null
                        ? ONE
                        : theDefaultCount + 1;
            }

            /* Adjust name/order count */
            super.adjustForItem(pItem);
        }

        /**
         * find default currency.
         * @return the default currency
         */
        public MoneyWiseCurrency getDefault() {
            return theDefault;
        }

        /**
         * Check validity of default count.
         * @return true/false
         */
        public boolean validDefaultCount() {
            return ONE.equals(theDefaultCount);
        }
    }
}
