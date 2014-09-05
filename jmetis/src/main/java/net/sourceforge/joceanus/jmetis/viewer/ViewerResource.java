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
package net.sourceforge.joceanus.jmetis.viewer;

import net.sourceforge.joceanus.jmetis.JMetisDataException;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr.ResourceId;

/**
 * Resource IDs for JMetis viewer.
 */
public enum ViewerResource implements ResourceId {
    /**
     * Difference IDENTICAL.
     */
    DIFFERENCE_IDENTICAL("difference.IDENTICAL"),

    /**
     * Difference SECURITY.
     */
    DIFFERENCE_SECURITY("difference.SECURITY"),

    /**
     * Difference DIFFERENT.
     */
    DIFFERENCE_DIFFERENT("difference.DIFFERENT"),

    /**
     * FieldValue UNKNOWN.
     */
    FIELDVALUE_UNKNOWN("field.UNKNOWN"),

    /**
     * FieldValue SKIP.
     */
    FIELDVALUE_SKIP("field.SKIP"),

    /**
     * Profile Object Name.
     */
    PROFILE_NAME("profile.Name"),

    /**
     * Profile Task Field.
     */
    PROFILE_TASK("profile.Task"),

    /**
     * Profile Status Field.
     */
    PROFILE_STATUS("profile.Status"),

    /**
     * Profile Elapsed Field.
     */
    PROFILE_ELAPSED("profile.Elapsed"),

    /**
     * Profile Hidden Field.
     */
    PROFILE_HIDDEN("profile.Hidden");

    /**
     * The Bundle name.
     */
    private static final String BUNDLE_NAME = ResourceMgr.getPackageBundle(JMetisDataException.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private ViewerResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMetis.viewer";
    }

    @Override
    public String getBundleName() {
        return BUNDLE_NAME;
    }

    /**
     * Obtain key for difference.
     * @param pValue the Value
     * @return the resource key
     */
    protected static ViewerResource getKeyForDifference(final Difference pValue) {
        switch (pValue) {
            case IDENTICAL:
                return DIFFERENCE_IDENTICAL;
            case SECURITY:
                return DIFFERENCE_SECURITY;
            case DIFFERENT:
                return DIFFERENCE_DIFFERENT;
            default:
                return null;
        }
    }

    /**
     * Obtain key for fieldValue.
     * @param pValue the Value
     * @return the resource key
     */
    protected static ViewerResource getKeyForFieldValue(final JDataFieldValue pValue) {
        switch (pValue) {
            case UNKNOWN:
                return FIELDVALUE_UNKNOWN;
            case SKIP:
                return FIELDVALUE_SKIP;
            default:
                return null;
        }
    }
}
