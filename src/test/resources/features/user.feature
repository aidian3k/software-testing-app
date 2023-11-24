Feature: User Management

  @checkForOkResponseSingleUser
  Scenario: Getting specific user information by id
    Given There exists in system the user with id 1, name "Adrian" and email "adrian@wp.pl"
    When User tries to get user with id 1
    Then The system should return the user with name "Adrian" and email "adrian@wp.pl"

  @checkForOkResponseUserArray
  Scenario Outline: Getting all saved users
    Given In the system there are two added users with names "<firstName>" and "<secondName>"
    When The user tries to find all users data
    Then The system should return a collection of saved users with names "<firstName>" and "<secondName>"

    Examples:
      |   firstName   |   secondName |
      |   Adrian      |   Cezary     |
      |   Dawidek     |   Kuba       |
      |   Daa         |   James      |

  @checkForOkResponseSingleUser
  Scenario Outline: : Getting the user by email
    Given In system exists a user with email "<userEmail>"
    When The user tries to find the user with email "<userEmail>"
    Then The system should return the user with email "<userEmail>"

    Examples:
      | userEmail |
      | adrian@wp.pl |
      | cezary@wp.pl  |
      | james@onet.com |

  @checkForOkResponseSingleUser
  Scenario Outline: As a user I want to create new user
    Given The user provides valid user data
    When The user tries to add new user with name "<name>", surname "<surname>", email "<email>" and password "<password>"
    Then The system should add new user with assigned name "<name>", surname "<surname>", email "<email>", password "<password>"

    Examples:
      |name  | surname | email       |   password       |
      | Jakub | Orzel   | orzel@wp.pl |   sample-password-jakub      |
      | Adrian | Nowosielski | adrian@wp.pl | sample-password-adrian    |

  @checkForBadResponseStatus
  Scenario Outline: The user provides wrong email when trying to create user
    Given The system is ready to add new user
    When The user tries to add new user with wrong "<email>"
    Then The system should throw an exception with map with key "email" and value "must be a well-formed email address"

    Examples:
      | email |
      | aws   |
      | not-an-email |

  @checkForBadResponseStatus
  Scenario Outline: Trying to create user with too long name
    Given The system is ready to add new user
    When The user tries to add the user with the username which have length of "<lengthOfUserName>" characters
    Then The system should throw an exception with map with key "name" and value containing string "size must be between 0 and 255"
    Examples:
      | lengthOfUserName |
      | 256              |
      | 300              |
      | 1024             |

  @checkForOkResponseSingleUser
  Scenario: Updating an existing user
    Given There is added user with name "adrian"
    When User tries to update the user with name adrian to name "adi"
    Then The system updates user's the username to "adi"

  Scenario Outline: Deleting non existing user
    Given The system database does not have user with id <invalidId>
    When The user tries to delete a user with the invalid id "<invalidId>"
    Then The system should return an error indicating the user was not found

    Examples:
    | invalidId |
    | 1024      |
    | 1025      |
    | -50       |


  Scenario: Deleting existing user by id
    Given There is added user
    When The user tries to delete existing the user in database
    Then The system should return OK response status code