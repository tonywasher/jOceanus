/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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

import java.math.BigInteger;

/**
 * Modulus Key lengths.
 */
public enum GordianModulus {
    /**
     * 1024.
     */
    MOD1024(1024, new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
                                 + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
                                 + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
                                 + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED"
                                 + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE65381"
                                 + "FFFFFFFFFFFFFFFF", 16)),

    /**
     * 1536.
     */
    MOD1536(1536, new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
                                 + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
                                 + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
                                 + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED"
                                 + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D"
                                 + "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F"
                                 + "83655D23DCA3AD961C62F356208552BB9ED529077096966D"
                                 + "670C354E4ABC9804F1746C08CA237327FFFFFFFFFFFFFFFF", 16)),

    /**
     * 2048.
     */
    MOD2048(2048, new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
                                 + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
                                 + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
                                 + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED"
                                 + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D"
                                 + "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F"
                                 + "83655D23DCA3AD961C62F356208552BB9ED529077096966D"
                                 + "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B"
                                 + "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9"
                                 + "DE2BCBF6955817183995497CEA956AE515D2261898FA0510"
                                 + "15728E5A8AACAA68FFFFFFFFFFFFFFFF", 16)),

    /**
     * 3072.
     */
    MOD3072(3072, new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
                                 + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
                                 + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
                                 + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED"
                                 + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D"
                                 + "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F"
                                 + "83655D23DCA3AD961C62F356208552BB9ED529077096966D"
                                 + "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B"
                                 + "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9"
                                 + "DE2BCBF6955817183995497CEA956AE515D2261898FA0510"
                                 + "15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64"
                                 + "ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7"
                                 + "ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B"
                                 + "F12FFA06D98A0864D87602733EC86A64521F2B18177B200C"
                                 + "BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31"
                                 + "43DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF", 16)),

    /**
     * 4096.
     */
    MOD4096(4096, new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
                                 + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
                                 + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
                                 + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED"
                                 + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D"
                                 + "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F"
                                 + "83655D23DCA3AD961C62F356208552BB9ED529077096966D"
                                 + "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B"
                                 + "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9"
                                 + "DE2BCBF6955817183995497CEA956AE515D2261898FA0510"
                                 + "15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64"
                                 + "ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7"
                                 + "ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B"
                                 + "F12FFA06D98A0864D87602733EC86A64521F2B18177B200C"
                                 + "BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31"
                                 + "43DB5BFCE0FD108E4B82D120A92108011A723C12A787E6D7"
                                 + "88719A10BDBA5B2699C327186AF4E23C1A946834B6150BDA"
                                 + "2583E9CA2AD44CE8DBBBC2DB04DE8EF92E8EFC141FBECAA6"
                                 + "287C59474E6BC05D99B2964FA090C3A2233BA186515BE7ED"
                                 + "1F612970CEE2D7AFB81BDD762170481CD0069127D5B05AA9"
                                 + "93B4EA988D8FDDC186FFB7DC90A6C08F4DF435C934063199"
                                 + "FFFFFFFFFFFFFFFF", 16)),

    /**
     * 6144.
     */
    MOD6144(6144, new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E08"
                                 + "8A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B"
                                 + "302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9"
                                 + "A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE6"
                                 + "49286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8"
                                 + "FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D"
                                 + "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C"
                                 + "180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF695581718"
                                 + "3995497CEA956AE515D2261898FA051015728E5A8AAAC42DAD33170D"
                                 + "04507A33A85521ABDF1CBA64ECFB850458DBEF0A8AEA71575D060C7D"
                                 + "B3970F85A6E1E4C7ABF5AE8CDB0933D71E8C94E04A25619DCEE3D226"
                                 + "1AD2EE6BF12FFA06D98A0864D87602733EC86A64521F2B18177B200C"
                                 + "BBE117577A615D6C770988C0BAD946E208E24FA074E5AB3143DB5BFC"
                                 + "E0FD108E4B82D120A92108011A723C12A787E6D788719A10BDBA5B26"
                                 + "99C327186AF4E23C1A946834B6150BDA2583E9CA2AD44CE8DBBBC2DB"
                                 + "04DE8EF92E8EFC141FBECAA6287C59474E6BC05D99B2964FA090C3A2"
                                 + "233BA186515BE7ED1F612970CEE2D7AFB81BDD762170481CD0069127"
                                 + "D5B05AA993B4EA988D8FDDC186FFB7DC90A6C08F4DF435C934028492"
                                 + "36C3FAB4D27C7026C1D4DCB2602646DEC9751E763DBA37BDF8FF9406"
                                 + "AD9E530EE5DB382F413001AEB06A53ED9027D831179727B0865A8918"
                                 + "DA3EDBEBCF9B14ED44CE6CBACED4BB1BDB7F1447E6CC254B33205151"
                                 + "2BD7AF426FB8F401378CD2BF5983CA01C64B92ECF032EA15D1721D03"
                                 + "F482D7CE6E74FEF6D55E702F46980C82B5A84031900B1C9E59E7C97F"
                                 + "BEC7E8F323A97A7E36CC88BE0F1D45B7FF585AC54BD407B22B4154AA"
                                 + "CC8F6D7EBF48E1D814CC5ED20F8037E0A79715EEF29BE32806A1D58B"
                                 + "B7C5DA76F550AA3D8A1FBFF0EB19CCB1A313D55CDA56C9EC2EF29632"
                                 + "387FE8D76E3C0468043E8F663F4860EE12BF2D5B0B7474D6E694F91E"
                                 + "6DCC4024FFFFFFFFFFFFFFFF", 16)),

    /**
     * 8192.
     */
    MOD8192(8192, new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
                                 + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
                                 + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
                                 + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED"
                                 + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D"
                                 + "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F"
                                 + "83655D23DCA3AD961C62F356208552BB9ED529077096966D"
                                 + "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B"
                                 + "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9"
                                 + "DE2BCBF6955817183995497CEA956AE515D2261898FA0510"
                                 + "15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64"
                                 + "ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7"
                                 + "ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B"
                                 + "F12FFA06D98A0864D87602733EC86A64521F2B18177B200C"
                                 + "BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31"
                                 + "43DB5BFCE0FD108E4B82D120A92108011A723C12A787E6D7"
                                 + "88719A10BDBA5B2699C327186AF4E23C1A946834B6150BDA"
                                 + "2583E9CA2AD44CE8DBBBC2DB04DE8EF92E8EFC141FBECAA6"
                                 + "287C59474E6BC05D99B2964FA090C3A2233BA186515BE7ED"
                                 + "1F612970CEE2D7AFB81BDD762170481CD0069127D5B05AA9"
                                 + "93B4EA988D8FDDC186FFB7DC90A6C08F4DF435C934028492"
                                 + "36C3FAB4D27C7026C1D4DCB2602646DEC9751E763DBA37BD"
                                 + "F8FF9406AD9E530EE5DB382F413001AEB06A53ED9027D831"
                                 + "179727B0865A8918DA3EDBEBCF9B14ED44CE6CBACED4BB1B"
                                 + "DB7F1447E6CC254B332051512BD7AF426FB8F401378CD2BF"
                                 + "5983CA01C64B92ECF032EA15D1721D03F482D7CE6E74FEF6"
                                 + "D55E702F46980C82B5A84031900B1C9E59E7C97FBEC7E8F3"
                                 + "23A97A7E36CC88BE0F1D45B7FF585AC54BD407B22B4154AA"
                                 + "CC8F6D7EBF48E1D814CC5ED20F8037E0A79715EEF29BE328"
                                 + "06A1D58BB7C5DA76F550AA3D8A1FBFF0EB19CCB1A313D55C"
                                 + "DA56C9EC2EF29632387FE8D76E3C0468043E8F663F4860EE"
                                 + "12BF2D5B0B7474D6E694F91E6DBE115974A3926F12FEE5E4"
                                 + "38777CB6A932DF8CD8BEC4D073B931BA3BC832B68D9DD300"
                                 + "741FA7BF8AFC47ED2576F6936BA424663AAB639C5AE4F568"
                                 + "3423B4742BF1C978238F16CBE39D652DE3FDB8BEFC848AD9"
                                 + "22222E04A4037C0713EB57A81A23F0C73473FC646CEA306B"
                                 + "4BCBC8862F8385DDFA9D4B7FA2C087E879683303ED5BDD3A"
                                 + "062B3CF5B3A278A66D2A13F83F44F82DDF310EE074AB6A36"
                                 + "4597E899A0255DC164F31CC50846851DF9AB48195DED7EA1"
                                 + "B1D510BD7EE74D73FAF36BC31ECFA268359046F4EB879F92"
                                 + "4009438B481C6CD7889A002ED5EE382BC9190DA6FC026E47"
                                 + "9558E4475677E9AA9E3050E2765694DFC81F56E880B96E71"
                                 + "60C980DD98EDD3DFFFFFFFFFFFFFFFFF", 16));

    /**
     * The DiffieHellman Generator.
     */
    public static final BigInteger DH_GENERATOR = BigInteger.valueOf(2);

    /**
     * The modulus length.
     */
    private final int theLength;

    /**
     * The DiffieHellman Prime.
     */
    private final BigInteger thePrime;

    /**
     * Constructor.
     * @param pLength the length of the modulus
     * @param pPrime the prime
     */
    GordianModulus(final int pLength,
                   final BigInteger pPrime) {
        theLength = pLength;
        thePrime = pPrime;
    }

    /**
     * Obtain the length of the modulus.
     * @return the length
     */
    public int getModulus() {
        return theLength;
    }

    /**
     * Obtain the DiffieHellman Prime.
     * @return the prime
     */
    public BigInteger getPrime() {
        return thePrime;
    }

    @Override
    public String toString() {
        return name();
    }
}
