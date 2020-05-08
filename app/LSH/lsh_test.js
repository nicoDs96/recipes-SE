
const { Shingler, MinHasher, SignatureMatrix, Bucket, BucketCollection, Index, XXHash64} = require("./lsh");

const K =5;
const N = 100;
const SEED = 1000;
const T =0.8;
const BANDS_ROW = 5;
const BANDS_NR = Math.trunc(N/BANDS_ROW);


console.log("-----------------------------------------------------\nINDEX TEST\n\n");
let doc3 =  "burro, farina, uova, latte intero, lievito in polvere";
let doc4 =  "riso, frutti di mare";


let index = new Index(K, N, SEED);

(async function(){
    let candidates = await index.searchSimilar(doc4);
    console.log("Similar Items:");
    console.log(candidates);
    console.log("\n\n");

})();

/*
console.log("-------------------------------------------------------------------\nLow Level Test\n\n");
let doc1= "Mozzarella di bufala, spaghetti, pomodoro, olio, basilico";
let doc2 = "pollo";
let s = new Shingler(K, SEED);
let shingling1 =  s.get_shingling(doc1, true);
let shingling2 = s.get_shingling(doc2, true);
//console.log(shingling1);
let m = new MinHasher(N,SEED);
let sig_mat = m.get_signature_matrix([shingling1,shingling2]);
console.log("Sig_mat");
console.log(sig_mat.matrix);

//console.log(sig_mat.get_bands()[0][0]); //1st band of 1st set


let bands = sig_mat.get_bands();
//console.log("BANDS");
//console.log(bands);
//console.log("\n\n");

let band_buckets = [] //array of BucketCollection, one collection per each band
const DOC_NR=bands.length;

for(let i=0;i< BANDS_NR; i++){

    band_buckets.push(
        new BucketCollection(BANDS_NR)
    );

    for(let doc_id=0; doc_id<DOC_NR; doc_id++){
        band_buckets[i].add_bucket(bands[doc_id][i],doc_id);
    }


}

//console.log("BAND BUCKETS");
//console.log(band_buckets)
//console.log("\n\n");


let query = "spaghetti, pomodoro, olio";
let shingled_query = s.get_shingling(query, true);
let query_signature = m.get_signature_matrix([shingled_query]);
let query_bands = query_signature.get_bands()[0];
//console.log("QUERY BANDS");
//console.log(query_bands)
//console.log("\n\n");

for(let band_idx=0;band_idx< query_bands.length;band_idx++){

    let band = query_bands[band_idx];
    let band_value = parseInt(XXHash64.hash64(Buffer.from(band),1000,"hex"),16);
    //console.debug(band_value);
    //console.debug( band_buckets[band_idx] );
    let collection = band_buckets[band_idx];
    let collision_bucket = collection.search_bucket(band_value);
    if (collision_bucket!=undefined){
        console.log(`Candidates Docs for band ${band_idx} `);
        console.log(collision_bucket.documents);
        console.log("\n\n");
    }

}


//let candidates =  index.get_similar_document(query);
//console.log("Similar Items:")
//console.log(candidates);
//console.log("\n\n");


 */