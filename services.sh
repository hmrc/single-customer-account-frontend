#!/bin/bash
sm --start SCA_FUTURES_ALL
sm --stop SINGLE_CUSTOMER_ACCOUNT_FRONTEND
sbt run
