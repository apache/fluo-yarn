#! /usr/bin/env bash

# Licensed to the Apache Software Foundation (ASF) under one or more contributor license
# agreements. See the NOTICE file distributed with this work for additional information regarding
# copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance with the License. You may obtain a
# copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied. See the License for the specific language governing permissions and limitations under
# the License.

lib_dir=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
maven_prefix=https://repo1.maven.org/maven2

function download {
  IFS=':' read -ra DEP <<< "$1"
  dir=$lib_dir/
  if [ -n "$2" ]; then
    dir=$lib_dir/$2
    if [ ! -d $dir ]; then
      mkdir $dir
    fi
  fi
  group=${DEP[0]}
  artifact=${DEP[1]}
  ftype=${DEP[2]}
  version=${DEP[3]}
  fn=$artifact-$version.$ftype
  path="${group//.//}/$artifact/$version/$fn"
  download_url=$maven_prefix/$path

  if [ -f $dir/$fn ]; then
    echo "SUCCESS: Dependency exists - $dir/$fn"
  else
    wget -q $download_url -P $dir
    if [ $? == 0 ]; then
      echo "SUCCESS: Dependency downloaded from $download_url"
    else
      echo "ERROR: Dependency download failed - $download_url"
    fi
  fi
}

echo "Fetching Fluo YARN launcher dependencies"
download aopalliance:aopalliance:jar:1.0
download ch.qos.logback:logback-classic:jar:1.1.3
download ch.qos.logback:logback-core:jar:1.1.3
download com.101tec:zkclient:jar:0.3
download com.google.code.findbugs:jsr305:jar:2.0.1
download com.google.code.gson:gson:jar:2.8.0
download com.google.guava:guava:jar:13.0.1
download com.yammer.metrics:metrics-annotation:jar:2.2.0
download com.yammer.metrics:metrics-core:jar:2.2.0
download net.sf.jopt-simple:jopt-simple:jar:3.2
download org.apache.kafka:kafka_2.10:jar:0.8.0
download org.apache.twill:twill-api:jar:0.12.0
download org.apache.twill:twill-common:jar:0.12.0
download org.apache.twill:twill-core:jar:0.12.0
download org.apache.twill:twill-discovery-api:jar:0.12.0
download org.apache.twill:twill-discovery-core:jar:0.12.0
download org.apache.twill:twill-ext:jar:0.12.0
download org.apache.twill:twill-yarn:jar:0.12.0
download org.apache.twill:twill-zookeeper:jar:0.12.0
download org.ow2.asm:asm-all:jar:5.0.2
download org.scala-lang:scala-compiler:jar:2.10.1
download org.scala-lang:scala-library:jar:2.10.1
download org.scala-lang:scala-reflect:jar:2.10.1
download org.slf4j:jcl-over-slf4j:jar:1.7.2
download org.slf4j:log4j-over-slf4j:jar:1.7.12
download org.slf4j:slf4j-api:jar:1.7.12
download org.xerial.snappy:snappy-java:jar:1.0.5
# See https://github.com/apache/fluo/issues/820
download io.netty:netty:jar:3.9.9.Final

echo -e "Done!\n"
echo "NOTE - The dependencies downloaded have been tested with some versions of Fluo, Hadoop, Zookeeper, and Accumulo."
echo "There is no guarantee they will work with all versions. The Fluo YARN launcher chose to defer dependency resolution to as"
echo "late as possible in order to make it easier to resolve dependency conflicts.  If you run into a dependency"
echo "conflict in your environment, please consider bringing it up on the Fluo dev list."
