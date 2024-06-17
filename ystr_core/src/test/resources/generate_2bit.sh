#!/usr/bin/env bash
wget https://hgdownload.cse.ucsc.edu/admin/exe/linux.x86_64/faToTwoBit
chmod +x faToTwoBit
./faToTwoBit simple.fa simple.2bit
./faToTwoBit -long simple.fa simple_l.2bit
rm -f faToTwoBit
