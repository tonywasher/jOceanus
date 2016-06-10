/*******************************************************************************
- * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.sheets;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetReader;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetWriter;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSpreadSheet;

/**
 * SpreadSheet extension for MoneyWiseData.
 * @author Tony Washer
 */
public class MoneyWiseSheet
        extends PrometheusSpreadSheet<MoneyWiseData> {
    @Override
    protected PrometheusSheetReader<MoneyWiseData> getSheetReader(final MetisThreadStatusReport pReport,
                                                                  final GordianHashManager pSecureMgr) {
        /* Create a MoneyWise Reader object and return it */
        return new MoneyWiseReader(pReport, pSecureMgr);
    }

    @Override
    protected PrometheusSheetWriter<MoneyWiseData> getSheetWriter(final MetisThreadStatusReport pReport) {
        /* Create a MoneyWise Writer object and return it */
        return new MoneyWiseWriter(pReport);
    }
}
