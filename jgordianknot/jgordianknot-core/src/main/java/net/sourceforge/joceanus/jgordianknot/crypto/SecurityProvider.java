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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Available security providers.
 */
public enum SecurityProvider {
    /**
     * BouncyCastle.
     */
    BC;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CryptoResource.getKeyForProvider(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain provider.
     * @return the provider
     */
    public String getProvider() {
        switch (this) {
            case BC:
                return "BC";
            default:
                return name();
        }
    }

    /**
     * New provider.
     */
    public void ensureInstalled() {
        switch (this) {
            case BC:
                Security.addProvider(new BouncyCastleProvider());
                break;
            default:
                break;
        }
    }
}
