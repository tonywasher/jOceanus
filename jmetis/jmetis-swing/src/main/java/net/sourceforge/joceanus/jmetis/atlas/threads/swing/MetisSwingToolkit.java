/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.threads.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.manager.swing.GordianSwingHashManager;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataTableItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisIndexedList;
import net.sourceforge.joceanus.jmetis.atlas.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.atlas.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.atlas.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.atlas.ui.swing.MetisSwingTableManager;
import net.sourceforge.joceanus.jmetis.atlas.viewer.swing.MetisSwingViewerWindow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.help.swing.TethysSwingHelpWindow;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Metis Swing Toolkit.
 */
public class MetisSwingToolkit
        extends MetisToolkit<JComponent, Icon> {
    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MetisSwingToolkit() throws OceanusException {
        this(null, true);
    }

    /**
     * Constructor.
     * @param pSlider use slider status
     * @throws OceanusException on error
     */
    public MetisSwingToolkit(final boolean pSlider) throws OceanusException {
        this(null, pSlider);
    }

    /**
     * Constructor.
     * @param pInfo the program info
     * @param pSlider use slider status
     * @throws OceanusException on error
     */
    public MetisSwingToolkit(final MetisProgram pInfo,
                             final boolean pSlider) throws OceanusException {
        super(pInfo, pSlider);
    }

    @Override
    public TethysSwingGuiFactory getGuiFactory() {
        return (TethysSwingGuiFactory) super.getGuiFactory();
    }

    @Override
    public MetisSwingThreadManager getThreadManager() {
        return (MetisSwingThreadManager) super.getThreadManager();
    }

    @Override
    protected TethysSwingGuiFactory newGuiFactory() {
        return new TethysSwingGuiFactory(getFormatter(), getProgramDefinitions());
    }

    @Override
    protected MetisSwingThreadManager newThreadManager(final boolean pSlider) {
        return new MetisSwingThreadManager(this, pSlider);
    }

    @Override
    protected GordianHashManager newSecurityManager(final GordianParameters pParameters) throws OceanusException {
        return new GordianSwingHashManager(getGuiFactory(), pParameters);
    }

    @Override
    public TethysSwingHelpWindow newHelpWindow() {
        return new TethysSwingHelpWindow(getGuiFactory());
    }

    @Override
    public MetisSwingViewerWindow newViewerWindow() throws OceanusException {
        return new MetisSwingViewerWindow(getGuiFactory(), getViewerManager());
    }

    @Override
    protected MetisSwingThreadProgressStatus newThreadSliderStatus(final MetisThreadManager<JComponent, Icon> pManager) {
        return new MetisSwingThreadProgressStatus(pManager, getGuiFactory());
    }

    @Override
    public <R extends MetisDataTableItem> MetisSwingTableManager<R> newTableManager(final MetisIndexedList<R> pList) {
        return new MetisSwingTableManager<>(getGuiFactory(), pList);
    }
}
