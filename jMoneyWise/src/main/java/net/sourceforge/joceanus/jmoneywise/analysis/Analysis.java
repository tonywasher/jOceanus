/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFieldValue;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisMaps.AccountRateMap;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisMaps.SecurityPriceMap;
import net.sourceforge.joceanus.jmoneywise.analysis.ChargeableEvent.ChargeableEventList;
import net.sourceforge.joceanus.jmoneywise.analysis.DilutionEvent.DilutionEventList;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxCategoryBucket.TaxCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;

/**
 * Data Analysis.
 * @author Tony Washer
 */
public class Analysis
        implements JDataContents {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Analysis.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Range Field Id.
     */
    private static final JDataField FIELD_RANGE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataRange"));

    /**
     * AccountBuckets Field Id.
     */
    private static final JDataField FIELD_ACCOUNTS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataAccounts"));

    /**
     * SecurityBuckets Field Id.
     */
    private static final JDataField FIELD_SECURITIES = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataSecurities"));

    /**
     * PayeeBuckets Field Id.
     */
    private static final JDataField FIELD_PAYEES = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataPayees"));

    /**
     * PortfolioBuckets Field Id.
     */
    private static final JDataField FIELD_PORTFOLIOS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataPortfolios"));

    /**
     * AccountCategoryBuckets Field Id.
     */
    private static final JDataField FIELD_ACTCATS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataAccountCat"));

    /**
     * EventCategoryBuckets Field Id.
     */
    private static final JDataField FIELD_EVTCATS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataEventCat"));

    /**
     * TaxCategoryBuckets Field Id.
     */
    private static final JDataField FIELD_TAXCATS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataTaxCat"));

    /**
     * Prices Field Id.
     */
    private static final JDataField FIELD_PRICES = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataPrices"));

    /**
     * Rates Field Id.
     */
    private static final JDataField FIELD_RATES = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataRates"));

    /**
     * Charges Field Id.
     */
    private static final JDataField FIELD_CHARGES = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataCharges"));

    /**
     * Dilutions Field Id.
     */
    private static final JDataField FIELD_DILUTIONS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataDilutions"));

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_RANGE.equals(pField)) {
            return theDateRange;
        }
        if (FIELD_ACCOUNTS.equals(pField)) {
            return (theAccounts.size() > 0)
                    ? theAccounts
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_SECURITIES.equals(pField)) {
            return (theSecurities.size() > 0)
                    ? theSecurities
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_PAYEES.equals(pField)) {
            return (thePayees.size() > 0)
                    ? thePayees
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_PORTFOLIOS.equals(pField)) {
            return (thePortfolios.size() > 0)
                    ? thePortfolios
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ACTCATS.equals(pField)) {
            return ((theAccountCategories != null) && (theAccountCategories.size() > 0))
                    ? theAccountCategories
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_EVTCATS.equals(pField)) {
            return (theEventCategories.size() > 0)
                    ? theEventCategories
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXCATS.equals(pField)) {
            return ((theTaxCategories != null) && (theTaxCategories.size() > 0))
                    ? theTaxCategories
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_PRICES.equals(pField)) {
            return (thePrices.size() > 0)
                    ? thePrices
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_RATES.equals(pField)) {
            return (theRates.size() > 0)
                    ? theRates
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_CHARGES.equals(pField)) {
            return (theCharges.size() > 0)
                    ? theCharges
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_DILUTIONS.equals(pField)) {
            return (theDilutions.size() > 0)
                    ? theDilutions
                    : JDataFieldValue.SkipField;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The DataSet.
     */
    private final FinanceData theData;

    /**
     * The DataRange.
     */
    private final JDateDayRange theDateRange;

    /**
     * The account buckets.
     */
    private final AccountBucketList theAccounts;

    /**
     * The security buckets.
     */
    private final SecurityBucketList theSecurities;

    /**
     * The payee buckets.
     */
    private final PayeeBucketList thePayees;

    /**
     * The portfolio buckets.
     */
    private final PortfolioBucketList thePortfolios;

    /**
     * The account category buckets.
     */
    private final AccountCategoryBucketList theAccountCategories;

    /**
     * The event category buckets.
     */
    private final EventCategoryBucketList theEventCategories;

    /**
     * The tax category buckets.
     */
    private final TaxCategoryBucketList theTaxCategories;

    /**
     * The prices.
     */
    private final SecurityPriceMap thePrices;

    /**
     * The rates.
     */
    private final AccountRateMap theRates;

    /**
     * The charges.
     */
    private final ChargeableEventList theCharges;

    /**
     * The dilutions.
     */
    private final DilutionEventList theDilutions;

    /**
     * Obtain the data.
     * @return the data
     */
    public FinanceData getData() {
        return theData;
    }

    /**
     * Obtain the date range.
     * @return the date range
     */
    public JDateDayRange getDateRange() {
        return theDateRange;
    }

    /**
     * Is this a ranged analysis?
     * @return true/false
     */
    public boolean isRangedAnalysis() {
        return (theDateRange.getStart() == null);
    }

    /**
     * Obtain the account buckets list.
     * @return the list
     */
    public AccountBucketList getAccounts() {
        return theAccounts;
    }

    /**
     * Obtain the security buckets list.
     * @return the list
     */
    public SecurityBucketList getSecurities() {
        return theSecurities;
    }

    /**
     * Obtain the payee buckets list.
     * @return the list
     */
    public PayeeBucketList getPayees() {
        return thePayees;
    }

    /**
     * Obtain the portfolio buckets list.
     * @return the list
     */
    public PortfolioBucketList getPortfolios() {
        return thePortfolios;
    }

    /**
     * Obtain the account categories list.
     * @return the list
     */
    public AccountCategoryBucketList getAccountCategories() {
        return theAccountCategories;
    }

    /**
     * Obtain the event categories list.
     * @return the list
     */
    public EventCategoryBucketList getEventCategories() {
        return theEventCategories;
    }

    /**
     * Obtain the tax categories list.
     * @return the list
     */
    public TaxCategoryBucketList getTaxCategories() {
        return theTaxCategories;
    }

    /**
     * Obtain the prices.
     * @return the prices
     */
    public SecurityPriceMap getPrices() {
        return thePrices;
    }

    /**
     * Obtain the rates.
     * @return the rates
     */
    public AccountRateMap getRates() {
        return theRates;
    }

    /**
     * Obtain the charges.
     * @return the charges
     */
    public ChargeableEventList getCharges() {
        return theCharges;
    }

    /**
     * Obtain the dilutions.
     * @return the dilutions
     */
    public DilutionEventList getDilutions() {
        return theDilutions;
    }

    /**
     * Constructor for a full analysis.
     * @param pData the data to analyse events for
     */
    protected Analysis(final FinanceData pData) {
        /* Store the data */
        theData = pData;
        theDateRange = theData.getDateRange();

        /* Create a new set of buckets */
        theAccounts = new AccountBucketList(this);
        theSecurities = new SecurityBucketList(this);
        thePayees = new PayeeBucketList(this);
        theEventCategories = new EventCategoryBucketList(this);
        theTaxCategories = null;

        /* Create totalling buckets */
        thePortfolios = new PortfolioBucketList(this);
        theAccountCategories = new AccountCategoryBucketList(this);

        /* Create the Dilution/Chargeable Event List */
        theCharges = new ChargeableEventList();
        theDilutions = new DilutionEventList(theData);

        /* Create the security price map */
        thePrices = new SecurityPriceMap(theData);

        /* Create the account rate map */
        theRates = new AccountRateMap(theData);

        /* Add opening balances */
        addOpeningBalances();
    }

    /**
     * Constructor for a dated analysis.
     * @param pSource the base analysis
     * @param pDate the date for the analysis
     */
    protected Analysis(final Analysis pSource,
                       final JDateDay pDate) {
        /* Store the data */
        theData = pSource.getData();
        theDateRange = new JDateDayRange(null, pDate);

        /* Access the underlying maps/lists */
        thePrices = pSource.getPrices();
        theRates = pSource.getRates();
        theCharges = pSource.getCharges();
        theDilutions = pSource.getDilutions();

        /* Create a new set of buckets */
        theAccounts = new AccountBucketList(this, pSource.getAccounts(), pDate);
        theSecurities = new SecurityBucketList(this, pSource.getSecurities(), pDate);
        thePayees = new PayeeBucketList(this, pSource.getPayees(), pDate);
        theEventCategories = new EventCategoryBucketList(this, pSource.getEventCategories(), pDate);
        theTaxCategories = null;

        /* Create totalling buckets */
        thePortfolios = new PortfolioBucketList(this);
        theAccountCategories = new AccountCategoryBucketList(this);
    }

    /**
     * Constructor for a ranged analysis.
     * @param pSource the base analysis
     * @param pRange the range for the analysis
     */
    protected Analysis(final Analysis pSource,
                       final JDateDayRange pRange) {
        /* Store the data */
        theData = pSource.getData();
        theDateRange = pRange;

        /* Access the underlying maps/lists */
        thePrices = pSource.getPrices();
        theRates = pSource.getRates();
        theCharges = pSource.getCharges();
        theDilutions = pSource.getDilutions();

        /* Create a new set of buckets */
        theAccounts = new AccountBucketList(this, pSource.getAccounts(), pRange);
        theSecurities = new SecurityBucketList(this, pSource.getSecurities(), pRange);
        thePayees = new PayeeBucketList(this, pSource.getPayees(), pRange);
        theEventCategories = new EventCategoryBucketList(this, pSource.getEventCategories(), pRange);
        theTaxCategories = null;

        /* Create totalling buckets */
        thePortfolios = new PortfolioBucketList(this);
        theAccountCategories = new AccountCategoryBucketList(this);
    }

    /**
     * Add opening balances for accounts.
     */
    private void addOpeningBalances() {
        /* Iterate through the accounts */
        Iterator<Account> myIterator = theData.getAccounts().iterator();
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();

            /* If the account has an opening balance */
            JMoney myBalance = myAccount.getOpeningBalance();
            if (myBalance != null) {
                /* Obtain the actual account bucket */
                AccountBucket myBucket = theAccounts.getBucket(myAccount);
                myBucket.setOpeningBalance(myBalance);
            }
        }
    }
}
