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

package io.github.tonywasher.joceanus.themis.xanalysis.solver.mapper;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprFieldAccess;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprMethodCall;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprMethodRef;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprObjectCreate;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeImport;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisTypeClassInterface;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverClass;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverFile;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverProject;

public class ThemisXAnalysisMapper
        implements AutoCloseable {
    /**
     * Project State.
     */
    private final ThemisXAnalysisMapperProjectState theProject;

    /**
     * Project State.
     */
    private final ThemisXAnalysisMapperFileState theFile;

    /**
     * Project State.
     */
    private final ThemisXAnalysisMapperTypeState theType;

    /**
     * Project State.
     */
    private final ThemisXAnalysisMapperNameState theName;

    /**
     * Constructor.
     *
     * @param pProject the project.
     * @throws OceanusException on error
     */
    public ThemisXAnalysisMapper(final ThemisXAnalysisSolverProject pProject) throws OceanusException {
        /* Create the project state */
        theProject = new ThemisXAnalysisMapperProjectState(pProject);

        /* Create the file state */
        theFile = new ThemisXAnalysisMapperFileState(theProject);
        theType = new ThemisXAnalysisMapperTypeState();
        theName = new ThemisXAnalysisMapperNameState();
    }

    /**
     * PreProcess package.
     *
     * @param pPackage the package
     */
    public void preProcessPackage(final ThemisXAnalysisSolverPackage pPackage) {
        /* Loop through the files in the package */
        for (ThemisXAnalysisSolverFile myFile : pPackage.getFiles()) {
            /* preProcess the file if it has not been done yet */
            if (myFile.needsPreProcess()) {
                preProcessFile(myFile);
            }
        }
    }

    /**
     * Process package.
     *
     * @param pPackage the package
     */
    public void processPackage(final ThemisXAnalysisSolverPackage pPackage) {
        /* Loop through the files in the package */
        for (ThemisXAnalysisSolverFile myFile : pPackage.getFiles()) {
            /* Process the file */
            processFile(myFile);
        }
    }

    /**
     * Reset fileState.
     *
     * @param pFile the file
     */
    private void resetFileState(final ThemisXAnalysisSolverFile pFile) {
        /* Reset the various states */
        theFile.initForFile(pFile);
        theType.reset();
        theName.reset();
    }

    /**
     * preProcess file.
     *
     * @param pFile the file
     */
    private void preProcessFile(final ThemisXAnalysisSolverFile pFile) {
        /* Note that we have pre-processed this file */
        pFile.markPreProcessed();

        /* preProcess the imports */
        preProcessImports(pFile);

        /* Reset the fileState */
        resetFileState(pFile);

        /* Loop through the classes in the file */
        for (ThemisXAnalysisSolverClass myClass : pFile.getClasses()) {
            final ThemisXAnalysisClassInstance myInstance = myClass.getUnderlyingClass();

            /* Ignore anonymous and local classes */
            if (myInstance.isAnonClass() || myInstance.isLocalDeclaration()) {
                continue;
            }

            /* Loop through the extends */
            for (ThemisXAnalysisTypeInstance myExtends : myInstance.getExtends()) {
                processAncestor(myClass, myExtends);
            }

            /* Loop through the implements */
            for (ThemisXAnalysisTypeInstance myImplements : myInstance.getImplements()) {
                processAncestor(myClass, myImplements);
            }

            /* Process inherited children */
            theFile.processInherited(myClass);
        }
    }


    /**
     * preProcess file.
     *
     * @param pFile the file
     */
    private void preProcessImports(final ThemisXAnalysisSolverFile pFile) {
        /* Loop through all the imports */
        for (ThemisXAnalysisNodeInstance myNode : pFile.getUnderlyingFile().getContents().getImports()) {
            /* If the import is of a project file */
            final ThemisXAnalysisNodeImport myImport = (ThemisXAnalysisNodeImport) myNode;
            final ThemisXAnalysisSolverClass myClass = theProject.getProjectClassMap().get(myImport.getFullName());
            if (myClass != null) {
                /* Make sure that the file has been pre-processed */
                final ThemisXAnalysisSolverFile myFile = (ThemisXAnalysisSolverFile) myClass.getOwningFile();
                if (myFile.needsPreProcess()) {
                    /* PreProcess the file */
                    preProcessFile(myFile);
                }
            }
        }
    }

    /**
     * process ancestor.
     *
     * @param pClass    the class
     * @param pAncestor the ancestor
     */
    private void processAncestor(final ThemisXAnalysisSolverClass pClass,
                                 final ThemisXAnalysisTypeInstance pAncestor) {
        /* Handle ClassInterface Reference */
        if (pAncestor instanceof ThemisXAnalysisTypeClassInterface myRef) {
            final ThemisXAnalysisClassInstance myResolved = theFile.processPossibleReference(myRef.getFullName());
            if (myResolved != null) {
                pClass.addAncestor(myResolved.getFullName());
            }
        }
    }

    /**
     * Process file.
     *
     * @param pFile the file
     */
    private void processFile(final ThemisXAnalysisSolverFile pFile) {
        /* Reset the fileState */
        resetFileState(pFile);

        /* Obtain the top-level class element */
        final ThemisXAnalysisSolverClass myBase = pFile.getTopLevel();
        final ThemisXAnalysisInstance myInstance = (ThemisXAnalysisInstance) myBase.getUnderlyingClass();
        processInstance(myInstance);

        /* Propagate the referenced classes */
        pFile.setReferenced(theFile.getReferenced());
    }

    /**
     * Process instance.
     *
     * @param pInstance the instance
     */
    private void processInstance(final ThemisXAnalysisInstance pInstance) {
        /* Process stacks */
        final boolean bumpType = theType.processInstance(pInstance);
        final boolean bumpName = theName.processInstance(pInstance);

        /* Process element */
        final boolean doChildren = processElement(pInstance);

        /* Process children */
        if (doChildren) {
            for (ThemisXAnalysisInstance myChild : pInstance.getChildren()) {
                processInstance(myChild);
            }
        }

        /* CleanUp stacks */
        if (bumpName) {
            theName.cleanUpAfterInstance();
        }
        if (bumpType) {
            theType.cleanUpAfterInstance();
        }
    }

    /**
     * Process element.
     *
     * @param pElement the element
     * @return process children? true/false
     */
    private boolean processElement(final ThemisXAnalysisInstance pElement) {
        /* Handle ClassInstance */
        if (pElement instanceof ThemisXAnalysisClassInstance myInstance
                && !myInstance.isAnonClass()
                && !myInstance.isLocalDeclaration()) {
            final ThemisXAnalysisSolverClass myClass = theProject.getProjectClassMap().get(myInstance.getFullName());
            theFile.processInherited(myClass);
        }

        /* Handle ClassInterface Reference */
        if (pElement instanceof ThemisXAnalysisTypeClassInterface myRef) {
            processClassReference(myRef);
            return false;
        }

        /* Handle FieldAccess */
        if (pElement instanceof ThemisXAnalysisExprFieldAccess myAccess) {
            processFieldAccess(myAccess);
        }

        /* Handle MethodCall */
        if (pElement instanceof ThemisXAnalysisExprMethodCall myCall) {
            processMethodCall(myCall);
        }

        /* Handle MethodReference */
        if (pElement instanceof ThemisXAnalysisExprMethodRef myRef) {
            processMethodReference(myRef);
        }

        /* Handle ObjectCreation */
        if (pElement instanceof ThemisXAnalysisExprObjectCreate myCreate) {
            processObjectCreate(myCreate);
        }
        return true;
    }

    /**
     * Process class reference.
     *
     * @param pReference the reference
     */
    private void processClassReference(final ThemisXAnalysisTypeClassInterface pReference) {
        /* Process as a possible reference */
        final ThemisXAnalysisClassInstance myResolved = theFile.processPossibleReference(pReference.getFullName());

        /* If we failed to resolve */
        if (myResolved == null) {
            /* Check for type parameters and variable names */
            final ThemisXAnalysisInstance myType = theType.lookUpType(pReference.getName());
            final ThemisXAnalysisInstance myName = theName.lookUpName(pReference.getName());

            /* Report failure */
            final boolean bFound = (myType != null || myName != null);
            if (!bFound) {
                System.out.println(pReference.getFullName());
            }
        }
    }

    /**
     * Process field Access.
     *
     * @param pAccess the fieldAccess
     */
    private void processFieldAccess(final ThemisXAnalysisExprFieldAccess pAccess) {
    }

    /**
     * Process method call.
     *
     * @param pCall the methodCall
     */
    private void processMethodCall(final ThemisXAnalysisExprMethodCall pCall) {
    }

    /**
     * Process method Reference.
     *
     * @param pReference the reference
     */
    private void processMethodReference(final ThemisXAnalysisExprMethodRef pReference) {
    }

    /**
     * Process object Creation.
     *
     * @param pCreate the creation
     */
    private void processObjectCreate(final ThemisXAnalysisExprObjectCreate pCreate) {
    }

    @Override
    public void close() {
        theProject.close();
    }
}
