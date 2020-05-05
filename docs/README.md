# Kibana aggregation

## RPC metrics

### RpcLogger fields

* kind : client | server
* method : String
* uri : String
* response_code
* request
* response

### Response code metrics

* Visualize > New.. > **Vertical Bar**
* Data > Metrics > **Y-Axis** = Aggregation:Count
* Buckets > **X-Axis** = Aggregation:Date Histogram | Field:**@timestamp**
* Split Series = Sub Aggregation:Terms | Field:**response_code**

For Y-Axis, can also click *Advanced*, JSON Input :

* { "kind":"client", "uri":"xx" }

[img/viz-histo-response-code.png]