/*******************************************************************************
 * Subversion: Java SubVersion Management
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
package net.sourceforge.JSvnManager.data;


/**
 * Report Subversion events.
 * @author Tony Washer
 */
public class JSvnReporter {
    /**
     * Report status.
     */
    public interface ReportStatus {
        /**
         * Report Status
         * @param pStatus the status
         */
        void reportStatus(final String pStatus);
    }

    /**
     * Report Task.
     */
    public interface ReportTask extends ReportStatus {
        /**
         * Complete Task.
         * @param pTask the task that has completed.
         */
        void completeTask(final Object pTask);
    }
}
