/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2022 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.test.atlas.ui.swing;

import net.sourceforge.joceanus.jmetis.launch.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jmetis.test.atlas.ui.MetisTestDataTable;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.HeadlessException;

/**
 * Table Tester.
 */
public class MetisSwingTestTable {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(MetisSwingTestTable.class);

    /**
     * Toolkit.
     */
    private final MetisSwingToolkit theToolkit;

    /**
     * GUI factory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * The Table.
     */
    private final MetisTestDataTable theTable;

    /**
     * Frame.
     */
    private final JFrame theFrame;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MetisSwingTestTable() throws OceanusException {
        /* Create toolkit */
        theToolkit = new MetisSwingToolkit(null);

        /* Access components */
        theGuiFactory = theToolkit.getGuiFactory();

        /* Create table */
        theTable = new MetisTestDataTable(theToolkit);

        /* Create the Panels */
        theFrame = new JFrame("MetisTable Demo");
        theGuiFactory.setFrame(theFrame);
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(MetisSwingTestTable::createAndShowGUI);
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the UI */
            final MetisSwingTestTable myTable = new MetisSwingTestTable();
            final JFrame myFrame = myTable.theFrame;

            /* Attach the panel to the frame */
            final JComponent myPanel = TethysSwingNode.getComponent(myTable.theTable);
            myPanel.setOpaque(true);
            myFrame.setContentPane(myPanel);
            myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);
        } catch (OceanusException
                | HeadlessException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }
}
