#!/bin/bash

TARGET_HOST=192.168.1.9

for i in "$@"
do
case ${i} in
    -d|--deploy)
    echo "deploy"
    scp -r  * root@${TARGET_HOST}:/opt/
    ;;

    -s|--start)
    echo "start"
    ssh root@${TARGET_HOST} /usr/bin/systemctl restart pbox.service
    ;;

    -q|--quit)
    echo "quit"
    ssh root@${TARGET_HOST} /usr/bin/systemctl stop pbox.service
    ;;

     -h|--help)
    show_help
    ;;

esac
done


function show_help {
    echo "d - deploy app"
    echo "s - start app"
    echo "q - quit app"
}





