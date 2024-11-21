/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.swing.dialog;

import javax.swing.JFrame;

import net.sourceforge.joceanus.tethys.ui.api.dialog.TethysUIAlert;
import net.sourceforge.joceanus.tethys.ui.api.dialog.TethysUIChildDialog;
import net.sourceforge.joceanus.tethys.ui.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.tethys.ui.api.dialog.TethysUIPasswordDialog;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;

/**
 * swing Dialog factory.
 */
public class TethysUISwingDialogFactory
        implements TethysUIDialogFactory {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * The frame.
     */
    private JFrame theFrame;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    public TethysUISwingDialogFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    /**
     * Set the frame.
     * @param pFrame the frame
     */
    public void setFrame(final JFrame pFrame) {
        theFrame = pFrame;
    }

    @Override
    public TethysUISwingFileSelector newFileSelector() {
        return new TethysUISwingFileSelector(theFrame);
    }

    @Override
    public TethysUISwingDirectorySelector newDirectorySelector() {
        return new TethysUISwingDirectorySelector(theFrame);
    }

    @Override
    public TethysUISwingAboutBox newAboutBox() {
        return new TethysUISwingAboutBox(theFactory, theFrame);
    }

    @Override
    public TethysUIPasswordDialog newPasswordDialog(final String pTitle,
                                                    final boolean pNeedConfirm) {
        return new TethysUISwingPasswordDialog(theFactory, theFrame, pTitle, pNeedConfirm);
    }

    @Override
    public TethysUIAlert newAlert() {
        return new TethysUISwingAlert(theFrame);
    }

    @Override
    public TethysUIChildDialog newChildDialog() {
        return new TethysUISwingChildDialog(theFrame);
    }
}
