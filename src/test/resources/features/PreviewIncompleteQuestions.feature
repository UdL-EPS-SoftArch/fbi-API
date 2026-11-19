@preview
Feature: Identify incomplete questions for a test preview
  Authors can highlight draft questions that do not meet publication requirements.

  Scenario Outline: Authors and administrators see incomplete questions in test order
    Given a draft preview test with complete and incomplete questions
    And I login as "<user>" with password "password"
    When I request incomplete questions for the preview test
    Then The response code is 200
    And the incomplete preview questions are in the expected order

    Examples:
      | user  |
      | demo  |
      | admin |

  Scenario Outline: Other tests or users yield no incomplete questions
    Given There is a registered user with username "other" and password "password" and email "other-preview@example.com"
    And a <fixture> preview test
    And I login as "<user>" with password "password"
    When I request incomplete questions for the preview test
    Then The response code is 200
    And the incomplete preview questions are empty

    Examples:
      | fixture     | user  |
      | empty draft | demo  |
      | published   | demo  |
      | draft       | other |
      | nonexistent | demo  |

  Scenario: An anonymous visitor cannot inspect incomplete questions
    Given a draft preview test with complete and incomplete questions
    And I'm not logged in
    When I request incomplete questions for the preview test
    Then The response code is 401
