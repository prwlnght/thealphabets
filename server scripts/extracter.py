import zipfile
import sys

fileName = sys.argv[1]
Name,ext = fileName.split(".")
#print(Name)
zfile = zipfile.ZipFile("C:/xampp/htdocs/test/uploads/"+fileName) #gautam.zip
zfile.extractall("C:/xampp/htdocs/test/uploads/"+Name) #gautam
#print("hello")
zfile.close()
