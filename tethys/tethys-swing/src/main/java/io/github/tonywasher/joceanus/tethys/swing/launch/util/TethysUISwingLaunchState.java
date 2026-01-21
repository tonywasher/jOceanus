/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.tethys.swing.launch.util;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIIconId;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUILaunchProgram;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIMainPanel;
import io.github.tonywasher.joceanus.tethys.swing.base.TethysUISwingNode;
import io.github.tonywasher.joceanus.tethys.swing.base.TethysUISwingUtils;
import io.github.tonywasher.joceanus.tethys.swing.factory.TethysUISwingFactory;
import io.github.tonywasher.joceanus.tethys.swing.menu.TethysUISwingMenuBarManager;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

/**
 * State for Swing program.
 */
public class TethysUISwingLaunchState {
    /**
     * The Program definition.
     */
    private final TethysUILaunchProgram theApp;

    /**
     * The factory.
     */
    private final TethysUISwingFactory theFactory;

    /**
     * The Frame.
     */
    private JFrame theFrame;

    /**
     * The Panel.
     */
    private TethysUIMainPanel theMain;

    /**
     * Constructor.
     *
     * @param pProgram the program definition
     * @throws OceanusException on error
     */
    public TethysUISwingLaunchState(final TethysUILaunchProgram pProgram) throws OceanusException {
        /* Store details. */
        theApp = pProgram;
        theFactory = new TethysUISwingFactory(pProgram);
    }

    /**
     * Create a SwingSSplash.
     */
    public void createSplash() {
        /* Sort out splash frame */
        final TethysUIIconId mySplashId = theApp.getSplash();
        if (mySplashId != null) {
            TethysUISwingSplash.renderSplashFrame(theApp.getName(), theApp.getVersion());
        }
    }

    /**
     * Initialise the main program.
     *
     * @throws OceanusException on error
     */
    public void createMain() throws OceanusException {
        /* Create the main panel */
        theMain = createMain(theApp, theFactory);

        /* Create the frame and declare it */
        theFrame = new JFrame(theApp.getName());
        theFactory.setFrame(theFrame);

        /* Add the Menu bar */
        final TethysUISwingMenuBarManager myMenuBar = (TethysUISwingMenuBarManager) theMain.getMenuBar();
        if (myMenuBar != null) {
            theFrame.setJMenuBar(((TethysUISwingMenuBarManager) theMain.getMenuBar()).getNode());
        }

        /* Attach the panel to the frame */
        theFrame.getContentPane().add(TethysUISwingNode.getComponent(theMain.getComponent()), BorderLayout.CENTER);
        theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        theFrame.addWindowListener(new WindowClose());

        /* Set preferred size if specified */
        final int[] myDim = theApp.getPanelDimensions();
        if (myDim != null) {
            theFrame.setPreferredSize(new Dimension(myDim[0], myDim[1]));
        }

        /* Add the icons to the frame */
        final TethysUIIconId[] myIds = theApp.getIcons();
        final Image[] myIcons = TethysUISwingUtils.getIcons(myIds);
        theFrame.setIconImages(Arrays.asList(myIcons));
        theFrame.setTitle(theApp.getName());

        /* Show the frame */
        theFrame.pack();
        theFrame.setLocationRelativeTo(null);
        theFrame.setVisible(true);

        /* Record startUp completion */
        theFactory.getActiveProfile().end();
        theFactory.activateLogSink();
    }

    /**
     * Create the main panel.
     *
     * @param pProgram the program state
     * @param pFactory the factory
     * @return the main panel
     * @throws OceanusException on error
     */
    private static TethysUIMainPanel createMain(final TethysUILaunchProgram pProgram,
                                                final TethysUISwingFactory pFactory) throws OceanusException {
        /* Create the main panel */
        return pProgram.createMainPanel(pFactory);
    }

    /**
     * Window Close Adapter.
     */
    private final class WindowClose
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
