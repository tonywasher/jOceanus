/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.base;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Validator classes.
 */
public class GordianValidator {
    /**
     * Obtain predicate for supported hMac digestTypes.
     * @return the predicate
     */
    public Predicate<GordianDigestType> supportedHMacDigestTypes() {
        return this::validHMacDigestType;
    }

    /**
     * Check HMacDigestType.
     * @param pDigestType the digestType
     * @return true/false
     */
    protected boolean validHMacDigestType(final GordianDigestType pDigestType) {
        return validDigestType(pDigestType) && pDigestType.supportsLargeData();
    }

    /**
     * Check DigestType.
     * @param pDigestType the digestType
     * @return true/false
     */
    public boolean validDigestType(final GordianDigestType pDigestType) {
        return true;
    }

    /**
     * Determine whether the digestType is valid for PasswordLock.
     * @return the predicate
     */
    public Predicate<GordianDigestType> supportedLockDigestTypes() {
        return supportedHMacDigestTypes().and(isCombinedHashDigest());
    }

    /**
     * Determine whether the digestType is valid for KeyGenLock.
     * @return the predicate
     */
    public Predicate<GordianDigestType> supportedKeyGenDigestTypes() {
        return supportedHMacDigestTypes().and(isExternalHashDigest());
    }

    /**
     * Determine whether the digestType is valid for agreement.
     * @return the predicate
     */
    public Predicate<GordianDigestType> supportedAgreementDigestTypes() {
        return supportedHMacDigestTypes();
    }

    /**
     * Determine whether the digestType is an external hash.
     * @return the predicate
     */
    private Predicate<GordianDigestType> isCombinedHashDigest() {
        return s -> s.getDefaultLength().getLength() >= GordianLength.LEN_256.getLength()
                && s.supportsLargeData();
    }

    /**
     * Determine whether the digestType is an external hash.
     * @return the predicate
     */
    public Predicate<GordianDigestType> isExternalHashDigest() {
        return s -> s.isLengthValid(GordianLength.LEN_512);
    }

    /**
     * Obtain a list of external digestTypes.
     * @return the list of supported digestTypes.
     */
    public List<GordianDigestType> listAllExternalDigestTypes() {
        return Arrays.stream(GordianDigestType.values())
                .filter(supportedExternalDigestTypes())
                .toList();
    }

    /**
     * Obtain predicate for supported external digests.
     * @return the predicate
     */
    public Predicate<GordianDigestType> supportedExternalDigestTypes() {
        return supportedHMacDigestTypes().and(isExternalHashDigest());
    }

    /**
     * Obtain predicate for supported keySet symKeySpecs.
     * @param pKeyLen the keyLength
     * @return the predicate
     */
    public Predicate<GordianSymKeySpec> supportedKeySetSymKeySpecs(final GordianLength pKeyLen) {
        return s -> supportedKeySetSymKeyTypes(pKeyLen).test(s.getSymKeyType())
                && s.getBlockLength() == GordianLength.LEN_128;
    }

    /**
     * Obtain predicate for keySet SymKeyTypes.
     * @param pKeyLen the keyLength
     * @return the predicate
     */
    public Predicate<GordianSymKeyType> supportedKeySetSymKeyTypes(final GordianLength pKeyLen) {
        return t -> validKeySetSymKeyType(t, pKeyLen);
    }

    /**
     * check valid keySet symKeyType.
     * @param pKeyType the symKeyType
     * @param pKeyLen the keyLength
     * @return true/false
     */
    private boolean validKeySetSymKeyType(final GordianSymKeyType pKeyType,
                                          final GordianLength pKeyLen) {
        return validSymKeyType(pKeyType)
                && validStdBlockSymKeyTypeForKeyLength(pKeyType, pKeyLen);
    }

    /**
     * Check standard block symKeyType.
     * @param pKeyType the symKeyType
     * @param pKeyLen the keyLength
     * @return true/false
     */
    public static boolean validStdBlockSymKeyTypeForKeyLength(final GordianSymKeyType pKeyType,
                                                              final GordianLength pKeyLen) {
        return validSymKeyTypeForKeyLength(pKeyType, pKeyLen)
                && pKeyType.getDefaultBlockLength().equals(GordianLength.LEN_128);
    }

    /**
     * Check SymKeyType.
     * @param pKeyType the symKeyType
     * @param pKeyLen the keyLength
     * @return true/false
     */
    public static boolean validSymKeyTypeForKeyLength(final GordianSymKeyType pKeyType,
                                                      final GordianLength pKeyLen) {
        return pKeyType.validForKeyLength(pKeyLen);
    }

    /**
     * Check SymKeyType.
     * @param pKeyType the symKeyType
     * @return true/false
     */
    public boolean validSymKeyType(final GordianSymKeyType pKeyType) {
        return pKeyType != null;
    }

    /**
     * Check StreamKeyType.
     * @param pKeyType the streamKeyType
     * @return true/false
     */
    public boolean validStreamKeyType(final GordianStreamKeyType pKeyType) {
        return pKeyType != null;
    }

    /**
     * Is Xof supported?
     * @return true/false
     */
    public boolean isXofSupported() {
        return true;
    }
}
