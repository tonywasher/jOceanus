/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.core.thread;

import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;

/**
 * Thread Status Manager.
 */
public interface TethysUICoreThreadStatusManager
        extends TethysUIComponent {
    /**
     * set Progress.
     * @param pStatus the status to apply
     */
    void setProgress(TethysUICoreThreadStatus pStatus);

    /**
     * set Completion.
     */
    void setCompletion();

    /**
     * set Failure.
     * @param pException the exception
     */
    void setFailure(Throwable pException);

    /**
     * set Cancelled.
     */
    void setCancelled();
}
