package com.github.yu_zhejian.ystr.sort;

import com.github.yu_zhejian.ystr.utils.Alphabets;
import com.github.yu_zhejian.ystr.utils.IterUtils;
import com.github.yu_zhejian.ystr.utils.KmerGenerator;
import com.github.yu_zhejian.ystr.utils.RandomKmerGenerator;
import com.github.yu_zhejian.ystr.utils.StrUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SortInterfaceTest {

    @Test
    void testLsd(){
        var si = new LsdSort();
        SortInterfaceTest.testUsingKmers(si);
    }
    @Test
    void testJavaStdSort(){
        var si = new JavaStdSort();
        SortInterfaceTest.testUsingKmers(si);
    }

    public static void testUsingKmers(final SortInterface si){
        for (int i = 1; i < 10; i++){
            var kmers = new ArrayList<>(IterUtils.collect(IterUtils.head(new RandomKmerGenerator(Alphabets.DNA5_ALPHABET.getValue(), i), 1000)));
            si.sort(kmers);
            StrUtils.requiresSorted(kmers);
        }
    }

}
