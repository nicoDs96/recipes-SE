const MongoClient = require('mongodb').MongoClient;
const cliProgress = require('cli-progress');
assert = require('assert');

const bar1 = new cliProgress.SingleBar({stream: process.stdout}, cliProgress.Presets.shades_grey);
bar1.render();


const db_url = 'mongodb://localhost:27017';
const dbName = 'recipe-se';
const collection_name='recipes';

(async () =>{

    await refactor(await get_all_recipes() );

    await refine();

    await createIndex();

    bar1.stop();

})();

/*
* sample query:
*
*

db.recipes.find(
    {$text: {$search:["farina00", "burro" "uova", "zucchero"]} },
    { score: { $meta: "textScore" } }
).sort({ score: { $meta: "textScore" } })
.pretty()

*/

async function get_all_recipes() {
    console.log("Initializing...");

    const client = new MongoClient(db_url);
    let recipes = undefined;

    try {
        await client.connect();
        //console.log("Connected correctly to server recipes");

        const db = client.db(dbName);

        // Get the collection
        const col = db.collection(collection_name);

        // Get first two documents that match the query
        recipes = await col.find().toArray();

    } catch (err) {
        console.log(err.stack);
    }

    // Close connection
    await client.close();
    return recipes;
}


async function refactor(recipes) {
    bar1.start(recipes.length, 0);

    try {
        var client = new MongoClient(db_url);
        await client.connect();
        //console.log("Connected correctly to server");
        var db = client.db(dbName);
        // Get the findAndModify collection
        var col = db.collection(collection_name);
    } catch (err) {
        console.log(err.stack);
    }
    var barcount=0;
    await recipes.forEach(async (recipe) => {

        bar1.update(barcount++);
        bar1.updateETA();

        let ingredients_idx = [];

        recipe.ingredients.forEach(object => {
            ingredients_idx.push(object.ingredient.toString().toLowerCase());
        });

        try{
            let r;

            //console.log(`_id: ${recipe._id}, ingredients_idx: ${ingredients_idx}`);
            // Modify and return the modified document
            r = await col.updateOne({_id: recipe._id.toString()}, {$set: {ingredients_idx: ingredients_idx}}, {
                returnOriginal: false,
                upsert: false
            });
            //assert.equal(1, r.matchedCount);
            //assert.equal(1, r.modifiedCount);


        } catch (err) {
            console.log(err.stack);
        }

    });

    // Close connection
    await client.close();
}

async function refine() {

    const client = new MongoClient(db_url);
    let recipes = undefined;
    try {
        await client.connect();
        //console.log("Connected correctly to server recipes");
        const db = client.db(dbName);
        // Get the collection
        const col = db.collection(collection_name);
        // Get first two documents that match the query
        recipes = await col.find({ingredients_idx: {"$exists": false}}).toArray();

        for(let i=0; i<recipes.length;i++){

            let ingredients_idx = [];

            recipes[i].ingredients.forEach(object => {
                ingredients_idx.push(object.ingredient.toString().toLowerCase());
            });


            let r;
            r = await col.updateOne({_id: recipes[i]._id}, {$set: {ingredients_idx: ingredients_idx}}, {
                returnOriginal: false,
                upsert: false
            });
            assert.equal(1, r.matchedCount);
            assert.equal(1, r.modifiedCount);
        }

    } catch (err) {
        console.log(err.stack);
    }
    await client.close();

}

async  function createIndex() {
    const client = new MongoClient(db_url);

    try {
        await client.connect();
        //console.log("Connected correctly to server recipes");
        const db = client.db(dbName);
        // Get the collection
        const col = db.collection(collection_name);

        await col.createIndex({ingredients_idx:"text"});

    }catch (err) {
        console.log(err.stack);
    }

    await client.close();

}