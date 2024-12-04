/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianValidator;

/**
 * Jca Validator.
 */
public class JcaValidator
        extends GordianValidator {
    @Override
    public boolean validSymKeyType(final GordianSymKeyType pKeyType) {
        return JcaCipherFactory.supportedSymKeyType(pKeyType)
                && super.validSymKeyType(pKeyType);
    }

    @Override
    protected boolean validHMacDigestType(final GordianDigestType pDigestType) {
        return JcaDigest.isHMacSupported(pDigestType)
                && validDigestType(pDigestType);
    }

    @Override
    public boolean validDigestType(final GordianDigestType pDigestType) {
        /* Perform standard checks */
        if (!super.validDigestType(pDigestType)) {
            return false;
        }

        /* Disable non-JCE digests */
        switch (pDigestType) {
            case JH:
            case GROESTL:
            case CUBEHASH:
            case KANGAROO:
            case ASCON:
            case ISAP:
            case PHOTONBEETLE:
            case SPARKLE:
            case XOODYAK:
                return false;
            default:
                return true;
        }
    }
}
