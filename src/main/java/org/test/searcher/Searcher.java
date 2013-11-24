package org.test.searcher;

import org.apache.commons.collections.CollectionUtils;
import org.test.Constants;
import org.test.lemmatizer.GrammemeTypes;
import org.test.lemmatizer.Lemmatizer;
import org.test.lemmatizer.LinkTypes;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Searcher {

    public static void main(String[] arguments) throws Exception {
        Searcher searcher = new Searcher();
        searcher.run();
    }

    public void run() throws Exception {
        Lemmatizer lemmatizer = new Lemmatizer();
        lemmatizer.load(
            new File(Constants.PATH_OUTPUT_NORM),
            new File(Constants.PATH_OUTPUT_FORM),
            new File(Constants.PATH_OUTPUT_FST)
        );

        search(lemmatizer, "абажурного");
        search(lemmatizer, "абажурной");
        search(lemmatizer, "абажурных");
        search(lemmatizer, "абажурными");

        search(lemmatizer, "положило");
        search(lemmatizer, "клала");

        search(lemmatizer, "стали");
        search(lemmatizer, "неопределенные");
        search(lemmatizer, "астрономические");
        search(lemmatizer, "преобразованные");
        search(lemmatizer, "частичные");
        search(lemmatizer, "античности");
        search(lemmatizer, "достаточными");
        search(lemmatizer, "длинношеее");

        search(lemmatizer, "поедем");
        search(lemmatizer, "поедим");

        search(lemmatizer, "сергеевной");
        search(lemmatizer, "александровича");
        search(lemmatizer, "иваном");
        search(lemmatizer, "московскими");
        search(lemmatizer, "ленинградское");

        search(lemmatizer, "черном");
        search(lemmatizer, "списке");
        search(lemmatizer, "профилем");
        search(lemmatizer, "паролем");
        search(lemmatizer, "логинами");
        search(lemmatizer, "пользователем");
        search(lemmatizer, "пользователями");
        search(lemmatizer, "пользовательские");

        search(lemmatizer, "друзьями");
        search(lemmatizer, "другие");
        search(lemmatizer, "друга");
    }

    private void search(Lemmatizer lemmatizer, String word) {
        System.out.print(word + ": ");

        List<Lemmatizer.WordNormForm> wordNormForms = lemmatizer.search(word);
        if (CollectionUtils.isNotEmpty(wordNormForms)) {
            System.out.println();

            Iterator<Lemmatizer.WordNormForm> wordNormFormIterator = wordNormForms.iterator();
            while (wordNormFormIterator.hasNext()) {
                Lemmatizer.WordNormForm wordNormForm = wordNormFormIterator.next();
                Lemmatizer.WordNorm wordNorm = wordNormForm.getWordNorm();

                System.out.print("  ");
                System.out.print(GrammemeTypes.byMasks(wordNormForm.getGrammemesMask1(), wordNormForm.getGrammemesMask2()));
                System.out.print(" -> ");
                System.out.print(wordNorm.getId() + "/" + wordNorm.getText());
                System.out.print(GrammemeTypes.byMasks(wordNorm.getGrammemesMask1(), wordNorm.getGrammemesMask2()));

                if (wordNorm.getLinkSize() > 0) {
                    int[] linkIds = wordNorm.getLinkIds();
                    byte[] linkTypes = wordNorm.getLinkTypes();
                    System.out.print(" LINKS(");
                    for (int i = 0; i < linkIds.length; i++) {
                        Lemmatizer.WordNorm linkWordNorm = lemmatizer.getLemmaById(linkIds[i]);
                        System.out.print(linkWordNorm.getId() + "/" + linkWordNorm.getText() + "[" + LinkTypes.byId(linkTypes[i]) + "]");
                        if (i < linkIds.length - 1) {
                            System.out.print(", ");
                        }
                    }
                    System.out.print(")");
                }

                if (wordNormFormIterator.hasNext()) {
                    System.out.println();
                }
            }
        } else {
            System.out.print("[NULL]");
        }

        System.out.println();
    }

}
