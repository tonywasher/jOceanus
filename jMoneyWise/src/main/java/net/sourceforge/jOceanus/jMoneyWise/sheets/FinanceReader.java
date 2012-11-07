/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.sheets;

import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetReader;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * SheetReader extension for FinanceData.
 * @author Tony Washer
 */
public class FinanceReader
        extends SheetReader<FinanceData> {
    /**
     * Thread control.
     */
    private final TaskControl<FinanceData> theTask;

    /**
     * Constructor.
     * @param pTask the Task control
     */
    public FinanceReader(final TaskControl<FinanceData> pTask) {
        /* Call super-constructor */
        super(pTask);

        /* Store the task */
        theTask = pTask;
    }

    /**
     * Register sheets.
     */
    @Override
    protected void registerSheets() {
        /* Register the sheets */
        addSheet(new SheetAccountType(this));
        addSheet(new SheetTransactionType(this));
        addSheet(new SheetTaxType(this));
        addSheet(new SheetTaxRegime(this));
        addSheet(new SheetFrequency(this));
        addSheet(new SheetTaxYearInfoType(this));
        addSheet(new SheetAccountInfoType(this));
        addSheet(new SheetEventInfoType(this));
        addSheet(new SheetTaxYear(this));
        addSheet(new SheetTaxYearInfo(this));
        addSheet(new SheetAccount(this));
        addSheet(new SheetAccountRate(this));
        addSheet(new SheetAccountPrice(this));
        addSheet(new SheetPattern(this));
        addSheet(new SheetPatternNew(this));
        addSheet(new SheetAccountInfo(this));
        addSheet(new SheetEvent(this));
        addSheet(new SheetEventInfo(this));
        addSheet(new SheetEventData(this));
        addSheet(new SheetEventValues(this));
    }

    @Override
    protected FinanceData newDataSet() {
        /* Create the new DataSet */
        return theTask.getNewDataSet();
    }
}
