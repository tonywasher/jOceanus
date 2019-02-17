/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmetis.threads.javafx;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianSecurityManager;
import net.sourceforge.joceanus.jgordianknot.api.javafx.GordianFXSecurityManager;
import net.sourceforge.joceanus.jmetis.atlas.ui.javafx.MetisFXTableManager;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.list.MetisListEditSession;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmetis.list.MetisListKey;
import net.sourceforge.joceanus.jmetis.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.viewer.javafx.MetisFXViewerWindow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.help.javafx.TethysFXHelpWindow;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * Metis javaFX Toolkit.
 */
public class MetisFXToolkit
        extends MetisToolkit {
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
    protected GordianSecurityManager newSecurityManager(final GordianParameters pParameters) throws OceanusException {
        return new GordianFXSecurityManager(getGuiFactory(), pParameters);
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
    protected MetisFXThreadProgressStatus newThreadSliderStatus(final MetisThreadManager pManager) {
        return new MetisFXThreadProgressStatus(pManager, getGuiFactory());
    }

    @Override
    public <R extends MetisFieldTableItem> MetisFXTableManager<R> newTableManager(final Class<R> pClazz,
                                                                                  final MetisListIndexed<R> pList) {
        return new MetisFXTableManager<>(getGuiFactory(), pClazz, pList);
    }

    @Override
    public <R extends MetisFieldTableItem> MetisFXTableManager<R> newTableManager(final MetisListKey pItemType,
                                                                                  final MetisListEditSession pSession) {
        return new MetisFXTableManager<>(getGuiFactory(), pItemType, pSession);
    }
}
