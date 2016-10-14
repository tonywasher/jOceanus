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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.CashCategoryBucket.CashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.ChargeableEvent.ChargeableEventList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositCategoryBucket.DepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DilutionEvent.DilutionEventMap;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanCategoryBucket.LoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxCalcBucket.TaxCalcBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionCategoryBucket.TransactionCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionTagBucket.TransactionTagBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxAnalysis;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxYear;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Data Analysis.
 * @author Tony Washer
 */
public class Analysis
        implements MetisDataContents {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * Range Field Id.
     */
    private static final MetisField FIELD_RANGE = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_RANGE.getValue());

    /**
     * Currency Field Id.
     */
    private static final MetisField FIELD_CURRENCY = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.CURRENCY.getItemName());

    /**
     * DepositBuckets Field Id.
     */
    private static final MetisField FIELD_DEPOSITS = FIELD_DEFS.declareLocalField(MoneyWiseDataType.DEPOSIT.getListName());

    /**
     * CashBuckets Field Id.
     */
    private static final MetisField FIELD_CASH = FIELD_DEFS.declareLocalField(MoneyWiseDataType.CASH.getListName());

    /**
     * LoanBuckets Field Id.
     */
    private static final MetisField FIELD_LOANS = FIELD_DEFS.declareLocalField(MoneyWiseDataType.LOAN.getListName());

    /**
     * PayeeBuckets Field Id.
     */
    private static final MetisField FIELD_PAYEES = FIELD_DEFS.declareLocalField(MoneyWiseDataType.PAYEE.getListName());

    /**
     * PortfolioBuckets Field Id.
     */
    private static final MetisField FIELD_PORTFOLIOS = FIELD_DEFS.declareLocalField(MoneyWiseDataType.PORTFOLIO.getListName());

    /**
     * DepositCategoryBuckets Field Id.
     */
    private static final MetisField FIELD_DEPCATS = FIELD_DEFS.declareLocalField(MoneyWiseDataType.DEPOSITCATEGORY.getListName());

    /**
     * CashCategoryBuckets Field Id.
     */
    private static final MetisField FIELD_CASHCATS = FIELD_DEFS.declareLocalField(MoneyWiseDataType.CASHCATEGORY.getListName());

    /**
     * LoanCategoryBuckets Field Id.
     */
    private static final MetisField FIELD_LOANCATS = FIELD_DEFS.declareLocalField(MoneyWiseDataType.LOANCATEGORY.getListName());

    /**
     * TransactionCategoryBuckets Field Id.
     */
    private static final MetisField FIELD_TRANCATS = FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSCATEGORY.getListName());

    /**
     * TransactionTagBuckets Field Id.
     */
    private static final MetisField FIELD_TRANSTAGS = FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSTAG.getListName());

    /**
     * TaxBasisBuckets Field Id.
     */
    private static final MetisField FIELD_TAXBASIS = FIELD_DEFS.declareLocalField(MoneyWiseDataType.TAXBASIS.getListName());

    /**
     * TaxCalcBuckets Field Id.
     */
    private static final MetisField FIELD_TAXCALC = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_TAXCALC.getValue());

    /**
     * NewTaxCalc Field Id.
     */
    private static final MetisField FIELD_TAXCALCNEW = FIELD_DEFS.declareLocalField("NewTax");

    /**
     * ChargeableGains Field Id.
     */
    private static final MetisField FIELD_CHARGES = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_CHARGES.getValue());

    /**
     * Dilutions Field Id.
     */
    private static final MetisField FIELD_DILUTIONS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_DILUTIONS.getValue());

    /**
     * The DataSet.
     */
    private final MoneyWiseData theData;

    /**
     * The Currency.
     */
    private final AssetCurrency theCurrency;

    /**
     * The Preference Manager.
     */
    private final MetisPreferenceManager thePreferences;

    /**
     * The DataRange.
     */
    private final TethysDateRange theDateRange;

    /**
     * The deposit buckets.
     */
    private final DepositBucketList theDeposits;

    /**
     * The cash buckets.
     */
    private final CashBucketList theCash;

    /**
     * The loan buckets.
     */
    private final LoanBucketList theLoans;

    /**
     * The payee buckets.
     */
    private final PayeeBucketList thePayees;

    /**
     * The portfolio buckets.
     */
    private final PortfolioBucketList thePortfolios;

    /**
     * The deposit category buckets.
     */
    private final DepositCategoryBucketList theDepositCategories;

    /**
     * The cash category buckets.
     */
    private final CashCategoryBucketList theCashCategories;

    /**
     * The loan category buckets.
     */
    private final LoanCategoryBucketList theLoanCategories;

    /**
     * The transaction category buckets.
     */
    private final TransactionCategoryBucketList theTransCategories;

    /**
     * The TransactionTag buckets.
     */
    private final TransactionTagBucketList theTransTags;

    /**
     * The tax basis buckets.
     */
    private final TaxBasisBucketList theTaxBasis;

    /**
     * The tax calculations buckets.
     */
    private final TaxCalcBucketList theTaxCalculations;

    /**
     * The new tax calculations.
     */
    private final MoneyWiseTaxAnalysis theTaxAnalysis;

    /**
     * The charges.
     */
    private final ChargeableEventList theCharges;

    /**
     * The dilutions.
     */
    private final DilutionEventMap theDilutions;

    /**
     * The security transactions.
     */
    private final List<Transaction> theSecurities;

    /**
     * Constructor for a full analysis.
     * @param pData the data to analyse events for
     * @param pPreferenceMgr the preference manager
     */
    protected Analysis(final MoneyWiseData pData,
                       final MetisPreferenceManager pPreferenceMgr) {
        /* Store the data */
        theData = pData;
        theCurrency = pData.getDefaultCurrency();
        thePreferences = pPreferenceMgr;
        theDateRange = theData.getDateRange();

        /* Create a new set of buckets */
        theDeposits = new DepositBucketList(this);
        theCash = new CashBucketList(this);
        theLoans = new LoanBucketList(this);
        thePortfolios = new PortfolioBucketList(this);
        thePayees = new PayeeBucketList(this);
        theTaxBasis = new TaxBasisBucketList(this);
        theTransCategories = new TransactionCategoryBucketList(this);
        theTransTags = new TransactionTagBucketList(this);

        /* Create totalling buckets */
        theDepositCategories = new DepositCategoryBucketList(this);
        theCashCategories = new CashCategoryBucketList(this);
        theLoanCategories = new LoanCategoryBucketList(this);
        theTaxCalculations = null;
        theTaxAnalysis = null;

        /* Create the Dilution/Chargeable Event List */
        theCharges = new ChargeableEventList();
        theDilutions = new DilutionEventMap();
        theSecurities = new ArrayList<>();
    }

    /**
     * Constructor for a view analysis.
     * @param pSource the base analysis
     */
    protected Analysis(final Analysis pSource) {
        /* Store the data */
        theData = pSource.getData();
        theCurrency = pSource.getCurrency();
        thePreferences = pSource.getPreferenceMgr();
        theDateRange = pSource.getDateRange();

        /* Access the underlying maps/lists */
        TethysDate myStart = theDateRange.getStart();
        theCharges = new ChargeableEventList();
        theDilutions = myStart == null
                                       ? new DilutionEventMap()
                                       : new DilutionEventMap(pSource.getDilutions(), myStart);
        theSecurities = pSource.getSecurities();

        /* Create a new set of buckets */
        theDeposits = new DepositBucketList(this, pSource.getDeposits());
        theCash = new CashBucketList(this, pSource.getCash());
        theLoans = new LoanBucketList(this, pSource.getLoans());
        thePortfolios = new PortfolioBucketList(this, pSource.getPortfolios());
        thePayees = new PayeeBucketList(this);
        theTaxBasis = new TaxBasisBucketList(this);
        theTransCategories = new TransactionCategoryBucketList(this);
        theTransTags = new TransactionTagBucketList(this);

        /* Create totalling buckets */
        theDepositCategories = new DepositCategoryBucketList(this);
        theCashCategories = new CashCategoryBucketList(this);
        theLoanCategories = new LoanCategoryBucketList(this);
        theTaxCalculations = null;
        theTaxAnalysis = null;
    }

    /**
     * Constructor for a dated analysis.
     * @param pManager the analysis manager
     * @param pDate the date for the analysis
     */
    protected Analysis(final AnalysisManager pManager,
                       final TethysDate pDate) {
        /* Store the data */
        Analysis myBase = pManager.getAnalysis();
        theData = myBase.getData();
        theCurrency = myBase.getCurrency();
        thePreferences = myBase.getPreferenceMgr();
        theDateRange = new TethysDateRange(theData.getDateRange().getStart(), pDate);

        /* Access the underlying maps/lists */
        theCharges = myBase.getCharges();
        theDilutions = myBase.getDilutions();
        theSecurities = myBase.getSecurities();

        /* Create a new set of buckets */
        theDeposits = new DepositBucketList(this, myBase.getDeposits(), pDate);
        theCash = new CashBucketList(this, myBase.getCash(), pDate);
        theLoans = new LoanBucketList(this, myBase.getLoans(), pDate);
        thePortfolios = new PortfolioBucketList(this, myBase.getPortfolios(), pDate);
        thePayees = new PayeeBucketList(this, myBase.getPayees(), pDate);
        theTaxBasis = new TaxBasisBucketList(this, myBase.getTaxBasis(), pDate);
        theTransCategories = new TransactionCategoryBucketList(this, myBase.getTransCategories(), pDate);
        theTransTags = new TransactionTagBucketList(this, myBase.getTransactionTags(), pDate);

        /* Create totalling buckets */
        theDepositCategories = new DepositCategoryBucketList(this);
        theCashCategories = new CashCategoryBucketList(this);
        theLoanCategories = new LoanCategoryBucketList(this);
        theTaxCalculations = null;
        theTaxAnalysis = null;
    }

    /**
     * Constructor for a ranged analysis.
     * @param pManager the analysis manager
     * @param pRange the range for the analysis
     */
    protected Analysis(final AnalysisManager pManager,
                       final TethysDateRange pRange) {
        /* Store the data */
        Analysis myBase = pManager.getAnalysis();
        theData = myBase.getData();
        theCurrency = myBase.getCurrency();
        thePreferences = myBase.getPreferenceMgr();
        theDateRange = pRange;

        /* Access the underlying maps/lists */
        theCharges = new ChargeableEventList(myBase.getCharges(), pRange);
        theDilutions = myBase.getDilutions();
        theSecurities = myBase.getSecurities();

        /* Create a new set of buckets */
        theDeposits = new DepositBucketList(this, myBase.getDeposits(), pRange);
        theCash = new CashBucketList(this, myBase.getCash(), pRange);
        theLoans = new LoanBucketList(this, myBase.getLoans(), pRange);
        thePortfolios = new PortfolioBucketList(this, myBase.getPortfolios(), pRange);
        thePayees = new PayeeBucketList(this, myBase.getPayees(), pRange);
        theTaxBasis = new TaxBasisBucketList(this, myBase.getTaxBasis(), pRange);
        theTransCategories = new TransactionCategoryBucketList(this, myBase.getTransCategories(), pRange);
        theTransTags = new TransactionTagBucketList(this, myBase.getTransactionTags(), pRange);

        /* Create totalling buckets */
        theDepositCategories = new DepositCategoryBucketList(this);
        theCashCategories = new CashCategoryBucketList(this);
        theLoanCategories = new LoanCategoryBucketList(this);

        /* Check to see whether this range matches a tax year */
        TaxYearList myTaxYears = theData.getTaxYears();
        TaxYear myTaxYear = myTaxYears.matchRange(theDateRange);

        /* Allocate tax calculations if required */
        if (myTaxYear == null) {
            theTaxCalculations = null;

        } else {
            theTaxCalculations = new TaxCalcBucketList(this, myTaxYear);
        }

        /* Handle new tax calculations */
        MoneyWiseTaxYear myYear = pManager.getTaxYearForRange(theDateRange);
        theTaxAnalysis = myYear != null
                                        ? myYear.analyseTaxYear(thePreferences, theTaxBasis)
                                        : null;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_RANGE.equals(pField)) {
            return theDateRange;
        }
        if (FIELD_CURRENCY.equals(pField)) {
            return theCurrency;
        }
        if (FIELD_DEPOSITS.equals(pField)) {
            return theDeposits.isEmpty()
                                         ? MetisFieldValue.SKIP
                                         : theDeposits;
        }
        if (FIELD_CASH.equals(pField)) {
            return theCash.isEmpty()
                                     ? MetisFieldValue.SKIP
                                     : theCash;
        }
        if (FIELD_LOANS.equals(pField)) {
            return theLoans.isEmpty()
                                      ? MetisFieldValue.SKIP
                                      : theLoans;
        }
        if (FIELD_PORTFOLIOS.equals(pField)) {
            return thePortfolios.isEmpty()
                                           ? MetisFieldValue.SKIP
                                           : thePortfolios;
        }
        if (FIELD_PAYEES.equals(pField)) {
            return thePayees.isEmpty()
                                       ? MetisFieldValue.SKIP
                                       : thePayees;
        }
        if (FIELD_DEPCATS.equals(pField)) {
            return theDepositCategories.isEmpty()
                                                  ? MetisFieldValue.SKIP
                                                  : theDepositCategories;
        }
        if (FIELD_CASHCATS.equals(pField)) {
            return theCashCategories.isEmpty()
                                               ? MetisFieldValue.SKIP
                                               : theCashCategories;
        }
        if (FIELD_LOANCATS.equals(pField)) {
            return theLoanCategories.isEmpty()
                                               ? MetisFieldValue.SKIP
                                               : theLoanCategories;
        }
        if (FIELD_TRANCATS.equals(pField)) {
            return theTransCategories.isEmpty()
                                                ? MetisFieldValue.SKIP
                                                : theTransCategories;
        }
        if (FIELD_TRANSTAGS.equals(pField)) {
            return theTransTags.isEmpty()
                                          ? MetisFieldValue.SKIP
                                          : theTransTags;
        }
        if (FIELD_TAXBASIS.equals(pField)) {
            return theTaxBasis.isEmpty()
                                         ? MetisFieldValue.SKIP
                                         : theTaxBasis;
        }
        if (FIELD_TAXCALC.equals(pField)) {
            return (theTaxCalculations != null) && (!theTaxCalculations.isEmpty())
                                                                                   ? theTaxCalculations
                                                                                   : MetisFieldValue.SKIP;
        }
        if (FIELD_TAXCALCNEW.equals(pField)) {
            return theTaxAnalysis != null
                                          ? theTaxAnalysis
                                          : MetisFieldValue.SKIP;
        }
        if (FIELD_CHARGES.equals(pField)) {
            return theCharges.isEmpty()
                                        ? MetisFieldValue.SKIP
                                        : theCharges;
        }
        if (FIELD_DILUTIONS.equals(pField)) {
            return theDilutions.isEmpty()
                                          ? MetisFieldValue.SKIP
                                          : theDilutions;
        }

        /* Unknown */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * Obtain the data.
     * @return the data
     */
    public MoneyWiseData getData() {
        return theData;
    }

    /**
     * Obtain the currency.
     * @return the currency
     */
    public AssetCurrency getCurrency() {
        return theCurrency;
    }

    /**
     * Obtain the preference manager.
     * @return the data
     */
    public MetisPreferenceManager getPreferenceMgr() {
        return thePreferences;
    }

    /**
     * Obtain the date range.
     * @return the date range
     */
    public TethysDateRange getDateRange() {
        return theDateRange;
    }

    /**
     * Is this a ranged analysis?
     * @return true/false
     */
    public boolean isRangedAnalysis() {
        return theDateRange.getStart() == null;
    }

    /**
     * Obtain the deposit buckets list.
     * @return the list
     */
    public DepositBucketList getDeposits() {
        return theDeposits;
    }

    /**
     * Obtain the cash buckets list.
     * @return the list
     */
    public CashBucketList getCash() {
        return theCash;
    }

    /**
     * Obtain the loan buckets list.
     * @return the list
     */
    public LoanBucketList getLoans() {
        return theLoans;
    }

    /**
     * Obtain the portfolio buckets list.
     * @return the list
     */
    public PortfolioBucketList getPortfolios() {
        return thePortfolios;
    }

    /**
     * Obtain the payee buckets list.
     * @return the list
     */
    public PayeeBucketList getPayees() {
        return thePayees;
    }

    /**
     * Obtain the deposit categories list.
     * @return the list
     */
    public DepositCategoryBucketList getDepositCategories() {
        return theDepositCategories;
    }

    /**
     * Obtain the cash categories list.
     * @return the list
     */
    public CashCategoryBucketList getCashCategories() {
        return theCashCategories;
    }

    /**
     * Obtain the loan categories list.
     * @return the list
     */
    public LoanCategoryBucketList getLoanCategories() {
        return theLoanCategories;
    }

    /**
     * Obtain the transaction categories list.
     * @return the list
     */
    public TransactionCategoryBucketList getTransCategories() {
        return theTransCategories;
    }

    /**
     * Obtain the transactionTag list.
     * @return the list
     */
    public TransactionTagBucketList getTransactionTags() {
        return theTransTags;
    }

    /**
     * Obtain the tax basis list.
     * @return the list
     */
    public TaxBasisBucketList getTaxBasis() {
        return theTaxBasis;
    }

    /**
     * Obtain the tax calculations list.
     * @return the list
     */
    public TaxCalcBucketList getTaxCalculations() {
        return theTaxCalculations;
    }

    /**
     * Obtain the tax analysis
     * @return the analysis
     */
    public MoneyWiseTaxAnalysis getTaxAnalysis() {
        return theTaxAnalysis;
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
    public DilutionEventMap getDilutions() {
        return theDilutions;
    }

    /**
     * Obtain the securities.
     * @return the securities
     */
    public List<Transaction> getSecurities() {
        return theSecurities;
    }

    /**
     * Add opening balances for accounts.
     * @param pHelper the transaction helper
     */
    protected void addOpeningBalances(final TransactionHelper pHelper) {
        /* Iterate through the deposits */
        Iterator<Deposit> myDepIterator = theData.getDeposits().iterator();
        while (myDepIterator.hasNext()) {
            Deposit myDeposit = myDepIterator.next();

            /* If the deposit has an opening balance */
            TethysMoney myBalance = myDeposit.getOpeningBalance();
            if (myBalance != null) {
                /* Obtain the actual deposit bucket */
                DepositBucket myBucket = theDeposits.getBucket(myDeposit);
                myBucket.setOpeningBalance(pHelper, myBalance);
            }
        }

        /* Iterate through the cash */
        Iterator<Cash> myCashIterator = theData.getCash().iterator();
        while (myCashIterator.hasNext()) {
            Cash myCash = myCashIterator.next();

            /* If the cash has an opening balance */
            TethysMoney myBalance = myCash.getOpeningBalance();
            if (myBalance != null) {
                /* Obtain the actual cash bucket */
                CashBucket myBucket = theCash.getBucket(myCash);
                myBucket.setOpeningBalance(pHelper, myBalance);
            }
        }

        /* Iterate through the loans */
        Iterator<Loan> myLoanIterator = theData.getLoans().iterator();
        while (myLoanIterator.hasNext()) {
            Loan myLoan = myLoanIterator.next();

            /* If the loan has an opening balance */
            TethysMoney myBalance = myLoan.getOpeningBalance();
            if (myBalance != null) {
                /* Obtain the actual loan bucket */
                LoanBucket myBucket = theLoans.getBucket(myLoan);
                myBucket.setOpeningBalance(pHelper, myBalance);
            }
        }
    }
}
