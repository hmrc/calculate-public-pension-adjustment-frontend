
# Calculate Public Pension Adjustment Frontend

This service will help you correct your tax position if you have been affected by reformed public service pensions introduced in 2015.

You can:

 - calculate any compensation for previously paid lifetime and/or annual allowance charges
 - calculate new, reduced or extra lifetime and/or annual allowance charges that you will have to pay
 - submit your information to HMRC for review
 - submit information on behalf of someone else to HMRC for review. You can only do this if you have power of attorney or deputyship

## Backends
[Calculate Public Pension Adjustment](https://github.com/hmrc/calculate-public-pension-adjustment)

## Persistence
This service uses mongodb to persist user answers.

## Requirements
This service is written in Scala using the Play framework, so needs at least a JRE to run.

JRE/JDK 11 is recommended.

The service also depends on mongodb.

## Running the service
Using service manager (sm or sm2)
Use the PUBLIC_PENSION_ADJUSTMENT_ALL profile to bring up all services using the latest tagged releases
```
sm2 --start PUBLIC_PENSION_ADJUSTMENT_ALL
```

Run `sm2 -s` to check what services are running

## Launching the service locally
To bring up the service on the configured port 12804, use
```
sbt run
```

## Testing the service
This service uses sbt-scoverage to provide test coverage reports.

Use the following command to run the tests with coverage and generate a report.
``sbt clean coverage test it:test coverageReport``

## Scalafmt
To prevent formatting failures in a GitHub pull request,
run the command ``sbt scalafmtAll`` before pushing to the remote repository.


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
