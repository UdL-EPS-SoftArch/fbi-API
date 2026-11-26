@US-COM-08
Feature: View a student's public profile
  As a visitor, student or administrator
  I want to see the information a student has made public

  Background:
    Given There is a registered user with username "public-profile-student" and password "password" and email "public-profile-student@sample.app"
    And the public profile of "public-profile-student" has name "Profile Student" and avatar "https://example.org/student.png"

  Scenario Outline: A public profile shows only public content
    Given "public-profile-student" has badges, leaderboard positions, tests and a completed attempt
    And the public profile viewer is "<viewer>"
    When I open the public profile of "public-profile-student"
    Then The response code is 200
    And the public profile shows "public-profile-student" with avatar "https://example.org/student.png"
    And the only badge shown on the public profile is "Visible profile badge"
    And the public profile shows global rank 2 and rank 3 for "Public profile subject"
    And the only custom test shown on the public profile is "Published public custom test"
    And the public profile hides "fullName"
    And the public profile hides "email"
    And the public profile exposes no attempt history or detailed results

    Examples:
      | viewer  |
      | visitor |
      | demo    |
      | admin   |

  Scenario Outline: A private profile shows only the public name
    Given "public-profile-student" has badges, leaderboard positions, tests and a completed attempt
    And "public-profile-student" lets the public profile show their full name true and email true
    And "public-profile-student" makes their public profile private
    And the public profile viewer is "<viewer>"
    When I open the public profile of "public-profile-student"
    Then The response code is 200
    And the public profile only identifies "public-profile-student" as private

    Examples:
      | viewer  |
      | visitor |
      | demo    |
      | admin   |

  Scenario Outline: Administrator accounts and missing users have no public profile
    Given the public profile viewer is "<viewer>"
    When I open the public profile of "<username>"
    Then The response code is 404

    Examples:
      | viewer  | username                |
      | visitor | admin                   |
      | demo    | admin                   |
      | admin   | admin                   |
      | visitor | missing-public-profile |

  Scenario Outline: Students decide independently which personal details are public
    Given "public-profile-student" lets the public profile show their full name <showName> and email <showEmail>
    And I'm not logged in
    When I open the public profile of "public-profile-student"
    Then The response code is 200
    And The profile field "<visible>" is "<value>"
    And the public profile hides "<hidden>"

    Examples:
      | showName | showEmail | visible  | value                             | hidden   |
      | true     | false     | fullName | Profile Student                   | email    |
      | false    | true      | email    | public-profile-student@sample.app | fullName |

  Scenario: The public profile does not reveal anonymous leaderboard positions
    Given "public-profile-student" has badges, leaderboard positions, tests and a completed attempt
    And "public-profile-student" chooses anonymity in leaderboards
    And I login as "demo" with password "password"
    When I open the public profile of "public-profile-student"
    Then The response code is 200
    And the public profile shows "public-profile-student" with avatar "https://example.org/student.png"
    And the public profile has no leaderboard positions
