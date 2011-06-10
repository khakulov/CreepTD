#!/bin/sh

for file in $(find ./)
	do
		#  sox -V -r 44100 -w -c 2 -u xb_warn.raw  xb_warn.wav
		 sox -V -r 44100 -w -c 2 -u $file $( echo $file | sed 's/raw/wav/g')
    done

