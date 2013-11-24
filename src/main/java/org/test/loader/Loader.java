package org.test.loader;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.digester3.Digester;
import org.apache.lucene.store.OutputStreamDataOutput;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Util;
import org.test.Constants;
import org.test.fst.IntegerOffsetOutputs;
import org.test.lemmatizer.GrammemeType;
import org.test.lemmatizer.GrammemeTypes;
import org.test.loader.entities.*;
import org.test.loader.util.CharFilterReader;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Run with the parameters -XX:+UseSerialGC -XX:+PrintGC -Xms3072m -Xmx3072m
 */
public class Loader {

    public static void main(String[] arguments) throws Exception {
        Loader loader = new Loader();
        loader.run();
    }

    private void run() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();

        OpCoDictionary dictionary = loadDictionaryFile(Constants.PATH_OPENCORPORA_ARCHIVE);
        System.out.println("Dictionary is loaded. Elapsed " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
        System.gc();

        System.out.println();
        System.out.println("Version      : " + dictionary.getVersion());
        System.out.println("Grammemes    : " + dictionary.getGrammemes().size());
        System.out.println("Restrictions : " + dictionary.getRestrictions().size());
        System.out.println("Links        : " + dictionary.getLinks().size());
        System.out.println("Link types   : " + dictionary.getLinkTypes().size());
        System.out.println("Lemmata      : " + dictionary.getLemmata().size());
        System.out.println();

        storeLemmata(dictionary);
        System.out.println("Norms file is created. Elapsed " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
        System.gc();

        List<OpCoLemmaForm> lemmaForms = storeLemmaForms(dictionary);
        System.out.println("Forms file is created. Elapsed " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
        System.gc();

        storeFST(lemmaForms);
        System.out.println("FST file is created. Elapsed " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");

        System.out.println("DONE");
    }

    private void storeLemmata(OpCoDictionary dictionary) throws Exception {
        OutputStream fileOutputStream = new FileOutputStream(Constants.PATH_OUTPUT_NORM);
        try {
            OutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 16 * 1024);
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
                try {
                    outputLemmaNorms(dataOutputStream, dictionary);
                } finally {
                    dataOutputStream.close();
                }
            } finally {
                bufferedOutputStream.close();
            }
        } finally {
            fileOutputStream.close();
        }
    }

    private void outputLemmaNorms(DataOutputStream outputStream, OpCoDictionary dictionary) throws Exception {
        List<OpCoLink> links = dictionary.getLinks();

        Multimap<Integer, OpCoLink> linkMultimap = ArrayListMultimap.create(dictionary.getLinks().size(), 2);
        for (OpCoLink link : links) {
            linkMultimap.put(link.getTo(), link);
        }

        List<OpCoLemma> lemmata = dictionary.getLemmata();
        Collections.sort(lemmata);

        outputStream.writeInt(lemmata.size());

        for (OpCoLemma lemma : lemmata) {
            outputStream.writeInt(lemma.getId());

            storeGrammemes(outputStream, lemma.getGrammemes());

            Collection<OpCoLink> lemmaLinks = linkMultimap.get(lemma.getId());
            if (CollectionUtils.isNotEmpty(lemmaLinks) && lemmaLinks.size() < Byte.MAX_VALUE) {
                outputStream.writeByte(lemmaLinks.size());
                for (OpCoLink link : lemmaLinks) {
                    outputStream.writeInt(link.getFrom());
                    outputStream.writeByte(link.getType());
                }
            } else {
                outputStream.writeByte(0);
            }

            outputStream.writeUTF(lemma.getText());
        }
    }

    private List<OpCoLemmaForm> storeLemmaForms(OpCoDictionary dictionary) throws IOException {
        OutputStream fileOutputStream = new FileOutputStream(Constants.PATH_OUTPUT_FORM);
        try {
            OutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 16 * 1024);
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
                try {
                    return outputLemmaForms(dataOutputStream, dictionary);
                } finally {
                    dataOutputStream.close();
                }
            } finally {
                bufferedOutputStream.close();
            }
        } finally {
            fileOutputStream.close();
        }
    }

    private List<OpCoLemmaForm> outputLemmaForms(DataOutputStream outputStream, OpCoDictionary dictionary) throws IOException {
        List<OpCoLemmaForm> lemmaForms = new ArrayList<OpCoLemmaForm>(dictionary.getLemmata().size() * 10);
        for (OpCoLemma lemma : dictionary.getLemmata()) {
            for (OpCoLemmaForm lemmaForm : lemma.getForms()) {
                lemmaForms.add(lemmaForm);
            }
        }

        Collections.sort(lemmaForms);

        System.out.println("Lemma forms  : " + lemmaForms.size());

        for (int i = 0; i < lemmaForms.size() - 1; i++) {
            OpCoLemmaForm thisForm = lemmaForms.get(i);
            OpCoLemmaForm nextForm = lemmaForms.get(i + 1);
            thisForm.setNext(thisForm.getText().equals(nextForm.getText()));
        }

        outputStream.writeInt(lemmaForms.size());

        for (OpCoLemmaForm lemmaForm : lemmaForms) {
            lemmaForm.setOffset(outputStream.size());
            outputStream.writeInt(lemmaForm.getLemmaId());
            outputStream.writeBoolean(lemmaForm.hasNext());
            storeGrammemes(outputStream, lemmaForm.getGrammemes());
        }

        return lemmaForms;
    }

    private void storeGrammemes(DataOutputStream outputStream, Set<String> grammemes) throws IOException {
        long mask1 = 0;
        long mask2 = 0;

        if (CollectionUtils.isNotEmpty(grammemes)) {
            for (String grammeme : grammemes) {
                GrammemeType type = GrammemeTypes.byName(grammeme);
                mask1 = type.getMask1(mask1);
                mask2 = type.getMask2(mask2);
            }
        }

        outputStream.writeLong(mask1);
        outputStream.writeLong(mask2);
    }

    private void storeFST(List<OpCoLemmaForm> lemmaForms) throws Exception {
        OutputStream fileOutputStream = new FileOutputStream(Constants.PATH_OUTPUT_FST);
        try {
            OutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 16 * 1024);
            try {
                buildFST(bufferedOutputStream, lemmaForms);
            } finally {
                bufferedOutputStream.close();
            }
        } finally {
            fileOutputStream.close();
        }
    }

    private void buildFST(OutputStream outputStream, List<OpCoLemmaForm> lemmaForms) throws Exception {
        Builder<Integer> builder = new Builder<Integer>(FST.INPUT_TYPE.BYTE2, IntegerOffsetOutputs.getSingleton());

        IntsRef intBuff = new IntsRef(256);
        for (OpCoLemmaForm lemmaForm : lemmaForms) {
            builder.add(Util.toUTF16(lemmaForm.getText(), intBuff), lemmaForm.getOffset());
        }

        FST<Integer> fst = builder.finish();
        if (fst == null) {
            throw new IllegalStateException("FST is empty");
        }

        OutputStreamDataOutput output = new OutputStreamDataOutput(outputStream);
        try {
            fst.save(output);
        } finally {
            output.close();
        }
    }

    private OpCoDictionary loadDictionaryFile(String path) throws Exception {
        InputStream fileInputStream = new FileInputStream(path);
        try {
            InputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 64 * 1024);
            try {
                InputStream bZip2CompressorInputStream = new BZip2CompressorInputStream(bufferedInputStream);
                try {
                    Reader inputStreamReader = new InputStreamReader(bZip2CompressorInputStream, "UTF-8");
                    try {
                        Reader filterReader = new CharFilterReader(inputStreamReader);
                        try {
                            return loadDictionaryReader(filterReader);
                        } finally {
                            filterReader.close();
                        }
                    } finally {
                        inputStreamReader.close();
                    }
                } finally {
                    bZip2CompressorInputStream.close();
                }
            } finally {
                bufferedInputStream.close();
            }
        } finally {
            fileInputStream.close();
        }
    }

    private OpCoDictionary loadDictionaryReader(Reader reader) throws Exception {
        Digester digester = new Digester();
        digester.setValidating(false);

        digester.addObjectCreate("dictionary", OpCoDictionary.class);
        digester.addSetProperties("dictionary",
            new String[] { "version", "revision" },
            new String[] { "version", "revision" });

        digester.addObjectCreate("dictionary/grammemes/grammeme", OpCoGrammeme.class);
        digester.addSetProperties("dictionary/grammemes/grammeme", "parent", "parentName");
        digester.addCallMethod("dictionary/grammemes/grammeme/name", "setName", 0);
        digester.addCallMethod("dictionary/grammemes/grammeme/alias", "setAlias", 0);
        digester.addCallMethod("dictionary/grammemes/grammeme/description", "setDescription", 0);
        digester.addSetNext("dictionary/grammemes/grammeme", "addGrammeme");

        digester.addObjectCreate("dictionary/links/link", OpCoLink.class);
        digester.addSetProperties("dictionary/links/link",
            new String[] { "id", "from", "to", "type" },
            new String[] { "id", "from", "to", "type" });
        digester.addSetNext("dictionary/links/link", "addLink");

        digester.addObjectCreate("dictionary/link_types/type", OpCoLinkType.class);
        digester.addSetProperties("dictionary/link_types/type", "id", "id");
        digester.addCallMethod("dictionary/link_types/type", "setContent", 0);
        digester.addSetNext("dictionary/link_types/type", "addLinkType");

        digester.addObjectCreate("dictionary/restrictions/restr", OpCoRestriction.class);
        digester.addSetProperties("dictionary/restrictions/restr", "type", "type");
        digester.addCallMethod("dictionary/restrictions/restr/left", "setLeftGrammeme", 0);
        digester.addCallMethod("dictionary/restrictions/restr/right", "setRightGrammeme", 0);
        digester.addSetProperties("dictionary/restrictions/restr/left", "type", "leftType");
        digester.addSetProperties("dictionary/restrictions/restr/right", "type", "rightType");
        digester.addSetNext("dictionary/restrictions/restr", "addRestriction");

        digester.addObjectCreate("dictionary/lemmata/lemma", OpCoLemma.class);
        digester.addSetProperties("dictionary/lemmata/lemma",
            new String[]{"id", "rev"},
            new String[]{"id", "revision"});
        digester.addSetProperties("dictionary/lemmata/lemma/l", "t", "text");
        digester.addSetNext("dictionary/lemmata/lemma", "addLemma");

        digester.addObjectCreate("dictionary/lemmata/lemma/l/g", OpCoGrammemeName.class);
        digester.addSetProperties("dictionary/lemmata/lemma/l/g", "v", "name");
        digester.addSetNext("dictionary/lemmata/lemma/l/g", "addGrammeme");

        digester.addObjectCreate("dictionary/lemmata/lemma/f", OpCoLemmaForm.class);
        digester.addSetProperties("dictionary/lemmata/lemma/f", "t", "text");
        digester.addSetNext("dictionary/lemmata/lemma/f", "addForm");

        digester.addObjectCreate("dictionary/lemmata/lemma/f/g", OpCoGrammemeName.class);
        digester.addSetProperties("dictionary/lemmata/lemma/f/g", "v", "name");
        digester.addSetNext("dictionary/lemmata/lemma/f/g", "addGrammeme");

        return digester.parse(reader);
    }

}
