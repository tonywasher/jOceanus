/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.util.Properties;

import net.sourceforge.joceanus.jtethys.JOceanusException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger theLogger = LoggerFactory.getLogger(Control.class.getName());

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
            /* Configure log4j */
            Properties myLogProp = new Properties();
            myLogProp.setProperty("log4j.rootLogger", "ERROR, A1");
            myLogProp.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
            myLogProp.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
            myLogProp.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
            PropertyConfigurator.configure(myLogProp);

            theWindow = new MainTab(theLogger);
            theWindow.makeFrame();

        } catch (JOceanusException e) {
            theLogger.error("createGUI didn't complete successfully", e);
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
