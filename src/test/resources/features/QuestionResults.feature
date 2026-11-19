@us-est-04
Feature: Preserve results after changing questions
  Submitted attempts retain their recorded results.

  Scenario Outline: Adding a question preserves submitted attempts
    Given a published <type> question test with submitted attempts
    And the question actor is "<actor>"
    When I POST a question with statement "Added question"
    Then The response code is 201
    And submitted results after adding a question are correct
    Examples:
      | type     | actor |
      | CUSTOM   | demo  |
      | OFFICIAL | admin |

  Scenario Outline: Changing a question score preserves submitted attempts
    Given a published <type> question test with submitted attempts
    And the question actor is "<actor>"
    When I PATCH the existing question score to 4
    Then The response code is 200
    And submitted results after changing the score are correct
    Examples:
      | type     | actor |
      | CUSTOM   | demo  |
      | OFFICIAL | admin |

  Scenario Outline: Deleting a question preserves submitted scores
    Given a published <type> question test with submitted attempts
    And the question has a report
    And the question actor is "<actor>"
    When I DELETE the existing question
    Then The response code is 200
    And the report is retained after deleting the question
    And submitted results after deleting the question are correct
    Examples:
      | type     | actor |
      | CUSTOM   | demo  |
      | OFFICIAL | admin |

  Scenario: An in-progress attempt keeps its original questions on submission
    Given a published CUSTOM question test with an in-progress attempt
    And the question actor is "demo"
    When I POST a question with statement "Added question"
    Then The response code is 201
    When I submit the in-progress question attempt
    Then The response code is 200
    And the submitted question attempt has 1 unanswered questions
