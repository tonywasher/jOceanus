/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr.ResourceId;

/**
 * Resource IDs for JMetis viewer.
 */
public enum ProgramResource implements ResourceId {
    /**
     * Program Name.
     */
    PROGRAM_NAME("name"),

    /**
     * Program Version.
     */
    PROGRAM_VERSION("version"),

    /**
     * Program Revision.
     */
    PROGRAM_REVISION("revision"),

    /**
     * Program BuildDate.
     */
    PROGRAM_BUILTON("builtOn"),

    /**
     * Program Copyright.
     */
    PROGRAM_COPYRIGHT("copyright");

    /**
     * The Bundle name.
     */
    private static final String BUNDLE_NAME = ResourceMgr.getPackageBundle(MoneyWiseDataType.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private ProgramResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "program";
    }

    @Override
    public String getBundleName() {
        return BUNDLE_NAME;
    }
}
