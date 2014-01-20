@echo off
setlocal

if ".%1" == "." goto :default
set projectName=JDateButton
set source=c:\Users\Tony\jOceanus\%projectName%
set target=c:\Users\Tony\sourceforge\%projectName%\trunk
set dirNames=.
goto :docopy

:default
set projectName=jOceanus
set source=c:\Users\Tony\%projectName%
set target=c:\Users\Tony\sourceforge\%projectName%\trunk
set dirNames=jmetis jprometheus jtethys
set dirNames=%dirNames% jGordianKnot jthemis jMoneyWise

:docopy
rem xcopy /Y %source%\pom.xml %target%\pom.xml
for %%a IN (%dirNames%) do call :copySrcDir %%a
goto :end

:copySrcDir
set dirName=%1
rem xcopy /Y %source%\%dirName%\pom.xml %target%\%dirName%\pom.xml
xcopy /S /Y %source%\%dirName%\src %target%\%dirName%\src
goto :eof

:setProject
set projectName=%1
set source=c:\Users\Tony\%projectName%
set target=c:\Users\Tony\sourceforge\%projectName%\trunk
goto :eof

:end
endlocal