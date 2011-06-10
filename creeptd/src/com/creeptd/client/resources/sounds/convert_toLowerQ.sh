#!/bin/sh

for file in $(find ./)
    do
		sox $file -r 22050 $( echo /tmp/rs/$file | sed 's/raw/wav/g')
done
