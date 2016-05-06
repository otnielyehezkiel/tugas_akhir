#!/usr/bin/env python
import psycopg2
import sys
import numpy
from sklearn.cluster import DBSCAN
import scipy.spatial.distance
import os
import stat

try:
    conn = psycopg2.connect("dbname='project' user='postgres' host='128.199.235.115' password='otniel'")
except:
    print "I am unable to connect to the database"

cur = conn.cursor()

try:
    cur.execute("""SELECT DISTINCT lat,lon,id from location""")
except:
    print "I can't SELECT"
r = cur.fetchall()
print "\nRows: \n"

X = numpy.array(r)
T = numpy.delete(X,  numpy.s_[2:3], axis=1)
Y = numpy.delete(X,  numpy.s_[0:2], axis=1)
Y = Y.astype(int)
db = DBSCAN(eps=0.00005, min_samples=1).fit(T)
d = T.shape
print d
core_samples = db.core_sample_indices_
labels = db.labels_
print labels

d_type = numpy.dtype([('lat', float), ('lon', float), ('id', int), ('label', int)])
ax = numpy.zeros(shape=(d[0]))
a = numpy.array(ax, dtype=d_type)
c = 0
for row in X:
    a[c]['lat'] = row[0]
    a[c]['lon'] = row[1]
    a[c]['id'] = Y[c]
    a[c]['label'] = labels[c]
    c += 1
print Y
data = numpy.sort(a, order='label')
print a
numpy.savetxt("/var/www/project/assets/images/foo.csv", data, fmt=['%.7f', '%.7f', '%i', '%i'], delimiter=",")

