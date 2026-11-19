@us-est-04
Feature: Calculate and preserve attempt scores
  Scenario Outline: Server grades stored answers and ignores supplied results
    Given a published <type> scoring test
    When a student answers and submits with forged grading data
    Then the persisted attempt score is 2 with server-graded answers
    Examples:
      | type     |
      | CUSTOM   |
      | OFFICIAL |

  Scenario Outline: An attempt uses the test version at its start
    Given a published <type> scoring test
    And a student starts and answers a scoring attempt
    When the scoring test is changed before submission
    And the student submits the scoring attempt
    Then the persisted attempt score is 2 with server-graded answers
    Examples:
      | type     |
      | CUSTOM   |
      | OFFICIAL |

  Scenario Outline: A new attempt uses the edited test
    Given a published <type> scoring test
    When the scoring test is changed before submission
    And a student answers and submits a new scoring attempt
    Then the persisted attempt score is 10 with server-graded answers
    Examples:
      | type     |
      | CUSTOM   |
      | OFFICIAL |

  Scenario Outline: Changing points preserves a submitted score
    Given a published <type> scoring test
    And a student answers and submits with forged grading data
    When the scoring author changes the first question points
    Then the scoring edit is persisted
    And the persisted attempt score remains 2
    Examples:
      | type     |
      | CUSTOM   |
      | OFFICIAL |

  Scenario Outline: Changing the correct answer preserves a submitted score
    Given a published <type> scoring test
    And a student answers and submits with forged grading data
    When the scoring author changes the second correct answer
    Then the scoring edit is persisted
    And the persisted attempt score remains 2
    Examples:
      | type     |
      | CUSTOM   |
      | OFFICIAL |

  Scenario Outline: A recorded official score cannot be directly edited
    Given a published OFFICIAL scoring test
    And a student answers and submits with forged grading data
    When "<actor>" tries to change the recorded score
    Then the score change is rejected
    And the persisted attempt score remains 2
    Examples:
      | actor   |
      | student |
      | admin   |

  Scenario Outline: Deleting answered content preserves the in-progress result
    Given a published CUSTOM scoring test
    And a student starts and answers a scoring attempt
    When the scoring author deletes the answered <content>
    And the student submits the scoring attempt
    Then the persisted attempt score remains 2
    Examples:
      | content  |
      | question |
      | option   |
