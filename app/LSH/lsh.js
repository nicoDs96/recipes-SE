var XXHash64 = require('xxhash');

const K =5;
const N = 200;
const SEED = 1000;
const T =0.8;
const BANDS_ROW = 3;
const BANDS_NR = Math.trunc(N/BANDS_ROW);

class Shingler {
    /**
     * @param {number} k size of a shingle
     * @param {number} seed used to hash shignles
     */
    constructor(k, seed) {
        if(!Number.isInteger(k)){
            throw "k is not an integer";
        }
        if(k<1){
            throw "k must be at least 1";
        }
        this.k=k;
        this.seed = seed;
    }

    /**
     * @param {string} document a string representing the document to be transformed
     * @param {boolean} hashed a flag telling if the output shingles must be hashed
     *
     * @return {Array<Integer | String>} k-shingle is an array representing k-shingle
     * of the input document. If hashed==true then @return Array<Integer>.sorted() else Array<String>
     */
    get_shingling(document, hashed=true) {
        /*
        * TRANSFORM THE DOCUMENT INTO A SET OF K SHINGLINGS.
        * IF hashed THEN RETURN HASHED SHINGLINGS
        * */
        document = document.toLowerCase();
        let shingles = new Set();

        for(let i=0; i<document.length - this.k+1; i++){
            shingles.add( document.substr(i,this.k) );
        }

        if(hashed){ // hash the shingles

            let hashed_shingles= new Set();
            shingles.forEach((key, elem, set)=>{
                hashed_shingles.add(
                    parseInt(XXHash64.hash64(Buffer.from(elem), this.seed, "hex"),16)
                );
            });

            return Array.from(hashed_shingles).sort();
        }
        return Array.from(shingles);
    }
}

class MinHasher {
    /**
     * @param {number} n size of the signature
     * @param {number} seed used to hash the signature
     */
    constructor(n, seed) {
        if(!Number.isInteger(n)){
            throw "k is not an integer";
        }
        if(n<1){
            throw "k must be at least 1. Reccomended: at least 200";
        }
        this.n = n;
        this.seed = seed;
    }

    /**
     *
     * @param {Array<>} set is a k-shingle
     * @return {Array<Integer>} signature is an array of size MinHasher.n representing the set signature.
     */
    get_set_signature(set) {
        let signature = []
        for(let i=0; i<this.n; i++){ //compute the minhash for each set
            let min_hash = Number.POSITIVE_INFINITY;
            set.forEach((element)=>{
                let hash = parseInt(XXHash64.hash64(Buffer.from(element.toString()),this.seed+i, "hex"),16);
                if (hash< min_hash){
                    min_hash = hash;
                }
            });
            signature.push(min_hash);
        }
        return signature;
    }

    /**
     * @param {Array<Integer|String>[]} set_list a list of set in k-shingling form
     *
     * @return {SignatureMatrix} signature_matrix the matrix of signatures.
     */
    get_signature_matrix(set_list){
        let signatures = []
        set_list.forEach((set)=>{
            signatures.push(this.get_set_signature(set))
        });
        return new SignatureMatrix(BANDS_ROW, BANDS_NR,signatures);
    }
}

class SignatureMatrix{

    /**
     * @param {number} row_nr number of rows of each band
     * @param {number} bands_nr number of bands of the signature matrix
     * @param {[]} signature_matrix rough computation of the signature matrix from the MinHasher get_signature_matrix()
     */
    constructor(row_nr, bands_nr, signature_matrix) {
        //row_nr*bands_nr=MinHasher.n
        this.row_nr = row_nr;
        this.bands_nr = bands_nr;
        this.signature_matrix = signature_matrix;
        // a 2-d array: bands[i] gives you all the signatures part in the i-th band. bands[i][j] gives the i-th band of
        // the j-th signature
        this.bands = [];

        this.signature_matrix.forEach((signature) =>{
            //COMPUTE THE BANDS FOR EACH SIGNATURE AND ADD IT TO THE bands ARRAY
            let set_bands = [];
            for(let i=0;i<this.bands_nr;i++){
                set_bands.push( signature.slice(i*this.row_nr, i*this.row_nr + this.row_nr).join(" ") );
            }
            this.bands.push(set_bands);
        });
    }

    get matrix(){
        return this.signature_matrix;
    }

    /**
     *
     * @return {[][]} band 2-D Array where each row represent a document while each column a band for the given set:
     * band[i][j] means the j-th band of the i-th document.
     */
    get_bands(){
        return this.bands;
    }

}


class Bucket{

    /**
     * @param {number} value hash value of the given bucket
     */
    constructor(value) {
        this.documents = [];
        //hashed signature of the i-th band for the j-th set
        // each band have multiple buckets (at most one per document)
        this.value = value;
    }

    /**
     *
     * @return {[]} list of documents that hashes to this bucket
     */
    get_documents(){
        return this.documents;
    }
    /**
     * @return {number} value hash value of the given bucket
     */
    get_value(){
        return this.value;
    }

    /**
     *
     * @param doc_id add a document to this bucket
     */
    add_document(doc_id){
        if (!this.documents.includes(doc_id)){
            this.documents.push(doc_id);
            this.documents.sort((a, b) => a - b);
        }
    }

    /**
     *
     * @return {number} size get the number of document in this bucket
     */
    get size(){
        return this.documents.length;
    }

}

class BucketCollection{

    /**
     * A collection of bucket for a given band. Each band has its own bucketCollection object instantiated.
     *
     * @param {number} band_nr number of bands in which the Signature matrix is divided
     */
    constructor(band_nr) {
        this.buckets_id = [];//array of integers to check whether a bucket already exist
        this.buckets = []
        this._band_nr = band_nr;
        this.seed = 1000;
    }


    get band_nr() {
        return this._band_nr;
    }

    /**
     *
     * @param {String} set_signature is a String representing the signature of a band
     * @param doc_id id of the document we are considering
     */
    add_bucket(set_signature, doc_id){
        let bucket_id = parseInt(XXHash64.hash64(Buffer.from(set_signature),this.seed,"hex"), 16);

        //if the bucket already exist for this band append docid to the bucket's documents
        if(this.buckets_id.includes(bucket_id)){
            const bucket = this.buckets.find(element=> element.value == bucket_id);
            bucket.add_document(doc_id);
        }

        // Create a new bucket and add it to the collection
        else{
            this.buckets_id.push(bucket_id);

            let b = new Bucket(bucket_id);
            b.add_document(doc_id)
            this.buckets.push(b);
        }
    }

    /**
     * Use this function to search the index for possible similarities
     *
     * @param {integer} bucket_value is the hash of a query document band
     * @return {undefined|Bucket} bucket returns either a bucket or undefined if the bucket does not exist.
     */
    search_bucket(bucket_value){
        if(this.buckets_id.includes(bucket_value)){
            const bucket = this.buckets.find(element=>
                element.value == bucket_value
            );
            return bucket;
        }
        return undefined;
    }


}

/*
 * 1. Get all the documents from the db
 * 2. Create and store a mapping from DB(doc_id) and SigMat(doc_id)
 * 3. mantain the bands of the signature matrix to allow queries
 */

class Index{

    constructor(k, n, seed, document_list) {
        this.shingler = new Shingler(k,seed);
        this.minhasher = new MinHasher(n, seed);


        // 1. build the mapping dbdocid -> sig_mat index
        // TODO

        //2.1 compute shinglings
        let shingling_list = [];
        document_list.forEach(document =>{
            shingling_list.push(
                this.shingler.get_shingling(document,true)
            );
        });
        //2.2 compute minhash signature and bands
        this.bands = this.minhasher.get_signature_matrix(shingling_list).get_bands();

        //2.3 compute bands buckets
        this.band_buckets=[];
        let DOC_NR=this.bands.length;
        for(let i=0;i< BANDS_NR; i++){

            this.band_buckets.push(
                new BucketCollection(BANDS_NR)
            );

            for(let doc_id=0; doc_id<DOC_NR; doc_id++){
                this.band_buckets[i].add_bucket(this.bands[doc_id][i],doc_id);
            }


        }

    }

    get_similar_document(query_doc){
        //query_doc is a string of comma separated ingredients .

        //Sign the query
        let shingled_query = s.get_shingling(query_doc, true);
        let query_signature = m.get_signature_matrix([shingled_query]);
        let query_bands = query_signature.get_bands()[0];

        //Search for a collision: candidates docs
        let candidates = [];
        for(let band_idx=0; band_idx< query_bands.length; band_idx++){

            let band = query_bands[band_idx];
            let band_value = parseInt(XXHash64.hash64(Buffer.from(band),1000,"hex"),16);
            //console.debug(band_value);
            //console.debug( band_buckets[band_idx] );
            let collection = this.band_buckets[band_idx];
            let collision_bucket = collection.search_bucket(band_value);
            if (collision_bucket!=undefined){
                console.log(`Candidates Docs for band ${band_idx} `);
                console.log(collision_bucket.documents);
                console.log("\n\n");
                collision_bucket.documents.forEach(el=>{candidates.push(el)});

            }

        }

        // return a list of doc_id that are potential candidates
        let candidates_set = this.remove_duplicates(candidates);
        return candidates_set;


    }

    remove_duplicates(array){
        let nodup = [];
        array.sort();
        let i=0, j=i+1;
        while( j<array.length){
            if(array[i]==array[j]){
                //skip array[i]
            }else{
                //add it
                nodup.push(array[i]);
            }
            //check the last element otherwise array[array.length-1] is not inserted
            if(j == array.length-1 && array[i]!=array[j]){
                nodup.push(array[j]);
            }
            //if all the elements are equal add the last
            if(j == array.length-1 && !nodup.includes(array[array.length-1])){
                nodup.push(array[j]);
            }

            i = i+1;
            j= i+1;
        }
        return nodup;

    }


}
//====================================================| TEST |==========================================================
console.log("-------------------------------------------------------------------\nLow Level Test\n\n");
let doc1= "Mozzarella di bufala, spaghetti, pomodoro, olio, basilico";
let doc2 = "pollo";
let s = new Shingler(K, SEED);
let shingling1 =  s.get_shingling(doc1, hashed=true);
let shingling2 = s.get_shingling(doc2, hashed=true);
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

console.log("-----------------------------------------------------\nINDEX TEST\n\n");
let doc3 =  "Mozzarella di bufala, spaghetti, pomodoro, basilico";
let doc4 =  "Mozzarella di bufala, spaghetti, pomodoro, olio d'oliva";
let index = new Index(K, N, SEED, [doc1,doc2,doc3,doc4]);


let candidates =  index.get_similar_document(query);
console.log("Similar Items:")
console.log(candidates);
console.log("\n\n")