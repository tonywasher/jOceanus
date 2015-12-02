/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.views;

import net.sourceforge.joceanus.jmetis.data.MetisExceptionWrapper;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;

/**
 * Interface to status bar.
 */
public interface StatusDisplay {
    /**
     * Update StatusBar.
     * @param pStatus the status data
     */
    void updateStatusBar(final StatusData pStatus);

    /**
     * Set Success string.
     * @param pOperation the operation
     */
    void setSuccess(final String pOperation);

    /**
     * Set Failure details.
     * @param pOperation the operation
     * @param pErrors the error list
     */
    void setFailure(final String pOperation,
                    final DataErrorList<MetisExceptionWrapper> pErrors);

    /**
     * Show progress panel.
     */
    void showProgressPanel();
}
