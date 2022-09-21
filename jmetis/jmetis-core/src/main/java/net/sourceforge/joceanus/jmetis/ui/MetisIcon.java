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
package net.sourceforge.joceanus.jmetis.ui;

import java.io.InputStream;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIConstant;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;

/**
 * Metis Icon IDs.
 */
public enum MetisIcon
        implements TethysUIIconId {
    /**
     * Delete.
     */
    DELETE("icons/OrangeJellyAlphaDelete.png"),

    /**
     * New.
     */
    NEW("icons/GreenJellyPlus.png"),

    /**
     * Commit.
     */
    COMMIT("icons/GreenJellyCheck.png"),

    /**
     * Cancel.
     */
    CANCEL("icons/OrangeJellyUndo.png"),

    /**
     * Undo.
     */
    UNDO("icons/OrangeJellyArrowLeft.png"),

    /**
     * Reset.
     */
    RESET("icons/OrangeJellyDoubleArrowLeft.png"),

    /**
     * Edit.
     */
    EDIT("icons/GreenJellyBusinessEdit.png"),

    /**
     * Active.
     */
    ACTIVE("icons/GreenJellySymbolActive.png"),

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
    SAVE("icons/GreenJellySaveToFile.png"),

    /**
     * The viewer icon.
     */
    VIEWER("icons/BlueJellyViewer.png");

    /**
     * New Button ToolTip.
     */
    public static final String TIP_NEW = MetisUIResource.ICON_TIP_NEW.getValue();

    /**
     * Delete Button ToolTip.
     */
    public static final String TIP_DELETE = MetisUIResource.ICON_TIP_DELETE.getValue();

    /**
     * Active Button ToolTip.
     */
    public static final String TIP_ACTIVE = MetisUIResource.ICON_TIP_ACTIVE.getValue();

    /**
     * Commit Button ToolTip.
     */
    public static final String TIP_COMMIT = MetisUIResource.ICON_TIP_COMMIT.getValue();

    /**
     * UnDo Button ToolTip.
     */
    public static final String TIP_UNDO = MetisUIResource.ICON_TIP_UNDO.getValue();

    /**
     * Reset Button ToolTip.
     */
    public static final String TIP_RESET = MetisUIResource.ICON_TIP_RESET.getValue();

    /**
     * Edit Button ToolTip.
     */
    public static final String TIP_EDIT = MetisUIResource.ICON_TIP_EDIT.getValue();

    /**
     * Cancel Button ToolTip.
     */
    public static final String TIP_CANCEL = MetisUIResource.ICON_TIP_CANCEL.getValue();

    /**
     * Print Button ToolTip.
     */
    private static final String TIP_PRINT = MetisUIResource.ICON_TIP_PRINT.getValue();

    /**
     * Download Button ToolTip.
     */
    private static final String TIP_DOWNLOAD = MetisUIResource.ICON_TIP_DOWNLOAD.getValue();

    /**
     * Save Button ToolTip.
     */
    public static final String TIP_SAVE = MetisUIResource.ICON_TIP_SAVE.getValue();

    /**
     * Viewer Button ToolTip.
     */
    public static final String TIP_VIEWER = MetisUIResource.ICON_TIP_VIEWER.getValue();

    /**
     * Default icon size.
     */
    public static final int ICON_SIZE = 32;

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

    @Override
    public InputStream getInputStream() {
        return MetisIcon.class.getResourceAsStream(theSource);
    }

    /**
     * Configure button.
     * @param pButton the button
     */
    public static void configureButton(final TethysUIButton pButton) {
        pButton.setIconOnly();
        pButton.setIconWidth(ICON_SIZE);
        pButton.setNullMargins();
    }

    /**
     * Configure new scroll button.
     * @param pButton the button manager
     */
    public static void configureNewScrollButton(final TethysUIScrollButtonManager<?> pButton) {
        pButton.setSimpleDetails(NEW, TethysUIConstant.DEFAULT_ICONWIDTH, TIP_NEW);
    }

    /**
     * Configure new icon button.
     * @param pButton the button manager
     */
    public static void configureNewIconButton(final TethysUIButton pButton) {
        MetisIcon.configureButton(pButton);
        pButton.setIcon(NEW);
        pButton.setToolTip(TIP_NEW);
    }

    /**
     * Configure edit icon button.
     * @param pButton the button manager
     */
    public static void configureEditIconButton(final TethysUIButton pButton) {
        MetisIcon.configureButton(pButton);
        pButton.setIcon(EDIT);
        pButton.setToolTip(TIP_EDIT);
    }

    /**
     * Configure delete icon button.
     * @param pButton the button manager
     */
    public static void configureDeleteIconButton(final TethysUIButton pButton) {
        MetisIcon.configureButton(pButton);
        pButton.setIcon(DELETE);
        pButton.setToolTip(TIP_DELETE);
    }

    /**
     * Configure commit icon button.
     * @param pButton the button manager
     */
    public static void configureCommitIconButton(final TethysUIButton pButton) {
        MetisIcon.configureButton(pButton);
        pButton.setIcon(COMMIT);
        pButton.setToolTip(TIP_COMMIT);
    }

    /**
     * Configure undo icon button.
     * @param pButton the button manager
     */
    public static void configureUndoIconButton(final TethysUIButton pButton) {
        MetisIcon.configureButton(pButton);
        pButton.setIcon(UNDO);
        pButton.setToolTip(TIP_UNDO);
    }

    /**
     * Configure reset icon button.
     * @param pButton the button manager
     */
    public static void configureResetIconButton(final TethysUIButton pButton) {
        MetisIcon.configureButton(pButton);
        pButton.setIcon(RESET);
        pButton.setToolTip(TIP_RESET);
    }

    /**
     * Configure cancel icon button.
     * @param pButton the button manager
     */
    public static void configureCancelIconButton(final TethysUIButton pButton) {
        MetisIcon.configureButton(pButton);
        pButton.setIcon(CANCEL);
        pButton.setToolTip(TIP_CANCEL);
    }

    /**
     * Configure print icon button.
     * @param pButton the button manager
     */
    public static void configurePrintIconButton(final TethysUIButton pButton) {
        configureButton(pButton);
        pButton.setIcon(PRINT);
        pButton.setToolTip(TIP_PRINT);
    }

    /**
     * Configure download icon button.
     * @param pButton the button manager
     */
    public static void configureDownloadIconButton(final TethysUIButton pButton) {
        configureButton(pButton);
        pButton.setIcon(DOWNLOAD);
        pButton.setToolTip(TIP_DOWNLOAD);
    }

    /**
     * Configure save icon button.
     * @param pButton the button manager
     */
    public static void configureSaveIconButton(final TethysUIButton pButton) {
        configureButton(pButton);
        pButton.setIcon(SAVE);
        pButton.setToolTip(TIP_SAVE);
    }

    /**
     * Configure viewer icon button.
     * @param pButton the button manager
     */
    public static void configureViewerIconButton(final TethysUIButton pButton) {
        configureButton(pButton);
        pButton.setIcon(VIEWER);
        pButton.setToolTip(TIP_VIEWER);
    }

    /**
     * Configure status icon button.
     * @return the mapSet configuration
     */
    public static TethysUIIconMapSet<MetisAction> configureStatusIconButton(final TethysUIFactory<?> pFactory) {
        final TethysUIIconMapSet<MetisAction> myMapSet = pFactory.buttonFactory().newIconMapSet();
        myMapSet.setMappingsForValue(MetisAction.ACTIVE, MetisAction.ACTIVE, ACTIVE, TIP_ACTIVE);
        myMapSet.setMappingsForValue(MetisAction.DELETE, MetisAction.DO, MetisIcon.DELETE, MetisIcon.TIP_DELETE);
        myMapSet.setMappingsForValue(MetisAction.INSERT, MetisAction.DO, MetisIcon.NEW, MetisIcon.TIP_NEW);
        return myMapSet;
    }
}
