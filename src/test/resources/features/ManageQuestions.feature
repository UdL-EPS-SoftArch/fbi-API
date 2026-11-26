Feature: Manage questions in a test
  Authors manage their own questions and administrators moderate all tests.

  Background:
    Given a CUSTOM draft question test owned by "demo" with 1 questions

  Scenario: A new question is appended with defaults
    Given I login as "demo" with password "password"
    When I POST a question with statement "New question"
    Then The response code is 201
    And the new question has default values and follows the existing questions

  Scenario Outline: Invalid statements are rejected
    Given I login as "demo" with password "password"
    When I POST a question with statement kind "<statement>"
    Then The response code is 400
    And the question test still has 1 questions
    Examples:
      | statement |
      | missing   |
      | empty     |
      | spaces    |

  Scenario: More than 100 questions are allowed
    Given the question test has 100 questions
    And I login as "demo" with password "password"
    When I POST a question with statement "Question 101"
    Then The response code is 201
    And the question test still has 101 questions
    And the new question has default values and follows the existing questions

  Scenario Outline: Authors and administrators manage questions
    Given I login as "<user>" with password "password"
    When I <method> the existing question
    Then The response code is <status>
    And the question operation "<method>" is persisted
    Examples:
      | user  | method | status |
      | demo  | PUT    | 200    |
      | demo  | PATCH  | 200    |
      | demo  | DELETE | 200    |
      | admin | POST   | 201    |
      | admin | PUT    | 200    |
      | admin | PATCH  | 200    |
      | admin | DELETE | 200    |

  Scenario Outline: Other users and anonymous visitors cannot manage questions
    Given the question actor is "<actor>"
    When I <method> the existing question
    Then the question request is denied
    And the original question remains unchanged
    Examples:
      | actor     | method |
      | other     | POST   |
      | other     | PUT    |
      | other     | PATCH  |
      | other     | DELETE |
      | anonymous | POST   |
      | anonymous | PUT    |
      | anonymous | PATCH  |
      | anonymous | DELETE |

  Scenario: A question cannot be moved to another test
    Given I login as "demo" with password "password"
    When I move the existing question to another test
    Then The response code is 400
    And the original question remains unchanged

  Scenario Outline: Edited statements must be nonblank
    Given I login as "demo" with password "password"
    When I <method> the existing question with an empty statement
    Then The response code is 400
    And the original question remains unchanged
    Examples:
      | method |
      | PUT    |
      | PATCH  |

  Scenario: Adding after a deletion appends after the remaining questions
    Given the question test has 3 questions
    And I login as "demo" with password "password"
    When I DELETE the existing question
    Then The response code is 200
    When I POST a question with statement "New question"
    Then The response code is 201
    And the new question follows the highest remaining position
