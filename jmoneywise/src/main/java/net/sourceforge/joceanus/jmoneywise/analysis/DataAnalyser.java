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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountCategoryBucket.CategoryType;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisMaps.SecurityPriceMap;
import net.sourceforge.joceanus.jmoneywise.analysis.DilutionEvent.DilutionEventList;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice.AccountPriceList;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Class to analyse data.
 * @author Tony Washer
 */
public class DataAnalyser
        implements JDataContents {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DataAnalyser.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Analysis field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataAnalysis"));

    /**
     * Analysis Manager field Id.
     */
    private static final JDataField FIELD_MANAGER = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataManager"));

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_MANAGER.equals(pField)) {
            return theManager;
        }

        /* Unknown */
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The Amount Tax threshold for "small" transactions (£3000).
     */
    private static final JMoney LIMIT_VALUE = JMoney.getWholeUnits(3000);

    /**
     * The Rate Tax threshold for "small" transactions (5%).
     */
    private static final JRate LIMIT_RATE = JRate.getWholePercentage(5);

    /**
     * The dataSet being analysed.
     */
    private final MoneyWiseData theData;

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The analysis manager.
     */
    private final AnalysisManager theManager;

    /**
     * The account bucket list.
     */
    private final AccountBucketList theAccountBuckets;

    /**
     * The security bucket list.
     */
    private final SecurityBucketList theSecurityBuckets;

    /**
     * The payee bucket list.
     */
    private final PayeeBucketList thePayeeBuckets;

    /**
     * The event category buckets.
     */
    private final EventCategoryBucketList theCategoryBuckets;

    /**
     * The taxBasis buckets.
     */
    private final TaxBasisBucketList theTaxBasisBuckets;

    /**
     * The taxMan account.
     */
    private final PayeeBucket theTaxMan;

    /**
     * The dilutions.
     */
    private final DilutionEventList theDilutions;

    /**
     * Obtain the analysis manager.
     * @return the analysis manager
     */
    public AnalysisManager getAnalysisManager() {
        return theManager;
    }

    /**
     * Obtain the dilutions.
     * @return the dilutions
     */
    public DilutionEventList getDilutions() {
        return theDilutions;
    }

    /**
     * Constructor for a full year set of accounts.
     * @param pData the Data to analyse
     * @param pPreferenceMgr the preference manager
     * @throws JOceanusException on error
     */
    public DataAnalyser(final MoneyWiseData pData,
                        final PreferenceManager pPreferenceMgr) throws JOceanusException {
        /* Store the parameters */
        theData = pData;

        /* Access the lists */
        TaxYearList myTaxYears = theData.getTaxYears();
        EventList myEvents = theData.getEvents();

        /* Create a new analysis */
        theAnalysis = new Analysis(theData, pPreferenceMgr);
        theManager = new AnalysisManager(theAnalysis, pPreferenceMgr.getLogger());

        /* Access details from the analysis */
        theAccountBuckets = theAnalysis.getAccounts();
        theSecurityBuckets = theAnalysis.getSecurities();
        thePayeeBuckets = theAnalysis.getPayees();
        theCategoryBuckets = theAnalysis.getEventCategories();
        theTaxBasisBuckets = theAnalysis.getTaxBasis();
        theDilutions = theAnalysis.getDilutions();
        theTaxMan = thePayeeBuckets.getBucket(AccountCategoryClass.TAXMAN);

        /* Access the Event iterator */
        Iterator<Event> myIterator = myEvents.listIterator();
        TaxYear myTax = null;
        JDateDay myDate = null;
        int myResult = -1;

        /* Loop through the Events extracting relevant elements */
        while (myIterator.hasNext()) {
            Event myCurr = myIterator.next();
            JDateDay myCurrDay = myCurr.getDate();

            /* Ignore deleted events */
            if (myCurr.isDeleted()) {
                continue;
            }

            /* If we have a current tax year */
            if (myDate != null) {
                /* Check that this event is still in the tax year */
                myResult = myDate.compareTo(myCurrDay);
            }

            /* If we have exhausted the tax year or else this is the first tax year */
            if (myResult < 0) {
                /* Access the relevant tax year */
                myTax = myTaxYears.findTaxYearForDate(myCurrDay);
                myDate = myTax.getTaxYear();
            }

            /* Touch underlying items */
            myCurr.touchUnderlyingItems();

            /* If the event has a parent */
            Event myParent = myCurr.getParent();
            if (myParent != null) {
                /* Register child against parent */
                myEvents.registerChild(myCurr);
            }

            /* If the event has a dilution factor */
            if (myCurr.getDilution() != null) {
                /* Add to the dilution event list */
                theDilutions.addDilution(myCurr);
            }

            /* Process the event in the report set */
            processEvent(myCurr);

            /* Touch tax year */
            myTax.touchItem(myCurr);
        }

        /* Analyse the basic ranged analysis */
        theManager.analyseBase();
    }

    /**
     * Mark active accounts.
     * @throws JOceanusException on error
     */
    public void markActiveAccounts() throws JOceanusException {
        /* Access the iterator */
        AccountBucketList myAccounts = theAnalysis.getAccounts();
        SecurityBucketList mySecurities = theAnalysis.getSecurities();

        /* Loop through the items to find the match */
        Iterator<AccountBucket> myActIterator = myAccounts.listIterator();
        while (myActIterator.hasNext()) {
            AccountBucket myCurr = myActIterator.next();
            Account myAccount = myCurr.getAccount();

            /* If we are closed */
            if (myAccount.isClosed()) {
                /* Ensure that we have correct closed/maturity dates */
                myAccount.adjustDates();
            }

            /* If we are active */
            if (myCurr.isActive()) {
                /* Set the account as non-close-able */
                myAccount.setNonCloseable();
            }
        }

        /* Loop through the items to find the match */
        Iterator<SecurityBucket> mySecIterator = mySecurities.listIterator();
        while (mySecIterator.hasNext()) {
            SecurityBucket myCurr = mySecIterator.next();
            Account mySecurity = myCurr.getSecurity();

            /* If we are closed */
            if (mySecurity.isClosed()) {
                /* Ensure that we have correct closed/maturity dates */
                mySecurity.adjustDates();
            }

            /* If we are active */
            if (myCurr.isActive()) {
                /* Set the security as non-closeable */
                mySecurity.setNonCloseable();
            }
        }
    }

    /**
     * Process an event.
     * @param pEvent the event to process
     * @throws JOceanusException on error
     */
    private void processEvent(final Event pEvent) throws JOceanusException {
        /* Access key details */
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        Account myChild = null;
        JMoney myAmount = pEvent.getAmount();

        /* If the event relates to a security item, split out the workings */
        if ((myDebit.hasUnits())
            || (myCredit.hasUnits())) {
            /* Process as a Security event */
            processSecurityEvent(pEvent);

            /* Else handle the event normally */
        } else {
            EventCategory myCat = pEvent.getCategory();

            /* Switch on category class */
            switch (myCat.getCategoryTypeClass()) {
                case INTEREST:
                    /* Obtain detailed category */
                    myCat = myDebit.getDetailedCategory(myCat);

                    /* True debit account is the parent */
                    myChild = myDebit.equals(myCredit)
                                                      ? null
                                                      : myDebit;
                    myDebit = myDebit.getParent();
                    break;
                case LOANINTERESTEARNED:
                    /* True debit account is the parent of the loan */
                    myDebit = myDebit.getParent();
                    break;
                case RENTALINCOME:
                case ROOMRENTALINCOME:
                    /* True debit account is the parent of the loan */
                    myChild = myDebit.equals(myCredit)
                                                      ? null
                                                      : myDebit;
                    myDebit = myCredit.getParent();
                    break;
                case WRITEOFF:
                case LOANINTERESTCHARGED:
                    /* True credit account is the parent of the loan */
                    myCredit = myCredit.getParent();
                    break;
                default:
                    break;
            }

            /* If the debit account is auto-Expense */
            EventCategory myAuto = myDebit.getAutoExpense();
            if (myAuto != null) {
                /* Subtract expense from Payee bucket */
                PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                myPayee.subtractExpense(pEvent, myAmount);

                /* Subtract expense from Category bucket */
                EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.subtractExpense(pEvent, myAmount);
                theTaxBasisBuckets.adjustAutoExpense(pEvent, false);

                /* else handle normally */
            } else {
                /* Determine the type of the debit account */
                CategoryType myType = AccountCategoryBucket.determineCategoryType(myDebit.getAccountCategory());
                switch (myType) {
                    case PORTFOLIO:
                    case PAYEE:
                        PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                        myPayee.adjustForDebit(pEvent);
                        break;
                    case MONEY:
                    case CREDITCARD:
                    default:
                        AccountBucket myAccount = theAccountBuckets.getBucket(myDebit);
                        myAccount.adjustForDebit(pEvent);
                        break;
                }
            }

            /* If the credit account is auto-Expense */
            myAuto = myCredit.getAutoExpense();
            if (myAuto != null) {
                /* Add expense to Payee bucket */
                PayeeBucket myPayee = thePayeeBuckets.getBucket(myCredit);
                myPayee.addExpense(pEvent, myAmount);

                /* Adjust the relevant category bucket */
                EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.addExpense(pEvent, myAmount);
                theTaxBasisBuckets.adjustAutoExpense(pEvent, true);

                /* else handle normally */
            } else {
                /* Determine the type of the credit account */
                CategoryType myType = AccountCategoryBucket.determineCategoryType(myCredit.getAccountCategory());
                switch (myType) {
                    case PORTFOLIO:
                    case PAYEE:
                        PayeeBucket myPayee = thePayeeBuckets.getBucket(myCredit);
                        myPayee.adjustForCredit(pEvent);
                        break;
                    case MONEY:
                    case CREDITCARD:
                    default:
                        AccountBucket myAccount = theAccountBuckets.getBucket(myCredit);
                        myAccount.adjustForCredit(pEvent);
                        break;
                }
            }

            /* If we should register the event with a child */
            if (myChild != null) {
                /* Access bucket and register it */
                AccountBucket myAccount = theAccountBuckets.getBucket(myChild);
                myAccount.registerEvent(pEvent);
            }

            /* Adjust the tax payments */
            theTaxMan.adjustForTaxPayments(pEvent);

            /* If the event category is not a transfer */
            if (!myCat.isTransfer()) {
                /* Adjust the relevant category buckets */
                theCategoryBuckets.adjustCategories(pEvent, myCat);
            }
        }
    }

    /**
     * Process a security event.
     * @param pEvent the event to process
     * @throws JOceanusException on error
     */
    private void processSecurityEvent(final Event pEvent) throws JOceanusException {
        /* Switch on the category */
        EventCategory myCat = pEvent.getCategory();
        switch (myCat.getCategoryTypeClass()) {
        /* Process a stock split */
            case STOCKSPLIT:
            case STOCKADJUST:
                processStockSplit(pEvent);
                break;
            /* Process a stock right taken */
            case STOCKRIGHTSTAKEN:
                processTransferIn(pEvent);
                break;
            /* Process a stock right taken */
            case STOCKRIGHTSWAIVED:
                processStockRightWaived(pEvent);
                break;
            /* Process a stock DeMerger */
            case STOCKDEMERGER:
                processStockDeMerger(pEvent);
                break;
            /* Process a Stock TakeOver */
            case STOCKTAKEOVER:
                processStockTakeover(pEvent);
                break;
            /* Process a dividend */
            case DIVIDEND:
                processDividend(pEvent);
                break;
            /* Process standard transfer in/out */
            case TRANSFER:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
                if (pEvent.getDebit().isCategoryClass(AccountCategoryClass.LIFEBOND)) {
                    processTaxableGain(pEvent);
                } else if (!pEvent.getDebit().hasUnits()) {
                    processTransferIn(pEvent);
                } else if (pEvent.getCredit().hasUnits()) {
                    processStockXchange(pEvent);
                } else {
                    processTransferOut(pEvent);
                }
                break;
            /* Throw an Exception */
            default:
                throw new JMoneyWiseLogicException("Unexpected category type: "
                                                   + myCat.getCategoryTypeClass());
        }
    }

    /**
     * Process an event that is a stock split.
     * <p>
     * This capital event relates only to the Credit Account since the debit account is the same.
     * @param pEvent the event
     */
    private void processStockSplit(final Event pEvent) {
        /* Stock split has identical credit/debit so just obtain credit account */
        Account myAccount = pEvent.getCredit();
        JUnits myDelta = pEvent.getCreditUnits();
        if (myDelta == null) {
            myDelta = new JUnits(pEvent.getDebitUnits());
            myDelta.negate();
        }

        /* Adjust the Security Units */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myAccount);
        myAsset.adjustUnits(myDelta);

        /* Register the event */
        myAsset.registerEvent(pEvent);

        /* StockSplit/Adjust is a transfer, so no need to update the categories */
    }

    /**
     * Process an event that is a transfer into capital (also StockRightTaken).
     * <p>
     * This capital event relates only to the Credit Account.
     * @param pEvent the event
     */
    private void processTransferIn(final Event pEvent) {
        /* Access debit account and category */
        Account myDebit = pEvent.getDebit();
        EventCategory myCat = pEvent.getCategory();

        /* Adjust the credit transfer details */
        processCreditXferIn(pEvent);

        /* Adjust the tax payments */
        theTaxMan.adjustForTaxPayments(pEvent);

        /* Determine the type of the debit account */
        CategoryType myType = AccountCategoryBucket.determineCategoryType(myDebit.getAccountCategory());
        switch (myType) {
            case PAYEE:
                PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                myPayee.adjustForDebit(pEvent);
                break;
            case MONEY:
            case CREDITCARD:
            default:
                AccountBucket myAccount = theAccountBuckets.getBucket(myDebit);
                myAccount.adjustForDebit(pEvent);
                break;
        }

        /* If the event category is not a transfer */
        if (!myCat.isTransfer()) {
            /* Adjust the relevant category buckets */
            theCategoryBuckets.adjustCategories(pEvent, myCat);
        }
    }

    /**
     * Process the credit side of a transfer in event.
     * @param pEvent the event
     */
    private void processCreditXferIn(final Event pEvent) {
        /* Transfer is to the credit account and may or may not have a change to the units */
        Account myAccount = pEvent.getCredit();
        JUnits myDeltaUnits = pEvent.getCreditUnits();
        JMoney myAmount = pEvent.getAmount();

        /* Access the Asset Account Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myAccount);

        /* Adjust the cost and investment */
        myAsset.adjustCost(myAmount);
        myAsset.adjustInvested(myAmount);

        /* If we have new units */
        if (myDeltaUnits != null) {
            /* Record change in units */
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* Register the event */
        myAsset.registerEvent(pEvent);
    }

    /**
     * Process a dividend event.
     * <p>
     * This capital event relates to the only to Debit account, although the Credit account may be identical to the credit account in which case the dividend is
     * re-invested
     * @param pEvent the event
     */
    private void processDividend(final Event pEvent) {
        /* The main account that we are interested in is the debit account */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JMoney myAmount = pEvent.getAmount();
        JMoney myTaxCredit = pEvent.getTaxCredit();
        JUnits myDeltaUnits = pEvent.getCreditUnits();

        /* Obtain detailed category */
        EventCategory myCat = myAccount.getDetailedCategory(pEvent.getCategory());

        /* True debit account is the parent */
        Account myDebit = myAccount.getParent();

        /* Adjust the debit payee bucket */
        PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
        myPayee.adjustForDebit(pEvent);

        /* Access the Asset Account Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myAccount);

        /* If this is a re-investment */
        if (myAccount.equals(myCredit)) {
            /* This amount is added to the cost, so record as the delta cost */
            myAsset.adjustCost(myAmount);

            /* Record the investment */
            myAsset.adjustInvested(myAmount);

            /* If we have new units */
            if (myDeltaUnits != null) {
                /* Record delta units */
                myAsset.adjustUnits(myDeltaUnits);
            }

            /* If we have a tax credit */
            if (myTaxCredit != null) {
                /* The Tax Credit is viewed as a received dividend from the account */
                myAsset.adjustDividend(myTaxCredit);
            }

            /* else we are paying out to another account */
        } else {
            /* Adjust the dividend total for this asset */
            JMoney myAdjust = new JMoney(myAmount);

            /* Any tax credit is viewed as a realised dividend from the account */
            if (myTaxCredit != null) {
                myAdjust.addAmount(myTaxCredit);
            }

            /* The Dividend is viewed as a dividend from the account */
            myAsset.adjustDividend(myAdjust);

            /* Adjust the credit account bucket */
            AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
            myBucket.adjustForCredit(pEvent);
        }

        /* Register the event */
        myAsset.registerEvent(pEvent);

        /* Adjust the tax payments */
        theTaxMan.adjustForTaxPayments(pEvent);

        /* Adjust the relevant category buckets */
        theCategoryBuckets.adjustCategories(pEvent, myCat);
    }

    /**
     * Process an event that is a transfer from capital.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processTransferOut(final Event pEvent) {
        /* Access credit account and category */
        Account myCredit = pEvent.getCredit();
        EventCategory myCat = pEvent.getCategory();

        /* Adjust the debit transfer details */
        processDebitXferOut(pEvent);

        /* Adjust the credit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* If the event category is not a transfer */
        if (!myCat.isTransfer()) {
            /* Adjust the relevant category buckets */
            theCategoryBuckets.adjustCategories(pEvent, myCat);
        }
    }

    /**
     * Process the debit side of a transfer out event.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processDebitXferOut(final Event pEvent) {
        /* Transfer out is from the debit account and may or may not have units */
        Account myAccount = pEvent.getDebit();
        JMoney myAmount = pEvent.getAmount();
        JUnits myDeltaUnits = pEvent.getDebitUnits();

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myAccount);
        SecurityValues myValues = myAsset.getValues();

        /* Record the delta investment */
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myAsset.adjustInvested(myDelta);

        /* Assume the the cost reduction is the full value */
        JMoney myReduction = new JMoney(myAmount);
        JMoney myCost = myValues.getMoneyValue(SecurityAttribute.COST);

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
            JUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Determine the delta to the cost */
        JMoney myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustCost(myDeltaCost);
        }

        /* Determine the delta to the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustGains(myDeltaGains);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(pEvent, myDeltaGains);
        }

        /* Register the event */
        myAsset.registerEvent(pEvent);
    }

    /**
     * Process an event that is a exchange between two capital accounts.
     * <p>
     * This represent a transfer out from the debit account and a transfer in to the credit account
     * @param pEvent the event
     */
    private void processStockXchange(final Event pEvent) {
        /* Adjust the debit transfer details */
        processDebitXferOut(pEvent);

        /* Adjust the credit transfer details */
        processCreditXferIn(pEvent);
    }

    /**
     * Process an event that is a taxable gain.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processTaxableGain(final Event pEvent) {
        /* Taxable Gain is from the debit account and may or may not have units */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JMoney myAmount = pEvent.getAmount();
        JUnits myDeltaUnits = pEvent.getDebitUnits();

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myAccount);
        SecurityValues myValues = myAsset.getValues();

        /* Record the delta investment */
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myAsset.adjustInvested(myDelta);

        /* Assume the the cost reduction is the full value */
        JMoney myReduction = new JMoney(myAmount);
        JMoney myCost = myValues.getMoneyValue(SecurityAttribute.COST);

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
            JUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Determine the delta to the cost */
        JMoney myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustCost(myDeltaCost);
        }

        /* Determine the delta to the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustGains(myDeltaGains);
        }

        /* Register the event */
        myAsset.registerEvent(pEvent);

        /* True debit account is the parent */
        Account myDebit = myAccount.getParent();

        /* Adjust the debit account bucket */
        PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
        myPayee.adjustForTaxCredit(pEvent);

        /* Adjust the credit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the taxableGains category bucket */
        theCategoryBuckets.adjustTaxableGain(pEvent, myReduction);

        /* Adjust the TaxMan account for the tax credit */
        theTaxMan.adjustForTaxPayments(pEvent);

        /* Add the chargeable event */
        theAnalysis.getCharges().addEvent(pEvent, myDeltaGains);
    }

    /**
     * Process an event that is stock right waived.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processStockRightWaived(final Event pEvent) {
        /* Stock Right Waived is from the debit account */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        AccountPriceList myPrices = theData.getAccountPrices();
        JMoney myAmount = pEvent.getAmount();
        JMoney myReduction;

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myAccount);
        SecurityValues myValues = myAsset.getValues();

        /* Record the delta investment */
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myAsset.adjustInvested(myDelta);

        /* Access the current cost */
        JMoney myCost = myValues.getMoneyValue(SecurityAttribute.COST);

        /* Get the appropriate price for the account */
        AccountPrice myActPrice = myPrices.getLatestPrice(myAccount, pEvent.getDate());
        JPrice myPrice = myActPrice.getPrice();

        /* Determine value of this stock at the current time */
        JUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
        JMoney myValue = myUnits.valueAtPrice(myPrice);

        /* Calculate the portion of the value that creates a large transaction */
        JMoney myPortion = myValue.valueAtRate(LIMIT_RATE);

        /* If this is a large stock waiver (> both valueLimit and rateLimit of value) */
        if ((myAmount.compareTo(LIMIT_VALUE) > 0)
            && (myAmount.compareTo(myPortion) > 0)) {
            /* Determine the total value of rights plus share value */
            JMoney myTotalValue = new JMoney(myAmount);
            myTotalValue.addAmount(myValue);

            /* Determine the reduction as a proportion of the total value */
            myReduction = myCost.valueAtWeight(myAmount, myTotalValue);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* Set the reduction to be the entire amount */
            myReduction = new JMoney(myAmount);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Calculate the delta cost */
        JMoney myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myAsset.adjustCost(myDeltaCost);

        /* Determine the delta gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have some gains */
        if (myDeltaGains.isNonZero()) {
            /* Record the delta gains */
            myAsset.adjustGains(myDeltaGains);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(pEvent, myDeltaGains);
        }

        /* Register the event */
        myValues = myAsset.registerEvent(pEvent);

        /* Record additional details to the registered event values */
        myValues.setValue(SecurityAttribute.PRICE, myPrice);
        myValues.setValue(SecurityAttribute.VALUATION, myValue);

        /* Adjust the credit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* StockRightWaived is a transfer, so no need to update the categories */
    }

    /**
     * Process an event that is Stock DeMerger.
     * <p>
     * This capital event relates to both the Credit and Debit accounts
     * @param pEvent the event
     */
    private void processStockDeMerger(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JDilution myDilution = pEvent.getDilution();
        JUnits myDeltaUnits = pEvent.getDebitUnits();

        /* Access the Debit Asset Security Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myDebit);
        SecurityValues myValues = myAsset.getValues();

        /* Calculate the diluted value of the Debit account */
        JMoney myCost = myValues.getMoneyValue(SecurityAttribute.COST);
        JMoney myNewCost = myCost.getDilutedMoney(myDilution);

        /* Calculate the delta to the cost */
        JMoney myDeltaCost = new JMoney(myNewCost);
        myDeltaCost.subtractAmount(myCost);

        /* Record the delta cost/investment */
        myAsset.adjustCost(myDeltaCost);
        myAsset.adjustInvested(myDeltaCost);

        /* If we reduced the units */
        if (myDeltaUnits != null) {
            /* Record the delta units */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* Register the event */
        myAsset.registerEvent(pEvent);

        /* Access the Credit Asset Account Bucket */
        myAsset = theSecurityBuckets.getBucket(myCredit);

        /* The deltaCost is transferred to the credit account */
        myDeltaCost = new JMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Record the delta cost/investment */
        myAsset.adjustCost(myDeltaCost);
        myAsset.adjustInvested(myDeltaCost);

        /* Record the current/delta units */
        myDeltaUnits = pEvent.getCreditUnits();
        myAsset.adjustUnits(myDeltaUnits);

        /* Register the event */
        myAsset.registerEvent(pEvent);

        /* StockDeMerger is a transfer, so no need to update the categories */
    }

    /**
     * Process an event that is StockTakeover.
     * <p>
     * This can be accomplished using a cash portion (to a ThirdParty account) and these workings are split out.
     * @param pEvent the event
     */
    private void processStockTakeover(final Event pEvent) {
        JMoney myAmount = pEvent.getAmount();
        Account myThirdParty = pEvent.getThirdParty();

        /* If we have a ThirdParty cash part of the transaction */
        if ((myThirdParty != null)
            && (myAmount.isNonZero())) {
            /* Process a Stock And Cash Takeover */
            processStockAndCashTakeOver(pEvent);
        } else {
            /* Process a StockOnly TakeOverk */
            processStockOnlyTakeOver(pEvent);
        }
    }

    /**
     * Process an event that is a StockOnlyTakeover.
     * <p>
     * This capital event relates to both the Credit and Debit accounts
     * @param pEvent the event
     */
    private void processStockOnlyTakeOver(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();

        /* Access the Asset Security Buckets */
        SecurityBucket myDebitAsset = theSecurityBuckets.getBucket(myDebit);
        SecurityValues myDebitValues = myDebitAsset.getValues();
        SecurityBucket myCreditAsset = theSecurityBuckets.getBucket(myCredit);
        SecurityValues myCreditValues = myCreditAsset.getValues();

        /* Get the appropriate price for the credit account */
        SecurityPriceMap myPriceMap = theAnalysis.getPrices();
        JPrice myPrice = myPriceMap.getPriceForDate(myCredit, pEvent.getDate());

        /* Determine value of the stock part of the takeover */
        JUnits myDeltaUnits = pEvent.getCreditUnits();
        JMoney myStockValue = myDeltaUnits.valueAtPrice(myPrice);

        /* Determine the residual cost of the old stock */
        JMoney myStockCost = myDebitValues.getMoneyValue(SecurityAttribute.COST);

        /* Adjust cost/units/invested of the credit account */
        myCreditAsset.adjustCost(myStockCost);
        myCreditAsset.adjustUnits(myDeltaUnits);
        myCreditAsset.adjustInvested(myStockValue);

        /* Register the event */
        myCreditValues = myCreditAsset.registerEvent(pEvent);
        myCreditValues.setValue(SecurityAttribute.PRICE, myPrice);
        myCreditValues.setValue(SecurityAttribute.VALUATION, myStockValue);

        /* Drive debit cost down to zero */
        JMoney myCost = myDebitValues.getMoneyValue(SecurityAttribute.COST);
        JMoney myDeltaCost = new JMoney(myCost);
        myDeltaCost.negate();
        myDebitAsset.adjustCost(myDeltaCost);

        /* Drive debit units down to zero */
        JUnits myUnits = myDebitValues.getUnitsValue(SecurityAttribute.UNITS);
        myDeltaUnits = new JUnits(myUnits);
        myDeltaUnits.negate();
        myDebitAsset.adjustUnits(myDeltaUnits);

        /* Adjust debit Invested amount */
        myStockValue = new JMoney(myStockValue);
        myStockValue.negate();
        myDebitAsset.adjustInvested(myStockValue);

        /* Register the event */
        myDebitAsset.registerEvent(pEvent);
    }

    /**
     * Process an event that is StockAndCashTakeover.
     * <p>
     * This capital event relates to both the Credit and Debit accounts. In particular it makes reference to the CashTakeOver aspect of the debit account
     * @param pEvent the event
     */
    private void processStockAndCashTakeOver(final Event pEvent) {
        JDateDay myDate = pEvent.getDate();
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        Account myThirdParty = pEvent.getThirdParty();
        JMoney myAmount = pEvent.getAmount();

        /* Access the Asset Security Buckets */
        SecurityBucket myDebitAsset = theSecurityBuckets.getBucket(myDebit);
        SecurityValues myDebitValues = myDebitAsset.getValues();
        SecurityBucket myCreditAsset = theSecurityBuckets.getBucket(myCredit);
        SecurityValues myCreditValues = myCreditAsset.getValues();

        /* Get the appropriate prices for the assets */
        SecurityPriceMap myPriceMap = theAnalysis.getPrices();
        JPrice myDebitPrice = myPriceMap.getPriceForDate(myDebit, myDate);
        JPrice myCreditPrice = myPriceMap.getPriceForDate(myCredit, myDate);

        /* Determine value of the base stock */
        JUnits myBaseUnits = myDebitValues.getUnitsValue(SecurityAttribute.UNITS);
        JMoney myBaseValue = myBaseUnits.valueAtPrice(myDebitPrice);

        /* Determine value of the stock part of the takeover */
        JUnits myDeltaUnits = pEvent.getCreditUnits();
        JMoney myStockValue = myDeltaUnits.valueAtPrice(myCreditPrice);

        /* Access the current debit cost */
        JMoney myCost = myDebitValues.getMoneyValue(SecurityAttribute.COST);
        JMoney myCostXfer;

        /* Calculate the portion of the value that creates a large transaction */
        JMoney myPortion = myBaseValue.valueAtRate(LIMIT_RATE);

        /* If this is a large cash takeover portion (> both valueLimit and rateLimit of value) */
        if ((myAmount.compareTo(LIMIT_VALUE) > 0)
            && (myAmount.compareTo(myPortion) > 0)) {
            /* Calculate the total cost of the takeover */
            JMoney myTotalCost = new JMoney(myAmount);
            myTotalCost.addAmount(myStockValue);

            /* Determine the transferable cost */
            myCostXfer = myCost.valueAtWeight(myStockValue, myTotalCost);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* If the cash amount is greater than the total cost */
            if (myAmount.compareTo(myCost) > 0) {
                /* No Cost is transferred to the credit asset */
                myCostXfer = new JMoney();
            } else {
                /* Transferred cost is cost minus the cash amount */
                myCostXfer = new JMoney(myCost);
                myCostXfer.subtractAmount(myAmount);
            }
        }

        /* Calculate the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.subtractAmount(myCost);
        myDeltaGains.addAmount(myCostXfer);

        /* If we have some gains */
        if (myDeltaGains.isNonZero()) {
            /* Record the delta gains */
            myDebitAsset.adjustGains(myDeltaGains);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(pEvent, myDeltaGains);
        }

        /* Adjust cost/units/invested of the credit account */
        myCreditAsset.adjustCost(myCostXfer);
        myCreditAsset.adjustUnits(myDeltaUnits);
        myCreditAsset.adjustInvested(myStockValue);

        /* Register the event */
        myCreditValues = myCreditAsset.registerEvent(pEvent);
        myCreditValues.setValue(SecurityAttribute.PRICE, myCreditPrice);
        myCreditValues.setValue(SecurityAttribute.VALUATION, myStockValue);

        /* Drive debit cost down to zero */
        JMoney myDeltaCost = new JMoney(myCost);
        myDeltaCost.negate();
        myDebitAsset.adjustCost(myDeltaCost);

        /* Drive debit units down to zero */
        JUnits myUnits = myDebitValues.getUnitsValue(SecurityAttribute.UNITS);
        myDeltaUnits = new JUnits(myUnits);
        myDeltaUnits.negate();
        myDebitAsset.adjustUnits(myDeltaUnits);

        /* Adjust debit Invested amount */
        myStockValue = new JMoney(myStockValue);
        myStockValue.addAmount(pEvent.getAmount());
        myStockValue.negate();
        myDebitAsset.adjustInvested(myStockValue);

        /* Register the event */
        myDebitValues = myDebitAsset.registerEvent(pEvent);
        myDebitValues.setValue(SecurityAttribute.PRICE, myDebitPrice);
        myCreditValues.setValue(SecurityAttribute.VALUATION, myBaseValue);

        /* Adjust the ThirdParty account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myThirdParty);
        myBucket.adjustForCredit(pEvent);
    }
}
