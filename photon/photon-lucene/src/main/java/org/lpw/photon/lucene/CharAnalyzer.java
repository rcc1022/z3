package org.lpw.photon.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

public class CharAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        Tokenizer tokenizer = new CharTokenizer();
        return new TokenStreamComponents(tokenizer, new LowerCaseFilter(tokenizer));
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new LowerCaseFilter(in);
    }
}
