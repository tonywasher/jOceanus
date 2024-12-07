/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.statics;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * Enumeration of Payee Type Classes.
 */
public enum MoneyWisePayeeClass
        implements PrometheusStaticDataClass {
    /**
     * Generic Payee Account.
     * <p>
     * This is a simple payee that represents an entity that monies are paid to.
     */
    PAYEE(1, 0),

    /**
     * Employer Account.
     * <p>
     * This is an employer account which is a specialised form of an {@link #INSTITUTION} payee. It
     * has the ability to pay dividends.
     */
    EMPLOYER(2, 1),

    /**
     * Institution Payee.
     * <p>
     * This is an institution (e.g. a bank) that holds another account of behalf of the client. It
     * is a specialised form of payee.
     */
    INSTITUTION(3, 2),

    /**
     * LoanHolder Account.
     * <p>
     * This is an individual who can own a PrivateLoan account, and who can be inherited from. It is
     * a specialised form of a payee.
     */
    INDIVIDUAL(4, 3),

    /**
     * Annuity.
     * <p>
     * This is an annuity that pays a TaxedIncome with TaxCredit and no NatInsurance.
     */
    ANNUITY(5, 4),

    /**
     * Inland Revenue.
     * <p>
     * This is a singular payee representing the tax authority. All TaxCredits etc. are deemed to
     * have been paid to the single account of this type.
     */
    TAXMAN(6, 5),

    /**
     * Government.
     * <p>
     * This is a singular payee representing the government. All Local Taxes should be paid to the
     * single account of this type.
     */
    GOVERNMENT(7, 6),

    /**
     * Market pseudo account.
     * <p>
     * This is a singular payee representing the market. All increases/decreases in value of an
     * asset that are due to fluctuations in unit prices are viewed as income/expense from the
     * single account of this type.
     */
    MARKET(8, 7);

    /**
     * The String name.
     */
    private String theName;

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

    /**
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     */
    MoneyWisePayeeClass(final int uId,
                        final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    @Override
    public int getClassId() {
        return theId;
    }

    @Override
    public int getOrder() {
        return theOrder;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseStaticResource.getKeyForPayeeType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws OceanusException on error
     */
    public static MoneyWisePayeeClass fromId(final int id) throws OceanusException {
        for (MoneyWisePayeeClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseStaticDataType.PAYEETYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the payeeType is hidden type.
     * @return <code>true</code> if the payee is hidden, <code>false</code> otherwise.
     */
    public boolean isHiddenType() {
        return this == MARKET;
    }

    /**
     * Determine whether the payeeType is hidden type.
     * @return <code>true</code> if the payee is hidden, <code>false</code> otherwise.
     */
    public boolean isAnnuity() {
        return this == ANNUITY;
    }

    /**
     * Determine whether the payee type is singular.
     * @return <code>true</code> if the payee type is singular, <code>false</code> otherwise.
     */
    public boolean isSingular() {
        switch (this) {
            case TAXMAN:
            case GOVERNMENT:
            case MARKET:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the PayeeType can parent the deposit type.
     * @param pClass the Deposit type
     * @return <code>true</code> if the payee type can the deposit type, <code>false</code>
     * otherwise.
     */
    public boolean canParentDeposit(final MoneyWiseDepositCategoryClass pClass) {
        switch (this) {
            case GOVERNMENT:
                return !MoneyWiseDepositCategoryClass.CHECKING.equals(pClass);
            case INSTITUTION:
            case EMPLOYER:
                return true;
            case PAYEE:
            case INDIVIDUAL:
            case MARKET:
            case TAXMAN:
            case ANNUITY:
            default:
                return false;
        }
    }

    /**
     * Determine whether the PayeeType can parent the loan type.
     * @param pClass the Loan type
     * @return <code>true</code> if the payee type can the loan type, <code>false</code> otherwise.
     */
    public boolean canParentLoan(final MoneyWiseLoanCategoryClass pClass) {
        switch (this) {
            case TAXMAN:
            case GOVERNMENT:
            case INSTITUTION:
            case EMPLOYER:
                return !MoneyWiseLoanCategoryClass.PRIVATELOAN.equals(pClass);
            case INDIVIDUAL:
                return MoneyWiseLoanCategoryClass.PRIVATELOAN.equals(pClass);
            case MARKET:
            case PAYEE:
            case ANNUITY:
            default:
                return false;
        }
    }

    /**
     * Determine whether the PayeeType can parent the security type.
     * @param pClass the Security type
     * @return <code>true</code> if the payee type can parent the security type, <code>false</code>
     * otherwise.
     */
    public boolean canParentSecurity(final MoneyWiseSecurityClass pClass) {
        switch (this) {
            case MARKET:
                return pClass.needsMarketParent();
            case INSTITUTION:
            case EMPLOYER:
                return !pClass.needsMarketParent();
            case GOVERNMENT:
                return pClass.isStatePension();
            case TAXMAN:
            case INDIVIDUAL:
            case PAYEE:
            case ANNUITY:
            default:
                return false;
        }
    }

    /**
     * Determine whether the PayeeType can parent a portfolio.
     * @return <code>true</code> if the payee type can parent a portfolio, <code>false</code>
     * otherwise.
     */
    public boolean canParentPortfolio() {
        switch (this) {
            case MARKET:
            case INSTITUTION:
            case EMPLOYER:
            case GOVERNMENT:
                return true;
            case TAXMAN:
            case INDIVIDUAL:
            case PAYEE:
            case ANNUITY:
            default:
                return false;
        }
    }

    /**
     * Determine whether the PayeeType can contribute to a pension.
     * @return <code>true</code> if the payee type can contribute to a pension, <code>false</code>
     * otherwise.
     */
    public boolean canContribPension() {
        switch (this) {
            case INSTITUTION:
            case EMPLOYER:
            case GOVERNMENT:
            case TAXMAN:
                return true;
            case MARKET:
            case INDIVIDUAL:
            case PAYEE:
            case ANNUITY:
            default:
                return false;
        }
    }

    /**
     * Determine whether the PayeeType can provide a taxedIncome.
     * @return <code>true</code> if the payee type can parent a portfolio, <code>false</code>
     * otherwise.
     */
    public boolean canProvideTaxedIncome() {
        switch (this) {
            case GOVERNMENT:
            case EMPLOYER:
            case INDIVIDUAL:
            case ANNUITY:
                return true;
            case MARKET:
            case TAXMAN:
            case PAYEE:
            default:
                return false;
        }
    }
}
