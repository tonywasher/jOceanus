/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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

import java.security.spec.PKCS8EncodedKeySpec;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetRecipe.GordianKeySetParameters;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * A full set of symmetric keys, subject to the relevant predicate.
 */
public final class GordianKeySet {
    /**
     * Initialisation Vector size.
     */
    private static final int BLOCKLEN = GordianLength.LEN_128.getByteLength();

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * Map of KeySpec to symKey.
     */
    private final Map<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> theSymKeyMap;

    /**
     * Map of KeyType to streamKey.
     */
    private final Map<GordianStreamKeyType, GordianKey<GordianStreamKeyType>> theStreamKeyMap;

    /**
     * The underlying Cipher.
     */
    private final GordianMultiCipher theCipher;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected GordianKeySet(final GordianFactory pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Create maps */
        theSymKeyMap = new HashMap<>();
        theStreamKeyMap = new EnumMap<>(GordianStreamKeyType.class);

        /* Create the cipher */
        theCipher = new GordianMultiCipher(this);
    }

    /**
     * Obtain the factory.
     * @return a new MultiCipher
     */
    public GordianFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the symKeySet.
     * @return the keySet
     */
    protected Map<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> getSymKeyMap() {
        return theSymKeyMap;
    }

    /**
     * Obtain the streamKeySet.
     * @return the keySet
     */
    protected Map<GordianStreamKeyType, GordianKey<GordianStreamKeyType>> getStreamKeyMap() {
        return theStreamKeyMap;
    }

    /**
     * Encryption length.
     * @param pDataLength the length of data to be encrypted
     * @return the length of encrypted data
     */
    public static int getEncryptionLength(final int pDataLength) {
        final int iBlocks = 1 + ((pDataLength - 1) % BLOCKLEN);
        return iBlocks
               * BLOCKLEN;
    }

    /**
     * Encryption overhead.
     * @return the encryption overhead
     */
    public static int getEncryptionOverhead() {
        return GordianKeySetRecipe.SALTLEN + GordianKeySetRecipe.RECIPELEN;
    }

    /**
     * Obtain keyWrapExpansion for # of steps.
     * @param pNumSteps the number of wrap steps
     * @return the keyWrap expansion
     */
    public static int getKeyWrapExpansion(final int pNumSteps) {
        final int myExpansion = (BLOCKLEN * pNumSteps) >> 1;
        return myExpansion + getEncryptionOverhead();
    }

    /**
     * Encrypt bytes.
     * @param pBytes the bytes to encrypt
     * @return the encrypted bytes
     * @throws OceanusException on error
     */
    public byte[] encryptBytes(final byte[] pBytes) throws OceanusException {
        /* Generate set of keys and initialisation vector */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Encrypt the bytes */
        theCipher.initCiphers(myParams, true);
        final byte[] myBytes = theCipher.finish(pBytes);

        /* Package and return the encrypted bytes */
        final byte[] myCloud = myRecipe.buildExternal(theFactory, myBytes);
        final byte[] myClear = decryptBytes(myCloud);
        if (!Arrays.areEqual(pBytes, myClear)) {
            throw new GordianDataException("Help");
        }

        return myCloud;
    }

    /**
     * Decrypt bytes.
     * @param pBytes the bytes to decrypt
     * @return the decrypted bytes
     * @throws OceanusException on error
     */
    public byte[] decryptBytes(final byte[] pBytes) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, pBytes);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Decrypt the bytes and return them */
        theCipher.initCiphers(myParams, false);
        return theCipher.finish(myBytes);
    }

    /**
     * secure Key.
     * @param pKeyToSecure the key to wrap
     * @return the securedKey
     * @throws OceanusException on error
     */
    protected byte[] secureKey(final GordianKey<?> pKeyToSecure) throws OceanusException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* secure the key */
        final byte[] myBytes = theCipher.secureKey(myParams, pKeyToSecure);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(theFactory, myBytes);
    }

    /**
     * derive Key.
     * @param <T> the keyType class
     * @param pSecuredKey the secured key
     * @param pKeyType the key type
     * @return the derived key
     * @throws OceanusException on error
     */
    protected <T> GordianKey<T> deriveKey(final byte[] pSecuredKey,
                                          final T pKeyType) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, pSecuredKey);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the key and return it */
        return theCipher.deriveKey(myParams, myBytes, pKeyType);
    }

    /**
     * secure privateKey.
     * @param pKeyPair the keyPair to secure
     * @return the securedPrivateKey
     * @throws OceanusException on error
     */
    protected byte[] securePrivateKey(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Wrap the key */
        final byte[] myBytes = theCipher.securePrivateKey(myParams, pKeyPair);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(theFactory, myBytes);
    }

    /**
     * derive privateKeySpec.
     * @param pSecuredPrivateKey the secured privateKey
     * @return the privateKeySpec
     * @throws OceanusException on error
     */
    protected PKCS8EncodedKeySpec derivePrivateKeySpec(final byte[] pSecuredPrivateKey) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, pSecuredPrivateKey);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the key and return it */
        return theCipher.derivePrivateKeySpec(myParams, myBytes);
    }

    /**
     * derive externalId from type.
     * @param <T> the Type class
     * @param pType the type
     * @return the externalId
     * @throws OceanusException on error
     */
    public <T> long deriveExternalIdForType(final T pType) throws OceanusException {
        final GordianIdManager myManager = theFactory.getIdManager();
        return myManager.deriveExternalIdFromType(pType);
    }

    /**
     * derive Type from externalId.
     * @param <T> the Type class
     * @param pExternalId the externalId
     * @param pTypeClass the class
     * @return the type
     * @throws OceanusException on error
     */
    public <T> T deriveTypeFromExternalId(final long pExternalId,
                                          final Class<T> pTypeClass) throws OceanusException {
        final GordianIdManager myManager = theFactory.getIdManager();
        return myManager.deriveTypeFromExternalId(pExternalId, pTypeClass);
    }

    /**
     * Declare symmetricKey.
     * @param pKey the key
     * @throws OceanusException on error
     */
    public void declareSymKey(final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        declareKey(pKey, theFactory.supportedKeySetSymKeySpecs(), theSymKeyMap);
    }

    /**
     * Declare streamKey.
     * @param pKey the key
     * @throws OceanusException on error
     */
    public void declareStreamKey(final GordianKey<GordianStreamKeyType> pKey) throws OceanusException {
        declareKey(pKey, theFactory.supportedKeySetStreamKeyTypes(), theStreamKeyMap);
    }

    /**
     * Declare Key.
     * @param <T> the keyType
     * @param pKey the key
     * @param pPredicate the predicate
     * @param pKeyMap the keyMap
     * @throws OceanusException on error
     */
    private static <T> void declareKey(final GordianKey<T> pKey,
                                       final Predicate<T> pPredicate,
                                       final Map<T, GordianKey<T>> pKeyMap) throws OceanusException {
        /* Access keyType */
        final T myKeyType = pKey.getKeyType();

        /* Check that the key is supported */
        if (!pPredicate.test(myKeyType)) {
            throw new GordianDataException("invalid keyType");
        }

        /* Look for existing key of this type */
        final GordianKey<T> myExisting = pKeyMap.get(myKeyType);
        if (myExisting != null) {
            /* Must be same as existing key */
            if (!myExisting.equals(pKey)) {
                throw new GordianDataException("keyType already declared");
            }

            /* else new key */
        } else {
            /* Store into map */
            pKeyMap.put(myKeyType, pKey);
        }
    }

    /**
     * Build key set from secret.
     * @param pSecret the secret.
     * @param pInitVector the initialisation vector.
     * @throws OceanusException on error
     */
    protected void buildFromSecret(final byte[] pSecret,
                                   final byte[] pInitVector) throws OceanusException {
        /* Loop through the symmetricKeys values */
        final Predicate<GordianSymKeyType> mySymPredicate = theFactory.supportedKeySetSymKeyTypes();
        for (GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* If this is supported for a keySet */
            if (mySymPredicate.test(myType)) {
                /* Generate the key and add to map */
                final GordianSymKeySpec mySpec = new GordianSymKeySpec(myType);
                theSymKeyMap.put(mySpec, generateKey(mySpec, pSecret, pInitVector));
            }
        }

        /* Loop through the streamKeys values */
        final Predicate<GordianStreamKeyType> myPredicate = theFactory.supportedKeySetStreamKeyTypes();
        for (GordianStreamKeyType myType : GordianStreamKeyType.values()) {
            /* If this is supported for a keySet */
            if (myPredicate.test(myType)) {
                /* Generate the key and add to map */
                theStreamKeyMap.put(myType, generateKey(myType, pSecret, pInitVector));
            }
        }
    }

    /**
     * Generate key for a Key Type from the secret and initVector.
     * @param <T> the class of key
     * @param pKeyType the keyType
     * @param pSecret the derived Secret
     * @param pInitVector the initialisation vector.
     * @return the generated key
     * @throws OceanusException on error
     */
    private <T> GordianKey<T> generateKey(final T pKeyType,
                                          final byte[] pSecret,
                                          final byte[] pInitVector) throws OceanusException {
        /* Generate a new Secret Key from the secret */
        final GordianKeyGenerator<T> myGenerator = theFactory.getKeyGenerator(pKeyType);
        return myGenerator.generateKeyFromSecret(pSecret, pInitVector);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof GordianKeySet)) {
            return false;
        }

        /* Access the target field */
        final GordianKeySet myThat = (GordianKeySet) pThat;

        /* Check differences */
        return theFactory.equals(myThat.getFactory())
               && theSymKeyMap.equals(myThat.getSymKeyMap())
               && theStreamKeyMap.equals(myThat.getStreamKeyMap());
    }

    @Override
    public int hashCode() {
        return GordianFactory.HASH_PRIME * theFactory.hashCode()
               + theSymKeyMap.hashCode()
               + theStreamKeyMap.hashCode();
    }
}
