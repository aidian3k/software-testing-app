Feature: User Management

  Scenario: Getting specific user information by id
    Given There exists in system the user with id 1, name "Adrian" and email "adrian@wp.pl"
    When User tries to get user with id 1
    Then The system should return the user with name "Adrian" and email "adrian@wp.pl"
    And The system should return 200 response

  Scenario Outline: Getting all saved users
    Given In system there are two added users with names <firstName> and <secondName>
    When The user tries to find all users data
    Then The system should return collection of saved users with names <firstName> and <secondName>
    And The system should return 200 response

    Examples:
      | firstName | secondName |
      | Adrian    | Cezary     |

  Scenario: Getting the user by email
    Given In system exists a user with email adrian@wp.pl
    When The user tries to find the user with email adrian@wp.pl
    Then The system should return the user with email adrian@wp.pl
    And The system should return 200 response

  Scenario Outline: As a user I want to create new user
    Given The user provides valid user data
    When The user tries to add new user with name <name>, surname <surname>, email <email> and password <password>
    Then The system should add new user with assigned id 3

    Examples:
    | name  | surname | email       |   password    |
    | Jakub | Orzel   | orzel@wp.pl |   jakub       |

  Scenario: The user provides wrong email when trying to create user
    Given The system is ready to add new user
    When The user tries to add new user with wrong email
    Then The system should throw an exception
    And The system should return 400 code response
    And The system should not add the user to database

  Scenario: Trying to create user with too long name
    Given The system is ready to add new user
    Then The user tries to add the user with the username which have length of 266 characters
    Then The system should throw an exception
    And The system should return 400 code response
    And The system should not add the user to database

  Scenario: Updating an existing user
    Given There is added user with id 3
    When User tries to update the user with id 3 with name adi
    Then The system updates the username with id 3 to adi
    And The system returns the 200 code response

  Scenario: Deleting non existing user
    Given The system database does not have user with id 100
    When The user tries to delete a user with the invalid id 100
    Then The system should return an error indicating the user was not found
    And The system should throw an exception
    And The system should return 400 code response

  Scenario: Deleting existing user by id
    Given There is added user with id 1
    When The user tries to delete the user with id 1
    Then The system correctly deletes the user with id 1 from database
    And The system returns 200 response code to the user