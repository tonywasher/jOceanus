/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.data;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxRegime;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.jOceanus.jSortedList.OrderedListIterator;

/**
 * Tax Year Class representing taxation parameters for a tax year.
 * @author Tony Washer
 */
public abstract class TaxYearBase extends DataItem implements Comparable<TaxYearBase> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxYearBase.class.getSimpleName();

    /**
     * TaxYear end of month day.
     */
    public static final int END_OF_MONTH_DAY = 5;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    /**
     * TaxYear field Id.
     */
    public static final JDataField FIELD_TAXYEAR = FIELD_DEFS.declareEqualityValueField("TaxYear");

    /**
     * TaxRegime field Id.
     */
    public static final JDataField FIELD_REGIME = FIELD_DEFS.declareEqualityValueField("Regime");

    @Override
    public String formatObject() {
        return toString();
    }

    @Override
    public String toString() {
        return Integer.toString(getTaxYear().getYear());
    }

    /**
     * Obtain TaxYear.
     * @return the taxYear date
     */
    public JDateDay getTaxYear() {
        return getTaxYear(getValueSet());
    }

    /**
     * Obtain TaxRegime.
     * @return the taxRegime
     */
    public TaxRegime getTaxRegime() {
        return getTaxRegime(getValueSet());
    }

    /**
     * Obtain TaxYear date.
     * @param pValueSet the valueSet
     * @return the date
     */
    public static JDateDay getTaxYear(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TAXYEAR, JDateDay.class);
    }

    /**
     * Obtain TaxRegime.
     * @param pValueSet the valueSet
     * @return the regime
     */
    public static TaxRegime getTaxRegime(final ValueSet pValueSet) {
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
     * Set Tax Year value.
     * @param pValue the value
     */
    private void setValueTaxYear(final JDateDay pValue) {
        getValueSet().setValue(FIELD_TAXYEAR, pValue);
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

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    /**
     * Copy constructor.
     * @param pList The List to build into
     * @param pTaxYear The TaxYear to copy
     */
    protected TaxYearBase(final TaxYearBaseList<? extends TaxYearBase> pList,
                          final TaxYearBase pTaxYear) {
        super(pList, pTaxYear);
    }

    /**
     * Secure constructor.
     * @param pList the list
     * @param uId the id
     * @param uRegimeId the regime id
     * @param pDate the date
     * @throws JDataException on error
     */
    protected TaxYearBase(final TaxYearBaseList<? extends TaxYearBase> pList,
                          final int uId,
                          final int uRegimeId,
                          final Date pDate) throws JDataException {
        /* Initialise item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the Id */
            setValueTaxRegime(uRegimeId);
            setValueTaxYear(new JDateDay(pDate));

            /* Look up the Regime */
            FinanceData myDataSet = getDataSet();

            /* Look up the Regime */
            TaxRegimeList myRegimes = myDataSet.getTaxRegimes();
            TaxRegime myRegime = myRegimes.findItemById(uRegimeId);
            if (myRegime == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Tax Regime Id");
            }
            setValueTaxRegime(myRegime);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param uId the id
     * @param pRegime the tax regime
     * @param pDate the date
     * @throws JDataException on error
     */
    protected TaxYearBase(final TaxYearBaseList<? extends TaxYearBase> pList,
                          final int uId,
                          final TaxRegime pRegime,
                          final Date pDate) throws JDataException {
        /* Initialise item */
        super(pList, uId);

        /* Record the details */
        setValueTaxRegime(pRegime);
        setValueTaxYear(new JDateDay(pDate));
    }

    /**
     * Edit constructor.
     * @param pList the list
     */
    protected TaxYearBase(final DataList<TaxYearBase> pList) {
        super(pList, 0);
    }

    @Override
    public int compareTo(final TaxYearBase pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If the dates differ */
        int iDiff = Difference.compareObject(getTaxYear(), pThat.getTaxYear());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    protected void relinkToDataSet() {
        FinanceData myData = getDataSet();
        TaxRegimeList myRegimes = myData.getTaxRegimes();

        /* Update to use the local copy of the TaxRegimes */
        TaxRegime myRegime = getTaxRegime();
        TaxRegime myNewReg = myRegimes.findItemById(myRegime.getId());
        setValueTaxRegime(myNewReg);
    }

    /**
     * Validate the taxYear.
     */
    @Override
    public void validate() {
        /* Access details */
        JDateDay myDate = getTaxYear();
        TaxRegime myTaxRegime = getTaxRegime();
        TaxYearBaseList<?> myList = (TaxYearBaseList<?>) getList();

        /* The date must not be null */
        if (myDate == null) {
            addError("Null date is not allowed", FIELD_TAXYEAR);

            /* else we have a date */
        } else {
            /* The date must be unique */
            if (myList.countInstances(myDate) > 1) {
                addError("Date must be unique", FIELD_TAXYEAR);
            }

            /* The day and month must be 5th April */
            if ((myDate.getDay() != END_OF_MONTH_DAY) || (myDate.getMonth() != Calendar.APRIL)) {
                addError("Date must be 5th April", FIELD_TAXYEAR);
            }
        }

        /* TaxRegime must be non-null */
        if (myTaxRegime == null) {
            addError("TaxRegime must be non-null", FIELD_REGIME);
        } else if (!myTaxRegime.getEnabled()) {
            addError("TaxRegime must be enabled", FIELD_REGIME);
        }
    }

    /**
     * Extract the date range represented by the tax years.
     * @return the range of tax years
     */
    public JDateDayRange getRange() {
        /* Access start date */
        JDateDay myStart = new JDateDay(getTaxYear());

        /* Move back to start of year */
        myStart.adjustYear(-1);
        myStart.adjustDay(1);

        /* Access last date */
        JDateDay myEnd = getTaxYear();

        /* Create the range */
        return new JDateDayRange(myStart, myEnd);
    }

    /**
     * Set a new tax regime.
     * @param pTaxYear the TaxYear
     */
    protected void setTaxYear(final JDateDay pTaxYear) {
        setValueTaxYear(pTaxYear);
    }

    /**
     * Set a new tax regime.
     * @param pTaxRegime the TaxRegime
     */
    public void setTaxRegime(final TaxRegime pTaxRegime) {
        setValueTaxRegime(pTaxRegime);
    }

    /**
     * Mark active items.
     */
    protected void markActiveItems() {
        /* mark the tax regime referred to */
        getTaxRegime().touchItem(this);
    }

    /**
     * Update taxYear from a taxYear extract.
     * @param pTaxYear the changed taxYear
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pTaxYear) {
        /* Can only update from TaxYear */
        if (!(pTaxYear instanceof TaxYearBase)) {
            return false;
        }

        /* Access as TaxYear */
        TaxYearNew myTaxYear = (TaxYearNew) pTaxYear;

        /* Store the current detail into history */
        pushHistory();

        /* Update the tax regime if required */
        if (!Difference.isEqual(getTaxRegime(), myTaxYear.getTaxRegime())) {
            setTaxRegime(myTaxYear.getTaxRegime());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Tax Year List class.
     * @param <T> the dataType
     */
    public abstract static class TaxYearBaseList<T extends TaxYearBase> extends DataList<T> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                TaxYearBaseList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        /**
         * Construct an empty CORE TaxYear list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         */
        protected TaxYearBaseList(final FinanceData pData,
                                  final Class<T> pClass) {
            super(pClass, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected TaxYearBaseList(final TaxYearBaseList<T> pSource) {
            super(pSource);
        }

        /**
         * Search for the tax year that encompasses this date.
         * @param pDate Date of item
         * @return The TaxYear if present (or null)
         */
        public T findTaxYearForDate(final JDateDay pDate) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();
            T myCurr = null;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                myCurr = myIterator.next();

                /* Access the range for this tax year */
                JDateDayRange myRange = myCurr.getRange();

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
         * Count the instances of a date.
         * @param pDate the date
         * @return The Item if present (or null)
         */
        protected int countInstances(final JDateDay pDate) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();
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
        public JDateDayRange getRange() {
            /* Access the iterator */
            OrderedListIterator<T> myIterator = listIterator();
            JDateDay myStart = null;
            JDateDay myEnd = null;

            /* Extract the first item */
            T myCurr = myIterator.peekFirst();
            if (myCurr != null) {
                /* Access start date */
                myStart = new JDateDay(myCurr.getTaxYear());

                /* Move back to start of year */
                myStart.adjustYear(-1);
                myStart.adjustDay(1);

                /* Extract the last item */
                myCurr = myIterator.peekLast();
                myEnd = myCurr.getTaxYear();
            }

            /* Create the range */
            return new JDateDayRange(myStart, myEnd);
        }

        /**
         * Mark active items.
         */
        public void markActiveItems() {
            /* Access the list iterator */
            Iterator<T> myIterator = listIterator();

            /* Loop through the Years */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();

                /* mark the items referred to */
                myCurr.markActiveItems();
            }
        }
    }
}
