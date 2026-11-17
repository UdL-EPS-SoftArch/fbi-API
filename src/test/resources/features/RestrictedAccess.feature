Feature: Restricted API access
  Scenario Outline: Visitors cannot access restricted operations
    Given I'm not logged in
    When I send an anonymous "<method>" request to "<path>"
    Then The response code is 401
    And The response has no body or redirect
    Examples:
      | method | path                              |
      | POST   | /testAttempts                     |
      | POST   | /tests                            |
      | POST   | /reports                          |
      | GET    | /identity                         |
      | GET    | /testAttempts                     |
      | GET    | /answerAttempts                   |
      | GET    | /reports                          |
      | GET    | /notifications                    |
      | GET    | /reports/search/findByStatus      |

  Scenario Outline: Public catalog remains accessible
    When I send an anonymous "GET" request to "<path>"
    Then The response code is 200
    Examples:
      | path                                     |
      | /subjects                                |
      | /tests                                   |
      | /badges                                  |
      | /leaderboards                            |
      | /subjects/search/findByName?name=missing  |
      | /tests/search/findByTitleContaining?text=missing |
      | /users/search/findByUsernameContaining?text=missing |

  Scenario Outline: Subject creation requires administrator role
    Given I login as "<user>" with password "password"
    When I create a subject through the API
    Then The response code is <status>
    Examples:
      | user  | status |
      | demo  | 403    |
      | admin | 201    |

  Scenario: Visitors only see published public tests in the catalog
    Given There is a published official test "Visible catalog test" with one question
    And There is a private test "Private catalog test"
    When I send an anonymous "GET" request to "/tests"
    Then The response code is 200
    And The catalog contains "Visible catalog test" but not "Private catalog test"

  Scenario: A configured origin can preflight a restricted operation
    When A configured origin sends a preflight request for POST "/tests"
    Then The response code is 200
    And The preflight permits the configured origin and POST
