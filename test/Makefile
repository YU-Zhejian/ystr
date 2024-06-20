all:
	make -C literature
	make -C ref
	make -C small

# c_elegans_minion_drna.fq:
# 	curl ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR892/005/SRR8929005/SRR8929005_1.fastq.gz | \
# 	pigz -cdf > c_elegans_minion_drna.fq

validate:
	make -C small validate
	make -C ref validate
	make -C literature validate
