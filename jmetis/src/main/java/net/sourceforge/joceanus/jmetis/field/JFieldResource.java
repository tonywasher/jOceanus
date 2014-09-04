/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.field;

import net.sourceforge.joceanus.jmetis.JMetisDataException;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr.ResourceId;

/**
 * Resource IDs for JMetis Field.
 */
public enum JFieldResource implements ResourceId {
    /**
     * FieldValue ERROR.
     */
    FIELDVALUE_ERROR("fieldvalue.ERROR");

    /**
     * The Bundle name.
     */
    private final static String BUNDLE_NAME = ResourceMgr.getPackageBundle(JMetisDataException.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private JFieldResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMetis.field";
    }

    @Override
    public String getBundleName() {
        return BUNDLE_NAME;
    }

    /**
     * Obtain key for fieldValue.
     * @param pValue the Value
     * @return the resource key
     */
    protected static JFieldResource getKeyForFieldValue(final JFieldValue pValue) {
        switch (pValue) {
            case ERROR:
                return FIELDVALUE_ERROR;
            default:
                return null;
        }
    }
}
