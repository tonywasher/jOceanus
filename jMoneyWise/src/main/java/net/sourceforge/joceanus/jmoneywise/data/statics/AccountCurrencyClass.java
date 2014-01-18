/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import java.util.Currency;

import net.sourceforge.joceanus.jdatamodels.data.StaticInterface;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of AccountCurrency Classes.
 */
public enum AccountCurrencyClass implements StaticInterface {
    /**
     * British Pounds.
     */
    GBP(1, 0),

    /**
     * US Dollars.
     */
    USD(2, 1),

    /**
     * EU Euro.
     */
    EUR(3, 2),

    /**
     * Canadian Dollars.
     */
    CAD(4, 3),

    /**
     * Australian Dollars.
     */
    AUD(5, 4),

    /**
     * NewZealand Dollars.
     */
    NZD(6, 5),

    /**
     * Chinese Yuan.
     */
    CNY(7, 6),

    /**
     * Japanese Yen.
     */
    JPY(8, 7),

    /**
     * HongKong Dollars.
     */
    HKD(9, 8),

    /**
     * South Korean Won.
     */
    KRW(10, 9),

    /**
     * Indian Rupee.
     */
    INR(11, 10),

    /**
     * Russian Rouble.
     */
    RUB(12, 11),

    /**
     * SouthAfrican Rand.
     */
    ZAR(13, 12),

    /**
     * Brazilian Real.
     */
    BRL(14, 13),

    /**
     * Albanian Lek.
     */
    ALL(15, 14),

    /**
     * Armenian Dram.
     */
    AMD(16, 15),

    /**
     * Azerbaijan New Manat.
     */
    AZN(17, 16),

    /**
     * Bosnia/Herzegovina Marka.
     */
    BAM(18, 17),

    /**
     * Bulgarian Lev.
     */
    BGN(19, 18),

    /**
     * Belarus Rouble.
     */
    BYR(20, 19),

    /**
     * Swiss Franc.
     */
    CHF(21, 20),

    /**
     * Czech Koruna.
     */
    CZK(22, 21),

    /**
     * Danish Krona.
     */
    DKK(23, 22),

    /**
     * Georgian Lari.
     */
    GEL(24, 23),

    /**
     * Hungarian Forint.
     */
    HUF(25, 24),

    /**
     * Iceland Koruna.
     */
    ISK(26, 25),

    /**
     * Lithuanian Litas.
     */
    LTL(27, 26),

    /**
     * Latvian Lat.
     */
    LVL(28, 29),

    /**
     * Moldovan Leu.
     */
    MDL(29, 28),

    /**
     * Macedonian Denar.
     */
    MKD(30, 29),

    /**
     * Norwegian Krona.
     */
    NOK(31, 30),

    /**
     * Polish Zloty.
     */
    PLN(32, 31),

    /**
     * Romanian New Leu.
     */
    RON(33, 32),

    /**
     * Serbian Dinar.
     */
    RSD(34, 33),

    /**
     * Swedish Krona.
     */
    SEK(35, 34),

    /**
     * Ukrainian Hryvna.
     */
    UAH(36, 35),

    /**
     * UAE Dinar.
     */
    AED(37, 36),

    /**
     * Afghanistan Afghani.
     */
    AFN(38, 37),

    /**
     * Bangladesh Taka.
     */
    BDT(39, 38),

    /**
     * Bahrain Dinar.
     */
    BHD(40, 39),

    /**
     * Brunei Dollar.
     */
    BND(41, 40),

    /**
     * Bhutan Ngultrum.
     */
    BTN(42, 41),

    /**
     * Indonesian Rupiah.
     */
    IDR(43, 42),

    /**
     * Israeli Shekel.
     */
    ILS(44, 43),

    /**
     * Iraqi Dinar.
     */
    IQD(45, 44),

    /**
     * Iranian Rial.
     */
    IRR(46, 45),

    /**
     * Jordan Dinar.
     */
    JOD(47, 46),

    /**
     * Kyrgystan Som.
     */
    KGS(48, 47),

    /**
     * Cambodian Riel.
     */
    KHR(49, 48),

    /**
     * Kuwaiti Dinar.
     */
    KWD(50, 49),

    /**
     * Kazakhstan Tenge.
     */
    KZT(51, 50),

    /**
     * Laos Kip.
     */
    LAK(52, 51),

    /**
     * Lebanon Pound.
     */
    LBP(53, 52),

    /**
     * SriLanka Rupee.
     */
    LKR(54, 53),

    /**
     * Myanmar Yat.
     */
    MMK(55, 54),

    /**
     * Mongolian Tughrik.
     */
    MNT(56, 55),

    /**
     * Macau Pataca.
     */
    MOP(57, 56),

    /**
     * Malaysian Ringgit.
     */
    MYR(58, 576),

    /**
     * Nepal Rupee.
     */
    NPR(59, 58),

    /**
     * Oman Rial.
     */
    OMR(60, 59),

    /**
     * Papua New Guinea Kina.
     */
    PGK(61, 60),

    /**
     * Phillipines Peso.
     */
    PHP(62, 61),

    /**
     * Pakistan Rupee.
     */
    PKR(63, 62),

    /**
     * Qatar Rial.
     */
    QAR(64, 63),

    /**
     * Saudi Riyal.
     */
    SAR(65, 64),

    /**
     * Singapore Dollar.
     */
    SGD(66, 65),

    /**
     * Syrian Pound.
     */
    SYP(67, 66),

    /**
     * Thailand Baht.
     */
    THB(68, 67),

    /**
     * Tajikstan Somoni.
     */
    TJS(69, 68),

    /**
     * Turkmenistan Manat.
     */
    TMT(70, 69),

    /**
     * Turkish Lira.
     */
    TRY(71, 70),

    /**
     * Taiwan Dollar.
     */
    TWD(72, 71),

    /**
     * Uzbekistan Som.
     */
    UZS(73, 72),

    /**
     * Vietnam Dong.
     */
    VND(74, 73),

    /**
     * Netherlands Antilles Guilderr.
     */
    ANG(75, 74),

    /**
     * Aruba Florin.
     */
    AWG(76, 75),

    /**
     * Barbados Dollar.
     */
    BBD(77, 76),

    /**
     * Bermuda Dollar.
     */
    BMD(78, 77),

    /**
     * Bahamas Dollar.
     */
    BSD(79, 78),

    /**
     * Belize Dollar.
     */
    BZD(80, 79),

    /**
     * Costa Rica Colon.
     */
    CRC(81, 80),

    /**
     * Cuban Convertable Peso.
     */
    CUC(82, 81),

    /**
     * Cuban Peso.
     */
    CUP(83, 82),

    /**
     * Dominican Republic Peso.
     */
    DOP(84, 83),

    /**
     * Guatemalan Quetzal.
     */
    GTQ(85, 84),

    /**
     * Honduras Lempira.
     */
    HNL(86, 85),

    /**
     * Haitian Gourde.
     */
    HTG(87, 86),

    /**
     * Jamaican Dollar.
     */
    JMD(88, 87),

    /**
     * Cayman Islands Dollar.
     */
    KYD(89, 88),

    /**
     * Mexican Peso.
     */
    MXN(90, 89),

    /**
     * Nicaraguan Cordoba.
     */
    NIO(91, 90),

    /**
     * Panama Balboa.
     */
    PAB(92, 91),

    /**
     * El Salvador Colon.
     */
    SVC(93, 92),

    /**
     * Trinidad/Tobago Dollar.
     */
    TTD(94, 93),

    /**
     * East Caribbean Dollar.
     */
    XCD(95, 94),

    /**
     * Argentinian Peso.
     */
    ARS(96, 95),

    /**
     * Bolivian Boliviano.
     */
    BOB(97, 96),

    /**
     * Chile Peso.
     */
    CLP(98, 97),

    /**
     * Columbian Peso.
     */
    COP(99, 98),

    /**
     * Guinean Franc.
     */
    GNF(100, 99),

    /**
     * Guyanan Dollar.
     */
    GYD(101, 100),

    /**
     * Peru Sol.
     */
    PEN(102, 101),

    /**
     * Paraguayan Guarani.
     */
    PYG(103, 102),

    /**
     * Surinam Dollar.
     */
    SRD(104, 103),

    /**
     * Uruguay Peso.
     */
    UYU(105, 104),

    /**
     * Venezualan Bolivar.
     */
    VEF(106, 105),

    /**
     * Angolan Kwanza.
     */
    AOA(107, 106),

    /**
     * Burundi Franc.
     */
    BIF(108, 107),

    /**
     * Botswana Pula.
     */
    BWP(109, 108),

    /**
     * Congo Franc.
     */
    CDF(110, 109),

    /**
     * Djibouti Franc.
     */
    DJF(111, 110),

    /**
     * Algerian Dinar.
     */
    DZD(112, 111),

    /**
     * Egypt Pound.
     */
    EGP(113, 112),

    /**
     * Eritrea Nakfa.
     */
    ERN(114, 113),

    /**
     * Ethiopian Birr.
     */
    ETB(115, 114),

    /**
     * Ghana Cedi.
     */
    GHS(116, 115),

    /**
     * Gambian Dalasi.
     */
    GMD(117, 116),

    /**
     * Kenyan Shilling.
     */
    KES(118, 117),

    /**
     * Liberian Dollar.
     */
    LRD(119, 118),

    /**
     * Lesotho Loti.
     */
    LSL(120, 119),

    /**
     * Libyan Dinar.
     */
    LYD(121, 120),

    /**
     * Moroccan Dirham.
     */
    MAD(122, 121),

    /**
     * Madagascan Ariary.
     */
    MGA(123, 122),

    /**
     * Mauritian Ouguiya.
     */
    MRO(124, 123),

    /**
     * Malawian Kwacha.
     */
    MWK(125, 124),

    /**
     * Mozambique Metical.
     */
    MZN(126, 125),

    /**
     * Namibia Dollar.
     */
    NAD(127, 126),

    /**
     * Nigeria Naira.
     */
    NGN(128, 127),

    /**
     * Rwanda Franc.
     */
    RWF(129, 128),

    /**
     * Sudan Pound.
     */
    SDG(130, 129),

    /**
     * Somali Shilling.
     */
    SOS(131, 130),

    /**
     * South Sudan Pound.
     */
    // SSP(132, 131),

    /**
     * Sierra Leone Leone.
     */
    SLL(133, 132),

    /**
     * Swazi Lilangeni.
     */
    SZL(134, 133),

    /**
     * Tunisian Dinar.
     */
    TND(135, 134),

    /**
     * Tanzania Shilling.
     */
    TZS(136, 135),

    /**
     * Ugandan Shilling.
     */
    UGX(137, 136),

    /**
     * Central African Franc.
     */
    XAF(138, 137),

    /**
     * West African Franc.
     */
    XOF(139, 138),

    /**
     * Yemeni Rial.
     */
    YER(140, 139),

    /**
     * Zambia Kwacha.
     */
    // ZMW(141, 140),

    /**
     * Cape Verde Escudo.
     */
    CVE(142, 141),

    /**
     * Fijian Dollar.
     */
    FJD(143, 142),

    /**
     * Falkland Islands Pound.
     */
    FKP(144, 143),

    /**
     * Gibraltar Pound.
     */
    GIP(145, 144),

    /**
     * Comoro Franc.
     */
    KMF(146, 145),

    /**
     * Mauritian Rupee.
     */
    MUR(147, 146),

    /**
     * Maldives Rufiyaa.
     */
    MVR(148, 147),

    /**
     * Solomon Isles Dollar.
     */
    SBD(149, 148),

    /**
     * Seychelles Rupee.
     */
    SCR(150, 149),

    /**
     * Saint Helena Pound.
     */
    SHP(151, 150),

    /**
     * Tongan Pa'anga.
     */
    TOP(152, 151),

    /**
     * Samoan Tala.
     */
    WST(153, 152),

    /**
     * Vanuatu Vatu.
     */
    VUV(154, 153),

    /**
     * Pacific Franc.
     */
    XPF(155, 154);

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

    /**
     * Currency.
     */
    private final Currency theCurrency;

    @Override
    public int getClassId() {
        return theId;
    }

    @Override
    public int getOrder() {
        return theOrder;
    }

    /**
     * Obtain currency.
     * @return the currency
     */
    public Currency getCurrency() {
        return theCurrency;
    }

    /**
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     */
    private AccountCurrencyClass(final int uId,
                                 final int uOrder) {
        theId = uId;
        theOrder = uOrder;
        String myName = name();
        theCurrency = Currency.getInstance(myName);
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JOceanusException on error
     */
    public static AccountCurrencyClass fromId(final int id) throws JOceanusException {
        for (AccountCurrencyClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid Account Currency Class Id: "
                                          + id);
    }
}
