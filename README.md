# cognito-resource-poc

## Summary

Demo application to show how to use spring with Amazon Cognito

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `de.codecentric.springbootsample.Application` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Endpoint's definition

* /auth/login : Endpoint  that performs a redirection to the login form of cognito.

* /auth/token : Endpoint that given a code as request param, makes an [authorization request](https://www.oauth.com/oauth2-servers/authorization/the-authorization-request/) and return the id_token, access_token and refresh_token.

* /user/me : Secured endpoint to get the JSON Web Token claims.


## Properties

* urls.cognito : cognito root auth url

* endpoints.authorize : cognito url for login

* endpoints.token : cognito url for authorization (get JWT)

* cognito.client : cognito client id

* cognito.secret : cognito client secret

* cognito.callback : valid callback url set in cognito (on this POC it will automatically redirect to the /auth/token endpoint)

* cognito.region : Cognito's aws region

* cognito.userPoolId : Cognito's User Pool Id

* cognito.keys : url for cognito jwt keys (format: https://cognito-idp.{region}.amazonaws.com/{userPoolId}/.well-known/jwks.json )

## Usage

1. Add correct values to application.yml .
2. Send a GET request to /auth/login for redirection to cognito login form.
3. After a successful login, cognito will invoke auth/token endpoint sending the code for the authorization.
4. Add 'Authorization' header with value 'Bearer {id_token} (obtained on the response of /auth/token) to access restricted endpoints (e.g: call /user/me to get the jwt claims).