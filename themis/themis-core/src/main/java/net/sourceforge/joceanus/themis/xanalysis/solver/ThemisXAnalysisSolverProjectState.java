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
package net.sourceforge.joceanus.themis.xanalysis.solver;

import com.github.javaparser.ast.Node;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNode;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeImport;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeName;
import net.sourceforge.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisType;
import net.sourceforge.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisTypeClassInterface;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverClass;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverFile;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverModule;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverProject;
import net.sourceforge.joceanus.themis.xanalysis.solver.reflect.ThemisXAnalysisReflectExternal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * State for solver.
 */
public class ThemisXAnalysisSolverProjectState {
    /**
     * Map of all java.lang classes.
     */
    private final Map<String, ThemisXAnalysisReflectExternal> theJavaLang;

    /**
     * Map of all classes defined in the project.
     */
    private final Map<String, ThemisXAnalysisSolverClass> theProjectClasses;

    /**
     * Map of all external classes referenced in the project.
     */
    private final Map<String, ThemisXAnalysisReflectExternal> theExternalClasses;

    /**
     * Map of all known short names in file.
     */
    private final Map<String, ThemisXAnalysisClassInstance> theKnownClasses;

    /**
     * The referenced classes.
     */
    private final List<ThemisXAnalysisSolverClass> theReferenced;

    /**
     * Constructor.
     * @param pProject the project
     */
    ThemisXAnalysisSolverProjectState(final ThemisXAnalysisSolverProject pProject) {
        /* Build the javaLang map */
        theJavaLang = ThemisXAnalysisReflectExternal.getJavaLangMap();

        /* build the project classMap */
        theProjectClasses = new LinkedHashMap<>();
        buildProjectClassMap(pProject);

        /* build the external classMap */
        theExternalClasses = new LinkedHashMap<>();
        buildExternalClassMap(pProject);

        /* Create the maps and lists */
        theKnownClasses = new LinkedHashMap<>();
        theReferenced = new ArrayList<>();
    }

    /**
     * Obtain the external classes.
     * @return the external classes.
     */
    Map<String, ThemisXAnalysisReflectExternal> getExternalClassMap() {
        return theExternalClasses;
    }

    /**
     * Build project classMap.
     * @param pProject the project
     */
    private void buildProjectClassMap(final ThemisXAnalysisSolverProject pProject) {
        /* Loop through all modules */
        for (ThemisXAnalysisSolverModule myModule : pProject.getModules()) {
            /* Loop through all packages */
            for (ThemisXAnalysisSolverPackage myPackage : myModule.getPackages()) {
                buildProjectClassMap(myPackage);
            }
        }
    }

    /**
     * Build project classMap.
     * @param pPackage the package
     */
    private void buildProjectClassMap(final ThemisXAnalysisSolverPackage pPackage) {
        /* Loop through all files */
        for (ThemisXAnalysisSolverFile myFile : pPackage.getFiles()) {
            /* Loop through all classes */
            for (ThemisXAnalysisSolverClass myClass : myFile.getClasses()) {
                final ThemisXAnalysisClassInstance myInstance = myClass.getUnderlyingClass();
                /* Ignore local and anonymous classes */
                if (!myInstance.isLocalDeclaration() && !myInstance.isAnonClass()) {
                    theProjectClasses.put(myClass.getFullName(), myClass);
                }
            }
        }
    }

    /**
     * Build external classMap.
     * @param pProject the project
     */
    private void buildExternalClassMap(final ThemisXAnalysisSolverProject pProject) {
        /* Initialise the map with the javaLang classes */
        for (ThemisXAnalysisReflectExternal myClass : theJavaLang.values()) {
            theExternalClasses.put(myClass.getFullName(), myClass);
        }

        /* Loop through all modules */
        for (ThemisXAnalysisSolverModule myModule : pProject.getModules()) {
            /* Loop through all packages */
            for (ThemisXAnalysisSolverPackage myPackage : myModule.getPackages()) {
                buildExternalClassMap(myPackage);
            }
        }
    }

    /**
     * Build external classMap.
     * @param pPackage the package to process.
     */
    private void buildExternalClassMap(final ThemisXAnalysisSolverPackage pPackage) {
        /* Loop through all files */
        for (ThemisXAnalysisSolverFile myFile : pPackage.getFiles()) {
            /* Process the imports */
            for (ThemisXAnalysisNodeInstance myInstance : myFile.getUnderlyingFile().getContents().getImports()) {
                /* Determine full name */
                final ThemisXAnalysisNodeImport myImport = (ThemisXAnalysisNodeImport) myInstance;
                final String myFullName = myImport.getFullName();

                /* If this is a previously unseen class */
                if (!theProjectClasses.containsKey(myFullName)) {
                    /* Ensure the external class map */
                    theExternalClasses.computeIfAbsent(myFullName, n -> new ThemisXAnalysisReflectExternal(myImport));
                }
            }
        }
    }

    /**
     * Look up import.
     * @param pImport the import definition.
     * @return the import class
     */
    private ThemisXAnalysisClassInstance lookUpImport(final ThemisXAnalysisNodeImport pImport) {
        /* Determine full name */
        final String myFullName = pImport.getFullName();

        /* Look for project class of this name */
        final ThemisXAnalysisSolverClass myClass = theProjectClasses.get(myFullName);
        return myClass != null
                ? myClass.getUnderlyingClass()
                : theExternalClasses.get(myFullName);
    }

    /**
     * Process package.
     * @param pPackage the package
     */
    void processPackage(final ThemisXAnalysisSolverPackage pPackage) {
        /* Loop through the files in the package */
        for (ThemisXAnalysisSolverFile myFile : pPackage.getFiles()) {
            /* Determine the possible references */
            determineKnownClasses(myFile);

            /* Detect references */
            detectClassOrInterfaceTypes(myFile);
            detectNameReferences(myFile);

            /* Store the references into the file */
            myFile.setReferenced(theReferenced);
        }

        /* Loop through the files in the package */
        for (ThemisXAnalysisSolverFile myFile : pPackage.getFiles()) {
            /* Process local references */
            myFile.processLocalReferences();
        }
    }

    /**
     * Determine known classes for file.
     * @param pFile the file to process.
     */
    private void determineKnownClasses(final ThemisXAnalysisSolverFile pFile) {
        /* Initialise the map with the javaLang classes */
        theKnownClasses.clear();
        theReferenced.clear();
        for (ThemisXAnalysisClassInstance myClass : theJavaLang.values()) {
            theKnownClasses.put(myClass.getName(), myClass);
        }

        /* Add all the package top-level classes */
        final ThemisXAnalysisSolverPackage myPackage = (ThemisXAnalysisSolverPackage) pFile.getOwningPackage();
        for (ThemisXAnalysisSolverFile myFile : myPackage.getFiles()) {
            final ThemisXAnalysisSolverClass myClass = myFile.getTopLevel();
            if (myClass != null) {
                theKnownClasses.put(myClass.getName(), myClass.getUnderlyingClass());
            }
        }

        /* Process the imports */
        for (ThemisXAnalysisNodeInstance myNode : pFile.getUnderlyingFile().getContents().getImports()) {
            /* LookUp the import and record it */
            final ThemisXAnalysisClassInstance myImport = lookUpImport((ThemisXAnalysisNodeImport) myNode);
            theKnownClasses.put(myImport.getName(), myImport);
        }

        /* Process the classes in the file */
        for (ThemisXAnalysisSolverClass myClass : pFile.getClasses()) {
            if (!myClass.getUnderlyingClass().isAnonClass()) {
                theKnownClasses.put(myClass.getName(), myClass.getUnderlyingClass());
            }
        }
    }

    /**
     * Detect Class or Interface References in a file.
     * @param pFile the file
     */
    private void detectClassOrInterfaceTypes(final ThemisXAnalysisSolverFile pFile) {
        /* Access class */
        final ThemisXAnalysisInstance myClass = (ThemisXAnalysisInstance) pFile.getTopLevel().getUnderlyingClass();

        /* Obtain all ClassOrInterface references */
        final List<ThemisXAnalysisInstance> myReferences = myClass.discoverNodes(ThemisXAnalysisType.CLASSINTERFACE);

        /* Loop through the references */
        for (ThemisXAnalysisInstance myNode : myReferences) {
            final ThemisXAnalysisTypeClassInterface myReference = (ThemisXAnalysisTypeClassInterface) myNode;
            final ThemisXAnalysisClassInstance myResolved = processPossibleReference(myReference.getName());
            if (myResolved != null) {
                myReference.setClassInstance(myResolved);
            } else {
                System.out.println(myReference.getName() + ":" + pFile);
            }
        }
    }

    /**
     * Detect Class or Interface References in a file.
     * @param pFile the file
     */
    private void detectNameReferences(final ThemisXAnalysisSolverFile pFile) {
        /* Access class */
        final ThemisXAnalysisInstance myClass = (ThemisXAnalysisInstance) pFile.getTopLevel().getUnderlyingClass();

        /* Obtain all Name expressions */
        final List<ThemisXAnalysisInstance> myReferences = myClass.discoverNodes(ThemisXAnalysisNode.NAME);

        /* Loop through the references */
        for (ThemisXAnalysisInstance myNode : myReferences) {
            final ThemisXAnalysisNodeName myReference = (ThemisXAnalysisNodeName) myNode;
            if (myReference.getQualifier() == null) {
                final ThemisXAnalysisClassInstance myResolved = processPossibleReference(myReference.getName());
                if (myResolved == null) {
                    look4Name(myReference);
                }
            }
        }
    }

    /**
     * Look4Name.
     * @param pName the name
     */
    private void look4Name(final ThemisXAnalysisNodeName pName) {
        final String myName = pName.getName();
        final Node myParent = pName.getNode().getParentNode().orElse(null);
        //if (myParent != null) {
//
        //}
    }

    /**
     * process possible reference.
     * @param pReference the possible reference.
     * @return the resolved class (if found)
     */
    private ThemisXAnalysisClassInstance processPossibleReference(final String pReference) {
        /* If the reference is interesting */
        final ThemisXAnalysisClassInstance myReference = theKnownClasses.get(pReference);
        if (myReference != null) {
            declareReferencedClass(myReference);
        }
        return myReference;
    }

    /**
     * Declare referenced class.
     * @param pClass the class
     */
    private void declareReferencedClass(final ThemisXAnalysisClassInstance pClass) {
        /* Lookup the project class and return if not in project */
        ThemisXAnalysisSolverClass myClass = theProjectClasses.get(pClass.getFullName());
        if (myClass == null) {
            return;
        }

        /* Convert to top-level class */
        if (!myClass.isTopLevel()) {
            myClass = ((ThemisXAnalysisSolverFile) myClass.getOwningFile()).getTopLevel();
        }

        /* If this is the first instance of the reference */
        if (myClass != null && !theReferenced.contains(myClass)) {
            /* Add to the list of referenced classes */
            theReferenced.add(myClass);
        }
    }
}
