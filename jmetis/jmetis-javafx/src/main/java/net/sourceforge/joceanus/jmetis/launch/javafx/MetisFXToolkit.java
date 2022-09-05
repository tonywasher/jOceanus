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
package net.sourceforge.joceanus.jmetis.launch.javafx;

import net.sourceforge.joceanus.jmetis.launch.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * Metis javaFX Toolkit.
 */
public class MetisFXToolkit
        extends MetisToolkit {
    /**
     * Constructor.
     * @param pInfo the program info
     * @throws OceanusException on error
     */
    public MetisFXToolkit(final MetisFXState pInfo) throws OceanusException {
        super(pInfo.getState());
    }

    @Override
    public TethysFXGuiFactory getGuiFactory() {
        return (TethysFXGuiFactory) super.getGuiFactory();
    }
}
