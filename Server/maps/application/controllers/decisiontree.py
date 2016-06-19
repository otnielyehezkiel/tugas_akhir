#!/usr/bin/env python
import psycopg2
import sys
import numpy
from sklearn import tree
from sklearn import svm
import os
import stat
from sklearn.externals.six import StringIO  
import pydot
from wand.image import Image
from wand.color import Color

my_data = numpy.genfromtxt( '/var/www/project/assets/images/train.csv', delimiter=',')

data = numpy.array(my_data)
X = data[:,1:5]
Y = data[:,0:1]
clf = tree.DecisionTreeClassifier(class_weight="balanced")
# clf = svm.LinearSVC(class_weight="balanced")
clf = clf.fit(X,Y)
# print(clf.feature_importances_)
dot_data = StringIO() 
# tree.export_graphviz(clf, out_file=dot_data)
# graph = pydot.graph_from_dot_data(dot_data.getvalue())
# graph.write_pdf('/var/www/project/assets/images/dtree.pdf') 

  # with Image(filename='/var/www/project/assets/images/dtree.pdf', resolution=120) as img:
#   with Image(width=img.width, height=img.height, background=Color("white")) as bg:
#     bg.composite(img,0,0)
#     bg.save(filename='/var/www/project/assets/images/dtree.png')

predict = numpy.genfromtxt('/var/www/project/assets/images/predict.csv', delimiter=',')
n = numpy.array(predict)
if len(n.shape)==2:
	o = n[:,0:4]
	p = n[:,4:5]
	jenis = n[:,5:6]
else:
	o = n[0:4]
	p = n[4:5]
	jenis = n[5:6]
# print n 
result = clf.predict(o)
hasil = numpy.column_stack((p,result,jenis))
hasil = hasil.astype(int)
print hasil

# update data hasil
conn = psycopg2.connect("dbname='project' user='postgres' host='128.199.235.115' password='otniel'")
cur = conn.cursor()
for row in hasil:
	if row[1] != row[2]:
		# cur.execute("UPDATE location SET jenis_id = (%s) WHERE id = (%s)",(row[1],row[0]))
		print "a"

conn.commit()
cur.close()
conn.close()