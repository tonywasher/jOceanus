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
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprFieldAccess;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprMethodCall;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprMethodRef;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprObjectCreate;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisTypeClassInterface;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverClass;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverFile;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverProject;

public class ThemisXAnalysisMapper {
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
     * Process package.
     *
     * @param pPackage the package
     */
    public void processPackage(final ThemisXAnalysisSolverPackage pPackage) {
        /* Loop through the files in the package */
        for (ThemisXAnalysisSolverFile myFile : pPackage.getFiles()) {
            /* Reset the various states */
            theFile.initForFile(myFile);
            theType.reset();
            theName.reset();

            /* Process the file */
            processFile(myFile);
        }
    }

    /**
     * Process file.
     *
     * @param pFile the file
     */
    private void processFile(final ThemisXAnalysisSolverFile pFile) {
        /* Obtain the top-level class element */
        final ThemisXAnalysisSolverClass myBase = pFile.getTopLevel();
        final ThemisXAnalysisInstance myInstance = (ThemisXAnalysisInstance) myBase.getUnderlyingClass();
        processInstance(myInstance);
    }

    /**
     * Process instance.
     *
     * @param pInstance the instance
     */
    private void processInstance(final ThemisXAnalysisInstance pInstance) {
        /* Process stacks */
        final boolean bumpType = theType.processElement(pInstance);
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
}
