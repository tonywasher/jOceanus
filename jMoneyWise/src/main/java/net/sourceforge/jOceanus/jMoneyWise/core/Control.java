/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jMoneyWise.ui.MainTab;

/**
 * Main entry point for program.
 * @author Tony Washer
 */
public final class Control {
    /**
     * The Main window.
     */
    private static MainTab theWindow = null;

    /**
     * Logger.
     */
    private static Logger theLogger = Logger.getLogger(Control.class.getName());

    /**
     * Private constructor to avoid instantiation.
     */
    private Control() {
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            theWindow = new MainTab();
            theWindow.makeFrame();

        } catch (JDataException e) {
            theLogger.log(Level.SEVERE, "createGUI didn't complete successfully", e);
        }
    }

    /**
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
