var express = require('express');
const MongoClient = require('mongodb').MongoClient;

const db_url = 'mongodb://localhost:27017';
const dbName = 'recipe-se';
const collection_name = 'recipes';
const collection_users = 'users';


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

app.post('/recipes', async (req, res) => {

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
app.post('/recipeSe',async (req,res)=>{

    try {
        user = await login(req.body)

        if (user != null) {
            
            console.log(user);
            res.status(200).send(JSON.stringify(user))


        } else {
            res.status(400).send();

        }


    }
    catch (e) {
        console.error("Error : " + e);
        res.status(500).send("500 - Internal Error");
    }
/*
    const query= {
        email: req.body.email,
        password:req.body.password
    }

    collection.findOne(query,(err,result)=>{

        if (result!=null){

            const objToSend = {
                name: result.name,
                email:result.email,


            }
            res.status(200).send(JSON.stringify(objToSend))

        }else{
            res.status(400).send()
        }
    })
    */
})
app.post('/signup', async (req, res) => {


    try {
        bool = await sign(req.body)

        if (bool) {
            res.status(200).send();


        } else {
            res.status(400).send();

        }


    }
    catch (e) {
        console.error("Error : " + e);
        console.log("Try to open mongo shell and run db.recipes.createIndex({ingredients_idx:\"text\"})\n");
        res.status(500).send("500 - Internal Error");
    }

});

//THIS MUST BE THE LAST ROUTE
app.use(function (req, res, next) {
    res.status(404).send('Sorry cant find that!');
});

app.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});


//Utilities section

async function query(ingredients) {

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
        .sort({ score: { $meta: "textScore" } })
        .toArray();

    // Close connection
    await client.close();
    console.log(recipes.length);
    return recipes;
}

async function sign(credentials) {

    const client = new MongoClient(db_url);
    console.log(credentials);

    await client.connect();
    //console.log("Connected correctly to server recipes");

    const db = client.db(dbName);

    // Get the collection
    const col = db.collection(collection_users);

    const newUser = {
        name: credentials.name,
        email: credentials.email,
        password: credentials.password
    }
    const query = { email: newUser.email }
    console.log(query);

    user = await col.findOne(query);
    if (user == null) {
        console.log("null");
        await col.insertOne(newUser);
        await client.close();
        return true;
    } else {
        console.log("Utente già presente")
        await client.close();
        return false;
    }

}
async function login(credentials) {

    const client = new MongoClient(db_url);
    console.log(credentials);

    await client.connect();
    //console.log("Connected correctly to server recipes");

    const db = client.db(dbName);

    // Get the collection
    const col = db.collection(collection_users);

    
    const query= {
        email: credentials.email,
        password:credentials.password
    }
    

    user = await col.findOne(query);
    
    if(user != null){
         objToSend = {
            name: user.name,
            email: user.email,
        }
        await client.close();
        return objToSend;

    }else{
        await client.close();
        return null
    }
  
}
