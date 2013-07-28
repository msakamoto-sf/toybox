# coding: utf-8
"""
'Image Collector'
$Id: image_collector.py 38972 2011-08-06 09:04:43Z msakamoto-sf $

Collect image datas from <img> tag "src" attribute, <a> tag "href" attribute 
in specified url.
(requires BeautifulSoup : http://www.crummy.com/software/BeautifulSoup/)

have fun!

http://www.glamenv-septzen.net/

copyright (c) 2009, sakamoto-gsyc-3s@glamenv-septzen.net
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
* Neither the name of the <ORGANIZATION> nor the names of its contributors 
  may be used to endorse or promote products derived from this software 
  without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

"""

import sys, os, datetime, time, logging, getopt, re
import urllib2, urlparse, httplib
from BeautifulSoup import BeautifulSoup

appconfig = {
  'url'       : '',
  'noatag'    : False,
  'noimgtag'  : False,
  'interval'  : 1000,
  'retry'     : 3,
  'retry-interval' : 3000,
  'dry-run'   : False,
  'hostnames' : [],
  }

# {{{ usage()

def usage():
  print 'usage: python %s [options] url' % sys.argv[0]
  print '''
  options are:
  -h, --help :
    display this message.

  --noatag :
    ignore <a> tag's "href" attribute.

  --noimgtag :
    ignore <img> tag's "src" attribute.

  -i, --interval <int> :
    request interval for millisecond. (default 1,000ms)

  -r, --retry <int> :
    retry times when http connection socket error happen. (default 3)

  --retry-interval <int> :
    retry interval by millisecond. (default 3,000ms)

  -n, --dry-run :
    don't create directory, html page file, logs.
    only retrieve target html page data, and extract image urls.

  --hostnames <hostnames> : 
    limit hostnames for image url.
    separate by space for multiple hostnames.
    ex:) --hostnames "a.com b.com c.com"
  '''

# }}}
# {{{ getopt() and argument parse

try:
  optlist, args = getopt.getopt(sys.argv[1:], 'hi:r:n', [
    'noatag', 'noimgtag', 'interval=', 'retry=', 'dry-run', 
    'hostnames=', 'help', 'retry-interval='
    ])
except getopt.GetoptError, e:
  print str(e)
  usage()
  sys.exit(-1)

for opt, val in optlist:
  if '--noatag' == opt:
    appconfig['noatag'] = True
  elif '--noimgtag' == opt:
    appconfig['noimgtag'] = True
  elif opt in ('-i', '--interval'):
    appconfig['interval'] = int(val)
  elif opt in ('-r', '--retry'):
    appconfig['retry'] = int(val)
  elif opt in ('-n', '--dry-run'):
    appconfig['dry-run'] = True
  elif '--retry-interval' == opt:
    appconfig['retry-interval'] = int(val)
  elif '--hostnames' == opt:
    for hostname in val.split(' '):
      if hostname:
        appconfig['hostnames'].append(hostname)
  elif opt in ('-h', '--help'):
    usage()
    quit()

if 1 != len(args):
  print 'url is not given!'
  usage()
  sys.exit(-1)

# }}}
# {{{ initialization

appconfig['url'] = args[0]
now_str = datetime.datetime.now().strftime('%Y%m%d_%H%M%S')
appconfig['now'] = now_str
appconfig['html_file'] = now_str + '.html'
appconfig['save_dir'] = os.path.join(os.getcwd(), now_str)
appconfig['log_file'] = now_str + ".log"

# basic file log configuration.
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s %(name)s %(levelname)s %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S',
    filename=appconfig['log_file'],
    filemode='w+b')
# create and adjust console log handler.
console = logging.StreamHandler()
console.setLevel(logging.DEBUG)
console.setFormatter(logging.Formatter('%(name)s %(levelname)s %(message)s'))
# add console log handler to root logger.
logging.getLogger('').addHandler(console)

log = logging.getLogger('main')

try:
  if appconfig['dry-run']:
    log.debug(
      "(dry-run) create image saving directory [%s]" % appconfig['save_dir'])
  else:
    os.mkdir(appconfig['save_dir'])
except BaseException, e:
  log.error("mkdir(%s) for image files failed." % appconfig['save_dir'])
  log.error(e)
  sys.exit(-1)

parts = urlparse.urlparse(appconfig['url'])
appconfig['url_prefix1'] = parts.scheme + '://' + parts.netloc
appconfig['url_prefix2'] = appconfig['url_prefix1'] + \
    os.path.dirname(parts.path)
if re.match(r'.*[^/]$', appconfig['url_prefix2']):
  appconfig['url_prefix2'] += '/'

log.info("============================")
log.info("Configurations :")
log.info("* target url:")
log.info("\t%s" % appconfig['url'])
log.info("\tauto fix(1) %s" % appconfig['url_prefix1'])
log.info("\tauto fix(2) %s" % appconfig['url_prefix2'])
log.info("* saving directory:")
log.info("\t%s" % appconfig['save_dir'])
log.info("* html page file:")
log.info("\t%s" % appconfig['html_file'])
log.info("* log file:")
log.info("\t%s" % appconfig['log_file'])
log.info("* get <img> 'src' attr:")
log.info("\t%s" % (not appconfig['noimgtag']))
log.info("* get <a> 'href' attr:")
log.info("\t%s" % (not appconfig['noatag']))
log.info("* interval:")
log.info("\t%d millisecond" % appconfig['interval'])
log.info("* error retry times:")
log.info("\t%d times" % appconfig['retry'])
log.info("* error retry interval:")
log.info("\t%d millisecond" % appconfig['retry-interval'])
log.info("* hostname restrictions:")
log.info("\t%s" % appconfig['hostnames'])
log.info("* dry-run:")
log.info("\t%s" % appconfig['dry-run'])
log.info("============================")

# }}}
# {{{ open html page and save it.

try:
  html = urllib2.urlopen(appconfig['url']).read()
except IOError, e:
  log.error("URL retrieving from [%s] failed." % appconfig['url'])
  log.error(e)
  sys.exit(-1)

if appconfig['dry-run']:
  log.debug(
    "(dry-run) save html file [%s]" % appconfig['html_file'])
else:
  html_f = None
  try:
    html_f = open(appconfig['html_file'], 'w+b')
    html_f.write(html)
    log.info("save html file [%s]" % appconfig['html_file'])
  except IOError, e:
    log.warn("writing contents data file to [%s] failed." 
        % appconfig['html_file'])
    log.warn(e)
    log.warn("data file saving skipped, continue...")
  finally:
    if html_f:
      try:
        html_f.close()
      except:
        pass

# }}}
# {{{ parse html by BeautifulSoup

elements = []
try:
  soup = BeautifulSoup(html)
except BaseException, e:
  log.error("BeautifulSoup error, failed.")
  log.error(e)
  sys.exit(-1)

# }}}

re_http = re.compile(r'^http://')
re_https = re.compile(r'^https://')
re_path = re.compile(r'^/')
re_ext = re.compile(r'^image/(\w{3,4})')
re_texts = re.compile(r'.*\.(txt|htm|html)$')
targets = []

# {{{ extract target image urls

def fixurl(cfg, url):
  if re_http.match(url):
    pass
  elif re_https.match(url):
    pass
  elif re_path.match(url):
    url = cfg['url_prefix1'] + url
  else:
    url = cfg['url_prefix2'] + url
  return url

image_urls = []

if not appconfig['noimgtag']:
  tags = soup.findAll('img')
  for tag in tags:
    if not tag.has_key('src'):
      continue
    url = tag['src']
    log.info("img#src: detected : %s" % url)
    fixed = fixurl(appconfig, url)
    if fixed != url:
      log.info("\tfixed to [%s]" % fixed)
    image_urls.append(fixed)

if not appconfig['noatag']:
  tags = soup.findAll('a')
  for tag in tags:
    if not tag.has_key('href'):
      continue
    url = tag['href']
    log.info("a#href: detected : %s" % url)
    fixed = fixurl(appconfig, url)
    if fixed != url:
      log.info("\tfixed to [%s]" % fixed)
    image_urls.append(fixed)

# remove duplicated url, and sort.
image_urls = sorted(image_urls)
image_url_set = set(image_urls)
if len(image_urls) != len(image_url_set):
  log.info(" %d urls are duplicated, removed." % 
      (len(image_urls) - len(image_url_set)))

image_urls = list(image_url_set)
image_urls = sorted(image_urls)

for url in image_urls:
  parts = urlparse.urlsplit(url)

  use_https = False
  if 'http' == parts.scheme:
    conn_key = 'http://' + parts.netloc
  elif 'https' == parts.scheme:
    conn_key = 'https://' + parts.netloc
    use_https = True

  hostname_ok = False
  if 0 == len(appconfig['hostnames']):
    hostname_ok = True
  else:
    for host in appconfig['hostnames']:
      if 0 <= parts.netloc.find(host):
        hostname_ok = True
        break

  if not hostname_ok:
    log.info("'%s' doesn't match limited hostnames %s" %
        (url, appconfig['hostnames']))
    continue

  path = parts.path
  if re_texts.match(path):
    log.info("obvious text, html link %s, ignored." % path)
    continue
  if parts.query:
    path = path + '?' + parts.query

  targets.append((conn_key, use_https, parts.netloc, path))

appconfig['count_all'] = len(targets)

log.info("============================")
log.info("extracted [%s] image urls..." % appconfig['count_all'])
if appconfig['dry-run']:
  log.debug("(dry-run) ends.")
  quit()

# }}}

log.info("processing start...")

http_connections = {}
appconfig['count_saved'] = 0
appconfig['count_skipped'] = 0
appconfig['count_error'] = 0

# {{{ borrow_http_connection()

def borrow_http_connection(connections, key, use_https, netloc):
  if connections.has_key(key):
    return connections[key]

  if use_https:
    connections[key] = httplib.HTTPSConnection(netloc)
  else:
    connections[key] = httplib.HTTPConnection(netloc)

  return connections[key]

# }}}
# {{{ clear_http_connection()

def clear_http_connection(connections):
  for con in connections.values():
    try:
      con.close()
    except:
      pass

# }}}
# {{{ get_data()

def get_data(cfg, con, path, logger, retrycnt):
  data = []
  content_type = ''
  try:
    con.request('GET', path)
    res = con.getresponse()
    content_type = res.getheader('Content-Type', '').strip()
    logger.info("\t[%s %s] Content-Type: %s" % (
      res.status, res.reason, content_type))

    # http status response check
    if 302 == res.status:
      res.read() # dummy read to clear read buffer
      new_loc = res.getheader('Location', '').strip()
      logger.info("\t[302] %s" % (new_loc))
      if 0 == len(new_loc):
        logger.error("Redirection URL is empty, skipped.")
        return ([], '')
      new_parts = urlparse.urlsplit(new_loc)

      new_path = new_parts.path
      if new_parts.query:
        new_path = new_path + '?' + new_parts.query

      if new_parts.netloc:
        use_https = False
        if 'http' == new_parts.scheme:
          conn_key = 'http://' + new_parts.netloc
        elif 'https' == new_parts.scheme:
          conn_key = 'https://' + new_parts.netloc
          use_https = True
        con = borrow_http_connection(
          http_connections, conn_key, use_https, new_parts.netloc)

      # retry
      data, content_type = get_data(cfg, con, new_path, logger, retrycnt)
      # return empty sequence or image data, content_type
      return (data, content_type)

    # http status response check
    if 200 != res.status:
      logger.warn("\t...skip")
      cfg['count_skipped'] += 1
      res.read() # dummy read to clear read buffer
      return ([], '')

    # retrive entrire data sequence
    data = res.read()

  except BaseException, e:
    logger.warn("HTTP socket error occurrs.")
    logger.warn(e)

    # check retry count over
    if retrycnt >= cfg['retry']:
      logger.error("Retry over!!")
      cfg['count_error'] += 1
      return ([], '')

    retrycnt += 1
    logger.warn("retry - %d times" % retrycnt)

    # wait retry interval
    time.sleep(cfg['retry-interval'] / 1000)

    # re-connect http
    try:
      con.close()
      con.connect()
    except:
      logger.error("HTTP re-connect failure, skipped.")
      cfg['count_skipped'] += 1
      return ([], '')

    # retry
    data, content_type = get_data(cfg, con, path, logger, retrycnt)

  # return empty sequence or image data, content_type
  return (data, content_type)

# }}}
# {{{ save_image_to_local()

def save_image_to_local(cfg, idx, ext, data, logger):

  # gen image data save filename
  filename = "%s_%05d.%s" % (cfg['now'], idx, ext)
  filepath = os.path.join(cfg['save_dir'], filename)
  logger.info("\tsave to [%s]" % filepath)

  # save
  img_f = None
  try:
    img_f = open(filepath, 'w+b')
    img_f.write(data)
    logger.info("\t\tsave ok.")
    cfg['count_saved'] += 1
  except IOError, e:
    log.warn("\t\tsaving failed, skipped.")
    log.warn(e)
    cfg['count_error'] += 1
  finally:
    if img_f:
      try:
        img_f.close()
      except:
        pass

# }}}
# {{{ data retrieve and save (main)

count = 0
for (conn_key, use_https, netloc, path) in targets:

  count += 1
  log.info("[%d/%d] %s%s" % (count, appconfig['count_all'], conn_key, path))

  con = borrow_http_connection(
      http_connections, conn_key, use_https, netloc)

  data, content_type = get_data(appconfig, con, path, log, 0)
  if 0 == len(data):
    continue

  # extract filename extension from content-type
  m = re_ext.match(content_type)
  if m:
    ext = m.group(1)
    if 'jpeg' == ext:
      ext = 'jpg'
  else:
    appconfig['count_skipped'] += 1
    log.info(
        "Content-Type: [%s] may not be image data, skipped." % content_type)
    continue

  save_image_to_local(appconfig, count, ext, data, log)

  # sleep
  time.sleep(appconfig['interval'] / 1000)

# }}}

clear_http_connection(http_connections)

log.info("all[%d]/saved[%d]/skipped[%d]/error[%d]" % (
  appconfig['count_all'], 
  appconfig['count_saved'], 
  appconfig['count_skipped'], 
  appconfig['count_error']))

