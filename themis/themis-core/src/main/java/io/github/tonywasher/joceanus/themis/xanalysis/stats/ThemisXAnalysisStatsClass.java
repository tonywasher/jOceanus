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
package io.github.tonywasher.joceanus.themis.xanalysis.stats;

import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Statistics Class.
 */
public class ThemisXAnalysisStatsClass
        implements ThemisXAnalysisStatsElement {
    /**
     * The underlying class.
     */
    private final ThemisXAnalysisClassInstance theClass;

    /**
     * The stats.
     */
    private final ThemisXAnalysisStats theStats;

    /**
     * The methods.
     */
    private final List<ThemisXAnalysisStatsMethod> theMethods;

    /**
     * The classes.
     */
    private final List<ThemisXAnalysisStatsClass> theClasses;

    /**
     * Constructor.
     *
     * @param pClass the parsed class
     */
    ThemisXAnalysisStatsClass(final ThemisXAnalysisClassInstance pClass) {
        /* Store the class */
        theClass = pClass;

        /* Create the stats */
        theStats = new ThemisXAnalysisStats();

        /* Create the class and method lists */
        theMethods = new ArrayList<>();
        theClasses = new ArrayList<>();
    }

    /**
     * Obtain the class.
     *
     * @return the class
     */
    public ThemisXAnalysisClassInstance getUnderlying() {
        return theClass;
    }

    @Override
    public ThemisXAnalysisStats getStats() {
        return theStats;
    }

    /**
     * Obtain the methods.
     *
     * @return the methods
     */
    List<ThemisXAnalysisStatsMethod> getMethods() {
        return theMethods;
    }

    /**
     * Obtain the classes.
     *
     * @return the classes
     */
    List<ThemisXAnalysisStatsClass> getClasses() {
        return theClasses;
    }

    @Override
    public void addMethod(final ThemisXAnalysisStatsElement pElement) {
        theMethods.add((ThemisXAnalysisStatsMethod) pElement);
    }

    @Override
    public void addClass(final ThemisXAnalysisStatsElement pElement) {
        theClasses.add((ThemisXAnalysisStatsClass) pElement);
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
        if (!(pThat instanceof ThemisXAnalysisStatsClass myThat)) {
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
