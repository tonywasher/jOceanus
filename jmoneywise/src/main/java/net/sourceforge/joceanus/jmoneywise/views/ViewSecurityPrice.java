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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.DilutionEvent.DilutionEventMap;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;

/**
 * Extension of SecurityPrice to cater for diluted prices.
 * @author Tony Washer
 */
public class ViewSecurityPrice
        extends SecurityPrice {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(ViewSecurityPrice.class.getName());

    /**
     * Object name.
     */
    public static final String OBJECT_NAME = NLS_BUNDLE.getString("DataName");

    /**
     * List name.
     */
    public static final String LIST_NAME = NLS_BUNDLE.getString("DataListName");

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, SecurityPrice.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Dilution Field Id.
     */
    public static final JDataField FIELD_DILUTION = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDilution"));

    /**
     * Diluted Price Field Id.
     */
    public static final JDataField FIELD_DILUTEDPRICE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDilutedPrice"));

    /**
     * Dilution state.
     */
    private DilutionState theDilutionState = DilutionState.UNKNOWN;

    /**
     * Obtain dilution.
     * @return the dilution
     */
    public JDilution getDilution() {
        if (theDilutionState.equals(DilutionState.UNKNOWN)) {
            calculateDiluted();
        }
        return getDilution(getValueSet());
    }

    /**
     * Obtain diluted price.
     * @return the diluted price
     */
    public JDilutedPrice getDilutedPrice() {
        if (theDilutionState.equals(DilutionState.UNKNOWN)) {
            calculateDiluted();
        }
        return getDilutedPrice(getValueSet());
    }

    /**
     * Obtain dilution.
     * @param pValueSet the valueSet
     * @return the dilution
     */
    public static JDilution getDilution(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DILUTION, JDilution.class);
    }

    /**
     * Obtain diluted price.
     * @param pValueSet the valueSet
     * @return the diluted price
     */
    public static JDilutedPrice getDilutedPrice(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DILUTEDPRICE, JDilutedPrice.class);
    }

    /**
     * Set dilution.
     * @param pValue the dilution
     */
    private void setValueDilution(final JDilution pValue) {
        getValueSet().setValue(FIELD_DILUTION, pValue);
    }

    /**
     * Set diluted price.
     * @param pValue the diluted price
     */
    private void setValueDilutedPrice(final JDilutedPrice pValue) {
        getValueSet().setValue(FIELD_DILUTEDPRICE, pValue);
    }

    @Override
    public SecurityPrice getBase() {
        return (SecurityPrice) super.getBase();
    }

    /**
     * Calculate Diluted values.
     */
    protected final void calculateDiluted() {
        /* Ignore if undiluted */
        if (theDilutionState.equals(DilutionState.UNDILUTED)) {
            calculateDiluted();
        }

        /* Access the list for the item */
        ViewSecurityPriceList myList = (ViewSecurityPriceList) getList();

        /* Set null default dilution */
        setValueDilution(null);
        setValueDilutedPrice(null);

        /* Access Price details */
        JDateDay myDate = getDate();
        JPrice myPrice = getPrice();
        Security mySecurity = getSecurity();

        /* Ignore if we have no details */
        if ((myDate == null) || (myPrice == null)) {
            return;
        }

        /* Obtain dilutions */
        DilutionEventMap myDilutions = myList.getDilutions();

        /* If we are unsure about dilutions check for them */
        if (theDilutionState.equals(DilutionState.UNKNOWN) &&
            !myDilutions.hasDilution(mySecurity)) {
            theDilutionState = DilutionState.UNDILUTED;
            return;
        }

        /* Determine the dilution factor for the date */
        JDilution myDilution = myDilutions.getDilutionFactor(mySecurity, myDate);

        /* If we have a dilution factor */
        if (myDilution != null) {
            /* Store dilution details */
            setValueDilution(myDilution);
            setValueDilutedPrice(myPrice.getDilutedPrice(myDilution));
        }

        /* Note dilution state */
        theDilutionState = DilutionState.DILUTED;
    }

    /**
     * Construct a copy of a Price.
     * @param pList the list
     * @param pPrice The Price
     */
    protected ViewSecurityPrice(final ViewSecurityPriceList pList,
                                final SecurityPrice pPrice) {
        /* Set standard values */
        super(pList, pPrice);
    }

    /**
     * Standard constructor for a newly inserted price.
     * @param pList the list
     */
    public ViewSecurityPrice(final ViewSecurityPriceList pList) {
        super(pList);
    }

    @Override
    public void setPrice(final JPrice pPrice) throws JOceanusException {
        super.setPrice(pPrice);
        calculateDiluted();
    }

    @Override
    public void setDate(final JDateDay pDate) {
        /* Store date */
        super.setDate(pDate);
        calculateDiluted();
    }

    /**
     * Price List.
     */
    public static class ViewSecurityPriceList
            extends SecurityPriceBaseList<ViewSecurityPrice> {
        /**
         * Report fields.
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
            return ViewSecurityPrice.FIELD_DEFS;
        }

        @Override
        protected ViewSecurityPriceList getEmptyList(final ListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        /**
         * The Dilutions field id.
         */
        public static final JDataField FIELD_DILUTIONS = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataDilutions"));

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_DILUTIONS.equals(pField)) {
                return (theDilutions.isEmpty())
                                               ? JDataFieldValue.SKIP
                                               : theDilutions;
            }
            return super.getFieldValue(pField);
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Dilutions list.
         */
        private final DilutionEventMap theDilutions;

        /**
         * Obtain dilutions.
         * @return the dilutions
         */
        private DilutionEventMap getDilutions() {
            return theDilutions;
        }

        /**
         * Construct an edit extract of a Price list.
         * @param pView The master view
         */
        public ViewSecurityPriceList(final View pView) {
            /* Declare the data and set the style */
            super(pView.getData(), ViewSecurityPrice.class, MoneyWiseDataType.SECURITYPRICE);
            setStyle(ListStyle.EDIT);

            /* Access the base prices */
            SecurityPriceList myPrices = getDataSet().getSecurityPrices();
            setBase(myPrices);

            /* Store dilution list and record whether we have dilutions */
            theDilutions = pView.getDilutions();

            /* Loop through the list */
            Iterator<SecurityPrice> myIterator = myPrices.listIterator();
            while (myIterator.hasNext()) {
                SecurityPrice myCurr = myIterator.next();

                /* Copy the item */
                ViewSecurityPrice myItem = new ViewSecurityPrice(this, myCurr);
                add(myItem);
            }
        }

        @Override
        public ViewSecurityPrice addCopyItem(final DataItem<?> pElement) {
            throw new UnsupportedOperationException();
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public ViewSecurityPrice addNewItem() {
            ViewSecurityPrice myPrice = new ViewSecurityPrice(this);
            // myPrice.setSecurity(theSecurity);
            add(myPrice);
            return myPrice;
        }

        @Override
        public ViewSecurityPrice addValuesItem(final DataValues<MoneyWiseDataType> pValues) {
            throw new UnsupportedOperationException();
        }

        /**
         * Resolve update set links.
         * @param pUpdateSet the updateSet
         * @throws JOceanusException on error
         */
        public void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
            /* Loop through the items */
            Iterator<ViewSecurityPrice> myIterator = iterator();
            while (myIterator.hasNext()) {
                ViewSecurityPrice myCurr = myIterator.next();
                myCurr.resolveUpdateSetLinks(pUpdateSet);
            }
        }
    }

    /**
     * Dilution state.
     */
    private enum DilutionState {
        /**
         * Unknown.
         */
        UNKNOWN,

        /**
         * Diluted.
         */
        DILUTED,

        /**
         * Undiluted.
         */
        UNDILUTED;
    }
}
