all: \
	simple.2bit \
	simple_l.2bit \
	simple.fa.fai \

simple.2bit:
	base64 -d -i simple.2bit.base64 > simple.2bit

simple_l.2bit:
	base64 -d -i simple_l.2bit.base64 > simple_l.2bit

simple.fa.fai:
	samtools faidx simple.fa

validate:
	sha256sum -c sha256sum
