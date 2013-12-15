@echo off
if exist ..\source.7z del ..\source.7z
"\Program Files"\7-Zip\7z a ..\source.7z -x!target/* -x!*/target/* -x!.metadata/* -r *.java *.html *.xml *.xdoc *.png *.apt *.vm *.properties *.bas