Feature: Management of user's posts
  Background:
    Given In db there is a user with id 1, name "Adrian" and email "dolphin@wp.pl"

  Scenario Outline: As a user I want to create new posts
    When The user with id 1 tries to add new posts with valid content "<content>"
    Then The system should return post with content "<content>"
    And The system should return the 201 response code to the user

    Examples:
    | content |
    | some-content |
    | some-other-valid-content |

  Scenario: Creating new post with too long content
    When The user with id 1 tries to add new post with content which has length 700
    Then The system should return 400 response code to the user

  Scenario: As a user I want to get a list of my posts
    Given The user with id 1 has at least two posts added
    When The user with id 1 wants to see all posts data
    Then The system should return the list of posts with size greater than 1
    And The system should return 200 response code

  Scenario: As a user I want to get post by Id
    Given In DB there is post with id 1 by user with id 1 with content "some-content"
    When The user wants to find the post with id 1
    Then The system should return requested post with content "some-content"
    And The system response should be 200

  Scenario: Get post with non existing id
    Given In database there is no post with id 100
    When The user wants to get the data of post with id 100
    Then The system should return 404 not found to the user
#
#  Scenario: Delete post with non-existing id
#    Given In datbase there is not a post with id 100
#    When The user with id 1 wants to delete the post with id 100
#    Then The system should throw an exception
#    And The system should return 400 repsonse code to the user
#
#  Scenario: Deleting multiple posts
#    Given There are 4 posts added to the user with id 1
#    When The user with id 1 wants to delete all posts in the database
#    Then User with id 1 should have 0 posts in database
#    And The system should return 400 response code
#
#  Scenario: Updating existing post
#    Given There is a post with id 1 added to the database and to user with id 1
#    When The user with id 1 wants to update the post with id 1 with name changed
#    Then The user with id 1 should have changed post to content changed
#    And The system response to the user should be 200 code
#
#  Scenario: Updating non existing post
#    Given There is not post in database with user id 1 and post id 100
#    When The user with id 1 wants to update the post with id 100
#    Then The system should throw an exception
#    And The system should return 404 response code
#
#  Scenario: Updating post with invalid content length
#    Given There is a post added to the user with id 1
#    When The user with id 1 wants to update the post's with id 1 content to 256 characters content
#    Then The system should throw an exception
#    And The system should return 400 response code to the user




