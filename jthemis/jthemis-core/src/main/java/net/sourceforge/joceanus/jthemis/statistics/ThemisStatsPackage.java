/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisChar;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisPackage;

/**
 * Package statistics.
 */
public class ThemisStatsPackage
        extends ThemisStatsBase {
    /**
     * The root name.
     */
    static final String ROOT = "(root)";

    /**
     * The package.
     */
    private final ThemisAnalysisPackage thePackage;

    /**
     * The child list.
     */
    private final List<ThemisStatsBase> theChildren;

    /**
     * The prefix.
     */
    private String thePrefix;

    /**
     * Constructor.
     * @param pPackage the package
     */
    ThemisStatsPackage(final ThemisAnalysisPackage pPackage) {
        /* Store parameters */
        thePackage = pPackage;

        /* Create lists */
        theChildren = new ArrayList<>();
    }

    /**
     * Obtain the package.
     * @return the package
     */
    public ThemisAnalysisPackage getPackage() {
        return thePackage;
    }

    @Override
    public Iterator<ThemisStatsBase> childIterator() {
        return theChildren.iterator();
    }

    /**
     * Set the prefix.
     * @param pPrefix the prefix
     */
    void setPrefix(final String pPrefix) {
        thePrefix = pPrefix;
    }

    /**
     * Add file to list.
     * @param pFile the file
     */
    void addFile(final ThemisStatsFile pFile) {
        /* Add file to list */
        theChildren.add(pFile);
        pFile.setParent(this);

        /* Increment # of files */
        incrementStat(ThemisStat.TNFI);

        /* Adjust counts */
        pFile.addStatsToTotals();
        addChildTotals(pFile);
    }

    @Override
    public String toString() {
        String myPackage = thePackage.getPackage();
        myPackage = myPackage.substring(thePrefix.length());
        if (myPackage.startsWith(Character.toString(ThemisAnalysisChar.PERIOD))) {
            myPackage = myPackage.substring(1);
        }
        return myPackage.length() > 0 ? myPackage : ROOT;
    }
}
