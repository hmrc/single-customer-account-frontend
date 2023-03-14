#!/bin/bash
sbt it:test
sbt clean compile scalastyle test dependencyUpdates
