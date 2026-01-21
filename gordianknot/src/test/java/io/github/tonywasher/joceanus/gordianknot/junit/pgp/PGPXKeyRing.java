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

package io.github.tonywasher.joceanus.gordianknot.junit.pgp;

/**
 * PGP KeyPairs Ids.
 */
public enum PGPXKeyRing {
    /**
     * RSA solo.
     */
    RSASOLO,

    /**
     * RSA duo.
     */
    RSADUO,

    /**
     * RSA trio.
     */
    RSATRIO,

    /**
     * RSA gnuPG.
     */
    RSAGPG,

    /**
     * DSA/ElGamal duo.
     */
    DSAELGAMALDUO,

    /**
     * DSA/ElGamal trio.
     */
    DSAELGAMALTRIO,

    /**
     * DSA/ElGamal gnuPG.
     */
    DSAELGAMALGPG,

    /**
     * EC solo.
     */
    ECSOLO,

    /**
     * EC duo.
     */
    ECDUO,

    /**
     * EC trio.
     */
    ECTRIO,

    /**
     * EC gnuPG.
     */
    ECGPG,

    /**
     * Edwards Duo.
     */
    EDWARDSDUO,

    /**
     * Edwards trio.
     */
    EDWARDSTRIO,

    /**
     * Edwards gnuPG.
     */
    EDWARDSGPG;

    /**
     * Obtain password for secret key
     *
     * @return password
     */
    String obtainPassword4Secret() {
        switch (this) {
            case RSASOLO:
            case RSADUO:
            case RSATRIO:
            case RSAGPG:
                return "pgprsa";
            case DSAELGAMALDUO:
            case DSAELGAMALTRIO:
            case DSAELGAMALGPG:
                return "pgpdsaelgamal";
            case ECSOLO:
            case ECDUO:
            case ECTRIO:
            case ECGPG:
                return "pgpec";
            case EDWARDSDUO:
            case EDWARDSTRIO:
            case EDWARDSGPG:
                return "pgpedwards";
            default:
                throw new IllegalArgumentException("Unexpected keyPairType.");
        }
    }

    /**
     * Obtain keyPairType.
     *
     * @return type
     */
    PGPXKeyRingType getKeyPairType() {
        switch (this) {
            case RSASOLO:
            case RSADUO:
            case RSATRIO:
            case RSAGPG:
                return PGPXKeyRingType.RSA;
            case DSAELGAMALDUO:
            case DSAELGAMALTRIO:
            case DSAELGAMALGPG:
                return PGPXKeyRingType.DSAELGAMAL;
            case ECSOLO:
            case ECDUO:
            case ECTRIO:
            case ECGPG:
                return PGPXKeyRingType.EC;
            case EDWARDSDUO:
            case EDWARDSTRIO:
            case EDWARDSGPG:
                return PGPXKeyRingType.EDWARDS;
            default:
                throw new IllegalArgumentException("Unexpected keyPairType.");
        }
    }

    /**
     * Obtain keyPairStyle.
     *
     * @return style
     */
    PGPXKeyRingStyle getKeyPairStyle() {
        switch (this) {
            case RSASOLO:
            case ECSOLO:
                return PGPXKeyRingStyle.SOLO;
            case RSADUO:
            case DSAELGAMALDUO:
            case ECDUO:
            case EDWARDSDUO:
                return PGPXKeyRingStyle.DUO;
            case RSATRIO:
            case DSAELGAMALTRIO:
            case ECTRIO:
            case EDWARDSTRIO:
                return PGPXKeyRingStyle.TRIO;
            case RSAGPG:
            case DSAELGAMALGPG:
            case ECGPG:
            case EDWARDSGPG:
                return PGPXKeyRingStyle.GPG;
            default:
                throw new IllegalArgumentException("Unexpected keyPairType.");
        }
    }

    /**
     * Is this pair the required style?
     *
     * @param pStyle the style
     * @return true/false
     */
    boolean isStyle(final PGPXKeyRingStyle pStyle) {
        return getKeyPairStyle() == pStyle;
    }

    /**
     * Obtain identity for keyRing
     *
     * @return identity
     */
    String obtainIdentity() {
        switch (this) {
            case RSASOLO:
                return "PGP RSA Solo <pgpRSASolo@gmail.com>";
            case RSADUO:
                return "PGP RSA Duo <pgpRSADuo@gmail.com>";
            case RSATRIO:
                return "PGP RSA Trio <pgpRSATrio@gmail.com>";
            case DSAELGAMALDUO:
                return "PGP DSA/ElGamal Duo <pgpDSADuo@gmail.com>";
            case DSAELGAMALTRIO:
                return "PGP DSA/ElGamal Trio <pgpDSATrio@gmail.com>";
            case ECSOLO:
                return "PGP EC Solo <pgpECSolo@gmail.com>";
            case ECDUO:
                return "PGP EC Duo <pgpECDuo@gmail.com>";
            case ECTRIO:
                return "PGP EC Trio <pgpECTrio@gmail.com>";
            case EDWARDSDUO:
                return "PGP Edwards Duo <pgpEdwardsDuo@gmail.com>";
            case EDWARDSTRIO:
                return "PGP Edwards Trio <pgpEdwardsTrio@gmail.com>";
            default:
                throw new IllegalArgumentException("Unexpected keyPairType.");
        }
    }

    /**
     * Obtain identity for keyRing
     *
     * @return identity
     */
    String obtainAltIdentity() {
        switch (this) {
            case RSASOLO:
                return "PGP RSA Alt Solo <pgpRSAAltSolo@gmail.com>";
            case RSADUO:
                return "PGP RSA Alt Duo <pgpRSAAltDuo@gmail.com>";
            case RSATRIO:
                return "PGP RSA Alt Trio <pgpRSAAltTrio@gmail.com>";
            case DSAELGAMALDUO:
                return "PGP DSA/ElGamal Alt Duo <pgpDSAAltDuo@gmail.com>";
            case DSAELGAMALTRIO:
                return "PGP DSA/ElGamal Alt Trio <pgpDSAAltTrio@gmail.com>";
            case ECSOLO:
                return "PGP EC Alt Solo <pgpECAltSolo@gmail.com>";
            case ECDUO:
                return "PGP EC Alt Duo <pgpECAltDuo@gmail.com>";
            case ECTRIO:
                return "PGP EC Alt Trio <pgpECAltTrio@gmail.com>";
            case EDWARDSDUO:
                return "PGP Edwards Alt Duo <pgpAltEdwardsDuo@gmail.com>";
            case EDWARDSTRIO:
                return "PGP Edwards Alt Trio <pgpAltEdwardsTrio@gmail.com>";
            default:
                throw new IllegalArgumentException("Unexpected keyPairType.");
        }
    }

    /**
     * Obtain fileName
     *
     * @return fileName
     */
    String obtainFilename() {
        switch (this) {
            case RSASOLO:
                return "PGPRSASolo";
            case RSADUO:
                return "PGPRSADuo";
            case RSATRIO:
                return "PGPRSATrio";
            case RSAGPG:
                return "PGPRSAGPG";
            case DSAELGAMALDUO:
                return "PGPDSADuo";
            case DSAELGAMALTRIO:
                return "PGPDSATrio";
            case DSAELGAMALGPG:
                return "PGPDSAGPG";
            case ECSOLO:
                return "PGPECSolo";
            case ECDUO:
                return "PGPECDuo";
            case ECTRIO:
                return "PGPECTrio";
            case ECGPG:
                return "PGPECGPG";
            case EDWARDSDUO:
                return "PGPEdwardsDuo";
            case EDWARDSTRIO:
                return "PGPEdwardsTrio";
            case EDWARDSGPG:
                return "PGPEdwardsGPG";
            default:
                throw new IllegalArgumentException("Unexpected keyPairType.");
        }
    }
}
