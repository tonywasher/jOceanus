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

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;

import net.sourceforge.joceanus.jgordianknot.JGordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.JGordianDataException;
import net.sourceforge.joceanus.jgordianknot.JGordianLogicException;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityRegister.AsymmetricRegister;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Asymmetric Key class. Note that the RSA asymmetric key cannot be used for bulk encryption due to limitations in the RSA implementation. The Asymmetric Keys
 * should only be used for Signatures and Wrapping keys.
 */
public class AsymmetricKey {
    /**
     * Cipher initialisation failure.
     */
    private static final String ERROR_CIPHER = "Failed to initialise Cipher";

    /**
     * Invalid elliptic partner error.
     */
    private static final String ERROR_ELPARTNER = "Shared Keys require both partners to be similar Elliptic";

    /**
     * Invalid partner error.
     */
    private static final String ERROR_PARTNER = "Asymmetric Encryption must be between similar partners";

    /**
     * Encoded Size for Public Keys.
     */
    public static final int PUBLICSIZE = 512;

    /**
     * Encrypted Size for Private Keys.
     */
    public static final int PRIVATESIZE = 1568;

    /**
     * The Public/Private Key Pair.
     */
    private final KeyPair theKeyPair;

    /**
     * The Key Type.
     */
    private final AsymKeyType theKeyType;

    /**
     * The security generator.
     */
    private final SecurityGenerator theGenerator;

    /**
     * The Key Agreement object.
     */
    private KeyAgreement theKeyAgreement = null;

    /**
     * The External Public Definition.
     */
    private final byte[] theExternalPublic;

    /**
     * The External Private Definition.
     */
    private final byte[] theExternalPrivate;

    /**
     * The Encoded Public Key.
     */
    private final byte[] thePublicKeyDef;

    /**
     * The Encoded Private Key.
     */
    private final byte[] thePrivateKeyDef;

    /**
     * The SaltBytes.
     */
    private final byte[] theSaltBytes;

    /**
     * The CipherSet.
     */
    private CipherSet theCipherSet = null;

    /**
     * The CipherSet map.
     */
    private final Map<AsymmetricKey, CipherSet> theCipherMap;

    /**
     * Obtain the Asymmetric Key type.
     * @return the key type
     */
    public AsymKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Is the Asymmetric Key a public only key.
     * @return true/false
     */
    public boolean isPublicOnly() {
        return theKeyPair.getPrivate() == null;
    }

    /**
     * Obtain the Private Key.
     * @return the private key
     */
    private PrivateKey getPrivateKey() {
        return theKeyPair.getPrivate();
    }

    /**
     * Obtain the Public Key.
     * @return the private key
     */
    protected PublicKey getPublicKey() {
        return theKeyPair.getPublic();
    }

    /**
     * Obtain the External Public Key definition.
     * @return the key definition
     */
    public byte[] getExternalPublic() {
        return Arrays.copyOf(theExternalPublic, theExternalPublic.length);
    }

    /**
     * Obtain the External Private Key definition.
     * @return the key definition
     */
    protected byte[] getExternalPrivate() {
        return Arrays.copyOf(theExternalPrivate, theExternalPrivate.length);
    }

    /**
     * AsymmetricKey Generator.
     * @param pGenerator the security generator
     * @return the new AsymmetricKey
     * @throws JOceanusException on error
     */
    protected static AsymmetricKey generateAsymmetricKey(final SecurityGenerator pGenerator) throws JOceanusException {
        /* Access random generator */
        SecureRandom myRandom = pGenerator.getRandom();
        AsymKeyType[] myType = AsymKeyType.getRandomTypes(1, myRandom);

        /* Generate a AsymmetricKey for the AsymKey type */
        return generateAsymmetricKey(pGenerator, myType[0]);
    }

    /**
     * AsymmetricKey Generator for Elliptic only.
     * @param pGenerator the security generator
     * @return the new AsymmetricKey
     * @throws JOceanusException on error
     */
    protected static AsymmetricKey generateEllipticAsymmetricKey(final SecurityGenerator pGenerator) throws JOceanusException {
        /* Access random generator */
        SecureRandom myRandom = pGenerator.getRandom();
        AsymKeyType[] myType = AsymKeyType.getRandomTypes(1, myRandom, true);

        /* Generate a AsymmetricKey for the AsymKey type */
        return generateAsymmetricKey(pGenerator, myType[0]);
    }

    /**
     * AsymmetricKey Generator.
     * @param pGenerator the security generator
     * @param pKeyType the Asymmetric Key type
     * @return the new AsymmetricKey
     * @throws JOceanusException on error
     */
    protected static AsymmetricKey generateAsymmetricKey(final SecurityGenerator pGenerator,
                                                         final AsymKeyType pKeyType) throws JOceanusException {
        /* Obtain the registration */
        SecurityRegister myRegister = pGenerator.getRegister();
        AsymmetricRegister myReg = myRegister.getAsymRegistration(pKeyType);

        /* Generate the KeyPair */
        KeyPair myPair = myReg.generateKeyPair();

        /* Generate a AsymmetricKey for the AsymKey type */
        return new AsymmetricKey(pGenerator, pKeyType, myPair);
    }

    /**
     * Constructor for new key.
     * @param pGenerator the security generator
     * @param pKeyType the key type
     * @param pPair the key pair
     * @throws JOceanusException on error
     */
    private AsymmetricKey(final SecurityGenerator pGenerator,
                          final AsymKeyType pKeyType,
                          final KeyPair pPair) throws JOceanusException {
        /* Store the key mode and the generator */
        theKeyType = pKeyType;
        theGenerator = pGenerator;
        theKeyPair = pPair;

        /* Access the encoded formats */
        thePrivateKeyDef = getPrivateKey().getEncoded();
        thePublicKeyDef = getPublicKey().getEncoded();

        /* Determine the external PublicKey definition */
        int myLen = thePublicKeyDef.length;
        theExternalPublic = new byte[myLen + 1];
        System.arraycopy(thePublicKeyDef, 0, theExternalPublic, 1, myLen);
        theExternalPublic[0] = (byte) theKeyType.getId();

        /* If the key is elliptic */
        if (theKeyType.isElliptic()) {
            /* Create cipher key and SaltBytes */
            theCipherMap = new HashMap<AsymmetricKey, CipherSet>();
            theSaltBytes = theGenerator.getRandomBytes(HashKey.INITVECTOR_LEN);

            /* Build external private */
            myLen = thePrivateKeyDef.length
                    + HashKey.INITVECTOR_LEN;
            theExternalPrivate = new byte[myLen];
            System.arraycopy(theSaltBytes, 0, theExternalPrivate, 0, HashKey.INITVECTOR_LEN);
            System.arraycopy(thePrivateKeyDef, 0, theExternalPrivate, HashKey.INITVECTOR_LEN, thePrivateKeyDef.length);

            /* else non-elliptic */
        } else {
            /* No need for cipherMap/saltBytes or different Private KeyDef */
            theCipherMap = null;
            theSaltBytes = null;
            theExternalPrivate = thePrivateKeyDef;
        }

        /* Check whether the PublicKey is too large */
        if (theExternalPublic.length > PUBLICSIZE) {
            throw new JGordianDataException("PublicKey too large: "
                                            + theExternalPublic.length);
        }
    }

    /**
     * Constructor from public KeySpec.
     * @param pGenerator the security generator
     * @param pExternalPublic the public KeySpec
     * @throws JOceanusException on error
     */
    protected AsymmetricKey(final SecurityGenerator pGenerator,
                            final byte[] pExternalPublic) throws JOceanusException {
        /* Store the key mode and the generator */
        theGenerator = pGenerator;
        thePrivateKeyDef = null;
        theExternalPrivate = null;
        theSaltBytes = null;
        theCipherMap = null;

        /* Obtain KeyType and Public KeyDef from ExternalPublic */
        theKeyType = AsymKeyType.fromId(pExternalPublic[0]);
        theExternalPublic = Arrays.copyOf(pExternalPublic, pExternalPublic.length);
        thePublicKeyDef = Arrays.copyOfRange(pExternalPublic, 1, pExternalPublic.length);

        /* Obtain the registration */
        SecurityRegister myRegister = pGenerator.getRegister();
        AsymmetricRegister myReg = myRegister.getAsymRegistration(theKeyType);

        /* Derive the KeyPair */
        theKeyPair = myReg.deriveKeyPair(null, thePublicKeyDef);
    }

    /**
     * Constructor from full specification.
     * @param pGenerator the security generator
     * @param pExternalPrivate the private KeySpec
     * @param pExternalPublic the public KeySpec
     * @throws JOceanusException on error
     */
    protected AsymmetricKey(final SecurityGenerator pGenerator,
                            final byte[] pExternalPrivate,
                            final byte[] pExternalPublic) throws JOceanusException {
        /* Store the key mode and the generator */
        theGenerator = pGenerator;

        /* Obtain KeyType and Public KeyDef from ExternalPublic */
        theKeyType = AsymKeyType.fromId(pExternalPublic[0]);
        theExternalPublic = Arrays.copyOf(pExternalPublic, pExternalPublic.length);
        thePublicKeyDef = Arrays.copyOfRange(pExternalPublic, 1, pExternalPublic.length);
        theExternalPrivate = Arrays.copyOf(pExternalPrivate, pExternalPrivate.length);

        /* If the key is elliptic */
        if (theKeyType.isElliptic()) {
            /* Obtain private keyDef and saltBytes */
            thePrivateKeyDef = Arrays.copyOfRange(pExternalPrivate, HashKey.INITVECTOR_LEN, pExternalPrivate.length);
            theSaltBytes = Arrays.copyOf(pExternalPrivate, HashKey.INITVECTOR_LEN);
            theCipherMap = new HashMap<AsymmetricKey, CipherSet>();

            /* else non-elliptic */
        } else {
            /* No need for cipherMap/saltBytes or different Private KeyDef */
            thePrivateKeyDef = Arrays.copyOf(pExternalPrivate, pExternalPrivate.length);
            theCipherMap = null;
            theSaltBytes = null;
        }

        /* Obtain the registration */
        SecurityRegister myRegister = pGenerator.getRegister();
        AsymmetricRegister myReg = myRegister.getAsymRegistration(theKeyType);

        /* Derive the KeyPair */
        theKeyPair = myReg.deriveKeyPair(thePrivateKeyDef, thePublicKeyDef);
    }

    @Override
    public int hashCode() {
        /* Calculate and return the hashCode for this asymmetric key */
        int hashCode = 1;
        if (thePrivateKeyDef != null) {
            hashCode += Arrays.hashCode(thePrivateKeyDef);
        }
        hashCode *= SecurityGenerator.HASH_PRIME;
        hashCode += theKeyType.getId();
        hashCode *= SecurityGenerator.HASH_PRIME;
        hashCode += Arrays.hashCode(thePublicKeyDef);
        return hashCode;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is an Asymmetric Key */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Key */
        AsymmetricKey myThat = (AsymmetricKey) pThat;

        /* Not equal if different types */
        if (theKeyType != myThat.getKeyType()) {
            return false;
        }

        /* Ensure that the private/public keys are identical */
        if (!Arrays.equals(myThat.thePrivateKeyDef, thePrivateKeyDef)) {
            return false;
        }
        return Arrays.equals(myThat.thePublicKeyDef, thePublicKeyDef);
    }

    /**
     * Get CipherSet for partner Elliptic Curve.
     * @param pPartner partner asymmetric key
     * @param pSaltBytes the salt bytes
     * @return the new CipherSet
     * @throws JOceanusException on error
     */
    public CipherSet getCipherSet(final AsymmetricKey pPartner,
                                  final byte[] pSaltBytes) throws JOceanusException {
        /* Both keys must be elliptic */
        if ((!theKeyType.isElliptic())
            || (pPartner.getKeyType() != theKeyType)) {
            throw new JGordianLogicException(ERROR_ELPARTNER);
        }

        /* Look for an already resolved CipherSet */
        CipherSet mySet = theCipherMap.get(pPartner);

        /* Return it if found */
        if (mySet != null) {
            return mySet;
        }

        /* Access the Shared Secret */
        byte[] mySecret = getSharedSecret(pPartner);

        /* Build the CipherSet */
        mySet = new CipherSet(theGenerator, pSaltBytes);

        /* Apply the Secret */
        mySet.buildCiphers(mySecret);

        /* Store the Set into the map */
        theCipherMap.put(pPartner, mySet);

        /* Return the Cipher Set */
        return mySet;
    }

    /**
     * Get CipherSet for internal Elliptic Curve.
     * @return the cipher set
     * @throws JOceanusException on error
     */
    public CipherSet getCipherSet() throws JOceanusException {
        /* Return PreExisting set */
        if (theCipherSet != null) {
            return theCipherSet;
        }

        /* Build the internal CipherSet */
        theCipherSet = getCipherSet(this, theSaltBytes);

        /* Return the Cipher Set */
        return theCipherSet;
    }

    /**
     * Obtain cipher.
     * @param bWrap initialise cipher for wrap true/false?
     * @return the Stream Cipher
     * @throws JOceanusException on error
     */
    private Cipher getCipher(final boolean bWrap) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Create a new cipher */
            return Cipher.getInstance(bWrap
                    ? theKeyType.getAlgorithm()
                    : theKeyType.getCipher(), theGenerator.getProviderName());

            /* catch exceptions */
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            throw new JGordianCryptoException(ERROR_CIPHER, e);
        }
    }

    /**
     * derive a SymmetricKey from secured key definition.
     * @param pSecuredKeyDef the secured key definition
     * @param pKeyType the key type
     * @return the Symmetric key
     * @throws JOceanusException on error
     */
    public SymmetricKey deriveSymmetricKey(final byte[] pSecuredKeyDef,
                                           final SymKeyType pKeyType) throws JOceanusException {
        SymmetricKey mySymKey;

        /* Cannot unwrap unless we have the private key */
        if (isPublicOnly()) {
            throw new JGordianLogicException("Cannot unwrap without private key");
        }

        /* Protect against exceptions */
        try {
            /* If we are elliptic */
            if (theKeyType.isElliptic()) {
                /* Access the internal CipherSet */
                CipherSet mySet = getCipherSet();

                /* Unwrap the Key */
                mySymKey = mySet.deriveSymmetricKey(pSecuredKeyDef, pKeyType);

                /* else we use RAS semantics */
            } else {
                /* Initialise the cipher */
                Cipher myCipher = getCipher(true);
                myCipher.init(Cipher.UNWRAP_MODE, getPrivateKey());

                /* unwrap the key */
                SecretKey myKey = (SecretKey) myCipher.unwrap(pSecuredKeyDef, pKeyType.getAlgorithm(), Cipher.SECRET_KEY);

                /* Build the symmetric key */
                mySymKey = new SymmetricKey(theGenerator, pKeyType, myKey);
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new JGordianCryptoException("Failed to unwrap key", e);
        }

        /* Return the new key */
        return mySymKey;
    }

    /**
     * Get the Secured Key Definition for a Symmetric Key.
     * @param pKey the Symmetric Key to secure
     * @return the secured key definition
     * @throws JOceanusException on error
     */
    public byte[] secureSymmetricKey(final SymmetricKey pKey) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* If we are elliptic */
            if (theKeyType.isElliptic()) {
                /* Access the internal CipherSet */
                CipherSet mySet = getCipherSet();

                /* Wrap the Key */
                return mySet.secureSymmetricKey(pKey);

                /* else we are using RSA semantics */
            } else {
                /* Initialise the cipher */
                Cipher myCipher = getCipher(true);
                myCipher.init(Cipher.WRAP_MODE, getPublicKey());

                /* wrap the key */
                return myCipher.wrap(pKey.getSecretKey());
            }

        } catch (InvalidKeyException | IllegalBlockSizeException e) {
            throw new JGordianCryptoException("Failed to wrap key", e);
        }
    }

    /**
     * Obtain shared secret for partner Asymmetric Key.
     * @param pPartner partner asymmetric key
     * @return the shared secret
     * @throws JOceanusException on error
     */
    private synchronized byte[] getSharedSecret(final AsymmetricKey pPartner) throws JOceanusException {
        /* Both keys must be elliptic */
        if ((!theKeyType.isElliptic())
            || (theKeyType != pPartner.getKeyType())) {
            throw new JGordianLogicException(ERROR_ELPARTNER);
        }

        /* Cannot generate unless we have the private key */
        if (isPublicOnly()) {
            throw new JGordianLogicException("Cannot generate secret without private key");
        }

        /* Protect against exceptions */
        try {
            /* If we do not currently have a key Agreement */
            if (theKeyAgreement == null) {
                /* Create the key agreement */
                theKeyAgreement = KeyAgreement.getInstance("ECDH", theGenerator.getProviderName());
            }

            /* Process the key agreement */
            theKeyAgreement.init(getPrivateKey());
            theKeyAgreement.doPhase(pPartner.getPublicKey(), true);

            /* Generate the secret */
            return theKeyAgreement.generateSecret();

            /* Handle exceptions */
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new JGordianCryptoException("Failed to negotiate key agreement", e);
        }
    }

    /**
     * Obtain a signature for this key.
     * @param bSign initialise for signature rather than verify
     * @return the signature object
     * @throws JOceanusException on error
     */
    public Signature getSignature(final boolean bSign) throws JOceanusException {
        /* Cannot sign unless we have the private key */
        if ((bSign)
            && (isPublicOnly())) {
            throw new JGordianLogicException("Cannot sign without private key");
        }

        /* Protect against exceptions */
        try {
            /* Create a signature */
            Signature mySignature = Signature.getInstance(theKeyType.getSignature(), theGenerator.getProviderName());
            if (bSign) {
                mySignature.initSign(getPrivateKey(), theGenerator.getRandom());
            } else {
                mySignature.initVerify(getPublicKey());
            }

            /* Complete the signature */
            return mySignature;

            /* Catch exceptions */
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new JGordianCryptoException("Exception building signature", e);
        }
    }

    /**
     * Encrypt bytes.
     * @param pBytes bytes to encrypt
     * @param pSaltBytes the salt bytes
     * @param pTarget target partner of encryption
     * @return Encrypted bytes
     * @throws JOceanusException on error
     */
    public byte[] encryptBytes(final byte[] pBytes,
                               final byte[] pSaltBytes,
                               final AsymmetricKey pTarget) throws JOceanusException {
        /* Target must be identical key type */
        if (theKeyType != pTarget.getKeyType()) {
            throw new JGordianLogicException(ERROR_PARTNER);
        }

        /* If we are elliptic */
        if (theKeyType.isElliptic()) {
            /* Access the target CipherSet */
            CipherSet mySet = getCipherSet(pTarget, pSaltBytes);

            /* Encrypt the string */
            return mySet.encryptBytes(pBytes);

            /* else handle RSA semantics */
        } else {
            return encryptStandardBytes(pBytes, pTarget);
        }

    }

    /**
     * Encrypt Standard bytes.
     * @param pBytes bytes to encrypt
     * @param pTarget target partner of encryption
     * @return Encrypted bytes
     * @throws JOceanusException on error
     */
    private byte[] encryptStandardBytes(final byte[] pBytes,
                                        final AsymmetricKey pTarget) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Create the cipher */
            Cipher myCipher = getCipher(false);
            myCipher.init(Cipher.ENCRYPT_MODE, pTarget.getPublicKey());

            /* Determine the block sizes */
            int iBlockSize = myCipher.getBlockSize();
            int iOutSize = myCipher.getOutputSize(iBlockSize);

            /* Determine the number of blocks */
            int iDataLen = pBytes.length;
            int iNumBlocks = 1 + ((iDataLen - 1) / iBlockSize);

            /* Allocate the output buffer */
            byte[] myOutput = new byte[iNumBlocks
                                       * iOutSize];

            /* Initialise offsets */
            int iOffset = 0;
            int iOutOffs = 0;

            /* Loop through the bytes in units of iBlockSize */
            while (iDataLen > 0) {
                /* Determine the length of data to process */
                int iNumBytes = iDataLen;
                if (iNumBytes > iBlockSize) {
                    iNumBytes = iBlockSize;
                }

                /* Encrypt the data */
                iOutSize = myCipher.doFinal(pBytes, iOffset, iNumBytes, myOutput, iOutOffs);

                /* Adjust offsets */
                iDataLen -= iNumBytes;
                iOffset += iNumBytes;
                iOutOffs += iOutSize;
            }

            /* Adjust output array correctly */
            if (iOutOffs < myOutput.length) {
                myOutput = Arrays.copyOf(myOutput, iOutOffs);
            }

            /* Return to caller */
            return myOutput;
        } catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new JGordianCryptoException(e.getMessage(), e);
        }
    }

    /**
     * Decrypt bytes.
     * @param pBytes bytes to decrypt
     * @param pSaltBytes the salt bytes
     * @param pSource source partner of encryption
     * @return Decrypted bytes
     * @throws JOceanusException on error
     */
    public byte[] decryptBytes(final byte[] pBytes,
                               final byte[] pSaltBytes,
                               final AsymmetricKey pSource) throws JOceanusException {
        /* Cannot decrypt unless we have the private key */
        if (isPublicOnly()) {
            throw new JGordianLogicException("Cannot decrypt without private key");
        }

        /* Source must be identical key type */
        if (theKeyType != pSource.getKeyType()) {
            throw new JGordianLogicException(ERROR_PARTNER);
        }

        /* If we are elliptic */
        if (theKeyType.isElliptic()) {
            /* Access the required CipherSet */
            CipherSet mySet = getCipherSet(pSource, pSaltBytes);

            /* Decrypt the string */
            return mySet.decryptBytes(pBytes);

            /* else handle RSA semantics */
        } else {
            return decryptStandardBytes(pBytes);
        }
    }

    /**
     * Decrypt Standard bytes.
     * @param pBytes bytes to decrypt
     * @return Decrypted bytes
     * @throws JOceanusException on error
     */
    private byte[] decryptStandardBytes(final byte[] pBytes) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Create the cipher */
            Cipher myCipher = getCipher(false);
            myCipher.init(Cipher.DECRYPT_MODE, getPrivateKey());

            /* Determine the block sizes */
            int iBlockSize = myCipher.getBlockSize();
            int iOutSize = myCipher.getOutputSize(iBlockSize);

            /* Determine the number of blocks */
            int iDataLen = pBytes.length;
            int iNumBlocks = 1 + ((iDataLen - 1) / iBlockSize);

            /* Allocate the output buffer */
            byte[] myOutput = new byte[iNumBlocks
                                       * iOutSize];

            /* Initialise offsets */
            int iOffset = 0;
            int iOutOffs = 0;

            /* Loop through the bytes in units of iBlockSize */
            while (iDataLen > 0) {
                /* Determine the length of data to process */
                int iNumBytes = iDataLen;
                if (iNumBytes > iBlockSize) {
                    iNumBytes = iBlockSize;
                }

                /* Encrypt the data */
                iOutSize = myCipher.doFinal(pBytes, iOffset, iNumBytes, myOutput, iOutOffs);

                /* Adjust offsets */
                iDataLen -= iNumBytes;
                iOffset += iNumBytes;
                iOutOffs += iOutSize;
            }

            /* Adjust output array correctly */
            if (iOutOffs < myOutput.length) {
                myOutput = Arrays.copyOf(myOutput, iOutOffs);
            }

            /* Create the string */
            return myOutput;
        } catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new JGordianCryptoException(e.getMessage(), e);
        }
    }
}