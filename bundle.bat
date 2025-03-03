@echo off 
setlocal
set GITBDL=..\gitwork.bdl
FOR /F %%I IN ('git symbolic-ref --short HEAD') DO @SET "GITBR=%%I"
if exist %GITBDL% del %GITBDL%
git bundle create %GITBDL% main..%GITBR%
endlocal