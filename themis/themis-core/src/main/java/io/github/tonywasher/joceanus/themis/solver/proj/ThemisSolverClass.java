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
package io.github.tonywasher.joceanus.themis.solver.proj;

import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisMethodInstance;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverDef.ThemisSolverClassDef;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverDef.ThemisSolverFileDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver Class.
 */
public class ThemisSolverClass
        implements ThemisSolverClassDef {
    /**
     * The fully qualified name of the class.
     */
    private final String theFullName;

    /**
     * The owning file.
     */
    private final ThemisSolverFileDef theFile;

    /**
     * The contained class.
     */
    private final ThemisClassInstance theClass;

    /**
     * The methods.
     */
    private final List<ThemisSolverMethod> theMethods;

    /**
     * The ancestors.
     */
    private final List<String> theAncestors;

    /**
     * Is this a standard class?
     */
    private final boolean isStandard;

    /**
     * Constructor.
     *
     * @param pFile  the owning file
     * @param pClass the parsed class
     */
    ThemisSolverClass(final ThemisSolverFileDef pFile,
                      final ThemisClassInstance pClass) {
        /* Store the parameters */
        theFile = pFile;
        theClass = pClass;
        theAncestors = new ArrayList<>();

        /* Access the full name */
        theFullName = theClass.getFullName();
        isStandard = pFile.getOwningPackage().isStandard();

        /* Populate the methodList */
        theMethods = new ArrayList<>();
        final ThemisInstance myNode = (ThemisInstance) theClass;
        for (ThemisInstance myMethod : myNode.discoverChildren(ThemisMethodInstance.class::isInstance)) {
            final ThemisSolverMethod mySolverMethod = new ThemisSolverMethod(this, (ThemisMethodInstance) myMethod);
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
    public ThemisSolverFileDef getOwningFile() {
        return theFile;
    }

    @Override
    public ThemisClassInstance getUnderlyingClass() {
        return theClass;
    }

    /**
     * Is this a standard class?
     *
     * @return true/false
     */
    public boolean isStandard() {
        return isStandard;
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
    public List<ThemisSolverMethod> getMethods() {
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

        /* Make sure that the object is a Class */
        if (!(pThat instanceof ThemisSolverClass myThat)) {
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

    /**
     * Obtain the list of ancestors.
     *
     * @return the list
     */
    public List<String> getAncestors() {
        return theAncestors;
    }

    /**
     * Add ancestor.
     *
     * @param pAncestor the ancestor
     */
    public void addAncestor(final String pAncestor) {
        theAncestors.add(pAncestor);
    }
}
