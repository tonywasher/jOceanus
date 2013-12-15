@echo off
setlocal
set source=c:\Users\Tony\jOceanus
set target=c:\Users\Tony\sourceforge\jOceanus\trunk

if ".%1" == "." goto :default
set dirNames=%1
goto :docopy

:default
set dirNames=jDataManager jDataModels jDateDay jDecimal jEventManager jFieldSet
set dirNames=%dirNames% jGordianKnot jHelpManager jJira jLayoutManager jMoneyWise
set dirNames=%dirNames% jPreferenceSet jSortedList jSpreadSheetManager jSvnManager jTableFilter

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