/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.solver.proj;

import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisFile;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisPackage;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverModuleDef;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverPackageDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver Package.
 */
public class ThemisXAnalysisSolverPackage
        implements ThemisXAnalysisSolverPackageDef {
    /**
     * The owning module.
     */
    private final ThemisXAnalysisSolverModuleDef theModule;

    /**
     * The underlying package.
     */
    private final ThemisXAnalysisPackage thePackage;

    /**
     * The files.
     */
    private final List<ThemisXAnalysisSolverFile> theFiles;

    /**
     * The list of packages that are referenced by this package.
     */
    private final List<ThemisXAnalysisSolverPackage> theReferenced;

    /**
     * Is the reference list circular?
     */
    private boolean isCircular;

    /**
     * Constructor.
     * @param pModule the owning module
     * @param pPackage the parsed package
     */
    ThemisXAnalysisSolverPackage(final ThemisXAnalysisSolverModuleDef pModule,
                                 final ThemisXAnalysisPackage pPackage) {
        /* Store the package and register with parser */
        theModule = pModule;
        thePackage = pPackage;
        theReferenced = new ArrayList<>();

        /* Populate the fileList */
        theFiles = new ArrayList<>();
        for (ThemisXAnalysisFile myFile : thePackage.getFiles()) {
            final ThemisXAnalysisSolverFile mySolverFile = new ThemisXAnalysisSolverFile(this, myFile);
            theFiles.add(mySolverFile);
        }
    }

    @Override
    public ThemisXAnalysisSolverModuleDef getOwningModule() {
        return theModule;
    }

    @Override
    public ThemisXAnalysisPackage getUnderlyingPackage() {
        return thePackage;
    }

    /**
     * Obtain the package name.
     * @return the package name
     */
    public String getPackageName() {
        return thePackage.getPackage();
    }

    /**
     * Obtain the file list.
     * @return the file list
     */
    public List<ThemisXAnalysisSolverFile> getFiles() {
        return theFiles;
    }

    /**
     * Obtain the list of referenced packages.
     * @return the list of referenced packages
     */
    public List<ThemisXAnalysisSolverPackage> getReferenced() {
        return theReferenced;
    }

    /**
     * Is teh reference list circular?
     * @return true/false
     */
    public boolean isCircular() {
        return isCircular;
    }

    /**
     * Mark the package as circular.
     */
    public void markCircular() {
        isCircular = true;
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

        /* Make sure that the object is a DSMPackage */
        if (!(pThat instanceof ThemisXAnalysisSolverPackage myThat)) {
            return false;
        }

        /* Check name of package */
        return getPackageName().equals(myThat.getPackageName());
    }

    @Override
    public int hashCode() {
        return getPackageName().hashCode();
    }

    @Override
    public String toString() {
        return thePackage.toString();
    }
}
