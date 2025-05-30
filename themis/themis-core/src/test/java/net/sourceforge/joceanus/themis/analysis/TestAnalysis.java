/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.themis.analysis;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.ThemisXAnalysisProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Test Analysis.
 */
class TestAnalysis {
    /**
     * The project.
     */
    private static final String PROJECT = "jOceanus";

    /**
     * The path base.
     */
    private static final String PATH_BASE = "../../";

    /**
     * The path xtra.
     */
    private static final String PATH_XTRA = "/src/main/java";

    /**
     * The package base.
     */
    private static final String PACKAGE_BASE = "net.sourceforge.joceanus.";

    /**
     * Create the analysis test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    Stream<DynamicNode> analyseSource() throws OceanusException {
        return Stream.of(
                    DynamicTest.dynamicTest("analyseSource", TestAnalysis::testProjectSource),
                    DynamicTest.dynamicTest("analyseDependencies", TestAnalysis::testProjectDependencies)
                );
    }

    /**
     * Test source analysis of the current project.
     */
    private static void testProjectSource() {
        /* Analyse source of project */
        File myLocation = new File(PATH_BASE);
        try {
            myLocation = new File(myLocation.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final ThemisXAnalysisProject myProj = new ThemisXAnalysisProject(new File(PATH_BASE));
        Assertions.assertNull(myProj.getError(), "Exception analysing project");

        /* Parse the base project */
        //final ThemisStatsParser myParser = new ThemisStatsParser();
        //final ThemisStatsProject myProject = myParser.parseProject(myProj);
    }

    /**
     * Test dependency analysis of the current project.
     */
    private static void testProjectDependencies() {
        /* Analyse dependencies of project */
        //final ThemisDSMProject myProject  = new ThemisDSMProject(new File(PATH_BASE));
        //Assertions.assertNotNull(myProject, "Failed to analyse project");
        //Assertions.assertNull(myProject.getError(), "Exception analysing project");
        //Assertions.assertTrue(myProject.hasModules(), "Project has no modules");
        //Assertions.assertFalse(myProject.getDefaultModule().isCircular(), "Project has circular dependencies");

        /* Build report of module */
        //final String myDoc = ThemisDSMReport.reportOnModule(myProject.getDefaultModule());
        //Assertions.assertNotNull(myProject, "Failed to build report");
    }

    /**
     * Test a module.
     *
     * @param pModule  the module name.
     */
    private static void testModule(final String pModule) {
        /* Determine full module/package names */
        final String myModule = PATH_BASE + pModule;

        /* Protect against exceptions */
        try {
            final ThemisAnalysisModule myMod = new ThemisAnalysisModule(new File(myModule));
        } catch (OceanusException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test a package.
     *
     * @param pModule  the module name.
     * @param pPackage the package name
     */
    private static void testPackage(final String pModule,
                                    final String pPackage) {
        /* Determine the module root */
        final int myIndex = pModule.indexOf('-');
        final String myRoot = pModule.substring(0, myIndex);

        /* Determine full module/package names */
        final String myModule = PATH_BASE + myRoot + ThemisAnalysisChar.COMMENT + pModule + PATH_XTRA;
        final String myPackage = PACKAGE_BASE + myRoot
                + (pPackage == null
                   ? ""
                   : ThemisAnalysisChar.PERIOD + pPackage);

        /* Protect against exceptions */
        try {
            final ThemisAnalysisPackage myPack = new ThemisAnalysisPackage(new File(myModule), myPackage);
        } catch (OceanusException e) {
            e.printStackTrace();
        }
    }
}
