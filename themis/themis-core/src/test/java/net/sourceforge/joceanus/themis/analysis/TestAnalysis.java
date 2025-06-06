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
import net.sourceforge.joceanus.themis.xanalysis.dsm.ThemisXAnalysisDSMProject;
import net.sourceforge.joceanus.themis.xanalysis.proj.ThemisXAnalysisProject;
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
    private static ThemisXAnalysisProject PARSED_PROJECT;

    /**
     * Create the analysis test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    Stream<DynamicNode> analyseSource() throws OceanusException {
        return Stream.of(
                    DynamicTest.dynamicTest("analyseSource", TestAnalysis::testProjectSource),
                    DynamicTest.dynamicTest("analyseDependencies", TestAnalysis::testProjectDependencies),
                    DynamicTest.dynamicTest("analyseStats", TestAnalysis::testProjectStats)
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
        PARSED_PROJECT = new ThemisXAnalysisProject(new File(PATH_BASE));
        Assertions.assertNull(PARSED_PROJECT.getError(), "Exception analysing project");
    }

    /**
     * Test dependency analysis of the current project.
     */
    private static void testProjectDependencies() {
        /* Make sure previous test executed */
        Assumptions.assumeTrue(PARSED_PROJECT != null);
        Assumptions.assumeTrue(PARSED_PROJECT.getError() == null);

        /* Analyse dependencies of project */
        final ThemisXAnalysisDSMProject myProject  = new ThemisXAnalysisDSMProject(PARSED_PROJECT);
        Assertions.assertNotNull(myProject, "Failed to analyse project");
        Assertions.assertNull(myProject.getError(), "Exception analysing project");
    }

    /**
     * Test dependency analysis of the current project.
     */
    private static void testProjectStats() {
        /* Make sure previous test executed */
        Assumptions.assumeTrue(PARSED_PROJECT != null);
        Assumptions.assumeTrue(PARSED_PROJECT.getError() == null);

        /* Analyse dependencies of project */
        final ThemisXAnalysisStatsProject myProject  = new ThemisXAnalysisStatsProject(PARSED_PROJECT);
        Assertions.assertNotNull(myProject, "Failed to analyse project");
        Assertions.assertNull(myProject.getError(), "Exception analysing project");
    }
}
