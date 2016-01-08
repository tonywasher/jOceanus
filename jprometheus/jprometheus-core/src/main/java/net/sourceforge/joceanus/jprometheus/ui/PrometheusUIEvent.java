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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jprometheus/jprometheus-core/src/main/java/net/sourceforge/joceanus/jprometheus/views/DataControl.java $
 * $Revision: 663 $
 * $Author: Tony $
 * $Date: 2015-12-28 14:55:10 +0000 (Mon, 28 Dec 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.ui;

/**
 * Prometheus UI events.
 */
public enum PrometheusUIEvent {
    /**
     * OK.
     */
    OK,

    /**
     * Undo.
     */
    UNDO,

    /**
     * ReWind.
     */
    REWIND,

    /**
     * Reset.
     */
    RESET,
}
