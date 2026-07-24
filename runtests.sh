#!/bin/bash
sbt it:test
sbt clean coverage compile scalastyle test dependencyUpdates coverageReport
