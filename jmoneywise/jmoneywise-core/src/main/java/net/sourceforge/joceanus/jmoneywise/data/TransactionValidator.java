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

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;

/**
 * Transaction validator.
 * @author Tony Washer
 */
public final class TransactionValidator {
    /**
     * Prevent instantiation.
     */
    private TransactionValidator() {
    }

    /**
     * Is the account valid as the base account in a transaction?
     * @param pAccount the account
     * @return true/false
     */
    public static boolean isValidAccount(final TransactionAsset pAccount) {
        /* Check type of account */
        AssetType myType = pAccount.getAssetType();
        return myType.isBaseAccount() && !pAccount.isHidden();
    }

    /**
     * Is the transaction valid for the base account in the transaction?.
     * @param pAccount the account
     * @param pCategory The category of the event
     * @return true/false
     */
    public static boolean isValidCategory(final TransactionAsset pAccount,
                                          final TransactionCategory pCategory) {
        /* Access details */
        AssetType myType = pAccount.getAssetType();
        TransactionCategoryClass myCatClass = pCategory.getCategoryTypeClass();

        /* Immediately reject hidden categories */
        if (myCatClass.isHiddenType()) {
            return false;
        }

        /* Switch on the CategoryClass */
        switch (myCatClass) {
            case TAXEDINCOME:
            case OTHERINCOME:
                /* Taxed/Other income must be to deposit/cash/loan */
                return myType.isValued();

            case GRANTINCOME:
            case BENEFITINCOME:
                /* Grant/Benefit income must be to deposit account */
                return myType.isDeposit();

            case GIFTEDINCOME:
            case INHERITED:
                /* Inheritance/Gifted must be to asset */
                return myType.isAsset();

            case INTEREST:
                /* Account must be deposit or portfolio */
                return myType.isDeposit() || myType.isPortfolio();

            case DIVIDEND:
                /* Account must be SecurityHolding */
                return myType.isSecurityHolding();

            case BADDEBT:
                /* Account must bee peer2Peer */
                return (pAccount instanceof Deposit)
                       && (((Deposit) pAccount).isDepositClass(DepositCategoryClass.PEER2PEER));

            case CASHBACK:
                return checkCashBack(pAccount);

            case LOYALTYBONUS:
                return checkLoyaltyBonus(pAccount);

            case RENTALINCOME:
            case ROOMRENTALINCOME:
                /* Account must be property */
                return (pAccount instanceof SecurityHolding)
                       && ((SecurityHolding) pAccount).getSecurity().isSecurityClass(SecurityTypeClass.PROPERTY);

            case UNITSADJUST:
                /* Account must be capital */
                return pAccount.isCapital();

            case STOCKSPLIT:
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
            case STOCKDEMERGER:
            case STOCKRIGHTSWAIVED:
            case STOCKRIGHTSTAKEN:
                /* Account must be shares */
                return pAccount.isShares();

            case WRITEOFF:
            case LOANINTERESTEARNED:
            case LOANINTERESTCHARGED:
            case TAXRELIEF:
                return myType.isLoan();

            case CHARITYDONATION:
            case LOCALTAXES:
            case TAXSETTLEMENT:
                return myType.isValued();

            case EXPENSE:
                return myType.isValued() || myType.isAutoExpense();

            case PORTFOLIOXFER:
                return (pAccount instanceof SecurityHolding)
                       || (pAccount instanceof Portfolio);

            case TRANSFER:
                return true;

                /* Reject other categories */
            default:
                return false;
        }
    }

    /**
     * Is the direction valid for the base account and category in the transaction?.
     * @param pAccount the account
     * @param pCategory The category of the event
     * @param pDirection the direction
     * @return true/false
     */
    public static boolean isValidDirection(final TransactionAsset pAccount,
                                           final TransactionCategory pCategory,
                                           final AssetDirection pDirection) {
        /* TODO relax some of these rules */

        /* Access details */
        TransactionCategoryClass myCatClass = pCategory.getCategoryTypeClass();

        /* Switch on the CategoryClass */
        switch (myCatClass) {
            case TAXEDINCOME:
                /* Cannot refund Taxed Income yet */
                return pDirection.isFrom();

            case GRANTINCOME:
                /* Cannot refund Grant Income yet */
                return pDirection.isFrom();

            case BENEFITINCOME:
                /* Cannot refund Benefit Income yet */
                return pDirection.isFrom();

            case GIFTEDINCOME:
            case INHERITED:
                /* Cannot refund Gifted/Inherited Income yet */
                return pDirection.isFrom();

            case RENTALINCOME:
            case ROOMRENTALINCOME:
                /* Cannot refund Rental Income yet */
                return pDirection.isTo();

            case INTEREST:
                /* Cannot refund Interest yet */
                return pDirection.isTo();

            case DIVIDEND:
                /* Cannot refund Dividend yet */
                return pDirection.isTo();

            case LOYALTYBONUS:
                /* Cannot refund loyaltyBonus yet */
                return pDirection.isTo();

            case WRITEOFF:
            case LOANINTERESTCHARGED:
                /* All need to be TO */
                return pDirection.isTo();

            case LOANINTERESTEARNED:
                /* All need to be FROM */
                return pDirection.isFrom();

            case UNITSADJUST:
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKTAKEOVER:
            case SECURITYREPLACE:
            case STOCKRIGHTSWAIVED:
            case PORTFOLIOXFER:
                /* All need to be To */
                return pDirection.isTo();

            case STOCKRIGHTSTAKEN:
                /* Needs to be From */
                return pDirection.isFrom();

            default:
                return true;
        }
    }

    /**
     * Is the partner valid for the base account and category in the transaction?.
     * @param pAccount the account
     * @param pCategory The category of the event
     * @param pPartner the partner
     * @return true/false
     */
    public static boolean isValidPartner(final TransactionAsset pAccount,
                                         final TransactionCategory pCategory,
                                         final TransactionAsset pPartner) {
        /* Access details */
        boolean isRecursive = Difference.isEqual(pAccount, pPartner);
        AssetType myPartnerType = pPartner.getAssetType();
        TransactionCategoryClass myCatClass = pCategory.getCategoryTypeClass();

        /* Immediately reject hidden partners */
        if (pPartner.isHidden()) {
            return false;
        }

        /* If this involves auto-expense */
        if (pAccount.isAutoExpense()
            || pPartner.isAutoExpense()) {
            /* Access account type */
            AssetType myAccountType = pAccount.getAssetType();

            /* Special processing */
            switch (myCatClass) {
                case TRANSFER:
                    /* Transfer must be to/from deposit/cash/loan */
                    return myPartnerType.isAutoExpense()
                                                        ? myAccountType.isValued()
                                                        : myPartnerType.isValued();

                case EXPENSE:
                    /* Transfer must be to/from payee */
                    return pPartner instanceof Payee;

                    /* Auto Expense cannot be used for other categories */
                default:
                    return false;
            }
        }

        /* Switch on the CategoryClass */
        switch (myCatClass) {
            case TAXEDINCOME:
                /* Taxed Income must have a Employer Payee partner */
                return (pPartner instanceof Payee)
                       && ((Payee) pPartner).isPayeeClass(PayeeTypeClass.EMPLOYER);

            case OTHERINCOME:
                /* Other Income must have a Payee partner */
                return pPartner instanceof Payee;

            case GRANTINCOME:
                /* Grant Income must have a Payee partner that can grant */
                return (pPartner instanceof Payee)
                       && ((Payee) pPartner).getPayeeTypeClass().canGrant();

            case LOCALTAXES:
            case BENEFITINCOME:
                /* Benefit Income/LocalTaxes must have a Government Payee partner */
                return (pPartner instanceof Payee)
                       && ((Payee) pPartner).isPayeeClass(PayeeTypeClass.GOVERNMENT);

            case GIFTEDINCOME:
            case INHERITED:
                /* Gifted/Inherited Income must have an Individual Payee partner */
                return (pPartner instanceof Payee)
                       && ((Payee) pPartner).isPayeeClass(PayeeTypeClass.INDIVIDUAL);

            case RENTALINCOME:
            case ROOMRENTALINCOME:
                /* RentalIncome must have a loan partner */
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

            case BADDEBT:
                return (pPartner instanceof Payee)
                       && Difference.isEqual(pPartner, pAccount.getParent());

            case UNITSADJUST:
            case STOCKSPLIT:
                /* Must be recursive */
                return isRecursive;

            case SECURITYREPLACE:
            case STOCKTAKEOVER:
            case STOCKDEMERGER:
                return checkTakeOver(pAccount, pPartner);

            case STOCKRIGHTSTAKEN:
            case STOCKRIGHTSWAIVED:
                return checkStockRights(pAccount, pPartner);

            case TRANSFER:
                return checkTransfer(pAccount, pPartner);

            case EXPENSE:
            case CHARITYDONATION:
                /* Expense must have a Payee partner */
                return pPartner instanceof Payee;

            case TAXSETTLEMENT:
            case TAXRELIEF:
                return (pPartner instanceof Payee)
                       && ((Payee) pPartner).isPayeeClass(PayeeTypeClass.TAXMAN);

            case PORTFOLIOXFER:
                return checkPortfolioXfer(pAccount, pPartner);

            default:
                return false;
        }
    }

    /**
     * Check dividend.
     * @param pAccount the holding providing the dividend.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkDividend(final TransactionAsset pAccount,
                                         final TransactionAsset pPartner) {
        /* Recursive is allowed */
        if (Difference.isEqual(pAccount, pPartner)) {
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
    private static boolean checkTakeOver(final TransactionAsset pAccount,
                                         final TransactionAsset pPartner) {
        /* Must be holding <-> holding */
        if (!(pAccount instanceof SecurityHolding)
            || !(pPartner instanceof SecurityHolding)) {
            return false;
        }

        /* Recursive is not allowed */
        if (Difference.isEqual(pAccount, pPartner)) {
            return false;
        }

        /* Access holdings */
        SecurityHolding myAccount = (SecurityHolding) pAccount;
        SecurityHolding myPartner = (SecurityHolding) pPartner;

        /* Portfolios must be the same */
        if (!Difference.isEqual(myAccount.getPortfolio(), myPartner.getPortfolio())) {
            return false;
        }

        /* Security types must be the same */
        return Difference.isEqual(myAccount.getSecurity().getSecurityType(), myPartner.getSecurity().getSecurityType());
    }

    /**
     * Check stock rights.
     * @param pAccount the account being transferred.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkStockRights(final TransactionAsset pAccount,
                                            final TransactionAsset pPartner) {
        /* If this is security -> portfolio */
        if ((pAccount instanceof SecurityHolding)
            && (pPartner instanceof Portfolio)) {
            /* Must be same portfolios */
            SecurityHolding myHolding = (SecurityHolding) pAccount;
            return Difference.isEqual(myHolding.getPortfolio(), pPartner);
        }

        /* partner must be valued */
        return pPartner.getAssetType().isValued();
    }

    /**
     * Check cashBack.
     * @param pAccount the account providing cashBack.
     * @return valid true/false
     */
    private static boolean checkCashBack(final TransactionAsset pAccount) {
        /* If this is deposit then must be peer2peer */
        if (pAccount instanceof Deposit) {
            return (((Deposit) pAccount).isDepositClass(DepositCategoryClass.PEER2PEER));
        }

        /* If this is loan then must be creditCard */
        if (pAccount instanceof Loan) {
            return (((Loan) pAccount).isLoanClass(LoanCategoryClass.CREDITCARD));
        }

        /* not allowed */
        return false;
    }

    /**
     * Check loyalty bonus.
     * @param pAccount the account providing bonus.
     * @return valid true/false
     */
    private static boolean checkLoyaltyBonus(final TransactionAsset pAccount) {
        /* If this is deposit then must be peer2peer */
        if (pAccount instanceof Deposit) {
            return (((Deposit) pAccount).isDepositClass(DepositCategoryClass.PEER2PEER));
        }

        /* must be portfolio */
        return pAccount instanceof Portfolio;
    }

    /**
     * Check loyalty bonus.
     * @param pAccount the account providing bonus.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkLoyaltyBonus(final TransactionAsset pAccount,
                                             final TransactionAsset pPartner) {
        /* If this is portfolio -> security holding */
        if ((pAccount instanceof Portfolio)
            && (pPartner instanceof SecurityHolding)) {
            /* Must be same portfolios */
            SecurityHolding myHolding = (SecurityHolding) pPartner;
            return Difference.isEqual(myHolding.getPortfolio(), pAccount);
        }

        /* must be recursive */
        return Difference.isEqual(pAccount, pPartner);
    }

    /**
     * Check transfer.
     * @param pAccount the account being transferred.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkTransfer(final TransactionAsset pAccount,
                                         final TransactionAsset pPartner) {
        /* Must not be recursive */
        if (Difference.isEqual(pAccount, pPartner)) {
            return false;
        }

        /* If this is security -> portfolio */
        if ((pAccount instanceof SecurityHolding)
            && (pPartner instanceof Portfolio)) {
            /* Must be same portfolios */
            SecurityHolding myHolding = (SecurityHolding) pAccount;
            if (!Difference.isEqual(myHolding.getPortfolio(), pPartner)) {
                return false;
            }
        }

        /* If this is security <- portfolio */
        if ((pPartner instanceof SecurityHolding)
            && (pAccount instanceof Portfolio)) {
            /* Must be same portfolios */
            SecurityHolding myHolding = (SecurityHolding) pPartner;
            if (!Difference.isEqual(myHolding.getPortfolio(), pAccount)) {
                return false;
            }
        }

        /* partner must be asset */
        return pPartner.getAssetType().isAsset();
    }

    /**
     * Check portfolioXfer.
     * @param pAccount the account being transferred.
     * @param pPartner the partner
     * @return valid true/false
     */
    private static boolean checkPortfolioXfer(final TransactionAsset pAccount,
                                              final TransactionAsset pPartner) {
        /* Partner must be portfolio */
        if (!(pPartner instanceof Portfolio)) {
            return false;
        }

        /* If account is portfolio */
        if (pAccount instanceof Portfolio) {
            /* Cannot be recursive */
            return !Difference.isEqual(pAccount, pPartner);
        }

        /* If account is security holding */
        if (pAccount instanceof SecurityHolding) {
            /* Must be different portfolios */
            SecurityHolding myHolding = (SecurityHolding) pAccount;
            return !Difference.isEqual(myHolding.getPortfolio(), pPartner);
        }

        /* Not allowed */
        return false;
    }
}
