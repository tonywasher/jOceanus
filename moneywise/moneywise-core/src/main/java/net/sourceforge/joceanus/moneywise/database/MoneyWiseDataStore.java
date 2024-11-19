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
package net.sourceforge.joceanus.moneywise.database;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.database.PrometheusDBConfig;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDataItem;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * Database extension for MoneyWiseData.
 * @author Tony Washer
 */
public class MoneyWiseDataStore
        extends PrometheusDataStore {
    /**
     * Construct a new Database class for load.
     * @param pDatabase the database
     * @param pConfig the config
     * @throws OceanusException on error
     */
    public MoneyWiseDataStore(final String pDatabase,
                              final PrometheusDBConfig pConfig) throws OceanusException {
        /* Call super-constructor */
        super(pDatabase, pConfig);

        /* Loop through the static types */
        for (MoneyWiseStaticDataType myType : MoneyWiseStaticDataType.values()) {
            /* Create the table */
            addTable(newTable(myType));
        }

        /* Loop through the basic types */
        for (MoneyWiseBasicDataType myType : MoneyWiseBasicDataType.values()) {
            /* Create the table */
            addTable(newTbble(myType));
        }
    }

    /**
     * Construct a new Database class for create database.
     * @param pConfig the config
     * @throws OceanusException on error
     */
    public MoneyWiseDataStore(final PrometheusDBConfig pConfig) throws OceanusException {
        /* Call super-constructor */
        super(pConfig);

        /* Loop through the static types */
        for (MoneyWiseStaticDataType myType : MoneyWiseStaticDataType.values()) {
            /* Create the table */
            addTable(newTable(myType));
        }

        /* Loop through the basic types */
        for (MoneyWiseBasicDataType myType : MoneyWiseBasicDataType.values()) {
            /* Create the table */
            addTable(newTbble(myType));
        }
    }

    /**
     * Create new table of required type.
     * @param pDataType the data type
     * @return the new table
     */
    private PrometheusTableDataItem<?> newTable(final MoneyWiseStaticDataType pDataType) {
        /* Switch on data Type */
        switch (pDataType) {
            case DEPOSITTYPE:
                return new MoneyWiseTableDepositCategoryType(this);
            case CASHTYPE:
                return new MoneyWiseTableCashCategoryType(this);
            case LOANTYPE:
                return new MoneyWiseTableLoanCategoryType(this);
            case PORTFOLIOTYPE:
                return new MoneyWiseTablePortfolioType(this);
            case PAYEETYPE:
                return new MoneyWiseTablePayeeType(this);
            case SECURITYTYPE:
                return new MoneyWiseTableSecurityType(this);
            case TRANSTYPE:
                return new MoneyWiseTableTransCategoryType(this);
            case ACCOUNTINFOTYPE:
                return new MoneyWiseTableAccountInfoType(this);
            case TRANSINFOTYPE:
                return new MoneyWiseTableTransInfoType(this);
            case CURRENCY:
                return new MoneyWiseTableCurrency(this);
            case TAXBASIS:
                return new MoneyWiseTableTaxBasis(this);
            default:
                throw new IllegalArgumentException(pDataType.toString());
        }
    }

    /**
     * Create new table of required type.
     * @param pDataType the data type
     * @return the new table
     */
    private PrometheusTableDataItem<?> newTbble(final MoneyWiseBasicDataType pDataType) {
        /* Switch on data Type */
        switch (pDataType) {
            case TRANSTAG:
                return new MoneyWiseTableTransTag(this);
            case REGION:
                return new MoneyWiseTableRegion(this);
            case DEPOSITCATEGORY:
                return new MoneyWiseTableDepositCategory(this);
            case CASHCATEGORY:
                return new MoneyWiseTableCashCategory(this);
            case LOANCATEGORY:
                return new MoneyWiseTableLoanCategory(this);
            case TRANSCATEGORY:
                return new MoneyWiseTableTransCategory(this);
            case EXCHANGERATE:
                return new MoneyWiseTableExchangeRate(this);
            case PAYEE:
                return new MoneyWiseTablePayee(this);
            case PAYEEINFO:
                return new MoneyWiseTablePayeeInfo(this);
            case SECURITY:
                return new MoneyWiseTableSecurity(this);
            case SECURITYPRICE:
                return new MoneyWiseTableSecurityPrice(this);
            case SECURITYINFO:
                return new MoneyWiseTableSecurityInfo(this);
            case DEPOSIT:
                return new MoneyWiseTableDeposit(this);
            case DEPOSITRATE:
                return new MoneyWiseTableDepositRate(this);
            case DEPOSITINFO:
                return new MoneyWiseTableDepositInfo(this);
            case CASH:
                return new MoneyWiseTableCash(this);
            case CASHINFO:
                return new MoneyWiseTableCashInfo(this);
            case LOAN:
                return new MoneyWiseTableLoan(this);
            case LOANINFO:
                return new MoneyWiseTableLoanInfo(this);
            case PORTFOLIO:
                return new MoneyWiseTablePortfolio(this);
            case PORTFOLIOINFO:
                return new MoneyWiseTablePortfolioInfo(this);
            case TRANSACTION:
                return new MoneyWiseTableTransaction(this);
            case TRANSACTIONINFO:
                return new MoneyWiseTableTransInfo(this);
            default:
                throw new IllegalArgumentException(pDataType.toString());
        }
    }
}
