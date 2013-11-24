package org.test.lemmatizer;

import java.util.*;

public final class GrammemeTypes {

    private static final Map<String, GrammemeType> LOOKUP_NAME_MAP = createLookupMap();

    private static final GrammemeType[] LOOKUP_ID_TABLE = createLookupTable();

    private GrammemeTypes() {
    }

    public static GrammemeType byName(String name) {
        GrammemeType type = LOOKUP_NAME_MAP.get(name);
        if (type == null) {
            throw new IllegalArgumentException("Missing type for name=" + name);
        }
        return type;
    }

    public static GrammemeType byId(int id) {
        GrammemeType type = LOOKUP_ID_TABLE[id];
        if (type == null) {
            throw new IllegalArgumentException("Missing type for id=" + id);
        }
        return type;
    }

    public static Set<GrammemeType> byMasks(long mask1, long mask2) {
        Set<GrammemeType> result = null;

        for (GrammemeType type : GrammemeType.values()) {
            if (type.isSet(mask1, mask2)) {
                if (result == null) {
                    result = EnumSet.of(type);
                } else {
                    result.add(type);
                }
            }
        }

        return (result != null) ? result : Collections.<GrammemeType>emptySet();
    }

    private static GrammemeType[] createLookupTable() {
        int maxId = Integer.MIN_VALUE;
        for (GrammemeType type : GrammemeType.values()) {
            if (type.getId() > maxId) {
                maxId = type.getId();
            }
        }

        GrammemeType[] types = new GrammemeType[maxId + 1];
        for (GrammemeType type : GrammemeType.values()) {
            types[type.getId()] = type;
        }

        return types;
    }

    private static Map<String, GrammemeType> createLookupMap() {
        Map<String, GrammemeType> map = new HashMap<String, GrammemeType>(GrammemeType.values().length);

        for (GrammemeType type : GrammemeType.values()) {
            map.put(type.getName(), type);
        }

        return map;
    }
}
