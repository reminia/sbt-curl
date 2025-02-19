# sbt-curl ![ci](https://github.com/reminia/sbt-curl/actions/workflows/ci.yml/badge.svg)

sbt-curl is a sbt plugin to run curl commands in sbt. <br/>
With this plugin, you can test your api directly with curl in sbt.

## Features

* Run curl command directly in sbt, like `> curl www.google.com`.
* Run a file of curl commands by `sbt curlTest`, it will execute the curl-test script defined in your project.
  See the [example](src/sbt-test/sbt-curl/simple/curl-test).

(TBD): Potentially add an eDSL to facilitate testing APIs with curl and expect commands, similar to the functionality in Cypress.

## Usage

Add the plugin:

```scala
resolvers += Resolver.url("GitHub Package Registry", url("https://maven.pkg.github.com/reminia/_"))(
  Resolver.ivyStylePatterns
)
credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  "_",
  System.getenv("GITHUB_TOKEN")
)
addSbtPlugin("me.yceel" % "sbt-curl" % "0.1.1")
```

You can:
* run curl command directly in your sbt REPL.
* run `curlTest` to execute all curl commands in the curl-script. <br/>
  Curl script is a file named `curl-script` or `curl-test` under root directory
  or root/project directory.

Check the project [api](src/sbt-test/sbt-curl/api/curl-test) for reference.
