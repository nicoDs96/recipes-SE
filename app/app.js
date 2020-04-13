/*
* @author: NicoDs96
* @description: this code is a simple express API demo.
* */
var express = require('express');
var app = express();

app.get('/', function (req, res) {
  res.send('Hello World!');
});


//THIS MUST BE THE LAST ROUTE
app.use(function(req, res, next) {
  res.status(404).send('Sorry cant find that!');
});

app.listen(3000, function () {
  console.log('Example app listening on port 3000!');
});


