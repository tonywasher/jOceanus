/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot.manager;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for Cryptographic package.
 */
public enum MgrResource implements ResourceId {
    /**
     * Provider BC.
     */
    PROVIDER_BC("provider.BC"),

    /**
     * Label Password.
     */
    LABEL_PASSWORD("label.password"),

    /**
     * Label Confirm.
     */
    LABEL_CONFIRM("label.confirm"),

    /**
     * Button OK.
     */
    BUTTON_OK("button.ok"),

    /**
     * Button Cancel.
     */
    BUTTON_CANCEL("button.cancel"),

    /**
     * Title for password.
     */
    TITLE_PASSWORD("title.password"),

    /**
     * Title for new password.
     */
    TITLE_NEWPASS("title.newPassword"),

    /**
     * Title for error.
     */
    TITLE_ERROR("title.error"),

    /**
     * Error Bad Password.
     */
    ERROR_BADPASS("error.badPassword"),

    /**
     * Error Confirm.
     */
    ERROR_CONFIRM("error.confirm"),

    /**
     * Error length 1.
     */
    ERROR_LENGTH1("error.length1"),

    /**
     * Error length 2.
     */
    ERROR_LENGTH2("error.length2");

    /**
     * The Resource Builder.
     */
    private static final ResourceBuilder BUILDER = ResourceBuilder.getPackageResourceBuilder(GordianCryptoException.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    MgrResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "mgr";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }
}
