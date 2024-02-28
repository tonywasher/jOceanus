/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.test.data;

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.builder.MoneyWiseTransactionBuilder;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Transactions builder.
 */
public class MoneyWiseTestTransactions {
    /**
     * TrasnactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * The dataSet.
     */
    private final MoneyWiseDataSet theData;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    MoneyWiseTestTransactions(final MoneyWiseDataSet pDataSet) {
        /* Create the builders */
        theTransBuilder = new MoneyWiseTransactionBuilder(pDataSet);
        theData = pDataSet;
    }

    /**
     * Create payees.
     * @throws OceanusException on error
     */
    public void createTransfers() throws OceanusException {
        theTransBuilder.date("01-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idDP_NatWideFlexDirect).amount("2000").build();
        theTransBuilder.date("01-Jun-1985").category(MoneyWiseTestCategories.idTC_Salary)
                .pair(MoneyWiseTestAccounts.idPY_IBM, MoneyWiseTestAccounts.idDP_NatWideFlexDirect)
                .amount("2000").taxCredit("0").build();
        theData.getTransactions().resolveDataSetLinks();
    }
}
