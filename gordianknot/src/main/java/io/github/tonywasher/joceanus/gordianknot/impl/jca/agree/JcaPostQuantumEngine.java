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

package io.github.tonywasher.joceanus.gordianknot.impl.jca.agree;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.agree.JcaAgreement.JcaAgreementBase;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPublicKey;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;

import javax.crypto.KeyGenerator;
import java.security.InvalidAlgorithmParameterException;

/**
 * Jca PostQuantum Agreement.
 */
public class JcaPostQuantumEngine
        extends JcaAgreementBase {
    /**
     * Key Agreement.
     */
    private final KeyGenerator theGenerator;

    /**
     * Constructor.
     *
     * @param pFactory   the security factory
     * @param pSpec      the agreementSpec
     * @param pGenerator the generator
     */
    JcaPostQuantumEngine(final GordianCoreAgreementFactory pFactory,
                         final GordianCoreAgreementSpec pSpec,
                         final KeyGenerator pGenerator) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Store the generator */
        theGenerator = pGenerator;
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create encapsulation */
            final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getServerKeyPair());
            final KEMGenerateSpec mySpec = new KEMGenerateSpec.Builder(myPublic.getPublicKey(),
                    GordianSymKeyType.AES.toString(), GordianLength.LEN_256.getLength()).withKdfAlgorithm(derivationAlgorithmId()).build();
            theGenerator.init(mySpec, getRandom());
            final SecretKeyWithEncapsulation mySecret = (SecretKeyWithEncapsulation) theGenerator.generateKey();

            /* Store the encapsulation */
            setEncapsulated(mySecret.getEncapsulation());

            /* Store secret */
            storeSecret(mySecret.getEncoded());

        } catch (InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create extractor */
            final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getServerKeyPair());
            final KEMExtractSpec mySpec = new KEMExtractSpec.Builder(myPrivate.getPrivateKey(), getEncapsulated(),
                    GordianSymKeyType.AES.toString(), GordianLength.LEN_256.getLength()).withKdfAlgorithm(derivationAlgorithmId()).build();
            theGenerator.init(mySpec);

            /* Store secret */
            final SecretKeyWithEncapsulation mySecret = (SecretKeyWithEncapsulation) theGenerator.generateKey();
            storeSecret(mySecret.getEncoded());

        } catch (InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }
}
