/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.data.basic;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;

/**
 * Enumeration of Asset Types.
 */
public enum MoneyWiseAssetType {
    /**
     * Deposit.
     */
    DEPOSIT(1),

    /**
     * Cash.
     */
    CASH(2),

    /**
     * AutoExpense.
     */
    AUTOEXPENSE(3),

    /**
     * Portfolio.
     */
    PORTFOLIO(4),

    /**
     * Loan.
     */
    LOAN(5),

    /**
     * Security.
     */
    SECURITY(6),

    /**
     * Payee.
     */
    PAYEE(7),

    /**
     * SecurityHolding.
     */
    SECURITYHOLDING(8),

    /**
     * PortfolioCash.
     */
    PORTFOLIOCASH(9),

    /**
     * TransactionCategory.
     */
    TRANSACTIONCATEGORY(10),

    /**
     * TaxBasis.
     */
    TAXBASIS(11),

    /**
     * TaxBasisAccount.
     */
    TAXBASISACCOUNT(12);

    /**
     * The asset shift for external Ids.
     */
    public static final int ASSETSHIFT = 60;

    /**
     * The asset shift for alternate external Ids.
     */
    public static final int ALTERNATESHIFT = 56;

    /**
     * The asset mask for external Ids.
     */
    public static final int ASSETMASK = 0xF0000000;

    /**
     * The alternate mask for external Ids.
     */
    public static final long ALTERNATEMASK = 0xFF00000000000000L;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Constructor.
     *
     * @param uId the Id
     */
    MoneyWiseAssetType(final int uId) {
        theId = uId;
    }

    /**
     * Obtain class Id.
     *
     * @return the Id
     */
    public int getId() {
        return theId;
    }

    /**
     * Obtain the external id.
     *
     * @param pType   the asset type
     * @param pBaseId the id
     * @return the external id
     */
    public static long createExternalId(final MoneyWiseAssetType pType,
                                        final long pBaseId) {
        return pBaseId
                + (((long) pType.getId()) << ASSETSHIFT);
    }

    /**
     * Obtain the external id.
     *
     * @param pType    the asset type
     * @param pMajorId the major id
     * @param pBaseId  the id
     * @return the external id
     */
    public static long createExternalId(final MoneyWiseAssetType pType,
                                        final int pMajorId,
                                        final int pBaseId) {
        final long myBase = pBaseId;
        final long myMajor = ((long) pMajorId) << Integer.SIZE;
        final long myAsset = ((long) pType.getId()) << ASSETSHIFT;
        return myAsset + myMajor + myBase;
    }

    /**
     * Obtain the alternate external id.
     *
     * @param pType       the asset type
     * @param pExternalId the id
     * @return the external id
     */
    public static long createAlternateExternalId(final MoneyWiseAssetType pType,
                                                 final long pExternalId) {
        final long myOldType = pExternalId >>> ASSETSHIFT;
        final long myBase = pExternalId & ~ALTERNATEMASK;
        final long myMajor = myOldType << ALTERNATESHIFT;
        final long myAsset = ((long) pType.getId()) << ASSETSHIFT;
        return myAsset + myMajor + myBase;
    }

    /**
     * Obtain the base id.
     *
     * @param pId the id
     * @return the base id
     */
    public static int getMajorId(final long pId) {
        final int myId = (int) (pId >>> Integer.SIZE);
        return myId & ~ASSETMASK;
    }

    /**
     * Obtain the qualifying id.
     *
     * @param pId the id
     * @return the base id
     */
    public static int getBaseId(final long pId) {
        return (int) pId;
    }

    /**
     * Obtain the assetType.
     *
     * @param pId the id
     * @return the assetType
     * @throws OceanusException on error
     */
    public static MoneyWiseAssetType getAssetType(final long pId) throws OceanusException {
        final int myId = (int) (pId >>> ASSETSHIFT);
        return fromId(myId);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseBasicResource.getKeyForAssetType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     *
     * @param id the id value
     * @return the corresponding enum object
     * @throws OceanusException on error
     */
    public static MoneyWiseAssetType fromId(final int id) throws OceanusException {
        for (MoneyWiseAssetType myClass : values()) {
            if (myClass.getId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseAssetType.class.getSimpleName() + ":" + id);
    }

    /**
     * Determine whether this is a valued asset.
     *
     * @return true/false
     */
    public boolean isValued() {
        switch (this) {
            case DEPOSIT:
            case CASH:
            case LOAN:
            case PORTFOLIO:
                return true;
            case AUTOEXPENSE:
            case SECURITY:
            case PAYEE:
            case SECURITYHOLDING:
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a base account.
     *
     * @return true/false
     */
    public boolean isBaseAccount() {
        switch (this) {
            case DEPOSIT:
            case AUTOEXPENSE:
            case CASH:
            case LOAN:
            case PORTFOLIO:
            case SECURITYHOLDING:
                return true;
            case SECURITY:
            case PAYEE:
            default:
                return false;
        }
    }

    /**
     * Determine whether this is an asset.
     *
     * @return true/false
     */
    public boolean isAsset() {
        switch (this) {
            case DEPOSIT:
            case AUTOEXPENSE:
            case SECURITY:
            case CASH:
            case LOAN:
            case SECURITYHOLDING:
            case PORTFOLIO:
                return true;
            case PAYEE:
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a deposit.
     *
     * @return true/false
     */
    public boolean isDeposit() {
        return this == DEPOSIT;
    }

    /**
     * Determine whether this is a loan.
     *
     * @return true/false
     */
    public boolean isLoan() {
        return this == LOAN;
    }

    /**
     * Determine whether this is a security.
     *
     * @return true/false
     */
    public boolean isSecurity() {
        return this == SECURITY;
    }

    /**
     * Determine whether this is a securityHolding.
     *
     * @return true/false
     */
    public boolean isSecurityHolding() {
        return this == SECURITYHOLDING;
    }

    /**
     * Determine whether this is a portfolio.
     *
     * @return true/false
     */
    public boolean isPortfolio() {
        return this == PORTFOLIO;
    }

    /**
     * Determine whether this is a payee.
     *
     * @return true/false
     */
    public boolean isPayee() {
        return this == PAYEE;
    }

    /**
     * Determine whether this is a cash.
     *
     * @return true/false
     */
    public boolean isCash() {
        return this == CASH;
    }

    /**
     * Determine whether this is an autoExpense.
     *
     * @return true/false
     */
    public boolean isAutoExpense() {
        return this == AUTOEXPENSE;
    }
}
