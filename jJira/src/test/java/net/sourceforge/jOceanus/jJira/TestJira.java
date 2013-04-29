/*******************************************************************************
 * jJira: Java Jira Link
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jJira;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jJira.data.Server;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceManager;

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
            Server myServer = new Server(new PreferenceManager());
            myServer.loadIssuesFromFilter("AllIssues");
        } catch (JDataException e) {
        }
    }
}
