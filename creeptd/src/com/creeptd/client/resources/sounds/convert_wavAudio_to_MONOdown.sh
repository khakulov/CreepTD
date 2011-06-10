#!/bin/sh

for file in $(find ./)
    do
		sox $file -c 1 $( echo /tmp/ns/$file | sed 's/raw/wav/g')
done
