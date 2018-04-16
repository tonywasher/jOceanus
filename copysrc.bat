@echo off
setlocal

if ".%1" == "." goto :default
set projectName=jdatebutton
set source=c:\Users\Tony\jOceanus\%projectName%
set target=c:\Users\Tony\sourceforge\%projectName%\trunk
set dirNames=jdatebutton-core jdatebutton-swing jdatebutton-javafx
set dirNames=%dirNames% .
goto :docopy

:default
set projectName=jOceanus
set source=c:\Users\Tony\%projectName%
set target=c:\Users\Tony\sourceforge\%projectName%\trunk
set dirNames=jtethys jtethys\jtethys-core jtethys\jtethys-swing jtethys\jtethys-javafx 
set dirNames=%dirNames% jtethys\jtethys-test jtethys\jtethys-test\jtethys-test-core
set dirNames=%dirNames% jtethys\jtethys-test\jtethys-test-swing jtethys\jtethys-test\jtethys-test-javafx
set dirNames=%dirNames% jgordianknot jgordianknot\jgordianknot-core jgordianknot\jgordianknot-swing jgordianknot\jgordianknot-javafx
set dirNames=%dirNames% jgordianknot\jgordianknot-test jgordianknot\jgordianknot-test\jgordianknot-test-core
set dirNames=%dirNames% jgordianknot\jgordianknot-test\jgordianknot-test-javafx jgordianknot\jgordianknot-test\jgordianknot-test-swing
set dirNames=%dirNames% jmetis jmetis\jmetis-core jmetis\jmetis-swing jmetis\jmetis-javafx
set dirNames=%dirNames% jmetis\jmetis-services jmetis\jmetis-services\jmetis-sheet-api jmetis\jmetis-services\jmetis-sheet-hssf
set dirNames=%dirNames% jmetis\jmetis-services\jmetis-sheet-jopen jmetis\jmetis-services\jmetis-sheet-odfdom jmetis\jmetis-services\jmetis-sheet-xssf
set dirNames=%dirNames% jmetis\jmetis-test jmetis\jmetis-test\jmetis-test-core
set dirNames=%dirNames% jmetis\jmetis-test\jmetis-test-javafx jmetis\jmetis-test\jmetis-test-swing
set dirNames=%dirNames% jprometheus jprometheus\jprometheus-core jprometheus\jprometheus-swing jprometheus\jprometheus-javafx
set dirNames=%dirNames% jthemis jthemis\jthemis-core jthemis\jthemis-swing jthemis\jthemis-javafx
set dirNames=%dirNames% jmoneywise jmoneywise\jmoneywise-core jmoneywise\jmoneywise-swing jmoneywise\jmoneywise-javafx jmoneywise\jmoneywise-test
set dirNames=%dirNames% jcoeus jcoeus\jcoeus-core jcoeus\jcoeus-swing jcoeus\jcoeus-javafx
set dirNames=%dirNames% .

:docopy
for %%a IN (%dirNames%) do call :copySrcDir %%a
goto :end

:copySrcDir
set dirName=%1
xcopy /S /I /Y %source%\%dirName%\src %target%\%dirName%\src
if "%dirName%" == "." goto :eof
xcopy /I /Y %source%\%dirName%\pom.xml %target%\%dirName%
goto :eof

:end
endlocal