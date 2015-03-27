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

import java.util.Properties;

import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jtethys.JOceanusException;

import org.apache.log4j.PropertyConfigurator;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(JSvnStarter.class);

    /**
     * Private constructor.
     */
    private JSvnStarter() {
    }

    /**
     * Obtain the manager.
     * @return the manager
     */
    public static JSvnManager getManager() {
        return theManager;
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

            /* Create the SvnManager program */
            theManager = new JSvnManager();

        } catch (JOceanusException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
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
