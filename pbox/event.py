#!/usr/bin/env python

import sys
from os.path import expanduser, join

path = '/opt/pbox/'
fn = join(path, 'run', 'nowplaying')

info = sys.stdin.readlines()
cmd = sys.argv[1]

if cmd == 'songstart':
    with open(fn, 'w') as f:
        f.write("".join(info))