# charities-claims-stubs

Provides stubbed endpoints required for local and Staging testing of DASS Replatform Charities services.

### charities-claims-validation stub

This stub currently mimics the behaviour of `charities-claims-validation` getting summary data and deletion of schedule data for local development and testing purposes.

## Technical documentation

### Before running the app

This service is typically started via Service Manager as part of the `DASS_CHARITIES_ALL` profile:

```bash
sm2 -start DASS_CHARITIES_ALL
```
If you want to run your local version of this stubs code, run:
```bash
sm2 -stop CHARITIES_CLAIMS_STUBS
```

If you need to run the local stub standalone:
```bash
sbt run
```
Note: this service runs on port 8034 by default

### Stubbed REST API

| Method   | Endpoint                                                                  | Description                                                                                                                                                     | Request Body | Response                                                                                                                                                                    | Test Scenarios                                                                                                                                                                                                                                                                                                                                                                                      |
|----------|---------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `GET`    | `/charities-claims-validation/:claimId/upload-results`                    | Retrieve upload summary for a claim. Returns schedule types (GiftAid, OtherIncome, CommunityBuildings, ConnectedCharities).                                    | N/A          | `200 OK`<br>`{ "uploads": [{ "reference": string, "validationType": string, "fileStatus": string, "uploadUrl": string? }] }`                                              | **Default:** Returns 4 hardcoded schedule types with `fileStatus: "VALIDATED"`<br>**claimId=000000:** Returns empty object `{}`<br>**claimId=no-giftaid:** Returns uploads without GiftAid type<br>**claimId=error-claim:** Returns `500 Internal Server Error`<br>**claimId=empty-uploads:** Returns `{ "uploads": [] }`<br>**claimId=not-found:** Returns `404 Not Found`                       |
| `DELETE` | `/charities-claims-validation/:claimId/upload-results/:reference`         | Delete a specific upload by reference.                                                                                                                    | N/A          | `200 OK`<br>`{ "success": boolean }`                                                                                                                                        | **Default:** Returns `{ "success": true }` for any claimId and reference<br>**claimId=000000 AND reference=000000:** Returns `{ "success": false }`<br>**reference=invalid-ref:** Returns `{ "success": false }`                                                                                                                                                                                   |

**Notes:**
- All responses are hardcoded based on claimId/reference patterns for testing purposes
- `uploadUrl` only present if `fileStatus` is `"AWAITING_UPLOAD"`
- No authentication required (local stub service)

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").