/*******************************************************************************
 * GordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot.api.keypair;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;

/**
 * DH Groups.
 */
public enum GordianDHGroup {
    /**
     * ffdhe 2048.
     */
    FFDHE2048(DHStandardGroups.rfc7919_ffdhe2048),

    /**
     * std 2048.
     */
    STD2048(DHStandardGroups.rfc3526_2048),

    /**
     * std 1024.
     */
    STD1024(DHStandardGroups.rfc2409_1024),

    /**
     * std 2048.
     */
    STD1536(DHStandardGroups.rfc3526_1536),

    /**
     * std 3072.
     */
    STD3072(DHStandardGroups.rfc3526_3072),

    /**
     * ffdhe 3072.
     */
    FFDHE3072(DHStandardGroups.rfc7919_ffdhe3072),

    /**
     * std 4096.
     */
    STD4096(DHStandardGroups.rfc3526_4096),

    /**
     * ffdhe 4096.
     */
    FFDHE4096(DHStandardGroups.rfc7919_ffdhe4096),

    /**
     * std 6144.
     */
    STD6144(DHStandardGroups.rfc3526_6144),

    /**
     * ffdhe 6144.
     */
    FFDHE6144(DHStandardGroups.rfc7919_ffdhe6144),

    /**
     * std 8192.
     */
    STD8192(DHStandardGroups.rfc3526_8192),

    /**
     * ffdhe 8192.
     */
    FFDHE8192(DHStandardGroups.rfc7919_ffdhe8192);

    /**
     * The DH Group.
     */
    private final DHParameters theParameters;

    /**
     * Constructor.
     * @param pParams the parameters
     */
    GordianDHGroup(final DHParameters pParams) {
        theParameters = pParams;
    }

    /**
     * Obtain the parameters.
     * @return the parameters
     */
    public DHParameters getParameters() {
        return theParameters;
    }

    /**
     * Obtain the group for parameters.
     * @param pParams the parameters
     * @return the group
     */
    public static GordianDHGroup getGroupForParams(final DHParameters pParams) {
        /* Loop through the values */
        for (GordianDHGroup myGroup: values()) {
            if (myGroup.getParameters().equals(pParams)) {
                return myGroup;
            }
        }
        return null;
    }
}
