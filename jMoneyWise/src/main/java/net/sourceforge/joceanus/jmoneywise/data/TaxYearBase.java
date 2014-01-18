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

import java.util.Calendar;
import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.joceanus.jsortedlist.OrderedListIterator;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Tax Year Class representing taxation parameters for a tax year.
 * @author Tony Washer
 */
public abstract class TaxYearBase
        extends DataItem
        implements Comparable<TaxYearBase> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxYearBase.class.getSimpleName();

    /**
     * TaxYear end of month day.
     */
    public static final int END_OF_MONTH_DAY = 5;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(TaxYearBase.class.getName());

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * TaxYear field Id.
     */
    public static final JDataField FIELD_TAXYEAR = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataTaxYear"));

    /**
     * DateRange field Id.
     */
    public static final JDataField FIELD_DATERANGE = FIELD_DEFS.declareDerivedValueField(NLS_BUNDLE.getString("DataRange"));

    /**
     * TaxRegime field Id.
     */
    public static final JDataField FIELD_REGIME = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataRegime"));

    /**
     * Bad Date Error Text.
     */
    private static final String ERROR_BADDATE = NLS_BUNDLE.getString("ErrorBadDate");

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
     * Obtain Date range.
     * @return the taxYear range
     */
    public JDateDayRange getDateRange() {
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
    public static JDateDay getTaxYear(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TAXYEAR, JDateDay.class);
    }

    /**
     * Obtain date range.
     * @param pValueSet the valueSet
     * @return the date range
     */
    public static JDateDayRange getDateRange(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATERANGE, JDateDayRange.class);
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
        JDateDayRange myRange = (pValue != null)
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
    private JDateDayRange deriveRange(final JDateDay pLastDate) {
        /* Access start date */
        JDateDay myStart = new JDateDay(pLastDate);

        /* Move back to start of year */
        myStart.adjustYear(-1);
        myStart.adjustDay(1);

        /* Create the range */
        return new JDateDayRange(myStart, pLastDate);
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
     * @param pId the id
     * @param pRegimeId the regime id
     * @param pDate the date
     * @throws JOceanusException on error
     */
    protected TaxYearBase(final TaxYearBaseList<? extends TaxYearBase> pList,
                          final int pId,
                          final int pRegimeId,
                          final JDateDay pDate) throws JOceanusException {
        /* Initialise item */
        super(pList, pId);

        /* Record the Id */
        setValueTaxRegime(pRegimeId);
        setValueTaxYear(pDate);
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param pId the id
     * @param pRegime the tax regime
     * @param pDate the date
     * @throws JOceanusException on error
     */
    protected TaxYearBase(final TaxYearBaseList<? extends TaxYearBase> pList,
                          final int pId,
                          final String pRegime,
                          final JDateDay pDate) throws JOceanusException {
        /* Initialise item */
        super(pList, pId);

        /* Record the details */
        setValueTaxRegime(pRegime);
        setValueTaxYear(pDate);
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
    public void resolveDataSetLinks() throws JOceanusException {
        /* Access Relevant lists */
        MoneyWiseData myData = getDataSet();
        TaxRegimeList myRegimes = myData.getTaxRegimes();
        ValueSet myValues = getValueSet();

        /* Adjust Tax Regime */
        Object myRegime = myValues.getValue(FIELD_REGIME);
        if (myRegime instanceof TaxRegime) {
            myRegime = ((TaxRegime) myRegime).getId();
        }
        if (myRegime instanceof Integer) {
            TaxRegime myReg = myRegimes.findItemById((Integer) myRegime);
            if (myReg == null) {
                addError(ERROR_UNKNOWN, FIELD_REGIME);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueTaxRegime(myReg);
        } else if (myRegime instanceof String) {
            TaxRegime myReg = myRegimes.findItemByName((String) myRegime);
            if (myReg == null) {
                addError(ERROR_UNKNOWN, FIELD_REGIME);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueTaxRegime(myReg);
        }
    }

    /**
     * Validate the taxYear.
     */
    @Override
    public void validate() {
        /* Access details */
        JDateDay myDate = getTaxYear();
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

            /* The day and month must be 5th April */
            if ((myDate.getDay() != END_OF_MONTH_DAY)
                || (myDate.getMonth() != Calendar.APRIL)) {
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
    public boolean applyChanges(final DataItem pTaxYear) {
        /* Can only update from TaxYear */
        if (!(pTaxYear instanceof TaxYearBase)) {
            return false;
        }

        /* Access as TaxYear */
        TaxYearBase myTaxYear = (TaxYearBase) pTaxYear;

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
    public abstract static class TaxYearBaseList<T extends TaxYearBase>
            extends DataList<T> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), DataList.FIELD_DEFS);

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE TaxYear list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         */
        protected TaxYearBaseList(final MoneyWiseData pData,
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
            /* Loop through the items to find the entry */
            Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();

                /* Access the range for this tax year */
                JDateDayRange myRange = myCurr.getDateRange();

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
        public T matchRange(final JDateDayRange pRange) {
            /* Loop through the items to find the entry */
            Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();

                /* Access the range for this tax year */
                JDateDayRange myRange = myCurr.getDateRange();

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
                myStart = myCurr.getDateRange().getStart();

                /* Extract the last item */
                myCurr = myIterator.peekLast();
                myEnd = myCurr.getTaxYear();
            }

            /* Create the range */
            return new JDateDayRange(myStart, myEnd);
        }
    }
}
