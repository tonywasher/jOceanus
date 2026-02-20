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
package io.github.tonywasher.joceanus.gordianknot.impl.core.base;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Validator classes.
 */
public class GordianValidator {
    /**
     * Obtain predicate for supported hMac digestTypes.
     *
     * @return the predicate
     */
    public Predicate<GordianNewDigestType> supportedHMacDigestTypes() {
        return this::validHMacDigestType;
    }

    /**
     * Check HMacDigestType.
     *
     * @param pDigestType the digestType
     * @return true/false
     */
    protected boolean validHMacDigestType(final GordianNewDigestType pDigestType) {
        return validDigestType(pDigestType) && GordianCoreDigestType.supportsLargeData(pDigestType);
    }

    /**
     * Check DigestType.
     *
     * @param pDigestType the digestType
     * @return true/false
     */
    public boolean validDigestType(final GordianNewDigestType pDigestType) {
        return true;
    }

    /**
     * Determine whether the digestType is valid for PasswordLock.
     *
     * @return the predicate
     */
    public Predicate<GordianNewDigestType> supportedLockDigestTypes() {
        return supportedHMacDigestTypes().and(isCombinedHashDigest());
    }

    /**
     * Determine whether the digestType is valid for KeyGenLock.
     *
     * @return the predicate
     */
    public Predicate<GordianNewDigestType> supportedKeyGenDigestTypes() {
        return supportedHMacDigestTypes().and(isExternalHashDigest());
    }

    /**
     * Determine whether the digestType is valid for agreement.
     *
     * @return the predicate
     */
    public Predicate<GordianNewDigestType> supportedAgreementDigestTypes() {
        return supportedHMacDigestTypes();
    }

    /**
     * Determine whether the digestType is an external hash.
     *
     * @return the predicate
     */
    private Predicate<GordianNewDigestType> isCombinedHashDigest() {
        return s -> GordianCoreDigestType.getDefaultLength(s).getLength() >= GordianLength.LEN_256.getLength()
                && GordianCoreDigestType.supportsLargeData(s);
    }

    /**
     * Determine whether the digestType is an external hash.
     *
     * @return the predicate
     */
    public Predicate<GordianNewDigestType> isExternalHashDigest() {
        return s -> GordianCoreDigestType.isLengthValid(s, GordianLength.LEN_512);
    }

    /**
     * Obtain a list of external digestTypes.
     *
     * @return the list of supported digestTypes.
     */
    public List<GordianNewDigestType> listAllExternalDigestTypes() {
        return Arrays.stream(GordianNewDigestType.values())
                .filter(supportedExternalDigestTypes())
                .toList();
    }

    /**
     * Obtain predicate for supported external digests.
     *
     * @return the predicate
     */
    public Predicate<GordianNewDigestType> supportedExternalDigestTypes() {
        return supportedHMacDigestTypes().and(isExternalHashDigest());
    }

    /**
     * Obtain predicate for supported keySet symKeySpecs.
     *
     * @param pKeyLen the keyLength
     * @return the predicate
     */
    public Predicate<GordianNewSymKeySpec> supportedKeySetSymKeySpecs(final GordianLength pKeyLen) {
        return s -> supportedKeySetSymKeyTypes(pKeyLen).test(s.getSymKeyType())
                && s.getBlockLength() == GordianLength.LEN_128;
    }

    /**
     * Obtain predicate for keySet SymKeyTypes.
     *
     * @param pKeyLen the keyLength
     * @return the predicate
     */
    public Predicate<GordianNewSymKeyType> supportedKeySetSymKeyTypes(final GordianLength pKeyLen) {
        return t -> validKeySetSymKeyType(t, pKeyLen);
    }

    /**
     * check valid keySet symKeyType.
     *
     * @param pKeyType the symKeyType
     * @param pKeyLen  the keyLength
     * @return true/false
     */
    private boolean validKeySetSymKeyType(final GordianNewSymKeyType pKeyType,
                                          final GordianLength pKeyLen) {
        return validSymKeyType(pKeyType)
                && validStdBlockSymKeyTypeForKeyLength(pKeyType, pKeyLen);
    }

    /**
     * Check standard block symKeyType.
     *
     * @param pKeyType the symKeyType
     * @param pKeyLen  the keyLength
     * @return true/false
     */
    public static boolean validStdBlockSymKeyTypeForKeyLength(final GordianNewSymKeyType pKeyType,
                                                              final GordianLength pKeyLen) {
        return validSymKeyTypeForKeyLength(pKeyType, pKeyLen)
                && GordianLength.LEN_128.equals(GordianCoreSymKeyType.getDefaultBlockLength(pKeyType));
    }

    /**
     * Check SymKeyType.
     *
     * @param pKeyType the symKeyType
     * @param pKeyLen  the keyLength
     * @return true/false
     */
    public static boolean validSymKeyTypeForKeyLength(final GordianNewSymKeyType pKeyType,
                                                      final GordianLength pKeyLen) {
        return GordianCoreSymKeyType.validForKeyLength(pKeyType, pKeyLen);
    }

    /**
     * Check SymKeyType.
     *
     * @param pKeyType the symKeyType
     * @return true/false
     */
    public boolean validSymKeyType(final GordianNewSymKeyType pKeyType) {
        return pKeyType != null;
    }

    /**
     * Check StreamKeyType.
     *
     * @param pKeyType the streamKeyType
     * @return true/false
     */
    public boolean validStreamKeyType(final GordianNewStreamKeyType pKeyType) {
        return pKeyType != null;
    }

    /**
     * Is Xof supported?
     *
     * @return true/false
     */
    public boolean isXofSupported() {
        return true;
    }
}
