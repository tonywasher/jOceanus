@echo off
setlocal
set zipApp="\Program Files"\7-Zip\7z
set fileName=MoneyWise.7z
set fileLoc=..
set xClude=-x!target/* -x!*/target/* -x!.metadata/* x!.recommenders/*
set fileTypes=*.java *.html *.xml *.xdoc *.png *.apt *.vm *.properties *.bas *.css
if exist %fileLoc%\%fileName% del %fileLoc%\%fileName%
%zipApp% a %fileLoc%\%fileName% %xClude% -r %fileTypes%
endlocal