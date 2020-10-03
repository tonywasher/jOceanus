/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile.ThemisAnalysisObject;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMClass;
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
    private final List<ThemisStatsClass> theClasses;

    /**
     * The methods list.
     */
    private final List<ThemisStatsMethod> theMethods;

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

    @Override
    public ThemisSMClass getSourceMeter() {
        return theSMClass;
    }

    @Override
    public Iterator<ThemisStatsClass> classIterator() {
        return theClasses.iterator();
    }

    @Override
    public void addClass(final ThemisStatsClass pClass) {
        /* Add class to list */
        theClasses.add(pClass);

        /* Adjust counts */
        adjustStat(ThemisSMStat.TNCL, pClass.getStat(ThemisSMStat.TNCL));
        adjustStat(ThemisSMStat.TNIN, pClass.getStat(ThemisSMStat.TNIN));
        adjustStat(ThemisSMStat.TNEN, pClass.getStat(ThemisSMStat.TNEN));
        adjustStat(ThemisSMStat.TNM, pClass.getStat(ThemisSMStat.TNM));
        adjustStat(ThemisSMStat.TNOS, pClass.getStat(ThemisSMStat.TNOS));
        adjustStat(ThemisSMStat.TLOC, pClass.getStat(ThemisSMStat.TLOC));
        adjustStat(ThemisSMStat.TLLOC, pClass.getStat(ThemisSMStat.TLLOC));
        adjustStat(ThemisSMStat.TCLOC, pClass.getStat(ThemisSMStat.TCLOC));
        adjustStat(ThemisSMStat.TDLOC, pClass.getStat(ThemisSMStat.TDLOC));
    }

    @Override
    public Iterator<ThemisStatsMethod> methodIterator() {
        return theMethods.iterator();
    }

    @Override
    public void addMethod(final ThemisStatsMethod pMethod) {
        /* Add method to list */
        theMethods.add(pMethod);

        /* Adjust counts */
        adjustStat(ThemisSMStat.LOC, pMethod.getStat(ThemisSMStat.LOC));
        adjustStat(ThemisSMStat.TNCL, pMethod.getStat(ThemisSMStat.TNCL));
        adjustStat(ThemisSMStat.TNIN, pMethod.getStat(ThemisSMStat.TNIN));
        adjustStat(ThemisSMStat.TNEN, pMethod.getStat(ThemisSMStat.TNEN));
        adjustStat(ThemisSMStat.TNM, pMethod.getStat(ThemisSMStat.TNM));
        adjustStat(ThemisSMStat.TNOS, pMethod.getStat(ThemisSMStat.TNOS));
        adjustStat(ThemisSMStat.TLOC, pMethod.getStat(ThemisSMStat.TLOC));
        adjustStat(ThemisSMStat.TLLOC, pMethod.getStat(ThemisSMStat.TLLOC));
        adjustStat(ThemisSMStat.TCLOC, pMethod.getStat(ThemisSMStat.TCLOC));
        adjustStat(ThemisSMStat.TDLOC, pMethod.getStat(ThemisSMStat.TDLOC));
    }
}
