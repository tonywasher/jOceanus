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

import java.util.Currency;
import java.util.Iterator;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase.AssetBaseList;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

import org.slf4j.Logger;

/**
 * Transaction builder.
 * @author Tony Washer
 */
public class TransactionBuilder {
    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The logger.
     */
    private final Logger theLogger;

    /**
     * The Date Range.
     */
    private JDateDayRange theRange;

    /**
     * The Transaction list.
     */
    private TransactionList theList;

    /**
     * Constructor.
     * @param pUpdateSet the updateSet
     * @param pLogger the logger
     */
    public TransactionBuilder(final UpdateSet<MoneyWiseDataType> pUpdateSet,
                              final Logger pLogger) {
        theUpdateSet = pUpdateSet;
        theLogger = pLogger;
    }

    /**
     * Set parameters.
     * @param pList the transaction list
     * @param pRange the date range
     */
    public void setParameters(final TransactionList pList,
                              final JDateDayRange pRange) {
        theList = pList;
        theRange = pRange;
    }

    /**
     * autoCorrect transaction after change.
     * @param pTrans the transaction
     * @throws JOceanusException on error
     */
    public void autoCorrect(final Transaction pTrans) throws JOceanusException {
        /* Access details */
        TransactionAsset myAccount = pTrans.getAccount();
        TransactionAsset myPartner = pTrans.getPartner();
        TransactionCategory myCategory = pTrans.getCategory();
        AssetDirection myDir = pTrans.getDirection();
        JMoney myAmount = pTrans.getAmount();
        Currency myCurrency = myAccount.getCurrency();

        /* Check that category is valid */
        if (!TransactionValidator.isValidCategory(myAccount, myCategory)) {
            /* Determine valid category */
            myCategory = getDefaultCategoryForAccount(myAccount);
            pTrans.setCategory(myCategory);
        }

        /* Check that direction is valid */
        if (!TransactionValidator.isValidDirection(myAccount, myCategory, myDir)) {
            /* Reverse direction */
            pTrans.switchDirection();
        }

        /* Check that partner is valid */
        if (!TransactionValidator.isValidPartner(myAccount, myCategory, myPartner)) {
            /* Determine valid partner */
            myPartner = getDefaultPartnerForAccountAndCategory(myAccount, myCategory);
            pTrans.setPartner(myPartner);
        }

        /* If we need to change currency */
        if (!myCurrency.equals(myAmount.getCurrency())) {
            /* Convert the currency */
            pTrans.setAmount(myAmount.changeCurrency(myCurrency));
        }

        /* If we need to reset money to zero */
        if (myCategory.getCategoryTypeClass().needsZeroAmount()
            && myAmount.isNonZero()) {
            /* Create a zero amount */
            pTrans.setAmount(new JMoney(myCurrency));
        }

        /* AutoCorrect the InfoSet */
        TransactionInfoSet myInfoSet = pTrans.getInfoSet();
        myInfoSet.autoCorrect(theUpdateSet);
    }

    /**
     * Build empty transaction.
     * @return the new transaction
     */
    private Transaction newTransaction() {
        /* Obtain a new transaction */
        return new Transaction(theList);
    }

    /**
     * Build default transaction.
     * @param pKey the key to base the new transaction around (or null)
     * @return the new transaction (or null if no possible transaction)
     */
    public Transaction buildTransaction(final Object pKey) {
        /* Protect against exceptions */
        try {
            if (pKey == null) {
                /* Build default transaction */
                return buildDefaultTransaction();
            }
            if (pKey instanceof Payee) {
                /* Build default payee transaction */
                return buildDefaultTransactionForPayee((Payee) pKey);
            }
            if (pKey instanceof SecurityHolding) {
                /* Build default holding transaction */
                return buildDefaultTransactionForHolding((SecurityHolding) pKey);
            }
            if (pKey instanceof TransactionAsset) {
                /* Build default account transaction */
                return buildDefaultTransactionForAccount((TransactionAsset) pKey);
            }
            if (pKey instanceof TransactionCategory) {
                /* Build default category transaction */
                return buildDefaultTransactionForCategory((TransactionCategory) pKey);
            }
        } catch (JOceanusException e) {
            theLogger.error("Unable to build transaction", e);
        }

        /* Unrecognised key */
        return null;
    }

    /**
     * Obtain adjusted range.
     * @param pTrans the transaction
     * @return the adjusted range
     */
    public JDateDayRange getAdjustedRange(final Transaction pTrans) {
        /* Access standard range */
        JDateDayRange myRange = theRange;

        /* If the account is a security holding */
        TransactionAsset myAccount = pTrans.getAccount();
        if (myAccount instanceof SecurityHolding) {
            /* Adjust range if necessary */
            myRange = adjustRange(myRange, (SecurityHolding) myAccount);
        }

        /* If the partner is a security holding */
        myAccount = pTrans.getPartner();
        if (myAccount instanceof SecurityHolding) {
            /* Adjust range if necessary */
            myRange = adjustRange(myRange, (SecurityHolding) myAccount);
        }

        /* Return the range */
        return myRange;
    }

    /**
     * Adjust range.
     * @param pRange the initial range
     * @param pHolding the holding
     * @return the adjusted range
     */
    private JDateDayRange adjustRange(final JDateDayRange pRange,
                                      final SecurityHolding pHolding) {
        /* Access relevant dates */
        JDateDay myDate = pHolding.getEarliestDate();
        JDateDay myEarliest = pRange.getStart();

        /* Adjust range if necessary */
        return myDate.compareTo(myEarliest) > 0
                                               ? new JDateDayRange(myDate, pRange.getEnd())
                                               : pRange;
    }

    /**
     * Build standard details.
     * @param pTrans the transaction to build
     * @throws JOceanusException on error
     */
    private void buildStandardDetails(final Transaction pTrans) throws JOceanusException {
        /* Access adjusted range */
        JDateDayRange myRange = getAdjustedRange(pTrans);

        /* Determine date */
        JDateDay myDate = new JDateDay();
        int iResult = myRange.compareTo(myDate);
        if (iResult < 0) {
            myDate = myRange.getEnd();
        } else if (iResult > 0) {
            myDate = myRange.getStart();
        }
        pTrans.setDate(myDate);

        /* Create a zero amount */
        TransactionAsset myAccount = pTrans.getAccount();
        Currency myCurrency = myAccount.getCurrency();
        pTrans.setAmount(new JMoney(myCurrency));
    }

    /**
     * Build default transaction.
     * @return the valid transaction (or null)
     * @throws JOceanusException on error
     */
    private Transaction buildDefaultTransaction() throws JOceanusException {
        TransactionCategory myCategory = getDefaultCategory();
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
     * @throws JOceanusException on error
     */
    private Transaction buildDefaultTransactionForPayee(final Payee pPayee) throws JOceanusException {
        /* Check for closed/hidden payee */
        if (pPayee.isClosed() || pPayee.isHidden()) {
            return null;
        }

        /* Build an empty transaction */
        Transaction myTrans = newTransaction();

        /* Record the payee */
        myTrans.setPartner(pPayee);

        /* Build default category */
        TransactionCategory myCategory = getDefaultCategory();
        if (myCategory == null) {
            return null;
        }
        myTrans.setCategory(myCategory);

        /* Build default account */
        TransactionAsset myAccount = getDefaultAccountForCategory(myCategory);
        if (myAccount == null) {
            return null;
        }
        myTrans.setAccount(myAccount);

        /* Check that we are valid after all this */
        if (!TransactionValidator.isValidPartner(myAccount, myCategory, pPayee)) {
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
     * @throws JOceanusException on error
     */
    private Transaction buildDefaultTransactionForCategory(final TransactionCategory pCategory) throws JOceanusException {
        /* Check for hidden category */
        if (pCategory.isHidden()) {
            return null;
        }

        /* Build an empty transaction */
        Transaction myTrans = newTransaction();

        /* Record the category */
        myTrans.setCategory(pCategory);

        /* Build default account category */
        TransactionAsset myAccount = getDefaultAccountForCategory(pCategory);
        if (myAccount == null) {
            return null;
        }
        myTrans.setAccount(myAccount);

        /* Build default partner */
        TransactionAsset myPartner = getDefaultPartnerForAccountAndCategory(myAccount, pCategory);
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
     * @throws JOceanusException on error
     */
    private Transaction buildDefaultTransactionForAccount(final TransactionAsset pAccount) throws JOceanusException {
        /* Check for closed account */
        if (pAccount.isClosed()) {
            return null;
        }

        /* Build an empty transaction */
        Transaction myTrans = newTransaction();

        /* Record the account */
        myTrans.setAccount(pAccount);

        /* Build default expense category */
        TransactionCategory myCategory = getDefaultCategory();
        if (myCategory == null) {
            return null;
        }
        myTrans.setCategory(myCategory);

        /* Build default partner */
        TransactionAsset myPartner = getDefaultPartnerForAccountAndCategory(pAccount, myCategory);
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
     * @throws JOceanusException on error
     */
    private Transaction buildDefaultTransactionForHolding(final SecurityHolding pHolding) throws JOceanusException {
        /* Check for closed holding */
        if (pHolding.isClosed()) {
            return null;
        }

        /* Build an empty transaction */
        Transaction myTrans = newTransaction();

        /* Record the account */
        myTrans.setAccount(pHolding);

        /* Build default category */
        TransactionCategory myCategory = getDefaultCategoryForAccount(pHolding);
        if (myCategory == null) {
            return null;
        }
        myTrans.setCategory(myCategory);

        /* Build default partner */
        TransactionAsset myPartner = getDefaultPartnerForAccountAndCategory(pHolding, myCategory);
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
    private TransactionAsset getDefaultAccountForCategory(final TransactionCategory pCategory) {
        /* Try deposits/cash/loans */
        TransactionAsset myAccount = getDefaultAssetForCategory(theUpdateSet.getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class), pCategory);
        if (myAccount == null) {
            myAccount = getDefaultAssetForCategory(theUpdateSet.getDataList(MoneyWiseDataType.CASH, CashList.class), pCategory);
        }
        if (myAccount == null) {
            myAccount = getDefaultAssetForCategory(theUpdateSet.getDataList(MoneyWiseDataType.LOAN, LoanList.class), pCategory);
        }

        /* Try holdings */
        if (myAccount == null) {
            myAccount = getDefaultHolding(theUpdateSet, pCategory);
        }

        /* Try portfolios */
        if (myAccount == null) {
            myAccount = getDefaultAssetForCategory(theUpdateSet.getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class), pCategory);
        }

        /* Return the account */
        return myAccount;
    }

    /**
     * Obtain default category.
     * @return the default category
     */
    private TransactionCategory getDefaultCategory() {
        /* Look for category in order of expense, income, transfer */
        TransactionCategory myCategory = getDefaultCategory(CategoryType.EXPENSE);
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
    private TransactionCategory getDefaultCategory(final CategoryType pType) {
        /* Access Categories */
        TransactionCategoryList myCategories = theUpdateSet.getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);

        /* Loop through the available category values */
        Iterator<TransactionCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            TransactionCategory myCategory = myIterator.next();

            /* Only process non-deleted low-level items */
            TransactionCategoryClass myClass = myCategory.getCategoryTypeClass();
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
    private TransactionCategory getDefaultCategoryForAccount(final TransactionAsset pAccount) {
        /* Access Categories */
        TransactionCategoryList myCategories = theUpdateSet.getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);

        /* Loop through the available category values */
        Iterator<TransactionCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            TransactionCategory myCategory = myIterator.next();

            /* Only process non-deleted low-level items */
            TransactionCategoryClass myClass = myCategory.getCategoryTypeClass();
            if (myCategory.isDeleted() || myClass.canParentCategory()) {
                continue;
            }

            /* Check whether the category is allowable for the owner */
            if (TransactionValidator.isValidCategory(pAccount, myCategory)) {
                return myCategory;
            }
        }

        /* No category available */
        return null;
    }

    /**
     * Obtain default partner for account and category.
     * @param pAccount the account
     * @param pCategory the category
     * @return the default partner
     */
    private TransactionAsset getDefaultPartnerForAccountAndCategory(final TransactionAsset pAccount,
                                                                    final TransactionCategory pCategory) {
        /* Try Payees */
        TransactionAsset myPartner = getDefaultPartnerAsset(theUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class), pAccount, pCategory);

        /* Try deposits/cash/loans */
        if (myPartner == null) {
            myPartner = getDefaultPartnerAsset(theUpdateSet.getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class), pAccount, pCategory);
        }

        if (myPartner == null) {
            myPartner = getDefaultPartnerAsset(theUpdateSet.getDataList(MoneyWiseDataType.CASH, CashList.class), pAccount, pCategory);
        }
        if (myPartner == null) {
            myPartner = getDefaultPartnerAsset(theUpdateSet.getDataList(MoneyWiseDataType.LOAN, LoanList.class), pAccount, pCategory);
        }

        /* Try portfolios */
        if (myPartner == null) {
            myPartner = getDefaultPartnerAsset(theUpdateSet.getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class), pAccount, pCategory);
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
    private <X extends AssetBase<X>> TransactionAsset getDefaultAssetForCategory(final AssetBaseList<X> pList,
                                                                                 final TransactionCategory pCategory) {
        /* Loop through the available values */
        Iterator<X> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            X myAsset = myIterator.next();

            /* Only process non-deleted, non-closed items */
            if (myAsset.isDeleted() || myAsset.isClosed()) {
                continue;
            }

            /* Check whether the asset is allowable for the owner */
            if (TransactionValidator.isValidCategory(myAsset, pCategory)) {
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
    private <X extends AssetBase<X>> TransactionAsset getDefaultPartnerAsset(final AssetBaseList<X> pList,
                                                                             final TransactionAsset pAccount,
                                                                             final TransactionCategory pCategory) {
        /* Loop through the available values */
        Iterator<X> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            X myAsset = myIterator.next();

            /* Only process non-deleted, non-closed items */
            if (myAsset.isDeleted() || myAsset.isClosed()) {
                continue;
            }

            /* Check whether the asset is allowable for the owner */
            if (TransactionValidator.isValidPartner(pAccount, pCategory, myAsset)) {
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
    private SecurityHolding getDefaultHolding(final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                              final TransactionCategory pCategory) {
        /* Access Portfolios and Holdings Map */
        MoneyWiseData myData = pUpdateSet.getDataSet(MoneyWiseData.class);
        PortfolioList myPortfolios = pUpdateSet.getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
        SecurityHoldingMap myMap = myData.getSecurityHoldingsMap();

        /* Loop through the Portfolios */
        Iterator<Portfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            Portfolio myPortfolio = myPortIterator.next();

            /* Ignore deleted or closed */
            if (myPortfolio.isDeleted() || myPortfolio.isClosed()) {
                continue;
            }

            /* Look for existing holdings */
            Iterator<SecurityHolding> myExistIterator = myMap.existingIterator(myPortfolio);
            if (myExistIterator != null) {
                /* Loop through them */
                while (myExistIterator.hasNext()) {
                    SecurityHolding myHolding = myExistIterator.next();

                    /* check that initial price is not too late */
                    JDateDay myEarliest = myHolding.getEarliestDate();
                    if (myEarliest == null
                        || theRange.compareTo(myEarliest) < 0) {
                        continue;
                    }

                    /* Check whether the asset is allowable for the combination */
                    if (TransactionValidator.isValidCategory(myHolding, pCategory)) {
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
     * @param pUpdateSet the update set
     * @param pAccount the account
     * @param pCategory the category
     * @return the default partner
     */
    private SecurityHolding getDefaultPartnerHolding(final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                                     final TransactionAsset pAccount,
                                                     final TransactionCategory pCategory) {
        /* Access Portfolios and Holdings Map */
        MoneyWiseData myData = pUpdateSet.getDataSet(MoneyWiseData.class);
        PortfolioList myPortfolios = pUpdateSet.getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
        SecurityHoldingMap myMap = myData.getSecurityHoldingsMap();

        /* Loop through the Portfolios */
        Iterator<Portfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            Portfolio myPortfolio = myPortIterator.next();

            /* Ignore deleted or closed */
            if (myPortfolio.isDeleted() || myPortfolio.isClosed()) {
                continue;
            }

            /* Look for existing holdings */
            Iterator<SecurityHolding> myExistIterator = myMap.existingIterator(myPortfolio);
            if (myExistIterator != null) {
                /* Loop through them */
                while (myExistIterator.hasNext()) {
                    SecurityHolding myHolding = myExistIterator.next();

                    /* check that initial price is not too late */
                    JDateDay myEarliest = myHolding.getEarliestDate();
                    if (myEarliest == null
                        || theRange.compareTo(myEarliest) < 0) {
                        continue;
                    }

                    /* Check whether the asset is allowable for the combination */
                    if (TransactionValidator.isValidPartner(pAccount, pCategory, myHolding)) {
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
