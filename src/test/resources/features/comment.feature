Feature: Comment management
  Background:
    Given There is added user with id 1 and name Adrian
    And The user with id 1 has the post with id 1 and content some-content

  Scenario: Fetching comments by post id
    Given The background criteria are met
    When The user with id 1 wants to add the comment with content some to the post with id 1
    Then The system returns the 200 response code to the user
    And The system returns the comment class with id 1 and content some

  Scenario: Fetching all comments
    Given The user with id 1 exists in database
    When The user with id 1 wants to search for all comments in the database
    Then The system returns a list of size 1
    And The system returns 200 response code to the user

  Scenario: Creating new comment
    Given The user with id 1 exists in database
    And The user with id 1 does not have comments in post with id 1
    When The user with id 1 wants to add new comment to the post with id 1 with content new-comment
    Then The system creates new comment with content new-comment

  Scenario: Deleting existing comment
    Given The user with id 1 exists in database
    And The post with id 1 exists in database
    And The comment with id 1 is attached to the post with id 1
    When The user wants to delete the comment with id 1 from the post with id 1
    Then The system returns code 200