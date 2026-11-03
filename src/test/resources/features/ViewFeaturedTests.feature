Feature: View featured tests
    Visitors can discover published featured tests on the home page.

    Scenario: Only public published featured tests appear in the public feed
        Given a published featured test "Spring basics" in subject "Software Architecture" of type "OFFICIAL"
        And an unpublished featured test "Draft test" in subject "Software Architecture" of type "CUSTOM"
        And a published test "Unfeatured test" in subject "Software Architecture" of type "CUSTOM" that is not featured
        And a private published featured test "Private test" in subject "Software Architecture" of type "CUSTOM"
        When a visitor requests featured tests
        Then The response code is 200
        And the featured feed contains only "Spring basics" with subject "Software Architecture" and type "OFFICIAL"

    Scenario: The featured feed is empty when there are no tests
        Given there are no tests
        When a visitor requests featured tests
        Then The response code is 200
        And the featured feed is empty
