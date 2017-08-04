# Licensed to the Apache Software Foundation (ASF) under one or more contributor license
# agreements.  See the NOTICE file distributed with this work for additional information regarding
# copyright ownership.  The ASF licenses this file to you under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with the License.  You may
# obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied. See the License for the specific language governing permissions and limitations under
# the License.

## Before fluo-yarn-env.sh is loaded, these environment variables are set and can be used in this file:

# cmd - Command that is being called such as oracle, worker, etc.
# app - Fluo application name 
# basedir - Root of Fluo YARN launcher installation
# conf - Directory containing Fluo YARN launcher configuration
# lib - Directory containing Fluo YARN launcher libraries

## Fluo installation
export FLUO_HOME="${FLUO_HOME:-/path/to/fluo}"

# File that contains properties need to connect to Fluo
export FLUO_CONN_PROPS=${FLUO_CONN_PROPS:-$FLUO_HOME/conf/fluo-conn.properties}

# Fluo Yarn deals with two classpaths, one for the launcher and another for
# the launched application.  The following is used to build the classpath for
# the launched application.  Later in this file CLASSPATH is defined and that is
# used for the launcher.
export FLUO_CLASSPATH=$($FLUO_HOME/bin/fluo classpath)

# The classpath for Fluo must be defined.  The Fluo tarball does not include
# jars for Accumulo, Zookeeper, or Hadoop.  This example env file offers two
# ways to setup the classpath with these jars.  Go to the end of the file for
# more info.

# This function attempts to obtain Accumulo, Hadoop, and Zookeeper jars from
# Fluo classpath
setupClasspathFromSystem()
{
  CLASSPATH="$lib/*:$FLUO_CLASSPATH"
}

# This function obtains Accumulo, Hadoop, and Zookeeper jars from
# $lib/ahz/. Before using this function, make sure you run
# `./lib/fetch.sh ahz` to download dependencies to this directory.
setupClasspathFromLib(){
  CLASSPATH="$lib/*:$lib/ahz/*"
}

# Call one of the following functions to setup the classpath or write your own
# bash code to setup the classpath for Fluo. You must also run the command
# `./lib/fetch.sh extra` to download extra Fluo dependencies before using Fluo.

setupClasspathFromSystem
#setupClasspathFromLib
