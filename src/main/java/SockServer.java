import org.json.JSONArray;
import org.json.JSONObject;

import java.net.*;
import java.util.Arrays;
import java.io.*;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 *
 */
public class SockServer {
  static Socket sock;
  static DataOutputStream os;
  static ObjectInputStream in;
  static String[] names = new String[100];
  static int namecount = 0;
  static int port = 8888;

  public static void main (String args[]) {

    if (args.length != 1) {
      System.out.println("Expected arguments: <port(int)>");
      System.exit(1);
    }

    try {
      port = Integer.parseInt(args[0]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port|sleepDelay] must be an integer");
      System.exit(2);
    }

    try {
      //open socket
      ServerSocket serv = new ServerSocket(8888); // create server socket on port 8888
      System.out.println("Server ready for connections");

      /**
       * Simple loop accepting one client and calling handling one request.
       *
       */

      while (true){
        System.out.println("Server waiting for a connection");
        sock = serv.accept(); // blocking wait

        // setup the object reading channel
        in = new ObjectInputStream(sock.getInputStream());

        // get output channel
        OutputStream out = sock.getOutputStream();

        // create an object output writer (Java only)
        os = new DataOutputStream(out);

        String s = (String) in.readObject();
        JSONObject req = new JSONObject(s);

        JSONObject res = testField(req, "type");
        if (!res.getBoolean("ok")) {
          overandout(res);
          continue;
        }

        // check which request it is (could also be a switch statement)
        if (req.getString("type").equals("echo")) {
          res = echo(req);
        } else if (req.getString("type").equals("add")) {
          res = add(req);
        } else if (req.getString("type").equals("addmany")) {
          res = addmany(req);
        } 
        else if (req.getString("type").equals("concat")) {
          res = concatenate(req);
        }
        else if (req.getString("type").equals("names")) {
          res = names(req);
        }
        else {
          res = wrongType(req);
        }
        overandout(res);
      }
    } catch(Exception e) {e.printStackTrace();}
  }


  /**
   * Checks if a specific field exists
   *
   */
  static JSONObject testField(JSONObject req, String key){
    JSONObject res = new JSONObject();

    // field does not exist
    if (!req.has(key)){
      res.put("ok", false);
      res.put("message", "Field " + key + " does not exist in request");
      return res;
    }
    return res.put("ok", true);
  }

  // handles the simple echo request
  static JSONObject echo(JSONObject req){
    JSONObject res = testField(req, "data");
    System.out.println(res);
    if (res.getBoolean("ok")) {
      if (!req.get("data").getClass().getName().equals("java.lang.String")){
        res.put("ok", false);
        res.put("message", "Field data needs to be of type: String");
        return res;
      }

      res.put("type", "echo");
      res.put("echo", "Here is your echo: " + req.getString("data"));
    }
    return res;
  }

  // handles the simple add request with two numbers
  static JSONObject add(JSONObject req){
    JSONObject res1 = testField(req, "num1");
    if (!res1.getBoolean("ok")) {
      return res1;
    }

    JSONObject res2 = testField(req, "num2");
    if (!res2.getBoolean("ok")) {
      return res2;
    }

    JSONObject res = new JSONObject();
    res.put("ok", true);
    res.put("type", "add");
    try {
    res.put("result", req.getInt("num1") + req.getInt("num2"));
    } catch (org.json.JSONException e){
      res.put("ok", false);
      res.put("message", "Field data needs to be of type: int");
    }
    return res;
  }

  // implement me in assignment 3
  static JSONObject concatenate(JSONObject req) {
      JSONObject res1 = testField(req, "s1");
      if (!res1.getBoolean("ok")) {
        return res1;
      }

      JSONObject res2 = testField(req, "s2");
      if (!res2.getBoolean("ok")) {
        return res2;
      }
      JSONObject res = new JSONObject();
      try {
      if(req.getString("s1").length() <= 0) {
          res.put("ok", false);
          res.put("type", "concat"); 
          res.put("message", "Provided string was too short");
      }
      else if(req.getString("s2").length() <= 0) {
          res.put("ok", false);
          res.put("type", "concat"); 
          res.put("message", "Provided string was too short"); 
      }
      else {
          res.put("ok", true);
          res.put("type", "concat"); 
          res.put("result", req.getString("s1") + " " + req.getString("s2"));
      }
      } catch (org.json.JSONException e) {
          res.put("ok", false);
          res.put("type", "concat"); 
          res.put("message", "Field data needs to be of type: string");
      }
      return res;
  }

  // implement me in assignment 3
  static JSONObject names(JSONObject req) {
      JSONObject res = testField(req, "name");
      if (!res.getBoolean("ok")) {
        return res;
      }
      res.put("type", "names");
      res.put("ok", true);
      boolean contains = false;
   
      try {
      contains = Arrays.stream(names).anyMatch(req.getString("name"):: equals);
      }catch (org.json.JSONException e) {
          res.put("ok", false);
          res.put("type", "names"); 
          res.put("message", "error with contains");
      }
      if(contains == true) {
          res.put("ok", false);
          res.put("type", "names");
          res.put("message", "already used");
          res.put("allNames", Arrays.toString(names));
      }
      else if (contains == false){
          if(!req.getString("name").equals("")) {
              names[namecount] = req.getString("name");
              namecount++;
              res.put("ok", true);
              res.put("type", "names");
              res.put("result", req.getString("name"));
              res.put("allNames", Arrays.toString(names));
          }
          else {
              res.put("ok", true);
              res.put("type", "names");
              res.put("allNames", Arrays.toString(names));
          }
      }
      else if (namecount == 0) {
          res.put("ok", false);
          res.put("type", "names");
          res.put("message", "list empty");
      }
      else{
          res.put("ok", true);
          res.put("type", "names");
          res.put("allNames", Arrays.toString(names));
      }
      return res;
  }

  // handles the simple addmany request
  static JSONObject addmany(JSONObject req){
    JSONObject res = testField(req, "nums");
    if (!res.getBoolean("ok")) {
      return res;
    }

    int result = 0;
    JSONArray array = req.getJSONArray("nums");
    for (int i = 0; i < array.length(); i ++){
      try{
        result += array.getInt(i);
      } catch (org.json.JSONException e){
        res.put("ok", false);
        res.put("message", "Values in array need to be ints");
        return res;
      }
    }

    res.put("ok", true);
    res.put("type", "addmany");
    res.put("result", result);
    return res;
  }

  // creates the error message for wrong type
  static JSONObject wrongType(JSONObject req){
    JSONObject res = new JSONObject();
    res.put("ok", false);
    res.put("message", "Type " + req.getString("type") + " is not supported.");
    return res;
  }

  // sends the response and closes the connection between client and server.
  static void overandout(JSONObject res) {
    try {
      os.writeUTF(res.toString());
      // make sure it wrote and doesn't get cached in a buffer
      os.flush();

      os.close();
      in.close();
      sock.close();
    } catch(Exception e) {e.printStackTrace();}

  }
}