/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JGordianKnot;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import net.sourceforge.JDataManager.DataConverter;
import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import net.sourceforge.JDataManager.ReportObject.ReportDetail;
import net.sourceforge.JGordianKnot.DataHayStack.AsymModeNeedle;
import net.sourceforge.JGordianKnot.DataHayStack.SymKeyNeedle;
import net.sourceforge.JGordianKnot.ZipFile.ZipFileEntry;

/**
 * Asymmetric Key class. Note that the RSA asymmetric key cannot be used for bulk encryption due to
 * limitations in the RSA implementation. The Asymmetric Keys should only be used for Signatures and Wrapping
 * keys.
 */
public class AsymmetricKey implements ReportDetail {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(AsymmetricKey.class.getSimpleName());

    /* Field IDs */
    public static final ReportField FIELD_KEYMODE = theFields.declareLocalField("KeyMode");
    public static final ReportField FIELD_CIPHERMAP = theFields.declareLocalField("CipherMap");
    public static final ReportField FIELD_SYMKEYMAP = theFields.declareLocalField("SymKeyMap");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_KEYMODE)
            return theKeyMode;
        if (pField == FIELD_CIPHERMAP)
            return theCipherMap;
        if (pField == FIELD_SYMKEYMAP)
            return theSymKeyMap;
        return null;
    }

    @Override
    public String getObjectSummary() {
        return "AsymmetricKey(" + theKeyType + ")";
    }

    /**
     * Encoded Size for Public Keys
     */
    public static final int PUBLICSIZE = 512;

    /**
     * Encrypted Size for Private Keys
     */
    public static final int PRIVATESIZE = 1280;

    /**
     * The Public/Private Key Pair
     */
    private final KeyPair theKeyPair;

    /**
     * The Key Mode
     */
    private final AsymKeyMode theKeyMode;

    /**
     * The Key Type
     */
    private final AsymKeyType theKeyType;

    /**
     * The security generator
     */
    private final SecurityGenerator theGenerator;

    /**
     * The Key Agreement object
     */
    private KeyAgreement theKeyAgreement = null;

    /**
     * The External Definition
     */
    private final byte[] theExternalKeyDef;

    /**
     * The Encoded Public Key
     */
    private final byte[] thePublicKeyDef;

    /**
     * The Encoded Private Key
     */
    private final byte[] thePrivateKeyDef;

    /**
     * The CipherSet
     */
    private CipherSet theCipherSet = null;

    /**
     * The CipherSet map
     */
    private final Map<AsymmetricKey, CipherSet> theCipherMap;

    /**
     * The Symmetric Key Map
     */
    private final Map<SymmetricKey, byte[]> theSymKeyMap;

    /**
     * Obtain the Asymmetric Key type
     * @return the key type
     */
    public AsymKeyType getKeyType() {
        return theKeyMode.getAsymKeyType();
    }

    /**
     * Obtain the Asymmetric Key mode
     * @return the key mode
     */
    public AsymKeyMode getKeyMode() {
        return theKeyMode;
    }

    /**
     * Is the Asymmetric Key a public only key
     * @return true/false
     */
    public boolean isPublicOnly() {
        return (theKeyPair.getPrivate() == null);
    }

    /**
     * Obtain the Private Key
     * @return the private key
     */
    protected PrivateKey getPrivateKey() {
        return theKeyPair.getPrivate();
    }

    /**
     * Obtain the Public Key
     * @return the private key
     */
    protected PublicKey getPublicKey() {
        return theKeyPair.getPublic();
    }

    /**
     * Obtain the External Key definition
     * @return the key definition
     */
    public byte[] getExternalDef() {
        return theExternalKeyDef;
    }

    /**
     * Constructor for new key
     * @param pGenerator the security generator
     * @param pKeyMode the key mode
     * @throws ModelException
     */
    protected AsymmetricKey(SecurityGenerator pGenerator,
                            AsymKeyMode pKeyMode) throws ModelException {
        /* Store the key mode and the generator */
        theKeyMode = pKeyMode;
        theKeyType = theKeyMode.getAsymKeyType();
        theGenerator = pGenerator;

        /* Generate the new KeyPair */
        theKeyPair = theGenerator.generateKeyPair(theKeyType);

        /* Access the encoded formats */
        thePrivateKeyDef = getPrivateKey().getEncoded();
        thePublicKeyDef = getPublicKey().getEncoded();

        /* Determine the external definition */
        AsymModeNeedle myNeedle = new AsymModeNeedle(theKeyMode, thePublicKeyDef);
        theExternalKeyDef = myNeedle.getExternal();

        /* Create the map for elliptic keys */
        if (theKeyType.isElliptic())
            theCipherMap = new HashMap<AsymmetricKey, CipherSet>();
        else
            theCipherMap = null;

        /* Build the SymmetricKey map */
        theSymKeyMap = new HashMap<SymmetricKey, byte[]>();

        /* Check whether the PublicKey is too large */
        if (theExternalKeyDef.length > PUBLICSIZE)
            throw new ModelException(ExceptionClass.DATA, "PublicKey too large: " + theExternalKeyDef.length);
    }

    /**
     * Constructor from public KeySpec
     * @param pGenerator the security generator
     * @param pKeySpec the public KeySpec
     * @throws ModelException
     */
    protected AsymmetricKey(SecurityGenerator pGenerator,
                            byte[] pKeySpec) throws ModelException {
        /* Parse the KeySpec */
        AsymModeNeedle myNeedle = new AsymModeNeedle(pKeySpec);

        /* Store the key mode and the generator */
        theKeyMode = myNeedle.getAsymKeyMode();
        theKeyType = theKeyMode.getAsymKeyType();
        theGenerator = pGenerator;
        theExternalKeyDef = pKeySpec;

        /* Derive the KeyPair */
        theKeyPair = theGenerator.deriveKeyPair(theKeyType, null, myNeedle.getPublicKey());

        /* Access the encoded formats */
        thePublicKeyDef = getPublicKey().getEncoded();
        thePrivateKeyDef = null;

        /* Create the map for elliptic keys */
        if (theKeyType.isElliptic())
            theCipherMap = new HashMap<AsymmetricKey, CipherSet>();
        else
            theCipherMap = null;

        /* Build the SymmetricKey map */
        theSymKeyMap = new HashMap<SymmetricKey, byte[]>();
    }

    /**
     * Constructor from full specification
     * @param pGenerator the security generator
     * @param pPrivateKey the private KeySpec
     * @param pKeySpec the public KeySpec
     * @throws ModelException
     */
    protected AsymmetricKey(SecurityGenerator pGenerator,
                            byte[] pPrivateKey,
                            byte[] pKeySpec) throws ModelException {
        /* Parse the KeySpec */
        AsymModeNeedle myNeedle = new AsymModeNeedle(pKeySpec);

        /* Store the key mode and the generator */
        theKeyMode = myNeedle.getAsymKeyMode();
        theKeyType = theKeyMode.getAsymKeyType();
        theGenerator = pGenerator;
        theExternalKeyDef = pKeySpec;

        /* Derive the KeyPair */
        theKeyPair = theGenerator.deriveKeyPair(theKeyType, pPrivateKey, myNeedle.getPublicKey());

        /* Access the encoded formats */
        thePrivateKeyDef = getPrivateKey().getEncoded();
        thePublicKeyDef = getPublicKey().getEncoded();

        /* Create the map for elliptic keys */
        if (theKeyType.isElliptic())
            theCipherMap = new HashMap<AsymmetricKey, CipherSet>();
        else
            theCipherMap = null;

        /* Build the SymmetricKey map */
        theSymKeyMap = new HashMap<SymmetricKey, byte[]>();
    }

    @Override
    public int hashCode() {
        /* Calculate and return the hashCode for this asymmetric key */
        int hashCode = 19 * thePublicKeyDef.hashCode();
        if (thePrivateKeyDef != null)
            hashCode += thePrivateKeyDef.hashCode();
        hashCode *= 19;
        hashCode += theKeyMode.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is an Asymmetric Key */
        if (pThat.getClass() != this.getClass())
            return false;

        /* Access the target Key */
        AsymmetricKey myThat = (AsymmetricKey) pThat;

        /* Not equal if different modes */
        if (!myThat.getKeyMode().equals(theKeyMode))
            return false;

        /* Ensure that the private/public keys are identical */
        if (!Arrays.equals(myThat.thePrivateKeyDef, thePrivateKeyDef))
            return false;
        if (!Arrays.equals(myThat.thePublicKeyDef, thePublicKeyDef))
            return false;

        /* Identical if those tests succeed */
        return true;
    }

    /**
     * Get CipherSet for partner Elliptic Curve
     * @param pPartner partner asymmetric key
     * @return the new CipherSet
     * @throws ModelException
     */
    public CipherSet getCipherSet(AsymmetricKey pPartner) throws ModelException {
        /* Both keys must be elliptic */
        if ((!theKeyType.isElliptic()) || (pPartner.getKeyType() != theKeyType))
            throw new ModelException(ExceptionClass.LOGIC,
                    "Shared Keys require both partners to be similar Elliptic");

        /* Look for an already resolved CipherSet */
        CipherSet mySet = theCipherMap.get(pPartner);

        /* Return it if found */
        if (mySet != null)
            return mySet;

        /* Access the Shared Secret */
        byte[] mySecret = getSharedSecret(pPartner);

        /* Build the CipherSet */
        mySet = new CipherSet(theGenerator, theKeyMode);

        /* Apply the Secret */
        mySet.buildCiphers(mySecret);

        /* Store the Set into the map */
        theCipherMap.put(pPartner, mySet);

        /* Return the Cipher Set */
        return mySet;
    }

    /**
     * Get CipherSet for internal Elliptic Curve
     * @return the cipher set
     * @throws ModelException
     */
    public CipherSet getCipherSet() throws ModelException {
        /* Return PreExisting set */
        if (theCipherSet != null)
            return theCipherSet;

        /* Build the internal CipherSet */
        theCipherSet = getCipherSet(this);

        /* Return the Cipher Set */
        return theCipherSet;
    }

    /**
     * derive a SymmetricKey from secured key definition
     * @param pSecuredKeyDef the secured key definition
     * @return the Symmetric key
     * @throws ModelException
     */
    public SymmetricKey deriveSymmetricKey(byte[] pSecuredKeyDef) throws ModelException {
        SymmetricKey mySymKey;
        CipherSet mySet;
        SecretKey myKey;
        Cipher myCipher;

        /* Cannot unwrap unless we have the private key */
        if (isPublicOnly())
            throw new ModelException(ExceptionClass.LOGIC, "Cannot unwrap without private key");

        /* Protect against exceptions */
        try {
            /* If we are elliptic */
            if (theKeyType.isElliptic()) {
                /* Access the internal CipherSet */
                mySet = getCipherSet();

                /* Unwrap the Key */
                mySymKey = mySet.deriveSymmetricKey(pSecuredKeyDef);
            }

            /* else we use RAS semantics */
            else {
                /* Initialise the cipher */
                myCipher = theGenerator.accessCipher(theKeyType.getAlgorithm());
                myCipher.init(Cipher.UNWRAP_MODE, getPrivateKey());

                /* Parse the KeySpec */
                SymKeyNeedle myNeedle = new SymKeyNeedle(pSecuredKeyDef);

                /* unwrap the key */
                SymKeyType myType = myNeedle.getSymKeyType();
                myKey = (SecretKey) myCipher.unwrap(myNeedle.getEncodedKey(), myType.getAlgorithm(),
                                                    Cipher.SECRET_KEY);

                /* Build the symmetric key */
                mySymKey = new SymmetricKey(theGenerator, myKey, myType);
            }
        }

        catch (ModelException e) {
            throw e;
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to unwrap key", e);
        }

        /* Add the key definition to the map */
        theSymKeyMap.put(mySymKey, pSecuredKeyDef);

        /* Return the new key */
        return mySymKey;
    }

    /**
     * Get the Secured Key Definition for a Symmetric Key
     * @param pKey the Symmetric Key to secure
     * @return the secured key definition
     * @throws ModelException
     */
    public byte[] secureSymmetricKey(SymmetricKey pKey) throws ModelException {
        byte[] myWrappedKey;
        Cipher myCipher;
        CipherSet mySet;

        /* Look for an entry in the map and return it if found */
        myWrappedKey = theSymKeyMap.get(pKey);
        if (myWrappedKey != null)
            return myWrappedKey;

        /* Protect against exceptions */
        try {
            /* If we are elliptic */
            if (theKeyType.isElliptic()) {
                /* Access the internal CipherSet */
                mySet = getCipherSet();

                /* Wrap the Key */
                myWrappedKey = mySet.secureSymmetricKey(pKey);
            }

            /* else we are using RSA semantics */
            else {
                /* Initialise the cipher */
                myCipher = theGenerator.accessCipher(theKeyType.getAlgorithm());
                myCipher.init(Cipher.WRAP_MODE, getPublicKey());

                /* wrap the key */
                myWrappedKey = myCipher.wrap(pKey.getSecretKey());

                /* Determine the external definition */
                SymKeyNeedle myNeedle = new SymKeyNeedle(pKey.getKeyType(), myWrappedKey);
                myWrappedKey = myNeedle.getExternal();
            }
        }

        catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to wrap key", e);
        }

        /* Add the key definition to the map */
        theSymKeyMap.put(pKey, myWrappedKey);

        /* Return to caller */
        return myWrappedKey;
    }

    /**
     * Obtain shared secret for partner Asymmetric Key
     * @param pPartner partner asymmetric key
     * @return the shared secret
     * @throws ModelException
     */
    private synchronized byte[] getSharedSecret(AsymmetricKey pPartner) throws ModelException {
        /* Both keys must be elliptic */
        if ((!theKeyType.isElliptic()) || (pPartner.theKeyType != theKeyType))
            throw new ModelException(ExceptionClass.LOGIC,
                    "Shared Keys require both partners to be similar Elliptic");

        /* Cannot generate unless we have the private key */
        if (isPublicOnly())
            throw new ModelException(ExceptionClass.LOGIC, "Cannot generate secret without private key");

        /* Protect against exceptions */
        try {
            /* If we do not currently have a key Agreement */
            if (theKeyAgreement == null) {
                /* Create the key agreement */
                theKeyAgreement = theGenerator.accessKeyAgreement("ECDH");
            }

            /* Process the key agreement */
            theKeyAgreement.init(getPrivateKey());
            theKeyAgreement.doPhase(pPartner.getPublicKey(), true);

            /* Generate the secret */
            return theKeyAgreement.generateSecret();
        }

        /* Handle exceptions */
        catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to negotiate key agreement", e);
        }
    }

    /**
     * Obtain the signature for the file entry
     * @param pEntry the ZipFile properties
     * @return the signature
     * @throws ModelException
     */
    public byte[] signFile(ZipFileEntry pEntry) throws ModelException {
        /* Cannot sign unless we have the private key */
        if (isPublicOnly())
            throw new ModelException(ExceptionClass.LOGIC, "Cannot sign without private key");

        /* Protect against exceptions */
        try {
            /* Create a signature */
            Signature mySignature = theGenerator.accessSignature(theKeyType.getSignature());
            mySignature.initSign(getPrivateKey(), theGenerator.getRandom());

            /* Build the signature */
            pEntry.signEntry(mySignature);

            /* Complete the signature */
            return mySignature.sign();
        }

        /* Catch exceptions */
        catch (ModelException e) {
            throw e;
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Exception calculating signature", e);
        }
    }

    /**
     * Verify the signature for the zipFileEntry
     * @param pEntry the ZipFile properties
     * @throws ModelException
     */
    public void verifyFile(ZipFileEntry pEntry) throws ModelException {
        /* Protect against exceptions */
        try {
            /* Create a signature */
            Signature mySignature = theGenerator.accessSignature(theKeyType.getSignature());
            mySignature.initVerify(getPublicKey());

            /* Build the signature */
            pEntry.signEntry(mySignature);

            /* Check the signature */
            if (!mySignature.verify(pEntry.getSignature())) {
                /* Throw an invalid file exception */
                throw new ModelException(ExceptionClass.CRYPTO, "Signature does not match");
            }
        }

        /* Catch exceptions */
        catch (ModelException e) {
            throw e;
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Exception occurred verifying signature", e);
        }
    }

    /**
     * Encrypt string
     * @param pString string to encrypt
     * @param pTarget target partner of encryption
     * @return Encrypted bytes
     * @throws ModelException
     */
    public byte[] encryptString(String pString,
                                AsymmetricKey pTarget) throws ModelException {
        /* Target must be identical key type */
        if (SecurityMode.isDifferent(pTarget.getKeyMode(), theKeyMode))
            throw new ModelException(ExceptionClass.LOGIC,
                    "Asymmetric Encryption must be between similar partners");

        /* Protect against exceptions */
        try {
            /* If we are elliptic */
            if (theKeyType.isElliptic()) {
                /* Access the target CipherSet */
                CipherSet mySet = getCipherSet(pTarget);

                /* Encrypt the string */
                return mySet.encryptString(pString);
            }

            /* else handle RSA semantics */
            else
                return encryptRSAString(pString, pTarget);
        }

        /* Catch exceptions */
        catch (ModelException e) {
            throw e;
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Exception occurred initialising cipher", e);
        }
    }

    /**
     * Encrypt string
     * @param pString string to encrypt
     * @param pTarget target partner of encryption
     * @return Encrypted bytes
     * @throws ModelException
     */
    private byte[] encryptRSAString(String pString,
                                    AsymmetricKey pTarget) throws ModelException {
        byte[] myBytes;
        byte[] myOutput;
        int iBlockSize;
        int iOutSize;
        int iNumBlocks;
        int iNumBytes;
        int iOffset;
        int iOutOffs;
        Cipher myCipher;

        /* Protect against exceptions */
        try {
            /* Create the cipher */
            myCipher = theGenerator.accessCipher(theKeyType.getCipher());
            myCipher.init(Cipher.ENCRYPT_MODE, pTarget.getPublicKey());

            /* Convert the string to a byte array */
            myBytes = DataConverter.stringToByteArray(pString);

            /* Determine the block sizes */
            iBlockSize = myCipher.getBlockSize();
            iOutSize = myCipher.getOutputSize(iBlockSize);

            /* Determine the number of blocks */
            int iDataLen = myBytes.length;
            iNumBlocks = 1 + ((iDataLen - 1) / iBlockSize);

            /* Allocate the output buffer */
            myOutput = new byte[iNumBlocks * iOutSize];

            /* Initialise offsets */
            iOffset = 0;
            iOutOffs = 0;

            /* Loop through the bytes in units of iBlockSize */
            while (iDataLen > 0) {
                /* Determine the length of data to process */
                iNumBytes = iDataLen;
                if (iNumBytes > iBlockSize)
                    iNumBytes = iBlockSize;

                /* Encrypt the data */
                iOutSize = myCipher.doFinal(myBytes, iOffset, iNumBytes, myOutput, iOutOffs);

                /* Adjust offsets */
                iDataLen -= iNumBytes;
                iOffset += iNumBytes;
                iOutOffs += iOutSize;
            }

            /* Adjust output array correctly */
            if (iOutOffs < myOutput.length)
                myOutput = Arrays.copyOf(myOutput, iOutOffs);
        }

        catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to encrypt string", e);
        }

        /* Return to caller */
        return myOutput;
    }

    /**
     * Decrypt string
     * @param pBytes encrypted string to decrypt
     * @param pSource source partner of encryption
     * @return Decrypted string
     * @throws ModelException
     */
    public String decryptString(byte[] pBytes,
                                AsymmetricKey pSource) throws ModelException {
        /* Cannot decrypt unless we have the private key */
        if (isPublicOnly())
            throw new ModelException(ExceptionClass.LOGIC, "Cannot decrypt without private key");

        /* Source must be identical key type */
        if (SecurityMode.isDifferent(pSource.getKeyMode(), getKeyMode()))
            throw new ModelException(ExceptionClass.LOGIC,
                    "Asymmetric Encryption must be between similar partners");

        /* Protect against exceptions */
        try {
            /* If we are elliptic */
            if (theKeyType.isElliptic()) {
                /* Access the required CipherSet */
                CipherSet mySet = getCipherSet(pSource);

                /* Decrypt the string */
                return mySet.decryptString(pBytes);
            }

            /* else handle RSA semantics */
            else
                return decryptRSAString(pBytes);
        }

        /* Catch exceptions */
        catch (ModelException e) {
            throw e;
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Exception occurred initialising cipher", e);
        }
    }

    /**
     * Decrypt RSA string
     * @param pBytes encrypted string to decrypt
     * @return Decrypted string
     * @throws ModelException
     */
    private String decryptRSAString(byte[] pBytes) throws ModelException {
        String myString;
        byte[] myOutput;
        int iBlockSize;
        int iOutSize;
        int iNumBlocks;
        int iNumBytes;
        int iOffset;
        int iOutOffs;
        Cipher myCipher;

        /* Protect against exceptions */
        try {
            /* Create the cipher */
            myCipher = theGenerator.accessCipher(theKeyType.getCipher());
            myCipher.init(Cipher.DECRYPT_MODE, getPrivateKey());

            /* Determine the block sizes */
            iBlockSize = myCipher.getBlockSize();
            iOutSize = myCipher.getOutputSize(iBlockSize);

            /* Determine the number of blocks */
            int iDataLen = pBytes.length;
            iNumBlocks = 1 + ((iDataLen - 1) / iBlockSize);

            /* Allocate the output buffer */
            myOutput = new byte[iNumBlocks * iOutSize];

            /* Initialise offsets */
            iOffset = 0;
            iOutOffs = 0;

            /* Loop through the bytes in units of iBlockSize */
            while (iDataLen > 0) {
                /* Determine the length of data to process */
                iNumBytes = iDataLen;
                if (iNumBytes > iBlockSize)
                    iNumBytes = iBlockSize;

                /* Encrypt the data */
                iOutSize = myCipher.doFinal(pBytes, iOffset, iNumBytes, myOutput, iOutOffs);

                /* Adjust offsets */
                iDataLen -= iNumBytes;
                iOffset += iNumBytes;
                iOutOffs += iOutSize;
            }

            /* Adjust output array correctly */
            if (iOutOffs < myOutput.length)
                myOutput = Arrays.copyOf(myOutput, iOutOffs);

            /* Create the string */
            myString = DataConverter.byteArrayToString(myOutput);
        }

        catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to decrypt string", e);
        }

        /* Return to caller */
        return myString;
    }
}
