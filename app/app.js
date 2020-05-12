var express = require('express');
const MongoClient = require('mongodb').MongoClient;

const db_url = 'mongodb://localhost:27017';
const dbName = 'recipe-se';
const collection_name='recipes';


var app = express();
app.use(express.json()) // for parsing application/json

app.get('/', function (req, res) {
    res.send('Hello World!');
});

app.get('/recipes', async (req, res) => {

    try {
        console.log(req.body);
        res.type('application/json');

        res.status(200);
        res.json(
            await query(req.body.ingredients.join(" "))
        );
        res.send();
    }
    catch (e) {
        console.error("Error : " + e);
        console.log("Try to open mongo shell and run db.recipes.createIndex({ingredients_idx:\"text\"})\n");
        res.status(500).send("500 - Internal Error");
    }

});


//THIS MUST BE THE LAST ROUTE
app.use(function(req, res, next) {
    res.status(404).send('Sorry cant find that!');
});

app.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});


//Utilities section

async function query(ingredients){

  const client = new MongoClient(db_url);
  let recipes = undefined;
  console.log(ingredients);

  await client.connect();
  //console.log("Connected correctly to server recipes");

  const db = client.db(dbName);

  // Get the collection
  const col = db.collection(collection_name);

  // Get first two documents that match the query

  recipes = await col.find({
    $text:
        {
          $search: ingredients,
          $caseSensitive: false,
        }
    }).project({ score: { $meta: "textScore" } })
      .sort({score:{$meta:"textScore"}})
      .toArray();

  // Close connection
  await client.close();
  console.log(recipes.length);
  return recipes;
}