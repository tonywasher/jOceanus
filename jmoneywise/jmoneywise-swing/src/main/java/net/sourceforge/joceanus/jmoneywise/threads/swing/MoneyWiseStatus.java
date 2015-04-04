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
package net.sourceforge.joceanus.jmoneywise.threads.swing;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;
import net.sourceforge.joceanus.jprometheus.ui.swing.StatusBar;

/**
 * Finance Status Thread control.
 */
public class MoneyWiseStatus
        extends ThreadStatus<MoneyWiseData, MoneyWiseDataType> {
    /**
     * The view.
     */
    private final View theView;

    /**
     * Constructor.
     * @param pView the view
     * @param pStatusBar the status bar
     */
    public MoneyWiseStatus(final View pView,
                           final StatusBar pStatusBar) {
        /* Call super constructor */
        super(pView, pStatusBar);

        /* Store view */
        theView = pView;
    }

    /**
     * Obtain view.
     * @return the view
     */
    protected View getView() {
        return theView;
    }
}
