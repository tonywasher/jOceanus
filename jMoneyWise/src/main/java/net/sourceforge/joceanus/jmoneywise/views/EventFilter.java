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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;

import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory.EventCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.EventGroup;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory.TaxCategoryList;

/**
 * Filter criteria for events.
 */
public class EventFilter
        implements JDataContents {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(EventFilter.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Accounts Field Id.
     */
    public static final JDataField FIELD_ACCOUNTS = FIELD_DEFS.declareLocalField("Accounts");

    /**
     * Payees Field Id.
     */
    public static final JDataField FIELD_PAYEES = FIELD_DEFS.declareLocalField("Payees");

    /**
     * EventCategories Field Id.
     */
    public static final JDataField FIELD_EVENTCATEGORIES = FIELD_DEFS.declareLocalField("EventCategories");

    /**
     * Filtered Accounts Field Id.
     */
    public static final JDataField FIELD_FILTERACCOUNTS = FIELD_DEFS.declareLocalField("FilteredAccounts");

    /**
     * Filtered Payees Field Id.
     */
    public static final JDataField FIELD_FILTEREDPAYEES = FIELD_DEFS.declareLocalField("FilteredPayees");

    /**
     * Filtered EventCategories Field Id.
     */
    public static final JDataField FIELD_FILTEREDEVENTCATEGORIES = FIELD_DEFS.declareLocalField("FilteredEventCategories");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ACCOUNTS.equals(pField)) {
            return theAccounts;
        }
        if (FIELD_PAYEES.equals(pField)) {
            return thePayees;
        }
        if (FIELD_EVENTCATEGORIES.equals(pField)) {
            return theEventCategories;
        }
        if (FIELD_FILTERACCOUNTS.equals(pField)) {
            return (theAccounts.isEmpty())
                    ? JDataFieldValue.SKIP
                    : theAccounts;
        }
        if (FIELD_FILTEREDPAYEES.equals(pField)) {
            return (thePayees.isEmpty())
                    ? JDataFieldValue.SKIP
                    : thePayees;
        }
        if (FIELD_FILTEREDEVENTCATEGORIES.equals(pField)) {
            return (theEventCategories.isEmpty())
                    ? JDataFieldValue.SKIP
                    : theEventCategories;
        }

        /* Unknown */
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The DataSet.
     */
    private FinanceData theDataSet;

    /**
     * Filtered Payee list.
     */
    private AccountList theFilteredPayees;

    /**
     * Payee list.
     */
    private final AccountList thePayees;

    /**
     * Filtered Account list.
     */
    private AccountList theFilteredAccounts;

    /**
     * Account list.
     */
    private final AccountList theAccounts;

    /**
     * Filtered Event Category List.
     */
    private EventCategoryList theFilteredEventCategories;

    /**
     * Event Category List.
     */
    private final EventCategoryList theEventCategories;

    /**
     * Filtered Tax Category List.
     */
    private TaxCategoryList theFilteredTaxCategories;

    /**
     * Tax Category List.
     */
    private final TaxCategoryList theTaxCategories;

    /**
     * Are we filtering on TaxMan payee?
     */
    private boolean filterOnTaxManPayee;

    /**
     * Are we filtering on TaxCredit category?
     */
    private boolean filterOnTaxCreditCategory;

    /**
     * Are we filtering on NatInsurance Category?
     */
    private boolean filterOnNatInsCategory;

    /**
     * Are we filtering on DeemedBenefit Category?
     */
    private boolean filterOnBenefitCategory;

    /**
     * Are we filtering on CharityDonation Category?
     */
    private boolean filterOnDonationCategory;

    /**
     * Obtain active Payee list.
     * @return the active payee list
     */
    public AccountList getPayeeList() {
        return thePayees;
    }

    /**
     * Obtain active Account list.
     * @return the active account list
     */
    public AccountList getAccountList() {
        return theAccounts;
    }

    /**
     * Obtain active Event Category list.
     * @return the active event category list
     */
    public EventCategoryList getEventCategoryList() {
        return theEventCategories;
    }

    /**
     * Obtain active Tax Category list.
     * @return the active tax category list
     */
    public TaxCategoryList getTaxCategoryList() {
        return theTaxCategories;
    }

    /**
     * Constructor.
     * @param pData the new dataSet
     */
    public EventFilter(final FinanceData pData) {
        /* Record the dataSet */
        theDataSet = pData;

        /* Allocate lists */
        theAccounts = new AccountList(theDataSet);
        thePayees = new AccountList(theDataSet);
        theEventCategories = new EventCategoryList(theDataSet);
        theTaxCategories = new TaxCategoryList(theDataSet);
    }

    /**
     * Migrate filter to a new dataSet.
     * @param pData the new dataSet
     */
    public void setDataSet(final FinanceData pData) {
        /* Record the dataSet */
        theDataSet = pData;

        /* If we have an account list */
        if (theFilteredAccounts != null) {
            /* Record existing list and create new one */
            AccountList myAccounts = pData.getAccounts();
            AccountList myList = theFilteredAccounts;
            theFilteredAccounts = new AccountList(theDataSet);

            /* Loop through the existing accounts */
            for (Account myAccount : myList) {
                /* Access account in new dataSet and add to filter if it still exists */
                Account myNew = myAccounts.findItemById(myAccount.getId());
                if (myNew != null) {
                    theFilteredAccounts.add(myNew);
                }
            }
        }

        /* If we have a payee list */
        if (theFilteredPayees != null) {
            /* Record existing list and create new one */
            AccountList myAccounts = pData.getAccounts();
            AccountList myList = theFilteredPayees;
            theFilteredPayees = new AccountList(theDataSet);

            /* Loop through the existing accounts */
            for (Account myAccount : myList) {
                /* Access account in new dataSet and add to filter if it still exists */
                Account myNew = myAccounts.findItemById(myAccount.getId());
                if (myNew != null) {
                    theFilteredPayees.add(myNew);
                }
            }
        }

        /* If we have an event category list */
        if (theFilteredEventCategories != null) {
            /* Record existing list and create new one */
            EventCategoryList myCategories = pData.getEventCategories();
            EventCategoryList myList = theFilteredEventCategories;
            theFilteredEventCategories = new EventCategoryList(theDataSet);

            /* Loop through the existing accounts */
            for (EventCategory myCategory : myList) {
                /* Access category in new dataSet and add to filter if it still exists */
                EventCategory myNew = myCategories.findItemById(myCategory.getId());
                if (myNew != null) {
                    theFilteredEventCategories.add(myNew);
                }
            }
        }
    }

    /**
     * Set filter for an AccountBucket.
     * @param pBucket the Account Bucket.
     */
    public void setFilter(final AccountBucket pBucket) {
        /* Clear selection by categories */
        theFilteredEventCategories = null;
        theFilteredTaxCategories = null;

        /* If the account is a non-asset */
        Account myAccount = pBucket.getAccount();
        if ((myAccount.isNonAsset())
            || (myAccount.getAutoExpense() != null)) {
            /* Set Filtered Payee list */
            theFilteredPayees = new AccountList(theDataSet);
            theFilteredPayees.add(myAccount);
            theFilteredAccounts = null;

            /* Determine whether we are filtering on TaxMan */
            filterOnTaxManPayee = (myAccount.isCategoryClass(AccountCategoryClass.TaxMan));
            /* else the account is a payee */
        } else {
            /* Set filtered account list */
            theFilteredAccounts = new AccountList(theDataSet);
            theFilteredAccounts.add(myAccount);
            theFilteredPayees = null;
        }
    }

    /**
     * Set filter for an EventCategoryBucket.
     * @param pBucket the EventCategory Bucket.
     */
    public void setFilter(final EventCategoryBucket pBucket) {
        /* Clear selection by tax categories/accounts */
        theFilteredTaxCategories = null;
        theFilteredPayees = null;
        theFilteredAccounts = null;

        /* Access category and set for final category */
        EventCategory myCategory = pBucket.getEventCategory();

        /* Set filtered event category list list */
        theFilteredEventCategories = new EventCategoryList(theDataSet);
        theFilteredEventCategories.add(myCategory);

        /* Determine whether we are filtering on various categories */
        filterOnTaxCreditCategory = (myCategory.isCategoryClass(EventCategoryClass.TaxCredit));
        filterOnNatInsCategory = (myCategory.isCategoryClass(EventCategoryClass.NatInsurance));
        filterOnBenefitCategory = (myCategory.isCategoryClass(EventCategoryClass.DeemedBenefit));
        filterOnDonationCategory = (myCategory.isCategoryClass(EventCategoryClass.CharityDonation));
    }

    /**
     * Set filter for a TaxBasisBucket.
     * @param pBucket the TaxBasis Bucket.
     */
    public void setFilter(final TaxBasisBucket pBucket) {
        /* Clear selection by event categories/accounts */
        theFilteredEventCategories = null;
        theFilteredPayees = null;
        theFilteredAccounts = null;

        /* Access category and set for final category */
        TaxCategory myCategory = pBucket.getTaxCategory();

        /* Set filtered tax category list list */
        theFilteredTaxCategories = new TaxCategoryList(theDataSet);
        theFilteredTaxCategories.add(myCategory);
    }

    /**
     * Set Payee List.
     * @param pPayees the list of Payees (or null for all)
     */
    public void setPayeeList(final AccountList pPayees) {
        /* Record new list */
        theFilteredPayees = pPayees;
        filterOnTaxManPayee = false;

        /* If we are filtering */
        if (theFilteredPayees != null) {
            /* Loop through the filtered payees */
            Iterator<Account> myIterator = theFilteredPayees.iterator();
            while (myIterator.hasNext()) {
                Account myAccount = myIterator.next();

                /* If the account is the taxMan class */
                if (myAccount.isCategoryClass(AccountCategoryClass.TaxMan)) {
                    /* Note that we are filtering on taxMan */
                    filterOnTaxManPayee = true;
                    break;
                }
            }
        }
    }

    /**
     * Set Account List.
     * @param pAccounts the list of Accounts (or null for all)
     */
    public void setAccountList(final AccountList pAccounts) {
        theFilteredAccounts = pAccounts;
    }

    /**
     * Set Event Category List.
     * @param pCategories the list of Categories (or null for all)
     */
    public void setEventCategoryList(final EventCategoryList pCategories) {
        /* Record new list */
        theFilteredEventCategories = pCategories;
        filterOnTaxCreditCategory = false;
        filterOnNatInsCategory = false;
        filterOnBenefitCategory = false;
        filterOnDonationCategory = false;

        /* If we are filtering */
        if (theFilteredEventCategories != null) {
            /* Loop through the filtered categories */
            Iterator<EventCategory> myIterator = theFilteredEventCategories.iterator();
            while (myIterator.hasNext()) {
                EventCategory myCategory = myIterator.next();

                /* Switch on category class */
                switch (myCategory.getCategoryTypeClass()) {
                    case TaxCredit:
                        filterOnTaxCreditCategory = true;
                        break;
                    case NatInsurance:
                        filterOnNatInsCategory = true;
                        break;
                    case DeemedBenefit:
                        filterOnBenefitCategory = true;
                        break;
                    case CharityDonation:
                        filterOnDonationCategory = true;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Set Tax Category List.
     * @param pCategories the list of Categories (or null for all)
     */
    public void setTaxCategoryList(final TaxCategoryList pCategories) {
        theFilteredTaxCategories = pCategories;
    }

    /**
     * Reset lists.
     */
    public void resetLists() {
        theAccounts.clear();
        thePayees.clear();
        theEventCategories.clear();
        theTaxCategories.clear();
    }

    /**
     * Register event.
     * @param pEvent the event to register
     */
    public void registerEvent(final Event pEvent) {
        /* Access details */
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        Account myThirdParty = pEvent.getThirdParty();
        EventCategory myDebitAuto = myDebit.getAutoExpense();
        EventCategory myCreditAuto = myCredit.getAutoExpense();
        EventCategory myCategory = pEvent.getCategory();
        EventCategoryClass myCatClass = myCategory.getCategoryTypeClass();
        AccountList myAccounts = theDataSet.getAccounts();
        EventCategoryList myCategories = theDataSet.getEventCategories();

        /* Add debit to appropriate list */
        if ((myDebit.isNonAsset())
            || (myDebit.getAutoExpense() != null)) {
            thePayees.add(myDebit);
        } else {
            theAccounts.add(myDebit);
        }

        /* Add credit to appropriate list */
        if ((myCredit.isNonAsset())
            || (myCredit.getAutoExpense() != null)) {
            thePayees.add(myCredit);
        } else {
            theAccounts.add(myCredit);
        }

        /* Register debit parent payee if required */
        if (myCatClass.isDebitParentPayee()) {
            /* Check for filtering on Parent */
            theAccounts.add(myDebit.getParent());
        }

        /* Register credit parent payee if required */
        if (myCatClass.isDebitParentPayee()) {
            /* Check for filtering on Parent */
            theAccounts.add(myCredit.getParent());
        }

        /* Add Third party to list if it exists */
        if (myThirdParty != null) {
            theAccounts.add(myThirdParty);
        }

        /* Add TaxMan/TaxCredit if required */
        if (pEvent.getTaxCredit() != null) {
            thePayees.add(myAccounts.getSingularClass(AccountCategoryClass.TaxMan));
            theEventCategories.add(myCategories.getSingularClass(EventCategoryClass.TaxCredit));
        }

        /* Add National Insurance if required */
        if (pEvent.getNatInsurance() != null) {
            theEventCategories.add(myCategories.getSingularClass(EventCategoryClass.NatInsurance));
        }

        /* Add Deemed Benefit required */
        if (pEvent.getDeemedBenefit() != null) {
            theEventCategories.add(myCategories.getSingularClass(EventCategoryClass.DeemedBenefit));
        }

        /* Add Charity Donation if required */
        if (pEvent.getCharityDonation() != null) {
            theEventCategories.add(myCategories.getSingularClass(EventCategoryClass.CharityDonation));
        }

        /* Register the event category */
        theEventCategories.add(myCategory);

        /* If the event is interest/dividend */
        if ((pEvent.isInterest())
            || (pEvent.isDividend())) {
            /* Register detailed category */
            theEventCategories.add(myDebit.getDetailedCategory(myCategory));
        }

        /* Register autoExpense items */
        if (myDebitAuto != null) {
            theEventCategories.add(myDebitAuto);
        }
        if (myCreditAuto != null) {
            theEventCategories.add(myCreditAuto);
        }
    }

    /**
     * Should we filter this event out?
     * @param pEvent the event to check
     * @return true/false
     */
    public boolean filterEvent(final Event pEvent) {
        /* If this is a split event */
        if (pEvent.isSplit()) {
            /* Filter out children */
            if (pEvent.isChild()) {
                return true;
            }

            /* Access the group */
            EventList myList = (EventList) pEvent.getList();
            EventGroup<Event> myGroup = myList.getGroup(pEvent);

            /* Loop through the elements */
            Iterator<Event> myIterator = myGroup.iterator();
            while (myIterator.hasNext()) {
                Event myEvent = myIterator.next();

                /* Check event */
                if (!filterSingleEvent(myEvent)) {
                    return false;
                }
            }

            /* Ignore Event Group */
            return true;
        }

        /* Check as a single event */
        return filterSingleEvent(pEvent);
    }

    /**
     * Should we filter this event out?
     * @param pEvent the event to check
     * @return true/false
     */
    private boolean filterSingleEvent(final Event pEvent) {
        /* If we are filtering on account */
        if ((theFilteredAccounts != null)
            && (filterOnAccount(pEvent))) {
            return true;
        }

        /* If we are filtering on payee */
        if ((theFilteredPayees != null)
            && (filterOnPayee(pEvent))) {
            return true;
        }

        /* If we are filtering on category */
        if ((theFilteredEventCategories != null)
            && (filterOnEventCategory(pEvent))) {
            return true;
        }

        /* Event not rejected by any filter */
        return false;
    }

    /**
     * Should we filter this event out based on account?
     * @param pEvent the event to check
     * @return true/false
     */
    private boolean filterOnAccount(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        Account myThirdParty = pEvent.getThirdParty();

        /* If there is a third party account */
        if ((myThirdParty != null)
            && (!filterOnAccount(myThirdParty))) {
            /* Allow event */
            return false;
        }

        /* If the debit account is an asset */
        if (myDebit.isAsset()) {
            /* Check for filtering on Debit */
            if (!filterOnAccount(myDebit)) {
                return false;
            }

            /* If we should also check the credit account */
            if ((myCredit.isAsset())
                && !myCredit.equals(myDebit)) {
                /* Return result of check */
                return filterOnAccount(myCredit);
            }

            /* Filter this event */
            return true;

            /* else credit account must be asset */
        } else {
            /* Return result of credit check */
            return filterOnAccount(myCredit);
        }
    }

    /**
     * Should we filter this event out based on account?
     * @param pAccount the account to check
     * @return true/false
     */
    private boolean filterOnAccount(final Account pAccount) {
        /* Check for presence of Account in list */
        return theFilteredAccounts.findItemById(pAccount.getId()) == null;
    }

    /**
     * Should we filter this event out based on payee?
     * @param pEvent the event to check
     * @return true/false
     */
    private boolean filterOnPayee(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();

        /* Check the debit account as a payee */
        if ((myDebit.isNonAsset())
            || (myDebit.getAutoExpense() != null)) {
            /* Check for filtering on Debit */
            if (!filterOnPayee(myDebit)) {
                return false;
            }
        }

        /* Check the credit account as a payee */
        if ((myCredit.isNonAsset())
            || (myCredit.getAutoExpense() != null)) {
            /* Check for filtering on Credit */
            if (!filterOnPayee(myCredit)) {
                return false;
            }
        }

        /* Access category */
        EventCategory myCategory = pEvent.getCategory();
        EventCategoryClass myCatClass = myCategory.getCategoryTypeClass();

        /* If this is a payment from the parent */
        if (myCatClass.isDebitParentPayee()) {
            /* Check for filtering on Parent */
            if (!filterOnPayee(myDebit.getParent())) {
                return false;
            }
        }

        /* If this is a expense to the parent */
        if (myCatClass.isCreditParentPayee()) {
            /* Check for filtering on Parent */
            if (!filterOnPayee(myCredit.getParent())) {
                return false;
            }
        }

        /* If we are filtering on TaxMan */
        if (filterOnTaxManPayee) {
            /* Allow if there is tax credit or national insurance */
            return (pEvent.getTaxCredit() == null)
                   && (pEvent.getNatInsurance() == null);
        }

        /* Has not met any rules so filter it */
        return true;
    }

    /**
     * Should we filter this event out based on payee?
     * @param pPayee the payee to check
     * @return true/false
     */
    private boolean filterOnPayee(final Account pPayee) {
        /* Check for presence of Payee in list */
        return theFilteredPayees.findItemById(pPayee.getId()) == null;
    }

    /**
     * Should we filter this event out based on event category?
     * @param pEvent the event to check
     * @return true/false
     */
    private boolean filterOnEventCategory(final Event pEvent) {
        Account myDebit = pEvent.getDebit();
        Account myCredit = pEvent.getCredit();
        EventCategory myDebitAuto = myDebit.getAutoExpense();
        EventCategory myCreditAuto = myCredit.getAutoExpense();
        EventCategory myCategory = pEvent.getCategory();

        /* If we should allow this category */
        if (!filterOnEventCategory(myCategory)) {
            /* Allow event */
            return false;
        }

        /* Check for debit autoExpense */
        if ((myDebitAuto != null)
            && (!filterOnEventCategory(myDebitAuto))) {
            return false;
        }

        /* Check for credit autoExpense */
        if ((myCreditAuto != null)
            && (!filterOnEventCategory(myCreditAuto))) {
            return false;
        }

        /* If the event is interest/dividend */
        if ((pEvent.isInterest())
            || (pEvent.isDividend())) {
            /* Look for filter on detailed category */
            return filterOnEventCategory(myDebit.getDetailedCategory(myCategory));
        }

        /* Look for tax credit filter */
        if ((filterOnTaxCreditCategory)
            && (pEvent.getTaxCredit() != null)) {
            return false;
        }

        /* Look for national insurance filter */
        if ((filterOnNatInsCategory)
            && (pEvent.getNatInsurance() != null)) {
            return false;
        }

        /* Look for deemed benefit filter */
        if ((filterOnBenefitCategory)
            && (pEvent.getDeemedBenefit() != null)) {
            return false;
        }

        /* Look for Charity Donation filter */
        if ((filterOnDonationCategory)
            && (pEvent.getCharityDonation() != null)) {
            return false;
        }

        /* Has not met any rules so filter it */
        return true;
    }

    /**
     * Should we filter this event out based on base category?
     * @param pCategory the category to check
     * @return true/false
     */
    private boolean filterOnEventCategory(final EventCategory pCategory) {
        /* Check for presence of Category in list */
        return theFilteredEventCategories.findItemById(pCategory.getId()) == null;
    }
}
