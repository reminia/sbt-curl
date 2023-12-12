# sbt-curl ![ci](https://github.com/reminia/sbt-curl/actions/workflows/ci.yml/badge.svg)

sbt-curl is a sbt plugin to run curl commands in sbt. <br/>
The motivation is to learn sbt by doing and then I came up with this idea. <br/>
With this plugin, you can test your api by curl command within sbt.

## Features

* Run curl command directly in sbt, like `> curl www.google.com`.
* Run a file of curl commands, try `sbt curlTest`, it will execute the curl-test script defined.
  See the [example](src/sbt-test/sbt-curl/simple/curl-test).

TBD: maybe add eDSl to support test api with curl / expect command.

## Usage

Add the plugin:

```
resolvers += Resolver.url("GitHub Package Registry", url("https://maven.pkg.github.com/reminia/_"))(
  Resolver.ivyStylePatterns
)
addSbtPlugin("me.yceel" % "sbt-curl" % "0.1.0")
```

You can:
* run curl command directly in your sbt eval.
* run `curlTest` to execute all curl commands in the curl-script. <br/>
  Curl script is a file named `curl-script` or `curl-test` under root directory
  or root/project directory.

Check the project [api](src/sbt-test/sbt-curl/api/curl-test) for reference.
