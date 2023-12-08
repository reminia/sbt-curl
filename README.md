# sbt-curl ![ci](https://github.com/reminia/sbt-curl/actions/workflows/ci.yml/badge.svg)
sbt-curl is a sbt plugin to run curl commands in sbt. <br/>
Motivation is to learn sbt by doing and then I came up this idea.

## Features
* Run curl command directly in sbt, like `sbt curl www.google.com`.
* Run a file of curl commands, try `sbt curlTest`, it will execute the curl-test script defined.
  See the [example](src/sbt-test/sbt-curl/simple/curl-test).

TBD: maybe add eDSl to support test api with curl / expect command.
