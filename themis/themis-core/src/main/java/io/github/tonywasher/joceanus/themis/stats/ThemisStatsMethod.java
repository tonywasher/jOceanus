/*
 * Themis: Java Project Framework
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
package io.github.tonywasher.joceanus.themis.stats;

import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisMethodInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Statistics Method.
 */
public class ThemisStatsMethod
        implements ThemisStatsElement {
    /**
     * The underlying package.
     */
    private final ThemisMethodInstance theMethod;

    /**
     * The stats.
     */
    private final ThemisStats theStats;

    /**
     * The classes.
     */
    private final List<ThemisStatsElement> theClasses;

    /**
     * Constructor.
     *
     * @param pMethod the parsed method
     */
    ThemisStatsMethod(final ThemisMethodInstance pMethod) {
        /* Store the method */
        theMethod = pMethod;

        /* Create the stats */
        theStats = new ThemisStats();

        /* Create the class list */
        theClasses = new ArrayList<>();
    }

    @Override
    public String getName() {
        return theMethod.getName();
    }

    /**
     * Obtain the method.
     *
     * @return the method
     */
    public ThemisMethodInstance getUnderlying() {
        return theMethod;
    }

    @Override
    public ThemisStats getStats() {
        return theStats;
    }

    /**
     * Obtain the classes.
     *
     * @return the classes
     */
    public List<ThemisStatsElement> getClasses() {
        return theClasses;
    }

    @Override
    public void addClass(final ThemisStatsElement pElement) {
        theClasses.add(pElement);
    }

    @Override
    public String toString() {
        return theMethod.toString();
    }
}
