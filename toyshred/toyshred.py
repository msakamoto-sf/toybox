import sys
import os.path
import random

if 2 > len(sys.argv):
  print "usage: %s file1 [file2, [file3,]...]" % (sys.argv[0])
  sys.exit(1)

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

for i in xrange(len(sys.argv) - 1):
  f = sys.argv[i+1]
  if shred1file(f):
    print "\tdelete %s ..." % (f)
    os.remove(f)

