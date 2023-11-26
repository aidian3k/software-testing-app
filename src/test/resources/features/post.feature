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
    Given In DB there is post by user with id 1 and content "some-content"
    When The user wants to find the post by id
    Then The system should return requested post with content "some-content"
    And The system response should be 200

  Scenario: Get post with non existing id
    Given In database there is no post with id 100
    When The user wants to get the data of post with id 100
    Then The system should return 404 not found to the user

  Scenario Outline: Delete post with non-existing id
    Given In database there's no post with id <invalidId>
    When The user wants to delete the post with "<invalidId>"
    Then The system should return an error indicating the post was not found

    Examples:
    | invalidId |
    | 1000 |
    | 15 |
    | -1 |

  Scenario: Deleting multiple posts
    Given In database there are two posts
    When The user tries to delete posts with ids
      | 1 |
      | 2 |
    Then The system should return OK response code


#  Scenario: Updating existing post
#    Given There is a post in database with id
#    When The user tries to update the post with content "new content"
#    Then The user gets confirmation of update

#  Scenario: Updating non existing post
#    Given There is not a post in database
#    When The user want to update that post
#    Then The system should return an error that there is no such post

#  Scenario: Updating post with invalid content length
#    Given There is a post in database which user want to update
#    When the user tries to update it with too long content
#    Then The system should return an error that the post is too long



