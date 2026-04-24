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

import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Statistics Class.
 */
public class ThemisStatsClass
        implements ThemisStatsElement {
    /**
     * The underlying class.
     */
    private final ThemisClassInstance theClass;

    /**
     * The stats.
     */
    private final ThemisStats theStats;

    /**
     * The methods.
     */
    private final List<ThemisStatsMethod> theMethods;

    /**
     * The classes.
     */
    private final List<ThemisStatsClass> theClasses;

    /**
     * Constructor.
     *
     * @param pClass the parsed class
     */
    ThemisStatsClass(final ThemisClassInstance pClass) {
        /* Store the class */
        theClass = pClass;

        /* Create the stats */
        theStats = new ThemisStats();

        /* Create the class and method lists */
        theMethods = new ArrayList<>();
        theClasses = new ArrayList<>();
    }

    /**
     * Obtain the class.
     *
     * @return the class
     */
    public ThemisClassInstance getUnderlying() {
        return theClass;
    }

    @Override
    public ThemisStats getStats() {
        return theStats;
    }

    /**
     * Obtain the methods.
     *
     * @return the methods
     */
    public List<ThemisStatsMethod> getMethods() {
        return theMethods;
    }

    /**
     * Obtain the classes.
     *
     * @return the classes
     */
    public List<ThemisStatsClass> getClasses() {
        return theClasses;
    }

    @Override
    public String getName() {
        return theClass.getName();
    }

    @Override
    public void addMethod(final ThemisStatsElement pElement) {
        theMethods.add((ThemisStatsMethod) pElement);
    }

    @Override
    public void addClass(final ThemisStatsElement pElement) {
        theClasses.add((ThemisStatsClass) pElement);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a StatsClass */
        if (!(pThat instanceof ThemisStatsClass myThat)) {
            return false;
        }

        /* Check equality of class */
        return theClass.equals(myThat.getUnderlying());
    }

    @Override
    public int hashCode() {
        return theClass.hashCode();
    }

    @Override
    public String toString() {
        return theClass.toString();
    }
}
