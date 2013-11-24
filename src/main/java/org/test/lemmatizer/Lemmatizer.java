package org.test.lemmatizer;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Util;
import org.test.fst.IntegerOffsetOutputs;

import java.io.*;
import java.util.*;

public class Lemmatizer {

    private static final int DEFAULT_WORD_LIST_SIZE = 3;

    private Map<Integer, WordNorm> lemmata;

    private FST<Integer> fst;

    private byte[] forms;

    public WordNorm getLemmaById(int id) {
        return lemmata.get(id);
    }

    public List<WordNormForm> search(String word) {
        IntsRef intsRef = new IntsRef(word.length());

        Integer offset;
        try {
            offset = Util.get(fst, Util.toUTF16(word, intsRef));
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IO error", e);
        }

        if (offset == null) {
            return null;
        }

        List<WordNormForm> norms = null;
        while (true) {
            int id = Ints.fromBytes(forms[offset++], forms[offset++], forms[offset++], forms[offset++]);

            WordNorm norm = lemmata.get(id);
            if (norm == null) {
                break;
            }

            boolean hasNext = forms[offset++] != 0;

            long mask1 = Longs.fromBytes(
                forms[offset++], forms[offset++], forms[offset++], forms[offset++],
                forms[offset++], forms[offset++], forms[offset++], forms[offset++]);
            long mask2 = Longs.fromBytes(
                forms[offset++], forms[offset++], forms[offset++], forms[offset++],
                forms[offset++], forms[offset++], forms[offset++], forms[offset++]);

            WordNormForm wordNormForm = new WordNormForm(norm, mask1, mask2);

            if (norms == null) {
                if (hasNext) {
                    norms = new ArrayList<WordNormForm>(DEFAULT_WORD_LIST_SIZE);
                    norms.add(wordNormForm);
                } else {
                    norms = Collections.singletonList(wordNormForm);
                }
            } else {
                norms.add(wordNormForm);
            }

            if (!hasNext) {
                break;
            }
        }

        return (norms != null) ? norms : Collections.<WordNormForm>emptyList();
    }

    public void load(File normFile, File formFile, File fstFile) throws IOException {
        Map<Integer, WordNorm> lemmata = loadLemmata(normFile);
        byte[] forms = loadForms(formFile);
        this.fst = loadFST(fstFile);
        this.lemmata = lemmata;
        this.forms = forms;
    }

    public byte[] loadForms(File file) throws IOException {
        InputStream fileInputStream = new FileInputStream(file);
        try {
            byte[] bytes = new byte[(int) file.length()];
            IOUtils.readFully(fileInputStream, bytes);
            return bytes;
        } finally {
            fileInputStream.close();
        }
    }

    private Map<Integer, WordNorm> loadLemmata(File file) throws IOException {
        InputStream fileInputStream = new FileInputStream(file);
        try {
            InputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            try {
                DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
                try {
                    return loadLemmata(dataInputStream);
                } finally {
                    dataInputStream.close();
                }
            } finally {
                bufferedInputStream.close();
            }
        } finally {
            fileInputStream.close();
        }
    }

    private Map<Integer, WordNorm> loadLemmata(DataInputStream inputStream) throws IOException {
        int size = inputStream.readInt();

        Map<Integer, WordNorm> map = new HashMap<Integer, WordNorm>(size);
        for (int i = 0; i < size; i++) {
            int id = inputStream.readInt();

            long mask1 = inputStream.readLong();
            long mask2 = inputStream.readLong();

            int[] linkIds = ArrayUtils.EMPTY_INT_ARRAY;
            byte[] linkTypes = ArrayUtils.EMPTY_BYTE_ARRAY;
            byte linkCount = inputStream.readByte();
            if (linkCount > 0) {
                linkIds = new int[linkCount];
                linkTypes = new byte[linkCount];
                for (int l = 0; l < linkCount; l++) {
                    linkIds[l] = inputStream.readInt();
                    linkTypes[l] = inputStream.readByte();
                }
            }

            String text = inputStream.readUTF();

            map.put(id, new WordNorm(id, text, mask1, mask2, linkIds, linkTypes));
        }

        return map;
    }

    private FST<Integer> loadFST(File file) throws IOException {
        return FST.read(file, IntegerOffsetOutputs.getSingleton());
    }

    public final static class WordNorm {

        private final int id;

        private final String text;

        private final long grammemesMask1;

        private final long grammemesMask2;

        private final int[] linkIds;

        private final byte[] linkTypes;

        private WordNorm(int id, String text, long grammemesMask1, long grammemesMask2, int[] linkIds, byte[] linkTypes) {
            this.id = id;
            this.text = text;
            this.grammemesMask1 = grammemesMask1;
            this.grammemesMask2 = grammemesMask2;
            this.linkIds = linkIds;
            this.linkTypes = linkTypes;
        }

        public int getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public long getGrammemesMask1() {
            return grammemesMask1;
        }

        public long getGrammemesMask2() {
            return grammemesMask2;
        }

        public int[] getLinkIds() {
            return linkIds;
        }

        public byte[] getLinkTypes() {
            return linkTypes;
        }

        public int getLinkSize() {
            return (linkIds != null) ? linkIds.length : 0;
        }
    }

    public final static class WordNormForm {

        private final WordNorm wordNorm;

        private final long grammemesMask1;

        private final long grammemesMask2;

        public WordNormForm(WordNorm wordNorm, long grammemesMask1, long grammemesMask2) {
            this.wordNorm = wordNorm;
            this.grammemesMask1 = grammemesMask1;
            this.grammemesMask2 = grammemesMask2;
        }

        public WordNorm getWordNorm() {
            return wordNorm;
        }

        public long getGrammemesMask1() {
            return grammemesMask1;
        }

        public long getGrammemesMask2() {
            return grammemesMask2;
        }
    }

}
