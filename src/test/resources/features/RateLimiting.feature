@ratelimit
Feature: Rate limit public and sensitive requests
  In order to prevent large-scale data extraction and abuse
  As an administrator
  I want public and sensitive endpoints to be rate limited

  Background:
    Given I'm not logged in

  Scenario: A client exceeding the public limit is told when to retry
    Given The "public" rate limit is 3 requests per 60 seconds
    When I request "/subjects" 4 times
    Then The response code is 429
    And The response has a Retry-After of 60 seconds
    And The error code is "TOO_MANY_REQUESTS"
    And The error message is "Too many requests"
    When The recovery clock advances by 60 seconds
    And I request "/subjects" 1 time
    Then The response code is 200

  Scenario: Limits can be changed without redeploying
    Given The "public" rate limit is 2 requests per 60 seconds
    When I request "/subjects" 3 times
    Then The response code is 429
    Given The "public" rate limit is 10 requests per 60 seconds
    When I request "/subjects" 1 time
    Then The response code is 200

  Scenario: A normal user never reaches the limits
    Given The "public" rate limit is 120 requests per 60 seconds
    And I login as "demo" with password "password"
    When I request "/subjects" 20 times
    Then The response code is 200
    When I request "/identity" 5 times
    Then The response code is 200

  Scenario: Registration has a stricter limit
    Given The "registration" rate limit is 2 requests per 3600 seconds
    When I register a new user with username "ratelimit1", email "ratelimit1@sample.app" and password "password"
    And I register a new user with username "ratelimit2", email "ratelimit2@sample.app" and password "password"
    Then The response code is 201
    When I register a new user with username "ratelimit3", email "ratelimit3@sample.app" and password "password"
    Then The response code is 429
    And The response has a Retry-After of 3600 seconds

  Scenario: Password recovery has a stricter limit
    Given The "recovery" rate limit is 2 requests per 900 seconds
    When I request password recovery 3 times for "recover@example.com"
    Then The response code is 429

  Scenario: Reporting has a stricter limit per account
    Given The "reports" rate limit is 2 requests per 3600 seconds
    And I login as "demo" with password "password"
    When I submit 3 reports
    Then The response code is 429
    And The response has a Retry-After of 3600 seconds
    And The error code is "TOO_MANY_REQUESTS"
    When I login as "admin" with password "password"
    And I submit 1 report
    Then The response code is 201
