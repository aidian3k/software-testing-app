Feature: Comment management
  Background:
    Given There is added user with with email "jan@example.com" and name "Jan" and surname "Kowalski" and password "sample-password"
    And The user has a post with content "some-content"

  Scenario: Create a comment to the user's post
    Given The post does not have comments yet
    When The user wants to add the comment with content "some-comment-content" to the post
    Then The system returns the 201 response code to the user
    And The system returns the comment class with content "some-comment-content"

  Scenario: Fetching all comments attached to the post
    Given The post has a comment with content "some-comment-content"
    And The post has other comment with content "other-content"
    When User wants to search for all comments of the post
    Then The system returns a list of size 2
    And The list contains comment with content "some-comment-content" and comment with content "other-content"
    And The system returns 200 response code to the user

  Scenario: Fetching all comments
    Given The first post has a comment with content "first-post-comment"
    And There is a second post with content "second-post-content"
    And The second post has a comment with content "second-post-comment"
    When User wants to search for all comments in the database
    Then Returned list is of size 2
    And The list contains comment with content "first-post-comment" and comment with content "second-post-comment"
    And The system returns 200 response code

#  Scenario: Fetching specific comment
#    Given Comment with content "some-comment-content" exists in database
#    When User wants to search for the comment with content "some-comment-content"
#    Then The system returns the comment with content "some-comment-content"
#    And Response code is equal to 200

  Scenario: Deleting existing comment
    Given The post has the comment with content "some-comment-content"
    When The user wants to delete the comment with content "some-comment-content" from the post
    Then The system returns code 200

  Scenario: Delete not existing comment
    Given The post does not have any comments
    When User wants to delete not existing comment with random id 125123 from the post
    Then The system returns code 404