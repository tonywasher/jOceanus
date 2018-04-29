/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.test.crypto.swing;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.manager.swing.GordianSwingHashManager;
import net.sourceforge.joceanus.jgordianknot.test.crypto.GordianListAlgorithms;
import net.sourceforge.joceanus.jgordianknot.test.crypto.GordianTestSuite;
import net.sourceforge.joceanus.jgordianknot.test.crypto.GordianTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Test suite.
 */
public class GordianSwingTester
        implements SecurityManagerCreator {
    /**
     * Create a logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(GordianSwingTester.class);

    /**
     * The Test suite.
     */
    private final GordianTestSuite theTests;

    /**
     * Constructor.
     */
    public GordianSwingTester() {
        theTests = new GordianTestSuite(this);
    }

    @Override
    public GordianHashManager newSecureManager() throws OceanusException {
        return new GordianSwingHashManager();
    }

    @Override
    public GordianHashManager newSecureManager(final GordianParameters pParams) throws OceanusException {
        return new GordianSwingHashManager(pParams);
    }

    /**
     * Constructor.
     * @param args the parameters
     * @throws OceanusException on error
     */
    public void runTests(final String[] args) throws OceanusException {
        if (args.length > 0) {
            /* handle check algorithms */
            if ("check".equals(args[0])) {
                theTests.checkAlgorithms();

                /* handle test security */
            } else if ("test".equals(args[0])) {
                theTests.testSecurity();

                /* handle zip file creation */
            } else if ("zip".equals(args[0])) {
                theTests.testZipFile();
            }
        } else {
            GordianListAlgorithms.listAlgorithms();
        }
    }

    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI(args);
            }
        });
    }

    /**
     * Create and show the GUI.
     * @param args the parameters
     */
    public static void createAndShowGUI(final String[] args) {
        try {
            GordianSwingTester myTests = new GordianSwingTester();
            myTests.runTests(args);
        } catch (Exception e) {
            LOGGER.fatal("Help", e);
        }
        System.exit(0);
    }
}
