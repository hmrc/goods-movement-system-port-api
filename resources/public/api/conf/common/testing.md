You can use the sandbox environment to [test this API](https://developer.service.hmrc.gov.uk/api-documentation/docs/testing).

Use the **Get reference data** endpoint to get JSON containing the list of valid `portId`s, `routeId`s and other IDs referenced in this API. 

You will need to set the **Port IDs Subscription field** in your Developer Hub application settings
before using any endpoints in this API, except for **Get reference data**.  This
must be set to the comma-separated list of ports at which your software will operate.
Your software will then only be permitted to use this API to access data for the specified ports.

When testing this API's endpoints in sandbox:

* **Get reference data** will return the latest known GVMS reference data
* The other endpoints will return a `403 Forbidden` unless the `portId` specified in the URL is present in the calling application's list of subscribed Port IDs (see above)
* The other endpoints will return a fixed response depending on the `portId` requested   



### Sandbox HTTP headers
To assist with testing, special HTTP headers can be added to adjust the behaviour of this API in the sandbox.  These headers have no effect in the Production environment.

<table>
<tr>
<th style="width:40%">Sandbox HTTP header</th>
<th style="">Behaviour</th>
</tr>
<tr>
<td>Gvms-Test-Mode</td>
<td>Include this header with value "integrated" to enable integrated test mode.  If the header is omitted or set to any other value then stubbed mode will be enabled by default.</td>
</tr>
<tr>
<td>Ignore-Effective-Dates</td>
<td>Include this header with value "true" to disable validation of effective dates for routes and ports.  This is useful if you need to test using a route from reference data that is not yet effective.</td>
</tr>
<tr>
<td>Test-Scenario</td>
<td>Include this header with an integer value to trigger different responses in the sandbox environment as described in the documentation below.</td>
</tr>
</table>


### Stubbed mode testing with the Get checked-in GMRs at departure port endpoint (standard exports)
When calling the List checked-in GMRs for route endpoint in the sandbox environment in the default stubbed test mode (see HTTP headers section above), include the HTTP header `Test-Scenario` with an integer value to alter the response to simulate different scenarios described in the table below.  If the header is not included scenario 1 will be assumed.

The `lastUpdatedFrom` and `lastUpdatedTo` filter parameters will not be effective in the stubbed test mode; software must use integrated test mode to test these.

<table>
    <thead>
        <th>Test-Scenario header value</th>
        <th>Scenario name</th>
        <th>Description</th>
    </thead>
    <tr>
        <td>1</td>
        <td>Scenario step 1</td>
        <td>Some GMRs checked-in to route; none yet cleared</td>
    </tr>
    <tr>
        <td>2</td>
        <td>Scenario step 2</td>
        <td>More GMRs checked-in to route; some now cleared</td>
    </tr>
    <tr>
        <td>3</td>
        <td>Scenario step 3</td>
        <td>Further GMRs checked-in to route; all now either cleared or marked for inspection</td>
    </tr>
    <tr>
        <td>4</td>
        <td>Scenario step 4</td>
        <td>Most GMRs have embarked and no longer listed; one remains checked-in and marked for inspection</td>
    </tr>
</table>
<br>

### Integrated mode testing with the Get checked-in GMRs at departure port endpoint (standard exports)
To test across GVMS haulier, carrier and port APIs, use the [Goods Vehicle Movements API](/api-documentation/docs/api/service/goods-movement-system-haulier-api/1.0)
to create a GMR then use the `gmrId` returned to you by the [Push/Pull Notifications API](/api-documentation/docs/api/service/push-pull-notifications-api/1.0)
when calling the Carrier API check-in readiness and check-in endpoints.

These endpoints will return `200 OK` if the `gmrId` provided is in the `OPEN` state and its vehicle, trailer or container numbers match those provided in your request.  If not you will receive the relevant `400` or `404` response for the error.

After calling the Carrier API check-in endpoint successfully, the application that created the GMR will receive a notification confirming the new GMR status of `CHECKED_IN`.

Your software can get a list of all checked-in GMRs for a port using the Get checked-in GMRs at departure port endpoint on this API, with the `Gvms-Test-Mode` header set to `integrated`.
At ports using the standard exports process, approximately 20 minutes after check-in GMRs returned by this endpoint in sandbox will include the `readyToEmbark` property set to `true`, indicating that clearance has been given for the vehicle/transport unit to embark.

The `inspectionRequired` flag will never be set when using the **integrated** test mode.
To simulate `inspectionRequired`, use the default stubbed test mode instead. 

### Schemas and examples

You can download JSON schemas and examples for request and response payloads for all endpoints in this API.

<p class="govuk-body-l" style="font-size: 24px;">
  <a href="/api-documentation/docs/api/download/goods-movement-system-port-api/1.0/gvms-port-schemas.zip" class="govuk-link">Goods Vehicle Movements Port API JSON schemas and examples</a>
</p>

<p class="govuk-body-s" style="font-size: 16px;margin-bottom: 15px;">ZIP, 9KB</p>

<p class="govuk-body-s" style="font-size: 16px;margin-bottom: 15px;">The ZIP file contains JSON files. Open in your preferred file viewer.</p>  