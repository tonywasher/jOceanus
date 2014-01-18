@echo off
setlocal
set source=c:\Users\Tony\jOceanus
set target=c:\Users\Tony\sourceforge\jOceanus\trunk

if ".%1" == "." goto :default
set dirNames=%1
goto :docopy

:default
set dirNames=jDataManager jprometheus jDateDay jDecimal jtethys jFieldSet
set dirNames=%dirNames% jGordianKnot jthemis jMoneyWise
set dirNames=%dirNames% jPreferenceSet jSortedList jSpreadSheetManager 

:docopy
rem xcopy /Y %source%\pom.xml %target%\pom.xml
for %%a IN (%dirNames%) do call :copySrcDir %%a
goto :end

:copySrcDir
set dirName=%1
rem xcopy /Y %source%\%dirName%\pom.xml %target%\%dirName%\pom.xml
xcopy /S /Y %source%\%dirName%\src %target%\%dirName%\src
goto :eof

:end
endlocal