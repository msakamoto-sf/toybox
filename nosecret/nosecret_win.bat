@echo off
rem sample shortcut batch file for windows.

set NOSECRET_PY_OPENSSL_PATH=%ProgramFiles%\GNU\bin\openssl.exe
set NOSECRET_PY_EDITOR_PATH=%USERPROFILE%\Documents\PrivateApplications\vim\gvim.exe
C:\Python27\python.exe %~dp0\nosecret.py %*
