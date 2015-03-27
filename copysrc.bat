@echo off
setlocal

if ".%1" == "." goto :default
set projectName=JDateButton
set source=c:\Users\Tony\jOceanus\%projectName%
set target=c:\Users\Tony\sourceforge\%projectName%\branches\v1.3.0
set dirNames=.
goto :docopy

:default
set projectName=jOceanus
set source=c:\Users\Tony\%projectName%
set target=c:\Users\Tony\sourceforge\%projectName%\trunk
set dirNames=jtethys jtethys\jtethys-core jtethys\jtethys-swing
set dirNames=%dirNames% jgordianknot jgordianknot\jgordianknot-core jgordianknot\jgordianknot-swing
set dirNames=%dirNames% jmetis jmetis\jmetis-core jmetis\jmetis-swing
set dirNames=%dirNames% jprometheus jprometheus\jprometheus-core
set dirNames=%dirNames% jthemis jthemis\jthemis-core
set dirNames=%dirNames% jmoneywise jmoneywise\jmoneywise-core
set dirNames=%dirNames% .

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