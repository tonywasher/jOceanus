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
package net.sourceforge.joceanus.gordianknot.impl.ext.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * AnubisEngine.
 * <p>
 * Directly ported from the Anubis C reference implementation found at https://embeddedsw.net/Cipher_Reference_Home.html
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class GordianAnubisEngine
        implements BlockCipher {
    /**
     * BlockSize Bits.
     */
    private static final int BLOCKSIZE = 128;

    /**
     * BlockSize Bytes.
     */
    private static final int BLOCKSIZEB = (BLOCKSIZE / 8);

    /**
     * keyBits State.
     */
    private int keyBits;

    /**
     * R State.
     */
    private int rState;

    /**
     * N State.
     */
    private int nState;

    /**
     * encryption State.
     */
    private Boolean forEncryption;

    /**
     * roundKey State.
     */
    private int[][] roundKey;

    /**
     * Table T0.
     */
    private static final int[] T0 = {
                    0xa753a6f5, 0xd3bb6bd0, 0xe6d1bf6e, 0x71e2d93b,
                    0xd0bd67da, 0xac458acf, 0x4d9a29b3, 0x79f2f90b,
                    0x3a74e89c, 0xc98f038c, 0x913f7e41, 0xfce5d732,
                    0x1e3c7844, 0x478e018f, 0x54a84de5, 0xbd67cea9,
                    0x8c050a0f, 0xa557aef9, 0x7af4f501, 0xfbebcb20,
                    0x63c69157, 0xb86ddab7, 0xdda753f4, 0xd4b577c2,
                    0xe5d7b364, 0xb37bf68d, 0xc59733a4, 0xbe61c2a3,
                    0xa94f9ed1, 0x880d1a17, 0x0c183028, 0xa259b2eb,
                    0x3972e496, 0xdfa35bf8, 0x2952a4f6, 0xdaa94fe6,
                    0x2b56acfa, 0xa84d9ad7, 0xcb8b0b80, 0x4c982db5,
                    0x4b9631a7, 0x224488cc, 0xaa4992db, 0x244890d8,
                    0x4182199b, 0x70e0dd3d, 0xa651a2f3, 0xf9efc32c,
                    0x5ab475c1, 0xe2d9af76, 0xb07dfa87, 0x366cd8b4,
                    0x7dfae913, 0xe4d5b762, 0x3366ccaa, 0xffe3db38,
                    0x60c09d5d, 0x204080c0, 0x08102030, 0x8b0b161d,
                    0x5ebc65d9, 0xab4b96dd, 0x7ffee11f, 0x78f0fd0d,
                    0x7cf8ed15, 0x2c58b0e8, 0x57ae41ef, 0xd2b96fd6,
                    0xdca557f2, 0x6ddaa973, 0x7efce519, 0x0d1a342e,
                    0x53a651f7, 0x94356a5f, 0xc39b2bb0, 0x2850a0f0,
                    0x274e9cd2, 0x060c1814, 0x5fbe61df, 0xad478ec9,
                    0x67ce814f, 0x5cb86dd5, 0x55aa49e3, 0x48903dad,
                    0x0e1c3824, 0x52a455f1, 0xeac98f46, 0x42841591,
                    0x5bb671c7, 0x5dba69d3, 0x3060c0a0, 0x58b07dcd,
                    0x51a259fb, 0x59b279cb, 0x3c78f088, 0x4e9c25b9,
                    0x3870e090, 0x8a09121b, 0x72e4d531, 0x14285078,
                    0xe7d3bb68, 0xc6913fae, 0xdea15ffe, 0x50a05dfd,
                    0x8e010203, 0x9239724b, 0xd1bf63dc, 0x77eec12f,
                    0x933b764d, 0x458a0983, 0x9a29527b, 0xce811f9e,
                    0x2d5ab4ee, 0x03060c0a, 0x62c49551, 0xb671e293,
                    0xb96fdeb1, 0xbf63c6a5, 0x96316253, 0x6bd6b167,
                    0x3f7efc82, 0x070e1c12, 0x1224486c, 0xae4182c3,
                    0x40801d9d, 0x3468d0b8, 0x468c0589, 0x3e7cf884,
                    0xdbab4be0, 0xcf831b98, 0xecc59752, 0xcc851792,
                    0xc19f23bc, 0xa15fbee1, 0xc09d27ba, 0xd6b17fce,
                    0x1d3a744e, 0xf4f5f702, 0x61c2995b, 0x3b76ec9a,
                    0x10204060, 0xd8ad47ea, 0x68d0bd6d, 0xa05dbae7,
                    0xb17ffe81, 0x0a14283c, 0x69d2b96b, 0x6cd8ad75,
                    0x499239ab, 0xfae9cf26, 0x76ecc529, 0xc49537a2,
                    0x9e214263, 0x9b2b567d, 0x6edca579, 0x992f5e71,
                    0xc2992fb6, 0xb773e695, 0x982d5a77, 0xbc65caaf,
                    0x8f030605, 0x85172e39, 0x1f3e7c42, 0xb475ea9f,
                    0xf8edc72a, 0x11224466, 0x2e5cb8e4, 0x00000000,
                    0x254a94de, 0x1c387048, 0x2a54a8fc, 0x3d7af48e,
                    0x050a141e, 0x4f9e21bf, 0x7bf6f107, 0xb279f28b,
                    0x3264c8ac, 0x903d7a47, 0xaf4386c5, 0x19326456,
                    0xa35bb6ed, 0xf7f3fb08, 0x73e6d137, 0x9d274e69,
                    0x152a547e, 0x74e8cd25, 0xeec19f5e, 0xca890f86,
                    0x9f234665, 0x0f1e3c22, 0x1b366c5a, 0x75eac923,
                    0x86112233, 0x84152a3f, 0x9c254a6f, 0x4a9435a1,
                    0x97336655, 0x1a34685c, 0x65ca8943, 0xf6f1ff0e,
                    0xedc79354, 0x09122436, 0xbb6bd6bd, 0x264c98d4,
                    0x831b362d, 0xebcb8b40, 0x6fdea17f, 0x811f3e21,
                    0x04081018, 0x6ad4b561, 0x43861197, 0x01020406,
                    0x172e5c72, 0xe1dfa37c, 0x87132635, 0xf5f7f304,
                    0x8d070e09, 0xe3dbab70, 0x23468cca, 0x801d3a27,
                    0x44880d85, 0x162c5874, 0x66cc8549, 0x214284c6,
                    0xfee1df3e, 0xd5b773c4, 0x3162c4a6, 0xd9af43ec,
                    0x356ad4be, 0x18306050, 0x0204080c, 0x64c88d45,
                    0xf2f9ef16, 0xf1ffe31c, 0x56ac45e9, 0xcd871394,
                    0x8219322b, 0xc88d078a, 0xba69d2bb, 0xf0fde71a,
                    0xefc39b58, 0xe9cf834c, 0xe8cd874a, 0xfde7d334,
                    0x890f1e11, 0xd7b37bc8, 0xc7933ba8, 0xb577ee99,
                    0xa455aaff, 0x2f5ebce2, 0x95376e59, 0x13264c6a,
                    0x0b162c3a, 0xf3fbeb10, 0xe0dda77a, 0x376edcb2,
            };

    /**
     * Table T1.
     */
    private static final int[] T1 = {
                    0x53a7f5a6, 0xbbd3d06b, 0xd1e66ebf, 0xe2713bd9,
                    0xbdd0da67, 0x45accf8a, 0x9a4db329, 0xf2790bf9,
                    0x743a9ce8, 0x8fc98c03, 0x3f91417e, 0xe5fc32d7,
                    0x3c1e4478, 0x8e478f01, 0xa854e54d, 0x67bda9ce,
                    0x058c0f0a, 0x57a5f9ae, 0xf47a01f5, 0xebfb20cb,
                    0xc6635791, 0x6db8b7da, 0xa7ddf453, 0xb5d4c277,
                    0xd7e564b3, 0x7bb38df6, 0x97c5a433, 0x61bea3c2,
                    0x4fa9d19e, 0x0d88171a, 0x180c2830, 0x59a2ebb2,
                    0x723996e4, 0xa3dff85b, 0x5229f6a4, 0xa9dae64f,
                    0x562bfaac, 0x4da8d79a, 0x8bcb800b, 0x984cb52d,
                    0x964ba731, 0x4422cc88, 0x49aadb92, 0x4824d890,
                    0x82419b19, 0xe0703ddd, 0x51a6f3a2, 0xeff92cc3,
                    0xb45ac175, 0xd9e276af, 0x7db087fa, 0x6c36b4d8,
                    0xfa7d13e9, 0xd5e462b7, 0x6633aacc, 0xe3ff38db,
                    0xc0605d9d, 0x4020c080, 0x10083020, 0x0b8b1d16,
                    0xbc5ed965, 0x4babdd96, 0xfe7f1fe1, 0xf0780dfd,
                    0xf87c15ed, 0x582ce8b0, 0xae57ef41, 0xb9d2d66f,
                    0xa5dcf257, 0xda6d73a9, 0xfc7e19e5, 0x1a0d2e34,
                    0xa653f751, 0x35945f6a, 0x9bc3b02b, 0x5028f0a0,
                    0x4e27d29c, 0x0c061418, 0xbe5fdf61, 0x47adc98e,
                    0xce674f81, 0xb85cd56d, 0xaa55e349, 0x9048ad3d,
                    0x1c0e2438, 0xa452f155, 0xc9ea468f, 0x84429115,
                    0xb65bc771, 0xba5dd369, 0x6030a0c0, 0xb058cd7d,
                    0xa251fb59, 0xb259cb79, 0x783c88f0, 0x9c4eb925,
                    0x703890e0, 0x098a1b12, 0xe47231d5, 0x28147850,
                    0xd3e768bb, 0x91c6ae3f, 0xa1defe5f, 0xa050fd5d,
                    0x018e0302, 0x39924b72, 0xbfd1dc63, 0xee772fc1,
                    0x3b934d76, 0x8a458309, 0x299a7b52, 0x81ce9e1f,
                    0x5a2deeb4, 0x06030a0c, 0xc4625195, 0x71b693e2,
                    0x6fb9b1de, 0x63bfa5c6, 0x31965362, 0xd66b67b1,
                    0x7e3f82fc, 0x0e07121c, 0x24126c48, 0x41aec382,
                    0x80409d1d, 0x6834b8d0, 0x8c468905, 0x7c3e84f8,
                    0xabdbe04b, 0x83cf981b, 0xc5ec5297, 0x85cc9217,
                    0x9fc1bc23, 0x5fa1e1be, 0x9dc0ba27, 0xb1d6ce7f,
                    0x3a1d4e74, 0xf5f402f7, 0xc2615b99, 0x763b9aec,
                    0x20106040, 0xadd8ea47, 0xd0686dbd, 0x5da0e7ba,
                    0x7fb181fe, 0x140a3c28, 0xd2696bb9, 0xd86c75ad,
                    0x9249ab39, 0xe9fa26cf, 0xec7629c5, 0x95c4a237,
                    0x219e6342, 0x2b9b7d56, 0xdc6e79a5, 0x2f99715e,
                    0x99c2b62f, 0x73b795e6, 0x2d98775a, 0x65bcafca,
                    0x038f0506, 0x1785392e, 0x3e1f427c, 0x75b49fea,
                    0xedf82ac7, 0x22116644, 0x5c2ee4b8, 0x00000000,
                    0x4a25de94, 0x381c4870, 0x542afca8, 0x7a3d8ef4,
                    0x0a051e14, 0x9e4fbf21, 0xf67b07f1, 0x79b28bf2,
                    0x6432acc8, 0x3d90477a, 0x43afc586, 0x32195664,
                    0x5ba3edb6, 0xf3f708fb, 0xe67337d1, 0x279d694e,
                    0x2a157e54, 0xe87425cd, 0xc1ee5e9f, 0x89ca860f,
                    0x239f6546, 0x1e0f223c, 0x361b5a6c, 0xea7523c9,
                    0x11863322, 0x15843f2a, 0x259c6f4a, 0x944aa135,
                    0x33975566, 0x341a5c68, 0xca654389, 0xf1f60eff,
                    0xc7ed5493, 0x12093624, 0x6bbbbdd6, 0x4c26d498,
                    0x1b832d36, 0xcbeb408b, 0xde6f7fa1, 0x1f81213e,
                    0x08041810, 0xd46a61b5, 0x86439711, 0x02010604,
                    0x2e17725c, 0xdfe17ca3, 0x13873526, 0xf7f504f3,
                    0x078d090e, 0xdbe370ab, 0x4623ca8c, 0x1d80273a,
                    0x8844850d, 0x2c167458, 0xcc664985, 0x4221c684,
                    0xe1fe3edf, 0xb7d5c473, 0x6231a6c4, 0xafd9ec43,
                    0x6a35bed4, 0x30185060, 0x04020c08, 0xc864458d,
                    0xf9f216ef, 0xfff11ce3, 0xac56e945, 0x87cd9413,
                    0x19822b32, 0x8dc88a07, 0x69babbd2, 0xfdf01ae7,
                    0xc3ef589b, 0xcfe94c83, 0xcde84a87, 0xe7fd34d3,
                    0x0f89111e, 0xb3d7c87b, 0x93c7a83b, 0x77b599ee,
                    0x55a4ffaa, 0x5e2fe2bc, 0x3795596e, 0x26136a4c,
                    0x160b3a2c, 0xfbf310eb, 0xdde07aa7, 0x6e37b2dc,
            };

    /**
     * Table T2.
     */
    private static final int[] T2 = {
                    0xa6f5a753, 0x6bd0d3bb, 0xbf6ee6d1, 0xd93b71e2,
                    0x67dad0bd, 0x8acfac45, 0x29b34d9a, 0xf90b79f2,
                    0xe89c3a74, 0x038cc98f, 0x7e41913f, 0xd732fce5,
                    0x78441e3c, 0x018f478e, 0x4de554a8, 0xcea9bd67,
                    0x0a0f8c05, 0xaef9a557, 0xf5017af4, 0xcb20fbeb,
                    0x915763c6, 0xdab7b86d, 0x53f4dda7, 0x77c2d4b5,
                    0xb364e5d7, 0xf68db37b, 0x33a4c597, 0xc2a3be61,
                    0x9ed1a94f, 0x1a17880d, 0x30280c18, 0xb2eba259,
                    0xe4963972, 0x5bf8dfa3, 0xa4f62952, 0x4fe6daa9,
                    0xacfa2b56, 0x9ad7a84d, 0x0b80cb8b, 0x2db54c98,
                    0x31a74b96, 0x88cc2244, 0x92dbaa49, 0x90d82448,
                    0x199b4182, 0xdd3d70e0, 0xa2f3a651, 0xc32cf9ef,
                    0x75c15ab4, 0xaf76e2d9, 0xfa87b07d, 0xd8b4366c,
                    0xe9137dfa, 0xb762e4d5, 0xccaa3366, 0xdb38ffe3,
                    0x9d5d60c0, 0x80c02040, 0x20300810, 0x161d8b0b,
                    0x65d95ebc, 0x96ddab4b, 0xe11f7ffe, 0xfd0d78f0,
                    0xed157cf8, 0xb0e82c58, 0x41ef57ae, 0x6fd6d2b9,
                    0x57f2dca5, 0xa9736dda, 0xe5197efc, 0x342e0d1a,
                    0x51f753a6, 0x6a5f9435, 0x2bb0c39b, 0xa0f02850,
                    0x9cd2274e, 0x1814060c, 0x61df5fbe, 0x8ec9ad47,
                    0x814f67ce, 0x6dd55cb8, 0x49e355aa, 0x3dad4890,
                    0x38240e1c, 0x55f152a4, 0x8f46eac9, 0x15914284,
                    0x71c75bb6, 0x69d35dba, 0xc0a03060, 0x7dcd58b0,
                    0x59fb51a2, 0x79cb59b2, 0xf0883c78, 0x25b94e9c,
                    0xe0903870, 0x121b8a09, 0xd53172e4, 0x50781428,
                    0xbb68e7d3, 0x3faec691, 0x5ffedea1, 0x5dfd50a0,
                    0x02038e01, 0x724b9239, 0x63dcd1bf, 0xc12f77ee,
                    0x764d933b, 0x0983458a, 0x527b9a29, 0x1f9ece81,
                    0xb4ee2d5a, 0x0c0a0306, 0x955162c4, 0xe293b671,
                    0xdeb1b96f, 0xc6a5bf63, 0x62539631, 0xb1676bd6,
                    0xfc823f7e, 0x1c12070e, 0x486c1224, 0x82c3ae41,
                    0x1d9d4080, 0xd0b83468, 0x0589468c, 0xf8843e7c,
                    0x4be0dbab, 0x1b98cf83, 0x9752ecc5, 0x1792cc85,
                    0x23bcc19f, 0xbee1a15f, 0x27bac09d, 0x7fced6b1,
                    0x744e1d3a, 0xf702f4f5, 0x995b61c2, 0xec9a3b76,
                    0x40601020, 0x47ead8ad, 0xbd6d68d0, 0xbae7a05d,
                    0xfe81b17f, 0x283c0a14, 0xb96b69d2, 0xad756cd8,
                    0x39ab4992, 0xcf26fae9, 0xc52976ec, 0x37a2c495,
                    0x42639e21, 0x567d9b2b, 0xa5796edc, 0x5e71992f,
                    0x2fb6c299, 0xe695b773, 0x5a77982d, 0xcaafbc65,
                    0x06058f03, 0x2e398517, 0x7c421f3e, 0xea9fb475,
                    0xc72af8ed, 0x44661122, 0xb8e42e5c, 0x00000000,
                    0x94de254a, 0x70481c38, 0xa8fc2a54, 0xf48e3d7a,
                    0x141e050a, 0x21bf4f9e, 0xf1077bf6, 0xf28bb279,
                    0xc8ac3264, 0x7a47903d, 0x86c5af43, 0x64561932,
                    0xb6eda35b, 0xfb08f7f3, 0xd13773e6, 0x4e699d27,
                    0x547e152a, 0xcd2574e8, 0x9f5eeec1, 0x0f86ca89,
                    0x46659f23, 0x3c220f1e, 0x6c5a1b36, 0xc92375ea,
                    0x22338611, 0x2a3f8415, 0x4a6f9c25, 0x35a14a94,
                    0x66559733, 0x685c1a34, 0x894365ca, 0xff0ef6f1,
                    0x9354edc7, 0x24360912, 0xd6bdbb6b, 0x98d4264c,
                    0x362d831b, 0x8b40ebcb, 0xa17f6fde, 0x3e21811f,
                    0x10180408, 0xb5616ad4, 0x11974386, 0x04060102,
                    0x5c72172e, 0xa37ce1df, 0x26358713, 0xf304f5f7,
                    0x0e098d07, 0xab70e3db, 0x8cca2346, 0x3a27801d,
                    0x0d854488, 0x5874162c, 0x854966cc, 0x84c62142,
                    0xdf3efee1, 0x73c4d5b7, 0xc4a63162, 0x43ecd9af,
                    0xd4be356a, 0x60501830, 0x080c0204, 0x8d4564c8,
                    0xef16f2f9, 0xe31cf1ff, 0x45e956ac, 0x1394cd87,
                    0x322b8219, 0x078ac88d, 0xd2bbba69, 0xe71af0fd,
                    0x9b58efc3, 0x834ce9cf, 0x874ae8cd, 0xd334fde7,
                    0x1e11890f, 0x7bc8d7b3, 0x3ba8c793, 0xee99b577,
                    0xaaffa455, 0xbce22f5e, 0x6e599537, 0x4c6a1326,
                    0x2c3a0b16, 0xeb10f3fb, 0xa77ae0dd, 0xdcb2376e,
            };

    /**
     * Table T3.
     */
    private static final int[] T3 = {
                    0xf5a653a7, 0xd06bbbd3, 0x6ebfd1e6, 0x3bd9e271,
                    0xda67bdd0, 0xcf8a45ac, 0xb3299a4d, 0x0bf9f279,
                    0x9ce8743a, 0x8c038fc9, 0x417e3f91, 0x32d7e5fc,
                    0x44783c1e, 0x8f018e47, 0xe54da854, 0xa9ce67bd,
                    0x0f0a058c, 0xf9ae57a5, 0x01f5f47a, 0x20cbebfb,
                    0x5791c663, 0xb7da6db8, 0xf453a7dd, 0xc277b5d4,
                    0x64b3d7e5, 0x8df67bb3, 0xa43397c5, 0xa3c261be,
                    0xd19e4fa9, 0x171a0d88, 0x2830180c, 0xebb259a2,
                    0x96e47239, 0xf85ba3df, 0xf6a45229, 0xe64fa9da,
                    0xfaac562b, 0xd79a4da8, 0x800b8bcb, 0xb52d984c,
                    0xa731964b, 0xcc884422, 0xdb9249aa, 0xd8904824,
                    0x9b198241, 0x3ddde070, 0xf3a251a6, 0x2cc3eff9,
                    0xc175b45a, 0x76afd9e2, 0x87fa7db0, 0xb4d86c36,
                    0x13e9fa7d, 0x62b7d5e4, 0xaacc6633, 0x38dbe3ff,
                    0x5d9dc060, 0xc0804020, 0x30201008, 0x1d160b8b,
                    0xd965bc5e, 0xdd964bab, 0x1fe1fe7f, 0x0dfdf078,
                    0x15edf87c, 0xe8b0582c, 0xef41ae57, 0xd66fb9d2,
                    0xf257a5dc, 0x73a9da6d, 0x19e5fc7e, 0x2e341a0d,
                    0xf751a653, 0x5f6a3594, 0xb02b9bc3, 0xf0a05028,
                    0xd29c4e27, 0x14180c06, 0xdf61be5f, 0xc98e47ad,
                    0x4f81ce67, 0xd56db85c, 0xe349aa55, 0xad3d9048,
                    0x24381c0e, 0xf155a452, 0x468fc9ea, 0x91158442,
                    0xc771b65b, 0xd369ba5d, 0xa0c06030, 0xcd7db058,
                    0xfb59a251, 0xcb79b259, 0x88f0783c, 0xb9259c4e,
                    0x90e07038, 0x1b12098a, 0x31d5e472, 0x78502814,
                    0x68bbd3e7, 0xae3f91c6, 0xfe5fa1de, 0xfd5da050,
                    0x0302018e, 0x4b723992, 0xdc63bfd1, 0x2fc1ee77,
                    0x4d763b93, 0x83098a45, 0x7b52299a, 0x9e1f81ce,
                    0xeeb45a2d, 0x0a0c0603, 0x5195c462, 0x93e271b6,
                    0xb1de6fb9, 0xa5c663bf, 0x53623196, 0x67b1d66b,
                    0x82fc7e3f, 0x121c0e07, 0x6c482412, 0xc38241ae,
                    0x9d1d8040, 0xb8d06834, 0x89058c46, 0x84f87c3e,
                    0xe04babdb, 0x981b83cf, 0x5297c5ec, 0x921785cc,
                    0xbc239fc1, 0xe1be5fa1, 0xba279dc0, 0xce7fb1d6,
                    0x4e743a1d, 0x02f7f5f4, 0x5b99c261, 0x9aec763b,
                    0x60402010, 0xea47add8, 0x6dbdd068, 0xe7ba5da0,
                    0x81fe7fb1, 0x3c28140a, 0x6bb9d269, 0x75add86c,
                    0xab399249, 0x26cfe9fa, 0x29c5ec76, 0xa23795c4,
                    0x6342219e, 0x7d562b9b, 0x79a5dc6e, 0x715e2f99,
                    0xb62f99c2, 0x95e673b7, 0x775a2d98, 0xafca65bc,
                    0x0506038f, 0x392e1785, 0x427c3e1f, 0x9fea75b4,
                    0x2ac7edf8, 0x66442211, 0xe4b85c2e, 0x00000000,
                    0xde944a25, 0x4870381c, 0xfca8542a, 0x8ef47a3d,
                    0x1e140a05, 0xbf219e4f, 0x07f1f67b, 0x8bf279b2,
                    0xacc86432, 0x477a3d90, 0xc58643af, 0x56643219,
                    0xedb65ba3, 0x08fbf3f7, 0x37d1e673, 0x694e279d,
                    0x7e542a15, 0x25cde874, 0x5e9fc1ee, 0x860f89ca,
                    0x6546239f, 0x223c1e0f, 0x5a6c361b, 0x23c9ea75,
                    0x33221186, 0x3f2a1584, 0x6f4a259c, 0xa135944a,
                    0x55663397, 0x5c68341a, 0x4389ca65, 0x0efff1f6,
                    0x5493c7ed, 0x36241209, 0xbdd66bbb, 0xd4984c26,
                    0x2d361b83, 0x408bcbeb, 0x7fa1de6f, 0x213e1f81,
                    0x18100804, 0x61b5d46a, 0x97118643, 0x06040201,
                    0x725c2e17, 0x7ca3dfe1, 0x35261387, 0x04f3f7f5,
                    0x090e078d, 0x70abdbe3, 0xca8c4623, 0x273a1d80,
                    0x850d8844, 0x74582c16, 0x4985cc66, 0xc6844221,
                    0x3edfe1fe, 0xc473b7d5, 0xa6c46231, 0xec43afd9,
                    0xbed46a35, 0x50603018, 0x0c080402, 0x458dc864,
                    0x16eff9f2, 0x1ce3fff1, 0xe945ac56, 0x941387cd,
                    0x2b321982, 0x8a078dc8, 0xbbd269ba, 0x1ae7fdf0,
                    0x589bc3ef, 0x4c83cfe9, 0x4a87cde8, 0x34d3e7fd,
                    0x111e0f89, 0xc87bb3d7, 0xa83b93c7, 0x99ee77b5,
                    0xffaa55a4, 0xe2bc5e2f, 0x596e3795, 0x6a4c2613,
                    0x3a2c160b, 0x10ebfbf3, 0x7aa7dde0, 0xb2dc6e37,
            };

    /**
     * Table T4.
     */
    private static final int[] T4 = {
                    0xa7a7a7a7, 0xd3d3d3d3, 0xe6e6e6e6, 0x71717171,
                    0xd0d0d0d0, 0xacacacac, 0x4d4d4d4d, 0x79797979,
                    0x3a3a3a3a, 0xc9c9c9c9, 0x91919191, 0xfcfcfcfc,
                    0x1e1e1e1e, 0x47474747, 0x54545454, 0xbdbdbdbd,
                    0x8c8c8c8c, 0xa5a5a5a5, 0x7a7a7a7a, 0xfbfbfbfb,
                    0x63636363, 0xb8b8b8b8, 0xdddddddd, 0xd4d4d4d4,
                    0xe5e5e5e5, 0xb3b3b3b3, 0xc5c5c5c5, 0xbebebebe,
                    0xa9a9a9a9, 0x88888888, 0x0c0c0c0c, 0xa2a2a2a2,
                    0x39393939, 0xdfdfdfdf, 0x29292929, 0xdadadada,
                    0x2b2b2b2b, 0xa8a8a8a8, 0xcbcbcbcb, 0x4c4c4c4c,
                    0x4b4b4b4b, 0x22222222, 0xaaaaaaaa, 0x24242424,
                    0x41414141, 0x70707070, 0xa6a6a6a6, 0xf9f9f9f9,
                    0x5a5a5a5a, 0xe2e2e2e2, 0xb0b0b0b0, 0x36363636,
                    0x7d7d7d7d, 0xe4e4e4e4, 0x33333333, 0xffffffff,
                    0x60606060, 0x20202020, 0x08080808, 0x8b8b8b8b,
                    0x5e5e5e5e, 0xabababab, 0x7f7f7f7f, 0x78787878,
                    0x7c7c7c7c, 0x2c2c2c2c, 0x57575757, 0xd2d2d2d2,
                    0xdcdcdcdc, 0x6d6d6d6d, 0x7e7e7e7e, 0x0d0d0d0d,
                    0x53535353, 0x94949494, 0xc3c3c3c3, 0x28282828,
                    0x27272727, 0x06060606, 0x5f5f5f5f, 0xadadadad,
                    0x67676767, 0x5c5c5c5c, 0x55555555, 0x48484848,
                    0x0e0e0e0e, 0x52525252, 0xeaeaeaea, 0x42424242,
                    0x5b5b5b5b, 0x5d5d5d5d, 0x30303030, 0x58585858,
                    0x51515151, 0x59595959, 0x3c3c3c3c, 0x4e4e4e4e,
                    0x38383838, 0x8a8a8a8a, 0x72727272, 0x14141414,
                    0xe7e7e7e7, 0xc6c6c6c6, 0xdededede, 0x50505050,
                    0x8e8e8e8e, 0x92929292, 0xd1d1d1d1, 0x77777777,
                    0x93939393, 0x45454545, 0x9a9a9a9a, 0xcececece,
                    0x2d2d2d2d, 0x03030303, 0x62626262, 0xb6b6b6b6,
                    0xb9b9b9b9, 0xbfbfbfbf, 0x96969696, 0x6b6b6b6b,
                    0x3f3f3f3f, 0x07070707, 0x12121212, 0xaeaeaeae,
                    0x40404040, 0x34343434, 0x46464646, 0x3e3e3e3e,
                    0xdbdbdbdb, 0xcfcfcfcf, 0xecececec, 0xcccccccc,
                    0xc1c1c1c1, 0xa1a1a1a1, 0xc0c0c0c0, 0xd6d6d6d6,
                    0x1d1d1d1d, 0xf4f4f4f4, 0x61616161, 0x3b3b3b3b,
                    0x10101010, 0xd8d8d8d8, 0x68686868, 0xa0a0a0a0,
                    0xb1b1b1b1, 0x0a0a0a0a, 0x69696969, 0x6c6c6c6c,
                    0x49494949, 0xfafafafa, 0x76767676, 0xc4c4c4c4,
                    0x9e9e9e9e, 0x9b9b9b9b, 0x6e6e6e6e, 0x99999999,
                    0xc2c2c2c2, 0xb7b7b7b7, 0x98989898, 0xbcbcbcbc,
                    0x8f8f8f8f, 0x85858585, 0x1f1f1f1f, 0xb4b4b4b4,
                    0xf8f8f8f8, 0x11111111, 0x2e2e2e2e, 0x00000000,
                    0x25252525, 0x1c1c1c1c, 0x2a2a2a2a, 0x3d3d3d3d,
                    0x05050505, 0x4f4f4f4f, 0x7b7b7b7b, 0xb2b2b2b2,
                    0x32323232, 0x90909090, 0xafafafaf, 0x19191919,
                    0xa3a3a3a3, 0xf7f7f7f7, 0x73737373, 0x9d9d9d9d,
                    0x15151515, 0x74747474, 0xeeeeeeee, 0xcacacaca,
                    0x9f9f9f9f, 0x0f0f0f0f, 0x1b1b1b1b, 0x75757575,
                    0x86868686, 0x84848484, 0x9c9c9c9c, 0x4a4a4a4a,
                    0x97979797, 0x1a1a1a1a, 0x65656565, 0xf6f6f6f6,
                    0xedededed, 0x09090909, 0xbbbbbbbb, 0x26262626,
                    0x83838383, 0xebebebeb, 0x6f6f6f6f, 0x81818181,
                    0x04040404, 0x6a6a6a6a, 0x43434343, 0x01010101,
                    0x17171717, 0xe1e1e1e1, 0x87878787, 0xf5f5f5f5,
                    0x8d8d8d8d, 0xe3e3e3e3, 0x23232323, 0x80808080,
                    0x44444444, 0x16161616, 0x66666666, 0x21212121,
                    0xfefefefe, 0xd5d5d5d5, 0x31313131, 0xd9d9d9d9,
                    0x35353535, 0x18181818, 0x02020202, 0x64646464,
                    0xf2f2f2f2, 0xf1f1f1f1, 0x56565656, 0xcdcdcdcd,
                    0x82828282, 0xc8c8c8c8, 0xbabababa, 0xf0f0f0f0,
                    0xefefefef, 0xe9e9e9e9, 0xe8e8e8e8, 0xfdfdfdfd,
                    0x89898989, 0xd7d7d7d7, 0xc7c7c7c7, 0xb5b5b5b5,
                    0xa4a4a4a4, 0x2f2f2f2f, 0x95959595, 0x13131313,
                    0x0b0b0b0b, 0xf3f3f3f3, 0xe0e0e0e0, 0x37373737,
            };

    /**
     * Table T5.
     */
    private static final int[] T5 = {
                    0x00000000, 0x01020608, 0x02040c10, 0x03060a18,
                    0x04081820, 0x050a1e28, 0x060c1430, 0x070e1238,
                    0x08103040, 0x09123648, 0x0a143c50, 0x0b163a58,
                    0x0c182860, 0x0d1a2e68, 0x0e1c2470, 0x0f1e2278,
                    0x10206080, 0x11226688, 0x12246c90, 0x13266a98,
                    0x142878a0, 0x152a7ea8, 0x162c74b0, 0x172e72b8,
                    0x183050c0, 0x193256c8, 0x1a345cd0, 0x1b365ad8,
                    0x1c3848e0, 0x1d3a4ee8, 0x1e3c44f0, 0x1f3e42f8,
                    0x2040c01d, 0x2142c615, 0x2244cc0d, 0x2346ca05,
                    0x2448d83d, 0x254ade35, 0x264cd42d, 0x274ed225,
                    0x2850f05d, 0x2952f655, 0x2a54fc4d, 0x2b56fa45,
                    0x2c58e87d, 0x2d5aee75, 0x2e5ce46d, 0x2f5ee265,
                    0x3060a09d, 0x3162a695, 0x3264ac8d, 0x3366aa85,
                    0x3468b8bd, 0x356abeb5, 0x366cb4ad, 0x376eb2a5,
                    0x387090dd, 0x397296d5, 0x3a749ccd, 0x3b769ac5,
                    0x3c7888fd, 0x3d7a8ef5, 0x3e7c84ed, 0x3f7e82e5,
                    0x40809d3a, 0x41829b32, 0x4284912a, 0x43869722,
                    0x4488851a, 0x458a8312, 0x468c890a, 0x478e8f02,
                    0x4890ad7a, 0x4992ab72, 0x4a94a16a, 0x4b96a762,
                    0x4c98b55a, 0x4d9ab352, 0x4e9cb94a, 0x4f9ebf42,
                    0x50a0fdba, 0x51a2fbb2, 0x52a4f1aa, 0x53a6f7a2,
                    0x54a8e59a, 0x55aae392, 0x56ace98a, 0x57aeef82,
                    0x58b0cdfa, 0x59b2cbf2, 0x5ab4c1ea, 0x5bb6c7e2,
                    0x5cb8d5da, 0x5dbad3d2, 0x5ebcd9ca, 0x5fbedfc2,
                    0x60c05d27, 0x61c25b2f, 0x62c45137, 0x63c6573f,
                    0x64c84507, 0x65ca430f, 0x66cc4917, 0x67ce4f1f,
                    0x68d06d67, 0x69d26b6f, 0x6ad46177, 0x6bd6677f,
                    0x6cd87547, 0x6dda734f, 0x6edc7957, 0x6fde7f5f,
                    0x70e03da7, 0x71e23baf, 0x72e431b7, 0x73e637bf,
                    0x74e82587, 0x75ea238f, 0x76ec2997, 0x77ee2f9f,
                    0x78f00de7, 0x79f20bef, 0x7af401f7, 0x7bf607ff,
                    0x7cf815c7, 0x7dfa13cf, 0x7efc19d7, 0x7ffe1fdf,
                    0x801d2774, 0x811f217c, 0x82192b64, 0x831b2d6c,
                    0x84153f54, 0x8517395c, 0x86113344, 0x8713354c,
                    0x880d1734, 0x890f113c, 0x8a091b24, 0x8b0b1d2c,
                    0x8c050f14, 0x8d07091c, 0x8e010304, 0x8f03050c,
                    0x903d47f4, 0x913f41fc, 0x92394be4, 0x933b4dec,
                    0x94355fd4, 0x953759dc, 0x963153c4, 0x973355cc,
                    0x982d77b4, 0x992f71bc, 0x9a297ba4, 0x9b2b7dac,
                    0x9c256f94, 0x9d27699c, 0x9e216384, 0x9f23658c,
                    0xa05de769, 0xa15fe161, 0xa259eb79, 0xa35bed71,
                    0xa455ff49, 0xa557f941, 0xa651f359, 0xa753f551,
                    0xa84dd729, 0xa94fd121, 0xaa49db39, 0xab4bdd31,
                    0xac45cf09, 0xad47c901, 0xae41c319, 0xaf43c511,
                    0xb07d87e9, 0xb17f81e1, 0xb2798bf9, 0xb37b8df1,
                    0xb4759fc9, 0xb57799c1, 0xb67193d9, 0xb77395d1,
                    0xb86db7a9, 0xb96fb1a1, 0xba69bbb9, 0xbb6bbdb1,
                    0xbc65af89, 0xbd67a981, 0xbe61a399, 0xbf63a591,
                    0xc09dba4e, 0xc19fbc46, 0xc299b65e, 0xc39bb056,
                    0xc495a26e, 0xc597a466, 0xc691ae7e, 0xc793a876,
                    0xc88d8a0e, 0xc98f8c06, 0xca89861e, 0xcb8b8016,
                    0xcc85922e, 0xcd879426, 0xce819e3e, 0xcf839836,
                    0xd0bddace, 0xd1bfdcc6, 0xd2b9d6de, 0xd3bbd0d6,
                    0xd4b5c2ee, 0xd5b7c4e6, 0xd6b1cefe, 0xd7b3c8f6,
                    0xd8adea8e, 0xd9afec86, 0xdaa9e69e, 0xdbabe096,
                    0xdca5f2ae, 0xdda7f4a6, 0xdea1febe, 0xdfa3f8b6,
                    0xe0dd7a53, 0xe1df7c5b, 0xe2d97643, 0xe3db704b,
                    0xe4d56273, 0xe5d7647b, 0xe6d16e63, 0xe7d3686b,
                    0xe8cd4a13, 0xe9cf4c1b, 0xeac94603, 0xebcb400b,
                    0xecc55233, 0xedc7543b, 0xeec15e23, 0xefc3582b,
                    0xf0fd1ad3, 0xf1ff1cdb, 0xf2f916c3, 0xf3fb10cb,
                    0xf4f502f3, 0xf5f704fb, 0xf6f10ee3, 0xf7f308eb,
                    0xf8ed2a93, 0xf9ef2c9b, 0xfae92683, 0xfbeb208b,
                    0xfce532b3, 0xfde734bb, 0xfee13ea3, 0xffe338ab,
            };

    /**
     * The round constants.
     */
    private static final int[] RC = {
                    0xa7d3e671, 0xd0ac4d79, 0x3ac991fc, 0x1e4754bd,
                    0x8ca57afb, 0x63b8ddd4, 0xe5b3c5be, 0xa9880ca2,
                    0x39df29da, 0x2ba8cb4c, 0x4b22aa24, 0x4170a6f9,
                    0x5ae2b036, 0x7de433ff, 0x6020088b, 0x5eab7f78,
                    0x7c2c57d2, 0xdc6d7e0d, 0x5394c328
    };

    /**
     * Create the Anubis key schedule for a given cipher key. Both encryption and decryption key
     * schedules are generated.
     *
     * @param key The 32N-bit cipher key.
     */
    private void nessieKeysetup(final byte[] key) {
        final int[] kappa = new int[nState];
        final int[] inter = new int[nState];
        final int[][] roundKeyEnc = new int[rState + 1][4];

        /*
         * map cipher key to initial key state (mu):
         */
        for (int i = 0, pos = 0; i < nState; i++, pos += 4) {
            kappa[i] = (key[pos] << 24)
                    ^ (key[pos + 1] << 16)
                    ^ (key[pos + 2] << 8)
                    ^ (key[pos + 3]);
        }

        /*
         * generate R + 1 round keys:
         */
        for (int r = 0; r <= rState; r++) {
            /*
             * generate r-th round key K^r:
             */
            int k0 = T4[(kappa[nState - 1] >>> 24)];
            int k1 = T4[(kappa[nState - 1] >>> 16) & 0xff];
            int k2 = T4[(kappa[nState - 1] >>> 8) & 0xff];
            int k3 = T4[(kappa[nState - 1]) & 0xff];
            for (int i = nState - 2; i >= 0; i--) {
                k0 = T4[(kappa[i] >>> 24)]
                        ^ (T5[(k0 >>> 24)] & 0xff000000)
                        ^ (T5[(k0 >>> 16) & 0xff] & 0x00ff0000)
                        ^ (T5[(k0 >>> 8) & 0xff] & 0x0000ff00)
                        ^ (T5[(k0) & 0xff] & 0x000000ff);
                k1 = T4[(kappa[i] >>> 16) & 0xff]
                        ^ (T5[(k1 >>> 24)] & 0xff000000)
                        ^ (T5[(k1 >>> 16) & 0xff] & 0x00ff0000)
                        ^ (T5[(k1 >>> 8) & 0xff] & 0x0000ff00)
                        ^ (T5[(k1) & 0xff] & 0x000000ff);
                k2 = T4[(kappa[i] >>> 8) & 0xff]
                        ^ (T5[(k2 >>> 24)] & 0xff000000)
                        ^ (T5[(k2 >>> 16) & 0xff] & 0x00ff0000)
                        ^ (T5[(k2 >>> 8) & 0xff] & 0x0000ff00)
                        ^ (T5[(k2) & 0xff] & 0x000000ff);
                k3 = T4[(kappa[i]) & 0xff]
                        ^ (T5[(k3 >>> 24)] & 0xff000000)
                        ^ (T5[(k3 >>> 16) & 0xff] & 0x00ff0000)
                        ^ (T5[(k3 >>> 8) & 0xff] & 0x0000ff00)
                        ^ (T5[(k3) & 0xff] & 0x000000ff);
            }
            /*
             * -- this is the code to use with the large U tables: K0 = K1 = K2 = K3 = 0; for (i =
             * 0; i < N; i++) { K0 ^= U[i][(kappa[i] >> 24) ]; K1 ^= U[i][(kappa[i] >> 16) & 0xff];
             * K2 ^= U[i][(kappa[i] >> 8) & 0xff]; K3 ^= U[i][(kappa[i] ) & 0xff]; }
             */
            roundKeyEnc[r][0] = k0;
            roundKeyEnc[r][1] = k1;
            roundKeyEnc[r][2] = k2;
            roundKeyEnc[r][3] = k3;

            /*
             * compute kappa^{r+1} from kappa^r:
             */
            if (r == rState) {
                break;
            }
            for (int i = 0; i < nState; i++) {
                int j = i;
                inter[i] = T0[(kappa[j--] >>> 24)];
                if (j < 0) {
                    j = nState - 1;
                }
                inter[i] ^= T1[(kappa[j--] >>> 16) & 0xff];
                if (j < 0) {
                    j = nState - 1;
                }
                inter[i] ^= T2[(kappa[j--] >>> 8) & 0xff];
                if (j < 0) {
                    j = nState - 1;
                }
                inter[i] ^= T3[(kappa[j]) & 0xff];
            }
            kappa[0] = inter[0] ^ RC[r];
            for (int i = 1; i < nState; i++) {
                kappa[i] = inter[i];
            }
        }
        roundKey = roundKeyEnc;

        /* If we are decrypting */
        if (!forEncryption) {
            /*
             * generate inverse key schedule: K'^0 = K^R, K'^R = K^0, K'^r = theta(K^{R-r}):
             */
            final int[][] roundKeyDec = new int[rState + 1][4];
            for (int i = 0; i < 4; i++) {
                roundKeyDec[0][i] = roundKeyEnc[rState][i];
                roundKeyDec[rState][i] = roundKeyEnc[0][i];
            }
            for (int r = 1; r < rState; r++) {
                for (int i = 0; i < 4; i++) {
                    final int v = roundKeyEnc[rState - r][i];
                    roundKeyDec[r][i] = T0[T4[(v >>> 24)] & 0xff]
                            ^ T1[T4[(v >>> 16) & 0xff] & 0xff]
                            ^ T2[T4[(v >>> 8) & 0xff] & 0xff]
                            ^ T3[T4[(v) & 0xff] & 0xff];
                }
            }
            roundKey = roundKeyDec;
        }
    }

    /**
     * Either encrypt or decrypt a data block, according to the key schedule.
     *
     * @param inBuffer the input buffer.
     * @param inOff the input offset.
     * @param outBuffer the output buffer.
     * @param outOff the output offset
     */
    private void crypt(final byte[] inBuffer,
                       final int inOff,
                       final byte[] outBuffer,
                       final int outOff) {
        final int[] state = new int[4];
        final int[] inter = new int[4];

        /*
         * map plaintext block to cipher state (mu) and add initial round key (sigma[K^0]):
         */
        for (int i = 0, pos = inOff; i < 4; i++, pos += 4) {
            state[i] = (inBuffer[pos] << 24)
                    ^ ((inBuffer[pos + 1] << 16) & 0xff0000)
                    ^ ((inBuffer[pos + 2] << 8) & 0xff00)
                    ^ ((inBuffer[pos + 3]) & 0xff)
                    ^ roundKey[0][i];
        }

        /*
         * R - 1 full rounds:
         */
        for (int r = 1; r < rState; r++) {
            inter[0] = T0[(state[0] >>> 24)]
                    ^ T1[(state[1] >>> 24)]
                    ^ T2[(state[2] >>> 24)]
                    ^ T3[(state[3] >>> 24)]
                    ^ roundKey[r][0];
            inter[1] = T0[(state[0] >>> 16) & 0xff]
                    ^ T1[(state[1] >>> 16) & 0xff]
                    ^ T2[(state[2] >>> 16) & 0xff]
                    ^ T3[(state[3] >>> 16) & 0xff]
                    ^ roundKey[r][1];
            inter[2] = T0[(state[0] >>> 8) & 0xff]
                    ^ T1[(state[1] >>> 8) & 0xff]
                    ^ T2[(state[2] >>> 8) & 0xff]
                    ^ T3[(state[3] >>> 8) & 0xff]
                    ^ roundKey[r][2];
            inter[3] = T0[(state[0]) & 0xff]
                    ^ T1[(state[1]) & 0xff]
                    ^ T2[(state[2]) & 0xff]
                    ^ T3[(state[3]) & 0xff]
                    ^ roundKey[r][3];
            state[0] = inter[0];
            state[1] = inter[1];
            state[2] = inter[2];
            state[3] = inter[3];
        }

        /*
         * last round:
         */
        inter[0] = (T0[(state[0] >>> 24)] & 0xff000000)
                ^ (T1[(state[1] >>> 24)] & 0x00ff0000)
                ^ (T2[(state[2] >>> 24)] & 0x0000ff00)
                ^ (T3[(state[3] >>> 24)] & 0x000000ff)
                ^ roundKey[rState][0];
        inter[1] = (T0[(state[0] >>> 16) & 0xff] & 0xff000000)
                ^ (T1[(state[1] >>> 16) & 0xff] & 0x00ff0000)
                ^ (T2[(state[2] >>> 16) & 0xff] & 0x0000ff00)
                ^ (T3[(state[3] >>> 16) & 0xff] & 0x000000ff)
                ^ roundKey[rState][1];
        inter[2] = (T0[(state[0] >>> 8) & 0xff] & 0xff000000)
                ^ (T1[(state[1] >>> 8) & 0xff] & 0x00ff0000)
                ^ (T2[(state[2] >>> 8) & 0xff] & 0x0000ff00)
                ^ (T3[(state[3] >>> 8) & 0xff] & 0x000000ff)
                ^ roundKey[rState][2];
        inter[3] = (T0[(state[0]) & 0xff] & 0xff000000)
                ^ (T1[(state[1]) & 0xff] & 0x00ff0000)
                ^ (T2[(state[2]) & 0xff] & 0x0000ff00)
                ^ (T3[(state[3]) & 0xff] & 0x000000ff)
                ^ roundKey[rState][3];

        /*
         * map cipher state to ciphertext block (mu^{-1}):
         */
        for (int i = 0, pos = outOff; i < 4; i++, pos += 4) {
            final int w = inter[i];
            outBuffer[pos] = (byte) (w >> 24);
            outBuffer[pos + 1] = (byte) (w >> 16);
            outBuffer[pos + 2] = (byte) (w >> 8);
            outBuffer[pos + 3] = (byte) (w);
        }
    }

    @Override
    public String getAlgorithmName() {
        return "Anubis";
    }

    @Override
    public int getBlockSize() {
        return BLOCKSIZEB;
    }

    @Override
    public void init(final boolean forEncrypt, final CipherParameters pParameters) {
        /* Reject invalid parameters */
        if (!(pParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to Anubis init - "
                    + pParameters.getClass().getName());
        }

        /* Determine keySize */
        final byte[] keyBytes = ((KeyParameter) pParameters).getKey();
        final int keyBitSize = keyBytes.length * Byte.SIZE;
        if (keyBitSize != 128 && keyBitSize != 192 && keyBitSize != 256) {
            throw new IllegalArgumentException("KeyBitSize must be 128, 192 or 256");
        }

        /* Record parameters */
        keyBits = keyBitSize;
        nState = keyBits >> 5;
        rState = 8 + nState;

        /* Set up key */
        this.forEncryption = forEncrypt;
        nessieKeysetup(keyBytes);
    }

    @Override
    public int processBlock(final byte[] in, final int inOff, final byte[] out, final int outOff) {
        if (forEncryption == null) {
            throw new IllegalStateException("Anubis engine not initialised");
        }
        if (inOff > (in.length - BLOCKSIZEB)) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff > (out.length - BLOCKSIZEB)) {
            throw new OutputLengthException("output buffer too short");
        }
        crypt(in, inOff, out, outOff);
        return BLOCKSIZEB;
    }

    @Override
    public void reset() {
        /* NoOp */
    }
}
