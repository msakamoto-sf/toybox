#!/bin/sh
# sample shortcut shellscript for unix.

export NOSECRET_PY_OPENSSL_PATH=/usr/bin/openssl
export NOSECRET_PY_EDITOR_PATH=/usr/bin/vim
python nosecret.py $*
