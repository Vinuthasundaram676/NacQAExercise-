#Author:
#Date:

@SmokeTesting
Feature: Validating Get Nace Detail API

Scenario: (01) Validating Get Nace API
  Given Calling get nace detail api
  Then validate the response status
  Then validate the response data
