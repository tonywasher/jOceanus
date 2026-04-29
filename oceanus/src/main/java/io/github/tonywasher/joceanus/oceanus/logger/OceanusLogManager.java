/*
 * Oceanus: Java Utilities
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.oceanus.logger;

/**
 * Log Manager.
 */
public final class OceanusLogManager {
    /**
     * Private constructor.
     */
    private OceanusLogManager() {
    }

    /**
     * Obtain the singleton engine instance.
     *
     * @return the instance.
     */
    private static OceanusLogEngine getInstance() {
        return OceanusLogManagerHelper.INSTANCE;
    }

    /**
     * Obtain a logger.
     *
     * @param pOwner the owning class
     * @return the logger
     */
    public static OceanusLogger getLogger(final Class<?> pOwner) {
        return new OceanusLogger(getInstance(), pOwner);
    }

    /**
     * Set Sink.
     *
     * @param pSink the sink
     */
    public static void setSink(final OceanusLogSink pSink) {
        OceanusLogEngine.setSink(pSink);
    }

    /**
     * Log Manager Helper.
     */
    private static final class OceanusLogManagerHelper {
        /**
         * The Log Engine instance.
         */
        private static final OceanusLogEngine INSTANCE = new OceanusLogEngine();
    }
}
