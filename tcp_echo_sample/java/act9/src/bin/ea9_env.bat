@echo off
REM Licensed to the Apache Software Foundation (ASF) under one
REM or more contributor license agreements.  See the NOTICE file
REM distributed with this work for additional information
REM regarding copyright ownership.  The ASF licenses this file
REM to you under the Apache License, Version 2.0 (the
REM "License"); you may not use this file except in compliance
REM with the License.  You may obtain a copy of the License at
REM 
REM   http://www.apache.org/licenses/LICENSE-2.0
REM 
REM Unless required by applicable law or agreed to in writing,
REM software distributed under the License is distributed on an
REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
REM KIND, either express or implied.  See the License for the
REM specific language governing permissions and limitations
REM under the License.

REM set JAVA_HOME=
REM set PATH=%JAVA_HOME%\bin;%PATH%
REM set COMMONS_DAEMON_PROCRUN=
REM set PATH=%COMMONS_DAEMON_PROCRUN%;%PATH%

set EA9_HOME=%~dp0
set EA9_SVC_NAME=EchoesAct9
set EA9_SVC_DISPLAY_NAME="Echoes Act9"
set EA9_SVC_DESCRIPTION="Echoes Act9 Service"

set JAVA_OPTIONS_SRV=-Xmx200m -Xloggc:"%EA9_HOME%gc.log"

set CLASSPATH=
for %%I in ("%EA9_HOME%lib\*.jar") do call ea9_addclp.bat "%%I"
