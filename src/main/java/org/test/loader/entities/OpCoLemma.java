package org.test.loader.entities;

import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OpCoLemma implements Comparable<OpCoLemma> {

    private int id;

    private int revision;

    private String text;

    private Set<String> grammemes;

    private List<OpCoLemmaForm> forms;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

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

    public List<OpCoLemmaForm> getForms() {
        return forms;
    }

    public void setForms(List<OpCoLemmaForm> forms) {
        this.forms = forms;
    }

    public void addForm(OpCoLemmaForm form) {
        if (forms == null) {
            forms = new ArrayList<OpCoLemmaForm>(6);
        }
        forms.add(form);

        form.setLemmaId(this.id);
    }

    @Override
    public int compareTo(OpCoLemma that) {
        int r = this.text.compareTo(that.text);
        if (r == 0) {
            return Ints.compare(this.id, that.id);
        } else {
            return r;
        }
    }

}
