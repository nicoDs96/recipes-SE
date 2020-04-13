/*
* @author: NicoDs96
* @description: this code is a scraper for Giallo Zafferano (giallozafferano.it). In the future it will be adapted to
*               other sites.
* */

const rp = require('request-promise');
const $ = require('cheerio');
const url = 'https://www.giallozafferano.it/ricette-cat/';

const get_ingredients =  (url) => {
    rp(url)
        .then( (html) =>{
            //get data

            let main_container = $('.gz-ingredients',html);
            let ingredients = $('.gz-list-ingredients', main_container );
            let calories = $('.gz-text-calories-total', main_container);

            console.debug( //output: 263
                calories
                 .children()
                 .text()
            );
            console.debug( //output: Ingredienti per 12 pancakes
                $('.gz-title-ingredients', ingredients).text()
            );
            console.debug();

            let keys = []; //ingredients
            let values = []; //quantity

            $('.gz-ingredient a', ingredients)
                .each( (index, element)=>{
                     keys.push( $(element).text() );
                });

            $('.gz-ingredient > *:not(a)', ingredients)
                .each( (index, element)=>{
                    values.push( $(element)
                                    .text()
                                    .replace(/\t+/g, " ")
                                    .replace(/\n/g, "")
                    );
                });
            console.debug(`keys:\t${keys}\nvalues:\t${values}`);
            if(values.length != keys.length){
                console.warn(`Keys are ${values.length} while Values are ${keys.length}`);
            }
            // return them st we can store in a document on MongoDB
            //return [calories,top-title,ingredients]

        })
        .catch((err)=>{
            console.error(err);
        });

}

rp(url)
    .then( (html) => {
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
        get_ingredients(result[0].attribs.href);

        // for each child get the page, store the information and loop till the end of the recipes for this page


    })
    .catch( (err) => {
        //handle error
        console.error(err);
    });