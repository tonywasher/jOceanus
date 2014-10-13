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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of Payee Type Classes.
 */
public enum PayeeTypeClass implements StaticInterface {
    /**
     * Generic Payee Account.
     * <p>
     * This is a simple payee that represents an entity that monies are paid to.
     */
    PAYEE(1, 0),

    /**
     * Employer Account.
     * <p>
     * This is an employer account which is a specialised form of an {@link #INSTITUTION} payee. It has the ability to pay dividends.
     */
    EMPLOYER(2, 1),

    /**
     * Institution Payee.
     * <p>
     * This is an institution (e.g. a bank) that holds another account of behalf of the client. It is a specialised form of payee.
     */
    INSTITUTION(3, 2),

    /**
     * LoanHolder Account.
     * <p>
     * This is an individual who can own a PrivateLoan account, and who can be inherited from. It is a specialised form of a payee.
     */
    INDIVIDUAL(4, 3),

    /**
     * Inland Revenue.
     * <p>
     * This is a singular payee representing the tax authority. All TaxCredits etc. are deemed to have been paid to the single account of this type.
     */
    TAXMAN(5, 4),

    /**
     * Government.
     * <p>
     * This is a singular payee representing the government. All Local Taxes should be paid to the single account of this type.
     */
    GOVERNMENT(6, 5),

    /**
     * Market pseudo account.
     * <p>
     * This is a singular payee representing the market. All increases/decreases in value of an asset that are due to fluctuations in unit prices are viewed as
     * income/expense from the single account of this type.
     */
    MARKET(7, 6);

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
            theName = StaticDataResource.getKeyForPayeeType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     */
    private PayeeTypeClass(final int uId,
                           final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JOceanusException on error
     */
    public static PayeeTypeClass fromId(final int id) throws JOceanusException {
        for (PayeeTypeClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.PAYEETYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the AccountCategoryType is a dividend provider.
     * @return <code>true</code> if the account category type is a dividend provider, <code>false</code> otherwise.
     */
    public boolean isDividend() {
        switch (this) {
            case EMPLOYER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the category type is singular.
     * @return <code>true</code> if the account category type is singular, <code>false</code> otherwise.
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
     * @return <code>true</code> if the payee type can the deposit type, <code>false</code> otherwise.
     */
    public boolean canParentDeposit(final DepositCategoryClass pClass) {
        switch (this) {
            case GOVERNMENT:
                return !DepositCategoryClass.CHECKING.equals(pClass);
            case INSTITUTION:
            case EMPLOYER:
                return true;
            case PAYEE:
            case INDIVIDUAL:
            case MARKET:
            case TAXMAN:
            default:
                return false;
        }
    }

    /**
     * Determine whether the PayeeType can parent the loan type.
     * @param pClass the Loan type
     * @return <code>true</code> if the payee type can the loan type, <code>false</code> otherwise.
     */
    public boolean canParentLoan(final LoanCategoryClass pClass) {
        switch (this) {
            case TAXMAN:
            case GOVERNMENT:
            case INSTITUTION:
            case EMPLOYER:
                return !LoanCategoryClass.PRIVATELOAN.equals(pClass);
            case INDIVIDUAL:
                return LoanCategoryClass.PRIVATELOAN.equals(pClass);
            case MARKET:
            case PAYEE:
            default:
                return false;
        }
    }

    /**
     * Determine whether the PayeeType can parent the security type.
     * @param pClass the Security type
     * @return <code>true</code> if the payee type can parent the security type, <code>false</code> otherwise.
     */
    public boolean canParentSecurity(final SecurityTypeClass pClass) {
        switch (this) {
            case MARKET:
                return pClass.needsMarketParent();
            case INSTITUTION:
            case EMPLOYER:
                return !pClass.needsMarketParent();
            case GOVERNMENT:
            case TAXMAN:
            case INDIVIDUAL:
            case PAYEE:
            default:
                return false;
        }
    }

    /**
     * Determine whether the PayeeType can parent a portfolio.
     * @return <code>true</code> if the payee type can parent a portfolio, <code>false</code> otherwise.
     */
    public boolean canParentPortfolio() {
        switch (this) {
            case MARKET:
            case INSTITUTION:
            case EMPLOYER:
                return true;
            case GOVERNMENT:
            case TAXMAN:
            case INDIVIDUAL:
            case PAYEE:
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType can issue grant income.
     * @return <code>true</code> if the account category type can grant income, <code>false</code> otherwise.
     */
    public boolean canGrant() {
        switch (this) {
            case INDIVIDUAL:
            case INSTITUTION:
            case GOVERNMENT:
                return true;
            default:
                return false;
        }
    }
}
