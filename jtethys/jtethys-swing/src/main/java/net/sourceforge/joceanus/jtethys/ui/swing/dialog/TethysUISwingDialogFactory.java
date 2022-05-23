/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.dialog;

import java.awt.Color;
import javax.swing.JFrame;

import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * swing Dialog factory.
 */
public class TethysUISwingDialogFactory
        implements TethysUIDialogFactory<Color> {
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
    public TethysUISwingColorPicker newColorPicker() {
        return new TethysUISwingColorPicker();
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
}
