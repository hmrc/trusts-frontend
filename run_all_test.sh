#!/usr/bin/env bash

sbt clean scalastyleAll compile coverage Test/test it/test coverageOff coverageReport dependencyUpdates
