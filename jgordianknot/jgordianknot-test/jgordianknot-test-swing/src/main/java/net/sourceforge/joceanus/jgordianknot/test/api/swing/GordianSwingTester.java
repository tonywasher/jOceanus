/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.test.api.swing;

import java.util.Arrays;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianSecurityManager;
import net.sourceforge.joceanus.jgordianknot.api.swing.GordianSwingSecurityManager;
import net.sourceforge.joceanus.jgordianknot.test.api.GordianTestSuite;
import net.sourceforge.joceanus.jgordianknot.test.api.GordianTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Security Test suite.
 */
public class GordianSwingTester
        implements SecurityManagerCreator {
    /**
     * Create a logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(GordianSwingTester.class);

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
    public GordianSecurityManager newSecureManager() throws OceanusException {
        return new GordianSwingSecurityManager();
    }

    @Override
    public GordianSecurityManager newSecureManager(final GordianParameters pParams) throws OceanusException {
        return new GordianSwingSecurityManager(pParams);
    }

    /**
     * Constructor.
     * @param args the parameters
     * @throws OceanusException on error
     */
    public void runTests(final String[] args) throws OceanusException {
        theTests.runTests(Arrays.asList(args));
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
            final GordianSwingTester myTests = new GordianSwingTester();
            myTests.runTests(args);
        } catch (Exception e) {
            LOGGER.fatal("Help", e);
        }
        System.exit(0);
    }
}
