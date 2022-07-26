#!/bin/bash
sbt clean coverage compile scalastyle test dependencyUpdates coverageReport
sbt it:test
