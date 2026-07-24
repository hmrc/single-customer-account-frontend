# single-customer-account-frontend

This is a mock frontend service used for testing the integration of the sca-wrapper library and single-customer-account-wrapper-data backend microservice.

Summary
-----------
This service covers the Single Customer Account journey. Use the service to:
* sign in locally using the auth login stub
* view the SCA frontend journey
* test against stubbed backend services

Requirements
------------

This service is written in [Scala 3.x](http://www.scala-lang.org/) and [Play 3.x](http://playframework.com/), so needs at least a [JRE 21](http://www.oracle.com/technetwork/java/javase/downloads/index.html) to run.

How to test the project
=======================

Unit Tests
----------
- **Unit test the entire test suite:** `sbt test`

- **Unit test a single spec file:** `sbt "test:testOnly *fileName"` (for example: `sbt "test:testOnly *HomeControllerSpec"`)

Integration tests
----------------
- **`sbt it/test`**

Acceptance tests
----------------
To verify the acceptance tests locally, follow the steps:
- start the sm2 container for SCA profile: `sm2 --start SCA_FUTURES_ALL`
- stop `SINGLE_CUSTOMER_ACCOUNT_FRONTEND` process running in sm2: `sm2 --stop SINGLE_CUSTOMER_ACCOUNT_FRONTEND`
- launch `single-customer-account-frontend` in terminal and execute the following command in the project directory: `sbt run`
- open the acceptance test suite repository in the terminal and execute the local run script: `./run_specs_local.sh`

Acronyms
--------
In the context of this service we use the following acronyms:

* [SCA]: Single Customer Account
* [API]: Application Programming Interface
* [JRE]: Java Runtime Environment
* [JSON]: JavaScript Object Notation
* [URL]: Uniform Resource Locator

License
--------

This code is open source software licensed under the [Apache 2.0 License].

[Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0.html
