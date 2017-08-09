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

####################################
# General variables that must be set
####################################

## Fluo installation
export FLUO_HOME="${FLUO_HOME:-/path/to/fluo}"
## Hadoop installation
export HADOOP_PREFIX="${HADOOP_PREFIX:-/path/to/hadoop}"
## File that contains properties need to connect to Fluo
export FLUO_CONN_PROPS=${FLUO_CONN_PROPS:-$FLUO_HOME/conf/fluo-conn.properties}

###########################
# Build classpath variables
###########################

## A classpath needs to built for the YARN launcher and the launched Fluo application

## Classpath for launched Fluo application
export FLUO_CLASSPATH=$($FLUO_HOME/bin/fluo classpath)

## Classpath of Fluo YARN Launcher
addToClasspath()
{
  local dir=$1
  local filterRegex=$2

  if [ ! -d "$dir" ]; then
    echo "ERROR $dir does not exist or not a directory"
    exit 1
  fi

  for jar in $dir/*.jar; do
    if ! [[ $jar =~ $filterRegex ]]; then
       LAUNCHER_CLASSPATH="$LAUNCHER_CLASSPATH:$jar"
    fi
  done
}

# Any jars matching this pattern is excluded from classpath
EXCLUDE_RE="(.*log4j.*)|(.*asm.*)|(.*guava.*)|(.*gson.*)"
LAUNCHER_CLASSPATH="$lib/*"
addToClasspath "$HADOOP_PREFIX/share/hadoop/common" $EXCLUDE_RE;
addToClasspath "$HADOOP_PREFIX/share/hadoop/common/lib" $EXCLUDE_RE;
addToClasspath "$HADOOP_PREFIX/share/hadoop/yarn" $EXCLUDE_RE;
addToClasspath "$HADOOP_PREFIX/share/hadoop/yarn/lib" $EXCLUDE_RE;
export LAUNCHER_CLASSPATH
