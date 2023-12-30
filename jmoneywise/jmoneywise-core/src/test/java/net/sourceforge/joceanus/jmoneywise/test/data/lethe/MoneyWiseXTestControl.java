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
package net.sourceforge.joceanus.jmoneywise.test.data.lethe;

import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKTaxYearCache;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusXToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.helper.TethysUIHelperFactory;

/**
 * Test security.
 */
public class MoneyWiseXTestControl {
    /**
     * Main entry point.
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        /* Protect against exceptions */
        try {
            /* Create the data */
            final TethysUIHelperFactory myFactory = new TethysUIHelperFactory();
            final PrometheusXToolkit myToolkit = new PrometheusXToolkit(myFactory);
            final MoneyWiseData myData = new MoneyWiseData(myToolkit, new MoneyWiseXUKTaxYearCache());

            /* Initialise the data */
            new MoneyWiseXTestSecurity(myData).initSecurity();
            new MoneyWiseXTestCategories(myData).buildBasic();
            new MoneyWiseXTestAccounts(myData).createAccounts();

            /* Catch exceptions */
        } catch (OceanusException e) {
            e.printStackTrace();
        }
    }
}
