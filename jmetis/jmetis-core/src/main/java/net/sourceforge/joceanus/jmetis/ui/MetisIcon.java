/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.ui;

import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;

/**
 * Metis Icon IDs.
 */
public enum MetisIcon implements TethysIconId {
    /**
     * The print icon.
     */
    PRINT("icons/BlueJellyPrint.png"),

    /**
     * The download arrow.
     */
    DOWNLOAD("icons/GreenJellyDownload.png"),

    /**
     * The save icon.
     */
    SAVE("icons/GreenJellySaveToFile.png");

    /**
     * Print Button ToolTip.
     */
    private static final String TIP_PRINT = MetisUIResource.ICON_PRINT.getValue();

    /**
     * Download Button ToolTip.
     */
    private static final String TIP_NEW = MetisUIResource.ICON_DOWNLOAD.getValue();

    /**
     * Save Button ToolTip.
     */
    private static final String TIP_SAVE = MetisUIResource.ICON_SAVE.getValue();

    /**
     * Default icon size.
     */
    public static final int ICON_SIZE = 24;

    /**
     * Source name.
     */
    private final String theSource;

    /**
     * Constructor.
     * @param pSourceName the source name
     */
    MetisIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    /**
     * Configure button.
     * @param pButton the button
     */
    public static void configureButton(final TethysButton<?, ?> pButton) {
        pButton.setIconOnly();
        pButton.setIconWidth(ICON_SIZE);
        pButton.setNullMargins();
    }

    /**
     * Configure print icon button.
     * @param pButton the button manager
     */
    public static void configurePrintIconButton(final TethysButton<?, ?> pButton) {
        configureButton(pButton);
        pButton.setIcon(PRINT);
        pButton.setToolTip(TIP_PRINT);
    }

    /**
     * Configure download icon button.
     * @param pButton the button manager
     */
    public static void configureDownloadIconButton(final TethysButton<?, ?> pButton) {
        configureButton(pButton);
        pButton.setIcon(DOWNLOAD);
        pButton.setToolTip(TIP_NEW);
    }

    /**
     * Configure save icon button.
     * @param pButton the button manager
     */
    public static void configureSaveIconButton(final TethysButton<?, ?> pButton) {
        configureButton(pButton);
        pButton.setIcon(SAVE);
        pButton.setToolTip(TIP_SAVE);
    }
}
