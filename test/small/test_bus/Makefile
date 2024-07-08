all: \
	output.s.c.bus \

output.s.c.bus:
	base64 -d -i output.s.c.bus.base64 > output.s.c.bus

validate:
	sha256sum -c sha256sum
