open_corpora_lemmatizer
=======================

Lemmatizer based on the OpenCorpora russian corpus

Instructions
---------------------

1. Download dict.opcorpora.xml.bz2 from http://opencorpora.org/dict.php

2. Put the archive into project root

3. Run org.test.loader.Loader with the parameters: -XX:+UseSerialGC -XX:+PrintGC -Xms3072m -Xmx3072m

4. It takes up to 3 mins to compose lemmatizer files.

5. Run org.test.searcher.Searcher and check out how the lemmatizer works.