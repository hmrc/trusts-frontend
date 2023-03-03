
# Trusts Frontend

This service allows users to register or maintain a trust. It asks initial questions to determine if the user needs to register or maintain their trust and redirects them to maintain-a-trust-frontend if they wish to maintain, otherwise this service is the hub for registering a trust and the data from all other trust registration services is gathered here for declaration.

To run locally using the micro-service provided by the service manager:

***sm2 --start TRUSTS_ALL -r***

alternatively if only the registration journey is needed use:

***sm2 --start REGISTER_TRUST_ALL -r***

or if only the maintain journey is needed use:

***sm2 --start MAINTAIN_TRUST_ALL -r***

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 9781 but is defaulted to that in build.sbt):

***sbt run***

## Testing the service
Run unit and integration tests before raising a PR to ensure your code changes pass the Jenkins pipeline. This runs all the unit tests and integration tests with scalastyle and checks for dependency updates:

`./run_all_tests.sh`