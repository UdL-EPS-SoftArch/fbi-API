@reorder
Feature: Reorder questions
  Authors can save the question sequence without changing the order of existing attempts.

  Background:
    Given a public test with three ordered questions for reordering

  Scenario: The author saves an order used by new attempts
    Given an attempt exists before reordering
    And the question actor is "demo"
    When the author saves the question order "2,0,1"
    Then The response code is 201
    And the question actor is "other"
    And the public question order is "2,0,1"
    And the question actor is "demo"
    And the existing attempt retains order "0,1,2"
    And a new attempt uses order "2,0,1"

  Scenario Outline: Invalid orders leave the sequence unchanged
    Given the question actor is "demo"
    When the author saves the question order "<order>"
    Then The response code is 400
    And the public question order is "0,1,2"
    Examples:
      | order   |
      | 0,1     |
      | 0,1,1   |
      | 0,1,999 |

  Scenario Outline: Only the author or an administrator can reorder
    Given the question actor is "<actor>"
    When the author saves the question order "2,0,1"
    Then the reorder request is denied
    And the public question order is "0,1,2"
    Examples:
      | actor     |
      | other     |
      | anonymous |

  Scenario: An administrator can reorder
    Given the question actor is "admin"
    When the author saves the question order "2,0,1"
    Then The response code is 201
    And the public question order is "2,0,1"

  Scenario: An old attempt has no recorded question order
    Given an old attempt without a recorded order
    Then the old attempt question order is null

  Scenario: A submitted attempt retains its question order
    Given an attempt exists before reordering
    And the existing attempt is submitted
    When the author saves the question order "2,0,1"
    Then The response code is 201
    And the existing attempt retains order "0,1,2"

  Scenario: A question update cannot corrupt the saved order
    Given the question actor is "demo"
    When I change a question position directly
    Then the public question order is "0,1,2"
