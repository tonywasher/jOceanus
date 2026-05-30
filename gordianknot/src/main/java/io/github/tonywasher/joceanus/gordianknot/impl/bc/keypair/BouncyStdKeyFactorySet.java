/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPairGenerator.BouncyKeyFactorySet;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Standard KeyFactorySet.
 */
public enum BouncyStdKeyFactorySet
        implements BouncyKeyFactorySet {
    /**
     * Instance.
     */
    INSTANCE;

    @Override
    public AsymmetricKeyParameter parsePKCS8EncodedKeySpec(final PKCS8EncodedKeySpec pEncodedKey) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Parse the encoded keySpec */
            final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pEncodedKey.getEncoded());
            return PrivateKeyFactory.createKey(myInfo);

        } catch (IOException e) {
            throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
        }
    }

    @Override
    public PKCS8EncodedKeySpec createPKCS8EncodedKeySpec(final AsymmetricKeyParameter pParams) throws GordianException {
        /* Protect against exceptions */
        try {
            /* build and return the encoding */
            final PrivateKeyInfo myInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(pParams, null);
            return new PKCS8EncodedKeySpec(myInfo.getEncoded());

        } catch (IOException e) {
            throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
        }
    }

    @Override
    public AsymmetricKeyParameter parseX509EncodedKeySpec(final X509EncodedKeySpec pEncodedKey) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Parse the encoded keySpec */
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            return PublicKeyFactory.createKey(myInfo);

        } catch (IOException e) {
            throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
        }
    }

    @Override
    public X509EncodedKeySpec createX509EncodedKeySpec(final AsymmetricKeyParameter pParams) throws GordianException {
        /* Protect against exceptions */
        try {
            /* build and return the encoding */
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(pParams);
            return new X509EncodedKeySpec(myInfo.getEncoded());

        } catch (IOException e) {
            throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
        }
    }
}
