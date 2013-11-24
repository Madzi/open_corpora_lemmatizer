package org.test.lemmatizer;

public enum LinkType {

    ADJF__ADJS(1),

    ADJ__COMP(2),

    INFN__VERB(3),

    INFN__PRTF(4),

    INFN__GRND(5),

    PRTF__PRTS(6),

    NAME__PATR(7),

    PATR_MASC__PATR_FEMN(8),

    SURN_MASC__SURN_FEMN(9),

    SURN_MASC__SURN_PLUR(10),

    PERF__IMPF(11),

    ADJF__SUPR_ejsh(12),

    PATR_MASC_FORM__PATR_MASC_INFR(13),

    PATR_FEMN_FORM__PATR_FEMN_INFR(14),

    ADJF_eish__SUPR_nai_eish(15),

    ADJF__SUPR_ajsh(16),

    ADJF_aish__SUPR_nai_aish(17),

    ADJF__SUPR_suppl(18),

    ADJF__SUPR_nai(19),

    ADJF__SUPR_slng(20),

    FULL_CONTRACTED(21);

    private final int id;

    private LinkType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
