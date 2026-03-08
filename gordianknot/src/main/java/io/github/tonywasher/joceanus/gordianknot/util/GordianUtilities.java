/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.util;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.spec.GordianKeySetSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.lock.spec.GordianPasswordLockSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianRandomSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySet;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keyset.GordianKeySetData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.lock.GordianFactoryLockImpl;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCorePBESpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keyset.GordianCoreKeySetSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.lock.GordianCorePasswordLockSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.random.GordianCoreRandomSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpecBuilder;

/**
 * Utilities.
 */
public final class GordianUtilities {
    /**
     * The ZipFile extension.
     */
    public static final String ZIPFILE_EXT = ".zip";

    /**
     * The Encrypted ZipFile extension.
     */
    public static final String SECUREZIPFILE_EXT = ".gkzip";

    /**
     * Private constructor.
     */
    private GordianUtilities() {
    }

    /**
     * Obtain Maximum KeyWrapLength.
     *
     * @return the maximum keyWrap size
     */
    public static int getMaximumKeyWrapLength() {
        return GordianCoreKeySet.getDataWrapLength(GordianLength.LEN_256.getByteLength());
    }

    /**
     * Obtain Maximum KeyWrapLength.
     *
     * @return the maximum keyWrap size
     */
    public static int getMaximumKeySetWrapLength() {
        final int my128 = GordianCoreKeySet.getKeySetWrapLength(GordianLength.LEN_128);
        final int my256 = GordianCoreKeySet.getKeySetWrapLength(GordianLength.LEN_256);
        return Math.max(my128, my256);
    }

    /**
     * Obtain FactoryLockLen.
     *
     * @return the factoryLock length
     */
    public static int getFactoryLockLen() {
        return GordianFactoryLockImpl.getEncodedLength();
    }

    /**
     * Obtain Encryption length.
     *
     * @param pDataLength the length of data to be encrypted
     * @return the length of encrypted data
     */
    public static int getKeySetEncryptionLength(final int pDataLength) {
        return GordianKeySetData.getEncryptionLength(pDataLength);
    }

    /**
     * Obtain DigestSpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianDigestSpecBuilder newDigestSpecBuilder() {
        return GordianCoreDigestSpecBuilder.newInstance();
    }

    /**
     * Obtain SymKeySpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianSymKeySpecBuilder newSymKeySpecBuilder() {
        return GordianCoreSymKeySpecBuilder.newInstance();
    }

    /**
     * Obtain SymKeySpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianSymCipherSpecBuilder newSymCipherSpecBuilder() {
        return GordianCoreSymCipherSpecBuilder.newInstance();
    }

    /**
     * Obtain SymKeySpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianStreamKeySpecBuilder newStreamKeySpecBuilder() {
        return GordianCoreStreamKeySpecBuilder.newInstance();
    }

    /**
     * Obtain SymKeySpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianStreamCipherSpecBuilder newStreamCipherSpecBuilder() {
        return GordianCoreStreamCipherSpecBuilder.newInstance();
    }

    /**
     * Obtain PBESpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianPBESpecBuilder newPBESpecBuilder() {
        return GordianCorePBESpecBuilder.newInstance();
    }

    /**
     * Obtain MacSpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianMacSpecBuilder newMacSpecBuilder() {
        return GordianCoreMacSpecBuilder.newInstance();
    }

    /**
     * Obtain RandomSpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianRandomSpecBuilder newRandomSpecBuilder() {
        return GordianCoreRandomSpecBuilder.newInstance();
    }

    /**
     * Obtain KeySetSpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianKeySetSpecBuilder newKeySetSpecBuilder() {
        return GordianCoreKeySetSpecBuilder.newInstance();
    }

    /**
     * Obtain PasswordLockSpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianPasswordLockSpecBuilder newPasswordLockSpecBuilder() {
        return GordianCorePasswordLockSpecBuilder.newInstance();
    }

    /**
     * Obtain KeyPairSpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianKeyPairSpecBuilder newKeyPairSpecBuilder() {
        return GordianCoreKeyPairSpecBuilder.newInstance();
    }

    /**
     * Obtain SignatureSpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianSignatureSpecBuilder newSignatureSpecBuilder() {
        return GordianCoreSignatureSpecBuilder.newInstance();
    }

    /**
     * Obtain EncryptorSpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianEncryptorSpecBuilder newEncryptorSpecBuilder() {
        return GordianCoreEncryptorSpecBuilder.newInstance();
    }

    /**
     * Obtain AgreementSpecBuilder instance.
     *
     * @return the specBuilder
     */
    public static GordianAgreementSpecBuilder newAgreementSpecBuilder() {
        return GordianCoreAgreementSpecBuilder.newInstance();
    }
}
