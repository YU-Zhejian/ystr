all:\
	e_coli.genomic.fna \
	sars_cov2.genomic.fna \
	ce11.genomic.fna \
	ce11.genomic.2bit \
	c_elegans_minion_drna.fq \
	ce11.ncbiRefSeq.gtf \
	oxford_3k.txt \
	shakespeare_gutenberg.txt \
	c_elegans_ests.fa \
	kjv_gutenberg.txt \


e_coli.genomic.fna:
	curl https://ftp.ncbi.nlm.nih.gov/genomes/all/GCF/000/005/845/GCF_000005845.2_ASM584v2/GCF_000005845.2_ASM584v2_genomic.fna.gz | \
	pigz -cdf > e_coli.genomic.fna
	samtools faidx e_coli.genomic.fna

sars_cov2.genomic.fna:
	curl https://ftp.ncbi.nlm.nih.gov/genomes/all/GCF/009/858/895/GCF_009858895.2_ASM985889v3/GCF_009858895.2_ASM985889v3_genomic.fna.gz | \
    pigz -cdf > sars_cov2.genomic.fna
	samtools faidx sars_cov2.genomic.fna

ce11.ncbiRefSeq.gtf:
	curl https://hgdownload.soe.ucsc.edu/goldenPath/ce11/bigZips/genes/ce11.ncbiRefSeq.gtf.gz | \
	pigz -cdf > ce11.ncbiRefSeq.gtf

ce11.genomic.fna:
	curl https://hgdownload.soe.ucsc.edu/goldenPath/ce11/bigZips/ce11.fa.gz | \
    pigz -cdf > ce11.genomic.fna
	samtools faidx ce11.genomic.fna
	bgzip -l9 -i -@32 ce11.genomic.fna
	pigz -dk ce11.genomic.fna.gz
	samtools faidx ce11.genomic.fna.gz

ce11.genomic.2bit:
	curl https://hgdownload.soe.ucsc.edu/goldenPath/ce11/bigZips/ce11.2bit -o ce11.genomic.2bit

c_elegans_minion_drna.fq:
	curl ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR892/005/SRR8929005/SRR8929005_1.fastq.gz | \
	pigz -cdf > c_elegans_minion_drna.fq

c_elegans_ests.fa:
	curl https://hgdownload.soe.ucsc.edu/goldenPath/ce11/bigZips/est.fa.gz | \
	pigz -cdf > c_elegans_ests.fa

oxford_3k.txt:
	curl -L https://github.com/jnoodle/English-Vocabulary-Word-List/raw/master/Oxford%203000.txt > oxford_3k.txt

shakespeare_gutenberg.txt:
	curl -L https://www.gutenberg.org/ebooks/100.txt.utf-8 > shakespeare_gutenberg.txt

kjv_gutenberg.txt:
	curl -L https://www.gutenberg.org/ebooks/10.txt.utf-8 > kjv_gutenberg.txt
