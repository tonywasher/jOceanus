/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ui.swing;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import net.sourceforge.joceanus.jcoeus.ui.panels.CoeusMainPanel;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingMenuBarManager;

/**
 * Coeus Swing Main Panel.
 */
public class CoeusSwingMainPanel
        extends CoeusMainPanel<JComponent, Icon> {
    /**
     * The frame.
     */
    private final JFrame theFrame;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    protected CoeusSwingMainPanel(final MetisSwingToolkit pToolkit) throws OceanusException {
        /* Initialise underlying class */
        super(pToolkit);
        theFrame = pToolkit.getGuiFactory().getFrame();

        /* Add the Menu bar */
        theFrame.setJMenuBar(getMenuBar().getNode());

        /* Attach the panel to the frame */
        theFrame.setContentPane(getTabs().getNode());
        theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        theFrame.addWindowListener(new WindowClose());

        /* Add the icons to the frame */
        TethysProgram myApp = pToolkit.getProgramDefinitions();
        TethysIconId[] myIds = myApp.getIcons();
        Image[] myIcons = TethysSwingGuiUtils.getIcons(myIds);
        theFrame.setIconImages(Arrays.asList(myIcons));
        theFrame.setTitle(myApp.getName());

        /* Show the frame */
        theFrame.pack();
        theFrame.setLocationRelativeTo(null);
        theFrame.setVisible(true);

        /* Record startUp completion */
        pToolkit.getActiveProfile().end();
    }

    @Override
    protected TethysSwingMenuBarManager getMenuBar() {
        return (TethysSwingMenuBarManager) super.getMenuBar();
    }

    /**
     * Window Close Adapter.
     */
    private class WindowClose
            extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent evt) {
            /* Dispose of the frame */
            theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            theFrame.dispose();
        }
    }
}