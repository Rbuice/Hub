##### Author: Instructor team SE, ASU Polytechnic, CIDSE, SE
* Version: September 2020


##### Purpose
This program shows a very simple client server implementation. The server
has 3 services, echo, add, addmany. Basic error handling on the server side
is implemented. Client does not have error handling and only has hard coded
calls to the server.

* Please run `gradle Socket` and `gradle Client` together.
* Program runs on localhost
* Port is hard coded

## Protocol: ##

### Echo: ###

Request: 

    {
        "type" : "echo", -- type of request
        "data" : <String>  -- String to be echoed 
    }

General response:

    {
        "type" : "echo", -- echoes the initial response
        "ok" : <bool>, -- true or false depending
        "message" : <String>,  -- error message if ok false
        "echo" : <String>  -- Echoed String if ok true
    }

Success response:

    {
        "type" : "echo",
        "ok" : true,
        "echo" : <String> -- the echoed string
    }

### Add: ### 
Request:

    {
        "type" : "add",
        "num1" : <string>, -- first number -- string needs to be an int number
        "num1" : <string> -- second number -- string needs to be an int number
    }

General response

    {
        "type" : "add", -- echoes the initial request
        "ok" : <bool>, -- true of false
        "message" : <String>,  -- error message if ok false
        "result" : <int>  -- result if ok true
    }

Success response:

    {
        "type" : "add",
        "ok" : true,
        "result" : <int> -- the result of add
    }

### AddMany: ###
Another request, this one does not just get two numbers but gets an array of numbers.

Request:

    {
    "type" : "addmany",
    "nums" : [<string>], -- json array of ints but given as strings, e.g. ["1", "2"]
    }

General response

    {
        "type" : "addmany", -- echoes the initial request
        "ok" : <bool>, -- true of false
        "message" : <String>,  -- error message if ok false
        "result" : <int>  -- result if ok true
    }

Success response:

    {
        "type" : "addmany",
        "ok" : true,
        "result" : <int> -- the result of add
    }

Error response:
If some numbers are not ints

    {
        "type" : "addmany",
        "ok" : false,
        "message" : "Values in array need to be ints"
    }

### Concatenate: ###
Another request, this one gets 2 strings and concatenates them, each string needs to be at least 5 characters long. 

Request:

    {
        "type" : "concat",
        "s1" : <string>,   
        "s2" : <string>
    }

General response

    {
        "type" : "concat", -- echoes the initial request
        "ok" : <bool>, -- true of false
        "message" : <String>,  -- error message if ok false
        "result" : <string>  -- result if ok true - then s1s2 will be concatenated
    }

Success response:

    {
        "type" : "concat",
        "ok" : true,
        "result" : <string>
    }

Special error response:

    {
        "type" : "concat",
        "ok" : false,
        "message" : "too short" // return that a string is too short
    }

### SaveNames: ###
If a "name" field is given then the server will add the name to a list of known names.
If the name already exists in the list there will be an error message. The server returns all known names in the response as a list and the name that was given in the request in the result field. 
If no name is provided, the server will return the list of known names only (there will not be a reult field. In case the list is empty an error response will be returned. 

Request:

    {
        "type" : "names",
        "name" : <string> // optional
    }

General response

    {
        "type" : "names", -- echoes the initial request
        "ok" : <bool> -- true of false
        "message" : <String>  -- error message if ok false
        "result" : <string>  -- result if ok true - returns name that was given if one was given
        "allNames" : [<string>] -- string array of all names saved on server
    }

Success response:

    {
        "type" : "names",
        "ok" : true,
        "result" : <string>, // the name that was given if one was given in request
        "allNames" : [<string>]
    }


Special error response:
This happens if the name is already in the list on the server

    {
        "type" : "names",
        "ok" : false,
        "message" : "already used",
        "allNames" : [<string>] // list of all names already on server
    }

This happens if the list of names is empty

    {
        "type" : "names",
        "ok" : false,
        "message" : "list empty"
    }

### General error responses: ###
These are used for all requests.

Error response: When a required field "key" is not in request

    {
        "ok" : false
        "message" : "Field <key> does not exist in request" 
    }

Error response: When a required field "key" is not of correct "type"

    {
        "ok" : false
        "message" : "Field <key> needs to be of type: <type>"
    }

Error response: When the "type" is not supported, so an unsupported request

    {
        "ok" : false
        "message" : "Type <type> is not supported."
    }

    