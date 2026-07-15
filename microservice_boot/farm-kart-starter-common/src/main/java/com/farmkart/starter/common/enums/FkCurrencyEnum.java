package com.farmkart.starter.common.enums;

import java.math.BigDecimal;

/**
 * ISO 4217 currency codes used across Farm Kart (orders, payments, notifications).
 * Default platform currency: {@link #INR}.
 */
public enum FkCurrencyEnum implements StringValuedEnum {

    AED("AED", "UAE Dirham", "د.إ"),
    AFN("AFN", "Afghan Afghani", "؋"),
    ALL("ALL", "Albanian Lek", "L"),
    AMD("AMD", "Armenian Dram", "֏"),
    ANG("ANG", "Netherlands Antillean Guilder", "ƒ"),
    AOA("AOA", "Angolan Kwanza", "Kz"),
    ARS("ARS", "Argentine Peso", "$"),
    AUD("AUD", "Australian Dollar", "A$"),
    AWG("AWG", "Aruban Florin", "ƒ"),
    AZN("AZN", "Azerbaijani Manat", "₼"),
    BAM("BAM", "Bosnia-Herzegovina Convertible Mark", "KM"),
    BBD("BBD", "Barbadian Dollar", "Bds$"),
    BDT("BDT", "Bangladeshi Taka", "৳"),
    BGN("BGN", "Bulgarian Lev", "лв"),
    BHD("BHD", "Bahraini Dinar", ".د.ب"),
    BIF("BIF", "Burundian Franc", "FBu"),
    BMD("BMD", "Bermudian Dollar", "$"),
    BND("BND", "Brunei Dollar", "B$"),
    BOB("BOB", "Bolivian Boliviano", "Bs."),
    BRL("BRL", "Brazilian Real", "R$"),
    BSD("BSD", "Bahamian Dollar", "B$"),
    BTN("BTN", "Bhutanese Ngultrum", "Nu."),
    BWP("BWP", "Botswana Pula", "P"),
    BYN("BYN", "Belarusian Ruble", "Br"),
    BZD("BZD", "Belize Dollar", "BZ$"),
    CAD("CAD", "Canadian Dollar", "C$"),
    CDF("CDF", "Congolese Franc", "FC"),
    CHF("CHF", "Swiss Franc", "CHF"),
    CLP("CLP", "Chilean Peso", "$"),
    CNY("CNY", "Chinese Yuan", "¥"),
    COP("COP", "Colombian Peso", "$"),
    CRC("CRC", "Costa Rican Colón", "₡"),
    CUP("CUP", "Cuban Peso", "$"),
    CVE("CVE", "Cape Verdean Escudo", "$"),
    CZK("CZK", "Czech Koruna", "Kč"),
    DJF("DJF", "Djiboutian Franc", "Fdj"),
    DKK("DKK", "Danish Krone", "kr"),
    DOP("DOP", "Dominican Peso", "RD$"),
    DZD("DZD", "Algerian Dinar", "د.ج"),
    EGP("EGP", "Egyptian Pound", "E£"),
    ERN("ERN", "Eritrean Nakfa", "Nfk"),
    ETB("ETB", "Ethiopian Birr", "Br"),
    EUR("EUR", "Euro", "€"),
    FJD("FJD", "Fijian Dollar", "FJ$"),
    FKP("FKP", "Falkland Islands Pound", "£"),
    GBP("GBP", "British Pound Sterling", "£"),
    GEL("GEL", "Georgian Lari", "₾"),
    GHS("GHS", "Ghanaian Cedi", "₵"),
    GIP("GIP", "Gibraltar Pound", "£"),
    GMD("GMD", "Gambian Dalasi", "D"),
    GNF("GNF", "Guinean Franc", "FG"),
    GTQ("GTQ", "Guatemalan Quetzal", "Q"),
    GYD("GYD", "Guyanaese Dollar", "G$"),
    HKD("HKD", "Hong Kong Dollar", "HK$"),
    HNL("HNL", "Honduran Lempira", "L"),
    HTG("HTG", "Haitian Gourde", "G"),
    HUF("HUF", "Hungarian Forint", "Ft"),
    IDR("IDR", "Indonesian Rupiah", "Rp"),
    ILS("ILS", "Israeli New Shekel", "₪"),
    INR("INR", "Indian Rupee", "₹"),
    IQD("IQD", "Iraqi Dinar", "ع.د"),
    IRR("IRR", "Iranian Rial", "﷼"),
    ISK("ISK", "Icelandic Króna", "kr"),
    JMD("JMD", "Jamaican Dollar", "J$"),
    JOD("JOD", "Jordanian Dinar", "JD"),
    JPY("JPY", "Japanese Yen", "¥"),
    KES("KES", "Kenyan Shilling", "KSh"),
    KGS("KGS", "Kyrgyzstani Som", "с"),
    KHR("KHR", "Cambodian Riel", "៛"),
    KMF("KMF", "Comorian Franc", "CF"),
    KRW("KRW", "South Korean Won", "₩"),
    KWD("KWD", "Kuwaiti Dinar", "د.ك"),
    KYD("KYD", "Cayman Islands Dollar", "CI$"),
    KZT("KZT", "Kazakhstani Tenge", "₸"),
    LAK("LAK", "Lao Kip", "₭"),
    LBP("LBP", "Lebanese Pound", "L£"),
    LKR("LKR", "Sri Lankan Rupee", "Rs"),
    LRD("LRD", "Liberian Dollar", "L$"),
    LSL("LSL", "Lesotho Loti", "L"),
    LYD("LYD", "Libyan Dinar", "ل.د"),
    MAD("MAD", "Moroccan Dirham", "د.م."),
    MDL("MDL", "Moldovan Leu", "L"),
    MGA("MGA", "Malagasy Ariary", "Ar"),
    MKD("MKD", "Macedonian Denar", "ден"),
    MMK("MMK", "Myanmar Kyat", "K"),
    MNT("MNT", "Mongolian Tögrög", "₮"),
    MOP("MOP", "Macanese Pataca", "MOP$"),
    MRU("MRU", "Mauritanian Ouguiya", "UM"),
    MUR("MUR", "Mauritian Rupee", "₨"),
    MVR("MVR", "Maldivian Rufiyaa", "Rf"),
    MWK("MWK", "Malawian Kwacha", "MK"),
    MXN("MXN", "Mexican Peso", "MX$"),
    MYR("MYR", "Malaysian Ringgit", "RM"),
    MZN("MZN", "Mozambican Metical", "MT"),
    NAD("NAD", "Namibian Dollar", "N$"),
    NGN("NGN", "Nigerian Naira", "₦"),
    NIO("NIO", "Nicaraguan Córdoba", "C$"),
    NOK("NOK", "Norwegian Krone", "kr"),
    NPR("NPR", "Nepalese Rupee", "₨"),
    NZD("NZD", "New Zealand Dollar", "NZ$"),
    OMR("OMR", "Omani Rial", "ر.ع."),
    PAB("PAB", "Panamanian Balboa", "B/."),
    PEN("PEN", "Peruvian Sol", "S/"),
    PGK("PGK", "Papua New Guinean Kina", "K"),
    PHP("PHP", "Philippine Peso", "₱"),
    PKR("PKR", "Pakistani Rupee", "₨"),
    PLN("PLN", "Polish Złoty", "zł"),
    PYG("PYG", "Paraguayan Guaraní", "₲"),
    QAR("QAR", "Qatari Rial", "ر.ق"),
    RON("RON", "Romanian Leu", "lei"),
    RSD("RSD", "Serbian Dinar", "дин."),
    RUB("RUB", "Russian Ruble", "₽"),
    RWF("RWF", "Rwandan Franc", "FRw"),
    SAR("SAR", "Saudi Riyal", "ر.س"),
    SBD("SBD", "Solomon Islands Dollar", "SI$"),
    SCR("SCR", "Seychellois Rupee", "₨"),
    SDG("SDG", "Sudanese Pound", "ج.س."),
    SEK("SEK", "Swedish Krona", "kr"),
    SGD("SGD", "Singapore Dollar", "S$"),
    SHP("SHP", "Saint Helena Pound", "£"),
    SLE("SLE", "Sierra Leonean Leone", "Le"),
    SOS("SOS", "Somali Shilling", "Sh"),
    SRD("SRD", "Surinamese Dollar", "$"),
    SSP("SSP", "South Sudanese Pound", "£"),
    STN("STN", "São Tomé and Príncipe Dobra", "Db"),
    SYP("SYP", "Syrian Pound", "£"),
    SZL("SZL", "Swazi Lilangeni", "E"),
    THB("THB", "Thai Baht", "฿"),
    TJS("TJS", "Tajikistani Somoni", "SM"),
    TMT("TMT", "Turkmenistani Manat", "m"),
    TND("TND", "Tunisian Dinar", "د.ت"),
    TRY("TRY", "Turkish Lira", "₺"),
    TTD("TTD", "Trinidad and Tobago Dollar", "TT$"),
    TWD("TWD", "New Taiwan Dollar", "NT$"),
    TZS("TZS", "Tanzanian Shilling", "TSh"),
    UAH("UAH", "Ukrainian Hryvnia", "₴"),
    UGX("UGX", "Ugandan Shilling", "USh"),
    USD("USD", "US Dollar", "$"),
    UYU("UYU", "Uruguayan Peso", "$U"),
    UZS("UZS", "Uzbekistani Som", "so'm"),
    VES("VES", "Venezuelan Bolívar", "Bs.S"),
    VND("VND", "Vietnamese Dong", "₫"),
    VUV("VUV", "Vanuatu Vatu", "VT"),
    WST("WST", "Samoan Tala", "WS$"),
    XAF("XAF", "Central African CFA Franc", "FCFA"),
    XCD("XCD", "East Caribbean Dollar", "EC$"),
    XOF("XOF", "West African CFA Franc", "CFA"),
    XPF("XPF", "CFP Franc", "₣"),
    YER("YER", "Yemeni Rial", "﷼"),
    ZAR("ZAR", "South African Rand", "R"),
    ZMW("ZMW", "Zambian Kwacha", "ZK"),
    ZWL("ZWL", "Zimbabwean Dollar", "Z$");

    private final String value;
    private final String displayName;
    private final String symbol;

    FkCurrencyEnum(String value, String displayName, String symbol) {
        this.value = value;
        this.displayName = displayName;
        this.symbol = symbol;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    /** Platform default for Indian agricultural marketplace. */
    public static FkCurrencyEnum getDefault() {
        return INR;
    }

    public static FkCurrencyEnum getFkCurrencyEnum(String value) {
        return StringValuedEnumSupport.fromValue(FkCurrencyEnum.class, value);
    }

    /** Returns enum for code or {@link #getDefault()} when unknown/null. */
    public static FkCurrencyEnum resolve(String value) {
        FkCurrencyEnum resolved = getFkCurrencyEnum(value);
        return resolved != null ? resolved : getDefault();
    }

    public String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return symbol;
        }
        return symbol + " " + amount.toPlainString();
    }

    public String formatAmount(String plainAmount) {
        return symbol + " " + (plainAmount != null ? plainAmount : "0");
    }
}
