/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.ui;

import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;

/**
 * Themis Application definition.
 */
public class ThemisApp
        extends TethysProgram {
    /**
     * Constructor.
     */
    public ThemisApp() {
        super("ThemisApp.properties");
    }

    @Override
    public TethysIconId[] getIcons() {
        return new TethysIconId[]
        { ThemisIcon.SMALL, ThemisIcon.BIG };
    }

    @Override
    public TethysIconId getSplash() {
        return ThemisIcon.SPLASH;
    }
}
