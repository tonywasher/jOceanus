/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.keypairset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDHGroup;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianRSAModulus;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSM2Elliptic;

/**
 * KeyPairSetSpec.
 */
public enum GordianKeyPairSetSpec {
    /**
     * 256bit RSA,EC,EdDSA Signature.
     */
    SIGNLO(GordianKeyPairSpec.rsa(GordianRSAModulus.MOD2048),
           GordianKeyPairSpec.ec(GordianDSAElliptic.SECP256R1),
           GordianKeyPairSpec.ed25519()),

    /**
     * 512bit RSA,EC,EdDSA Signature.
     */
    SIGNHI(GordianKeyPairSpec.rsa(GordianRSAModulus.MOD3072),
           GordianKeyPairSpec.ec(GordianDSAElliptic.SECP521R1),
           GordianKeyPairSpec.ed448()),

    /**
     * 256bit DH,EC,XDH.
     */
    AGREELO(GordianKeyPairSpec.dh(GordianDHGroup.FFDHE2048),
            GordianKeyPairSpec.ec(GordianDSAElliptic.SECP256R1),
            GordianKeyPairSpec.x25519()),

    /**
     * 512bit DH,EC,XDH.
     */
    AGREEHI(GordianKeyPairSpec.dh(GordianDHGroup.FFDHE3072),
            GordianKeyPairSpec.ec(GordianDSAElliptic.SECP521R1),
            GordianKeyPairSpec.x448()),

    /**
     * 256bit RSA,ELGAMAL,SM2.
     */
    ENCRYPT(GordianKeyPairSpec.rsa(GordianRSAModulus.MOD2048),
            GordianKeyPairSpec.elGamal(GordianDHGroup.FFDHE2048),
            GordianKeyPairSpec.sm2(GordianSM2Elliptic.SM2P256V1));

    /**
     * List of Specs.
     */
    private final List<GordianKeyPairSpec> theSpecs;

    /**
     * Constructor.
     * @param pSpecs the specs
     */
    GordianKeyPairSetSpec(final GordianKeyPairSpec... pSpecs) {
        theSpecs = new ArrayList<>();
        Collections.addAll(theSpecs, pSpecs);
    }

    /**
     * Obtain an iterator for the keyPairSpecs.
     * @return the iterator
     */
    public Iterator<GordianKeyPairSpec> iterator() {
        return theSpecs.iterator();
    }

    /**
     * Obtain the number of keyPairs for this spec.
     * @return the number of keyPairs
     */
    public int numKeyPairs() {
        return theSpecs.size();
    }

    /**
     * can this keySpec be used for signatures?
     * @return true/false
     */
    public boolean canSign() {
        switch (this) {
            case SIGNLO:
            case SIGNHI:
                return true;
            case AGREELO:
            case AGREEHI:
            case ENCRYPT:
            default:
                return false;
        }
    }

    /**
     * can this keySpec be used for agreements?
     * @return true/false
     */
    public boolean canAgree() {
        switch (this) {
            case AGREELO:
            case AGREEHI:
                return true;
            case SIGNLO:
            case SIGNHI:
            default:
            case ENCRYPT:
                return false;
        }
    }

    /**
     * can this keySpec be used for encryption?
     * @return true/false
     */
    public boolean canEncrypt() {
        return this == ENCRYPT;
    }
}
