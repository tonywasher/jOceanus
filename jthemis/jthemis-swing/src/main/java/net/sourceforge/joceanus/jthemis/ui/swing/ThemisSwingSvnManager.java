/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.ui.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingMenuBarManager;
import net.sourceforge.joceanus.jthemis.ui.ThemisSvnManager;

/**
 * Top level Swing SvnManager window.
 */
public class ThemisSwingSvnManager
        extends ThemisSvnManager<JComponent, Icon> {
    /**
     * The frame.
     */
    private final JFrame theFrame;

    /**
     * Constructor.
     * @param pFrame the frame
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    protected ThemisSwingSvnManager(final JFrame pFrame,
                                    final MetisSwingToolkit pToolkit) throws OceanusException {
        /* Initialise underlying class */
        super(pToolkit);
        theFrame = pFrame;

        /* Add the menuBar */
        /* Add the Menu bar */
        pFrame.setJMenuBar(getMenuBar().getNode());

        /* Attach the panel to the frame */
        pFrame.setContentPane(getTabs().getNode());
        pFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        pFrame.addWindowListener(new WindowClose());
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
            /* terminate the executor */
            handleWindowClosed();

            /* Dispose of the frame */
            theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            theFrame.dispose();
        }
    }
}