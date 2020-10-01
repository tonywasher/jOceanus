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

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisModule;

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
     * The package list.
     */
    private final List<ThemisStatsPackage> thePackages;

    /**
     * Constructor.
     * @param pModule the module
     */
    ThemisStatsModule(final ThemisAnalysisModule pModule) {
        /* Store parameters */
        theModule = pModule;

        /* Create lists */
        thePackages = new ArrayList<>();
    }

    /**
     * Obtain the module.
     * @return the module
     */
    public ThemisAnalysisModule getModule() {
        return theModule;
    }

    /**
     * Obtain package iterator.
     * @return the iterator
     */
    Iterator<ThemisStatsPackage> packageIterator() {
        return thePackages.iterator();
    }

    /**
     * Add package to list.
     * @param pPackage the package
     */
    void addPackage(final ThemisStatsPackage pPackage) {
        thePackages.add(pPackage);
    }
}
