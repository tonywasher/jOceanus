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
package net.sourceforge.joceanus.jmetis.launch.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.sourceforge.joceanus.jmetis.launch.MetisMainPanel;
import net.sourceforge.joceanus.jmetis.launch.MetisProgram;
import net.sourceforge.joceanus.jmetis.profile.MetisState;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingMenuBarManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSplash;

/**
 * State for Swing program.
 */
public class MetisSwingState {
    /**
     * The Program definition.
     */
    private final MetisState theInfo;

    /**
     * The Frame.
     */
    private JFrame theFrame;

    /**
     * The Panel.
     */
    private MetisMainPanel theMain;

    /**
     * Constructor.
     * @param pClazz the program definition class
     * @throws OceanusException on error
     */
    public MetisSwingState(final Class<? extends TethysProgram> pClazz) throws OceanusException {
        /* Create the program class. */
        theInfo = new MetisState(pClazz);
    }

    /**
     * Create a SwingSSplash.
     */
    public void createSplash() {
        /* Sort out splash frame */
        final TethysProgram myApp = theInfo.getProgramDefinitions();
        final TethysIconId mySplashId = myApp.getSplash();
        if (mySplashId != null) {
            TethysSwingSplash.renderSplashFrame(myApp.getName(), myApp.getVersion());
        }
    }

    /**
     * Initialise the main program.
     * @throws OceanusException on error
     */
    public void createMain() throws OceanusException {
        /* Access program definitions */
        final TethysProgram myApp = theInfo.getProgramDefinitions();
        final MetisProgram myDef = (MetisProgram) myApp;

        /* Create the toolkit */
        final MetisSwingToolkit myToolkit = new MetisSwingToolkit(theInfo, myDef.useSliderStatus());

        /* Create the frame and declare it */
        theFrame = new JFrame(myApp.getName());
        myToolkit.getGuiFactory().setFrame(theFrame);

        /* Create the main panel */
        theMain = createMain(myDef, myToolkit);

        /* Add the Menu bar */
        theFrame.setJMenuBar(((TethysSwingMenuBarManager) theMain.getMenuBar()).getNode());

        /* Create a new pane */
        final JPanel myPane = new JPanel();
        myPane.setLayout(new BorderLayout());
        myPane.add(TethysSwingNode.getComponent(theMain.getComponent()), BorderLayout.CENTER);

        /* Set preferred size if specified */
        final int[] myDim = myDef.getPanelDimensions();
        if (myDim != null) {
            myPane.setPreferredSize(new Dimension(myDim[0], myDim[1]));
        }

        /* Attach the panel to the frame */
        theFrame.setContentPane(myPane);
        theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        theFrame.addWindowListener(new WindowClose());

        /* Add the icons to the frame */
        final TethysIconId[] myIds = myApp.getIcons();
        final Image[] myIcons = TethysSwingGuiUtils.getIcons(myIds);
        theFrame.setIconImages(Arrays.asList(myIcons));
        theFrame.setTitle(myApp.getName());

        /* Show the frame */
        theFrame.pack();
        theFrame.setLocationRelativeTo(null);
        theFrame.setVisible(true);

        /* Record startUp completion */
        myToolkit.getActiveProfile().end();
        myToolkit.getGuiFactory().activateLogSink();
    }

    /**
     * Create the main panel.
     * @param pProgram the program state
     * @param pToolkit the toolkit
     * @return the main panel
     * @throws OceanusException on error
     */
    private static MetisMainPanel createMain(final MetisProgram pProgram,
                                             final MetisSwingToolkit pToolkit) throws OceanusException {
        /* Create the main panel */
        return pProgram.createMainPanel(pToolkit);
    }

    /**
     * Window Close Adapter.
     */
    private class WindowClose
            extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent evt) {
            /* Close child windows */
            if (theMain.handleAppClose()) {
                /* Dispose of the frame */
                theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                theFrame.dispose();
            }
        }
    }
}
