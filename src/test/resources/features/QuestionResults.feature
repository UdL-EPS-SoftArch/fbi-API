Feature: Update results after changing questions
  Published tests use the current questions to calculate results and rankings.

  Scenario Outline: Adding a question updates submitted attempts
    Given a published <type> question test with submitted attempts
    And the question actor is "<actor>"
    When I POST a question with statement "Added question"
    Then The response code is 201
    And submitted results after adding a question are correct
    And the rankings reflect the recalculated <type> results
    Examples:
      | type     | actor |
      | CUSTOM   | demo  |
      | OFFICIAL | admin |

  Scenario Outline: Changing a question score recalculates submitted attempts
    Given a published <type> question test with submitted attempts
    And the question actor is "<actor>"
    When I PATCH the existing question score to 4
    Then The response code is 200
    And submitted results after changing the score are correct
    And the rankings reflect the recalculated <type> results
    Examples:
      | type     | actor |
      | CUSTOM   | demo  |
      | OFFICIAL | admin |

  Scenario Outline: Deleting a question removes its contribution
    Given a published <type> question test with submitted attempts
    And the question has a report
    And the question actor is "<actor>"
    When I DELETE the existing question
    Then The response code is 200
    And submitted results after deleting the question are correct
    And the question options and answers are removed and its report is retained
    And the rankings reflect the recalculated <type> results
    Examples:
      | type     | actor |
      | CUSTOM   | demo  |
      | OFFICIAL | admin |

  Scenario: A score change can change the best official attempt
    Given a published OFFICIAL question test with submitted attempts
    And another question makes the second attempt worth 1 point
    And the question actor is "admin"
    When I PATCH the existing question score to 0
    Then The response code is 200
    And the second attempt becomes the best result in both rankings

  Scenario: An in-progress attempt uses newly added questions on submission
    Given a published CUSTOM question test with an in-progress attempt
    And the question actor is "demo"
    When I POST a question with statement "Added question"
    Then The response code is 201
    When I submit the in-progress question attempt
    Then The response code is 200
    And the submitted question attempt has 2 unanswered questions
