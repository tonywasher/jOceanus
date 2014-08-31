/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.svn.ui;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top level JSvnManager starter.
 * @author Tony Washer
 */
public final class JSvnStarter {
    /**
     * SvnManager.
     */
    private static JSvnManager theManager;

    /**
     * Logger.
     */
    private static Logger theLogger = LoggerFactory.getLogger(JSvnStarter.class.getName());

    /**
     * Obtain the manager.
     * @return the manager
     */
    public static JSvnManager getManager() {
        return theManager;
    }

    /**
     * Private constructor.
     */
    private JSvnStarter() {
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        /* Create the SvnManager program */
        theManager = new JSvnManager(theLogger);
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
