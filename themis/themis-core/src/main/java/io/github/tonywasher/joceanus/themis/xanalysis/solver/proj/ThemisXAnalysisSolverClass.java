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
package io.github.tonywasher.joceanus.themis.xanalysis.solver.proj;

import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisMethodInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverClassDef;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverFileDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver Class.
 */
public class ThemisXAnalysisSolverClass
        implements ThemisXAnalysisSolverClassDef {
    /**
     * The fully qualified name of the class.
     */
    private final String theFullName;

    /**
     * The owning file.
     */
    private final ThemisXAnalysisSolverFileDef theFile;

    /**
     * The contained class.
     */
    private final ThemisXAnalysisClassInstance theClass;

    /**
     * The methods.
     */
    private final List<ThemisXAnalysisSolverMethod> theMethods;

    /**
     * Is the reference list circular?
     */
    private boolean isCircular;

    /**
     * Constructor.
     *
     * @param pFile  the owning file
     * @param pClass the parsed class
     */
    ThemisXAnalysisSolverClass(final ThemisXAnalysisSolverFileDef pFile,
                               final ThemisXAnalysisClassInstance pClass) {
        /* Store the parameters */
        theFile = pFile;
        theClass = pClass;

        /* Access the full name */
        theFullName = theClass.getFullName();

        /* Populate the methodList */
        theMethods = new ArrayList<>();
        final ThemisXAnalysisInstance myNode = (ThemisXAnalysisInstance) theClass;
        for (ThemisXAnalysisInstance myMethod : myNode.discoverChildren(ThemisXAnalysisMethodInstance.class::isInstance)) {
            final ThemisXAnalysisSolverMethod mySolverMethod = new ThemisXAnalysisSolverMethod(this, (ThemisXAnalysisMethodInstance) myMethod);
            theMethods.add(mySolverMethod);
        }
    }

    /**
     * Obtain the name of the class.
     *
     * @return the name
     */
    public String getName() {
        return theClass.getName();
    }

    @Override
    public String getFullName() {
        return theFullName;
    }

    @Override
    public ThemisXAnalysisSolverFileDef getOwningFile() {
        return theFile;
    }

    @Override
    public ThemisXAnalysisClassInstance getUnderlyingClass() {
        return theClass;
    }

    /**
     * Is this a top-level class?
     *
     * @return true/false
     */
    public boolean isTopLevel() {
        return theClass.isTopLevel();
    }

    /**
     * Obtain the method list.
     *
     * @return the method list
     */
    public List<ThemisXAnalysisSolverMethod> getMethods() {
        return theMethods;
    }

    /**
     * Obtain the package.
     *
     * @return the packageName
     */
    public String getPackageName() {
        return theFile.getOwningPackage().toString();
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

        /* Make sure that the object is a DSMClass */
        if (!(pThat instanceof ThemisXAnalysisSolverClass myThat)) {
            return false;
        }

        /* Check full name */
        return theFullName.equals(myThat.getFullName());
    }

    @Override
    public int hashCode() {
        return theFullName.hashCode();
    }

    @Override
    public String toString() {
        return theFullName;
    }
}
