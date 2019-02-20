/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.junit.regression;

import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Security Test suite - List Algorithms.
 */
public final class ListAlgorithms {
    /**
     * Create a logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(ListAlgorithms.class);

    /**
     * List the supported algorithms.
     */
    public static void main(String[] pArgs) {
        final Set<String> ciphers = new HashSet<>();
        final Set<String> secretKeyFactories = new HashSet<>();
        final Set<String> keyFactories = new HashSet<>();
        final Set<String> keyAgreements = new HashSet<>();
        final Set<String> keyGenerators = new HashSet<>();
        final Set<String> keyPairGenerators = new HashSet<>();
        final Set<String> messageDigests = new HashSet<>();
        final Set<String> macs = new HashSet<>();
        final Set<String> signatures = new HashSet<>();
        final Set<String> randoms = new HashSet<>();
        final Set<String> remaining = new HashSet<>();

        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastlePQCProvider());

        for (Provider myProvider : Security.getProviders()) {
            if (!"BC".equals(myProvider.getName())
                    && !"BCPQC".equals(myProvider.getName())) {
                continue;
            }
            for (Object key : myProvider.keySet()) {
                String entry = (String) key;
                if (entry.startsWith("Alg.Alias.")) {
                    entry = entry.substring("Alg.Alias.".length());
                }
                if (entry.contains(".OID.")
                        || entry.contains(".1.")) {
                    continue;
                }
                if (entry.startsWith("Cipher.")) {
                    ciphers.add(entry.substring("Cipher.".length()));
                } else if (entry.startsWith("SecretKeyFactory.")) {
                    secretKeyFactories.add(entry.substring("SecretKeyFactory.".length()));
                } else if (entry.startsWith("KeyFactory.")) {
                    keyFactories.add(entry.substring("KeyFactory.".length()));
                } else if (entry.startsWith("KeyAgreement.")) {
                    keyAgreements.add(entry.substring("KeyAgreement.".length()));
                } else if (entry.startsWith("KeyGenerator.")) {
                    keyGenerators.add(entry.substring("KeyGenerator.".length()));
                } else if (entry.startsWith("KeyPairGenerator.")) {
                    keyPairGenerators.add(entry.substring("KeyPairGenerator.".length()));
                } else if (entry.startsWith("MessageDigest.")) {
                    messageDigests.add(entry.substring("MessageDigest.".length()));
                } else if (entry.startsWith("Mac.")) {
                    macs.add(entry.substring("Mac.".length()));
                } else if (entry.startsWith("Signature.")) {
                    signatures.add(entry.substring("Signature.".length()));
                } else if (entry.startsWith("SecureRandom.")) {
                    randoms.add(entry.substring("SecureRandom.".length()));
                } else {
                    remaining.add(entry);
                }
            }
        }

        printSet("Ciphers", ciphers);
        printSet("SecretKeyFactories", secretKeyFactories);
        printSet("KeyFactories", keyFactories);
        printSet("KeyAgreements", keyAgreements);
        printSet("KeyGenerators", keyGenerators);
        printSet("KeyPairGenerators", keyPairGenerators);
        printSet("MessageDigests", messageDigests);
        printSet("Macs", macs);
        printSet("Signatures", signatures);
        printSet("Randoms", randoms);
        printSet("Remaining", remaining);
    }

    /**
     * Print out a set of algorithms.
     * @param setName the name of the set
     * @param algorithms the set of algorithms
     */
    private static void printSet(final String setName,
                                 final Set<String> algorithms) {
        LOGGER.error(setName
                + ":");
        if (algorithms.isEmpty()) {
            LOGGER.error("            None available.");
        } else {
            final Iterator<String> it = algorithms.iterator();
            while (it.hasNext()) {
                final String name = it.next();
                LOGGER.error("            "
                        + name);
            }
        }
    }
}
