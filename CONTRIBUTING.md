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

# Contributing to Fluo Yarn

## Building Fluo Yarn

If you have [Git], [Maven], and [Java][java] (version 8+) installed, run these commands to build
Fluo Yarn:

    git clone https://github.com/apache/fluo-yarn.git
    cd fluo-yarn
    mvn package

## Pull Request

Before making a pull request please attempt to run `mvn verify`.  If it fails and you are not sure 
why, it's OK to go ahead and make the pull request.

## See Also

* [How to Contribute][contribute] on Apache Fluo project website

[Git]: https://git-scm.com/
[java]: http://openjdk.java.net/
[Maven]: https://maven.apache.org/
[contribute]: https://fluo.apache.org/how-to-contribute/
