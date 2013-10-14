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
package net.sourceforge.jOceanus.jMoneyWise.analysis;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.analysis.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.AccountCategoryBucket.CategoryType;
import net.sourceforge.jOceanus.jMoneyWise.analysis.DilutionEvent.DilutionEventList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.InvestmentAnalysis.InvestmentAttribute;
import net.sourceforge.jOceanus.jMoneyWise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.SecurityBucket.SecurityAttribute;
import net.sourceforge.jOceanus.jMoneyWise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice.AccountPriceList;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.Event.EventList;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory.EventCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;

/**
 * Class to analyse data.
 * @author Tony Washer
 */
public class DataAnalyser
        implements JDataContents {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(DataAnalyser.class.getSimpleName());

    /**
     * Analysis field Id.
     */
    public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return (theAnalysis == null)
                    ? JDataFieldValue.SkipField
                    : theAnalysis;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
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
    private final FinanceData theData;

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The event categories.
     */
    private final EventCategoryList theCategories;

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
    private EventCategoryBucketList theCategoryBuckets;

    /**
     * The taxMan account.
     */
    private PayeeBucket theTaxMan;

    /**
     * The dilutions.
     */
    private final DilutionEventList theDilutions;

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    public Analysis getAnalysis() {
        return theAnalysis;
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
     * @throws JDataException on error
     */
    public DataAnalyser(final FinanceData pData) throws JDataException {
        /* Store the parameters */
        theData = pData;

        /* Access the Categories and TaxMan account */
        theCategories = theData.getEventCategories();
        Account myTaxMan = theData.getAccounts().getSingularClass(AccountCategoryClass.TaxMan);

        /* Create the Dilution Event List */
        theDilutions = new DilutionEventList(theData);

        /* Access the lists */
        // TaxYearList myTaxYears = theData.getTaxYears();
        EventList myEvents = theData.getEvents();

        /* Create a new analysis */
        theAnalysis = new Analysis(theData);

        /* Access details from the analysis */
        theAccountBuckets = theAnalysis.getAccounts();
        theSecurityBuckets = theAnalysis.getSecurities();
        thePayeeBuckets = theAnalysis.getPayees();
        theCategoryBuckets = theAnalysis.getEventCategories();
        theTaxMan = thePayeeBuckets.getBucket(myTaxMan);

        /* Access the Event iterator */
        Iterator<Event> myIterator = myEvents.listIterator();
        // TaxYear myTax = null;
        // JDateDay myDate = null;
        // int myResult = -1;

        /* Reset the groups */
        // myEvents.resetGroups();

        /* Loop through the Events extracting relevant elements */
        while (myIterator.hasNext()) {
            Event myCurr = myIterator.next();
            // JDateDay myCurrDay = myCurr.getDate();

            /* Ignore deleted events */
            if (myCurr.isDeleted()) {
                continue;
            }

            /* If we have a current tax year */
            // if (myDate != null) {
            /* Check that this event is still in the tax year */
            // myResult = myDate.compareTo(myCurrDay);
            // }

            /* If we have exhausted the tax year or else this is the first tax year */
            // if (myResult < 0) {
            /* Access the relevant tax year */
            // myTax = myTaxYears.findTaxYearForDate(myCurrDay);
            // myDate = myTax.getTaxYear();

            /* If we have an existing meta analysis year */
            // if (theMetaAnalysis != null) {
            /* analyse accounts */
            // theMetaAnalysis.analyseAccounts();
            // }

            /* Create the new Analysis */
            // AnalysisYear myYear = theYears.getNewAnalysis(myTax, theAnalysis);
            // theAnalysis = myYear.getAnalysis();
            // theMetaAnalysis = myYear.getMetaAnalysis();
            // }

            /* Touch credit account */
            // Account myCredit = myCurr.getCredit();
            // myCredit.touchItem(myCurr);

            /* Touch debit accounts */
            // Account myDebit = myCurr.getDebit();
            // myDebit.touchItem(myCurr);

            /* Touch Category */
            // EventCategory myCategory = myCurr.getCategory();
            // myCategory.touchItem(myCurr);

            /* If the event has a parent */
            // Event myParent = myCurr.getParent();
            // if (myParent != null) {
            /* Touch the parent */
            // myParent.touchItem(myCurr);

            /* Register child against parent */
            // myEvents.registerChild(myCurr);
            // }

            /* If the event has a dilution factor */
            if (myCurr.getDilution() != null) {
                /* Add to the dilution event list */
                theDilutions.addDilution(myCurr);
            }

            /* Process the event in the breakdown */
            // myBreakdown.processEvent(myCurr);

            /* Process the event in the report set */
            processEvent(myCurr);
            // myTax.touchItem(myCurr);
        }

        /* analyse accounts */
        // if (theMetaAnalysis != null) {
        // theMetaAnalysis.analyseAccounts();
        // }

        /* Update analysis object */
        // mySection.setObject(this);
    }

    /**
     * Process an event.
     * @param pEvent the event to process
     * @throws JDataException on error
     */
    private void processEvent(final Event pEvent) throws JDataException {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JMoney myAmount = pEvent.getAmount();

        /* If the event relates to a priced item split out the workings */
        if ((myDebit.hasUnits())
            || (myCredit.hasUnits())) {
            /* Process as a Capital event */
            processCapitalEvent(pEvent);

            /* Else handle the event normally */
        } else {
            EventCategory myCat = pEvent.getCategory();

            /* Switch on category class */
            switch (myCat.getCategoryTypeClass()) {
                case Interest:
                    /* Obtain detailed category */
                    myCat = myDebit.getDetailedCategory(myCat);

                    /* True debit account is the parent */
                    myDebit = myDebit.getParent();
                    break;
                case LoanInterestEarned:
                    /* True debit account is the parent of the loan */
                    myDebit = myDebit.getParent();
                    break;
                case RentalIncome:
                case RoomRentalIncome:
                    /* True debit account is the parent of the loan */
                    myDebit = myCredit.getParent();
                    break;
                case WriteOff:
                case LoanInterestCharged:
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
                myPayee.subtractExpense(myAmount);

                /* Subtract expense from Category bucket */
                EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.subtractExpense(myAmount);

                /* else handle normally */
            } else {
                /* Determine the type of the debit account */
                CategoryType myType = AccountCategoryBucket.determineCategoryType(myDebit.getAccountCategory());
                switch (myType) {
                    case Payee:
                        PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                        myPayee.adjustForDebit(pEvent);
                        break;
                    case Money:
                    case CreditCard:
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
                myPayee.addExpense(myAmount);

                /* Adjust the relevant category bucket */
                EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.addExpense(myAmount);
                /* else handle normally */
            } else {
                /* Determine the type of the credit account */
                CategoryType myType = AccountCategoryBucket.determineCategoryType(myCredit.getAccountCategory());
                switch (myType) {
                    case Payee:
                        PayeeBucket myPayee = thePayeeBuckets.getBucket(myCredit);
                        myPayee.adjustForCredit(pEvent);
                        break;
                    case Money:
                    case CreditCard:
                    default:
                        AccountBucket myAccount = theAccountBuckets.getBucket(myCredit);
                        myAccount.adjustForCredit(pEvent);
                        break;
                }
            }

            /* Adjust the tax payments */
            theTaxMan.adjustForTaxPayments(pEvent);

            /* If the event category is not a transfer */
            if (!myCat.isTransfer()) {
                /* Adjust the relevant category bucket */
                EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
                myCatBucket.adjustValues(pEvent);
            }
        }
    }

    /**
     * Process a capital event.
     * @param pEvent the event to process
     * @throws JDataException on error
     */
    private void processCapitalEvent(final Event pEvent) throws JDataException {
        EventCategory myCat = pEvent.getCategory();

        /* Switch on the category */
        switch (myCat.getCategoryTypeClass()) {
        /* Process a stock split */
            case StockSplit:
            case StockAdjust:
                processStockSplit(pEvent);
                break;
            /* Process a stock right taken */
            case StockRightsTaken:
                processTransferIn(pEvent);
                break;
            /* Process a stock right taken */
            case StockRightsWaived:
                processStockRightWaived(pEvent);
                break;
            /* Process a stock DeMerger */
            case StockDeMerger:
                processStockDeMerger(pEvent);
                break;
            /* Process a Stock TakeOver */
            case StockTakeOver:
                processStockTakeover(pEvent);
                break;
            /* Process a dividend */
            case Dividend:
                processDividend(pEvent);
                break;
            /* Process standard transfer in/out */
            case Transfer:
            case Expense:
            case Inherited:
            case OtherIncome:
                if (pEvent.getDebit().isCategoryClass(AccountCategoryClass.LifeBond)) {
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
                throw new JDataException(ExceptionClass.LOGIC, "Unexpected category type: "
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

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myAccount);

        /* Allocate an Investment Analysis and adjust the units */
        InvestmentAnalysis myAnalysis = myAsset.getInvestmentAnalyses().addAnalysis(pEvent);
        JUnits myUnits = myAsset.getUnitsAttribute(SecurityAttribute.Units);
        myAnalysis.adjustUnits(myUnits, myDelta);

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
            case Payee:
                PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                myPayee.adjustForDebit(pEvent);
                break;
            case Money:
            case CreditCard:
            default:
                AccountBucket myAccount = theAccountBuckets.getBucket(myDebit);
                myAccount.adjustForDebit(pEvent);
                break;
        }

        /* If the event category is not a transfer */
        if (!myCat.isTransfer()) {
            /* Adjust the relevant event category bucket */
            EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
            myCatBucket.adjustValues(pEvent);
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

        /* Allocate an Investment Analysis and adjust cost */
        InvestmentAnalysis myAnalysis = myAsset.getInvestmentAnalyses().addAnalysis(pEvent);
        JMoney myCost = myAsset.getMoneyAttribute(SecurityAttribute.Cost);
        myAnalysis.adjustCost(myCost, myAmount);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(SecurityAttribute.Invested);
        myAnalysis.adjustInvested(myInvested, myAmount);

        /* If we have new units */
        if (myDeltaUnits != null) {
            /* Record change and adjust units */
            JUnits myUnits = myAsset.getUnitsAttribute(SecurityAttribute.Units);
            myAnalysis.adjustUnits(myUnits, myDeltaUnits);
        }
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

        /* Allocate an Investment Analysis */
        InvestmentAnalysis myAnalysis = myAsset.getInvestmentAnalyses().addAnalysis(pEvent);

        /* If this is a re-investment */
        if (myAccount.equals(myCredit)) {
            /* This amount is added to the cost, so record as the delta cost */
            JMoney myCost = myAsset.getMoneyAttribute(SecurityAttribute.Cost);
            myAnalysis.adjustCost(myCost, myAmount);

            /* Record the current/delta investment */
            JMoney myInvested = myAsset.getMoneyAttribute(SecurityAttribute.Invested);
            myAnalysis.adjustInvested(myInvested, myAmount);

            /* If we have new units */
            if (myDeltaUnits != null) {
                /* Record current and delta units */
                JUnits myUnits = myAsset.getUnitsAttribute(SecurityAttribute.Units);
                myAnalysis.adjustUnits(myUnits, myDeltaUnits);
            }

            /* If we have a tax credit */
            if (myTaxCredit != null) {
                /* The Tax Credit is viewed as a received dividend from the account */
                JMoney myDividend = myAsset.getMoneyAttribute(SecurityAttribute.Dividend);
                myAnalysis.adjustDividend(myDividend, myTaxCredit);
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
            JMoney myDividend = myAsset.getMoneyAttribute(SecurityAttribute.Dividend);
            myAnalysis.adjustDividend(myDividend, myAdjust);

            /* Adjust the credit account bucket */
            AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
            myBucket.adjustForCredit(pEvent);
        }

        /* Adjust the tax payments */
        theTaxMan.adjustForTaxPayments(pEvent);

        /* Adjust the relevant transaction bucket */
        EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
        myCatBucket.adjustValues(pEvent);
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
            /* Adjust the relevant transaction bucket */
            EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
            myCatBucket.adjustValues(pEvent);
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

        /* Allocate an investment analysis and record Current and delta costs */
        InvestmentAnalysis myAnalysis = myAsset.getInvestmentAnalyses().addAnalysis(pEvent);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(SecurityAttribute.Invested);
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myAnalysis.adjustInvested(myInvested, myDelta);

        /* Assume the the cost reduction is the full value */
        JMoney myReduction = new JMoney(myAmount);
        JMoney myCost = myAsset.getMoneyAttribute(SecurityAttribute.Cost);

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
            JUnits myUnits = myAsset.getUnitsAttribute(SecurityAttribute.Units);
            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myAnalysis.adjustUnits(myUnits, myDeltaUnits);
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
            /* This amount is subtracted from the cost, so record as the delta cost */
            myAnalysis.adjustCost(myCost, myDeltaCost);
        }

        /* Determine the delta to the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            JMoney myGains = myAsset.getMoneyAttribute(SecurityAttribute.Gains);
            myAnalysis.adjustGains(myGains, myDeltaGains);
        }
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
     * Process an event that is a taxable gain. This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processTaxableGain(final Event pEvent) {
        /* Taxable Gain is from the debit account and may or may not have units */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JMoney myAmount = pEvent.getAmount();
        JUnits myDeltaUnits = pEvent.getDebitUnits();
        EventCategory myCat = theCategories.getSingularClass(EventCategoryClass.TaxableGain);

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myAccount);

        /* Allocate an investment analysis */
        InvestmentAnalysis myAnalysis = myAsset.getInvestmentAnalyses().addAnalysis(pEvent);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(SecurityAttribute.Invested);
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myAnalysis.adjustInvested(myInvested, myDelta);

        /* Assume the the cost reduction is the full value */
        JMoney myReduction = new JMoney(myAmount);
        JMoney myCost = myAsset.getMoneyAttribute(SecurityAttribute.Cost);

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
            JUnits myUnits = myAsset.getUnitsAttribute(SecurityAttribute.Units);
            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record current and delta units */
            myAnalysis.adjustUnits(myUnits, myDeltaUnits);
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
            /* This amount is subtracted from the cost, so record as the delta cost */
            myAnalysis.adjustCost(myCost, myDeltaCost);
        }

        /* Determine the delta to the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* This amount is subtracted from the cost, so record as the delta cost */
            JMoney myGains = myAsset.getMoneyAttribute(SecurityAttribute.Gains);
            myAnalysis.adjustGains(myGains, myDeltaGains);
        }

        /* True debit account is the parent */
        Account myDebit = myAccount.getParent();

        /* Adjust the debit account bucket */
        PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
        myPayee.addIncome(pEvent.getTaxCredit());

        /* Adjust the credit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* Adjust the taxableGains category bucket */
        EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myCat);
        myCatBucket.adjustValues(pEvent);
        myCatBucket.subtractIncome(myReduction);

        /* Adjust the TaxMan account for the tax credit */
        theTaxMan.adjustForTaxPayments(pEvent);

        /* Add the chargeable event */
        theAnalysis.getCharges().addEvent(pEvent, myDeltaGains);
    }

    /**
     * Process an event that is stock right waived. This capital event relates only to the Debit Account
     * @param pEvent the event
     */
    private void processStockRightWaived(final Event pEvent) {
        /* Stock Right Waived is from the debit account */
        Account myAccount = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        AccountPriceList myPrices = theData.getPrices();
        JMoney myAmount = pEvent.getAmount();
        JMoney myReduction;

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myAccount);

        /* Allocate an investment analysis */
        InvestmentAnalysis myAnalysis = myAsset.getInvestmentAnalyses().addAnalysis(pEvent);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(SecurityAttribute.Invested);
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myAnalysis.adjustInvested(myInvested, myDelta);

        /* Get the appropriate price for the account */
        AccountPrice myActPrice = myPrices.getLatestPrice(myAccount, pEvent.getDate());
        JPrice myPrice = myActPrice.getPrice();
        myAnalysis.setAttribute(InvestmentAttribute.InitialPrice, myPrice);

        /* Determine value of this stock at the current time */
        JUnits myUnits = myAsset.getUnitsAttribute(SecurityAttribute.Units);
        JMoney myValue = myUnits.valueAtPrice(myPrice);
        myAnalysis.setAttribute(InvestmentAttribute.InitialValue, myValue);

        /* Access the current cost */
        JMoney myCost = myAsset.getMoneyAttribute(SecurityAttribute.Cost);

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
        myAnalysis.adjustCost(myCost, myDeltaCost);

        /* Calculate the delta gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* Record the current/delta gains */
        JMoney myGains = myAsset.getMoneyAttribute(SecurityAttribute.Gains);
        myAnalysis.adjustGains(myGains, myDeltaGains);

        /* Adjust the credit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
        myBucket.adjustForCredit(pEvent);

        /* StockRightWaived is a transfer, so no need to update the categories */
    }

    /**
     * Process an event that is Stock DeMerger. This capital event relates to both the Credit and Debit accounts
     * @param pEvent the event
     */
    private void processStockDeMerger(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        JDilution myDilution = pEvent.getDilution();
        JUnits myDeltaUnits = pEvent.getDebitUnits();

        /* Access the Debit Asset Security Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myDebit);

        /* Allocate an investment analysis */
        InvestmentAnalysis myAnalysis = myAsset.getInvestmentAnalyses().addAnalysis(pEvent);

        /* Calculate the diluted value of the Debit account */
        JMoney myCost = myAsset.getMoneyAttribute(SecurityAttribute.Cost);
        JMoney myNewCost = myCost.getDilutedMoney(myDilution);

        /* Calculate the delta to the cost */
        JMoney myDeltaCost = new JMoney(myNewCost);
        myDeltaCost.subtractAmount(myCost);

        /* Record the current/delta cost */
        myAnalysis.adjustCost(myCost, myDeltaCost);

        /* Record the current/delta investment */
        JMoney myInvested = myAsset.getMoneyAttribute(SecurityAttribute.Invested);
        myAnalysis.adjustInvested(myInvested, myDeltaCost);

        /* If we reduced the units */
        if (myDeltaUnits != null) {
            /* Record the current/delta units */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();
            JUnits myUnits = myAsset.getUnitsAttribute(SecurityAttribute.Units);
            myAnalysis.adjustUnits(myUnits, myDeltaUnits);
        }

        /* Access the Credit Asset Account Bucket */
        myAsset = theSecurityBuckets.getBucket(myCredit);

        /* Allocate a Capital event */
        myAnalysis = myAsset.getInvestmentAnalyses().addAnalysis(pEvent);

        /* The deltaCost is transferred to the credit account */
        myDeltaCost = new JMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myCost = myAsset.getMoneyAttribute(SecurityAttribute.Cost);
        myAnalysis.adjustCost(myCost, myDeltaCost);

        /* Record the current/delta investment */
        myInvested = myAsset.getMoneyAttribute(SecurityAttribute.Invested);
        myAnalysis.adjustInvested(myInvested, myDeltaCost);

        /* Record the current/delta units */
        myDeltaUnits = pEvent.getCreditUnits();
        JUnits myUnits = myAsset.getUnitsAttribute(SecurityAttribute.Units);
        myAnalysis.adjustUnits(myUnits, myDeltaUnits);

        /* StockDeMerger is a transfer, so no need to update the categories */
    }

    /**
     * Process an event that is the Cash portion of a StockTakeOver. This capital event to the Debit Account and to the ThirdParty Account.
     * @param pEvent the event
     * @param pSource the debit capital event
     */
    private void processCashTakeover(final Event pEvent,
                                     final InvestmentAnalysis pSource) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getThirdParty();
        AccountPriceList myPrices = theData.getPrices();
        JMoney myAmount = pEvent.getAmount();

        /* Access the Debit Asset Security Bucket */
        SecurityBucket myAsset = theSecurityBuckets.getBucket(myDebit);

        /* Get the appropriate price for the account */
        AccountPrice myActPrice = myPrices.getLatestPrice(myDebit, pEvent.getDate());
        JPrice myPrice = myActPrice.getPrice();
        pSource.setAttribute(InvestmentAttribute.FinalPrice, myPrice);

        /* Determine value of this stock at the current time */
        JUnits myUnits = myAsset.getUnitsAttribute(SecurityAttribute.Units);
        JMoney myValue = myUnits.valueAtPrice(myPrice);
        pSource.setAttribute(InvestmentAttribute.FinalValue, myValue);

        /* Record the cash value of the takeover */
        pSource.setAttribute(InvestmentAttribute.TakeOverCashValue, myAmount);

        /* Access the current cost */
        JMoney myCost = myAsset.getMoneyAttribute(SecurityAttribute.Cost);
        JMoney myCashCost;
        JMoney myStockCost;

        /* Calculate the portion of the value that creates a large transaction */
        JMoney myPortion = myValue.valueAtRate(LIMIT_RATE);

        /* If this is a large cash takeover portion (> both valueLimit and rateLimit of value) */
        if ((myAmount.compareTo(LIMIT_VALUE) > 0)
            && (myAmount.compareTo(myPortion) > 0)) {
            /* Calculate the total cost of the takeover */
            JMoney myTotalCost = new JMoney(myAmount);
            myValue = pSource.getMoneyAttribute(InvestmentAttribute.TakeOverStockValue);
            myTotalCost.addAmount(myValue);

            /* Split the total cost of the takeover between stock and cash */
            myStockCost = myTotalCost.valueAtWeight(myValue, myTotalCost);
            myCashCost = new JMoney(myTotalCost);
            myCashCost.subtractAmount(myStockCost);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* Set the CashCost to be the entire amount */
            myCashCost = new JMoney(myAmount);

            /* If the reduction is greater than the total cost */
            if (myCashCost.compareTo(myCost) > 0) {
                /* Reduction is the total cost */
                myCashCost = new JMoney(myCost);
            }

            /* Calculate the residual cost */
            myStockCost = new JMoney(myCashCost);
            myStockCost.negate();
            myStockCost.addAmount(myCost);
        }

        /* Record the cost values */
        pSource.setAttribute(InvestmentAttribute.TakeOverCashCost, myCashCost);
        pSource.setAttribute(InvestmentAttribute.TakeOverStockCost, myStockCost);

        /* Calculate the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.subtractAmount(myCashCost);

        /* Record the current/delta gains */
        JMoney myGains = myAsset.getMoneyAttribute(SecurityAttribute.Gains);
        pSource.adjustGains(myGains, myDeltaGains);

        /* Adjust the credit account bucket */
        AccountBucket myBucket = theAccountBuckets.getBucket(myCredit);
        myBucket.adjustForCredit(pEvent);
    }

    /**
     * Process an event that is StockTakeover. This capital event relates to both the Credit and Debit accounts In particular it makes reference to the
     * CashTakeOver aspect of the debit account
     * @param pEvent the event
     */
    private void processStockTakeover(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        Account myThirdParty = pEvent.getThirdParty();
        AccountPriceList myPrices = theData.getPrices();
        JMoney myStockCost;

        /* Access the Asset Security Buckets */
        SecurityBucket myDebitAsset = theSecurityBuckets.getBucket(myDebit);
        SecurityBucket myCreditAsset = theSecurityBuckets.getBucket(myCredit);

        /* Allocate the events */
        InvestmentAnalysis myDebitAnalysis = myDebitAsset.getInvestmentAnalyses().addAnalysis(pEvent);
        InvestmentAnalysis myCreditAnalysis = myCreditAsset.getInvestmentAnalyses().addAnalysis(pEvent);

        /* Get the appropriate price for the credit account */
        AccountPrice myActPrice = myPrices.getLatestPrice(myCredit, pEvent.getDate());
        JPrice myPrice = myActPrice.getPrice();
        myDebitAnalysis.setAttribute(InvestmentAttribute.TakeOverStockPrice, myPrice);

        /* Determine value of the stock part of the takeover */
        JUnits myDeltaUnits = pEvent.getCreditUnits();
        JMoney myStockValue = myDeltaUnits.valueAtPrice(myPrice);
        myDebitAnalysis.setAttribute(InvestmentAttribute.TakeOverStockValue, myStockValue);

        /* If we have a ThirdParty cash part of the transaction */
        if ((myThirdParty != null)
            && (pEvent.getAmount().isNonZero())) {
            /* Process the cash part of the takeover */
            processCashTakeover(pEvent, myDebitAnalysis);

            /* Determine the cost of the new stock */
            myStockCost = myDebitAnalysis.getMoneyAttribute(InvestmentAttribute.TakeOverStockCost);
        } else {
            /* Determine the cost of the new stock */
            myStockCost = myDebitAsset.getMoneyAttribute(SecurityAttribute.Cost);

            /* Record it as the takeOver cost */
            myDebitAnalysis.setAttribute(InvestmentAttribute.TakeOverStockCost, new JMoney(myStockCost));
        }

        /* Adjust cost/units/invested of the credit account */
        JMoney myCost = myCreditAsset.getMoneyAttribute(SecurityAttribute.Cost);
        myCreditAnalysis.adjustCost(myCost, myStockCost);
        JUnits myUnits = myCreditAsset.getUnitsAttribute(SecurityAttribute.Units);
        myCreditAnalysis.adjustUnits(myUnits, myDeltaUnits);
        JMoney myInvested = myCreditAsset.getMoneyAttribute(SecurityAttribute.Invested);
        myCreditAnalysis.adjustInvested(myInvested, myStockValue);

        /* Drive debit cost down to zero */
        myCost = myDebitAsset.getMoneyAttribute(SecurityAttribute.Cost);
        JMoney myDeltaCost = new JMoney(myCost);
        myDeltaCost.negate();
        myDebitAnalysis.adjustCost(myCost, myDeltaCost);

        /* Drive debit units down to zero */
        myUnits = myDebitAsset.getUnitsAttribute(SecurityAttribute.Units);
        myDeltaUnits = new JUnits(myUnits);
        myDeltaUnits.negate();
        myDebitAnalysis.adjustUnits(myUnits, myDeltaUnits);

        /* Adjust debit Invested amount */
        myStockValue = new JMoney(myStockValue);
        myStockValue.addAmount(pEvent.getAmount());
        myStockValue.negate();
        myInvested = myDebitAsset.getMoneyAttribute(SecurityAttribute.Invested);
        myDebitAnalysis.adjustInvested(myInvested, myStockValue);
    }
}
