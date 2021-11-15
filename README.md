---
title: Eversign endpoint
keywords: 
last_updated: November 14, 2017
tags: []
summary: "Detailed description of the API of the Eversign endpoint."
---

## Overview

The Eversign endpoint has the following features:
 
- Access to the whole REST API
- Helpers for the API methods
- Uploading and downloading of files integrated with SLINGR's features
- Handling of webhooks

Please make sure you take a look at the documentation from Eversign as features are based on its API:

- [API Documentation](https://eversign.com/api/documentation)

## Quick start

Once you configured the endpoint, you can list current documents with this call:

```js
var docs = app.endpoints.eversign.listDocuments('all');
log('docs: '+JSON.stringify(docs));
```

You can create a new document from a template like this:

```js
var res = app.endpoints.eversign.createDocumentFromTemplate({
	"sandbox": 0,
	"template_id": "j6yMcaF4gQBIIQ",
	"title": "My New Document",
	"message": "This is my message.",
	"redirect": "https://myredirect.com/completed",
	"redirect_decline": "https://myredirect.com/declined",
	"client": "",
	"expires": 1494276966,
	"embedded_signing_enabled": 0,
	"signers": [{
		"role": "Sales Rep",
		"name": "Paul McSign",
		"email": "paul@mcsign.com",
		"pin": "1234",
		"message": "This is my custom message to Paul."
	}, {
		"role": "Client",
		"name": "Julian McSign",
		"email": "julian@mcsign.com",
		"pin": "4321",
		"message": ""
	}],
	"recipients": [{
		"role": "Assistant",
		"name": "Jane McSign",
		"email": "jane@mcsign.com"
	}, {
		"role": "Finance Department",
		"name": "Frank McSign",
		"email": "frank@mcsign.com"
	}],
	"fields": [{
		"identifier": "unique_field_identifier_1",
		"value": "Merge Field Content"
	}, {
		"identifier": "unique_field_identifier_2",
		"value": "Other Merge Field Content"
	}]
});
log(JSON.stringify(res));
```

Then you can download the signed document like this:

```js
var res = app.endpoints.eversign.downloadFinalDocument('j6yMcaF4gQBIIQ', 'signed-document.pdf');
var record = sys.data.createRecord('contracts');
record.field('file').val({
  id: res.fileId,
  name: res.fileName,
  contentType: res.contentType
});
sys.data.save(record);
```

## Configuration

Before configuring the endpoint you will need to have an account in Eversign. 

### API key

This is the API key of the Eversign account.

### Business ID

The business ID to use in all request. If not specified the default one will be used.

### Sandbox

Indicates if the sandbox will be use, which is useful for testing.

### Webhook URL

This is the URL you should configure in the webhook of your Eversign account.

## Javascript API

The Javascript API of the endpoint is based on the [REST API of Eversign](https://eversign.com/api/documentation),
so you should see their documentation for details on parameters and data formats.

### List businesses

```js
app.endpoints.eversign.listBusinesses();
```

Returns a list of businesses.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#list-businesses) for more details about the request and response.

### Create document

```js
app.endpoints.eversign.createDocument(body);
```

Creates a document from uploaded files and returns the details.

One important thing to keep in mind for this methods is the handling of files. In order to make it simpler to integrate
the endpoint with SLINGR apps, the field `fiels.file_id` could be a file ID in the SLINGR app. The endpoint will take
care of uploading the file to Eversign. If you don't want to use this feature you can provide a file ID of Eversign
or any other way allowed by Eversign's API.


This way, when you create a document, you will do something like this:

```js
var body = {
    ...
    files: [
        {
            name: 'contract.pdf',
            file_id: record.field('contract').id()
        }
    ],
    ...
};
var res = app.endpoints.eversign.createDocument(body);
log('document hash: ' + res.document_hash);
```

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#create-document) for more details about the request and response.

### Create document from template

```js
app.endpoints.eversign.createDocumentFromTemplate(body);
```

Creates a document from a template and returns the details.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#use-template) for more details about the request and response.

### Get document

```js
app.endpoints.eversign.getDocument(documentHash);
```

Returns the details of a document.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#get-document-template) for more details about the request and response.

### Get template

```js
app.endpoints.eversign.getTemplate(documentHash);
```

Returns the details of a template.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#get-document-template) for more details about the request and response.

### List documents

```js
app.endpoints.eversign.listDocuments(type);
```

Returns the list of documents in the account. In `type` you can filter by status.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#list-documents) for more details about the request and response.

### List templates

```js
app.endpoints.eversign.listTemplates(type);
```

Returns the list of documents in the account. In `type` you can filter by status.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#list-templates) for more details about the request and response.

### Send reminder

```js
app.endpoints.eversign.sendReminder(documentHash, signerId);
```

Sends a reminder to one of the signers of the document.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#send-reminder) for more details about the request and response.

### Delete document

```js
app.endpoints.eversign.deleteDocument(documentHash);
```

Deletes a document.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#delete-document) for more details about the request and response.

### Delete template

```js
app.endpoints.eversign.deleteDocument(documentHash);
```

Deletes a template.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#delete-document) for more details about the request and response.

### Cancel document

```js
app.endpoints.eversign.cancelDocument(documentHash);
```

Cancels a document.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#cancel-document) for more details about the request and response.

### Download original document

```js
app.endpoints.eversign.downloadOriginalDocument(documentHash, fileName);
```

Downloads the original document before it is filled and signed. It returns the details of the downloaded file in the
SLINGR app:

```js
var res = app.endpoints.eversign.downloadOriginalDocument('j6yMcaF4gQBIIQ', 'original-document.pdf');
var record = sys.data.createRecord('contracts');
record.field('file').val({
  id: res.fileId,
  name: res.fileName,
  contentType: res.contentType
});
sys.data.save(record);
```

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#download-original-pdf) for more details about the request and response.

### Download final document

```js
app.endpoints.eversign.downloadFinalDocument(documentHash, fileName, auditTrail);
```

Downloads the signed document. It returns the details of the downloaded file in the SLINGR app:

```js
var res = app.endpoints.eversign.downloadFinalDocument('j6yMcaF4gQBIIQ', 'original-document.pdf');
var record = sys.data.createRecord('contracts');
record.field('file').val({
  id: res.fileId,
  name: res.fileName,
  contentType: res.contentType
});
sys.data.save(record);
```

If you want to get the audit trail, send `true` in the parameter `auditTrail`.

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#download-final-pdf) for more details about the request and response.

### Upload upload

```js
app.endpoints.eversign.uploadFile(name, fileId);
```

Uploads a file to Eversign that can be used later for documents. It returns the details of the uploaded file.

Here is a sample of how you can upload a file and then use it to create a document:

```js
var uploadedFile = app.endpoints.eversign.uploadFile('contract.pdf', record.field('contract').id());
var body = {
    ...
    files: [
        {
            name: 'contract.pdf',
            file_id: uploadedFile.file_id
        }
    ],
    ...
};
var res = app.endpoints.eversign.createDocument(body);
log('document hash: ' + res.document_hash);

```

Check [REST API of Eversign](https://eversign.com/api/documentation/methods#upload-file) for more details about the request and response.


## Events

### Webhook

If webhooks were configured, you will receive events when the status of documents changes. You can find more information
about the format of events in the [Eversign's documentation](https://eversign.com/api/documentation/webhooks).

```js
sys.logs.info('event type: '+event.data.event_type);
sys.logs.info('doc hash: '+event.data.meta.related_document_hash);
```

The field `event_hash` will be automatically validated by the endpoint.

## About SLINGR

SLINGR is a low-code rapid application development platform that accelerates development, with robust architecture for integrations and executing custom workflows and automation.

[More info about SLINGR](https://slingr.io)

## License

This endpoint is licensed under the Apache License 2.0. See the `LICENSE` file for more details.
