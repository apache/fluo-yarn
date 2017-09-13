<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# Fluo YARN launcher

[![Build Status][ti]][tl] [![Apache License][li]][ll]

A tool for running Apache Fluo applications in Hadoop YARN.

**This is currently a work in progress that depends on unreleased features of Fluo and will not be ready for use until after Fluo 1.2.0 is released.**  Sometime after Fluo 1.2.0 is released this project will make its first release.

## Requirements

The Fluo YARN launcher requires [Apache Fluo][Fluo] and [Hadoop YARN][YARN]:

| Software    | Recommended Version | Minimum Version |
|-------------|---------------------|-----------------|
| [Fluo]      | 1.2.0               | 1.2.0           |
| [YARN]      | 2.7.2               | 2.6.0           |

See the [related projects page][related] for external projects that may help in setting up these dependencies.

## Set up your Fluo application

Before you can launch a Fluo application in YARN, you should follow Fluo's [install] and [application] instructions
to install Fluo and initialize a Fluo application. After your application has been initialized, follow
the insructions below to install the Fluo YARN launcher and run your application in YARN. Avoid using the
`fluo` command to start local oracle and worker processes if you are running in YARN.

## Install and Configure Fluo YARN launcher

Before you can install the Fluo YARN launcher, you will need to obtain a distribution tarball. It is
recommended that you download the [latest release][release]. You can also build a distribution from the
master branch by following these steps which create a tarball in `distribution/target`:

    git clone https://github.com/apache/fluo-yarn.git
    cd fluo-yarn/
    mvn package

After you obtain a Fluo YARN distribution tarball, follow these steps to install Fluo.

1. Choose a directory with plenty of space, untar the distribution, and run `fetch.sh` to retrieve dependencies:

        tar -xvzf fluo-yarn-1.0.0-bin.tar.gz
        cd fluo-yarn-1.0.0
        ./lib/fetch.sh

    The distribution contains a `fluo-yarn` script in `bin/` that administers Fluo and the
    following configuration files in `conf/`:

    | Configuration file          | Description                                                             |
    |-----------------------------|-------------------------------------------------------------------------|
    | [fluo-yarn-env.sh]          | Configures classpath for `fluo-yarn` script. Required for all commands. |
    | [fluo-yarn.properties]      | Configures how application runs in YARN.  Required for `start` command. |
    | [log4j.properties]          | Configures logging                                                      |

2. Configure [fluo-yarn-env.sh]

    * Set `FLUO_HOME` if it is not in your environment
    * Modify `FLUO_CONN_PROPS` if you don't want use the default.

3. Configure [fluo-yarn.properties] to set how your application will be launched in YARN:

    * YARN resource manager hostname
    * Number of oracle and worker instances
    * Max memory usage per oracle/worker

   If you are managing multiple Fluo applications in YARN, you can copy this file and configure it for
   each application.

## Start Fluo application in YARN

Follow the instructions below to start your application in YARN. If you have not done so already, you should [initialize
your Fluo application][application] before following these instructions.

1. Configure [fluo-yarn-env.sh] and [fluo-yarn.properties] if you have not already.

2. Run the commands below to start your Fluo application in YARN.

        fluo-yarn start myapp conf/fluo-yarn.properties

   The commands will retrieve your application configuration and observer jars (using your application name) before
   starting the application in YARN. The command will output a YARN application ID that can be used to find your
   application in the YARN resource manager and view its logs.

## Manage Fluo application in YARN

Except for stopping your application in YARN, the `fluo` script can be used to manage your application using the
`scan` and `wait` commands.  See [Fluo's application instructions][application] for more information.

When you want you stop your Fluo application, use the the YARN resource manager or the 
`yarn application -kill <App ID>` to stop the application in YARN.

[Fluo]: https://fluo.apache.org/
[YARN]: http://hadoop.apache.org/
[related]: https://fluo.apache.org/related-projects/
[related]: https://fluo.apache.org/related-projects/
[install]: https://github.com/apache/fluo/blob/master/docs/install.md
[application]: https://github.com/apache/fluo/blob/master/docs/applications.md
[release]: https://fluo.apache.org/download/
[fluo-yarn-env.sh]: distribution/conf/fluo-yarn-env.sh
[fluo-yarn.properties]: distribution/conf/fluo-yarn.properties
[log4j.properties]: distribution/conf/log4j.properties
[ti]: https://travis-ci.org/apache/fluo-yarn.svg?branch=master
[tl]: https://travis-ci.org/apache/fluo-yarn
[li]: http://img.shields.io/badge/license-ASL-blue.svg
[ll]: https://github.com/apache/fluo-yarn/blob/master/LICENSE
