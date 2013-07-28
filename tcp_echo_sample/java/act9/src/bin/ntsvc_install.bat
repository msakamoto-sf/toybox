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

call ea9_env.bat

set PRUNSRV_PARAM=//IS//%EA9_SVC_NAME% --DisplayName=%EA9_SVC_DISPLAY_NAME% --Description=%EA9_SVC_DESCRIPTION% --Startup=manual
set PRUNSRV_PARAM=%PRUNSRV_PARAM% --LogLevel=Debug --StdOutput=auto --StdError=auto
set PRUNSRV_PARAM=%PRUNSRV_PARAM% --Classpath=%CLASSPATH%
set PRUNSRV_PARAM=%PRUNSRV_PARAM% --StartMode=java --StartClass=echoes.act9.server.EchoesServer  --StartParams="%EA9_HOME%echoes-act9.ini"
set PRUNSRV_PARAM=%PRUNSRV_PARAM% --StopMode=java  --StopClass=echoes.act9.client.EchoesShutdown --StopParams="%EA9_HOME%echoes-act9.ini"

REM LogPath default : %SystemRoot%\System32\LogFiles\Apache
REM set PRUNSRV_PARAM=%PRUNSRV_PARAM% --LogPath=

prunsrv %PRUNSRV_PARAM%
