/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jthemis.jira;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import net.sourceforge.joceanus.jmetis.atlas.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer;

/**
 * Jira test suite.
 */
public class TestJira {
    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(String[] args) {
        try {
            /* Configure log4j */
            Properties myLogProp = new Properties();
            myLogProp.setProperty("log4j.rootLogger", "ERROR, A1");
            myLogProp.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
            myLogProp.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
            myLogProp.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
            PropertyConfigurator.configure(myLogProp);

            MetisViewerManager myViewer = new MetisViewerManager();
            ThemisJiraServer myServer = new ThemisJiraServer(new MetisPreferenceManager(myViewer));
            myServer.getIssue("FIN-47");
            System.exit(0);
        } catch (OceanusException e) {
        }
    }
}
