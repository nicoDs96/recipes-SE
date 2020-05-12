# API Doc

## Data Assets
Recipes:
```JSON
{
   "_id":{
      "$oid":"5e971b1c5ae9133c5769c877"
   },
   "calories":"1031",
   "portion_nr":"Ingredienti per 2 pizze",
   "ingredients":[
      {
         "ingredient":"Farina Manitoba",
         "quantity":"  200 g "
      },
      {
         "ingredient":"Farina 00",
         "quantity":"  300 g "
      },
      {
         "ingredient":"Acqua",
         "quantity":" a temperatura ambiente 300 ml "
      },
      {
         "ingredient":"Olio extravergine d'oliva",
         "quantity":"  35 g "
      },
      {
         "ingredient":"Sale fino",
         "quantity":"   10 g "
      },
      {
         "ingredient":"Lievito di birra fresco",
         "quantity":" (oppure 1,5-2 g se secco) 5 g "
      }
   ],
   "href":"https://ricette.giallozafferano.it/Impasto-per-pizza.html",
   "title":"Pasta per la pizza"
}
```
 ## CRUD
 |URI| CRUD | DESCRIPTION | Payload | HTTP RESP |
 |---|---|---|---|---|
 | recipes/ | GET | search by ingredients in payload | ```JSON [{"ingredient":"farina", "Quantity":"10g"},...] ``` | 200 (OK) Success   <br>403 (Restricted) Unauthorized user   <br>404 (Not Found) bad request, the resource does not exists   <br>405 (Method Not Allowed) <br>500 (internal Server Error) if the server raise an exception.|
 | recipes/ | POST | ... | ```JSON ... ``` | 200 (OK) Success   <br>403 (Restricted) Unauthorized user   <br>404 (Not Found) bad request, the resource does not exists   <br>405 (Method Not Allowed) <br>500 (internal Server Error) if the server raise an exception.|
