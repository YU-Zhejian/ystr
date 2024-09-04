all: \
	output.s.c.bus \

output.s.c.bus:
	base64 -d -i output.s.c.bus.base64 > output.s.c.bus
	# bustools text output.s.c.bus -f -o output.s.c.bus.tsv

validate:
	sha256sum -c sha256sum
