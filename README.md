
# single-customer-account-frontend

This is the main service repo, we currently only have this single frontend service, it's likely that this will be split into a frontend and backend to separate future business logic.

SCA Stub [single-customer-account-stub](https://github.com/hmrc/single-customer-account-stub#single-customer-account-stub) 
This is a testing repo for stubbing API calls to backend HOD API's or other MDTP API's.

### **Requirements**

* To run the service locally you need to start the SCA profile from service manager:
* 
* `sm2 --start SCA_FUTURES_ALL`
* 
* Then stop the SCA frontend using the service manager and start is locally:
* `sm2 --stop SINGLE_CUSTOMER_ACCOUNT_FRONTEND`
* `sbt run`

### **Using the application**

To log in using the Authority Wizard please click the link below:
* Local : http://localhost:9949/auth-login-stub/gg-sign-in
* QA : https://www.qa.tax.service.gov.uk/auth-login-stub/gg-sign-in?continue=%2Fsingle-customer-account
* Use the below credentials to login :
* Confidence Level: 200
* Redirect url local : http://localhost:8420/single-customer-account
* Redirect url local for testing deprecated layout method: http://localhost:8420/single-customer-account/oldLayout
* Redirect url QA :  /single-customer-account
* NINO: Pick one of the following
- AA999999A
- AA999999B
- AA999999C
- AA999999D (use this if you'd like the user's name on SCA to match the name displayed on the Self Assessment service)
- HT009413A (use this to access the NI/State Pension service)
- ER872414B (use this to access PTA)
  Add Enrolment : Enrolment Key : HMRC-PT, Identifier Name : NINO: Identifier Value: NINO value that is being used from the above step.
  (optional) If you'd like the SA tile to be displayed, navigate to the following section and enter:
- Enrolments - Presets - SA - Add Preset - Identifier Value: 1126388017

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

