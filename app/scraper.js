/*
* @author: NicoDs96
* @description: this code is a scraper for Giallo Zafferano (giallozafferano.it). In the future it will be adapted to
*               other sites.
* */

const rp = require('request-promise');
const $ = require('cheerio');
const fs = require('fs');
const MongoClient = require('mongodb').MongoClient;
const assert = require('assert');
//const cliProgress = require('cli-progress');

const url = 'https://www.giallozafferano.it/ricette-cat/'; //https://www.giallozafferano.it/ricette-cat/page336/

const get_ingredients = (url) => {
    return rp(url)
        .then( (html) =>{
            //get data

            let main_container = $('.gz-ingredients',html);
            let ingredients = $('.gz-list-ingredients', main_container );
            let calories = $('.gz-text-calories-total', main_container);
            let ret_json = new Object();

            ret_json['calories'] = calories.children().text(); //output: 263

            //output: Ingredienti per 12 pancakes
            ret_json['portion_nr'] = $('.gz-title-ingredients', ingredients).text();

            let keys = []; //ingredients
            let values = []; //quantity

            //get ingredient names
            $('.gz-ingredient a', ingredients)
                .each( (index, element)=>{
                     keys.push( $(element).text() );
                });

            //get ingredient quantities
            $('.gz-ingredient > *:not(a)', ingredients)
                .each( (index, element)=>{
                    values.push( $(element)
                                    .text()
                                    .replace(/\t+/g, " ")
                                    .replace(/\n/g, "")
                    );
                });

            if(values.length != keys.length){
                console.warn(`Keys are ${values.length} while Values are ${keys.length}`);
                ret_json['ingredients'] = undefined;
                ret_json['keys'] = keys;
                ret_json['values'] = values;
                //ingredient will be manually defined later with a review phase on the DB consistency
            }
            else{
                ret_json['ingredients']= []
                for(let i=0; i<keys.length; i++){
                    let o = new Object();
                    o.ingredient = keys[i]
                    o.quantity = values[i]
                    ret_json['ingredients'].push( o );
                }
            }
            //console.log(ret_json);

            //return new Promise( (resolve, reject) => {resolve(ret_json)});
            return ret_json;

        })
        .catch((err)=>{
            console.error(err);
        });

};
const write_doc = (doc, db_url='mongodb://localhost:27017', dbName = 'recipe-se', collection_name='recipes')=>{
    const client = new MongoClient(db_url, useUnifiedTopology=true);

    // Use connect method to connect to the Server
    client.connect((err) => {
        assert.equal(null, err);
        //console.log("Connected successfully to server");
        const db = client.db(dbName);
        db.collection(collection_name).insertOne(doc, (err, r) => {
            assert.equal(null, err);
            assert.equal(1, r.insertedCount);
        });
        client.close();
    });
}

const scrape_page = async (full_url) =>{
    rp(full_url)
        .then( async (html) => {
            /*
            - TODO: save html file for further analysis (backup ...)
            - TODO: save log for recovery on errors (like url you failed to scrape st we can recover data later)
            - TODO: build a logger and redirect it to a file
             */

            // Get all the <a> tag childern of div with class .gz-description
            let result = $(".gz-description > a", html);
            //Download all recipes in the page
            for(let j=0; j<result.length; j++){
                let data =  await get_ingredients(result[j].attribs.href).then( (jsn) =>{
                    jsn['href'] = result[j].attribs.href;
                    jsn['title'] = result[j].attribs.title;
                    return jsn;
                });
                //console.log(await data);
                write_doc(await data);

            }
        })
        .catch( (err) => {
            //handle error
            console.error(err);
        });
}

let main_prog = async (full_url) => {
    await scrape_page(full_url);
}

let page_max = 336;
let page = 1;
let full_url = url+'page'+page+'/';

let interval =setInterval(() => {
    main_prog(full_url);
    // runs after 1 seconds
    page++;
    full_url = url+'page'+page+'/';
    if(page > page_max){
        clearInterval(interval);
    }
    console.log(full_url);
}, 1500)




