/*
* @author: NicoDs96
* @description: this code is a scraper for Giallo Zafferano (giallozafferano.it). In the future it will be adapted to
*               other sites.
* */

const rp = require('request-promise');
const $ = require('cheerio');
const url = 'https://www.giallozafferano.it/ricette-cat/';

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

            return new Promise( (resolve, reject) => {resolve(ret_json)});

        })
        .catch((err)=>{
            console.error(err);
        });

}

rp(url)
    .then( async (html) => {
        /*
        - TODO: save html file for further analysis (backup ...)
        - TODO: save log for recovery on errors (like url you failed to scrape st we can recover data later)
        - TODO: build a logger and redirect it to a file
         */

        // Get all the <a> tag childern of div with class .gz-description
        let result = $(".gz-description > a", html);

        console.log(result.length);
        console.log(result[0].attribs.href);
        console.log(result[0].attribs.title);

        console.log('Tryng to parse ingredients:\n');

        //console.log(await get_ingredients(result[0].attribs.href));


         let data =  await get_ingredients(result[0].attribs.href).then( (jsn) =>{
            console.log(jsn);
            jsn['href'] = result[0].attribs.href;
            jsn['title'] = result[0].attribs.title;
            console.log("DIOCANE");

            return jsn;
         });
         console.log(data);







        //console.log(data);

        // for each child get the page, store the information and loop till the end of the recipes for this page
        /*
        const MongoClient = require('mongodb').MongoClient;
        const assert = require('assert');

        // Connection URL
        const url = 'mongodb://localhost:27017';

        // Database Name
        const dbName = 'myproject';

        // Create a new MongoClient
        const client = new MongoClient(url);

        // Use connect method to connect to the Server
        client.connect(function(err) {
          assert.equal(null, err);
          console.log("Connected successfully to server");

          const db = client.db(dbName);

          client.close();
        });
        * */


    })
    .catch( (err) => {
        //handle error
        console.error(err);
    });