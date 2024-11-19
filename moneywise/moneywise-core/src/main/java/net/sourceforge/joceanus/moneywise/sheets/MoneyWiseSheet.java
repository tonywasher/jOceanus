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
package net.sourceforge.joceanus.moneywise.sheets;

import net.sourceforge.joceanus.gordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetReader;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetWriter;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

/**
 * SpreadSheet extension for MoneyWiseData.
 * @author Tony Washer
 */
public class MoneyWiseSheet
        extends PrometheusSpreadSheet {
    /**
     * The Data file name.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public MoneyWiseSheet(final TethysUIFactory<?> pFactory) {
        theGuiFactory = pFactory;
    }

    @Override
    protected PrometheusSheetReader getSheetReader(final TethysUIThreadStatusReport pReport,
                                                   final GordianPasswordManager pPasswordMgr) {
        /* Create a MoneyWise Reader object and return it */
        return new MoneyWiseReader(theGuiFactory, pReport, pPasswordMgr);
    }

    @Override
    protected PrometheusSheetWriter getSheetWriter(final TethysUIThreadStatusReport pReport) {
        /* Create a MoneyWise Writer object and return it */
        return new MoneyWiseWriter(theGuiFactory, pReport);
    }
}
