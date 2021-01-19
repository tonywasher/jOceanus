/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jthemis.statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile.ThemisAnalysisObject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisIf.ThemisIteratorChain;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMClass;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMClass.ThemisSMClassType;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStat;

/**
 * Class statistics.
 */
public class ThemisStatsClass
        extends ThemisStatsBase {
    /**
     * The class.
     */
    private final ThemisAnalysisObject theClass;

    /**
     * The sourceMeter class Stats.
     */
    private final ThemisSMClass theSMClass;

    /**
     * The class list.
     */
    private final List<ThemisStatsBase> theClasses;

    /**
     * The method list.
     */
    private final List<ThemisStatsBase> theMethods;

    /**
     * Constructor.
     * @param pClass the class
     * @param pSourceMeter the sourceMeter stats
     */
    ThemisStatsClass(final ThemisAnalysisObject pClass,
                     final ThemisSMClass pSourceMeter) {
        /* Store parameters */
        theClass = pClass;
        theSMClass = pSourceMeter;

        /* Create lists */
        theClasses = new ArrayList<>();
        theMethods = new ArrayList<>();
    }

    /**
     * Obtain the class.
     * @return the class
     */
    public ThemisAnalysisObject getObject() {
        return theClass;
    }

    /**
     * Obtain the classType.
     * @return the classType
     */
    public ThemisSMClassType getClassType() {
        return theSMClass != null ? theSMClass.getClassType() : ThemisSMClassType.CLASS;
    }

    @Override
    public ThemisSMClass getSourceMeter() {
        return theSMClass;
    }

    @Override
    public Map<ThemisSMStat, Integer> getSourceMeterStats() {
        return theSMClass == null ? null : theSMClass.getStatistics();
    }

    @Override
    public Iterator<ThemisStatsBase> childIterator() {
        return new ThemisIteratorChain<>(theClasses.iterator(), theMethods.iterator());
    }

    @Override
    public String toString() {
        return theClass.getShortName();
    }

    @Override
    public void addClass(final ThemisStatsClass pClass) {
        /* Add class to list */
        theClasses.add(pClass);
        pClass.setParent(this);

        /* Adjust counts */
        pClass.addStatsToTotals();
        addChildTotals(pClass);
    }

    @Override
    public void addMethod(final ThemisStatsMethod pMethod) {
        /* Add method to list */
        theMethods.add(pMethod);
        pMethod.setParent(this);

        /* Add method counts to class */
        pMethod.addMethodStatsToTotals();
        addMethodStatsToClass(pMethod);
        addChildTotals(pMethod);
    }
}