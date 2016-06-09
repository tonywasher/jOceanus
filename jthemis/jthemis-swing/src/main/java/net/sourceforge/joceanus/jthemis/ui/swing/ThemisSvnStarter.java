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
package net.sourceforge.joceanus.jthemis.ui.swing;

import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Top level JSvnManager starter.
 * @author Tony Washer
 */
public final class ThemisSvnStarter {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ThemisSvnStarter.class);

    /**
     * Private constructor.
     */
    private ThemisSvnStarter() {
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

            /* Create the Toolkit */
            MetisSwingToolkit myToolkit = new MetisSwingToolkit(null, false);

            /* Create the frame and declare it */
            JFrame myFrame = new JFrame(ThemisSvnManager.class.getSimpleName());
            myToolkit.getGuiFactory().setFrame(myFrame);

            /* Create the SvnManager program */
            new ThemisSwingSvnManager(myFrame, myToolkit);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);

        } catch (OceanusException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}
