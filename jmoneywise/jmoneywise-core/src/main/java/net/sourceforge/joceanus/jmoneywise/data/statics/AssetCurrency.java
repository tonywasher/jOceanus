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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;

import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmetis.data.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * AssetCurrency data type.
 * @author Tony Washer
 */
public class AssetCurrency
        extends StaticData<AssetCurrency, AssetCurrencyClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.CURRENCY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.CURRENCY.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    /**
     * Default Field Id.
     */
    public static final JDataField FIELD_DEFAULT = FIELD_DEFS.declareEqualityValueField(StaticDataResource.CURRENCY_DEFAULT.getValue());

    /**
     * Copy Constructor.
     * @param pList The list to associate the Account Currency with
     * @param pCurrency The Account Currency to copy
     */
    protected AssetCurrency(final AssetCurrencyList pList,
                            final AssetCurrency pCurrency) {
        super(pList, pCurrency);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Currency with
     * @param pName Name of Account Currency
     * @throws JOceanusException on error
     */
    private AssetCurrency(final AssetCurrencyList pList,
                          final String pName) throws JOceanusException {
        super(pList, pName);
        setValueDefault(Boolean.FALSE);
        setValueEnabled(Boolean.FALSE);
        setValueDesc(getCurrencyClass().getCurrency().getDisplayName());
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Currency with
     * @param pClass Class of Account Currency
     * @throws JOceanusException on error
     */
    private AssetCurrency(final AssetCurrencyList pList,
                          final AssetCurrencyClass pClass) throws JOceanusException {
        super(pList, pClass);
        setValueDefault(Boolean.FALSE);
        setValueEnabled(Boolean.TRUE);
        setValueDesc(pClass.getCurrency().getDisplayName());
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private AssetCurrency(final AssetCurrencyList pList,
                          final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        super(pList, pValues);

        /* Store the Default */
        Object myValue = pValues.getValue(FIELD_DEFAULT);
        if (myValue instanceof Boolean) {
            setValueDefault((Boolean) myValue);
        } else if (myValue instanceof String) {
            JDataFormatter myFormatter = getDataSet().getDataFormatter();
            setValueDefault(myFormatter.parseValue((String) myValue, Boolean.class));
        }
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_DEFAULT.equals(pField)) {
            return isDefault();
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Is this the default currency.
     * @return true/false
     */
    public Boolean isDefault() {
        return isDefault(getValueSet());
    }

    /**
     * Is this the default currency.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isDefault(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DEFAULT, Boolean.class);
    }

    /**
     * Set default indication.
     * @param pValue the value
     */
    private void setValueDefault(final Boolean pValue) {
        getValueSet().setValue(FIELD_DEFAULT, (pValue != null)
                                                              ? pValue
                                                              : Boolean.FALSE);
    }

    /**
     * Return the Currency class of the AccountCurrency.
     * @return the class
     */
    public AssetCurrencyClass getCurrencyClass() {
        return super.getStaticClass();
    }

    @Override
    public AssetCurrency getBase() {
        return (AssetCurrency) super.getBase();
    }

    @Override
    public AssetCurrencyList getList() {
        return (AssetCurrencyList) super.getList();
    }

    /**
     * Return the Currency of the AccountCurrency.
     * @return the currency
     */
    public Currency getCurrency() {
        return getCurrencyClass().getCurrency();
    }

    @Override
    public int compareTo(final AssetCurrency pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Handle differences in default value */
        if (!isDefault().equals(pThat.isDefault())) {
            return isDefault()
                              ? -1
                              : 1;
        }

        /* Handle normally */
        return super.compareTo(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        ValueSet myValues = getValueSet();

        /* Adjust Default */
        Object myDefault = myValues.getValue(FIELD_DEFAULT);
        if (myDefault == null) {
            setValueDefault(Boolean.FALSE);
        }
    }

    /**
     * Set default indication.
     * @param pDefault the new indication
     */
    private void setDefault(final Boolean pDefault) {
        setValueDefault(pDefault);
    }

    @Override
    public void validate() {
        AssetCurrencyList myList = getList();
        CurrencyDataMap myMap = myList.getDataMap();

        /* Check that default is non-null */
        if (isDefault() == null) {
            addError(ERROR_MISSING, FIELD_DEFAULT);

            /* else check various things for a default currency */
        } else if (isDefault()) {
            /* Check that default is enabled */
            if (!getEnabled()) {
                addError(ERROR_DISABLED, FIELD_DEFAULT);
            }

            /* Check for multiple defaults */
            if (!myMap.validDefaultCount()) {
                addError("Multiple default currencies", FIELD_DEFAULT);
            }
        }

        /* Validate it */
        super.validate();
    }

    @Override
    public boolean applyChanges(final DataItem<?> pData) {
        /* Can only apply changes for AccountCurrency */
        if (!(pData instanceof AssetCurrency)) {
            return false;
        }

        /* Access the data */
        AssetCurrency myData = (AssetCurrency) pData;

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
     * Represents a list of {@link AssetCurrency} objects.
     */
    public static class AssetCurrencyList
            extends StaticList<AssetCurrency, AssetCurrencyClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, StaticList.FIELD_DEFS);

        /**
         * Construct an empty CORE account currency list.
         * @param pData the DataSet for the list
         */
        public AssetCurrencyList(final DataSet<?, ?> pData) {
            super(AssetCurrency.class, pData, MoneyWiseDataType.CURRENCY, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AssetCurrencyList(final AssetCurrencyList pSource) {
            super(pSource);
        }

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
            return AssetCurrency.FIELD_DEFS;
        }

        @Override
        protected Class<AssetCurrencyClass> getEnumClass() {
            return AssetCurrencyClass.class;
        }

        @Override
        protected CurrencyDataMap getDataMap() {
            return (CurrencyDataMap) super.getDataMap();
        }

        @Override
        protected AssetCurrencyList getEmptyList(final ListStyle pStyle) {
            AssetCurrencyList myList = new AssetCurrencyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AssetCurrency addCopyItem(final DataItem<?> pItem) {
            /* Can only clone an AccountCurrency */
            if (!(pItem instanceof AssetCurrency)) {
                throw new UnsupportedOperationException();
            }

            AssetCurrency myCurr = new AssetCurrency(this, (AssetCurrency) pItem);
            add(myCurr);
            return myCurr;
        }

        @Override
        public AssetCurrency addNewItem() {
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
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pCurrency) throws JOceanusException {
            /* Create a new Account Currency */
            AssetCurrency myCurr = new AssetCurrency(this, pCurrency);

            /* Check that this AccountCurrencyId has not been previously added */
            if (!isIdUnique(myCurr.getId())) {
                myCurr.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }

            /* Add the Account Currency to the list */
            append(myCurr);
        }

        @Override
        public AssetCurrency addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the currency */
            AssetCurrency myCurrency = new AssetCurrency(this, pValues);

            /* Check that this CurrencyId has not been previously added */
            if (!isIdUnique(myCurrency.getId())) {
                myCurrency.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCurrency, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myCurrency);

            /* Return it */
            return myCurrency;
        }

        @Override
        public void populateDefaults() throws JOceanusException {
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
            Locale myLocale = Locale.getDefault();
            DecimalFormatSymbols mySymbols = DecimalFormatSymbols.getInstance(myLocale);
            Currency myCurrency = mySymbols.getCurrency();

            /* Find the currency in the list */
            AssetCurrency myCurr = findCurrency(myCurrency);
            if (myCurr == null) {
                /* Default to GBP if local currency not found */
                myCurr = findItemByClass(AssetCurrencyClass.GBP);
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
        public AssetCurrency findCurrency(final Currency pCurrency) {
            /* Look up the currency */
            AssetCurrencyClass myClass = AssetCurrencyClass.fromCurrency(pCurrency);
            return findItemByClass(myClass);
        }

        /**
         * Find the default currency.
         * @return The default currency
         */
        public AssetCurrency findDefault() {
            /* look up the default in the map */
            CurrencyDataMap myMap = getDataMap();
            return myMap == null
                                ? null
                                : myMap.getDefault();
        }

        @Override
        protected AssetCurrency newItem(final AssetCurrencyClass pClass) throws JOceanusException {
            /* Create the currency */
            AssetCurrency myCurr = new AssetCurrency(this, pClass);

            /* Check that this CurrId has not been previously added */
            if (!isIdUnique(myCurr.getId())) {
                myCurr.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myCurr);

            /* Return it */
            return myCurr;
        }

        /**
         * Set default currency.
         * @param pCurrency the new default currency.
         */
        public void setDefaultCurrency(final AssetCurrency pCurrency) {
            /* Find the default currency */
            AssetCurrency myCurr = findDefault();

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
        protected CurrencyDataMap allocateDataMap() {
            return new CurrencyDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    protected static final class CurrencyDataMap
            extends StaticDataMap<AssetCurrency, AssetCurrencyClass, MoneyWiseDataType> {
        /**
         * Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(PrometheusDataResource.STATICDATAMAP_NAME.getValue(), StaticDataMap.FIELD_DEFS);

        /**
         * Default Field Id.
         */
        public static final JDataField FIELD_DEFAULT = FIELD_DEFS.declareEqualityValueField(StaticDataResource.CURRENCY_DEFAULT.getValue());

        /**
         * Default value.
         */
        private AssetCurrency theDefault;

        /**
         * Default count.
         */
        private Integer theDefaultCount;

        /**
         * Constructor.
         */
        private CurrencyDataMap() {
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_DEFAULT.equals(pField)) {
                return theDefault;
            }

            /* Unknown */
            return super.getFieldValue(pField);
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public void resetMap() {
            super.resetMap();
            theDefault = null;
            theDefaultCount = null;
        }

        @Override
        public void adjustForItem(final AssetCurrency pItem) {
            /* Adjust order count */
            if (pItem.isDefault()) {
                theDefault = pItem;
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
        public AssetCurrency getDefault() {
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