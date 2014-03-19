LMClassifier
============

It's a heristic classifier build on top of berkeley LM: https://code.google.com/p/berkeleylm/

Use the "lm.sh" script under <PRJ_DIR>/scripts foder to generate multiple binary LM models, then load it up with command:
    java -ea -mx2000m -server -cp ./*.jar edu.berkeley.nlp.lm.io.LMClassifier ./bin_model/

It will try to read input from standard input, and output the classificated result to the standard output.

To train different LM models, you need to prepare the training data in folder: ./corpus , The file name will be the class tag. It's your own responsibility to collect the text to be used for training. e.g: pruning, normalization of the text in each category. A sample normalization script is provided.

