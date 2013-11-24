package org.test.loader.entities;

import java.util.ArrayList;
import java.util.List;

public class OpCoDictionary {

    private String version;

    private String revision;

    private List<OpCoGrammeme> grammemes = new ArrayList<OpCoGrammeme>(128);

    private List<OpCoLink> links = new ArrayList<OpCoLink>(512 * 1024);

    private List<OpCoLinkType> linkTypes = new ArrayList<OpCoLinkType>(64);

    private List<OpCoRestriction> restrictions = new ArrayList<OpCoRestriction>(512);

    private List<OpCoLemma> lemmata = new ArrayList<OpCoLemma>(512 * 1024);

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public List<OpCoGrammeme> getGrammemes() {
        return grammemes;
    }

    public void setGrammemes(List<OpCoGrammeme> grammemes) {
        this.grammemes = grammemes;
    }

    public void addGrammeme(OpCoGrammeme grammeme) {
        grammemes.add(grammeme);
    }

    public List<OpCoLink> getLinks() {
        return links;
    }

    public void setLinks(List<OpCoLink> links) {
        this.links = links;
    }

    public void addLink(OpCoLink link) {
        links.add(link);
    }

    public List<OpCoLinkType> getLinkTypes() {
        return linkTypes;
    }

    public void setLinkTypes(List<OpCoLinkType> linkTypes) {
        this.linkTypes = linkTypes;
    }

    public void addLinkType(OpCoLinkType linkType) {
        linkTypes.add(linkType);
    }

    public List<OpCoRestriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<OpCoRestriction> restrictions) {
        this.restrictions = restrictions;
    }

    public void addRestriction(OpCoRestriction restriction) {
        restrictions.add(restriction);
    }

    public List<OpCoLemma> getLemmata() {
        return lemmata;
    }

    public void setLemmata(List<OpCoLemma> lemmata) {
        this.lemmata = lemmata;
    }

    public void addLemma(OpCoLemma lemma) {
        lemmata.add(lemma);
    }
}
