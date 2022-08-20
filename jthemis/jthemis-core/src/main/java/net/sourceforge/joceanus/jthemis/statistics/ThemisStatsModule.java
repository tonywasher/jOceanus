/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2022 Tony Washer
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

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisChar;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisModule;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStat;

/**
 * Module statistics.
 */
public class ThemisStatsModule
        extends ThemisStatsBase {
    /**
     * The module.
     */
    private final ThemisAnalysisModule theModule;

    /**
     * The child list.
     */
    private final List<ThemisStatsBase> theChildren;

    /**
     * The packagePrefix.
     */
    private String thePrefix;

    /**
     * Constructor.
     * @param pModule the module
     */
    ThemisStatsModule(final ThemisAnalysisModule pModule) {
        /* Store parameters */
        theModule = pModule;

        /* Create lists */
        theChildren = new ArrayList<>();
    }

    /**
     * Obtain the module.
     * @return the module
     */
    public ThemisAnalysisModule getModule() {
        return theModule;
    }

    @Override
    public String toString() {
        return theModule.getName();
    }

    /**
     * Obtain package iterator.
     * @return the iterator
     */
    public Iterator<ThemisStatsBase> childIterator() {
        return theChildren.iterator();
    }

    /**
     * Add package to list.
     * @param pPackage the package
     */
    void addPackage(final ThemisStatsPackage pPackage) {
        /* Adjust the prefix */
        adjustPrefix(pPackage);

        /* Add package to list */
        if (ThemisStatsPackage.ROOT.equals(pPackage.toString())) {
            theChildren.add(0, pPackage);
        } else {
            theChildren.add(pPackage);
        }
        pPackage.setParent(this);

        /* Increment # of packages */
        incrementStat(ThemisSMStat.TNPKG);

        /* Adjust count of files */
        adjustChildStat(pPackage, ThemisSMStat.TNFI);

        /* Adjust counts */
        addChildTotals(pPackage);
    }

    /**
     * Adjust the prefix.
     * @param pPackage the package
     */
    private void adjustPrefix(final ThemisStatsPackage pPackage) {
        /* Access the package name */
        final String myName = pPackage.getPackage().getPackage();

        /* If this is the first package */
        if (thePrefix == null) {
            /* Prefix is everything prior to the last subPackage */
            final int myIndex = myName.lastIndexOf(ThemisAnalysisChar.PERIOD);
            thePrefix = myIndex == -1 ? "" : myName.substring(0, myIndex);

            /* Find the common prefix */
        } else if (!myName.startsWith(thePrefix)) {
            /* Determine length */
            final int myLength = Math.min(thePrefix.length(), myName.length());

            /* Loop while prefixes are the same */
            for (int i = 0; i < myLength; i++) {
                /* If we have found a difference */
                if (thePrefix.charAt(i) != myName.charAt(i)) {
                    /* Strip the prefix down */
                    thePrefix = thePrefix.substring(0, i);
                    break;
                }
            }

            /* If the package is a prefix of the prefix */
            if (thePrefix.startsWith(myName)) {
                thePrefix = myName;
            }

            /* Update prefix for existing packages */
            theChildren.forEach(s -> ((ThemisStatsPackage) s).setPrefix(thePrefix));
        }

        /* Set prefix for package */
        pPackage.setPrefix(thePrefix);
    }
}
