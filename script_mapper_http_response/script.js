var response = httpGet("https://jsonplaceholder.typicode.com/todos/1").data;

var repos = JSON.parse(response);

token.setOtherClaims("fake", repos);

exports = repos;

/*************
UTILITY FUNCTIONS
*************/

function httpGet(theUrl) {
  var con = new java.net.URL(theUrl).openConnection();
  con.requestMethod = "GET";

  return asResponse(con);
}

function asResponse(con) {
  var d = read(con.inputStream);

  return { data: d, statusCode: con.responseCode };
}

function read(inputStream) {
  var inReader = new java.io.BufferedReader(
    new java.io.InputStreamReader(inputStream)
  );
  var inputLine;
  var response = new java.lang.StringBuffer();

  while ((inputLine = inReader.readLine()) !== null) {
    response.append(inputLine);
  }
  inReader.close();
  return response.toString();
}
