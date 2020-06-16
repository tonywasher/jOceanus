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
    private static final String PATH_BASE = "c:/Users/Tony/gitNew/jOceanus/";

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
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        testCoeus();
        testGordianKnot();
        testMetis();
        testMoneyWise();
        testPrometheus();
        testTethys();
        testThemis();
    }

    /**
     * Coeus.
     */
    private static void testCoeus() {
        testCoeusCore();
        testCoeusSwing();
        testCoeusJavaFX();
    }

    /**
     * CoeusCore.
     */
    private static void testCoeusCore() {
        testPackage("jcoeus-core", "data");
        testPackage("jcoeus-core", "data.fundingcircle");
        testPackage("jcoeus-core", "data.lendingworks");
        testPackage("jcoeus-core", "data.ratesetter");
        testPackage("jcoeus-core", "data.zopa");
        testPackage("jcoeus-core", "ui");
        testPackage("jcoeus-core", "ui.panels");
        testPackage("jcoeus-core", "ui.report");
        testPackage("jcoeus-core", null);
    }

    /**
     * CoeusSwing.
     */
    private static void testCoeusSwing() {
        testPackage("jcoeus-swing", "ui.swing");
    }

    /**
     * CoeusJavaFX.
     */
    private static void testCoeusJavaFX() {
        testPackage("jcoeus-javafx", "ui.javafx");
    }

    /**
     * GordianKnot.
     */
    private static void testGordianKnot() {
        testGordianKnotCore();
        testGordianKnotSwing();
        testGordianKnotJavaFX();
    }

    /**
     * GordianKnotCore.
     */
    private static void testGordianKnotCore() {
        testPackage("jgordianknot-core", "api.agree");
        testPackage("jgordianknot-core", "api.asym");
        testPackage("jgordianknot-core", "api.base");
        testPackage("jgordianknot-core", "api.cipher");
        testPackage("jgordianknot-core", "api.digest");
        testPackage("jgordianknot-core", "api.encrypt");
        testPackage("jgordianknot-core", "api.factory");
        testPackage("jgordianknot-core", "api.key");
        testPackage("jgordianknot-core", "api.keyset");
        testPackage("jgordianknot-core", "api.keystore");
        testPackage("jgordianknot-core", "api.mac");
        testPackage("jgordianknot-core", "api.password");
        testPackage("jgordianknot-core", "api.random");
        testPackage("jgordianknot-core", "api.sign");
        testPackage("jgordianknot-core", "api.zip");
        testPackage("jgordianknot-core", "impl.bc");
        testPackage("jgordianknot-core", "impl.core.agree");
        testPackage("jgordianknot-core", "impl.core.base");
        testPackage("jgordianknot-core", "impl.core.cipher");
        testPackage("jgordianknot-core", "impl.core.digest");
        testPackage("jgordianknot-core", "impl.core.encrypt");
        testPackage("jgordianknot-core", "impl.core.key");
        testPackage("jgordianknot-core", "impl.core.keypair");
        testPackage("jgordianknot-core", "impl.core.keyset");
        //testPackage("jgordianknot-core", "impl.core.keystore");
        testPackage("jgordianknot-core", "impl.core.mac");
        testPackage("jgordianknot-core", "impl.core.password");
        testPackage("jgordianknot-core", "impl.core.random");
        testPackage("jgordianknot-core", "impl.core.sign");
        testPackage("jgordianknot-core", "impl.core.stream");
        testPackage("jgordianknot-core", "impl.core.zip");
        testPackage("jgordianknot-core", "impl.jca");
        testPackage("jgordianknot-core", "util");
    }

    /**
     * GoprdianKnotSwing.
     */
    private static void testGordianKnotSwing() {
        testPackage("jgordianknot-swing", "api.swing");
    }

    /**
     * GordianKnotJavaFX.
     */
    private static void testGordianKnotJavaFX() {
        testPackage("jgordianknot-javafx", "api.javafx");
    }

    /**
     * Metis.
     */
    private static void testMetis() {
        testMetisCore();
        testMetisSwing();
        testMetisJavaFX();
    }

    /**
     * MetisCore.
     */
    private static void testMetisCore() {
        testPackage("jmetis-core", "atlas.ui");
        testPackage("jmetis-core", "data");
        testPackage("jmetis-core", "field");
        testPackage("jmetis-core", "http");
        testPackage("jmetis-core", "lethe.data");
        testPackage("jmetis-core", "lethe.field");
        testPackage("jmetis-core", "list");
        testPackage("jmetis-core", "preference");
        testPackage("jmetis-core", "profile");
        testPackage("jmetis-core", "report");
        testPackage("jmetis-core", "threads");
        testPackage("jmetis-core", "ui");
        testPackage("jmetis-core", "viewer");
        testPackage("jmetis-core", null);
    }

    /**
     * MetisSwing.
     */
    private static void testMetisSwing() {
        testPackage("jmetis-swing", "atlas.ui.swing");
        testPackage("jmetis-swing", "lethe.field.swing");
        testPackage("jmetis-swing", "threads.swing");
        testPackage("jmetis-swing", "viewer.swing");
    }

    /**
     * MetisJavaFX.
     */
    private static void testMetisJavaFX() {
        testPackage("jmetis-javafx", "atlas.ui.javafx");
        testPackage("jmetis-javafx", "threads.javafx");
        testPackage("jmetis-javafx", "viewer.javafx");
    }

    /**
     * Moneywise.
     */
    private static void testMoneyWise() {
        testMoneyWiseCore();
        testMoneyWiseSwing();
    }

    /**
     * MoneyWiseCore.
     */
    private static void testMoneyWiseCore() {
        testPackage("jmoneywise-core", "help");
        testPackage("jmoneywise-core", "lethe.analysis");
        testPackage("jmoneywise-core", "lethe.data");
        testPackage("jmoneywise-core", "lethe.data.statics");
        testPackage("jmoneywise-core", "lethe.database");
        testPackage("jmoneywise-core", "lethe.quicken.definitions");
        testPackage("jmoneywise-core", "lethe.quicken.file");
        testPackage("jmoneywise-core", "lethe.reports");
        testPackage("jmoneywise-core", "lethe.sheets");
        testPackage("jmoneywise-core", "lethe.tax");
        testPackage("jmoneywise-core", "lethe.tax.uk");
        testPackage("jmoneywise-core", "lethe.threads");
        testPackage("jmoneywise-core", "lethe.ui");
        testPackage("jmoneywise-core", "lethe.ui.controls");
        testPackage("jmoneywise-core", "lethe.views");
        testPackage("jmoneywise-core", null);
    }

    /**
     * MoneyWiseSwing.
     */
    private static void testMoneyWiseSwing() {
        testPackage("jmoneywise-swing", "lethe.swing");
        testPackage("jmoneywise-swing", "lethe.ui.dialog.swing");
        testPackage("jmoneywise-swing", "lethe.ui.swing");
    }

    /**
     * Prometheus.
     */
    private static void testPrometheus() {
        testPrometheusCore();
        testPrometheusSwing();
        testPrometheusJavaFX();
        testPrometheusServices();
    }

    /**
     * PrometheusCore.
     */
    private static void testPrometheusCore() {
        testPackage("jprometheus-core", "atlas.database");
        testPackage("jprometheus-core", "atlas.field");
        testPackage("jprometheus-core", "atlas.preference");
        testPackage("jprometheus-core", "lethe");
        //testPackage("jprometheus-core", "lethe.data");
        testPackage("jprometheus-core", "lethe.database");
        testPackage("jprometheus-core", "lethe.sheets");
        testPackage("jprometheus-core", "lethe.threads");
        testPackage("jprometheus-core", "lethe.ui");
        testPackage("jprometheus-core", "lethe.views");
        testPackage("jprometheus-core", null);
    }

    /**
     * PrometheusSwing.
     */
    private static void testPrometheusSwing() {
        //testPackage("jprometheus-swing", "lethe.swing");
        testPackage("jprometheus-swing", "lethe.ui.swing");
    }

    /**
     * PrometheusJavaFX.
     */
    private static void testPrometheusJavaFX() {
        testPackage("jprometheus-javafx", "javafx");
    }

    /**
     * PrometheusServices.
     */
    private static void testPrometheusServices() {
        //testPackage("jprometheus-services/jprometheus-sheet-api", "service.sheet");
        testPackage("jprometheus-services/jprometheus-sheet-hssf", "service.sheet.hssf");
        testPackage("jprometheus-services/jprometheus-sheet-odf", "service.sheet.odf");
        testPackage("jprometheus-services/jprometheus-sheet-xssf", "service.sheet.xssf");
    }

    /**
     * Tethys.
     */
    private static void testTethys() {
        testTethysCore();
        testTethysSwing();
        testTethysJavaFX();
    }

    /**
     * TethysCore.
     */
    private static void testTethysCore() {
        testPackage("jtethys-core", "date");
        testPackage("jtethys-core", "decimal");
        testPackage("jtethys-core", "event");
        testPackage("jtethys-core", "help");
        testPackage("jtethys-core", "logger");
        testPackage("jtethys-core", "resource");
        testPackage("jtethys-core", "ui");
        testPackage("jtethys-core", null);
    }

    /**
     * TethysSwing.
     */
    private static void testTethysSwing() {
        testPackage("jtethys-swing", "help.swing");
        testPackage("jtethys-swing", "ui.swing");
    }

    /**
     * TethysJavaFX.
     */
    private static void testTethysJavaFX() {
        testPackage("jtethys-javafx", "help.javafx");
        testPackage("jtethys-javafx", "ui.javafx");
    }

    /**
     * Themis.
     */
    private static void testThemis() {
        testThemisCore();
        testThemisSwing();
        testThemisJavaFX();
    }

    /**
     * ThemisCore.
     */
    private static void testThemisCore() {
        testPackage("jthemis-core", "analysis");
        testPackage("jthemis-core", "dsm");
        testPackage("jthemis-core", "scm.data");
        testPackage("jthemis-core", "scm.maven");
        testPackage("jthemis-core", "scm.tasks");
        testPackage("jthemis-core", "git.data");
        testPackage("jthemis-core", "sf.data");
        testPackage("jthemis-core", "threads");
        testPackage("jthemis-core", "ui");
        testPackage("jthemis-core", "ui.dsm");
        testPackage("jthemis-core", null);
    }

    /**
     * ThemisSwing.
     */
    private static void testThemisSwing() {
        testPackage("jthemis-swing", "ui.swing");
    }

    /**
     * ThemisJavaFX.
     */
    private static void testThemisJavaFX() {
        testPackage("jthemis-javafx", "ui.javafx");
    }

    /**
     * Test a package.
     * @param pModule the module name.
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
