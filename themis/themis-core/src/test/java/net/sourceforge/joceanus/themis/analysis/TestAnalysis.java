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
import net.sourceforge.joceanus.themis.xanalysis.parser.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisProject;
import net.sourceforge.joceanus.themis.xanalysis.solver.ThemisXAnalysisSolver;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverProject;
import net.sourceforge.joceanus.themis.xanalysis.stats.ThemisXAnalysisStatsProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
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
     * The path base.
     */
    private static final String PATH_BASE = "../../";

    /**
     * The parsed project.
     */
    private ThemisXAnalysisProject theParsedProject;

    /**
     * Create the analysis test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    Stream<DynamicNode> analyseSource() throws OceanusException {
        return Stream.of(
                    DynamicTest.dynamicTest("analyseSource", this::testProjectSource),
                    DynamicTest.dynamicTest("analyseDependencies", this::testProjectDependencies),
                    DynamicTest.dynamicTest("analyseStats", this::testProjectStats)
                );
    }

    /**
     * Test source analysis of the current project.
     */
    private void testProjectSource() {
        /* Analyse source of project */
        File myLocation = new File(PATH_BASE);
        try {
            myLocation = new File(myLocation.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final ThemisXAnalysisParser myParser = new ThemisXAnalysisParser(myLocation);
        Assertions.assertNull(myParser.getError(), "Exception analysing project");
        theParsedProject = myParser.getProject();
    }

    /**
     * Test dependency analysis of the current project.
     */
    private void testProjectDependencies() {
        /* Make sure previous test executed */
        Assumptions.assumeTrue(theParsedProject != null);

        /* Analyse dependencies of project */
        final ThemisXAnalysisSolverProject myProject  = new ThemisXAnalysisSolverProject(theParsedProject);
        final ThemisXAnalysisSolver mySolver  = new ThemisXAnalysisSolver(myProject);
        Assertions.assertNotNull(mySolver, "Failed to analyse project");
        //Assertions.assertNull(myProject.getError(), "Exception analysing project");
    }

    /**
     * Test dependency analysis of the current project.
     */
    private void testProjectStats() {
        /* Make sure previous test executed */
        Assumptions.assumeTrue(theParsedProject != null);

        /* Analyse dependencies of project */
        final ThemisXAnalysisStatsProject myProject  = new ThemisXAnalysisStatsProject(theParsedProject);
        Assertions.assertNotNull(myProject, "Failed to analyse project");
        Assertions.assertNull(myProject.getError(), "Exception analysing project");
    }
}
