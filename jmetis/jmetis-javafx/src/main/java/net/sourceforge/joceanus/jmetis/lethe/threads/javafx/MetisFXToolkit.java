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
package net.sourceforge.joceanus.jmetis.lethe.threads.javafx;

import javafx.scene.Node;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.manager.javafx.GordianFXHashManager;
import net.sourceforge.joceanus.jmetis.lethe.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.lethe.viewer.javafx.MetisFXViewerWindow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.help.javafx.TethysFXHelpWindow;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * Metis javaFX Toolkit.
 */
public class MetisFXToolkit
        extends MetisToolkit<Node, Node> {
    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MetisFXToolkit() throws OceanusException {
        this(null, true);
    }

    /**
     * Constructor.
     * @param pSlider use slider status
     * @throws OceanusException on error
     */
    public MetisFXToolkit(final boolean pSlider) throws OceanusException {
        this(null, pSlider);
    }

    /**
     * Constructor.
     * @param pInfo the program info
     * @param pSlider use slider status
     * @throws OceanusException on error
     */
    public MetisFXToolkit(final MetisProgram pInfo,
                          final boolean pSlider) throws OceanusException {
        super(pInfo, pSlider);
    }

    @Override
    public TethysFXGuiFactory getGuiFactory() {
        return (TethysFXGuiFactory) super.getGuiFactory();
    }

    @Override
    public MetisFXThreadManager getThreadManager() {
        return (MetisFXThreadManager) super.getThreadManager();
    }

    @Override
    protected TethysFXGuiFactory newGuiFactory() {
        return new TethysFXGuiFactory(getFormatter(), getProgramDefinitions());
    }

    @Override
    protected MetisFXThreadManager newThreadManager(final boolean pSlider) {
        return new MetisFXThreadManager(this, pSlider);
    }

    @Override
    protected GordianHashManager newSecurityManager(final GordianParameters pParameters) throws OceanusException {
        return new GordianFXHashManager(getGuiFactory(), pParameters);
    }

    @Override
    public TethysFXHelpWindow newHelpWindow() {
        return new TethysFXHelpWindow(getGuiFactory());
    }

    @Override
    public MetisFXViewerWindow newViewerWindow() throws OceanusException {
        return new MetisFXViewerWindow(getGuiFactory(), getViewerManager());
    }

    @Override
    protected MetisFXThreadProgressStatus newThreadSliderStatus(final MetisThreadManager<Node, Node> pManager) {
        return new MetisFXThreadProgressStatus(pManager, getGuiFactory());
    }
}