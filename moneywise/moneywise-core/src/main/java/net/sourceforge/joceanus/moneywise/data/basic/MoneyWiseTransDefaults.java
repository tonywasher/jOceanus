/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.basic;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase.MoneyWiseAssetBaseList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataValidator.MoneyWiseDataValidatorTrans;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan.MoneyWiseLoanList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding.MoneyWiseSecurityHoldingMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Currency;
import java.util.Iterator;

/**
 * Transaction builder.
 * @author Tony Washer
 */
public class MoneyWiseTransDefaults {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MoneyWiseTransDefaults.class);

    /**
     * The updateSet.
     */
    private final PrometheusEditSet theUpdateSet;

    /**
     * The Date Range.
     */
    private OceanusDateRange theRange;

    /**
     * The Transaction list.
     */
    private MoneyWiseTransactionList theList;

    /**
     * Constructor.
     * @param pUpdateSet the updateSet
     */
    public MoneyWiseTransDefaults(final PrometheusEditSet pUpdateSet) {
        theUpdateSet = pUpdateSet;
    }

    /**
     * Obtain range.
     * @return the date range
     */
    public OceanusDateRange getRange() {
        return theRange;
    }

    /**
     * Set parameters.
     * @param pList the transaction list
     * @param pRange the date range
     */
    public void setParameters(final MoneyWiseTransactionList pList,
                              final OceanusDateRange pRange) {
        theList = pList;
        theRange = pRange;
    }

    /**
     * autoCorrect transaction after change.
     * @param pTrans the transaction
     * @throws OceanusException on error
     */
    public void autoCorrect(final MoneyWiseTransaction pTrans) throws OceanusException {
        /* Access details */
        final MoneyWiseTransAsset myAccount = pTrans.getAccount();
        MoneyWiseTransAsset myPartner = pTrans.getPartner();
        MoneyWiseTransCategory myCategory = pTrans.getCategory();
        final MoneyWiseAssetDirection myDir = pTrans.getDirection();
        final MoneyWiseDataValidatorTrans<?> myValidator = pTrans.getList().getValidator();
        final OceanusMoney myAmount = pTrans.getAmount();
        final Currency myCurrency = myAccount.getCurrency();

        /* Check that category is valid */
        if (!myValidator.isValidCategory(myAccount, myCategory)) {
            /* Determine valid category */
            myCategory = getDefaultCategoryForAccount(myAccount);
            pTrans.setCategory(myCategory);
        }

        /* Check that direction is valid */
        if (!myValidator.isValidDirection(myAccount, myCategory, myDir)) {
            /* Reverse direction */
            pTrans.switchDirection();
        }

        /* Check that partner is valid */
        if (!myValidator.isValidPartner(myAccount, myCategory, myPartner)) {
            /* Determine valid partner */
            myPartner = getDefaultPartnerForAccountAndCategory(myAccount, myCategory);
            pTrans.setPartner(myPartner);
        }

        /* If we need to null money */
        if (myCategory.getCategoryTypeClass().needsNullAmount()) {
            if (myAmount != null) {
                /* Create a zero amount */
                pTrans.setAmount(null);
            }

            /* Else money is required */
        } else {
            if (myAmount == null) {
                /* Create a zero amount */
                pTrans.setAmount(new OceanusMoney(myCurrency));

                /* If we need to change currency */
            } else if (!myCurrency.equals(myAmount.getCurrency())) {
                /* Convert the currency */
                pTrans.setAmount(myAmount.changeCurrency(myCurrency));
            }
        }

        /* AutoCorrect the InfoSet */
        final MoneyWiseTransInfoSet myInfoSet = pTrans.getInfoSet();
        myInfoSet.autoCorrect(theUpdateSet);
    }

    /**
     * Build empty transaction.
     * @return the new transaction
     */
    private MoneyWiseTransaction newTransaction() {
        /* Obtain a new transaction */
        return new MoneyWiseTransaction(theList);
    }

    /**
     * Build default transaction.
     * @param pKey the key to base the new transaction around (or null)
     * @return the new transaction (or null if no possible transaction)
     */
    public MoneyWiseTransaction buildTransaction(final Object pKey) {
        /* Protect against exceptions */
        try {
            if (pKey == null) {
                /* Build default transaction */
                return buildDefaultTransaction();
            }
            if (pKey instanceof MoneyWisePayee) {
                /* Build default payee transaction */
                return buildDefaultTransactionForPayee((MoneyWisePayee) pKey);
            }
            if (pKey instanceof MoneyWiseSecurityHolding) {
                /* Build default holding transaction */
                return buildDefaultTransactionForHolding((MoneyWiseSecurityHolding) pKey);
            }
            if (pKey instanceof MoneyWiseTransAsset) {
                /* Build default account transaction */
                return buildDefaultTransactionForAccount((MoneyWiseTransAsset) pKey);
            }
            if (pKey instanceof MoneyWiseTransCategory) {
                /* Build default category transaction */
                return buildDefaultTransactionForCategory((MoneyWiseTransCategory) pKey);
            }
        } catch (OceanusException e) {
            LOGGER.error("Unable to build transaction", e);
        }

        /* Unrecognised key */
        return null;
    }

    /**
     * Build standard details.
     * @param pTrans the transaction to build
     * @throws OceanusException on error
     */
    private void buildStandardDetails(final MoneyWiseTransaction pTrans) throws OceanusException {
        /* Access standard range */
        final OceanusDateRange myRange = theRange;

        /* Set default direction */
        pTrans.setDirection(MoneyWiseAssetDirection.TO);

        /* Determine date */
        OceanusDate myDate = new OceanusDate();
        final int iResult = myRange.compareToDate(myDate);
        if (iResult < 0) {
            myDate = myRange.getEnd();
        } else if (iResult > 0) {
            myDate = myRange.getStart();
        }
        pTrans.setDate(myDate);

        /* Create a zero amount */
        final MoneyWiseTransAsset myAccount = pTrans.getAccount();
        final Currency myCurrency = myAccount.getCurrency();
        pTrans.setAmount(new OceanusMoney(myCurrency));
    }

    /**
     * Build default transaction.
     * @return the valid transaction (or null)
     * @throws OceanusException on error
     */
    private MoneyWiseTransaction buildDefaultTransaction() throws OceanusException {
        final MoneyWiseTransCategory myCategory = getDefaultCategory();
        if (myCategory == null) {
            return null;
        }

        /* Look for transaction for this category */
        return buildDefaultTransactionForCategory(myCategory);
    }

    /**
     * Build default transaction for payee.
     * @param pPayee the payee to build for
     * @return the valid transaction (or null)
     * @throws OceanusException on error
     */
    private MoneyWiseTransaction buildDefaultTransactionForPayee(final MoneyWisePayee pPayee) throws OceanusException {
        /* Check for closed/hidden payee */
        if (pPayee.isClosed() || pPayee.isHidden()) {
            return null;
        }

        /* Build an empty transaction */
        final MoneyWiseTransaction myTrans = newTransaction();

        /* Record the payee */
        myTrans.setPartner(pPayee);

        /* Build default category */
        final MoneyWiseTransCategory myCategory = getDefaultCategory();
        if (myCategory == null) {
            return null;
        }
        myTrans.setCategory(myCategory);

        /* Build default account */
        final MoneyWiseTransAsset myAccount = getDefaultAccountForCategory(myCategory);
        if (myAccount == null) {
            return null;
        }
        myTrans.setAccount(myAccount);

        /* Check that we are valid after all this */
        if (!myTrans.getList().getValidator().isValidPartner(myAccount, myCategory, pPayee)) {
            return null;
        }

        /* build standard details */
        buildStandardDetails(myTrans);

        /* AutoCorrect the transaction */
        autoCorrect(myTrans);

        /* Return the new transaction */
        return myTrans;
    }

    /**
     * Build default transaction for category.
     * @param pCategory the category to build for
     * @return the valid transaction (or null)
     * @throws OceanusException on error
     */
    private MoneyWiseTransaction buildDefaultTransactionForCategory(final MoneyWiseTransCategory pCategory) throws OceanusException {
        /* Check for hidden category */
        if (pCategory.isHidden()) {
            return null;
        }

        /* Build an empty transaction */
        final MoneyWiseTransaction myTrans = newTransaction();

        /* Record the category */
        myTrans.setCategory(pCategory);

        /* Build default account category */
        final MoneyWiseTransAsset myAccount = getDefaultAccountForCategory(pCategory);
        if (myAccount == null) {
            return null;
        }
        myTrans.setAccount(myAccount);

        /* Build default partner */
        final MoneyWiseTransAsset myPartner = getDefaultPartnerForAccountAndCategory(myAccount, pCategory);
        if (myPartner == null) {
            return null;
        }
        myTrans.setPartner(myPartner);

        /* build standard details */
        buildStandardDetails(myTrans);

        /* AutoCorrect the transaction */
        autoCorrect(myTrans);

        /* Return the new transaction */
        return myTrans;
    }

    /**
     * Build default transaction for Deposit/Loan/Cash.
     * @param pAccount the Deposit/Loan/Cash to build for
     * @return the valid transaction (or null)
     * @throws OceanusException on error
     */
    private MoneyWiseTransaction buildDefaultTransactionForAccount(final MoneyWiseTransAsset pAccount) throws OceanusException {
        /* Check for closed account */
        if (pAccount.isClosed()) {
            return null;
        }

        /* Build an empty transaction */
        final MoneyWiseTransaction myTrans = newTransaction();

        /* Record the account */
        myTrans.setAccount(pAccount);

        /* Build default expense category */
        final MoneyWiseTransCategory myCategory = getDefaultCategory();
        if (myCategory == null) {
            return null;
        }
        myTrans.setCategory(myCategory);

        /* Build default partner */
        final MoneyWiseTransAsset myPartner = getDefaultPartnerForAccountAndCategory(pAccount, myCategory);
        if (myPartner == null) {
            return null;
        }
        myTrans.setPartner(myPartner);

        /* build standard details */
        buildStandardDetails(myTrans);

        /* AutoCorrect the transaction */
        autoCorrect(myTrans);

        /* Return the new transaction */
        return myTrans;
    }

    /**
     * Build default transaction for securityHolding.
     * @param pHolding the SecurityHolding to build for
     * @return the valid transaction (or null)
     * @throws OceanusException on error
     */
    private MoneyWiseTransaction buildDefaultTransactionForHolding(final MoneyWiseSecurityHolding pHolding) throws OceanusException {
        /* Check for closed holding */
        if (pHolding.isClosed()) {
            return null;
        }

        /* Build an empty transaction */
        final MoneyWiseTransaction myTrans = newTransaction();

        /* Record the account */
        myTrans.setAccount(pHolding);

        /* Build default category */
        final MoneyWiseTransCategory myCategory = getDefaultCategoryForAccount(pHolding);
        myTrans.setCategory(myCategory);

        /* Build default partner */
        final MoneyWiseTransAsset myPartner = getDefaultPartnerForAccountAndCategory(pHolding, myCategory);
        if (myPartner == null) {
            return null;
        }
        myTrans.setPartner(myPartner);

        /* build standard details */
        buildStandardDetails(myTrans);

        /* AutoCorrect the transaction */
        autoCorrect(myTrans);

        /* Return the new transaction */
        return myTrans;
    }

    /**
     * Obtain default account for category.
     * @param pCategory the category
     * @return the default account
     */
    private MoneyWiseTransAsset getDefaultAccountForCategory(final MoneyWiseTransCategory pCategory) {
        /* Try deposits/cash/loans */
        MoneyWiseTransAsset myAccount = getDefaultAssetForCategory(theUpdateSet.getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class), pCategory);
        if (myAccount == null) {
            myAccount = getDefaultAssetForCategory(theUpdateSet.getDataList(MoneyWiseBasicDataType.CASH, MoneyWiseCashList.class), pCategory);
        }
        if (myAccount == null) {
            myAccount = getDefaultAssetForCategory(theUpdateSet.getDataList(MoneyWiseBasicDataType.LOAN, MoneyWiseLoanList.class), pCategory);
        }

        /* Try holdings */
        if (myAccount == null) {
            myAccount = getDefaultHolding(theUpdateSet, pCategory);
        }

        /* Try portfolios */
        if (myAccount == null) {
            myAccount = getDefaultAssetForCategory(theUpdateSet.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class), pCategory);
        }

        /* Return the account */
        return myAccount;
    }

    /**
     * Obtain default category.
     * @return the default category
     */
    private MoneyWiseTransCategory getDefaultCategory() {
        /* Look for category in order of expense, income, transfer */
        MoneyWiseTransCategory myCategory = getDefaultCategory(CategoryType.EXPENSE);
        if (myCategory == null) {
            myCategory = getDefaultCategory(CategoryType.INCOME);
        }
        if (myCategory == null) {
            myCategory = getDefaultCategory(CategoryType.TRANSFER);
        }

        /* Return category */
        return myCategory;
    }

    /**
     * Obtain default category.
     * @param pType the category type
     * @return the default category
     */
    private MoneyWiseTransCategory getDefaultCategory(final CategoryType pType) {
        /* Access Categories */
        final MoneyWiseTransCategoryList myCategories = theUpdateSet.getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class);

        /* Loop through the available category values */
        final Iterator<MoneyWiseTransCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransCategory myCategory = myIterator.next();

            /* Only process non-deleted low-level items */
            final MoneyWiseTransCategoryClass myClass = myCategory.getCategoryTypeClass();
            if (myCategory.isDeleted() || myClass.canParentCategory()) {
                continue;
            }

            /* Switch on type */
            switch (pType) {
                case EXPENSE:
                    if (myClass.isExpense()) {
                        return myCategory;
                    }
                    break;
                case INCOME:
                    if (myClass.isIncome()) {
                        return myCategory;
                    }
                    break;
                case TRANSFER:
                default:
                    if (myClass.isTransfer()) {
                        return myCategory;
                    }
                    break;
            }
        }

        /* No category available */
        return null;
    }

    /**
     * Obtain default category for account.
     * @param pAccount the account
     * @return the default category
     */
    private MoneyWiseTransCategory getDefaultCategoryForAccount(final MoneyWiseTransAsset pAccount) {
        /* Access Categories */
        final MoneyWiseTransCategoryList myCategories = theUpdateSet.getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class);
        final MoneyWiseDataValidatorTrans<?> myValidator = theList.getValidator();

        /* Loop through the available category values */
        final Iterator<MoneyWiseTransCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransCategory myCategory = myIterator.next();

            /* Only process non-deleted low-level items */
            final MoneyWiseTransCategoryClass myClass = myCategory.getCategoryTypeClass();
            if (myCategory.isDeleted() || myClass.canParentCategory()) {
                continue;
            }

            /* Check whether the category is allowable for the owner */
            if (myValidator.isValidCategory(pAccount, myCategory)) {
                return myCategory;
            }
        }

        /* No category available */
        throw new IllegalArgumentException();
    }

    /**
     * Obtain default partner for account and category.
     * @param pAccount the account
     * @param pCategory the category
     * @return the default partner
     */
    private MoneyWiseTransAsset getDefaultPartnerForAccountAndCategory(final MoneyWiseTransAsset pAccount,
                                                                       final MoneyWiseTransCategory pCategory) {
        /* Try Payees */
        MoneyWiseTransAsset myPartner = getDefaultPartnerAsset(theUpdateSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class), pAccount, pCategory);

        /* Try deposits/cash/loans */
        if (myPartner == null) {
            myPartner = getDefaultPartnerAsset(theUpdateSet.getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class), pAccount, pCategory);
        }
        if (myPartner == null) {
            myPartner = getDefaultPartnerAsset(theUpdateSet.getDataList(MoneyWiseBasicDataType.CASH, MoneyWiseCashList.class), pAccount, pCategory);
        }
        if (myPartner == null) {
            myPartner = getDefaultPartnerAsset(theUpdateSet.getDataList(MoneyWiseBasicDataType.LOAN, MoneyWiseLoanList.class), pAccount, pCategory);
        }

        /* Try portfolios */
        if (myPartner == null) {
            myPartner = getDefaultPartnerAsset(theUpdateSet.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class), pAccount, pCategory);
        }

        /* Try holdings */
        if (myPartner == null) {
            myPartner = getDefaultPartnerHolding(theUpdateSet, pAccount, pCategory);
        }

        /* Return the partner */
        return myPartner;
    }

    /**
     * Obtain the default account from an asset list.
     * @param <X> the Asset type
     * @param pList the list to select from
     * @param pCategory the category
     * @return the default partner or null
     */
    private <X extends MoneyWiseAssetBase> MoneyWiseTransAsset getDefaultAssetForCategory(final MoneyWiseAssetBaseList<X> pList,
                                                                                          final MoneyWiseTransCategory pCategory) {
        /* Loop through the available values */
        final MoneyWiseDataValidatorTrans<?> myValidator = theList.getValidator();
        final Iterator<X> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            final X myAsset = myIterator.next();

            /* Only process non-deleted, non-closed items */
            if (myAsset.isDeleted() || myAsset.isClosed()) {
                continue;
            }

            /* Check whether the asset is allowable for the owner */
            if (myValidator.isValidCategory(myAsset, pCategory)) {
                return myAsset;
            }
        }

        /* No asset available */
        return null;
    }

    /**
     * Obtain the default partner from an asset list.
     * @param <X> the Asset type
     * @param pList the list to select from
     * @param pAccount the account
     * @param pCategory the category
     * @return the default partner or null
     */
    private <X extends MoneyWiseAssetBase> MoneyWiseTransAsset getDefaultPartnerAsset(final MoneyWiseAssetBaseList<X> pList,
                                                                                      final MoneyWiseTransAsset pAccount,
                                                                                      final MoneyWiseTransCategory pCategory) {
        /* Loop through the available values */
        final MoneyWiseDataValidatorTrans<?> myValidator = theList.getValidator();
        final Iterator<X> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            final X myAsset = myIterator.next();

            /* Only process non-deleted, non-closed items */
            if (myAsset.isDeleted() || myAsset.isClosed()) {
                continue;
            }

            /* Check whether the asset is allowable for the owner */
            if (myValidator.isValidPartner(pAccount, pCategory, myAsset)) {
                return myAsset;
            }
        }

        /* No asset available */
        return null;
    }

    /**
     * Obtain the default security holding from the security map.
     * @param pUpdateSet the update set
     * @param pCategory the category
     * @return the default partner
     */
    private MoneyWiseSecurityHolding getDefaultHolding(final PrometheusEditSet pUpdateSet,
                                                       final MoneyWiseTransCategory pCategory) {
        /* Access Portfolios and Holdings Map */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pUpdateSet.getDataSet();
        final MoneyWisePortfolioList myPortfolios = pUpdateSet.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class);
        final MoneyWiseSecurityHoldingMap myMap = myData.getPortfolios().getSecurityHoldingsMap();
        final MoneyWiseDataValidatorTrans<?> myValidator = theList.getValidator();

        /* Loop through the Portfolios */
        final Iterator<MoneyWisePortfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWisePortfolio myPortfolio = myPortIterator.next();

            /* Ignore deleted or closed */
            if (myPortfolio.isDeleted() || myPortfolio.isClosed()) {
                continue;
            }

            /* Look for existing holdings */
            final Iterator<MoneyWiseSecurityHolding> myExistIterator = myMap.existingIterator(myPortfolio);
            if (myExistIterator != null) {
                /* Loop through them */
                while (myExistIterator.hasNext()) {
                    final MoneyWiseSecurityHolding myHolding = myExistIterator.next();

                    /* Check whether the asset is allowable for the combination */
                    if (myValidator.isValidCategory(myHolding, pCategory)) {
                        return myHolding;
                    }
                }
            }
        }

        /* No holding available */
        return null;
    }

    /**
     * Obtain the default partner security holding from the security map.
     * @param pEditSet the edit set
     * @param pAccount the account
     * @param pCategory the category
     * @return the default partner
     */
    private MoneyWiseSecurityHolding getDefaultPartnerHolding(final PrometheusEditSet pEditSet,
                                                              final MoneyWiseTransAsset pAccount,
                                                              final MoneyWiseTransCategory pCategory) {
        /* Access Portfolios and Holdings Map */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pEditSet.getDataSet();
        final MoneyWisePortfolioList myPortfolios = pEditSet.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class);
        final MoneyWiseSecurityHoldingMap myMap = myData.getPortfolios().getSecurityHoldingsMap();
        final MoneyWiseDataValidatorTrans<?> myValidator = theList.getValidator();

        /* Loop through the Portfolios */
        final Iterator<MoneyWisePortfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWisePortfolio myPortfolio = myPortIterator.next();

            /* Ignore deleted or closed */
            if (myPortfolio.isDeleted() || myPortfolio.isClosed()) {
                continue;
            }

            /* Look for existing holdings */
            final Iterator<MoneyWiseSecurityHolding> myExistIterator = myMap.existingIterator(myPortfolio);
            if (myExistIterator != null) {
                /* Loop through them */
                while (myExistIterator.hasNext()) {
                    final MoneyWiseSecurityHolding myHolding = myExistIterator.next();

                    /* Check whether the asset is allowable for the combination */
                    if (myValidator.isValidPartner(pAccount, pCategory, myHolding)) {
                        return myHolding;
                    }
                }
            }
        }

        /* No holding available */
        return null;
    }

    /**
     * Category types.
     */
    private enum CategoryType {
        /**
         * Expense.
         */
        EXPENSE,

        /**
         * Income.
         */
        INCOME,

        /**
         * Transfer.
         */
        TRANSFER;
    }
}
