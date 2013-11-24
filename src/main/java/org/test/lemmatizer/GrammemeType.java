package org.test.lemmatizer;

public enum GrammemeType {

    POST,
    NOUN(POST),
    ADJF(POST),
    ADJS(POST),
    COMP(POST),
    VERB(POST),
    INFN(POST),
    PRTF(POST),
    PRTS(POST),
    GRND(POST),
    NUMR(POST),
    ADVB(POST),
    NPRO(POST),
    PRED(POST),
    PREP(POST),
    CONJ(POST),
    PRCL(POST),
    INTJ(POST),

    ANim,
    anim(ANim),
    inan(ANim),

    GNdr,
    masc(GNdr),
    femn(GNdr),
    neut(GNdr),

    Ms_f("Ms-f"),

    NMbr(),
    sing(NMbr),
    plur(NMbr),

    Sgtm(),
    Pltm(),
    Fixd(),

    CAse(),
    nomn(CAse),
    gent(CAse),
    datv(CAse),
    accs(CAse),
    ablt(CAse),
    loct(CAse),

    voct(nomn),
    gen1(gent),
    gen2(gent),
    acc2(accs),
    loc1(loct),
    loc2(loct),

    Abbr,
    Name,
    Surn,
    Patr,
    Geox,
    Orgn,
    Trad,
    Subx,
    Supr,
    Qual,
    Apro,
    Anum,
    Poss,
    V_ey("V-ey"),
    V_oy("V-oy"),
    Cmp2,
    V_ej("V-ej"),

    ASpc,
    perf(ASpc),
    impf(ASpc),

    TRns,
    tran(TRns),
    intr(TRns),

    Impe,
    Uimp,
    Mult,
    Refl,

    PErs,
    l1per(PErs, "1per"),
    l2per(PErs, "2per"),
    l3per(PErs, "3per"),

    TEns,
    pres(TEns),
    past(TEns),
    futr(TEns),

    MOod,
    indc(MOod),
    impr(MOod),

    INvl,
    incl(INvl),
    excl(INvl),

    VOic,
    actv(VOic),
    pssv(VOic),

    Infr,
    Slng,
    Arch,
    Litr,
    Erro,
    Dist,
    Ques,
    Dmns,
    Prnt,
    V_be("V-be"),
    V_en("V-en"),
    V_ie("V-ie"),
    V_bi("V-bi"),
    Fimp,
    Prdx,
    Coun,
    Coll,
    V_sh("V-sh"),
    Af_p("Af-p"),
    Inmx,
    Vpre;

    private final GrammemeType parent;

    private final String name;

    private final int id;

    private final long mask1;

    private final long mask2;

    private GrammemeType() {
        this(null, null);
    }

    private GrammemeType(GrammemeType parent) {
        this(parent, null);
    }

    private GrammemeType(String name) {
        this(null, name);
    }

    private GrammemeType(GrammemeType parent, String specialName) {
        this.parent = parent;
        this.name = (specialName != null) ? specialName : name();
        this.id = ordinal();
        this.mask1 = (id < 64) ? 1L << id : 0;
        this.mask2 = (id < 64) ? 0 : 1L << (id - 64);
    }

    public GrammemeType getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public long getMask1() {
        return mask1;
    }

    public long getMask2() {
        return mask2;
    }

    public long getMask1(long mask) {
        return mask | mask1;
    }

    public long getMask2(long mask) {
        return mask | mask2;
    }

    public boolean isSet(long mask1, long mask2) {
        return ((this.mask1 & mask1) != 0) || ((this.mask2 & mask2) != 0);
    }

    @Override
    public String toString() {
        return (parent != null) ? parent.toString() + "/" + name : name;
    }
}
