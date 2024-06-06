package com.github.yu_zhejian.ystr_demo;

import com.github.yu_zhejian.ystr.IterUtils;
import com.github.yu_zhejian.ystr.rolling.MinimizerCalculator;
import com.github.yu_zhejian.ystr.rolling.NtHashBase;
import com.github.yu_zhejian.ystr.rolling.PrecomputedNtHash;

import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;

import io.vavr.Tuple2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

public class GenomeIndexer {
    public void generateHashes(byte[] string, File posOut, File negOut) throws IOException {
        int k = 14;
        int start = 0;
        var fwdNtHashIterator = new MinimizerCalculator(
                NtHashBase.getFwdHash(new PrecomputedNtHash(string, k, start)), k, start, true);
        var revNtHashIterator = new MinimizerCalculator(
                NtHashBase.getRevHash(new PrecomputedNtHash(string, k, start)), k, start, true);
        var bb = ByteBuffer.allocate(8);

        try(var posW = new FileWriter(posOut); var negW = new FileWriter(negOut)){
            for (var i : IterUtils.iterable(IterUtils.dedup(fwdNtHashIterator, Tuple2::_1))) {
                posW.write();
            }
        }

        for (var i : IterUtils.iterable(IterUtils.dedup(fwdNtHashIterator, Tuple2::_1))) {
            System.out.printf("%s %s%n", Long.toHexString(i._1()), i._2());
        }
    }

    public static void main(String[] args) {
        var fnaPath = "F:\\home\\Documents\\ystr\\test\\sars_cov2.genomic.fna";
        var gi = new GenomeIndexer();
        var refi = new FastaSequenceIndex(new File(fnaPath + ".fai"));
        try (var ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(new File(fnaPath))) {
            for (var seqIt : IterUtils.iterable(refi.iterator()))
                gi.generateHashes(ref.getSequence(seqIt.getContig()).getBases());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
