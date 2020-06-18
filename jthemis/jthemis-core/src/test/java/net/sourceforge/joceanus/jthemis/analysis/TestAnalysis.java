/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jthemis.analysis;

import java.io.File;
import java.nio.CharBuffer;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Test Analysis.
 */
public class TestAnalysis {
    /**
     * The path base.
     */
    private static final String PATH_BASE = System.getProperty("user.home") + "/gitNew/jOceanus/";

    /**
     * The path xtra.
     */
    private static final String PATH_XTRA = "/src/main/java";

    /**
     * The package base.
     */
    private static final String PACKAGE_BASE = "net.sourceforge.joceanus.";

    /**
     * Main.
     *
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        //testPackage("jtethys-test", "test.ui.swing");
        testProject();
    }

    /**
     * Test a project.
     */
    private static void testProject() {
        /* Protect against exceptions */
        try {
            final ThemisAnalysisProject myProj = new ThemisAnalysisProject(new File(PATH_BASE));
            int i = 0;
        } catch (OceanusException e) {
            e.printStackTrace();
        }
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
            int i = 0;
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
            int i = 0;
        } catch (OceanusException e) {
            e.printStackTrace();
        }
    }
}
