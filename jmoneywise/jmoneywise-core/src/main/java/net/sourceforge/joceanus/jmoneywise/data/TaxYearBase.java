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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedListIterator;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;

/**
 * Tax Year Class representing taxation parameters for a tax year.
 * @author Tony Washer
 * @param <T> the dataType
 */
public abstract class TaxYearBase<T extends TaxYearBase<T>>
        extends DataItem<MoneyWiseDataType>
        implements Comparable<T> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxYearBase.class.getSimpleName();

    /**
     * Local Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    /**
     * TaxYear field Id.
     */
    public static final MetisField FIELD_TAXYEAR = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataType.TAXYEAR.getItemName());

    /**
     * DateRange field Id.
     */
    public static final MetisField FIELD_DATERANGE = FIELD_DEFS.declareDerivedValueField(MoneyWiseDataResource.MONEYWISEDATA_RANGE.getValue());

    /**
     * TaxRegime field Id.
     */
    public static final MetisField FIELD_REGIME = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.TAXREGIME.getItemName());

    /**
     * Bad Date Error Text.
     */
    private static final String ERROR_BADDATE = MoneyWiseDataResource.TAXYEAR_ERROR_BADDATE.getValue();

    /**
     * Copy constructor.
     * @param pList The List to build into
     * @param pTaxYear The TaxYear to copy
     */
    protected TaxYearBase(final TaxYearBaseList<T> pList,
                          final TaxYearBase<T> pTaxYear) {
        super(pList, pTaxYear);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    protected TaxYearBase(final TaxYearBaseList<T> pList,
                          final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Store the Year */
            Object myValue = pValues.getValue(FIELD_TAXYEAR);
            if (myValue instanceof TethysDate) {
                setValueTaxYear((TethysDate) myValue);
            } else if (myValue instanceof String) {
                MetisDataFormatter myFormatter = getDataSet().getDataFormatter();
                TethysDateFormatter myParser = myFormatter.getDateFormatter();
                setValueTaxYear(myParser.parseDateDay((String) myValue));
            }

            /* Store the Regime */
            myValue = pValues.getValue(FIELD_REGIME);
            if (myValue instanceof Integer) {
                setValueTaxRegime((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueTaxRegime((String) myValue);
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Edit constructor.
     * @param pList the list
     */
    protected TaxYearBase(final DataList<T, MoneyWiseDataType> pList) {
        super(pList, 0);
    }

    @Override
    public String formatObject() {
        return toString();
    }

    @Override
    public String toString() {
        return Integer.toString(getTaxYear().getYear());
    }

    @Override
    public boolean includeXmlField(final MetisField pField) {
        /* Determine whether fields should be included */
        if (FIELD_TAXYEAR.equals(pField)) {
            return true;
        }
        if (FIELD_REGIME.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain TaxYear.
     * @return the taxYear date
     */
    public TethysDate getTaxYear() {
        return getTaxYear(getValueSet());
    }

    /**
     * Obtain Date range.
     * @return the taxYear range
     */
    public TethysDateRange getDateRange() {
        return getDateRange(getValueSet());
    }

    /**
     * Obtain TaxRegime.
     * @return the taxRegime
     */
    public TaxRegime getTaxRegime() {
        return getTaxRegime(getValueSet());
    }

    /**
     * Obtain TaxRegimeId.
     * @return the taxRegimeId
     */
    public Integer getTaxRegimeId() {
        TaxRegime myRegime = getTaxRegime();
        return (myRegime == null)
                                  ? null
                                  : myRegime.getId();
    }

    /**
     * Obtain TaxRegimeName.
     * @return the taxRegimeName
     */
    public String getTaxRegimeName() {
        TaxRegime myRegime = getTaxRegime();
        return (myRegime == null)
                                  ? null
                                  : myRegime.getName();
    }

    /**
     * Obtain TaxYear date.
     * @param pValueSet the valueSet
     * @return the date
     */
    public static TethysDate getTaxYear(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TAXYEAR, TethysDate.class);
    }

    /**
     * Obtain date range.
     * @param pValueSet the valueSet
     * @return the date range
     */
    public static TethysDateRange getDateRange(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATERANGE, TethysDateRange.class);
    }

    /**
     * Obtain TaxRegime.
     * @param pValueSet the valueSet
     * @return the regime
     */
    public static TaxRegime getTaxRegime(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_REGIME, TaxRegime.class);
    }

    /**
     * Do we have a low Salary Band?
     * @return true/false
     */
    public boolean hasLoSalaryBand() {
        return getTaxRegime().hasLoSalaryBand();
    }

    /**
     * Do we have a additional Tax Band?
     * @return true/false
     */
    public boolean hasAdditionalTaxBand() {
        return getTaxRegime().hasAdditionalTaxBand();
    }

    /**
     * Do we treat Capital Gains as Income?
     * @return true/false
     */
    public boolean hasCapitalGainsAsIncome() {
        return getTaxRegime().hasCapitalGainsAsIncome();
    }

    /**
     * Determine whether this tax year supports a savings allowance.
     * @return <code>true/false</code>
     */
    public boolean hasSavingsAllowance() {
        return getTaxRegime().hasSavingsAllowance();
    }

    /**
     * Determine whether this tax year supports age related allowances.
     * @return <code>true/false</code>
     */
    public boolean hasAgeRelatedAllowance() {
        return getTaxRegime().hasAgeRelatedAllowance();
    }

    /**
     * Determine whether this tax year splits out residential capital gains.
     * @return <code>true/false</code>
     */
    public boolean hasResidentialCapitalGains() {
        return getTaxRegime().hasResidentialCapitalGains();
    }

    /**
     * Set Tax Year value.
     * @param pValue the value
     */
    private void setValueTaxYear(final TethysDate pValue) {
        getValueSet().setValue(FIELD_TAXYEAR, pValue);
        TethysDateRange myRange = (pValue != null)
                                                   ? deriveRange(pValue)
                                                   : null;
        getValueSet().setValue(FIELD_DATERANGE, myRange);
    }

    /**
     * Set Tax Regime value.
     * @param pValue the value
     */
    private void setValueTaxRegime(final TaxRegime pValue) {
        getValueSet().setValue(FIELD_REGIME, pValue);
    }

    /**
     * Set Tax Regime id.
     * @param pId the id
     */
    private void setValueTaxRegime(final Integer pId) {
        getValueSet().setValue(FIELD_REGIME, pId);
    }

    /**
     * Set Tax Regime.
     * @param pName the name
     */
    private void setValueTaxRegime(final String pName) {
        getValueSet().setValue(FIELD_REGIME, pName);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public TaxYearBaseList<?> getList() {
        return (TaxYearBaseList<?>) super.getList();
    }

    /**
     * Derive range for a Tax Year.
     * @param pLastDate the last date of the tax year
     * @return the range for the tax year
     */
    private static TethysDateRange deriveRange(final TethysDate pLastDate) {
        /* Access start date */
        TethysDate myStart = new TethysDate(pLastDate);

        /* Move back to start of year */
        myStart.adjustYear(-1);
        myStart.adjustDay(1);

        /* Create the range */
        return new TethysDateRange(myStart, pLastDate);
    }

    @Override
    public int compareTo(final T pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If the dates differ */
        int iDiff = MetisDifference.compareObject(getTaxYear(), pThat.getTaxYear());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_REGIME, myData.getTaxRegimes());
    }

    /**
     * Validate the taxYear.
     */
    @Override
    public void validate() {
        /* Access details */
        TethysDate myDate = getTaxYear();
        TaxRegime myTaxRegime = getTaxRegime();
        TaxYearBaseList<?> myList = getList();

        /* The date must not be null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_TAXYEAR);

            /* else we have a date */
        } else {
            /* The date must be unique */
            if (myList.countInstances(myDate) > 1) {
                addError(ERROR_DUPLICATE, FIELD_TAXYEAR);
            }

            /* The day and month must be end of taxYear */
            if (!myDate.equals(TethysFiscalYear.UK.endOfYear(myDate))) {
                addError(ERROR_BADDATE, FIELD_TAXYEAR);
            }
        }

        /* TaxRegime must be non-null */
        if (myTaxRegime == null) {
            addError(ERROR_MISSING, FIELD_REGIME);
        } else if (!myTaxRegime.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_REGIME);
        }
    }

    /**
     * Set a new tax regime.
     * @param pTaxYear the TaxYear
     */
    protected void setTaxYear(final TethysDate pTaxYear) {
        setValueTaxYear(pTaxYear);
    }

    /**
     * Set a new tax regime.
     * @param pTaxRegime the TaxRegime
     */
    public void setTaxRegime(final TaxRegime pTaxRegime) {
        setValueTaxRegime(pTaxRegime);
    }

    @Override
    public void touchUnderlyingItems() {
        /* mark the tax regime referred to */
        getTaxRegime().touchItem(this);
    }

    /**
     * Update taxYear from a taxYear extract.
     * @param pTaxYear the changed taxYear
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pTaxYear) {
        /* Can only update from TaxYear */
        if (!(pTaxYear instanceof TaxYearBase)) {
            return false;
        }

        /* Access as TaxYear */
        TaxYearBase<?> myTaxYear = (TaxYearBase<?>) pTaxYear;

        /* Store the current detail into history */
        pushHistory();

        /* Update the tax regime if required */
        if (!MetisDifference.isEqual(getTaxRegime(), myTaxYear.getTaxRegime())) {
            setTaxRegime(myTaxYear.getTaxRegime());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Tax Year List class.
     * @param <T> the dataType
     */
    public abstract static class TaxYearBaseList<T extends TaxYearBase<T>>
            extends DataList<T, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(TaxYearBaseList.class.getSimpleName(), DataList.FIELD_DEFS);

        /**
         * Construct an empty CORE TaxYear list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected TaxYearBaseList(final MoneyWiseData pData,
                                  final Class<T> pClass,
                                  final MoneyWiseDataType pItemType) {
            super(pClass, pData, pItemType, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected TaxYearBaseList(final TaxYearBaseList<T> pSource) {
            super(pSource);
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Search for the tax year that encompasses this date.
         * @param pDate Date of item
         * @return The TaxYear if present (or null)
         */
        public T findTaxYearForDate(final TethysDate pDate) {
            /* Loop through the items to find the entry */
            Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();

                /* Access the range for this tax year */
                TethysDateRange myRange = myCurr.getDateRange();

                /* Determine whether the date is owned by the tax year */
                int iDiff = myRange.compareTo(pDate);
                if (iDiff == 0) {
                    return myCurr;
                }
            }

            /* Return to caller */
            return null;
        }

        /**
         * Match a range.
         * @param pRange the date range
         * @return the matching TaxYear or null
         */
        public T matchRange(final TethysDateRange pRange) {
            /* Loop through the items to find the entry */
            Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();

                /* Ignore deleted items */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Access the range for this tax year */
                TethysDateRange myRange = myCurr.getDateRange();

                /* Determine whether the range matches the tax year */
                int iDiff = myRange.compareTo(pRange);
                if (iDiff == 0) {
                    return myCurr;
                }
            }

            /* Return to caller */
            return null;
        }

        /**
         * Count the instances of a date.
         * @param pDate the date
         * @return The Item if present (or null)
         */
        protected int countInstances(final TethysDate pDate) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();

                /* Ignore deleted items */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Adjust count */
                int iDiff = pDate.compareTo(myCurr.getTaxYear());
                if (iDiff == 0) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Extract the date range represented by the tax years.
         * @return the range of tax years
         */
        public TethysDateRange getRange() {
            /* Access the iterator */
            MetisOrderedListIterator<T> myIterator = listIterator();
            TethysDate myStart = null;
            TethysDate myEnd = null;

            /* Extract the first item */
            T myCurr = myIterator.peekFirst();
            if (myCurr != null) {
                /* Access start date */
                myStart = myCurr.getDateRange().getStart();

                /* Extract the last item */
                myCurr = myIterator.peekLast();
                myEnd = myCurr.getTaxYear();
            }

            /* Create the range */
            return new TethysDateRange(myStart, myEnd);
        }
    }
}
