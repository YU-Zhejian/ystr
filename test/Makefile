all:\
	e_coli.genomic.fna \
	sars_cov2.genomic.fna \


e_coli.genomic.fna:
	curl https://ftp.ncbi.nlm.nih.gov/genomes/all/GCF/000/005/845/GCF_000005845.2_ASM584v2/GCF_000005845.2_ASM584v2_genomic.fna.gz | \
	pigz -cdf > e_coli.genomic.fna
	samtools faidx e_coli.genomic.fna

sars_cov2.genomic.fna:
	curl https://ftp.ncbi.nlm.nih.gov/genomes/all/GCF/009/858/895/GCF_009858895.2_ASM985889v3/GCF_009858895.2_ASM985889v3_genomic.fna.gz | \
    pigz -cdf > sars_cov2.genomic.fna
	samtools faidx sars_cov2.genomic.fna
