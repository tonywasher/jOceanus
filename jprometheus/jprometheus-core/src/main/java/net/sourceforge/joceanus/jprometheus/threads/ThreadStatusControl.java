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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jprometheus/jprometheus-swing/src/main/java/net/sourceforge/joceanus/jprometheus/threads/swing/ThreadStatus.java $
 * $Revision: 589 $
 * $Author: Tony $
 * $Date: 2015-04-02 15:53:05 +0100 (Thu, 02 Apr 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.threads;

import net.sourceforge.joceanus.jprometheus.views.StatusData;

/**
 * Thread Status Control interface.
 * @author Tony Washer
 */
public interface ThreadStatusControl {
    /**
     * is the thread cancelled?
     * @return true/false
     */
    boolean isCancelled();

    /**
     * publish status data.
     * @param pData the data to publish
     */
    void publishIt(final StatusData pData);
}