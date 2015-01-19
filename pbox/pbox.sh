#!/bin/bash

echo $((510241024)) > /proc/sys/net/core/rmem_max

# check if internet is available
((count = 1000))
while [[ $count -ne 0 ]] ; do
    ping -c 1 8.8.8.8
    rc=$?
    if [[ $rc -eq 0 ]] ; then
        ((count = 1))
    fi
    ((count = count - 1))
done

if [[ $rc -eq 0 ]] ; then
    /usr/bin/python /opt/pbox/PBox.py
else
    echo "no internet :("
fi