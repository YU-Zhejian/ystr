package org.labw.gtf_reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.labw.gtf_stream.GtfAttributesTokenizerWithFSA;
import org.labw.gtf_stream.GtfParserException;

import java.util.List;

class GTFAttributesTokenizerWithFSATest {

    @Test
    void parse() throws GtfParserException {
        var tokenizer = new GtfAttributesTokenizerWithFSA();
        var parsedResult = tokenizer.parse(
                "gene_id \"ENSG00000290825.1\"; gene_type \"lncRNA\"; gene_name \"DDX11L2\"; level 2; tag \"overlaps_pseudogene\";");
        assertEquals(
                parsedResult.keys(), List.of("gene_id", "gene_type", "gene_name", "level", "tag"));
        assertEquals(
                parsedResult.values(),
                List.of("ENSG00000290825.1", "lncRNA", "DDX11L2", "2", "overlaps_pseudogene"));
        parsedResult = tokenizer.parse(
                "gene_id \"ENSG00000290825.1\"; transcript_id \"ENST00000456328.2\"; gene_type \"lncRNA\"; gene_name \"DDX11L2\"; transcript_type \"lncRNA\"; transcript_name \"DDX11L2-202\"; exon_number 3; exon_id \"ENSE00002312635.1\"; level 2; transcript_support_level \"1\"; tag \"basic\"; tag \"Ensembl_canonical\"; havana_transcript \"OTTHUMT00000362751.1\";");
        assertEquals(
                parsedResult.keys(),
                List.of(
                        "gene_id",
                        "transcript_id",
                        "gene_type",
                        "gene_name",
                        "transcript_type",
                        "transcript_name",
                        "exon_number",
                        "exon_id",
                        "level",
                        "transcript_support_level",
                        "tag",
                        "tag",
                        "havana_transcript"));
        assertEquals(
                parsedResult.values(),
                List.of(
                        "ENSG00000290825.1",
                        "ENST00000456328.2",
                        "lncRNA",
                        "DDX11L2",
                        "lncRNA",
                        "DDX11L2-202",
                        "3",
                        "ENSE00002312635.1",
                        "2",
                        "1",
                        "basic",
                        "Ensembl_canonical",
                        "OTTHUMT00000362751.1"));
        parsedResult = tokenizer.parse(
                "gene_id \"TALONG000095235\"; gene_name \"TALONG000095235\"; gene_status \"NOVEL\"; talon_gene \"95235\"; transcript_id \"TALONT000651092\"; transcript_status \"NOVEL\"; transcript_name \"TALONT000651092\"; talon_transcript \"651092\"; antisense_transcript \"TRUE\"");
        assertEquals(
                parsedResult.keys(),
                List.of(
                        "gene_id",
                        "gene_name",
                        "gene_status",
                        "talon_gene",
                        "transcript_id",
                        "transcript_status",
                        "transcript_name",
                        "talon_transcript",
                        "antisense_transcript"));
        assertEquals(
                parsedResult.values(),
                List.of(
                        "TALONG000095235",
                        "TALONG000095235",
                        "NOVEL",
                        "95235",
                        "TALONT000651092",
                        "NOVEL",
                        "TALONT000651092",
                        "651092",
                        "TRUE"));
    }
}
