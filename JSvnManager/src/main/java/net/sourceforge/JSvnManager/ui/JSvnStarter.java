/*******************************************************************************
 * Subversion: Java SubVersion Management
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JSvnManager.ui;

import javax.swing.SwingUtilities;

/**
 * Top level JSvnManager starter.
 * @author Tony Washer
 */
public final class JSvnStarter {
    /**
     * Logger.
     */
    private static JSvnManager theManager;

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
        theManager = new JSvnManager();
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
