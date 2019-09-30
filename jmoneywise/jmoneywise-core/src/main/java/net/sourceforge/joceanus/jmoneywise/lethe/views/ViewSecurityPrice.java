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
package net.sourceforge.joceanus.jmoneywise.lethe.views;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DilutionEvent.DilutionEventMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * Extension of SecurityPrice to cater for diluted prices.
 * @author Tony Washer
 */
public class ViewSecurityPrice
        extends SecurityPrice {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseViewResource.VIEWPRICE_NAME.getValue();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseViewResource.VIEWPRICE_LIST.getValue();

    /**
     * Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, SecurityPrice.FIELD_DEFS);

    /**
     * Dilution Field Id.
     */
    public static final MetisField FIELD_DILUTION = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DILUTION.getValue(), MetisDataType.DILUTION);

    /**
     * Diluted Price Field Id.
     */
    public static final MetisField FIELD_DILUTEDPRICE = FIELD_DEFS.declareEqualityValueField(MoneyWiseViewResource.VIEWPRICE_DILUTEDPRICE.getValue(), MetisDataType.DILUTION);

    /**
     * Dilution state.
     */
    private DilutionState theDilutionState = DilutionState.UNKNOWN;

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
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Obtain dilution.
     * @return the dilution
     */
    public TethysDilution getDilution() {
        if (theDilutionState.equals(DilutionState.UNKNOWN)) {
            calculateDiluted();
        }
        return getDilution(getValueSet());
    }

    /**
     * Obtain diluted price.
     * @return the diluted price
     */
    public TethysDilutedPrice getDilutedPrice() {
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
    public static TethysDilution getDilution(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DILUTION, TethysDilution.class);
    }

    /**
     * Obtain diluted price.
     * @param pValueSet the valueSet
     * @return the diluted price
     */
    public static TethysDilutedPrice getDilutedPrice(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DILUTEDPRICE, TethysDilutedPrice.class);
    }

    /**
     * Set dilution.
     * @param pValue the dilution
     */
    private void setValueDilution(final TethysDilution pValue) {
        getValueSet().setValue(FIELD_DILUTION, pValue);
    }

    /**
     * Set diluted price.
     * @param pValue the diluted price
     */
    private void setValueDilutedPrice(final TethysDilutedPrice pValue) {
        getValueSet().setValue(FIELD_DILUTEDPRICE, pValue);
    }

    /**
     * Calculate Diluted values.
     */
    protected final void calculateDiluted() {
        /* Ignore if undiluted */
        if (theDilutionState.equals(DilutionState.UNDILUTED)) {
            return;
        }

        /* Access the list for the item */
        final ViewSecurityPriceList myList = (ViewSecurityPriceList) getList();

        /* Set null default dilution */
        setValueDilution(null);
        setValueDilutedPrice(null);

        /* Access Price details */
        final TethysDate myDate = getDate();
        final TethysPrice myPrice = getPrice();
        final Security mySecurity = getSecurity();

        /* Ignore if we have no details */
        if ((myDate == null) || (myPrice == null)) {
            return;
        }

        /* Obtain dilutions */
        final DilutionEventMap myDilutions = myList.getDilutions();

        /* If we are unsure about dilutions check for them */
        if (theDilutionState.equals(DilutionState.UNKNOWN)
            && !myDilutions.hasDilution(mySecurity)) {
            theDilutionState = DilutionState.UNDILUTED;
            return;
        }

        /* Determine the dilution factor for the date */
        final TethysDilution myDilution = myDilutions.getDilutionFactor(mySecurity, myDate);

        /* If we have a dilution factor */
        if (myDilution != null) {
            /* Store dilution details */
            setValueDilution(myDilution);
            setValueDilutedPrice(myPrice.getDilutedPrice(myDilution));
        }

        /* Note dilution state */
        theDilutionState = DilutionState.DILUTED;
    }

    @Override
    public void setPrice(final TethysPrice pPrice) throws OceanusException {
        super.setPrice(pPrice);
        calculateDiluted();
    }

    @Override
    public void setDate(final TethysDate pDate) {
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
        private static final MetisFieldSet<ViewSecurityPriceList> FIELD_DEFS = MetisFieldSet.newFieldSet(ViewSecurityPriceList.class);

        /*
         * The fields.
         */
        static {
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_DILUTIONS, ViewSecurityPriceList::getDilutions);
        }

        /**
         * Dilutions list.
         */
        private final DilutionEventMap theDilutions;

        /**
         * Construct an edit extract of a Price list.
         * @param pView The master view
         * @param pUpdateSet the updateSet
         * @throws OceanusException on error
         */
        public ViewSecurityPriceList(final View pView,
                                     final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
            /* Declare the data and set the style */
            super(pView.getData(), ViewSecurityPrice.class, MoneyWiseDataType.SECURITYPRICE);
            setStyle(ListStyle.EDIT);
            ensureMap();

            /* Access the base prices */
            final SecurityPriceList myPrices = getDataSet().getSecurityPrices();
            setBase(myPrices);

            /* Store dilution list and record whether we have dilutions */
            theDilutions = pView.getDilutions();

            /* Loop through the list */
            final Iterator<SecurityPrice> myIterator = myPrices.iterator();
            while (myIterator.hasNext()) {
                final SecurityPrice myCurr = myIterator.next();

                /* Copy the item */
                final ViewSecurityPrice myItem = new ViewSecurityPrice(this, myCurr);
                myItem.resolveUpdateSetLinks(pUpdateSet);
                add(myItem);

                /* Adjust the map */
                myItem.adjustMapForItem();
            }
        }

        @Override
        public MetisFieldSet<ViewSecurityPriceList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return ViewSecurityPrice.FIELD_DEFS;
        }

        @Override
        protected ViewSecurityPriceList getEmptyList(final ListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Obtain dilutions.
         * @return the dilutions
         */
        private DilutionEventMap getDilutions() {
            return theDilutions;
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
            final ViewSecurityPrice myPrice = new ViewSecurityPrice(this);
            add(myPrice);
            return myPrice;
        }

        @Override
        public ViewSecurityPrice addValuesItem(final DataValues<MoneyWiseDataType> pValues) {
            throw new UnsupportedOperationException();
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
