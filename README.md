
# Trusts Frontend

This service allows users to register or maintain a trust. It asks initial questions to determine if the user needs to register or maintain their trust and redirects them to maintain-a-trust-frontend if they wish to maintain, otherwise this service is the hub for registering a trust and the data from all other trust registration services is gathered here for declaration.

To run locally using the micro-service provided by the service manager:

```bash
sm2 --start TRUSTS_ALL
```

alternatively if only the registration journey is needed use:

```bash
sm2 --start REGISTER_TRUST_ALL
```

or if only the maintain journey is needed use:

```bash
sm2 --start MAINTAIN_TRUST_ALL
```

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 9781 but is defaulted to that in build.sbt):

```bash
sbt run
```

Use the following command to run your local copy with the test-only routes:

```bash
sbt run -Dapplication.router=testOnlyDoNotUseInAppConf.Routes
```

## Testing the service
Run unit and integration tests before raising a PR to ensure your code changes pass the Jenkins pipeline. This runs all the unit tests and integration tests with scalastyle and checks for dependency updates:

`./run_all_tests.sh`

## Making content changes

### Messages in the past tense

For the Settlor registration journey several questions have both a past and present tense. 
Which tense the question is displayed in depends on whether the settlor is alive at the time of registration and the answer to the question `settlorAliveYesNo`. 

For example if the settlor is deceased the question `What is the settlor name?` becomes `What was the settlors name?`.
To add a past tense question create a new question with `PastTense` appended to the key for example: `settlorIndividualNamePastTense.checkYourAnswersLabel`.

The past tense question should then be displayed correctly in the print draft of the registration.

### A/B Testing

This service has Optimizely integration.

Pages created for the purpose of A/B testing are under the package name `abTestingUseOnly`. Routes should be prepended with "/det/"

Example: `/trusts-registration/det/sign-out`
