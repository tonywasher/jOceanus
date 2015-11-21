/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot;

import java.io.File;

import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jgordianknot.SecurityTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.manager.SecureManager;
import net.sourceforge.joceanus.jgordianknot.manager.swing.SwingSecureManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Security Test suite.
 */
public class SwingSecurityTester
        implements SecurityManagerCreator {
    /**
     * The Test suite.
     */
    private final SecurityTestSuite theTests;

    /**
     * Constructor.
     */
    public SwingSecurityTester() {
        theTests = new SecurityTestSuite(this);
    }

    @Override
    public SecureManager newSecureManager() throws JOceanusException {
        return new SwingSecureManager();
    }

    @Override
    public SecureManager newSecureManager(final GordianParameters pParams) throws JOceanusException {
        return new SwingSecureManager(pParams);
    }

    /**
     * Constructor.
     * @param args the parameters
     * @throws JOceanusException on error
     */
    public void runTests(final String[] args) throws JOceanusException {
        if (args.length > 0) {
            /* handle check algorithms */
            if ("check".equals(args[0])) {
                theTests.checkAlgorithms();

                /* handle test security */
            } else if ("test".equals(args[0])) {
                theTests.testSecurity();

                /* handle zip file creation */
            } else if ("zip".equals(args[0])) {
                File myZipFile = new File("c:\\Users\\Tony\\TestStdZip.zip");
                theTests.createZipFile(myZipFile, new File("c:\\Users\\Tony\\tester"), true);
                theTests.extractZipFile(myZipFile, new File("c:\\Users\\Tony\\testcomp"));
            }
        } else {
            SecurityTestSuite.listAlgorithms();
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
            SwingSecurityTester myTests = new SwingSecurityTester();
            myTests.runTests(args);
        } catch (Exception e) {
            System.out.println("Help");
            e.printStackTrace();
        }
        System.exit(0);
    }
}
