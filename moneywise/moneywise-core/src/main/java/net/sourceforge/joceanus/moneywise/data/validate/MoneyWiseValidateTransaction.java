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
package net.sourceforge.joceanus.moneywise.data.validate;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataValidator.MoneyWiseDataValidatorTrans;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransInfoSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Currency;

/**
 * Validator for transaction.
 */
public class MoneyWiseValidateTransaction
        implements MoneyWiseDataValidatorTrans<MoneyWiseTransaction> {
    /**
     * Are we using new validation?
     */
    private final boolean newValidation;

    /**
     * The infoSet validator.
     */
    private final MoneyWiseValidateTransInfoSet theInfoSet;

    /**
     * The defaults engine.
     */
    private final MoneyWiseValidateTransDefaults theDefaults;

    /**
     * Set the editSet.
     */
    private PrometheusEditSet theEditSet;

    /**
     * Constructor.
     * @param pNewValidation true/false
     */
    MoneyWiseValidateTransaction(final boolean pNewValidation) {
        newValidation = pNewValidation;
        theInfoSet = new MoneyWiseValidateTransInfoSet(pNewValidation);
        theDefaults = new MoneyWiseValidateTransDefaults(this);
    }

    @Override
    public void setEditSet(final PrometheusEditSet pEditSet) {
        theEditSet = pEditSet;
    }

    /**
     * Obtain the editSet
     * @return the editSet
     */
    PrometheusEditSet getEditSet() {
        if (theEditSet == null) {
            throw new IllegalStateException("editSet not set up");
        }
        return theEditSet;
    }

    @Override
    public void validate(final PrometheusDataItem pTrans) {
        final MoneyWiseTransaction myTrans = (MoneyWiseTransaction) pTrans;
        final OceanusDate myDate = myTrans.getDate();
        final MoneyWiseTransAsset myAccount = myTrans.getAccount();
        final MoneyWiseTransAsset myPartner = myTrans.getPartner();
        final MoneyWiseTransCategory myCategory = myTrans.getCategory();
        final MoneyWiseAssetDirection myDir = myTrans.getDirection();
        final OceanusMoney myAmount = myTrans.getAmount();
        final OceanusUnits myAccountUnits = myTrans.getAccountDeltaUnits();
        final OceanusUnits myPartnerUnits = myTrans.getPartnerDeltaUnits();
        boolean doCheckCombo = true;

        /* Header is always valid */
        if (pTrans.isHeader()) {
            pTrans.setValidEdit();
            return;
        }

        /* Determine date range to check for */
        final OceanusDateRange myRange = myTrans.getDataSet().getDateRange();

        /* The date must be non-null */
        if (myDate == null) {
            pTrans.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            /* The date must be in-range */
        } else if (myRange.compareToDate(myDate) != 0) {
            pTrans.addError(PrometheusDataItem.ERROR_RANGE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        }

        /* Account must be non-null */
        if (myAccount == null) {
            pTrans.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.TRANSACTION_ACCOUNT);
            doCheckCombo = false;

        } else {
            /* Account must be valid */
            if (!isValidAccount(myAccount)) {
                pTrans.addError(MoneyWiseTransBase.ERROR_COMBO, MoneyWiseBasicResource.TRANSACTION_ACCOUNT);
                doCheckCombo = false;
            }
        }

        /* Category must be non-null */
        if (myCategory == null) {
            pTrans.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicDataType.TRANSCATEGORY);
            doCheckCombo = false;

            /* Category must be valid for Account */
        } else if (doCheckCombo
                && !isValidCategory(myAccount, myCategory)) {
            pTrans.addError(MoneyWiseTransBase.ERROR_COMBO, MoneyWiseBasicDataType.TRANSCATEGORY);
            doCheckCombo = false;
        }

        /* Direction must be non-null */
        if (myDir == null) {
            pTrans.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.TRANSACTION_DIRECTION);
            doCheckCombo = false;

            /* Direction must be valid for Account */
        } else if (doCheckCombo
                && !isValidDirection(myAccount, myCategory, myDir)) {
            pTrans.addError(MoneyWiseTransBase.ERROR_COMBO, MoneyWiseBasicResource.TRANSACTION_DIRECTION);
            doCheckCombo = false;
        }

        /* Partner must be non-null */
        if (myPartner == null) {
            pTrans.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.TRANSACTION_PARTNER);

        } else {
            /* Partner must be valid for Account */
            if (doCheckCombo
                    && !isValidPartner(myAccount, myCategory, myPartner)) {
                pTrans.addError(MoneyWiseTransBase.ERROR_COMBO, MoneyWiseBasicResource.TRANSACTION_PARTNER);
            }
        }

        /* If money is null */
        if (myAmount == null) {
            /* Check that it must be null */
            if (!needsNullAmount(myTrans)) {
                pTrans.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.TRANSACTION_AMOUNT);
            }

            /* else non-null money */
        } else {
            /* Check that it must be null */
            if (needsNullAmount(myTrans)) {
                pTrans.addError(PrometheusDataItem.ERROR_EXIST, MoneyWiseBasicResource.TRANSACTION_AMOUNT);
            }

            /* Money must not be negative */
            if (!myAmount.isPositive()) {
                pTrans.addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseBasicResource.TRANSACTION_AMOUNT);
            }

            /* Check that amount is correct currency */
            if (myAccount != null) {
                final Currency myCurrency = myAccount.getCurrency();
                if (!myAmount.getCurrency().equals(myCurrency)) {
                    pTrans.addError(MoneyWiseTransBase.ERROR_CURRENCY, MoneyWiseBasicResource.TRANSACTION_AMOUNT);
                }
            }
        }

        /* Cannot have PartnerUnits if securities are identical */
        if (myAccountUnits != null
                && myPartnerUnits != null
                && MetisDataDifference.isEqual(myAccount, myPartner)) {
            pTrans.addError(MoneyWiseTransaction.ERROR_CIRCULAR, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.PARTNERDELTAUNITS));
        }

        /* If we have a category and an infoSet */
        if (myCategory != null
                && myTrans.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(myTrans.getInfoSet());
        }

        /* Set validation flag */
        if (!pTrans.hasErrors()) {
            pTrans.setValidEdit();
        }
    }

    /**
     * Determines whether an event needs a zero amount.
     * @param pTrans the transaction
     * @return true/false
     */
    public boolean needsNullAmount(final MoneyWiseTransaction pTrans) {
        final MoneyWiseTransCategoryClass myClass = pTrans.getCategoryClass();
        return myClass != null
                && myClass.needsNullAmount();
    }

    @Override
    public boolean isValidAccount(final MoneyWiseTransAsset pAccount) {
        /* Validate securityHolding */
        if (pAccount instanceof MoneyWiseSecurityHolding
                && !checkSecurityHolding((MoneyWiseSecurityHolding) pAccount)) {
            return false;
        }

        /* Reject pensions portfolio */
        if (pAccount instanceof MoneyWisePortfolio
                && ((MoneyWisePortfolio) pAccount).getCategoryClass().holdsPensions()) {
            return false;
        }

        /* Check type of account */
        final MoneyWiseAssetType myType = pAccount.getAssetType();
        return myType.isBaseAccount() && !pAccount.isHidden();
    }

    @Override
    public boolean isValidCategory(final MoneyWiseTransAsset pAccount,
                                   final MoneyWiseTransCategory pCategory) {
        /* Access details */
        final MoneyWiseAssetType myType = pAccount.getAssetType();
        final MoneyWiseTransCategoryClass myCatClass = pCategory.getCategoryTypeClass();

        /* Immediately reject hidden categories */
        if (myCatClass.isHiddenType()) {
            return false;
        }

        /* Switch on the CategoryClass */
        switch (myCatClass) {
            case TAXEDINCOME:
            case GROSSINCOME:
            case RECOVEREDEXPENSES:
            case OTHERINCOME:
                /* Taxed/Other income must be to deposit/cash/loan */
                return myType.isValued();

            case PENSIONCONTRIB:
                /* Pension contribution must be to a Pension holding or to a SIPP */
                return (pAccount instanceof MoneyWiseSecurityHolding
                        && ((MoneyWiseSecurityHolding) pAccount).getSecurity().getCategoryClass().isPension())
                        || (pAccount instanceof MoneyWisePortfolio
                        && ((MoneyWisePortfolio) pAccount).isPortfolioClass(MoneyWisePortfolioClass.SIPP));

            case GIFTEDINCOME:
            case INHERITED:
                /* Inheritance/Gifted must be to asset */
                return myType.isAsset();

            case INTEREST:
                /* Account must be deposit or portfolio */
                return myType.isDeposit() || myType.isPortfolio();

            case DIVIDEND:
            case SECURITYCLOSURE:
                /* Account must be SecurityHolding */
                return myType.isSecurityHolding();

            case BADDEBTCAPITAL:
            case BADDEBTINTEREST:
                /* Account must be peer2Peer */
                return pAccount instanceof MoneyWiseDeposit
                        && ((MoneyWiseDeposit) pAccount).isDepositClass(MoneyWiseDepositCategoryClass.PEER2PEER);

            case CASHBACK:
                return checkCashBack(pAccount);

            case LOYALTYBONUS:
                return checkLoyaltyBonus(pAccount);

            case RENTALINCOME:
            case RENTALEXPENSE:
            case ROOMRENTALINCOME:
                /* Account must be property */
                return pAccount instanceof MoneyWiseSecurityHolding
                        && ((MoneyWiseSecurityHolding) pAccount).getSecurity().isSecurityClass(MoneyWiseSecurityClass.PROPERTY);

            case UNITSADJUST:
            case SECURITYREPLACE:
                /* Account must be capital */
                return pAccount.isCapital();

            case STOCKSPLIT:
            case STOCKTAKEOVER:
            case STOCKDEMERGER:
            case STOCKRIGHTSISSUE:
                /* Account must be shares */
                return pAccount.isShares();

            case WRITEOFF:
            case LOANINTERESTEARNED:
            case LOANINTERESTCHARGED:
            case TAXRELIEF:
                return myType.isLoan();

            case LOCALTAXES:
            case INCOMETAX:
                return myType.isValued();

            case EXPENSE:
                return myType.isValued() || myType.isAutoExpense();

            case PORTFOLIOXFER:
                return pAccount instanceof MoneyWiseSecurityHolding
                        || pAccount instanceof MoneyWisePortfolio;

            case TRANSFER:
                return true;

            /* Reject other categories */
            default:
                return false;
        }
    }

    @Override
    public boolean isValidDirection(final MoneyWiseTransAsset pAccount,
                                    final MoneyWiseTransCategory pCategory,
                                    final MoneyWiseAssetDirection pDirection) {
        /* TODO relax some of these rules */

        /* Access details */
        final MoneyWiseTransCategoryClass myCatClass = pCategory.getCategoryTypeClass();

        /* Switch on the CategoryClass */
        switch (myCatClass) {
            case TAXEDINCOME:
            case GROSSINCOME:
                /* Cannot refund Taxed Income yet */
                return newValidation || pDirection.isFrom();

            case PENSIONCONTRIB:
                /* Cannot refund Pension Contribution */
                return pDirection.isFrom();

            case GIFTEDINCOME:
            case INHERITED:
                /* Cannot refund Gifted/Inherited Income yet */
                return newValidation || pDirection.isFrom();

            case RENTALINCOME:
            case ROOMRENTALINCOME:
                /* Cannot refund Rental Income */
                return pDirection.isTo();

            case RENTALEXPENSE:
                /* Cannot refund Rental Expense */
                return pDirection.isFrom();

            case INTEREST:
                /* Cannot refund Interest yet */
                return newValidation || pDirection.isTo();

            case DIVIDEND:
            case SECURITYCLOSURE:
                /* Cannot refund Dividend yet */
                return pDirection.isTo();

            case LOYALTYBONUS:
                /* Cannot refund loyaltyBonus yet */
                return newValidation || pDirection.isTo();

            case WRITEOFF:
            case LOANINTERESTCHARGED:
                /* All need to be TO */
                return newValidation || pDirection.isTo();

            case LOANINTERESTEARNED:
                /* All need to be FROM */
                return newValidation || pDirection.isFrom();

            case UNITSADJUST:
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKTAKEOVER:
            case SECURITYREPLACE:
            case PORTFOLIOXFER:
                /* All need to be To */
                return pDirection.isTo();

            default:
                return true;
        }
    }

    @Override
    public boolean isValidPartner(final MoneyWiseTransAsset pAccount,
                                  final MoneyWiseTransCategory pCategory,
                                  final MoneyWiseTransAsset pPartner) {
        /* Access details */
        final boolean isRecursive = MetisDataDifference.isEqual(pAccount, pPartner);
        final MoneyWiseAssetType myPartnerType = pPartner.getAssetType();
        final MoneyWiseTransCategoryClass myCatClass = pCategory.getCategoryTypeClass();

        /* Immediately reject hidden partners */
        if (pPartner.isHidden()) {
            return false;
        }

        /* Validate securityHolding */
        if (pPartner instanceof MoneyWiseSecurityHolding
                && !checkSecurityHolding((MoneyWiseSecurityHolding) pPartner)) {
            return false;
        }

        /* Reject pensions portfolio */
        if (pPartner instanceof MoneyWisePortfolio
                && ((MoneyWisePortfolio) pPartner).getCategoryClass().holdsPensions()) {
            return false;
        }

        /* If this involves auto-expense */
        if (pAccount.isAutoExpense()
                || pPartner.isAutoExpense()) {
            /* Access account type */
            final MoneyWiseAssetType myAccountType = pAccount.getAssetType();

            /* Special processing */
            switch (myCatClass) {
                case TRANSFER:
                    /* Transfer must be to/from deposit/cash/loan */
                    return myPartnerType.isAutoExpense()
                            ? myAccountType.isValued()
                            : myPartnerType.isValued();

                case EXPENSE:
                    /* Transfer must be to/from payee */
                    return pPartner instanceof MoneyWisePayee;

                /* Auto Expense cannot be used for other categories */
                default:
                    return false;
            }
        }

        /* Switch on the CategoryClass */
        switch (myCatClass) {
            case TAXEDINCOME:
            case GROSSINCOME:
                /* Taxed Income must have a Payee that can provide income */
                return pPartner instanceof MoneyWisePayee
                        && ((MoneyWisePayee) pPartner).getCategoryClass().canProvideTaxedIncome();

            case PENSIONCONTRIB:
                /* Pension Contribution must be a payee that can parent */
                return pPartner instanceof MoneyWisePayee
                        && ((MoneyWisePayee) pPartner).getCategoryClass().canContribPension();

            case OTHERINCOME:
            case RECOVEREDEXPENSES:
                /* Other Income must have a Payee partner */
                return pPartner instanceof MoneyWisePayee;

            case LOCALTAXES:
                /* LocalTaxes must have a Government Payee partner */
                return pPartner instanceof MoneyWisePayee
                        && ((MoneyWisePayee) pPartner).isPayeeClass(MoneyWisePayeeClass.GOVERNMENT);

            case GIFTEDINCOME:
            case INHERITED:
                /* Gifted/Inherited Income must have an Individual Payee partner */
                return pPartner instanceof MoneyWisePayee
                        && ((MoneyWisePayee) pPartner).isPayeeClass(MoneyWisePayeeClass.INDIVIDUAL);

            case RENTALINCOME:
            case RENTALEXPENSE:
            case ROOMRENTALINCOME:
                /* RentalIncome/Expense must have a loan partner */
                return myPartnerType.isLoan();

            case WRITEOFF:
            case LOANINTERESTEARNED:
            case LOANINTERESTCHARGED:
                /* WriteOff/LoanInterestEarned/Charged must be recursive */
                return isRecursive;

            case INTEREST:
            case CASHBACK:
                /* Interest/CashBack is to a valued account */
                return myPartnerType.isValued();

            case DIVIDEND:
                return checkDividend(pAccount, pPartner);

            case LOYALTYBONUS:
                return checkLoyaltyBonus(pAccount, pPartner);

            case BADDEBTCAPITAL:
            case BADDEBTINTEREST:
                return pPartner instanceof MoneyWisePayee
                        && MetisDataDifference.isEqual(pPartner, pAccount.getParent());

            case UNITSADJUST:
            case STOCKSPLIT:
                /* Must be recursive */
                return isRecursive;

            case SECURITYREPLACE:
            case STOCKTAKEOVER:
            case STOCKDEMERGER:
                return checkTakeOver(pAccount, pPartner);

            case STOCKRIGHTSISSUE:
                return checkStockRights(pAccount, pPartner);

            case TRANSFER:
                return checkTransfer(pAccount, pPartner);

            case SECURITYCLOSURE:
                return checkSecurityClosure(pAccount, pPartner);

            case EXPENSE:
                /* Expense must have a Payee partner */
                return pPartner instanceof MoneyWisePayee;

            case INCOMETAX:
            case TAXRELIEF:
                return pPartner instanceof MoneyWisePayee
                        && ((MoneyWisePayee) pPartner).isPayeeClass(MoneyWisePayeeClass.TAXMAN);

            case PORTFOLIOXFER:
                return checkPortfolioXfer(pAccount, pPartner);

            default:
                return false;
        }
    }

    /**
     * Check securityHolding.
     * @param pHolding the securityHolding
     * @return valid true/false
     */
    private static boolean checkSecurityHolding(final MoneyWiseSecurityHolding pHolding) {
        /* Access the components */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();

        /* If the portfolio can hold pensions */
        if (myPortfolio.getCategoryClass().holdsPensions()) {
            /* Can only hold pensions */
            return mySecurity.getCategoryClass().isPension();
        }

        /* cannot be a pension */
        return !mySecurity.getCategoryClass().isPension();
    }

    /**
     * Check dividend.
     * @param pAccount the holding providing the dividend.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkDividend(final MoneyWiseTransAsset pAccount,
                                         final MoneyWiseTransAsset pPartner) {
        /* Recursive is allowed */
        if (MetisDataDifference.isEqual(pAccount, pPartner)) {
            return true;
        }

        /* partner must be valued */
        return pPartner.getAssetType().isValued();
    }

    /**
     * Check TakeOver.
     * @param pAccount the holding being acted on.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkTakeOver(final MoneyWiseTransAsset pAccount,
                                         final MoneyWiseTransAsset pPartner) {
        /* Must be holding <-> holding */
        if (!(pAccount instanceof MoneyWiseSecurityHolding)
                || !(pPartner instanceof MoneyWiseSecurityHolding)) {
            return false;
        }

        /* Recursive is not allowed */
        if (MetisDataDifference.isEqual(pAccount, pPartner)) {
            return false;
        }

        /* Access holdings */
        final MoneyWiseSecurityHolding myAccount = (MoneyWiseSecurityHolding) pAccount;
        final MoneyWiseSecurityHolding myPartner = (MoneyWiseSecurityHolding) pPartner;

        /* Portfolios must be the same */
        if (!MetisDataDifference.isEqual(myAccount.getPortfolio(), myPartner.getPortfolio())) {
            return false;
        }

        /* Security types must be the same */
        return MetisDataDifference.isEqual(myAccount.getSecurity().getCategory(), myPartner.getSecurity().getCategory());
    }

    /**
     * Check stock rights.
     * @param pAccount the account being transferred.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkStockRights(final MoneyWiseTransAsset pAccount,
                                            final MoneyWiseTransAsset pPartner) {
        /* If this is security -> portfolio */
        if (pAccount instanceof MoneyWiseSecurityHolding
                && pPartner instanceof MoneyWisePortfolio) {
            /* Must be same portfolios */
            final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pAccount;
            return MetisDataDifference.isEqual(myHolding.getPortfolio(), pPartner);
        }

        /* partner must be valued */
        return pPartner.getAssetType().isValued();
    }

    /**
     * Check cashBack.
     * @param pAccount the account providing cashBack.
     * @return valid true/false
     */
    private static boolean checkCashBack(final MoneyWiseTransAsset pAccount) {
        /* If this is deposit then check whether it can support cashBack */
        if (pAccount instanceof MoneyWiseDeposit) {
            return ((MoneyWiseDeposit) pAccount).getCategoryClass().canCashBack();
        }

        /* If this is loan then check whether it can support cashBack */
        if (pAccount instanceof MoneyWiseLoan) {
            return ((MoneyWiseLoan) pAccount).getCategoryClass().canCashBack();
        }

        /* not allowed */
        return false;
    }

    /**
     * Check loyalty bonus.
     * @param pAccount the account providing bonus.
     * @return valid true/false
     */
    private boolean checkLoyaltyBonus(final MoneyWiseTransAsset pAccount) {
        /* If this is deposit then check whether it can support loyaltyBonus */
        if (pAccount instanceof MoneyWiseDeposit) {
            return newValidation
                    || ((MoneyWiseDeposit) pAccount).getCategoryClass().canLoyaltyBonus();
        }

        /* must be portfolio */
        return pAccount instanceof MoneyWisePortfolio;
    }

    /**
     * Check loyalty bonus.
     * @param pAccount the account providing bonus.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkLoyaltyBonus(final MoneyWiseTransAsset pAccount,
                                             final MoneyWiseTransAsset pPartner) {
        /* If this is portfolio -> security holding */
        if (pAccount instanceof MoneyWisePortfolio
                && pPartner instanceof MoneyWiseSecurityHolding) {
            /* Must be same portfolios */
            final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pPartner;
            return MetisDataDifference.isEqual(myHolding.getPortfolio(), pAccount);
        }

        /* must be recursive */
        return MetisDataDifference.isEqual(pAccount, pPartner);
    }

    /**
     * Check transfer.
     * @param pAccount the account being transferred.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkTransfer(final MoneyWiseTransAsset pAccount,
                                         final MoneyWiseTransAsset pPartner) {
        /* Must not be recursive */
        if (MetisDataDifference.isEqual(pAccount, pPartner)) {
            return false;
        }

        /* If this is security -> portfolio */
        if (pAccount instanceof MoneyWiseSecurityHolding
                && pPartner instanceof MoneyWisePortfolio) {
            /* Must be same portfolios */
            final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pAccount;
            if (!MetisDataDifference.isEqual(myHolding.getPortfolio(), pPartner)) {
                return false;
            }
        }

        /* If this is security <- portfolio */
        if (pPartner instanceof MoneyWiseSecurityHolding
                && pAccount instanceof MoneyWisePortfolio) {
            /* Must be same portfolios */
            final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pPartner;
            if (!MetisDataDifference.isEqual(myHolding.getPortfolio(), pAccount)) {
                return false;
            }
        }

        /* partner must be asset */
        return pPartner.getAssetType().isAsset();
    }

    /**
     * Check securityClosure.
     * @param pAccount the account being closed.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkSecurityClosure(final MoneyWiseTransAsset pAccount,
                                                final MoneyWiseTransAsset pPartner) {
        /* Must not be recursive */
        if (MetisDataDifference.isEqual(pAccount, pPartner)) {
            return false;
        }

        /* partner must be valued */
        return pPartner.getAssetType().isValued();
    }

    /**
     * Check portfolioXfer.
     * @param pAccount the account being transferred.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkPortfolioXfer(final MoneyWiseTransAsset pAccount,
                                              final MoneyWiseTransAsset pPartner) {
        /* Partner must be portfolio */
        if (!(pPartner instanceof MoneyWisePortfolio)) {
            return false;
        }

        /* If account is portfolio */
        if (pAccount instanceof MoneyWisePortfolio) {
            /* Cannot be recursive */
            if (MetisDataDifference.isEqual(pAccount, pPartner)) {
                return false;
            }

            /* Must be same currency */
            return MetisDataDifference.isEqual(pAccount.getAssetCurrency(), pPartner.getAssetCurrency());
        }

        /* If account is security holding */
        if (pAccount instanceof MoneyWiseSecurityHolding) {
            /* Must be different portfolios */
            final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pAccount;
            return !MetisDataDifference.isEqual(myHolding.getPortfolio(), pPartner);
        }

        /* Not allowed */
        return false;
    }

    @Override
    public void autoCorrect(MoneyWiseTransaction pItem) throws OceanusException {
        theDefaults.autoCorrect(pItem);
    }

    @Override
    public void setDefaults(MoneyWiseTransaction pItem) throws OceanusException {

    }
}
