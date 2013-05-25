"""
nosecret.py : simple batch doing ...
 1. decrypt encrypted file to temporary file.
 2. open decrypted temporary file by your favorite editor.
 3. if modified, encrypt again.
... with OpenSSL command line interface.

Copyright 2011 sakamoto.gsyc.3s@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""
__author__ = 'sakamoto.gsyc.3s@gmail.com'

import sys
import os
import random
import tempfile
import subprocess

def writezeros(fobj, flen):
  fobj.seek(0)
  for i in xrange(flen):
    fobj.write(chr(0))
  print "\twrite zeros for %d bytes." % (flen)

def writerands(fobj, flen):
  fobj.seek(0)
  for i in xrange(flen):
    fobj.write(chr(random.randint(0, 255)))
  print "\twrite randum numbersfor %d bytes." % (flen)

def shred1file(fname):
  print "Shredding start for %s ..." % (fname)
  try:
    filelen = os.path.getsize(fname)
  except OSError:
    print "\tfile does not exist or inaccesible."
    return False

  print "\tlength: %d bytes" % (filelen)
  f = open(fname, "wb")
  shredded = False
  try:
    writerands(f, filelen)
    writezeros(f, filelen)
    writerands(f, filelen)
    shredded = True
  finally:
    f.flush()
    os.fsync(f.fileno())
    f.close()
  return shredded

if 2 > len(sys.argv):
  print "usage: %s openssl_aes256_cbc_encrypted_file" % (sys.argv[0])
  print "set Environment Values:"
  print "\tNOSECRET_PY_OPENSSL_PATH   = path to OpenSSL CLI executable (required)"
  print "\tNOSECRET_PY_OPENSSL_CIPHER = OpenSSL Cipher Type (optional, default: aes-256-cbc)"
  print "\tNOSECRET_PY_EDITOR_PATH    = path to editor program (required)"
  sys.exit(1)

openssl_cli = os.environ.get("NOSECRET_PY_OPENSSL_PATH")
if not openssl_cli:
  print >>sys.stderr, "!!set NOSECRET_PY_OPENSSL_PATH Environment Value to OpenSSL CLI executable."
  sys.exit(2)
if "\"" == openssl_cli[0]:
  openssl_cli = openssl_cli[1:]
if "\"" == openssl_cli[-1]:
  openssl_cli = openssl_cli[0:-1]
print >>sys.stderr, "!!OpenSSL CLI = [%s]" % (openssl_cli)

openssl_cipher = "aes-256-cbc"
openssl_cipher_env = os.environ.get("NOSECRET_PY_OPENSSL_CIPHER")
if openssl_cipher_env:
  print >>sys.stderr, "!!set NOSECRET_PY_OPENSSL_CIPHER Environment Value to OpenSSL Cipher."
  openssl_cipher = openssl_cipher_env
print >>sys.stderr, "!!OpenSSL Cipher Type : %s" % (openssl_cipher)
openssl_cipher = "-" + openssl_cipher

editor_exe = os.environ.get("NOSECRET_PY_EDITOR_PATH")
if not editor_exe:
  print >>sys.stderr, "!!set NOSECRET_PY_EDITOR_PATH Environment Value to editor program."
  sys.exit(3)
if "\"" == editor_exe[0]:
  editor_exe = editor_exe[1:]
if "\"" == editor_exe[-1]:
  editor_exe = editor_exe[0:-1]
print >>sys.stderr, "!!Editor Program = [%s]" % (editor_exe)

encrypted_file = sys.argv[1]
try:
  decrypt_proc = subprocess.Popen([
      openssl_cli, "enc", openssl_cipher, "-d", "-in", encrypted_file
      ], stdout=subprocess.PIPE)
  openssl_stdout, openssl_stderr = decrypt_proc.communicate()
  if decrypt_proc.returncode:
    raise Exception, "OpenSSL aborted(returncode=%d)." % (decrypt_proc.returncode)
except Exception, e:
  print >>sys.stderr, "!!execution failed:", e
  sys.exit(4)

tempfd, tempfname = tempfile.mkstemp()
print >>sys.stderr, "!!temporary file = [%s]" % (tempfname)
tempfo = os.fdopen(tempfd, "w+b")
tempfo.write(openssl_stdout)
tempfo.flush()
os.fsync(tempfo.fileno())
tempfo.close()
# if any errors, abort here. delete tempfile manually by yourself. :P

edit_mtime1 = os.stat(tempfname).st_mtime

try:
  retcode = subprocess.call([editor_exe, tempfname])
  print >>sys.stderr, "!!editor returned [%d]" % (retcode)
  if retcode:
    print >>sys.stderr, "!!editor abort? check editor's swap or temp files :("
except OSError, e:
  print >>sys.stderr, "!!execution failed:", e

edit_mtime2 = os.stat(tempfname).st_mtime

if edit_mtime2 > edit_mtime1:
  print >>sys.stderr, "!!changed, encyrpting..."
  try:
    retcode = subprocess.call(
        [openssl_cli, "enc", openssl_cipher, "-e", "-out", encrypted_file, "-in", tempfname])
    print >>sys.stderr, "!!OpenSSL encryption returned [%d]" % (retcode)
  except OSError, e:
    print >>sys.stderr, "!!execution failed:", e

if shred1file(tempfname):
  print "!!deleted %s" % (tempfname)
  os.remove(tempfname)

