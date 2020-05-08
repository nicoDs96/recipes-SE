const MongoClient = require('mongodb').MongoClient;
const assert = require('assert');
const cliProgress = require('cli-progress');
const { Shingler, MinHasher, SignatureMatrix, XXHash64} = require("./lsh");

const K =5;
const N = 100;
const SEED = 1000;
const T =0.8;
const BANDS_ROW = 5;
const BANDS_NR = Math.trunc(N/BANDS_ROW);

var bar1 = new cliProgress.SingleBar({stream: process.stdout}, cliProgress.Presets.legacy);
// start the progress bar with a total value of 200 and start value of 0
bar1.start(5027, 0);
bar1.render();

const db_url = 'mongodb://localhost:27017';
const dbName = 'recipe-se';
const collection_name='recipes';

const shingler = new Shingler(K, SEED);
const minHasher = new MinHasher(N,SEED);



async function get_all_recipes() {
    console.log("Initializing...");

    const client = new MongoClient(db_url);
    let recipes= undefined;
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

get_all_recipes().then( async recipes =>{

        // update the current value in your application..
        bar1.increment(1);
        let init_time = new Date().getTime();

        for(let i=0; i< recipes.length; i++){

            let ingredients = "";
            let recipeId = recipes[i]._id;
            let recipeShinglingSet;
            let recipeSignatureBands;
            let band_value;
            let bucket;

            //console.log(i);
            let recipe_time = new Date().getTime();

            recipes[i].ingredients.forEach( object =>{
                ingredients = ingredients + object.ingredient.toString().toLowerCase()+ " ," ;

            });
            ingredients = ingredients.slice(0,ingredients.length-2);
            recipeShinglingSet = shingler.get_shingling(ingredients, true);
            recipeSignatureBands = minHasher.get_signature_matrix([recipeShinglingSet]).get_bands()[0];

            for(let j=0; j<BANDS_NR; j++) {
                band_value = parseInt(XXHash64.hash64(Buffer.from(recipeSignatureBands[j]), SEED, "hex"), 16);

                let bucket = {"band": j, "value": band_value, "recipeId": recipeId};
                //console.log(bucket);
                await writeBucket(bucket);

            }
            //console.log( `recipe ${i} comupation time: ${(  new Date().getTime() - recipe_time)/1000} sec.`);


        }


        console.log( `Total comupation time: ${(new Date().getTime() - init_time )/1000} sec.`);

});


async function writeBucket(bucket) {
    try {
        const client = new MongoClient(db_url);
        await client.connect();
        //console.log("Connected correctly to server");
        const db = client.db(dbName);
        // Get the findAndModify collection
        const col = db.collection('Buckets');
        let r;

        // Modify and return the modified document
        r = await col.findOneAndUpdate({band:bucket.band, value:bucket.value}, {$addToSet: {recipes_ids: bucket.recipeId}}, {
            returnOriginal: false,
            upsert: true
        });
        //assert.equal(1, r.value.b);
        // Close connection
        client.close();

    } catch (err) {
        console.log(err.stack);
    }

}