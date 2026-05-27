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
package io.github.tonywasher.joceanus.themis.analysis;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadManager;
import io.github.tonywasher.joceanus.tethys.helper.TethysUIHelperFactory;
import io.github.tonywasher.joceanus.themis.parser.ThemisParser;
import io.github.tonywasher.joceanus.themis.solver.ThemisSolver;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverProject;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsProject;
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
     * The project parser.
     */
    private ThemisParser theProjectParser;

    /**
     * The threadManager.
     */
    private TethysUIThreadManager theThreadMgr;

    /**
     * Create the analysis test suite.
     *
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
     *
     * @throws OceanusException on error
     */
    private void testProjectSource() throws OceanusException {
        /* Analyse source of project */
        File myLocation = new File(PATH_BASE);
        final TethysUIHelperFactory myFactory = new TethysUIHelperFactory();
        theThreadMgr = myFactory.threadFactory().newThreadManager();
        try {
            myLocation = new File(myLocation.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        theProjectParser = new ThemisParser(theThreadMgr, myLocation);
        Assertions.assertNotNull(theProjectParser, "Exception analysing project");
    }

    /**
     * Test dependency analysis of the current project.
     *
     * @throws OceanusException on error
     */
    private void testProjectDependencies() throws OceanusException {
        /* Make sure previous test executed */
        Assumptions.assumeTrue(theProjectParser != null);

        /* Analyse dependencies of project */
        final ThemisSolver mySolver = new ThemisSolver(theProjectParser);
        final ThemisSolverProject myProject = mySolver.getProject();
        Assertions.assertNotNull(myProject, "Exception analysing project");
    }

    /**
     * Test dependency analysis of the current project.
     *
     * @throws OceanusException on error
     */
    private void testProjectStats() throws OceanusException {
        /* Make sure previous test executed */
        Assumptions.assumeTrue(theProjectParser != null);

        /* Analyse dependencies of project */
        final ThemisStatsProject myProject = new ThemisStatsProject(theProjectParser);
        Assertions.assertNotNull(myProject, "Exception analysing project");
    }
}
