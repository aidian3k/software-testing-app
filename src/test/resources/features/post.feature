Feature: Management of user's posts
  Background: There is added user with id 1, name Adrian, email adrian@wp.pl

  Scenario Outline: As a user I want to create new posts
    Given There is added user with id 1 and name Adrian
    When The user with id 1 tries to add new posts with valid content <content>
    Then The system should correctly create new post in database
    And The system should return the 201 response code to the user

    Examples:
    | content |
    | some-content |

  Scenario: Creating new post with too long content
    Given There is added user with id 1 and name Adrian
    When The user with id 1 tries to add new post with content which has length 256
    Then The system should throw an exception
    And The system should not add the post to database
    And The system should return 400 response code to the user

  Scenario: Get all posts that user have
    Given The user with id 1 has two posts added
    When The user with id 1 wants to see all posts data
    Then The system should return the list of posts with size 1
    And The system should return 200 response code

  Scenario: Get post by id
    Given There exists a post in database with id 1 with content some-content
    When The user with id 1 wants to find the post with id 1
    Then The system should return Post class with content some-content
    And The system response should be 200

  Scenario: Get post with non existing id
    Given In database there is not a post with id 100
    When The user with id 1wants to get the data of post with id 100
    Then The system should throw an exception
    And The system should return 200 response code to the user

  Scenario: Delete post with non-existing id
    Given In datbase there is not a post with id 100
    When The user with id 1 wants to delete the post with id 100
    Then The system should throw an exception
    And The system should return 400 repsonse code to the user

  Scenario: Deleting multiple posts
    Given There are 4 posts added to the user with id 1
    When The user with id 1 wants to delete all posts in the database
    Then User with id 1 should have 0 posts in database
    And The system should return 400 response code

  Scenario: Updating existing post
    Given There is a post with id 1 added to the database and to user with id 1
    When The user with id 1 wants to update the post with id 1 with name changed
    Then The user with id 1 should have changed post to content changed
    And The system response to the user should be 200 code

  Scenario: Updating non existing post
    Given There is not post in database with user id 1 and post id 100
    When The user with id 1 wants to update the post with id 100
    Then The system should throw an exception
    And The system should return 404 response code

  Scenario: Updating post with invalid content length
    Given There is a post added to the user with id 1
    When The user with id 1 wants to update the post's with id 1 content to 256 characters content
    Then The system should throw an exception
    And The system should return 400 response code to the user




