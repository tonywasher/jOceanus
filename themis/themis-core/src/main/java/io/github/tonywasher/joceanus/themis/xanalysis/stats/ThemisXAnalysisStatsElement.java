/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.themis.xanalysis.stats;

/**
 * Basis Stats element interface.
 */
public interface ThemisXAnalysisStatsElement {
    /**
     * Obtain the statistics.
     *
     * @return the statistics
     */
    ThemisXAnalysisStats getStats();

    /**
     * Add a method to the stats.
     *
     * @param pMethod the method element
     */
    default void addMethod(ThemisXAnalysisStatsElement pMethod) {
        throw new IllegalStateException();
    }

    /**
     * Add a class to the stats.
     *
     * @param pClazz the class element
     */
    default void addClass(ThemisXAnalysisStatsElement pClazz) {
        throw new IllegalStateException();
    }
}
