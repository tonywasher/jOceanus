/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.jira;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer;

/**
 * Jira test suite.
 */
public final class TestJira {
    /**
     * Private constructor.
     */
    private TestJira() {
    }

    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(final String[] args) {
        try {
            final MetisViewerManager myViewer = new MetisViewerManager();
            final ThemisJiraServer myServer = new ThemisJiraServer(new MetisPreferenceManager(myViewer));
            myServer.getIssue("FIN-47");
            System.exit(0);
        } catch (OceanusException e) {
            System.out.println("Help");
        }
    }
}
