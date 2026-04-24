/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.solver.mapper;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisNodeInstance;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeImport;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverClass;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverFile;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverModule;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverPackage;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverProject;
import io.github.tonywasher.joceanus.themis.solver.reflect.ThemisReflectExternal;
import io.github.tonywasher.joceanus.themis.solver.reflect.ThemisReflectJar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Project State.
 */
public class ThemisMapperProjectState
        implements AutoCloseable {
    /**
     * Map of all classes defined in the project.
     */
    private final Map<String, ThemisSolverClass> theProjectClasses;

    /**
     * Map of all external classes referenced in the project.
     */
    private final Map<String, ThemisReflectExternal> theExternalClasses;

    /**
     * The Jar parser.
     */
    private final ThemisReflectJar theJar;

    /**
     * Constructor.
     *
     * @param pProject the project
     * @throws OceanusException on error
     */
    ThemisMapperProjectState(final ThemisSolverProject pProject) throws OceanusException {
        /* build the project classMap */
        theProjectClasses = new LinkedHashMap<>();
        buildProjectClassMap(pProject);

        /* build the external classMap */
        theExternalClasses = new LinkedHashMap<>();
        buildExternalClassMap(pProject);

        /* Process external classes */
        theJar = new ThemisReflectJar(pProject.getProjectParser(), theExternalClasses);
        theJar.processExternalClasses();
    }

    /**
     * Obtain the project classes.
     *
     * @return the project classes.
     */
    Map<String, ThemisSolverClass> getProjectClassMap() {
        return theProjectClasses;
    }

    /**
     * Obtain the external classes.
     *
     * @return the external classes.
     */
    Map<String, ThemisReflectExternal> getExternalClassMap() {
        return theExternalClasses;
    }

    /**
     * Build project classMap.
     *
     * @param pProject the project
     */
    private void buildProjectClassMap(final ThemisSolverProject pProject) {
        /* Loop through all modules */
        for (ThemisSolverModule myModule : pProject.getModules()) {
            /* Loop through all packages */
            for (ThemisSolverPackage myPackage : myModule.getPackages().values()) {
                buildProjectClassMap(myPackage);
            }
        }
    }

    /**
     * Build project classMap.
     *
     * @param pPackage the package
     */
    private void buildProjectClassMap(final ThemisSolverPackage pPackage) {
        /* Loop through all files */
        for (ThemisSolverFile myFile : pPackage.getFiles()) {
            /* Loop through all classes */
            for (ThemisSolverClass myClass : myFile.getClasses()) {
                theProjectClasses.put(myClass.getFullName(), myClass);
            }
        }
    }

    /**
     * Build external classMap.
     *
     * @param pProject the project
     */
    private void buildExternalClassMap(final ThemisSolverProject pProject) {
        /* Loop through all modules */
        for (ThemisSolverModule myModule : pProject.getModules()) {
            /* Loop through all packages */
            for (ThemisSolverPackage myPackage : myModule.getPackages().values()) {
                buildExternalClassMap(myPackage);
            }
        }
    }

    /**
     * Build external classMap.
     *
     * @param pPackage the package to process.
     */
    private void buildExternalClassMap(final ThemisSolverPackage pPackage) {
        /* Loop through all files */
        for (ThemisSolverFile myFile : pPackage.getFiles()) {
            /* Process the imports */
            for (ThemisNodeInstance myInstance : myFile.getUnderlyingFile().getContents().getImports()) {
                /* Determine full name */
                final ThemisNodeImport myImport = (ThemisNodeImport) myInstance;
                final String myFullName = myImport.getFullName();

                /* If this is a previously unseen class */
                if (!theProjectClasses.containsKey(myFullName)) {
                    /* Ensure the external class map */
                    theExternalClasses.computeIfAbsent(myFullName, n -> new ThemisReflectExternal(myImport));
                }
            }
        }
    }

    /**
     * Obtain a list of all children of a project class.
     *
     * @param pClass the class
     * @return the children
     */
    public List<ThemisClassInstance> listAllInherited(final String pClass) {
        /* Create list of all ancestors */
        final List<String> myAncestors = listAllAncestors(pClass);

        /* Build list of all children of the ancestors */
        final List<ThemisClassInstance> myResult = new ArrayList<>();
        for (String myAncestor : myAncestors) {
            listAllInherited(myResult, myAncestor);
        }
        return myResult;
    }

    /**
     * Obtain a list of all inherited children of a class.
     *
     * @param pResult the list to populate
     * @param pClass  the class
     */
    private void listAllInherited(final List<ThemisClassInstance> pResult,
                                  final String pClass) {
        final ThemisSolverClass myProjectClass = theProjectClasses.get(pClass);
        if (myProjectClass != null) {
            listAllProjectInherited(pResult, myProjectClass);
        } else {
            listAllExternalInherited(pResult, theExternalClasses.get(pClass));
        }
    }

    /**
     * Obtain a list of all inherited children of a project class.
     *
     * @param pResult the list to populate
     * @param pClass  the class
     */
    private void listAllProjectInherited(final List<ThemisClassInstance> pResult,
                                         final ThemisSolverClass pClass) {
        final String myParent = pClass.getFullName();
        for (ThemisSolverClass myChild : theProjectClasses.values()) {
            final String myCheckName = myParent + ThemisChar.PERIOD + myChild.getName();
            if (myCheckName.equals(myChild.getFullName())) {
                pResult.add(myChild.getUnderlyingClass());
            }
        }
    }

    /**
     * Obtain a list of all inherited children of an external class.
     *
     * @param pResult the list to populate
     * @param pClass  the class
     */
    private void listAllExternalInherited(final List<ThemisClassInstance> pResult,
                                          final ThemisReflectExternal pClass) {
        final String myParent = pClass.getFullName();
        for (ThemisReflectExternal myChild : theExternalClasses.values()) {
            final String myCheckName = myParent + ThemisChar.PERIOD + myChild.getName();
            if (myCheckName.equals(myChild.getFullName())) {
                pResult.add(myChild.getClassInstance());
            }
        }
    }

    /**
     * Obtain a list of all ancestors of a class.
     *
     * @param pClass the class
     * @return the ancestors
     */
    private List<String> listAllAncestors(final String pClass) {
        final List<String> myResult = new ArrayList<>();
        final ThemisSolverClass myClass = theProjectClasses.get(pClass);
        if (myClass != null) {
            listAllProjectAncestors(myResult, myClass);
        } else {
            listAllExternalAncestors(myResult, theExternalClasses.get(pClass));
        }
        return myResult;
    }

    /**
     * Populate a list of all ancestors of a project class.
     *
     * @param pExisting the list of existing ancestors
     * @param pClass    the class
     */
    private void listAllProjectAncestors(final List<String> pExisting,
                                         final ThemisSolverClass pClass) {
        for (String myAncestor : pClass.getAncestors()) {
            /* If the ancestor is unknown */
            if (!pExisting.contains(myAncestor)) {
                /* Add the ancestor */
                pExisting.add(myAncestor);

                /* If the ancestor is a project file */
                final ThemisSolverClass myClass = theProjectClasses.get(myAncestor);
                if (myClass != null) {
                    /* Process all ancestors */
                    listAllProjectAncestors(pExisting, myClass);

                    /* else must be external so add all ancestors */
                } else {
                    listAllExternalAncestors(pExisting, theExternalClasses.get(myAncestor));
                }
            }
        }
    }

    /**
     * Populate a list of all ancestors of an external class.
     *
     * @param pExisting the list of existing ancestors
     * @param pClass    the class
     */
    private void listAllExternalAncestors(final List<String> pExisting,
                                          final ThemisReflectExternal pClass) {
        for (String myAncestor : pClass.getAncestors()) {
            /* If the ancestor is unknown */
            if (!pExisting.contains(myAncestor)) {
                /* Add the ancestor and all of its ancestors */
                pExisting.add(myAncestor);
                listAllExternalAncestors(pExisting, theExternalClasses.get(myAncestor));
            }
        }
    }

    /**
     * Try a class as a java.lang class.
     *
     * @param pName the class name
     * @return the loaded class or null if it did not exist
     */
    public ThemisReflectExternal tryJavaLang(final String pName) {
        return theJar.tryJavaLang(pName);
    }

    /**
     * Try a class as a java.lang class.
     *
     * @param pName the class name
     * @return the loaded class or null if it did not exist
     */
    public ThemisReflectExternal tryNamedClass(final String pName) {
        return theJar.tryNamedClass(pName);
    }

    @Override
    public void close() {
        theJar.close();
    }
}
