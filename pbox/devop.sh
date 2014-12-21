#!/bin/bash

for i in "$@"
do
case ${i} in
    -d|--deploy)
    echo "deploy"
    scp -r  * root@192.168.1.10:/opt/
    ;;

    -s|--start)
    echo "start"
    ssh root@192.168.1.10 /usr/bin/systemctl restart pbox.service
    ;;

    -q|--quit)
    echo "quit"
    ssh root@192.168.1.10 /usr/bin/systemctl stop pbox.service
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





