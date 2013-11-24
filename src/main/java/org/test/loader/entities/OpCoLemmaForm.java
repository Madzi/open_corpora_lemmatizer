package org.test.loader.entities;

import com.google.common.primitives.Ints;

import java.util.HashSet;
import java.util.Set;

public class OpCoLemmaForm implements Comparable<OpCoLemmaForm> {

    private String text;

    private Set<String> grammemes;

    private int lemmaId;

    private int offset;

    private boolean next;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<String> getGrammemes() {
        return grammemes;
    }

    public void setGrammemes(Set<String> grammemes) {
        this.grammemes = grammemes;
    }

    public void addGrammeme(String grammeme) {
        if (grammemes == null) {
            grammemes = new HashSet<String>(2);
        }
        grammemes.add(grammeme);
    }

    public void addGrammeme(OpCoGrammemeName grammeme) {
        addGrammeme(grammeme.getName());
    }

    public int getLemmaId() {
        return lemmaId;
    }

    public void setLemmaId(int lemmaId) {
        this.lemmaId = lemmaId;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean hasNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    @Override
    public int compareTo(OpCoLemmaForm that) {
        int r = this.text.compareTo(that.text);
        if (r == 0) {
            return Ints.compare(this.lemmaId, that.lemmaId);
        } else {
            return r;
        }
    }

}
