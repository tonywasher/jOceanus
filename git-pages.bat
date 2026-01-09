@echo off
setlocal
set REPOSITORY=https://github.com/tonywasher/jOceanus.git
set TARGET=gitpages
cd target

rem remove existing work directory
if exist %TARGET% rd /s /q %TARGET%

rem clone the github pages branch
git clone --branch gh-pages %REPOSITORY% %TARGET%

rem remove existing content (preserving .gitattributes)
cd %TARGET%
for /D %%G in ("*") do rd /s /q %%G
attrib +r .gitattributes
del * /q > nul 2>&1
attrib -r .gitattributes

rem copy staging
xcopy ..\staging . /s /q

rem add all files to git
git add .
git commit -m "Publish webSite%

rem push to repository
git push

rem cleanup
cd ..
rd /s /q %TARGET%
endlocal