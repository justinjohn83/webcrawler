var express = require('express');
var app = express();
 
app.get('/', function(req, res) {
   res.sendfile('./index.html');
});

app.get("/js/*",function(req,res){
	  res.sendfile(__dirname + req.path);
	});
 
app.listen(3000);


console.log('Server running at http://127.0.0.1:3000/');
