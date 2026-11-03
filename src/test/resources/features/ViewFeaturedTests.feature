Feature: View featured tests
    Visitors can discover published featured tests on the home page.

    Scenario: Only published featured tests appear in the public feed
        Given a published featured test "Spring basics" in subject "Software Architecture" of type "OFFICIAL"
        And an unpublished featured test "Draft test" in subject "Software Architecture" of type "CUSTOM"
        And a published test "Unfeatured test" in subject "Software Architecture" of type "CUSTOM" that is not featured
        When a visitor requests featured tests
        Then The response code is 200
        And the featured feed contains only "Spring basics" with subject "Software Architecture" and type "OFFICIAL"
