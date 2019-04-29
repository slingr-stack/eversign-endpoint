/////////////////////
// Public API
/////////////////////

endpoint.listBusinesses = function() {
    return endpoint.get('/business');
};

endpoint.createDocument = function(body) {
    if (body.files) {
        body.files.forEach(function(file) {
            // first check if this is a Slingr ID; otherwise leave as it is
            if (file.file_id && file.file_id.length == 24) {
                var uploadedFile = endpoint.uploadFile(file.name, file.file_id);
                // replace file_id by the one returned by eversign
                file.file_id = uploadedFile.file_id;
            }
        });
    }
    return endpoint.post('/document', body);
};

endpoint.createDocumentFromTemplate = function(body) {
    if (!body || !body.template_id) {
        sys.exceptions.throwException('badRequest', 'template_id must be present');
    }
    return endpoint.post('/document', body);
};

endpoint.getDocument = function(documentHash) {
    return endpoint.get('/document', {params: {document_hash: documentHash}});
};

endpoint.getTemplate = function(templateHash) {
    return endpoint.get('/document', {params: {document_hash: templateHash}});
};

endpoint.listDocuments = function(type) {
    if (!type) type = 'all';
    return endpoint.get('/document', {params: {type: type}});
};

endpoint.listTemplates = function(type) {
    if (!type) type = 'templates';
    return endpoint.get('/document', {params: {type: type}});
};

endpoint.sendReminder = function(documentHash, signerId) {
    return endpoint.post('/send_reminder', {document_hash: documentHash, signer_id: signerId});
};

endpoint.deleteDocument = function(documentHash) {
    return endpoint.delete('/document', {params: {document_hash: documentHash}});
};

endpoint.deleteTemplate = function(documentHash) {
    return endpoint.delete('/document', {params: {document_hash: documentHash}});
};

endpoint.cancelDocument = function(documentHash) {
    return endpoint.delete('/document', {params: {document_hash: documentHash, cancel: 1}});
};

endpoint.downloadOriginalDocument = function(documentHash, fileName) {
    var request = {
        path: '/download_raw_document',
        params: {
            document_hash: documentHash
        },
        forceDownload: true,
        downloadSync: true,
        fileName: fileName || 'document.pdf'
    };
    return endpoint.get(request);
};

endpoint.downloadFinalDocument = function(documentHash, fileName, auditTrail) {
    var request = {
        path: '/download_final_document',
        params: {
            document_hash: documentHash
        },
        forceDownload: true,
        downloadSync: true,
        fileName: fileName || 'document.pdf'
    };
    if (auditTrail) {
        request.params.audit_trail = 1;
    }
    return endpoint.get(request);
};

endpoint.uploadFile = function(name, fileId) {
    return endpoint.post({
        path: '/file',
        multipart: true,
        parts: [
            {
                name: 'upload',
                type: 'file',
                fileId: fileId
            }
        ]
    });
};

///////////////////////////////////
// Public API - Generic Functions
///////////////////////////////////

endpoint.get = function(url, options) {
    options = checkHttpOptions(url, options);
    return endpoint._get(options);
};

endpoint.post = function(url, options) {
    options = checkHttpOptions(url, options);
    return endpoint._post(options);
};

endpoint.delete = function(url, options) {
    options = checkHttpOptions(url, options);
    return endpoint._delete(options);
};


/////////////////////////////////////
//  Private helpers
////////////////////////////////////

var checkHttpOptions = function (url, options) {
    options = options || {};
    if (!!url) {
        if (isObject(url)) {
            // take the 'url' parameter as the options
            options = url || {};
        } else {
            if (!!options.path || !!options.params || !!options.body) {
                // options contains the http package format
                options.path = url;
            } else {
                // create html package
                options = {
                    path: url,
                    body: options
                }
            }
        }
    }
    return options;
};

var isObject = function (obj) {
    return !!obj && stringType(obj) === '[object Object]'
};

var stringType = Function.prototype.call.bind(Object.prototype.toString);
