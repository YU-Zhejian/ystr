all:\
	ce11.genomic.fna \
	ce11.genomic.2bit \
	ce11.ncbiRefSeq.gtf \
	c_elegans_ests.fa \
	ce11.genomic.fna.gz \
	e_coli.genomic.fna \
	sars_cov2.genomic.fna \



e_coli.genomic.fna:
	curl https://ftp.ncbi.nlm.nih.gov/genomes/all/GCF/000/005/845/GCF_000005845.2_ASM584v2/GCF_000005845.2_ASM584v2_genomic.fna.gz | \
	gzip -cdf > e_coli.genomic.fna
	samtools faidx e_coli.genomic.fna

sars_cov2.genomic.fna:
	curl https://ftp.ncbi.nlm.nih.gov/genomes/all/GCF/009/858/895/GCF_009858895.2_ASM985889v3/GCF_009858895.2_ASM985889v3_genomic.fna.gz | \
    gzip -cdf > sars_cov2.genomic.fna
	samtools faidx sars_cov2.genomic.fna

ce11.ncbiRefSeq.gtf:
	curl https://hgdownload.soe.ucsc.edu/goldenPath/ce11/bigZips/genes/ce11.ncbiRefSeq.gtf.gz | \
	gzip -cdf > ce11.ncbiRefSeq.gtf

ce11.genomic.fna:
	curl https://hgdownload.soe.ucsc.edu/goldenPath/ce11/bigZips/ce11.fa.gz | \
    gzip -cdf > ce11.genomic.fna
	samtools faidx ce11.genomic.fna

ce11.genomic.fna.gz:
	bgzip -l9 -@32 -c -f < ce11.genomic.fna > ce11.genomic.fna.gz
	samtools faidx ce11.genomic.fna.gz

c_elegans_ests.fa:
	curl https://hgdownload.soe.ucsc.edu/goldenPath/ce11/bigZips/est.fa.gz | \
	pigz -cdf > c_elegans_ests.fa

ce11.genomic.2bit:
	curl https://hgdownload.soe.ucsc.edu/goldenPath/ce11/bigZips/ce11.2bit -o ce11.genomic.2bit

validate:
	sha256sum -c sha256sum
