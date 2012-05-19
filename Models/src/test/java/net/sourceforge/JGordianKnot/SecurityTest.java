package net.sourceforge.JGordianKnot;

import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.SwingUtilities;

import net.sourceforge.JDataManager.ModelException;

public class SecurityTest {
    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void createAndShowGUI() {
        try {
            /* Test security */
            testSecurity();
        } catch (Exception e) {
            System.out.println("Help");
        }
    }

    /**
     * Test security algorithms
     * @throws ModelException
     */
    protected static void testSecurity() throws ModelException {
        /* Create new Password Hash */
        SecureManager myManager = new SecureManager();
        PasswordHash myHash = myManager.resolvePasswordHash(null, "New");
        SecurityGenerator myGen = myHash.getSecurityGenerator();

        /* Create new symmetric key and asymmetric Key */
        SymmetricKey mySym = myGen.generateSymmetricKey(SymKeyType.AES);
        AsymmetricKey myAsym = myGen.generateAsymmetricKey();

        /* Secure the keys */
        byte[] mySymSafe = myHash.secureSymmetricKey(mySym);
        byte[] myAsymSafe = myHash.securePrivateKey(myAsym);
        byte[] myAsymPublic = myAsym.getExternalDef();
        byte[] mySymSafe2 = myAsym.secureSymmetricKey(mySym);

        /* Create a message digest */
        MsgDigest myDigest = new MsgDigest(myGen);
        myDigest.update(mySymSafe);
        myDigest.update(myAsymSafe);
        myDigest.update(myAsymPublic);
        myDigest.update(mySymSafe2);
        byte[] myDigestBytes = myDigest.buildExternal();
        long myDataLen = myDigest.getDataLength();

        /* Start a new session */
        myManager = new SecureManager();
        PasswordHash myNewHash = myManager.resolvePasswordHash(myHash.getHashBytes(), "Test");
        myGen = myHash.getSecurityGenerator();

        /* Create a message digest */
        myDigest = new MsgDigest(myGen, myDigestBytes, myDataLen, "Test");
        myDigest.update(mySymSafe);
        myDigest.update(myAsymSafe);
        myDigest.update(myAsymPublic);
        myDigest.update(mySymSafe2);
        myDigest.validateDigest();

        /* Derive the keys */
        AsymmetricKey myAsym1 = myNewHash.deriveAsymmetricKey(myAsymSafe, myAsymPublic);
        SymmetricKey mySym1 = myNewHash.deriveSymmetricKey(mySymSafe);
        SymmetricKey mySym2 = myAsym1.deriveSymmetricKey(mySymSafe2);

        /* Check the keys are the same */
        if (!myAsym1.equals(myAsym))
            System.out.println("help");
        if (!mySym1.equals(mySym))
            System.out.println("help");
        if (!mySym2.equals(mySym))
            System.out.println("help");
    }

    /**
     * List the supported algorithms
     * @param pProvider the provider
     */
    protected static void listAlgorithms(SecurityProvider pProvider) {
        Set<String> ciphers = new HashSet<String>();
        Set<String> keyFactories = new HashSet<String>();
        Set<String> messageDigests = new HashSet<String>();
        Set<String> macs = new HashSet<String>();
        Set<String> signatures = new HashSet<String>();
        Set<String> remaining = new HashSet<String>();

        pProvider.ensureInstalled();
        Provider[] providers = Security.getProviders();

        for (int i = 0; i != providers.length; i++) {
            if (!providers[i].getName().equals(pProvider.getProvider()))
                continue;
            Iterator<Object> it = providers[i].keySet().iterator();
            while (it.hasNext()) {
                String entry = (String) it.next();
                if (entry.startsWith("Alg.Alias.")) {
                    entry = entry.substring("Alg.Alias.".length());
                }
                if (entry.startsWith("Cipher.")) {
                    ciphers.add(entry.substring("Cipher.".length()));
                } else if (entry.startsWith("SecretKeyFactory.")) {
                    keyFactories.add(entry.substring("SecretKeyFactory.".length()));
                } else if (entry.startsWith("MessageDigest.")) {
                    messageDigests.add(entry.substring("MessageDigest.".length()));
                } else if (entry.startsWith("Mac.")) {
                    macs.add(entry.substring("Mac.".length()));
                } else if (entry.startsWith("Signature.")) {
                    signatures.add(entry.substring("Signature.".length()));
                } else
                    remaining.add(entry);
            }
        }

        printSet("Ciphers", ciphers);
        printSet("SecretKeyFactories", keyFactories);
        printSet("MessageDigests", messageDigests);
        printSet("Macs", macs);
        printSet("Signatures", signatures);
        printSet("Remaining", remaining);
    }

    /**
     * Print out a set of algorithms
     * @param setName the name of the set
     * @param algorithms the set of algorithms
     */
    private static void printSet(String setName,
                                 Set<String> algorithms) {
        System.out.println(setName + ":");
        if (algorithms.isEmpty()) {
            System.out.println("            None available.");
        } else {
            Iterator<String> it = algorithms.iterator();
            while (it.hasNext()) {
                String name = it.next();
                System.out.println("            " + name);
            }
        }
    }
}
