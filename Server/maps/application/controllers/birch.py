#!/usr/bin/env python
import psycopg2
import sys
import numpy
from sklearn.cluster import Birch
import scipy.spatial.distance
import os
import stat

try:
    conn = psycopg2.connect("dbname='project' user='postgres' host='128.199.235.115' password='otniel'")
except:
    print "I am unable to connect to the database"

cur = conn.cursor()

try:
    cur.execute("""SELECT DISTINCT lat,lon,id from location where id>=825 and jenis_id=3""")
except:
    print "I can't SELECT"
r = cur.fetchall()
print "\nRows: \n"

X = numpy.array(r)
T = numpy.delete(X,  numpy.s_[2:3], axis=1)
Y = numpy.delete(X,  numpy.s_[0:2], axis=1)
Y = Y.astype(int)
db = Birch(branching_factor=2, n_clusters=None, threshold=0.00009).fit(T)
d = T.shape
# print d
labels = db.labels_
# print labels

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

data = numpy.sort(a, order='label')


# Calculate centroid every Lab
z = numpy.unique(data['label'])
cen = numpy.zeros(len(z))
tipe = numpy.dtype([('cLat', float), ('cLon', float),('label',int)])
centroid = numpy.array(cen, dtype=tipe)
for i in range(len(z)):
	arr = numpy.extract(data['label']==i, data)
	centroid[i]['cLat'] = numpy.sum(arr['lat'])
	centroid[i]['cLon'] = numpy.sum(arr['lon'])
	centroid[i]['cLat'] /= len(arr)
	centroid[i]['cLon'] /= len(arr)
	centroid[i]['label'] = i
print centroid

#insert to database
conn = psycopg2.connect("dbname='project' user='postgres' host='128.199.235.115' password='otniel'")
cur = conn.cursor()
cur.execute("DELETE FROM cluster")
for row in centroid:
	print row['cLat'], row['cLon'], row['label']
	cur.execute("INSERT INTO cluster(label,clat,clon) VALUES (%s,%s,%s) ;", (row['label'],row['cLat'],row['cLon']))
conn.commit()
cur.close()
conn.close()

# Save data to csv file
numpy.savetxt("/var/www/project/assets/images/foo.csv", data, fmt=['%.7f', '%.7f', '%i', '%i'], delimiter=",")

