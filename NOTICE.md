# NOTICE

## Jetty

### Attribution

Torqlang server uses Jetty per the Apache License Version 2.0

## MongoDB Northwind

### Attribution

Some Torqlang examples use the Mongo DB Northwind data forked from <https://github.com/jasny/mongodb-northwind> and residing at <https://github.com/torqlang/mongodb-northwind>.

Changes made in the core-examples module:
1) Orders are normalized. Details are extracted from `orders.json` and placed into `order_details.json`.
2) Purchase Orders are normalized. Details are extracted from `purchase_orders.json` and placed into `purchase_order_details.json`.
3) Date properties, such as `"order_date": {"$date", "2006-04-25T17:26:53Z"}`, are simplified to `"order_date": "2006-04-25T17:26:53Z"` to match the OpenAPI metadata `"order_date": {"type": "string", "format": "date-time"}`.
4) Identifiers are changed from "_id" to "id".
5) Files `orders_status` and `orders_tax_status` are renamed to `order_status` and `order_tax_status`, respectively, to agree with the original Northwind ERD.

### MongoDB Northwind License

Copyright (c) 2020, Arnold Daniels.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, this
  list of conditions and the following disclaimer in the documentation and/or
  other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
