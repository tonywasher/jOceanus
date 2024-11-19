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
package net.sourceforge.joceanus.moneywise.help;

import net.sourceforge.joceanus.metis.help.MetisHelpEntry;
import net.sourceforge.joceanus.metis.help.MetisHelpModule;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * Help Module for FinanceApp.
 * @author Tony Washer
 */
public class MoneyWiseHelp
        extends MetisHelpModule {
    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MoneyWiseHelp() throws OceanusException {
        /* Initialise the underlying module */
        super("MoneyWise Help");

        /* Create accounts tree */
        final MetisHelpEntry myAccounts = addRootEntry(defineContentsEntry("Accounts"));
        myAccounts.addChildEntry(defineHelpEntry("Deposits", MoneyWiseHelpPage.HELP_DEPOSITS));
        myAccounts.addChildEntry(defineHelpEntry("Loans", MoneyWiseHelpPage.HELP_LOANS));

        /* Create static tree */
        final MetisHelpEntry myStatic = addRootEntry(defineContentsEntry("StaticData"));
        myStatic.addChildEntry(defineHelpEntry("AccountTypes", MoneyWiseHelpPage.HELP_ACCOUNTTYPES));
        myStatic.addChildEntry(defineHelpEntry("TransactionTypes", MoneyWiseHelpPage.HELP_TRANTYPES));

        /* Load help pages */
        loadHelpPages();

        /* Load the CSS */
        loadCSS(MoneyWiseHelpStyleSheet.CSS_HELP);
    }
}
