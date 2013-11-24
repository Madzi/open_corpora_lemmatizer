package org.test.lemmatizer;

public final class LinkTypes {

    private static final LinkType[] LOOKUP_TABLE = createLookupTable();

    private LinkTypes() {
    }

    public static LinkType byId(int id) {
        LinkType linkType = LOOKUP_TABLE[id];
        if (linkType == null) {
            throw new IllegalArgumentException("Unknown link type for id=" + id);
        } else {
            return linkType;
        }
    }

    private static LinkType[] createLookupTable() {
        int maxId = Integer.MIN_VALUE;
        for (LinkType linkType : LinkType.values()) {
            if (linkType.getId() > maxId) {
                maxId = linkType.getId();
            }
        }

        LinkType[] linkTypes = new LinkType[maxId + 1];
        for (LinkType linkType : LinkType.values()) {
            linkTypes[linkType.getId()] = linkType;
        }

        return linkTypes;
    }


}
