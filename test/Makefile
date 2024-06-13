all:\
	e_coli.genomic.fna \
	sars_cov2.genomic.fna \
	ce11.genomic.fna \
	c_elegans_minion_drna.fq \
	ce11.ncbiRefSeq.gtf \

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

c_elegans_minion_drna.fq:
	curl ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR892/005/SRR8929005/SRR8929005_1.fastq.gz | \
	pigz -cdf > c_elegans_minion_drna.fq

c_elegans_ests.fa:
	curl https://hgdownload.soe.ucsc.edu/goldenPath/ce11/bigZips/est.fa.gz | \
	pigz -cdf > c_elegans_ests.fa
